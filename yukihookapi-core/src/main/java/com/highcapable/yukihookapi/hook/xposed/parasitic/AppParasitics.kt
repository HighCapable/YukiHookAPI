/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2022/8/14.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("QueryPermissionsNeeded")

package com.highcapable.yukihookapi.hook.xposed.parasitic

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.AndroidAppHelper
import android.app.Application
import android.app.Instrumentation
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiProperty
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.hasMethod
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ActivityManagerClass
import com.highcapable.yukihookapi.hook.type.android.ActivityManagerNativeClass
import com.highcapable.yukihookapi.hook.type.android.ActivityTaskManagerClass
import com.highcapable.yukihookapi.hook.type.android.ActivityThreadClass
import com.highcapable.yukihookapi.hook.type.android.ApplicationClass
import com.highcapable.yukihookapi.hook.type.android.ContextClass
import com.highcapable.yukihookapi.hook.type.android.ContextImplClass
import com.highcapable.yukihookapi.hook.type.android.HandlerClass
import com.highcapable.yukihookapi.hook.type.android.IActivityManagerClass
import com.highcapable.yukihookapi.hook.type.android.IActivityTaskManagerClass
import com.highcapable.yukihookapi.hook.type.android.InstrumentationClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
import com.highcapable.yukihookapi.hook.type.android.SingletonClass
import com.highcapable.yukihookapi.hook.type.android.UserHandleClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.JavaClassLoader
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiXposedModuleStatus
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.InstrumentationDelegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl.HandlerDelegateImpl
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl.IActivityManagerProxyImpl

/**
 * 这是一个管理 APP 寄生功能的控制类
 *
 * 通过这些功能即可轻松实现对 (Xposed) 宿主环境的 [Resources] 注入以及 [Activity] 代理
 */
internal object AppParasitics {

    /** Android 系统框架名称 */
    internal const val SYSTEM_FRAMEWORK_NAME = "android"

    /** [YukiHookDataChannel] 是否已经注册 */
    private var isDataChannelRegistered = false

    /** [Activity] 代理是否已经注册 */
    private var isActivityProxyRegistered = false

    /** [ClassLoader] 是否已被 Hook */
    private var isClassLoaderHooked = false

    /** [ClassLoader] 监听回调数组 */
    private var classLoaderCallbacks = mutableMapOf<Int, (Class<*>) -> Unit>()

    /** 当前 Hook APP (宿主) 的生命周期演绎者数组 */
    private val appLifecycleActors = mutableMapOf<String, AppLifecycleActor>()

    /**
     * 当前 Hook APP (宿主) 的全局生命周期 [Application]
     *
     * 需要 [YukiHookAPI.Configs.isEnableDataChannel] or [appLifecycleActors] 不为空才会生效
     */
    internal var hostApplication: Application? = null

    /**
     * 当前环境中使用的 [ClassLoader]
     *
     * 装载位于 (Xposed) 宿主环境与模块环境时均使用当前 DEX 内的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 为空
     */
    internal val baseClassLoader get() = classOf<YukiHookAPI>().classLoader ?: error("Operating system not supported")

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
     * 获取当前宿主的 [Application]
     * @return [Application] or null
     */
    internal val currentApplication
        get() = runCatching { AndroidAppHelper.currentApplication() }.getOrNull() ?: runCatching {
            ActivityThreadClass.method { name = "currentApplication" }.ignored().get().invoke<Application>()
        }.getOrNull()

    /**
     * 获取当前宿主的 [ApplicationInfo]
     * @return [ApplicationInfo] or null
     */
    internal val currentApplicationInfo
        get() = runCatching { AndroidAppHelper.currentApplicationInfo() }.getOrNull() ?: runCatching {
            ActivityThreadClass.method { name = "currentActivityThread" }.ignored().get().call()
                ?.current(ignored = true)?.field { name = "mBoundApplication" }
                ?.current(ignored = true)?.field { name = "appInfo" }
                ?.cast<ApplicationInfo>()
        }.getOrNull()

    /**
     * 获取当前宿主的包名
     * @return [String]
     */
    internal val currentPackageName get() = currentApplicationInfo?.packageName ?: SYSTEM_FRAMEWORK_NAME

