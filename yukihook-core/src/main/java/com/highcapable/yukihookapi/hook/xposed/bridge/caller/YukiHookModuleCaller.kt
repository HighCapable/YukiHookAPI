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
 * This file is created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.caller

import android.app.Application
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.annotation.RestrictTo
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.runtime.bridge.HookRegistrationBridge
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge

/**
 * Routes calls to the Xposed module lifecycle implementation.
 *
 * - Note: This class will be used internally by the KSP-generated Xposed module entry.
 *   Do not call this class manually.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
object YukiHookModuleCaller {

    /**
     * Checks that a framework event listener is being configured inside the registration block.
     * @throws IllegalStateException if no registration block is running on the current thread.
     */
    fun checkFrameworkEventsRegistration() =
        XposedModuleRuntime.checkFrameworkEventsRegistration()

    /**
     * Runs the single framework event registration block for this module process.
     * @param entry the entry currently running [YukiHookXposedModule.onInit].
     * @param block the runtime's event configuration block.
     * @throws IllegalStateException if called outside this entry's initialization or more than once.
     */
    fun callRegisterFrameworkEvents(entry: YukiHookXposedModule, block: () -> Unit) =
        XposedModuleRuntime.registerFrameworkEvents(entry, block)

    /**
     * Calls [YukiHookXposedModule.onInit] with framework event registration enabled for this entry.
     * @param entry the user entry.
     */
    fun callOnInit(entry: YukiHookXposedModule) =
        XposedModuleRuntime.createHookEntry(entry) { entry.onInit() }

    /**
     * Calls [YukiHookXposedModule.onHook] with the current Xposed module.
     * @param createHookEntry creates the user entry after the host environment is initialized.
     * @param appInfo the current Xposed module application info.
     * @param packageName the package name of the current process.
     * @param sourceDir the current Xposed module source directory
     * @return [Boolean] whether the initialization was successful.
     */
    fun callOnHook(
        createHookEntry: () -> YukiHookXposedModule,
        appInfo: ApplicationInfo? = null,
        packageName: String = appInfo?.packageName ?: error("packageName is required."),
        sourceDir: String = appInfo?.sourceDir ?: error("sourceDir is required.")
    ) = runCatching {
        XposedModuleRuntime.onStartLoadModule(packageName, sourceDir, appInfo)

        val hookEntry = createHookEntry()
        callOnInit(hookEntry)

        XposedModuleRuntime.checkHookLoading()
        hookEntry.onHook()
    }.onFailure { YLog.innerE("YukiHook failed to initialize the module", it) }.isSuccess

    /**
     * Prepares the module's hook component before creating the user entry.
     * @param session the runtime-owned callback gate.
     * @param registrations the runtime's native hook operations.
     * @return [HookRegistrationBridge] the operations to install in the runtime's hook dispatch.
     */
    fun callAttachModuleSession(session: ModuleSessionBridge, registrations: HookRegistrationBridge) =
        XposedModuleRuntime.attachModuleSession(session, registrations)

    /**
     * Prepares the current module instance's state and validates it with the runtime before disposal.
     * @param extras neutral request data.
     * @param saveState publishes the state, or returns false to refuse the request.
     * @return [Boolean] whether the current module instance accepted retirement.
     */
    fun callPrepareReloadModule(extras: Bundle?, saveState: (Any) -> Boolean) =
        XposedModuleRuntime.onPrepareReloadModule(extras, saveState)

    /**
     * Runs a framework callback only while this module instance is accepting work.
     * @param block the callback to run.
     */
    fun callDispatchFrameworkEvent(block: () -> Unit) =
        XposedModuleRuntime.dispatchFrameworkEvent(block)

    /**
     * Initializes and replays a new module instance without synthesizing completed host events.
     * @param createHookEntry the new entry factory.
     * @param appInfo the new Xposed module application info.
     * @param state neutral state from the old module instance.
     * @param onFailure receives the original failure for reporting after old hooks are cleaned up.
     * @return [Boolean] whether this module instance was restored successfully.
     */
    fun callRestoreModule(
        createHookEntry: () -> YukiHookXposedModule,
        appInfo: ApplicationInfo,
        state: Any?,
        onFailure: (Throwable) -> Unit
    ) = runCatching {
        XposedModuleRuntime.onStartLoadModule(appInfo.packageName, appInfo.sourceDir, appInfo)

        val saved = XposedModuleRuntime.onBeginRestoreModule(appInfo, state)
        val entry = createHookEntry()

        callOnInit(entry)
        XposedModuleRuntime.onRestoreUserState(saved)

        XposedModuleRuntime.checkHookLoading()
        entry.onHook()

        XposedModuleRuntime.onRestoreComponents(appInfo, saved)
    }.onFailure {
        onFailure(it)
        YLog.innerE("YukiHook failed to restore the module", it)
    }.isSuccess

    /**
     * Completes restored callbacks and reports any collected registration failures to the framework.
     * @param extras additional data supplied by the runtime.
     */
    fun callFinishRestoreModule(extras: Bundle?) =
        XposedModuleRuntime.onFinishRestoreModule(extras)

    /** Ends failure collection after an unrecoverable module restoration error. */
    fun callAbortRestoreModule() =
        XposedModuleRuntime.onAbortRestoreModule()

    /**
     * Records a native event or cleanup failure without stopping other reload work.
     * @param failure the failure to record.
     */
    fun callRecordRestoreFailure(failure: Throwable) =
        XposedModuleRuntime.recordRestoreFailure(failure)

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
    ) = XposedModuleRuntime.onPackageLoaded(
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
    ) = XposedModuleRuntime.onPackageLoaded(
        type = HookEntryType.SYSTEM_SERVER,
        packageName = packageName,
        processName = processName,
        mainProcessName = mainProcessName,
        isFirstApplication = true,
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
    ) = XposedModuleRuntime.onPackageLoaded(
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