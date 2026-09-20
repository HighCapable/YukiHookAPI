/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import androidx.lifecycle.Lifecycle
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.annotation.xposed.YukiHookXposed82Entry
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import com.highcapable.yukihookapi.hook.core.api.type.ExecutorType
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import kotlin.jvm.JvmName

/**
 * [YukiHookAPI] entry point.
 *
 * Xposed module loading automatically adapts the relevant APIs. Call [encase] directly to complete the operation.
 */
object YukiHookAPI {

    /** Whether the welcome message has not yet been logged. */
    private var isSayHelloOnceTime = true

    /** Whether loading originated from a standalone hook API. */
    internal var isStandalone = false

    /** The tag name. */
    const val TAG = YukiHookAPIProperties.PROJECT_NAME

    /** The current version. */
    const val VERSION = YukiHookAPIProperties.PROJECT_YUKIHOOKAPI_BOM_VERSION

    /**
     * Current [YukiHookAPI] status.
     */
    object Status {

        /**
         * The selected runtime, independently of module activation or hook availability.
         * @return [YukiHookRuntime]
         */
        val runtime get() = YukiHookBridge.runtime

        /**
         * Observes framework service availability. The listener is invoked immediately and on bind/death.
         * Callbacks may run on a binder thread. Call [FrameworkServiceBridge.unregister] on the returned subscription to cancel it.
         *
         * Optionally pass a [Lifecycle] to automatically cancel the subscription when it is destroyed.
         * When provided, the subscription remains active while the lifecycle is stopped,
         * an already destroyed lifecycle does not register or invoke the listener.
         * @param lifecycle the optional lifecycle for automatic cancellation, null by default for manual cancellation only.
         * @param listener the listener notified when the framework service is available or unavailable.
         * @return [FrameworkServiceBridge] or null.
         */
        @RequiresRuntime(YukiHookRuntime.LIBXPOSED)
        fun observeFrameworkService(lifecycle: Lifecycle? = null, listener: FrameworkServiceBridge) =
            YukiHookBridge.observeService(lifecycle, listener)

        /**
         * Gets the project compilation timestamp in local time.
         * @return [Long]
         */
        val compiledTimestamp get() = runCatching { YukiHookAPI_Impl.compiledTimestamp }.getOrNull() ?: 0L

        /**
         * Gets whether the current environment is a host environment.
         * @return [Boolean]
         */
        val isHostEnvironment get() = XposedModuleRuntime.isHostEnvironment

        /**
         * Checks whether module activation is detected through Xposed, TaiChi/Wuji.
         *
         * - Includes both Xposed runtimes through [isXposedModuleActive]. Its runtime-specific
         *   initialization requirements and asynchronous service behavior apply.
         *
         * - Also checks [isTaiChiModuleActive], which requires [ModuleApplication] in the module.
         * @return [Boolean] whether activation is currently detected.
         */
        val isModuleActive get() = isXposedModuleActive || isTaiChiModuleActive

        /**
         * Checks whether module activation is detected through the selected Xposed runtime.
         *
         * - Returns true when [isHostEnvironment] is true, independently of module services.
         *
         * - With [YukiHookRuntime.XPOSED_82], module detection uses self-hook status. It requires
         *   [YukiHookXposed82Entry.generateModuleStatus] and framework injection into the module's own process.
         * - With [YukiHookRuntime.LIBXPOSED], module detection requires a single compatible framework service connection.
         *   [ModuleApplication] initializes it automatically. [observeFrameworkService] can also initialize it and observe changes.
         *   Binding is asynchronous: false before connection or after disconnection does not necessarily mean the module is disabled.
         *   The framework may keep the service connected after the module is disabled, so true does not guarantee
         *   that the module is still enabled in the manager. This is not a real-time query of the manager's enable/disable state.
         *
         * - This does not verify the scope or whether hooks are installed in a particular target app.
         * @return [Boolean] whether activation is currently detected.
         */
        val isXposedModuleActive get() = isHostEnvironment || YukiHookBridge.isModuleActive

