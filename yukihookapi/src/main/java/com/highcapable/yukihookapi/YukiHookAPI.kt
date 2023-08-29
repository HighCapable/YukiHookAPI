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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI.Configs.debugLog
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiProperty
import com.highcapable.yukihookapi.hook.core.api.compat.type.ExecutorType
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.log.YukiHookLogger
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiXposedModuleStatus
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import java.lang.reflect.Member

/**
 * [YukiHookAPI] 的装载调用类
 *
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 *
 * Xposed 模块装载方式已经自动对接相关 API - 可直接调用 [encase] 完成操作
 *
 * 你可以调用 [configs] 对 [YukiHookAPI] 进行配置
 */
object YukiHookAPI {

    /** 是否还未输出欢迎信息 */
    private var isShowSplashLogOnceTime = true

    /** 标识是否从自定义 Hook API 装载 */
    internal var isLoadedFromBaseContext = false

    // TODO
    const val TAG = ""

    /** 版本名称 */
    const val API_VERSION_NAME = BuildConfig.API_VERSION_NAME

    /** 版本号 */
    const val API_VERSION_CODE = BuildConfig.API_VERSION_CODE

    /**
     * 当前 [YukiHookAPI] 的状态
     */
    object Status {

        /**
         * 获取项目编译完成的时间戳 (当前本地时间)
         * @return [Long]
         */
        val compiledTimestamp get() = runCatching { YukiHookAPI_Impl.compiledTimestamp }.getOrNull() ?: 0L

        /**
         * 获取当前是否为 (Xposed) 宿主环境
         * @return [Boolean]
         */
        val isXposedEnvironment get() = YukiXposedModule.isXposedEnvironment

        /**
         * 获取当前 Hook Framework 名称
         *
         * - ❗此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - ❗请现在转移到 [Executor.name]
         * @return [String]
         */
        @Deprecated(
            message = "请使用新方式来实现此功能",
            ReplaceWith("Executor.name", "com.highcapable.yukihookapi.YukiHookAPI.Status.Executor")
        )
        val executorName get() = Executor.name

        /**
         * 获取当前 Hook Framework 版本
         *
         * - ❗此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - ❗请现在转移到 [Executor.apiLevel]、[Executor.versionName]、[Executor.versionCode]
         * @return [Int]
         */
        @Deprecated(
            message = "请使用新方式来实现此功能",
            ReplaceWith("Executor.apiLevel", "com.highcapable.yukihookapi.YukiHookAPI.Status.Executor")
        )
        val executorVersion get() = Executor.apiLevel

        /**
         * 判断模块是否在 Xposed 或太极、无极中激活
         *
         * - ❗在模块环境中你需要将 [Application] 继承于 [ModuleApplication]
         *
         * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
         *
         * - ❗在 (Xposed) 宿主环境中仅返回非 [isTaiChiModuleActive] 的激活状态
         * @return [Boolean] 是否激活
         */
        val isModuleActive get() = isXposedEnvironment || YukiXposedModuleStatus.isActive || isTaiChiModuleActive

        /**
         * 仅判断模块是否在 Xposed 中激活
         *
         * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
         *
         * - ❗在 (Xposed) 宿主环境中始终返回 true
         * @return [Boolean] 是否激活
         */
        val isXposedModuleActive get() = isXposedEnvironment || YukiXposedModuleStatus.isActive

        /**
         * 仅判断模块是否在太极、无极中激活
         *
         * - ❗在模块环境中你需要将 [Application] 继承于 [ModuleApplication]
         *
         * - ❗在 (Xposed) 宿主环境中始终返回 false
         * @return [Boolean] 是否激活
         */
        val isTaiChiModuleActive get() = isXposedEnvironment.not() && (ModuleApplication.currentContext?.isTaiChiModuleActive ?: false)

        /**
         * 判断当前 Hook Framework 是否支持资源钩子(Resources Hook)
         *
         * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
         *
         * - ❗在 (Xposed) 宿主环境中可能会延迟等待事件回调后才会返回 true
         *
         * - ❗请注意你需要确保 [InjectYukiHookWithXposed.isUsingResourcesHook] 已启用 - 否则始终返回 false
         * @return [Boolean] 是否支持
         */
        val isSupportResourcesHook
            get() = YukiXposedModule.isSupportResourcesHook.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.isSupportResourcesHook

