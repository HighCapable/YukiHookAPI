/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Handler
import android.os.UserHandle
import androidx.annotation.RequiresApi
import com.highcapable.betterandroid.system.extension.component.registerReceiver
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.lazyClass
import com.highcapable.kavaref.extension.lazyClassOrNull
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiProperty
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.core.api.reflect.AndroidHiddenApiBypassResolver
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiXposedModuleStatus
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics.appLifecycleActors
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.InstrumentationDelegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl.HandlerDelegateImpl
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl.IActivityManagerProxyImpl

/**
 * Controller for app parasitic features.
 *
 * These features provide [Resources] injection and [Activity] proxies for a (Xposed) host environment.
 */
internal object AppParasitics {

    /** The Android system framework name. */
    internal const val SYSTEM_FRAMEWORK_NAME = "android"

    /** Whether [YukiHookDataChannel] has been registered. */
    private var isDataChannelRegistered = false

    /** Whether the [Activity] proxy has been registered. */
    private var isActivityProxyRegistered = false

    /** Whether the [ClassLoader] has been hooked. */
    private var isClassLoaderHooked = false

    /** The [ClassLoader] listener callbacks. */
    private var classLoaderCallbacks = mutableMapOf<Int, (Class<*>) -> Unit>()

    /** Lifecycle actors for the current host app. */
    private val appLifecycleActors = mutableMapOf<String, AppLifecycleActor>()

    private val ActivityThreadClass by lazyClass("android.app.ActivityThread")
    private val ContextImplClass by lazyClass("android.app.ContextImpl")
    private val ActivityManagerNativeClass by lazyClass("android.app.ActivityManagerNative")
    private val SingletonClass by lazyClass("android.util.Singleton")
    private val IActivityManagerClass by lazyClass("android.app.IActivityManager")
    private val ActivityTaskManagerClass by lazyClassOrNull("android.app.ActivityTaskManager")
    private val IActivityTaskManagerClass by lazyClass("android.app.IActivityTaskManager")

    /**
     * Global lifecycle [Application] of the current host app.
     *
     * This is effective only when [YukiHookAPI.Configs.isEnableDataChannel] is enabled or [appLifecycleActors] is not empty.
     */
    internal var hostApplication: Application? = null

    /**
     * The [ClassLoader] used in the current environment.
     *
     * Both the (Xposed) host and module environments use the [ClassLoader] from the current DEX.
     * @return [ClassLoader]
     * @throws IllegalStateException if the [ClassLoader] is null.
     */
    internal val baseClassLoader get() = classOf<YukiHookAPI>().classLoader ?: error("Operating system not supported")

    /**
     * Gets the current system framework [Context].
     * @return [Context] a ContextImpl instance or null.
     */
    internal val systemContext get(): Context? {
        val scope = ActivityThreadClass.resolve()
            .processor(AndroidHiddenApiBypassResolver.get())
            .optional(silent = true)
        val current = scope.firstMethodOrNull {
            name = "currentActivityThread"
            emptyParameters()
        }?.invoke()
        return scope.firstMethodOrNull {
            name = "getSystemContext"
            emptyParameters()
        }?.of(current)?.invokeQuietly<Context>()
    }

    /**
     * Gets the current host [Application].
     * @return [Application] or null.
     */
    internal val currentApplication
        get() = runCatching { AndroidAppHelper.currentApplication() }.getOrNull()
            ?: ActivityThreadClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstMethodOrNull { name = "currentApplication" }
                ?.invoke<Application>()

    /**
     * Gets the current host [ApplicationInfo].
     * @return [ApplicationInfo] or null.
     */
    internal val currentApplicationInfo
        get() = runCatching { AndroidAppHelper.currentApplicationInfo() }.getOrNull()
            ?: let {
                val scope = ActivityThreadClass.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional(silent = true)
                val current = scope.firstMethodOrNull {
                    name = "currentActivityThread"
                    emptyParameters()
                }?.invoke()
                val currentScope = current?.asResolver()?.optional(silent = true)
                val mBoundApplication = currentScope?.firstFieldOrNull { name = "mBoundApplication" }?.get()
                val appScope = mBoundApplication?.asResolver()?.optional(silent = true)
                appScope?.firstFieldOrNull { name = "appInfo" }?.get<ApplicationInfo>()
            }

    /**
     * Gets the current host package name.
     * @return [String]
     */
    internal val currentPackageName get() = currentApplicationInfo?.packageName ?: SYSTEM_FRAMEWORK_NAME

