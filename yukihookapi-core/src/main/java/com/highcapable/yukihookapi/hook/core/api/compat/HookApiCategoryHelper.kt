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
package com.highcapable.yukihookapi.hook.core.api.compat

import de.robv.android.xposed.XposedBridge

/**
 * Hook API 类型工具类
 */
internal object HookApiCategoryHelper {

    /** 目前支持的 API 类型定义 - 按优先级正序排列 */
    private val supportedCategories = arrayOf(HookApiCategory.ROVO89_XPOSED)

    /**
     * 获取当前支持的 API 类型
     * @return [HookApiCategory]
     */
    internal val currentCategory
        get() = supportedCategories.let { categories ->
            categories.forEach { if (hasCategory(it)) return@let it }
            HookApiCategory.UNKNOWN
        }

    /**
     * 获取当前环境是否存在可用的 Hook API
     * @return [Boolean]
     */
    internal val hasAvailableHookApi get() = currentCategory != HookApiCategory.UNKNOWN

    /**
     * 判断当前运行环境是否存在当前 Hook API 类型
     * @return [Boolean]
     */
    private fun hasCategory(category: HookApiCategory) = when (category) {
        HookApiCategory.ROVO89_XPOSED -> runCatching { XposedBridge.getXposedVersion(); true }.getOrNull() ?: false
        else -> false
    }
}