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
 * This file is created by fankes on 2022/2/2.
 * This file is modified by fankes on 2022/4/22.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent

/**
 * Xposed loading entry point for [YukiHookAPI].
 *
 * - Annotate the implementation with [InjectYukiHookWithXposed] to mark the module Hook entry.
 *
 * [onInit] is called automatically while [YukiHookAPI] initializes.
 *
 * [onHook] is called automatically when Hook loading starts.
 *
 * Call [YukiHookAPI.configs] or [configs] from [onInit].
 *
 * Call [YukiHookAPI.encase] or [encase] from [onHook].
 *
 * Override [onXposedEvent] to listen for native Xposed API events.
 *
 * See [IYukiHookXposedInit Interface](https://highcapable.github.io/YukiHookAPI/en/config/xposed-using#iyukihookxposedinit-interface)
 */
interface IYukiHookXposedInit {

    /**
     * Configures [YukiHookAPI.Configs] during initialization.
     *
     * - Perform initialization only. Do not run Hook operations here.
     *
     * This method is optional when no custom configuration is required.
     */
    fun onInit() {}

    /**
     * Starts module Hook loading.
     *
     * Xposed API
     *
     * Call [YukiHookAPI.encase] or [encase] to start Hook operations.
     */
    fun onHook()

    /**
     * Listens for native Xposed loading events.
     *
     * Implement native Xposed compatibility here when required by a Hook.
     *
     * Use [YukiXposedEvent] to register event callbacks.
     *
     * Available events:
     *
     * [YukiXposedEvent.onInitZygote]
     *
     * [YukiXposedEvent.onHandleLoadPackage]
     *
     * [YukiXposedEvent.onHandleInitPackageResources]
     *
     * - Use this callback only for native Xposed APIs. Do not operate [YukiHookAPI] here.
     */
    fun onXposedEvent() {}
}