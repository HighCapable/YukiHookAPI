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
 * This file is Created by fankes on 2022/4/3.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiGenerateApi
import com.highcapable.yukihookapi.hook.param.PackageParam
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 这是一个 Xposed 模块的入口装载类实现桥
 *
 * 实现与 [IXposedHookLoadPackage] 接口通讯
 *
 * - ❗装载代码将自动生成 - 请勿修改或移动以及重命名此类的任何方法与变量
 */
@YukiGenerateApi
object YukiHookXposedBridge {

    /** Xposed 是否装载完成 */
    private var isXposedInitialized = false

    /** Xposed Hook API 方法体回调 */
    internal var packageParamCallback: (PackageParam.() -> Unit)? = null

    /**
     * 模块是否装载了 Xposed 回调方法
     *
     * - ❗装载代码将自动生成 - 请勿手动修改 - 会引发未知异常
     * @return [Boolean]
     */
    @YukiGenerateApi
    val isXposedCallbackSetUp
        get() = isXposedInitialized.not() && packageParamCallback != null

    /**
     * 当前 Hook 的对象是模块自身
     *
     * - ❗装载代码将自动生成 - 请勿手动修改 - 会引发未知异常
     */
    @YukiGenerateApi
    var isModulePackageXposedEnv = false

    /**
     * 预设的 Xposed 模块包名
     *
     * - ❗装载代码将自动生成 - 请勿手动修改 - 会引发未知异常
     */
    @YukiGenerateApi
    var modulePackageName = ""

    /**
     * 标识 Xposed API 装载完成
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     */
    @YukiGenerateApi
    fun callXposedInitialized() {
        isXposedInitialized = true
    }

    /**
     * 装载 Xposed API 回调
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param lpparam Xposed [XC_LoadPackage.LoadPackageParam]
     */
    @YukiGenerateApi
    fun callXposedLoaded(lpparam: XC_LoadPackage.LoadPackageParam) = YukiHookAPI.onXposedLoaded(lpparam)
}