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
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.xposed.channel

import android.app.ActivityManager
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.os.TransactionTooLargeException
import androidx.core.content.getSystemService
import androidx.core.os.use
import com.highcapable.betterandroid.system.extension.component.BroadcastReceiver
import com.highcapable.betterandroid.system.extension.component.getSerializableCompat
import com.highcapable.betterandroid.system.extension.component.registerReceiver
import com.highcapable.betterandroid.system.extension.component.sendBroadcast
import com.highcapable.betterandroid.ui.extension.component.hostActivity
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.utils.RandomString
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.channel.annotation.SendTooLargeChannelData
import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import com.highcapable.yukihookapi.hook.xposed.channel.data.wrapper.ChannelDataWrapper
import com.highcapable.yukihookapi.hook.xposed.channel.priority.ChannelPriority
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

/**
 * Data channel between the Xposed module and host.
 *
 * Exchanges data by registering [BroadcastReceiver] instances in both the module and host.
 *
 * The module's [Application] must inherit [ModuleApplication] to use this feature.
 *
 * - Require both the module and host remain alive.
 */
class YukiHookDataChannel private constructor() {

    internal companion object {

        /** Whether the current environment is a host environment. */
        private val isHostEnvironment = XposedModuleRuntime.isHostEnvironment

        /** Automatically generated Xposed module build version. */
        private val moduleGeneratedVersion = YukiHookAPI.Status.compiledTimestamp.toString()

        /** Tag for requesting the module build version. */
        private const val GET_MODULE_GENERATED_VERSION = "module_generated_version_get"

        /** Tag for the module build version result. */
        private const val RESULT_MODULE_GENERATED_VERSION = "module_generated_version_result"

        /** Tag for requesting debug log data. */
        private const val GET_YUKI_LOGGER_IN_MEMORY_DATA = "yuki_logger_in_memory_data_get"

        /** Tag for the debug log data result. */
        private val RESULT_YUKI_LOGGER_IN_MEMORY_DATA = ChannelData<List<YLogData>>("yuki_logger_in_memory_data_result")

        /** Value used for listener-only results. */
        private const val VALUE_WAIT_FOR_LISTENER = "wait_for_listener_value"

        /**
         * Maximum data size in bytes allowed for a system broadcast.
         *
         * The standard limit is 1 MB. Tests show known limits of 1, 2, or 3 MB across different systems.
         * Tests also show that sending 900 KB in segments still fails on one Android 13 device.
         * To accommodate system-specific limits, the default is 500 KB. Larger data is sent in segments of this size.
         */
        private var receiverDataMaxByteSize = 500 * 1024

        /**
         * Maximum data-size compression factor for system broadcasts using segmented data.
         *
         * Each segment must also remain within [receiverDataMaxByteSize].
         * This factor is used to divide [receiverDataMaxByteSize].
         * The formula is [receiverDataMaxByteSize] / [receiverDataMaxByteCompressionFactor] = [receiverDataSegmentMaxByteSize].
         */
        private var receiverDataMaxByteCompressionFactor = 3

        /**
         * Gets the maximum size in bytes allowed for one segmented system-broadcast payload.
         * @return [Int]
         */
        private val receiverDataSegmentMaxByteSize get() = receiverDataMaxByteSize / receiverDataMaxByteCompressionFactor

        /** The current [YukiHookDataChannel] singleton. */
        @Suppress("StaticFieldLeak")
        private val instance = YukiHookDataChannel()

        /**
         * Gets the [YukiHookDataChannel] singleton.
         * @return [YukiHookDataChannel]
         */
        fun get() = instance
    }

    /**
     * Listener type for key-value callbacks.
     */
    private enum class CallbackKeyType {
        SINGLE,
        CDATA,
        WAIT_FOR_LISTENER
    }

    /** Registered broadcast callbacks. */
    private val receiverCallbacks = ConcurrentHashMap<String, Pair<Context?, (String, Intent) -> Unit>>()

    /** The [Context] currently used to register broadcasts. */
    private var receiverContext: Context? = null

    /** The broadcast receiver. */
    private var handlerReceiver: BroadcastReceiver? = null

