/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is created by fankes on 2022/2/3.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.log

import android.system.ErrnoException
import android.util.Log
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.utils.toStackTrace
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import java.io.File
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 需要打印的日志类型
 *
 * 决定于模块与 (Xposed) 宿主环境使用的打印方式
 */
enum class LoggerType {
    /** 仅使用 [Log] */
    LOGD,

    /**
     * 仅在 (Xposed) 宿主环境使用
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在迁移到 [XPOSED_ENVIRONMENT]
     */
    @Deprecated(message = "请使用新的命名方法", ReplaceWith("XPOSED_ENVIRONMENT"))
    XPOSEDBRIDGE,

    /**
     * 仅在 (Xposed) 宿主环境使用
     *
     * - ❗只能在 (Xposed) 宿主环境中使用 - 模块环境将不生效
     */
    XPOSED_ENVIRONMENT,

    /**
     * 分区使用
     *
     * (Xposed) 宿主环境仅使用 [XPOSED_ENVIRONMENT]
     *
     * 模块环境仅使用 [LOGD]
     */
    SCOPE,

    /**
     * 同时使用
     *
     * (Xposed) 宿主环境使用 [LOGD] 与 [XPOSED_ENVIRONMENT]
     *
     * 模块环境仅使用 [LOGD]
     */
    BOTH
}

/**
 * 调试日志数据实现类
 * @param timestamp 当前时间戳
 * @param time 当前 UTC 时间
 * @param tag 当前标签
 * @param priority 当前优先级 - D、I、W、E
 * @param packageName 当前包名
 * @param userId 当前用户 ID
 * @param msg 当前日志内容
 * @param throwable 当前异常堆栈
 */
data class YukiLoggerData internal constructor(
    var timestamp: Long = 0L,
    var time: String = "",
    var tag: String = YukiHookLogger.Configs.tag,
    var priority: String = "",
    var packageName: String = "",
    var userId: Int = 0,
    var msg: String = "",
    var throwable: Throwable? = null
) : Serializable {

    /** 是否隐式打印 */
    internal var isImplicit = false

    init {
        timestamp = System.currentTimeMillis()
        time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT).format(Date(timestamp))
        packageName = if (YukiXposedModule.isXposedEnvironment) YukiXposedModule.hostProcessName else AppParasitics.currentPackageName
        userId = AppParasitics.findUserId(AppParasitics.currentPackageName)
    }

    /**
     * 获取头部时间字符串
     * @return [String]
     */
    internal val head get() = "$time ------ "

    override fun toString(): String {
        var content = ""
        YukiHookLogger.Configs.elements.takeIf { it.isNotEmpty() }?.forEach {
            if (it == YukiHookLogger.Configs.TAG) content += "[$tag]"
            if (it == YukiHookLogger.Configs.PRIORITY) content += "[$priority]"
            if (it == YukiHookLogger.Configs.PACKAGE_NAME && isImplicit.not() && packageName.isNotBlank()) content += "[$packageName]"
            if (it == YukiHookLogger.Configs.USER_ID && isImplicit.not() && userId != 0) content += "[$userId]"
        }
        return content.takeIf { it.isNotBlank() }?.let { "$content--> $msg" } ?: msg
    }
}

/**
 * 调试日志实现类
 */
object YukiHookLogger {

    /**
     * 当前全部已记录的日志数据
     *
     * - ❗获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     */
    val inMemoryData = ArrayList<YukiLoggerData>()

    /**
     * 获取当前日志文件内容
     *
     * 如果当前没有已记录的日志会返回空字符串
     *
     * - ❗获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     * @return [String]
     */
    val contents get() = contents()

    /**
     * 获取、格式化当前日志文件内容
     *
     * 如果当前没有已记录的日志 ([data] 为空) 会返回空字符串
     *
     * - ❗获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     * @param data 日志数据 - 默认为 [inMemoryData]
     * @return [String]
     */
    fun contents(data: ArrayList<YukiLoggerData> = inMemoryData): String {
        var content = ""
        data.takeIf { it.isNotEmpty() }?.forEach {
            content += "${it.head}$it\n"
            it.throwable?.also { e ->
                content += "${it.head}Dump stack trace for \"${e.current().name}\":\n"
                content += e.toStackTrace()
            }
        }
        return content
    }

    /**
     * 清除全部已记录的日志
     *
     * 你也可以直接获取 [inMemoryData] 来清除
     *
     * - ❗获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     */
    fun clear() = inMemoryData.clear()

    /**
     * 保存当前日志到文件
     *
     * 若当前未开启 [Configs.isRecord] 或记录为空则不会进行任何操作
     *
     * 日志文件会追加到 [fileName] 的文件结尾 - 若文件不存在会自动创建
     *
     * - ❗文件读写权限取决于当前宿主、模块已获取的权限
     * @param fileName 完整文件名 - 例如 /data/data/.../files/xxx.log
     * @param data 日志数据 - 默认为 [inMemoryData]
     * @throws ErrnoException 如果目标路径不可写
     */
    fun saveToFile(fileName: String, data: ArrayList<YukiLoggerData> = inMemoryData) {
        if (data.isNotEmpty()) File(fileName).appendText(contents(data))
    }

    /**
     * 配置 [YukiHookLogger]
     */
    object Configs {

        /**
         * 标签
         *
         * 显示效果如下 ↓
         *
         * ```
         * [YukiHookAPI][...][...]--> ...
         * ```
         */
        const val TAG = 1000

        /**
         * 优先级
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][E][...]--> ...
         * ```
         */
        const val PRIORITY = 1001

        /**
         * 当前宿主的包名
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][com.demo.test][...]--> ...
         * ```
         */
        const val PACKAGE_NAME = 1002

