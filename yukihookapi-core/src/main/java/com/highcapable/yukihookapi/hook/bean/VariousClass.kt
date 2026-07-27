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
 * This file is created by fankes on 2022/2/10.
 */
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.factory.toClassOrNull

/**
 * Resolves the first available [Class] from multiple candidate names.
 * @param name one or more class names, checked in declaration order.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class VariousClass(private vararg val name: String) {

    /**
     * Gets the first matching class.
     *
     * - Uses [loader] to load each target [Class].
     * @param loader the [ClassLoader], or the default loader when null.
     * @param initialize whether to initialize static class blocks, defaults to false.
     * @return [Class]
     * @throws IllegalStateException if none of the candidate classes can be resolved.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun get(loader: ClassLoader? = null, initialize: Boolean = false): Class<*> {
        var finalClass: Class<*>? = null
        if (name.isNotEmpty()) run {
            name.forEach {
                finalClass = it.toClassOrNull(loader, initialize)
                if (finalClass != null) return@run
            }
        }
        return finalClass ?: error("VariousClass match failed of those $this")
    }

    /**
     * Gets the first matching class, or null.
     *
     * - Uses [loader] to load each target [Class].
     *
     * Returns null instead of throwing when no [Class] matches.
     * @param loader the [ClassLoader], or the default loader when null.
     * @param initialize whether to initialize static class blocks, defaults to false.
     * @return [Class] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun getOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = runCatching { get(loader, initialize) }.getOrNull()

    override fun toString(): String {
        var result = ""
        return if (name.isNotEmpty()) {
            name.forEach { result += "\"$it\"," }
            "[${result.substring(0, result.lastIndex)}]"
        } else "[]"
    }
}