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
 * This file is created by fankes on 2022/2/3.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.status

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * Xposed module Hook status implementation.
 *
 * Use the following APIs to determine whether the current module is active.
 *
 * Call [YukiHookAPI.Status.isModuleActive] or [YukiHookAPI.Status.isTaiChiModuleActive].
 *
 * Call [YukiHookAPI.Status.isXposedModuleActive].
 *
 * You can also use [YukiHookAPI.Status.Executor] to obtain details about the current Hook Framework.
 *
 * See [Xposed Module own Active State](https://highcapable.github.io/YukiHookAPI/en/guide/example#xposed-module-own-active-state)
 */
internal object YukiXposedModuleStatus {

    internal const val IS_ACTIVE_METHOD_NAME = "__--"
    internal const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"
    internal const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    internal const val GET_EXECUTOR_API_LEVEL_METHOD_NAME = "-__-"
    internal const val GET_EXECUTOR_VERSION_NAME_METHOD_NAME = "-_-_"
    internal const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

    /**
     * Gets the fully qualified class name of YukiXposedModuleStatus.
     * @return [String]
     */
    internal val className get() = runCatching { YukiXposedModuleStatus_Impl.className }.getOrNull() ?: ""

    /**
     * Gets the activation status of the current module.
     *
     * Use [YukiHookAPI.Status.isModuleActive], [YukiHookAPI.Status.isXposedModuleActive], or [YukiHookAPI.Status.isTaiChiModuleActive]
     * to determine the module activation status.
     * @return [Boolean]
     */
    internal val isActive get() = classMethod(IS_ACTIVE_METHOD_NAME)?.invoke<Boolean>() ?: false

    /**
     * Gets whether the current Hook Framework supports Resources Hook.
     *
     * Use [YukiHookAPI.Status.isSupportResourcesHook] to determine the support status.
     * @return [Boolean]
     */
    internal val isSupportResourcesHook get() = classMethod(IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME)?.invoke<Boolean>() ?: false

    /**
     * Gets the current Hook Framework name.
     *
     * Use [YukiHookAPI.Status.Executor.name] instead.
     * @return [String] `unknown` when the module is inactive.
     */
    internal val executorName get() = classMethod(GET_EXECUTOR_NAME_METHOD_NAME)?.invoke<String>()?.ifBlank { "unknown" } ?: "unknown"

    /**
     * Gets the API version of the current Hook Framework.
     *
     * Use [YukiHookAPI.Status.Executor.apiLevel] instead.
     * @return [Int] -1 when the module is inactive.
     */
    internal val executorApiLevel get() = classMethod(GET_EXECUTOR_API_LEVEL_METHOD_NAME)?.invoke<Int>()?.takeIf { it > 0 } ?: -1

    /**
     * Gets the version name of the current Hook Framework.
     *
     * Use [YukiHookAPI.Status.Executor.versionName] instead.
     * @return [Int] `unknown` when the module is inactive.
     */
    internal val executorVersionName get() = classMethod(GET_EXECUTOR_VERSION_NAME_METHOD_NAME)?.invoke<String>()?.ifBlank { "unknown" } ?: "unknown"

    /**
     * Gets the version code of the current Hook Framework.
     *
     * Use [YukiHookAPI.Status.Executor.versionCode] instead.
     * @return [Int] -1 when the module is inactive.
     */
    internal val executorVersionCode get() = classMethod(GET_EXECUTOR_VERSION_CODE_METHOD_NAME)?.invoke<Int>()?.takeIf { it > 0 } ?: -1

    /**
     * Gets a method instance through [className].
     * @param name the method name.
     * @return [MethodResolver] or null.
     */
    private fun classMethod(name: String) = className.toClassOrNull()?.resolve()
        ?.optional(silent = true)
        ?.firstMethodOrNull { this.name = name }.apply {
            if (this == null) YLog.innerW("Failed to initialize YukiXposedModuleStatus")
        }
}