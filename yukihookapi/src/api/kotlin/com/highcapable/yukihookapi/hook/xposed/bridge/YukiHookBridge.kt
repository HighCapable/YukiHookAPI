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
@file:Suppress("unused", "StaticFieldLeak")

package com.highcapable.yukihookapi.hook.xposed.bridge

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiGenerateApi
import com.highcapable.yukihookapi.hook.factory.*
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.type.android.*
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClassLoader
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiHookHelper
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiMemberHook
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.xposed.bridge.inject.YukiHookBridge_Injector
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiHookModuleStatus
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookInitPackageResources
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 这是一个对接 Xposed Hook 入口与 [XposedBridge] 的装载类实现桥
 *
 * 实现与 [IXposedHookZygoteInit]、[IXposedHookLoadPackage]、[IXposedHookInitPackageResources] 接口通讯
 *
 * - ❗装载代码将自动生成 - 请勿修改或移动以及重命名此类的任何方法与变量
 */
@YukiGenerateApi
object YukiHookBridge {

    /** Android 系统框架名称 */
    @PublishedApi
    internal const val SYSTEM_FRAMEWORK_NAME = "android"

    /** Xposed 是否装载完成 */
    private var isXposedInitialized = false

    /** [YukiHookDataChannel] 是否已经注册 */
    private var isDataChannelRegister = false

    /** 当前 Hook 进程是否正处于 [IXposedHookZygoteInit.initZygote] */
    private var isInitializingZygote = false

    /** 已在 [PackageParam] 中被装载的 APP 包名 */
    private val loadedPackageNames = HashSet<String>()

    /** 当前 [PackageParamWrapper] 实例数组 */
    private val packageParamWrappers = HashMap<String, PackageParamWrapper>()

    /** 已被注入到宿主 [Resources] 中的当前 Xposed 模块资源 HashCode 数组 */
    private val injectedHostResourcesHashCodes = HashSet<Int>()

    /** 当前 [PackageParam] 方法体回调 */
    internal var packageParamCallback: (PackageParam.() -> Unit)? = null

    /** 当前 Hook Framework 是否支持 Resources Hook */
    internal var isSupportResourcesHook = false

    /**
     * 当前 Hook APP (宿主) 的全局生命周期 [Application]
     *
     * 需要 [YukiHookAPI.Configs.isEnableDataChannel] 或 [AppLifecycleCallback.isCallbackSetUp] 才会生效
     */
    internal var hostApplication: Application? = null

    /** 当前 Xposed 模块自身 APK 路径 */
    internal var moduleAppFilePath = ""

    /** 当前 Xposed 模块自身 [Resources] */
    internal var moduleAppResources: YukiModuleResources? = null

    /**
     * 获取当前 Xposed 模块自身动态 [Resources]
     * @return [YukiModuleResources] or null
     */
    internal val dynamicModuleAppResources get() = runCatching { YukiModuleResources.wrapper(moduleAppFilePath) }.getOrNull()

    /**
     * 自动生成的 Xposed 模块构建版本号
     * @return [String]
     */
    internal val moduleGeneratedVersion get() = YukiHookBridge_Injector.getModuleGeneratedVersion()

    /**
     * 当前宿主正在进行的 Hook 进程标识名称
     * @return [String]
     */
    internal val hostProcessName get() = if (isInitializingZygote) "android-zygote" else YukiHookAppHelper.currentPackageName() ?: "unknown"

    /**
     * 获取当前系统框架的 [Context]
     * @return [Context] ContextImpl 实例对象
     * @throws IllegalStateException 如果获取不到系统框架的 [Context]
     */
    internal val systemContext
        get() = ActivityThreadClass.method { name = "currentActivityThread" }.ignored().get().call()?.let {
            ActivityThreadClass.method { name = "getSystemContext" }.ignored().get(it).invoke<Context?>()
        } ?: error("Failed to got SystemContext")

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
     * 预设的 Xposed 模块包名
     *
     * - ❗装载代码将自动生成 - 请勿手动修改 - 会引发未知异常
     */
    @YukiGenerateApi
    var modulePackageName = ""

    /**
     * 获取当前 Hook 框架的名称
     *
     * 从 [XposedBridge] 获取 TAG
     * @return [String] 无法获取会返回 unknown - [hasXposedBridge] 不存在会返回 invalid
     */
    internal val executorName
        get() = runCatching {
            classOf<XposedBridge>().field { name = "TAG" }.ignored().get().string().takeIf { it.isNotBlank() }
                ?.replace(oldValue = "Bridge", newValue = "")?.replace(oldValue = "-", newValue = "")?.trim() ?: "unknown"
        }.getOrNull() ?: "invalid"

