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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.reload

import android.os.Bundle
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleLifecycleManager
import com.highcapable.yukihookapi.hook.xposed.reload.entity.ReloadResult
import com.highcapable.yukihookapi.hook.xposed.reload.entity.ReloadStatus
import com.highcapable.yukihookapi.hook.xposed.reload.entity.ReloadTarget
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Queries running hosts and requests module code reloads after an APK update.
 *
 * Call [isAvailable] before using this API.
 *
 * Obtain this facade from [YukiHook.Module.reload] in the module process.
 * Use preferences to propagate configuration changes instead of reloading code.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
class YukiHookModuleReload private constructor() {

    internal companion object {

        /** Whether the current environment is a host environment. */
        private val isHostEnvironment = XposedModuleRuntime.isHostEnvironment

        /** The current [YukiHookModuleReload] singleton. */
        private val instance = YukiHookModuleReload()

        /**
         * Gets the [YukiHookModuleReload] singleton.
         * @return [YukiHookModuleReload]
         */
        fun get() = instance
    }

    private val current by YukiHookBridge::moduleReload

    /** Checks that this API is used from the module process. */
    private fun checkApi() {
        require(!YukiHook.isStandalone) {
            "YukiHookModuleReload not allowed in standalone hook API."
        }
        check(!isHostEnvironment) {
            "YukiHookModuleReload is only available in a module process."
        }
        check(YukiHookBridge.isModuleActive) {
            "Activate the module in the hook framework to use YukiHookModuleReload."
        }
    }

    /**
     * Gets whether a compatible framework service is available in the module process.
     * This does not guarantee that a target will accept a reload request.
     * @return [Boolean] false in a host process, on an unsupported runtime, or when the service is unavailable.
     */
    val isAvailable get() = runCatching {
        checkApi()
        current.isAvailable
    }.getOrDefault(false)

    /**
     * Queries the framework for fresh reload target snapshots on every access.
     * @return [List]<[ReloadTarget]> the running hosts available for reload.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    val targets: List<ReloadTarget> get() {
        checkApi()
        return current.targets.map(::ReloadTarget)
    }

    /**
     * Requests a reload of the selected host.
     *
     * Returning from this function only means the request was submitted.
     * The callback runs on the main thread.
     * Submission errors are thrown directly and do not invoke the callback.
     * Neither a successful submission nor a target snapshot guarantees that the old module accepts reload.
     * @param target a target from [targets].
     * @param extras primitive values, strings, arrays and nested bundles only, no module-defined objects.
     * @param callback the result listener.
     * @throws IllegalStateException if the module service is not connected.
     */
    fun request(target: ReloadTarget, extras: Bundle? = null, callback: (ReloadResult) -> Unit) {
        checkApi()

        ModuleLifecycleManager.checkBundle(extras)
        current.request(target.handle, extras) { status, message ->
            callback(ReloadResult(target, ReloadStatus.valueOf(status.name), message))
        }
    }

    /**
     * Queries current targets and requests a reload of the single matching process.
     *
     * - Note: The process name, UID and PID are query filters, not stable target identities.
     *   This query and request are not atomic. Query and submission errors are thrown directly and do not invoke the callback.
     *   If no unique target matches, the callback receives null immediately on the calling thread.
     *   A submitted request's result callback runs on the main thread.
     * @param processName the exact process name to match.
     * @param uid optional process UID filter.
     * @param pid optional process PID filter.
     * @param extras primitive values, strings, arrays and nested bundles only, no module-defined objects.
     * @param callback the result listener, or null when no unique target matches.
     * @throws IllegalStateException if the module service is unavailable.
     */
    fun request(processName: String, uid: Int? = null, pid: Int? = null, extras: Bundle? = null, callback: (ReloadResult?) -> Unit) {
        val target = targets.singleOrNull {
            it.processName == processName && (uid == null || it.uid == uid) && (pid == null || it.pid == pid)
        } ?: return callback(null)

        request(target, extras) { callback(it) }
    }
}