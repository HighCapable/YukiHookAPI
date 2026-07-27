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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI.Configs.debugLog
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiProperty
import com.highcapable.yukihookapi.hook.core.api.compat.type.ExecutorType
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.isTaiChiModuleActive
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiXposedModuleStatus
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import java.lang.reflect.Member

/**
 * [YukiHookAPI] loading entry point.
 *
 * Supports both module loading and custom Hook loading.
 *
 * Xposed module loading automatically adapts the relevant APIs. Call [encase] directly to complete the operation.
 *
 * Call [configs] to configure [YukiHookAPI].
 */
object YukiHookAPI {

    /** Whether the welcome message has not yet been printed. */
    private var isShowSplashLogOnceTime = true

    /** Whether loading originated from a custom Hook API. */
    internal var isLoadedFromBaseContext = false

    /** The tag name. */
    const val TAG = YukiHookAPIProperties.PROJECT_NAME

    /** The current version. */
    const val VERSION = YukiHookAPIProperties.PROJECT_YUKIHOOKAPI_CORE_VERSION

    /**
     * Version name.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [VERSION].
     */
    @Deprecated(message = "Version names and version codes are no longer distinguished", ReplaceWith("VERSION"))
    const val API_VERSION_NAME = VERSION

    /**
     * Version code.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [VERSION].
     */
    @Deprecated(message = "Version names and version codes are no longer distinguished", ReplaceWith("VERSION"))
    const val API_VERSION_CODE = -1

    /**
     * Current [YukiHookAPI] status.
     */
    object Status {

        /**
         * Gets the project compilation timestamp in local time.
         * @return [Long]
         */
        val compiledTimestamp get() = runCatching { YukiHookAPI_Impl.compiledTimestamp }.getOrNull() ?: 0L

        /**
         * Gets whether the current environment is a (Xposed) host environment.
         * @return [Boolean]
         */
        val isXposedEnvironment get() = YukiXposedModule.isXposedEnvironment

        /**
         * Gets the current Hook Framework name.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [Executor.name].
         * @return [String]
         */
        @Deprecated(
            message = "Use the new API to implement this feature",
            ReplaceWith("Executor.name", "com.highcapable.yukihookapi.YukiHookAPI.Status.Executor")
        )
        val executorName get() = Executor.name

        /**
         * Gets the current Hook Framework version.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [Executor.apiLevel], [Executor.versionName], and [Executor.versionCode].
         * @return [Int]
         */
        @Deprecated(
            message = "Use the new API to implement this feature",
            ReplaceWith("Executor.apiLevel", "com.highcapable.yukihookapi.YukiHookAPI.Status.Executor")
        )
        val executorVersion get() = Executor.apiLevel

        /**
         * Checks whether the module is active in Xposed, TaiChi, or Wuji.
         *
         * - In the module environment, [Application] must extend [ModuleApplication].
         *
         * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
         *
         * - In a (Xposed) host environment, only the activation state excluding [isTaiChiModuleActive] is returned.
         * @return [Boolean] whether the module is active.
         */
        val isModuleActive get() = isXposedEnvironment || YukiXposedModuleStatus.isActive || isTaiChiModuleActive

        /**
         * Checks only whether the module is active in Xposed.
         *
         * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
         *
         * - Always returns true in a (Xposed) host environment.
         * @return [Boolean] whether the module is active.
         */
        val isXposedModuleActive get() = isXposedEnvironment || YukiXposedModuleStatus.isActive

        /**
         * Checks only whether the module is active in TaiChi or Wuji.
         *
         * - In the module environment, [Application] must extend [ModuleApplication].
         *
         * - Always returns false in a (Xposed) host environment.
         * @return [Boolean] whether the module is active.
         */
        val isTaiChiModuleActive get() = isXposedEnvironment.not() && (ModuleApplication.currentContext?.isTaiChiModuleActive ?: false)

        /**
         * Checks whether the current Hook Framework supports Resources Hook.
         *
         * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
         *
         * - In a (Xposed) host environment, true may be returned only after a delayed event callback.
         *
         * - Ensure that [InjectYukiHookWithXposed.isUsingResourcesHook] is enabled. Otherwise this always returns false.
         * @return [Boolean] whether Resources Hook is supported.
         */
        val isSupportResourcesHook
            get() = YukiXposedModule.isSupportResourcesHook.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.isSupportResourcesHook

        /**
         * Information about the Hook Framework used by the current [YukiHookAPI].
         */
        object Executor {

