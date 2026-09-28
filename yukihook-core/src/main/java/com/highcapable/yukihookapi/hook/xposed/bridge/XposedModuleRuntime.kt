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
 * This file is created by fankes on 2022/4/3.
 * This file is modified by fankes on 2023/1/9.
 */
@file:Suppress("SameParameterValue")

package com.highcapable.yukihookapi.hook.xposed.bridge

import android.content.pm.ApplicationInfo
import android.content.res.Resources
import android.os.Bundle
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.context.PackageContext
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.component.HookRegistration
import com.highcapable.yukihookapi.hook.xposed.bridge.component.PackageRegistration
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle.XposedModuleLifecycle
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.ModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleLifecycleManager
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleReloadable
import com.highcapable.yukihookapi.hook.xposed.bridge.session.state.ModuleHandoffState
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.HookRegistrationBridge
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge

/**
 * Core Xposed module runtime for [YukiHook].
 */
internal object XposedModuleRuntime : XposedModuleLifecycle {

    private var events = ModuleEventDispatcher()
    private val packageRegistration = PackageRegistration(::dispatchPackage)
    private var hookRegistration: HookRegistration? = null

    private val hookEntry = ThreadLocal<YukiHookXposedModule>()
    private val registeringFrameworkEvents = ThreadLocal<Boolean>()
    private var isFrameworkEventsRegistered = false

    /** Whether the Xposed module has been loaded. */
    private var isModuleLoaded = false

    /** Whether the current Hook process is running in Zygote. */
    private var isInitializingZygote = false

    /** The current Xposed module resources. */
    private var currentModuleResources: ModuleResources? = null

    /** The current [PackageParam] block callback. */
    var packageParamCallback: (PackageParam.() -> Unit)? = null

    /** The predefined Xposed module package name. */
    var modulePackageName = ""
        private set

    /** The current Xposed module source directory. */
    var moduleSourceDir = ""
        private set

    /** The current Xposed module application info. */
    var moduleAppInfo: ApplicationInfo? = null
        private set

    /**
     * Gets the current Xposed module resources synchronized with the host configuration.
     * @return [ModuleResources] or null.
     */
    val moduleResources get() = currentModuleResources
        ?.syncConfiguration(AppParasitism.currentApplication?.resources ?: Resources.getSystem())

    /**
     * Gets the identifier of the hook process currently running in the host.
     * @return [String]
     */
    val hostProcessName get() = if (isInitializingZygote) "android-zygote" else AppParasitism.currentPackageName

    /**
     * Gets whether the current environment is a host environment.
     * @return [Boolean]
     */
    val isHostEnvironment get() = YukiHookBridge.isAvailable && isModuleLoaded

    /**
     * Checks that registration runs inside this entry's `onInit` on the current thread.
     * @param entry the current [YukiHookXposedModule] entry.
     * @param name the name of the feature being registered.
     * @throws IllegalStateException if this entry is not being initialized on the current thread.
     */
    fun checkInitializingEntry(entry: YukiHookXposedModule, name: String) = check(hookEntry.get() === entry) {
        "You must registered $name in onInit of the current process."
    }

    /**
     * Checks that package hooks have not been declared before `onHook` runs.
     * @throws IllegalStateException if a package callback was already registered.
     */
    fun checkHookLoading() = check(packageParamCallback == null) {
        "Do not load hookers in onInit."
    }

    /**
     * Runs the entry's initialization with its registration context bound to this thread.
     * @param entry the current [YukiHookXposedModule] entry.
     * @param block the initialization block.
     */
    fun createHookEntry(entry: YukiHookXposedModule, block: () -> Unit) {
        hookEntry.set(entry)

        try {
            block()
        } finally {
            hookEntry.remove()
        }
    }

    /**
     * Checks that a framework event listener is being configured inside the registration block.
     * @throws IllegalStateException if no registration block is running on the current thread.
     */
    fun checkFrameworkEventsRegistration() = check(registeringFrameworkEvents.get() == true) {
        "Framework event listeners must be configured inside `registerFrameworkEvents`."
    }

    /**
     * Registers one framework event configuration block during entry initialization.
     * @param entry the current [YukiHookXposedModule] entry.
     * @param block the framework event configuration block.
     * @throws IllegalStateException if called outside this entry's initialization or more than once.
     */
    fun registerFrameworkEvents(entry: YukiHookXposedModule, block: () -> Unit) {
        checkInitializingEntry(entry, "framework events")
        synchronized(this) {
            check(!isFrameworkEventsRegistered) {
                "Framework events can only be registered once per module process."
            }
            isFrameworkEventsRegistered = true
        }
        registeringFrameworkEvents.set(true)

        try {
            block()
        } finally {
            registeringFrameworkEvents.remove()
        }
    }

    /** Reloads the current Xposed module's [Resources]. */
    fun reloadModuleResources() {
        currentModuleResources = ModuleResources.from(moduleSourceDir)
    }

