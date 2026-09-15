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

package com.highcapable.yukihookapi.hook.xposed.bridge.event

import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface

/**
 * Registers framework event listeners from [YukiHookXposedModule.onInit].
 *
 * The configuration block runs immediately, event callbacks receive the attached [XposedInterface]
 * and the original framework parameters when the framework dispatches them.
 * Use native libxposed APIs in these callbacks and keep `YukiHookAPI` hook loading in [YukiHookXposedModule.onHook].
 *
 * Only one registration block is allowed per module process, even across different entry instances.
 * @receiver [YukiHookXposedModule]
 * @param block the event configuration block.
 * @throws IllegalStateException if called outside [YukiHookXposedModule.onInit] or more than once.
 */
fun YukiHookXposedModule.registerFrameworkEvents(block: YukiLibXposedEvent.() -> Unit) =
    YukiHookModuleCaller.callRegisterFrameworkEvents(this) { YukiLibXposedEvent.block() }

/**
 * Registers listeners for framework lifecycle events exposed by libxposed.
 * Configure listeners only inside [registerFrameworkEvents]. Each lifecycle listener can be set once.
 */
object YukiLibXposedEvent {

    internal var moduleLoaded: (XposedInterface.(XposedModuleInterface.ModuleLoadedParam) -> Unit)? = null
        private set
    internal var packageLoaded: (XposedInterface.(XposedModuleInterface.PackageLoadedParam) -> Unit)? = null
        private set
    internal var packageReady: (XposedInterface.(XposedModuleInterface.PackageReadyParam) -> Unit)? = null
        private set
    internal var systemServerStarting: (XposedInterface.(XposedModuleInterface.SystemServerStartingParam) -> Unit)? = null
        private set

    /**
     * Sets the `onModuleLoaded` event listener. This is process-local module loading, not zygote initialization.
     * @param callback the event callback with the attached API as its receiver.
     */
    fun onModuleLoaded(callback: XposedInterface.(XposedModuleInterface.ModuleLoadedParam) -> Unit) {
        YukiHookModuleCaller.checkFrameworkEventsRegistration()
        check(moduleLoaded == null) {
            "onModuleLoaded is already registered."
        }

        moduleLoaded = callback
    }

    /**
     * Sets the `onPackageLoaded` event listener for the default class loader on Android 10 and later.
     * @param callback the event callback with the attached API as its receiver.
     */
    fun onPackageLoaded(callback: XposedInterface.(XposedModuleInterface.PackageLoadedParam) -> Unit) {
        YukiHookModuleCaller.checkFrameworkEventsRegistration()
        check(packageLoaded == null) {
            "onPackageLoaded is already registered."
        }

        packageLoaded = callback
    }

    /**
     * Sets the `onPackageReady` event listener for the final package class loader.
     * @param callback the event callback with the attached API as its receiver.
     */
    fun onPackageReady(callback: XposedInterface.(XposedModuleInterface.PackageReadyParam) -> Unit) {
        YukiHookModuleCaller.checkFrameworkEventsRegistration()
        check(packageReady == null) {
            "onPackageReady is already registered."
        }

        packageReady = callback
    }

    /**
     * Sets the `onSystemServerStarting` event listener, which replaces the first package lifecycle in system_server.
     * @param callback the event callback with the attached API as its receiver.
     */
    fun onSystemServerStarting(callback: XposedInterface.(XposedModuleInterface.SystemServerStartingParam) -> Unit) {
        YukiHookModuleCaller.checkFrameworkEventsRegistration()
        check(systemServerStarting == null) {
            "onSystemServerStarting is already registered."
        }

        systemServerStarting = callback
    }
}