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
 * This file is created by fankes on 2023/9/27.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.log

import android.system.ErrnoException
import android.util.Log
import com.highcapable.kavaref.KavaRef
import com.highcapable.kavaref.runtime.KavaRefRuntime
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.utils.factory.dumpToString
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import java.io.File

/**
 * Global log manager.
 */
object YLog {

    /**
     * [YLog] configuration.
     */
    object Configs {

        /**
         * Tag.
         *
         * Example output:
         *
         * ```
         * [YukiHookAPI][...][...] ...
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
         * Current host app package name.
         *
         * Example output:
         *
         * ```
         * [...][com.demo.test][...] ...
         * ```
         */
        const val PACKAGE_NAME = 1002

        /**
         * Current host app user ID, omitted for the owner user.
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
         * The default value is [YukiHookAPI.TAG].
         *
         * You can replace it with your own value.
         */
        var tag = YukiHookAPI.TAG

        /**
         * Whether debug log output is enabled, true by default.
         *
         * - Disabling this stops all log output from [YukiHookAPI].
         *
         * It does not affect logs printed manually through the following methods.
         *
         * [debug]、[info]、[warn]、[error]
         *
         * Disabling [isEnable] also disables [YukiHookAPI.Configs.isDebug].
         */
        var isEnable = true
            set(value) {
                field = value
                initKavaRefLoggerIfNot()
            }

        /**
         * Whether debug log recording is enabled, false by default.
         *
         * Enabling this records all available logs and exception stack traces in memory.
         *
         * [isEnable] must also be enabled.
         *
         * - Excessive logging may slow down the host app or cause frequent garbage collection.
         *
         * Once enabled, call [saveToFile] to save logs to a file in real time or use [contents] to obtain the current log contents.
         */
        var isRecord = false
            set(value) {
                field = value
                initKavaRefLoggerIfNot()
            }

        /** The current ordered list of elements. */
        internal var elements = arrayOf(TAG, PRIORITY, PACKAGE_NAME, USER_ID)

        /**
         * Customizes the elements displayed in debug logs.
         *
         * This applies only to recorded logs and logs in the (Xposed) host environment.
         *
         * Log elements are displayed in the order specified by [item].
         *
         * Leave [item] empty to hide every element except the log message.
         *
         * Available elements are [TAG], [PRIORITY], [PACKAGE_NAME], and [USER_ID].
         *
         * Default order:
         *
         * ```
         * [TAG][PRIORITY][PACKAGE_NAME][USER_ID] Message
         * ```
         * @param item the custom element array.
         */
        fun elements(vararg item: Int) {
            elements = arrayOf(*item.toTypedArray())
        }

        /** Completes the configuration block. */
        internal fun build() = Unit
    }

    /**
     * All currently recorded log data.
     *
     * - Log data is isolated between host app and module processes.
     */
    val inMemoryData = mutableListOf<YLogData>()

    /**
     * Gets the current log file contents.
     *
     * Returns an empty string when no logs have been recorded.
     *
     * - Log data is isolated between host app and module processes.
     * @return [String]
     */
    val contents get() = contents()

    /**
     * Gets and formats the current log file contents.
     *
     * Returns an empty string when no logs have been recorded and [data] is empty.
     *
     * - Log data is isolated between host app and module processes.
     * @param data the log data, [inMemoryData] by default.
     * @return [String]
     */
    fun contents(data: List<YLogData> = inMemoryData): String {
        var content = ""
        data.takeIf { it.isNotEmpty() }?.forEach {
            content += "${it.head}$it\n"
            it.throwable?.also { e ->
                content += "${it.head}Dump stack trace for \"${e.javaClass.name}\":\n"
                content += e.dumpToString()
            }
        }; return content
    }

    /**
     * Clears all recorded logs.
     *
     * You can also clear [inMemoryData] directly.
     *
     * - Log data is isolated between host app and module processes.
     */
    fun clear() = inMemoryData.clear()

    /**
     * Saves the current logs to a file.
     *
     * Performs no operation when [Configs.isRecord] is disabled or no logs have been recorded.
     *
     * Logs are appended to [fileName], which is created automatically when it does not exist.
     *
     * - File access depends on the permissions granted to the current host app or module.
     * @param fileName the full file name, for example `/data/data/.../files/xxx.log`.
     * @param data the log data, [inMemoryData] by default.
     * @throws ErrnoException if the target path is not writable.
     */
    fun saveToFile(fileName: String, data: List<YLogData> = inMemoryData) {
        if (data.isNotEmpty()) File(fileName).appendText(contents(data))
    }

