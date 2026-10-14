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
 * This file is created by fankes on 2026/9/7.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime.bridge

import androidx.annotation.MainThread

/**
 * Receives framework service availability changes.
 * Each successful observation returns an independent subscription that can unregister itself.
 */
fun interface FrameworkServiceBridge {

    /**
     * Called on registration and when the framework service binds or dies.
     *
     * Callbacks run on the main thread. When registered from the main thread, the first callback runs immediately,
     * otherwise it is posted to the main thread.
     * @param available whether the framework service is available.
     */
    @MainThread
    fun onServiceChanged(available: Boolean)

    /**
     * Cancels this subscription without disconnecting the framework service.
     *
     * Call this on the subscription returned by observation, not the input listener.
     * The default implementation does nothing for an unregistered listener.
     */
    fun unregister() = Unit
}