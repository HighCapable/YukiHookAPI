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
 * This file is created by fankes on 2026/9/21.
 */
package com.highcapable.yukihookapi.runtime.bridge

import androidx.annotation.MainThread
import androidx.annotation.RestrictTo

/**
 * Module-scope contract implemented by the selected runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ModuleScopeBridge {

    /**
     * Receives the result of a scope request.
     *
     * The callback is always invoked on the main thread.
     */
    interface RequestCallback {

        /**
         * Called with the packages approved by the framework.
         * @param packages the approved packages, which may be a subset of the requested packages.
         */
        @MainThread
        fun onApproved(packages: List<String>)

        /**
         * Called when the scope request fails.
         * @param message the failure message provided by the framework.
         */
        @MainThread
        fun onFailed(message: String)
    }

    /** Whether module-scope operations are currently available. */
    val isAvailable: Boolean

    /** Gets a snapshot of the current module scope. */
    val packages: List<String>

    /**
     * Requests additional packages for the module scope.
     * @param packages the package names to request.
     * @param callback the callback invoked with the approved packages or a failure message.
     */
    fun request(packages: Collection<String>, callback: RequestCallback)

    /**
     * Removes packages from the module scope.
     * @param packages the package names to remove.
     */
    fun remove(packages: Collection<String>)
}