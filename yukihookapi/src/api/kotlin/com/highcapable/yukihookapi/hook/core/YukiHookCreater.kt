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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.highcapable.yukihookapi.hook.core

import android.os.SystemClock
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.core.finder.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.MethodFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Field
import java.lang.reflect.Member

/**
 * [YukiHookAPI] 核心 Hook 实现类
 *
 * 这是一个 API 对接类 - 实现原生对接 [XposedBridge]
 * @param packageParam 需要传入 [PackageParam] 实现方法调用
 * @param hookClass 要 Hook 的 [HookClass] 实例
 */
class YukiHookCreater(private val packageParam: PackageParam, private val hookClass: HookClass) {

    /**
     * Hook 模式定义
     */
    enum class HookMemberMode { HOOK_ALL_METHODS, HOOK_ALL_CONSTRUCTORS, HOOK_CONVENTIONAL }

    /** 是否对当前 [YukiHookCreater] 禁止执行 Hook 操作 */
    private var isDisableCreaterRunHook = false

    /** 设置要 Hook 的方法、构造类 */
    private var hookMembers = HashSet<MemberHookCreater>()

    /** [hookClass] 找不到时出现的错误回调 */
    private var onHookClassNotFoundFailureCallback: ((Throwable) -> Unit)? = null

    /**
     * 得到当前被 Hook 的 [Class]
     *
     * - ❗不推荐直接使用 - 万一得不到 [Class] 对象则会无法处理异常导致崩溃
     * @return [Class]
     * @throws IllegalStateException 如果当前 [Class] 未被正确装载
     */
    val instanceClass
        get() = hookClass.instance ?: error("Cannot get hook class \"${hookClass.name}\" cause ${hookClass.throwable?.message}")

    /**
     * 注入要 Hook 的方法、构造类
     * @param tag 可设置标签 - 在发生错误时方便进行调试
     * @param initiate 方法体
     * @return [MemberHookCreater.Result]
     */
    fun injectMember(tag: String = "Default", initiate: MemberHookCreater.() -> Unit) =
        MemberHookCreater(tag).apply(initiate).apply { hookMembers.add(this) }.build()

    /**
     * Hook 执行入口
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @throws IllegalStateException 如果必要参数没有被设置
     * @return [Result]
     */
    @YukiPrivateApi
    fun hook(): Result {
        if (YukiHookAPI.hasXposedBridge.not()) return Result()
        return if (hookMembers.isEmpty()) error("Hook Members is empty, hook aborted")
        else Result().also {
            Thread {
                /** 延迟使得方法取到返回值 */
                SystemClock.sleep(1)
                when {
                    isDisableCreaterRunHook.not() && hookClass.instance != null -> {
                        it.onPrepareHook?.invoke()
                        hookMembers.forEach { m -> m.hook() }
                    }
                    isDisableCreaterRunHook.not() && hookClass.instance == null ->
                        if (onHookClassNotFoundFailureCallback == null)
                            yLoggerE(msg = "HookClass [${hookClass.name}] not found", e = hookClass.throwable)
                        else onHookClassNotFoundFailureCallback?.invoke(hookClass.throwable ?: Throwable("[${hookClass.name}] not found"))
                }
            }.start()
        }
    }