    /**
     * 获取当前宿主的进程名
     * @return [String]
     */
    internal val currentProcessName
        get() = runCatching { AndroidAppHelper.currentProcessName() }.getOrNull() ?: runCatching {
            ActivityThreadClass.method { name = "currentPackageName" }.ignored().get().string()
                .takeIf { it.isNotBlank() } ?: SYSTEM_FRAMEWORK_NAME
        }.getOrNull() ?: SYSTEM_FRAMEWORK_NAME

    /**
     * 获取指定 [packageName] 的用户 ID
     *
     * 机主为 0 - 应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同
     * @param packageName 当前包名
     * @return [Int]
     */
    internal fun findUserId(packageName: String) = runCatching {
        @Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")
        UserHandleClass.method {
            name = "getUserId"
            param(IntType)
        }.ignored().get().int(systemContext.packageManager.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES).uid)
    }.getOrNull() ?: 0

    /**
     * 监听并 Hook 当前 [ClassLoader] 的 [ClassLoader.loadClass] 方法
     * @param loader 当前 [ClassLoader]
     * @param result 回调 - ([Class] 实例对象)
     */
    internal fun hookClassLoader(loader: ClassLoader?, result: (Class<*>) -> Unit) {
        if (loader == null) return
        if (YukiXposedModule.isXposedEnvironment.not()) return YLog.innerW("You can only use hook ClassLoader method in Xposed Environment")
        classLoaderCallbacks[loader.hashCode()] = result
        if (isClassLoaderHooked) return
        runCatching {
            YukiHookHelper.hook(JavaClassLoader.method { name = "loadClass"; param(StringClass, BooleanType) }, object : YukiMemberHook() {
                override fun afterHookedMember(param: Param) {
                    param.instance?.also { loader ->
                        (param.result as? Class<*>?)?.also { classLoaderCallbacks[loader.hashCode()]?.invoke(it) }
                    }
                }
            })
            isClassLoaderHooked = true
        }.onFailure { YLog.innerW("Try to hook ClassLoader failed: $it") }
    }

    /**
     * Hook 模块 APP 相关功能 - 包括自身激活状态、Resources Hook 支持状态以及 [SharedPreferences]
     * @param loader 模块的 [ClassLoader]
     * @param type 当前正在进行的 Hook 类型
     */
    internal fun hookModuleAppRelated(loader: ClassLoader?, type: HookEntryType) {
        if (YukiHookAPI.Configs.isEnableHookSharedPreferences && type == HookEntryType.PACKAGE)
            YukiHookHelper.hook(ContextImplClass.method { name = "setFilePermissionsFromMode" }, object : YukiMemberHook() {
                override fun beforeHookedMember(param: Param) {
                    if ((param.args?.get(0) as? String?)?.endsWith("preferences.xml") == true) param.args?.set(1, 1)
                }
            })
        YukiXposedModuleStatus.className.toClassOrNull(loader)?.apply {
            if (type != HookEntryType.RESOURCES) {
                YukiHookHelper.hook(method { name = YukiXposedModuleStatus.IS_ACTIVE_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = true
                    })
                YukiHookHelper.hook(method { name = YukiXposedModuleStatus.GET_EXECUTOR_NAME_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.name
                    })
                YukiHookHelper.hook(
                    method { name = YukiXposedModuleStatus.GET_EXECUTOR_API_LEVEL_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.apiLevel
                    })
                YukiHookHelper.hook(
                    method { name = YukiXposedModuleStatus.GET_EXECUTOR_VERSION_NAME_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.versionName
                    })
                YukiHookHelper.hook(
                    method { name = YukiXposedModuleStatus.GET_EXECUTOR_VERSION_CODE_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.versionCode
                    })
            } else YukiHookHelper.hook(method { name = YukiXposedModuleStatus.IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME },
                object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = true
                })
        }
    }

    /**
     * 注入当前 Hook APP (宿主) 全局生命周期
     * @param packageName 包名
     */
    internal fun registerToAppLifecycle(packageName: String) {
        /**
         * 向当前 Hook APP (宿主) 抛出异常或打印错误日志
         * @param throwable 当前异常
         */
        fun YukiHookCallback.Param.throwToAppOrLogger(throwable: Throwable) {
            if (AppLifecycleActor.isOnFailureThrowToApp != false) this.throwable = throwable
            else YLog.innerE("An exception occurred during AppLifecycle event", e = throwable)
        }
        /** Hook [Application] 装载方法 */
        runCatching {
            if (appLifecycleActors.isNotEmpty()) {
                YukiHookHelper.hook(ApplicationClass.method { name = "attach"; param(ContextClass) }, object : YukiMemberHook() {
                    override fun beforeHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.args?.get(0) as? Context?)?.also { actor.attachBaseContextCallback?.invoke(it, false) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }

                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.args?.get(0) as? Context?)?.also { actor.attachBaseContextCallback?.invoke(it, true) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTerminate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.instance as? Application?)?.also { actor.onTerminateCallback?.invoke(it) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onLowMemory" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.instance as? Application?)?.also { actor.onLowMemoryCallback?.invoke(it) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onTrimMemory"; param(IntType) }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            val self = param.instance as? Application? ?: return
                            val type = param.args?.get(0) as? Int? ?: return
                            appLifecycleActors.forEach { (_, actor) -> actor.onTrimMemoryCallback?.invoke(self, type) }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(ApplicationClass.method { name = "onConfigurationChanged" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            val self = param.instance as? Application? ?: return
                            val config = param.args?.get(0) as? Configuration? ?: return
                            appLifecycleActors.forEach { (_, actor) -> actor.onConfigurationChangedCallback?.invoke(self, config) }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
            }
            if (YukiHookAPI.Configs.isEnableDataChannel || appLifecycleActors.isNotEmpty())
                YukiHookHelper.hook(InstrumentationClass.method { name = "callApplicationOnCreate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            (param.args?.get(0) as? Application?)?.also {
                                /**
                                 * 注册广播
                                 * @param result 回调 - ([Context] 当前实例, [Intent] 当前对象)
                                 */
                                fun IntentFilter.registerReceiver(result: (Context, Intent) -> Unit) {
                                    object : BroadcastReceiver() {
                                        override fun onReceive(context: Context?, intent: Intent?) {
                                            if (context == null || intent == null) return
                                            result(context, intent)
                                        }
                                    }.also { receiver ->
                                        /**
                                         * 从 Android 14 (及预览版) 开始
                                         * 外部广播必须声明 [Context.RECEIVER_EXPORTED]
                                         */
                                        @SuppressLint("UnspecifiedRegisterReceiverFlag")
                                        if (Build.VERSION.SDK_INT >= 33)
                                            it.registerReceiver(receiver, this, Context.RECEIVER_EXPORTED)
                                        else it.registerReceiver(receiver, this)
                                    }
                                }
                                hostApplication = it
                                appLifecycleActors.forEach { (_, actor) ->
                                    actor.onCreateCallback?.invoke(it)
                                    actor.onReceiverActionsCallbacks.takeIf { e -> e.isNotEmpty() }?.forEach { (_, e) ->
                                        if (e.first.isNotEmpty()) IntentFilter().apply {
                                            e.first.forEach { action -> addAction(action) }
                                        }.registerReceiver(e.second)
                                    }
                                    actor.onReceiverFiltersCallbacks.takeIf { e -> e.isNotEmpty() }
                                        ?.forEach { (_, e) -> e.first.registerReceiver(e.second) }
                                }
                                runCatching {
                                    /** 过滤系统框架与一系列服务组件包名不唯一的情况 */
                                    if (isDataChannelRegistered ||
                                        (currentPackageName == SYSTEM_FRAMEWORK_NAME && packageName != SYSTEM_FRAMEWORK_NAME)
                                    ) return
                                    YukiHookDataChannel.instance().register(it, packageName)
                                    isDataChannelRegistered = true
                                }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
        }
    }

    /**
     * 向 Hook APP (宿主) 注入当前 Xposed 模块的资源
     * @param hostResources 需要注入的宿主 [Resources]
     */
    internal fun injectModuleAppResources(hostResources: Resources) {
        if (YukiXposedModule.isXposedEnvironment) runCatching {
            if (currentPackageName == YukiXposedModule.modulePackageName)
                return YLog.innerE("You cannot inject module resources into yourself")
            hostResources.assets.current(ignored = true).method {
                name = "addAssetPath"
                param(StringClass)
            }.call(YukiXposedModule.moduleAppFilePath)
        }.onFailure {
            YLog.innerE("Failed to inject module resources into [$hostResources]", it)
        } else YLog.innerW("You can only inject module resources in Xposed Environment")
    }

    /**
     * 向 Hook APP (宿主) 注册当前 Xposed 模块的 [Activity]
     * @param context 当前 [Context]
     * @param proxy 代理的 [Activity]
     */
    @RequiresApi(Build.VERSION_CODES.N)
    internal fun registerModuleAppActivities(context: Context, proxy: Any?) {
        if (isActivityProxyRegistered) return
        if (YukiXposedModule.isXposedEnvironment.not()) return YLog.innerW("You can only register Activity Proxy in Xposed Environment")
        if (context.packageName == YukiXposedModule.modulePackageName) return YLog.innerE("You cannot register Activity Proxy into yourself")
        if (Build.VERSION.SDK_INT < 24) return YLog.innerE("Activity Proxy only support for Android 7.0 (API 24) or higher")
        runCatching {
            ActivityProxyConfig.apply {
                proxyIntentName = "${YukiXposedModule.modulePackageName}.ACTIVITY_PROXY_INTENT"
                proxyClassName = proxy?.let {
                    when (it) {
                        is String, is CharSequence -> it.toString()
                        is Class<*> -> it.name
                        else -> error("This proxy [$it] type is not allowed")
                    }
                }?.takeIf { it.isNotBlank() } ?: context.packageManager?.runCatching {
                    @Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")
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
                        if (current().name != HandlerDelegateImpl.wrapperClassName) set(HandlerDelegateImpl.createWrapper(baseInstance = this))
                    } ?: set(HandlerDelegateImpl.createWrapper())
                }
            }
            /** Patched [ActivityManager] */
            runCatching {
                runCatching {
                    ActivityManagerNativeClass.field { name = "gDefault" }.ignored().get().any()
                }.getOrNull() ?: ActivityManagerClass.field { name = "IActivityManagerSingleton" }.ignored().get().any()
            }.getOrNull()?.also { default ->
                SingletonClass.field { name = "mInstance" }.ignored().result {
                    get(default).apply { any()?.also { set(IActivityManagerProxyImpl.createWrapper(IActivityManagerClass, it)) } }
                    ActivityTaskManagerClass?.field { name = "IActivityTaskManagerSingleton" }?.ignored()?.get()?.any()?.also { singleton ->
                        SingletonClass.method { name = "get" }.ignored().get(singleton).call()
                        get(singleton).apply { any()?.also { set(IActivityManagerProxyImpl.createWrapper(IActivityTaskManagerClass, it)) } }
                    }
                }
            }
            isActivityProxyRegistered = true
        }.onFailure { YLog.innerE("Activity Proxy initialization failed because got an exception", it) }
    }

    /**
     * 当前 Hook APP (宿主) 的生命周期演绎者
     */
    internal class AppLifecycleActor {

        internal companion object {

            /** 是否在发生异常时将异常抛出给宿主 */
            internal var isOnFailureThrowToApp: Boolean? = null

            /**
             * 获取、创建新的 [AppLifecycleActor]
             * @param instance 实例
             * @return [AppLifecycleActor]
             */
            internal fun get(instance: Any) =
                appLifecycleActors[instance.toString()] ?: AppLifecycleActor().apply { appLifecycleActors[instance.toString()] = this }
        }

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
        internal val onReceiverActionsCallbacks = mutableMapOf<String, Pair<Array<out String>, (Context, Intent) -> Unit>>()

        /** 系统广播监听回调 */
        internal val onReceiverFiltersCallbacks = mutableMapOf<String, Pair<IntentFilter, (Context, Intent) -> Unit>>()
    }
}