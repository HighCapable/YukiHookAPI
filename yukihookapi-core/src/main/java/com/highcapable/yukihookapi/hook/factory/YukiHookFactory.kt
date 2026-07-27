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
@file:Suppress("unused", "UnusedReceiverParameter", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.factory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Process
import android.view.ContextThemeWrapper
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import androidx.core.net.toUri
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper.ModuleContextThemeWrapper
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Calls [YukiHookAPI.configs] from [IYukiHookXposedInit].
 * @param initiate the configuration block.
 */
inline fun IYukiHookXposedInit.configs(initiate: YukiHookAPI.Configs.() -> Unit) = YukiHookAPI.configs(initiate)

/**
 * Calls [YukiHookAPI.encase] from [IYukiHookXposedInit].
 * @param initiate the Hook block.
 */
fun IYukiHookXposedInit.encase(initiate: PackageParam.() -> Unit) = YukiHookAPI.encase(initiate)

/**
 * Loads [YukiHookAPI] from [IYukiHookXposedInit].
 * @param hooker the required, non-empty Hooker array.
 * @throws IllegalStateException if [hooker] is empty.
 */
fun IYukiHookXposedInit.encase(vararg hooker: YukiBaseHooker) = YukiHookAPI.encase(hooker = hooker)

/**
 * Gets the module preferences object.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [Context.prefs].
 * @return [YukiHookPrefsBridge]
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("prefs()"))
val Context.modulePrefs get() = prefs()

/**
 * Gets the module preferences object.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [Context.prefs].
 * @return [YukiHookPrefsBridge]
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("prefs(name)"))
fun Context.modulePrefs(name: String) = prefs(name)

/**
 * Creates a [YukiHookPrefsBridge] object.
 *
 * This API is available in both module and (Xposed) host environments.
 *
 * To store data in the current host app's private storage from a (Xposed) host environment, use [YukiHookPrefsBridge.native].
 *
 * Without any explicit conditions, the (Xposed) host environment reads module data by default.
 * @param name the custom SharedPreferences storage name, empty by default.
 * @return [YukiHookPrefsBridge]
 */
fun Context.prefs(name: String = "") = YukiHookPrefsBridge.from(context = this).let { if (name.isNotBlank()) it.name(name) else it }

/**
 * Gets a [YukiHookDataChannel] object.
 *
 * - This API is available only in the module environment and has no effect elsewhere.
 * @param packageName the target host app package name.
 * @return [YukiHookDataChannel.NameSpace]
 */
fun Context.dataChannel(packageName: String) = YukiHookDataChannel.instance().nameSpace(context = this, packageName)

/**
 * Gets the current process name.
 * @return [String]
 */
val Context.processName
    get() = runCatching {
        BufferedReader(FileReader(File("/proc/${Process.myPid()}/cmdline"))).let { buff ->
            buff.readLine().trim().let {
                buff.close()
                it
            }
        }
    }.getOrNull() ?: packageName ?: ""

/**
 * Injects the current Xposed module's resources into the host app [Context].
 *
 * After a successful injection, APIs such as [ImageView.setImageResource] or [Resources.getString] can load the current module's resource IDs directly.
 *
 * Injected resources are scoped to the current [Context], so call this method for every host [Context] that needs them.
 *
 * See [Inject Module App's Resources](https://highcapable.github.io/YukiHookAPI/en/special-features/host-inject#inject-module-app-s-resources)
 *
 * - This API is available only in a (Xposed) host environment. Elsewhere it has no effect and prints a warning.
 */
fun Context.injectModuleAppResources() = resources?.injectModuleAppResources()

/**
 * Injects the current Xposed module's resources directly into the specified host app [Resources].
 *
 * After a successful injection, APIs such as [ImageView.setImageResource] or [Resources.getString] can load the current module's resource IDs directly.
 *
 * Injected resources are scoped to the current [Resources], so call this method for every host [Resources] instance that needs them.
 *
 * See [Inject Module App's Resources](https://highcapable.github.io/YukiHookAPI/en/special-features/host-inject#inject-module-app-s-resources)
 *
 * - This API is available only in a (Xposed) host environment. Elsewhere it has no effect and prints a warning.
 */
fun Resources.injectModuleAppResources() = AppParasitics.injectModuleAppResources(hostResources = this)

/**
 * Registers the current Xposed module's [Activity] in the host app.
 *
 * After registration, [Context.startActivity] can launch an [Activity] that is not registered in the host app.
 *
 * When an unregistered [Activity] starts in the host app, this API automatically calls [injectModuleAppResources] to inject module resources.
 *
 * - Each [Activity] that starts in the host app must implement [ModuleActivity].
 *
 * See [Register Module App's Activity](https://highcapable.github.io/YukiHookAPI/en/special-features/host-inject#register-module-app-s-activity)
 *
 * - This API is available only in a (Xposed) host environment. Elsewhere it has no effect and prints a warning.
 *
 * - Requires Android 7.0 (API 24) or later.
 * @param proxy the proxy [Activity], which must exist in the host AndroidManifest. Omit it to use the default [Activity].
 */
@RequiresApi(AndroidVersion.N)
fun Context.registerModuleAppActivities(proxy: Any? = null) = AppParasitics.registerModuleAppActivities(context = this, proxy)

/**
 * Creates a [ContextThemeWrapper] proxy that applies the current Xposed module's theme resources.
 *
 * In a host app, this API automatically calls [injectModuleAppResources] to inject the current module's resources.
 *
 * - If this API throws [ClassCastException] in a host app, set a new [configuration] manually.
 *
 * See [Create ContextThemeWrapper Proxy](https://highcapable.github.io/YukiHookAPI/en/special-features/host-inject#create-contextthemewrapper-proxy)
 * @param theme the theme resource ID.
 * @param configuration the [Configuration] to use, null by default.
 * @return [ModuleContextThemeWrapper]
 */
fun Context.applyModuleTheme(@StyleRes theme: Int, configuration: Configuration? = null) =
    ModuleContextThemeWrapper.wrapper(baseContext = this, theme, configuration)

/**
 * Checks only whether the module is active in TaiChi or Wuji.
 *
 * This implementation wraps and improves the example from the official TaiChi documentation.
 * @return [Boolean] whether the module is active.
 */
internal val Context.isTaiChiModuleActive: Boolean
    get() {
        /**
         * Gets whether the module is active.
         * @return [Boolean] or null.
         */
        fun isModuleActive() =
            contentResolver?.call("content://me.weishu.exposed.CP/".toUri(), "active", null, null)?.getBoolean("active", false)
        return runCatching { isModuleActive() }.getOrNull() ?: runCatching {
            startActivity(Intent("me.weishu.exp.ACTION_ACTIVE").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            isModuleActive()
        }.getOrNull() ?: false
    }