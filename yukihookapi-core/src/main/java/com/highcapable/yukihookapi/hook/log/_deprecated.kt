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
 * This file is created by fankes on 2022/2/3.
 */
@file:Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "DeprecatedCallableAddReplaceWith", "DEPRECATION")

package com.highcapable.yukihookapi.hook.log

import java.io.Serializable

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog")
enum class LoggerType {
    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    LOGD,

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    XPOSEDBRIDGE,

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    XPOSED_ENVIRONMENT,

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    SCOPE,

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    BOTH
}

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog")
data class YukiLoggerData @Deprecated(message = "Migrate to YLog") internal constructor(
    @Deprecated(message = "Migrate to YLog")
    var timestamp: Long = 0L,
    @Deprecated(message = "Migrate to YLog")
    var time: String = "",
    @Deprecated(message = "Migrate to YLog")
    var tag: String = YukiHookLogger.Configs.tag,
    @Deprecated(message = "Migrate to YLog")
    var priority: String = "",
    @Deprecated(message = "Migrate to YLog")
    var packageName: String = "",
    @Deprecated(message = "Migrate to YLog")
    var userId: Int = 0,
    @Deprecated(message = "Migrate to YLog")
    var msg: String = "",
    @Deprecated(message = "Migrate to YLog")
    var throwable: Throwable? = null
) : Serializable

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog")
object YukiHookLogger {

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    val inMemoryData = ArrayList<YukiLoggerData>()

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    val contents get() = ""

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    fun contents(data: ArrayList<YukiLoggerData> = inMemoryData) = ""

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    fun clear() = Unit

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    fun saveToFile(fileName: String, data: ArrayList<YukiLoggerData> = inMemoryData) = Unit

    /**
     * - LoggerFactory is deprecated. Migrate to [YLog].
     */
    @Deprecated(message = "Migrate to YLog")
    object Configs {

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        const val TAG = ""

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        const val PRIORITY = -1

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        const val PACKAGE_NAME = -1

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        const val USER_ID = -1

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        var isEnable = true

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        var isRecord = false

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        var tag = ""

        /**
         * - LoggerFactory is deprecated. Migrate to [YLog].
         */
        @Deprecated(message = "Migrate to YLog")
        fun elements(vararg item: Int) = Unit
    }
}

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog", ReplaceWith("YLog.debug(msg = msg, tag = tag)"))
fun loggerD(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.debug(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog", ReplaceWith("YLog.info(msg = msg, tag = tag)"))
fun loggerI(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.info(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog", ReplaceWith("YLog.warn(msg = msg, tag = tag)"))
fun loggerW(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.warn(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory is deprecated. Migrate to [YLog].
 */
@Deprecated(message = "Migrate to YLog", ReplaceWith("YLog.error(msg = msg, e = e, tag = tag)"))
fun loggerE(tag: String = YLog.Configs.tag, msg: String = "", e: Throwable? = null, type: LoggerType = LoggerType.BOTH) {
    YLog.error(msg, e = e, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}