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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.libxposed.entry

import androidx.annotation.RequiresApi
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiLibXposedEvent
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedHookApi
import io.github.libxposed.api.XposedModule
import io.github.libxposed.api.XposedModuleInterface

/**
 * LibXposed entry base used by KSP, native lifecycle details stay in this runtime.
 */
abstract class LibXposedEntry : XposedModule() {

    private companion object {
        const val FRAMEWORK_PACKAGE_NAME = "android"
    }

    /** Creates the user's entry only after the framework interface has been attached. */
    protected abstract fun createHookEntry(): YukiHookXposedModule

    private var initialized = false
    private var processName = ""

    final override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
        LibXposedHookApi(this).attach()

        processName = param.processName
            // Removes transport padding and trailing replacement characters from a process name.
            .substringBefore('\u0000')
            .trimEnd('\uFFFD')
        initialized = YukiHookModuleCaller.callOnHook(createHookEntry(), moduleApplicationInfo)
        YukiLibXposedEvent.moduleLoaded?.invoke(this, param)
    }

    @RequiresApi(29)
    final override fun onPackageLoaded(param: XposedModuleInterface.PackageLoadedParam) {
        YukiLibXposedEvent.packageLoaded?.invoke(this, param)
    }

    final override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (initialized) YukiHookModuleCaller.callOnPackageLoaded(
            packageName = param.packageName,
            processName = processName,
            mainProcessName = param.packageName.trim(),
            isFirstApplication = param.isFirstPackage,
            packageNameForMatching = param.packageName,
            loader = param.classLoader,
            appInfo = param.applicationInfo
        )
        YukiLibXposedEvent.packageReady?.invoke(this, param)
    }

    final override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
        if (initialized) YukiHookModuleCaller.callOnSystemServerStarting(
            packageName = FRAMEWORK_PACKAGE_NAME,
            processName = processName,
            mainProcessName = processName.trim(),
            packageNameForMatching = null,
            loader = param.classLoader
        )
        YukiLibXposedEvent.systemServerStarting?.invoke(this, param)
    }
}