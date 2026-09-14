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
 * This file is created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.caller

import android.app.Application
import android.content.pm.ApplicationInfo
import androidx.annotation.RestrictTo
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

/**
 * Routes calls to the Xposed module lifecycle implementation.
 *
 * - Note: This class will be used internally by the KSP-generated Xposed module entry.
 *   Do not call this class manually.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object YukiXposedModuleCaller {

    private val initializingEntry = ThreadLocal<IYukiHookXposedInit>()
    private val registeringFrameworkEvents = ThreadLocal<Boolean>()
    private var isFrameworkEventsRegistered = false

    /**
     * Checks that a framework event listener is being configured inside the registration block.
     * @throws IllegalStateException if no registration block is running on the current thread.
     */
    fun checkFrameworkEventsRegistration() = check(registeringFrameworkEvents.get() == true) {
        "Framework event listeners must be configured inside `registerFrameworkEvents`."
    }

    /**
     * Calls [IYukiHookXposedInit.onInit] with framework event registration enabled for this entry.
     * Used by both the host runtime and the generated module application entry.
     * @param entry the user entry.
     */
    fun callOnInit(entry: IYukiHookXposedInit) {
        initializingEntry.set(entry)

        try {
            entry.onInit()
        } finally {
            initializingEntry.remove()
        }
    }

    /**
     * Calls [IYukiHookXposedInit.onHook] on the user entry only after the runtime has attached its hook interface.
     * @param entry the user entry.
     * @param packageName the package name of the current process.
     * @param appFilePath the path of the current process's APK file.
     * @return [Boolean] whether the initialization was successful.
     */
    fun callOnHook(entry: IYukiHookXposedInit, packageName: String, appFilePath: String) = runCatching {
        YukiXposedModule.onStartLoadModule(packageName, appFilePath)
        callOnInit(entry)

        check(!YukiXposedModule.isXposedCallbackSetUp) {
            "Do not load hookers in `onInit`."
        }
        entry.onHook()

        YukiXposedModule.onFinishLoadModule()
    }.onFailure { YLog.innerE("YukiHookAPI failed to initialize the module.", it) }.isSuccess

    /**
     * Runs the single framework event registration block for this module process.
     * @param entry the entry currently running [IYukiHookXposedInit.onInit].
     * @param block the runtime's event configuration block.
     * @throws IllegalStateException if called outside this entry's initialization or more than once.
     */
    fun callRegisterFrameworkEvents(entry: IYukiHookXposedInit, block: () -> Unit) {
        check(initializingEntry.get() === entry) {
            "Framework events must be registered in `onInit`, not `onHook` or later callbacks."
        }
        synchronized(this) {
            check(!isFrameworkEventsRegistered) { "Framework events can only be registered once per module process." }
            isFrameworkEventsRegistered = true
        }
        registeringFrameworkEvents.set(true)

        try {
            block()
        } finally {
            registeringFrameworkEvents.remove()
        }
    }

    /**
     * Dispatches genuine legacy zygote initialization.
     * @param packageName the package identity supplied by the runtime.
     * @param processName the process identity supplied by the runtime.
     * @param mainProcessName the main process identity supplied by the runtime.
     * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
     * @param loader the class loader of the current process.
     */
    fun callOnZygote(
        packageName: String,
        processName: String,
        mainProcessName: String,
        packageNameForMatching: String?,
        loader: ClassLoader
    ) = YukiXposedModule.onPackageLoaded(
        type = HookEntryType.ZYGOTE,
        packageName = packageName,
        processName = processName,
        mainProcessName = mainProcessName,
        isFirstApplication = false,
        packageNameForMatching = packageNameForMatching,
        appClassLoader = loader
    )

    /**
     * Dispatches system server startup independently of ordinary package loading.
     * @param packageName the system package identity supplied by the runtime.
     * @param processName the system process identity supplied by the runtime.
     * @param mainProcessName the main process identity supplied by the runtime.
     * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
     * @param loader the class loader of the system server.
     */
    fun callOnSystemServerStarting(
        packageName: String,
        processName: String,
        mainProcessName: String,
        packageNameForMatching: String?,
        loader: ClassLoader
    ) = YukiXposedModule.onPackageLoaded(
        type = HookEntryType.SYSTEM_SERVER,
        packageName = packageName,
        processName = processName,
        mainProcessName = mainProcessName,
        isFirstApplication = false,
        packageNameForMatching = packageNameForMatching,
        appClassLoader = loader
    )

    /**
     * Dispatches a package whose final class loader is ready.
     * @param packageName the name of the package.
     * @param processName the name of the process.
     * @param mainProcessName the main process identity supplied by the runtime.
     * @param isFirstApplication whether this package owns the first [Application] in the current process.
     * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
     * @param loader the class loader of the package.
     * @param appInfo the application info of the package.
     */
    fun callOnPackageLoaded(
        packageName: String,
        processName: String,
        mainProcessName: String,
        isFirstApplication: Boolean,
        packageNameForMatching: String?,
        loader: ClassLoader,
        appInfo: ApplicationInfo?
    ) = YukiXposedModule.onPackageLoaded(
        type = HookEntryType.PACKAGE,
        packageName = packageName,
        processName = processName,
        mainProcessName = mainProcessName,
        isFirstApplication = isFirstApplication,
        packageNameForMatching = packageNameForMatching,
        appClassLoader = loader,
        appInfo = appInfo
    )
}