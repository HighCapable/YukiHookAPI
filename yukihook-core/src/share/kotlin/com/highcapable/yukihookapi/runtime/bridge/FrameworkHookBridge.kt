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
 * This file is created by fankes on 2026/9/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.runtime.bridge

import android.content.Context
import android.content.SharedPreferences
import androidx.annotation.RestrictTo
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Member

/**
 * Binary hook bridge contract shared by core and the selected runtime,
 * providing access to the selected runtime and its services.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface FrameworkHookBridge {

    /** The current runtime type. */
    val runtime: YukiHookRuntime

    /** Whether the runtime is available for hook operations. */
    val isAvailable: Boolean

    /** Whether the standalone hook API is available. */
    val isStandaloneAvailable: Boolean

    /** Whether the module has one available framework service. */
    val isModuleActive: Boolean

    /** Runtime framework API version. */
    val apiVersion: Int

    /** Framework display name. */
    val frameworkName: String

    /** Framework version. */
    val frameworkVersion: String

    /** Framework version code. */
    val frameworkVersionCode: Long

    /** Module-scope bridge. */
    val moduleScope: ModuleScopeBridge

    /** Executable invocation capabilities supplied by the selected runtime. */
    val executableInvoker: ExecutableInvokerBridge

    /** Initializes module runtime services without loading host-only API classes. */
    fun initializeModuleServices()

    /**
     * Installs the generated legacy module-status hooks when supported by the selected runtime.
     * @param loader the class loader to use.
     */
    fun installLegacyModuleStatusHooks(loader: ClassLoader?)

    /**
     * Observes service readiness and returns an independent subscription for the listener.
     * @param lifecycle the optional lifecycle for automatic cancellation, or null for manual cancellation only.
     * @param listener the listener to observe service readiness.
     * @return [FrameworkServiceBridge] or null.
     * @throws UnsupportedOperationException when the selected runtime does not support framework services.
     */
    fun observeService(lifecycle: Lifecycle?, listener: FrameworkServiceBridge): FrameworkServiceBridge?

    /**
     * Opens the storage selected by the runtime for the current environment.
     * @param context the context to use.
     * @param packageName the package name of the storage.
     * @param name the name of the storage.
     * @param hostEnvironment whether the storage is for the host environment.
     * @return [SharedPreferences]
     */
    fun preferences(context: Context?, packageName: String, name: String, hostEnvironment: Boolean): SharedPreferences

    /**
     * Gets shared-file storage for the current environment.
     * @param hostEnvironment whether storage is accessed from a host environment.
     * @return [SharedFilesBridge]
     * @throws UnsupportedOperationException when the selected runtime does not support shared files.
     */
    fun sharedFiles(hostEnvironment: Boolean): SharedFilesBridge

    /**
     * Deoptimizes a method or constructor so its callees are invoked without runtime inlining.
     * @param member the calling method or constructor to deoptimize.
     * @return [Boolean] whether deoptimization succeeded.
     * @throws UnsupportedOperationException when the selected runtime does not support deoptimization.
     */
    fun deoptimize(member: Member): Boolean

    /**
     * Registers a hook using the supplied hooker contract and returns an independent hook that can remove itself.
     * @param target the point to hook.
     * @param priority the priority of the hook.
     * @param hooker the hooker to invoke when the hook is triggered.
     * @return [HookerBridge]
     */
    fun hook(target: HookPoint, priority: Int, hooker: HookerBridge): HookerBridge

    /**
     * Replaces a registered hook with a new hooker while preserving the registration settings.
     * @param handle the handle returned by [hook].
     * @param replacement the replacement hooker callback.
     * @return [HookerBridge]
     * @throws UnsupportedOperationException when the selected runtime does not support replacement.
     */
    fun replace(handle: HookerBridge, replacement: HookerBridge): HookerBridge

    /**
     * Logs using the attached framework without discarding its priority or tag.
     * @param priority the priority of the log.
     * @param tag the tag of the log.
     * @param message the message of the log.
     * @param throwable the throwable of the log.
     */
    fun log(priority: Int, tag: String, message: String, throwable: Throwable?)
}