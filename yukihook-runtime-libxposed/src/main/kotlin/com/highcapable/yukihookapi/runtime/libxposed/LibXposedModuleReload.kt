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
package com.highcapable.yukihookapi.runtime.libxposed

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleReloadBridge
import io.github.libxposed.service.HookedTarget
import io.github.libxposed.service.HotReloadResult
import io.github.libxposed.service.XposedService

/**
 * Maps module-service targets without exposing or recreating their native identity.
 */
internal object LibXposedModuleReload : ModuleReloadBridge {

    private class TargetHandle(val handle: LibXposedService.ReloadTarget) : ModuleReloadBridge.Target {
        override val uid get() = handle.target.uid
        override val pid get() = handle.target.pid
        override val processName get() = handle.target.processName
        override val state get() = when (handle.target.state) {
            HookedTarget.State.UP_TO_DATE -> ModuleReloadBridge.State.UP_TO_DATE
            HookedTarget.State.STALE -> ModuleReloadBridge.State.STALE
            HookedTarget.State.RELOADING -> ModuleReloadBridge.State.RELOADING
            HookedTarget.State.FAILED -> ModuleReloadBridge.State.FAILED
        }
        override val loadedVersionCode get() = handle.target.loadedVersionCode
    }

    private val mainHandler = Handler(Looper.getMainLooper())

    override val isAvailable get() = LibXposedService.apiVersion >= XposedService.API_102
    override val targets: List<ModuleReloadBridge.Target> get() = LibXposedService.getReloadTargets().map(::TargetHandle)

    override fun request(target: ModuleReloadBridge.Target, extras: Bundle?, callback: (ModuleReloadBridge.Status, String?) -> Unit) {
        require(target is TargetHandle) {
            "The reload target was not issued by this runtime."
        }

        LibXposedService.requestReload(target.handle, extras) { result ->
            val status = when (result.status()) {
                HotReloadResult.Status.SUCCEEDED -> ModuleReloadBridge.Status.SUCCEEDED
                HotReloadResult.Status.FAILED -> ModuleReloadBridge.Status.FAILED
                HotReloadResult.Status.UNSUPPORTED -> ModuleReloadBridge.Status.UNSUPPORTED
                HotReloadResult.Status.IN_PROGRESS -> ModuleReloadBridge.Status.IN_PROGRESS
                HotReloadResult.Status.PROCESS_DIED -> ModuleReloadBridge.Status.PROCESS_DIED
            }
            mainHandler.post {
                runCatching { callback(status, result.message()) }.onFailure {
                    Log.e("YukiHook-Runtime", "Module reload result listener failed", it)
                }
            }
        }
    }
}