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

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Wraps [PackageParam] state for the current hook entry.
 * @param type the current hook entry type.
 * @param packageName the package name.
 * @param processName the current process name.
 * @param mainProcessName the main process identity supplied by the runtime.
 * @param isFirstApplication whether this package owns the first [Application] in the current process.
 * @param appClassLoader app [ClassLoader].
 * @param appInfo app [ApplicationInfo].
 * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
 */
internal data class PackageParamWrapper(
    val type: HookEntryType,
    val packageName: String,
    val processName: String,
    val mainProcessName: String = packageName.trim(),
    val isFirstApplication: Boolean,
    val appClassLoader: ClassLoader,
    val appInfo: ApplicationInfo? = null,
    private val packageNameForMatching: String? = packageName
) {

    companion object {

        /**
         * Creates a new [PackageParamWrapper] from the current [Context].
         * @param context the current context.
         * @param type the current hook entry type, defaults to [HookEntryType.PACKAGE].
         * @return [PackageParamWrapper]
         */
        fun from(context: Context, type: HookEntryType = HookEntryType.PACKAGE) = PackageParamWrapper(
            type = type, 
            packageName = context.packageName, 
            processName = context.processName, 
            mainProcessName = context.packageName, 
            isFirstApplication = type == HookEntryType.PACKAGE,
            appClassLoader = context.classLoader, 
            appInfo = context.applicationInfo
        )
    }

    /**
     * Gets the ID of the current wrapper.
     * @return [String]
     */
    val id = "$type:$packageName"

    /**
     * Checks if the current wrapper matches the specified package name.
     * @param name the package name to check.
     * @return [Boolean]
     */
    fun matchesPackage(name: String) = packageNameForMatching == name
}