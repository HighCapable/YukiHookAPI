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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress(
    "unused", "UNUSED_PARAMETER", "MemberVisibilityCanBePrivate", "UnusedReceiverParameter",
    "DeprecatedCallableAddReplaceWith", "PropertyName", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION"
)

package com.highcapable.yukihookapi.hook.core

import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.isNotSubclassOf
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
 * Core [Member] Hook implementation for [YukiHookAPI].
 *
 * The core API is implemented through [YukiHookHelper].
 * @param packageParam the [PackageParam] used to invoke methods.
 * @param hookClass the [HookClass] instance to hook.
 */
@OptIn(LegacyHookApi::class)
class YukiMemberHookCreator internal constructor(private val packageParam: PackageParam, private val hookClass: HookClass) {

    internal companion object {

        /**
         * Creates [YukiMemberHookCreator.MemberHookCreator].
         * @param packageParam the [PackageParam] used to invoke methods.
         * @param members the [Member] list to specify.
         * @param priority the Hook priority.
         * @param isLazyMode whether lazy mode is enabled.
         * @return [YukiMemberHookCreator.MemberHookCreator]
         */
        internal fun createMemberHook(packageParam: PackageParam, members: List<Member>, priority: YukiHookPriority, isLazyMode: Boolean) =
            YukiMemberHookCreator(packageParam, HookClass.createPlaceholder())
                .createMemberHook(priority, if (isLazyMode) HookMode.LAZY_MEMBERS else HookMode.IMMEDIATE)
                .also { if (members.isNotEmpty()) it.members.apply { clear(); addAll(members) } }
    }

    /**
     * Default Hook callback priority.
     *
     * - This property is deprecated. It will be removed in a future version.
     *
     * - Migrate to [YukiHookPriority] now.
     */
    @Deprecated(message = "Use the new approach to implement this feature")
    val PRIORITY_DEFAULT = 0x0

    /**
     * Delayed Hook callback priority.
     *
     * - This property is deprecated. It will be removed in a future version.
     *
     * - Migrate to [YukiHookPriority] now.
     */
    @Deprecated(message = "Use the new approach to implement this feature")
    val PRIORITY_LOWEST = 0x1

    /**
     * Faster Hook callback priority.
     *
     * - This property is deprecated. It will be removed in a future version.
     *
     * - Migrate to [YukiHookPriority] now.
     */
    @Deprecated(message = "Use the new approach to implement this feature")
    val PRIORITY_HIGHEST = 0x2

    /** Error callback invoked when [hookClass] cannot be found. */
    private var onHookClassNotFoundFailureCallback: ((Throwable) -> Unit)? = null

    /** Conditions that prevent the current [YukiMemberHookCreator] from performing Hook operations. */
    private val disableCreatorRunHookReasons = mutableSetOf<Boolean>()

    /** Whether the current [YukiMemberHookCreator] is prevented from performing Hook operations. */
    private var isDisableCreatorRunHook = false

    /** Preconfigured [Method] and [Constructor] instances to hook. */
    private var preHookMembers = mutableMapOf<String, MemberHookCreator.LegacyCreator>()

    /**
     * Updates the conditions that prevent the current [YukiMemberHookCreator] from performing Hook operations.
     * @param reason the current condition.
     */
    private fun updateDisableCreatorRunHookReasons(reason: Boolean) {
        disableCreatorRunHookReasons.add(reason)
        conditions {
            disableCreatorRunHookReasons.forEach { and(it) }
        }.finally { isDisableCreatorRunHook = true }.without { isDisableCreatorRunHook = false }
    }

    /**
     * Whether the current call scope does not require Hooking.
     *
     * Filters [HookEntryType.ZYGOTE] and [HookEntryType.PACKAGE].
     * @return [Boolean]
     */
    private val isHooklessScope get() = packageParam.wrapper?.type == HookEntryType.RESOURCES

    /**
     * Gets the currently hooked [Class].
     *
     * - This property is deprecated. It will be removed in a future version.
     *
     * - Its use is no longer recommended.
     * @return [Class]
     */
    @Deprecated(message = "Its use is no longer recommended")
    val instanceClass: Class<*> get() = hookClass.instance ?: error("This function \"instanceClass\" was deprecated")

