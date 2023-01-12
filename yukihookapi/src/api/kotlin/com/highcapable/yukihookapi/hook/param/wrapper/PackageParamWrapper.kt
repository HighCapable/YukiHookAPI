/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is Created by fankes on 2022/2/7.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.param.wrapper

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import dalvik.system.PathClassLoader

/**
 * 用于包装 [PackageParam]
 * @param type 当前正在进行的 Hook 类型
 * @param packageName 包名
 * @param processName 当前进程名
 * @param appClassLoader APP [ClassLoader]
 * @param appInfo APP [ApplicationInfo]
 * @param appResources APP [YukiResources]
 */
@PublishedApi
internal class PackageParamWrapper internal constructor(
    var type: HookEntryType,
    var packageName: String,
    var processName: String,
    var appClassLoader: ClassLoader,
    var appInfo: ApplicationInfo? = null,
    var appResources: YukiResources? = null
) {

    /**
     * 获取当前正在进行的 Hook 进程是否正确
     *
     * 此功能为修复在 Hook 系统框架、系统 APP 等情况时会出现 [ClassLoader] 不匹配的问题
     *
     * 如果 [type] 不是 [HookEntryType.ZYGOTE] 那么 [appClassLoader] 就应该得到 [PathClassLoader]
     * @return [Boolean] 是否正确
     */
    internal val isCorrectProcess get() = type == HookEntryType.ZYGOTE || (type != HookEntryType.ZYGOTE && appClassLoader is PathClassLoader)

    override fun toString() =
        "[type] $type [packageName] $packageName [processName] $processName [appClassLoader] $appClassLoader [appInfo] $appInfo [appResources] $appResources"
}