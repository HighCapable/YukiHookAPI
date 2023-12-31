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
 * This file is created by fankes on 2022/2/3.
 */
@file:Suppress("unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "DeprecatedCallableAddReplaceWith", "DEPRECATION")

package com.highcapable.yukihookapi.hook.log

import java.io.Serializable

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog")
enum class LoggerType {
    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    LOGD,

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    XPOSEDBRIDGE,

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    XPOSED_ENVIRONMENT,

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    SCOPE,

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    BOTH
}

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog")
data class YukiLoggerData @Deprecated(message = "请迁移到 YLog") internal constructor(
    @Deprecated(message = "请迁移到 YLog")
    var timestamp: Long = 0L,
    @Deprecated(message = "请迁移到 YLog")
    var time: String = "",
    @Deprecated(message = "请迁移到 YLog")
    var tag: String = YukiHookLogger.Configs.tag,
    @Deprecated(message = "请迁移到 YLog")
    var priority: String = "",
    @Deprecated(message = "请迁移到 YLog")
    var packageName: String = "",
    @Deprecated(message = "请迁移到 YLog")
    var userId: Int = 0,
    @Deprecated(message = "请迁移到 YLog")
    var msg: String = "",
    @Deprecated(message = "请迁移到 YLog")
    var throwable: Throwable? = null
) : Serializable

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog")
object YukiHookLogger {

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    val inMemoryData = ArrayList<YukiLoggerData>()

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    val contents get() = ""

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    fun contents(data: ArrayList<YukiLoggerData> = inMemoryData) = ""

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    fun clear() = Unit

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    fun saveToFile(fileName: String, data: ArrayList<YukiLoggerData> = inMemoryData) = Unit

    /**
     * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
     */
    @Deprecated(message = "请迁移到 YLog")
    object Configs {

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        const val TAG = ""

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        const val PRIORITY = -1

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        const val PACKAGE_NAME = -1

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        const val USER_ID = -1

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        var isEnable = true

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        var isRecord = false

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        var tag = ""

        /**
         * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
         */
        @Deprecated(message = "请迁移到 YLog")
        fun elements(vararg item: Int) = Unit
    }
}

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog", ReplaceWith("YLog.debug(msg = msg, tag = tag)"))
fun loggerD(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.debug(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog", ReplaceWith("YLog.info(msg = msg, tag = tag)"))
fun loggerI(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.info(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog", ReplaceWith("YLog.warn(msg = msg, tag = tag)"))
fun loggerW(tag: String = YLog.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) {
    YLog.warn(msg, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}

/**
 * - LoggerFactory 已被弃用 - 请迁移到 [YLog]
 */
@Deprecated(message = "请迁移到 YLog", ReplaceWith("YLog.error(msg = msg, e = e, tag = tag)"))
fun loggerE(tag: String = YLog.Configs.tag, msg: String = "", e: Throwable? = null, type: LoggerType = LoggerType.BOTH) {
    YLog.error(msg, e = e, tag = tag, env = when (type) {
        LoggerType.BOTH -> YLog.EnvType.BOTH
        LoggerType.LOGD -> YLog.EnvType.LOGD
        LoggerType.SCOPE -> YLog.EnvType.SCOPE
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> YLog.EnvType.XPOSED_ENVIRONMENT
    })
}