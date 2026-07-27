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
 * This file is created by fankes on 2022/1/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event.caller

import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Dispatches native Xposed API loading events to registered callbacks.
 */
internal object YukiXposedEventCaller {

    /**
     * Dispatches the `initZygote` event.
     * @param sparam the Xposed API parameters.
     */
    internal fun callInitZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        if (sparam == null) return
        YukiXposedEvent.initZygoteCallback?.invoke(sparam)
    }

    /**
     * Dispatches the `handleLoadPackage` event.
     * @param lpparam the Xposed API parameters.
     */
    internal fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) return
        YukiXposedEvent.handleLoadPackageCallback?.invoke(lpparam)
    }

    /**
     * Dispatches the `handleInitPackageResources` event.
     * @param resparam the Xposed API parameters.
     */
    internal fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam == null) return
        YukiXposedEvent.handleInitPackageResourcesCallback?.invoke(resparam)
    }
}