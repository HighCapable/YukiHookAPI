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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.annotation.xposed

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Marks the [YukiHookAPI] libxposed entry class for code generation.
 *
 * Requires `yukihookapi-runtime-libxposed`. Missing, conflicting, or mismatched runtimes will cause KSP to reject the build.
 *
 * Looking for legacy Xposed 82, please use [YukiHookXposed82Entry] with `yukihookapi-runtime-xposed82` instead.
 *
 * - KSP generates `java_init.list`, `module.prop`, and `scope.list` under `META-INF/xposed` as build resources.
 *   `native_init.list` is also generated when [nativeLibraries] is not empty.
 * - The framework reads the module name and description from the application's standard
 *   `android:label` and `android:description` attributes, not legacy `xposed*` meta-data
 *   or `module.prop`. KSP does not generate or modify the AndroidManifest.
 * - Hot reload is not supported in 1.x. Support is deferred to 2.x.
 *
 * The generated `java_init.list` entry uses the fully qualified annotated class name + `_YukiHookXposedInit`, or [entryClassName].
 *
 * - Entry files are generated per build variant. Do not add duplicate entries under the source directory.
 * - The annotated class must implement [YukiHookXposedModule].
 * - Exactly one Hook entry may use either entry annotation. Multiple entries produce a compile-time error.
 * @see YukiHookXposed82Entry
 * @param entryClassName the generated Xposed entry class name, or an empty string to use entry class name + `_YukiHookXposedInit`.
 * @param minApiVersion minimum framework API version, at least 101.
 * @param targetApiVersion targeted API behavior, independently of the compile-only API dependency.
 * @param scope scope packages. An empty array declares no recommended scope. Use `system` explicitly for system_server.
 * @param staticScope whether scope selection is restricted to the declared list.
 * @param nativeLibraries native entry library filenames, such as `libexample.so`, one per generated line.
 * Empty disables generation of `native_init.list`. Libraries must be packaged and loaded by the module itself.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@Target(AnnotationTarget.CLASS)
annotation class YukiHookLibXposedEntry(
    val entryClassName: String = "",
    val minApiVersion: Int = 101,
    val targetApiVersion: Int,
    val scope: Array<String> = [],
    val staticScope: Boolean = false,
    val nativeLibraries: Array<String> = []
)