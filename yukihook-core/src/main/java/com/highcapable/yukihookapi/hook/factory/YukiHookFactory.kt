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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "UnusedReceiverParameter", "DEPRECATION")

package com.highcapable.yukihookapi.hook.factory

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Process
import android.view.ContextThemeWrapper
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.net.toUri
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper.ModuleContextThemeWrapper
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import java.io.File

/**
 * Calls [YukiHook.configure] from [YukiHookXposedModule].
 * @receiver [YukiHookXposedModule]
 * @param block the configuration block.
 */
inline fun YukiHookXposedModule.configure(block: YukiHook.Config.() -> Unit) = YukiHook.configure(block)

/**
 * Calls [YukiHook.encase] from [YukiHookXposedModule].
 * @receiver [YukiHookXposedModule]
 * @param block the param block.
 */
fun YukiHookXposedModule.encase(block: PackageParam.() -> Unit) = YukiHook.encase(block)

/**
 * Loads [YukiHook] from [YukiHookXposedModule].
 * @receiver [YukiHookXposedModule]
 * @param hookers the hookers.
 * @throws IllegalStateException if [hookers] is empty.
 */
fun YukiHookXposedModule.encase(vararg hookers: YukiBaseHooker) = YukiHook.encase(hookers = hookers)

/**
 * Creates a [YukiHookPreferences] object.
 *
 * This API is available in both module and host environments.
 *
 * To store data in the current host's private storage from a host environment, use [YukiHookPreferences.native].
 * Without any explicit conditions, the host environment reads module data by default.
 * @receiver the current context.
 * @param name the custom shared preferences storage name, empty by default.
 * @return [YukiHookPreferences]
 */
fun Context.preferences(name: String = "") = YukiHookPreferences.from(this).let { if (name.isNotBlank()) it.name(name) else it }

/**
 * Gets a [YukiHookDataChannel] object.
 *
 * This API is available only in the module environment and has no effect elsewhere.
 * @receiver the current context.
 * @param packageName the target host package name.
 * @return [YukiHookDataChannel.Namespace]
 */
fun Context.dataChannel(packageName: String) = YukiHookDataChannel.get().namespace(this, packageName)

/**
 * Gets the current process name.
 * @receiver the current context.
 * @return [String]
 */
val Context.processName
    get() = (if (AndroidVersion.isAtLeast(AndroidVersion.P))
        Application.getProcessName() ?: ""
    else runCatching {
        File("/proc/${Process.myPid()}/cmdline").readText()
    }.getOrNull())
        ?.takeIf { it.isNotBlank() }
        // Removes transport padding and trailing replacement characters from a process name.
        ?.substringBefore('\u0000')
        ?.trimEnd('\uFFFD')
        ?: packageName ?: ""

/**
 * Injects the current Xposed module's resources into the host [Context].
 *
 * After a successful injection, APIs such as [ImageView.setImageResource] or [Resources.getString]
 * can load the current module's resource IDs directly.
 * Injected resources are scoped to the current [Context], so call this function for every host [Context] that needs them.
 * Injection completes synchronously.
 *
 * This API is available only in a host environment. Elsewhere, it has no effect and logs a warning.
 * On Android 11 (API 30) or later, call this function on the main thread.
 * @see Resources.injectModuleResources
 * @receiver the current context.
 */
@MainThread
fun Context.injectModuleResources() = resources?.injectModuleResources()

/**
 * Injects the current Xposed module's resources directly into the specified host [Resources].
 *
 * After a successful injection, APIs such as [ImageView.setImageResource] or [Resources.getString]
 * can load the current module's resource IDs directly.
 * Injected resources are scoped to the current [Resources], so call this function for every host [Resources] instance that needs them.
 * Injection completes synchronously.
 *
 * This API is available only in a host environment. Elsewhere, it has no effect and logs a warning.
 * On Android 11 (API 30) or later, call this function on the main thread.
 * @see Context.injectModuleResources
 * @receiver the current resources.
 */
@MainThread
fun Resources.injectModuleResources() = AppParasitism.injectModuleResources(this)

/**
 * Registers the current Xposed module's [Activity] in the host.
 *
 * After registration, [Context.startActivity] can launch an [Activity] that is not registered in the host.
 * When an unregistered [Activity] starts in the host, this API automatically calls [Context.injectModuleResources] to inject module resources.
 *
 * If your module may use [YukiHookLibXposedEntry.HotReload], it is recommended to use [PackageParam.registerModuleActivitiesWhenReady]
 * to register the module's [Activity], and avoid registering unstable callback events in the host lifecycle functions.
 *
 * Each [Activity] that starts in the host must implement [ModuleActivity].
 * This API is available only in a host environment. Elsewhere, it has no effect and logs a warning.
 *
 * A pending [PackageParam.registerModuleActivitiesWhenReady] request takes precedence over this direct call.
 *
 * - Requires Android 7.0 (API 24) or later.
 * @see PackageParam.registerModuleActivitiesWhenReady
 * @receiver the current context.
 * @param proxy the proxy [Activity], which must exist in the host's AndroidManifest. Omit it to use the default [Activity].
 */
