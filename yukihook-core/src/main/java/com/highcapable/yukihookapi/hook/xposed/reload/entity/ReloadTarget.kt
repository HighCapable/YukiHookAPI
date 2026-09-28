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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.reload.entity

import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.reload.YukiHookModuleReload
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleReloadBridge

/**
 * An immutable target snapshot.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED, minApiVersion = 102)
class ReloadTarget internal constructor(internal val handle: ModuleReloadBridge.Target) {

    /** Process UID. */
    val uid by handle::uid

    /** Process PID. */
    val pid by handle::pid

    /** Process name. */
    val processName by handle::processName

    /**
     * State at query time.
     * Query [YukiHookModuleReload.targets] again to refresh it.
     */
    val state get() = ReloadState.valueOf(handle.state.name)

    /** Diagnostic module version code, not a code fingerprint. */
    val loadedVersionCode by handle::loadedVersionCode

    override fun toString() = "ReloadTarget(uid=$uid, pid=$pid, processName='$processName', state=$state, loadedVersionCode=$loadedVersionCode)"
}