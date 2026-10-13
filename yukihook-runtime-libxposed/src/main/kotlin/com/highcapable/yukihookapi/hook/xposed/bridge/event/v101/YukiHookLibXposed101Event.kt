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
 * This file is created by fankes on 2026/9/28.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event.v101

import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiHookLibXposedEvent
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface

/**
 * Sets the `onModuleLoaded` event listener. This is process-local module loading, not zygote initialization.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the event callback with the attached API as its receiver.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
fun YukiHookLibXposedEvent.onModuleLoaded(callback: XposedInterface.(XposedModuleInterface.ModuleLoadedParam) -> Unit) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(moduleLoaded == null) {
        "onModuleLoaded is already registered."
    }

    moduleLoaded = callback
}

/**
 * Sets the `onPackageLoaded` event listener for the default class loader on Android 10 and later.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the event callback with the attached API as its receiver.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
fun YukiHookLibXposedEvent.onPackageLoaded(callback: XposedInterface.(XposedModuleInterface.PackageLoadedParam) -> Unit) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(packageLoaded == null) {
        "onPackageLoaded is already registered."
    }

    packageLoaded = callback
}

/**
 * Sets the `onPackageReady` event listener for the final package class loader.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the event callback with the attached API as its receiver.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
fun YukiHookLibXposedEvent.onPackageReady(callback: XposedInterface.(XposedModuleInterface.PackageReadyParam) -> Unit) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(packageReady == null) {
        "onPackageReady is already registered."
    }

    packageReady = callback
}

/**
 * Sets the `onSystemServerStarting` event listener, which replaces the first package lifecycle in system_server.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the event callback with the attached API as its receiver.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
fun YukiHookLibXposedEvent.onSystemServerStarting(callback: XposedInterface.(XposedModuleInterface.SystemServerStartingParam) -> Unit) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(systemServerStarting == null) {
        "onSystemServerStarting is already registered."
    }

    systemServerStarting = callback
}