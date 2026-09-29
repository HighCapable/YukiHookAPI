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
package com.highcapable.yukihookapi.runtime.bridge.session

import androidx.annotation.RestrictTo

/**
 * Coordinates managed callbacks with the current module instance's retirement.
 * Locks cover state changes only, user callbacks never run while holding them.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ModuleSessionBridge {

    /**
     * Acquires one use of this module instance, or returns false when it is retiring.
     * @return [Boolean] true if the use was acquired, false if the module instance is retiring.
     */
    fun enter(): Boolean

    /** Releases one acquired use. */
    fun leave()

    /**
     * Stops new uses if no managed callback is running.
     *
     * A busy module instance remains active.
     * @return [Boolean] true if the module instance is now retiring, false if it is busy and remains active.
     */
    fun freeze(): Boolean

    /** Reopens a module instance after a refused request, before disposal has started. */
    fun resume()
}