        /**
         * 当前 [YukiHookAPI] 使用的 Hook Framework 相关信息
         */
        object Executor {

            /**
             * 获取当前 Hook Framework 名称
             *
             * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
             * @return [String] 无法获取会返回 unknown - 获取失败会返回 invalid
             */
            val name
                get() = HookApiProperty.name.takeIf { isXposedEnvironment } ?: when {
                    isXposedModuleActive -> YukiXposedModuleStatus.executorName
                    isTaiChiModuleActive -> HookApiProperty.TAICHI_XPOSED_NAME
                    else -> YukiXposedModuleStatus.executorName
                }

            /**
             * 获取当前 Hook Framework 类型
             *
             * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
             * @return [ExecutorType]
             */
            val type get() = HookApiProperty.type.takeIf { isXposedEnvironment } ?: HookApiProperty.type(YukiXposedModuleStatus.executorName)

            /**
             * 获取当前 Hook Framework 的 API 版本
             *
             * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
             * @return [Int] 无法获取会返回 -1
             */
            val apiLevel get() = HookApiProperty.apiLevel.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorApiLevel

            /**
             * 获取当前 Hook Framework 版本名称
             *
             * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
             * @return [String] 无法获取会返回 unknown - 不支持会返回 unsupported
             */
            val versionName get() = HookApiProperty.versionName.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorVersionName

            /**
             * 获取当前 Hook Framework 版本号
             *
             * - ❗在模块环境中需要启用 [Configs.isEnableHookModuleStatus]
             * @return [Int] 无法获取会返回 -1 - 不支持会返回 0
             */
            val versionCode get() = HookApiProperty.versionCode.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorVersionCode
        }
    }

    /**
     * 配置 [YukiHookAPI]
     */
    object Configs {

        /**
         * 配置 [YukiHookLogger.Configs] 相关参数
         * @param initiate 方法体
         */
        inline fun debugLog(initiate: YukiHookLogger.Configs.() -> Unit) = YukiHookLogger.Configs.apply(initiate).build()

        /**
         * 这是一个调试日志的全局标识
         *
         * - ❗此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - ❗请现在转移到 [debugLog] 并使用 [YukiHookLogger.Configs.tag]
         */
        @Deprecated(message = "请使用新方式来实现此功能")
        var debugTag
            get() = YukiHookLogger.Configs.tag
            set(value) {
                YukiHookLogger.Configs.tag = value
            }

        /**
         * 是否开启调试模式 - 默认启用
         *
         * 启用后将交由日志输出管理器打印详细 Hook 日志到控制台
         *
         * 关闭后将只输出 Error 级别的日志
         *
         * 请过滤 [debugTag] 即可找到每条日志
         *
         * 当 [YukiHookLogger.Configs.isEnable] 关闭后 [isDebug] 也将同时关闭
         */
        var isDebug = true

        /**
         * 是否启用调试日志的输出功能
         *
         * - ❗此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - ❗请现在转移到 [debugLog] 并使用 [YukiHookLogger.Configs.isEnable]
         */
        @Deprecated(message = "请使用新方式来实现此功能")
        var isAllowPrintingLogs
            get() = YukiHookLogger.Configs.isEnable
            set(value) {
                YukiHookLogger.Configs.isEnable = value
            }

        /**
         * 是否启用 [YukiHookPrefsBridge] 的键值缓存功能
         *
         * - ❗此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - ❗请现在转移到 [isEnablePrefsBridgeCache]
         */
        @Deprecated(message = "请使用新的命名方法来实现此功能", ReplaceWith("isEnablePrefsBridgeCache"))
        var isEnableModulePrefsCache = false

        /**
         * 是否启用 [YukiHookPrefsBridge] 的键值缓存功能
         *
         * - ❗此方法及功能已被移除 - 在之后的版本中将直接被删除
         *
         * - ❗键值的直接缓存功能已被移除 - 因为其存在内存溢出 (OOM) 问题
         */
        @Deprecated(message = "此方法及功能已被移除，请删除此方法")
        var isEnablePrefsBridgeCache = false

