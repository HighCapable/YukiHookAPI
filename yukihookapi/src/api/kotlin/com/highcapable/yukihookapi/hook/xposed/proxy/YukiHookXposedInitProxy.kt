/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.yLoggerW

/**
 * [YukiHookAPI] 的 Xposed 装载 API 调用接口
 *
 * - ❗此接口已弃用 - 在之后的版本中将直接被删除
 *
 * - ❗请现在转移到 [IYukiHookXposedInit] 否则此接口的声明将在自动处理程序中被拦截
 */
@Deprecated(message = "此接口的命名和功能已被弃用", ReplaceWith(expression = "IYukiHookXposedInit"))
interface YukiHookXposedInitProxy {

    /**
     * - ❗此方法已过时
     *
     * - ❗请将接口转移到 [IYukiHookXposedInit]
     */
    @Deprecated(message = "请将接口转移到 IYukiHookXposedInit")
    fun onInit() = yLoggerW(msg = "YukiHookXposedInitProxy was deprecated")

    /**
     * - ❗此方法已过时
     *
     * - ❗请将接口转移到 [IYukiHookXposedInit]
     */
    @Deprecated(message = "请将接口转移到 IYukiHookXposedInit")
    fun onHook() = yLoggerW(msg = "YukiHookXposedInitProxy was deprecated")
}