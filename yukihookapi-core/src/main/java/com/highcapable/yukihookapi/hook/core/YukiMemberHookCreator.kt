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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress(
    "unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "UnusedReceiverParameter",
    "DeprecatedCallableAddReplaceWith", "PropertyName", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE"
)

package com.highcapable.yukihookapi.hook.core

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.core.annotation.LegacyHookApi
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberHook
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiMemberReplacement
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ConstructorConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.MethodConditions
import com.highcapable.yukihookapi.hook.factory.MembersType
import com.highcapable.yukihookapi.hook.factory.allConstructors
import com.highcapable.yukihookapi.hook.factory.allMethods
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.notExtends
import com.highcapable.yukihookapi.hook.factory.notImplements
import com.highcapable.yukihookapi.hook.factory.toJavaPrimitiveType
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.utils.factory.RandomSeed
import com.highcapable.yukihookapi.hook.utils.factory.await
import com.highcapable.yukihookapi.hook.utils.factory.conditions
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [YukiHookAPI] 的 [Member] 核心 Hook 实现类
 *
 * 核心 API 对接 [YukiHookHelper] 实现
 * @param packageParam 需要传入 [PackageParam] 实现方法调用
 * @param hookClass 要 Hook 的 [HookClass] 实例
 */
class YukiMemberHookCreator internal constructor(private val packageParam: PackageParam, private val hookClass: HookClass) {

    internal companion object {

        /**
         * 创建 [YukiMemberHookCreator.MemberHookCreator]
         * @param packageParam 需要传入 [PackageParam] 实现方法调用
         * @param members 要指定的 [Member] 数组
         * @param priority Hook 优先级
         * @param isLazyMode 是否为惰性模式
         * @return [YukiMemberHookCreator.MemberHookCreator]
         */
        internal fun createMemberHook(packageParam: PackageParam, members: List<Member>, priority: YukiHookPriority, isLazyMode: Boolean) =
            YukiMemberHookCreator(packageParam, HookClass.createPlaceholder())
                .createMemberHook(priority, if (isLazyMode) HookMode.LAZY_MEMBERS else HookMode.IMMEDIATE)
                .also { if (members.isNotEmpty()) it.members.apply { clear(); addAll(members) } }
    }

    /**
     * 默认 Hook 回调优先级
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 请现在迁移到 [YukiHookPriority]
     */
    @Deprecated(message = "请使用新方式来实现此功能")
    val PRIORITY_DEFAULT = 0x0

    /**
     * 延迟回调 Hook 方法结果
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 请现在迁移到 [YukiHookPriority]
     */
    @Deprecated(message = "请使用新方式来实现此功能")
    val PRIORITY_LOWEST = 0x1

    /**
     * 更快回调 Hook 方法结果
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 请现在迁移到 [YukiHookPriority]
     */
    @Deprecated(message = "请使用新方式来实现此功能")
    val PRIORITY_HIGHEST = 0x2

    /** [hookClass] 找不到时出现的错误回调 */
    private var onHookClassNotFoundFailureCallback: ((Throwable) -> Unit)? = null

    /** 当前 [YukiMemberHookCreator] 禁止执行 Hook 操作的条件数组 */
    private val disableCreatorRunHookReasons = mutableSetOf<Boolean>()

    /** 是否对当前 [YukiMemberHookCreator] 禁止执行 Hook 操作 */
    private var isDisableCreatorRunHook = false

    /** 设置要 Hook 的 [Method]、[Constructor] */
    private var preHookMembers = mutableMapOf<String, MemberHookCreator.LegacyCreator>()

    /**
     * 更新当前 [YukiMemberHookCreator] 禁止执行 Hook 操作的条件
     * @param reason 当前条件
     */
    private fun updateDisableCreatorRunHookReasons(reason: Boolean) {
        disableCreatorRunHookReasons.add(reason)
        conditions {
            disableCreatorRunHookReasons.forEach { and(it) }
        }.finally { isDisableCreatorRunHook = true }.without { isDisableCreatorRunHook = false }
    }

