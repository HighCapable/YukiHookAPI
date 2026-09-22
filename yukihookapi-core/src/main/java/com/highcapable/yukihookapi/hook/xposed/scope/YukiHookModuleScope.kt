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
 * This file is created by fankes on 2026/9/21.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.scope

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.ModuleScopeBridge

/**
 * [YukiHookAPI] module scope backed by the module environment.
 *
 * Call [isAvailable] before using this API.
 *
 * This API does not restart host processes or manage existing hook registrations.
 * Restart the affected host processes to apply their new scope.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
class YukiHookModuleScope private constructor() {

    internal companion object {

        /** Whether the current environment is a host environment. */
        private val isHostEnvironment = XposedModuleRuntime.isHostEnvironment

        /** The current [YukiHookModuleScope] singleton. */
        private val instance = YukiHookModuleScope()

        /**
         * Gets the [YukiHookModuleScope] singleton.
         * @return [YukiHookModuleScope]
         */
        fun get() = instance
    }

    private val current by YukiHookBridge::moduleScope

    /** Checks that this API is used from the module process. */
    private fun checkApi() {
        require(!YukiHookAPI.isStandalone) {
            "YukiHookModuleScope not allowed in standalone hook API."
        }
        check(!isHostEnvironment) {
            "YukiHookModuleScope is only available in a module process."
        }
        check(YukiHookBridge.isModuleActive) {
            "Activate the module in the hook framework to use YukiHookModuleScope."
        }
    }

    /**
     * Gets whether a compatible framework service is available in the module process.
     * @return [Boolean] false in a host process, on an unsupported runtime, or when the service is unavailable.
     */
    val isAvailable get() = runCatching {
        checkApi()
        current.isAvailable
    }.getOrDefault(false)

    /**
     * Queries the framework for the current module scope on every access.
     * @return [List]<[String]> a snapshot of the selected package names, which may include `system` for system_server.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    val packages: List<String> get() {
        checkApi()
        return current.packages
    }

    /**
     * Requests additional packages for the module scope.
     *
     * Returning from this function only means the request was submitted.
     * The callback is always invoked on the main thread.
     * Submission errors are thrown directly and do not invoke the callback.
     * @param packages package names to request, including `system` when targeting system server.
     * @param callback callback invoked with the approved packages or a failure message.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    fun request(
        vararg packages: String,
        callback: (approved: Boolean, errorMessage: String?, packages: List<String>) -> Unit
    ) = request(packages.toList(), callback)

    /**
     * Requests additional packages for the module scope.
     *
     * Returning from this function only means the request was submitted.
     * The callback is always invoked on the main thread.
     * Submission errors are thrown directly and do not invoke the callback.
     * @param packages package names to request, including `system` when targeting system server.
     * @param callback callback invoked with the approved packages or a failure message.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    fun request(
        packages: Collection<String>,
        callback: (approved: Boolean, errorMessage: String?, packages: List<String>) -> Unit
    ) {
        checkApi()
        current.request(packages, object : ModuleScopeBridge.RequestCallback {
            override fun onApproved(packages: List<String>) = callback(true, null, packages)
            override fun onFailed(message: String) = callback(false, message, emptyList())
        })
    }

    /**
     * Removes packages from the module scope.
     *
     * Returning from this function only means the removal request was submitted.
     * The framework does not report the result of this operation.
     * @param packages package names to remove.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    fun remove(vararg packages: String) = remove(packages.toList())

    /**
     * Removes packages from the module scope.
     *
     * Returning from this function only means the removal request was submitted.
     * The framework does not report the result of this operation.
     * @param packages package names to remove.
     * @throws IllegalStateException in a host process or when the framework service is unavailable.
     */
    fun remove(packages: Collection<String>) {
        checkApi()
        current.remove(packages)
    }
}