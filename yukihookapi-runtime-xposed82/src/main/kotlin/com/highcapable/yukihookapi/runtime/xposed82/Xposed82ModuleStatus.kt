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
 * This file is created by fankes on 2026/9/7.
 */
package com.highcapable.yukihookapi.runtime.xposed82

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.YukiHookAPI_Impl
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint

/**
 * Transports legacy module status through the generated self-hook target.
 */
internal object Xposed82ModuleStatus {

    private const val DEFAULT_PRIORITY = 50

    private const val IS_ACTIVE_METHOD_NAME = "__--"
    private const val GET_EXECUTOR_API_VERSION_METHOD_NAME = "-__-"
    private const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    private const val GET_EXECUTOR_VERSION_METHOD_NAME = "-_-_"
    private const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

    private val className = runCatching { YukiHookAPI_Impl.legacyModuleStatusClassName }.getOrDefault("")

    // Cache query methods in this module's `ClassLoader`, not their changing self-hook results.
    private val methods by lazy {
        val methodNames = setOf(
            IS_ACTIVE_METHOD_NAME,
            GET_EXECUTOR_API_VERSION_METHOD_NAME,
            GET_EXECUTOR_NAME_METHOD_NAME,
            GET_EXECUTOR_VERSION_METHOD_NAME,
            GET_EXECUTOR_VERSION_CODE_METHOD_NAME
        )
        className.takeIf { it.isNotBlank() }
            ?.toClassOrNull(classOf<Xposed82ModuleStatus>().classLoader)
            ?.resolve()
            ?.optional(silent = true)
            ?.method {
                name { it in methodNames }
                emptyParameters()
            }?.associateBy { it.self.name }.orEmpty()
    }

    val isActive get() = invoke<Boolean>(IS_ACTIVE_METHOD_NAME) ?: false
    val apiVersion get() = invoke<Int>(GET_EXECUTOR_API_VERSION_METHOD_NAME)?.takeIf { it > 0 } ?: -1
    val frameworkName get() = invoke<String>(GET_EXECUTOR_NAME_METHOD_NAME)?.ifBlank { "unknown" } ?: "unknown"
    val frameworkVersion get() = invoke<String>(GET_EXECUTOR_VERSION_METHOD_NAME)?.ifBlank { "unknown" } ?: "unknown"
    val frameworkVersionCode get() = invoke<Long>(GET_EXECUTOR_VERSION_CODE_METHOD_NAME)?.takeIf { it >= 0 } ?: -1L

    fun install(loader: ClassLoader?) {
        if (loader == null || className.isBlank()) return

        // Self-hook targets belong to the supplied app ClassLoader, independently of query methods.
        val target = className.toClassOrNull(loader) ?: return
        hook(target, IS_ACTIVE_METHOD_NAME) { true }
        hook(target, GET_EXECUTOR_API_VERSION_METHOD_NAME) { YukiHookBridge.apiVersion }
        hook(target, GET_EXECUTOR_NAME_METHOD_NAME) { YukiHookBridge.frameworkName }
        hook(target, GET_EXECUTOR_VERSION_METHOD_NAME) { YukiHookBridge.frameworkVersion }
        hook(target, GET_EXECUTOR_VERSION_CODE_METHOD_NAME) { YukiHookBridge.frameworkVersionCode }
    }

    private fun hook(target: Class<*>, methodName: String, result: () -> Any?) {
        val method = target.resolve()
            .optional(silent = true)
            .firstMethodOrNull {
                name = methodName
                emptyParameters()
            }?.self ?: return

        YukiHookBridge.hook(HookPoint.Executable(method), DEFAULT_PRIORITY, object : ClassicHookerBridge {
            override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                param.result = result()
            }
        })
    }

    private fun <T> invoke(methodName: String) = methods[methodName]?.invokeQuietly<T>()
}