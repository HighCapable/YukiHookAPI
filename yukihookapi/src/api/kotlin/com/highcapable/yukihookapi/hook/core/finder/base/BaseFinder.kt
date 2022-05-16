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
 * This file is Created by fankes on 2022/2/18.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import android.os.SystemClock
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreater
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import java.lang.reflect.Member
import kotlin.math.abs

/**
 * 这是查找类功能的基本类实现
 * @param tag 当前查找类的标识
 * @param hookInstance 当前 Hook 实例
 * @param classSet 当前需要查找的 [Class] 实例
 */
abstract class BaseFinder(
    private val tag: String,
    open val hookInstance: YukiMemberHookCreater.MemberHookCreater? = null,
    open val classSet: Class<*>? = null
) {

    /**
     * 字节码下标筛选数据类型
     */
    @PublishedApi
    internal enum class IndexConfigType { ORDER, MATCH }

    /** 字节码顺序下标 */
    internal var orderIndex: Pair<Int, Boolean>? = null

    /** 字节码筛选下标 */
    internal var matchIndex: Pair<Int, Boolean>? = null

    /**
     * 字节码下标筛选实现类
     * @param type 类型
     */
    inner class IndexTypeCondition @PublishedApi internal constructor(private val type: IndexConfigType) {

        /**
         * 设置下标
         *
         * 若 index 小于零则为倒序 - 此时可以使用 [IndexTypeConditionSort.reverse] 方法实现
         *
         * 可使用 [IndexTypeConditionSort.first] 和 [IndexTypeConditionSort.last] 设置首位和末位筛选条件
         * @param num 下标
         */
        fun index(num: Int) = when (type) {
            IndexConfigType.ORDER -> orderIndex = Pair(num, true)
            IndexConfigType.MATCH -> matchIndex = Pair(num, true)
        }

        /**
         * 得到下标
         * @return [IndexTypeConditionSort]
         */
        fun index() = IndexTypeConditionSort()

        /**
         * 字节码下标排序实现类
         *
         * - ❗请使用 [index] 方法来获取 [IndexTypeConditionSort]
         */
        inner class IndexTypeConditionSort internal constructor() {

            /** 设置满足条件的第一个*/
            fun first() = index(num = 0)

            /** 设置满足条件的最后一个*/
            fun last() = when (type) {
                IndexConfigType.ORDER -> orderIndex = Pair(0, false)
                IndexConfigType.MATCH -> matchIndex = Pair(0, false)
            }

            /**
             * 设置倒序下标
             * @param num 下标
             */
            fun reverse(num: Int) = when {
                num < 0 -> index(abs(num))
                num == 0 -> index().last()
                else -> index(-num)
            }
        }
    }

    /** 是否使用了重查找功能 */
    @PublishedApi
    internal var isUsingRemedyPlan = false

    /** 是否将结果设置到目标 [YukiMemberHookCreater.MemberHookCreater] */
    internal var isBindToHooker = false

    /** 是否开启忽略错误警告功能 */
    internal var isShutErrorPrinting = false

    /** 当前找到的 [Member] */
    internal var memberInstance: Member? = null

    /**
     * 获取当前使用的 TAG
     * @return [String] 使用的 TAG
     */
    internal val hookTag get() = hookInstance?.tag ?: "FinderMode"

    /**
     * 判断是否没有设置 Hook 过程中 方法、构造类、变量 找不到的任何异常拦截
     * @return [Boolean] 没有设置任何异常拦截
     */
    internal val isNotIgnoredNoSuchMemberFailure get() = hookInstance?.isNotIgnoredNoSuchMemberFailure ?: true

    /** 需要输出的日志内容 */
    private var loggingContent: Pair<String, Throwable?>? = null

    /**
     * 将目标类型转换为可识别的兼容类型
     * @return [Class] or null
     */
    internal fun Any?.compat() = when (this) {
        null -> null
        is Class<*> -> this
        is String -> runCatching { classOf(name = this, classSet!!.classLoader) }.getOrNull() ?: UndefinedType
        is VariousClass -> runCatching { get(classSet!!.classLoader) }.getOrNull() ?: UndefinedType
        else -> error("$tag match type \"$javaClass\" not allowed")
    } as Class<*>?

    /**
     * 发生错误时输出日志
     * @param msg 消息日志
     * @param throwable 错误
     * @param isAlwaysPrint 忽略条件每次都打印错误
     */
    internal fun onFailureMsg(msg: String = "", throwable: Throwable? = null, isAlwaysPrint: Boolean = false) {
        /** 创建日志 */
        fun build() {
            if (isNotIgnoredNoSuchMemberFailure && isUsingRemedyPlan.not() && isShutErrorPrinting.not())
                loggingContent = Pair(msg, throwable)
        }
        /** 判断绑定到 Hooker 时仅创建日志 */
        if (isBindToHooker) return Thread {
            /** 延迟使得方法取到返回值 */
            SystemClock.sleep(1)
            build()
        }.start()
        /** 判断始终输出日志或等待结果后输出日志 */
        if (isAlwaysPrint) build().run { printLogIfExist() }
        else Thread {
            /** 延迟使得方法取到返回值 */
            SystemClock.sleep(1)
            build().run { printLogIfExist() }
        }.start()
    }

    /** 存在日志时输出日志 */
    internal fun printLogIfExist() {
        if (loggingContent != null) yLoggerE(
            msg = "${hookInstance?.packageName?.let { "[$it] " } ?: ""}NoSuch$tag happend in [$classSet] ${loggingContent?.first} [${hookTag}]",
            e = loggingContent?.second
        )
        /** 仅输出一次 - 然后清掉日志 */
        loggingContent = null
    }

    /**
     * Hook 过程中开启了 [YukiHookAPI.Configs.isDebug] 输出调试信息
     * @param msg 调试日志内容
     */
    internal fun onHookLogMsg(msg: String) {
        if (YukiHookAPI.Configs.isDebug && YukiHookBridge.hasXposedBridge)
            hookInstance?.also { yLoggerI(msg = "[${it.packageName}] $msg") } ?: yLoggerI(msg = msg)
    }

    /**
     * 得到结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param isBind 是否将结果设置到目标 [YukiMemberHookCreater.MemberHookCreater]
     * @return [Any]
     */
    @YukiPrivateApi
    abstract fun build(isBind: Boolean = false): Any

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Any]
     */
    @YukiPrivateApi
    abstract fun failure(throwable: Throwable?): Any
}