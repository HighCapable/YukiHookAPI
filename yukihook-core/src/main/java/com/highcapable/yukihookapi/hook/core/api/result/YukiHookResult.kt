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
package com.highcapable.yukihookapi.hook.core.api.result

import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge

/**
 * Holds an immutable snapshot of an independent hook registration.
 * @param target the hooked point.
 * @param hooker the registered hook returned by the runtime.
 */
internal class YukiHookResult private constructor(val target: HookPoint, val hooker: HookerBridge) {

    companion object {

        /**
         * Creates a [YukiHookResult] instance.
         * @param target the hooked point.
         * @param hooker the registered hook returned by the runtime.
         * @return [YukiHookResult]
         */
        fun from(target: HookPoint, hooker: HookerBridge) = YukiHookResult(target, hooker)
    }

    /** Removes this registration without removing other hooks on [target]. */
    fun remove() = hooker.remove()
}