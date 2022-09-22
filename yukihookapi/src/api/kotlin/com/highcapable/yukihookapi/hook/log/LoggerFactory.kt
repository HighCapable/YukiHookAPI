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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.log

import android.system.ErrnoException
import android.util.Log
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.utils.toStackTrace
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import de.robv.android.xposed.XposedBridge
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

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
 * 调试日志数据实现类
 * @param time 当前时间
 * @param tag 当前标签
 * @param priority 当前优先级
 * @param packageName 当前包名
 * @param userId 当前用户 ID
 * @param msg 当前日志内容
 * @param throwable 当前异常堆栈
 */
internal class LoggerData internal constructor(
    internal var time: String = "",
    internal var tag: String = YukiHookLogger.Configs.tag,
    internal var priority: String = "",
    internal var packageName: String = "",
    internal var userId: Int = 0,
    internal var msg: String = "",
    internal var throwable: Throwable? = null
) {

    /** 是否隐式打印 */
    internal var isImplicit = false

    init {
        time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT).format(Date())
        packageName = YukiHookBridge.hostProcessName.takeIf { it != "unknown" } ?: YukiHookBridge.modulePackageName
        userId = AppParasitics.findUserId(packageName)
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
            if (it == YukiHookLogger.Configs.PACKAGE_NAME && isImplicit.not()) content += "[$packageName]"
            if (it == YukiHookLogger.Configs.USER_ID && isImplicit.not() && userId != 0) content += "[$userId]"
        }
        return content.takeIf { it.isNotBlank() }?.let { "$content--> $msg" } ?: msg
    }
}

/**
 * 调试日志实现类
 */
object YukiHookLogger {

    /** 当前全部已记录的日志 */
    internal val inMemoryData = HashSet<LoggerData>()

    /**
     * 获取当前日志文件内容
     *
     * 如果当前没有已记录的日志会返回空字符串
     * @return [String]
     */
    val contents: String
        get() {
            var content = ""
            inMemoryData.takeIf { it.isNotEmpty() }?.forEach {
                content += "${it.head}$it\n"
                it.throwable?.also { e ->
                    content += "${it.head}Dump stack trace for \"${e.current().name}\":\n"
                    content += e.toStackTrace()
                }
            }
            return content
        }

    /** 清除全部已记录的日志 */
    fun clear() = inMemoryData.clear()

    /**
     * 保存当前日志到文件
     *
     * 若当前未开启 [Configs.isRecord] 或记录为空则不会进行任何操作
     *
     * 日志文件会追加到 [fileName] 的文件结尾 - 若文件不存在会自动创建
     *
     * - ❗文件读写权限取决于当前宿主已获取的权限
     * @param fileName 完整文件名 - 例如 /data/data/.../files/xxx.log
     * @throws ErrnoException 如果目标路径不可写
     */
    fun saveToFile(fileName: String) {
        if (inMemoryData.isNotEmpty()) File(fileName).appendText(contents)
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
         * 只对日志记录和 [XposedBridge.log] 生效
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
 * 向控制台和 [XposedBridge] 打印日志 - 最终实现方法
 * @param type 日志打印的类型
 * @param data 日志数据
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 */
private fun baseLogger(type: LoggerType, data: LoggerData, isImplicit: Boolean = false) {
    /** 打印到 [Log] */
    fun loggerInLogd() = when (data.priority) {
        "D" -> Log.d(data.tag, data.msg)
        "I" -> Log.i(data.tag, data.msg)
        "W" -> Log.w(data.tag, data.msg)
        "E" -> Log.e(data.tag, data.msg, data.throwable)
        else -> Log.wtf(data.tag, data.msg, data.throwable)
    }

    /** 打印到 [XposedBridge.log] */
    fun loggerInXposed() {
        XposedBridge.log(data.also { it.isImplicit = isImplicit }.toString())
        data.throwable?.also { e -> XposedBridge.log(e) }
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
    if (isImplicit.not() && YukiHookLogger.Configs.isRecord) YukiHookLogger.inMemoryData.add(data)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - D
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerD(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, LoggerData(priority = "D", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - I
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerI(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, LoggerData(priority = "I", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - W
 * @param msg 日志打印的内容
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerW(msg: String, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, LoggerData(priority = "W", msg = msg), isImplicit)
}

/**
 * [YukiHookAPI] 向控制台和 [XposedBridge] 打印日志 - E
 * @param msg 日志打印的内容
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
 * @param isDisableLog 禁止打印日志 - 标识后将什么也不做 - 默认为 false
 */
internal fun yLoggerE(msg: String, e: Throwable? = null, isImplicit: Boolean = false, isDisableLog: Boolean = false) {
    if (YukiHookLogger.Configs.isEnable.not() || isDisableLog) return
    baseLogger(LoggerType.BOTH, LoggerData(priority = "E", msg = msg, throwable = e), isImplicit)
}

/**
 * 向控制台和 [XposedBridge] 打印日志 - D
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerD(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, LoggerData(priority = "D", tag = tag, msg = msg))

/**
 * 向控制台和 [XposedBridge] 打印日志 - I
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerI(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, LoggerData(priority = "I", tag = tag, msg = msg))

/**
 * 向控制台和 [XposedBridge] 打印日志 - W
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerW(tag: String = YukiHookLogger.Configs.tag, msg: String, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, LoggerData(priority = "W", tag = tag, msg = msg))

/**
 * 向控制台和 [XposedBridge] 打印日志 - E
 *
 * [XposedBridge] 中的日志打印风格为 [[tag]]「类型」--> [msg]
 * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [YukiHookLogger.Configs.tag]
 * @param msg 日志打印的内容
 * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
 * @param type 日志打印的类型 - 默认为 [LoggerType.BOTH]
 */
fun loggerE(tag: String = YukiHookLogger.Configs.tag, msg: String, e: Throwable? = null, type: LoggerType = LoggerType.BOTH) =
    baseLogger(type, LoggerData(priority = "E", tag = tag, msg = msg, throwable = e))