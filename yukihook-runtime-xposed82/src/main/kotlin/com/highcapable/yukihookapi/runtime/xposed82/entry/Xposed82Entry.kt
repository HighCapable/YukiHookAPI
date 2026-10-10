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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.xposed82.entry

import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiHookXposed82Event
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Xposed API 82 entry base used by KSP.
 */
abstract class Xposed82Entry : IXposedHookZygoteInit, IXposedHookLoadPackage {

    private companion object {
        const val FRAMEWORK_PACKAGE_NAME = "android"
        const val FRAMEWORK_SCOPE_NAME = "system"
    }

    /** Module application ID emitted by KSP. */
    protected abstract val modulePackageName: String

    /** Creates the module's entry after framework attachment. */
    protected abstract fun createHookEntry(): YukiHookXposedModule

    private var initialized = false

    final override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam) {
        YukiHookBridge.attach()

        initialized = YukiHookModuleCaller.callOnHook(::createHookEntry, packageName = modulePackageName, sourceDir = startupParam.modulePath)
        if (initialized) YukiHookModuleCaller.callOnZygote(
            packageName = FRAMEWORK_PACKAGE_NAME,
            processName = FRAMEWORK_PACKAGE_NAME,
            mainProcessName = FRAMEWORK_PACKAGE_NAME,
            packageNameForMatching = FRAMEWORK_PACKAGE_NAME,
            loader = XposedBridge.BOOTCLASSLOADER
        )
        YukiHookXposed82Event.initZygoteCallback?.invoke(startupParam)
    }

    final override fun handleLoadPackage(param: XC_LoadPackage.LoadPackageParam) {
        if (!initialized) return

        val processName = param.processName
            // Removes transport padding and trailing replacement characters from a process name.
            .substringBefore('\u0000')
            .trimEnd('\uFFFD')

        when {
            // Xposed 82 reports system_server as the android package without `ApplicationInfo`.
            param.packageName == FRAMEWORK_PACKAGE_NAME && param.appInfo == null -> YukiHookModuleCaller.callOnSystemServerStarting(
                packageName = param.packageName,
                processName = processName,
                mainProcessName = param.packageName.trim(),
                packageNameForMatching = param.packageName,
                loader = param.classLoader
            )
            // The framework package outside system_server is not an app entry, the system server is reported above.
            param.packageName != FRAMEWORK_PACKAGE_NAME && param.packageName != FRAMEWORK_SCOPE_NAME -> YukiHookModuleCaller.callOnPackageLoaded(
                packageName = param.packageName,
                processName = processName,
                mainProcessName = param.appInfo?.processName ?: param.packageName.trim(),
                isFirstApplication = param.isFirstApplication,
                packageNameForMatching = param.packageName,
                loader = param.classLoader,
                appInfo = param.appInfo
            )
        }
        YukiHookXposed82Event.handleLoadPackageCallback?.invoke(param)
    }
}