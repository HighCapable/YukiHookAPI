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
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime.libxposed.entry

import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiHookLibXposedEvent
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleReloadPolicy
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedHookApi
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedModuleSession
import com.highcapable.yukihookapi.runtime.libxposed.entry.base.BaseLibXposedEntry
import com.highcapable.yukihookapi.runtime.state.ModuleStateSpec
import io.github.libxposed.api.XposedModuleInterface

/**
 * Libxposed API 102 entry for `libxposed:api` 102 version.
 */
abstract class LibXposed102Entry : BaseLibXposedEntry() {

    /**
     * Keeps managed and native reload states separate across module instances.
     */
    private data class HandoffState(
        val managedState: Array<*>,
        val nativeState: Any?
    ) {

        companion object : ModuleStateSpec<HandoffState> {

            private const val VERSION = YukiHook.VERSION

            /** Wraps the state produced by the core reload manager. */
            fun fromManagedState(managedState: Any, nativeState: Any?) = HandoffState(
                managedState as? Array<*> ?: error("Invalid YukiHook reload state."), nativeState
            )

            override fun decode(encoded: Any?): HandoffState {
                val values = encoded as? Array<*> ?: error("Missing module reload state. Restart the host to try again.")
                check(values.size == 3 && values[0] == VERSION) {
                    "Unsupported module reload state. Restart the host to try again."
                }

                val managedState = values[1] as? Array<*> ?: error("Missing YukiHook reload state. Restart the host to try again.")
                return HandoffState(managedState, values[2])
            }

            override fun encode(state: HandoffState) = arrayOf(VERSION, state.managedState, state.nativeState)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as HandoffState

            if (!managedState.contentEquals(other.managedState)) return false
            if (nativeState != other.nativeState) return false

            return true
        }

        override fun hashCode(): Int {
            var result = managedState.contentHashCode()
            result = 31 * result + (nativeState?.hashCode() ?: 0)
            return result
        }
    }

    /** Reload policy supplied by the generated entry. */
    protected open val hotReload = ModuleReloadPolicy.NONE

    final override fun onHotReloading(param: XposedModuleInterface.HotReloadingParam): Boolean {
        if (!initialized || hotReload == ModuleReloadPolicy.NONE) return false

        return YukiHookModuleCaller.callPrepareReloadModule(param.extras) { managedState ->
            var nativeState: Any? = null
            val nativeParam = object : XposedModuleInterface.HotReloadingParam by param {
                override fun setSavedInstanceState(outState: Any?) {
                    // Let the framework validate native state before combining the two owners' data.
                    param.setSavedInstanceState(outState)
                    nativeState = outState
                }
            }

            if (YukiHookLibXposedEvent.hotReloading?.invoke(this, nativeParam) != false) {
                param.setSavedInstanceState(HandoffState.encode(HandoffState.fromManagedState(managedState, nativeState)))
                true
            } else false
        }
    }

    final override fun onHotReloaded(param: XposedModuleInterface.HotReloadedParam) {
        // NONE rejects the next request, it must still complete an incoming handoff.
        attachRuntime(restoring = true)
        parseProcessName(param)

        var restoreFailure: Throwable? = null
        runCatching {
            val saved = HandoffState.decode(param.savedInstanceState)
            initialized = YukiHookModuleCaller.callRestoreModule(::createHookEntry, moduleApplicationInfo, saved.managedState) {
                restoreFailure = it
            }
            if (!initialized) throw checkNotNull(restoreFailure) {
                "Module restoration failed without a cause."
            }

            val nativeParam = object : XposedModuleInterface.HotReloadedParam by param {
                override fun getSavedInstanceState() = saved.nativeState
                override fun getOldHookHandles() = param.oldHookHandles.filterNot { it.id?.startsWith("${YukiHook.TAG}:") == true }
            }
            runCatching {
                YukiHookLibXposedEvent.hotReloaded?.invoke(this, nativeParam)
            }.onFailure(YukiHookModuleCaller::callRecordRestoreFailure)
        }.also {
            // Replaced handles are already invalidated. unhook is idempotent and removes only
            // old registrations, including declarations omitted from this module instance.
            param.oldHookHandles.forEach { handle ->
                runCatching { handle.unhook() }.onFailure(YukiHookModuleCaller::callRecordRestoreFailure)
            }
        }.onFailure {
            YukiHookModuleCaller.callAbortRestoreModule()
        }.getOrThrow()

        YukiHookModuleCaller.callFinishRestoreModule(param.extras)
    }

    final override fun attachRuntime() = attachRuntime(restoring = false)

    private fun attachRuntime(restoring: Boolean) {
        val session = if (restoring || hotReload != ModuleReloadPolicy.NONE)
            LibXposedModuleSession()
        else null

        val api = LibXposedHookApi(this, session)
        val registrations = session?.let { YukiHookModuleCaller.callAttachModuleSession(it, api) } ?: api
        api.attach(registrations)
    }
}