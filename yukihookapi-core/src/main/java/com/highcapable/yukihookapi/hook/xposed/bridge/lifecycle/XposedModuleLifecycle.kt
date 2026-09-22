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
 * This file is created by fankes on 2023/1/11.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Defines the Xposed module loading lifecycle.
 */
internal interface XposedModuleLifecycle {

    /**
     * Called when the Xposed module starts loading.
     * @param packageName the current Xposed module package name.
     * @param sourceDir the current Xposed module source directory.
     * @param appInfo the current Xposed module [ApplicationInfo], or null when unavailable.
     */
    fun onStartLoadModule(packageName: String, sourceDir: String, appInfo: ApplicationInfo?)

    /** Called when the Xposed module finishes loading. */
    fun onFinishLoadModule()

    /**
     * Called when an available host starts loading.
     * @param type the current hook entry type.
     * @param packageName the host package name.
     * @param processName the host process name.
     * @param mainProcessName the main process identity supplied by the runtime.
     * @param isFirstApplication whether this package owns the first [android.app.Application] in the current process.
     * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
     * @param appClassLoader the host [ClassLoader].
     * @param appInfo the host [android.content.pm.ApplicationInfo].
     */
    fun onPackageLoaded(
        type: HookEntryType,
        packageName: String,
        processName: String,
        mainProcessName: String,
        isFirstApplication: Boolean,
        packageNameForMatching: String?,
        appClassLoader: ClassLoader,
        appInfo: ApplicationInfo? = null
    )
}