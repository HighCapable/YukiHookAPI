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
package com.highcapable.yukihookapi.hook.core.api.compat

import com.highcapable.yukihookapi.hook.core.api.factory.YukiHookCallbackDelegate
import com.highcapable.yukihookapi.hook.core.api.factory.callAfterHookedMember
import com.highcapable.yukihookapi.hook.core.api.factory.callBeforeHookedMember
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

/**
 * Adapts core Hook operations to the active Hook API.
 */
internal object HookCompatHelper {

    /**
     * [HookApiCategory.ROVO89_XPOSED]
     *
     * Adapts an unhook handle for a hooked [Member].
     * @return [YukiMemberHook.HookedMember]
     */
    private fun XC_MethodHook.Unhook.compat() =
        YukiHookCallbackDelegate.createHookedMemberCallback(
            member = { hookedMethod },
            onRemove = { unhook() }
        )

    /**
     * [HookApiCategory.ROVO89_XPOSED]
     *
     * Adapts native Hook callback parameters.
     * @return [YukiHookCallback.Param]
     */
    private fun XC_MethodHook.MethodHookParam.compat() =
        YukiHookCallbackDelegate.createParamCallback(
            member = { method },
            instance = { thisObject },
            args = { args },
            hasThrowable = { hasThrowable() },
            result = { it, assign -> if (assign) result = it; result },
            throwable = { it, assign -> if (assign) throwable = it; throwable }
        )

    /**
     * Adapts a [YukiHookCallback] to the native Hook API callback.
     * @return [Any] the native callback.
     */
    private fun YukiHookCallback.compat() = when (HookApiCategoryHelper.currentCategory) {
        HookApiCategory.ROVO89_XPOSED -> object : XC_MethodHook(
            when (priority) {
                YukiHookPriority.DEFAULT -> 50
                YukiHookPriority.LOWEST -> -10000
                YukiHookPriority.HIGHEST -> 10000
            }
        ) {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                if (param == null) return
                this@compat.callBeforeHookedMember(param.compat())
            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                if (param == null) return
                this@compat.callAfterHookedMember(param.compat())
            }
        }
        HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
    }

    /**
     * Hook [Member]
     * @param member the method or constructor to Hook.
     * @param callback the Hook callback.
     * @return [YukiMemberHook.HookedMember] or null.
     */
    internal fun hookMember(member: Member?, callback: YukiHookCallback): YukiMemberHook.HookedMember? {
        if (member == null) return null
        return when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> XposedBridge.hookMethod(member, callback.compat()).compat()
            HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
        }
    }

    /**
     * Invokes the original unhooked [Member].
     * @param member the member instance.
     * @param args the argument array.
     * @return [Any] or null.
     */
    internal fun invokeOriginalMember(member: Member?, instance: Any?, args: Array<out Any?>?): Any? {
        if (member == null) return null
        return when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> XposedBridge.invokeOriginalMethod(member, instance, args)
            HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
        }
    }

    /**
     * Prints through the active Hook API logger.
     * @param msg the log message.
     * @param e the exception stack trace, defaults to null.
     */
    internal fun logByHooker(msg: String, e: Throwable? = null) {
        when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> {
                XposedBridge.log(msg)
                e?.also { XposedBridge.log(it) }
            }
            HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
        }
    }

    /** Throws an error for an unsupported Hook API. */
    private fun throwUnsupportedHookApiError(): Nothing =
        error("YukiHookAPI cannot support current Hook API or cannot found any available Hook APIs in current environment")
}