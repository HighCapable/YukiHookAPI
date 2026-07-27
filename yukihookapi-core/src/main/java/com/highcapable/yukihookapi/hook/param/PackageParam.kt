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
@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE", "DeprecatedCallableAddReplaceWith", "DEPRECATION",
    "TYPEALIAS_EXPANSION_DEPRECATION"
)

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
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.bean.HookResources
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.YukiResourcesHookCreator
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.core.annotation.LegacyResourcesHook
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.classes.DexClassFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ClassConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ClassLoaderInitializer
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.LazyClass
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.utils.factory.value
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import com.highcapable.kavaref.extension.lazyClass as lazyClassGlobal
import com.highcapable.kavaref.extension.lazyClassOrNull as lazyClassOrNullGlobal
import com.highcapable.kavaref.extension.toClass as toClassGlobal
import com.highcapable.kavaref.extension.toClassOrNull as toClassOrNullGlobal
import com.highcapable.yukihookapi.hook.bean.VariousClass as LegacyVariousClass
import com.highcapable.yukihookapi.hook.factory.hasClass as hasClassLegacy
import com.highcapable.yukihookapi.hook.factory.lazyClass as lazyClassGlobalLegacy
import com.highcapable.yukihookapi.hook.factory.lazyClassOrNull as lazyClassOrNullGlobalLegacy

