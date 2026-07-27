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
 * This file is created by fankes on 2022/4/9.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.proxy

import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import java.lang.reflect.Member

/**
 * Base Hook callback.
 * @param priority the Hook priority.
 */
internal abstract class YukiHookCallback(internal open val priority: YukiHookPriority) {

    /**
     * Provides parameters and mutable state for a Hook callback.
     */
    internal interface Param {

        /**
         * Gets the hooked [Member].
         * @return [Member] or null.
         */
        val member: Member?

        /**
         * Gets the current receiver instance.
         * @return [Any] or null.
         */
        val instance: Any?

        /**
         * Gets the method or constructor arguments.
         * @return [Array] or null.
         */
        val args: Array<Any?>?

        /**
         * Gets or sets the invocation result.
         * @return [Any] or null.
         */
        var result: Any?

        /**
         * Gets whether the invocation has a configured throwable.
         * @return [Boolean]
         */
        val hasThrowable: Boolean

        /**
         * Gets or sets the throwable raised by the invocation.
         * @return [Throwable] or null.
         * @throws Throwable
         */
        var throwable: Throwable?
    }
}