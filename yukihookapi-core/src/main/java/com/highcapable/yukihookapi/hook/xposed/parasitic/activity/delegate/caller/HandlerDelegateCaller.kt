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
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Message
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.android.ActivityThreadClass
import com.highcapable.yukihookapi.hook.type.android.ClientTransactionClass
import com.highcapable.yukihookapi.hook.type.android.IBinderClass
import com.highcapable.yukihookapi.hook.type.android.IntentClass
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

    /**
     * 调用代理的 [Handler.Callback.handleMessage] 方法
     * @param baseInstance 原始实例
     * @param msg 当前消息实例
     * @return [Boolean]
     */
    internal fun callHandleMessage(baseInstance: Handler.Callback?, msg: Message): Boolean {
        when (msg.what) {
            LAUNCH_ACTIVITY -> runCatching {
                msg.obj.current(ignored = true).field { name = "intent" }.apply {
                    cast<Intent?>()?.also { intent ->
                        IntentClass.field { name = "mExtras" }.ignored().get(intent).cast<Bundle?>()
                            ?.classLoader = AppParasitics.currentApplication?.classLoader
                        @Suppress("DEPRECATION")
                        if (intent.hasExtra(ActivityProxyConfig.proxyIntentName))
                            set(intent.getParcelableExtra(ActivityProxyConfig.proxyIntentName))
                    }
                }
            }.onFailure { YLog.innerE("Activity Proxy got an exception in msg.what [$LAUNCH_ACTIVITY]", it) }
            EXECUTE_TRANSACTION -> msg.obj?.runCatching client@{
                ClientTransactionClass.method { name = "getCallbacks" }.ignored().get(this).list<Any?>().takeIf { it.isNotEmpty() }
                    ?.forEach { item ->
                        item?.current(ignored = true)?.takeIf { it.name.contains("LaunchActivityItem") }?.field { name = "mIntent" }
                            ?.apply {
                                cast<Intent?>()?.also { intent ->
                                    IntentClass.field { name = "mExtras" }.ignored().get(intent).cast<Bundle?>()
                                        ?.classLoader = AppParasitics.currentApplication?.classLoader
                                    @Suppress("DEPRECATION")
                                    if (intent.hasExtra(ActivityProxyConfig.proxyIntentName))
                                        intent.getParcelableExtra<Intent>(ActivityProxyConfig.proxyIntentName).also { subIntent ->
                                            if (Build.VERSION.SDK_INT >= 31)
                                                ActivityThreadClass.method { name = "currentActivityThread" }.ignored().get().call()
                                                    ?.current(ignored = true)?.method {
                                                        name = "getLaunchingActivity"
                                                        param(IBinderClass)
                                                    }?.call(this@client.current(ignored = true).method { name = "getActivityToken" }.call())
                                                    ?.current(ignored = true)?.field { name = "intent" }?.set(subIntent)
                                            set(subIntent)
                                        }
                                }
                            }
                    }
            }?.onFailure { YLog.innerE("Activity Proxy got an exception in msg.what [$EXECUTE_TRANSACTION]", it) }
        }
        return baseInstance?.handleMessage(msg) ?: false
    }
}