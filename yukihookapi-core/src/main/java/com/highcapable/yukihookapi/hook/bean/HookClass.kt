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
 * This file is created by fankes on 2022/2/9.
 */
package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi

/**
 * Holds the [Class] associated with the current Hook.
 * @param instance the class instance.
 * @param name the fully qualified name.
 * @param throwable the resolution error.
 */
@LegacyHookApi
class HookClass internal constructor(
    internal var instance: Class<*>? = null,
    internal var name: String,
    internal var throwable: Throwable? = null
) {

    internal companion object {

        /** Placeholder [Class] name. */
        private const val PLACEHOLDER_CLASS_NAME = "placeholder_hook_class"

        /**
         * Creates a placeholder [HookClass].
         * @return [HookClass]
         */
        internal fun createPlaceholder() = HookClass(name = PLACEHOLDER_CLASS_NAME, throwable = Throwable("There is no hook class instance"))
    }

    /**
     * Gets whether this is a placeholder [HookClass].
     * @return [Boolean]
     */
    internal val isPlaceholder get() = name == PLACEHOLDER_CLASS_NAME

    override fun toString() = "[class] $name [throwable] $throwable [instance] $instance"
}