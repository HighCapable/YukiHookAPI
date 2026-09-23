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
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle.XposedModuleLifecycle
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.ModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.runtime.YukiHookBridge

/**
 * Core Xposed module runtime for [YukiHook].
 */
internal object XposedModuleRuntime : XposedModuleLifecycle {

    /** Whether the Xposed module has been loaded. */
    private var isModuleLoaded = false

    /** Whether Xposed module loading has completed. */
    private var isModuleLoadFinished = false

    /** Whether the current Hook process is running in Zygote. */
    private var isInitializingZygote = false

    /** Hook entry IDs already loaded into [PackageParam]. */
    private val loadedEntries = mutableSetOf<String>()

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

    /** The current Xposed module resources. */
    private var currentModuleResources: ModuleResources? = null

    /**
     * Gets the current Xposed module resources synchronized with the host configuration.
     * @return [ModuleResources] or null.
     */
    val moduleResources get() = currentModuleResources
        ?.syncConfiguration(AppParasitism.currentApplication?.resources ?: Resources.getSystem())

    /**
     * Gets whether a hook callback was registered before module loading completed.
     * @return [Boolean]
     */
    val hasEarlyHookCallback get() = !isModuleLoadFinished && packageParamCallback != null

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
     * Automatically ignores log-collection injection instances that may appear on MIUI.
     * @param packageName the current package name.
     * @return [Boolean] whether such an instance exists.
     */
    private fun isMiuiCatcherPatch(packageName: String?) =
        (packageName == "com.miui.contentcatcher" || packageName == "com.miui.catcherpatch") &&
            javaClass.classLoader?.hasClass("android.miui.R") == true

    /** Reloads the current Xposed module's [Resources]. */
    fun reloadModuleResources() {
        currentModuleResources = ModuleResources.from(moduleSourceDir)
    }

    override fun onStartLoadModule(packageName: String, sourceDir: String, appInfo: ApplicationInfo?) {
        isModuleLoaded = true

        modulePackageName = packageName
        moduleSourceDir = sourceDir
        moduleAppInfo = appInfo
        reloadModuleResources()
    }

    override fun onFinishLoadModule() {
        isModuleLoadFinished = true
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
        val wrapper = PackageParamWrapper(
            type = type,
            packageName = packageName,
            processName = processName,
            mainProcessName = mainProcessName,
            isFirstApplication = isFirstApplication,
            appClassLoader = appClassLoader,
            appInfo = appInfo,
            packageNameForMatching = packageNameForMatching
        )
        if (type != HookEntryType.ZYGOTE && synchronized(loadedEntries) { !loadedEntries.add(wrapper.id) }) return
        isInitializingZygote = type == HookEntryType.ZYGOTE

        runCatching {
            packageParamCallback?.invoke(PackageParam(wrapper).apply { YukiHook.sayHello() })

            if (wrapper.type != HookEntryType.ZYGOTE && wrapper.packageName == modulePackageName)
                AppParasitism.hookLegacyModuleRelated(wrapper.appClassLoader)
            if (wrapper.isFirstApplication) AppParasitism.prepareHostApplication(wrapper.packageName, wrapper.type)
        }.onFailure { YLog.innerE("An exception occurred in the hooking process of YukiHook", it) }
    }
}