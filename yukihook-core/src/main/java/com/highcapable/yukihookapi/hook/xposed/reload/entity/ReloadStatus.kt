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
 * This file is created by fankes on 2026/9/28.
 */
package com.highcapable.yukihookapi.hook.xposed.reload.entity

import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Outcome of a submitted request.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
enum class ReloadStatus {
    /** The requested module instance completed initialization. */
    SUCCEEDED,

    /**
     * The module refused or failed to reload.
     * [ReloadResult.message] contains the original diagnostic.
     */
    FAILED,

    /** The framework cannot reload this target. */
    UNSUPPORTED,

    /** Another request is already running for this target. */
    IN_PROGRESS,

    /** The target died before the request completed. */
    PROCESS_DIED
}