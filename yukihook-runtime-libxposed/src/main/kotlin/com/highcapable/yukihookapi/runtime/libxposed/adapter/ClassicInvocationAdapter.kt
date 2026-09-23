/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.libxposed.adapter

import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import java.lang.reflect.Member

/**
 * Per-invocation state for the classic before/after contract over an interceptor chain.
 */
internal class ClassicInvocationAdapter private constructor(
    override val member: Member,
    override val instance: Any?,
    override val args: Array<Any?>,
    private val onFailure: (Throwable) -> Unit
) : ClassicHookerBridge.Param {

    companion object {

        /**
         * Create a new invocation for the given member, instance, and arguments.
         * @param member the member being invoked.
         * @param instance the instance the method is invoked on, or null for static methods.
         * @param args the arguments to the method call.
         * @param onFailure callback when an exception occurs in the hooker.
         * @return [ClassicInvocationAdapter]
         */
        fun from(member: Member, instance: Any?, args: Array<Any?>, onFailure: (Throwable) -> Unit) =
            ClassicInvocationAdapter(member, instance, args, onFailure)
    }

    private var returnEarly = false
    private var currentResult: Any? = null
    private var currentThrowable: Throwable? = null

    override var result: Any?
        get() = currentResult
        set(value) {
            currentResult = value
            currentThrowable = null
            returnEarly = true
        }

    override var throwable: Throwable?
        get() = currentThrowable
        set(value) {
            currentThrowable = value
            currentResult = null
            returnEarly = true
        }

    fun intercept(hooker: ClassicHookerBridge, proceed: (Array<Any?>) -> Any?): Any? {
        try {
            hooker.beforeHookedMember(this)
        } catch (e: Throwable) {
            onFailure(e)
            currentResult = null
            currentThrowable = null
            returnEarly = false
        }

        if (!returnEarly) try {
            currentResult = proceed(args)
        } catch (e: Throwable) {
            currentThrowable = e
        }

        // After hooks also run on short-circuit and on exceptions from the rest of the chain.
        val proceededResult = currentResult
        val proceededThrowable = currentThrowable
        try {
            hooker.afterHookedMember(this)
        } catch (e: Throwable) {
            onFailure(e)
            currentResult = proceededResult
            currentThrowable = proceededThrowable
        }

        currentThrowable?.let { throw it }
        return currentResult
    }
}