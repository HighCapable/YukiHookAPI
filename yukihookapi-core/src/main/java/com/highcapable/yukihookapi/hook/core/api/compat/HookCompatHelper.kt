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
 * Hook API 兼容层处理工具类
 */
internal object HookCompatHelper {

    /**
     * [HookApiCategory.ROVO89_XPOSED]
     *
     * 兼容对接已 Hook 的 [Member] 接口
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
     * 兼容对接 Hook 结果回调接口
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
     * 兼容对接 Hook 回调接口
     * @return [Any] 原始接口
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
     * @param member 需要 Hook 的方法、构造方法
     * @param callback 回调
     * @return [YukiMemberHook.HookedMember] or null
     */
    internal fun hookMember(member: Member?, callback: YukiHookCallback): YukiMemberHook.HookedMember? {
        if (member == null) return null
        return when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> XposedBridge.hookMethod(member, callback.compat()).compat()
            HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
        }
    }

    /**
     * 执行未进行 Hook 的原始 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     */
    internal fun invokeOriginalMember(member: Member?, instance: Any?, args: Array<out Any?>?): Any? {
        if (member == null) return null
        return when (HookApiCategoryHelper.currentCategory) {
            HookApiCategory.ROVO89_XPOSED -> XposedBridge.invokeOriginalMethod(member, instance, args)
            HookApiCategory.UNKNOWN -> throwUnsupportedHookApiError()
        }
    }

    /**
     * 使用当前 Hook API 自带的日志功能打印日志
     * @param msg 日志打印的内容
     * @param e 异常堆栈信息 - 默认空
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

    /** 抛出不支持的 API 类型异常 */
    private fun throwUnsupportedHookApiError(): Nothing =
        error("YukiHookAPI cannot support current Hook API or cannot found any available Hook APIs in current environment")
}