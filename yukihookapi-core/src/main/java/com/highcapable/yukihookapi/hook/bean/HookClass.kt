/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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