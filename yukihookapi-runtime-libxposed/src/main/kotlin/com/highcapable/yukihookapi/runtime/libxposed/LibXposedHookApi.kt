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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.libxposed

import android.util.Log
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.libxposed.adapter.ClassicInvocationAdapter
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Executable
import java.lang.reflect.Member

/**
 * All native hook API calls are compiled here against API 102.
 */
internal class LibXposedHookApi(private val api: XposedInterface) {

    fun attach() {
        YukiHookBridge.hostFrameworkName = api.frameworkName
        YukiHookBridge.hostApiLevel = api.apiVersion
        YukiHookBridge.hostVersion = api.frameworkVersion
        YukiHookBridge.hostVersionCode = api.frameworkVersionCode.coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
        YukiHookBridge.executableInvokerBridge = LibXposedExecutableInvoker(api)
        YukiHookBridge.deoptimizeCallback = ::deoptimize
        YukiHookBridge.hookCallback = ::hook
        YukiHookBridge.loggerCallback = { priority, tag, message, throwable ->
            api.log(priority, tag, message, throwable)
        }

        LibXposedPreferences.attach(api)
        LibXposedSharedFiles.attach(api)
    }

    private fun deoptimize(member: Member): Boolean {
        require(member is Executable) { "Only methods and constructors can be deoptimized." }
        return api.deoptimize(member)
    }

    private fun hook(target: HookPoint, priority: Int, hooker: HookerBridge): HookerBridge {
        val nativeHooker = when (hooker) {
            is ChainHookerBridge -> XposedInterface.Hooker { chain -> hooker.intercept(chain.toBridge()) }
            is ClassicHookerBridge -> XposedInterface.Hooker { chain ->
                ClassicInvocationAdapter.from(
                    member = chain.executable,
                    instance = chain.thisObject,
                    args = chain.args.toTypedArray()
                ) { failure ->
                    api.log(Log.ERROR, "YukiHookAPI", "An exception occurred in a classic hook callback.", failure)
                }.intercept(hooker) { chain.proceed(it) }
            }
            else -> throw UnsupportedOperationException("Unsupported hooker type for libxposed: ${hooker.javaClass.name}.")
        }
        val builder = when (target) {
            is HookPoint.Executable -> api.hook(target.value as Executable)
            is HookPoint.Initializer -> api.hookClassInitializer(target.type)
        }
        val handle = builder
            .setPriority(priority)
            // Classic hookers apply their own failure policy, chain hookers must preserve
            // the interceptor's result or exception without automatic recovery.
            .setExceptionMode(XposedInterface.ExceptionMode.PASSTHROUGH)
            .intercept(nativeHooker)

        return object : HookerBridge by hooker {
            override fun remove() = handle.unhook()
        }
    }

    private fun XposedInterface.Chain.toBridge() = object : ChainHookerBridge.Chain {
        override val member get() = executable
        override val instance: Any? get() = thisObject
        override val args: List<Any?> get() = this@toBridge.args
        override fun getArg(index: Int): Any? = this@toBridge.getArg(index)
        override fun proceed(): Any? = this@toBridge.proceed()
        override fun proceed(args: Array<out Any?>): Any? = this@toBridge.proceed(args)
        override fun proceedWith(instance: Any): Any? = this@toBridge.proceedWith(instance)
        override fun proceedWith(instance: Any, args: Array<out Any?>): Any? = this@toBridge.proceedWith(instance, args)
    }
}