        /**
         * 是否启用当前 Xposed 模块自身 [Resources] 缓存功能
         *
         * - 为防止内存复用过高问题 - 此功能默认启用
         *
         * - ❗关闭后每次使用 [PackageParam.moduleAppResources] 都会重新创建 - 可能会造成运行缓慢
         *
         * 你可以手动调用 [PackageParam.refreshModuleAppResources] 来刷新缓存
         */
        var isEnableModuleAppResourcesCache = true

        /**
         * 是否启用 Hook Xposed 模块激活等状态功能
         *
         * - 为原生支持 Xposed 模块激活状态检测 - 此功能默认启用
         *
         * - ❗关闭后你将不能再在模块环境中使用 [YukiHookAPI.Status] 中的功能
         */
        var isEnableHookModuleStatus = true

        /**
         * 是否启用 Hook [SharedPreferences]
         *
         * 启用后将在模块启动时强制将 [SharedPreferences] 文件权限调整为 [Context.MODE_WORLD_READABLE] (0664)
         *
         * - ❗这是一个可选的实验性功能 - 此功能默认不启用
         *
         * - 仅用于修复某些系统可能会出现在启用了 New XSharedPreferences 后依然出现文件权限错误问题 - 若你能正常使用 [YukiHookPrefsBridge] 就不建议启用此功能
         */
        var isEnableHookSharedPreferences = false

        /**
         * 是否启用当前 Xposed 模块与宿主交互的 [YukiHookDataChannel] 功能
         *
         * 请确保 Xposed 模块的 [Application] 继承于 [ModuleApplication] 才能有效
         *
         * - 此功能默认启用 - 关闭后将不会在功能初始化的时候装载 [YukiHookDataChannel]
         */
        var isEnableDataChannel = true

        /**
         * 是否启用 [Member] 缓存功能
         *
         * - ❗此方法及功能已被移除 - 在之后的版本中将直接被删除
         *
         * - ❗[Member] 的直接缓存功能已被移除 - 因为其存在内存溢出 (OOM) 问题
         */
        @Deprecated(message = "此方法及功能已被移除，请删除此方法")
        var isEnableMemberCache = false
    }

    /**
     * 配置 [YukiHookAPI] 相关参数
     *
     * 详情请参考 [configs 方法](https://fankes.github.io/YukiHookAPI/zh-cn/config/api-example#configs-%E6%96%B9%E6%B3%95)
     *
     * For English version, see [configs Method](https://fankes.github.io/YukiHookAPI/en/config/api-example#configs-method)
     * @param initiate 方法体
     */
    inline fun configs(initiate: Configs.() -> Unit) {
        Configs.apply(initiate)
    }

    /**
     * 作为 Xposed 模块装载调用入口方法
     *
     * 用法请参考 [API 文档](https://fankes.github.io/YukiHookAPI/zh-cn/api/home)
     *
     * For English version, see [API Document](https://fankes.github.io/YukiHookAPI/en/api/home)
     *
     * 配置请参考 [通过 lambda 创建](https://fankes.github.io/YukiHookAPI/zh-cn/config/api-example#%E9%80%9A%E8%BF%87-lambda-%E5%88%9B%E5%BB%BA)
     *
     * For English version, see [Created by lambda](https://fankes.github.io/YukiHookAPI/en/config/api-example#created-by-lambda)
     * @param initiate Hook 方法体
     */
    fun encase(initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = false
        if (YukiXposedModule.isXposedEnvironment)
            YukiXposedModule.packageParamCallback = initiate
        else printNotFoundHookApiError()
    }