    /**
     * 当前是否为不需要 Hook 的调用域
     *
     * 过滤 [HookEntryType.ZYGOTE] and [HookEntryType.PACKAGE]
     * @return [Boolean]
     */
    private val isHooklessScope get() = packageParam.wrapper?.type == HookEntryType.RESOURCES

    /**
     * 得到当前被 Hook 的 [Class]
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 不再推荐使用
     * @return [Class]
     */
    @Deprecated(message = "不再推荐使用")
    val instanceClass: Class<*> get() = hookClass.instance ?: error("This function \"instanceClass\" was deprecated")

    /**
     * 注入要 Hook 的 [Method]、[Constructor]
     * @param priority Hook 优先级 - 默认为 [YukiHookPriority.DEFAULT]
     * @param initiate 方法体
     * @return [MemberHookCreator.Result]
     */
    @LegacyHookApi
    inline fun injectMember(priority: YukiHookPriority = YukiHookPriority.DEFAULT, initiate: MemberHookCreator.LegacyCreator.() -> Unit) =
        createMemberHook(priority, HookMode.LAZY_CLASSES).createLegacy().apply(initiate).apply { preHookMembers[toString()] = this }.build()

    /**
     * 注入要 Hook 的 [Method]、[Constructor]
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 请现在迁移到另一个 [injectMember]
     * @return [MemberHookCreator.Result]
     */
    @Suppress("DEPRECATION")
    @LegacyHookApi
    @Deprecated(message = "请使用新方式来实现 Hook 功能", ReplaceWith("injectMember(initiate = initiate)"))
    inline fun injectMember(priority: Int = PRIORITY_DEFAULT, tag: String = "Default", initiate: MemberHookCreator.LegacyCreator.() -> Unit) =
        injectMember(initiate = initiate)

    /**
     * 允许 Hook 过程中的所有危险行为
     *
     * - 此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - 此功能已被弃用
     */
    @Deprecated(message = "此功能已被弃用")
    fun useDangerousOperation(option: String) {
    }

    /**
     * Hook 执行入口
     * @return [Result]
     */
    @LegacyHookApi
    internal fun hook() = when {
        HookApiCategoryHelper.hasAvailableHookApi.not() || isHooklessScope && HookParam.isCallbackCalled.not() -> Result()
        preHookMembers.isEmpty() -> Result().also {
            if (hookClass.isPlaceholder) YLog.innerW("Hook Members is empty, hook aborted")
            else YLog.innerW("Hook Members is empty in [${hookClass.name}], hook aborted")
        }
        else -> Result().await {
            when {
                isDisableCreatorRunHook.not() && (hookClass.instance != null || hookClass.isPlaceholder) ->
                    runCatching {
                        it.onPrepareHook?.invoke()
                        preHookMembers.forEach { (_, m) -> m.hook() }
                    }.onFailure {
                        if (onHookClassNotFoundFailureCallback == null)
                            YLog.innerE("Hook initialization failed because got an exception", e = it)
                        else onHookClassNotFoundFailureCallback?.invoke(it)
                    }
                isDisableCreatorRunHook.not() && hookClass.instance == null ->
                    if (onHookClassNotFoundFailureCallback == null)
                        YLog.innerE("HookClass [${hookClass.name}] not found", e = hookClass.throwable)
                    else onHookClassNotFoundFailureCallback?.invoke(hookClass.throwable ?: Throwable("[${hookClass.name}] not found"))
            }
        }
    }

    /**
     * 创建 [MemberHookCreator]
     * @param priority Hook 优先级
     * @param hookMode Hook 模式
     * @return [MemberHookCreator]
     */
    private fun createMemberHook(priority: YukiHookPriority, hookMode: HookMode) = MemberHookCreator(priority, hookMode)

