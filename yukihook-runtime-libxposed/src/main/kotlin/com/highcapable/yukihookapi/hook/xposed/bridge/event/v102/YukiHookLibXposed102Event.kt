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

package com.highcapable.yukihookapi.hook.xposed.bridge.event.v102

import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiHookLibXposedEvent
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.api.XposedModuleInterface

/**
 * Sets the `onHotReloading` event listener for API 102 or later.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the native event listener.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, apiVersion = 102)
fun YukiHookLibXposedEvent.onHotReloading(callback: XposedInterface.(XposedModuleInterface.HotReloadingParam) -> Boolean) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(hotReloading == null) {
        "onHotReloading is already registered."
    }

    hotReloading = callback
}

/**
 * Sets the `onHotReloaded` event listener for API 102 or later.
 * @receiver [YukiHookLibXposedEvent] the framework event registry.
 * @param callback the native event listener.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, apiVersion = 102)
fun YukiHookLibXposedEvent.onHotReloaded(callback: XposedInterface.(XposedModuleInterface.HotReloadedParam) -> Unit) {
    YukiHookModuleCaller.checkFrameworkEventsRegistration()
    check(hotReloaded == null) {
        "onHotReloaded is already registered."
    }

    hotReloaded = callback
}