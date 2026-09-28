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

import com.highcapable.yukihookapi.YukiHook
import org.junit.Assert
import org.junit.Test

/**
 * Checks module reload behavior before the framework service connects.
 */
class ModuleReloadAvailabilityTest {

    @Test
    fun disconnectedServiceReportsActivationBeforeQueryingTargets() {
        val reload = YukiHook.Module.reload

        Assert.assertFalse(reload.isAvailable)
        val failure = Assert.assertThrows(IllegalStateException::class.java) { reload.targets }
        Assert.assertEquals(
            "Activate the module in the hook framework to use YukiHookModuleReload.",
            failure.message
        )
    }
}