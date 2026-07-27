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
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.base.MemberBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ConstructorConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import com.highcapable.yukihookapi.hook.utils.factory.unit
import java.lang.reflect.Constructor
import java.lang.reflect.Member

/**
 * [Constructor] finder.
 *
 * Finds a specific [Constructor] or group of [Constructor] instances by type.
 * @param classSet the [Class] instance to search.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class ConstructorFinder internal constructor(override val classSet: Class<*>? = null) : MemberBaseFinder(tag = "Constructor", classSet) {

    internal companion object {

        /**
         * Creates a [Constructor] finder through [YukiMemberHookCreator.MemberHookCreator].
         * @param hookInstance the current Hooker.
         * @param classSet the [Class] instance to search.
         * @return [ConstructorFinder]
         */
        internal fun fromHooker(hookInstance: YukiMemberHookCreator.MemberHookCreator, classSet: Class<*>? = null) =
            ConstructorFinder(classSet).apply { hookerManager.instance = hookInstance }
    }

    override var rulesData = ConstructorRulesData()

    /** The current [classSet]. */
    private var usedClassSet = classSet

    /** The current remedy-plan result callback. */
    private var remedyPlansCallback: (() -> Unit)? = null

    /**
     * Sets the [Constructor] parameter count.
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
     * Sets the [Constructor] modifier conditions.
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
     * Configures an empty, parameterless [Constructor].
     *
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun emptyParam() = paramCount(num = 0)

    /**
     * Sets the [Constructor] parameters.
     *
     * When [paramCount] is also used, the number of [paramType] entries must exactly match [paramCount].
     *
     * If a [Constructor] contains unhelpful long type names, use [VagueType] in their place.
     *
     * For example, given the following parameter structure:
     *
     * ```java
     * Foo(String var1, boolean var2, com.demo.Test var3, int var4)
     * ```
     *
     * It can be written as:
     *
     * ```kotlin
     * param(StringType, BooleanType, VagueType, IntType)
     * ```
     *
     * - For a parameterless [Constructor], use [emptyParam] to set the finder condition.
     *
     * - For a parameterized [Constructor], use this method to set parameters or [paramCount] to specify their count.
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
     * Sets the [Constructor] parameter conditions.
     *
     * Example:
     *
     * ```kotlin
     * param { it[1] == StringClass || it[2].name == "java.lang.String" }
     * ```
     *
     * - For a parameterless [Constructor], use [emptyParam] to set the finder condition.
     *
     * - For a parameterized [Constructor], use this method to set parameters or [paramCount] to specify their count.
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
     * Sets the [Constructor] parameter count.
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
     * Sets the [Constructor] parameter-count range.
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
     * Sets the [Constructor] parameter-count condition.
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
     * Searches for the current [Constructor] in all superclasses of [classSet].
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
     * Finds a [Constructor] or group of [Constructor] instances.
     * @return [MutableList]<[Constructor]>
     * @throws NoSuchMethodError if no [Constructor] can be found.
     */
    private val result by lazy { ReflectionTool.findConstructors(usedClassSet, rulesData) }

    /**
     * Sets the instances.
     * @param constructors the currently found [Constructor] instances.
     */
    private fun setInstance(constructors: MutableList<Constructor<*>>) {
        memberInstances.clear()
        constructors.takeIf { it.isNotEmpty() }?.onEach { memberInstances.add(it) }
            ?.first()?.apply { if (hookerManager.isMemberBinded) hookerManager.bindMember(member = this) }
    }

    /** Gets the [Constructor] result. */
    private fun internalBuild() {
        if (classSet == null) error(CLASSSET_IS_NULL)
        runBlocking {
            setInstance(result)
        }.result { ms ->
            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Constructor [$it] takes ${ms}ms") }
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
     * [Constructor] remedy-plan implementation.
     *
     * Accumulates failed attempts until the search succeeds.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class RemedyPlan internal constructor() {

        /** Failed attempts. */
        private val remedyPlans = mutableSetOf<Pair<ConstructorFinder, Result>>()

        /**
         * Adds a [Constructor] to search for again.
         *
         * Add multiple alternative [Constructor] definitions until one succeeds.
         *
         * If every attempt fails, the search stops and prints an error log.
         * @param initiate the finder block.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun constructor(initiate: ConstructorConditions) = Result().apply {
            remedyPlans.add(Pair(ConstructorFinder(classSet).apply {
                hookerManager = this@ConstructorFinder.hookerManager
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
                            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Constructor [$it] takes ${ms}ms") }
                        }
                        isFindSuccess = true
                        plan.second.onFindCallback?.invoke(memberInstances.constructors())
                        remedyPlansCallback?.invoke()
                        memberInstances.takeIf { it.isNotEmpty() }
                            ?.forEach { debugMsg(msg = "RemedyPlan successed after ${index + 1} attempts of Constructor [$it]") }
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
            internal var onFindCallback: (MutableList<Constructor<*>>.() -> Unit)? = null

            /**
             * Runs when a result is found.
             * @param initiate the callback.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun onFind(initiate: MutableList<Constructor<*>>.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Constructor] finder result processor for [hookerManager].
     * @param isNoSuch whether no [Constructor] was found, false by default.
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
         * Assigns all matching [Constructor] instances to [hookerManager].
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
         * Creates a [Constructor] remedy plan.
         *
         * Use this when a [Constructor] may exist in different forms.
         *
         * [RemedyPlan] can search again without using [onNoSuchConstructor] to catch an exception and perform a second search.
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
         * Listens for a missing [Constructor].
         *
         * - Returns only the first error, not errors from [RemedyPlan].
         * @param result the error callback.
         * @return [Process] this process for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun onNoSuchConstructor(result: (Throwable) -> Unit): Process {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }
    }

    /**
     * [Constructor] finder result implementation.
     * @param isNoSuch whether no [Constructor] was found, false by default.
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
         * Gets a [Constructor] instance handler.
         *
         * - Returns only the first result when multiple [Constructor] instances match.
         *
         * - This method cannot return an object when [memberInstances] is empty.
         *
         * - When [remedys] is set, use the [wait] result callback.
         * @return [Instance]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun get() = Instance(give())

        /**
         * Gets all [Constructor] instance handlers.
         *
         * - Returns all [Constructor] instances matching the finder conditions.
         *
         * - This method cannot return objects when [memberInstances] is empty.
         *
         * - When [remedys] is set, use the [waitAll] result callback.
         * @return [MutableList]<[Instance]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all() = mutableListOf<Instance>().apply { giveAll().takeIf { it.isNotEmpty() }?.forEach { add(Instance(it)) } }

        /**
         * Gets the [Constructor] itself.
         *
         * - Returns only the first result when multiple [Constructor] instances match.
         *
         * - Returns null when the finder conditions produce no result.
         * @return [Constructor] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun give() = giveAll().takeIf { it.isNotEmpty() }?.first()

        /**
         * Gets the [Constructor] instances themselves.
         *
         * - Returns all [Constructor] instances matching the finder conditions.
         *
         * - Returns an empty [MutableList] when the finder conditions produce no result.
         * @return [MutableList]<[Constructor]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun giveAll() = memberInstances.takeIf { it.isNotEmpty() }?.constructors() ?: mutableListOf()

        /**
         * Gets a [Constructor] instance handler.
         *
         * - Returns only the first result when multiple [Constructor] instances match.
         *
         * - When [remedys] is set, this method is required to obtain the result.
         *
         * - This callback is not invoked when [remedys] is not set.
         * @param initiate the [Instance] callback.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun wait(initiate: Instance.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(get())
            else remedyPlansCallback = { initiate(get()) }
        }

        /**
         * Gets all [Constructor] instance handlers.
         *
         * - Returns all [Constructor] instances matching the finder conditions.
         *
         * - When [remedys] is set, this method is required to obtain the result.
         *
         * - This callback is not invoked when [remedys] is not set.
         * @param initiate the [MutableList]<[Instance]> callback.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun waitAll(initiate: MutableList<Instance>.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(all())
            else remedyPlansCallback = { initiate(all()) }
        }

        /**
         * Creates a [Constructor] remedy plan.
         *
         * Use this when a [Constructor] may exist in different forms.
         *
         * [RemedyPlan] can search again without using [onNoSuchConstructor] to catch an exception and perform a second search.
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
         * Listens for a missing [Constructor].
         *
         * - Returns only the first error, not errors from [RemedyPlan].
         * @param result the error callback.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun onNoSuchConstructor(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * Ignores exceptions and stops printing error logs.
         *
         * - Automatically ignored when [MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure] is false.
         *
         * - To listen for exception results in this state, implement [onNoSuchConstructor] manually.
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
         * [Constructor] instance handler.
         *
         * Invokes and creates target instances.
         *
         * - Use [get], [wait], [all], or [waitAll] to obtain [Instance].
         * @param constructor the current [Constructor] instance.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inner class Instance internal constructor(private val constructor: Constructor<*>?) {

            /**
             * Invokes the [Constructor] to create a target instance.
             * @param args the [Constructor] arguments.
             * @return [Any] or null.
             */
            private fun baseCall(vararg args: Any?) = constructor?.newInstance(*args)

            /**
             * Invokes the [Constructor] to create a target instance without specifying its type.
             * @param args the [Constructor] arguments.
             * @return [Any] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun call(vararg args: Any?) = baseCall(*args)

            /**
             * Invokes the [Constructor] to create a target instance of type [T].
             * @param args the [Constructor] arguments.
             * @return [T] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun <T> newInstance(vararg args: Any?) = baseCall(*args) as? T?

            override fun toString() = "[${constructor?.name ?: "<empty>"}]"
        }
    }
}