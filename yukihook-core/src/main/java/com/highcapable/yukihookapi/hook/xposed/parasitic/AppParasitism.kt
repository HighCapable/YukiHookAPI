/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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

import android.app.Activity
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
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.os.UserHandle
import androidx.annotation.RequiresApi
import com.highcapable.betterandroid.system.extension.component.getParcelableExtraCompat
import com.highcapable.betterandroid.system.extension.component.registerReceiver
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.createInstanceAsTypeOrNull
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.kavaref.extension.isSubclassOf
import com.highcapable.kavaref.extension.lazyClass
import com.highcapable.kavaref.extension.toClass
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.reflect.AndroidHiddenApiBypassResolver
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle.AppLifecycleBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.ModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import kotlin.reflect.KClass

/**
 * Controller for app parasitism features.
 *
 * These features provide [Resources] injection and [Activity] proxies for host environment.
 */
internal object AppParasitism {

    /** The Android framework package name. */
    const val FRAMEWORK_PACKAGE_NAME = "android"

    /** Whether [YukiHookDataChannel] has been registered. */
    private var isDataChannelRegistered = false

    /** Whether the [Activity] proxy has been registered. */
    private var isActivityProxyRegistered = false

    /** Whether host lifecycle methods have been hooked. */
    private var isAppLifecycleInstalled = false

    /** Whether [Instrumentation.callApplicationOnCreate] has been hooked. */
    private var isApplicationOnCreateInstalled = false

    /** Whether system server initialization is waiting for the main looper. */
    private var isSystemServerInitializationPending = false

    /** Lifecycle callbacks for the current host. */
    @Volatile
    private var lifecycles = emptyList<AppLifecycleBridge>()

    /** Lifecycle callbacks that have already received the host [Application]. */
    private val initializedLifecycles = mutableSetOf<AppLifecycleBridge>()

    /** Package owning the first [Application] in this process. */
    private var lifecyclePackageName = ""

    private val ActivityThreadClass by lazyClass("android.app.ActivityThread")
    private val ContextImplClass by lazyClass("android.app.ContextImpl")

    /**
     * Global lifecycle [Application] of the current host.
     *
     * This is effective only when [YukiHook.Config.dataChannel] is enabled or [lifecycles] is not empty.
     */
    var hostApplication: Application? = null

    /**
     * The [ClassLoader] used in the current environment.
     *
     * Both the host and module environments use the [ClassLoader] from the current DEX.
     * @return [ClassLoader]
     * @throws IllegalStateException if the [ClassLoader] is null.
     */
    val baseClassLoader get() = classOf<YukiHook>().classLoader ?: error("Operating system not supported.")

    /**
     * Gets the current system server [Context].
     * @return [Context] a `ContextImpl` instance or null.
     */
    val systemContext get(): Context? {
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
    val currentApplication get() = hostApplication ?: ActivityThreadClass.resolve()
        .processor(AndroidHiddenApiBypassResolver.get())
        .optional(silent = true)
        .firstMethodOrNull { name = "currentApplication" }
        ?.invoke<Application>()

    /**
     * Gets the current host [ApplicationInfo].
     * @return [ApplicationInfo] or null.
     */
    val currentApplicationInfo get() = currentApplication?.applicationInfo ?: run {
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
    val currentPackageName get() = currentApplicationInfo?.packageName ?: FRAMEWORK_PACKAGE_NAME

    /**
     * Gets the current host process name.
     * @return [String]
     */
    val currentProcessName get() = ActivityThreadClass.resolve()
        .processor(AndroidHiddenApiBypassResolver.get())
        .optional(silent = true)
        .firstMethodOrNull { name = "currentProcessName" }
        ?.invoke<String>()
        ?.takeIf { it.isNotBlank() }
        ?: FRAMEWORK_PACKAGE_NAME

    /**
     * Gets the user ID for [packageName].
     *
     * The owner is 0. Cloned apps and work profiles have environment-dependent IDs.
     * @param packageName the current package name.
     * @return [Int]
     */
    fun findUserId(packageName: String): Int {
        val uid = systemContext?.packageManager
            ?.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES)
            ?.uid ?: return 0

        return UserHandle::class.resolve()
            .processor(AndroidHiddenApiBypassResolver.get())
            .optional(silent = true)
            .firstMethodOrNull {
                name = "getUserId"
                parameters(Int::class)
            }?.invoke<Int>(uid) ?: 0
    }

    /**
     * Hooks legacy module activation status and [SharedPreferences].
     * @param loader the module [ClassLoader].
     */
    fun hookLegacyModuleRelated(loader: ClassLoader?) {
        if (YukiHookBridge.runtime != YukiHookRuntime.XPOSED_82) return

        if (YukiHook.Config.hookSharedPreferences) ContextImplClass.resolve()
            .processor(AndroidHiddenApiBypassResolver.get())
            .optional(silent = true)
            .firstMethodOrNull { name = "setFilePermissionsFromMode" }?.let {
                YukiHookHelper.hook(
                    it, object : ClassicHookerBridge {
                        override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                            if ((param.args[0] as? String?)?.endsWith("preferences.xml") == true) param.args[1] = 1
                        }
                    }
                )
            }
        YukiHookBridge.installLegacyModuleStatusHooks(loader)
    }

