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
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")

package com.highcapable.yukihookapi.hook.core

import android.os.SystemClock
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.core.finder.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import java.lang.reflect.Field
import java.lang.reflect.Member

/**
 * [YukiHookAPI] 的 [Member] 核心 Hook 实现类
 *
 * 核心 API 对接 [YukiHookBridge.Hooker] 实现
 * @param packageParam 需要传入 [PackageParam] 实现方法调用
 * @param hookClass 要 Hook 的 [HookClass] 实例
 */
class YukiMemberHookCreater(@PublishedApi internal val packageParam: PackageParam, @PublishedApi internal val hookClass: HookClass) {

    /** 默认 Hook 回调优先级 */
    val PRIORITY_DEFAULT = YukiHookBridge.Hooker.PRIORITY_DEFAULT

    /** 延迟回调 Hook 方法结果 */
    val PRIORITY_LOWEST = YukiHookBridge.Hooker.PRIORITY_LOWEST

    /** 更快回调 Hook 方法结果 */
    val PRIORITY_HIGHEST = YukiHookBridge.Hooker.PRIORITY_HIGHEST

    /**
     * Hook 模式定义
     */
    @PublishedApi
    internal enum class HookMemberMode { HOOK_ALL_METHODS, HOOK_ALL_CONSTRUCTORS, HOOK_CONVENTIONAL }

    /** [hookClass] 找不到时出现的错误回调 */
    private var onHookClassNotFoundFailureCallback: ((Throwable) -> Unit)? = null

    /** 是否对当前 [YukiMemberHookCreater] 禁止执行 Hook 操作 */
    @PublishedApi
    internal var isDisableCreaterRunHook = false

    /** 设置要 Hook 的方法、构造类 */
    @PublishedApi
    internal var preHookMembers = HashMap<String, MemberHookCreater>()

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
     * @param priority Hook 优先级 - 默认 [PRIORITY_DEFAULT]
     * @param tag 可设置标签 - 在发生错误时方便进行调试
     * @param initiate 方法体
     * @return [MemberHookCreater.Result]
     */
    inline fun injectMember(priority: Int = PRIORITY_DEFAULT, tag: String = "Default", initiate: MemberHookCreater.() -> Unit) =
        MemberHookCreater(priority, tag, packageParam.exhibitName).apply(initiate).apply { preHookMembers[toString()] = this }.build()

    /**
     * Hook 执行入口
     * @throws IllegalStateException 如果必要参数没有被设置
     * @return [Result]
     */
    @PublishedApi
    internal fun hook(): Result {
        if (YukiHookBridge.hasXposedBridge.not()) return Result()
        /** 过滤 [HookEntryType.ZYGOTE] 与 [HookEntryType.PACKAGE] 或 [HookParam.isCallbackCalled] 已被执行 */
        if (packageParam.wrapper?.type == HookEntryType.RESOURCES && HookParam.isCallbackCalled.not()) return Result()
        return if (preHookMembers.isEmpty()) error("Hook Members is empty, hook aborted")
        else Result().also {
            Thread {
                /** 延迟使得方法取到返回值 */
                SystemClock.sleep(1)
                when {
                    isDisableCreaterRunHook.not() && hookClass.instance != null -> {
                        it.onPrepareHook?.invoke()
                        preHookMembers.forEach { (_, m) -> m.hook() }
                    }
                    isDisableCreaterRunHook.not() && hookClass.instance == null ->
                        if (onHookClassNotFoundFailureCallback == null)
                            yLoggerE(msg = "[${packageParam.exhibitName}] HookClass [${hookClass.name}] not found", e = hookClass.throwable)
                        else onHookClassNotFoundFailureCallback?.invoke(hookClass.throwable ?: Throwable("[${hookClass.name}] not found"))
                }
            }.start()
        }
    }

