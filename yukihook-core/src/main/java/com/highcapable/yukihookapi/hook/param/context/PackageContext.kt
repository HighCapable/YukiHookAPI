/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
package com.highcapable.yukihookapi.hook.param.context

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.runtime.state.ModuleStateSpec

/**
 * Holds [PackageParam] state for the current hook entry.
 * @param type the current hook entry type.
 * @param packageName the package name.
 * @param processName the current process name.
 * @param mainProcessName the main process identity supplied by the runtime.
 * @param isFirstApplication whether this package owns the first [Application] in the current process.
 * @param classLoader the host [ClassLoader].
 * @param appInfo the host [ApplicationInfo].
 * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
 */
internal data class PackageContext(
    val type: HookEntryType,
    val packageName: String,
    val processName: String,
    val mainProcessName: String = packageName.trim(),
    val isFirstApplication: Boolean,
    val classLoader: ClassLoader,
    val appInfo: ApplicationInfo? = null,
    val packageNameForMatching: String? = packageName
) {

    companion object : ModuleStateSpec<PackageContext> {

        /** The ID of the hook entry being declared on the current thread. */
        private val declaringEntry = ThreadLocal<String>()

        /**
         * Creates a new [PackageContext] from the current [Context].
         * @param context the current context.
         * @param type the current hook entry type, defaults to [HookEntryType.PACKAGE].
         * @return [PackageContext]
         */
        fun from(context: Context, type: HookEntryType = HookEntryType.PACKAGE) = PackageContext(
            type = type, 
            packageName = context.packageName, 
            processName = context.processName, 
            mainProcessName = context.applicationInfo.processName ?: context.packageName,
            isFirstApplication = type == HookEntryType.PACKAGE,
            classLoader = context.classLoader,
            appInfo = context.applicationInfo
        )

        override fun encode(state: PackageContext) = arrayOf(
            state.type.name, state.packageName, state.processName, state.mainProcessName,
            state.isFirstApplication, state.classLoader, state.appInfo, state.packageNameForMatching
        )

        override fun decode(encoded: Any?): PackageContext {
            val values = encoded as? Array<*> ?: error("Missing package context state.")
            check(values.size == 8) {
                "Invalid package context state: expected 8 values, but got ${values.size}"
            }

            return PackageContext(
                type = HookEntryType.valueOf(values[0] as String),
                packageName = values[1] as String,
                processName = values[2] as String,
                mainProcessName = values[3] as String,
                isFirstApplication = values[4] as Boolean,
                classLoader = values[5] as ClassLoader,
                appInfo = values[6] as? ApplicationInfo,
                packageNameForMatching = values[7] as? String
            )
        }
    }

    /**
     * Gets the ID of the current hook entry.
     * @return [String]
     */
    val id = "$type:$packageName"

    /**
     * Gets whether this hook entry is being declared on the current thread.
     * @return [Boolean]
     */
    val isDeclaring get() = declaringEntry.get() == id

    /**
     * Runs the declarations of this hook entry with it bound to the current thread.
     * @param block the declarations.
     * @return [T]
     */
    fun <T> declare(block: () -> T): T {
        val previous = declaringEntry.get()
        declaringEntry.set(id)

        return try {
            block()
        } finally {
            if (previous == null)
                declaringEntry.remove()
            else declaringEntry.set(previous)
        }
    }

    /**
     * Checks if the current hook entry matches the specified package name.
     * @param name the package name to check.
     * @return [Boolean]
     */
    fun matchesPackage(name: String) = packageNameForMatching == name
}