    /**
     * Injects the [Method] and [Constructor] instances to hook.
     * @param priority the Hook priority. The default is [YukiHookPriority.DEFAULT].
     * @param initiate the function body.
     * @return [MemberHookCreator.Result]
     */
    @LegacyHookApi
    inline fun injectMember(priority: YukiHookPriority = YukiHookPriority.DEFAULT, initiate: MemberHookCreator.LegacyCreator.() -> Unit) =
        createMemberHook(priority, HookMode.LAZY_CLASSES).createLegacy().apply(initiate).apply { preHookMembers[toString()] = this }.build()

    /**
     * Injects the [Method] and [Constructor] instances to hook.
     *
     * - This function is deprecated. It will be removed in a future version.
     *
     * - Migrate to the other [injectMember] now.
     * @return [MemberHookCreator.Result]
     */
    @Suppress("DEPRECATION")
    @LegacyHookApi
    @Deprecated(message = "Use the new approach to implement the Hook feature", ReplaceWith("injectMember(initiate = initiate)"))
    inline fun injectMember(priority: Int = PRIORITY_DEFAULT, tag: String = "Default", initiate: MemberHookCreator.LegacyCreator.() -> Unit) =
        injectMember(initiate = initiate)

    /**
     * Allows all dangerous operations during Hooking.
     *
     * - This function is deprecated. It will be removed in a future version.
     *
     * - This feature is deprecated.
     */
    @Deprecated(message = "This feature is deprecated")
    fun useDangerousOperation(option: String) {
    }

    /**
     * Hook execution entry point.
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
     * Creates [MemberHookCreator].
     * @param priority the Hook priority.
     * @param hookMode the Hook mode.
     * @return [MemberHookCreator]
     */
    private fun createMemberHook(priority: YukiHookPriority, hookMode: HookMode) = MemberHookCreator(priority, hookMode)

    /**
     * Core Hook feature implementation.
     *
     * Finds and processes the [Method] and [Constructor] instances to hook.
     * @param priority the Hook priority.
     * @param hookMode the Hook mode.
     */
    inner class MemberHookCreator internal constructor(private val priority: YukiHookPriority, private val hookMode: HookMode) {

        /** Hook result instance. */
        private var result: Result? = null

        /** Whether Hooking has already been performed. */
        private var isHooked = false

        /** [before] callback body ID. */
        private val beforeHookId = RandomSeed.createString()

        /** [after] callback body ID. */
        private val afterHookId = RandomSeed.createString()

        /** [replaceAny] and [replaceUnit] callback body ID. */
        private val replaceHookId = RandomSeed.createString()

        /** [before] callback. */
        private var beforeHookCallback: (HookParam.() -> Unit)? = null

        /** [after] callback. */
        private var afterHookCallback: (HookParam.() -> Unit)? = null

        /** [replaceAny] and [replaceUnit] callback. */
        private var replaceHookCallback: (HookParam.() -> Any?)? = null

        /** Callback invoked when Hooking succeeds. */
        private var onHookedCallback: ((Member) -> Unit)? = null

        /** Error callback invoked when [members] cannot be found. */
        private var onNoSuchMemberFailureCallback: ((Throwable) -> Unit)? = null

        /** Error callback invoked during Hooking. */
        private var onConductFailureCallback: ((HookParam, Throwable) -> Unit)? = null

        /** Error callback invoked when Hooking starts. */
        private var onHookingFailureCallback: ((Throwable) -> Unit)? = null

        /** Callback for all errors. */
        private var onAllFailureCallback: ((Throwable) -> Unit)? = null

        /** Whether to throw an exception to the current Hook APP when one occurs. */
        private var isOnFailureThrowToApp = false

        /** Whether replacement Hook mode is enabled. */
        private var isReplaceHookMode = false

        /** Whether the current [MemberHookCreator] is prevented from performing Hook operations. */
        private var isDisableMemberRunHook = false

        /** Exception that occurred during lookup. */
        private var findingThrowable: Throwable? = null

        /** Whether the [members] to hook have been configured. */
        private var isHookMemberSetup = false

        /** Currently hooked [Method] and [Constructor] instances. */
        private val hookedMembers = mutableSetOf<YukiMemberHook.HookedMember>()

        /** Current [Method] and [Constructor] instances to hook. */
        internal val members = mutableSetOf<Member>()

        /**
         * Hooks before [Member] execution completes.
         *
         * - Cannot be used together with [replaceAny], [replaceUnit], or [replaceTo].
         * @param initiate the [HookParam] function body.
         * @return [HookCallback]
         */
        fun before(initiate: HookParam.() -> Unit): HookCallback {
            isReplaceHookMode = false
            beforeHookCallback = initiate
            immediateHook()
            return HookCallback()
        }

        /**
         * Hooks after [Member] execution completes.
         *
         * - Cannot be used together with [replaceAny], [replaceUnit], or [replaceTo].
         * @param initiate the [HookParam] function body.
         * @return [HookCallback]
         */
        fun after(initiate: HookParam.() -> Unit): HookCallback {
            isReplaceHookMode = false
            afterHookCallback = initiate
            immediateHook()
            return HookCallback()
        }

        /**
         * Intercepts and replaces this [Member] with a return value.
         *
         * - Cannot be used together with [before] or [after].
         * @param initiate the [HookParam] function body.
         */
        fun replaceAny(initiate: HookParam.() -> Any?) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
            immediateHook()
        }

