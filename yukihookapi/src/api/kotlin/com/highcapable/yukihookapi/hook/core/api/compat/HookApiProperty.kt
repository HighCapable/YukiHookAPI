/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
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
 * This file is Created by fankes on 2023/1/9.
 */
@file:Suppress("MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.api.compat

import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import de.robv.android.xposed.XposedBridge

/**
 * Hook API 相关属性
 */
internal object HookApiProperty {

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