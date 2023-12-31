/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 * 实现对原生 Xposed API 装载事件监听的回调监听事件处理类
 */
internal object YukiXposedEventCaller {

    /**
     * 回调 initZygote 事件监听
     * @param sparam Xposed API 实例
     */
    internal fun callInitZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        if (sparam == null) return
        YukiXposedEvent.initZygoteCallback?.invoke(sparam)
    }

    /**
     * 回调 handleLoadPackage 事件监听
     * @param lpparam Xposed API 实例
     */
    internal fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) return
        YukiXposedEvent.handleLoadPackageCallback?.invoke(lpparam)
    }

    /**
     * 回调 handleInitPackageResources 事件监听
     * @param resparam Xposed API 实例
     */
    internal fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        if (resparam == null) return
        YukiXposedEvent.handleInitPackageResourcesCallback?.invoke(resparam)
    }
}