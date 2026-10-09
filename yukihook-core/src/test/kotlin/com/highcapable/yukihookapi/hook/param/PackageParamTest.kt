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
 * This file is created by fankes on 2026/10/3.
 */
package com.highcapable.yukihookapi.hook.param

import com.highcapable.yukihookapi.hook.param.context.PackageContext
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import org.junit.Assert
import org.junit.Test

/**
 * Checks package loading rules that do not depend on the hook environment.
 */
class PackageParamTest {

    private fun param(type: HookEntryType, packageName: String, packageNameForMatching: String? = packageName) = PackageParam(
        PackageContext(
            type = type,
            packageName = packageName,
            processName = packageName,
            isFirstApplication = true,
            classLoader = javaClass.classLoader!!,
            packageNameForMatching = packageNameForMatching
        )
    )

    private fun PackageParam.loads(block: PackageParam.(() -> Unit) -> Unit): Boolean {
        var loaded = false
        block { loaded = true }
        return loaded
    }

    @Test
    fun redirectsSystemFrameworkToSystemServer() {
        // Libxposed excludes system_server from package matching, Xposed 82 reports it as the android package.
        listOf(
            param(HookEntryType.SYSTEM_SERVER, "android", packageNameForMatching = null),
            param(HookEntryType.SYSTEM_SERVER, "android")
        ).forEach { param ->
            Assert.assertTrue(param.loads { loadApp("android") { it() } })
            Assert.assertTrue(param.loads { loadApp("system") { it() } })
            Assert.assertTrue(param.loads { loadApp("com.example", "system") { it() } })
            Assert.assertFalse(param.loads { loadApp("com.example") { it() } })
        }
    }

    @Test
    fun keepsSystemFrameworkOutOfApps() {
        val param = param(HookEntryType.PACKAGE, "com.example")

        Assert.assertFalse(param.loads { loadApp("android") { it() } })
        Assert.assertFalse(param.loads { loadApp("system") { it() } })
        Assert.assertTrue(param.loads { loadApp("android", "com.example") { it() } })
    }

    @Test
    fun loadsNothingWithoutContext() {
        val param = PackageParam()

        Assert.assertFalse(param.loads { loadApp("android") { it() } })
        Assert.assertFalse(param.loads { loadApp("com.example", "system") { it() } })
    }
}