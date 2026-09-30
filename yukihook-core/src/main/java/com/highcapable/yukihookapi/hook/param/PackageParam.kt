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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.content.res.Resources
import androidx.annotation.RequiresApi
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry
import com.highcapable.yukihookapi.hook.core.YukiHookCreator
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.reflect.ClassLoaderInitializer
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.injectModuleResources
import com.highcapable.yukihookapi.hook.factory.registerModuleActivities
import com.highcapable.yukihookapi.hook.param.context.PackageContext
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle.AppLifecycleBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.ModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.file.YukiHookSharedFiles
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.typeOf
import com.highcapable.kavaref.extension.lazyClass as lazyClassGlobal
import com.highcapable.kavaref.extension.lazyClassOrNull as lazyClassOrNullGlobal
import com.highcapable.kavaref.extension.toClass as toClassGlobal
import com.highcapable.kavaref.extension.toClassOrNull as toClassOrNullGlobal

/**
 * Target host entry object implementation for hooking.
 * @param context the package context for [PackageParam], null by default.
 */
open class PackageParam internal constructor(internal var context: PackageContext? = null) {

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
     * This is unavailable when loaded as a standalone hook API.
     */
    class ModuleSpec internal constructor() {

        /**
         * Gets the [ApplicationInfo] of the current Xposed module.
         * @return [ApplicationInfo]
         * @throws IllegalStateException if the current hook framework does not support this feature.
         */
        @get:RequiresRuntime(YukiHookRuntime.LIBXPOSED)
        val appInfo get() = XposedModuleRuntime.moduleAppInfo ?: error("Current hook framework not support module application info.")

        /**
         * Gets the source directory of the current Xposed module.
         * @return [String]
         */
        val sourceDir get() = XposedModuleRuntime.moduleSourceDir

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
     * Setting an incorrect or invalid [ClassLoader] causes feature failures. Use this carefully.
     * @return [ClassLoader] or null.
     */
    var hostClassLoader
        get() = currentClassLoader ?: context?.classLoader ?: AppParasitism.currentApplication?.classLoader
        set(value) {
            currentClassLoader = value
        }

    /**
     * Gets the [ApplicationInfo] of the current host.
     * @return [ApplicationInfo]
     */
    val appInfo get() = context?.appInfo ?: AppParasitism.currentApplicationInfo ?: ApplicationInfo()

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
     * It may be null during initial loading. Retrieve it later or configure a [registerAppLifecycle] listener.
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
    val processName get() = context?.processName ?: AppParasitism.currentProcessName

    /**
     * Gets the package name of the current host.
     * @return [String]
     */
    val packageName get() = context?.packageName ?: AppParasitism.currentPackageName

    /**
     * Whether the current host is the first [Application].
     * @return [Boolean]
     */
    val isFirstApplication get() = context?.isFirstApplication ?: false

    /**
     * Gets the main process name of the current host.
     *
     * The runtime supplies the system process identity during [loadSystem], otherwise this corresponds to [packageName].
     * @return [String]
     */
    val mainProcessName get() = context?.mainProcessName ?: packageName.trim()

    /**
     * Gets the [ModuleSpec] of the current Xposed module.
     *
     * This is unavailable when loaded as a standalone hook API.
     */
    val module = ModuleSpec()

    /**
     * Creates a [YukiHookPreferences] object.
     *
     * This is unavailable when loaded as a standalone hook API and throws an exception.
     * @return [YukiHookPreferences]
     */
    val preferences get() = YukiHookPreferences.from()

    /**
     * Creates a [YukiHookPreferences] object.
     *
     * This is unavailable when loaded as a standalone hook API and throws an exception.
     * @param name the custom shared preferences storage name.
     * @return [YukiHookPreferences]
     */
    fun preferences(name: String) = preferences.name(name)

    /**
     * Gets the [YukiHookSharedFiles] object.
     *
     * This is unavailable when loaded as a standalone hook API and throws an exception.
     * @return [YukiHookSharedFiles]
     */
    @get:RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    val sharedFiles get() = YukiHookSharedFiles.get()

