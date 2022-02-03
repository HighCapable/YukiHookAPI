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
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.core

import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import com.highcapable.yukihookapi.param.HookParam
import com.highcapable.yukihookapi.param.PackageParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * YukiHook 核心类实现方法
 *
 * 这是一个 API 对接类 - 实现原生对接 [XposedBridge]
 * @param packageParam 需要传入 [PackageParam] 实现方法调用
 * @param hookClass 要 Hook 的 [Class]
 */
class YukiHookCreater(private val packageParam: PackageParam, val hookClass: Class<*>) {

    /** 设置要 Hook 的方法、构造类 */
    private var hookMembers = HashMap<String, MemberHookCreater>()

    /**
     * 注入要 Hook 的方法、构造类
     * @param initiate 方法体
     */
    fun injectMember(initiate: MemberHookCreater.() -> Unit) =
        MemberHookCreater().apply(initiate).apply {
            hookMembers[toString()] = this
        }.create()

    /**
     * Hook 执行入口 - 不可在外部调用
     * @throws IllegalStateException 如果必要参数没有被设置
     */
    @DoNotUseMethod
    fun hook() {
        if (hookMembers.isEmpty()) error("Hook Members is empty,hook aborted")
        hookMembers.forEach { (_, hooker) -> hooker.hook() }
    }

