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
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.YukiHookAPI
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
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
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
     * Gets the [appClassLoader] initializer.
     * @return [ClassLoaderInitializer]
     */
    private val appLoaderInit get(): ClassLoaderInitializer = { appClassLoader }

    /**
     * Gets or sets the [ClassLoader] of the current host.
     *
     * You can manually set the [ClassLoader] of the current host here. It is obtained automatically by default.
     *
     * - Setting an incorrect or invalid [ClassLoader] causes feature failures. Use this carefully.
     * @return [ClassLoader] or null.
     */
    var appClassLoader
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
    val appUserId get() = AppParasitism.findUserId(packageName)

    /**
     * Gets the [Application] instance of the current host.
     *
     * - It may be null during initial loading. Retrieve it later or configure a [registerAppLifecycle] listener.
     * @return [Application] or null.
     */
    val appContext get() = AppParasitism.hostApplication ?: AppParasitism.currentApplication

    /**
     * Gets the [Resources] of the current host.
     *
     * - This is available after [appContext] finishes loading.
     * @return [Resources] or null.
     */
    val appResources get() = appContext?.resources

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
     * Gets the APK file path of the current Xposed module.
     *
     * - This is unavailable when loaded as a custom hook API and returns an empty string.
     * @return [String]
     */
    val moduleFilePath get() = XposedModuleRuntime.moduleFilePath

    /**
     * Gets the [Resources] of the current Xposed module.
     *
     * - This is unavailable when loaded as a custom hook API or under an unsupported hook framework and throws an exception.
     * @return [ModuleResources]
     * @throws IllegalStateException if the current hook framework does not support this feature.
     */
    val moduleResources
        get() = (if (YukiHookAPI.Configs.isEnableModuleResourcesCache) XposedModuleRuntime.moduleResources
        else XposedModuleRuntime.dynamicModuleResources) ?: error("Current hook framework not support module resources.")

    /**
     * Creates a [YukiHookPrefsBridge] object.
     *
     * - This is unavailable when loaded as a custom hook API and throws an exception.
     * @return [YukiHookPrefsBridge]
     */
    val prefs get() = YukiHookPrefsBridge.from()

    /**
     * Creates a [YukiHookPrefsBridge] object.
     *
     * - This is unavailable when loaded as a custom hook API and throws an exception.
     * @param name the custom SharedPreferences storage name.
     * @return [YukiHookPrefsBridge]
     */
    fun prefs(name: String) = prefs.name(name)

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

    /** Refreshes the [Resources] of the current Xposed module. */
    fun refreshModuleResources() = XposedModuleRuntime.refreshModuleResources()

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
     * - Supports for [YukiHookRuntime.XPOSED_82] only.
     * @see loadApp
     * @see loadSystem
     * @param block the param block.
     */
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
     * - Supports for [YukiHookRuntime.XPOSED_82] only.
     * @see loadApp
     * @see loadSystem
     * @param hooker the hooker.
     */
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
     * - Supports for [YukiHookRuntime.XPOSED_82] only.
     * @see loadApp
     * @see loadSystem
     * @param hookers the hookers.
     */
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
    fun String.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassGlobal(loader, initialize)

    /** @see toClassGlobal */
    @JvmName("toClassTyped")
    fun <T : Any> String.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassGlobal<T>(loader, initialize)

    /** @see toClassOrNullGlobal */
    fun String.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal(loader, initialize)

    /** @see toClassOrNullGlobal */
    @JvmName("toClassOrNullTyped")
    fun <T : Any> String.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal<T>(loader, initialize)

    /** @see toClassGlobal */
    fun VariousClass.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = load(loader, initialize)

    /** @see toClassGlobal */
    fun VariousClass.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = loadOrNull(loader, initialize)

    /** @see lazyClassGlobal */
    fun lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal(name, initialize, loader)

    /** @see lazyClassGlobal */
    @JvmName("lazyClassTyped")
    fun <T : Any> lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal<T>(name, initialize, loader)

    /** @see lazyClassGlobal */
    fun lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal(variousClass, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    fun lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal(name, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    @JvmName("lazyClassOrNullTyped")
    fun <T : Any> lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal<T>(name, initialize, loader)

    /** @see lazyClassOrNullGlobal */
    fun lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal(variousClass, initialize, loader)

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
    ) = YukiHookCreator.createChainHooker(listOf(this), priority, invocation).build()

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
    ) = YukiHookCreator.createChainHooker(listOf(self), priority, invocation).build()

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
    ) = YukiHookCreator.createChainHooker(map { it.self }, priority, invocation).build()

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
    ) = YukiHookCreator.createChainHooker(toList(), priority, invocation).build()

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
    ) = YukiHookCreator.createChainHooker(this, priority, invocation).build()

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

    private fun MemberResolver<*, *>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) = when (this) {
        is ConstructorResolver,
        is MethodResolver -> YukiHookCreator.createClassicHooker(listOf(self), priority, isLazyMode)
        else -> error("This type $this not support to hook, supported are constructor and method.")
    }

    private fun Collection<MemberResolver<*, *>>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(onEach {
            require(it is ConstructorResolver || it is MethodResolver) {
                "This type $it not support to hook, supported are constructor and method."
            }
        }.map { it.self }, priority, isLazyMode)

    @JvmName("createHookerByMember")
    private fun Collection<Member>.createHooker(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiHookCreator.createClassicHooker(onEach {
            require(it is Constructor<*> || it is Method) { "This type $it not support to hook, supported are constructor and method." }
        }, priority, isLazyMode)

    // region Deprecated properties and functions for backward compatibility

    /** Gets the APK file path of the current Xposed module. */
    @Deprecated(message = "Use moduleFilePath instead.", replaceWith = ReplaceWith("moduleFilePath"))
    val moduleAppFilePath get() = moduleFilePath

    /** Gets the [Resources] of the current Xposed module. */
    @Deprecated(message = "Use moduleResources instead.", replaceWith = ReplaceWith("moduleResources"))
    val moduleAppResources get() = moduleResources

    /** Refreshes the [Resources] of the current Xposed module. */
    @Deprecated(message = "Use refreshModuleResources instead.", replaceWith = ReplaceWith("refreshModuleResources()"))
    fun refreshModuleAppResources() = refreshModuleResources()

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