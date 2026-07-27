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
@file:Suppress("MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.api.compat

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.condition.type.Modifiers
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.yukihookapi.hook.core.api.compat.type.ExecutorType
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import de.robv.android.xposed.XposedBridge

/**
 * Resolves properties of the active Hook API.
 */
internal object HookApiProperty {

    /** Xposed framework name. */
    internal const val XPOSED_NAME = "Xposed"

    /** LSPosed framework name. */
    internal const val LSPOSED_NAME = "LSPosed"

    /** EdXposed framework name. */
    internal const val ED_XPOSED_NAME = "EdXposed"

    /** TaiChi Xposed framework name. */
    internal const val TAICHI_XPOSED_NAME = "TaiChi"

    /** BugXposed framework name. */
    internal const val BUG_XPOSED_NAME = "BugXposed"

    /** Fully qualified TaiChi ExposedBridge class name. */
    internal const val EXPOSED_BRIDGE_CLASS_NAME = "me.weishu.exposed.ExposedBridge"

    /** Fully qualified BugXposed BugLoad class name. */
    internal const val BUG_LOAD_CLASS_NAME = "com.bug.load.BugLoad"

    /**
     * Gets the current Hook Framework name.
     * @return [String] `unknown` when unavailable or `invalid` when resolution fails.
     */
    internal val name
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> when {
                AppParasitics.currentApplication?.classLoader?.hasClass(EXPOSED_BRIDGE_CLASS_NAME) == true -> TAICHI_XPOSED_NAME
                AppParasitics.currentApplication?.classLoader?.hasClass(BUG_LOAD_CLASS_NAME) == true -> BUG_XPOSED_NAME
                else -> runCatching {
                    XposedBridge::class.resolve()
                        .optional(silent = true)
                        .firstFieldOrNull {
                            name = "TAG"
                            modifiers(Modifiers.STATIC)
                        }?.get<String>()?.takeIf { it.isNotBlank() }
                        ?.replace("Bridge", "")?.replace("-", "")?.trim() ?: "unknown"
                }.getOrNull() ?: "invalid"
            }
            HookApiCategory.UNKNOWN -> "unknown"
        }

    /**
     * Gets the current Hook Framework type.
     * @return [ExecutorType]
     */
    internal val type get() = type()

    /**
     * Resolves a Hook Framework type from its name.
     * @param executorName the Hook Framework name, defaults to [name].
     * @return [ExecutorType]
     */
    internal fun type(executorName: String = name) = when (executorName) {
        BUG_XPOSED_NAME -> ExecutorType.BUG_XPOSED
        TAICHI_XPOSED_NAME -> ExecutorType.TAICHI_XPOSED
        ED_XPOSED_NAME -> ExecutorType.ED_XPOSED
        LSPOSED_NAME -> ExecutorType.LSPOSED_LSPATCH
        XPOSED_NAME -> ExecutorType.XPOSED
        else -> ExecutorType.UNKNOWN
    }

    /**
     * Gets the current Hook Framework API version.
     * @return [Int] the API version, or -1 when unavailable.
     */
    internal val apiLevel
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> runCatching { XposedBridge.getXposedVersion() }.getOrNull() ?: -1
            HookApiCategory.UNKNOWN -> -1
        }

    /**
     * Gets the current Hook Framework version name.
     * @return [String] `unknown` when unavailable or `unsupported` when unsupported.
     */
    internal val versionName
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> "unsupported"
            HookApiCategory.UNKNOWN -> "unknown"
        }

    /**
     * Gets the current Hook Framework version code.
     * @return [Int] -1 when unavailable or 0 when unsupported.
     */
    internal val versionCode
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> 0
            HookApiCategory.UNKNOWN -> -1
        }
}