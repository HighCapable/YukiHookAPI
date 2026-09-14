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
 * Classic before/after binary hooker implementation.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ClassicHookerBridge : HookerBridge {

    /**
     * Called before the intercepted member executes.
     * @param param the current invocation parameters.
     */
    fun beforeHookedMember(param: Param) = Unit

    /**
     * Called after execution, including a skipped call or a thrown exception.
     * @param param the current invocation parameters.
     */
    fun afterHookedMember(param: Param) = Unit

    /**
     * Mutable state belonging to a single intercepted invocation.
     */
    interface Param : HookerBridge.Invocation {

        /** Mutable invocation arguments. */
        val args: Array<Any?>

        /** Assigning a result, including null, skips the remaining call and clears the throwable. */
        var result: Any?

        /** Assigning a throwable clears the result. Assigning null also skips the remaining call. */
        var throwable: Throwable?

        override fun getArg(index: Int) = args[index]
    }
}