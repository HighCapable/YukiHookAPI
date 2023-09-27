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
 * This file is created by fankes on 2022/2/18.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.yLoggerD
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.utils.factory.await
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是 [Member] 查找类功能的基本类实现
 * @param tag 当前查找类的标识
 * @param classSet 当前需要查找的 [Class] 实例
 */
abstract class MemberBaseFinder internal constructor(private val tag: String, internal open val classSet: Class<*>? = null) : BaseFinder() {

    internal companion object {

        /** [classSet] 为 null 的提示 */
        internal const val CLASSSET_IS_NULL = "classSet is null"
    }

    /** 当前 [MemberHookerManager] */
    internal var hookerManager = MemberHookerManager()

    /** 是否使用了重查找功能 */
    internal var isUsingRemedyPlan = false

    /** 是否开启忽略错误警告功能 */
    internal var isIgnoreErrorLogs = false

    /** 当前找到的 [Member] 数组 */
    internal var memberInstances = HashSet<Member>()

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
     * 在开启 [YukiHookAPI.Configs.isDebug] 且在 [HookApiCategoryHelper.hasAvailableHookApi] 且在 Hook 过程中情况下输出调试信息
     * @param msg 消息内容
     */
    internal fun debugMsg(msg: String) {
        if (YukiHookAPI.Configs.isDebug && HookApiCategoryHelper.hasAvailableHookApi && hookerManager.instance != null)
            yLoggerD(msg = "$msg${hookerManager.tailTag}")
    }

    /**
     * 发生错误时输出日志
     * @param msg 消息内容
     * @param e 异常堆栈 - 默认空
     * @param e 异常堆栈数组 - 默认空
     * @param isAlwaysMode 忽略条件每次都输出日志
     */
    internal fun errorMsg(msg: String = "", e: Throwable? = null, es: List<Throwable> = emptyList(), isAlwaysMode: Boolean = false) {
        /** 判断是否为 [CLASSSET_IS_NULL] */
        if (e?.message == CLASSSET_IS_NULL) return
        await {
            if (isIgnoreErrorLogs || hookerManager.isNotIgnoredNoSuchMemberFailure.not()) return@await
            if (isAlwaysMode.not() && isUsingRemedyPlan) return@await
            yLoggerE(msg = "NoSuch$tag happend in [$classSet] $msg${hookerManager.tailTag}".trim(), e = e)
            es.forEachIndexed { index, e -> yLoggerE(msg = "Throwable [${index + 1}]", e = e) }
        }
    }

    /**
     * 返回结果处理类并设置到目标 [YukiMemberHookCreator.MemberHookCreator]
     *
     * - 此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [BaseFinder.BaseResult]
     */
    internal abstract fun process(): BaseResult

    /**
     * 返回只有异常的结果处理类并作用于目标 [YukiMemberHookCreator.MemberHookCreator]
     *
     * - 此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [BaseFinder.BaseResult]
     */
    internal abstract fun denied(throwable: Throwable?): BaseResult

    /**
     * 当前 Hooker 管理实现类
     */
    internal inner class MemberHookerManager {

        /** 当前 Hooker */
        internal var instance: YukiMemberHookCreator.MemberHookCreator? = null

        /** 当前 [Member] 是否设置到当前 Hooker */
        internal var isMemberBinded = false

        /**
         * 判断是否没有设置 Hook 过程中 方法、构造方法、变量 找不到的任何异常拦截
         * @return [Boolean] 没有设置任何异常拦截
         */
        internal val isNotIgnoredNoSuchMemberFailure get() = instance?.isNotIgnoredNoSuchMemberFailure ?: true

        /**
         * 获取当前日志尾部打印的 TAG 用于标识当前 Hook 实例
         * @return [String]
         */
        internal val tailTag get() = instance?.tag?.let { if (it.isNotBlank()) " [$it]" else "" } ?: ""

        /**
         * 绑定当前 [Member] 到当前 Hooker
         * @param member 当前 [Member]
         */
        internal fun bindMember(member: Member?) {
            instance?.members?.clear()
            member?.also { instance?.members?.add(it) }
        }

        /**
         * 绑定 [Member] 数组到当前 Hooker
         * @param members 当前 [Member] 数组
         */
        internal fun bindMembers(members: HashSet<Member>) {
            instance?.members?.clear()
            members.forEach { instance?.members?.add(it) }
        }
    }
}