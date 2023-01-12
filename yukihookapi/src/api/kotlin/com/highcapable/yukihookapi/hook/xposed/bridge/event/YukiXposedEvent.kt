/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/4/30.
 * This file is Modified by fankes on 2022/1/10.
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