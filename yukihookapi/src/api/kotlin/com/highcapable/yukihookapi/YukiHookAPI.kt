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
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi

import android.content.pm.ApplicationInfo
import androidx.annotation.Keep
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.DoNotUseField
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.CustomParam
import com.highcapable.yukihookapi.hook.param.PackageParam
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * YukiHook 的装载 API 调用类
 *
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 *
 * 模块装载方式已经自动对接 Xposed API - 可直接调用 [encase] 完成操作
 */
@Keep
object YukiHookAPI {

    /**
     * 这是一个调试日志的全局标识
     *
     * 默认文案为 YukiHookAPI
     *
     * 你可以修改为你自己的文案
     */
    var debugTag = "YukiHookAPI"

    /**
     * 是否开启调试模式 - 默认启用
     *
     * 启用后将交由日志输出管理器打印详细 Hook 日志到控制台
     *
     * 关闭后将只输出 Error 级别的日志
     *
     * 请过滤 [debugTag] 即可找到每条日志
     */
    var isDebug = true

    /**
     * Xposed Hook API 绑定的模块包名 - 未写将自动生成
     * - 你不应该设置此变量的名称 - 请使用 [encase] 装载模块包名
     */
    @DoNotUseField
    @Keep
    var modulePackageName = ""

    /** Xposed Hook API 方法体回调 */
    private var packageParamCallback: (PackageParam.() -> Unit)? = null

    /**
     * 装载 Xposed API 回调
     *
     * - 装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param lpparam Xposed [XC_LoadPackage.LoadPackageParam]
     */
    @DoNotUseMethod
    @Keep
    fun onXposedLoaded(lpparam: XC_LoadPackage.LoadPackageParam) = packageParamCallback?.invoke(PackageParam(lpparam))

    /**
     * 作为模块装载调用入口方法 - Xposed API
     * @param moduleName 你的模块包名 - 可通过 Xposed API 自动生成
     * @param initiate Hook 方法体
     */
    fun encase(moduleName: String = "", initiate: PackageParam.() -> Unit) {
        modulePackageName = moduleName
        packageParamCallback = initiate
    }

    /**
     * 作为模块装载调用入口方法 - Xposed API
     * @param moduleName 你的模块包名 - 可通过 Xposed API 自动生成
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(moduleName: String = "", vararg hooker: YukiBaseHooker) {
        modulePackageName = moduleName
        packageParamCallback = {
            if (hooker.isNotEmpty())
                hooker.forEach { it.assignInstance(packageParam = this) }
            else error("Hooker is empty")
        }
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
    ) = initiate.invoke(PackageParam(customParam = CustomParam(classLoader, appInfo, packageName)))
}