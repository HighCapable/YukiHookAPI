/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2023/4/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.caller

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import com.highcapable.yukihookapi.annotation.YukiGenerateApi
import com.highcapable.yukihookapi.hook.factory.*
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 代理当前 [ActivityManager] 调用类
 *
 * - ❗装载代码将自动生成 - 请勿手动调用
 */
@YukiGenerateApi
object IActivityManagerProxyCaller {

    /**
     * 获取当前使用的 [ClassLoader]
     *
     * - ❗装载代码将自动生成 - 请勿手动调用
     * @return [ClassLoader]
     */
    @YukiGenerateApi
    val currentClassLoader get() = AppParasitics.baseClassLoader

    /**
     * 调用代理的 [InvocationHandler.invoke] 方法
     *
     * - ❗装载代码将自动生成 - 请勿手动调用
     * @param baseInstance 原始实例
     * @param method 被调用方法
     * @param args 被调用方法参数
     * @return [Any] or null
     */
    @YukiGenerateApi
    fun callInvoke(baseInstance: Any, method: Method?, args: Array<Any>?): Any? {
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