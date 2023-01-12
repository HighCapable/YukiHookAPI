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
 * This file is Created by fankes on 2022/8/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate

import android.app.ActivityManager
import android.content.Intent
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

/**
 * 代理当前 [ActivityManager]
 * @param baseInstance 原始实例
 */
internal class IActivityManagerProxy private constructor(private val baseInstance: Any) : InvocationHandler {

    internal companion object {

        /**
         * 创建 [IActivityManagerProxy] 代理
         * @param clazz 代理的目标 [Class]
         * @param instance 代理的目标实例
         * @return [Any] 代理包装后的实例
         */
        internal fun wrapper(clazz: Class<*>?, instance: Any) =
            Proxy.newProxyInstance(AppParasitics.baseClassLoader, arrayOf(clazz), IActivityManagerProxy(instance))
    }

    override fun invoke(proxy: Any?, method: Method?, args: Array<Any>?): Any? {
        if (method?.name == "startActivity") args?.indexOfFirst { it is Intent }?.also { index ->
            val argsInstance = (args[index] as? Intent) ?: return@also
            val component = argsInstance.component
            if (component != null &&
                component.packageName == AppParasitics.currentPackageName &&
                component.className.startsWith(YukiXposedModule.modulePackageName)
            ) args[index] = Intent().apply {
                setClassName(component.packageName, ActivityProxyConfig.proxyClassName)
                putExtra(ActivityProxyConfig.proxyIntentName, argsInstance)
            }
        }
        return method?.invoke(baseInstance, *(args ?: emptyArray()))
    }
}