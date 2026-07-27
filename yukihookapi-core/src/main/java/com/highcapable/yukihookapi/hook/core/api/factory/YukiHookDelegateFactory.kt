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
package com.highcapable.yukihookapi.hook.core.api.factory

import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberReplacement
import java.lang.reflect.Member

/**
 * Creates adapters for Hook API callbacks.
 */
internal object YukiHookCallbackDelegate {

    /**
     * Creates a [YukiMemberHook.HookedMember].
     * @param member the callback that provides the [Member] instance.
     * @param onRemove the callback that removes the Hook.
     * @return [YukiMemberHook.HookedMember]
     */
    internal fun createHookedMemberCallback(member: () -> Member?, onRemove: () -> Unit) =
        object : YukiMemberHook.HookedMember() {
            override val member get() = member()
            override fun remove() {
                onRemove()
            }
        }

    /**
     * Creates [YukiHookCallback.Param].
     * @param member the callback that provides the [Member] instance.
     * @param instance the callback that provides the current receiver instance.
     * @param args the callback that provides the method or constructor arguments.
     * @param hasThrowable the callback that reports whether a throwable is present.
     * @param result the callback that gets or sets the Hook result.
     * @param throwable the callback that gets or sets the Hook throwable.
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
 * Calls [YukiMemberHook.beforeHookedMember].
 * @param param the Hook callback parameters.
 */
internal fun YukiHookCallback.callBeforeHookedMember(param: YukiHookCallback.Param) {
    if (this !is YukiMemberHook) error("Invalid YukiHookCallback type")
    if (this is YukiMemberReplacement)
        param.result = replaceHookedMember(param)
    else beforeHookedMember(param)
}

/**
 * Calls [YukiMemberHook.afterHookedMember].
 * @param param the Hook callback parameters.
 */
internal fun YukiHookCallback.callAfterHookedMember(param: YukiHookCallback.Param) {
    if (this !is YukiMemberHook) error("Invalid YukiHookCallback type")
    afterHookedMember(param)
}