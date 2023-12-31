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
 * This file is created by fankes on 2022/4/30.
 * This file is modified by fankes on 2022/1/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event

import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * 实现对原生 Xposed API 的装载事件监听
 */
object YukiXposedEvent {

    /** 监听 initZygote 开始的回调方法 */
    internal var initZygoteCallback: ((StartupParam) -> Unit)? = null

    /** 监听 handleLoadPackage 开始的回调方法 */
    internal var handleLoadPackageCallback: ((LoadPackageParam) -> Unit)? = null

    /** 监听 handleInitPackageResources 开始的回调方法 */
    internal var handleInitPackageResourcesCallback: ((InitPackageResourcesParam) -> Unit)? = null

    /**
     * 对 [YukiXposedEvent] 创建一个方法体
     * @param initiate 方法体
     */
    inline fun events(initiate: YukiXposedEvent.() -> Unit) {
        YukiXposedEvent.apply(initiate)
    }

    /**
     * 设置 initZygote 事件监听
     * @param result 回调方法体
     */
    fun onInitZygote(result: (StartupParam) -> Unit) {
        initZygoteCallback = result
    }

    /**
     * 设置 handleLoadPackage 事件监听
     * @param result 回调方法体
     */
    fun onHandleLoadPackage(result: (LoadPackageParam) -> Unit) {
        handleLoadPackageCallback = result
    }

    /**
     * 设置 handleInitPackageResources 事件监听
     * @param result 回调方法体
     */
    fun onHandleInitPackageResources(result: (InitPackageResourcesParam) -> Unit) {
        handleInitPackageResourcesCallback = result
    }
}