    /**
     * Gets the current host process name.
     * @return [String]
     */
    internal val currentProcessName
        get() = runCatching { AndroidAppHelper.currentProcessName() }.getOrNull()
            ?: ActivityThreadClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstMethodOrNull { name = "currentPackageName" }
                ?.invoke<String>()
                ?.takeIf { it.isNotBlank() }
            ?: SYSTEM_FRAMEWORK_NAME

    /**
     * Gets the user ID for [packageName].
     *
     * The owner is 0. Cloned apps and work profiles have environment-dependent IDs.
     * @param packageName the current package name.
     * @return [Int]
     */
    internal fun findUserId(packageName: String): Int {
        val uid = systemContext?.packageManager?.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES)?.uid
            ?: return 0
        return UserHandle::class.resolve()
            .processor(AndroidHiddenApiBypassResolver.get())
            .optional(silent = true)
            .firstMethodOrNull {
                name = "getUserId"
                parameters(Int::class)
            }?.invoke<Int>(uid) ?: 0
    }

    /**
     * Listens to and hooks [ClassLoader.loadClass] on the current [ClassLoader].
     * @param loader the current [ClassLoader].
     * @param result the callback receiving a [Class] instance.
     */
    internal fun hookClassLoader(loader: ClassLoader?, result: (Class<*>) -> Unit) {
        if (loader == null) return
        if (YukiXposedModule.isXposedEnvironment.not()) return YLog.innerW("You can only use hook ClassLoader method in Xposed Environment")
        classLoaderCallbacks[loader.hashCode()] = result
        if (isClassLoaderHooked) return
        val loadClass = ClassLoader::class.resolve()
            .optional(silent = true)
            .firstMethodOrNull {
                name = "loadClass"
                parameters(String::class, Boolean::class)
            }
        runCatching {
            YukiHookHelper.hook(loadClass, object : YukiMemberHook() {
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
     * Hooks module app features, including its activation status, Resources Hook support status, and [SharedPreferences].
     * @param loader the module [ClassLoader].
     * @param type the current Hook type.
     */
    internal fun hookModuleAppRelated(loader: ClassLoader?, type: HookEntryType) {
        if (YukiHookAPI.Configs.isEnableHookSharedPreferences && type == HookEntryType.PACKAGE)
            YukiHookHelper.hook(
                ContextImplClass.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional(silent = true)
                    .firstMethodOrNull { name = "setFilePermissionsFromMode" },
                object : YukiMemberHook() {
                    override fun beforeHookedMember(param: Param) {
                        if ((param.args?.get(0) as? String?)?.endsWith("preferences.xml") == true) param.args?.set(1, 1)
                    }
                }
            )
        YukiXposedModuleStatus.className.toClassOrNull(loader)?.resolve()?.optional(silent = true)?.apply {
            if (type != HookEntryType.RESOURCES) {
                YukiHookHelper.hook(firstMethodOrNull { name = YukiXposedModuleStatus.IS_ACTIVE_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = true
                    })
                YukiHookHelper.hook(firstMethodOrNull { name = YukiXposedModuleStatus.GET_EXECUTOR_NAME_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.name
                    })
                YukiHookHelper.hook(
                    firstMethodOrNull { name = YukiXposedModuleStatus.GET_EXECUTOR_API_LEVEL_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.apiLevel
                    })
                YukiHookHelper.hook(
                    firstMethodOrNull { name = YukiXposedModuleStatus.GET_EXECUTOR_VERSION_NAME_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.versionName
                    })
                YukiHookHelper.hook(
                    firstMethodOrNull { name = YukiXposedModuleStatus.GET_EXECUTOR_VERSION_CODE_METHOD_NAME },
                    object : YukiMemberReplacement() {
                        override fun replaceHookedMember(param: Param) = HookApiProperty.versionCode
                    })
            } else YukiHookHelper.hook(firstMethodOrNull { name = YukiXposedModuleStatus.IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME },
                object : YukiMemberReplacement() {
                    override fun replaceHookedMember(param: Param) = true
                })
        }
    }

    /**
     * Injects the global lifecycle of the current host app.
     * @param packageName the package name.
     */
    internal fun registerToAppLifecycle(packageName: String) {
        /**
         * Throws an exception to the current host app or prints an error log.
         * @param throwable the current exception.
         */
        fun YukiHookCallback.Param.throwToAppOrLogger(throwable: Throwable) {
            if (AppLifecycleActor.isOnFailureThrowToApp != false) this.throwable = throwable
            else YLog.innerE("An exception occurred during AppLifecycle event", e = throwable)
        }
        // Hooks [Application] loading methods.
        runCatching {
            if (appLifecycleActors.isNotEmpty()) Application::class.resolve().optional(silent = true).apply {
                YukiHookHelper.hook(firstMethod { name = "attach"; parameters(Context::class) }, object : YukiMemberHook() {
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
                YukiHookHelper.hook(firstMethod { name = "onTerminate" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.instance as? Application?)?.also { actor.onTerminateCallback?.invoke(it) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(firstMethod { name = "onLowMemory" }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            appLifecycleActors.forEach { (_, actor) ->
                                (param.instance as? Application?)?.also { actor.onLowMemoryCallback?.invoke(it) }
                            }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(firstMethod { name = "onTrimMemory"; parameters(Int::class) }, object : YukiMemberHook() {
                    override fun afterHookedMember(param: Param) {
                        runCatching {
                            val self = param.instance as? Application? ?: return
                            val type = param.args?.get(0) as? Int? ?: return
                            appLifecycleActors.forEach { (_, actor) -> actor.onTrimMemoryCallback?.invoke(self, type) }
                        }.onFailure { param.throwToAppOrLogger(it) }
                    }
                })
                YukiHookHelper.hook(firstMethod { name = "onConfigurationChanged" }, object : YukiMemberHook() {
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
                YukiHookHelper.hook(
                    Instrumentation::class.resolve().optional(silent = true).firstMethodOrNull { name = "callApplicationOnCreate" },
                    object : YukiMemberHook() {
                        override fun afterHookedMember(param: Param) {
                            runCatching {
                                (param.args?.get(0) as? Application?)?.also {
                                    /**
                                     * Registers a broadcast receiver.
                                     * @param result the callback receiving the current [Context] and [Intent].
                                     */
                                    fun IntentFilter.registerReceiver(result: (Context, Intent) -> Unit) {
                                        it.registerReceiver(filter = this, exported = true) { context, intent ->
                                            result(context, intent)
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
                                        // Filters cases where the system framework and service components do not have unique package names.
                                        if (isDataChannelRegistered ||
                                            (currentPackageName == SYSTEM_FRAMEWORK_NAME && packageName != SYSTEM_FRAMEWORK_NAME)
                                        ) return
                                        YukiHookDataChannel.instance().register(it, packageName)
                                        isDataChannelRegistered = true
                                    }
                                }
                            }.onFailure { param.throwToAppOrLogger(it) }
                        }
                    }
                )
        }
    }

    /**
     * Injects the current Xposed module's resources into the host app.
     * @param hostResources the host [Resources] to inject.
     */
    internal fun injectModuleAppResources(hostResources: Resources) {
        if (YukiXposedModule.isXposedEnvironment) runCatching {
            if (currentPackageName == YukiXposedModule.modulePackageName)
                return YLog.innerE("You cannot inject module resources into yourself")
            hostResources.assets.asResolver()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstMethodOrNull {
                    name = "addAssetPath"
                    parameters(String::class)
                }?.invoke(YukiXposedModule.moduleAppFilePath)
        }.onFailure {
            YLog.innerE("Failed to inject module resources into [$hostResources]", it)
        } else YLog.innerW("You can only inject module resources in Xposed Environment")
    }

    /**
     * Registers the current Xposed module's [Activity] in the host app.
     * @param context the current [Context].
     * @param proxy the proxy [Activity].
     */
    @RequiresApi(AndroidVersion.N)
    internal fun registerModuleAppActivities(context: Context, proxy: Any?) {
        if (isActivityProxyRegistered) return
        if (YukiXposedModule.isXposedEnvironment.not()) return YLog.innerW("You can only register Activity Proxy in Xposed Environment")
        if (context.packageName == YukiXposedModule.modulePackageName) return YLog.innerE("You cannot register Activity Proxy into yourself")
        @SuppressLint("ObsoleteSdkInt")
        if (AndroidVersion.isAtMost(AndroidVersion.N)) return YLog.innerE("Activity Proxy only support for Android 7.0 (API 24) or higher")
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
                val checkIsActivity = proxyClassName.toClassOrNull(context.classLoader)
                    ?.resolve()?.optional(silent = true)
                    ?.firstMethodOrNull {
                        name = "setIntent"
                        parameters(Intent::class)
                        superclass()
                    } != null
                if (!checkIsActivity) {
                    if (proxyClassName.isBlank()) error("Cound not got launch intent for package \"${context.packageName}\"")
                    else error("Could not found \"$proxyClassName\" or Class is not a type of Activity")
                }
            }
            // Patched [Instrumentation]
            val sCurrentActivityThread = ActivityThreadClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "sCurrentActivityThread" }
                ?.get()
            val instrumentation = sCurrentActivityThread?.asResolver()
                ?.processor(AndroidHiddenApiBypassResolver.get())
                ?.optional(silent = true)
                ?.firstMethodOrNull { name = "getInstrumentation" }
                ?.invoke<Instrumentation>() ?: error("Could not found Instrumentation in ActivityThread")
            sCurrentActivityThread.asResolver()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "mInstrumentation" }
                ?.set(InstrumentationDelegate.wrapper(instrumentation))
            val mH = sCurrentActivityThread.asResolver()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "mH" }
                ?.get<Handler>() ?: error("Could not found mH in ActivityThread")
            val mCallbackResolver = Handler::class.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "mCallback" }
                ?.of(mH)
            val mCallback = mCallbackResolver?.get<Handler.Callback>()
            if (mCallback != null) {
                if (mCallback.javaClass.name != HandlerDelegateImpl.wrapperClassName)
                    mCallbackResolver.set(HandlerDelegateImpl.createWrapper(mCallback))
            } else mCallbackResolver?.set(HandlerDelegateImpl.createWrapper())
            // Patched [ActivityManager]
            val gDefault = ActivityManagerNativeClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "gDefault" }
                ?.get()
                ?: ActivityManager::class.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional(silent = true)
                    .firstFieldOrNull {
                        name = "IActivityManagerSingleton"
                    }?.get()
            val mInstanceResolver = SingletonClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "mInstance" }
                ?.of(gDefault)
            val mInstance = mInstanceResolver?.get()
            mInstance?.let {
                mInstanceResolver.set(IActivityManagerProxyImpl.createWrapper(IActivityManagerClass, it))
            }
            val singleton = ActivityTaskManagerClass?.resolve()
                ?.processor(AndroidHiddenApiBypassResolver.get())
                ?.optional(silent = true)
                ?.firstFieldOrNull { name = "IActivityTaskManagerSingleton" }
                ?.get()
            singleton?.let {
                SingletonClass.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional(silent = true)
                    .firstMethodOrNull { name = "get" }
                    ?.of(singleton)
                    ?.invokeQuietly()
                val mInstanceResolver2 = mInstanceResolver?.copy()?.of(singleton)
                val mInstance2 = mInstanceResolver2?.get()
                mInstance2?.let {
                    mInstanceResolver2.set(IActivityManagerProxyImpl.createWrapper(IActivityTaskManagerClass, it))
                }
            }
            isActivityProxyRegistered = true
        }.onFailure { YLog.innerE("Activity Proxy initialization failed because got an exception", it) }
    }

    /**
     * Lifecycle actor for the current host app.
     */
    internal class AppLifecycleActor {

        internal companion object {

            /** Whether to throw an exception to the host app when a failure occurs. */
            internal var isOnFailureThrowToApp: Boolean? = null

            /**
             * Gets or creates a new [AppLifecycleActor].
             * @param instance the source instance.
             * @return [AppLifecycleActor]
             */
            internal fun get(instance: Any) =
                appLifecycleActors[instance.toString()] ?: AppLifecycleActor().apply { appLifecycleActors[instance.toString()] = this }
        }

        /** [Application.attachBaseContext] callback. */
        internal var attachBaseContextCallback: ((Context, Boolean) -> Unit)? = null

        /** [Application.onCreate] callback. */
        internal var onCreateCallback: (Application.() -> Unit)? = null

        /** [Application.onTerminate] callback. */
        internal var onTerminateCallback: (Application.() -> Unit)? = null

        /** [Application.onLowMemory] callback. */
        internal var onLowMemoryCallback: (Application.() -> Unit)? = null

        /** [Application.onTrimMemory] callback. */
        internal var onTrimMemoryCallback: ((Application, Int) -> Unit)? = null

        /** [Application.onConfigurationChanged] callback. */
        internal var onConfigurationChangedCallback: ((Application, Configuration) -> Unit)? = null

        /** System broadcast listener callbacks. */
        internal val onReceiverActionsCallbacks = mutableMapOf<String, Pair<Array<out String>, (Context, Intent) -> Unit>>()

        /** System broadcast listener callbacks. */
        internal val onReceiverFiltersCallbacks = mutableMapOf<String, Pair<IntentFilter, (Context, Intent) -> Unit>>()
    }
}