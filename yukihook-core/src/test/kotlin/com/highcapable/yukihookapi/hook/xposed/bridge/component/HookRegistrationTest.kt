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
package com.highcapable.yukihookapi.hook.xposed.bridge.component

import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.runtime.bridge.HookRegistrationBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Member

/**
 * Checks stable registration IDs, independent handles and failure reporting.
 */
class HookRegistrationTest {

    private val target = HookPoint.Executable(String::class.java.getDeclaredMethod("length"))
    private val callback = ChainHookerBridge { it.proceed() }

    @Test
    fun replayUsesTheSameIdsWithoutMixingPackageScopes() {
        val firstRuntime = RecordingRuntime()
        val first = HookRegistration(firstRuntime, ModuleEventDispatcher())
        first.withEntry("host") {
            first.hook(target, 50, callback)
            first.hook(target, 50, callback)
        }
        first.withEntry("other") { first.hook(target, 50, callback) }

        val nextRuntime = RecordingRuntime()
        val next = HookRegistration(nextRuntime, ModuleEventDispatcher())
        next.withEntry("other") { next.hook(target, 50, callback) }
        next.withEntry("host") {
            next.hook(target, 50, callback)
            next.hook(target, 50, callback)
        }

        Assert.assertEquals(
            listOf("YukiHook:12:package:host:0", "YukiHook:12:package:host:1", "YukiHook:13:package:other:0"),
            firstRuntime.ids
        )
        Assert.assertEquals(
            listOf("YukiHook:13:package:other:0", "YukiHook:12:package:host:0", "YukiHook:12:package:host:1"),
            nextRuntime.ids
        )
    }

    @Test
    fun replacementForwardsTheOriginalHandle() {
        val runtime = RecordingRuntime()
        val hooks = HookRegistration(runtime, ModuleEventDispatcher())
        val original = hooks.withEntry("host") {
            hooks.hook(target, 50, callback)
        }
        val replacement = hooks.replace(original, callback)
        replacement.remove()
        original.remove()

        Assert.assertEquals(listOf(original), runtime.replaced)
        Assert.assertEquals(listOf(replacement, original), runtime.removed)
    }

    @Test
    fun registrationFailureIsReportedEvenWhenTheCallerCatchesIt() {
        val failure = IllegalStateException("Runtime rejected hook")
        val runtime = object : HookRegistrationBridge {
            override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge, id: String?): HookerBridge = throw failure
            override fun replace(handle: HookerBridge, replacement: HookerBridge): HookerBridge = error("No hook was registered")
            override fun deoptimize(member: Member): Boolean = error("Unexpected deoptimization")
        }
        val failures = mutableListOf<Throwable>()
        val hooks = HookRegistration(runtime, ModuleEventDispatcher(onFailure = { failures += it }))

        val result = runCatching { hooks.hook(target, 50, callback) }

        Assert.assertSame(failure, result.exceptionOrNull())
        Assert.assertEquals(listOf(failure), failures)
    }

    private class RecordingRuntime : HookRegistrationBridge {

        val ids = mutableListOf<String?>()
        val removed = mutableListOf<HookerBridge>()
        val replaced = mutableListOf<HookerBridge>()

        override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge, id: String?): HookerBridge {
            ids += id
            return newHandle()
        }

        override fun replace(handle: HookerBridge, replacement: HookerBridge): HookerBridge {
            replaced += handle
            return newHandle()
        }

        override fun deoptimize(member: Member): Boolean = error("Unexpected deoptimization")

        private fun newHandle(): HookerBridge = object : HookerBridge {
            override fun remove() { removed += this }
        }
    }
}