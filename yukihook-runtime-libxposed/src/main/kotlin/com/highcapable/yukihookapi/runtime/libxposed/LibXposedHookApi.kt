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
package com.highcapable.yukihookapi.runtime.libxposed

import android.util.Log
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.HookRegistrationBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import com.highcapable.yukihookapi.runtime.libxposed.adapter.ClassicInvocationAdapter
import com.highcapable.yukihookapi.runtime.libxposed.generated.YukiHookProperties
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Executable
import java.lang.reflect.Member

/**
 * All native hook API calls are compiled here.
 */
internal class LibXposedHookApi(
    private val api: XposedInterface,
    private val moduleSession: LibXposedModuleSession? = null
) : HookRegistrationBridge {

    private companion object {
        const val HOOK_REPLACEMENT_API_VERSION = XposedInterface.API_102
    }

    override val defaultPriority get() = XposedInterface.PRIORITY_DEFAULT
    override val lowestPriority get() = XposedInterface.PRIORITY_LOWEST
    override val highestPriority get() = XposedInterface.PRIORITY_HIGHEST

    fun attach(registrations: HookRegistrationBridge? = null) {
        YukiHookBridge.attach(
            YukiHookBridge.ApiSpec(
                apiVersion = api.apiVersion,
                frameworkName = api.frameworkName,
                frameworkVersion = api.frameworkVersion,
                frameworkVersionCode = api.frameworkVersionCode,
                executableInvokerBridge = LibXposedExecutableInvoker(api),
                hookRegistrationBridge = registrations ?: this,
                loggerCallback = api::log
            )
        )

        LibXposedPreferences.attach(api)
        LibXposedSharedFiles.attach(api)
    }

    override fun deoptimize(member: Member): Boolean {
        require(member is Executable) {
            "Only methods and constructors can be deoptimized."
        }

        return api.deoptimize(member)
    }

    override fun hook(target: HookPoint, priority: Int, hooker: HookerBridge, id: String?): HookerBridge {
        val nativeHooker = createNativeHooker(hooker)
        val builder = when (target) {
            is HookPoint.Executable -> api.hook(target.value as Executable)
            is HookPoint.Initializer -> api.hookClassInitializer(target.type)
        }
        val handle = builder
            .setPriority(priority)
            .apply { if (id != null) setId(id) }
            // Classic hookers apply their own failure policy, chain hookers must preserve
            // the interceptor's result or exception without automatic recovery.
            .setExceptionMode(XposedInterface.ExceptionMode.PASSTHROUGH)
            .intercept(use(nativeHooker))

        return RegistrationHandle(handle)
    }

    override fun replace(handle: HookerBridge, replacement: HookerBridge): HookerBridge {
        require(handle is RegistrationHandle) {
            "The hook handle was not created by the libxposed runtime."
        }

        // The runtime supports replacement, only an older framework lacks it, so keep the current hook instead of failing.
        if (api.apiVersion < HOOK_REPLACEMENT_API_VERSION) {
            api.log(
                Log.WARN, YukiHookProperties.PROJECT_YUKIHOOK_RUNTIME_NAME,
                "Atomic hook replacement is unavailable in Xposed API ${api.apiVersion}, the hook keeps its current callback",
                null
            )
            return handle
        }

        return handle.replace(replacement)
    }

    private fun createNativeHooker(hooker: HookerBridge) = when (hooker) {
        is ChainHookerBridge -> XposedInterface.Hooker { chain -> hooker.intercept(chain.toBridge()) }
        is ClassicHookerBridge -> XposedInterface.Hooker { chain ->
            ClassicInvocationAdapter.from(
                member = chain.executable,
                instance = chain.thisObject,
                args = chain.args.toTypedArray()
            ) { failure ->
                api.log(
                    Log.ERROR,
                    YukiHookProperties.PROJECT_YUKIHOOK_RUNTIME_NAME,
                    "An exception occurred in a classic hook callback",
                    failure
                )
            }.intercept(hooker) { chain.proceed(it) }
        }
        else -> throw UnsupportedOperationException("Unsupported hooker type for libxposed API: ${hooker.javaClass.name}")
    }

    private fun use(hooker: XposedInterface.Hooker): XposedInterface.Hooker {
        val session = moduleSession ?: return hooker

        return XposedInterface.Hooker { chain ->
            if (!session.enter()) return@Hooker chain.proceed()

            try {
                hooker.intercept(chain)
            } finally {
                session.leave()
            }
        }
    }

    private inner class RegistrationHandle(
        private val nativeHandle: XposedInterface.HookHandle
    ) : HookerBridge {

        private val lock = Any()

        override fun remove() = synchronized(lock) {
            nativeHandle.unhook()
        }

        fun replace(replacement: HookerBridge): HookerBridge = synchronized(lock) {
            RegistrationHandle(nativeHandle.replaceHook(use(createNativeHooker(replacement))))
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