    /**
     * Hook 核心功能实现类
     *
     * 查找和处理需要 Hook 的方法、构造类
     * @param priority Hook 优先级
     * @param tag 当前设置的标签
     * @param packageName 当前 Hook 的 APP 包名
     */
    inner class MemberHookCreater @PublishedApi internal constructor(
        private val priority: Int,
        internal val tag: String,
        internal val packageName: String
    ) {

        /** 是否已经执行 Hook */
        private var isHooked = false

        /** [beforeHook] 回调 */
        private var beforeHookCallback: (HookParam.() -> Unit)? = null

        /** [afterHook] 回调 */
        private var afterHookCallback: (HookParam.() -> Unit)? = null

        /** [replaceAny]、[replaceUnit] 回调 */
        private var replaceHookCallback: (HookParam.() -> Any?)? = null

        /** [replaceTo]、[replaceToTrue]、[replaceToFalse]、[intercept] 的值 */
        private var replaceHookResult: Any? = null

        /** Hook 成功时回调 */
        private var onHookedCallback: ((Member) -> Unit)? = null

        /** 重复 Hook 时回调 */
        private var onAlreadyHookedCallback: ((Member) -> Unit)? = null

        /** 找不到 [member] 出现错误回调 */
        private var onNoSuchMemberFailureCallback: ((Throwable) -> Unit)? = null

        /** Hook 过程中出现错误回调 */
        private var onConductFailureCallback: ((HookParam, Throwable) -> Unit)? = null

        /** Hook 开始时出现错误回调 */
        private var onHookingFailureCallback: ((Throwable) -> Unit)? = null

        /** 全部错误回调 */
        private var onAllFailureCallback: ((Throwable) -> Unit)? = null

        /** 是否为替换 Hook 模式 */
        private var isReplaceHookMode = false

        /** 是否为仅替换 Hook 结果模式 */
        private var isReplaceHookOnlyResultMode = false

        /** 是否对当前 [MemberHookCreater] 禁止执行 Hook 操作 */
        @PublishedApi
        internal var isDisableMemberRunHook = false

        /** 查找过程中发生的异常 */
        @PublishedApi
        internal var findingThrowable: Throwable? = null

        /** 标识是否已经设置了要 Hook 的 [member] */
        @PublishedApi
        internal var isHookMemberSetup = false

        /** Hook 当前模式类型 */
        @PublishedApi
        internal var hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL

        /** 当前的查找实例 */
        @PublishedApi
        internal var finder: BaseFinder? = null

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
        inline fun method(initiate: MethodFinder.() -> Unit) = try {
            hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL
            isHookMemberSetup = true
            MethodFinder(hookInstance = this, hookClass.instance).apply(initiate).apply { finder = this }.build(isBind = true)
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
        inline fun constructor(initiate: ConstructorFinder.() -> Unit = {}) = try {
            hookMemberMode = HookMemberMode.HOOK_CONVENTIONAL
            isHookMemberSetup = true
            ConstructorFinder(hookInstance = this, hookClass.instance).apply(initiate).apply { finder = this }.build(isBind = true)
        } catch (e: Throwable) {
            findingThrowable = e
            ConstructorFinder(hookInstance = this).failure(e)
        }

        /**
         * 使用当前 [hookClass] 查找并得到 [Field]
         * @param initiate 方法体
         * @return [FieldFinder.Result]
         */
        inline fun HookParam.field(initiate: FieldFinder.() -> Unit) =
            if (hookClass.instance == null) FieldFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else FieldFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

        /**
         * 使用当前 [hookClass] 查找并得到方法
         * @param initiate 方法体
         * @return [MethodFinder.Result]
         */
        inline fun HookParam.method(initiate: MethodFinder.() -> Unit) =
            if (hookClass.instance == null) MethodFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else MethodFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

        /**
         * 使用当前 [hookClass] 查找并得到构造方法
         * @param initiate 方法体
         * @return [ConstructorFinder.Result]
         */
        inline fun HookParam.constructor(initiate: ConstructorFinder.() -> Unit) =
            if (hookClass.instance == null) ConstructorFinder(hookInstance = this@MemberHookCreater).failure(hookClass.throwable)
            else ConstructorFinder(hookInstance = this@MemberHookCreater, hookClass.instance).apply(initiate).build()

        /**
         * 注入要 Hook 的方法、构造类 (嵌套 Hook)
         * @param priority Hook 优先级 - 默认 [PRIORITY_DEFAULT]
         * @param tag 可设置标签 - 在发生错误时方便进行调试
         * @param initiate 方法体
         * @return [MemberHookCreater.Result]
         */
        inline fun HookParam.injectMember(
            priority: Int = PRIORITY_DEFAULT,
            tag: String = "InnerDefault",
            initiate: MemberHookCreater.() -> Unit
        ) = this@YukiMemberHookCreater.injectMember(priority, tag, initiate).also { this@YukiMemberHookCreater.hook() }

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
            isReplaceHookOnlyResultMode = false
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
            isReplaceHookOnlyResultMode = false
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
            isReplaceHookOnlyResultMode = true
            replaceHookResult = any
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
            isReplaceHookOnlyResultMode = true
            replaceHookResult = true
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
            isReplaceHookOnlyResultMode = true
            replaceHookResult = false
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
            isReplaceHookOnlyResultMode = true
            replaceHookResult = null
        }

        /**
         * Hook 创建入口
         * @return [Result]
         */
        @PublishedApi
        internal fun build() = Result()

        /** Hook 执行入口 */
        @PublishedApi
        internal fun hook() {
            if (YukiHookBridge.hasXposedBridge.not() || isHooked || isDisableMemberRunHook) return
            isHooked = true
            finder?.printLogIfExist()
            if (hookClass.instance == null) {
                (hookClass.throwable ?: Throwable("HookClass [${hookClass.name}] not found")).also {
                    onHookingFailureCallback?.invoke(it)
                    onAllFailureCallback?.invoke(it)
                    if (isNotIgnoredHookingFailure) onHookFailureMsg(it)
                }
                return
            }
            /** 定义替换 Hook 的 [HookParam] */
            val replaceHookParam = HookParam(createrInstance = this@YukiMemberHookCreater)

            /** 定义替换 Hook 回调方法体 */
            val replaceMent = object : YukiHookBridge.Hooker.YukiMemberReplacement(priority) {
                override fun replaceHookedMember(wrapper: HookParamWrapper): Any? {
                    return replaceHookParam.assign(wrapper).let { param ->
                        try {
                            if (replaceHookCallback != null || isReplaceHookOnlyResultMode)
                                onHookLogMsg(msg = "Replace Hook Member [${member ?: "All Member $allMethodsName"}] done [$tag]")
                            (if (isReplaceHookOnlyResultMode) replaceHookResult else replaceHookCallback?.invoke(param)).also { HookParam.invoke() }
                        } catch (e: Throwable) {
                            onConductFailureCallback?.invoke(param, e)
                            onAllFailureCallback?.invoke(e)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) onHookFailureMsg(e)
                            null
                        }
                    }
                }
            }

            /** 定义前 Hook 的 [HookParam] */
            val beforeHookParam = HookParam(createrInstance = this@YukiMemberHookCreater)

            /** 定义后 Hook 的 [HookParam] */
            val afterHookParam = HookParam(createrInstance = this@YukiMemberHookCreater)

            /** 定义前后 Hook 回调方法体 */
            val beforeAfterHook = object : YukiHookBridge.Hooker.YukiMemberHook(priority) {
                override fun beforeHookedMember(wrapper: HookParamWrapper) {
                    beforeHookParam.assign(wrapper).also { param ->
                        runCatching {
                            beforeHookCallback?.invoke(param)
                            if (beforeHookCallback != null)
                                onHookLogMsg(msg = "Before Hook Member [${member ?: "All of \"$allMethodsName\""}] done [$tag]")
                            HookParam.invoke()
                        }.onFailure {
                            onConductFailureCallback?.invoke(param, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) onHookFailureMsg(it)
                        }
                    }
                }

                override fun afterHookedMember(wrapper: HookParamWrapper) {
                    afterHookParam.assign(wrapper).also { param ->
                        runCatching {
                            afterHookCallback?.invoke(param)
                            if (afterHookCallback != null)
                                onHookLogMsg(msg = "After Hook Member [${member ?: "All of \"$allMethodsName\""}] done [$tag]")
                            HookParam.invoke()
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
                            (if (isReplaceHookMode) YukiHookBridge.Hooker.hookMethod(member, replaceMent)
                            else YukiHookBridge.Hooker.hookMethod(member, beforeAfterHook)).also {
                                when {
                                    it.first == null -> error("Hook Member [$member] failed")
                                    it.second -> onAlreadyHookedCallback?.invoke(it.first!!)
                                    else -> onHookedCallback?.invoke(it.first!!)
                                }
                            }
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
                    if (isNotIgnoredNoSuchMemberFailure) yLoggerE(
                        msg = "[$packageName] " + (if (isHookMemberSetup)
                            "Hooked Member with a finding error by $hookClass [$tag]"
                        else "Hooked Member cannot be non-null by $hookClass [$tag]"),
                        e = findingThrowable ?: it
                    )
                }
            else runCatching {
                when (hookMemberMode) {
                    HookMemberMode.HOOK_ALL_METHODS ->
                        (if (isReplaceHookMode) YukiHookBridge.Hooker.hookAllMethods(hookClass.instance, allMethodsName, replaceMent)
                        else YukiHookBridge.Hooker.hookAllMethods(hookClass.instance, allMethodsName, beforeAfterHook)).also {
                            when {
                                it.first.isEmpty() -> throw NoSuchMethodError("No Method name \"$allMethodsName\" matched")
                                it.second -> it.first.forEach { e -> onAlreadyHookedCallback?.invoke(e) }
                                else -> it.first.forEach { e -> onHookedCallback?.invoke(e) }
                            }
                        }
                    HookMemberMode.HOOK_ALL_CONSTRUCTORS ->
                        (if (isReplaceHookMode) YukiHookBridge.Hooker.hookAllConstructors(hookClass.instance, replaceMent)
                        else YukiHookBridge.Hooker.hookAllConstructors(hookClass.instance, beforeAfterHook)).also {
                            when {
                                it.first.isEmpty() -> throw NoSuchMethodError("No Constructor matched")
                                it.second -> it.first.forEach { e -> onAlreadyHookedCallback?.invoke(e) }
                                else -> it.first.forEach { e -> onHookedCallback?.invoke(e) }
                            }
                        }
                    else -> error("Hooked got a no error possible")
                }
            }.onFailure {
                val isMemberNotFound = it.message?.lowercase()?.contains(other = "nosuch") == true ||
                        it is NoSuchMethodError || it is NoSuchFieldError
                if (isMemberNotFound) onNoSuchMemberFailureCallback?.invoke(it)
                onAllFailureCallback?.invoke(it)
                if ((isNotIgnoredHookingFailure && isMemberNotFound.not()) || (isNotIgnoredNoSuchMemberFailure && isMemberNotFound))
                    yLoggerE(msg = "[$packageName] Hooked All Members with an error in $hookClass [$tag]", e = it)
            }
        }

        /**
         * Hook 过程中开启了 [YukiHookAPI.Configs.isDebug] 输出调试信息
         * @param msg 调试日志内容
         */
        private fun onHookLogMsg(msg: String) {
            if (YukiHookAPI.Configs.isDebug) yLoggerI(msg = "[$packageName] $msg")
        }

        /**
         * Hook 失败但未设置 [onAllFailureCallback] 将默认输出失败信息
         * @param throwable 异常信息
         */
        private fun onHookFailureMsg(throwable: Throwable) =
            yLoggerE(msg = "[$packageName] Try to hook ${hookClass.instance ?: hookClass.name}[$member] got an Exception [$tag]", e = throwable)

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

        override fun toString() = "[tag] $tag [priority] $priority [class] $hookClass [member] $member $allMethodsName [mode] $hookMemberMode"

        /**
         * 监听 Hook 结果实现类
         *
         * 可在这里处理失败事件监听
         */
        inner class Result internal constructor() {

            /**
             * 创建监听事件方法体
             * @param initiate 方法体
             * @return [Result] 可继续向下监听
             */
            inline fun result(initiate: Result.() -> Unit) = apply(initiate)

            /**
             * 添加执行 Hook 需要满足的条件
             *
             * 不满足条件将直接停止 Hook
             * @param condition 条件方法体
             * @return [Result] 可继续向下监听
             */
            inline fun by(condition: () -> Boolean): Result {
                isDisableMemberRunHook = (runCatching { condition() }.getOrNull() ?: false).not()
                if (isDisableMemberRunHook) ignoredAllFailure()
                return this
            }

            /**
             * 监听 [member] Hook 成功的回调方法
             *
             * 在首次 Hook 成功后回调
             *
             * 在重复 Hook 时会回调 [onAlreadyHooked]
             * @param result 回调被 Hook 的 [Member]
             * @return [Result] 可继续向下监听
             */
            fun onHooked(result: (Member) -> Unit): Result {
                onHookedCallback = result
                return this
            }

            /**
             * 监听 [member] 重复 Hook 的回调方法
             *
             * - ❗同一个 [hookClass] 中的同一个 [member] 不会被 API 重复 Hook - 若由于各种原因重复 Hook 会回调此方法
             * @param result 回调被重复 Hook 的 [Member]
             * @return [Result] 可继续向下监听
             */
            fun onAlreadyHooked(result: (Member) -> Unit): Result {
                onAlreadyHookedCallback = result
                return this
            }

            /**
             * 监听 [member] 不存在发生错误的回调方法
             * @param result 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result {
                onNoSuchMemberFailureCallback = result
                return this
            }

            /**
             * 忽略 [member] 不存在发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredNoSuchMemberFailure() = onNoSuchMemberFailure {}

            /**
             * 监听 Hook 进行过程中发生错误的回调方法
             * @param result 回调错误 - ([HookParam] 当前 Hook 实例,[Throwable] 异常)
             * @return [Result] 可继续向下监听
             */
            fun onConductFailure(result: (HookParam, Throwable) -> Unit): Result {
                onConductFailureCallback = result
                return this
            }

            /**
             * 忽略 Hook 进行过程中发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredConductFailure() = onConductFailure { _, _ -> }

            /**
             * 监听 Hook 开始时发生错误的回调方法
             * @param result 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onHookingFailure(result: (Throwable) -> Unit): Result {
                onHookingFailureCallback = result
                return this
            }

            /**
             * 忽略 Hook 开始时发生的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredHookingFailure() = onHookingFailure {}

            /**
             * 监听全部 Hook 过程发生错误的回调方法
             * @param result 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onAllFailure(result: (Throwable) -> Unit): Result {
                onAllFailureCallback = result
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
    inner class Result internal constructor() {

        /** Hook 开始时的监听事件回调 */
        internal var onPrepareHook: (() -> Unit)? = null

        /**
         * 创建监听事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 添加执行 Hook 需要满足的条件
         *
         * 不满足条件将直接停止 Hook
         * @param condition 条件方法体
         * @return [Result] 可继续向下监听
         */
        inline fun by(condition: () -> Boolean): Result {
            isDisableCreaterRunHook = (runCatching { condition() }.getOrNull() ?: false).not()
            return this
        }

        /**
         * 监听 [hookClass] 存在时准备开始 Hook 的操作
         * @param callback 准备开始 Hook 后回调
         * @return [Result] 可继续向下监听
         */
        fun onPrepareHook(callback: () -> Unit): Result {
            onPrepareHook = callback
            return this
        }

        /**
         * 监听 [hookClass] 找不到时发生错误的回调方法
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result {
            onHookClassNotFoundFailureCallback = result
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