    /**
     * 作为 Xposed 模块装载调用入口方法
     *
     * 用法请参考 [API 文档](https://fankes.github.io/YukiHookAPI/zh-cn/api/home)
     *
     * For English version, see [API Document](https://fankes.github.io/YukiHookAPI/en/api/home)
     *
     * 配置请参考 [通过自定义 Hooker 创建](https://fankes.github.io/YukiHookAPI/zh-cn/config/api-example#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
     *
     * For English version, see [Created by Custom Hooker](https://fankes.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = false
        if (YukiXposedModule.isXposedEnvironment)
            YukiXposedModule.packageParamCallback = {
                if (hooker.isNotEmpty())
                    hooker.forEach { it.assignInstance(packageParam = this) }
                else yLoggerE(msg = "Failed to passing \"encase\" method because your hooker param is empty", isImplicit = true)
            }
        else printNotFoundHookApiError()
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中实现 [YukiHookAPI] 的装载
     *
     * 详情请参考 [作为 Hook API 使用](https://fankes.github.io/YukiHookAPI/zh-cn/guide/quick-start#%E4%BD%9C%E4%B8%BA-hook-api-%E4%BD%BF%E7%94%A8)
     *
     * For English version, see [Use as Hook API](https://fankes.github.io/YukiHookAPI/en/guide/quick-start#use-as-hook-api)
     *
     * 用法请参考 [API 文档](https://fankes.github.io/YukiHookAPI/zh-cn/api/home)
     *
     * For English version, see [API Document](https://fankes.github.io/YukiHookAPI/en/api/home)
     *
     * 配置请参考 [通过 lambda 创建](https://fankes.github.io/YukiHookAPI/zh-cn/config/api-example#%E9%80%9A%E8%BF%87-lambda-%E5%88%9B%E5%BB%BA)
     *
     * For English version, see [Created by lambda](https://fankes.github.io/YukiHookAPI/en/config/api-example#created-by-lambda)
     * @param baseContext attachBaseContext
     * @param initiate Hook 方法体
     */
    fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = true
        when {
            HookApiCategoryHelper.hasAvailableHookApi && baseContext != null ->
                initiate(baseContext.createPackageParam().apply { printSplashInfo() })
            else -> printNotFoundHookApiError()
        }
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中实现 [YukiHookAPI] 的装载
     *
     * 详情请参考 [作为 Hook API 使用](https://fankes.github.io/YukiHookAPI/zh-cn/guide/quick-start#%E4%BD%9C%E4%B8%BA-hook-api-%E4%BD%BF%E7%94%A8)
     *
     * For English version, see [Use as Hook API](https://fankes.github.io/YukiHookAPI/en/guide/quick-start#use-as-hook-api)
     *
     * 用法请参考 [API 文档](https://fankes.github.io/YukiHookAPI/zh-cn/api/home)
     *
     * For English version, see [API Document](https://fankes.github.io/YukiHookAPI/en/api/home)
     *
     * 配置请参考 [通过自定义 Hooker 创建](https://fankes.github.io/YukiHookAPI/zh-cn/config/api-example#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
     *
     * For English version, see [Created by Custom Hooker](https://fankes.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
     * @param baseContext attachBaseContext
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = true
        if (HookApiCategoryHelper.hasAvailableHookApi)
            (if (baseContext != null)
                if (hooker.isNotEmpty()) {
                    printSplashInfo()
                    hooker.forEach { it.assignInstance(packageParam = baseContext.createPackageParam()) }
                } else yLoggerE(msg = "Failed to passing \"encase\" method because your hooker param is empty", isImplicit = true))
        else printNotFoundHookApiError()
    }

    /** 输出欢迎信息调试日志 */
    internal fun printSplashInfo() {
        if (Configs.isDebug.not() || isShowSplashLogOnceTime.not()) return
        isShowSplashLogOnceTime = false
        yLoggerI(
            msg = "Welcome to YukiHookAPI $API_VERSION_NAME($API_VERSION_CODE)! Using ${Status.Executor.name} API ${Status.Executor.apiLevel}",
            isImplicit = true
        )
    }

    /** 输出找不到 Hook API 的错误日志 */
    private fun printNotFoundHookApiError() =
        yLoggerE(msg = "Could not found any available Hook APIs in current environment! Aborted", isImplicit = true)

    /**
     * 通过 baseContext 创建 Hook 入口类
     * @return [PackageParam]
     */
    private fun Context.createPackageParam() =
        PackageParam(PackageParamWrapper(HookEntryType.PACKAGE, packageName, processName, classLoader, applicationInfo))
}