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
 * This file is created by fankes on 2022/5/16.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "StaticFieldLeak", "KotlinConstantConditions")

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
import android.os.Parcel
import android.os.Parcelable
import android.os.TransactionTooLargeException
import com.highcapable.betterandroid.system.extension.component.BroadcastReceiver
import com.highcapable.betterandroid.system.extension.component.getSerializableCompat
import com.highcapable.betterandroid.system.extension.component.registerReceiver
import com.highcapable.betterandroid.system.extension.component.sendBroadcast
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
 * Data communication bridge for a Xposed module.
 *
 * Exchanges data by registering [BroadcastReceiver] instances in both the module and host app.
 *
 * The module's [Application] must extend [ModuleApplication] to use this feature.
 *
 * - Both the module and host app must remain alive to communicate.
 */
class YukiHookDataChannel private constructor() {

    internal companion object {

        /** Whether the current environment is a (Xposed) host environment. */
        private val isXposedEnvironment = YukiXposedModule.isXposedEnvironment

        /** Automatically generated Xposed module build version. */
        private val moduleGeneratedVersion = YukiHookAPI.Status.compiledTimestamp.toString()

        /** Tag for requesting the module build version. */
        private const val GET_MODULE_GENERATED_VERSION = "module_generated_version_get"

        /** Tag for the module build version result. */
        private const val RESULT_MODULE_GENERATED_VERSION = "module_generated_version_result"

        /** Tag for requesting debug log data. */
        private const val GET_YUKI_LOGGER_INMEMORY_DATA = "yuki_logger_inmemory_data_get"

        /** Tag for the debug log data result. */
        private val RESULT_YUKI_LOGGER_INMEMORY_DATA = ChannelData<List<YLogData>>("yuki_logger_inmemory_data_result")

        /** Value used for listener-only results. */
        private const val VALUE_WAIT_FOR_LISTENER = "wait_for_listener_value"

        /**
         * Maximum data size in bytes allowed for a system broadcast.
         *
         * The standard limit is 1 MB. Tests show known limits of 1, 2, or 3 MB across different systems.
         *
         * Tests also show that sending 900 KB in segments still fails on one Android 13 device.
         *
         * To accommodate system-specific limits, the default is 500 KB. Larger data is sent in segments of this size.
         */
        private var receiverDataMaxByteSize = 500 * 1024

        /**
         * Maximum data-size compression factor for system broadcasts using segmented data.
         *
         * Each segment must also remain within [receiverDataMaxByteSize].
         *
         * This factor is used to divide [receiverDataMaxByteSize].
         *
         * The formula is [receiverDataMaxByteSize] / [receiverDataMaxByteCompressionFactor] = [receiverDataSegmentMaxByteSize].
         */
        private var receiverDataMaxByteCompressionFactor = 3

        /**
         * Gets the maximum size in bytes allowed for one segmented system-broadcast payload.
         * @return [Int]
         */
        private val receiverDataSegmentMaxByteSize get() = receiverDataMaxByteSize / receiverDataMaxByteCompressionFactor

        /** The current [YukiHookDataChannel] singleton. */
        private var instance: YukiHookDataChannel? = null

        /**
         * Gets the [YukiHookDataChannel] singleton.
         * @return [YukiHookDataChannel]
         */
        internal fun instance() = instance ?: YukiHookDataChannel().apply { instance = this }
    }

    /**
     * Listener type for key-value callbacks.
     */
    private enum class CallbackKeyType { SINGLE, CDATA, VMFL }

    /** Registered broadcast callbacks. */
    private var receiverCallbacks = ConcurrentHashMap<String, Pair<Context?, (String, Intent) -> Unit>>()

    /** The [Context] currently used to register broadcasts. */
    private var receiverContext: Context? = null

    /** Whether data larger than [receiverDataMaxByteSize] may be sent. */
    private var isAllowSendTooLargeData = false

