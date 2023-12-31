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
 * This file is created by fankes on 2023/1/9.
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