    /**
     * Prints a debug-level log.
     *
     * Prints to the console and the (Xposed) host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Configs.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun debug(msg: Any? = null, e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "D", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an info-level log.
     *
     * Prints to the console and the (Xposed) host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Configs.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun info(msg: Any? = null, e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "I", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints a warn-level log.
     *
     * Prints to the console and the (Xposed) host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param tag the log tag, preferably the module name. [Configs.tag] by default.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun warn(msg: Any? = null, e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "W", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an error-level log.
     *
     * Prints to the console and the (Xposed) host environment.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param tag the log tag, preferably the module name. [Configs.tag] by default.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param env the log environment, [EnvType.BOTH] by default.
     */
    fun error(msg: Any? = null, e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "E", tag = tag, msg = msg.toString(), throwable = e))

    /**
     * Prints an internal debug-level [YukiHookAPI] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerD(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not() || YukiHookAPI.Configs.isDebug.not()) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "D", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal info-level [YukiHookAPI] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerI(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "I", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal warn-level [YukiHookAPI] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerW(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "W", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Prints an internal error-level [YukiHookAPI] log.
     * @param msg the log message, empty by default. Set only [e] to print just an exception stack trace.
     * @param e the optional exception stack trace, printed in full automatically.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    internal fun innerE(msg: Any? = null, e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return initKavaRefLoggerIfNot()
        log(EnvType.BOTH, YLogData(priority = "E", msg = msg.toString(), throwable = e), isImplicit)
    }

    /**
     * Final implementation that prints logs to the console and the (Xposed) host environment.
     * @param env the log environment.
     * @param data the log data.
     * @param isImplicit whether to print implicitly without recording or displaying the package name and user ID.
     */
    private fun log(env: EnvType, data: YLogData, isImplicit: Boolean = false) {
        initKavaRefLoggerIfNot()

        /** Whether this is a valid log. */
        val isNotBlankLog = data.msg.isNotBlank() || (data.msg.isBlank() && data.throwable != null)

        /** Prints to [Log]. */
        fun logByLogd() = when (data.priority) {
            "D" -> Log.d(data.tag, data.msg, data.throwable)
            "I" -> Log.i(data.tag, data.msg, data.throwable)
            "W" -> Log.w(data.tag, data.msg, data.throwable)
            "E" -> Log.e(data.tag, data.msg, data.throwable)
            else -> Log.wtf(data.tag, data.msg, data.throwable)
        }

        /** Prints to the (Xposed) host environment. */
        fun logByHooker() {
            if (isNotBlankLog) YukiHookHelper.logByHooker(data.also { it.isImplicit = isImplicit }.toString(), data.throwable)
        }
        when (env) {
            EnvType.LOGD -> logByLogd()
            EnvType.XPOSED_ENVIRONMENT -> logByHooker()
            EnvType.SCOPE -> if (YukiXposedModule.isXposedEnvironment) logByHooker() else logByLogd()
            EnvType.BOTH -> {
                logByLogd()
                if (YukiXposedModule.isXposedEnvironment) logByHooker()
            }
        }
        if (isImplicit.not() && Configs.isRecord && isNotBlankLog) inMemoryData.add(data)
    }

    /** Defines the [KavaRef] logger. */
    private val kavaRefLogger = object : KavaRefRuntime.Logger {

        override val tag get() = Configs.tag

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
            !Configs.isEnable -> KavaRefRuntime.LogLevel.OFF
            YukiHookAPI.Configs.isDebug -> KavaRefRuntime.LogLevel.DEBUG
            else -> KavaRefRuntime.LogLevel.INFO
        }
    }

    /**
     * Log output environment type.
     *
     * Determines how logs are printed in module and (Xposed) host environments.
     */
    enum class EnvType {
        /** Uses only [Log]. */
        LOGD,

        /**
         * Uses only the (Xposed) host environment.
         *
         * - Available only in the (Xposed) host environment and has no effect in the module environment.
         */
        XPOSED_ENVIRONMENT,

        /**
         * Uses environment-specific output.
         *
         * The (Xposed) host environment uses only [XPOSED_ENVIRONMENT].
         *
         * The module environment uses only [LOGD].
         */
        SCOPE,

        /**
         * Uses both outputs.
         *
         * The (Xposed) host environment uses [LOGD] and [XPOSED_ENVIRONMENT].
         *
         * The module environment uses only [LOGD].
         */
        BOTH
    }
}