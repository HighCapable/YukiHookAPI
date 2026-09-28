/*
 * YukiHook - A modern Hook API and Xposed module solution.
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
package com.highcapable.yukihookapi.runtime.state

import androidx.annotation.RestrictTo

/**
 * Defines how a module-owned state is represented across module instances.
 *
 * - Note: Only the encoded value crosses module instances, it must not retain old module objects.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ModuleStateSpec<T : Any> {

    /**
     * Encodes [state] into classloader-neutral values.
     * @param state the state to encode.
     * @return [Any]
     */
    fun encode(state: T): Any

    /**
     * Rebuilds a state owned by the current module instance from [encoded].
     * @param encoded the encoded state.
     * @return [T]
     */
    fun decode(encoded: Any?): T
}