    /**
     * Hook 核心功能实现类
     *
     * 查找和处理需要 Hook 的 [Method]、[Constructor]
     * @param priority Hook 优先级
     * @param hookMode Hook 模式
     */
    inner class MemberHookCreator internal constructor(private val priority: YukiHookPriority, private val hookMode: HookMode) {

        /** Hook 结果实例 */
        private var result: Result? = null

        /** 是否已经执行 Hook */
        private var isHooked = false

        /** [before] 回调方法体 ID */
        private val beforeHookId = RandomSeed.createString()

        /** [after] 回调方法体 ID */
        private val afterHookId = RandomSeed.createString()

        /** [replaceAny]、[replaceUnit] 回调方法体 ID */
        private val replaceHookId = RandomSeed.createString()

        /** [before] 回调 */
        private var beforeHookCallback: (HookParam.() -> Unit)? = null

        /** [after] 回调 */
        private var afterHookCallback: (HookParam.() -> Unit)? = null

        /** [replaceAny]、[replaceUnit] 回调 */
        private var replaceHookCallback: (HookParam.() -> Any?)? = null

        /** Hook 成功时回调 */
        private var onHookedCallback: ((Member) -> Unit)? = null

        /** 重复 Hook 时回调 */
        private var onAlreadyHookedCallback: ((Member) -> Unit)? = null

        /** 找不到 [members] 出现错误回调 */
        private var onNoSuchMemberFailureCallback: ((Throwable) -> Unit)? = null

        /** Hook 过程中出现错误回调 */
        private var onConductFailureCallback: ((HookParam, Throwable) -> Unit)? = null

        /** Hook 开始时出现错误回调 */
        private var onHookingFailureCallback: ((Throwable) -> Unit)? = null

        /** 全部错误回调 */
        private var onAllFailureCallback: ((Throwable) -> Unit)? = null

        /** 发生异常时是否将异常抛出给当前 Hook APP */
        private var isOnFailureThrowToApp = false

        /** 是否为替换 Hook 模式 */
        private var isReplaceHookMode = false

        /** 是否对当前 [MemberHookCreator] 禁止执行 Hook 操作 */
        private var isDisableMemberRunHook = false

        /** 查找过程中发生的异常 */
        private var findingThrowable: Throwable? = null

        /** 标识是否已经设置了要 Hook 的 [members] */
        private var isHookMemberSetup = false

        /** 当前被 Hook 的 [Method]、[Constructor] 实例数组 */
        private val hookedMembers = mutableSetOf<YukiMemberHook.HookedMember>()

        /** 当前需要 Hook 的 [Method]、[Constructor] */
        internal val members = mutableSetOf<Member>()

        /**
         * 在 [Member] 执行完成前 Hook
         *
         * - 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
         * @param initiate [HookParam] 方法体
         * @return [HookCallback]
         */
        fun before(initiate: HookParam.() -> Unit): HookCallback {
            isReplaceHookMode = false
            beforeHookCallback = initiate
            immediateHook()
            return HookCallback()
        }

        /**
         * 在 [Member] 执行完成后 Hook
         *
         * - 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
         * @param initiate [HookParam] 方法体
         * @return [HookCallback]
         */
        fun after(initiate: HookParam.() -> Unit): HookCallback {
            isReplaceHookMode = false
            afterHookCallback = initiate
            immediateHook()
            return HookCallback()
        }

        /**
         * 拦截并替换此 [Member] 内容 - 给出返回值
         *
         * - 不可与 [before]、[after] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceAny(initiate: HookParam.() -> Any?) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
            immediateHook()
        }

        /**
         * 拦截并替换此 [Member] 内容 - 没有返回值 ([Unit])
         *
         * - 不可与 [before]、[after] 同时使用
         * @param initiate [HookParam] 方法体
         */
        fun replaceUnit(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
            immediateHook()
        }

        /**
         * 拦截并替换 [Member] 返回值
         *
         * - 不可与 [before]、[after] 同时使用
         * @param any 要替换为的返回值对象
         */
        fun replaceTo(any: Any?) {
            isReplaceHookMode = true
            replaceHookCallback = { any }
            immediateHook()
        }

        /**
         * 拦截并替换 [Member] 返回值为 true
         *
         * - 确保替换 [Member] 的返回对象为 [Boolean]
         *
         * - 不可与 [before]、[after] 同时使用
         */
        fun replaceToTrue() {
            isReplaceHookMode = true
            replaceHookCallback = { true }
            immediateHook()
        }

        /**
         * 拦截并替换 [Member] 返回值为 false
         *
         * - 确保替换 [Member] 的返回对象为 [Boolean]
         *
         * - 不可与 [before]、[after] 同时使用
         */
        fun replaceToFalse() {
            isReplaceHookMode = true
            replaceHookCallback = { false }
            immediateHook()
        }

        /**
         * 拦截此 [Member]
         *
         * - 这将会禁止此 [Member] 执行并返回 null
         *
         * - 注意：例如 [Int]、[Long]、[Boolean] 常量返回值的 [Member] 一旦被设置为 null 可能会造成 Hook APP 抛出异常
         *
         * - 不可与 [before]、[after] 同时使用
         */
        fun intercept() {
            isReplaceHookMode = true
            replaceHookCallback = { null }
            immediateHook()
        }

        /**
         * 移除当前注入的 Hook [Method]、[Constructor] (解除 Hook)
         *
         * - 你只能在 Hook 回调方法中使用此功能
         * @param result 回调是否成功
         */
        fun removeSelf(result: (Boolean) -> Unit = {}) = this.result?.remove(result) ?: result(false)

        /**
         * Hook 创建入口
         * @return [Result]
         */
        internal fun build() = Result().apply {
            result = this
            immediateHook(isLazyMode = true)
        }

        /**
         * 调用即时 Hook
         * @param isLazyMode 是否为惰性模式 - 默认否
         */
        private fun immediateHook(isLazyMode: Boolean = false) {
            if (isLazyMode && hookMode == HookMode.LAZY_MEMBERS || hookMode == HookMode.IMMEDIATE) hook()
        }

        /** Hook 执行入口 */
        internal fun hook() {
            if (HookApiCategoryHelper.hasAvailableHookApi.not() || isHooklessScope || isHooked || isDisableMemberRunHook) return
            isHooked = true
            if (hookClass.instance == null && hookClass.isPlaceholder.not()) {
                (hookClass.throwable ?: Throwable("HookClass [${hookClass.name}] not found")).also {
                    onHookingFailureCallback?.invoke(it)
                    onAllFailureCallback?.invoke(it)
                    if (isNotIgnoredHookingFailure) hookErrorMsg(it)
                }
                return
            }
            members.takeIf { it.isNotEmpty() }?.forEach { member ->
                runCatching {
                    member.hook().also {
                        when {
                            it.hookedMember?.member == null -> error("Hook Member [$member] failed")
                            it.isAlreadyHooked -> onAlreadyHookedCallback?.invoke(it.hookedMember.member!!)
                                ?: YLog.innerW("Already Hooked Member [$member], this will be ignored")
                            else -> {
                                hookedMembers.add(it.hookedMember)
                                onHookedCallback?.invoke(it.hookedMember.member!!)
                            }
                        }
                    }
                }.onFailure {
                    onHookingFailureCallback?.invoke(it)
                    onAllFailureCallback?.invoke(it)
                    if (isNotIgnoredHookingFailure) hookErrorMsg(it, member)
                }
            } ?: Throwable("Finding Error isSetUpMember [$isHookMemberSetup]").also {
                onNoSuchMemberFailureCallback?.invoke(it)
                onHookingFailureCallback?.invoke(it)
                onAllFailureCallback?.invoke(it)
                /** 如果不是使用 [injectMember] 创建的实例将不发出任何警告 */
                if (hookMode != HookMode.LAZY_CLASSES) return
                if (isNotIgnoredNoSuchMemberFailure) YLog.innerE(
                    msg = when {
                        hookClass.isPlaceholder ->
                            if (isHookMemberSetup)
                                "Hooked Member with a finding error"
                            else "Hooked Member cannot be null"
                        else ->
                            if (isHookMemberSetup)
                                "Hooked Member with a finding error by $hookClass"
                            else "Hooked Member cannot be null by $hookClass]"
                    }, e = findingThrowable ?: it
                )
            }
        }

        /**
         * Hook [Method]、[Constructor]
         * @return [YukiHookResult]
         */
        private fun Member.hook(): YukiHookResult {
            /** 定义替换 Hook 回调方法体 */
            val replaceMent = object : YukiMemberReplacement(priority) {
                override fun replaceHookedMember(param: Param) =
                    HookParam.create(this@YukiMemberHookCreator, replaceHookId, param).let { assign ->
                        runCatching {
                            replaceHookCallback?.invoke(assign).also {
                                checkingReturnType((param.member as? Method?)?.returnType, it?.javaClass)
                                if (replaceHookCallback != null) YLog.innerD("Replace Hook Member [${this@hook}] done")
                                HookParam.invoke()
                            }
                        }.getOrElse {
                            onConductFailureCallback?.invoke(assign, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) hookErrorMsg(it, member = this@hook)
                            /** 若发生异常则会自动调用未经 Hook 的原始 [Member] 保证 Hook APP 正常运行 */
                            assign.callOriginal()
                        }
                    }
            }

            /** 定义前后 Hook 回调方法体 */
            val beforeAfterHook = object : YukiMemberHook(priority) {
                override fun beforeHookedMember(param: Param) {
                    HookParam.create(this@YukiMemberHookCreator, beforeHookId, param).also { assign ->
                        runCatching {
                            beforeHookCallback?.invoke(assign)
                            checkingReturnType((param.member as? Method?)?.returnType, param.result?.javaClass)
                            if (beforeHookCallback != null) YLog.innerD("Before Hook Member [${this@hook}] done")
                            HookParam.invoke()
                        }.onFailure {
                            onConductFailureCallback?.invoke(assign, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) hookErrorMsg(it, member = this@hook)
                            if (isOnFailureThrowToApp) param.throwable = it
                        }
                    }
                }

                override fun afterHookedMember(param: Param) {
                    HookParam.create(this@YukiMemberHookCreator, afterHookId, param).also { assign ->
                        runCatching {
                            afterHookCallback?.invoke(assign)
                            if (afterHookCallback != null) YLog.innerD("After Hook Member [${this@hook}] done")
                            HookParam.invoke()
                        }.onFailure {
                            onConductFailureCallback?.invoke(assign, it)
                            onAllFailureCallback?.invoke(it)
                            if (onConductFailureCallback == null && onAllFailureCallback == null) hookErrorMsg(it, member = this@hook)
                            if (isOnFailureThrowToApp) param.throwable = it
                        }
                    }
                }
            }
            return YukiHookHelper.hookMember(member = this, if (isReplaceHookMode) replaceMent else beforeAfterHook)
        }

        /**
         * 检查被 Hook [Member] 的返回值
         * @param origin 原始返回值
         * @param target 目标返回值
         * @throws IllegalStateException 如果返回值不正确
         */
        private fun checkingReturnType(origin: Class<*>?, target: Class<*>?) {
            if (origin == null || target == null) return
            origin.toJavaPrimitiveType().also { o ->
                target.toJavaPrimitiveType().also { t ->
                    if (o notExtends t && t notExtends o && o notImplements t && t notImplements o)
                        error("Hooked method return type match failed, required [$origin] but got [$target]")
                }
            }
        }

        /**
         * Hook 失败但未设置 [onAllFailureCallback] 将默认输出失败信息
         * @param e 异常堆栈
         * @param member 异常 [Member] - 可空
         */
        private fun hookErrorMsg(e: Throwable, member: Member? = null) =
            if (hookClass.isPlaceholder)
                YLog.innerE("Try to hook ${member?.let { "[$it]" } ?: "nothing"} got an exception", e)
            else YLog.innerE("Try to hook [${hookClass.instance ?: hookClass.name}]${member?.let { "[$it]" } ?: ""} got an exception", e)

        /**
         * 判断是否没有设置 Hook 过程中的任何异常拦截
         * @return [Boolean] 没有设置任何异常拦截
         */
        private val isNotIgnoredHookingFailure get() = onHookingFailureCallback == null && onAllFailureCallback == null

        /**
         * 判断是否没有设置 Hook 过程中 [members] 找不到的任何异常拦截
         * @return [Boolean] 没有设置任何异常拦截
         */
        internal val isNotIgnoredNoSuchMemberFailure get() = onNoSuchMemberFailureCallback == null && isNotIgnoredHookingFailure

        override fun toString() =
            if (hookClass.isPlaceholder) "[priority] $priority [members] $members"
            else "[priority] $priority [class] $hookClass [members] $members"

        /**
         * 创建 [LegacyCreator]
         * @return [LegacyCreator]
         */
        internal fun createLegacy() = LegacyCreator()

        /**
         * 使用 [injectMember] 创建的 Hook 核心功能实现类 (旧版本)
         */
        inner class LegacyCreator internal constructor() {

            /**
             * 手动指定要 Hook 的 [Method]、[Constructor]
             *
             * 你可以调用 [instanceClass] 来手动查找要 Hook 的 [Method]、[Constructor]
             *
             * - 不建议使用此方法设置目标需要 Hook 的 [Member] 对象 - 你可以使用 [method] or [constructor] 方法
             *
             * - 在同一个 [injectMember] 中你只能使用一次 [members]、[allMembers]、[method]、[constructor] 方法 - 否则结果会被替换
             * @param member 要指定的 [Member] or [Member] 数组
             * @throws IllegalStateException 如果 [member] 参数为空
             */
            fun members(vararg member: Member?) {
                if (member.isEmpty()) error("Custom Hooking Members is empty")
                members.clear()
                member.forEach { it?.also { members.add(it) } }
            }

            /**
             * 查找并 Hook [hookClass] 中指定 [name] 的全部 [Method]
             *
             * - 此方法已弃用 - 在之后的版本中将直接被删除
             *
             * - 请现在迁移到 [MethodFinder] or [allMembers]
             * @param name 方法名称
             */
            @Deprecated(message = "请使用新方式来实现 Hook 所有方法", ReplaceWith("method { this.name = name }.all()"))
            fun allMethods(name: String) = method { this.name = name }.all()

            /**
             * 查找并 Hook [hookClass] 中的全部 [Constructor]
             *
             * - 此方法已弃用 - 在之后的版本中将直接被删除
             *
             * - 请现在迁移到 [ConstructorFinder] or [allMembers]
             */
            @Deprecated(
                message = "请使用新方式来实现 Hook 所有构造方法",
                ReplaceWith("allMembers(MembersType.CONSTRUCTOR)", "com.highcapable.yukihookapi.hook.factory.MembersType")
            )
            fun allConstructors() = allMembers(MembersType.CONSTRUCTOR)

            /**
             * 查找并 Hook [hookClass] 中的全部 [Method]、[Constructor]
             *
             * - 在同一个 [injectMember] 中你只能使用一次 [members]、[allMembers]、[method]、[constructor] 方法 - 否则结果会被替换
             *
             * - 警告：无法准确处理每个 [Member] 的返回值和 param - 建议使用 [method] or [constructor] 对每个 [Member] 单独 Hook
             *
             * - 如果 [hookClass] 中没有 [Member] 可能会发生错误
             * @param type 过滤 [Member] 类型 - 默认为 [MembersType.ALL]
             */
            fun allMembers(type: MembersType = MembersType.ALL) {
                members.clear()
                if (type == MembersType.ALL || type == MembersType.CONSTRUCTOR)
                    hookClass.instance?.allConstructors { _, constructor -> members.add(constructor) }
                if (type == MembersType.ALL || type == MembersType.METHOD)
                    hookClass.instance?.allMethods { _, method -> members.add(method) }
                isHookMemberSetup = true
            }

            /**
             * 查找 [hookClass] 需要 Hook 的 [Method]
             *
             * - 在同一个 [injectMember] 中你只能使用一次 [members]、[allMembers]、[method]、[constructor] 方法 - 否则结果会被替换
             * @param initiate 方法体
             * @return [MethodFinder.Process]
             */
            inline fun method(initiate: MethodConditions) = runCatching {
                isHookMemberSetup = true
                MethodFinder.fromHooker(hookInstance = this@MemberHookCreator, hookClass.instance).apply(initiate).process()
            }.getOrElse {
                findingThrowable = it
                MethodFinder.fromHooker(hookInstance = this@MemberHookCreator).denied(it)
            }

            /**
             * 查找 [hookClass] 需要 Hook 的 [Constructor]
             *
             * - 在同一个 [injectMember] 中你只能使用一次 [members]、[allMembers]、[method]、[constructor] 方法 - 否则结果会被替换
             * @param initiate 方法体
             * @return [ConstructorFinder.Process]
             */
            inline fun constructor(initiate: ConstructorConditions = { emptyParam() }) = runCatching {
                isHookMemberSetup = true
                ConstructorFinder.fromHooker(hookInstance = this@MemberHookCreator, hookClass.instance).apply(initiate).process()
            }.getOrElse {
                findingThrowable = it
                ConstructorFinder.fromHooker(hookInstance = this@MemberHookCreator).denied(it)
            }

            /**
             * 注入要 Hook 的 [Method]、[Constructor] (嵌套 Hook)
             *
             * - 此方法已弃用 - 在之后的版本中将直接被删除
             *
             * - 嵌套 Hook 功能已弃用
             */
            @Suppress("DEPRECATION")
            @LegacyHookApi
            @Deprecated(message = "嵌套 Hook 功能已弃用")
            inline fun HookParam.injectMember(
                priority: Int = PRIORITY_DEFAULT,
                tag: String = "InnerDefault",
                initiate: MemberHookCreator.() -> Unit
            ) = Unit

            /**
             * 在 [Member] 执行完成前 Hook
             *
             * - 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
             * @param initiate [HookParam] 方法体
             * @return [HookCallback]
             */
            fun beforeHook(initiate: HookParam.() -> Unit) = before(initiate)

            /**
             * 在 [Member] 执行完成后 Hook
             *
             * - 不可与 [replaceAny]、[replaceUnit]、[replaceTo] 同时使用
             * @param initiate [HookParam] 方法体
             * @return [HookCallback]
             */
            fun afterHook(initiate: HookParam.() -> Unit) = after(initiate)

            /**
             * 拦截并替换此 [Member] 内容 - 给出返回值
             *
             * - 不可与 [before]、[after] 同时使用
             * @param initiate [HookParam] 方法体
             */
            fun replaceAny(initiate: HookParam.() -> Any?) = this@MemberHookCreator.replaceAny(initiate)

            /**
             * 拦截并替换此 [Member] 内容 - 没有返回值 ([Unit])
             *
             * - 不可与 [before]、[after] 同时使用
             * @param initiate [HookParam] 方法体
             */
            fun replaceUnit(initiate: HookParam.() -> Unit) = this@MemberHookCreator.replaceUnit(initiate)

            /**
             * 拦截并替换 [Member] 返回值
             *
             * - 不可与 [before]、[after] 同时使用
             * @param any 要替换为的返回值对象
             */
            fun replaceTo(any: Any?) = this@MemberHookCreator.replaceTo(any)

            /**
             * 拦截并替换 [Member] 返回值为 true
             *
             * - 确保替换 [Member] 的返回对象为 [Boolean]
             *
             * - 不可与 [before]、[after] 同时使用
             */
            fun replaceToTrue() = this@MemberHookCreator.replaceToTrue()

            /**
             * 拦截并替换 [Member] 返回值为 false
             *
             * - 确保替换 [Member] 的返回对象为 [Boolean]
             *
             * - 不可与 [before]、[after] 同时使用
             */
            fun replaceToFalse() = this@MemberHookCreator.replaceToFalse()

            /**
             * 拦截此 [Member]
             *
             * - 这将会禁止此 [Member] 执行并返回 null
             *
             * - 注意：例如 [Int]、[Long]、[Boolean] 常量返回值的 [Member] 一旦被设置为 null 可能会造成 Hook APP 抛出异常
             *
             * - 不可与 [before]、[after] 同时使用
             */
            fun intercept() = this@MemberHookCreator.intercept()

            /**
             * 移除当前注入的 Hook [Method]、[Constructor] (解除 Hook)
             *
             * - 你只能在 Hook 回调方法中使用此功能
             * @param result 回调是否成功
             */
            fun removeSelf(result: (Boolean) -> Unit = {}) = this@MemberHookCreator.removeSelf(result)

            /**
             * Hook 创建入口
             * @return [Result]
             */
            internal fun build() = this@MemberHookCreator.build()

            /** Hook 执行入口 */
            internal fun hook() = this@MemberHookCreator.hook()

            override fun toString() = "LegacyCreator by ${this@MemberHookCreator}"
        }

        /**
         * Hook 方法体回调实现类
         */
        inner class HookCallback internal constructor() {

            /** 当回调方法体内发生异常时将异常抛出给当前 Hook APP */
            fun onFailureThrowToApp() {
                isOnFailureThrowToApp = true
            }
        }

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
             * 监听 [members] Hook 成功的回调方法
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
             * 监听 [members] 重复 Hook 的回调方法
             *
             * - 同一个 [hookClass] 中的同一个 [members] 不会被 API 重复 Hook - 若由于各种原因重复 Hook 会回调此方法
             * @param result 回调被重复 Hook 的 [Member]
             * @return [Result] 可继续向下监听
             */
            fun onAlreadyHooked(result: (Member) -> Unit): Result {
                onAlreadyHookedCallback = result
                return this
            }

            /**
             * 监听 [members] 不存在发生错误的回调方法
             * @param result 回调错误
             * @return [Result] 可继续向下监听
             */
            @LegacyHookApi
            fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result {
                onNoSuchMemberFailureCallback = result
                return this
            }

            /**
             * 忽略 [members] 不存在发生的错误
             * @return [Result] 可继续向下监听
             */
            @LegacyHookApi
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

            /**
             * 移除当前注入的 Hook [Method]、[Constructor] (解除 Hook)
             *
             * - 你只能在 Hook 成功后才能解除 Hook - 可监听 [onHooked] 事件
             * @param result 回调是否成功
             */
            fun remove(result: (Boolean) -> Unit = {}) {
                hookedMembers.takeIf { it.isNotEmpty() }?.apply {
                    forEach {
                        it.remove()
                        YLog.innerD("Remove Hooked Member [${it.member}] done")
                    }
                    runCatching { preHookMembers.remove(this@MemberHookCreator.toString()) }
                    clear()
                    result(true)
                } ?: result(false)
            }
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
        @LegacyHookApi
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 添加执行 Hook 需要满足的条件
         *
         * 不满足条件将直接停止 Hook
         * @param condition 条件方法体
         * @return [Result] 可继续向下监听
         */
        @LegacyHookApi
        inline fun by(condition: () -> Boolean): Result {
            updateDisableCreatorRunHookReasons((runCatching { condition() }.getOrNull() ?: false).not())
            return this
        }

        /**
         * 监听 [hookClass] 存在时准备开始 Hook 的操作
         * @param callback 准备开始 Hook 后回调
         * @return [Result] 可继续向下监听
         */
        @LegacyHookApi
        fun onPrepareHook(callback: () -> Unit): Result {
            onPrepareHook = callback
            return this
        }

        /**
         * 监听 [hookClass] 找不到时发生错误的回调方法
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        @LegacyHookApi
        fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result {
            onHookClassNotFoundFailureCallback = result
            return this
        }

        /**
         * 忽略 [hookClass] 找不到时出现的错误
         * @return [Result] 可继续向下监听
         */
        @LegacyHookApi
        fun ignoredHookClassNotFoundFailure(): Result {
            by { hookClass.instance != null }
            return this
        }
    }

    /**
     * Hook 模式类型定义类
     */
    internal enum class HookMode {
        /** 惰性模式 [Class] */
        LAZY_CLASSES,

        /** 惰性模式 [Member] */
        LAZY_MEMBERS,

        /** 即时模式 */
        IMMEDIATE
    }
}