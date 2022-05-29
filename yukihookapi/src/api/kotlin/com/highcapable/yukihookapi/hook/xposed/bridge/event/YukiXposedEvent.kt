/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
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
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event

import com.highcapable.yukihookapi.annotation.YukiGenerateApi
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_InitPackageResources.InitPackageResourcesParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

/**
 * 实现对原生 Xposed API 的装载事件监听
 */
object YukiXposedEvent {

    /** 监听 initZygote 开始的回调方法 */
    private var initZygoteCallback: ((StartupParam) -> Unit)? = null

    /** 监听 handleLoadPackage 开始的回调方法 */
    private var handleLoadPackageCallback: ((LoadPackageParam) -> Unit)? = null

    /** 监听 handleInitPackageResources 开始的回调方法 */
    private var handleInitPackageResourcesCallback: ((InitPackageResourcesParam) -> Unit)? = null

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

    /**
     * 回调监听事件处理类
     *
     * - ❗装载代码将自动生成 - 请勿手动调用
     */
    @YukiGenerateApi
    object EventHandler {

        /**
         * 回调 initZygote 事件监听
         *
         * - ❗装载代码将自动生成 - 请勿手动调用
         * @param sparam Xposed API 实例
         */
        @YukiGenerateApi
        fun callInitZygote(sparam: StartupParam?) {
            if (sparam == null) return
            initZygoteCallback?.invoke(sparam)
        }

        /**
         * 回调 handleLoadPackage 事件监听
         *
         * - ❗装载代码将自动生成 - 请勿手动调用
         * @param lpparam Xposed API 实例
         */
        @YukiGenerateApi
        fun callHandleLoadPackage(lpparam: LoadPackageParam?) {
            if (lpparam == null) return
            handleLoadPackageCallback?.invoke(lpparam)
        }

        /**
         * 回调 handleInitPackageResources 事件监听
         *
         * - ❗装载代码将自动生成 - 请勿手动调用
         * @param resparam Xposed API 实例
         */
        @YukiGenerateApi
        fun callHandleInitPackageResources(resparam: InitPackageResourcesParam?) {
            if (resparam == null) return
            handleInitPackageResourcesCallback?.invoke(resparam)
        }
    }
}