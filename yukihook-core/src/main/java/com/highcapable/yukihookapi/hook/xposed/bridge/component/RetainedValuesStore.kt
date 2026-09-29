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
 * This file is created by fankes on 2026/9/29.
 */
@file:Suppress("UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.xposed.bridge.component

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleLifecycleManager
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleReloadable
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.state.ModuleStateSpec

/**
 * Keeps classloader-neutral values across module instances without retaining module-owned objects.
 */
internal class RetainedValuesStore : ModuleReloadable {

    companion object {
        const val RELOAD_ID = "retained-values-store"
    }

    private data class SavedResult(val id: String, val value: Any?) {

        companion object : ModuleStateSpec<SavedResult> {

            override fun encode(state: SavedResult) = arrayOf(state.id, state.value)

            override fun decode(encoded: Any?): SavedResult {
                val values = encoded as? Array<*> ?: error("Invalid retained value state.")
                check(values.size == 2 && values[0] is String) {
                    "Invalid retained value state."
                }

                return SavedResult(values[0] as String, values[1])
            }
        }
    }

    private var events = ModuleEventDispatcher()

    private val usedIds = mutableSetOf<String>()
    private val results = linkedMapOf<String, Any?>()
    private var restored = emptyMap<String, Any?>()

    override fun onAttach(events: ModuleEventDispatcher) {
        this.events = events
    }

    override fun onSaveState() = synchronized(this) {
        results.map { (id, value) -> SavedResult.encode(SavedResult(id, value)) }.toTypedArray()
    }

    override fun onPrepareRestore(appInfo: ApplicationInfo, state: Any?) {
        val values = state as? Array<*> ?: error("Missing retained value state.")

        restored = buildMap {
            values.forEach {
                val result = SavedResult.decode(it)
                check(result.id !in this) {
                    "Duplicate retained value '${result.id}'."
                }

                put(result.id, result.value)
            }
        }
    }

    /**
     * Retains a saved or newly computed value for this module instance.
     * @param hostEnvironment whether the runtime has loaded a host environment.
     * @param id the versioned identity of the value.
     * @param block computes the value when no saved result exists.
     * @return [T]
     * @throws IllegalArgumentException if [id] is blank or already used in this module instance.
     * @throws IllegalStateException if the result cannot safely cross module class loaders.
     */
    fun <T> retain(hostEnvironment: Boolean, id: String, block: () -> T): T {
        check(hostEnvironment && YukiHookBridge.runtime == YukiHookRuntime.LIBXPOSED && YukiHookBridge.apiVersion >= 102) {
            "RetainedValuesStore requires host environment on Xposed API 102 or later."
        }

        return events.use {
            require(id.isNotBlank()) {
                "Retained result ID must not be blank."
            }

            synchronized(this) {
                require(usedIds.add(id)) { "Retained result ID '$id' is already used in this module instance." }
            }

            runCatching {
                val value = if (restored.containsKey(id)) restored[id] else block()
                ModuleLifecycleManager.checkValue(value)
                if (events.isManaged) synchronized(this) { results[id] = value }

                value as T
            }.getOrElse {
                synchronized(this) { usedIds.remove(id) }
                throw it
            }
        }
    }
}