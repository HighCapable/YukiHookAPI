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
 * This file is created by fankes on 2023/1/9.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.caller

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Routes calls to the Xposed module lifecycle implementation.
 */
internal object YukiXposedModuleCaller {

    /**
     * Gets whether the module has loaded its Xposed callback.
     * @return [Boolean]
     */
    internal val isXposedCallbackSetUp get() = YukiXposedModule.isXposedCallbackSetUp

    /**
     * Signals that the Xposed module started loading.
     * @param packageName the current Xposed module package name.
     * @param appFilePath the current Xposed module APK path.
     */
    internal fun callOnStartLoadModule(packageName: String, appFilePath: String) = YukiXposedModule.onStartLoadModule(packageName, appFilePath)

    /**
     * Signals that the Xposed module finished loading.
     */
    internal fun callOnFinishLoadModule() = YukiXposedModule.onFinishLoadModule()

    /**
     * Signals that an available host app started loading.
     * @param type the current Hook entry type.
     * @param packageName the host package name.
     * @param processName the host process name.
     * @param appClassLoader the host [ClassLoader].
     * @param appInfo the host [ApplicationInfo].
     * @param appResources the host [YukiResources].
     */
    internal fun callOnPackageLoaded(
        type: HookEntryType,
        packageName: String?,
        processName: String? = "",
        appClassLoader: ClassLoader? = null,
        appInfo: ApplicationInfo? = null,
        appResources: YukiResources? = null
    ) = YukiXposedModule.onPackageLoaded(type, packageName, processName, appClassLoader, appInfo, appResources)

    /**
     * Prints an error-level log entry.
     * @param msg the log message.
     * @param e the exception stack trace, defaults to null.
     */
    internal fun callLogError(msg: String, e: Throwable? = null) = YLog.innerE(msg, e)
}