    /**
     * Hook 核心功能实现类
     *
     * 查找和处理需要 Hook 的方法、构造类
     * @param tag 当前设置的标签
     */
    inner class MemberHookCreater(var tag: String) {

        /** [beforeHook] 回调 */
        private var beforeHookCallback: (HookParam.() -> Unit)? = null

        /** [afterHook] 回调 */
        private var afterHookCallback: (HookParam.() -> Unit)? = null

        /** [replaceAny]、[replaceUnit]、[replaceTo] 等回调 */
        private var replaceHookCallback: (HookParam.() -> Any?)? = null

        /** Hook 成功时回调 */
        private var onHookedCallback: ((Member) -> Unit)? = null

        /** 找不到 [member] 出现错误回调 */
        private var onNoSuchMemberFailureCallback: ((Throwable) -> Unit)? = null

        /** Hook 过程中出现错误回调 */
        private var onConductFailureCallback: ((HookParam, Throwable) -> Unit)? = null

        /** Hook 开始时出现错误回调 */
        private var onHookingFailureCallback: ((Throwable) -> Unit)? = null

        /** 全部错误回调 */
        private var onAllFailureCallback: ((Throwable) -> Unit)? = null

        /** 查找过程中发生的异常 */
        private var findingThrowable: Throwable? = null

        /** 是否为替换 Hook 模式 */
        private var isReplaceHookMode = false

        /** 标识是否已经设置了要 Hook 的 [member] */
        private var isHookMemberSetup = false

        /** 是否对当前 [MemberHookCreater] 禁止执行 Hook 操作 */
        private var isDisableMemberRunHook = false

        /** Hook 当前模式类型 */
        private var hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL

        /** 全部方法的名称 */
        private var allMethodsName = ""

        /**
         * 手动指定要 Hook 的方法、构造类
         *
         * 你可以调用 [instanceClass] 来手动查询要 Hook 的方法
         */
        var member: Member? = null

        /**
         * 查找并 Hook [hookClass] 中指定 [name] 的全部方法
         *
         * 在同一个 [injectMember] 中
         *
         * 你只能使用一次 [allMethods]、[allConstructors]、[method]、[constructor] 方法 - 否则结果会被替换
         *
         * - ❗警告：无法准确处理每个方法的返回值和 param - 建议使用 [method] 对每个方法单独 Hook
         *
         * - ❗如果 [hookClass] 中没有方法可能会发生错误
         * @param name 方法名称
         */
        fun allMethods(name: String) {
            allMethodsName = name
            hookMemberMode = HookMemberMode.HOOK_ALL_METHODS
            isHookMemberSetup = true
        }

        /**
         * 查找并 Hook [hookClass] 中的全部构造方法
         *
         * 在同一个 [injectMember] 中
         *
         * 你只能使用一次 [allMethods]、[allConstructors]、[method]、[constructor] 方法 - 否则结果会被替换
         *
         * - ❗警告：无法准确处理每个构造方法的 param - 建议使用 [constructor] 对每个构造方法单独 Hook
         *
         * - ❗如果 [hookClass] 中没有构造方法可能会发生错误
         */
        fun allConstructors() {
            allMethodsName = "<init>"
            hookMemberMode = HookMemberMode.HOOK_ALL_CONSTRUCTORS
            isHookMemberSetup = true
        }

        /**
         * 查找 [hookClass] 需要 Hook 的方法
         *
         * 在同一个 [injectMember] 中
         *
         * 你只能使用一次 [allMethods]、[allConstructors]、[method]、[constructor] 方法 - 否则结果会被替换
         * @param initiate 方法体
         * @return [MethodFinder.Result]
         */
        fun method(initiate: MethodFinder.() -> Unit) = try {
            hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL
            isHookMemberSetup = true
            MethodFinder(hookInstance = this, hookClass.instance).apply(initiate).build(isBind = true)
        } catch (e: Throwable) {
            findingThrowable = e
            MethodFinder(hookInstance = this).failure(e)
        }

        /**
         * 查找 [hookClass] 需要 Hook 的构造方法
         *
         * 在同一个 [injectMember] 中
         *
         * 你只能使用一次 [allMethods]、[allConstructors]、[method]、[constructor] 方法 - 否则结果会被替换
         * @param initiate 方法体
         * @return [ConstructorFinder.Result]
         */
        fun constructor(initiate: ConstructorFinder.() -> Unit = {}) = try {
            hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL
            isHookMemberSetup = true
            ConstructorFinder(hookInstance = this, hookClass.instance).apply(initiate).build(isBind = true)
        } catch (e: Throwable) {
            findingThrowable = e
            ConstructorFinder(hookInstance = this).failure(e)
        }

        /**
         * 使用当前 [hookClass] 查找并得到 [Field]
         * @param initiate 方法体
         * @return [FieldFinder.Result]
         */
        fun HookParam.field(initiate: FieldFinder.() -> Unit) =
            if (hookClass.instance == null) FieldFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else FieldFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

        /**
         * 使用当前 [hookClass] 查找并得到方法
         * @param initiate 方法体
         * @return [MethodFinder.Result]
         */
        fun HookParam.method(initiate: MethodFinder.() -> Unit) =
            if (hookClass.instance == null) MethodFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else MethodFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

        /**
         * 使用当前 [hookClass] 查找并得到构造方法
         * @param initiate 方法体
         * @return [ConstructorFinder.Result]
         */
        fun HookParam.constructor(initiate: ConstructorFinder.() -> Unit) =
            if (hookClass.instance == null) ConstructorFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else ConstructorFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

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
         * 拦截并替换此方法内容 - 给出返回值
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceAny(initiate: HookParam.() -> Any?) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
        }

