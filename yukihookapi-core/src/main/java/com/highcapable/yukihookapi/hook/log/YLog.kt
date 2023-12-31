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
 * This file is created by fankes on 2023/9/27.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.log

import android.system.ErrnoException
import android.util.Log
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.log.data.YLogData
import com.highcapable.yukihookapi.hook.utils.factory.dumpToString
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import java.io.File

/**
 * 全局 Log 管理类
 */
object YLog {

    /**
     * 配置 [YLog]
     */
    object Configs {

        /**
         * 标签
         *
         * 显示效果如下 ↓
         *
         * ```
         * [YukiHookAPI][...][...] ...
         * ```
         */
        const val TAG = 1000

        /**
         * 优先级
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][E][...] ...
         * ```
         */
        const val PRIORITY = 1001

        /**
         * 当前宿主的包名
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][com.demo.test][...] ...
         * ```
         */
        const val PACKAGE_NAME = 1002

        /**
         * 当前宿主的用户 ID (主用户不显示)
         *
         * 显示效果如下 ↓
         *
         * ```
         * [...][...][999] ...
         * ```
         */
        const val USER_ID = 1003

        /**
         * 这是一个调试日志的全局标识
         *
         * 默认文案为 [YukiHookAPI.TAG]
         *
         * 你可以修改为你自己的文案
         */
        var tag = YukiHookAPI.TAG

        /**
         * 是否启用调试日志的输出功能 - 默认启用
         *
         * - 关闭后将会停用 [YukiHookAPI] 对全部日志的输出
         *
         * 但是不影响当你手动调用下面这些方法输出日志
         *
         * [debug]、[info]、[warn]、[error]
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
         * - 过量的日志可能会导致宿主运行缓慢或造成频繁 GC
         *
         * 开启后你可以调用 [saveToFile] 实时保存日志到文件或使用 [contents] 获取实时日志文件
         */
        var isRecord = false