    /**
     * Gets the [YukiHookDataChannel] object.
     *
     * This is unavailable when loaded as a standalone hook API and throws an exception.
     * @return [YukiHookDataChannel.Namespace]
     * @throws IllegalStateException if loaded in [HookEntryType.ZYGOTE].
     */
    val dataChannel get() = if (context?.type != HookEntryType.ZYGOTE)
        YukiHookDataChannel.get().namespace(packageName = packageName)
    else error("YukiHookDataChannel cannot used in zygote events, please use it in loadApp or loadSystem.")

    /**
     * Binds the [PackageContext] to the current [PackageParam].
     * @param context the package context for [PackageParam].
     * @return [PackageParam]
     */
    internal fun bind(context: PackageContext?): PackageParam {
        currentClassLoader = null
        this.context = context
        return this
    }

    /**
     * Registers a lifecycle listener for the current host [Application].
     *
     * This is loaded only for the package that owns the first [Application] in the current process.
     * In system server, [AppLifecycle.onCreate] is dispatched after system services start because its [Application]
     * has already completed creation, while [AppLifecycle.attachBaseContext] cannot observe the completed event.
     * @param throwToHostOnFailure whether to throw exceptions to the host when they occur, true by default.
     * @param block the lifecycle block.
     */
    fun registerAppLifecycle(throwToHostOnFailure: Boolean = true, block: AppLifecycle.() -> Unit) {
        val current = context ?: return
        if (current.isFirstApplication) AppLifecycle.register(
            packageName = packageName,
            throwToHostOnFailure = throwToHostOnFailure,
            entryType = current.type,
            block = block
        )
    }

    /**
     * Registers the current Xposed module's [Activity] in the host when its [Application] is available.
     *
     * After registration, [Context.startActivity] can launch an [Activity] that is not registered in the host.
     * When an unregistered [Activity] starts in the host, this API automatically calls [Context.injectModuleResources] to inject module resources.
     *
     * Only the first request for this host is used. A failed registration may be retried.
     * When declared from `onHook`, package replay repeats this request after module reload.
     *
     * Direct registration on a [Context] remains immediate and is not replayed.
     * This is loaded only for the package that owns the first [Application] in the current process.
     *
     * - Requires Android 7.0 (API 24) or later.
     * @see Context.registerModuleActivities
     * @param proxy the proxy [Activity], which must exist in the host's AndroidManifest. Omit it to use the default [Activity].
     */
    @RequiresApi(AndroidVersion.N)
    fun registerModuleActivitiesWhenReady(proxy: Any? = null) {
        if (context?.type == HookEntryType.PACKAGE && context?.isFirstApplication == true)
            AppParasitism.registerModuleActivitiesWhenReady(packageName, proxy)
    }

    /**
     * Retains a value across module instances.
     *
     * With [YukiHookLibXposedEntry.HotReload] enabled, the same [key] and declared result type reuse the saved result without running [block].
     * Keys must be non-blank and unique per module instance.
     *
     * A change to the declared result type, its type arguments or nullability invalidates the saved result and runs [block] again.
     *
     * For example, changing `Int` to `String` or `List<Int>` to `List<String>` recomputes the value even with the same [key].
     * The cache type is the inferred or explicit [T], not the runtime class of the value.
     *
     * Change [key] when the calculation changes but the declared result type stays the same.
     * Values must not retain objects or classes owned by the module class loader.
     * Keep hook registration and other actions that must run after reload outside [block].
     *
     * Examples:
     *
     * ```kotlin
     * val title = remember("host-title-v1") {
     *     "$packageName ($processName)"
     * }
     * val method = remember("activity-onCreate-v1") {
     *     Activity::class.java.getDeclaredMethod("onCreate", Bundle::class.java)
     * }
     *
     * method.hook {
     *     after {
     *         instance<Activity>().title = title
     *     }
     * }
     * ```
     * @param key the versioned key of the result value.
     * @param block computes the value when no saved result has the same declared type.
     * @return [T] the saved or newly computed result.
     * @throws IllegalArgumentException if [key] is blank or already used in this module instance.
     * @throws IllegalStateException if the result cannot safely cross module class loaders.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
    inline fun <reified T> remember(key: String, noinline block: () -> T) = remember(typeOf<T>(), key, block)

    /**
     * Retains a value across module instances.
     *
     * With [YukiHookLibXposedEntry.HotReload] enabled, the same [key] and declared result type reuse the saved result without running [block].
     * Keys must be non-blank and unique per module instance.
     *
     * A change to the declared result type, its type arguments or nullability invalidates the saved result and runs [block] again.
     *
     * For example, changing `Int` to `String` or `List<Int>` to `List<String>` recomputes the value even with the same [key].
     * The cache type is the inferred or explicit [T], not the runtime class of the value.
     *
     * Change [key] when the calculation changes but the declared result type stays the same.
     * Values must not retain objects or classes owned by the module class loader.
     * Keep hook registration and other actions that must run after reload outside [block].
     * @param type the declared result type, including type arguments and nullability.
     * @param key the versioned key of the result value.
     * @param block computes the value when no saved result has the same declared type.
     * @return [T] the saved or newly computed result.
     * @throws IllegalArgumentException if [key] is blank or already used in this module instance.
     * @throws IllegalStateException if the result cannot safely cross module class loaders.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
    fun <T> remember(type: KType, key: String, block: () -> T) = XposedModuleRuntime.retainValue(type, key, block)

    /**
     * Loads and hooks apps with the specified package name.
     * @see loadSystem
     * @param name the package name.
     * @param block the param block.
     */
    fun loadApp(name: String, block: PackageParam.() -> Unit) {
        if (context?.type != HookEntryType.ZYGOTE && (context?.matchesPackage(name) == true || name.isBlank())) block(this)
    }

