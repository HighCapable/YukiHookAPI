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
 * This file is Created by fankes on 2023/1/9.
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
    private fun XC_MethodHook.Unhook.compat() = YukiHookCallbackDelegate.createHookedMemberCallback(hookedMethod) { unhook() }

    /**
     * [HookApiCategory.ROVO89_XPOSED]
     *
     * 兼容对接 Hook 结果回调接口
     * @return [YukiHookCallback.Param]
     */
    private fun XC_MethodHook.MethodHookParam.compat() =
        YukiHookCallbackDelegate.createParamCallback(
            dataExtra = extra,
            member = method,
            instance = thisObject,
            args = args,
            hasThrowable = hasThrowable(),
            resultCallback = { result = it },
            throwableCallback = { throwable = it },
            result = result,
            throwable = throwable
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
    internal fun invokeOriginalMember(member: Member?, instance: Any?, vararg args: Any?): Any? {
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