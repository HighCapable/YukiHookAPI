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

import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge

/**
 * Tracks managed uses of one module instance, without waiting for arbitrary host calls under a lock.
 */
internal class LibXposedModuleSession : ModuleSessionBridge {

    private val lock = Any()
    private var uses = 0
    private var frozen = false

    override fun enter() = synchronized(lock) {
        if (!frozen) {
            uses++
            true
        } else false
    }

    override fun leave() {
        synchronized(lock) {
            check(uses > 0) { "Unbalanced module session use." }
            uses--
        }
    }

    override fun freeze() = synchronized(lock) {
        if (uses == 0) {
            frozen = true
            true
        } else false
    }

    override fun resume() {
        synchronized(lock) { frozen = false }
    }

    override fun close() {
        synchronized(lock) { frozen = true }
    }
}