    /**
     * Registers lifecycle callbacks for the first [Application] in this process.
     * @param packageName the package owning the [Application].
     * @param lifecycle the lifecycle callbacks.
     * @param entryType the current hook entry type.
     */
    @Synchronized
    fun registerAppLifecycle(packageName: String, lifecycle: AppLifecycleBridge, entryType: HookEntryType) {
        if (YukiHook.isStandalone) return

        prepareLifecyclePackage(packageName)
        lifecycles += lifecycle

        installAppLifecycleHooks()
        installApplicationOnCreateHook()
        if (entryType == HookEntryType.SYSTEM_SERVER) scheduleSystemServerInitialization()
    }

    /**
     * Prepares services that require the first [Application] in this process.
     * @param packageName the package owning the [Application].
     * @param entryType the current hook entry type.
     */
    @Synchronized
    fun prepareHostApplication(packageName: String, entryType: HookEntryType) {
        prepareLifecyclePackage(packageName)

        if (YukiHook.Config.dataChannel) installApplicationOnCreateHook()
        if (entryType == HookEntryType.SYSTEM_SERVER) scheduleSystemServerInitialization()
    }

    /**
     * Assigns the package owning the first [Application] in this process.
     * @param packageName the package owning the [Application].
     */
    private fun prepareLifecyclePackage(packageName: String) {
        if (lifecyclePackageName.isEmpty()) lifecyclePackageName = packageName
    }

