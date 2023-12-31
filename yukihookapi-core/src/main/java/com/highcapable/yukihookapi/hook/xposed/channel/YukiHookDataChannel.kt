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
 * This file is created by fankes on 2022/5/16.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "StaticFieldLeak", "KotlinConstantConditions")

package com.highcapable.yukihookapi.hook.xposed.channel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.ACTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.TransactionTooLargeException
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.utils.factory.RandomSeed
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.channel.annotation.SendTooLargeChannelData
import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import com.highcapable.yukihookapi.hook.xposed.channel.data.wrapper.ChannelDataWrapper
import com.highcapable.yukihookapi.hook.xposed.channel.priority.ChannelPriority
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * 实现 Xposed 模块的数据通讯桥
 *
 * 通过模块与宿主相互注册 [BroadcastReceiver] 来实现数据的交互
 *
 * 模块需要将 [Application] 继承于 [ModuleApplication] 来实现此功能
 *
 * - 模块与宿主需要保持存活状态 - 否则无法建立通讯
 *
 * 详情请参考 [API 文档 - YukiHookDataChannel](https://highcapable.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/channel/YukiHookDataChannel)
 *
 * For English version, see [API Document - YukiHookDataChannel](https://highcapable.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/channel/YukiHookDataChannel)
 */
class YukiHookDataChannel private constructor() {

    internal companion object {

        /** 是否为 (Xposed) 宿主环境 */
        private val isXposedEnvironment = YukiXposedModule.isXposedEnvironment

        /** 自动生成的 Xposed 模块构建版本号 */
        private val moduleGeneratedVersion = YukiHookAPI.Status.compiledTimestamp.toString()

        /** 模块构建版本号获取标签 */
        private const val GET_MODULE_GENERATED_VERSION = "module_generated_version_get"

        /** 模块构建版本号结果标签 */
        private const val RESULT_MODULE_GENERATED_VERSION = "module_generated_version_result"

        /** 调试日志数据获取标签 */
        private const val GET_YUKI_LOGGER_INMEMORY_DATA = "yuki_logger_inmemory_data_get"

        /** 调试日志数据结果标签 */
        private val RESULT_YUKI_LOGGER_INMEMORY_DATA = ChannelData<List<YLogData>>("yuki_logger_inmemory_data_result")

        /** 仅监听结果键值 */
        private const val VALUE_WAIT_FOR_LISTENER = "wait_for_listener_value"

        /**
         * 系统广播允许发送的最大数据字节大小
         *
         * 标准为 1 MB - 实测不同系统目前已知能得到的数据分别有 1、2、3 MB
         *
         * 经过测试分段发送 900 KB 数据在 1 台 Android 13 系统的设备上依然会发生异常
         *
         * 综上所述 - 为了防止系统不同限制不同 - 最终决定默认设置为 500 KB - 超出后以此大小分段发送数据
         */
        private var receiverDataMaxByteSize = 500 * 1024

        /**
         * 系统广播允许发送的最大数据字节大小倍数 (分段数据)
         *
         * 分段后的数据每次也不能超出 [receiverDataMaxByteSize] 的大小
         *
         * 此倍数被作用于分配 [receiverDataMaxByteSize] 的大小
         *
         * 倍数计算公式为 [receiverDataMaxByteSize] / [receiverDataMaxByteCompressionFactor] = [receiverDataSegmentMaxByteSize]
         */
        private var receiverDataMaxByteCompressionFactor = 3

        /**
         * 获取当前系统广播允许的最大单个分段数据字节大小
         * @return [Int]
         */
        private val receiverDataSegmentMaxByteSize get() = receiverDataMaxByteSize / receiverDataMaxByteCompressionFactor

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

    /** 是否允许发送超出 [receiverDataMaxByteSize] 大小的数据 */
    private var isAllowSendTooLargeData = false

    /** 广播接收器 */
    private val handlerReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent == null) return
                intent.action?.also { action ->
                    runCatching {
                        receiverCallbacks.takeIf { it.isNotEmpty() }?.apply {
                            mutableListOf<String>().also { destroyedCallbacks ->
                                forEach { (key, it) ->
                                    when {
                                        (it.first as? Activity?)?.isDestroyed == true -> destroyedCallbacks.add(key)
                                        isCurrentBroadcast(it.first) -> it.second(action, intent)
                                    }
                                }
                                destroyedCallbacks.takeIf { it.isNotEmpty() }?.forEach { remove(it) }
                            }
                        }
                    }.onFailure { YLog.innerE("Received action \"$action\" failed", it) }
                }
            }
        }
    }

    /** 检查 API 装载状态 */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookDataChannel not allowed in Custom Hook API")
        if (isXposedEnvironment && YukiXposedModule.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
        isAllowSendTooLargeData = false
    }

    /**
     * 是否为当前正在使用的广播回调事件
     * @param context 当前实例
     * @return [Boolean]
     */
    private fun isCurrentBroadcast(context: Context?) = runCatching {
        @Suppress("DEPRECATION")
        context is Application || isXposedEnvironment || (((context ?: receiverContext)
            ?.getSystemService(ACTIVITY_SERVICE) as? ActivityManager?)
            ?.getRunningTasks(9999)?.filter { context?.javaClass?.name == it?.topActivity?.className }?.size ?: 0) > 0
    }.getOrNull() ?: YLog.innerW("Couldn't got current Activity status because a SecurityException blocked it").let { false }

    /**
     * 获取宿主广播 Action 名称
     * @param packageName 包名
     * @return [String]
     */
    private fun hostActionName(packageName: String) = "yuki_hook_host_data_channel_${packageName.trim().hashCode()}"

    /**
     * 获取模块广播 Action 名称
     * @param context 实例 - 默认空
     * @return [String]
     */
    private fun moduleActionName(context: Context? = null) =
        "yuki_hook_module_data_channel_${YukiXposedModule.modulePackageName.ifBlank { context?.packageName ?: "" }.trim().hashCode()}"

    /**
     * 注册广播
     * @param context 目标 Hook APP (宿主) 或模块全局上下文实例 - 为空停止注册
     * @param packageName 包名 - 为空获取 [context] 的 [Context.getPackageName]
     */
    internal fun register(context: Context?, packageName: String = context?.packageName ?: "") {
        if (YukiHookAPI.Configs.isEnableDataChannel.not() || context == null) return
        receiverContext = context
        IntentFilter().apply {
            addAction(if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
        }.also { filter ->
            /**
             * 从 Android 14 (及预览版) 开始
             * 外部广播必须声明 [Context.RECEIVER_EXPORTED]
             */
            @SuppressLint("UnspecifiedRegisterReceiverFlag")
            if (Build.VERSION.SDK_INT >= 33)
                context.registerReceiver(handlerReceiver, filter, Context.RECEIVER_EXPORTED)
            else context.registerReceiver(handlerReceiver, filter)
        }
        /** 排除模块环境下模块注册自身广播 */
        if (isXposedEnvironment.not()) return
        nameSpace(context, packageName).with {
            /** 注册监听模块与宿主的版本是否匹配 */
            wait<String>(GET_MODULE_GENERATED_VERSION) { fromPackageName ->
                nameSpace(context, fromPackageName).put(RESULT_MODULE_GENERATED_VERSION, moduleGeneratedVersion)
            }
            /** 注册监听模块与宿主之间的调试日志数据 */
            wait<String>(GET_YUKI_LOGGER_INMEMORY_DATA) { fromPackageName ->
                nameSpace(context, fromPackageName).put(RESULT_YUKI_LOGGER_INMEMORY_DATA, YLog.inMemoryData)
            }
        }
    }

    /**
     * 获取命名空间
     * @param context 上下文实例
     * @param packageName 目标 Hook APP (宿主) 的包名
     * @return [NameSpace]
     */
    internal fun nameSpace(context: Context? = null, packageName: String): NameSpace {
        checkApi()
        return NameSpace(context = context ?: receiverContext, packageName)
    }

    /**
     * 分段数据临时集合实例
     * @param listData [List] 数据数组
     * @param mapData [Map] 数据数组
     * @param setData [Set] 数据数组
     * @param stringData [String] 数据数组
     */
    internal inner class SegmentsTempData(
        var listData: MutableList<List<*>> = mutableListOf(),
        var mapData: MutableList<Map<*, *>> = mutableListOf(),
        var setData: MutableList<Set<*>> = mutableListOf(),
        var stringData: MutableList<String> = mutableListOf()
    )

    /**
     * [YukiHookDataChannel] 命名空间
     *
     * - 请使用 [nameSpace] 方法来获取 [NameSpace]
     * @param context 上下文实例
     * @param packageName 目标 Hook APP (宿主) 的包名
     */
    inner class NameSpace internal constructor(private val context: Context?, private val packageName: String) {

        /** 当前分段数据临时集合数据 */
        private val segmentsTempData = ConcurrentHashMap<String, SegmentsTempData>()

        /**
         * 键值尾部名称
         * @param type 类型
         * @return [String]
         */
        private fun keyShortName(type: CallbackKeyType) =
            "${keyNonRepeatName}_${if (isXposedEnvironment) "X" else context?.javaClass?.name ?: "M"}_${type.ordinal}"

        /**
         * 键值不重复名称 - 确保每个宿主使用的键值名称互不干扰
         * @return [String]
         */
        private val keyNonRepeatName get() = "_${packageName.hashCode()}"

        /**
         * 创建一个调用空间
         * @param initiate 方法体
         * @return [NameSpace] 可继续向下监听
         */
        inline fun with(initiate: NameSpace.() -> Unit) = apply(initiate)

        /**
         * [YukiHookDataChannel] 允许发送的最大数据字节大小
         *
         * 默认为 500 KB (500 * 1024) - 详情请参考 [receiverDataMaxByteSize] 的注释
         *
         * 最小不能低于 100 KB (100 * 1024) - 否则会被重新设置为 100 KB (100 * 1024)
         *
         * 设置后将在全局生效 - 直到当前进程结束
         *
         * - 超出最大数据字节大小后的数据将被自动分段发送
         *
         * - 警告：请谨慎调整此参数 - 如果超出了系统能够允许的大小会引发 [TransactionTooLargeException] 异常
         * @return [Int]
         */
        var dataMaxByteSize
            get() = receiverDataMaxByteSize
            set(value) {
                receiverDataMaxByteSize = if (value < 100 * 1024) 100 * 1024 else value
            }

        /**
         * [YukiHookDataChannel] 允许发送的最大数据字节大小倍数 (分段数据)
         *
         * 默认为 3 - 详情请参考 [receiverDataMaxByteCompressionFactor] 的注释
         *
         * 最小不能低于 2 - 否则会被重新设置为 2
         *
         * 设置后将在全局生效 - 直到当前进程结束
         *
         * - 超出最大数据字节大小后的数据将按照此倍数自动划分 [receiverDataMaxByteSize] 的大小
         *
         * - 警告：请谨慎调整此参数 - 如果超出了系统能够允许的大小会引发 [TransactionTooLargeException] 异常
         * @return [Int]
         */
        var dataMaxByteCompressionFactor
            get() = receiverDataMaxByteCompressionFactor
            set(value) {
                receiverDataMaxByteCompressionFactor = if (value < 2) 2 else value
            }

        /**
         * 解除发送数据的大小限制并禁止开启分段发送功能
         *
         * 仅会在每次调用时生效 - 下一次没有调用此方法则此功能将被自动关闭
         *
         * 你还需要在整个调用域中声明注解 [SendTooLargeChannelData] 以消除警告
         *
         * - 若你不知道允许此功能会带来何种后果 - 请勿使用
         * @return [NameSpace]
         */
        @SendTooLargeChannelData
        fun allowSendTooLargeData(): NameSpace {
            isAllowSendTooLargeData = true
            return this
        }

        /**
         * 发送键值数据
         * @param key 键值名称
         * @param value 键值数据
         */
        fun <T> put(key: String, value: T) = parseSendingData(ChannelData(key, value).toWrapper())

        /**
         * 发送键值数据
         * @param data 键值实例
         * @param value 键值数据 - 未指定为 [ChannelData.value]
         */
        fun <T> put(data: ChannelData<T>, value: T? = data.value) = parseSendingData(ChannelData(data.key, value).toWrapper())

        /**
         * 发送键值数据
         * @param data 键值实例
         */
        fun put(vararg data: ChannelData<*>) = data.takeIf { it.isNotEmpty() }?.forEach { parseSendingData(it.toWrapper()) }

        /**
         * 仅发送键值监听 - 使用默认值 [VALUE_WAIT_FOR_LISTENER] 发送键值数据
         * @param key 键值名称
         */
        fun put(key: String) = put(key, VALUE_WAIT_FOR_LISTENER)

        /**
         * 获取键值数据
         * @param key 键值名称
         * @param priority 响应优先级 - 默认不设置
         * @param result 回调结果数据
         */
        fun <T> wait(key: String, priority: ChannelPriority? = null, result: (value: T) -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.SINGLE)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        parseReceivedData(intent.getDataWrapper(key), result)
            }
        }

        /**
         * 获取键值数据
         * @param data 键值实例
         * @param priority 响应优先级 - 默认不设置
         * @param result 回调结果数据
         */
        fun <T> wait(data: ChannelData<T>, priority: ChannelPriority? = null, result: (value: T) -> Unit) {
            receiverCallbacks[data.key + keyShortName(CallbackKeyType.CDATA)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        parseReceivedData(intent.getDataWrapper(data.key), result)
            }
        }

        /**
         * 仅获取监听结果 - 不获取键值数据
         *
         * - 仅限使用 [VALUE_WAIT_FOR_LISTENER] 发送的监听才能被接收
         * @param key 键值名称
         * @param priority 响应优先级 - 默认不设置
         * @param callback 回调结果
         */
        fun wait(key: String, priority: ChannelPriority? = null, callback: () -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.VMFL)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        intent.getDataWrapper<String>(key)?.let { if (it.instance.value == VALUE_WAIT_FOR_LISTENER) callback() }
            }
        }

        /**
         * 获取模块与宿主的版本是否匹配
         *
         * 通过此方法可原生判断 Xposed 模块更新后宿主并未重新装载造成两者不匹配的情况
         * @param priority 响应优先级 - 默认不设置
         * @param result 回调是否匹配
         */
        fun checkingVersionEquals(priority: ChannelPriority? = null, result: (Boolean) -> Unit) {
            wait<String>(RESULT_MODULE_GENERATED_VERSION, priority) { result(it == moduleGeneratedVersion) }
            put(GET_MODULE_GENERATED_VERSION, packageName)
        }

        /**
         * 获取模块与宿主之间的 [List]<[YLogData]> 数据
         *
         * 由于模块与宿主处于不同的进程 - 我们可以使用数据通讯桥访问各自的调试日志数据
         *
         * - 模块与宿主必须启用 [YLog.Configs.isRecord] 才能获取到调试日志数据
         *
         * - 由于 Android 限制了数据传输大小的最大值 - 如果调试日志过多将会自动进行分段发送 - 数据越大速度越慢
         * @param priority 响应优先级 - 默认不设置
         * @param result 回调 [List]<[YLogData]>
         */
        fun obtainLoggerInMemoryData(priority: ChannelPriority? = null, result: (List<YLogData>) -> Unit) {
            wait(RESULT_YUKI_LOGGER_INMEMORY_DATA, priority) { result(it) }
            put(GET_YUKI_LOGGER_INMEMORY_DATA, packageName)
        }

        /**
         * 从 [Intent] 获取接收到的任意类型数据转换为 [ChannelDataWrapper]<[T]> 实例
         * @param key 键值名称
         * @return [ChannelDataWrapper]<[T]> or null
         */
        private fun <T> Intent.getDataWrapper(key: String) = runCatching {
            @Suppress("DEPRECATION")
            extras?.getSerializable(key + keyNonRepeatName) as? ChannelDataWrapper<T>
        }.getOrNull()

        /**
         * [ChannelData]<[T]> 转换为 [ChannelDataWrapper]<[T]> 实例
         * @param id 包装实例 ID - 默认为 [RandomSeed.createString]
         * @param size 分段数据总大小 (长度) - 默认为 -1
         * @param index 分段数据当前接收到的下标 - 默认为 -1
         * @return [ChannelDataWrapper]<[T]>
         */
        private fun <T> ChannelData<T>.toWrapper(id: String = RandomSeed.createString(), size: Int = -1, index: Int = -1) =
            ChannelDataWrapper(id, size > 0, size, index, this)

        /**
         * 计算任意类型所占空间的字节大小
         * @return [Int] 字节大小
         */
        private fun Any.calDataByteSize(): Int {
            val key = if (this is ChannelData<*>) key else "placeholder"
            val value = if (this is ChannelData<*>) value else this
            val bundle = Bundle().apply {
                when (value) {
                    null -> Unit
                    is Boolean -> putBoolean(key, value)
                    is BooleanArray -> putBooleanArray(key, value)
                    is Byte -> putByte(key, value)
                    is ByteArray -> putByteArray(key, value)
                    is Char -> putChar(key, value)
                    is CharArray -> putCharArray(key, value)
                    is Double -> putDouble(key, value)
                    is DoubleArray -> putDoubleArray(key, value)
                    is Float -> putFloat(key, value)
                    is FloatArray -> putFloatArray(key, value)
                    is Int -> putInt(key, value)
                    is IntArray -> putIntArray(key, value)
                    is Long -> putLong(key, value)
                    is LongArray -> putLongArray(key, value)
                    is Short -> putShort(key, value)
                    is ShortArray -> putShortArray(key, value)
                    is String -> putString(key, value)
                    is Array<*> -> putSerializable(key, value)
                    is CharSequence -> putCharSequence(key, value)
                    is Parcelable -> putParcelable(key, value)
                    is Serializable -> putSerializable(key, value)
                    else -> error("Key-Value type ${value.javaClass.name} is not allowed")
                }
            }
            return runCatching {
                Parcel.obtain().let { parcel ->
                    parcel.writeBundle(bundle)
                    val size = parcel.dataSize()
                    parcel.recycle()
                    size
                }
            }.getOrNull() ?: -1
        }

        /**
         * 处理收到的广播数据
         * @param wrapper 键值数据包装类
         * @param result 回调结果数据
         */
        private fun <T> parseReceivedData(wrapper: ChannelDataWrapper<T>?, result: (value: T) -> Unit) {
            if (YukiHookAPI.Configs.isEnableDataChannel.not()) return
            if (wrapper == null) return
            if (wrapper.isSegmentsType) runCatching {
                val tempData = segmentsTempData[wrapper.wrapperId] ?: SegmentsTempData().apply { segmentsTempData[wrapper.wrapperId] = this }
                when (wrapper.instance.value) {
                    is List<*> -> (wrapper.instance.value as List<*>).also { value ->
                        if (tempData.listData.isEmpty() && wrapper.segmentsIndex > 0) return
                        tempData.listData.add(wrapper.segmentsIndex, value)
                        if (tempData.listData.size == wrapper.segmentsSize) {
                            result(mutableListOf<Any?>().also { list -> tempData.listData.forEach { list.addAll(it) } } as T)
                            tempData.listData.clear()
                            segmentsTempData.remove(wrapper.wrapperId)
                        }
                    }
                    is Map<*, *> -> (wrapper.instance.value as Map<*, *>).also { value ->
                        if (tempData.mapData.isEmpty() && wrapper.segmentsIndex > 0) return
                        tempData.mapData.add(wrapper.segmentsIndex, value)
                        if (tempData.mapData.size == wrapper.segmentsSize) {
                            result(mutableMapOf<Any?, Any?>().also { map -> tempData.mapData.forEach { it.forEach { (k, v) -> map[k] = v } } } as T)
                            tempData.mapData.clear()
                            segmentsTempData.remove(wrapper.wrapperId)
                        }
                    }
                    is Set<*> -> (wrapper.instance.value as Set<*>).also { value ->
                        if (tempData.setData.isEmpty() && wrapper.segmentsIndex > 0) return
                        tempData.setData.add(wrapper.segmentsIndex, value)
                        if (tempData.setData.size == wrapper.segmentsSize) {
                            result(mutableSetOf<Any?>().also { set -> tempData.setData.forEach { set.addAll(it) } } as T)
                            tempData.setData.clear()
                            segmentsTempData.remove(wrapper.wrapperId)
                        }
                    }
                    is String -> (wrapper.instance.value as String).also { value ->
                        if (tempData.stringData.isEmpty() && wrapper.segmentsIndex > 0) return
                        tempData.stringData.add(wrapper.segmentsIndex, value)
                        if (tempData.stringData.size == wrapper.segmentsSize) {
                            result(StringBuilder().apply { tempData.stringData.forEach { append(it) } }.toString() as T)
                            tempData.stringData.clear()
                            segmentsTempData.remove(wrapper.wrapperId)
                        }
                    }
                    else -> YLog.innerE("Unsupported segments data key of \"${wrapper.instance.key}\"'s type")
                }
            }.onFailure {
                YLog.innerE("YukiHookDataChannel cannot merge this segments data key of \"${wrapper.instance.key}\"", it)
            } else wrapper.instance.value?.let { e -> result(e) }
        }

        /**
         * 处理需要发送的广播数据
         * @param wrapper 键值数据包装类
         */
        private fun parseSendingData(wrapper: ChannelDataWrapper<*>) {
            if (YukiHookAPI.Configs.isEnableDataChannel.not()) return
            /** 当前包装实例 ID */
            val wrapperId = RandomSeed.createString()

            /** 当前需要发送的数据字节大小 */
            val dataByteSize = wrapper.instance.calDataByteSize()
            if (dataByteSize < 0 && isAllowSendTooLargeData.not()) return YLog.innerE(
                msg = "YukiHookDataChannel cannot calculate the byte size of the data key of \"${wrapper.instance.key}\" to be sent, " +
                    "so this data cannot be sent\n" +
                    "If you want to lift this restriction, use the allowSendTooLargeData function when calling, " +
                    "but this may cause the app crash"
            )
            /**
             * 如果数据过大打印警告信息 - 仅限 [YukiHookAPI.Configs.isDebug] 启用时生效
             * @param name 数据类型名称
             * @param size 分段总大小 (长度)
             */
            fun loggerForTooLargeData(name: String, size: Int) {
                if (YukiHookAPI.Configs.isDebug) YLog.innerW(
                    msg = "This data key of \"${wrapper.instance.key}\" type $name is too large (total ${dataByteSize / 1024f} KB, " +
                        "limit ${receiverDataMaxByteSize / 1024f} KB), will be segmented to $size piece to send"
                )
            }

            /**
             * 如果数据过大且无法分段打印错误信息
             * @param suggestionMessage 建议内容 - 默认空
             */
            fun loggerForUnprocessableData(suggestionMessage: String = "") = YLog.innerE(
                msg = "YukiHookDataChannel cannot send this data key of \"${wrapper.instance.key}\" type ${wrapper.instance.value?.javaClass}, " +
                    "because it is too large (total ${dataByteSize / 1024f} KB, " +
                    "limit ${receiverDataMaxByteSize / 1024f} KB) and cannot be segmented\n" +
                    (if (suggestionMessage.isNotBlank()) "$suggestionMessage\n" else "") +
                    "If you want to lift this restriction, use the allowSendTooLargeData function when calling, " +
                    "but this may cause the app crash"
            )

            /**
             * 如果数据过大且无法分段打印错误信息 (首元素超出 - 分段数组内容为空)
             * @param name 数据类型名称
             */
            fun loggerForUnprocessableDataByFirstElement(name: String) = loggerForUnprocessableData(
                suggestionMessage = "Failed to segment $name type because the size of its first element has exceeded the maximum limit"
            )
            when {
                wrapper.isSegmentsType || isAllowSendTooLargeData -> pushReceiver(wrapper)
                dataByteSize >= receiverDataMaxByteSize -> when (wrapper.instance.value) {
                    is List<*> -> (wrapper.instance.value as List<*>).also { value ->
                        val segments = mutableListOf<List<*>>()
                        var segment = mutableListOf<Any?>()
                        value.forEach {
                            segment.add(it)
                            if (segment.calDataByteSize() >= receiverDataSegmentMaxByteSize) {
                                segments.add(segment)
                                segment = mutableListOf()
                            }
                        }
                        if (segment.isNotEmpty()) segments.add(segment)
                        loggerForTooLargeData(name = "List", segments.size)
                        segments.takeIf { it.isNotEmpty() }?.forEachIndexed { p, it ->
                            pushReceiver(ChannelData(wrapper.instance.key, it).toWrapper(wrapperId, segments.size, p))
                        } ?: loggerForUnprocessableDataByFirstElement(name = "List")
                    }
                    is Map<*, *> -> (wrapper.instance.value as Map<*, *>).also { value ->
                        val segments = mutableListOf<Map<*, *>>()
                        var segment = mutableMapOf<Any?, Any?>()
                        value.forEach { (k, v) ->
                            segment[k] = v
                            if (segment.calDataByteSize() >= receiverDataSegmentMaxByteSize) {
                                segments.add(segment)
                                segment = mutableMapOf()
                            }
                        }
                        if (segment.isNotEmpty()) segments.add(segment)
                        loggerForTooLargeData(name = "Map", segments.size)
                        segments.takeIf { it.isNotEmpty() }?.forEachIndexed { p, it ->
                            pushReceiver(ChannelData(wrapper.instance.key, it).toWrapper(wrapperId, segments.size, p))
                        } ?: loggerForUnprocessableDataByFirstElement(name = "Map")
                    }
                    is Set<*> -> (wrapper.instance.value as Set<*>).also { value ->
                        val segments = mutableListOf<Set<*>>()
                        var segment = mutableSetOf<Any?>()
                        value.forEach {
                            segment.add(it)
                            if (segment.calDataByteSize() >= receiverDataSegmentMaxByteSize) {
                                segments.add(segment)
                                segment = mutableSetOf()
                            }
                        }
                        if (segment.isNotEmpty()) segments.add(segment)
                        loggerForTooLargeData(name = "Set", segments.size)
                        segments.takeIf { it.isNotEmpty() }?.forEachIndexed { p, it ->
                            pushReceiver(ChannelData(wrapper.instance.key, it).toWrapper(wrapperId, segments.size, p))
                        } ?: loggerForUnprocessableDataByFirstElement(name = "Set")
                    }
                    is String -> (wrapper.instance.value as String).also { value ->
                        /** 由于字符会被按照双字节计算 - 所以这里将限制字节大小除以 2 */
                        val twoByteMaxSize = receiverDataMaxByteSize / 2
                        val segments = mutableListOf<String>()
                        for (i in 0..value.length step twoByteMaxSize)
                            if (i + twoByteMaxSize <= value.length)
                                segments.add(value.substring(i, i + twoByteMaxSize))
                            else segments.add(value.substring(i, value.length))
                        if (segments.size == 1) return pushReceiver(wrapper)
                        loggerForTooLargeData(name = "String", segments.size)
                        segments.takeIf { it.isNotEmpty() }?.forEachIndexed { p, it ->
                            pushReceiver(ChannelData(wrapper.instance.key, it).toWrapper(wrapperId, segments.size, p))
                        } ?: loggerForUnprocessableDataByFirstElement(name = "String")
                    }
                    is ByteArray, is CharArray, is ShortArray,
                    is IntArray, is LongArray, is FloatArray,
                    is DoubleArray, is BooleanArray, is Array<*> -> loggerForUnprocessableData(
                        suggestionMessage = "Primitive Array type like String[], int[] ... cannot be segmented, " +
                            "the suggestion is send those data using List type"
                    )
                    else -> loggerForUnprocessableData()
                }
                else -> pushReceiver(wrapper)
            }
        }

        /**
         * 发送广播
         * @param wrapper 键值数据包装类
         */
        private fun pushReceiver(wrapper: ChannelDataWrapper<*>) {
            /** 发送广播 */
            (context ?: AppParasitics.currentApplication)?.sendBroadcast(Intent().apply {
                action = if (isXposedEnvironment) moduleActionName() else hostActionName(packageName)
                /** 由于系统框架的包名可能不唯一 - 为防止发生问题不再对系统框架的广播设置接收者包名 */
                if (packageName != AppParasitics.SYSTEM_FRAMEWORK_NAME)
                    setPackage(if (isXposedEnvironment) YukiXposedModule.modulePackageName else packageName)
                putExtra(wrapper.instance.key + keyNonRepeatName, wrapper)
            }) ?: YLog.innerE("Failed to sendBroadcast like \"${wrapper.instance.key}\", because got null context in \"$packageName\"")
        }
    }
}