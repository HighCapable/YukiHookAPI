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
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.runtime.bridge.ExecutableInvokerBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkHookBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedPreferences
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedService
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedSharedFiles
import java.lang.reflect.Member

/**
 * The core bridge for the `YukiHookAPI` runtime for `yukihookapi-runtime-libxposed`.
 */
internal object YukiHookBridge : FrameworkHookBridge {

    private const val NOT_ATTACHED_ERROR = "libxposed is not attached to this process."

    class ApiSpec(
        val apiVersion: Int = -1,
        val frameworkName: String = "unknown",
        val frameworkVersion: String = "unknown",
        val frameworkVersionCode: Long = -1L,
        val executableInvokerBridge: ExecutableInvokerBridge? = null,
        val deoptimizeCallback: ((Member) -> Boolean)? = null,
        val hookCallback: ((HookPoint, Int, HookerBridge) -> HookerBridge)? = null,
        val loggerCallback: ((Int, String, String, Throwable?) -> Unit)? = null
    )

    private var apiSpec: ApiSpec? = null
    private val api get() = apiSpec ?: error(NOT_ATTACHED_ERROR)

    override val runtime get() = YukiHookRuntime.LIBXPOSED
    override val isHookAvailable get() = apiSpec != null
    override val isCustomHookAvailable get() = false
    override val isModuleActive get() = LibXposedService.isAvailable
    override val apiVersion get() = if (isHookAvailable) api.apiVersion else LibXposedService.apiVersion
    override val frameworkName get() = if (isHookAvailable) api.frameworkName else LibXposedService.frameworkName
    override val frameworkVersion get() = if (isHookAvailable) api.frameworkVersion else LibXposedService.frameworkVersion
    override val frameworkVersionCode get() = if (isHookAvailable) api.frameworkVersionCode else LibXposedService.frameworkVersionCode
    override val executableInvoker get() = checkApi(api.executableInvokerBridge)

    fun attach(spec: ApiSpec) {
        apiSpec = spec
    }

    override fun initializeModuleServices() = LibXposedService.initialize()
    override fun installLegacyModuleStatusHooks(loader: ClassLoader?) = Unit

    override fun observeService(lifecycle: Lifecycle?, listener: FrameworkServiceBridge) = LibXposedService.observe(lifecycle, listener)

    override fun deoptimize(member: Member) = checkApi(api.deoptimizeCallback)(member)
    override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge) = checkApi(api.hookCallback)(target, priority, hooker)

    override fun log(priority: Int, tag: String, message: String, throwable: Throwable?) {
        val callback = api.loggerCallback

        if (callback != null)
            callback(priority, tag, message, throwable)
        else Log.println(priority, tag, message + (throwable?.let { "\n${Log.getStackTraceString(it)}" } ?: ""))
    }

    override fun preferences(context: Context?, packageName: String, name: String, hostEnvironment: Boolean) =
        LibXposedPreferences.open(context.takeUnless { hostEnvironment }, name)

    override fun sharedFiles(hostEnvironment: Boolean) = LibXposedSharedFiles.open(hostEnvironment)

    private inline fun <reified T : Any> checkApi(value: T?) = checkNotNull(value) { NOT_ATTACHED_ERROR }
}