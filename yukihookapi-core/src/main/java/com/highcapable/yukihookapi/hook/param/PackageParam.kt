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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.param

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.content.res.Resources
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.core.YukiHookCreator
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.reflect.ClassLoaderInitializer
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle.AppLifecycleBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.ModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import java.lang.reflect.Member
import kotlin.reflect.KClass
import com.highcapable.kavaref.extension.lazyClass as lazyClassGlobal
import com.highcapable.kavaref.extension.lazyClassOrNull as lazyClassOrNullGlobal
import com.highcapable.kavaref.extension.toClass as toClassGlobal
import com.highcapable.kavaref.extension.toClassOrNull as toClassOrNullGlobal

/**
 * Target app entry object implementation for hooking.
 * @param wrapper the parameter wrapper instance for [PackageParam], null by default.
 */
open class PackageParam internal constructor(internal var wrapper: PackageParamWrapper? = null) {

    /** The currently configured [ClassLoader]. */
    private var currentClassLoader: ClassLoader? = null

    /**
     * Gets the [hostClassLoader] initializer.
     * @return [ClassLoaderInitializer]
     */
    private val hostClassLoaderInit get(): ClassLoaderInitializer = { hostClassLoader }

    /**
     * Module specification for the current Xposed module.
     *
     * - This is unavailable when loaded as a custom hook API.
     */
    class ModuleSpec internal constructor() {

        /**
         * Gets the APK file path of the current Xposed module.
         * @return [String]
         */
        val filePath get() = XposedModuleRuntime.moduleFilePath

        /**
         * Gets the [Resources] of the current Xposed module.
         * @return [ModuleResources]
         * @throws IllegalStateException if the current hook framework does not support this feature.
         */
        val resources get() = XposedModuleRuntime.moduleResources ?: error("Current hook framework not support module resources.")

        /** Reloads the [Resources] of the current Xposed module. */
        fun reloadResources() = XposedModuleRuntime.reloadModuleResources()
    }

    /**
     * Gets or sets the [ClassLoader] of the current host.
     *
     * You can manually set the [ClassLoader] of the current host here. It is obtained automatically by default.
     *
     * - Setting an incorrect or invalid [ClassLoader] causes feature failures. Use this carefully.
     * @return [ClassLoader] or null.
     */
    var hostClassLoader
        get() = currentClassLoader ?: wrapper?.appClassLoader ?: AppParasitism.currentApplication?.classLoader
        set(value) {
            currentClassLoader = value
        }

    /**
     * Gets the [ApplicationInfo] of the current host.
     * @return [ApplicationInfo]
     */
    val appInfo get() = wrapper?.appInfo ?: AppParasitism.currentApplicationInfo ?: ApplicationInfo()

    /**
     * Gets the user ID of the current host.
     *
     * The owner is 0. Cloned apps and work profiles have different IDs depending on the system environment.
     * @return [Int]
     */
    val userId get() = AppParasitism.findUserId(packageName)

    /**
     * Gets the [Application] instance of the current host.
     *
     * - It may be null during initial loading. Retrieve it later or configure a [registerAppLifecycle] listener.
     * @return [Application] or null.
     */
    val hostApplication get() = AppParasitism.hostApplication ?: AppParasitism.currentApplication

    /**
     * Gets the [Context] of the current system server.
     * @return [Context] the `ContextImpl` instance.
     * @throws IllegalStateException if the [Context] of the system server cannot be obtained.
     */
    val systemContext get() = AppParasitism.systemContext ?: error("Failed to got system context.")

    /**
     * Gets the process name of the current host.
     *
     * The default process name is [packageName].
     * @return [String]
     */
    val processName get() = wrapper?.processName ?: AppParasitism.currentProcessName

    /**
     * Gets the package name of the current host.
     * @return [String]
     */
    val packageName get() = wrapper?.packageName ?: AppParasitism.currentPackageName

    /**
     * Whether the current host is the first [Application].
     * @return [Boolean]
     */
    val isFirstApplication get() = wrapper?.isFirstApplication ?: false

    /**
     * Gets the main process name of the current host.
     *
     * The runtime supplies the system process identity during [loadSystem]; otherwise this corresponds to [packageName].
     * @return [String]
     */
    val mainProcessName get() = wrapper?.mainProcessName ?: packageName.trim()

    /**
     * Gets the [ModuleSpec] of the current Xposed module.
     *
     * - This is unavailable when loaded as a custom hook API.
     */
    val module = ModuleSpec()

