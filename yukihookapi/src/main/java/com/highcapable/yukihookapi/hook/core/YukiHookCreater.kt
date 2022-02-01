/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.highcapable.yukihookapi.hook.core

import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.param.HookParam
import com.highcapable.yukihookapi.param.PackageParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

/**
 * YukiHook 核心类实现方法
 * 这是一个 API 对接类 - 实现原生对接 [XposedBridge]
 * @param instance 需要传入 [PackageParam] 实现方法调用
 * @param hookClass 要 Hook 的 [Class]
 */
class YukiHookCreater(private val instance: PackageParam, val hookClass: Class<*>) {

    /** @call Base Field */
    private var beforeHookCallback: (HookParam.() -> Unit)? = null

    /** @call Base Field */
    private var afterHookCallback: (HookParam.() -> Unit)? = null

    /** @call Base Field */
    private var replaceHookCallback: (HookParam.() -> Any?)? = null

    /** 是否为替换模式 */
    private var isReplaceMode = false

    /** 设置要 Hook 的方法、构造类 */
    var grabMember: Member? = null

    /**
     * 在方法执行完成前 Hook
     * 不可与 [replaceAny] [replaceVoid] [replaceTo] 同时使用
     * @param initiate [HookParam] 方法体
     */
    fun beforeHook(initiate: HookParam.() -> Unit) {
        isReplaceMode = false
        beforeHookCallback = initiate
    }

    /**
     * 在方法执行完成后 Hook
     * 不可与 [replaceAny] [replaceVoid] [replaceTo] 同时使用
     * @param initiate [HookParam] 方法体
     */
    fun afterHook(initiate: HookParam.() -> Unit) {
        isReplaceMode = false
        afterHookCallback = initiate
    }

    /**
     * 替换此方法内容 - 给出返回值
     * 不可与 [beforeHook] [afterHook] 同时使用
     * @param initiate [HookParam] 方法体
     */
    fun replaceAny(initiate: HookParam.() -> Any?) {
        isReplaceMode = true
        replaceHookCallback = initiate
    }

    /**
     * 替换此方法内容 - 没有返回值 (Void)
     * 不可与 [beforeHook] [afterHook] 同时使用
     * @param initiate [HookParam] 方法体
     */
    fun replaceVoid(initiate: HookParam.() -> Unit) {
        isReplaceMode = true
        replaceHookCallback = initiate
    }

    /**
     * 替换方法返回值
     * 不可与 [beforeHook] [afterHook] 同时使用
     * @param any 要替换为的返回值对象
     */
    fun replaceTo(any: Any?) {
        isReplaceMode = true
        replaceHookCallback = { any }
    }

    /**
     * 替换方法返回值为 true
     * 确保替换方法的返回对象为 [Boolean]
     * 不可与 [beforeHook] [afterHook] 同时使用
     */
    fun replaceToTrue() {
        isReplaceMode = true
        replaceHookCallback = { true }
    }

    /**
     * 替换方法返回值为 false
     * 确保替换方法的返回对象为 [Boolean]
     * 不可与 [beforeHook] [afterHook] 同时使用
     */
    fun replaceToFalse() {
        isReplaceMode = true
        replaceHookCallback = { false }
    }

    /**
     * 拦截此方法
     * 不可与 [beforeHook] [afterHook] 同时使用
     */
    fun intercept() {
        isReplaceMode = true
        replaceHookCallback = { null }
    }

    /**
     * Hook 执行入口 - 不可在外部调用
     * @throws IllegalStateException 如果必要参数没有被设置
     */
    @DoNotUseMethod
    fun hook() {
        if (grabMember == null) error("Target hook method cannot be null")
        if (isReplaceMode)
            XposedBridge.hookMethod(grabMember, object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    if (param == null) return null
                    return replaceHookCallback?.invoke(HookParam(param))
                }
            })
        else
            XposedBridge.hookMethod(grabMember, object : XC_MethodHook() {
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    if (param == null) return
                    beforeHookCallback?.invoke(HookParam(param))
                }

                override fun afterHookedMethod(param: MethodHookParam?) {
                    if (param == null) return
                    afterHookCallback?.invoke(HookParam(param))
                }
            })
    }
}