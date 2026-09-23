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
 * This file is created by fankes on 2026/9/13.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.lifecycle

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration

/**
 * Lifecycle callback contract between the public API and host implementation.
 */
internal interface AppLifecycleBridge {

    /** Whether callback failures should be thrown to the current host. */
    val throwToHostOnFailure: Boolean

    /** Broadcast listeners registered from action names. */
    val receiverActions: List<Pair<Array<out String>, (Context, Intent) -> Unit>>

    /** Broadcast listeners registered from intent filters. */
    val receiverFilters: List<Pair<IntentFilter, (Context, Intent) -> Unit>>

    /** Called around [Application.attachBaseContext]. */
    fun onAttachBaseContext(baseContext: Context, hasCalledSuper: Boolean) = Unit

    /** Called after [Application.onCreate]. */
    fun onCreate(application: Application) = Unit

    /** Called after [Application.onTerminate]. */
    fun onTerminate(application: Application) = Unit

    /** Called after [Application.onLowMemory]. */
    fun onLowMemory(application: Application) = Unit

    /** Called after [Application.onTrimMemory]. */
    fun onTrimMemory(application: Application, level: Int) = Unit

    /** Called after [Application.onConfigurationChanged]. */
    fun onConfigurationChanged(application: Application, config: Configuration) = Unit
}