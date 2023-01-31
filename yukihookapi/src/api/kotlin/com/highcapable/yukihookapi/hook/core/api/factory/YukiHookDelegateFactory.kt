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
 * This file is Created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.factory

import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.core.api.store.YukiHookCacheStore
import java.lang.reflect.Member

/**
 * Hook API 回调事件代理类
 */
internal object YukiHookCallbackDelegate {

    /**
     * 创建 [YukiMemberHook.HookedMember] 实例
     * @param member [Member] 实例 (代理回调)
     * @param onRemove 回调解除 Hook 事件 (代理回调)
     * @return [YukiMemberHook.HookedMember]
     */
    internal fun createHookedMemberCallback(member: () -> Member?, onRemove: () -> Unit) =
        object : YukiMemberHook.HookedMember() {
            override val member get() = member()
            override fun remove() {
                onRemove()
                runCatching { YukiHookCacheStore.hookedMembers.remove(this) }
            }
        }

    /**
     * 创建 [YukiHookCallback.Param] 实例
     * @param member [Member] 实例 (代理回调)
     * @param instance 当前实例对象 (代理回调)
     * @param args 方法、构造方法数组 (代理回调)
     * @param hasThrowable 是否存在设置过的方法调用抛出异常 (代理回调)
     * @param result 当前 Hook 方法返回值 (结果) (代理回调)
     * @param throwable 当前 Hook 方法调用抛出的异常 (代理回调)
     * @return [YukiHookCallback.Param]
     */
    internal fun createParamCallback(
        member: () -> Member?,
        instance: () -> Any?,
        args: () -> Array<Any?>?,
        hasThrowable: () -> Boolean,
        result: (Any?, Boolean) -> Any?,
        throwable: (Throwable?, Boolean) -> Throwable?
    ) = object : YukiHookCallback.Param {
        override val member get() = member()
        override val instance get() = instance()
        override val args get() = args()
        override val hasThrowable get() = hasThrowable()
        override var result
            get() = result(null, false)
            set(value) {
                result(value, true)
            }
        override var throwable
            get() = throwable(null, false)
            set(value) {
                throwable(value, true)
            }
    }
}

/**
 * 调用 [YukiMemberHook.beforeHookedMember] 事件
 * @param param Hook 结果回调接口
 */
internal fun YukiHookCallback.callBeforeHookedMember(param: YukiHookCallback.Param) {
    if (this !is YukiMemberHook) error("Invalid YukiHookCallback type")
    if (this is YukiMemberReplacement)
        param.result = replaceHookedMember(param)
    else beforeHookedMember(param)
}

/**
 * 调用 [YukiMemberHook.afterHookedMember] 事件
 * @param param Hook 结果回调接口
 */
internal fun YukiHookCallback.callAfterHookedMember(param: YukiHookCallback.Param) {
    if (this !is YukiMemberHook) error("Invalid YukiHookCallback type")
    afterHookedMember(param)
}