        /**
         * Checks only whether the module is active in TaiChi/Wuji.
         *
         * - In the module environment, [Application] must inherit [ModuleApplication].
         * - Always returns false in a host environment.
         * @return [Boolean] whether the module is active.
         */
        val isTaiChiModuleActive get() = !isHostEnvironment && (ModuleApplication.current?.isTaiChiModuleActive ?: false)

        // region Deprecated properties for backward compatibility

        /** Gets whether the current environment is a host environment. */
        @Deprecated(message = "Use isHostEnvironment instead.", replaceWith = ReplaceWith("isHostEnvironment"))
        val isXposedEnvironment get() = isHostEnvironment

        // endregion

        /**
         * Information about the hook framework used by the current [YukiHookAPI].
         *
         * - In a legacy module, [YukiHookXposed82Entry.generateModuleStatus] must be enabled.
         * - In a modern module, these values follow framework-service availability.
         */
        object Executor {

            /**
             * Gets the current hook framework name.
             * @return [String] `unknown` when unavailable or `invalid` when resolution fails.
             */
            val name get() = if (isTaiChiModuleActive && !isXposedModuleActive)
                ExecutorType.TAICHI_XPOSED.explicitName
            else YukiHookBridge.frameworkName

            /**
             * Gets the current hook framework type.
             * @return [ExecutorType]
             */
            val type get() = ExecutorType.from(name)

            /**
             * Gets the current hook framework API version.
             * @return [Int] -1 when unavailable.
             */
            val apiVersion get() = YukiHookBridge.apiVersion

            /**
             * Gets the current hook framework version.
             * @return [String] `unknown` when unavailable or `unsupported` when unsupported.
             */
            val version get() = YukiHookBridge.frameworkVersion

            /**
             * Gets the current hook framework version code.
             * @return [Int] -1 when unavailable or 0 when unsupported.
             */
            val versionCode get() = YukiHookBridge.frameworkVersionCode

            // region Deprecated properties for backward compatibility

            /** Gets the current hook framework API version. */
            @Deprecated(message = "Use apiVersion instead.", replaceWith = ReplaceWith("apiVersion"))
            val apiLevel get() = apiVersion

            /** Gets the current hook framework version. */
            @Deprecated(message = "Use version instead.", replaceWith = ReplaceWith("version"))
            val versionName get() = version

            // endregion
        }
    }

    /**
     * [YukiHookAPI] configuration.
     */
    object Configs {

        /**
         * Configures [YLog.Configs].
         * @param block the configuration block.
         */
        inline fun debugLog(block: YLog.Configs.() -> Unit) = YLog.Configs.apply(block).let {}

        /**
         * Whether debug mode is enabled, false by default.
         *
         * Once enabled, the log manager prints detailed Hook logs to the console.
         *
         * Disabling [YLog.Configs.enabled] also disables [debug].
         */
        @set:JvmName("setDebugEnabled")
        var debug = false

        /**
         * Whether hook support for [SharedPreferences] is enabled.
         *
         * Once enabled, module startup forces [SharedPreferences] file permissions to [Context.MODE_WORLD_READABLE] (0664).
         *
         * - This optional experimental feature is disabled by default.
         * - Use this only to fix file-permission errors that may remain on some systems after enabling legacy new `XSharedPreferences`.
         *
         * Do not enable it when [YukiHookPreferences] already works correctly.
         */
        @RequiresRuntime(YukiHookRuntime.XPOSED_82)
        @Deprecated(message = "Not recommended. This is a legacy trade-off switch, meaningless for modern frameworks.")
        var isEnableHookSharedPreferences = false

        /**
         * Whether [YukiHookDataChannel] between the current Xposed module and host is enabled.
         *
         * The Xposed module's [Application] must inherit [ModuleApplication].
         *
         * - This feature is enabled by default. When disabled, initialization does not load [YukiHookDataChannel].
         */
        var dataChannel = true

        // region Deprecated properties for backward compatibility

        /** Whether debug mode is enabled. */
        @Deprecated(message = "Use debug instead.", replaceWith = ReplaceWith("debug"))
        var isDebug
            get() = debug
            set(value) {
                debug = value
            }

