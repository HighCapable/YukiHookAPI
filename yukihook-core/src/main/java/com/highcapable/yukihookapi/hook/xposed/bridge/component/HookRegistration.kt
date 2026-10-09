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
package com.highcapable.yukihookapi.hook.xposed.bridge.component

import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.runtime.bridge.HookRegistrationBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Member

/**
 * Assigns stable identities to hook registrations in each package scope.
 */
internal class HookRegistration(
    private val base: HookRegistrationBridge,
    private val events: ModuleEventDispatcher
) : HookRegistrationBridge {

    private val scope = ThreadLocal<String>()
    private val indexes = mutableMapOf<Key, Int>()

    private data class Key(
        val scope: String,
        val target: HookPoint
    )

    override fun deoptimize(member: Member) = events.use {
        runCatching {
            base.deoptimize(member)
        }.onFailure(events::reportFailure).getOrThrow()
    }

    override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge, id: String?) = events.use {
        runCatching {
            base.hook(target, priority, hooker, id ?: target.resolveNextId())
        }.onFailure(events::reportFailure).getOrThrow()
    }

    override fun replace(handle: HookerBridge, replacement: HookerBridge) = events.use {
        runCatching {
            base.replace(handle, replacement)
        }.onFailure(events::reportFailure).getOrThrow()
    }

    /**
     * Runs declarations with a stable entry ID for hook registration.
     * @param entryId the entry ID to use for hook registration.
     * @param block the block to run with the entry ID.
     * @return [T] the result of the block.
     */
    fun <T> withEntry(entryId: String, block: () -> T): T {
        val previous = scope.get()
        scope.set("package:$entryId")

        return try {
            block()
        } finally {
            if (previous == null)
                scope.remove()
            else scope.set(previous)
        }
    }

    private fun HookPoint.resolveNextId(): String {
        val name = scope.get() ?: "dynamic"
        val index = synchronized(indexes) {
            val key = Key(name, this)
            (indexes[key] ?: 0).also { indexes[key] = it + 1 }
        }

        return "${YukiHook.TAG}:${name.length}:$name:$index"
    }
}