/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
 * This file is Created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.annotation.xposed

import androidx.annotation.Keep
import com.highcapable.yukihookapi.hook.proxy.YukiHookInitializeProxy
import com.highcapable.yukihookapi.hook.xposed.YukiHookLoadPackage

/**
 * 标识注入 YukiHook 的类
 *
 * 此类将使用 [YukiHookLoadPackage] 自动调用 XposedInit
 *
 * 你可以将被注释的类继承于 [YukiHookInitializeProxy] 接口实现 [YukiHookInitializeProxy.onHook] 方法
 *
 * 只能拥有一个 Hook 入口 - 多个入口将以首个得到的入口为准
 */
@Target(AnnotationTarget.CLASS)
@Keep
annotation class InjectYukiHookWithXposed
