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
 * This file is Created by fankes on 2022/2/3.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.log

import android.util.Log
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import de.robv.android.xposed.XposedBridge

/**
 * 需要打印的日志类型
 *
 * 决定于模块与 (Xposed) 宿主环境使用的打印方式
 */
enum class LoggerType {
    /** 仅使用 [Log] */
    LOGD,

    /**
     * 仅使用 [XposedBridge.log]
     *
     * - ❗只能在 (Xposed) 宿主环境中使用 - 模块环境将不生效
     */
    XPOSEDBRIDGE,

    /**
     * 分区使用
     *
     * (Xposed) 宿主环境仅使用 [XPOSEDBRIDGE]
     *
     * 模块环境仅使用 [LOGD]
     */
    SCOPE,

    /**
     * 同时使用
     *
     * (Xposed) 宿主环境使用 [LOGD] 与 [XPOSEDBRIDGE]
     *
     * 模块环境仅使用 [LOGD]
     */
    BOTH
}

/**
 * 向控制台和 [XposedBridge] 打印日志 - 最终实现方法
 * @param format 日志打印的格式
 * @param type 日志打印的类型
 * @param tag 日志打印的标签
 * @param msg 日志打印的内容
 * @param e 异常堆栈信息
 * @param isShowProcessName 是否显示当前进程名称 - 仅限 [XposedBridge.log]
 */
private fun baseLogger(format: String, type: LoggerType, tag: String, msg: String, e: Throwable? = null, isShowProcessName: Boolean = true) {
    /** 打印到 [Log] */
    fun loggerInLogd() = when (format) {
        "D" -> Log.d(tag, msg)
        "I" -> Log.i(tag, msg)
        "W" -> Log.w(tag, msg)
        "E" -> Log.e(tag, msg, e)
        else -> Log.wtf(tag, msg, e)
    }

    /** 打印到 [XposedBridge.log] */
    fun loggerInXposed() = runCatching {
        YukiHookBridge.hostProcessName.also {
            val appUserId = AppParasitics.findUserId(it)
            XposedBridge.log("[$tag][$format]${if (isShowProcessName) (if (appUserId != 0) "[$it][$appUserId]" else "[$it]") else ""}--> $msg")
            e?.also { e -> XposedBridge.log(e) }
        }
    }
    when (type) {
        LoggerType.LOGD -> loggerInLogd()
        LoggerType.XPOSEDBRIDGE -> loggerInXposed()
        LoggerType.SCOPE -> if (YukiHookBridge.hasXposedBridge) loggerInXposed() else loggerInLogd()
        LoggerType.BOTH -> {
            loggerInLogd()
            if (YukiHookBridge.hasXposedBridge) loggerInXposed()
        }
    }
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - D
 * @param msg 日志打印的内容
 * @param isShowProcessName 是否显示当前进程名称 - 仅限 [XposedBridge.log]
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerD(msg: String, isShowProcessName: Boolean = true, isDisableLog: Boolean = false) {
    if (YukiHookAPI.Configs.isAllowPrintingLogs.not() || isDisableLog) return
    baseLogger(format = "D", LoggerType.BOTH, YukiHookAPI.Configs.debugTag, msg, isShowProcessName = isShowProcessName)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - I
 * @param msg 日志打印的内容
 * @param isShowProcessName 是否显示当前进程名称 - 仅限 [XposedBridge.log]
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerI(msg: String, isShowProcessName: Boolean = true, isDisableLog: Boolean = false) {
    if (YukiHookAPI.Configs.isAllowPrintingLogs.not() || isDisableLog) return
    baseLogger(format = "I", LoggerType.BOTH, YukiHookAPI.Configs.debugTag, msg, isShowProcessName = isShowProcessName)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - W
 * @param msg 日志打印的内容
 * @param isShowProcessName 是否显示当前进程名称 - 仅限 [XposedBridge.log]
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerW(msg: String, isShowProcessName: Boolean = true, isDisableLog: Boolean = false) {
    if (YukiHookAPI.Configs.isAllowPrintingLogs.not() || isDisableLog) return
    baseLogger(format = "W", LoggerType.BOTH, YukiHookAPI.Configs.debugTag, msg, isShowProcessName = isShowProcessName)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - E
 * @param msg 日志打印的内容
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param isShowProcessName 是否显示当前进程名称 - 仅限 [XposedBridge.log]
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerE(msg: String, e: Throwable? = null, isShowProcessName: Boolean = true, isDisableLog: Boolean = false) {
    if (YukiHookAPI.Configs.isAllowPrintingLogs.not() || isDisableLog) return
    baseLogger(format = "E", LoggerType.BOTH, YukiHookAPI.Configs.debugTag, msg, e, isShowProcessName)
}

/**
 * 向控制台和 [XposedBridge] 打印日志 - D
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookAPI.Configs.debugTag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerD(tag: String = YukiHookAPI.Configs.debugTag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(format = "D", type, tag, msg)

/**
 * 向控制台和 [XposedBridge] 打印日志 - I
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookAPI.Configs.debugTag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerI(tag: String = YukiHookAPI.Configs.debugTag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(format = "I", type, tag, msg)

/**
 * 向控制台和 [XposedBridge] 打印日志 - W
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookAPI.Configs.debugTag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerW(tag: String = YukiHookAPI.Configs.debugTag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(format = "W", type, tag, msg)

/**
 * 向控制台和 [XposedBridge] 打印日志 - E
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookAPI.Configs.debugTag]
 * @param msg 日志打印的内容
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerE(tag: String = YukiHookAPI.Configs.debugTag, msg: String, e: Throwable? = null, type: LoggerType = LoggerType.BOTH) =
    baseLogger(format = "E", type, tag, msg, e)