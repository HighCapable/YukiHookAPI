/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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

import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import kotlin.reflect.KClass

/**
 * Marks the [YukiHook] libxposed entry class for code generation.
 *
 * Requires `yukihook-runtime-libxposed`. Missing, conflicting, or mismatched runtimes will cause KSP to reject the build.
 *
 * Looking for legacy Xposed 82, please use [YukiHookXposed82Entry] with `yukihook-runtime-xposed82` instead.
 *
 * - KSP generates `java_init.list`, `module.prop`, and `scope.list` under `META-INF/xposed` as build resources.
 *   `native_init.list` is also generated when [nativeEntries] is not empty.
 * - The framework reads the module name and description from the application's standard
 *   `android:label` and `android:description` attributes, not legacy `xposed*` meta-data
 *   or `module.prop`. KSP does not generate or modify the AndroidManifest.
 * - [HotReload.MANUAL] and [HotReload.AUTO] rebuild module hooks without restarting the host.
 *   Completed host lifecycle events are not replayed.
 *   Modules must release their own threads, native hooks and external callbacks before accepting a reload.
 *   Requests can be refused while managed callbacks or module activities are active, or after legacy resource-path injection.
 *   A host that has not loaded a reload-enabled module instance still needs to restart once.
 *
 * The generated `java_init.list` lists the [YukiHook] entry first, followed by [javaEntries].
 * Its [YukiHook] entry uses the fully qualified annotated class name + `_YukiHookXposedInit`, or [entryClassName].
 *
 * - Entry files are generated per build variant. Do not add duplicate entries under the source directory.
 * - The annotated class must implement [YukiHookXposedModule].
 * - Exactly one hook entry may use either entry annotation. Multiple entries produce a compile-time error.
 * @see YukiHookXposed82Entry
 * @param entryClassName the generated Xposed entry class name, or an empty string to use entry class name + `_YukiHookXposedInit`.
 * @param minApiVersion minimum framework API version, at least 101.
 * @param targetApiVersion targeted API behavior, independently of the compile-only API dependency.
 * @param scope scope packages. An empty array declares no recommended scope. Use `system` explicitly for system_server.
 * @param staticScope whether scope selection is restricted to the declared list.
 * @param hotReload the module reload policy, [HotReload.NONE] by default. [HotReload.MANUAL] and [HotReload.AUTO] require
 * [minApiVersion] at least 102 and an empty [javaEntries] list. An incoming reload is completed when the new module instance
 * declares [HotReload.NONE] with [minApiVersion] at least 102.
 * @param javaEntries additional native libxposed Java entries extending `XposedModule`.
 * These entries use their own framework callbacks and must keep their class names when R8 obfuscation is enabled.
 * @param nativeEntries native entry library filenames, such as `libexample.so`, one per generated line.
 * Empty disables generation of `native_init.list`. Package these libraries in the module APK, the framework loads them as native entries.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@Target(AnnotationTarget.CLASS)
annotation class YukiHookLibXposedEntry(
    val entryClassName: String = "",
    val minApiVersion: Int = 101,
    val targetApiVersion: Int,
    val scope: Array<String> = [],
    val staticScope: Boolean = false,
    val hotReload: HotReload = HotReload.NONE,
    val javaEntries: Array<KClass<*>> = [],
    val nativeEntries: Array<String> = []
) {

    /**
     * Module reload policy.
     */
    enum class HotReload {
        /**
         * Disables reload requests for this module instance.
         * An incoming reload still completes initialization.
         */
        NONE,

        /** Allows reload requests initiated manually through the module service. */
        MANUAL,

        /** Allows manual requests and asks the framework to reload running hosts after an APK update. */
        AUTO
    }
}