        /**
         * 拦截并替换此方法内容 - 没有返回值 ([Unit])
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceUnit(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
        }

        /**
         * 拦截并替换方法返回值
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         * @param any 要替换为的返回值对象
         */
        fun replaceTo(any: Any?) {
            isReplaceHookMode = true
            replaceHookCallback = { any }
        }

        /**
         * 拦截并替换方法返回值为 true
         *
         * - ❗确保替换方法的返回对象为 [Boolean]
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         */
        fun replaceToTrue() {
            isReplaceHookMode = true
            replaceHookCallback = { true }
        }

        /**
         * 拦截并替换方法返回值为 false
         *
         * - ❗确保替换方法的返回对象为 [Boolean]
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
         * - ❗这将会禁止此方法执行并返回 null
         *
         * 不可与 [beforeHook]、[afterHook] 同时使用
         */
        fun intercept() {
            isReplaceHookMode = true
            replaceHookCallback = { null }
        }

        /**
         * Hook 创建入口
         *
         * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
         * @return [Result]
         */
        @YukiPrivateApi
        fun build() = Result()

        /**
         * Hook 执行入口
         *
         * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
         */
        @YukiPrivateApi
        fun hook() {
            if (YukiHookAPI.hasXposedBridge.not() || isDisableMemberRunHook) return
            if (hookClass.instance == null) {
                (hookClass.throwable ?: Throwable("HookClass [${hookClass.name}] not found")).also {
                    onHookingFailureCallback?.invoke(it)
                    onAllFailureCallback?.invoke(it)
                    if (isNotIgnoredHookingFailure) onHookFailureMsg(it)
                }
                return
            }
            /** 定义替换 Hook 回调方法体 */
            val replaceMent = object : XC_MethodReplacement() {
                override fun replaceHookedMethod(baseParam: MethodHookParam?): Any? {
                    if (baseParam == null) return null
                    return HookParam(createrInstance = this@YukiHookCreater, HookParamWrapper(baseParam)).let { param ->
                        try {
                            if (replaceHookCallback != null)
                                onHookLogMsg(msg = "Replace Hook Member [${member ?: "All Member $allMethodsName"}] done [$tag]")
                            replaceHookCallback?.invoke(param)
                        } catch (e: Throwable) {
                            onConductFailureCallback?.invoke(param, e)
                            onAllFailureCallback?.invoke(e)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) onHookFailureMsg(e)
                            null
                        }
                    }
                }
            }

            /** 定义前后 Hook 回调方法体 */
            val beforeAfterHook = object : XC_MethodHook() {
                override fun beforeHookedMethod(baseParam: MethodHookParam?) {
                    if (baseParam == null) return
                    HookParam(createrInstance = this@YukiHookCreater, HookParamWrapper(baseParam)).also { param ->
                        runCatching {
                            beforeHookCallback?.invoke(param)
                            if (beforeHookCallback != null)
                                onHookLogMsg(msg = "Before Hook Member [${member ?: "All of \"$allMethodsName\""}] done [$tag]")
                        }.onFailure {
                            onConductFailureCallback?.invoke(param, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) onHookFailureMsg(it)
                        }
                    }
                }