    /**
     * 智能全局方法、构造类查找类实现方法
     *
     * 处理需要 Hook 的方法
     */
    inner class MemberHookCreater {

        /** [beforeHook] 回调 */
        private var beforeHookCallback: (HookParam.() -> Unit)? = null

        /** [afterHook] 回调 */
        private var afterHookCallback: (HookParam.() -> Unit)? = null

        /** [replaceAny]、[replaceUnit]、[replaceTo] 等回调 */
        private var replaceHookCallback: (HookParam.() -> Any?)? = null

        /** [onFailure] 回调 */
        private var onFailureCallback: ((HookParam?, Throwable) -> Unit)? = null

        /** 是否为替换 Hook 模式 */
        private var isReplaceHookMode = false

        /** 是否停止 Hook */
        private var isStopHookMode = false

        /**
         * 手动指定要 Hook 的方法、构造类
         *
         * 你可以调用 [hookClass] 来手动查询要 Hook 的方法
         */
        var member: Member? = null

        /**
         * 查找需要 Hook 的方法
         *
         * 你只能使用一次 [method] 或 [constructor] 方法 - 否则结果会被替换
         * @param initiate 方法体
         */
        fun method(initiate: MethodFinder.() -> Unit) {
            runCatching {
                member = MethodFinder().apply(initiate).find()
            }.onFailure {
                isStopHookMode = true
                onFailureCallback?.invoke(null, it) ?: onHookFailure(it)
            }
        }

        /**
         * 查找需要 Hook 的构造类
         *
         * 你只能使用一次 [method] 或 [constructor] 方法 - 否则结果会被替换
         * @param initiate 方法体
         */
        fun constructor(initiate: ConstructorFinder.() -> Unit) {
            runCatching {
                member = ConstructorFinder().apply(initiate).find()
            }.onFailure {
                isStopHookMode = true
                onFailureCallback?.invoke(null, it) ?: onHookFailure(it)
            }
        }

        /**
         * 在方法执行完成前 Hook
         *
         * 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun beforeHook(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = false
            beforeHookCallback = initiate
        }

        /**
         * 在方法执行完成后 Hook
         *
         * 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun afterHook(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = false
            afterHookCallback = initiate
        }

        /**
         * 替换此方法内容 - 给出返回值
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceAny(initiate: HookParam.() -> Any?) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
        }

        /**
         * 替换此方法内容 - 没有返回值 ([Unit])
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceUnit(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
        }

        /**
         * 替换方法返回值
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param any 要替换为的返回值对象
         */
        fun replaceTo(any: Any?) {
            isReplaceHookMode = true
            replaceHookCallback = { any }
        }

        /**
         * 替换方法返回值为 true
         *
         * 确保替换方法的返回对象为 [Boolean]
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         */
        fun replaceToTrue() {
            isReplaceHookMode = true
            replaceHookCallback = { true }
        }

        /**
         * 替换方法返回值为 false
         *
         * 确保替换方法的返回对象为 [Boolean]
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         */
        fun replaceToFalse() {
            isReplaceHookMode = true
            replaceHookCallback = { false }
        }

        /**
         * 拦截此方法
         *
         * 这将会禁止此方法执行并返回 null
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         */
        fun intercept() {
            isReplaceHookMode = true
            replaceHookCallback = { null }
        }

        /**
         * Hook 创建入口 - 不可在外部调用
         * @return [MemberHookResult]
         */
        @DoNotUseMethod
        fun create() = MemberHookResult()

        /**
         * Hook 执行入口 - 不可在外部调用
         * @throws IllegalStateException 如果必要参数没有被设置
         */
        @DoNotUseMethod
        fun hook() {
            if (isStopHookMode) return
            member?.also { member ->
                runCatching {
                    if (isReplaceHookMode)
                        XposedBridge.hookMethod(member, object : XC_MethodReplacement() {
                            override fun replaceHookedMethod(baseParam: MethodHookParam?): Any? {
                                if (baseParam == null) return null
                                return HookParam(baseParam).let { param ->
                                    try {
                                        replaceHookCallback?.invoke(param)
                                    } catch (e: Throwable) {
                                        onFailureCallback?.invoke(param, e) ?: onHookFailure(e)
                                        null
                                    }
                                }
                            }
                        })
                    else
                        XposedBridge.hookMethod(member, object : XC_MethodHook() {
                            override fun beforeHookedMethod(baseParam: MethodHookParam?) {
                                if (baseParam == null) return
                                HookParam(baseParam).also { param ->
                                    runCatching {
                                        beforeHookCallback?.invoke(param)
                                    }.onFailure {
                                        onFailureCallback?.invoke(param, it) ?: onHookFailure(it)
                                    }
                                }
                            }

                            override fun afterHookedMethod(baseParam: MethodHookParam?) {
                                if (baseParam == null) return
                                HookParam(baseParam).also { param ->
                                    runCatching {
                                        afterHookCallback?.invoke(param)
                                    }.onFailure {
                                        onFailureCallback?.invoke(param, it) ?: onHookFailure(it)
                                    }
                                }
                            }
                        })
                }.onFailure {
                    onFailureCallback?.invoke(null, it) ?: onHookFailure(it)
                }
            } ?: error("Hook Member cannot be null")
        }

        /**
         * Hook 失败但未设置 [onFailureCallback] 将默认输出失败信息
         * @param throwable 异常信息
         */
        private fun onHookFailure(throwable: Throwable) =
            loggerE(msg = "Try to hook $hookClass[$member] got an Exception", e = throwable)

        override fun toString() = "$member#YukiHook"

        /**
         * [Method] 查找类
         *
         * 可通过指定类型查找指定方法
         */
        inner class MethodFinder {

            /** 方法参数 */
            private var params: Array<out Class<*>>? = null

            /** 方法名 */
            var name = ""

            /** 方法返回值 */
            var returnType: Class<*>? = null

            /**
             * 方法参数
             * @param param 参数数组
             */
            fun param(vararg param: Class<*>) {
                params = param
            }

            /**
             * 得到方法 - 不能在外部调用
             * @return [Method]
             * @throws NoSuchMethodError 如果找不到方法
             */
            @DoNotUseMethod
            fun find(): Method =
                when {
                    name.isBlank() -> error("Method name cannot be empty")
                    else ->
                        if (params != null)
                            ReflectionUtils.findMethodBestMatch(hookClass, returnType, name, *params!!)
                        else ReflectionUtils.findMethodNoParam(hookClass, returnType, name)
                }
        }

        /**
         * [Constructor] 查找类
         *
         * 可通过指定类型查找指定构造类
         */
        inner class ConstructorFinder {

            /** 方法参数 */
            private var params: Array<out Class<*>>? = null

            /**
             * 方法参数
             * @param param 参数数组
             */
            fun param(vararg param: Class<*>) {
                params = param
            }

            /**
             * 得到构造类 - 不能在外部调用
             * @return [Constructor]
             * @throws NoSuchMethodError 如果找不到构造类
             */
            @DoNotUseMethod
            fun find(): Constructor<*> =
                if (params != null)
                    ReflectionUtils.findConstructorExact(hookClass, *params!!)
                else ReflectionUtils.findConstructorExact(hookClass)
        }

        /**
         * 监听 Hook 结果实现类
         *
         * 可在这里处理失败事件
         */
        inner class MemberHookResult {

            /**
             * 监听 Hook 过程发生错误的回调方法
             * @param initiate 回调错误 - ([HookParam] 当前 Hook 实例 or null,[Throwable] 异常)
             */
            fun onFailure(initiate: (HookParam?, Throwable) -> Unit) {
                onFailureCallback = initiate
            }
        }
    }
}