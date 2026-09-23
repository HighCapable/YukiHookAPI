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
 * This file is created by fankes on 2023/9/27.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.log

import android.system.ErrnoException
import android.util.Log
import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.runtime.KavaRefRuntime
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import java.io.File
import java.util.Collections

/**
 * Global log manager.
 */
object YLog {

    /**
     * [YLog] configuration.
     */
    object Config {

        /**
         * Tag.
         *
         * Example output:
         *
         * ```
         * [YukiHook][...][...] ...
         * ```
         */
        const val TAG = 1000

        /**
         * Priority.
         *
         * Example output:
         *
         * ```
         * [...][E][...] ...
         * ```
         */
        const val PRIORITY = 1001

        /**
         * Current host package name.
         *
         * Example output:
         *
         * ```
         * [...][com.demo.test][...] ...
         * ```
         */
        const val PACKAGE_NAME = 1002

        /**
         * Current host user ID, omitted for the owner user.
         *
         * Example output:
         *
         * ```
         * [...][...][999] ...
         * ```
         */
        const val USER_ID = 1003

        /**
         * Global identifier for debug logs.
         *
         * The default value is [YukiHook.TAG].
         *
         * You can replace it with your own value.
         */
        var tag = YukiHook.TAG

        /**
         * Whether debug log output is enabled, true by default.
         *
         * - Disabling this stops all log output from [YukiHook].
         *
         * It does not affect logs printed manually through the following methods.
         *
         * [debug]、[info]、[warn]、[error]
         *
         * Disabling [enabled] also disables [YukiHook.Config.debug].
         */
        var enabled = true
            set(value) {
                field = value
                initKavaRefLoggerIfNot()
            }

        /**
         * Whether debug log recording is enabled, false by default.
         *
         * Enabling this records all available logs and exception stack traces in memory.
         *
         * [enabled] must also be enabled.
         *
         * - Excessive logging may slow down the host or cause frequent garbage collection.
         *
         * Once enabled, call [saveToFile] to save logs to a file in real time or use [contents] to obtain the current log contents.
         */
        var recording = false
            set(value) {
                field = value
                initKavaRefLoggerIfNot()
            }

        // region Deprecated properties for backward compatibility

        /** Whether debug log output is enabled. */
        @Deprecated(message = "Use enabled instead.", replaceWith = ReplaceWith("enabled"))
        var isEnable
            get() = enabled
            set(value) {
                enabled = value
            }

        /** Whether debug log recording is enabled. */
        @Deprecated(message = "Use recording instead.", replaceWith = ReplaceWith("recording"))
        var isRecord
            get() = recording
            set(value) {
                recording = value
            }

        // endregion

        /** The current ordered list of elements. */
        internal var elements = arrayOf(TAG, PRIORITY, PACKAGE_NAME, USER_ID)

        /**
         * Customizes the elements displayed in debug logs.
         *
         * This applies only to recorded logs and logs in the (Xposed) host environment.
         * Log elements are displayed in the order specified by [item].
         * Leave [item] empty to hide every element except the log message.
         *
         * Available elements are [TAG], [PRIORITY], [PACKAGE_NAME], and [USER_ID].
         *
         * Default order:
         * ```
         * [TAG][PRIORITY][PACKAGE_NAME][USER_ID] Message
         * ```
         * @param item the custom element array.
         */
        fun elements(vararg item: Int) {
            elements = arrayOf(*item.toTypedArray())
        }
    }

    /**
     * All currently recorded log data.
     *
     * - Log data is isolated between host and module processes.
     */
    val inMemoryData: MutableList<YLogData> = Collections.synchronizedList(mutableListOf())

    /**
     * Gets the current log file contents.
     *
     * Returns an empty string when no logs have been recorded.
     *
     * - Log data is isolated between host and module processes.
     * @return [String]
     */
    val contents get() = contents()

    /**
     * Returns a stable snapshot of the currently [inMemoryData].
     * @return [List]<[YLogData]>
     */
    internal fun createSnapshot() = synchronized(inMemoryData) { inMemoryData.toList() }

    /**
     * Gets and formats the current log file contents.
     *
     * Returns an empty string when no logs have been recorded and [data] is empty.
     *
     * - Log data is isolated between host and module processes.
     * @param data the log data, [inMemoryData] by default.
     * @return [String]
     */
    fun contents(data: List<YLogData> = inMemoryData): String {
        val currentData = if (data === inMemoryData) createSnapshot() else data

        return buildString {
            currentData.forEach {
                append("${it.head}$it\n")
                it.throwable?.also { e ->
                    append("${it.head}Dump stack trace for \"${e.javaClass.name}\":\n")
                    append(e.stackTraceToString())
                }
            }
        }
    }

    /**
     * Clears all recorded logs.
     *
     * You can also clear [inMemoryData] directly.
     *
     * - Log data is isolated between host and module processes.
     */
    fun clear() = inMemoryData.clear()

    /**
     * Saves the current logs to a file.
     *
     * Performs no operation when [Config.recording] is disabled or no logs have been recorded.
     * Logs are appended to [fileName], which is created automatically when it does not exist.
     *
     * - File access depends on the permissions granted to the current host or module.
     * @param fileName the full file name, for example `/data/data/.../files/xxx.log`.
     * @param data the log data, [inMemoryData] by default.
     * @throws ErrnoException if the target path is not writable.
     */
    fun saveToFile(fileName: String, data: List<YLogData> = inMemoryData) {
        val currentData = if (data === inMemoryData) createSnapshot() else data
        if (currentData.isNotEmpty()) File(fileName).appendText(contents(currentData))
    }

