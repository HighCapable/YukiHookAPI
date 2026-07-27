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
 * This file is created by fankes on 2022/4/30.
 * This file is modified by fankes on 2022/1/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * Registers listeners for native Xposed API loading events.
 */
object YukiXposedEvent {

    /** Callback invoked when `initZygote` starts. */
    internal var initZygoteCallback: ((StartupParam) -> Unit)? = null

    /** Callback invoked when `handleLoadPackage` starts. */
    internal var handleLoadPackageCallback: ((LoadPackageParam) -> Unit)? = null

    /** Callback invoked when `handleInitPackageResources` starts. */
    internal var handleInitPackageResourcesCallback: ((InitPackageResourcesParam) -> Unit)? = null

    /**
     * Configures [YukiXposedEvent].
     * @param initiate the configuration block.
     */
    inline fun events(initiate: YukiXposedEvent.() -> Unit) {
        YukiXposedEvent.apply(initiate)
    }

    /**
     * Sets the `initZygote` event listener.
     * @param result the event callback.
     */
    fun onInitZygote(result: (StartupParam) -> Unit) {
        initZygoteCallback = result
    }

    /**
     * Sets the `handleLoadPackage` event listener.
     * @param result the event callback.
     */
    fun onHandleLoadPackage(result: (LoadPackageParam) -> Unit) {
        handleLoadPackageCallback = result
    }

    /**
     * Sets the `handleInitPackageResources` event listener.
     * @param result the event callback.
     */
    fun onHandleInitPackageResources(result: (InitPackageResourcesParam) -> Unit) {
        handleInitPackageResourcesCallback = result
    }
}