@RequiresApi(AndroidVersion.N)
fun Context.registerModuleActivities(proxy: Any? = null) = AppParasitism.registerModuleActivities(this, proxy)

/**
 * Creates a [ContextThemeWrapper] proxy that applies the current Xposed module's theme resources.
 *
 * In a host, this API automatically calls [injectModuleResources] to inject the current module's resources.
 *
 * If this API throws [ClassCastException] in a host, set a new [configuration] manually.
 * @receiver the current context.
 * @param theme the theme resource ID.
 * @param configuration the [Configuration] to use, null by default.
 * @return [ModuleContextThemeWrapper]
 */
fun Context.applyModuleTheme(@StyleRes theme: Int, configuration: Configuration? = null) =
    ModuleContextThemeWrapper.from(this, theme, configuration)

// region Deprecated functions for backward compatibility

/**
 * This function does nothing and does not invoke [block].
 *
 * - Use [YukiHookXposedModule.configure] instead.
 */
@Deprecated(
    message = "Use YukiHookXposedModule.configure instead.",
    replaceWith = ReplaceWith("configure(block)")
)
inline fun IYukiHookXposedInit.configs(block: YukiHookAPI.Configs.() -> Unit) = Unit

/**
 * This function does nothing and does not invoke [block].
 *
 * - Use [YukiHookXposedModule.encase] instead.
 */
@Deprecated(message = "Use YukiHookXposedModule.encase instead.")
fun IYukiHookXposedInit.encase(block: PackageParam.() -> Unit) = Unit

/**
 * This function does nothing and does not invoke [hookers].
 *
 * - Use [YukiHookXposedModule.encase] instead.
 */
@Deprecated(message = "Use YukiHookXposedModule.encase instead.")
fun IYukiHookXposedInit.encase(vararg hookers: YukiBaseHooker) = Unit

/**
 * This function does nothing and does not invoke [block].
 *
 * - Use the selected runtime's `registerFrameworkEvents` extension from [YukiHookXposedModule.onInit] instead.
 */
@Deprecated(message = "This API has no effect. Use the runtime's registerFrameworkEvents in onInit instead.")
inline fun YukiHookXposedModule.events(block: YukiXposedEvent.() -> Unit) = Unit

/**
 * This function does nothing and does not invoke [block].
 *
 * - Use the selected runtime's `registerFrameworkEvents` extension from [IYukiHookXposedInit.onInit] instead.
 */
@Deprecated(message = "This API has no effect. Use the runtime's registerFrameworkEvents in onInit instead.")
inline fun IYukiHookXposedInit.events(block: YukiXposedEvent.() -> Unit) = Unit

/** Creates a [YukiHookPreferences] object. */
@Deprecated(message = "Use preferences instead.", replaceWith = ReplaceWith("preferences(name)"))
fun Context.prefs(name: String = "") = preferences(name)

/** Registers the current Xposed module's [Activity] in the host. */
@Deprecated(message = "Use registerModuleActivities instead.", replaceWith = ReplaceWith("registerModuleActivities(proxy)"))
@RequiresApi(AndroidVersion.N)
fun Context.registerModuleAppActivities(proxy: Any? = null) = registerModuleActivities(proxy)

/** Injects the current Xposed module's resources into the host [Context]. */
@Deprecated(message = "Use injectModuleResources instead.", replaceWith = ReplaceWith("injectModuleResources()"))
@MainThread
fun Context.injectModuleAppResources() = injectModuleResources()

/** Injects the current Xposed module's resources directly into the specified host [Resources]. */
@Deprecated(message = "Use injectModuleResources instead.", replaceWith = ReplaceWith("injectModuleResources()"))
@MainThread
fun Resources.injectModuleAppResources() = injectModuleResources()

// endregion

/**
 * Checks only whether the module is active in TaiChi/Wuji.
 *
 * This implementation wraps and improves the example from the official TaiChi documentation.
 * @receiver the current context.
 * @return [Boolean] whether the module is active.
 */
internal val Context.isTaiChiModuleActive: Boolean get() {
    /**
     * Gets whether the module is active.
     * @return [Boolean] or null.
     */
    fun isModuleActive() = contentResolver?.call(
        "content://me.weishu.exposed.CP/".toUri(),
        "active",
        null, null
    )?.getBoolean("active", false)

    return runCatching { isModuleActive() }.getOrNull() ?: runCatching {
        startActivity(Intent("me.weishu.exp.ACTION_ACTIVE").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
        isModuleActive()
    }.getOrNull() ?: false
}