/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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