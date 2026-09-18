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
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.runtime.bridge.ExecutableInvokerBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkHookBridge
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import com.highcapable.yukihookapi.runtime.bridge.SharedFilesBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Member

/**
 * The core bridge for the `YukiHookAPI` runtime.
 * Exactly one runtime supplies these symbols in the APK.
 */
object YukiHookBridge : FrameworkHookBridge {

    override val runtime: YukiHookRuntime get() = error("Stub!")
    override val isHookAvailable: Boolean get() = error("Stub!")
    override val isCustomHookAvailable get(): Boolean = error("Stub!")
    override val isModuleActive: Boolean get() = error("Stub!")
    override val frameworkName: String get() = error("Stub!")
    override val apiLevel: Int get() = error("Stub!")
    override val frameworkVersion: String get() = error("Stub!")
    override val frameworkVersionCode: Int get() = error("Stub!")
    override val executableInvoker: ExecutableInvokerBridge get() = error("Stub!")

    override fun initializeModuleServices(): Unit = error("Stub!")
    override fun installLegacyModuleStatusHooks(loader: ClassLoader?): Unit = error("Stub!")
    override fun observeService(lifecycle: Lifecycle?, listener: FrameworkServiceBridge): FrameworkServiceBridge? = error("Stub!")
    override fun deoptimize(member: Member): Boolean = error("Stub!")
    override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge): HookerBridge = error("Stub!")
    override fun log(priority: Int, tag: String, message: String, throwable: Throwable?): Unit = error("Stub!")
    override fun preferences(context: Context?, packageName: String, name: String, hostEnvironment: Boolean): SharedPreferences = error("Stub!")
    override fun sharedFiles(hostEnvironment: Boolean): SharedFilesBridge = error("Stub!")
}