    /**
     * Attaches the runtime session and prepares hook registration before module loading.
     * @param session the current [ModuleSessionBridge] session.
     * @param registrations the current [HookRegistrationBridge] registrations.
     * @return [HookRegistrationBridge] for the current session.
     */
    fun attachModuleSession(session: ModuleSessionBridge, registrations: HookRegistrationBridge): HookRegistrationBridge {
        ModuleLifecycleManager.attach(session)

        events = ModuleLifecycleManager.events
        val component = HookRegistration(registrations, events)
        hookRegistration = component

        return component
    }

    /**
     * Dispatches a framework callback while this module instance accepts work.
     * @param block the framework callback block.
     */
    fun dispatchFrameworkEvent(block: () -> Unit) = ModuleLifecycleManager.events.dispatch(block)

    /**
     * Records a caught runtime callback failure.
     * @param failure the failure to record.
     */
    fun recordRestoreFailure(failure: Throwable) = ModuleLifecycleManager.recordRestoreFailure(failure)

    override fun onStartLoadModule(packageName: String, sourceDir: String, appInfo: ApplicationInfo?) {
        isModuleLoaded = true

        modulePackageName = packageName
        moduleSourceDir = sourceDir
        moduleAppInfo = appInfo
        reloadModuleResources()
        registerLifecycleComponents()
    }

    override fun onPackageLoaded(
        type: HookEntryType,
        packageName: String,
        processName: String,
        mainProcessName: String,
        isFirstApplication: Boolean,
        packageNameForMatching: String?,
        appClassLoader: ClassLoader,
        appInfo: ApplicationInfo?
    ) {
        if (isMiuiCatcherPatch(packageName)) return

        dispatchPackage(
            PackageContext(
                type = type,
                packageName = packageName,
                processName = processName,
                mainProcessName = mainProcessName,
                isFirstApplication = isFirstApplication,
                classLoader = appClassLoader,
                appInfo = appInfo,
                packageNameForMatching = packageNameForMatching
            )
        )
    }

    override fun onPrepareReloadModule(extras: Bundle?, saveState: (Any) -> Boolean) = ModuleLifecycleManager.prepare(extras, saveState)

    override fun onBeginRestoreModule(appInfo: ApplicationInfo, state: Any?) = ModuleLifecycleManager.beginRestore(state).also {
        ModuleLifecycleManager.prepareRestore(appInfo, it)
        ModuleLifecycleManager.restoreComponents(appInfo, it, ModuleReloadable.RestorePhase.PREREQUISITE)
    }

    override fun onRestoreUserState(state: ModuleHandoffState) = ModuleLifecycleManager.restoreState(state)

    override fun onRestoreComponents(appInfo: ApplicationInfo, state: ModuleHandoffState) =
        ModuleLifecycleManager.restoreComponents(appInfo, state, ModuleReloadable.RestorePhase.REBUILD)

    override fun onFinishRestoreModule(extras: Bundle?) = ModuleLifecycleManager.finishRestore(extras)

    override fun onAbortRestoreModule() = ModuleLifecycleManager.abortRestore()

    private fun registerLifecycleComponents() {
        if (hookRegistration == null) return

        // The data channel caches host identity, so registration follows onStartLoadModule's identity setup.
        ModuleLifecycleManager.register(ModuleResources.RELOAD_ID, ModuleResources)
        ModuleLifecycleManager.register(PackageRegistration.RELOAD_ID, packageRegistration)
        ModuleLifecycleManager.register(AppParasitism.RELOAD_ID, AppParasitism)
        ModuleLifecycleManager.register(YukiHookDataChannel.RELOAD_ID, YukiHookDataChannel)
    }

    /**
     * Automatically ignores log-collection injection instances that may appear on MIUI.
     * @param packageName the current package name.
     * @return [Boolean] whether such an instance exists.
     */
    private fun isMiuiCatcherPatch(packageName: String?) =
        (packageName == "com.miui.contentcatcher" || packageName == "com.miui.catcherpatch") &&
            javaClass.classLoader?.hasClass("android.miui.R") == true

    private fun dispatchPackage(context: PackageContext) {
        if (!packageRegistration.record(context)) return
        isInitializingZygote = context.type == HookEntryType.ZYGOTE

        runCatching {
            val callback = {
                packageParamCallback?.invoke(PackageParam(context).apply { YukiHook.sayHello() })
            }
            val component = hookRegistration
            if (component == null) callback() else component.withEntry(context.id, callback)

            if (context.type != HookEntryType.ZYGOTE && context.packageName == modulePackageName)
                AppParasitism.hookLegacyModuleRelated(context.classLoader)
            if (context.isFirstApplication) AppParasitism.prepareHostApplication(context.packageName, context.type)
        }.onFailure {
            events.reportFailure(it)
            YLog.innerE("An exception occurred in the hooking process of YukiHook", it)
        }
    }
}