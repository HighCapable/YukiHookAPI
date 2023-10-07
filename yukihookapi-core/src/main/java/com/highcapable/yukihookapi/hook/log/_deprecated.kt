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