/**
 * Target APP entry object implementation for Hooking.
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
     * Gets or sets the [ClassLoader] of the current Hook APP.
     *
     * You can manually set the [ClassLoader] of the current Hook APP here. It is obtained automatically by default.
     *
     * - Setting an incorrect or invalid [ClassLoader] causes feature failures. Use this carefully.
     * @return [ClassLoader] or null.
     */
    var appClassLoader
        get() = currentClassLoader ?: wrapper?.appClassLoader ?: AppParasitics.currentApplication?.classLoader
        set(value) {
            currentClassLoader = value
        }

    /**
     * Gets the [ApplicationInfo] of the current Hook APP.
     * @return [ApplicationInfo]
     */
    val appInfo get() = wrapper?.appInfo ?: AppParasitics.currentApplicationInfo ?: ApplicationInfo()

    /**
     * Gets the user ID of the current Hook APP.
     *
     * The owner is 0. Cloned apps and work profiles have different IDs depending on the system environment.
     * @return [Int]
     */
    val appUserId get() = AppParasitics.findUserId(packageName)

    /**
     * Gets the [Application] instance of the current Hook APP.
     *
     * - It may be null during initial loading. Retrieve it later or configure an [onAppLifecycle] listener.
     * @return [Application] or null.
     */
    val appContext get() = AppParasitics.hostApplication ?: AppParasitics.currentApplication

    /**
     * Gets the [Resources] of the current Hook APP.
     *
     * - This can only be called inside [HookResources.hook] or after [appContext] finishes loading.
     * @return [Resources] or null.
     */
    val appResources get() = wrapper?.appResources ?: appContext?.resources

    /**
     * Gets the [Context] of the current system framework.
     * @return [Context] the ContextImpl instance.
     * @throws IllegalStateException if the [Context] of the system framework cannot be obtained.
     */
    val systemContext get() = AppParasitics.systemContext ?: error("Failed to got SystemContext")

    /**
     * Gets the process name of the current Hook APP.
     *
     * The default process name is [packageName].
     * @return [String]
     */
    val processName get() = wrapper?.processName ?: AppParasitics.currentProcessName

    /**
     * Gets the package name of the current Hook APP.
     * @return [String]
     */
    val packageName get() = wrapper?.packageName ?: AppParasitics.currentPackageName

    /**
     * Whether the current Hook APP is the first [Application].
     * @return [Boolean]
     */
    val isFirstApplication get() = packageName.trim() == processName.trim()

    /**
     * Gets the main process name of the current Hook APP.
     *
     * This corresponds to [packageName].
     * @return [String]
     */
    val mainProcessName get() = packageName.trim()

    /**
     * Gets the APK file path of the current Xposed module.
     *
     * - This is unavailable when loaded as a Hook API and returns an empty string.
     * @return [String]
     */
    val moduleAppFilePath get() = YukiXposedModule.moduleAppFilePath

    /**
     * Gets the [Resources] of the current Xposed module.
     *
     * - This is unavailable when loaded as a Hook API or under an unsupported Hook Framework and throws an exception.
     * @return [YukiModuleResources]
     * @throws IllegalStateException if the current Hook Framework does not support this feature.
     */
    val moduleAppResources
        get() = (if (YukiHookAPI.Configs.isEnableModuleAppResourcesCache) YukiXposedModule.moduleAppResources
        else YukiXposedModule.dynamicModuleAppResources) ?: error("Current Hook Framework not support moduleAppResources")

    /**
     * Creates a [YukiHookPrefsBridge] object.
     *
     * - This is unavailable when loaded as a Hook API and throws an exception.
     * @return [YukiHookPrefsBridge]
     */
    val prefs get() = YukiHookPrefsBridge.from()

    /**
     * Creates a [YukiHookPrefsBridge] object.
     *
     * - This is unavailable when loaded as a Hook API and throws an exception.
     * @param name the custom SharedPreferences storage name.
     * @return [YukiHookPrefsBridge]
     */
    fun prefs(name: String) = prefs.name(name)

    /**
     * Gets the [YukiHookDataChannel] object.
     *
     * - This is unavailable when loaded as a Hook API and throws an exception.
     * @return [YukiHookDataChannel.NameSpace]
     * @throws IllegalStateException if loaded in [HookEntryType.ZYGOTE].
     */
    val dataChannel
        get() = if (wrapper?.type != HookEntryType.ZYGOTE)
            YukiHookDataChannel.instance().nameSpace(packageName = packageName)
        else error("YukiHookDataChannel cannot used in zygote")

    /**
     * Sets the [PackageParamWrapper] used by [PackageParam].
     * @param wrapper the parameter wrapper instance for [PackageParam].
     * @return [PackageParam]
     */
    internal fun assign(wrapper: PackageParamWrapper?): PackageParam {
        this.wrapper = wrapper
        return this
    }

    /**
     * Gets the [YukiResources] object of the current Hook APP.
     *
     * Call [HookResources.hook] to start Hooking.
     * @return [HookResources]
     */
    @LegacyResourcesHook
    fun resources() = HookResources(wrapper?.appResources)

    /** Refreshes the [Resources] of the current Xposed module. */
    fun refreshModuleAppResources() = YukiXposedModule.refreshModuleAppResources()

    /**
     * Listens for lifecycle loading events of the current Hook APP.
     *
     * - This is not loaded in [loadZygote] and is loaded only in [loadSystem] and [loadApp].
     *
     * - When loaded as a Hook API, use the native [Application] to implement lifecycle listeners.
     * @param isOnFailureThrowToApp whether to throw exceptions to the host when they occur, true by default. This setting is effective only for the first Hooker.
     * @param initiate the lifecycle block.
     */
    inline fun onAppLifecycle(isOnFailureThrowToApp: Boolean = true, initiate: AppLifecycle.() -> Unit) =
        AppLifecycle(isOnFailureThrowToApp).apply(initiate).build()

    /**
     * Loads and hooks the APP with the specified package name.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param name the package name.
     * @param initiate the Hook block.
     */
    inline fun loadApp(name: String, initiate: PackageParam.() -> Unit) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (packageName == name || name.isBlank())) initiate(this)
    }

    /**
     * Loads and hooks APPs with the specified package names.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param name the package names.
     * @param initiate the Hook block.
     */
    inline fun loadApp(vararg name: String, initiate: PackageParam.() -> Unit) {
        if (name.isEmpty()) return loadApp(initiate = initiate)
        if (wrapper?.type != HookEntryType.ZYGOTE && name.any { it == packageName }) initiate(this)
    }

    /**
     * Loads and hooks the APP with the specified package name.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param name the package name.
     * @param hooker the Hook subclass.
     */
    fun loadApp(name: String, hooker: YukiBaseHooker) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (packageName == name || name.isBlank())) loadHooker(hooker)
    }

    /**
     * Loads and hooks the APP with the specified package name.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param name the package name. An empty name matches all APPs except [loadZygote] events.
     * @param hooker the Hook subclasses.
     */
    fun loadApp(name: String, vararg hooker: YukiBaseHooker) {
        if (hooker.isEmpty()) error("loadApp method need a \"hooker\" param")
        if (wrapper?.type != HookEntryType.ZYGOTE && (packageName == name || name.isBlank())) hooker.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks all APPs.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param isExcludeSelf whether to exclude the module itself, false by default. When enabled, the hooked APPs do not include the current module.
     * @param initiate the Hook block.
     */
    inline fun loadApp(isExcludeSelf: Boolean = false, initiate: PackageParam.() -> Unit) {
        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (isExcludeSelf.not() || isExcludeSelf && packageName != YukiXposedModule.modulePackageName)
        ) initiate(this)
    }

    /**
     * Loads and hooks all APPs.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param isExcludeSelf whether to exclude the module itself, false by default. When enabled, the hooked APPs do not include the current module.
     * @param hooker the Hook subclass.
     */
    fun loadApp(isExcludeSelf: Boolean = false, hooker: YukiBaseHooker) {
        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (isExcludeSelf.not() || isExcludeSelf && packageName != YukiXposedModule.modulePackageName)
        ) loadHooker(hooker)
    }

    /**
     * Loads and hooks all APPs.
     *
     * Use [loadZygote] to load APP Zygote events.
     *
     * Use [loadSystem] to hook the system framework.
     * @param isExcludeSelf whether to exclude the module itself, false by default. When enabled, the hooked APPs do not include the current module.
     * @param hooker the Hook subclasses.
     */
    fun loadApp(isExcludeSelf: Boolean = false, vararg hooker: YukiBaseHooker) {
        if (hooker.isEmpty()) error("loadApp method need a \"hooker\" param")
        if (wrapper?.type != HookEntryType.ZYGOTE &&
            (isExcludeSelf.not() || isExcludeSelf && packageName != YukiXposedModule.modulePackageName)
        ) hooker.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks the system framework.
     * @param initiate the Hook block.
     */
    inline fun loadSystem(initiate: PackageParam.() -> Unit) = loadApp(AppParasitics.SYSTEM_FRAMEWORK_NAME, initiate)

    /**
     * Loads and hooks the system framework.
     * @param hooker the Hook subclass.
     */
    fun loadSystem(hooker: YukiBaseHooker) = loadApp(AppParasitics.SYSTEM_FRAMEWORK_NAME, hooker)

    /**
     * Loads and hooks the system framework.
     * @param hooker the Hook subclasses.
     */
    fun loadSystem(vararg hooker: YukiBaseHooker) {
        if (hooker.isEmpty()) error("loadSystem method need a \"hooker\" param")
        loadApp(AppParasitics.SYSTEM_FRAMEWORK_NAME, *hooker)
    }

    /**
     * Loads APP Zygote events.
     * @param initiate the Hook block.
     */
    inline fun loadZygote(initiate: PackageParam.() -> Unit) {
        if (wrapper?.type == HookEntryType.ZYGOTE) initiate(this)
    }

    /**
     * Loads APP Zygote events.
     * @param hooker the Hook subclass.
     */
    fun loadZygote(hooker: YukiBaseHooker) {
        if (wrapper?.type == HookEntryType.ZYGOTE) loadHooker(hooker)
    }

    /**
     * Loads APP Zygote events.
     * @param hooker the Hook subclasses.
     */
    fun loadZygote(vararg hooker: YukiBaseHooker) {
        if (hooker.isEmpty()) error("loadZygote method need a \"hooker\" param")
        if (wrapper?.type == HookEntryType.ZYGOTE) hooker.forEach { loadHooker(it) }
    }

    /**
     * Loads and hooks a specified APP process.
     * @param name the process name. Use [mainProcessName] to specify the main process, which has the same effect as [isFirstApplication].
     * @param initiate the Hook block.
     */
    inline fun withProcess(name: String, initiate: PackageParam.() -> Unit) {
        if (processName == name) initiate(this)
    }

    /**
     * Loads and hooks specified APP processes.
     * @param name the process names. Use [mainProcessName] to specify the main process, which has the same effect as [isFirstApplication].
     * @param initiate the Hook block.
     */
    inline fun withProcess(vararg name: String, initiate: PackageParam.() -> Unit) {
        if (name.isEmpty()) error("withProcess method need a \"name\" param")
        if (name.any { it == processName }) initiate(this)
    }

    /**
     * Loads and hooks a specified APP process.
     * @param name the process name. Use [mainProcessName] to specify the main process, which has the same effect as [isFirstApplication].
     * @param hooker the Hook subclass.
     */
    fun withProcess(name: String, hooker: YukiBaseHooker) {
        if (processName == name) loadHooker(hooker)
    }

    /**
     * Loads and hooks a specified APP process.
     * @param name the process name. Use [mainProcessName] to specify the main process, which has the same effect as [isFirstApplication].
     * @param hooker the Hook subclasses.
     */
    fun withProcess(name: String, vararg hooker: YukiBaseHooker) {
        if (name.isEmpty()) error("withProcess method need a \"hooker\" param")
        if (processName == name) hooker.forEach { loadHooker(it) }
    }

    /**
     * Loads a Hook subclass.
     *
     * You can continue loading Hookers from a Hooker.
     * @param hooker the Hook subclass.
     */
    fun loadHooker(hooker: YukiBaseHooker) {
        hooker.wrapper?.also {
            if (it.packageName.isNotBlank() && it.type != HookEntryType.ZYGOTE)
                if (it.packageName == wrapper?.packageName)
                    hooker.assignInstance(packageParam = this)
                else YLog.innerW(
                    msg = "This Hooker \"${hooker::class.java.name}\" is singleton or reused, " +
                        "but the current process has multiple package name \"${wrapper?.packageName}\", " +
                        "the original is \"${it.packageName}\"\n" +
                        "Make sure your Hooker supports multiple instances for this situation\n" +
                        "The process with package name \"${wrapper?.packageName}\" will be ignored"
                )
            else hooker.assignInstance(packageParam = this)
        } ?: hooker.assignInstance(packageParam = this)
    }

    /**
     * Finds [Class] instances in the current Hook APP Dex through [appClassLoader] using specified conditions.
     *
     * - This function can be very time-consuming when there are too many [Class] instances or the lookup conditions are complex.
     *
     * - Enabling [async] or setting [name] is recommended. [name] automatically creates a local cache for different versions of the Hook APP (host) to improve efficiency.
     *
     * - This feature is still experimental. Performance and stability issues may remain. Report any issues you encounter and help us improve it.
     * @param name the name identifying the current [Class] cache. Caching is disabled when omitted. Enabling caching automatically enables [async].
     * @param async whether asynchronous lookup is enabled, false by default.
     * @param initiate the finder block.
     * @return [DexClassFinder.Result]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun searchClass(name: String = "", async: Boolean = false, initiate: ClassConditions) =
        DexClassFinder(name, async = async || name.isNotBlank(), appClassLoader).apply(initiate).build()

    /**
     * Converts a string class name to a concrete class in the current Hook APP.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [toClass].
     * @return [Class]
     * @throws NoClassDefFoundError if the [Class] cannot be found.
     */
    @Deprecated(message = "Use the new naming method", ReplaceWith("toClass()"))
    val String.clazz
        get() = toClass()

    /**
     * Converts [LegacyVariousClass] to a concrete class in the current Hook APP.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [toClass].
     * @return [Class]
     * @throws IllegalStateException if no [Class] matches.
     */
    @Deprecated(message = "Use the new naming method", ReplaceWith("toClass()"))
    val LegacyVariousClass.clazz
        get() = toClass()

    /**
     * Checks whether a string class name exists.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [hasClass].
     * @return [Boolean] whether the class exists.
     */
    @Deprecated(message = "Use the new naming method", ReplaceWith("hasClass()"))
    val String.hasClass
        get() = hasClass()

    /**
     * Converts a string class name to a concrete class in [loader].
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class]
     * @throws NoClassDefFoundError if the [Class] cannot be found.
     */
    fun String.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassGlobal(loader, initialize)

    /**
     * Converts a string class name to a concrete class in [loader].
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class]<[T]>
     * @throws NoClassDefFoundError if the [Class] cannot be found.
     * @throws IllegalStateException if the [Class] type is not [T].
     */
    @JvmName("toClass_Generics")
    inline fun <reified T : Any> String.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassGlobal<T>(loader, initialize)

    /**
     * Converts a string class name to a concrete class in [loader].
     *
     * Returns null without throwing an exception when the [Class] cannot be found.
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class] or null.
     */
    fun String.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal(loader, initialize)

    /**
     * Converts a string class name to a concrete class in [loader].
     *
     * Returns null without throwing an exception when the [Class] cannot be found.
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class]<[T]> or null.
     */
    @JvmName("toClassOrNull_Generics")
    inline fun <reified T : Any> String.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) =
        toClassOrNullGlobal<T>(loader, initialize)

    /**
     * Converts [LegacyVariousClass] to a concrete class in [loader].
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class]
     * @throws IllegalStateException if no [Class] matches.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun LegacyVariousClass.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = get(loader, initialize)

    /**
     * Converts [VariousClass] to a concrete class in [loader].
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class]
     * @throws IllegalStateException if no [Class] matches.
     */
    fun VariousClass.toClass(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = load(loader, initialize)

    /**
     * Converts [LegacyVariousClass] to a concrete class in [loader].
     *
     * Returns null without throwing an exception when no [Class] matches.
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun LegacyVariousClass.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = getOrNull(loader, initialize)

    /**
     * Converts [VariousClass] to a concrete class in [loader].
     *
     * Returns null without throwing an exception when no [Class] matches.
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @return [Class] or null.
     */
    fun VariousClass.toClassOrNull(loader: ClassLoader? = appClassLoader, initialize: Boolean = false) = loadOrNull(loader, initialize)

    /**
     * Creates a lazily loaded non-null [Class] instance.
     * @param name the fully qualified class name.
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.NonNull]
     */
    fun lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal(name, initialize, loader)

    /**
     * Creates a lazily loaded non-null [Class] instance of type [T].
     * @param name the fully qualified class name.
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.NonNull]<[T]>
     */
    @JvmName("lazyClass_Generics")
    inline fun <reified T : Any> lazyClass(name: String, initialize: Boolean = false, noinline loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal<T>(name, initialize, loader)

    /**
     * Creates a lazily loaded non-null [Class] instance.
     * @param variousClass the [LegacyVariousClass].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.NonNull]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun lazyClass(variousClass: LegacyVariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobalLegacy(variousClass, initialize, loader)

    /**
     * Creates a lazily loaded non-null [Class] instance.
     * @param variousClass the [VariousClass].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.NonNull]
     */
    fun lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassGlobal(variousClass, initialize, loader)

    /**
     * Creates a lazily loaded nullable [Class] instance.
     * @param name the fully qualified class name.
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.Nullable]
     */
    fun lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal(name, initialize, loader)

    /**
     * Creates a lazily loaded nullable [Class] instance of type [T].
     * @param name the fully qualified class name.
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.Nullable]<[T]>
     */
    @JvmName("lazyClassOrNull_Generics")
    inline fun <reified T : Any> lazyClassOrNull(name: String, initialize: Boolean = false, noinline loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal<T>(name, initialize, loader)

    /**
     * Creates a lazily loaded nullable [Class] instance.
     * @param variousClass the [LegacyVariousClass].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.Nullable]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun lazyClassOrNull(variousClass: LegacyVariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobalLegacy(variousClass, initialize, loader)

    /**
     * Creates a lazily loaded nullable [Class] instance.
     * @param variousClass the [VariousClass].
     * @param initialize whether to initialize the static block of the [Class], false by default.
     * @param loader the [ClassLoader] to load the class. The default is [appClassLoader].
     * @return [LazyClass.Nullable]
     */
    fun lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = appLoaderInit) =
        lazyClassOrNullGlobal(variousClass, initialize, loader)

    /**
     * Checks whether a string class name exists.
     * @param loader the [ClassLoader] containing the [Class]. The default is [appClassLoader].
     * @return [Boolean] whether the class exists.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.hasClass(loader: ClassLoader? = appClassLoader) = hasClassLegacy(loader)

    /**
     * Finds and loads [HookClass].
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [toClass].
     * @return [HookClass]
     */
    @LegacyHookApi
    @Deprecated(message = "This function is no longer recommended", ReplaceWith("name.toClass(loader)"))
    fun findClass(name: String, loader: ClassLoader? = appClassLoader) = name.toHookClass(loader)

    /**
     * Finds and loads [HookClass].
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [LegacyVariousClass].
     * @return [HookClass]
     */
    @LegacyHookApi
    @Deprecated(message = "This function is no longer recommended", ReplaceWith("VariousClass(*name)"))
    fun findClass(vararg name: String, loader: ClassLoader? = appClassLoader) = LegacyVariousClass(*name).toHookClass(loader)

    /**
     * Hooks methods and constructors.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [toClass].
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.Result]
     */
    @LegacyHookApi
    @Deprecated(message = "This function is no longer recommended", ReplaceWith("this.toClass().hook(initiate = initiate)"))
    inline fun String.hook(initiate: YukiMemberHookCreator.() -> Unit) = toHookClass().hook(initiate = initiate)

    /**
     * Hooks methods and constructors.
     *
     * - Automatically selects the [ClassLoader] matching the current [Class], preferring [appClassLoader].
     *
     * - Enable [isForceUseAbsolute] if the current [Class] is not in [appClassLoader] and automatic matching cannot find it.
     * @param isForceUseAbsolute whether to force use of the absolute instance, false by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.Result]
     */
    @LegacyHookApi
    inline fun Class<*>.hook(isForceUseAbsolute: Boolean = false, initiate: YukiMemberHookCreator.() -> Unit) = when {
        isForceUseAbsolute -> toHookClass()
        name.hasClass() -> name.toClass().toHookClass()
        else -> toHookClass()
    }.hook(initiate)

    /**
     * Hooks methods and constructors.
     *
     * - Uses the current [appClassLoader] to load the target [Class].
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.Result]
     */
    @LegacyHookApi
    inline fun LegacyVariousClass.hook(initiate: YukiMemberHookCreator.() -> Unit) = toHookClass().hook(initiate)

    /**
     * Hooks methods and constructors.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.Result]
     */
    @LegacyHookApi
    inline fun HookClass.hook(initiate: YukiMemberHookCreator.() -> Unit) =
        YukiMemberHookCreator(packageParam = this@PackageParam, hookClass = this).apply(initiate).hook()

    /**
     * Hooks methods and constructors directly.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    fun Member.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = listOf(this).baseHook(priority)

    /**
     * Hooks methods and constructors directly.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    inline fun Member.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = listOf(this).baseHook(priority, isLazyMode = true).apply(initiate).build()

    /**
     * Hooks methods and constructors directly through [BaseFinder.BaseResult].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun BaseFinder.BaseResult.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = baseHook(isMultiple = false, priority)

    /**
     * Hooks methods and constructors directly through [BaseFinder.BaseResult].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun BaseFinder.BaseResult.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = baseHook(isMultiple = false, priority, isLazyMode = true).apply(initiate).build()

    /**
     * Hooks methods and constructors directly through [MemberResolver].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    fun MemberResolver<*, *>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = baseHook(priority)

    /**
     * Hooks methods and constructors directly through [MemberResolver].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    @JvmName("hook_MemberResolver")
    inline fun MemberResolver<*, *>.hook(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = hook(priority).apply(initiate).build()

    /**
     * Hooks methods and constructors directly through a [List] of [MemberResolver] instances.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    fun List<MemberResolver<*, *>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = baseHook(priority)

    /**
     * Hooks methods and constructors directly through a [List] of [MemberResolver] instances.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    @JvmName("hookAll_MemberResolver")
    inline fun List<MemberResolver<*, *>>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = hookAll(priority).apply(initiate).build()

    /**
     * Hooks methods and constructors directly in a batch.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    fun Array<Member>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = toList().baseHook(priority)

    /**
     * Hooks methods and constructors directly in a batch.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    inline fun Array<Member>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = toList().baseHook(priority, isLazyMode = true).apply(initiate).build()

    /**
     * Hooks methods and constructors directly in a batch.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    @JvmName("hookAll_Member")
    fun List<Member>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = baseHook(priority)

    /**
     * Hooks methods and constructors directly in a batch.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    inline fun List<Member>.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = baseHook(priority, isLazyMode = true).apply(initiate).build()

    /**
     * Hooks methods and constructors directly in a batch through [BaseFinder.BaseResult].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun BaseFinder.BaseResult.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) = baseHook(isMultiple = true, priority)

    /**
     * Hooks methods and constructors directly in a batch through [BaseFinder.BaseResult].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority, [YukiHookPriority.DEFAULT] by default.
     * @param initiate the Hook block.
     * @return [YukiMemberHookCreator.MemberHookCreator.Result]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun BaseFinder.BaseResult.hookAll(
        priority: YukiHookPriority = YukiHookPriority.DEFAULT,
        initiate: YukiMemberHookCreator.MemberHookCreator.() -> Unit
    ) = baseHook(isMultiple = true, priority, isLazyMode = true).apply(initiate).build()

    /**
     * Hooks methods and constructors directly through [BaseFinder.BaseResult].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param isMultiple whether this is a multiple lookup.
     * @param priority the Hook priority.
     * @param isLazyMode whether lazy mode is enabled, false by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    private fun BaseFinder.BaseResult.baseHook(isMultiple: Boolean, priority: YukiHookPriority, isLazyMode: Boolean = false) =
        when (this) {
            is DexClassFinder.Result ->
                error("Use of searchClass { ... }.hook { ... } is an error, please use like searchClass { ... }.get()?.hook { ... }")
            is ConstructorFinder.Result -> {
                val members = if (isMultiple) giveAll()
                else mutableListOf<Member>().also { give()?.also { e -> it.add(e) } }
                YukiMemberHookCreator.createMemberHook(packageParam = this@PackageParam, members, priority, isLazyMode)
            }
            is MethodFinder.Result -> {
                val members = if (isMultiple) giveAll()
                else mutableListOf<Member>().also { give()?.also { e -> it.add(e) } }
                YukiMemberHookCreator.createMemberHook(packageParam = this@PackageParam, members, priority, isLazyMode)
            }
            else -> error("This type [$this] not support to hook, supported are Constructors and Methods")
        }

    /**
     * Hooks methods and constructors directly through [MemberResolver].
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority.
     * @param isLazyMode whether lazy mode is enabled, false by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    private fun MemberResolver<*, *>.baseHook(priority: YukiHookPriority, isLazyMode: Boolean = false) = when (this) {
        is ConstructorResolver,
        is MethodResolver -> YukiMemberHookCreator.createMemberHook(packageParam = this@PackageParam, listOf(self), priority, isLazyMode)
        else -> error("This type [$this] not support to hook, supported are Constructors and Methods")
    }

    /**
     * Hooks methods and constructors directly through a [List] of [MemberResolver] instances.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. KavaRef will take it over completely in 2.0.0.
     * @param priority the Hook priority.
     * @param isLazyMode whether lazy mode is enabled, false by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    private fun List<MemberResolver<*, *>>.baseHook(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiMemberHookCreator.createMemberHook(packageParam = this@PackageParam, onEach {
            if (it !is ConstructorResolver && it !is MethodResolver)
                error("This type [$it] not support to hook, supported are Constructors and Methods")
        }.map { it.self }, priority, isLazyMode)

    /**
     * Hooks methods and constructors directly.
     *
     * - This feature is still experimental and remains here temporarily in 1.x.x. It will be fully merged into the new API in 2.0.0.
     * @param priority the Hook priority.
     * @param isLazyMode whether lazy mode is enabled, false by default.
     * @return [YukiMemberHookCreator.MemberHookCreator]
     */
    @JvmName("baseHook_Member")
    private fun List<Member>.baseHook(priority: YukiHookPriority, isLazyMode: Boolean = false) =
        YukiMemberHookCreator.createMemberHook(packageParam = this@PackageParam, onEach {
            if (it !is Constructor<*> && it !is Method) error("This type [$it] not support to hook, supported are Constructors and Methods")
        }, priority, isLazyMode)

    /**
     * Hooks the Resources of the APP.
     *
     * - This feature is no longer enabled by default. Set [InjectYukiHookWithXposed.isUsingResourcesHook] manually to enable it.
     * @param initiate the Hook block.
     */
    @LegacyResourcesHook
    inline fun HookResources.hook(initiate: YukiResourcesHookCreator.() -> Unit) =
        YukiResourcesHookCreator(packageParam = this@PackageParam, hookResources = this).apply(initiate).hook()

    /**
     * Converts [LegacyVariousClass] to [HookClass].
     * @param loader the current [ClassLoader]. The default is [appClassLoader].
     * @return [HookClass]
     */
    @LegacyHookApi
    private fun LegacyVariousClass.toHookClass(loader: ClassLoader? = appClassLoader) =
        runCatching { get(loader).toHookClass() }.getOrElse { HookClass(name = "VariousClass", throwable = Throwable(it.message)) }

    /**
     * Converts [Class] to [HookClass].
     * @return [HookClass]
     */
    @LegacyHookApi
    private fun Class<*>.toHookClass() = HookClass(instance = this, name)

    /**
     * Converts a string class name to [HookClass].
     * @param loader the current [ClassLoader]. The default is [appClassLoader].
     * @return [HookClass]
     */
    @LegacyHookApi
    private fun String.toHookClass(loader: ClassLoader? = appClassLoader) = HookClass(toClassOrNull(loader), name = this)

    /**
     * Lifecycle instance handler for the current Hook APP.
     *
     * - Use [onAppLifecycle] to obtain [AppLifecycle].
     * @param isOnFailureThrowToApp whether to throw exceptions to the host when they occur.
     */
    inner class AppLifecycle internal constructor(private val isOnFailureThrowToApp: Boolean) {

        /**
         * Whether the current operation is in the [HookEntryType.PACKAGE] call scope.
         *
         * To avoid configuring callback events multiple times, they take effect only after Hooking starts.
         * @return [Boolean]
         */
        private val isCurrentScope get() = wrapper?.type == HookEntryType.PACKAGE

        /**
         * Listens for the current Hook APP loading [Application.attachBaseContext].
         * @param result callback with the base [Context] and whether super has been called.
         */
        fun attachBaseContext(result: (baseContext: Context, hasCalledSuper: Boolean) -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).attachBaseContextCallback = result
        }

        /**
         * Listens for the current Hook APP loading [Application.onCreate].
         * @param initiate the callback block.
         */
        fun onCreate(initiate: Application.() -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).onCreateCallback = initiate
        }

        /**
         * Listens for the current Hook APP loading [Application.onTerminate].
         * @param initiate the callback block.
         */
        fun onTerminate(initiate: Application.() -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).onTerminateCallback = initiate
        }

        /**
         * Listens for the current Hook APP loading [Application.onLowMemory].
         * @param initiate the callback block.
         */
        fun onLowMemory(initiate: Application.() -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).onLowMemoryCallback = initiate
        }

        /**
         * Listens for the current Hook APP loading [Application.onTrimMemory].
         * @param result callback with the current [Application] instance and [Int] level.
         */
        fun onTrimMemory(result: (self: Application, level: Int) -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).onTrimMemoryCallback = result
        }

        /**
         * Listens for the current Hook APP loading [Application.onConfigurationChanged].
         * @param result callback with the current [Application] instance and [Configuration] instance.
         */
        fun onConfigurationChanged(result: (self: Application, config: Configuration) -> Unit) {
            if (isCurrentScope) AppParasitics.AppLifecycleActor.get(this@PackageParam).onConfigurationChangedCallback = result
        }

        /**
         * Registers a system broadcast listener.
         * @param action the system broadcast actions.
         * @param result callback with the current [Context] and [Intent].
         */
        fun registerReceiver(vararg action: String, result: (context: Context, intent: Intent) -> Unit) {
            if (isCurrentScope && action.isNotEmpty())
                AppParasitics.AppLifecycleActor.get(this@PackageParam).onReceiverActionsCallbacks[action.value()] = action to result
        }

        /**
         * Registers a system broadcast listener.
         * @param filter the broadcast intent filter.
         * @param result callback with the current [Context] and [Intent].
         */
        fun registerReceiver(filter: IntentFilter, result: (context: Context, intent: Intent) -> Unit) {
            if (isCurrentScope)
                AppParasitics.AppLifecycleActor.get(this@PackageParam).onReceiverFiltersCallbacks[filter.toString()] = filter to result
        }

        /** Configures lifecycle listener callbacks. */
        internal fun build() {
            if (AppParasitics.AppLifecycleActor.isOnFailureThrowToApp == null)
                AppParasitics.AppLifecycleActor.isOnFailureThrowToApp = isOnFailureThrowToApp
        }
    }

    override fun toString() = "PackageParam(${super.toString()}) by $wrapper"
}