    /**
     * Creates a [YukiHookPreferences] object.
     *
     * - This is unavailable when loaded as a custom hook API and throws an exception.
     * @return [YukiHookPreferences]
     */
    val preferences get() = YukiHookPreferences.from()

    /**
     * Creates a [YukiHookPreferences] object.
     *
     * - This is unavailable when loaded as a custom hook API and throws an exception.
     * @param name the custom SharedPreferences storage name.
     * @return [YukiHookPreferences]
     */
    fun preferences(name: String) = preferences.name(name)

    /**
     * Gets the [YukiHookDataChannel] object.
     *
     * - This is unavailable when loaded as a custom hook API and throws an exception.
     * @return [YukiHookDataChannel.Namespace]
     * @throws IllegalStateException if loaded in [HookEntryType.ZYGOTE].
     */
    val dataChannel get() = if (wrapper?.type != HookEntryType.ZYGOTE)
        YukiHookDataChannel.get().namespace(packageName = packageName)
    else error("YukiHookDataChannel cannot used in zygote events, please use it in loadApp or loadSystem.")

    /**
     * Binds the [PackageParamWrapper] to the current [PackageParam].
     * @param wrapper the parameter wrapper instance for [PackageParam].
     * @return [PackageParam]
     */
    internal fun bind(wrapper: PackageParamWrapper?): PackageParam {
        currentClassLoader = null
        this.wrapper = wrapper
        return this
    }

    /**
     * Registers a lifecycle listener for the current host [Application].
     *
     * This is loaded only for the package that owns the first [Application] in the current process.
     * A standalone custom hook API can observe only lifecycle events that occur after registration.
     * @param onFailureThrowToHost whether to throw exceptions to the host when they occur, true by default.
     * @param block the lifecycle block.
     */
    fun registerAppLifecycle(onFailureThrowToHost: Boolean = true, block: AppLifecycle.() -> Unit) {
        if (isFirstApplication) AppLifecycle.register(packageName, onFailureThrowToHost, block)
    }

