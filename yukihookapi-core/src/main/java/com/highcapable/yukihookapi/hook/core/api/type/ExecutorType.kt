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
 * This file is created by fankes on 2023/4/16.
 */
package com.highcapable.yukihookapi.hook.core.api.type

/**
 * Identifies a hook framework implementation.
 *
 * Includes the most widely used hook frameworks currently known.
 * Additional types may be added as new hook frameworks become relevant.
 * Unrecognized hook frameworks are represented by [UNKNOWN].
 * @param explicitName the explicit name.
 */
enum class ExecutorType(val explicitName: String) {
    /** Unknown hook framework. */
    UNKNOWN("Unknown"),

    /** Original or third-party Xposed. */
    XPOSED("Xposed"),

    /** LSPosed、LSPatch */
    LSPOSED_LSPATCH("LSPosed"),

    /** EdXposed */
    ED_XPOSED("EdXposed"),

    /** TaiChi. */
    TAICHI_XPOSED("TaiChi"),

    /** BugXposed. */
    BUG_XPOSED("BugXposed");

    companion object {

        /**
         * Returns the [ExecutorType] corresponding to the given [name].
         *
         * If no matching [ExecutorType] is found, returns [UNKNOWN].
         * @param name the name of the hook framework to look up.
         * @return [ExecutorType]
         */
        fun from(name: String) = entries.firstOrNull { it.explicitName.equals(name, ignoreCase = true) } ?: UNKNOWN
    }
}