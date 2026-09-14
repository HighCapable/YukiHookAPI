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
 * This file is created by fankes on 2022/4/15.
 */
package com.highcapable.yukihookapi.hook.xposed.application

import android.app.Application
import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.runtime.YukiHookBridge

/**
 * Provides an [Application] base class for Xposed modules built with [YukiHookAPI].
 *
 * Inherit this class from the Xposed module's [Application].
 * Or declare it directly in the `application` tag of AndroidManifest.
 *
 * This provides the following features:
 *
 * - Initializes [YukiHookBridge] for module app runtime services.
 * - Exposes the module-wide static [appContext].
 * - Loads [YukiHookAPI.Configs] in both module and host environments to avoid duplicate configuration.
 * - Enables [YukiHookDataChannel] communication between the module and host.
 * - Exposes [YukiHookAPI.Status.isTaiChiModuleActive] for TaiChi/Wuji activation checks.
 */
open class ModuleApplication : Application() {

    companion object {

        /** Module-wide static [Application] instance. */
        internal var current: ModuleApplication? = null

        /**
         * Gets the module-wide static [Application] instance.
         * @return [ModuleApplication]
         * @throws IllegalStateException if the [Application] has not finished loading correctly.
         */
        val appContext get() = current ?: error("App is dead, You cannot call to appContext.")
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        current = this

        YukiHookBridge.initializeModuleApp()
        // Calls [IYukiHookXposedInit.onInit] on the Hook entry class.
        runCatching { ModuleApplication_Impl.callHookEntryInit() }
        YukiHookDataChannel.get().register(context = this)
    }
}