    /**
     * Loads and hooks apps with the specified package name.
     * @see loadSystem
     * @param name the package name.
     * @param block the param block.
     */
    fun loadApp(name: String, block: PackageParam.() -> Unit) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (wrapper?.matchesPackage(name) == true || name.isBlank())) block(this)
    }

    /**
     * Loads and hooks apps with the specified package names.
     * @see loadSystem
     * @param name the package names.
     * @param block the param block.
     */
    fun loadApp(vararg name: String, block: PackageParam.() -> Unit) {
        if (name.isEmpty()) return loadApp(block = block)
        if (wrapper?.type != HookEntryType.ZYGOTE && name.any { wrapper?.matchesPackage(it) == true }) block(this)
    }

    /**
     * Loads and hooks apps with the specified package name.
     * @see loadSystem
     * @param name the package name.
     * @param hookers the hookers.
     */
    fun loadApp(name: String, hookers: YukiBaseHooker) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (wrapper?.matchesPackage(name) == true || name.isBlank())) loadHooker(hookers)
    }

    /**
     * Loads and hooks apps with the specified package name.
     * @see loadSystem
     * @param name the package name. An empty name matches all apps except [loadZygote] events.
     * @param hookers the hookers.
     */
    fun loadApp(name: String, vararg hookers: YukiBaseHooker) {
        require(hookers.isNotEmpty()) {
            "loadApp function required \"hookers\" param."
        }

        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (wrapper?.matchesPackage(name) == true || name.isBlank())
        ) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks all apps.
     * @see loadSystem
     * @param excludeSelf whether to exclude the module itself, false by default. When enabled, the hosts do not include the current module.
     * @param block the param block.
     */
    fun loadApp(excludeSelf: Boolean = false, block: PackageParam.() -> Unit) {
        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || wrapper?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
        ) block(this)
    }

    /**
     * Loads and hooks all apps.
     * @see loadSystem
     * @param excludeSelf whether to exclude the module itself, false by default. When enabled, the hosts do not include the current module.
     * @param hooker the hooker.
     */
    fun loadApp(excludeSelf: Boolean = false, hooker: YukiBaseHooker) {
        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || wrapper?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
        ) loadHooker(hooker)
    }

    /**
     * Loads and hooks all apps.
     * @see loadSystem
     * @param excludeSelf whether to exclude the module itself, false by default. When enabled, the hosts do not include the current module.
     * @param hookers the hookers.
     */
    fun loadApp(excludeSelf: Boolean = false, vararg hookers: YukiBaseHooker) {
        require(hookers.isNotEmpty()) {
            "loadApp function required \"hookers\" param."
        }

        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || wrapper?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
        ) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks the system server.
     * @see loadApp
     * @param block the param block.
     */
    fun loadSystem(block: PackageParam.() -> Unit) {
        if (wrapper?.type == HookEntryType.SYSTEM_SERVER) block(this)
    }

    /**
     * Loads and hooks the system server.
     * @see loadApp
     * @param hooker the hooker.
     */
    fun loadSystem(hooker: YukiBaseHooker) {
        if (wrapper?.type == HookEntryType.SYSTEM_SERVER) loadHooker(hooker)
    }

    /**
     * Loads and hooks the system server.
     * @see loadApp
     * @param hookers the hookers.
     */
    fun loadSystem(vararg hookers: YukiBaseHooker) {
        require(hookers.isNotEmpty()) {
            "loadSystem function required \"hookers\" param."
        }

        if (wrapper?.type == HookEntryType.SYSTEM_SERVER) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
     *
     * @see loadApp
     * @see loadSystem
     * @param block the param block.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Use loadApp or loadSystem instead.")
    fun loadZygote(block: PackageParam.() -> Unit) {
        if (wrapper?.type == HookEntryType.ZYGOTE) block(this)
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
     *
     * @see loadApp
     * @see loadSystem
     * @param hooker the hooker.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Use loadApp or loadSystem instead.")
    fun loadZygote(hooker: YukiBaseHooker) {
        if (wrapper?.type == HookEntryType.ZYGOTE) loadHooker(hooker)
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
     *
     * @see loadApp
     * @see loadSystem
     * @param hookers the hookers.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Use loadApp or loadSystem instead.")
    fun loadZygote(vararg hookers: YukiBaseHooker) {
        require(hookers.isNotEmpty()) {
            "loadZygote function required \"hookers\" param."
        }

        if (wrapper?.type == HookEntryType.ZYGOTE) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks a specified app process.
     * @param name the process name. Use [mainProcessName] to specify the main process.
     * @param block the param block.
     */
    fun withProcess(name: String, block: PackageParam.() -> Unit) {
        if (processName == name) block(this)
    }

    /**
     * Loads and hooks specified app processes.
     * @param names the process names. Use [mainProcessName] to specify the main process.
     * @param block the param block.
     */
    fun withProcess(vararg names: String, block: PackageParam.() -> Unit) {
        require(names.isNotEmpty()) {
            "withProcess function required \"names\" param."
        }

        if (names.any { it == processName }) block(this)
    }

    /**
     * Loads and hooks a specified app process.
     * @param name the process name. Use [mainProcessName] to specify the main process.
     * @param hooker the hooker.
     */
    fun withProcess(name: String, hooker: YukiBaseHooker) {
        if (processName == name) loadHooker(hooker)
    }

    /**
     * Loads and hooks a specified app process.
     * @param name the process name. Use [mainProcessName] to specify the main process.
     * @param hookers the hookers.
     */
    fun withProcess(name: String, vararg hookers: YukiBaseHooker) {
        require(hookers.isNotEmpty()) {
            "withProcess function required \"hookers\" param."
        }

        if (processName == name) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads a hooker.
     *
     * You can continue loading hookers from a hooker.
     * @param hooker the hooker.
     */
    fun loadHooker(hooker: YukiBaseHooker) = hooker.bindTo(param = this)

    /** @see toClassGlobal */
    fun String.toClass(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) =
        toClassGlobal(loader, initialize)

    /** @see toClassGlobal */
    @JvmName("toClassTyped")
    fun <T : Any> String.toClass(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) =
        toClassGlobal<T>(loader, initialize)

    /** @see toClassOrNullGlobal */
    fun String.toClassOrNull(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal(loader, initialize)

    /** @see toClassOrNullGlobal */
    @JvmName("toClassOrNullTyped")
    fun <T : Any> String.toClassOrNull(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal<T>(loader, initialize)

    /** @see toClassGlobal */
    fun VariousClass.toClass(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) = load(loader, initialize)

    /** @see toClassGlobal */
    fun VariousClass.toClassOrNull(loader: ClassLoader? = hostClassLoader, initialize: Boolean = false) = loadOrNull(loader, initialize)

    /** @see lazyClassGlobal */
    fun lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassGlobal(name, initialize, loader)

    /** @see lazyClassGlobal */
    @JvmName("lazyClassTyped")
    fun <T : Any> lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassGlobal<T>(name, initialize, loader)

    /** @see lazyClassGlobal */
    fun lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassGlobal(variousClass, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    fun lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassOrNullGlobal(name, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    @JvmName("lazyClassOrNullTyped")
    fun <T : Any> lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassOrNullGlobal<T>(name, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    fun lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = hostClassLoaderInit) =
        lazyClassOrNullGlobal(variousClass, initialize, loader)

    /**
     * Creates a direct chain hook for the static initializer of this class.
     *
     * - Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = YukiHookCreator.createChainHooker(listOf(HookPoint.Initializer(this)), priority, invocation).build()

    /**
     * Creates a direct chain hook for the static initializer of this [KClass].
     *
     * - If the class is already initialized, this hook is never invoked.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = java.intercept(priority, invocation)

    /**
     * Creates direct chain hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Initializer(it) }, priority, invocation).build()

    /**
     * Creates direct chain hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = map { it.java }.interceptAll(priority, invocation)

    /**
     * Creates direct chain hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("interceptAllByClass")
    fun Collection<Class<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Initializer(it) }, priority, invocation).build()

    /**
     * Creates direct chain hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("interceptAllByKClass")
    fun Collection<KClass<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = map { it.java }.interceptAll(priority, invocation)

    /**
     * Creates a direct chain hook for methods and constructors.
     *
     * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
     *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
     *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
     *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    fun Member.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(listOf(HookPoint.Executable(this)), priority, invocation).build()

    /**
     * Creates a direct chain hook for methods and constructors through [MemberResolver].
     *
     * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
     *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
     *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
     *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @JvmName("interceptByMemberResolver")
    fun MemberResolver<*, *>.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(listOf(HookPoint.Executable(self)), priority, invocation).build()

    /**
     * Creates a direct chain hook for methods and constructors through a [List] of [MemberResolver] instances.
     *
     * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
     *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
     *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
     *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @JvmName("interceptAllByMemberResolver")
    fun Collection<MemberResolver<*, *>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Executable(it.self) }, priority, invocation).build()

    /**
     * Creates a direct chain hook for methods and constructors through an [Array] of [Member] instances.
     *
     * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
     *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
     *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
     *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    fun Array<Member>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Executable(it) }, priority, invocation).build()

    /**
     * Creates a direct chain hook for methods and constructors through a [List] of [Member] instances.
     *
     * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
     *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
     *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
     *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @JvmName("interceptAllByMember")
    fun Collection<Member>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Executable(it) }, priority, invocation).build()

    /**
     * Creates a direct hook for the static initializer of this class.
     *
     * - Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * - Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = listOf(this).createHooker(priority)

    /**
     * Creates a direct hook for the static initializer of this class.
     *
     * - Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * - Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = listOf(this).createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for the static initializer of this [KClass].
     *
     * - If the class is already initialized, this hook is never invoked.
     * - Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = java.hook(priority)

    /**
     * Creates a direct hook for the static initializer of this [KClass].
     *
     * - If the class is already initialized, this hook is never invoked.
     * - Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = java.hook(priority, hooker)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = toList().createHooker(priority)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = toList().createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
        map { it.java }.hookAll(priority)

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = map { it.java }.hookAll(priority, hooker)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("hookAllByClass")
    fun Collection<Class<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = createHooker(priority)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - Obtain every class without initializing it. An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("hookAllByClass")
    fun Collection<Class<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("hookAllByKClass")
    fun Collection<KClass<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
        map { it.java }.hookAll(priority)

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * - Each class is registered independently; this operation is not atomic.
     * - An already initialized class never invokes this hook.
     * - Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("hookAllByKClass")
    fun Collection<KClass<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = map { it.java }.hookAll(priority, hooker)

    /**
     * Creates a direct hook for methods and constructors.
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    fun Member.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = listOf(this).createHooker(priority)

    /**
     * Creates a direct hook for methods and constructors.
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    fun Member.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = listOf(this).createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for methods and constructors through [MemberResolver].
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    fun MemberResolver<*, *>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = createHooker(priority)

    /**
     * Creates a direct hook for methods and constructors through [MemberResolver].
     * @receiver the current member to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @JvmName("hookByMemberResolver")
    fun MemberResolver<*, *>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for methods and constructors through a [List] of [MemberResolver] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    fun Collection<MemberResolver<*, *>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = createHooker(priority)

    /**
     * Creates a direct hook for methods and constructors through a [List] of [MemberResolver] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    @JvmName("hookAll_MemberResolver")
    fun Collection<MemberResolver<*, *>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for methods and constructors through an [Array] of [Member] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    fun Array<Member>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = toList().createHooker(priority)

    /**
     * Creates a direct hook for methods and constructors through an [Array] of [Member] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    fun Array<Member>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = toList().createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for methods and constructors through a [List] of [Member] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @JvmName("hookAllByMember")
    fun Collection<Member>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = createHooker(priority)

    /**
     * Creates a direct hook for methods and constructors through a [List] of [Member] instances.
     * @receiver the current members to hook.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Result]
     */
    fun Collection<Member>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = createHooker(priority, isLazyMode = true).apply(hooker).build()

    private fun MemberResolver<*, *>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(listOf(HookPoint.Executable(self)), priority, isLazyMode)

    private fun Collection<MemberResolver<*, *>>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(map { HookPoint.Executable(it.self) }, priority, isLazyMode)

    @JvmName("createHookerByClass")
    private fun Collection<Class<*>>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(map { HookPoint.Initializer(it) }, priority, isLazyMode)

    @JvmName("createHookerByMember")
    private fun Collection<Member>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(map { HookPoint.Executable(it) }, priority, isLazyMode)

    // region Deprecated properties and functions for backward compatibility

    /** Gets or sets the [ClassLoader] of the current host. */
    @Deprecated(message = "Use hostClassLoader instead.", replaceWith = ReplaceWith("hostClassLoader"))
    var appClassLoader get() = hostClassLoader
        set(value) {
            hostClassLoader = value
        }

    /** Gets the user ID of the current host. */
    @Deprecated(message = "Use userId instead.", replaceWith = ReplaceWith("userId"))
    val appUserId get() = userId

    /** Gets the [Application] instance of the current host. */
    @Deprecated(message = "Use hostApplication instead.", replaceWith = ReplaceWith("hostApplication"))
    val appContext get() = hostApplication

    /** Gets the [Resources] of the current host. */
    @Deprecated(message = "Use hostApplication?.resources instead.", replaceWith = ReplaceWith("hostApplication?.resources"))
    val appResources get() = hostApplication?.resources

    /** Gets the APK file path of the current Xposed module. */
    @Deprecated(message = "Use module.filePath instead.", replaceWith = ReplaceWith("module.filePath"))
    val moduleAppFilePath get() = module.filePath

    /** Gets the [Resources] of the current Xposed module. */
    @Deprecated(message = "Use module.resources instead.", replaceWith = ReplaceWith("module.resources"))
    val moduleAppResources get() = module.resources

    /** Creates a [YukiHookPreferences] object. */
    @Deprecated(message = "Use preferences instead.", replaceWith = ReplaceWith("preferences"))
    val prefs get() = preferences

    /** Creates a [YukiHookPreferences] object. */
    @Deprecated(message = "Use preferences instead.", replaceWith = ReplaceWith("preferences(name)"))
    fun prefs(name: String) = preferences(name)

    /** Refreshes the [Resources] of the current Xposed module. */
    @Deprecated(message = "Use module.reloadResources instead.", replaceWith = ReplaceWith("module.reloadResources()"))
    fun refreshModuleAppResources() = module.reloadResources()

    /** Listens for lifecycle loading events of the current host. */
    @Deprecated(
        message = "Use registerAppLifecycle instead.",
        replaceWith = ReplaceWith("registerAppLifecycle(isOnFailureThrowToApp, block)")
    )
    fun onAppLifecycle(isOnFailureThrowToApp: Boolean = true, block: AppLifecycle.() -> Unit) =
        registerAppLifecycle(isOnFailureThrowToApp, block)

    // endregion

    /**
     * Lifecycle instance handler for the current host.
     *
     * - Use [registerAppLifecycle] to obtain [AppLifecycle].
     * @param packageName the package name of the current host.
     * @param onFailureThrowToHost whether to throw exceptions to the host when they occur.
     */
    class AppLifecycle private constructor(private val packageName: String, private val onFailureThrowToHost: Boolean) {

        internal companion object {

            /**
             * Registers a lifecycle listener for the current host [Application].
             * @param packageName the package name of the current host.
             * @param onFailureThrowToHost whether to throw exceptions to the host when they occur.
             */
            fun register(
                packageName: String,
                onFailureThrowToHost: Boolean,
                block: AppLifecycle.() -> Unit
            ) = AppLifecycle(packageName, onFailureThrowToHost).apply(block).build()
        }

        private var attachBaseContextCallback: ((Context, Boolean) -> Unit)? = null
        private var onCreateCallback: (Application.() -> Unit)? = null
        private var onTerminateCallback: (Application.() -> Unit)? = null
        private var onLowMemoryCallback: (Application.() -> Unit)? = null
        private var onTrimMemoryCallback: ((Application, Int) -> Unit)? = null
        private var onConfigurationChangedCallback: ((Application, Configuration) -> Unit)? = null

        private val onReceiverActionsCallbacks = mutableListOf<Pair<Array<out String>, (Context, Intent) -> Unit>>()
        private val onReceiverFiltersCallbacks = mutableListOf<Pair<IntentFilter, (Context, Intent) -> Unit>>()

        /**
         * Listens for the current host loading [Application.attachBaseContext].
         * @param callback callback with the base [Context] and whether super has been called.
         */
        fun attachBaseContext(callback: (baseContext: Context, hasCalledSuper: Boolean) -> Unit) {
            attachBaseContextCallback = callback
        }

        /**
         * Listens for the current host loading [Application.onCreate].
         * @param callback the callback block.
         */
        fun onCreate(callback: Application.() -> Unit) {
            onCreateCallback = callback
        }

        /**
         * Listens for the current host loading [Application.onTerminate].
         * @param callback the callback block.
         */
        fun onTerminate(callback: Application.() -> Unit) {
            onTerminateCallback = callback
        }

        /**
         * Listens for the current host loading [Application.onLowMemory].
         * @param callback the callback block.
         */
        fun onLowMemory(callback: Application.() -> Unit) {
            onLowMemoryCallback = callback
        }

        /**
         * Listens for the current host loading [Application.onTrimMemory].
         * @param callback callback with the current [Application] instance and [Int] level.
         */
        fun onTrimMemory(callback: (self: Application, level: Int) -> Unit) {
            onTrimMemoryCallback = callback
        }

        /**
         * Listens for the current host loading [Application.onConfigurationChanged].
         * @param callback callback with the current [Application] instance and [Configuration] instance.
         */
        fun onConfigurationChanged(callback: (self: Application, config: Configuration) -> Unit) {
            onConfigurationChangedCallback = callback
        }

        /**
         * Registers a system broadcast listener.
         * @param actions the system broadcast actions.
         * @param callback callback with the current [Context] and [Intent].
         */
        fun registerReceiver(vararg actions: String, callback: (context: Context, intent: Intent) -> Unit) {
            if (actions.isNotEmpty()) onReceiverActionsCallbacks += actions to callback
        }

        /**
         * Registers a system broadcast listener.
         * @param filter the broadcast intent filter.
         * @param callback callback with the current [Context] and [Intent].
         */
        fun registerReceiver(filter: IntentFilter, callback: (context: Context, intent: Intent) -> Unit) {
            onReceiverFiltersCallbacks += filter to callback
        }

        private fun build() = AppParasitism.registerAppLifecycle(packageName, object : AppLifecycleBridge {

            override val onFailureThrowToHost = this@AppLifecycle.onFailureThrowToHost
            override val receiverActions = this@AppLifecycle.onReceiverActionsCallbacks
            override val receiverFilters = this@AppLifecycle.onReceiverFiltersCallbacks

            override fun onAttachBaseContext(baseContext: Context, hasCalledSuper: Boolean) {
                attachBaseContextCallback?.invoke(baseContext, hasCalledSuper)
            }

            override fun onCreate(application: Application) {
                onCreateCallback?.invoke(application)
            }

            override fun onTerminate(application: Application) {
                onTerminateCallback?.invoke(application)
            }

            override fun onLowMemory(application: Application) {
                onLowMemoryCallback?.invoke(application)
            }

            override fun onTrimMemory(application: Application, level: Int) {
                onTrimMemoryCallback?.invoke(application, level)
            }

            override fun onConfigurationChanged(application: Application, config: Configuration) {
                onConfigurationChangedCallback?.invoke(application, config)
            }
        })
    }

    override fun toString() = "PackageParam(${super.toString()}) by $wrapper"
}