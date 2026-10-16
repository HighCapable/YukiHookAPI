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
package com.highcapable.yukihookapi.hook.xposed.bridge.session.state

import android.os.Bundle
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.runtime.state.ModuleStateSpec

/**
 * State owned by [YukiHook] during a module handoff.
 */
internal data class ModuleHandoffState(
    val userState: Bundle?,
    val components: Map<String, Any?>
) {

    companion object : ModuleStateSpec<ModuleHandoffState> {

        private const val SCHEMA_VERSION = YukiHook.SCHEMA_VERSION

        override fun decode(encoded: Any?): ModuleHandoffState {
            val values = encoded as? Array<*> ?: error("Missing YukiHook reload state.")
            // States saved before the schema version start with the YukiHook version, so their schema is unknown.
            check(values.getOrNull(0) == SCHEMA_VERSION) {
                "Unsupported YukiHook reload state schema ${values.getOrNull(0) as? Int ?: "unknown"}, the current schema is " +
                    "$SCHEMA_VERSION. Restart the host to apply this module update."
            }
            check(values.size == 3) {
                "Invalid YukiHook reload state."
            }

            val userState = values[1]
            check(userState == null || userState is Bundle) {
                "Invalid module reload user state."
            }

            val saved = values[2] as? Array<*> ?: error("Missing component reload states.")
            val components = buildMap {
                saved.forEach { value ->
                    val component = ComponentState.decode(value)
                    check(component.id !in this) {
                        "Duplicate reload component '${component.id}'"
                    }

                    put(component.id, component.state)
                }
            }

            return ModuleHandoffState(userState, components)
        }

        override fun encode(state: ModuleHandoffState) = arrayOf(
            SCHEMA_VERSION, state.userState,
            state.components.map { (id, value) -> ComponentState.encode(ComponentState(id, value)) }.toTypedArray()
        )
    }

    private data class ComponentState(
        val id: String,
        val state: Any?
    ) {

        companion object : ModuleStateSpec<ComponentState> {

            override fun decode(encoded: Any?): ComponentState {
                val values = encoded as? Array<*> ?: error("Invalid component reload state.")
                check(values.size == 2 && values[0] is String) {
                    "Invalid component reload state."
                }

                return ComponentState(values[0] as String, values[1])
            }

            override fun encode(state: ComponentState) = arrayOf(state.id, state.state)
        }
    }
}