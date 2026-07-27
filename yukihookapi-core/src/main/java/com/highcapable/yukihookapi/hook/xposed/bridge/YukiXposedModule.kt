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
 * This file is created by fankes on 2022/4/3.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge

import android.content.pm.ApplicationInfo
import android.content.res.Resources
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.bridge.proxy.IYukiXposedModuleLifecycle
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import dalvik.system.PathClassLoader

/**
 * Core Xposed module implementation.
 */
internal object YukiXposedModule : IYukiXposedModuleLifecycle {

    /** Whether the Xposed module has been loaded. */
    private var isModuleLoaded = false

    /** Whether Xposed module loading has completed. */
    private var isModuleLoadFinished = false

    /** Whether the current Hook process is running in Zygote. */
    private var isInitializingZygote = false

    /** The current [PackageParam] instances. */
    private val packageParams = mutableMapOf<String, PackageParam>()

    /** App package names already loaded into [PackageParam]. */
    private val loadedPackageNames = mutableSetOf<String>()

    /** The current [PackageParamWrapper] instances. */
    private val packageParamWrappers = mutableMapOf<String, PackageParamWrapper>()

    /** The current [PackageParam] block callback. */
    internal var packageParamCallback: (PackageParam.() -> Unit)? = null

    /** Whether the current Hook Framework supports Resources Hook. */
    internal var isSupportResourcesHook = false

    /** The predefined Xposed module package name. */
    internal var modulePackageName = ""

    /** The APK path of the current Xposed module. */
    internal var moduleAppFilePath = ""

    /** The current Xposed module's [Resources]. */
    internal var moduleAppResources: YukiModuleResources? = null

    /**
     * Gets the current Xposed module's dynamic [Resources].
     * @return [YukiModuleResources] or null.
     */
    internal val dynamicModuleAppResources get() = runCatching { YukiModuleResources.wrapper(moduleAppFilePath) }.getOrNull()

    /**
     * Gets whether the module has loaded its Xposed callback.
     * @return [Boolean]
     */
    internal val isXposedCallbackSetUp get() = isModuleLoadFinished.not() && packageParamCallback != null

    /**
     * Gets the identifier of the Hook process currently running in the host app.
     * @return [String]
     */
    internal val hostProcessName get() = if (isInitializingZygote) "android-zygote" else AppParasitics.currentPackageName

    /**
     * Gets whether the current environment is a (Xposed) host environment.
     * @return [Boolean]
     */
    internal val isXposedEnvironment get() = HookApiCategoryHelper.hasAvailableHookApi && isModuleLoaded

    /**
     * Automatically ignores log-collection injection instances that may appear on MIUI.
     * @param packageName the current package name.
     * @return [Boolean] whether such an instance exists.
     */
    private fun isMiuiCatcherPatch(packageName: String?) =
        (packageName == "com.miui.contentcatcher" || packageName == "com.miui.catcherpatch") &&
            javaClass.classLoader?.hasClass("android.miui.R") == true

    /**
     * Checks whether the current package has already been loaded for the specified [HookEntryType].
     * @param packageName the package name.
     * @param type the current Hook type.
     * @return [Boolean] whether the package has already been loaded.
     */
    private fun isPackageLoaded(packageName: String?, type: HookEntryType): Boolean {
        if (packageName == null) return false
        if (loadedPackageNames.contains("$packageName:$type")) return true
        loadedPackageNames.add("$packageName:$type")
        return false
    }

    /**
     * Instantiates the current [PackageParamWrapper] as [PackageParam].
     *
     * Creates a new instance automatically when none exists.
     * @return [PackageParam]
     */
    private fun PackageParamWrapper.instantiate() = packageParams[wrapperNameId] ?: PackageParam().apply { packageParams[wrapperNameId] = this }

    /**
     * Creates or updates a [PackageParamWrapper].
     *
     * Avoids initially loading through [ClassLoader.getSystemClassLoader] when [appClassLoader] is null
     * and [type] is not [HookEntryType.ZYGOTE].
     * @param type the current Hook type.
     * @param packageName the package name.
     * @param processName the current process name.
     * @param appClassLoader app [ClassLoader].
     * @param appInfo app [ApplicationInfo].
     * @param appResources app [YukiResources].
     * @return [PackageParamWrapper] or null.
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
                    packageName = packageName ?: AppParasitics.SYSTEM_FRAMEWORK_NAME,
                    processName = processName ?: AppParasitics.SYSTEM_FRAMEWORK_NAME,
                    appClassLoader = appClassLoader ?: ClassLoader.getSystemClassLoader(),
                    appInfo = appInfo,
                    appResources = appResources
                ).also { packageParamWrappers[packageName ?: AppParasitics.SYSTEM_FRAMEWORK_NAME] = it }
            else null
        else packageParamWrappers[packageName]?.also { wrapper ->
            wrapper.type = type
            packageName?.takeIf { it.isNotBlank() }?.also { wrapper.packageName = it }
            processName?.takeIf { it.isNotBlank() }?.also { wrapper.processName = it }
            appClassLoader?.takeIf { type == HookEntryType.ZYGOTE || it is PathClassLoader }?.also { wrapper.appClassLoader = it }
            appInfo?.also { wrapper.appInfo = it }
            appResources?.also { wrapper.appResources = it }
        }
    }

    /** Refreshes the current Xposed module's [Resources]. */
    internal fun refreshModuleAppResources() {
        dynamicModuleAppResources?.let { moduleAppResources = it }
    }

    override fun onStartLoadModule(packageName: String, appFilePath: String) {
        isModuleLoaded = true
        modulePackageName = packageName
        moduleAppFilePath = appFilePath
        refreshModuleAppResources()
    }

    override fun onFinishLoadModule() {
        isModuleLoadFinished = true
    }

    override fun onPackageLoaded(
        type: HookEntryType,
        packageName: String?,
        processName: String?,
        appClassLoader: ClassLoader?,
        appInfo: ApplicationInfo?,
        appResources: YukiResources?
    ) {
        if (isMiuiCatcherPatch(packageName).not()) when (type) {
            HookEntryType.ZYGOTE ->
                assignWrapper(HookEntryType.ZYGOTE, AppParasitics.SYSTEM_FRAMEWORK_NAME, AppParasitics.SYSTEM_FRAMEWORK_NAME, appClassLoader)
            HookEntryType.PACKAGE ->
                if (isPackageLoaded(packageName, HookEntryType.PACKAGE).not())
                    assignWrapper(HookEntryType.PACKAGE, packageName, processName, appClassLoader, appInfo)
                else null
            HookEntryType.RESOURCES ->
                // [packageName] may resolve to a package other than the actual host app, so ignore it when it differs from
                // [AppParasitics.currentPackageName].
                if (isPackageLoaded(packageName, HookEntryType.RESOURCES).not() && packageName == AppParasitics.currentPackageName)
                    assignWrapper(HookEntryType.RESOURCES, packageName, appResources = appResources)
                else null
        }?.also {
            runCatching {
                if (it.isCorrectProcess) packageParamCallback?.invoke(it.instantiate().assign(it).apply { YukiHookAPI.printSplashInfo() })
                if (it.type != HookEntryType.ZYGOTE && it.packageName == modulePackageName)
                    AppParasitics.hookModuleAppRelated(it.appClassLoader, it.type)
                if (it.type == HookEntryType.PACKAGE) AppParasitics.registerToAppLifecycle(it.packageName)
                if (it.type == HookEntryType.RESOURCES) isSupportResourcesHook = true
            }.onFailure { YLog.innerE("An exception occurred in the Hooking Process of YukiHookAPI", it) }
        }
    }
}