        /**
         * Intercepts and replaces this [Member] without a return value ([Unit]).
         *
         * - Cannot be used together with [before] or [after].
         * @param initiate the [HookParam] function body.
         */
        fun replaceUnit(initiate: HookParam.() -> Unit) {
            isReplaceHookMode = true
            replaceHookCallback = initiate
            immediateHook()
        }

        /**
         * Intercepts and replaces the [Member] return value.
         *
         * - Cannot be used together with [before] or [after].
         * @param any the replacement return value.
         */
        fun replaceTo(any: Any?) {
            isReplaceHookMode = true
            replaceHookCallback = { any }
            immediateHook()
        }

        /**
         * Intercepts and replaces the [Member] return value with true.
         *
         * - Ensure that the return object of the replaced [Member] is [Boolean].
         *
         * - Cannot be used together with [before] or [after].
         */
        fun replaceToTrue() {
            isReplaceHookMode = true
            replaceHookCallback = { true }
            immediateHook()
        }

        /**
         * Intercepts and replaces the [Member] return value with false.
         *
         * - Ensure that the return object of the replaced [Member] is [Boolean].
         *
         * - Cannot be used together with [before] or [after].
         */
        fun replaceToFalse() {
            isReplaceHookMode = true
            replaceHookCallback = { false }
            immediateHook()
        }

        /**
         * Intercepts this [Member].
         *
         * - This prevents the [Member] from executing and returns null.
         *
         * - Note: Setting a [Member] with a constant return value such as [Int], [Long], or [Boolean] to null may cause the Hook APP to throw an exception.
         *
         * - Cannot be used together with [before] or [after].
         */
        fun intercept() {
            isReplaceHookMode = true
            replaceHookCallback = { null }
            immediateHook()
        }

        /**
         * Removes the currently injected Hook [Method] and [Constructor] instances (unhooks them).
         *
         * - This feature can only be used in a Hook callback.
         * @param result callback indicating whether the operation succeeded.
         */
        fun removeSelf(result: (Boolean) -> Unit = {}) = this.result?.remove(result) ?: result(false)

        /**
         * Hook creation entry point.
         * @return [Result]
         */
        internal fun build() = Result().apply {
            result = this
            immediateHook(isLazyMode = true)
        }

        /**
         * Invokes Hooking immediately.
         * @param isLazyMode whether lazy mode is enabled. The default is false.
         */
        private fun immediateHook(isLazyMode: Boolean = false) {
            if (isLazyMode && hookMode == HookMode.LAZY_MEMBERS || hookMode == HookMode.IMMEDIATE) hook()
        }