    /** The broadcast receiver callback. */
    private val handlerReceiverCallback = BroadcastReceiver { _, intent ->
        intent.action?.also { action ->
            runCatching {
                receiverCallbacks.forEach { (key, param) ->
                    when {
                        param.first?.hostActivity?.isDestroyed == true -> receiverCallbacks.remove(key, param)
                        isCurrentBroadcast(param.first) -> param.second(action, intent)
                    }
                }
            }.onFailure { YLog.innerE("Received action \"$action\" failed", it) }
        }
    }

    /** Checks the API loading state. */
    private fun checkApi() {
        require(!YukiHookAPI.isStandalone) {
            "YukiHookDataChannel not allowed in standalone hook API."
        }
        if (isHostEnvironment) require(XposedModuleRuntime.modulePackageName.isNotBlank()) {
            "Xposed modulePackageName load failed, please reset and rebuild it."
        }
    }

    /**
     * Checks whether this broadcast callback event is currently active.
     * @param context the current instance.
     * @return [Boolean]
     */
    private fun isCurrentBroadcast(context: Context?) = runCatching {
        @Suppress("DEPRECATION")
        context is Application || isHostEnvironment || ((context ?: receiverContext)
            ?.getSystemService<ActivityManager>()
            ?.getRunningTasks(9999)?.any { context?.javaClass?.name == it?.topActivity?.className } == true)
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
    private fun moduleActionName(context: Context? = null) = "yuki_hook_module_data_channel_${XposedModuleRuntime.modulePackageName.ifBlank {
        context?.packageName ?: ""
    }.trim().hashCode()}"

    /**
     * Registers broadcasts.
     * @param context the global context of the target host or module. Registration stops when null.
     * @param packageName the package name. When empty, it is obtained from [Context.getPackageName] on [context].
     */
    internal fun register(context: Context?, packageName: String = context?.packageName ?: "") {
        if (!YukiHookAPI.Configs.dataChannel || context == null) return

        receiverContext = context
        val filter = IntentFilter().apply {
            addAction(if (isHostEnvironment) hostActionName(packageName) else moduleActionName(context))
        }
        handlerReceiver = context.registerReceiver(filter, exported = true, body = handlerReceiverCallback)

        // Prevents the module from registering its own broadcast in the module environment.
        if (!isHostEnvironment) return

        namespace(context, packageName).with {
            // Registers a listener that checks whether module and host versions match.
            wait<String>(GET_MODULE_GENERATED_VERSION) { fromPackageName ->
                namespace(context, fromPackageName).put(RESULT_MODULE_GENERATED_VERSION, moduleGeneratedVersion)
            }
            // Registers a listener for debug log data exchanged between the module and host.
            wait<String>(GET_YUKI_LOGGER_IN_MEMORY_DATA) { fromPackageName ->
                namespace(context, fromPackageName).put(RESULT_YUKI_LOGGER_IN_MEMORY_DATA, YLog.createSnapshot())
            }
        }
    }

    /** Unregisters broadcasts. */
    internal fun unregister() {
        handlerReceiver?.let { receiverContext?.unregisterReceiver(it) }
        receiverCallbacks.clear()
        receiverContext = null
    }

    /**
     * Gets a namespace.
     * @param context the context instance.
     * @param packageName the target host package name.
     * @return [Namespace]
     */
    internal fun namespace(context: Context? = null, packageName: String): Namespace {
        checkApi()
        return Namespace(context = context ?: receiverContext, packageName)
    }

    /**
     * Temporary collection for segmented data.
     * @param listData the [List] data segments.
     * @param mapData the [Map] data segments.
     * @param setData the [Set] data segments.
     * @param stringData the [String] data segments.
     */
    internal class SegmentsTempData(
        var listData: MutableList<List<*>>? = null,
        var mapData: MutableList<Map<*, *>>? = null,
        var setData: MutableList<Set<*>>? = null,
        var stringData: MutableList<String>? = null
    )

    /**
     * [YukiHookDataChannel] namespace.
     *
     * - Use [namespace] to obtain [Namespace].
     * @param context the context instance.
     * @param packageName the target host package name.
     */
    inner class Namespace internal constructor(private val context: Context?, private val packageName: String) {

        /** The current temporary segmented data. */
        private val segmentsTempData = ConcurrentHashMap<String, SegmentsTempData>()

        /** Whether sending from this namespace may bypass the data-size limit. */
        private var allowSendTooLargeData = false

        /**
         * Gets the key-value suffix.
         * @param type the callback type.
         * @return [String]
         */
        private fun keyShortName(type: CallbackKeyType) = "${keyNonRepeatName}_${
            if (isHostEnvironment) "X" else context?.javaClass?.name ?: "M"
        }_${type.ordinal}"

        /**
         * Unique key-value name that prevents names used by different hosts from interfering with one another.
         * @return [String]
         */
        private val keyNonRepeatName get() = "_${packageName.hashCode()}"

        /**
         * Creates an invocation scope.
         * @param block the invocation block.
         * @return [Namespace] this namespace for chaining.
         */
        inline fun with(block: Namespace.() -> Unit) = apply(block)

        /**
         * Maximum data size in bytes that [YukiHookDataChannel] may send before segmenting it.
         *
         * The default is 500 KB (500 * 1024). See the documentation for [receiverDataMaxByteSize].
         * The minimum is 100 KB (100 * 1024). Lower values are reset to 100 KB.
         * The value applies globally until the current process ends.
         *
         * - Data larger than this maximum is sent in segments automatically.
         * - Warning: adjust this value carefully. Exceeding the system limit causes [TransactionTooLargeException].
         * @return [Int]
         */
        var maxDataSizeBytes
            get() = receiverDataMaxByteSize
            set(value) {
                receiverDataMaxByteSize = if (value < 100 * 1024) 100 * 1024 else value
            }

        /**
         * Divisor used to calculate the size of each data segment.
         *
         * The default is 3. See the documentation for [receiverDataMaxByteCompressionFactor].
         * The minimum is 2. Lower values are reset to 2.
         * The value applies globally until the current process ends.
         *
         * - [receiverDataMaxByteSize] is divided by this value to determine the target size of each segment.
         * - Warning: adjust this value carefully. Exceeding the system limit causes [TransactionTooLargeException].
         * @return [Int]
         */
        var segmentSizeDivisor
            get() = receiverDataMaxByteCompressionFactor
            set(value) {
                receiverDataMaxByteCompressionFactor = if (value < 2) 2 else value
            }

        /**
         * Removes the outgoing data-size limit and disables segmented sending.
         *
         * Once enabled, this applies to every subsequent sending invocation from the current [Namespace] instance.
         * Obtain a new [Namespace] instance to restore the data-size limit.
         * Declare [SendTooLargeChannelData] across the invocation scope to suppress the warning.
         *
         * - Do not use this feature unless you understand its consequences.
         * @return [Namespace]
         */
        @SendTooLargeChannelData
        fun allowOversizedData(): Namespace {
            allowSendTooLargeData = true
            return this
        }

        /**
         * Sends key-value data.
         * @param key the key name.
         * @param value the value data.
         */
        fun <T> put(key: String, value: T) = parseSendingData(
            ChannelData(key, value).toWrapper(),
            allowSendTooLargeData
        )

        /**
         * Sends key-value data.
         * @param data the key-value instance.
         * @param value the value data, [ChannelData.value] when omitted.
         */
        fun <T> put(data: ChannelData<T>, value: T? = data.value) = parseSendingData(
            ChannelData(data.key, value).toWrapper(),
            allowSendTooLargeData
        )

        /**
         * Sends key-value data.
         * @param data the key-value instances.
         */
        fun put(vararg data: ChannelData<*>) = data.takeIf { it.isNotEmpty() }?.let {
            it.forEach { item -> parseSendingData(item.toWrapper(), allowSendTooLargeData) }
        }

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
                    if (action == if (isHostEnvironment) hostActionName(packageName) else moduleActionName(context))
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
                    if (action == if (isHostEnvironment) hostActionName(packageName) else moduleActionName(context))
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
            receiverCallbacks[key + keyShortName(CallbackKeyType.WAIT_FOR_LISTENER)] = Pair(context) { action, intent ->
                if (priority == null || priority.result)
                    if (action == if (isHostEnvironment) hostActionName(packageName) else moduleActionName(context))
                        intent.getDataWrapper<String>(key)?.let { if (it.instance.value == VALUE_WAIT_FOR_LISTENER) callback() }
            }
        }

        /**
         * Checks whether the module and host versions match.
         *
         * This method detects a mismatch caused when the Xposed module is updated but the host has not reloaded it.
         * @param priority the response priority, unset by default.
         * @param result the callback receiving whether the versions match.
         */
        fun checkVersionMatch(priority: ChannelPriority? = null, result: (Boolean) -> Unit) {
            wait<String>(RESULT_MODULE_GENERATED_VERSION, priority) { result(it == moduleGeneratedVersion) }
            put(GET_MODULE_GENERATED_VERSION, packageName)
        }

        /**
         * Gets [List]<[YLogData]> data exchanged between the module and host.
         *
         * Because the module and host run in different processes, this bridge provides access to each process's debug log data.
         *
         * - Both the module and host must enable [YLog.Configs.recording] to obtain debug log data.
         * - Android limits transfer size. Large debug logs are sent in segments automatically, and larger data transfers more slowly.
         * @param priority the response priority, unset by default.
         * @param result the [List]<[YLogData]> callback.
         */
        fun obtainLoggerInMemoryData(priority: ChannelPriority? = null, result: (List<YLogData>) -> Unit) {
            wait(RESULT_YUKI_LOGGER_IN_MEMORY_DATA, priority) { result(it) }
            put(GET_YUKI_LOGGER_IN_MEMORY_DATA, packageName)
        }

        // region Deprecated properties and functions for backward compatibility

        /** Maximum data size in bytes that [YukiHookDataChannel] may send before segmenting it. */
        @Deprecated(message = "Use maxDataSizeBytes instead.", replaceWith = ReplaceWith("maxDataSizeBytes"))
        var dataMaxByteSize
            get() = maxDataSizeBytes
            set(value) {
                maxDataSizeBytes = value
            }

        /** Divisor used to calculate the size of each data segment. */
        @Deprecated(message = "Use segmentSizeDivisor instead.", replaceWith = ReplaceWith("segmentSizeDivisor"))
        var dataMaxByteCompressionFactor
            get() = segmentSizeDivisor
            set(value) {
                segmentSizeDivisor = value
            }

        /** Removes the outgoing data-size limit through [allowOversizedData]. */
        @Deprecated(message = "Use allowOversizedData instead.", replaceWith = ReplaceWith("allowOversizedData()"))
        @SendTooLargeChannelData
        fun allowSendTooLargeData() = allowOversizedData()

        /** Checks whether the module and host versions match. */
        @Deprecated(message = "Use checkVersionMatch instead.", replaceWith = ReplaceWith("checkVersionMatch(priority, result)"))
        fun checkingVersionEquals(priority: ChannelPriority? = null, result: (Boolean) -> Unit) = checkVersionMatch(priority, result)

        // endregion

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
         * @param id the wrapper instance ID, [RandomString.generate] by default.
         * @param size the total segmented-data size, -1 by default.
         * @param index the currently received segment index, -1 by default.
         * @return [ChannelDataWrapper]<[T]>
         */
        private fun <T> ChannelData<T>.toWrapper(id: String = RandomString.generate(), size: Int = -1, index: Int = -1) =
            ChannelDataWrapper(id, size > 0, size, index, this)

        /**
         * Calculates the size in bytes occupied by any supported type.
         * @receiver [Any]
         * @return [Int] the byte size.
         */
        private fun Any.calDataByteSize(): Int {
            val key = if (this is ChannelData<*>) key else "placeholder"
            val value = if (this is ChannelData<*>) value else this
            val bundle = Bundle().apply {
                // Bundle chooses the Parcel encoding from the stored value, regardless of the put overload.
                when (value) {
                    null -> Unit
                    is Serializable -> putSerializable(key, value)
                    is CharSequence -> putCharSequence(key, value)
                    is Parcelable -> putParcelable(key, value)
                    else -> error("Key-Value type ${value.javaClass.name} is not allowed.")
                }
            }

            return runCatching {
                Parcel.obtain().use { parcel -> 
                    bundle.calDataByteSize(parcel)
                }
            }.getOrNull() ?: -1
        }

        /** Measures a bundle using the supplied scratch parcel. */
        private fun Bundle.calDataByteSize(parcel: Parcel) = runCatching {
            parcel.setDataPosition(0)
            parcel.setDataSize(0)
            parcel.writeBundle(this)
            parcel.dataSize()
        }.getOrNull() ?: -1

        /** Splits a collection while preserving the existing per-element size checks and segment types. */
        private inline fun <T, C : Serializable> Iterable<T>.toSegments(create: () -> C, append: C.(T) -> Unit): List<C> {
            val segments = mutableListOf<C>()
            val bundle = Bundle()

            Parcel.obtain().use { parcel ->
                var segment: C? = null
                for (element in this) {
                    val current = segment ?: create().also {
                        segment = it
                        bundle.putSerializable("placeholder", it)
                    }
                    current.append(element)
                    if (bundle.calDataByteSize(parcel) >= receiverDataSegmentMaxByteSize) {
                        segments.add(current)
                        segment = null
                    }
                }
                segment?.let(segments::add)
            }
            return segments
        }

        /**
         * Processes received broadcast data.
         * @param wrapper the key-value data wrapper.
         * @param result the result-data callback.
         */
        private fun <T> parseReceivedData(wrapper: ChannelDataWrapper<T>?, result: (value: T) -> Unit) {
            if (!YukiHookAPI.Configs.dataChannel) return
            if (wrapper == null) return

            if (wrapper.isSegmentsType) runCatching {
                val tempData = segmentsTempData[wrapper.wrapperId] ?: SegmentsTempData().apply { segmentsTempData[wrapper.wrapperId] = this }
                when (val value = wrapper.instance.value) {
                    is List<*> -> {
                        val segments = tempData.listData ?: mutableListOf<List<*>>().also { tempData.listData = it }
                        mergeReceivedSegments(wrapper, value, segments, result) { data ->
                            ArrayList<Any?>(data.sumOf { it.size }).apply {
                                for (segment in data) for (element in segment) add(element)
                            }
                        }
                    }
                    is Map<*, *> -> {
                        val segments = tempData.mapData ?: mutableListOf<Map<*, *>>().also { tempData.mapData = it }
                        mergeReceivedSegments(wrapper, value, segments, result) { data ->
                            LinkedHashMap<Any?, Any?>(data.sumOf { it.size }).apply {
                                for (segment in data) putAll(segment)
                            }
                        }
                    }
                    is Set<*> -> {
                        val segments = tempData.setData ?: mutableListOf<Set<*>>().also { tempData.setData = it }
                        mergeReceivedSegments(wrapper, value, segments, result) { data ->
                            LinkedHashSet<Any?>(data.sumOf { it.size }).apply {
                                for (segment in data) addAll(segment)
                            }
                        }
                    }
                    is String -> {
                        val segments = tempData.stringData ?: mutableListOf<String>().also { tempData.stringData = it }
                        mergeReceivedSegments(wrapper, value, segments, result) { data ->
                            buildString(data.sumOf { it.length }) {
                                for (segment in data) append(segment)
                            }
                        }
                    }
                    else -> YLog.innerE("Unsupported segments data key of \"${wrapper.instance.key}\"'s type")
                }
            }.onFailure {
                YLog.innerE("YukiHookDataChannel cannot merge this segments data key of \"${wrapper.instance.key}\"", it)
            } else wrapper.instance.value?.let { e -> result(e) }
        }

        /** Merges a complete set of segments before delivering and releasing it. */
        private inline fun <T, V> mergeReceivedSegments(
            wrapper: ChannelDataWrapper<T>,
            value: V,
            segments: MutableList<V>,
            result: (T) -> Unit,
            merge: (List<V>) -> Any
        ) {
            if (segments.isEmpty() && wrapper.segmentsIndex > 0) return

            segments.add(wrapper.segmentsIndex, value)
            if (segments.size == wrapper.segmentsSize) {
                result(merge(segments) as T)
                segments.clear()
                segmentsTempData.remove(wrapper.wrapperId)
            }
        }

        /**
         * Processes broadcast data to send.
         * @param wrapper the key-value data wrapper.
         * @param allowSendTooLargeData whether to allow sending data larger than the system limit.
         */
        private fun parseSendingData(wrapper: ChannelDataWrapper<*>, allowSendTooLargeData: Boolean) {
            if (!YukiHookAPI.Configs.dataChannel) return

            // The current wrapper instance ID.
            val wrapperId = RandomString.generate()
            // The size in bytes of the data to send.
            val dataByteSize = wrapper.instance.calDataByteSize()
            if (dataByteSize < 0 && !allowSendTooLargeData) return YLog.innerE(
                msg = "YukiHookDataChannel cannot calculate the byte size of the data key of \"${wrapper.instance.key}\" to be sent, " +
                    "so this data cannot be sent.\n" +
                    "If you want to lift this restriction, use the allowOversizedData function when calling, " +
                    "but this may cause the app crash"
            )

            /**
             * Logs a warning when data is too large. This is effective only when [YukiHookAPI.Configs.debug] is enabled.
             * @param name the data type name.
             * @param size the total number of segments.
             */
            fun logForTooLargeData(name: String, size: Int) {
                if (YukiHookAPI.Configs.debug) YLog.innerW(
                    msg = "This data key of \"${wrapper.instance.key}\" type $name is too large (total ${dataByteSize / 1024f} KB, " +
                        "limit ${receiverDataMaxByteSize / 1024f} KB), will be segmented to $size piece to send"
                )
            }

            /**
             * Logs an error when data is too large and cannot be segmented.
             * @param suggestionMessage the optional suggestion, empty by default.
             */
            fun logForUnprocessableData(suggestionMessage: String = "") = YLog.innerE(
                msg = "YukiHookDataChannel cannot send this data key of \"${wrapper.instance.key}\" type ${wrapper.instance.value?.javaClass}, " +
                    "because it is too large (total ${dataByteSize / 1024f} KB, " +
                    "limit ${receiverDataMaxByteSize / 1024f} KB) and cannot be segmented.\n" +
                    (if (suggestionMessage.isNotBlank()) "$suggestionMessage\n" else "") +
                    "If you want to lift this restriction, use the allowOversizedData function when calling, " +
                    "but this may cause the app crash"
            )

            /**
             * Logs an error when the first element is too large to segment and the segmented array is empty.
             * @param name the data type name.
             */
            fun logForUnprocessableDataByFirstElement(name: String) = logForUnprocessableData(
                suggestionMessage = "Failed to segment $name type because the size of its first element has exceeded the maximum limit"
            )
            when {
                wrapper.isSegmentsType || allowSendTooLargeData -> sendBroadcast(wrapper)
                dataByteSize >= receiverDataMaxByteSize -> {
                    val name: String
                    val segments = when (val value = wrapper.instance.value) {
                        is List<*> -> {
                            name = "List"
                            value.toSegments(create = { ArrayList<Any?>() }) { add(it) }
                        }
                        is Map<*, *> -> {
                            name = "Map"
                            value.entries.toSegments(create = { LinkedHashMap<Any?, Any?>() }) { put(it.key, it.value) }
                        }
                        is Set<*> -> {
                            name = "Set"
                            value.toSegments(create = { LinkedHashSet<Any?>() }) { add(it) }
                        }
                        is String -> {
                            // Preserve the existing character boundaries, including the trailing empty segment.
                            val twoByteMaxSize = receiverDataMaxByteSize / 2
                            val segmentsSize = value.length / twoByteMaxSize + 1
                            if (segmentsSize == 1) return sendBroadcast(wrapper)

                            logForTooLargeData(name = "String", segmentsSize)
                            repeat(segmentsSize) { index ->
                                val start = index * twoByteMaxSize
                                val segment = value.substring(start, minOf(start + twoByteMaxSize, value.length))
                                sendBroadcast(ChannelData(wrapper.instance.key, segment).toWrapper(wrapperId, segmentsSize, index))
                            }
                            return
                        }
                        else -> return logForUnprocessableData(
                            suggestionMessage = if (value?.javaClass?.isArray == true)
                                "Primitive Array type like String[], int[] ... cannot be segmented, " +
                                    "the suggestion is send those data using List type"
                            else ""
                        )
                    }
                    logForTooLargeData(name, segments.size)
                    if (segments.isEmpty()) return logForUnprocessableDataByFirstElement(name)

                    segments.forEachIndexed { index, segment ->
                        sendBroadcast(ChannelData(wrapper.instance.key, segment).toWrapper(wrapperId, segments.size, index))
                    }
                }
                else -> sendBroadcast(wrapper)
            }
        }

        /**
         * Sends a broadcast.
         * @param wrapper the key-value data wrapper.
         */
        private fun sendBroadcast(wrapper: ChannelDataWrapper<*>) {
            (context ?: AppParasitism.currentApplication)?.sendBroadcast {
                action = if (isHostEnvironment) moduleActionName() else hostActionName(packageName)

                // The system server package name may not be unique, so its broadcasts do not set a recipient package.
                if (packageName != AppParasitism.FRAMEWORK_PACKAGE_NAME)
                    setPackage(if (isHostEnvironment) XposedModuleRuntime.modulePackageName else packageName)
                putExtra(wrapper.instance.key + keyNonRepeatName, wrapper)
            } ?: YLog.innerE("Failed to sendBroadcast like \"${wrapper.instance.key}\", because got null context in \"$packageName\"")
        }
    }
}