        /** Whether [YukiHookDataChannel] between the current Xposed module and host is enabled. */
        @Deprecated(message = "Use dataChannel instead.", replaceWith = ReplaceWith("dataChannel"))
        var isEnableDataChannel
            get() = dataChannel
            set(value) {
                dataChannel = value
            }

        /**
         * Whether caching the current Xposed module's [Resources] is enabled.
         *
         * - [YukiHookAPI] has adjusted module caching strategy, so this feature is no longer needed.
         */
        @Deprecated(message = "This property was deprecated and no longer has any effect, removed it directly.")
        var isEnableModuleAppResourcesCache = true

        // endregion
    }

    /**
     * Configures [YukiHookAPI].
     * @param block the configuration block.
     */
    inline fun configs(block: Configs.() -> Unit) = Configs.apply(block).let {}

    /**
     * Loads entry point for a Xposed module.
     * @param block the param block.
     */
    fun encase(block: PackageParam.() -> Unit) {
        isStandalone = false

        if (XposedModuleRuntime.isHostEnvironment)
            XposedModuleRuntime.packageParamCallback = block
        else logNotFoundHookApiError()
    }

    /**
     * Loads entry point for a Xposed module.
     * @param hookers the hookers.
     * @throws IllegalStateException if [hookers] is empty.
     */
    fun encase(vararg hookers: YukiBaseHooker) {
        isStandalone = false

        if (XposedModuleRuntime.isHostEnvironment)
            XposedModuleRuntime.packageParamCallback = {
                if (hookers.isNotEmpty())
                    hookers.forEach { loadHooker(it) }
                else YLog.innerE("Failed to passing \"encase\" function because your hookers param is empty", isImplicit = true)
            }
        else logNotFoundHookApiError()
    }

    /**
     * Loads the legacy standalone hook API from an [Application].
     * @param baseContext the attachment base context.
     * @param block the param block.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Not recommended, use the hook framework's native API instead.")
    fun encase(baseContext: Context?, block: PackageParam.() -> Unit) {
        if (YukiHookBridge.runtime != YukiHookRuntime.XPOSED_82) return

        isStandalone = true
        when {
            YukiHookBridge.isStandaloneAvailable && baseContext != null -> block(baseContext.createPackageParam().apply { sayHello() })
            else -> logNotFoundHookApiError()
        }
    }

    /**
     * Loads the legacy standalone hook API from an [Application].
     * @param baseContext the attachment base context.
     * @param hooker the hookers.
     * @throws IllegalStateException if [hooker] is empty.
     */
    @RequiresRuntime(YukiHookRuntime.XPOSED_82)
    @Deprecated(message = "Not recommended, use the hook framework's native API instead.")
    fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker) {
        if (YukiHookBridge.runtime != YukiHookRuntime.XPOSED_82) return

        isStandalone = true
        if (YukiHookBridge.isStandaloneAvailable) {
            if (baseContext != null)
                if (hooker.isNotEmpty()) {
                    sayHello()
                    val param = baseContext.createPackageParam()
                    hooker.forEach { param.loadHooker(it) }
                } else YLog.innerE("Failed to passing \"encase\" function because your hookers param is empty", isImplicit = true)
        } else logNotFoundHookApiError()
    }

    /** Shows a welcome message when [Configs.debug] is enabled. */
    internal fun sayHello() {
        if (!Configs.debug || !isSayHelloOnceTime) return

        isSayHelloOnceTime = false
        YLog.innerD("Welcome to YukiHookAPI $VERSION! Running on ${Status.Executor.name} API ${Status.Executor.apiVersion}", isImplicit = true)
    }

    /** Logs an error when no hook API can be found. */
    private fun logNotFoundHookApiError() = YLog.innerE(
        "Could not found any available hook APIs in current environment. Aborted",
        isImplicit = true
    )

    /**
     * Creates the hook entry object from the base context.
     * @return [PackageParam]
     */
    private fun Context.createPackageParam() = PackageParam(PackageParamWrapper.from(this))
}