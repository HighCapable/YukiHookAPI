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
 * This file is Created by fankes on 2022/8/14.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("QueryPermissionsNeeded")

package com.highcapable.yukihookapi.hook.xposed.parasitic

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.app.Instrumentation
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.*
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.type.android.*
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClassLoader
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiHookHelper
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiMemberHook
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiHookModuleStatus
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.HandlerDelegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.IActivityManagerProxy
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.InstrumentationDelegate

/**
 * 这是一个管理 APP 寄生功能的控制类
 *
 * 通过这些功能即可轻松实现对 (Xposed) 宿主环境的 [Resources] 注入以及 [Activity] 代理
 */
internal object AppParasitics {

    /** [YukiHookDataChannel] 是否已经注册 */
    private var isDataChannelRegistered = false

    /** [Activity] 代理是否已经注册 */
    private var isActivityProxyRegistered = false

    /** [ClassLoader] 是否已被 Hook */
    private var isClassLoaderHooked = false

    /** [ClassLoader] 监听回调数组 */
    private var classLoaderCallbacks = HashMap<Int, (Class<*>) -> Unit>()

    /**
     * 当前 Hook APP (宿主) 的全局生命周期 [Application]
     *
     * 需要 [YukiHookAPI.Configs.isEnableDataChannel] or [AppLifecycleCallback.isCallbackSetUp] 才会生效
     */
    internal var hostApplication: Application? = null

    /** 当前 Xposed 模块自身 APK 路径 */
    internal var moduleAppFilePath = ""

    /** 当前 Xposed 模块自身 [Resources] */
    internal var moduleAppResources: YukiModuleResources? = null

    /**
     * 当前环境中使用的 [ClassLoader]
     *
     * 装载位于 (Xposed) 宿主环境与模块环境时均使用当前 DEX 内的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 为空
     */
    internal val baseClassLoader get() = classOf<YukiHookAPI>().classLoader ?: error("Operating system not supported")

