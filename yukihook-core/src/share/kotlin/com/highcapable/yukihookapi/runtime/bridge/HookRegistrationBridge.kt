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
package com.highcapable.yukihookapi.runtime.bridge

import androidx.annotation.RestrictTo
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Member

/**
 * Hook registration, deoptimization and replacement supplied by the selected runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface HookRegistrationBridge {

    /**
     * Deoptimizes a method or constructor so its callees are invoked without runtime inlining.
     * @param member the calling method or constructor to deoptimize.
     * @return [Boolean] whether deoptimization succeeded.
     */
    fun deoptimize(member: Member): Boolean

    /**
     * Registers one hook and returns its independent handle.
     * @param target the point to hook.
     * @param priority the hook priority.
     * @param hooker the callback to register.
     * @param id the optional identity shared across module instances.
     * @return [HookerBridge] the registered handle.
     */
    fun hook(target: HookPoint, priority: Int, hooker: HookerBridge, id: String? = null): HookerBridge

    /**
     * Replaces one hook while preserving its registration settings.
     * @param handle the current registered handle.
     * @param replacement the new callback.
     * @return [HookerBridge] the replacement's independent handle.
     */
    fun replace(handle: HookerBridge, replacement: HookerBridge): HookerBridge
}