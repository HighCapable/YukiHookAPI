/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.highcapable.yukihookapi.hook

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.YukiHook.encase
import com.highcapable.yukihookapi.param.CustomParam
import com.highcapable.yukihookapi.param.PackageParam

/**
 * YukiHook 的装载 API 调用类
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 * 模块装载方式已经自动对接 Xposed API - 可直接调用 [encase] 完成操作
 */
object YukiHook {

    /** Xposed Hook API 方法体回调 */
    internal var packageParamCallback: (PackageParam.() -> Unit)? = null

    /** YukiHook 的 API 只能装载一次 */
    private var isLoaded = false

    /**
     * 自身作为模块装载调用入口方法 - Xposed API
     * ⚠️ 注意：只能装载一次
     * @param initiate Hook 方法体
     * @throws IllegalStateException 重复调用会抛出异常
     */
    fun encase(initiate: PackageParam.() -> Unit) {
        if (isLoaded) error("YukiHook API already loaded")
        isLoaded = true
        packageParamCallback = initiate
    }

    /**
     * 自定义 Hook 方法装载入口
     * @param classLoader [ClassLoader]
     * @param packageName 包名
     * @param appInfo [ApplicationInfo]
     * @param initiate Hook 方法体
     */
    fun encase(
        classLoader: ClassLoader,
        packageName: String,
        appInfo: ApplicationInfo,
        initiate: PackageParam.() -> Unit
    ) = initiate.invoke(PackageParam(customInstance = CustomParam(classLoader, appInfo, packageName)))
}