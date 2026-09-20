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
 * This file is created by fankes on 2026/9/15.
 */
package com.highcapable.yukihookapi.annotation.xposed

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Marks the [YukiHookAPI] Xposed API 82 entry class for code generation.
 *
 * Requires `yukihookapi-runtime-xposed82`. Missing, conflicting, or mismatched runtimes will cause KSP to reject the build.
 *
 * Looking for modern libxposed, please use [YukiHookLibXposedEntry] with `yukihookapi-runtime-libxposed` instead.
 *
 * - The AndroidManifest source directory defaults to `src/main` and can be customized through [sourcePath].
 * - KSP generates `assets/xposed_init` as a build resource.
 * - The runtime AndroidManifest supplies `xposedmodule=true` and `xposedminversion=82`.
 *   To override the minimum version, declare it in your AndroidManifest with `tools:replace="android:value"`.
 *   Declare `xposeddescription` and `xposedscope` yourself when needed. KSP does not generate or modify the AndroidManifest.
 *
 * The generated `xposed_init` entry uses the fully qualified annotated class name + `_YukiHookXposedInit`, or [entryClassName].
 *
 * - [modulePackageName] overrides the module application ID. Otherwise, `BuildConfig.APPLICATION_ID` is read.
 * - A custom [modulePackageName] produces a compile-time warning so the value can be verified.
 * - Entry files are generated per build variant. Do not add duplicate entries under the source directory.
 * - The annotated class must implement [YukiHookXposedModule].
 * - Exactly one Hook entry may use either entry annotation. Multiple entries produce a compile-time error.
 * @see YukiHookLibXposedEntry
 * @param sourcePath the project-relative AndroidManifest source directory, defaults to `src/main`.
 * @param modulePackageName the module application ID, or an empty string to read `BuildConfig.APPLICATION_ID`.
 * @param entryClassName the generated Xposed entry class name, or an empty string to use entry class name + `_YukiHookXposedInit`.
 * @param generateModuleStatus whether legacy self-hook status generation is enabled, defaults to true.
 */
@RequiresRuntime(YukiHookRuntime.XPOSED_82)
@Target(AnnotationTarget.CLASS)
annotation class YukiHookXposed82Entry(
    val sourcePath: String = "src/main",
    val modulePackageName: String = "",
    val entryClassName: String = "",
    val generateModuleStatus: Boolean = true
)