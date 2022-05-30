/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
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
 * This file is Created by fankes on 2022/5/16.
 */
@file:Suppress("StaticFieldLeak", "UNCHECKED_CAST", "unused", "MemberVisibilityCanBePrivate", "DEPRECATION")

package com.highcapable.yukihookapi.hook.xposed.channel

import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcelable
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * 实现 Xposed 模块的数据通讯桥
 *
 * 通过模块与宿主相互注册 [BroadcastReceiver] 来实现数据的交互
 *
 * 模块需要将 [Application] 继承于 [ModuleApplication] 来实现此功能
 *
 * - ❗模块与宿主需要保持存活状态 - 否则无法建立通讯
 *
 * - 详情请参考 [API 文档 - YukiHookDataChannel](https://fankes.github.io/YukiHookAPI/#/api/document?id=yukihookdatachannel-class)
 */
class YukiHookDataChannel private constructor() {

    internal companion object {

        /** 是否为 Xposed 环境 */
        private val isXposedEnvironment = YukiHookBridge.hasXposedBridge

        /** 模块构建版本号获取标签 */
        private const val GET_MODULE_GENERATED_VERSION = "module_generated_version_get"

        /** 模块构建版本号结果标签 */
        private const val RESULT_MODULE_GENERATED_VERSION = "module_generated_version_result"

        /** 仅监听结果键值 */
        private const val VALUE_WAIT_FOR_LISTENER = "wait_for_listener_value"

        /** 当前 [YukiHookDataChannel] 单例 */
        private var instance: YukiHookDataChannel? = null

        /**
         * 获取 [YukiHookDataChannel] 单例
         * @return [YukiHookDataChannel]
         */
        internal fun instance() = instance ?: YukiHookDataChannel().apply { instance = this }
    }

    /**
     * 键值回调的监听类型定义
     */
    private enum class CallbackKeyType { SINGLE, CDATA, VMFL }

    /** 注册广播回调数组 */
    private var receiverCallbacks = ConcurrentHashMap<String, Pair<Context?, (String, Intent) -> Unit>>()

    /** 当前注册广播的 [Context] */
    private var receiverContext: Context? = null

    /** 广播接收器 */
    private val handlerReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                intent.action?.also { action ->
                    runCatching {
                        receiverCallbacks.takeIf { it.isNotEmpty() }?.apply {
                            arrayListOf<String>().also { destroyedCallbacks ->
                                forEach { (key, it) ->
                                    when {
                                        (it.first as? Activity?)?.isDestroyed == true -> destroyedCallbacks.add(key)
                                        isCurrentBroadcast(it.first) -> it.second(action, intent)
                                    }
                                }
                                destroyedCallbacks.takeIf { it.isNotEmpty() }?.forEach { remove(it) }
                            }
                        }
                    }.onFailure { loggerE(msg = "Received action \"$action\" failed", e = it) }
                }
            }
        }
    }

    /** 检查 API 装载状态 */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookDataChannel not allowed in Custom Hook API")
        if (YukiHookBridge.hasXposedBridge && YukiHookBridge.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
    }

    /**
     * 是否为当前正在使用的广播回调事件
     * @param context 当前实例
     * @return [Boolean]
     */
    private fun isCurrentBroadcast(context: Context?) = runCatching {
        isXposedEnvironment || (((context ?: receiverContext)
            ?.getSystemService(ACTIVITY_SERVICE) as? ActivityManager?)
            ?.getRunningTasks(9999)?.filter { context?.javaClass?.name == it?.topActivity?.className }?.size ?: 0) > 0
    }.getOrNull() ?: loggerW(msg = "Couldn't got current Activity status because a SecurityException blocked it").let { false }

    /**
     * 获取宿主广播 Action 名称
     * @param packageName 包名
     * @return [String]
     */
    private fun hostActionName(packageName: String) = "yukihookapi.intent.action.HOST_DATA_CHANNEL_${packageName.trim().hashCode()}"

    /**
     * 获取模块广播 Action 名称
     * @param context 实例 - 默认空
     * @return [String]
     */
    private fun moduleActionName(context: Context? = null) = "yukihookapi.intent.action.MODULE_DATA_CHANNEL_${
        YukiHookBridge.modulePackageName.ifBlank { context?.packageName ?: "" }.trim().hashCode()
    }"

    /**
     * 注册广播
     * @param context 目标 Hook APP (宿主) 或模块全局上下文实例 - 为空停止注册
     * @param packageName 包名 - 为空获取 [context] 的 [Context.getPackageName]
     */
    internal fun register(context: Context?, packageName: String = context?.packageName ?: "") {
        if (context == null) return
        if (YukiHookAPI.Configs.isEnableDataChannel.not() || receiverContext != null) return
        receiverContext = context
        context.registerReceiver(
            handlerReceiver, IntentFilter().apply {
                addAction(if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
            }
        )
        /** 注册监听模块与宿主的版本是否匹配 */
        nameSpace(context, packageName, isSecure = false).wait<String>(GET_MODULE_GENERATED_VERSION) { fromPackageName ->
            nameSpace(context, fromPackageName, isSecure = false).put(RESULT_MODULE_GENERATED_VERSION, YukiHookBridge.moduleGeneratedVersion)
        }
    }

    /**
     * 获取命名空间
     * @param context 上下文实例
     * @param packageName 目标 Hook APP (宿主) 的包名
     * @param isSecure 是否启用安全检查 - 默认是
     * @return [NameSpace]
     */
    internal fun nameSpace(context: Context? = null, packageName: String, isSecure: Boolean = true): NameSpace {
        checkApi()
        return NameSpace(context = context ?: receiverContext, packageName, isSecure)
    }

    /**
     * [YukiHookDataChannel] 命名空间
     *
     * - ❗请使用 [nameSpace] 方法来获取 [NameSpace]
     * @param context 上下文实例
     * @param packageName 目标 Hook APP (宿主) 的包名
     * @param isSecure 是否启用安全检查
     */
    inner class NameSpace internal constructor(private val context: Context?, private val packageName: String, private val isSecure: Boolean) {

        /**
         * 键值尾部名称
         * @param type 类型
         * @return [String]
         */
        private fun keyShortName(type: CallbackKeyType) = "_${if (isXposedEnvironment) "X" else context?.javaClass?.name ?: "M"}_${type.ordinal}"

        /**
         * 创建一个调用空间
         * @param initiate 方法体
         * @return [NameSpace] 可继续向下监听
         */
        inline fun with(initiate: NameSpace.() -> Unit) = apply(initiate)

        /**
         * 发送键值数据
         * @param key 键值名称
         * @param value 键值数据
         */
        fun <T> put(key: String, value: T) = pushReceiver(ChannelData(key, value))

        /**
         * 发送键值数据
         * @param data 键值实例
         * @param value 键值数据 - 未指定为 [ChannelData.value]
         */
        fun <T> put(data: ChannelData<T>, value: T? = data.value) = pushReceiver(ChannelData(data.key, value))

        /**
         * 发送键值数据
         * @param data 键值实例
         */
        fun put(vararg data: ChannelData<*>) = data.takeIf { it.isNotEmpty() }?.let { pushReceiver(*it) }

        /**
         * 仅发送键值监听 - 使用默认值 [VALUE_WAIT_FOR_LISTENER] 发送键值数据
         * @param key 键值名称
         */
        fun put(key: String) = pushReceiver(ChannelData(key, VALUE_WAIT_FOR_LISTENER))

        /**
         * 获取键值数据
         * @param key 键值名称
         * @param result 回调结果数据
         */
        fun <T> wait(key: String, result: (value: T) -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.SINGLE)] = Pair(context) { action, intent ->
                if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                    (intent.extras?.get(key) as? T?)?.let { result(it) }
            }
        }

        /**
         * 获取键值数据
         * @param data 键值实例
         * @param result 回调结果数据
         */
        fun <T> wait(data: ChannelData<T>, result: (value: T) -> Unit) {
            receiverCallbacks[data.key + keyShortName(CallbackKeyType.CDATA)] = Pair(context) { action, intent ->
                if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                    (intent.extras?.get(data.key) as? T?)?.let { result(it) }
            }
        }

        /**
         * 仅获取监听结果 - 不获取键值数据
         *
         * - ❗仅限使用 [VALUE_WAIT_FOR_LISTENER] 发送的监听才能被接收
         * @param key 键值名称
         * @param callback 回调结果
         */
        fun wait(key: String, callback: () -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.VMFL)] = Pair(context) { action, intent ->
                if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                    if (intent.getStringExtra(key) == VALUE_WAIT_FOR_LISTENER) callback()
            }
        }

        /**
         * 获取模块与宿主的版本是否匹配
         *
         * 通过此方法可原生判断 Xposed 模块更新后宿主并未重新装载造成两者不匹配的情况
         * @param result 回调是否匹配
         */
        fun checkingVersionEquals(result: (Boolean) -> Unit) {
            wait<String>(RESULT_MODULE_GENERATED_VERSION) { result(it == YukiHookBridge.moduleGeneratedVersion) }
            put(GET_MODULE_GENERATED_VERSION, packageName)
        }

        /**
         * 发送广播
         * @param data 键值数据
         */
        private fun pushReceiver(vararg data: ChannelData<*>) {
            if (YukiHookAPI.Configs.isEnableDataChannel.not()) return
            /** 在 [isSecure] 启用的情况下 - 在模块环境中只能使用 [Activity] 发送广播 */
            if (isSecure && context != null) if (isXposedEnvironment.not() && context !is Activity)
                error("YukiHookDataChannel only support used on an Activity, but this current context is \"${context.javaClass.name}\"")
            /** 发送广播 */
            (context ?: YukiHookAppHelper.currentApplication())?.sendBroadcast(Intent().apply {
                action = if (isXposedEnvironment) moduleActionName() else hostActionName(packageName)
                data.takeIf { it.isNotEmpty() }?.forEach {
                    when (it.value) {
                        null -> Unit
                        is Bundle -> putExtra(it.key, it.value as Bundle)
                        is Parcelable -> putExtra(it.key, it.value as Parcelable)
                        is Serializable -> putExtra(it.key, it.value as Serializable)
                        is Array<*> -> putExtra(it.key, it.value as Array<*>)
                        is Boolean -> putExtra(it.key, it.value as Boolean)
                        is BooleanArray -> putExtra(it.key, it.value as BooleanArray)
                        is Byte -> putExtra(it.key, it.value as Byte)
                        is ByteArray -> putExtra(it.key, it.value as ByteArray)
                        is Char -> putExtra(it.key, it.value as Char)
                        is CharArray -> putExtra(it.key, it.value as CharArray)
                        is CharSequence -> putExtra(it.key, it.value as CharSequence)
                        is Double -> putExtra(it.key, it.value as Double)
                        is DoubleArray -> putExtra(it.key, it.value as DoubleArray)
                        is Float -> putExtra(it.key, it.value as Float)
                        is FloatArray -> putExtra(it.key, it.value as FloatArray)
                        is Int -> putExtra(it.key, it.value as Int)
                        is IntArray -> putExtra(it.key, it.value as IntArray)
                        is Long -> putExtra(it.key, it.value as Long)
                        is LongArray -> putExtra(it.key, it.value as LongArray)
                        is Short -> putExtra(it.key, it.value as Short)
                        is ShortArray -> putExtra(it.key, it.value as ShortArray)
                        is String -> putExtra(it.key, it.value as String)
                        else -> error("Key-Value type ${it.value?.javaClass?.name} is not allowed")
                    }
                }
            }) ?: yLoggerE(
                msg = "Failed to sendBroadcast like \"${data.takeIf { it.isNotEmpty() }?.get(0)?.key ?: "unknown"}\", " +
                        "because got null context in \"$packageName\""
            )
        }
    }
}