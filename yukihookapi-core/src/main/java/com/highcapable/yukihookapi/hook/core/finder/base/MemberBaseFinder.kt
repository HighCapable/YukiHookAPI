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
 * This file is created by fankes on 2022/2/18.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.YLog
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
    internal var memberInstances = mutableListOf<Member>()

    /**
     * 将 [MutableList]<[Member]> 转换为 [MutableList]<[Field]>
     * @return [MutableList]<[Field]>
     */
    internal fun MutableList<Member>.fields() =
        mutableListOf<Field>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Field?)?.also { f -> it.add(f) } } }

    /**
     * 将 [MutableList]<[Member]> 转换为 [MutableList]<[Method]>
     * @return [MutableList]<[Method]>
     */
    internal fun MutableList<Member>.methods() =
        mutableListOf<Method>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Method?)?.also { m -> it.add(m) } } }

    /**
     * 将 [MutableList]<[Member]> 转换为 [MutableList]<[Constructor]>
     * @return [MutableList]<[Constructor]>
     */
    internal fun MutableList<Member>.constructors() =
        mutableListOf<Constructor<*>>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Constructor<*>?)?.also { c -> it.add(c) } } }

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
        if (HookApiCategoryHelper.hasAvailableHookApi && hookerManager.instance != null) YLog.innerD(msg)
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
            YLog.innerE("NoSuch$tag happend in [$classSet] $msg".trim(), e)
            es.forEachIndexed { index, e -> YLog.innerE("Throwable [${index + 1}]", e) }
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
        internal fun bindMembers(members: MutableList<Member>) {
            instance?.members?.clear()
            members.forEach { instance?.members?.add(it) }
        }
    }
}