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
 * This file is created by fankes on 2023/4/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.caller

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import com.highcapable.kavaref.extension.createInstanceAsTypeOrNull
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.kavaref.extension.isSubclassOf
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * Routes calls for the current [ActivityManager] proxy.
 */
internal object IActivityManagerProxyCaller {

    /**
     * Gets the current [ClassLoader].
     * @return [ClassLoader]
     */
    internal val currentClassLoader get() = AppParasitics.baseClassLoader

    /**
     * Calls the proxied [InvocationHandler.invoke] method.
     * @param baseInstance the original instance.
     * @param method the invoked method.
     * @param args the invoked method arguments.
     * @return [Any] or null.
     */
    internal fun callInvoke(baseInstance: Any, method: Method?, args: Array<Any>?): Any? {
        if (method?.name == "startActivity") args?.indexOfFirst { it is Intent }?.also { index ->
            val argsInstance = (args[index] as? Intent) ?: return@also
            val component = argsInstance.component
            // Uses the host package name to determine whether the launched [Activity] belongs to the current host.
            // Uses the default [ClassLoader] to determine whether the current [Class] belongs to the module.
            if (component != null &&
                component.packageName == AppParasitics.currentPackageName &&
                javaClass.classLoader?.hasClass(component.className) == true
            ) args[index] = Intent().apply {
                /**
                 * Verifies that the class name exists.
                 * @return [String] or null.
                 */
                fun String.verify() = if (AppParasitics.hostApplication?.classLoader?.hasClass(this) == true) this else null
                setClassName(component.packageName, component.className.toClassOrNull()?.runCatching {
                    when {
                        this isSubclassOf ModuleActivity::class ->
                            createInstanceAsTypeOrNull<ModuleActivity>()?.proxyClassName?.verify()
                        else -> null
                    }
                }?.getOrNull() ?: ActivityProxyConfig.proxyClassName)
                putExtra(ActivityProxyConfig.proxyIntentName, argsInstance)
            }
        }
        return method?.invoke(baseInstance, *(args ?: emptyArray()))
    }
}