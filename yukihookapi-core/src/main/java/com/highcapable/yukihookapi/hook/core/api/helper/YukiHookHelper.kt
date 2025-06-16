/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
 * This file is created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.helper

import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.api.compat.HookCompatHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.log.YLog
import java.lang.reflect.Member

/**
 * Hook 核心功能实现工具类
 */
internal object YukiHookHelper {

    /**
     * Hook [MemberResolver]
     * @param resolver 需要 Hook 的 [MemberResolver]
     * @param callback 回调
     * @return [YukiHookResult]
     */
    internal fun hook(resolver: MemberResolver<*, *>?, callback: YukiHookCallback) =
        resolver?.self?.let { hookMember(it, callback) } ?: YukiHookResult()

    /**
     * Hook [Member]
     * @param member 需要 Hook 的方法、构造方法
     * @param callback 回调
     * @return [YukiHookResult]
     */
    internal fun hookMember(member: Member?, callback: YukiHookCallback) =
        YukiHookResult(hookedMember = HookCompatHelper.hookMember(member, callback))

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     * @throws IllegalStateException 如果 [Member] 参数个数不正确
     */
    internal fun invokeOriginalMember(member: Member?, instance: Any?, args: Array<out Any?>?) = member?.let {
        runCatching { HookCompatHelper.invokeOriginalMember(member, instance, args) }.onFailure {
            if (it.message?.lowercase()?.contains("wrong number of arguments") == true) error(it.message ?: it.toString())
            YLog.innerE("Invoke original Member [$member] failed", it)
        }.getOrNull()
    }

    /**
     * 使用当前 Hook API 自带的日志功能打印日志
     * @param msg 日志打印的内容
     * @param e 异常堆栈信息 - 默认空
     */
    internal fun logByHooker(msg: String, e: Throwable? = null) {
        if (HookApiCategoryHelper.hasAvailableHookApi) HookCompatHelper.logByHooker(msg, e)
    }
}