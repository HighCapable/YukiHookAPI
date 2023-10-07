/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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