            /**
             * Gets the current Hook Framework name.
             *
             * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
             * @return [String] `unknown` when unavailable or `invalid` when resolution fails.
             */
            val name
                get() = HookApiProperty.name.takeIf { isXposedEnvironment } ?: when {
                    isXposedModuleActive -> YukiXposedModuleStatus.executorName
                    isTaiChiModuleActive -> HookApiProperty.TAICHI_XPOSED_NAME
                    else -> YukiXposedModuleStatus.executorName
                }

            /**
             * Gets the current Hook Framework type.
             *
             * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
             * @return [ExecutorType]
             */
            val type get() = HookApiProperty.type.takeIf { isXposedEnvironment } ?: HookApiProperty.type(YukiXposedModuleStatus.executorName)

            /**
             * Gets the current Hook Framework API version.
             *
             * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
             * @return [Int] -1 when unavailable.
             */
            val apiLevel get() = HookApiProperty.apiLevel.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorApiLevel

            /**
             * Gets the current Hook Framework version name.
             *
             * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
             * @return [String] `unknown` when unavailable or `unsupported` when unsupported.
             */
            val versionName get() = HookApiProperty.versionName.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorVersionName

            /**
             * Gets the current Hook Framework version code.
             *
             * - In the module environment, [InjectYukiHookWithXposed.isUsingXposedModuleStatus] must be enabled.
             * @return [Int] -1 when unavailable or 0 when unsupported.
             */
            val versionCode get() = HookApiProperty.versionCode.takeIf { isXposedEnvironment } ?: YukiXposedModuleStatus.executorVersionCode
        }
    }

    /**
     * [YukiHookAPI] configuration.
     */
    object Configs {

        /**
         * Configures [YLog.Configs].
         * @param initiate the configuration block.
         */
        inline fun debugLog(initiate: YLog.Configs.() -> Unit) = YLog.Configs.apply(initiate).build()

        /**
         * Global identifier for debug logs.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [debugLog] and use [YLog.Configs.tag].
         */
        @Deprecated(message = "Use the new API to implement this feature")
        var debugTag
            get() = YLog.Configs.tag
            set(value) {
                YLog.Configs.tag = value
            }

        /**
         * Whether debug mode is enabled, false by default.
         *
         * Once enabled, the log manager prints detailed Hook logs to the console.
         *
         * Disabling [YLog.Configs.isEnable] also disables [isDebug].
         */
        var isDebug = false

        /**
         * Whether debug log output is enabled.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [debugLog] and use [YLog.Configs.isEnable].
         */
        @Deprecated(message = "Use the new API to implement this feature")
        var isAllowPrintingLogs
            get() = YLog.Configs.isEnable
            set(value) {
                YLog.Configs.isEnable = value
            }

        /**
         * Whether [YukiHookPrefsBridge] key-value caching is enabled.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [isEnablePrefsBridgeCache].
         */
        @Deprecated(message = "Use the renamed API to implement this feature", ReplaceWith("isEnablePrefsBridgeCache"))
        var isEnableModulePrefsCache = false

        /**
         * Whether [YukiHookPrefsBridge] key-value caching is enabled.
         *
         * - This API and feature have been removed and will be deleted in a future version.
         *
         * - Direct key-value caching was removed because it can cause out-of-memory errors.
         */
        @Deprecated(message = "This API and feature have been removed. Delete this call")
        var isEnablePrefsBridgeCache = false

        /**
         * Whether caching the current Xposed module's [Resources] is enabled.
         *
         * - This feature is enabled by default to prevent excessive memory reuse.
         *
         * - When disabled, every use of [PackageParam.moduleAppResources] creates a new instance and may reduce performance.
         *
         * Call [PackageParam.refreshModuleAppResources] to refresh the cache manually.
         */
        var isEnableModuleAppResourcesCache = true

        /**
         * Whether Hook support for Xposed module activation and related states is enabled.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [InjectYukiHookWithXposed.isUsingXposedModuleStatus].
         */
        @Deprecated(message = "Migrate manually to the new API")
        var isEnableHookModuleStatus = true

        /**
         * Whether Hook support for [SharedPreferences] is enabled.
         *
         * Once enabled, module startup forces [SharedPreferences] file permissions to [Context.MODE_WORLD_READABLE] (0664).
         *
         * - This optional experimental feature is disabled by default.
         *
         * - Use this only to fix file-permission errors that may remain on some systems after enabling New XSharedPreferences.
         * Do not enable it when [YukiHookPrefsBridge] already works correctly.
         */
        var isEnableHookSharedPreferences = false

