/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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

/**
 * 创建一个当前 Hook 的 [Class] 接管类
 * @param instance 实例
 * @param name 完整名称
 * @param throwable 异常
 */
class HookClass internal constructor(
    internal var instance: Class<*>? = null,
    internal var name: String,
    internal var throwable: Throwable? = null
) {

    internal companion object {

        /** 占位符 [Class] 名称 */
        private const val PLACEHOLDER_CLASS_NAME = "placeholder_hook_class"

        /**
         * 创建占位符 [HookClass]
         * @return [HookClass]
         */
        internal fun createPlaceholder() = HookClass(name = PLACEHOLDER_CLASS_NAME, throwable = Throwable("There is no hook class instance"))
    }

    /**
     * 是否为占位符 [HookClass]
     * @return [Boolean]
     */
    internal val isPlaceholder get() = name == PLACEHOLDER_CLASS_NAME

    override fun toString() = "[class] $name [throwable] $throwable [instance] $instance"
}