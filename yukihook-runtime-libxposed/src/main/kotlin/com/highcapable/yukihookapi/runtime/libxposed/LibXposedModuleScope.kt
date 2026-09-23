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
 * This file is created by fankes on 2026/9/21.
 */
package com.highcapable.yukihookapi.runtime.libxposed

import android.os.Handler
import android.os.Looper
import com.highcapable.yukihookapi.runtime.bridge.ModuleScopeBridge
import io.github.libxposed.service.XposedService

/**
 * Module scope backed by the libxposed module service.
 */
internal object LibXposedModuleScope : ModuleScopeBridge {

    private val mainHandler = Handler(Looper.getMainLooper())

    override val isAvailable get() = LibXposedService.isAvailable
    override val packages get() = LibXposedService.getScope()

    override fun request(packages: Collection<String>, callback: ModuleScopeBridge.RequestCallback) =
        LibXposedService.requestScope(packages.toList(), object : XposedService.OnScopeEventListener {

            override fun onScopeRequestApproved(approved: List<String>) {
                mainHandler.post { callback.onApproved(approved) }
            }

            override fun onScopeRequestFailed(message: String) {
                mainHandler.post { callback.onFailed(message) }
            }
        })

    override fun remove(packages: Collection<String>) = LibXposedService.removeScope(packages.toList())
}