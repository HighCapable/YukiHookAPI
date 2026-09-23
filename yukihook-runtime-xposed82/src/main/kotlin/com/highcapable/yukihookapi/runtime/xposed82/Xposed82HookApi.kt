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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.xposed82

import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.hasClass
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.xposed82.adapter.ChainInvocationAdapter
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XCallback
import java.lang.reflect.Member

/**
 * Host-only native calls, never initialized by the module application.
 */
internal object Xposed82HookApi {

    private val chainHooks = mutableMapOf<Member, Pair<ChainInvocationAdapter, HookerBridge>>()

    val isAvailable get() = runCatching { XposedBridge.getXposedVersion(); true }.getOrDefault(false)
    val apiVersion get() = runCatching { XposedBridge.getXposedVersion() }.getOrDefault(-1)
    val frameworkName get() = runCatching {
        val loader = classOf<XposedBridge>().classLoader

        when {
            loader?.hasClass("me.weishu.exposed.ExposedBridge") == true -> "TaiChi"
            loader?.hasClass("com.bug.load.BugLoad") == true -> "BugXposed"
            else -> XposedBridge::class.resolve()
                .optional(silent = true)
                .firstFieldOrNull { name = "TAG" }
                ?.get<String>()
                ?.replace("Bridge", "")
                ?.replace("-", "")
                ?.trim()
                ?.ifBlank { "unknown" }
                ?: "unknown"
        }
    }.getOrDefault("invalid")

    fun hookMember(member: Member, priority: Int, hooker: HookerBridge) = when (hooker) {
        is ChainHookerBridge -> hookChainMember(member, priority, hooker)
        is ClassicHookerBridge -> hookClassicMember(member, priority, hooker)
        else -> throw UnsupportedOperationException("Unsupported hooker type for Xposed API 82: ${hooker.javaClass.name}.")
    }

    private fun hookChainMember(member: Member, priority: Int, hooker: ChainHookerBridge): HookerBridge = synchronized(chainHooks) {
        val (adapter, nativeHook) = chainHooks.getOrPut(member) {
            val adapter = ChainInvocationAdapter(member) { instance, args ->
                Xposed82ExecutableInvoker.invokeOriginal(member, instance, args)
            }

            // API 82 cannot resume native callbacks from proceed. Keep ordinary hooks outside
            // the local chain; native hooks ordered after this entry can still be skipped.
            adapter to hookClassicMember(member, XCallback.PRIORITY_LOWEST, adapter)
        }
        val handle = adapter.hookMember(priority, hooker)

        object : HookerBridge by handle {
            override fun remove() = synchronized(chainHooks) {
                if (chainHooks[member]?.first !== adapter) return@synchronized

                handle.remove()
                if (adapter.isEmpty) {
                    nativeHook.remove()
                    chainHooks.remove(member)
                }
            }
        }
    }

    private fun hookClassicMember(member: Member, priority: Int, hooker: ClassicHookerBridge): HookerBridge {
        val handle = XposedBridge.hookMethod(member, object : XC_MethodHook(priority) {
            override fun beforeHookedMethod(param: MethodHookParam) = hooker.beforeHookedMember(param.toBridge())
            override fun afterHookedMethod(param: MethodHookParam) = hooker.afterHookedMember(param.toBridge())
        })

        return object : HookerBridge by hooker {
            override fun remove() = handle.unhook()
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun log(priority: Int, tag: String, message: String, throwable: Throwable?) {
        XposedBridge.log(message)
        throwable?.let { XposedBridge.log(it) }
    }

    private fun XC_MethodHook.MethodHookParam.toBridge() = object : ClassicHookerBridge.Param {
        override val member: Member get() = this@toBridge.method
        override val instance: Any? get() = this@toBridge.thisObject
        override val args: Array<Any?> get() = this@toBridge.args
        override var result: Any? get() = this@toBridge.result
            set(value) {
                this@toBridge.result = value
            }
        override var throwable: Throwable? get() = this@toBridge.throwable
            set(value) {
                this@toBridge.throwable = value
            }
    }
}