    /** Hooks lifecycle methods used by registered callbacks. */
    private fun installAppLifecycleHooks() {
        if (isAppLifecycleInstalled) return

        val hooked = mutableListOf<YukiHookResult>()
        runCatching {
            Application::class.resolve().optional(silent = true).apply {
                YukiHookHelper.hook(firstMethod { name = "attach"; parameters(Context::class) }, object : ClassicHookerBridge {
                    override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                        val baseContext = param.args[0] as? Context ?: return
                        if (baseContext.packageName != lifecyclePackageName) return

                        param.dispatchAppLifecycle { onAttachBaseContext(baseContext, false) }
                    }

                    override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                        val baseContext = param.args[0] as? Context ?: return
                        if (baseContext.packageName != lifecyclePackageName) return

                        param.dispatchAppLifecycle { onAttachBaseContext(baseContext, true) }
                    }
                }).let(hooked::add)
                YukiHookHelper.hook(firstMethod { name = "onTerminate" }, object : ClassicHookerBridge {
                    override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                        val application = param.instance as? Application ?: return
                        if (application.packageName != lifecyclePackageName) return

                        param.dispatchAppLifecycle { onTerminate(application) }
                    }
                }).let(hooked::add)
                YukiHookHelper.hook(firstMethod { name = "onLowMemory" }, object : ClassicHookerBridge {
                    override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                        val application = param.instance as? Application ?: return
                        if (application.packageName != lifecyclePackageName) return

                        param.dispatchAppLifecycle { onLowMemory(application) }
                    }
                }).let(hooked::add)
                YukiHookHelper.hook(firstMethod { name = "onTrimMemory"; parameters(Int::class) }, object : ClassicHookerBridge {
                    override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                        val application = param.instance as? Application ?: return
                        if (application.packageName != lifecyclePackageName) return
                        val level = param.args[0] as? Int ?: return

                        param.dispatchAppLifecycle { onTrimMemory(application, level) }
                    }
                }).let(hooked::add)
                YukiHookHelper.hook(
                    firstMethod { name = "onConfigurationChanged"; parameters(Configuration::class) },
                    object : ClassicHookerBridge {
                        override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                            val application = param.instance as? Application ?: return
                            if (application.packageName != lifecyclePackageName) return
                            val config = param.args[0] as? Configuration ?: return

                            param.dispatchAppLifecycle { onConfigurationChanged(application, config) }
                        }
                    }
                ).let(hooked::add)
            }

            isAppLifecycleInstalled = true
        }.onFailure {
            hooked.forEach { result -> result.remove() }
            YLog.innerE("App lifecycle initialization failed", it)
        }
    }

    /** Hooks [Instrumentation.callApplicationOnCreate] for lifecycle callbacks and the data channel. */
    private fun installApplicationOnCreateHook() {
        if (isApplicationOnCreateInstalled) return

        runCatching {
            YukiHookHelper.hook(
                Instrumentation::class.resolve().optional(silent = true).firstMethod {
                    name = "callApplicationOnCreate"
                    parameters(Application::class)
                }, object : ClassicHookerBridge {
                    override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                        val application = param.args[0] as? Application ?: return
                        if (application.packageName != lifecyclePackageName) return

                        initializeHostApplication(application)?.let { param.throwable = it }
                    }
                }
            )

            isApplicationOnCreateInstalled = true
        }.onFailure { YLog.innerE("Application onCreate initialization failed", it) }
    }

    /**
     * Initializes host-dependent features when the host [Application] is available.
     * @param application the host [Application].
     */
    private fun initializeHostApplication(application: Application): Throwable? {
        hostApplication = application

        val pendingLifecycles = synchronized(this) {
            lifecycles.filter { initializedLifecycles.add(it) }
        }
        val failureToHost = pendingLifecycles.dispatchAppLifecycle {
            onCreate(application)

            receiverActions.forEach { (actions, callback) ->
                application.registerReceiver(
                    filter = IntentFilter().apply { actions.forEach { action -> addAction(action) } },
                    exported = true
                ) { context, intent -> callback(context, intent) }
            }
            receiverFilters.forEach { (filter, callback) ->
                application.registerReceiver(filter = filter, exported = true) { context, intent ->
                    callback(context, intent)
                }
            }
        }

        if (YukiHook.Config.dataChannel && !isDataChannelRegistered) runCatching {
            YukiHookDataChannel.get().register(application, lifecyclePackageName)
            isDataChannelRegistered = true
        }.onFailure { YLog.innerE("Data channel initialization failed", it) }

        return failureToHost
    }

    /** Schedules system server initialization after its services finish starting. */
    @Synchronized
    private fun scheduleSystemServerInitialization() {
        if (isSystemServerInitializationPending) return

        isSystemServerInitializationPending = true
        Handler(Looper.getMainLooper()).post {
            synchronized(this) { isSystemServerInitializationPending = false }
            initializeReadyHostApplication()
        }
    }

    /** Initializes the system server [Application] created before the runtime callback. */
    private fun initializeReadyHostApplication() {
        currentApplication
            ?.takeIf { it.packageName == lifecyclePackageName }
            ?.let(::initializeHostApplication)
            ?.let { YLog.innerE("An exception occurred during AppLifecycle event", e = it) }
    }

    /** Dispatches one lifecycle event without letting one registration interrupt the others. */
    private inline fun ClassicHookerBridge.Param.dispatchAppLifecycle(
        crossinline callback: AppLifecycleBridge.() -> Unit
    ) {
        lifecycles.dispatchAppLifecycle(callback)?.let { throwable = it }
    }

    /** Dispatches one lifecycle event and returns the first failure intended for the host. */
    private inline fun Iterable<AppLifecycleBridge>.dispatchAppLifecycle(
        crossinline callback: AppLifecycleBridge.() -> Unit
    ): Throwable? {
        var failureToHost: Throwable? = null
        forEach { lifecycle ->
            runCatching { lifecycle.callback() }.onFailure { failure ->
                if (lifecycle.throwToHostOnFailure) {
                    if (failureToHost == null) failureToHost = failure
                } else YLog.innerE("An exception occurred during AppLifecycle event", e = failure)
            }
        }
        return failureToHost
    }

    /**
     * Injects the current Xposed module's resources into the host.
     * @param hostResources the host [Resources] to inject.
     */
    fun injectModuleResources(hostResources: Resources) {
        if (!XposedModuleRuntime.isHostEnvironment)
            return YLog.innerW("You can only inject module resources in host environment")
        if (currentPackageName == XposedModuleRuntime.modulePackageName)
            return YLog.innerE("You cannot inject module resources into yourself")

        ModuleResources.inject(hostResources, XposedModuleRuntime.moduleSourceDir) { throwable ->
            throwable?.let { YLog.innerE("Failed to inject module resources into $hostResources", it) }
        }
    }

    /**
     * Registers the current Xposed module's [Activity] in the host.
     * @param context the current [Context].
     * @param proxy the proxy [Activity].
     */
    @RequiresApi(AndroidVersion.N)
    @Synchronized
    fun registerModuleActivities(context: Context, proxy: Any?) {
        fun String.toModuleActivityClassOrNull() = toClassOrNull(baseClassLoader)?.takeIf {
            it isSubclassOf Activity::class && it isSubclassOf ModuleActivity::class
        }

        fun Intent.wrap(): Intent {
            val component = this.component ?: return this
            if (component.packageName != currentPackageName) return this
            val activityClass = component.className.toModuleActivityClassOrNull() ?: return this

            val proxyClassName = activityClass.runCatching {
                createInstanceAsTypeOrNull<ModuleActivity>()?.proxyClassName
                    ?.takeIf { hostApplication?.classLoader?.hasClass(it) == true }
            }.getOrNull() ?: ActivityProxyConfig.proxyClassName

            return Intent().apply {
                setClassName(component.packageName, proxyClassName)
                putExtra(ActivityProxyConfig.proxyIntentName, this@wrap)
            }
        }

        if (isActivityProxyRegistered) return
        if (!XposedModuleRuntime.isHostEnvironment) return YLog.innerW("You can only register Activity proxy in host environment")
        if (context.packageName == XposedModuleRuntime.modulePackageName) return YLog.innerE("You cannot register Activity proxy into yourself")
        if (AndroidVersion.isAtMost(AndroidVersion.M)) return YLog.innerE("Activity proxy only support for Android 7.0 (API 24) or higher")

        val hooked = mutableListOf<YukiHookResult>()
        runCatching {
            ActivityProxyConfig.apply {
                proxyIntentName = "${XposedModuleRuntime.modulePackageName}.ACTIVITY_PROXY_INTENT"
                proxyClassName = proxy?.let {
                    when (it) {
                        is String, is CharSequence -> it.toString()
                        is Class<*> -> it.name
                        else -> error("This proxy $it which is not allowed.")
                    }
                }?.takeIf { it.isNotBlank() } ?: context.packageManager?.runCatching {
                    queryIntentActivities(getLaunchIntentForPackage(context.packageName)!!, 0)
                        .first()
                        .activityInfo.name
                }?.getOrNull() ?: ""

                val checkIsActivity = proxyClassName.toClassOrNull(context.classLoader)
                    ?.resolve()?.optional(silent = true)
                    ?.firstMethodOrNull {
                        name = "setIntent"
                        parameters(Intent::class)
                        superclass()
                    } != null

                if (!checkIsActivity) {
                    if (proxyClassName.isBlank())
                        error("Cound not got launch intent for package \"${context.packageName}\".")
                    else error("Could not found \"$proxyClassName\" or class is not a type of Activity.")
                }
            }

            val sCurrentActivityThread = ActivityThreadClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstFieldOrNull { name = "sCurrentActivityThread" }
                ?.get()
            val instrumentation = sCurrentActivityThread?.asResolver()
                ?.processor(AndroidHiddenApiBypassResolver.get())
                ?.optional(silent = true)
                ?.firstMethodOrNull { name = "getInstrumentation" }
                ?.invoke<Instrumentation>() ?: error("Could not found Instrumentation in ActivityThread.")
            val instrumentationResolver = instrumentation.asResolver().processor(AndroidHiddenApiBypassResolver.get())

            val performLaunchActivity = ActivityThreadClass.resolve()
                .processor(AndroidHiddenApiBypassResolver.get())
                .firstMethod {
                    name = "performLaunchActivity"
                    parameters($$"android.app.ActivityThread$ActivityClientRecord", Intent::class)
                }
            val newActivityResolver = instrumentationResolver.firstMethod {
                name = "newActivity"
                parameters(ClassLoader::class, String::class, Intent::class)
                superclass()
            }

            fun callActivityOnCreateResolver(types: Array<out KClass<*>>) =
                instrumentationResolver.firstMethod {
                    name = "callActivityOnCreate"
                    parameters(*types)
                    superclass()
                }

            val callActivityOnCreateResolvers = listOf(
                arrayOf(Activity::class, Bundle::class),
                arrayOf(Activity::class, Bundle::class, PersistableBundle::class)
            ).map { types -> callActivityOnCreateResolver(types) }

            val managerClasses = buildList {
                add(AndroidVersion.require(AndroidVersion.O, "android.app.ActivityManagerProxy") {
                    $$"android.app.IActivityManager$Stub$Proxy"
                })
                if (AndroidVersion.isAtLeast(AndroidVersion.Q)) add($$"android.app.IActivityTaskManager$Stub$Proxy")
            }
            val startActivityResolvers = managerClasses.flatMap {
                it.toClass().resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional(silent = true)
                    .method {
                        name = "startActivity"
                        parameters { args -> classOf<Intent>() in args }
                    }
            }
            check(startActivityResolvers.isNotEmpty()) {
                "Could not find ActivityManager startActivity methods."
            }

            // Resolve each overload separately so a custom `Instrumentation` can override only one of them.
            YukiHookHelper.hook(newActivityResolver, object : ClassicHookerBridge {
                override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                    if (param.instance !== instrumentation || param.throwable == null) return
                    val className = param.args[1] as? String ?: return

                    className.toModuleActivityClassOrNull()
                        ?.createInstanceAsTypeOrNull<Activity>()
                        ?.let { param.result = it }
                }
            }).let(hooked::add)
            callActivityOnCreateResolvers.forEach { resolver ->
                YukiHookHelper.hook(resolver, object : ClassicHookerBridge {
                    override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                        if (param.instance !== instrumentation) return
                        val activity = param.args[0] as? Activity ?: return
                        if (activity !is ModuleActivity) return

                        (param.args[1] as? Bundle)?.classLoader = baseClassLoader
                        injectModuleResources(activity.resources)
                    }
                }).let(hooked::add)
            }
            YukiHookHelper.hook(performLaunchActivity, object : ClassicHookerBridge {
                override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                    val record = param.args[0] ?: return

                    val intentResolver = record.asResolver().processor(AndroidHiddenApiBypassResolver.get())
                        .firstField { name = "intent" }
                    val intent = intentResolver.get<Intent>() ?: return
                    intent.setExtrasClassLoader(currentApplication?.classLoader)
                    if (!intent.hasExtra(ActivityProxyConfig.proxyIntentName)) return

                    // Android 12 can cache this record before the launch transaction executes.
                    // Restore the actual record consumed by ActivityThread instead of the transaction item.
                    val original = intent.getParcelableExtraCompat<Intent>(ActivityProxyConfig.proxyIntentName) ?: return
                    intentResolver.set(original)
                }
            }).let(hooked::add)
            startActivityResolvers.forEach { resolver ->
                YukiHookHelper.hook(resolver, object : ClassicHookerBridge {
                    override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                        val args = param.args

                        val index = args.indexOfFirst { it is Intent }
                        if (index >= 0) args[index] = (args[index] as Intent).wrap()
                    }
                }).let(hooked::add)
            }

            isActivityProxyRegistered = true
        }.onFailure {
            hooked.forEach { result -> result.remove() }
            YLog.innerE("Activity proxy initialization failed because got an exception", it)
        }
    }
}