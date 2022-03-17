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
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "OPT_IN_USAGE", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.DoNotUseField
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.log.*
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * [YukiHookAPI] 的装载调用类
 *
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 *
 * 模块装载方式已经自动对接 Xposed API - 可直接调用 [encase] 完成操作
 *
 * 你可以调用 [configs] 对 [YukiHookAPI] 进行配置
 */
object YukiHookAPI {

    /** Xposed Hook API 方法体回调 */
    private var packageParamCallback: (PackageParam.() -> Unit)? = null

    /** 是否还未输出欢迎信息 */
    private var isShowSplashLogOnceTime = true

    /** 获取当前 [YukiHookAPI] 的版本 */
    const val API_VERSION_NAME = "1.0.4"

    /** 获取当前 [YukiHookAPI] 的版本号 */
    const val API_VERSION_CODE = 5

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
     * 获取当前 Hook 框架的名称
     *
     * 从 [XposedBridge] 获取 TAG
     * @return [String] 无法获取会返回 unknown - [hasXposedBridge] 不存在会返回 invalid
     */
    val executorName
        get() = runCatching {
            (XposedBridge::class.java.getDeclaredField("TAG").apply { isAccessible = true }.get(null) as? String?)
                ?.replace(oldValue = "Bridge", newValue = "")?.replace(oldValue = "-", newValue = "")?.trim() ?: "unknown"
        }.getOrNull() ?: "invalid"

    /**
     * 获取当前 Hook 框架的版本
     *
     * 获取 [XposedBridge.getXposedVersion]
     * @return [Int] 无法获取会返回 -1
     */
    val executorVersion get() = runCatching { XposedBridge.getXposedVersion() }.getOrNull() ?: -1

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
         *
         * 当 [isAllowPrintingLogs] 关闭后 [isDebug] 也将同时关闭
         */
        var isDebug = true

        /**
         * 是否启用调试日志的输出功能
         *
         * - ❗关闭后将会停用 [YukiHookAPI] 对全部日志的输出
         *
         *  但是不影响当你手动调用下面这些方法输出日志
         *
         *  [loggerD]、[loggerI]、[loggerW]、[loggerE]
         *
         *  当 [isAllowPrintingLogs] 关闭后 [isDebug] 也将同时关闭
         */
        var isAllowPrintingLogs = true
    }

    /**
     * 配置 [YukiHookAPI] 相关参数
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
            ).apply { printSplashLog() }
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
        else printNoXposedEnvLog()
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
                else yLoggerE(msg = "Failed to passing \"encase\" method because your hooker param is empty")
            }
        else printNoXposedEnvLog()
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
        when {
            hasXposedBridge && baseContext != null -> initiate.invoke(baseContext.packagePararm.apply { printSplashLog() })
            else -> printNoXposedEnvLog()
        }
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
                if (hooker.isNotEmpty()) {
                    printSplashLog()
                    hooker.forEach { it.assignInstance(packageParam = baseContext.packagePararm) }
                } else yLoggerE(msg = "Failed to passing \"encase\" method because your hooker param is empty"))
        else printNoXposedEnvLog()
    }

    /** 输出找不到 [XposedBridge] 的错误日志 */
    private fun printNoXposedEnvLog() = yLoggerE(msg = "Could not found XposedBridge in current space! Aborted")

    /** 输出欢迎信息调试日志 */
    private fun printSplashLog() {
        if (!Configs.isDebug || !isShowSplashLogOnceTime) return
        isShowSplashLogOnceTime = false
        yLoggerI(msg = "Welcome to YukiHookAPI $API_VERSION_NAME($API_VERSION_CODE)! Using $executorName API $executorVersion")
    }

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
    internal val hasXposedBridge get() = executorVersion >= 0
}