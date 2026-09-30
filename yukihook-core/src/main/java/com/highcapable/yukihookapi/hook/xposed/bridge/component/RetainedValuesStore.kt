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
package com.highcapable.yukihookapi.hook.xposed.bridge.component

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleLifecycleManager
import com.highcapable.yukihookapi.hook.xposed.bridge.session.ModuleReloadable
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.state.ModuleStateSpec
import kotlin.reflect.KClass
import kotlin.reflect.KType

/**
 * Keeps classloader-neutral values across module instances without retaining module-owned objects.
 */
internal class RetainedValuesStore : ModuleReloadable {

    companion object {
        const val RELOAD_ID = "retained-values-store"
    }

    private data class SavedResult(val key: String, val type: String, val value: Any?) {

        companion object : ModuleStateSpec<SavedResult> {

            override fun encode(state: SavedResult) = arrayOf(state.key, state.type, state.value)

            override fun decode(encoded: Any?): SavedResult {
                val values = encoded as? Array<*> ?: error("Invalid retained value state.")
                check(values.size == 3 && values[0] is String && values[1] is String) {
                    "Invalid retained value state."
                }

                return SavedResult(values[0] as String, values[1] as String, values[2])
            }
        }
    }

    private var events = ModuleEventDispatcher()

    private val usedKeys = mutableSetOf<String>()
    private val results = linkedMapOf<String, SavedResult>()
    private var restored = emptyMap<String, SavedResult>()

    override fun onAttach(events: ModuleEventDispatcher) {
        this.events = events
    }

    override fun onSaveState(recovery: Boolean) = synchronized(this) {
        results.values.map { SavedResult.encode(it) }.toTypedArray()
    }

    override fun onPrepareRestore(appInfo: ApplicationInfo, state: Any?) {
        val values = state as? Array<*> ?: error("Missing retained value state.")

        restored = buildMap {
            values.forEach {
                val result = SavedResult.decode(it)
                check(result.key !in this) {
                    "Duplicate retained value key '${result.key}'."
                }

                put(result.key, result)
            }
        }
    }

    /**
     * Retains a saved value with the same declared type or computes a new value for this module instance.
     * @param hostEnvironment whether the runtime has loaded a host environment.
     * @param type the declared result type, including type arguments and nullability.
     * @param key the versioned key of the result value.
     * @param block computes the value when no saved result has the same type.
     * @return [T]
     * @throws IllegalArgumentException if [key] is blank or already used in this module instance.
     * @throws IllegalStateException if the result cannot safely cross module class loaders.
     */
    fun <T> retain(hostEnvironment: Boolean, type: KType, key: String, block: () -> T): T {
        check(hostEnvironment && YukiHookBridge.runtime == YukiHookRuntime.LIBXPOSED && YukiHookBridge.apiVersion >= 102) {
            "RetainedValuesStore requires host environment on Xposed API 102 or later."
        }

        return events.use {
            require(key.isNotBlank()) {
                "Retained result key must not be blank."
            }

            synchronized(this) {
                require(usedKeys.add(key)) { "Retained result key '$key' is already used in this module instance." }
            }

            runCatching {
                val typeSignature = type.toCacheType()
                val saved = restored[key]
                val value = if (typeSignature != null && saved?.type == typeSignature) saved.value else block()

                ModuleLifecycleManager.checkValue(value)
                if (events.isManaged && typeSignature != null) synchronized(this) {
                    results[key] = SavedResult(key, typeSignature, value)
                }

                @Suppress("UNCHECKED_CAST")
                value as T
            }.getOrElse {
                synchronized(this) { usedKeys.remove(key) }
                throw it
            }
        }
    }

    private fun KType.toCacheType(): String? {
        val name = (classifier as? KClass<*>)?.qualifiedName ?: return null
        val parameters = arguments.map { argument ->
            val nestedType = argument.type ?: return@map "*"
            val nested = nestedType.toCacheType() ?: return null
            "${argument.variance}:${nested.length}:$nested"
        }

        return buildString {
            append("1:").append(name.length).append(':').append(name)
            append(if (isMarkedNullable) '?' else '!')
            append('<').append(parameters.size).append(':')
            parameters.forEach { append(it.length).append(':').append(it) }
            append('>')
        }
    }
}