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
 * This file is created by fankes on 2022/2/7.
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
internal class PackageParamWrapper internal constructor(
    var type: HookEntryType,
    var packageName: String,
    var processName: String,
    var appClassLoader: ClassLoader,
    var appInfo: ApplicationInfo? = null,
    var appResources: YukiResources? = null
) {

    /**
     * 获取当前包装实例的名称 ID
     * @return [String]
     */
    internal val wrapperNameId get() = if (type == HookEntryType.ZYGOTE) "android-zygote" else packageName

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