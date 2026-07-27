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
package com.highcapable.yukihookapi.hook.core.api.compat

import de.robv.android.xposed.XposedBridge

/**
 * Resolves the Hook API available in the current environment.
 */
internal object HookApiCategoryHelper {

    /** Supported API implementations in descending selection priority. */
    private val supportedCategories = arrayOf(HookApiCategory.ROVO89_XPOSED)

    /**
     * Gets the currently available API implementation.
     * @return [HookApiCategory]
     */
    internal val currentCategory
        get() = supportedCategories.let { categories ->
            categories.forEach { if (hasCategory(it)) return@let it }
            HookApiCategory.UNKNOWN
        }

    /**
     * Gets whether a supported Hook API is available in the current environment.
     * @return [Boolean]
     */
    internal val hasAvailableHookApi get() = currentCategory != HookApiCategory.UNKNOWN

    /**
     * Checks whether the given Hook API implementation exists in the current environment.
     * @return [Boolean]
     */
    private fun hasCategory(category: HookApiCategory) = when (category) {
        HookApiCategory.ROVO89_XPOSED -> runCatching { XposedBridge.getXposedVersion(); true }.getOrNull() ?: false
        else -> false
    }
}