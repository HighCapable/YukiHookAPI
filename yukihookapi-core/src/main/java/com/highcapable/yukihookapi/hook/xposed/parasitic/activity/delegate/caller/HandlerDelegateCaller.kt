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
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Message
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.lazyClass
import com.highcapable.yukihookapi.hook.core.api.reflect.AndroidHiddenApiBypassResolver
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config.ActivityProxyConfig

/**
 * 代理当前 [Handler.Callback] 调用类
 */
internal object HandlerDelegateCaller {

    /** 启动 [Activity] */
    private const val LAUNCH_ACTIVITY = 100

    /** 执行事务处理 */
    private const val EXECUTE_TRANSACTION = 159

    private val ActivityThreadClass by lazyClass("android.app.ActivityThread")
    private val ClientTransactionClass by lazyClass("android.app.servertransaction.ClientTransaction")

    private val mExtrasResolver by lazy {
        Intent::class.resolve().optional().firstFieldOrNull { name = "mExtras" }
    }

    /**
     * 调用代理的 [Handler.Callback.handleMessage] 方法
     * @param baseInstance 原始实例
     * @param msg 当前消息实例
     * @return [Boolean]
     */
    internal fun callHandleMessage(baseInstance: Handler.Callback?, msg: Message): Boolean {
        when (msg.what) {
            LAUNCH_ACTIVITY -> {
                val intentResolver = msg.obj.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional()
                    .firstFieldOrNull { name = "intent" }
                val intent = intentResolver?.get<Intent>()
                val mExtras = mExtrasResolver?.copy()?.of(intent)?.getQuietly<Bundle>()
                mExtras?.classLoader = AppParasitics.currentApplication?.classLoader
                @Suppress("DEPRECATION")
                if (intent?.hasExtra(ActivityProxyConfig.proxyIntentName) == true)
                    intentResolver.set(intent.getParcelableExtra(ActivityProxyConfig.proxyIntentName))
            }
            EXECUTE_TRANSACTION -> {
                val callbacks = ClientTransactionClass.resolve()
                    .processor(AndroidHiddenApiBypassResolver.get())
                    .optional()
                    .firstMethodOrNull {
                        name = "getCallbacks"
                    }?.of(msg.obj)
                    ?.invokeQuietly<List<Any>>()
                    ?.takeIf { it.isNotEmpty() }
                callbacks?.filter { it.javaClass.name.contains("LaunchActivityItem") }?.forEach { item ->
                    val itemResolver = item.resolve().optional()
                        .firstFieldOrNull { name = "mIntent" }
                    val intent = itemResolver?.get<Intent>()
                    val mExtras = mExtrasResolver?.copy()?.of(intent)?.getQuietly<Bundle>()
                    mExtras?.classLoader = AppParasitics.currentApplication?.classLoader
                    if (intent?.hasExtra(ActivityProxyConfig.proxyIntentName) == true) {
                        @Suppress("DEPRECATION")
                        val subIntent = intent.getParcelableExtra<Intent>(ActivityProxyConfig.proxyIntentName)
                        if (Build.VERSION.SDK_INT >= 31) {
                            val currentActivityThread = ActivityThreadClass.resolve()
                                .processor(AndroidHiddenApiBypassResolver.get())
                                .optional()
                                .firstMethodOrNull { name = "currentActivityThread" }
                                ?.invoke()
                            val token = msg.obj.resolve()
                                .processor(AndroidHiddenApiBypassResolver.get())
                                .optional()
                                .firstMethodOrNull { name = "getActivityToken" }
                                ?.invokeQuietly()
                            val launchingActivity = currentActivityThread?.resolve()
                                ?.processor(AndroidHiddenApiBypassResolver.get())
                                ?.optional()
                                ?.firstMethodOrNull {
                                    name = "getLaunchingActivity"
                                    parameters(IBinder::class)
                                }?.invokeQuietly(token)
                            launchingActivity?.resolve()
                                ?.processor(AndroidHiddenApiBypassResolver.get())
                                ?.optional()
                                ?.firstFieldOrNull { name = "intent" }
                                ?.set(subIntent)
                        }; itemResolver.set(subIntent)
                    }
                }
            }
        }
        return baseInstance?.handleMessage(msg) ?: false
    }
}