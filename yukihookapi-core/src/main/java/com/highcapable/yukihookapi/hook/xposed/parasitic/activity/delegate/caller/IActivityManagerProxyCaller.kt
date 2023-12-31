/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
import com.highcapable.yukihookapi.hook.factory.buildOf
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.extends
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 代理当前 [ActivityManager] 调用类
 */
internal object IActivityManagerProxyCaller {

    /**
     * 获取当前使用的 [ClassLoader]
     * @return [ClassLoader]
     */
    internal val currentClassLoader get() = AppParasitics.baseClassLoader

    /**
     * 调用代理的 [InvocationHandler.invoke] 方法
     * @param baseInstance 原始实例
     * @param method 被调用方法
     * @param args 被调用方法参数
     * @return [Any] or null
     */
    internal fun callInvoke(baseInstance: Any, method: Method?, args: Array<Any>?): Any? {
        if (method?.name == "startActivity") args?.indexOfFirst { it is Intent }?.also { index ->
            val argsInstance = (args[index] as? Intent) ?: return@also
            val component = argsInstance.component
            /**
             * 使用宿主包名判断当前启动的 [Activity] 位于当前宿主
             * 使用默认的 [ClassLoader] 判断当前 [Class] 处于模块中
             */
            if (component != null &&
                component.packageName == AppParasitics.currentPackageName &&
                component.className.hasClass()
            ) args[index] = Intent().apply {
                /**
                 * 验证类名是否存在
                 * @return [String] or null
                 */
                fun String.verify() = if (hasClass(AppParasitics.hostApplication?.classLoader)) this else null
                setClassName(component.packageName, component.className.toClassOrNull()?.runCatching {
                    when {
                        this extends classOf<ModuleAppActivity>() -> buildOf<ModuleAppActivity>()?.proxyClassName?.verify()
                        this extends classOf<ModuleAppCompatActivity>() -> buildOf<ModuleAppCompatActivity>()?.proxyClassName?.verify()
                        else -> null
                    }
                }?.getOrNull() ?: ActivityProxyConfig.proxyClassName)
                putExtra(ActivityProxyConfig.proxyIntentName, argsInstance)
            }
        }
        return method?.invoke(baseInstance, *(args ?: emptyArray()))
    }
}