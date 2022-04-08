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
 * This file is Created by fankes on 2022/4/9.
 */
package com.highcapable.yukihookapi.hook.xposed.helper

import android.app.AndroidAppHelper
import android.app.Application
import android.content.pm.ApplicationInfo

/**
 * 这是一个宿主 Hook 功能接口
 *
 * 对接 [AndroidAppHelper]
 */
internal object YukiHookAppHelper {

    /**
     * 获取当前宿主的 [Application]
     * @return [Application] or null
     */
    internal fun currentApplication() = runCatching { AndroidAppHelper.currentApplication() }.getOrNull()

    /**
     * 获取当前宿主的 [ApplicationInfo]
     * @return [ApplicationInfo] or null
     */
    internal fun currentApplicationInfo() = runCatching { AndroidAppHelper.currentApplicationInfo() }.getOrNull()

    /**
     * 获取当前宿主的包名
     * @return [String] or null
     */
    internal fun currentPackageName() = runCatching { AndroidAppHelper.currentPackageName() }.getOrNull()

    /**
     * 获取当前宿主的进程名
     * @return [String] or null
     */
    internal fun currentProcessName() = runCatching { AndroidAppHelper.currentProcessName() }.getOrNull()
}