    /**
     * 获取当前 Xposed 模块自身动态 [Resources]
     * @return [YukiModuleResources] or null
     */
    internal val dynamicModuleAppResources get() = runCatching { YukiModuleResources.wrapper(moduleAppFilePath) }.getOrNull()

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
     * 获取指定 [packageName] 的用户 ID
     *
     * 机主为 0 - 应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同
     * @param packageName 当前包名
     * @return [Int]
     */
    internal fun findUserId(packageName: String) = runCatching {
        UserHandleClass.method {
            name = "getUserId"
            param(IntType)
        }.ignored().get().int(systemContext.packageManager.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES).uid)
    }.getOrNull() ?: 0

    /**
     * Hook 模块 APP 相关功能 - 包括自身激活状态、Resources Hook 支持状态以及 [SharedPreferences]
     *
     * - ❗装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param loader 模块的 [ClassLoader]
     * @param type 当前正在进行的 Hook 类型
     */
    internal fun hookModuleAppRelated(loader: ClassLoader?, type: HookEntryType) {
        if (YukiHookAPI.Configs.isEnableHookSharedPreferences && type == HookEntryType.PACKAGE)
            YukiHookHelper.hook(ContextImplClass.method { name = "setFilePermissionsFromMode" }, object : YukiMemberHook() {
                override fun beforeHookedMember(param: Param) {
                    if ((param.args?.get(0) as? String?)?.endsWith(suffix = "preferences.xml") == true) param.args?.set(1, 1)
                }
            })
        if (YukiHookAPI.Configs.isEnableHookModuleStatus) classOf<YukiHookModuleStatus>(loader).apply {
            if (type != HookEntryType.RESOURCES) {
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.IS_ACTIVE_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = true
                })
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.GET_XPOSED_TAG_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = YukiHookBridge.executorName
                })
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.GET_XPOSED_VERSION_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = YukiHookBridge.executorVersion
                })
            } else
                YukiHookHelper.hook(method { name = YukiHookModuleStatus.HAS_RESOURCES_HOOK_METHOD_NAME }, object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = true
                })
        }
    }

    /**
     * 注入当前 Hook APP (宿主) 全局生命周期
     * @param packageName 包名
     */
    internal fun registerToAppLifecycle(packageName: String) {
        /** Hook [Application] 装载方法 */
        runCatching {
            if (AppLifecycleCallback.isCallbackSetUp) {
                YukiHookHelper.hook(ApplicationClass.method { name = "attach"; param(ContextClass) }, object : YukiMemberHook() {
                    override fun beforeHookedMember(param: Param) {
                        runCatching {
                            (param.args?.get(0) as? Context?)?.also { AppLifecycleCallback.attachBaseContextCallback?.invoke(it, false) }
                        }.onFailure { param.throwable = it }
                    }

                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            (param.args?.get(0) as? Context?)?.also { AppLifecycleCallback.attachBaseContextCallback?.invoke(it, true) }
                        }.onFailure { param.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTerminate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            (param.instance as? Application?)?.also { AppLifecycleCallback.onTerminateCallback?.invoke(it) }
                        }.onFailure { param.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onLowMemory" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            (param.instance as? Application?)?.also { AppLifecycleCallback.onLowMemoryCallback?.invoke(it) }
                        }.onFailure { param.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTrimMemory"; param(IntType) }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            val self = param.instance as? Application? ?: return
                            val type = param.args?.get(0) as? Int? ?: return
                            AppLifecycleCallback.onTrimMemoryCallback?.invoke(self, type)
                        }.onFailure { param.throwable = it }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onConfigurationChanged" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            val self = param.instance as? Application? ?: return
                            val config = param.args?.get(0) as? Configuration? ?: return
                            AppLifecycleCallback.onConfigurationChangedCallback?.invoke(self, config)
                        }.onFailure { param.throwable = it }
                    }
                })
            }
            if (YukiHookAPI.Configs.isEnableDataChannel || AppLifecycleCallback.isCallbackSetUp)
                YukiHookHelper.hook(InstrumentationClass.method { name = "callApplicationOnCreate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            (param.args?.get(0) as? Application?)?.also {
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
                                runCatching {
                                    /** 过滤系统框架与一系列服务组件包名不唯一的情况 */
                                    if (isDataChannelRegistered ||
                                        (YukiHookAppHelper.currentPackageName() == YukiHookBridge.SYSTEM_FRAMEWORK_NAME &&
                                                packageName != YukiHookBridge.SYSTEM_FRAMEWORK_NAME)
                                    ) return
                                    YukiHookDataChannel.instance().register(it, packageName)
                                    isDataChannelRegistered = true
                                }
                            }
                        }.onFailure { param.throwable = it }
                    }
                })
        }
    }

    /**
     * 监听并 Hook 当前 [ClassLoader] 的 [ClassLoader.loadClass] 方法
     * @param loader 当前 [ClassLoader]
     * @param result 回调 - ([Class] 实例对象)
     */
    internal fun hookClassLoader(loader: ClassLoader?, result: (Class<*>) -> Unit) {
        if (loader == null) return
        if (YukiHookBridge.hasXposedBridge.not()) return yLoggerW(msg = "You can only use hook ClassLoader method in Xposed Environment")
        classLoaderCallbacks[loader.hashCode()] = result
        if (isClassLoaderHooked) return
        runCatching {
            YukiHookHelper.hook(JavaClassLoader.method { name = "loadClass"; param(StringType, BooleanType) }, object : YukiMemberHook() {
                override fun afterHookedMember(param: Param) {
                    param.instance?.also { loader ->
                        (param.result as? Class<*>?)?.also { classLoaderCallbacks[loader.hashCode()]?.invoke(it) }
                    }
                }
            })
            isClassLoaderHooked = true
        }.onFailure { yLoggerW(msg = "Try to hook ClassLoader failed: $it") }
    }

    /**
     * 向 Hook APP (宿主) 注入当前 Xposed 模块的资源
     * @param hostResources 需要注入的宿主 [Resources]
     */
    internal fun injectModuleAppResources(hostResources: Resources) {
        if (YukiHookBridge.hasXposedBridge) runCatching {
            hostResources.assets.current(ignored = true).method { name = "addAssetPath"; param(StringType) }.call(moduleAppFilePath)
        }.onFailure {
            yLoggerE(msg = "Failed to inject module resources into [$hostResources]", e = it)
        } else yLoggerW(msg = "You can only inject module resources in Xposed Environment")
    }

    /** 刷新当前 Xposed 模块自身 [Resources] */
    internal fun refreshModuleAppResources() {
        dynamicModuleAppResources?.let { moduleAppResources = it }
    }

    /**
     * 向 Hook APP (宿主) 注册当前 Xposed 模块的 [Activity]
     * @param context 当前 [Context]
     * @param proxy 代理的 [Activity]
     */
    internal fun registerModuleAppActivities(context: Context, proxy: Any?) {
        if (isActivityProxyRegistered) return
        if (YukiHookBridge.hasXposedBridge.not()) return yLoggerW(msg = "You can only register Activity Proxy in Xposed Environment")
        runCatching {
            ActivityProxyConfig.apply {
                proxyIntentName = "${YukiHookBridge.modulePackageName}.ACTIVITY_PROXY_INTENT"
                proxyClassName = proxy?.let {
                    when (it) {
                        is String, is CharSequence -> it.toString()
                        is Class<*> -> it.name
                        else -> error("This proxy [$it] type is not allowed")
                    }
                }?.takeIf { it.isNotBlank() } ?: context.packageManager?.runCatching {
                    queryIntentActivities(getLaunchIntentForPackage(context.packageName)!!, 0).first().activityInfo.name
                }?.getOrNull() ?: ""
                if ((proxyClassName.hasClass(context.classLoader) && proxyClassName.toClass(context.classLoader).hasMethod {
                        name = "setIntent"; param(IntentClass); superClass()
                    }).not()
                ) (if (proxyClassName.isBlank()) error("Cound not got launch intent for package \"${context.packageName}\"")
                else error("Could not found \"$proxyClassName\" or Class is not a type of Activity"))
            }
            /** Patched [Instrumentation] */
            ActivityThreadClass.field { name = "sCurrentActivityThread" }.ignored().get().any()?.current(ignored = true) {
                method { name = "getInstrumentation" }
                    .invoke<Instrumentation>()
                    ?.also { field { name = "mInstrumentation" }.set(InstrumentationDelegate.wrapper(it)) }
                HandlerClass.field { name = "mCallback" }.get(field { name = "mH" }.any()).apply {
                    cast<Handler.Callback?>()?.apply {
                        if (current().name != classOf<HandlerDelegate>().name) set(HandlerDelegate.wrapper(baseInstance = this))
                    } ?: set(HandlerDelegate.wrapper())
                }
            }
            /** Patched [ActivityManager] */
            runCatching {
                runCatching {
                    ActivityManagerNativeClass.field { name = "gDefault" }.ignored().get().any()
                }.getOrNull() ?: ActivityManagerClass.field { name = "IActivityManagerSingleton" }.ignored().get().any()
            }.getOrNull()?.also { default ->
                SingletonClass.field { name = "mInstance" }.ignored().result {
                    get(default).apply { any()?.also { set(IActivityManagerProxy.wrapper(IActivityManagerClass, it)) } }
                    ActivityTaskManagerClass.field { name = "IActivityTaskManagerSingleton" }.ignored().get().any().also { singleton ->
                        SingletonClass.method { name = "get" }.ignored().get(singleton).call()
                        get(singleton).apply { any()?.also { set(IActivityManagerProxy.wrapper(IActivityTaskManagerClass, it)) } }
                    }
                }
            }
            isActivityProxyRegistered = true
        }.onFailure { yLoggerE(msg = "Activity Proxy initialization failed because got an Exception", e = it) }
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