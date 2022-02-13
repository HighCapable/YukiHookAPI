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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.DoNotUseField
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * YukiHookAPI 的装载调用类
 *
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 *
 * 模块装载方式已经自动对接 Xposed API - 可直接调用 [encase] 完成操作
 *
 * 你可以调用 [configs] 对 YukiHookAPI 进行配置
 */
object YukiHookAPI {

    /** Xposed Hook API 方法体回调 */
    private var packageParamCallback: (PackageParam.() -> Unit)? = null

    /**
     * 预设的 Xposed 模块包名
     *
     * - ❗这是私有 API - 请勿手动修改 - 会引发未知异常
     */
    @DoNotUseField
    var modulePackageName = ""

    /**
     * 标识是否从自定义 Hook API 装载
     *
     * - ❗这是私有 API - 请勿手动修改 - 否则会导致功能判断错误
     */
    internal var isLoadedFromBaseContext = false

    /**
     * 配置 YukiHookAPI
     */
    object Configs {

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
    }

    /**
     * 配置 YukiHookAPI 相关参数
     *
     * 详情请参考 [configs 方法](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#configs-%E6%96%B9%E6%B3%95)
     * @param initiate 方法体
     * @return [Configs]
     */
    fun configs(initiate: Configs.() -> Unit) = Configs.apply(initiate)

    /**
     * 装载 Xposed API 回调
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param lpparam Xposed [XC_LoadPackage.LoadPackageParam]
     */
    @DoNotUseMethod
    fun onXposedLoaded(lpparam: XC_LoadPackage.LoadPackageParam) =
        packageParamCallback?.invoke(
            PackageParam(
                PackageParamWrapper(
                    packageName = lpparam.packageName,
                    processName = lpparam.processName,
                    appClassLoader = lpparam.classLoader,
                    appInfo = lpparam.appInfo
                )
            )
        )

    /**
     * 作为模块装载调用入口方法 - Xposed API
     *
     * 用法请参考 [API 文档](https://github.com/fankes/YukiHookAPI/wiki/API-%E6%96%87%E6%A1%A3)
     *
     * 配置请参考 [通过 Lambda 创建](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#%E9%80%9A%E8%BF%87-lambda-%E5%88%9B%E5%BB%BA)
     * @param initiate Hook 方法体
     */
    fun encase(initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = false
        if (hasXposedBridge)
            packageParamCallback = initiate
        else printNoXposedBridge()
    }

    /**
     * 作为模块装载调用入口方法 - Xposed API
     *
     * 用法请参考 [API 文档](https://github.com/fankes/YukiHookAPI/wiki/API-%E6%96%87%E6%A1%A3)
     *
     * 配置请参考 [通过自定义 Hooker 创建](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = false
        if (hasXposedBridge)
            packageParamCallback = {
                if (hooker.isNotEmpty())
                    hooker.forEach { it.assignInstance(packageParam = this) }
                else error("Hooker is empty")
            }
        else printNoXposedBridge()
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中实现 [YukiHookAPI] 的装载
     *
     * 详情请参考 [作为 Hook API 使用](https://github.com/fankes/YukiHookAPI/wiki#%E4%BD%9C%E4%B8%BA-hook-api-%E4%BD%BF%E7%94%A8)
     *
     * 配置请参考 [通过 Lambda 创建](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#%E9%80%9A%E8%BF%87-lambda-%E5%88%9B%E5%BB%BA)
     *
     * 用法请参考 [API 文档](https://github.com/fankes/YukiHookAPI/wiki/API-%E6%96%87%E6%A1%A3)
     * @param baseContext attachBaseContext
     * @param initiate Hook 方法体
     */
    fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = true
        if (hasXposedBridge)
            (if (baseContext != null) initiate.invoke(baseContext.packagePararm))
        else printNoXposedBridge()
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中实现 [YukiHookAPI] 的装载
     *
     * 详情请参考 [作为 Hook API 使用](https://github.com/fankes/YukiHookAPI/wiki#%E4%BD%9C%E4%B8%BA-hook-api-%E4%BD%BF%E7%94%A8)
     *
     * 配置请参考 [通过自定义 Hooker 创建](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
     *
     * 用法请参考 [API 文档](https://github.com/fankes/YukiHookAPI/wiki/API-%E6%96%87%E6%A1%A3)
     * @param baseContext attachBaseContext
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = true
        if (hasXposedBridge)
            (if (baseContext != null)
                if (hooker.isNotEmpty())
                    hooker.forEach { it.assignInstance(packageParam = baseContext.packagePararm) }
                else error("Hooker is empty"))
        else printNoXposedBridge()
    }

    /** 输出找不到 [XposedBridge] 的错误日志 */
    private fun printNoXposedBridge() = loggerE(msg = "Could not found XposedBridge in current space! Aborted")

    /**
     * 通过 baseContext 创建 Hook 入口类
     * @return [PackageParam]
     */
    private val Context.packagePararm
        get() = PackageParam(PackageParamWrapper(packageName, processName, classLoader, applicationInfo))

    /**
     * 是否存在 [XposedBridge]
     * @return [Boolean]
     */
    internal val hasXposedBridge get() = ("de.robv.android.xposed.XposedBridge").hasClass
}