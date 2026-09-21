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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.application

import android.app.Application
import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.file.YukiHookSharedFiles
import com.highcapable.yukihookapi.hook.xposed.scope.YukiHookModuleScope
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Provides an [Application] base class for Xposed modules built with [YukiHookAPI].
 *
 * Inherit this class from the Xposed module's [Application].
 * Or declare it directly in the `application` tag of AndroidManifest.
 *
 * This provides the following features:
 *
 * - Initializes [YukiHookBridge] for module runtime services.
 * - Exposes the module-wide static [current], you can also call [requireContext].
 * - Gets and uses [YukiHookModuleScope].
 * - Gets and uses [YukiHookSharedFiles].
 * - Loads [YukiHookAPI.Configs] in both module and host environments to avoid duplicate configuration.
 * - Enables [YukiHookDataChannel] communication between the module and host.
 * - Exposes [YukiHookAPI.Status.isTaiChiModuleActive] for TaiChi/Wuji activation checks.
 */
open class ModuleApplication : Application() {

    companion object {

        /**
         * Module-wide static [Application] instance.
         * @see requireContext
         */
        var current: ModuleApplication? = null

        /**
         * Requires the module-wide static [Application] instance.
         * @see current
         * @return [ModuleApplication]
         * @throws IllegalStateException if the [Application] has not finished loading correctly.
         */
        fun requireContext() = current ?: error("ModuleApplication not attached to a context.")

        /**
         * Gets the module-wide [YukiHookModuleScope] object.
         * @return [YukiHookModuleScope]
         */
        @get:RequiresRuntime(YukiHookRuntime.LIBXPOSED)
        val moduleScope get() = YukiHookModuleScope.get()

        /**
         * Gets the  [YukiHookSharedFiles] object.
         *
         * - This API is available in both module and host environments.
         *
         * Files are writable from the module environment and read-only from a host environment.
         * This storage is independent of the module application's private file directory.
         * @return [YukiHookSharedFiles]
         */
        @get:RequiresRuntime(YukiHookRuntime.LIBXPOSED)
        val sharedFiles get() = YukiHookSharedFiles.get()

        /** Gets the module-wide static [Application] instance. */
        @Deprecated(message = "Use requireContext instead.", replaceWith = ReplaceWith("requireContext()"))
        val appContext get() = requireContext()
    }

    @CallSuper
    override fun onCreate() {
        super.onCreate()
        current = this

        YukiHookBridge.initializeModuleServices()
        // Calls [YukiHookXposedModule.onInit] on the hook entry class.
        runCatching { ModuleApplication_Impl.callHookEntryInit() }
        YukiHookDataChannel.get().register(this)
    }

    @CallSuper
    override fun onTerminate() {
        YukiHookDataChannel.get().unregister()
        current = null

        super.onTerminate()
    }
}