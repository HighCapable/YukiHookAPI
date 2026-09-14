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
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.runtime.bridge.FrameworkHookBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedPreferences
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedService
import java.lang.reflect.Member

/**
 * The core bridge for the `YukiHookAPI` runtime for `yukihookapi-runtime-libxposed`.
 */
internal object YukiHookBridge : FrameworkHookBridge {

    var hostFrameworkName = "unknown"
    var hostApiLevel = -1
    var hostVersion = "unknown"
    var hostVersionCode = -1

    var hookCallback: ((Member, Int, HookerBridge) -> HookerBridge)? = null
    var originalCallback: ((Member, Any?, Array<out Any?>) -> Any?)? = null
    var loggerCallback: ((Int, String, String, Throwable?) -> Unit)? = null
    var preferencesCallback: ((String) -> SharedPreferences)? = null

    override val runtime get() = YukiHookRuntime.LIBXPOSED
    override val isHookAvailable get() = hookCallback != null
    override val isCustomHookAvailable get() = false
    override val isModuleActive get() = LibXposedService.isAvailable
    override val frameworkName get() = if (isHookAvailable) hostFrameworkName else LibXposedService.frameworkName
    override val apiLevel get() = if (isHookAvailable) hostApiLevel else LibXposedService.apiLevel
    override val frameworkVersion get() = if (isHookAvailable) hostVersion else LibXposedService.frameworkVersion
    override val frameworkVersionCode get() = if (isHookAvailable) hostVersionCode else LibXposedService.frameworkVersionCode

    override fun initializeModuleApp() = LibXposedService.initialize()
    override fun installLegacyModuleStatusHooks(loader: ClassLoader?) = Unit

    override fun observeService(lifecycle: Lifecycle?, listener: FrameworkServiceBridge) = LibXposedService.observe(lifecycle, listener)

    override fun hookMember(member: Member, priority: Int, hooker: HookerBridge) = checkNotNull(hookCallback) {
        "libxposed is not attached to this process."
    }(member, priority, hooker)

    override fun invokeOriginal(member: Member, instance: Any?, args: Array<out Any?>) = checkNotNull(originalCallback) {
        "libxposed is not attached to this process."
    }(member, instance, args)

    override fun log(priority: Int, tag: String, message: String, throwable: Throwable?) {
        val callback = loggerCallback

        if (callback != null)
            callback(priority, tag, message, throwable)
        else Log.println(priority, tag, message + (throwable?.let { "\n${Log.getStackTraceString(it)}" } ?: ""))
    }

    override fun preferences(context: Context?, packageName: String, name: String, hostEnvironment: Boolean) =
        LibXposedPreferences.open(context.takeUnless { hostEnvironment }, name)
}