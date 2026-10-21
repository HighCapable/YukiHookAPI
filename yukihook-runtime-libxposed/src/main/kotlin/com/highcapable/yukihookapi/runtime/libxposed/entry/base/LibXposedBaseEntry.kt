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
 * This file is created by fankes on 2026/9/27.
 */
package com.highcapable.yukihookapi.runtime.libxposed.entry.base

import androidx.annotation.RequiresApi
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiHookLibXposedEvent
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedHookApi
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * Common libxposed API entry base used by KSP. Native lifecycle details stay in this runtime.
 */
abstract class LibXposedBaseEntry : XposedModule() {

    private companion object {
        const val FRAMEWORK_PACKAGE_NAME = "android"
    }

    /** Whether the user's entry initialized successfully. */
    protected var initialized = false

    /** Current process name supplied by the framework. */
    protected var processName = ""

    /** Whether observed package contexts are retained even when entry initialization failed. */
    protected open val retainPackageContexts = false

    /** Creates the user's entry only after the framework interface has been attached. */
    protected abstract fun createHookEntry(): YukiHookXposedModule

    /** Attaches the runtime before loading the user's entry. */
    protected open fun attachRuntime() = LibXposedHookApi(this).attach()

    /** Stops managed work after initial loading failed, without discarding observed package contexts. */
    protected open fun onInitializationFailed() = Unit

    /** Parses the process name from the framework parameter. */
    protected fun parseProcessName(param: XposedModuleInterface.ModuleLoadedParam) {
        processName = param.processName
            // Removes transport padding and trailing replacement characters from a process name.
            .substringBefore('\u0000')
            .trimEnd('\uFFFD')
    }

    final override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        runCatching {
            parseProcessName(param)
            attachRuntime()

            initialized = YukiHookModuleCaller.callOnHook(::createHookEntry, moduleApplicationInfo, processName = processName)
        }.also {
            if (it.isFailure || !initialized) {
                initialized = false
                onInitializationFailed()
            }
        }.getOrThrow()

        // Like the other framework events, a failing listener does not undo the completed initialization.
        YukiHookModuleCaller.callDispatchFrameworkEvent {
            if (initialized) YukiHookLibXposedEvent.moduleLoaded?.invoke(this, param)
        }
    }

    @RequiresApi(29)
    final override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) = YukiHookModuleCaller.callDispatchFrameworkEvent {
        if (initialized) YukiHookLibXposedEvent.packageLoaded?.invoke(this, param)
    }

    final override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        // The framework package outside system_server is not an app entry, the system server has its own callback.
        if ((initialized || retainPackageContexts) && param.packageName != FRAMEWORK_PACKAGE_NAME)
            YukiHookModuleCaller.callOnPackageLoaded(
                packageName = param.packageName,
                processName = processName,
                mainProcessName = param.applicationInfo.processName ?: param.packageName.trim(),
                isFirstApplication = param.isFirstPackage,
                packageNameForMatching = param.packageName,
                loader = param.classLoader,
                appInfo = param.applicationInfo,
                dispatch = initialized
            )

        YukiHookModuleCaller.callDispatchFrameworkEvent {
            if (initialized) YukiHookLibXposedEvent.packageReady?.invoke(this, param)
        }
    }

    final override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        if (initialized || retainPackageContexts) YukiHookModuleCaller.callOnSystemServerStarting(
            packageName = FRAMEWORK_PACKAGE_NAME,
            processName = processName,
            mainProcessName = processName.trim(),
            packageNameForMatching = null,
            loader = param.classLoader,
            dispatch = initialized
        )

        YukiHookModuleCaller.callDispatchFrameworkEvent {
            if (initialized) YukiHookLibXposedEvent.systemServerStarting?.invoke(this, param)
        }
    }
}