        /**
         * Whether [YukiHookDataChannel] communication between the current Xposed module and host app is enabled.
         *
         * The Xposed module's [Application] must extend [ModuleApplication].
         *
         * - This feature is enabled by default. When disabled, initialization does not load [YukiHookDataChannel].
         */
        var isEnableDataChannel = true

        /**
         * Whether [Member] caching is enabled.
         *
         * - This API and feature have been removed and will be deleted in a future version.
         *
         * - Direct [Member] caching was removed because it can cause out-of-memory errors.
         */
        @Deprecated(message = "This API and feature have been removed. Delete this call")
        var isEnableMemberCache = false

        /** Completes the configuration block. */
        internal fun build() = Unit
    }

    /**
     * Configures [YukiHookAPI].
     *
     * See [configs Method](https://highcapable.github.io/YukiHookAPI/en/config/api-example#configs-method)
     * @param initiate the configuration block.
     */
    inline fun configs(initiate: Configs.() -> Unit) = Configs.apply(initiate).build()

    /**
     * Loading entry point for a Xposed module.
     *
     * See [Created by lambda](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-lambda)
     * @param initiate the Hook block.
     */
    fun encase(initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = false
        if (YukiXposedModule.isXposedEnvironment)
            YukiXposedModule.packageParamCallback = initiate
        else printNotFoundHookApiError()
    }

    /**
     * Loading entry point for a Xposed module.
     *
     * See [Created by Custom Hooker](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
     * @param hooker the required, non-empty Hooker array.
     * @throws IllegalStateException if [hooker] is empty.
     */
    fun encase(vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = false
        if (YukiXposedModule.isXposedEnvironment)
            YukiXposedModule.packageParamCallback = {
                if (hooker.isNotEmpty())
                    hooker.forEach { it.assignInstance(packageParam = this) }
                else YLog.innerE("Failed to passing \"encase\" method because your hooker param is empty", isImplicit = true)
            }
        else printNotFoundHookApiError()
    }

    /**
     * Loading entry point for an [Application].
     *
     * Load [YukiHookAPI] from [Application.attachBaseContext].
     *
     * See [Use as Hook API](https://highcapable.github.io/YukiHookAPI/en/guide/quick-start#use-as-hook-api)
     *
     * See [Created by lambda](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-lambda)
     * @param baseContext attachBaseContext.
     * @param initiate the Hook block.
     */
    fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit) {
        isLoadedFromBaseContext = true
        when {
            HookApiCategoryHelper.hasAvailableHookApi && baseContext != null ->
                initiate(baseContext.createPackageParam().apply { printSplashInfo() })
            else -> printNotFoundHookApiError()
        }
    }

    /**
     * Loading entry point for an [Application].
     *
     * Load [YukiHookAPI] from [Application.attachBaseContext].
     *
     * See [Use as Hook API](https://highcapable.github.io/YukiHookAPI/en/guide/quick-start#use-as-hook-api)
     *
     * See [Created by Custom Hooker](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
     * @param baseContext attachBaseContext.
     * @param hooker the required, non-empty Hooker array.
     * @throws IllegalStateException if [hooker] is empty.
     */
    fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker) {
        isLoadedFromBaseContext = true
        if (HookApiCategoryHelper.hasAvailableHookApi) {
            if (baseContext != null)
                if (hooker.isNotEmpty()) {
                    printSplashInfo()
                    hooker.forEach { it.assignInstance(packageParam = baseContext.createPackageParam()) }
                } else YLog.innerE("Failed to passing \"encase\" method because your hooker param is empty", isImplicit = true)
        } else printNotFoundHookApiError()
    }

    /** Prints the welcome debug log. */
    internal fun printSplashInfo() {
        if (Configs.isDebug.not() || isShowSplashLogOnceTime.not()) return
        isShowSplashLogOnceTime = false
        YLog.innerD("Welcome to YukiHookAPI $VERSION! Using ${Status.Executor.name} API ${Status.Executor.apiLevel}", isImplicit = true)
    }

    /** Prints an error when no Hook API can be found. */
    private fun printNotFoundHookApiError() =
        YLog.innerE("Could not found any available Hook APIs in current environment! Aborted", isImplicit = true)

    /**
     * Creates the Hook entry object from the base context.
     * @return [PackageParam]
     */
    private fun Context.createPackageParam() =
        PackageParam(PackageParamWrapper(HookEntryType.PACKAGE, packageName, processName, classLoader, applicationInfo))
}