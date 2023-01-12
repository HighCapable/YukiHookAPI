/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
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
 * This file is Created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.helper

import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.api.compat.HookCompatHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.core.api.store.YukiHookCacheStore
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import java.lang.reflect.Member

/**
 * Hook 核心功能实现工具类
 */
internal object YukiHookHelper {

    /**
     * Hook [BaseFinder.BaseResult]
     * @param traction 直接调用 [BaseFinder.BaseResult]
     * @param callback 回调
     * @return [YukiHookResult]
     */
    internal fun hook(traction: BaseFinder.BaseResult, callback: YukiHookCallback) = runCatching {
        val member: Member? = when (traction) {
            is MethodFinder.Result -> traction.ignored().give()
            is ConstructorFinder.Result -> traction.ignored().give()
            else -> error("Unexpected BaseFinder result interface type")
        }
        hookMember(member, callback)
    }.onFailure { yLoggerE(msg = "An exception occurred when hooking internal function", e = it) }.getOrNull() ?: YukiHookResult()

    /**
     * Hook [Member]
     * @param member 需要 Hook 的方法、构造方法
     * @param callback 回调
     * @return [YukiHookResult]
     */
    internal fun hookMember(member: Member?, callback: YukiHookCallback): YukiHookResult {
        runCatching {
            YukiHookCacheStore.hookedMembers.takeIf { it.isNotEmpty() }?.forEach {
                if (it.member.toString() == member?.toString()) return YukiHookResult(isAlreadyHooked = true, it)
            }
        }
        return HookCompatHelper.hookMember(member, callback).let {
            if (it != null) YukiHookCacheStore.hookedMembers.add(it)
            YukiHookResult(hookedMember = it)
        }
    }

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     */
    internal fun invokeOriginalMember(member: Member?, instance: Any?, vararg args: Any?) =
        if (HookApiCategoryHelper.hasAvailableHookApi && YukiHookCacheStore.hookedMembers.any { it.member.toString() == member.toString() })
            member?.let {
                runCatching { HookCompatHelper.invokeOriginalMember(member, instance, args) }
                    .onFailure { yLoggerE(msg = "Invoke original Member [$member] failed", e = it) }
                    .getOrNull()
            }
        else null

    /**
     * 使用当前 Hook API 自带的日志功能打印日志
     * @param msg 日志打印的内容
     * @param e 异常堆栈信息 - 默认空
     */
    internal fun logByHooker(msg: String, e: Throwable? = null) {
        if (HookApiCategoryHelper.hasAvailableHookApi) HookCompatHelper.logByHooker(msg, e)
    }
}