        /** 当前已添加的元素顺序列表数组 */
        internal var elements = arrayOf(TAG, PRIORITY, PACKAGE_NAME, USER_ID)

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
         * [TAG][PRIORITY][PACKAGE_NAME][USER_ID] Message
         * ```
         * @param item 自定义的元素数组
         */
        fun elements(vararg item: Int) {
            elements = arrayOf(*item.toTypedArray())
        }

        /** 结束方法体 */
        internal fun build() = Unit
    }

    /**
     * 当前全部已记录的日志数据
     *
     * - 获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     */
    val inMemoryData = mutableListOf<YLogData>()

    /**
     * 获取当前日志文件内容
     *
     * 如果当前没有已记录的日志会返回空字符串
     *
     * - 获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     * @return [String]
     */
    val contents get() = contents()

    /**
     * 获取、格式化当前日志文件内容
     *
     * 如果当前没有已记录的日志 ([data] 为空) 会返回空字符串
     *
     * - 获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     * @param data 日志数据 - 默认为 [inMemoryData]
     * @return [String]
     */
    fun contents(data: List<YLogData> = inMemoryData): String {
        var content = ""
        data.takeIf { it.isNotEmpty() }?.forEach {
            content += "${it.head}$it\n"
            it.throwable?.also { e ->
                content += "${it.head}Dump stack trace for \"${e.current().name}\":\n"
                content += e.dumpToString()
            }
        }; return content
    }

    /**
     * 清除全部已记录的日志
     *
     * 你也可以直接获取 [inMemoryData] 来清除
     *
     * - 获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的
     */
    fun clear() = inMemoryData.clear()

    /**
     * 保存当前日志到文件
     *
     * 若当前未开启 [Configs.isRecord] 或记录为空则不会进行任何操作
     *
     * 日志文件会追加到 [fileName] 的文件结尾 - 若文件不存在会自动创建
     *
     * - 文件读写权限取决于当前宿主、模块已获取的权限
     * @param fileName 完整文件名 - 例如 /data/data/.../files/xxx.log
     * @param data 日志数据 - 默认为 [inMemoryData]
     * @throws ErrnoException 如果目标路径不可写
     */
    fun saveToFile(fileName: String, data: List<YLogData> = inMemoryData) {
        if (data.isNotEmpty()) File(fileName).appendText(contents(data))
    }

    /**
     * 打印 Debug 级别 Log
     *
     * 向控制台和 (Xposed) 宿主环境打印日志
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [Configs.tag]
     * @param env 日志打印的环境 - 默认为 [EnvType.BOTH]
     */
    fun debug(msg: String = "", e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "D", tag = tag, msg = msg, throwable = e))

    /**
     * 打印 Info 级别 Log
     *
     * 向控制台和 (Xposed) 宿主环境打印日志
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [Configs.tag]
     * @param env 日志打印的环境 - 默认为 [EnvType.BOTH]
     */
    fun info(msg: String = "", e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "I", tag = tag, msg = msg, throwable = e))

    /**
     * 打印 Warn 级别 Log
     *
     * 向控制台和 (Xposed) 宿主环境打印日志
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [Configs.tag]
     * @param env 日志打印的环境 - 默认为 [EnvType.BOTH]
     */
    fun warn(msg: String = "", e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "W", tag = tag, msg = msg, throwable = e))

    /**
     * 打印 Error 级别 Log
     *
     * 向控制台和 (Xposed) 宿主环境打印日志
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param tag 日志打印的标签 - 建议和自己的模块名称设置成一样的 - 默认为 [Configs.tag]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param env 日志打印的环境 - 默认为 [EnvType.BOTH]
     */
    fun error(msg: String = "", e: Throwable? = null, tag: String = Configs.tag, env: EnvType = EnvType.BOTH) =
        log(env, YLogData(priority = "E", tag = tag, msg = msg, throwable = e))

    /**
     * [YukiHookAPI] (内部) 打印 Debug 级别 Log
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
     */
    internal fun innerD(msg: String = "", e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not() || YukiHookAPI.Configs.isDebug.not()) return
        log(EnvType.BOTH, YLogData(priority = "D", msg = msg, throwable = e), isImplicit)
    }

    /**
     * [YukiHookAPI] (内部) 打印 Info 级别 Log
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
     */
    internal fun innerI(msg: String = "", e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return
        log(EnvType.BOTH, YLogData(priority = "I", msg = msg, throwable = e), isImplicit)
    }

    /**
     * [YukiHookAPI] (内部) 打印 Warn 级别 Log
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
     */
    internal fun innerW(msg: String = "", e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return
        log(EnvType.BOTH, YLogData(priority = "W", msg = msg, throwable = e), isImplicit)
    }

    /**
     * [YukiHookAPI] (内部) 打印 Error 级别 Log
     * @param msg 日志打印的内容 - 默认空 - 如果你仅想打印异常堆栈可只设置 [e]
     * @param e 可填入异常堆栈信息 - 将自动完整打印到控制台
     * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
     */
    internal fun innerE(msg: String = "", e: Throwable? = null, isImplicit: Boolean = false) {
        if (Configs.isEnable.not()) return
        log(EnvType.BOTH, YLogData(priority = "E", msg = msg, throwable = e), isImplicit)
    }

    /**
     * 向控制台和 (Xposed) 宿主环境打印日志 - 最终实现方法
     * @param env 日志打印的环境
     * @param data 日志数据
     * @param isImplicit 是否隐式打印 - 不会记录 - 也不会显示包名和用户 ID
     */
    private fun log(env: EnvType, data: YLogData, isImplicit: Boolean = false) {
        /** 是否为有效日志 */
        val isNotBlankLog = data.msg.isNotBlank() || (data.msg.isBlank() && data.throwable != null)

        /** 打印到 [Log] */
        fun logByLogd() = when (data.priority) {
            "D" -> Log.d(data.tag, data.msg, data.throwable)
            "I" -> Log.i(data.tag, data.msg, data.throwable)
            "W" -> Log.w(data.tag, data.msg, data.throwable)
            "E" -> Log.e(data.tag, data.msg, data.throwable)
            else -> Log.wtf(data.tag, data.msg, data.throwable)
        }

        /** 打印到 (Xposed) 宿主环境 */
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

    /**
     * 需要打印的日志环境类型
     *
     * 决定于模块与 (Xposed) 宿主环境使用的打印方式
     */
    enum class EnvType {
        /** 仅使用 [Log] */
        LOGD,

        /**
         * 仅在 (Xposed) 宿主环境使用
         *
         * - 只能在 (Xposed) 宿主环境中使用 - 模块环境将不生效
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
}