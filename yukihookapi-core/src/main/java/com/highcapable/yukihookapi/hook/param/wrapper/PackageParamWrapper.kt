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
 * Wraps [PackageParam] state for the current Hook entry.
 * @param type the current Hook entry type.
 * @param packageName the package name.
 * @param processName the current process name.
 * @param appClassLoader app [ClassLoader].
 * @param appInfo app [ApplicationInfo].
 * @param appResources app [YukiResources].
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
     * Gets the name ID of the current wrapper.
     * @return [String]
     */
    internal val wrapperNameId get() = if (type == HookEntryType.ZYGOTE) "android-zygote" else packageName

    /**
     * Gets whether the current Hook process uses the expected [ClassLoader].
     *
     * This prevents [ClassLoader] mismatches while hooking the system framework or system apps.
     *
     * When [type] is not [HookEntryType.ZYGOTE], [appClassLoader] must be a [PathClassLoader].
     * @return [Boolean] whether the process configuration is valid.
     */
    internal val isCorrectProcess get() = type == HookEntryType.ZYGOTE || (type != HookEntryType.ZYGOTE && appClassLoader is PathClassLoader)

    override fun toString() =
        "[type] $type [packageName] $packageName [processName] $processName [appClassLoader] $appClassLoader [appInfo] $appInfo [appResources] $appResources"
}