    /**
     * Loads and hooks apps with the specified package names.
     * @see loadSystem
     * @param name the package names.
     * @param block the param block.
     */
    fun loadApp(vararg name: String, block: PackageParam.() -> Unit) {
        if (name.isEmpty()) return loadApp(block = block)
        if (context?.type != HookEntryType.ZYGOTE && name.any { context?.matchesPackage(it) == true }) block(this)
    }

    /**
     * Loads and hooks apps with the specified package name.
     * @see loadSystem
     * @param name the package name.
     * @param hookers the hookers.
     */
    fun loadApp(name: String, hookers: YukiBaseHooker) {
        if (context?.type != HookEntryType.ZYGOTE && (context?.matchesPackage(name) == true || name.isBlank())) loadHooker(hookers)
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

        if (context?.type != HookEntryType.ZYGOTE &&
            (context?.matchesPackage(name) == true || name.isBlank())
        ) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks all apps.
     * @see loadSystem
     * @param excludeSelf whether to exclude the module itself, false by default. When enabled, the hosts do not include the current module.
     * @param block the param block.
     */
    fun loadApp(excludeSelf: Boolean = false, block: PackageParam.() -> Unit) {
        if (context?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || context?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
        ) block(this)
    }

    /**
     * Loads and hooks all apps.
     * @see loadSystem
     * @param excludeSelf whether to exclude the module itself, false by default. When enabled, the hosts do not include the current module.
     * @param hooker the hooker.
     */
    fun loadApp(excludeSelf: Boolean = false, hooker: YukiBaseHooker) {
        if (context?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || context?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
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

        if (context?.type != HookEntryType.ZYGOTE &&
            (!excludeSelf || context?.matchesPackage(XposedModuleRuntime.modulePackageName) != true)
        ) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks the system server.
     * @see loadApp
     * @param block the param block.
     */
    fun loadSystem(block: PackageParam.() -> Unit) {
        if (context?.type == HookEntryType.SYSTEM_SERVER) block(this)
    }

    /**
     * Loads and hooks the system server.
     * @see loadApp
     * @param hooker the hooker.
     */
    fun loadSystem(hooker: YukiBaseHooker) {
        if (context?.type == HookEntryType.SYSTEM_SERVER) loadHooker(hooker)
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

        if (context?.type == HookEntryType.SYSTEM_SERVER) hookers.forEach { loadHooker(it) }
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
     * @see loadApp
     * @see loadSystem
     * @param block the param block.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Use loadApp or loadSystem instead.")
    fun loadZygote(block: PackageParam.() -> Unit) {
        if (context?.type == HookEntryType.ZYGOTE) block(this)
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
     * @see loadApp
     * @see loadSystem
     * @param hooker the hooker.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Use loadApp or loadSystem instead.")
    fun loadZygote(hooker: YukiBaseHooker) {
        if (context?.type == HookEntryType.ZYGOTE) loadHooker(hooker)
    }

    /**
     * Loads legacy zygote events.
     *
     * - Migrate app hooks to [loadApp] and system server hooks to [loadSystem]. These run later
     *   and cannot preserve parent-zygote execution or fork inheritance.
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

        if (context?.type == HookEntryType.ZYGOTE) hookers.forEach { loadHooker(it) }
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
     * Invokes the method with the given arguments.
     *
     * This action calls the original method without hook.
     * @see MethodResolver.invoke
     * @receiver [MethodResolver]
     * @return [T] or null.
     */
    @JvmName("invokeOriginalTyped")
    fun <T> MethodResolver<*>.invokeOriginal(vararg args: Any?) =
        YukiHookHelper.invokeOriginal(self, instance, args) as T?

    /**
     * Invokes the method with the given arguments and ignore any exceptions.
     *
     * This action calls the original method without hook.
     * @see MethodResolver.invokeQuietly
     * @receiver [MethodResolver]
     * @return [T] or null.
     */
    @JvmName("invokeOriginalQuietlyTyped")
    fun <T> MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
        runCatching { invokeOriginal<T>(*args) }.getOrNull()

    /**
     * Invokes the method with the given arguments.
     *
     * This action calls the original method without hook.
     * @see MethodResolver.invoke
     * @receiver [MethodResolver]
     * @return [Any] or null.
     */
    fun MethodResolver<*>.invokeOriginal(vararg args: Any?) =
        YukiHookHelper.invokeOriginal(self, instance, args)

    /**
     * Invokes the method with the given arguments and ignore any exceptions.
     *
     * This action calls the original method without hook.
     * @see MethodResolver.invokeQuietly
     * @receiver [MethodResolver]
     * @return [Any] or null.
     */
    fun MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
        runCatching { invokeOriginal(*args) }.getOrNull()

    /**
     * Invokes this method non-virtually on its instance with the given arguments.
     *
     * This action directly invokes the method declared by this resolver without hook or subclass overrides.
     * An instance must be set using [MethodResolver.of].
     * @see MethodResolver.invoke
     * @receiver [MethodResolver]
     * @return [T] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("invokeSpecialTyped")
    fun <T> MethodResolver<*>.invokeSpecial(vararg args: Any?) =
        YukiHookHelper.invokeSpecial(self, checkNotNull(instance) { "An instance is required for special invocation." }, args) as T

    /**
     * Invokes this method non-virtually on its instance with the given arguments and ignore any exceptions.
     *
     * This action directly invokes the method declared by this resolver without hook or subclass overrides.
     * An instance must be set using [MethodResolver.of].
     * @see MethodResolver.invokeQuietly
     * @receiver [MethodResolver]
     * @return [T] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("invokeSpecialQuietlyTyped")
    fun <T> MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
        runCatching { invokeSpecial<T>(*args) }.getOrNull()

    /**
     * Invokes this method non-virtually on its instance with the given arguments.
     *
     * This action directly invokes the method declared by this resolver without hook or subclass overrides.
     * An instance must be set using [MethodResolver.of].
     * @see MethodResolver.invoke
     * @receiver [MethodResolver]
     * @return [Any] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun MethodResolver<*>.invokeSpecial(vararg args: Any?) =
        YukiHookHelper.invokeSpecial(self, checkNotNull(instance) { "An instance is required for special invocation." }, args)

    /**
     * Invokes this method non-virtually on its instance with the given arguments and ignore any exceptions.
     *
     * This action directly invokes the method declared by this resolver without hook or subclass overrides.
     * An instance must be set using [MethodResolver.of].
     * @see MethodResolver.invokeQuietly
     * @receiver [MethodResolver]
     * @return [Any] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
        runCatching { invokeSpecial(*args) }.getOrNull()

    /**
     * Initialize an existing [instance] non-virtually with this constructor and the given arguments.
     *
     * This action initializes the existing instance and does not create a new one.
     * @receiver [ConstructorResolver]
     * @param instance the instance to initialize.
     * @return [Any] null after the constructor completes.
     */
    fun ConstructorResolver<*>.initializeSpecial(instance: Any, vararg args: Any?) =
        YukiHookHelper.invokeSpecial(self, instance, args)

    /**
     * Initialize an existing [instance] non-virtually with this constructor and the given arguments and ignore any exceptions.
     *
     * This action initializes the existing instance and does not create a new one.
     * @receiver [ConstructorResolver]
     * @param instance the instance to initialize.
     * @return [Any] or null.
     */
    fun ConstructorResolver<*>.initializeSpecialQuietly(instance: Any, vararg args: Any?) =
        runCatching { initializeSpecial(instance, *args) }.getOrNull()

    /**
     * Creates a new instance with this original constructor and the given arguments.
     *
     * This action skips all hooks registered for the constructor.
     * @receiver [ConstructorResolver]
     * @see ConstructorResolver.create
     * @return [T]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> ConstructorResolver<T>.createOriginal(vararg args: Any?) =
        YukiHookHelper.newInstanceOriginal(self, args) as T

    /**
     * Creates a new instance with this original constructor and the given arguments, and ignore any exceptions.
     *
     * This action skips all hooks registered for the constructor.
     * @receiver [ConstructorResolver]
     * @see ConstructorResolver.createQuietly
     * @return [T] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> ConstructorResolver<T>.createOriginalQuietly(vararg args: Any?) =
        runCatching { createOriginal(*args) }.getOrNull()

    /**
     * Creates a [subclass] instance initialized by this parent constructor and the given arguments.
     *
     * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
     * so subclass fields can remain uninitialized.
     * @receiver [ConstructorResolver]
     * @see ConstructorResolver.create
     * @param subclass the subclass to instantiate.
     * @return [T]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> ConstructorResolver<*>.createSpecial(subclass: Class<T>, vararg args: Any?) =
        YukiHookHelper.newInstanceSpecial(self, subclass, args) as T

    /**
     * Creates a [subclass] instance initialized by this parent constructor and the given arguments, and ignore any exceptions.
     *
     * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
     * so subclass fields can remain uninitialized.
     * @receiver [ConstructorResolver]
     * @see ConstructorResolver.createQuietly
     * @param subclass the subclass to instantiate.
     * @return [T] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> ConstructorResolver<*>.createSpecialQuietly(subclass: Class<T>, vararg args: Any?) =
        runCatching { createSpecial(subclass, *args) }.getOrNull()

    /**
     * Invokes this method on the given [instance] with the given arguments.
     *
     * This action calls the original method without hook.
     * @receiver [Method]
     * @param instance the instance to invoke the method on, or null for a static method.
     * @return [T] or null.
     */
    @JvmName("invokeOriginalTyped")
    fun <T> Method.invokeOriginal(instance: Any?, vararg args: Any?) =
        YukiHookHelper.invokeOriginal(this, instance, args) as T?

    /**
     * Invokes this method on the given [instance] with the given arguments.
     *
     * This action calls the original method without hook.
     * @receiver [Method]
     * @param instance the instance to invoke the method on, or null for a static method.
     * @return [Any] or null.
     */
    fun Method.invokeOriginal(instance: Any?, vararg args: Any?) =
        YukiHookHelper.invokeOriginal(this, instance, args)

    /**
     * Invokes this method non-virtually on the given [instance] with the given arguments.
     *
     * This action directly invokes this method without hook or subclass overrides.
     * @receiver [Method]
     * @param instance the instance to invoke the method on.
     * @return [T] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("invokeSpecialTyped")
    fun <T> Method.invokeSpecial(instance: Any, vararg args: Any?) =
        YukiHookHelper.invokeSpecial(this, instance, args) as T

    /**
     * Invokes this method non-virtually on the given [instance] with the given arguments.
     *
     * This action directly invokes this method without hook or subclass overrides.
     * @receiver [Method]
     * @param instance the instance to invoke the method on.
     * @return [Any] or null.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Method.invokeSpecial(instance: Any, vararg args: Any?) =
        YukiHookHelper.invokeSpecial(this, instance, args)

    /**
     * Initialize an existing [instance] non-virtually with this constructor and the given arguments.
     *
     * This action initializes the existing instance and does not create a new one.
     * @receiver [Constructor]
     * @param instance the instance to initialize.
     * @return [Any] null after the constructor completes.
     */
    fun Constructor<*>.initializeSpecial(instance: Any, vararg args: Any?) =
        YukiHookHelper.invokeSpecial(this, instance, args)

    /**
     * Creates a new instance with this original constructor and the given arguments.
     *
     * This action skips all hooks registered for the constructor.
     * @receiver [Constructor]
     * @return [T]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> Constructor<T>.newInstanceOriginal(vararg args: Any?) =
        YukiHookHelper.newInstanceOriginal(this, args) as T

    /**
     * Creates a [subclass] instance initialized by this parent constructor and the given arguments.
     *
     * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
     * so subclass fields can remain uninitialized.
     * @receiver [Constructor]
     * @param subclass the subclass to instantiate.
     * @return [T]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun <T : Any> Constructor<T>.newInstanceSpecial(subclass: Class<T>, vararg args: Any?) =
        YukiHookHelper.newInstanceSpecial(this, subclass, args) as T

    /**
     * Deoptimizes this calling method or constructor so its callees are invoked without runtime inlining.
     *
     * Use this on the caller containing an inlined call, not on the hooked callee.
     * @receiver the calling method or constructor to deoptimize.
     * @return [Boolean] whether deoptimization succeeded.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Member.deoptimize() = YukiHookHelper.deoptimize(this)

    /**
     * Deoptimizes this calling method or constructor through [MemberResolver] so its callees are invoked
     * without runtime inlining.
     *
     * Use this on the caller containing an inlined call, not on the hooked callee.
     * @receiver the calling method or constructor to deoptimize.
     * @return [Boolean] whether deoptimization succeeded.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun MemberResolver<*, *>.deoptimize() = YukiHookHelper.deoptimize(self)

    /**
     * Deoptimizes these calling methods and constructors through a [Collection] of [MemberResolver] instances.
     *
     * A false result does not stop the remaining members. Use these on callers containing inlined calls, not on hooked callees.
     * @receiver the calling methods and constructors to deoptimize.
     * @return [Boolean] whether every deoptimization succeeded.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("deoptimizeAllByMemberResolver")
    fun Collection<MemberResolver<*, *>>.deoptimizeAll() = deoptimizeAll { it.self }

    /**
     * Deoptimizes these calling methods and constructors so their callees are invoked without runtime inlining.
     *
     * A false result does not stop the remaining members. Use these on callers containing inlined calls, not on hooked callees.
     * @receiver the calling methods and constructors to deoptimize.
     * @return [Boolean] whether every deoptimization succeeded.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<Member>.deoptimizeAll() = asIterable().deoptimizeAll { it }

    /**
     * Deoptimizes these calling methods and constructors so their callees are invoked without runtime inlining.
     *
     * A false result does not stop the remaining members. Use these on callers containing inlined calls, not on hooked callees.
     * @receiver the calling methods and constructors to deoptimize.
     * @return [Boolean] whether every deoptimization succeeded.
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    @JvmName("deoptimizeAllByMember")
    fun Collection<Member>.deoptimizeAll() = deoptimizeAll { it }

    /**
     * Creates a direct chain hook for the static initializer of this class.
     *
     * Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = YukiHookCreator.createChainHooker(listOf(HookPoint.Initializer(this)), priority, invocation).build()

    /**
     * Creates a direct chain hook for the static initializer of this [KClass].
     *
     * If the class is already initialized, this hook is never invoked.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.intercept(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = java.intercept(priority, invocation)

    /**
     * Creates direct chain hooks for the static initializers of these classes.
     *
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Initializer(it) }, priority, invocation).build()

    /**
     * Creates direct chain hooks for the static initializers of these [KClass] instances.
     *
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any?
    ) = map { it.java }.interceptAll(priority, invocation)

    /**
     * Creates direct chain hooks for the static initializers of these classes.
     *
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting without [HookChain.proceed] skips static initialization and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param invocation the chain hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @JvmName("interceptAllByMember")
    fun Collection<Member>.interceptAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        invocation: HookChain.() -> Any? = { null }
    ) = YukiHookCreator.createChainHooker(map { HookPoint.Executable(it) }, priority, invocation).build()

    /**
     * Replaces a chain hook registration with a new chain callback.
     *
     * The returned handle remains the same registration group.
     * Each target in a batch is replaced independently.
     *
     * An invocation already in progress continues with the previous callback.
     * @receiver the chain hook registration handle.
     * @param invocation the replacement chain callback.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
    @JvmName("replaceChain")
    fun YukiHookCreator.MemberHooker<HookChain>.Handle.replace(
        invocation: HookChain.() -> Any?
    ) = apply { owner.asType<YukiHookCreator.ChainMemberHooker>().replace(invocation) }

    /**
     * Replaces a classic hook registration with a new classic callback configuration.
     *
     * The returned handle remains the same registration group.
     * The block may configure [YukiHookCreator.ClassicMemberHooker.before], [YukiHookCreator.ClassicMemberHooker.after]
     * or [YukiHookCreator.ClassicMemberHooker.intercept].
     * Each target in a batch is replaced independently.
     *
     * An invocation already in progress continues with the previous callback.
     * @receiver the classic hook registration handle.
     * @param hooker the replacement classic callback configuration.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
    @JvmName("replaceClassic")
    fun YukiHookCreator.MemberHooker<HookParam>.Handle.replace(
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = apply { owner.asType<YukiHookCreator.ClassicMemberHooker>().replace(hooker) }

    /**
     * Creates a direct hook for the static initializer of this class.
     *
     * Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = listOf(this).createHooker(priority)

    /**
     * Creates a direct hook for the static initializer of this class.
     *
     * Obtain the class without initializing it. If the class is already initialized, this hook is never invoked.
     * Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the class whose static initializer will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Class<*>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = listOf(this).createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates a direct hook for the static initializer of this [KClass].
     *
     * If the class is already initialized, this hook is never invoked.
     * Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = java.hook(priority)

    /**
     * Creates a direct hook for the static initializer of this [KClass].
     *
     * If the class is already initialized, this hook is never invoked.
     * Intercepting the initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun KClass<*>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = java.hook(priority, hooker)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = toList().createHooker(priority)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out Class<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = toList().createHooker(priority, isLazyMode = true).apply(hooker).build()

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiHookCreator.ClassicMemberHooker]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
        map { it.java }.hookAll(priority)

    /**
     * Creates direct hooks for the static initializers of these [KClass] instances.
     *
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
    fun Array<out KClass<*>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = map { it.java }.hookAll(priority, hooker)

    /**
     * Creates direct hooks for the static initializers of these classes.
     *
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
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
     * Each class is registered independently, this operation is not atomic.
     * Obtain every class without initializing it. An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @receiver the classes whose static initializers will be hooked.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
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
     * Each class is registered independently, this operation is not atomic.
     * An already initialized class never invokes this hook.
     * Intercepting an initializer skips its execution and leaves static fields at JVM defaults.
     * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param hooker the hook block.
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    @JvmName("hookAllByMemberResolver")
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
     * @return [YukiHookCreator.MemberHooker.Handle]
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
     * @return [YukiHookCreator.MemberHooker.Handle]
     */
    fun Collection<Member>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
    ) = createHooker(priority, isLazyMode = true).apply(hooker).build()

    private inline fun <T> Iterable<T>.deoptimizeAll(member: (T) -> Member): Boolean {
        var isSuccessful = true
        forEach { if (!YukiHookHelper.deoptimize(member(it))) isSuccessful = false }
        return isSuccessful
    }

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
    @Deprecated(message = "Use module.sourceDir instead.", replaceWith = ReplaceWith("module.sourceDir"))
    val moduleAppFilePath get() = module.sourceDir

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
     * @param throwToHostOnFailure whether to throw exceptions to the host when they occur.
     */
    class AppLifecycle private constructor(private val throwToHostOnFailure: Boolean) {

        internal companion object {

            /**
             * Registers a lifecycle listener for the current host [Application].
             * @param packageName the package name of the current host.
             * @param throwToHostOnFailure whether to throw exceptions to the host when they occur.
             * @param entryType the current hook entry type.
             */
            fun register(
                packageName: String,
                throwToHostOnFailure: Boolean,
                entryType: HookEntryType,
                block: AppLifecycle.() -> Unit
            ) = AppParasitism.registerAppLifecycle(packageName, entryType, AppLifecycle(throwToHostOnFailure).apply(block)::build)
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

        private fun build() = object : AppLifecycleBridge {

            override val throwToHostOnFailure = this@AppLifecycle.throwToHostOnFailure
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
        }
    }

    override fun toString() = "PackageParam(${super.toString()}) by $context"
}