        /** Hook execution entry point. */
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
                // No warning is issued for instances not created using [injectMember].
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
         * Hooks [Method] and [Constructor] instances.
         * @return [YukiHookResult]
         */
        private fun Member.hook(): YukiHookResult {
            /** Replacement Hook callback body. */
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
                            // If an exception occurs, the original unhooked [Member] is invoked to keep the Hook APP running normally.
                            assign.callOriginal()
                        }
                    }
            }

            /** Before and after Hook callback body. */
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
         * Checks the return value of the hooked [Member].
         * @param origin the original return value.
         * @param target the target return value.
         * @throws IllegalStateException if the return value is incorrect.
         */
        private fun checkingReturnType(origin: Class<*>?, target: Class<*>?) {
            if (origin == null || target == null) return
            if (origin == classOf<Any>()) return
            origin.toJavaPrimitiveType().also { o ->
                target.toJavaPrimitiveType().also { t ->
                    if (o isNotSubclassOf t && t isNotSubclassOf o)
                        error("Hooked method return type match failed, required [$origin] but got [$target]")
                }
            }
        }

        /**
         * Outputs failure information by default when Hooking fails and [onAllFailureCallback] is not set.
         * @param e the exception stack trace.
         * @param member the exceptional [Member], nullable.
         */
        private fun hookErrorMsg(e: Throwable, member: Member? = null) =
            if (hookClass.isPlaceholder)
                YLog.innerE("Try to hook ${member?.let { "[$it]" } ?: "nothing"} got an exception", e)
            else YLog.innerE("Try to hook [${hookClass.instance ?: hookClass.name}]${member?.let { "[$it]" } ?: ""} got an exception", e)

        /**
         * Whether no exception handling is configured during Hooking.
         * @return [Boolean] whether no exception handling is configured.
         */
        private val isNotIgnoredHookingFailure get() = onHookingFailureCallback == null && onAllFailureCallback == null

        /**
         * Whether no exception handling is configured for missing [members] during Hooking.
         * @return [Boolean] whether no exception handling is configured.
         */
        internal val isNotIgnoredNoSuchMemberFailure get() = onNoSuchMemberFailureCallback == null && isNotIgnoredHookingFailure

        override fun toString() =
            if (hookClass.isPlaceholder) "[priority] $priority [members] $members"
            else "[priority] $priority [class] $hookClass [members] $members"

        /**
         * Creates [LegacyCreator].
         * @return [LegacyCreator]
         */
        internal fun createLegacy() = LegacyCreator()

        /**
         * Legacy core Hook implementation created with [injectMember].
         */
        inner class LegacyCreator internal constructor() {

            /**
             * Manually specifies the [Method] and [Constructor] instances to hook.
             *
             * You can call [instanceClass] to manually find the [Method] and [Constructor] instances to hook.
             *
             * - Using this function to set the target [Member] to hook is not recommended. Use [method] or [constructor].
             *
             * - Only one of [members], [allMembers], [method], or [constructor] can be used in the same [injectMember], otherwise the result will be replaced.
             * @param member the [Member] or [Member] array to specify.
             * @throws IllegalStateException if [member] is empty.
             */
            fun members(vararg member: Member?) {
                if (member.isEmpty()) error("Custom Hooking Members is empty")
                members.clear()
                member.forEach { it?.also { members.add(it) } }
            }

            /**
             * Finds and hooks all [Method] instances named [name] in [hookClass].
             *
             * - This function is deprecated. It will be removed in a future version.
             *
             * - Migrate to [MethodFinder] or [allMembers] now.
             * @param name the method name.
             */
            @Deprecated(message = "Use the new approach to Hook all methods", ReplaceWith("method { this.name = name }.all()"))
            fun allMethods(name: String) = method { this.name = name }.all()

            /**
             * Finds and hooks all [Constructor] instances in [hookClass].
             *
             * - This function is deprecated. It will be removed in a future version.
             *
             * - Migrate to [ConstructorFinder] or [allMembers] now.
             */
            @Deprecated(
                message = "Use the new approach to Hook all constructors",
                ReplaceWith("allMembers(MembersType.CONSTRUCTOR)", "com.highcapable.yukihookapi.hook.factory.MembersType")
            )
            fun allConstructors() = allMembers(MembersType.CONSTRUCTOR)

            /**
             * Finds and hooks all [Method] and [Constructor] instances in [hookClass].
             *
             * - Only one of [members], [allMembers], [method], or [constructor] can be used in the same [injectMember], otherwise the result will be replaced.
             *
             * - Warning: The return value and parameters of each [Member] cannot be handled accurately. Use [method] or [constructor] to hook each [Member] separately.
             *
             * - An error may occur if [hookClass] has no [Member].
             * @param type the [Member] type to filter. The default is [MembersType.ALL].
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
             * Finds the [Method] to hook in [hookClass].
             *
             * - Only one of [members], [allMembers], [method], or [constructor] can be used in the same [injectMember], otherwise the result will be replaced.
             * @param initiate the function body.
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
             * Finds the [Constructor] to hook in [hookClass].
             *
             * - Only one of [members], [allMembers], [method], or [constructor] can be used in the same [injectMember], otherwise the result will be replaced.
             * @param initiate the function body.
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
             * Injects the [Method] and [Constructor] instances to hook (nested Hook).
             *
             * - This function is deprecated. It will be removed in a future version.
             *
             * - Nested Hooking is deprecated.
             */
            @Suppress("DEPRECATION")
            @LegacyHookApi
            @Deprecated(message = "Nested Hooking is deprecated")
            inline fun HookParam.injectMember(
                priority: Int = PRIORITY_DEFAULT,
                tag: String = "InnerDefault",
                initiate: MemberHookCreator.() -> Unit
            ) = Unit

            /**
             * Hooks before [Member] execution completes.
             *
             * - Cannot be used together with [replaceAny], [replaceUnit], or [replaceTo].
             * @param initiate the [HookParam] function body.
             * @return [HookCallback]
             */
            fun beforeHook(initiate: HookParam.() -> Unit) = before(initiate)

            /**
             * Hooks after [Member] execution completes.
             *
             * - Cannot be used together with [replaceAny], [replaceUnit], or [replaceTo].
             * @param initiate the [HookParam] function body.
             * @return [HookCallback]
             */
            fun afterHook(initiate: HookParam.() -> Unit) = after(initiate)

            /**
             * Intercepts and replaces this [Member] with a return value.
             *
             * - Cannot be used together with [before] or [after].
             * @param initiate the [HookParam] function body.
             */
            fun replaceAny(initiate: HookParam.() -> Any?) = this@MemberHookCreator.replaceAny(initiate)

            /**
             * Intercepts and replaces this [Member] without a return value ([Unit]).
             *
             * - Cannot be used together with [before] or [after].
             * @param initiate the [HookParam] function body.
             */
            fun replaceUnit(initiate: HookParam.() -> Unit) = this@MemberHookCreator.replaceUnit(initiate)

            /**
             * Intercepts and replaces the [Member] return value.
             *
             * - Cannot be used together with [before] or [after].
             * @param any the replacement return value.
             */
            fun replaceTo(any: Any?) = this@MemberHookCreator.replaceTo(any)

            /**
             * Intercepts and replaces the [Member] return value with true.
             *
             * - Ensure that the return object of the replaced [Member] is [Boolean].
             *
             * - Cannot be used together with [before] or [after].
             */
            fun replaceToTrue() = this@MemberHookCreator.replaceToTrue()

            /**
             * Intercepts and replaces the [Member] return value with false.
             *
             * - Ensure that the return object of the replaced [Member] is [Boolean].
             *
             * - Cannot be used together with [before] or [after].
             */
            fun replaceToFalse() = this@MemberHookCreator.replaceToFalse()

            /**
             * Intercepts this [Member].
             *
             * - This prevents the [Member] from executing and returns null.
             *
             * - Note: Setting a [Member] with a constant return value such as [Int], [Long], or [Boolean] to null may cause the Hook APP to throw an exception.
             *
             * - Cannot be used together with [before] or [after].
             */
            fun intercept() = this@MemberHookCreator.intercept()

            /**
             * Removes the currently injected Hook [Method] and [Constructor] instances (unhooks them).
             *
             * - This feature can only be used in a Hook callback.
             * @param result callback indicating whether the operation succeeded.
             */
            fun removeSelf(result: (Boolean) -> Unit = {}) = this@MemberHookCreator.removeSelf(result)

            /**
             * Hook creation entry point.
             * @return [Result]
             */
            internal fun build() = this@MemberHookCreator.build()

            /** Hook execution entry point. */
            internal fun hook() = this@MemberHookCreator.hook()

            override fun toString() = "LegacyCreator by ${this@MemberHookCreator}"
        }

        /**
         * Hook callback body implementation.
         */
        inner class HookCallback internal constructor() {

            /** Throws an exception to the current Hook APP when one occurs in the callback body. */
            fun onFailureThrowToApp() {
                isOnFailureThrowToApp = true
            }
        }

        /**
         * Hook result listener implementation.
         *
         * Failure event listeners can be handled here.
         */
        inner class Result internal constructor() {

            /**
             * Creates the listener event body.
             * @param initiate the function body.
             * @return [Result] that can continue listening.
             */
            inline fun result(initiate: Result.() -> Unit) = apply(initiate)

            /**
             * Adds a condition required to perform Hooking.
             *
             * Hooking stops immediately if the condition is not met.
             * @param condition the condition body.
             * @return [Result] that can continue listening.
             */
            inline fun by(condition: () -> Boolean): Result {
                isDisableMemberRunHook = (runCatching { condition() }.getOrNull() ?: false).not()
                if (isDisableMemberRunHook) ignoredAllFailure()
                return this
            }

            /**
             * Listens for the callback when [members] are hooked successfully.
             *
             * Invoked after the first successful Hook.
             *
             * [onAlreadyHooked] is invoked when Hooking is repeated.
             * @param result callback with the hooked [Member].
             * @return [Result] that can continue listening.
             */
            fun onHooked(result: (Member) -> Unit): Result {
                onHookedCallback = result
                return this
            }

            /**
             * Listens for the callback when [members] are hooked repeatedly.
             *
             * - This function and feature have been removed. They will be deleted in a future version.
             *
             * - Repeated Hook operations are no longer restricted.
             * @return [Result] that can continue listening.
             */
            @Deprecated(message = "This function and feature have been removed. Delete this function")
            fun onAlreadyHooked(result: (Member) -> Unit) = this

            /**
             * Listens for the error callback when [members] do not exist.
             * @param result the error callback.
             * @return [Result] that can continue listening.
             */
            @LegacyHookApi
            fun onNoSuchMemberFailure(result: (Throwable) -> Unit): Result {
                onNoSuchMemberFailureCallback = result
                return this
            }

            /**
             * Ignores errors caused by missing [members].
             * @return [Result] that can continue listening.
             */
            @LegacyHookApi
            fun ignoredNoSuchMemberFailure() = onNoSuchMemberFailure {}

            /**
             * Listens for the error callback during Hooking.
             * @param result the error callback with the current Hook [HookParam] and [Throwable] exception.
             * @return [Result] that can continue listening.
             */
            fun onConductFailure(result: (HookParam, Throwable) -> Unit): Result {
                onConductFailureCallback = result
                return this
            }

            /**
             * Ignores errors that occur during Hooking.
             * @return [Result] that can continue listening.
             */
            fun ignoredConductFailure() = onConductFailure { _, _ -> }

            /**
             * Listens for the error callback when Hooking starts.
             * @param result the error callback.
             * @return [Result] that can continue listening.
             */
            fun onHookingFailure(result: (Throwable) -> Unit): Result {
                onHookingFailureCallback = result
                return this
            }

            /**
             * Ignores errors that occur when Hooking starts.
             * @return [Result] that can continue listening.
             */
            fun ignoredHookingFailure() = onHookingFailure {}

            /**
             * Listens for the error callback throughout Hooking.
             * @param result the error callback.
             * @return [Result] that can continue listening.
             */
            fun onAllFailure(result: (Throwable) -> Unit): Result {
                onAllFailureCallback = result
                return this
            }

            /**
             * Ignores all errors that occur during Hooking.
             * @return [Result] that can continue listening.
             */
            fun ignoredAllFailure() = onAllFailure {}

            /**
             * Removes the currently injected Hook [Method] and [Constructor] instances (unhooks them).
             *
             * - Hooking can only be removed after it succeeds. Listen for the [onHooked] event.
             * @param result callback indicating whether the operation succeeded.
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
     * All Hook results listener implementation.
     *
     * Failure event listeners can be handled here.
     */
    inner class Result internal constructor() {

        /** Listener event callback invoked when Hooking starts. */
        internal var onPrepareHook: (() -> Unit)? = null

        /**
         * Creates the listener event body.
         * @param initiate the function body.
         * @return [Result] that can continue listening.
         */
        @LegacyHookApi
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * Adds a condition required to perform Hooking.
         *
         * Hooking stops immediately if the condition is not met.
         * @param condition the condition body.
         * @return [Result] that can continue listening.
         */
        @LegacyHookApi
        inline fun by(condition: () -> Boolean): Result {
            updateDisableCreatorRunHookReasons((runCatching { condition() }.getOrNull() ?: false).not())
            return this
        }

        /**
         * Listens for the operation that prepares to start Hooking when [hookClass] exists.
         * @param callback callback invoked after Hooking is prepared.
         * @return [Result] that can continue listening.
         */
        @LegacyHookApi
        fun onPrepareHook(callback: () -> Unit): Result {
            onPrepareHook = callback
            return this
        }

        /**
         * Listens for the error callback when [hookClass] cannot be found.
         * @param result the error callback.
         * @return [Result] that can continue listening.
         */
        @LegacyHookApi
        fun onHookClassNotFoundFailure(result: (Throwable) -> Unit): Result {
            onHookClassNotFoundFailureCallback = result
            return this
        }

        /**
         * Ignores errors caused by a missing [hookClass].
         * @return [Result] that can continue listening.
         */
        @LegacyHookApi
        fun ignoredHookClassNotFoundFailure(): Result {
            by { hookClass.instance != null }
            return this
        }
    }

    /**
     * Hook mode type definition.
     */
    internal enum class HookMode {
        /** Lazy [Class] mode. */
        LAZY_CLASSES,

        /** Lazy [Member] mode. */
        LAZY_MEMBERS,

        /** Immediate mode. */
        IMMEDIATE
    }
}