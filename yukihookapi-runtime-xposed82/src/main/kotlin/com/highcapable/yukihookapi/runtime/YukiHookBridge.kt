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
@file:Suppress("unused", "RedundantNullableReturnType")

package com.highcapable.yukihookapi.runtime

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.runtime.bridge.FrameworkHookBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.xposed82.Xposed82ExecutableInvoker
import com.highcapable.yukihookapi.runtime.xposed82.Xposed82HookApi
import com.highcapable.yukihookapi.runtime.xposed82.Xposed82ModuleStatus
import com.highcapable.yukihookapi.runtime.xposed82.Xposed82Preferences
import java.lang.reflect.Member

/**
 * The core bridge for the `YukiHookAPI` runtime for `yukihookapi-runtime-xposed82`.
 */
internal object YukiHookBridge : FrameworkHookBridge {

    private var attached = false

    override val runtime get() = YukiHookRuntime.XPOSED_82
    override val isHookAvailable get() = attached

    override val isCustomHookAvailable get(): Boolean {
        if (attached) return true
        attached = Xposed82HookApi.isAvailable
        return attached
    }

    override val isModuleActive get() = Xposed82ModuleStatus.isActive
    override val frameworkName get() = if (attached) Xposed82HookApi.frameworkName else Xposed82ModuleStatus.frameworkName
    override val apiLevel get() = if (attached) Xposed82HookApi.apiLevel else Xposed82ModuleStatus.apiLevel
    override val frameworkVersion get() = if (attached) "unsupported" else Xposed82ModuleStatus.frameworkVersion
    override val frameworkVersionCode get() = if (attached) 0 else Xposed82ModuleStatus.frameworkVersionCode
    override val executableInvoker get() = if (attached) Xposed82ExecutableInvoker else error("Xposed API is not attached to this process.")

    fun attach() {
        attached = true
    }

    override fun initializeModuleServices() = Unit
    override fun installLegacyModuleStatusHooks(loader: ClassLoader?) = Xposed82ModuleStatus.install(loader)

    override fun observeService(lifecycle: Lifecycle?, listener: FrameworkServiceBridge): FrameworkServiceBridge? =
        throw UnsupportedOperationException("Framework service unavailable in Xposed API 82.")

    override fun deoptimize(member: Member): Boolean =
        throw UnsupportedOperationException("Method deoptimization is unavailable in Xposed API 82.")

    override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge): HookerBridge {
        if (hooker !is ClassicHookerBridge && hooker !is ChainHookerBridge)
            throw UnsupportedOperationException("Unsupported hooker type for Xposed API 82: ${hooker.javaClass.name}.")

        check(attached) {
            "Xposed API is not attached to this process."
        }

        return when (target) {
            is HookPoint.Executable -> Xposed82HookApi.hookMember(target.value, priority, hooker)
            is HookPoint.Initializer -> throw UnsupportedOperationException(
                "Static initializer hooks are unavailable in Xposed API 82."
            )
        }
    }

    override fun log(priority: Int, tag: String, message: String, throwable: Throwable?) {
        if (attached)
            Xposed82HookApi.log(priority, tag, message, throwable)
        else Log.println(priority, tag, message + (throwable?.let { "\n${Log.getStackTraceString(it)}" } ?: ""))
    }

    override fun preferences(context: Context?, packageName: String, name: String, hostEnvironment: Boolean) =
        Xposed82Preferences.open(context.takeUnless { hostEnvironment }, packageName, name)
}