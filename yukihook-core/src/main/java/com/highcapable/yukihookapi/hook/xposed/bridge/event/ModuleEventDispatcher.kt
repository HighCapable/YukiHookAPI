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
package com.highcapable.yukihookapi.hook.xposed.bridge.event

import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge

/**
 * Runs module work through the session and failure sink supplied by the entry.
 */
internal class ModuleEventDispatcher(
    private val session: ModuleSessionBridge? = null,
    private val onFailure: (Throwable) -> Unit = {}
) {

    /**
     * Returns whether this dispatcher participates in a managed module session.
     * @return [Boolean]
     */
    val isManaged get() = session != null

    /**
     * Runs an operation, rejecting new work after this session has retired.
     * @param block the operation to run.
     * @return [T]
     */
    fun <T> use(block: () -> T): T {
        val current = session ?: return block()
        check(current.enter()) {
            "This module session is retiring."
        }

        return try {
            block()
        } finally {
            current.leave()
        }
    }

    /**
     * Delivers an event only while this session accepts callbacks.
     * @param block the event to deliver.
     */
    fun dispatch(block: () -> Unit) {
        val current = session
        if (current != null && !current.enter()) return

        try {
            block()
        } finally {
            current?.leave()
        }
    }

    /**
     * Reports a caught failure without changing the caller's exception policy.
     * @param failure the failure to report.
     */
    fun reportFailure(failure: Throwable) = onFailure(failure)
}