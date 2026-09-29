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
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime.bridge.hooker

import androidx.annotation.RestrictTo
import java.lang.reflect.Member

/**
 * Binary hooker contract shared by core and the selected runtime.
 * Each registration returns an independent hook that can remove itself.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface HookerBridge {

    /**
     * Removes this hook without removing other registrations for the same member.
     *
     * Call this on the hook returned by registration, not the input hooker.
     * The default implementation does nothing for an unregistered hooker.
     */
    fun remove() = Unit

    /**
     * Information shared by hooker invocation types.
     */
    interface Invocation {

        /** The intercepted method or constructor. */
        val member: Member

        /** Receiver, or null for a static method. */
        val instance: Any?

        /**
         * Gets an invocation argument.
         * @param index the argument index.
         * @return [Any] the argument, or null.
         * @throws IndexOutOfBoundsException when the index is out of bounds.
         */
        fun getArg(index: Int): Any?
    }
}