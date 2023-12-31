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
 * This file is created by fankes on 2023/1/9.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.api.compat

import com.highcapable.yukihookapi.hook.core.api.compat.type.ExecutorType
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import de.robv.android.xposed.XposedBridge

/**
 * Hook API 相关属性
 */
internal object HookApiProperty {

    /** Xposed 框架名称 */
    internal const val XPOSED_NAME = "Xposed"

    /** LSPosed 框架名称 */
    internal const val LSPOSED_NAME = "LSPosed"

    /** EdXposed 框架名称 */
    internal const val ED_XPOSED_NAME = "EdXposed"

    /** TaiChi (太极) Xposed 框架名称 */
    internal const val TAICHI_XPOSED_NAME = "TaiChi"

    /** BugXposed (应用转生) Xposed 框架名称 */
    internal const val BUG_XPOSED_NAME = "BugXposed"

    /** TaiChi (太极) ExposedBridge 完整类名 */
    internal const val EXPOSED_BRIDGE_CLASS_NAME = "me.weishu.exposed.ExposedBridge"

    /** BugXposed (应用转生) BugLoad 完整类名 */
    internal const val BUG_LOAD_CLASS_NAME = "com.bug.load.BugLoad"

    /**
     * 获取当前 Hook Framework 名称
     * @return [String] 无法获取会返回 unknown - 获取失败会返回 invalid
     */
    internal val name
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> when {
                EXPOSED_BRIDGE_CLASS_NAME.hasClass(AppParasitics.currentApplication?.classLoader) -> TAICHI_XPOSED_NAME
                BUG_LOAD_CLASS_NAME.hasClass(AppParasitics.currentApplication?.classLoader) -> BUG_XPOSED_NAME
                else -> runCatching {
                    classOf<XposedBridge>().field { name = "TAG" }.ignored().get().string().takeIf { it.isNotBlank() }
                        ?.replace("Bridge", "")?.replace("-", "")?.trim() ?: "unknown"
                }.getOrNull() ?: "invalid"
            }
            HookApiCategory.UNKNOWN -> "unknown"
        }

    /**
     * 获取当前 Hook Framework 类型
     * @return [ExecutorType]
     */
    internal val type get() = type()

    /**
     * 获取当前 Hook Framework 类型
     * @param executorName Hook Framework 名称 - 默认为 [name]
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
     * 获取当前 Hook Framework 的 API 版本
     * @return [Int] 无法获取会返回 -1
     */
    internal val apiLevel
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> runCatching { XposedBridge.getXposedVersion() }.getOrNull() ?: -1
            HookApiCategory.UNKNOWN -> -1
        }

    /**
     * 获取当前 Hook Framework 版本名称
     * @return [String] 无法获取会返回 unknown - 不支持会返回 unsupported
     */
    internal val versionName
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> "unsupported"
            HookApiCategory.UNKNOWN -> "unknown"
        }

    /**
     * 获取当前 Hook Framework 版本号
     * @return [Int] 无法获取会返回 -1 - 不支持会返回 0
     */
    internal val versionCode
        get() = when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> 0
            HookApiCategory.UNKNOWN -> -1
        }
}