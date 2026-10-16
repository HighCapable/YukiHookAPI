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
package com.highcapable.yukihookapi.hook.xposed.bridge.session

import com.highcapable.yukihookapi.generated.YukiHookProperties
import com.highcapable.yukihookapi.hook.xposed.bridge.session.state.ModuleHandoffState
import org.junit.Assert
import org.junit.Test

/**
 * Checks the state handed between YukiHook module instances.
 */
class ModuleHandoffStateTest {

    @Test
    fun componentValuesSurviveTheFrameworkHandoff() {
        val previous = ModuleHandoffState(
            userState = null,
            components = linkedMapOf("resources" to null, "packages" to arrayOf("host", "secondary"))
        )

        val transferred = ModuleHandoffState.encode(previous)
        val restored = ModuleHandoffState.decode(transferred)

        Assert.assertEquals(YukiHookProperties.PROJECT_YUKIHOOK_SCHEMA_VERSION, transferred[0])
        Assert.assertEquals(listOf("resources", "packages"), restored.components.keys.toList())
        Assert.assertNull(restored.components["resources"])
        Assert.assertEquals(listOf("host", "secondary"), (restored.components["packages"] as Array<*>).toList())
    }

    @Test
    fun anotherSchemaCannotRestoreThisGeneration() {
        val previous = arrayOf<Any?>(YukiHookProperties.PROJECT_YUKIHOOK_SCHEMA_VERSION + 1, null, emptyArray<Any>())

        val failure = Assert.assertThrows(IllegalStateException::class.java) {
            ModuleHandoffState.decode(previous)
        }

        Assert.assertTrue(failure.message.orEmpty().startsWith(
            "Unsupported YukiHook reload state schema ${YukiHookProperties.PROJECT_YUKIHOOK_SCHEMA_VERSION + 1},"
        ))
    }

    @Test
    fun stateBeforeTheSchemaVersionCannotRestoreThisGeneration() {
        val previous = arrayOf<Any?>("1.4.0", null, emptyArray<Any>())

        val failure = Assert.assertThrows(IllegalStateException::class.java) {
            ModuleHandoffState.decode(previous)
        }

        Assert.assertTrue(failure.message.orEmpty().startsWith("Unsupported YukiHook reload state schema unknown,"))
    }
}