    /**
     * Prints a debug-level log.
     *
     * Prints to the console and the host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Config.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun debug(msg: Any? = null, e: Throwable? = null, tag: String = Config.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "D", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an info-level log.
     *
     * Prints to the console and the host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Config.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun info(msg: Any? = null, e: Throwable? = null, tag: String = Config.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "I", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints a warn-level log.
     *
     * Prints to the console and the host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Config.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun warn(msg: Any? = null, e: Throwable? = null, tag: String = Config.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "W", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an error-level log.
     *
     * Prints to the console and the host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param tag the log tag, preferably the module name. [Config.tag] by default.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun error(msg: Any? = null, e: Throwable? = null, tag: String = Config.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "E", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an internal debug-level [YukiHook] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerD(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (!Config.enabled || !YukiHook.Config.debug) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "D", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal info-level [YukiHook] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerI(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (!Config.enabled) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "I", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal warn-level [YukiHook] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerW(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (!Config.enabled) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "W", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal error-level [YukiHook] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerE(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (!Config.enabled) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "E", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Final implementation that prints logs to the console and the host environment.
     * @param env the log environment.
     * @param data the log data.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    private fun log(env: EnvType, data: YLogData, isImplicit: Boolean = false) {
        initKavaRefLoggerIfNot()

        // Whether this is a valid log.
        val isNotBlankLog = data.msg.isNotBlank() || (data.msg.isBlank() && data.throwable != null)

        // Android-compatible log priority.
        val priority = when (data.priority) {
            "D" -> Log.DEBUG
            "I" -> Log.INFO
            "W" -> Log.WARN
            "E" -> Log.ERROR
            else -> Log.ASSERT
        }

        /** Prints to [Log]. */
        fun logByLogd() = when (priority) {
            Log.DEBUG -> Log.d(data.tag, data.msg, data.throwable)
            Log.INFO -> Log.i(data.tag, data.msg, data.throwable)
            Log.WARN -> Log.w(data.tag, data.msg, data.throwable)
            Log.ERROR -> Log.e(data.tag, data.msg, data.throwable)
            else -> Log.wtf(data.tag, data.msg, data.throwable)
        }

        /** Prints to the host environment. */
        fun logByHooker() {
            if (isNotBlankLog) YukiHookHelper.log(
                priority = priority,
                tag = data.tag,
                msg = data.also { it.isImplicit = isImplicit }.toString(),
                e = data.throwable
            )
        }

        when (env) {
            EnvType.LOGD -> logByLogd()
            EnvType.XPOSED_ENVIRONMENT -> logByHooker()
            EnvType.SCOPE -> if (XposedModuleRuntime.isHostEnvironment) logByHooker() else logByLogd()
            EnvType.BOTH -> {
                logByLogd()
                if (XposedModuleRuntime.isHostEnvironment) logByHooker()
            }
        }
        if (!isImplicit && Config.recording && isNotBlankLog) inMemoryData.add(data)
    }

    /** Defines the [KavaRef] logger. */
    private val kavaRefLogger = object : KavaRefRuntime.Logger {

        override val tag get() = Config.tag

        override fun debug(msg: Any?, throwable: Throwable?) {
            this@YLog.debug(msg.toString(), throwable)
        }

        override fun error(msg: Any?, throwable: Throwable?) {
            this@YLog.error(msg.toString(), throwable)
        }

        override fun info(msg: Any?, throwable: Throwable?) {
            this@YLog.info(msg.toString(), throwable)
        }

        override fun warn(msg: Any?, throwable: Throwable?) {
            this@YLog.warn(msg.toString(), throwable)
        }
    }

    /** Whether the [KavaRef] logger has been initialized. */
    private var isKavaRefLoggerInit = false

    /** Initializes the [KavaRef] logger on the first call only. */
    private fun initKavaRefLoggerIfNot() {
        updateKavaRefLogLevel()
        if (isKavaRefLoggerInit) return

        KavaRef.setLogger(kavaRefLogger)
        isKavaRefLoggerInit = true
    }

    /** Updates the [KavaRef] logger level. */
    private fun updateKavaRefLogLevel() {
        KavaRef.logLevel = when {
            !Config.enabled -> KavaRefRuntime.LogLevel.OFF
            YukiHook.Config.debug -> KavaRefRuntime.LogLevel.DEBUG
            else -> KavaRefRuntime.LogLevel.INFO
        }
    }

    /**
     * Log output environment type.
     *
     * Determines how logs are printed in module and host environments.
     */
    enum class EnvType {
        /** Uses only [Log]. */
        LOGD,

        /**
         * Uses only the host environment.
         *
         * - Available only in the host environment and has no effect in the module environment.
         */
        XPOSED_ENVIRONMENT,

        /**
         * Uses environment-specific output.
         *
         * The host environment uses only [XPOSED_ENVIRONMENT].
         *
         * The module environment uses only [LOGD].
         */
        SCOPE,

        /**
         * Uses both outputs.
         *
         * The host environment uses [LOGD] and [XPOSED_ENVIRONMENT].
         *
         * The module environment uses only [LOGD].
         */
        BOTH
    }
}