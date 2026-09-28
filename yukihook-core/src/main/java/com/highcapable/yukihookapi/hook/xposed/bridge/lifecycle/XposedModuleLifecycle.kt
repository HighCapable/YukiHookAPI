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
 * This file is created by fankes on 2023/1/11.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle

import android.content.pm.ApplicationInfo
import android.os.Bundle
import com.highcapable.yukihookapi.hook.xposed.bridge.session.state.ModuleHandoffState
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Defines the Xposed module loading and reload lifecycle.
 */
internal interface XposedModuleLifecycle {

    /**
     * Called when the Xposed module starts loading.
     * @param packageName the current Xposed module package name.
     * @param sourceDir the current Xposed module source directory.
     * @param appInfo the current Xposed module [ApplicationInfo], or null when unavailable.
     */
    fun onStartLoadModule(packageName: String, sourceDir: String, appInfo: ApplicationInfo?)

    /**
     * Called when an available host starts loading.
     * @param type the current hook entry type.
     * @param packageName the host package name.
     * @param processName the host process name.
     * @param mainProcessName the main process identity supplied by the runtime.
     * @param isFirstApplication whether this package owns the first [android.app.Application] in the current process.
     * @param packageNameForMatching the package name eligible for explicit matching, or null to exclude this event.
     * @param appClassLoader the host [ClassLoader].
     * @param appInfo the host [android.content.pm.ApplicationInfo].
     */
    fun onPackageLoaded(
        type: HookEntryType,
        packageName: String,
        processName: String,
        mainProcessName: String,
        isFirstApplication: Boolean,
        packageNameForMatching: String?,
        appClassLoader: ClassLoader,
        appInfo: ApplicationInfo? = null
    )

    /**
     * Saves the old module instance and retires it when the runtime accepts the handoff.
     * @param extras the extras supplied by the runtime, or null when unavailable.
     * @param saveState a lambda to save the old module instance's state, returning true if successful, false otherwise.
     * @return [Boolean]
     */
    fun onPrepareReloadModule(extras: Bundle?, saveState: (Any) -> Boolean): Boolean

    /**
     * Restores component prerequisites before creating the new entry.
     * @param appInfo the new Xposed module application info.
     * @param state the old module instance's state, or null when unavailable.
     * @return [ModuleHandoffState]
     */
    fun onBeginRestoreModule(appInfo: ApplicationInfo, state: Any?): ModuleHandoffState

    /**
     * Restores user state after onInit and before `onHook`.
     * @param state the old user state.
     */
    fun onRestoreUserState(state: ModuleHandoffState)

    /**
     * Restores component registrations after `onHook`.
     * @param appInfo the new Xposed module application info.
     * @param state the old user state.
     */
    fun onRestoreComponents(appInfo: ApplicationInfo, state: ModuleHandoffState)

    /**
     * Finishes restoring the new module instance and reports collected failures.
     * @param extras additional data supplied by the runtime.
     */
    fun onFinishRestoreModule(extras: Bundle?)

    /** Aborts failure collection after an unrecoverable restore error. */
    fun onAbortRestoreModule()
}