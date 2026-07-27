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
 * This file is created by fankes on 2022/2/4.
 */
@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "KotlinConstantConditions", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE",
    "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith"
)

package com.highcapable.yukihookapi.hook.core.finder.members

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.base.MemberBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.MethodConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import com.highcapable.yukihookapi.hook.utils.factory.unit
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [Method] finder.
 *
 * Finds a specific [Method] or group of [Method] instances by type.
 * @param classSet the [Class] instance to search.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class MethodFinder internal constructor(override val classSet: Class<*>? = null) : MemberBaseFinder(tag = "Method", classSet) {

    internal companion object {

        /**
         * Creates a [Method] finder through [YukiMemberHookCreator.MemberHookCreator].
         * @param hookInstance the current Hooker.
         * @param classSet the [Class] instance to search.
         * @return [MethodFinder]
         */
        internal fun fromHooker(hookInstance: YukiMemberHookCreator.MemberHookCreator, classSet: Class<*>? = null) =
            MethodFinder(classSet).apply { hookerManager.instance = hookInstance }
    }

    override var rulesData = MethodRulesData()

    /** The current [classSet]. */
    private var usedClassSet = classSet

    /** The current remedy-plan result callback. */
    private var remedyPlansCallback: (() -> Unit)? = null

    /**
     * Sets the [Method] name.
     *
     * - When no name is specified, at least one other condition is required.
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var name
        get() = rulesData.name
        set(value) {
            rulesData.name = value
        }

    /**
     * Sets the [Method] parameter count.
     *
     * You can use this property to specify only the parameter count without using [param] to specify parameter types.
     *
     * A negative parameter count is ignored and [param] is used instead.
     * @return [Int]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var paramCount
        get() = rulesData.paramCount
        set(value) {
            rulesData.paramCount = value
        }

    /**
     * Sets the [Method] return type.
     *
     * - The value must be [Class], [String], or [VariousClass].
     *
     * - The return type is optional.
     * @return [Any] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var returnType
        get() = rulesData.returnType
        set(value) {
            rulesData.returnType = value.compat()
        }

    /**
     * Sets the [Method] modifier conditions.
     *
     * - The conditions are optional.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun modifiers(conditions: ModifierConditions): IndexTypeCondition {
        rulesData.modifiers = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Configures an empty, parameterless [Method].
     *
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun emptyParam() = paramCount(num = 0)

    /**
     * Sets the [Method] parameters.
     *
     * When [paramCount] is also used, the number of [paramType] entries must exactly match [paramCount].
     *
     * If a [Method] contains unhelpful long type names, use [VagueType] in their place.
     *
     * For example, given the following parameter structure:
     *
     * ```java
     * void foo(String var1, boolean var2, com.demo.Test var3, int var4)
     * ```
     *
     * It can be written as:
     *
     * ```kotlin
     * param(StringType, BooleanType, VagueType, IntType)
     * ```
     *
     * - For a parameterless [Method], use [emptyParam] to set the finder condition.
     *
     * - For a parameterized [Method], use this method to set parameters or [paramCount] to specify their count.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param paramType the parameter type array. Entries must be [Class], [String], or [VariousClass].
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(vararg paramType: Any): IndexTypeCondition {
        if (paramType.isEmpty()) error("paramTypes is empty, please use emptyParam() instead")
        rulesData.paramTypes = mutableListOf<Class<*>>().apply { paramType.forEach { add(it.compat() ?: UndefinedType) } }.toTypedArray()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] parameter conditions.
     *
     * Example:
     *
     * ```kotlin
     * param { it[1] == StringClass || it[2].name == "java.lang.String" }
     * ```
     *
     * - For a parameterless [Method], use [emptyParam] to set the finder condition.
     *
     * - For a parameterized [Method], use this method to set parameters or [paramCount] to specify their count.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(conditions: ObjectsConditions): IndexTypeCondition {
        rulesData.paramTypesConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Filters by the bytecode order index.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * Sets the [Method] name.
     *
     * - When no name is specified, at least one other condition is required.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param value the name.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun name(value: String): IndexTypeCondition {
        rulesData.name = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] name condition.
     *
     * - When no name is specified, at least one other condition is required.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun name(conditions: NameConditions): IndexTypeCondition {
        rulesData.nameConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] parameter count.
     *
     * You can use this method to specify only the parameter count without using [param] to specify parameter types.
     *
     * A negative parameter count is ignored and [param] is used instead.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param num the count.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(num: Int): IndexTypeCondition {
        rulesData.paramCount = num
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] parameter-count range.
     *
     * You can use this method to specify only the parameter-count range without using [param] to specify parameter types.
     *
     * Example:
     *
     * ```kotlin
     * paramCount(1..5)
     * ```
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param numRange the count range.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(numRange: IntRange): IndexTypeCondition {
        rulesData.paramCountRange = numRange
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] parameter-count condition.
     *
     * You can use this method to specify only a parameter-count condition without using [param] to specify parameter types.
     *
     * Example:
     *
     * ```kotlin
     * paramCount { it >= 5 || it.isZero() }
     * ```
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(conditions: CountConditions): IndexTypeCondition {
        rulesData.paramCountConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] return type.
     *
     * - The return type is optional.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param value the return type. It must be [Class], [String], or [VariousClass].
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun returnType(value: Any): IndexTypeCondition {
        rulesData.returnType = value.compat()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Method] return-type condition.
     *
     * - The return type is optional.
     *
     * Example:
     *
     * ```kotlin
     * returnType { it == StringClass || it.name == "java.lang.String" }
     * ```
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun returnType(conditions: ObjectConditions): IndexTypeCondition {
        rulesData.returnTypeConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Searches for the current [Method] in all superclasses of [classSet].
     *
     * - A deep superclass hierarchy may take time to search. The API stops at the last class before [Any].
     * @param isOnlySuperClass whether to search only superclasses of [classSet]. This has no effect when the superclass is [Any].
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun superClass(isOnlySuperClass: Boolean = false) {
        rulesData.isFindInSuper = true
        if (isOnlySuperClass && classSet?.hasExtends == true) usedClassSet = classSet.superclass
    }

    /**
     * Finds a [Method] or group of [Method] instances.
     * @return [MutableList]<[Method]>
     * @throws NoSuchMethodError if no [Method] can be found.
     */
    private val result get() = ReflectionTool.findMethods(usedClassSet, rulesData)

    /**
     * Sets the instances.
     * @param methods the currently found [Method] instances.
     */
    private fun setInstance(methods: MutableList<Method>) {
        memberInstances.clear()
        methods.takeIf { it.isNotEmpty() }?.onEach { memberInstances.add(it) }
            ?.first()?.apply { if (hookerManager.isMemberBinded) hookerManager.bindMember(member = this) }
    }

    /** Gets the [Method] result. */
    private fun internalBuild() {
        if (classSet == null) error(CLASSSET_IS_NULL)
        runBlocking {
            setInstance(result)
        }.result { ms ->
            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Method [$it] takes ${ms}ms") }
        }
    }

    override fun build() = runCatching {
        internalBuild()
        Result()
    }.getOrElse {
        errorMsg(e = it)
        Result(isNoSuch = true, it)
    }

    override fun process() = runCatching {
        hookerManager.isMemberBinded = true
        internalBuild()
        Process()
    }.getOrElse {
        errorMsg(e = it)
        Process(isNoSuch = true, it)
    }

    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    override fun denied(throwable: Throwable?) = Process(isNoSuch = true, throwable)

    /**
     * [Method] remedy-plan implementation.
     *
     * Accumulates failed attempts until the search succeeds.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class RemedyPlan internal constructor() {

        /** Failed attempts. */
        private val remedyPlans = mutableSetOf<Pair<MethodFinder, Result>>()

        /**
         * Adds a [Method] to search for again.
         *
         * Add multiple alternative [Method] definitions until one succeeds.
         *
         * If every attempt fails, the search stops and prints an error log.
         * @param initiate the finder block.
         * @return [Result] the result.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun method(initiate: MethodConditions) = Result().apply {
            remedyPlans.add(Pair(MethodFinder(classSet).apply {
                hookerManager = this@MethodFinder.hookerManager
            }.apply(initiate), this))
        }

        /** Starts the remedy plan. */
        internal fun build() {
            if (classSet == null) return
            if (remedyPlans.isNotEmpty()) {
                val errors = mutableListOf<Throwable>()
                var isFindSuccess = false
                remedyPlans.forEachIndexed { index, plan ->
                    runCatching {
                        runBlocking {
                            setInstance(plan.first.result)
                        }.result { ms ->
                            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Method [$it] takes ${ms}ms") }
                        }
                        isFindSuccess = true
                        plan.second.onFindCallback?.invoke(memberInstances.methods())
                        remedyPlansCallback?.invoke()
                        memberInstances.takeIf { it.isNotEmpty() }
                            ?.forEach { debugMsg(msg = "RemedyPlan successed after ${index + 1} attempts of Method [$it]") }
                        return
                    }.onFailure { errors.add(it) }
                }
                if (isFindSuccess) return
                errorMsg(msg = "RemedyPlan failed after ${remedyPlans.size} attempts", es = errors, isAlwaysMode = true)
                remedyPlans.clear()
            } else YLog.innerW("RemedyPlan is empty, forgot it?")
        }

        /**
         * [RemedyPlan] result implementation.
         *
         * Handles the success callback.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inner class Result internal constructor() {

            /** Callback invoked when a result is found. */
            internal var onFindCallback: (MutableList<Method>.() -> Unit)? = null

            /**
             * Runs when a result is found.
             * @param initiate the callback.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun onFind(initiate: MutableList<Method>.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Method] finder result processor for [hookerManager].
     * @param isNoSuch whether no [Method] was found, false by default.
     * @param throwable the error.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class Process internal constructor(
        internal val isNoSuch: Boolean = false,
        internal val throwable: Throwable? = null
    ) : BaseResult {

        /**
         * Creates the result listener block.
         * @param initiate the listener block.
         * @return [Process] this process for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun result(initiate: Process.() -> Unit) = apply(initiate)

        /**
         * Assigns all matching [Method] instances to [hookerManager].
         * @return [Process] this process for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all(): Process {
            fun MutableList<Member>.bind() = takeIf { it.isNotEmpty() }?.apply { hookerManager.bindMembers(members = this) }.unit()
            if (isUsingRemedyPlan)
                remedyPlansCallback = { memberInstances.bind() }
            else memberInstances.bind()
            return this
        }

        /**
         * Creates a [Method] remedy plan.
         *
         * Use this when a [Method] may exist in different forms.
         *
         * [RemedyPlan] can search again without using [onNoSuchMethod] to catch an exception and perform a second search.
         *
         * If the first search fails, add more finder blocks here until one succeeds.
         * @param initiate the remedy-plan block.
         * @return [Process] this process for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun remedys(initiate: RemedyPlan.() -> Unit): Process {
            isUsingRemedyPlan = true
            if (isNoSuch) RemedyPlan().apply(initiate).build()
            return this
        }

        /**
         * Listens for a missing [Method].
         *
         * - Returns only the first error, not errors from [RemedyPlan].
         * @param result the error callback.
         * @return [Process] this process for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun onNoSuchMethod(result: (Throwable) -> Unit): Process {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }
    }

    /**
     * [Method] finder result implementation.
     * @param isNoSuch whether no [Method] was found, false by default.
     * @param throwable the error.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class Result internal constructor(
        internal val isNoSuch: Boolean = false,
        internal val throwable: Throwable? = null
    ) : BaseResult {

        /**
         * Creates the result listener block.
         * @param initiate the listener block.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * Gets a [Method] instance handler.
         *
         * - Returns only the first result when multiple [Method] instances match.
         *
         * - This method cannot return an object when [memberInstances] is empty.
         *
         * - When [remedys] is set, use the [wait] result callback.
         * @param instance the object containing the [Method]. Omit it for a static method. The default is null.
         * @return [Instance]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun get(instance: Any? = null) = Instance(instance, give())

        /**
         * Gets all [Method] instance handlers.
         *
         * - Returns all [Method] instances matching the finder conditions.
         *
         * - This method cannot return objects when [memberInstances] is empty.
         *
         * - When [remedys] is set, use the [waitAll] result callback.
         * @param instance the object containing the [Method]. Omit it for a static method. The default is null.
         * @return [MutableList]<[Instance]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all(instance: Any? = null) =
            mutableListOf<Instance>().apply { giveAll().takeIf { it.isNotEmpty() }?.forEach { add(Instance(instance, it)) } }

        /**
         * Gets the [Method] itself.
         *
         * - Returns only the first result when multiple [Method] instances match.
         *
         * - Returns null when the finder conditions produce no result.
         * @return [Method] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun give() = giveAll().takeIf { it.isNotEmpty() }?.first()

        /**
         * Gets the [Method] instances themselves.
         *
         * - Returns all [Method] instances matching the finder conditions.
         *
         * - Returns an empty [MutableList] when the finder conditions produce no result.
         * @return [MutableList]<[Method]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun giveAll() = memberInstances.takeIf { it.isNotEmpty() }?.methods() ?: mutableListOf()

        /**
         * Gets a [Method] instance handler.
         *
         * - Returns only the first result when multiple [Method] instances match.
         *
         * - When [remedys] is set, this method is required to obtain the result.
         *
         * - This callback is not invoked when [remedys] is not set.
         * @param instance the containing instance.
         * @param initiate the [Instance] callback.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun wait(instance: Any? = null, initiate: Instance.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(get(instance))
            else remedyPlansCallback = { initiate(get(instance)) }
        }

        /**
         * Gets all [Method] instance handlers.
         *
         * - Returns all [Method] instances matching the finder conditions.
         *
         * - When [remedys] is set, this method is required to obtain the result.
         *
         * - This callback is not invoked when [remedys] is not set.
         * @param instance the containing instance.
         * @param initiate the [MutableList]<[Instance]> callback.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun waitAll(instance: Any? = null, initiate: MutableList<Instance>.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(all(instance))
            else remedyPlansCallback = { initiate(all(instance)) }
        }

        /**
         * Creates a [Method] remedy plan.
         *
         * Use this when a [Method] may exist in different forms.
         *
         * [RemedyPlan] can search again without using [onNoSuchMethod] to catch an exception and perform a second search.
         *
         * If the first search fails, add more finder blocks here until one succeeds.
         * @param initiate the remedy-plan block.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun remedys(initiate: RemedyPlan.() -> Unit): Result {
            isUsingRemedyPlan = true
            if (isNoSuch) RemedyPlan().apply(initiate).build()
            return this
        }

        /**
         * Listens for a missing [Method].
         *
         * - Returns only the first error, not errors from [RemedyPlan].
         * @param result the error callback.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun onNoSuchMethod(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * Ignores exceptions and stops printing error logs.
         *
         * - Automatically ignored when [MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure] is false.
         *
         * - To listen for exception results in this state, implement [onNoSuchMethod] manually.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun ignored(): Result {
            isIgnoreErrorLogs = true
            return this
        }

        /**
         * Ignores exceptions and stops printing error logs.
         *
         * - This API is deprecated and will be removed in a future version.
         *
         * - Migrate to [ignored].
         * @return [Result] this result for chaining.
         */
        @Deprecated(message = "Use the new naming method", ReplaceWith("ignored()"))
        fun ignoredError() = ignored()

        /**
         * [Method] instance handler.
         *
         * - Use [get], [wait], [all], or [waitAll] to obtain [Instance].
         * @param instance the instance of the class containing the current [Method].
         * @param method the current [Method] instance.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inner class Instance internal constructor(private val instance: Any?, private val method: Method?) {

            /** Whether the original unhooked [Method] should be invoked. */
            private var isCallOriginal = false

            /**
             * Marks the current [Method] to invoke its original unhooked implementation.
             *
             * If the current [Method] is not hooked, the original [Method.invoke] is used.
             *
             * - This feature is available only in the (Xposed) host environment.
             * @return [Instance] this instance for chaining.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun original(): Instance {
                isCallOriginal = true
                return this
            }

            /**
             * Invokes the [Method].
             * @param args the method arguments.
             * @return [Any] or null.
             */
            private fun baseCall(vararg args: Any?) =
                if (isCallOriginal)
                    YukiHookHelper.invokeOriginalMember(method, instance, args)
                else method?.invoke(instance, *args)

            /**
             * Invokes the [Method] without specifying a return type.
             * @param args the method arguments.
             * @return [Any] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun call(vararg args: Any?) = baseCall(*args)

            /**
             * Invokes the [Method] with return type [T].
             * @param args the method arguments.
             * @return [T] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun <T> invoke(vararg args: Any?) = baseCall(*args) as? T?

            /**
             * Invokes the [Method] with return type [Byte].
             *
             * - Verify the target variable type. An error returns null.
             * @param args the method arguments.
             * @return [Byte] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun byte(vararg args: Any?) = invoke<Byte?>(*args)

            /**
             * Invokes the [Method] with return type [Int].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Int] 0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun int(vararg args: Any?) = invoke(*args) ?: 0

            /**
             * Invokes the [Method] with return type [Long].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Long] 0L when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun long(vararg args: Any?) = invoke(*args) ?: 0L

            /**
             * Invokes the [Method] with return type [Short].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Short] 0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun short(vararg args: Any?) = invoke<Short?>(*args) ?: 0

            /**
             * Invokes the [Method] with return type [Double].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Double] 0.0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun double(vararg args: Any?) = invoke(*args) ?: 0.0

            /**
             * Invokes the [Method] with return type [Float].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Float] 0f when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun float(vararg args: Any?) = invoke(*args) ?: 0f

            /**
             * Invokes the [Method] with return type [String].
             * @param args the method arguments.
             * @return [String] an empty string when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun string(vararg args: Any?) = invoke(*args) ?: ""

            /**
             * Invokes the [Method] with return type [Char].
             * @param args the method arguments.
             * @return [Char] a space character when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun char(vararg args: Any?) = invoke(*args) ?: ' '

            /**
             * Invokes the [Method] with return type [Boolean].
             *
             * - Verify the target [Method] return value. An error returns the default value.
             * @param args the method arguments.
             * @return [Boolean] false when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun boolean(vararg args: Any?) = invoke(*args) ?: false

            /**
             * Invokes the [Method] with an [Array] return value whose elements are of type [T].
             *
             * - Verify the target [Method] return value. An error returns an empty array.
             * @return [Array] an empty array when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            inline fun <reified T> array(vararg args: Any?) = invoke(*args) ?: arrayOf<T>()

            /**
             * Invokes the [Method] with a [List] return value whose elements are of type [T].
             *
             * - Verify the target [Method] return value. An error returns an empty list.
             * @return [List] an empty list when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            inline fun <reified T> list(vararg args: Any?) = invoke(*args) ?: listOf<T>()

            override fun toString() = "[${method?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}]"
        }
    }
}