    /** The broadcast receiver. */
    private val handlerReceiver = BroadcastReceiver { _, intent ->
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

    /** Checks the API loading state. */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookDataChannel not allowed in Custom Hook API")
        if (isXposedEnvironment && YukiXposedModule.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
        isAllowSendTooLargeData = false
    }

    /**
     * Checks whether this broadcast callback event is currently active.
     * @param context the current instance.
     * @return [Boolean]
     */
    private fun isCurrentBroadcast(context: Context?) = runCatching {
        @Suppress("DEPRECATION")
        context is Application || isXposedEnvironment || (((context ?: receiverContext)
            ?.getSystemService(ACTIVITY_SERVICE) as? ActivityManager?)
            ?.getRunningTasks(9999)?.filter { context?.javaClass?.name == it?.topActivity?.className }?.size ?: 0) > 0
    }.getOrNull() ?: YLog.innerW("Couldn't got current Activity status because a SecurityException blocked it").let { false }

    /**
     * Gets the host broadcast action name.
     * @param packageName the package name.
     * @return [String]
     */
    private fun hostActionName(packageName: String) = "yuki_hook_host_data_channel_${packageName.trim().hashCode()}"

    /**
     * Gets the module broadcast action name.
     * @param context the optional context instance, null by default.
     * @return [String]
     */
    private fun moduleActionName(context: Context? = null) =
        "yuki_hook_module_data_channel_${YukiXposedModule.modulePackageName.ifBlank { context?.packageName ?: "" }.trim().hashCode()}"

    /**
     * Registers broadcasts.
     * @param context the global context of the target host app or module. Registration stops when null.
     * @param packageName the package name. When empty, it is obtained from [Context.getPackageName] on [context].
     */
    internal fun register(context: Context?, packageName: String = context?.packageName ?: "") {
        if (YukiHookAPI.Configs.isEnableDataChannel.not() || context == null) return
        receiverContext = context
        val filter = IntentFilter().apply {
            addAction(if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
        }
        context.registerReceiver(filter, exported = true, body = handlerReceiver)
        // Prevents the module from registering its own broadcast in the module environment.
        if (isXposedEnvironment.not()) return
        nameSpace(context, packageName).with {
            // Registers a listener that checks whether module and host app versions match.
            wait<String>(GET_MODULE_GENERATED_VERSION) { fromPackageName ->
                nameSpace(context, fromPackageName).put(RESULT_MODULE_GENERATED_VERSION, moduleGeneratedVersion)
            }
            // Registers a listener for debug log data exchanged between the module and host app.
            wait<String>(GET_YUKI_LOGGER_INMEMORY_DATA) { fromPackageName ->
                nameSpace(context, fromPackageName).put(RESULT_YUKI_LOGGER_INMEMORY_DATA, YLog.inMemoryData)
            }
        }
    }

    /**
     * Gets a namespace.
     * @param context the context instance.
     * @param packageName the target host app package name.
     * @return [NameSpace]
     */
    internal fun nameSpace(context: Context? = null, packageName: String): NameSpace {
        checkApi()
        return NameSpace(context = context ?: receiverContext, packageName)
    }

    /**
     * Temporary collection for segmented data.
     * @param listData the [List] data segments.
     * @param mapData the [Map] data segments.
     * @param setData the [Set] data segments.
     * @param stringData the [String] data segments.
     */
    internal inner class SegmentsTempData(
        var listData: MutableList<List<*>> = mutableListOf(),
        var mapData: MutableList<Map<*, *>> = mutableListOf(),
        var setData: MutableList<Set<*>> = mutableListOf(),
        var stringData: MutableList<String> = mutableListOf()
    )

    /**
     * [YukiHookDataChannel] namespace.
     *
     * - Use [nameSpace] to obtain [NameSpace].
     * @param context the context instance.
     * @param packageName the target host app package name.
     */
    inner class NameSpace internal constructor(private val context: Context?, private val packageName: String) {

        /** The current temporary segmented data. */
        private val segmentsTempData = ConcurrentHashMap<String, SegmentsTempData>()

        /**
         * Gets the key-value suffix.
         * @param type the callback type.
         * @return [String]
         */
        private fun keyShortName(type: CallbackKeyType) =
            "${keyNonRepeatName}_${if (isXposedEnvironment) "X" else context?.javaClass?.name ?: "M"}_${type.ordinal}"

        /**
         * Unique key-value name that prevents names used by different host apps from interfering with one another.
         * @return [String]
         */
        private val keyNonRepeatName get() = "_${packageName.hashCode()}"

        /**
         * Creates an invocation scope.
         * @param initiate the invocation block.
         * @return [NameSpace] this namespace for chaining.
         */
        inline fun with(initiate: NameSpace.() -> Unit) = apply(initiate)

        /**
         * Maximum data size in bytes that [YukiHookDataChannel] may send.
         *
         * The default is 500 KB (500 * 1024). See the documentation for [receiverDataMaxByteSize].
         *
         * The minimum is 100 KB (100 * 1024). Lower values are reset to 100 KB.
         *
         * The value applies globally until the current process ends.
         *
         * - Data larger than this maximum is sent in segments automatically.
         *
         * - Warning: adjust this value carefully. Exceeding the system limit causes [TransactionTooLargeException].
         * @return [Int]
         */
        var dataMaxByteSize
            get() = receiverDataMaxByteSize
            set(value) {
                receiverDataMaxByteSize = if (value < 100 * 1024) 100 * 1024 else value
            }

        /**
         * Maximum data-size compression factor that [YukiHookDataChannel] may use for segmented data.
         *
         * The default is 3. See the documentation for [receiverDataMaxByteCompressionFactor].
         *
         * The minimum is 2. Lower values are reset to 2.
         *
         * The value applies globally until the current process ends.
         *
         * - Data larger than the maximum is divided automatically by this factor relative to [receiverDataMaxByteSize].
         *
         * - Warning: adjust this value carefully. Exceeding the system limit causes [TransactionTooLargeException].
         * @return [Int]
         */
        var dataMaxByteCompressionFactor
            get() = receiverDataMaxByteCompressionFactor
            set(value) {
                receiverDataMaxByteCompressionFactor = if (value < 2) 2 else value
            }

        /**
         * Removes the outgoing data-size limit and disables segmented sending.
         *
         * This applies only to the current invocation. The feature is disabled automatically unless called again next time.
         *
         * Declare [SendTooLargeChannelData] across the invocation scope to suppress the warning.
         *
         * - Do not use this feature unless you understand its consequences.
         * @return [NameSpace]
         */
        @SendTooLargeChannelData
        fun allowSendTooLargeData(): NameSpace {
            isAllowSendTooLargeData = true
            return this
        }

        /**
         * Sends key-value data.
         * @param key the key name.
         * @param value the value data.
         */
        fun <T> put(key: String, value: T) = parseSendingData(ChannelData(key, value).toWrapper())

        /**
         * Sends key-value data.
         * @param data the key-value instance.
         * @param value the value data, [ChannelData.value] when omitted.
         */
        fun <T> put(data: ChannelData<T>, value: T? = data.value) = parseSendingData(ChannelData(data.key, value).toWrapper())

        /**
         * Sends key-value data.
         * @param data the key-value instances.
         */
        fun put(vararg data: ChannelData<*>) = data.takeIf { it.isNotEmpty() }?.forEach { parseSendingData(it.toWrapper()) }

        /**
         * Sends a key-value listener request only, using [VALUE_WAIT_FOR_LISTENER] as the value.
         * @param key the key name.
         */
        fun put(key: String) = put(key, VALUE_WAIT_FOR_LISTENER)

        /**
         * Receives key-value data.
         * @param key the key name.
         * @param priority the response priority, unset by default.
         * @param result the result-data callback.
         */
        fun <T> wait(key: String, priority: ChannelPriority? = null, result: (value: T) -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.SINGLE)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        parseReceivedData(intent.getDataWrapper(key), result)
            }
        }

        /**
         * Receives key-value data.
         * @param data the key-value instance.
         * @param priority the response priority, unset by default.
         * @param result the result-data callback.
         */
        fun <T> wait(data: ChannelData<T>, priority: ChannelPriority? = null, result: (value: T) -> Unit) {
            receiverCallbacks[data.key + keyShortName(CallbackKeyType.CDATA)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        parseReceivedData(intent.getDataWrapper(data.key), result)
            }
        }

        /**
         * Receives only a listener result without key-value data.
         *
         * - Only listeners sent with [VALUE_WAIT_FOR_LISTENER] can be received.
         * @param key the key name.
         * @param priority the response priority, unset by default.
         * @param callback the result callback.
         */
        fun wait(key: String, priority: ChannelPriority? = null, callback: () -> Unit) {
            receiverCallbacks[key + keyShortName(CallbackKeyType.VMFL)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isXposedEnvironment) hostActionName(packageName) else moduleActionName(context))
                        intent.getDataWrapper<String>(key)?.let { if (it.instance.value == VALUE_WAIT_FOR_LISTENER) callback() }
            }
        }

        /**
         * Gets whether the module and host app versions match.
         *
         * This method detects a mismatch caused when the Xposed module is updated but the host app has not reloaded it.
         * @param priority the response priority, unset by default.
         * @param result the callback receiving whether the versions match.
         */
        fun checkingVersionEquals(priority: ChannelPriority? = null, result: (Boolean) -> Unit) {
            wait<String>(RESULT_MODULE_GENERATED_VERSION, priority) { result(it == moduleGeneratedVersion) }
            put(GET_MODULE_GENERATED_VERSION, packageName)
        }

        /**
         * Gets [List]<[YLogData]> data exchanged between the module and host app.
         *
         * Because the module and host app run in different processes, this bridge provides access to each process's debug log data.
         *
         * - Both the module and host app must enable [YLog.Configs.isRecord] to obtain debug log data.
         *
         * - Android limits transfer size. Large debug logs are sent in segments automatically, and larger data transfers more slowly.
         * @param priority the response priority, unset by default.
         * @param result the [List]<[YLogData]> callback.
         */
        fun obtainLoggerInMemoryData(priority: ChannelPriority? = null, result: (List<YLogData>) -> Unit) {
            wait(RESULT_YUKI_LOGGER_INMEMORY_DATA, priority) { result(it) }
            put(GET_YUKI_LOGGER_INMEMORY_DATA, packageName)
        }

        /**
         * Converts arbitrary received data from an [Intent] into a [ChannelDataWrapper]<[T]> instance.
         * @param key the key name.
         * @return [ChannelDataWrapper]<[T]> or null.
         */
        private fun <T> Intent.getDataWrapper(key: String) = runCatching {
            extras?.getSerializableCompat<ChannelDataWrapper<T>>(key + keyNonRepeatName)
        }.getOrNull()

        /**
         * Converts [ChannelData]<[T]> into a [ChannelDataWrapper]<[T]> instance.
         * @param id the wrapper instance ID, [RandomSeed.createString] by default.
         * @param size the total segmented-data size, -1 by default.
         * @param index the currently received segment index, -1 by default.
         * @return [ChannelDataWrapper]<[T]>
         */
        private fun <T> ChannelData<T>.toWrapper(id: String = RandomSeed.createString(), size: Int = -1, index: Int = -1) =
            ChannelDataWrapper(id, size > 0, size, index, this)

        /**
         * Calculates the size in bytes occupied by any supported type.
         * @return [Int] the byte size.
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
         * Processes received broadcast data.
         * @param wrapper the key-value data wrapper.
         * @param result the result-data callback.
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
         * Processes broadcast data to send.
         * @param wrapper the key-value data wrapper.
         */
        private fun parseSendingData(wrapper: ChannelDataWrapper<*>) {
            if (YukiHookAPI.Configs.isEnableDataChannel.not()) return
            /** The current wrapper instance ID. */
            val wrapperId = RandomSeed.createString()

            /** The size in bytes of the data to send. */
            val dataByteSize = wrapper.instance.calDataByteSize()
            if (dataByteSize < 0 && isAllowSendTooLargeData.not()) return YLog.innerE(
                msg = "YukiHookDataChannel cannot calculate the byte size of the data key of \"${wrapper.instance.key}\" to be sent, " +
                    "so this data cannot be sent\n" +
                    "If you want to lift this restriction, use the allowSendTooLargeData function when calling, " +
                    "but this may cause the app crash"
            )
            /**
             * Prints a warning when data is too large. This is effective only when [YukiHookAPI.Configs.isDebug] is enabled.
             * @param name the data type name.
             * @param size the total number of segments.
             */
            fun loggerForTooLargeData(name: String, size: Int) {
                if (YukiHookAPI.Configs.isDebug) YLog.innerW(
                    msg = "This data key of \"${wrapper.instance.key}\" type $name is too large (total ${dataByteSize / 1024f} KB, " +
                        "limit ${receiverDataMaxByteSize / 1024f} KB), will be segmented to $size piece to send"
                )
            }

            /**
             * Prints an error when data is too large and cannot be segmented.
             * @param suggestionMessage the optional suggestion, empty by default.
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
             * Prints an error when the first element is too large to segment and the segmented array is empty.
             * @param name the data type name.
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
                        /** Characters are counted as two bytes, so divide the byte-size limit by two. */
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
         * Sends a broadcast.
         * @param wrapper the key-value data wrapper.
         */
        private fun pushReceiver(wrapper: ChannelDataWrapper<*>) {
            // Sends the broadcast.
            (context ?: AppParasitics.currentApplication)?.sendBroadcast {
                action = if (isXposedEnvironment) moduleActionName() else hostActionName(packageName)
                // The system framework package name may not be unique, so its broadcasts do not set a recipient package.
                if (packageName != AppParasitics.SYSTEM_FRAMEWORK_NAME)
                    setPackage(if (isXposedEnvironment) YukiXposedModule.modulePackageName else packageName)
                putExtra(wrapper.instance.key + keyNonRepeatName, wrapper)
            } ?: YLog.innerE("Failed to sendBroadcast like \"${wrapper.instance.key}\", because got null context in \"$packageName\"")
        }
    }
}