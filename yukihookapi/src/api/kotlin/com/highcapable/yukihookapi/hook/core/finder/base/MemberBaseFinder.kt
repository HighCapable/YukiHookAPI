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

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.utils.await
import com.highcapable.yukihookapi.hook.utils.unit
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是 [Member] 查找类功能的基本类实现
 * @param tag 当前查找类的标识
 * @param hookInstance 当前 Hook 实例
 * @param classSet 当前需要查找的 [Class] 实例
 */
abstract class MemberBaseFinder internal constructor(
    private val tag: String,
    @PublishedApi
    internal open val hookInstance: YukiMemberHookCreator.MemberHookCreator? = null,
    @PublishedApi
    internal open val classSet: Class<*>? = null
) : BaseFinder() {

    /** 是否使用了重查找功能 */
    @PublishedApi
    internal var isUsingRemedyPlan = false

    /** 是否将结果设置到目标 [YukiMemberHookCreator.MemberHookCreator] */
    internal var isBindToHooker = false

    /** 是否开启忽略错误警告功能 */
    internal var isShutErrorPrinting = false

    /** 当前找到的 [Member] 数组 */
    internal var memberInstances = HashSet<Member>()

    /**
     * 获取当前使用的 TAG
     * @return [String] 使用的 TAG
     */
    internal val hookTag get() = hookInstance?.tag ?: "FinderMode"

    /**
     * 判断是否没有设置 Hook 过程中 方法、构造方法、变量 找不到的任何异常拦截
     * @return [Boolean] 没有设置任何异常拦截
     */
    internal val isNotIgnoredNoSuchMemberFailure get() = hookInstance?.isNotIgnoredNoSuchMemberFailure ?: true

    /** 需要输出的日志内容 */
    private var loggingContent: Pair<String, Throwable?>? = null

    /**
     * 将 [HashSet]<[Member]> 转换为 [HashSet]<[Field]>
     * @return [HashSet]<[Field]>
     */
    internal fun HashSet<Member>.fields() =
        hashSetOf<Field>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Field?)?.also { f -> it.add(f) } } }

    /**
     * 将 [HashSet]<[Member]> 转换为 [HashSet]<[Method]>
     * @return [HashSet]<[Method]>
     */
    internal fun HashSet<Member>.methods() =
        hashSetOf<Method>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Method?)?.also { m -> it.add(m) } } }

    /**
     * 将 [HashSet]<[Member]> 转换为 [HashSet]<[Constructor]>
     * @return [HashSet]<[Constructor]>
     */
    internal fun HashSet<Member>.constructors() =
        hashSetOf<Constructor<*>>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Constructor<*>?)?.also { c -> it.add(c) } } }

    /**
     * 将目标类型转换为可识别的兼容类型
     * @return [Class] or null
     */
    internal fun Any?.compat() = compat(tag, classSet?.classLoader)

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
        if (isBindToHooker) return await { build() }.unit()
        /** 判断始终输出日志或等待结果后输出日志 */
        if (isAlwaysPrint) build().run { printLogIfExist() }
        else await { build().run { printLogIfExist() } }
    }

    /** 存在日志时输出日志 */
    internal fun printLogIfExist() {
        if (loggingContent != null) yLoggerE(
            msg = "NoSuch$tag happend in [$classSet] ${loggingContent?.first} [${hookTag}]",
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
        if (YukiHookAPI.Configs.isDebug && YukiHookBridge.hasXposedBridge && hookInstance != null) yLoggerI(msg = msg)
    }

    /**
     * 返回结果处理类并设置到目标 [YukiMemberHookCreator.MemberHookCreator]
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [BaseFinder.BaseResult]
     */
    @YukiPrivateApi
    abstract fun process(): BaseResult

    /**
     * 返回只有异常的结果处理类并作用于目标 [YukiMemberHookCreator.MemberHookCreator]
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [BaseFinder.BaseResult]
     */
    @YukiPrivateApi
    abstract fun denied(throwable: Throwable?): BaseResult
}