    /**
     * 获取当前 Hook 框架的版本
     *
     * 获取 [XposedBridge.getXposedVersion]
     * @return [Int] 无法获取会返回 -1
     */
    internal val executorVersion get() = runCatching { XposedBridge.getXposedVersion() }.getOrNull() ?: -1

    /**
     * 是否存在 [XposedBridge]
     * @return [Boolean]
     */
    internal val hasXposedBridge get() = executorVersion >= 0

    /**
     * 获取指定 [packageName] 的用户 ID
     *
     * 机主为 0 - 应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同
     * @param packageName 当前包名
     * @return [Int]
     */
    internal fun findUserId(packageName: String) =
        UserHandleClass.method {
            name = "getUserId"
            param(IntType)
        }.ignored().get().int(systemContext.packageManager.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES).uid)

    /**
     * 自动忽略 MIUI 系统可能出现的日志收集注入实例
     * @param packageName 当前包名
     * @return [Boolean] 是否存在
     */
    private fun isMiuiCatcherPatch(packageName: String?) =
        (packageName == "com.miui.contentcatcher" || packageName == "com.miui.catcherpatch") && ("android.miui.R").hasClass()

    /**
     * 当前包名是否已在指定的 [HookEntryType] 被装载
     * @param packageName 包名
     * @param type 当前 Hook 类型
     * @return [Boolean] 是否已被装载
     */
    private fun isPackageLoaded(packageName: String, type: HookEntryType): Boolean {
        if (loadedPackageNames.contains("$packageName:$type")) return true
        loadedPackageNames.add("$packageName:$type")
        return false
    }

    /**
     * 创建、修改 [PackageParamWrapper]
     *
     * 忽略在 [type] 不为 [HookEntryType.ZYGOTE] 时 [appClassLoader] 为空导致首次使用 [XposedBridge.BOOTCLASSLOADER] 装载的问题
     * @param type 当前正在进行的 Hook 类型
     * @param packageName 包名
     * @param processName 当前进程名
     * @param appClassLoader APP [ClassLoader]
     * @param appInfo APP [ApplicationInfo]
     * @param appResources APP [YukiResources]
     * @return [PackageParamWrapper] or null
     */
    private fun assignWrapper(
        type: HookEntryType,
        packageName: String?,
        processName: String? = "",
        appClassLoader: ClassLoader? = null,
        appInfo: ApplicationInfo? = null,
        appResources: YukiResources? = null
    ) = run {
        isInitializingZygote = type == HookEntryType.ZYGOTE
        if (packageParamWrappers[packageName] == null)
            if (type == HookEntryType.ZYGOTE || appClassLoader != null)
                PackageParamWrapper(
                    type = type,
                    packageName = packageName ?: SYSTEM_FRAMEWORK_NAME,
                    processName = processName ?: SYSTEM_FRAMEWORK_NAME,
                    appClassLoader = appClassLoader ?: XposedBridge.BOOTCLASSLOADER,
                    appInfo = appInfo,
                    appResources = appResources
                ).also { packageParamWrappers[packageName ?: SYSTEM_FRAMEWORK_NAME] = it }
            else null
        else packageParamWrappers[packageName]?.also {
            it.type = type
            if (packageName?.isNotBlank() == true) it.packageName = packageName
            if (processName?.isNotBlank() == true) it.processName = processName
            if (appClassLoader != null && (type == HookEntryType.ZYGOTE || appClassLoader is PathClassLoader)) it.appClassLoader = appClassLoader
            if (appInfo != null) it.appInfo = appInfo
            if (appResources != null) it.appResources = appResources
        }
    }

    /**
     * 注入当前 Hook APP (宿主) 全局生命周期
     * @param packageName 包名
     */
    private fun registerToAppLifecycle(packageName: String) {
        /** Hook [Application] 装载方法 */
        runCatching {
            if (AppLifecycleCallback.isCallbackSetUp) {
                YukiHookHelper.hook(ApplicationClass.method { name = "attach"; param(ContextClass) }, object : YukiMemberHook() {
                    override fun beforeHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            (wrapper.args?.get(0) as? Context?)?.also { AppLifecycleCallback.attachBaseContextCallback?.invoke(it, false) }
                        }.onFailure { wrapper.throwable = it }
                    }

                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            (wrapper.args?.get(0) as? Context?)?.also { AppLifecycleCallback.attachBaseContextCallback?.invoke(it, true) }
                        }.onFailure { wrapper.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTerminate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            (wrapper.instance as? Application?)?.also { AppLifecycleCallback.onTerminateCallback?.invoke(it) }
                        }.onFailure { wrapper.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onLowMemory" }, object : YukiMemberHook() {
                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            (wrapper.instance as? Application?)?.also { AppLifecycleCallback.onLowMemoryCallback?.invoke(it) }
                        }.onFailure { wrapper.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTrimMemory"; param(IntType) }, object : YukiMemberHook() {
                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            val self = wrapper.instance as? Application? ?: return
                            val type = wrapper.args?.get(0) as? Int? ?: return
                            AppLifecycleCallback.onTrimMemoryCallback?.invoke(self, type)
                        }.onFailure { wrapper.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onConfigurationChanged" }, object : YukiMemberHook() {
                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            val self = wrapper.instance as? Application? ?: return
                            val config = wrapper.args?.get(0) as? Configuration? ?: return
                            AppLifecycleCallback.onConfigurationChangedCallback?.invoke(self, config)
                        }.onFailure { wrapper.throwable = it }
                    }
                })
            }
            if (YukiHookAPI.Configs.isEnableDataChannel || AppLifecycleCallback.isCallbackSetUp)
                YukiHookHelper.hook(InstrumentationClass.method { name = "callApplicationOnCreate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(wrapper: HookParamWrapper) {
                        runCatching {
                            (wrapper.args?.get(0) as? Application?)?.also {
                                hostApplication = it
                                AppLifecycleCallback.onCreateCallback?.invoke(it)
                                AppLifecycleCallback.onReceiversCallback.takeIf { e -> e.isNotEmpty() }?.forEach { (_, e) ->
                                    if (e.first.isNotEmpty()) it.registerReceiver(object : BroadcastReceiver() {
                                        override fun onReceive(context: Context?, intent: Intent?) {
                                            if (context == null || intent == null) return
                                            if (e.first.any { e -> e == intent.action }) e.second(context, intent)
                                        }
                                    }, IntentFilter().apply { e.first.forEach { e -> addAction(e) } })
                                }
                                if (isDataChannelRegister) return
                                isDataChannelRegister = true
                                runCatching { YukiHookDataChannel.instance().register(it, packageName) }
                            }
                        }.onFailure { wrapper.throwable = it }
                    }
                })
        }
    }

    /**
     * 向 Hook APP (宿主) 注入当前 Xposed 模块的资源
     * @param hostResources 需要注入的宿主 [Resources]
     */
    internal fun injectModuleAppResources(hostResources: Resources) {
        if (injectedHostResourcesHashCodes.contains(hostResources.hashCode())) return
        if (hasXposedBridge) runCatching {
            hostResources.assets.current {
                method {
                    name = "addAssetPath"
                    param(StringType)
                }.call(moduleAppFilePath)
            }
            injectedHostResourcesHashCodes.add(hostResources.hashCode())
        }.onFailure {
            yLoggerE(msg = "Failed to inject module resources into [$hostResources]", e = it)
        } else yLoggerW(msg = "You can only inject module resources in Xposed Environment")
    }

    /** 刷新当前 Xposed 模块自身 [Resources] */
    internal fun refreshModuleAppResources() {
        dynamicModuleAppResources?.let { moduleAppResources = it }
    }

    /**
     * 监听并 Hook 当前 [ClassLoader] 的 [ClassLoader.loadClass] 方法
     * @param loader 当前 [ClassLoader]
     * @param result 回调 - ([Class] 实例对象,[Boolean] 是否 resolve)
     */
    internal fun hookClassLoader(loader: ClassLoader?, result: (clazz: Class<*>, resolve: Boolean) -> Unit) {
        runCatching {
            YukiHookHelper.hook(JavaClassLoader.method { name = "loadClass"; param(StringType, BooleanType) }, object : YukiMemberHook() {
                override fun afterHookedMember(wrapper: HookParamWrapper) {
                    if (wrapper.instance?.javaClass?.name == loader?.javaClass?.name)
                        (wrapper.result as? Class<*>?)?.also { result(it, wrapper.args?.get(1) as? Boolean ?: false) }
                }
            })
        }.onFailure { yLoggerW(msg = "Try to hook ClassLoader failed: $it") }
    }

    /**
     * Hook 模块自身激活状态和 Resources Hook 支持状态
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param loader 模块的 [ClassLoader]
     * @param isHookResourcesStatus 是否 Hook Resources 支持状态
     */
    @YukiGenerateApi
    fun hookModuleAppStatus(loader: ClassLoader?, isHookResourcesStatus: Boolean = false) {
        if (YukiHookAPI.Configs.isEnableHookModuleStatus.not()) return
        classOf<YukiHookModuleStatus>(loader).apply {
            if (isHookResourcesStatus.not()) {
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.IS_ACTIVE_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(wrapper: HookParamWrapper) = true
                })
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.GET_XPOSED_TAG_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(wrapper: HookParamWrapper) = executorName
                })
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.GET_XPOSED_VERSION_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(wrapper: HookParamWrapper) = executorVersion
                })
            } else
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.HAS_RESOURCES_HOOK_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(wrapper: HookParamWrapper) = true
                })
        }
    }

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
     * 装载 Xposed API Zygote 回调
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param sparam Xposed [IXposedHookZygoteInit.StartupParam]
     */
    @YukiGenerateApi
    fun callXposedZygoteLoaded(sparam: IXposedHookZygoteInit.StartupParam) {
        moduleAppFilePath = sparam.modulePath
        refreshModuleAppResources()
    }

    /**
     * 装载 Xposed API 回调
     *
     * 这里的入口会装载三次
     *
     * - 在 [IXposedHookZygoteInit.initZygote] 时装载
     *
     * - 在 [IXposedHookLoadPackage.handleLoadPackage] 时装载
     *
     * - 在 [IXposedHookInitPackageResources.handleInitPackageResources] 时装载
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed API 事件
     * @param isZygoteLoaded 是否为 Xposed [IXposedHookZygoteInit.initZygote]
     * @param lpparam Xposed [XC_LoadPackage.LoadPackageParam]
     * @param resparam Xposed [XC_InitPackageResources.InitPackageResourcesParam]
     */
    @YukiGenerateApi
    fun callXposedLoaded(
        isZygoteLoaded: Boolean,
        lpparam: XC_LoadPackage.LoadPackageParam? = null,
        resparam: XC_InitPackageResources.InitPackageResourcesParam? = null
    ) {
        if (isMiuiCatcherPatch(packageName = lpparam?.packageName ?: resparam?.packageName).not()) when {
            isZygoteLoaded -> assignWrapper(HookEntryType.ZYGOTE, SYSTEM_FRAMEWORK_NAME, SYSTEM_FRAMEWORK_NAME)
            lpparam != null ->
                if (isPackageLoaded(lpparam.packageName, HookEntryType.PACKAGE).not())
                    assignWrapper(HookEntryType.PACKAGE, lpparam.packageName, lpparam.processName, lpparam.classLoader, lpparam.appInfo)
                else null
            resparam != null ->
                if (isPackageLoaded(resparam.packageName, HookEntryType.RESOURCES).not())
                    assignWrapper(HookEntryType.RESOURCES, resparam.packageName, appResources = YukiResources.wrapper(resparam.res))
                else null
            else -> null
        }?.also {
            YukiHookAPI.onXposedLoaded(it)
            if (it.type == HookEntryType.PACKAGE) registerToAppLifecycle(it.packageName)
            if (it.type == HookEntryType.RESOURCES) isSupportResourcesHook = true
        }
    }

    /**
     * 当前 Hook APP (宿主) 的生命周期回调处理类
     */
    internal object AppLifecycleCallback {

        /** 是否已设置回调 */
        internal var isCallbackSetUp = false

        /** [Application.attachBaseContext] 回调 */
        internal var attachBaseContextCallback: ((Context, Boolean) -> Unit)? = null

        /** [Application.onCreate] 回调 */
        internal var onCreateCallback: (Application.() -> Unit)? = null

        /** [Application.onTerminate] 回调 */
        internal var onTerminateCallback: (Application.() -> Unit)? = null

        /** [Application.onLowMemory] 回调 */
        internal var onLowMemoryCallback: (Application.() -> Unit)? = null

        /** [Application.onTrimMemory] 回调 */
        internal var onTrimMemoryCallback: ((Application, Int) -> Unit)? = null

        /** [Application.onConfigurationChanged] 回调 */
        internal var onConfigurationChangedCallback: ((Application, Configuration) -> Unit)? = null

        /** 系统广播监听回调 */
        internal val onReceiversCallback = HashMap<String, Pair<Array<out String>, (Context, Intent) -> Unit>>()
    }
}