        /**
         * 当前宿主的用户 ID (主用户不显示)
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][...][999]--> ...
         * ```
         */
        const val USER_ID = 1003

        /** 当前已添加的元素顺序列表数组 */
        internal var elements = arrayOf(TAG, PRIORITY, PACKAGE_NAME, USER_ID)

        /**
         * 是否启用调试日志的输出功能 - 默认启用
         *
         * - ❗关闭后将会停用 [YukiHookAPI] 对全部日志的输出
         *
         * 但是不影响当你手动调用下面这些方法输出日志
         *
         * [loggerD]、[loggerI]、[loggerW]、[loggerE]
         *
         * 当 [isEnable] 关闭后 [YukiHookAPI.Configs.isDebug] 也将同时关闭
         */
        var isEnable = true

        /**
         * 是否启用调试日志的记录功能 - 默认不启用
         *
         * 开启后将会在内存中记录全部可用的日志和异常堆栈
         *
         * 需要同时启用 [isEnable] 才能有效
         *
         * - ❗过量的日志可能会导致宿主运行缓慢或造成频繁 GC
         *
         * 开启后你可以调用 [YukiHookLogger.saveToFile] 实时保存日志到文件或使用 [YukiHookLogger.contents] 获取实时日志文件
         */
        var isRecord = false

        /**
         * 这是一个调试日志的全局标识
         *
         * 默认文案为 YukiHookAPI
         *
         * 你可以修改为你自己的文案
         */
        var tag = "YukiHookAPI"

        /**
         * 自定义调试日志对外显示的元素
         *
         * 只对日志记录和 (Xposed) 宿主环境的日志生效
         *
         * 日志元素的排列将按照你在 [item] 中设置的顺序进行显示
         *
         * 你还可以留空 [item] 以不显示除日志内容外的全部元素
         *
         * 可用的元素有：[TAG]、[PRIORITY]、[PACKAGE_NAME]、[USER_ID]
         *
         * 默认排列方式如下 ↓
         *
         * ```
         * [TAG][PRIORITY][PACKAGE_NAME][USER_ID]--> Message
         * ```
         * @param item 自定义的元素数组
         */
        fun elements(vararg item: Int) {
            elements = arrayOf(*item.toTypedArray())
        }

        /** 结束方法体 */
        @PublishedApi
        internal fun build() = Unit
    }
}

/**
 * 向控制台和 (Xposed) 宿主环境打印日志 - 最终实现方法
 * @param type 日志打印的类型
 * @param data 日志数据
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 */
private fun baseLogger(type: LoggerType, data: YukiLoggerData, isImplicit: Boolean = false) {
    /** 是否为有效日志 */
    val isNotBlankLog = data.msg.isNotBlank() || (data.msg.isBlank() && data.throwable != null)

    /** 打印到 [Log] */
    fun logByLogd() = when (data.priority) {
        "D" -> Log.d(data.tag, data.msg)
        "I" -> Log.i(data.tag, data.msg)
        "W" -> Log.w(data.tag, data.msg)
        "E" -> Log.e(data.tag, data.msg, data.throwable)
        else -> Log.wtf(data.tag, data.msg, data.throwable)
    }

    /** 打印到 (Xposed) 宿主环境 */
    fun logByHooker() {
        if (isNotBlankLog) YukiHookHelper.logByHooker(data.also { it.isImplicit = isImplicit }.toString(), data.throwable)
    }
    @Suppress("DEPRECATION")
    when (type) {
        LoggerType.LOGD -> logByLogd()
        LoggerType.XPOSEDBRIDGE, LoggerType.XPOSED_ENVIRONMENT -> logByHooker()
        LoggerType.SCOPE -> if (YukiXposedModule.isXposedEnvironment) logByHooker() else logByLogd()
        LoggerType.BOTH -> {
            logByLogd()
            if (YukiXposedModule.isXposedEnvironment) logByHooker()
        }
    }
    if (isImplicit.not() && YukiHookLogger.Configs.isRecord && isNotBlankLog) YukiHookLogger.inMemoryData.add(data)
}

/**
 * [YukiHookAPI] 向控制台和 (Xposed) 宿主环境打印日志 - D
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerD(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, YukiLoggerData(priority = "D", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 (Xposed) 宿主环境打印日志 - I
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerI(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, YukiLoggerData(priority = "I", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 (Xposed) 宿主环境打印日志 - W
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerW(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, YukiLoggerData(priority = "W", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 (Xposed) 宿主环境打印日志 - E
 * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerE(msg: String = "", e: Throwable? = null, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, YukiLoggerData(priority = "E", msg = msg, throwable = e), isImplicit)
}

/**
 * 向控制台和 (Xposed) 宿主环境打印日志 - D
 *
 * (Xposed) 宿主环境中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerD(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, YukiLoggerData(priority = "D", tag = tag, msg = msg))

/**
 * 向控制台和 (Xposed) 宿主环境打印日志 - I
 *
 * (Xposed) 宿主环境中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerI(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, YukiLoggerData(priority = "I", tag = tag, msg = msg))

/**
 * 向控制台和 (Xposed) 宿主环境打印日志 - W
 *
 * (Xposed) 宿主环境中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerW(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, YukiLoggerData(priority = "W", tag = tag, msg = msg))

/**
 * 向控制台和 (Xposed) 宿主环境打印日志 - E
 *
 * (Xposed) 宿主环境中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerE(tag: String = YukiHookLogger.Configs.tag, msg: String = "", e: Throwable? = null, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, YukiLoggerData(priority = "E", tag = tag, msg = msg, throwable = e))