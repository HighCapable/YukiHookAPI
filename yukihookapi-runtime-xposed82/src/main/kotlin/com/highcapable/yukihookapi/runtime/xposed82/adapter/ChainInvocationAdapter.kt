/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
 * This file is created by fankes on 2026/9/12.
 */
package com.highcapable.yukihookapi.runtime.xposed82.adapter

import com.highcapable.kavaref.extension.isStatic
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Adapts module-local chain registrations to a classic before callback for one member.
 * @param member the intercepted method or constructor.
 * @param invokeOriginal the original invocation, bypassing all native hooks.
 */
internal class ChainInvocationAdapter(
    private val member: Member,
    private val invokeOriginal: (Any?, Array<out Any?>) -> Any?
) : ClassicHookerBridge {

    private val returnsValue = member is Method && member.returnType != Void.TYPE

    @Volatile
    private var registrations = emptyList<Registration>()

    /** Whether the native hook can be removed because no local interceptors remain. */
    val isEmpty get() = registrations.isEmpty()

    /**
     * Registers an interceptor without changing an invocation already in progress.
     * @param priority the priority within the local chain, highest first.
     * @param hooker the interceptor to invoke.
     * @return [HookerBridge] an independent registration handle.
     */
    @Synchronized
    fun hookMember(priority: Int, hooker: ChainHookerBridge): HookerBridge {
        val registration = Registration(priority, hooker)
        registrations = (registrations + registration).sortedByDescending { it.priority }

        return object : HookerBridge by hooker {
            override fun remove() = synchronized(this@ChainInvocationAdapter) {
                registrations = registrations.filterNot { it === registration }
            }
        }
    }

    override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
        val snapshot = registrations
        // An already captured native callback may still run after the last interceptor is removed.
        if (snapshot.isEmpty()) return

        try {
            param.result = Chain(param.instance, buildList { addAll(param.args) }, snapshot, 0).proceed()
        } catch (throwable: Throwable) {
            // Throwing out of before would make API 82 log the failure and run the original again.
            param.throwable = throwable
        }
    }

    private class Registration(val priority: Int, hooker: ChainHookerBridge) : ChainHookerBridge by hooker

    private inner class Chain(
        override val instance: Any?,
        override val args: List<Any?>,
        private val snapshot: List<ChainHookerBridge>,
        private val index: Int
    ) : ChainHookerBridge.Chain {

        override val member get() = this@ChainInvocationAdapter.member

        override fun getArg(index: Int) = args[index]

        override fun proceed() = proceed(instance, args)

        override fun proceed(args: Array<out Any?>) = proceed(instance, buildList { addAll(args) })

        override fun proceedWith(instance: Any): Any? {
            require(!member.isStatic) { "Cannot change the receiver of a static method." }
            return proceed(instance, args)
        }

        override fun proceedWith(instance: Any, args: Array<out Any?>): Any? {
            require(!member.isStatic) { "Cannot change the receiver of a static method." }
            return proceed(instance, buildList { addAll(args) })
        }

        private fun proceed(instance: Any?, args: List<Any?>): Any? {
            val result = if (index < snapshot.size)
                snapshot[index].intercept(Chain(instance, args, snapshot, index + 1))
            else try {
                invokeOriginal(instance, args.toTypedArray())
            } catch (throwable: InvocationTargetException) {
                // Unwrap the native invocation boundary, not exceptions thrown by interceptors.
                throw throwable.targetException
            }

            return if (returnsValue) result else null
        }
    }
}