                override fun afterHookedMethod(baseParam: MethodHookParam?) {
                    if (baseParam == null) return
                    HookParam(createrInstance = this@YukiHookCreater, HookParamWrapper(baseParam)).also { param ->
                        runCatching {
                            afterHookCallback?.invoke(param)
                            if (afterHookCallback != null)
                                onHookLogMsg(msg = "After Hook Member [${member ?: "All of \"$allMethodsName\""}] done [$tag]")
                        }.onFailure {
                            onConductFailureCallback?.invoke(param, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) onHookFailureMsg(it)
                        }
                    }
                }
            }
            if (hookMemberMode == HookMemberMode.HOOK_CONVENTIONAL)
                if (member != null)
                    member.also { member ->
                        runCatching {
                            if (isReplaceHookMode)
                                XposedBridge.hookMethod(member, replaceMent)?.hookedMethod?.also { onHookedCallback?.invoke(it) }
                                    ?: error("Hook Member [$member] failed")
                            else XposedBridge.hookMethod(member, beforeAfterHook)?.hookedMethod?.also { onHookedCallback?.invoke(it) }
                                ?: error("Hook Member [$member] failed")
                        }.onFailure {
                            onHookingFailureCallback?.invoke(it)
                            onAllFailureCallback?.invoke(it)
                            if (isNotIgnoredHookingFailure) onHookFailureMsg(it)
                        }
                    }
                else Throwable("Finding Error isSetUpMember [$isHookMemberSetup] [$tag]").also {
                    onNoSuchMemberFailureCallback?.invoke(it)
                    onHookingFailureCallback?.invoke(it)
                    onAllFailureCallback?.invoke(it)
                    if (isNotIgnoredNoSuchMemberFailure)
                        yLoggerE(
                            msg = if (isHookMemberSetup)
                                "Hooked Member with a finding error by $hookClass [$tag]"
                            else "Hooked Member cannot be non-null by $hookClass [$tag]",
                            e = findingThrowable ?: it
                        )
                }
            else runCatching {
                when (hookMemberMode) {
                    HookMemberMode.HOOK_ALL_METHODS ->
                        if (isReplaceHookMode)
                            XposedBridge.hookAllMethods(hookClass.instance, allMethodsName, replaceMent).also {
                                if (it.isEmpty()) throw NoSuchMethodError("No Method name \"$allMethodsName\" matched")
                                else it.forEach { e -> onHookedCallback?.invoke(e.hookedMethod) }
                            }
                        else XposedBridge.hookAllMethods(hookClass.instance, allMethodsName, beforeAfterHook).also {
                            if (it.isEmpty()) throw NoSuchMethodError("No Method name \"$allMethodsName\" matched")
                            else it.forEach { e -> onHookedCallback?.invoke(e.hookedMethod) }
                        }
                    HookMemberMode.HOOK_ALL_CONSTRUCTORS ->
                        if (isReplaceHookMode)
                            XposedBridge.hookAllConstructors(hookClass.instance, replaceMent).also {
                                if (it.isEmpty()) throw NoSuchMethodError("No Constructor matched")
                                else it.forEach { e -> onHookedCallback?.invoke(e.hookedMethod) }
                            }
                        else XposedBridge.hookAllConstructors(hookClass.instance, beforeAfterHook).also {
                            if (it.isEmpty()) throw NoSuchMethodError("No Constructor matched")
                            else it.forEach { e -> onHookedCallback?.invoke(e.hookedMethod) }
                        }
                    else -> error("Hooked got a no error possible")
                }
            }.onFailure {
                val isMemberNotFound = it.message?.lowercase()?.contains(other = "nosuch") == true ||
                        it is NoSuchMethodError || it is NoSuchFieldError
                if (isMemberNotFound) onNoSuchMemberFailureCallback?.invoke(it)
                onAllFailureCallback?.invoke(it)
                if ((isNotIgnoredHookingFailure && isMemberNotFound.not()) || (isNotIgnoredNoSuchMemberFailure && isMemberNotFound))
                    yLoggerE(msg = "Hooked All Members with an error in Class [$hookClass] [$tag]", e = it)
            }
        }

        /**
         * Hook 过程中开启了 [YukiHookAPI.Configs.isDebug] 输出调试信息
         * @param msg 调试日志内容
         */
        private fun onHookLogMsg(msg: String) {
            if (YukiHookAPI.Configs.isDebug) yLoggerI(msg = msg)
        }

        /**
         * Hook 失败但未设置 [onAllFailureCallback] 将默认输出失败信息
         * @param throwable 异常信息
         */
        private fun onHookFailureMsg(throwable: Throwable) =
            yLoggerE(msg = "Try to hook ${hookClass.instance ?: hookClass.name}[$member] got an Exception [$tag]", e = throwable)

        /**
         * 判断是否没有设置 Hook 过程中的任何异常拦截
         * @return [Boolean] 没有设置任何异常拦截
         */
        private val isNotIgnoredHookingFailure get() = onHookingFailureCallback == null && onAllFailureCallback == null

        /**
         * 判断是否没有设置 Hook 过程中 [member] 找不到的任何异常拦截
         * @return [Boolean] 没有设置任何异常拦截
         */
        internal val isNotIgnoredNoSuchMemberFailure get() = onNoSuchMemberFailureCallback == null && isNotIgnoredHookingFailure

        override fun toString() = "[tag] $tag [class] $hookClass [member] $member $allMethodsName [mode] $hookMemberMode"

        /**
         * 监听 Hook 结果实现类
         *
         * 可在这里处理失败事件监听
         */
        inner class Result {

            /**
             * 创建监听事件方法体
             * @param initiate 方法体
             * @return [Result] 可继续向下监听
             */
            fun result(initiate: Result.() -> Unit) = apply(initiate)

            /**
             * 添加执行 Hook 需要满足的条件
             *
             * 不满足条件将直接停止 Hook
             * @param initiate 条件方法体
             * @return [Result] 可继续向下监听
             */
            fun by(initiate: () -> Boolean): Result {
                isDisableMemberRunHook = (runCatching { initiate() }.getOrNull() ?: false).not()
                if (isDisableMemberRunHook) ignoredAllFailure()
                return this
            }

            /**
             * 监听 [member] Hook 成功的回调方法
             *
             * 在首次 Hook 成功后回调
             * @param initiate 回调被 Hook 的 [Member]
             * @return [Result] 可继续向下监听
             */
            fun onHooked(initiate: (Member) -> Unit): Result {
                onHookedCallback = initiate
                return this
            }

            /**
             * 监听 [member] 不存在发生错误的回调方法
             * @param initiate 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onNoSuchMemberFailure(initiate: (Throwable) -> Unit): Result {
                onNoSuchMemberFailureCallback = initiate
                return this
            }

            /**
             * 忽略 [member] 不存在发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredNoSuchMemberFailure() = onNoSuchMemberFailure {}

            /**
             * 监听 Hook 进行过程中发生错误的回调方法
             * @param initiate 回调错误 - ([HookParam] 当前 Hook 实例,[Throwable] 异常)
             * @return [Result] 可继续向下监听
             */
            fun onConductFailure(initiate: (HookParam, Throwable) -> Unit): Result {
                onConductFailureCallback = initiate
                return this
            }

            /**
             * 忽略 Hook 进行过程中发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredConductFailure() = onConductFailure { _, _ -> }

            /**
             * 监听 Hook 开始时发生错误的回调方法
             * @param initiate 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onHookingFailure(initiate: (Throwable) -> Unit): Result {
                onHookingFailureCallback = initiate
                return this
            }

            /**
             * 忽略 Hook 开始时发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredHookingFailure() = onHookingFailure {}

            /**
             * 监听全部 Hook 过程发生错误的回调方法
             * @param initiate 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onAllFailure(initiate: (Throwable) -> Unit): Result {
                onAllFailureCallback = initiate
                return this
            }

            /**
             * 忽略全部 Hook 过程发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredAllFailure() = onAllFailure {}
        }
    }

    /**
     * 监听全部 Hook 结果实现类
     *
     * 可在这里处理失败事件监听
     */
    inner class Result {

        /** Hook 开始时的监听事件回调 */
        internal var onPrepareHook: (() -> Unit)? = null

        /**
         * 创建监听事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 添加执行 Hook 需要满足的条件
         *
         * 不满足条件将直接停止 Hook
         * @param initiate 条件方法体
         * @return [Result] 可继续向下监听
         */
        fun by(initiate: () -> Boolean): Result {
            isDisableCreaterRunHook = (runCatching { initiate() }.getOrNull() ?: false).not()
            return this
        }

        /**
         * 监听 [hookClass] 存在时准备开始 Hook 的操作
         * @param initiate 条件方法体
         * @return [Result] 可继续向下监听
         */
        fun onPrepareHook(initiate: () -> Unit): Result {
            onPrepareHook = initiate
            return this
        }

        /**
         * 监听 [hookClass] 找不到时发生错误的回调方法
         * @param initiate 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onHookClassNotFoundFailure(initiate: (Throwable) -> Unit): Result {
            onHookClassNotFoundFailureCallback = initiate
            return this
        }

        /**
         * 忽略 [hookClass] 找不到时出现的错误
         * @return [Result] 可继续向下监听
         */
        fun ignoredHookClassNotFoundFailure(): Result {
            by { hookClass.instance != null }
            return this
        }
    }
}