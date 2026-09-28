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
 * A completed request.
 * @param target the originally selected snapshot.
 * @param status the request outcome.
 * @param message the original diagnostic message, or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
@ConsistentCopyVisibility
data class ModuleReloadResult internal constructor(
    val target: ModuleTarget,
    val status: ModuleReloadStatus,
    val message: String?
)