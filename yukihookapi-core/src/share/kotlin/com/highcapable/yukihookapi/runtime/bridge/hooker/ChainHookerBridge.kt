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
 * This file is created by fankes on 2026/9/11.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime.bridge.hooker

import androidx.annotation.RestrictTo

/**
 * Chain interceptor binary hooker implementation.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun interface ChainHookerBridge : HookerBridge {

    /**
     * Intercepts an invocation, returning a result or throwing an exception to the caller.
     * Return [Chain.proceed] to continue without changing the result, omit it to short-circuit.
     * Return values are ignored for void methods and constructors.
     * @param chain the current invocation chain.
     * @return [Any] the invocation result, or null.
     * @throws Throwable when the interceptor or the remaining chain fails.
     */
    fun intercept(chain: Chain): Any?

    /**
     * The remaining interceptor chain for a single invocation.
     * Must not be shared across threads or used after [intercept] returns or throws.
     */
    interface Chain : HookerBridge.Invocation {

        /**
         * Read-only invocation arguments.
         * Pass a new array to [proceed] to change them.
         */
        val args: List<Any?>

        /**
         * Continues the remaining chain with the current arguments and receiver.
         * @return [Any] the downstream result, or null for void methods and constructors.
         * @throws Throwable when the remaining chain or original member fails.
         */
        fun proceed(): Any?

        /**
         * Continues the remaining chain with new arguments and the current receiver.
         * @param args the arguments to pass downstream.
         * @return [Any] the downstream result, or null for void methods and constructors.
         * @throws Throwable when the remaining chain or original member fails.
         */
        fun proceed(args: Array<out Any?>): Any?

        /**
         * Continues the remaining chain with a new receiver and the current arguments.
         * Must not be used for static methods.
         * @param instance the receiver to pass downstream.
         * @return [Any] the downstream result, or null for void methods and constructors.
         * @throws Throwable when the remaining chain or original member fails.
         */
        fun proceedWith(instance: Any): Any?

        /**
         * Continues the remaining chain with a new receiver and arguments.
         * Must not be used for static methods.
         * @param instance the receiver to pass downstream.
         * @param args the arguments to pass downstream.
         * @return [Any] the downstream result, or null for void methods and constructors.
         * @throws Throwable when the remaining chain or original member fails.
         */
        fun proceedWith(instance: Any, args: Array<out Any?>): Any?
    }
}