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
    "unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "KotlinConstantConditions", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE",
    "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith"
)

package com.highcapable.yukihookapi.hook.core.finder.members

import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.base.MemberBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.FieldConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import java.lang.reflect.Field

/**
 * [Field] finder.
 *
 * Finds a specific [Field] or group of [Field] instances by type.
 * @param classSet the [Class] instance to search.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class FieldFinder internal constructor(override val classSet: Class<*>? = null) : MemberBaseFinder(tag = "Field", classSet) {

    internal companion object {

        /**
         * Creates a [Field] finder through [YukiMemberHookCreator.MemberHookCreator].
         * @param hookInstance the current Hooker.
         * @param classSet the [Class] instance to search.
         * @return [FieldFinder]
         */
        internal fun fromHooker(hookInstance: YukiMemberHookCreator.MemberHookCreator, classSet: Class<*>? = null) =
            FieldFinder(classSet).apply { hookerManager.instance = hookInstance }
    }

    override var rulesData = FieldRulesData()

    /** The current [classSet]. */
    private var usedClassSet = classSet

    /** The current remedy-plan result callback. */
    private var remedyPlansCallback: (() -> Unit)? = null

    /**
     * Sets the [Field] name.
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
     * Sets the [Field] type.
     *
     * - The value must be [Class], [String], or [VariousClass].
     *
     * - The type is optional.
     * @return [Any] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var type
        get() = rulesData.type
        set(value) {
            rulesData.type = value.compat()
        }

    /**
     * Sets the [Field] modifier conditions.
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
     * Filters by the bytecode order index.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * Sets the [Field] name.
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
     * Sets the [Field] name condition.
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
     * Sets the [Field] type.
     *
     * - The type is optional.
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param value the type. It must be [Class], [String], or [VariousClass].
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun type(value: Any): IndexTypeCondition {
        rulesData.type = value.compat()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Sets the [Field] type condition.
     *
     * - The type is optional.
     *
     * Example:
     *
     * ```kotlin
     * type { it == StringClass || it.name == "java.lang.String" }
     * ```
     *
     * - When multiple [BaseFinder.IndexTypeCondition] instances are present, only the last one takes effect except for [order].
     * @param conditions the condition block.
     * @return [BaseFinder.IndexTypeCondition]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun type(conditions: ObjectConditions): IndexTypeCondition {
        rulesData.typeConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * Searches for the current [Field] in all superclasses of [classSet].
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
     * Finds a [Field] or group of [Field] instances.
     * @return [MutableList]<[Field]>
     * @throws NoSuchFieldError if no [Field] can be found.
     */
    private val result get() = ReflectionTool.findFields(usedClassSet, rulesData)

    /**
     * Sets the instances.
     * @param fields the currently found [Field] instances.
     */
    private fun setInstance(fields: MutableList<Field>) {
        memberInstances.clear()
        fields.takeIf { it.isNotEmpty() }?.forEach { memberInstances.add(it) }
    }

    /** Gets the [Field] result. */
    private fun internalBuild() {
        if (classSet == null) error(CLASSSET_IS_NULL)
        runBlocking {
            setInstance(result)
        }.result { ms ->
            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Field [$it] takes ${ms}ms") }
        }
    }

    override fun build() = runCatching {
        internalBuild()
        Result()
    }.getOrElse {
        errorMsg(e = it)
        Result(isNoSuch = true, it)
    }

    override fun process() = error("FieldFinder does not contain this usage")

    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    override fun denied(throwable: Throwable?) = error("FieldFinder does not contain this usage")

    /**
     * [Field] remedy-plan implementation.
     *
     * Accumulates failed attempts until the search succeeds.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class RemedyPlan internal constructor() {

        /** Failed attempts. */
        private val remedyPlans = mutableSetOf<Pair<FieldFinder, Result>>()

        /**
         * Adds a [Field] to search for again.
         *
         * Add multiple alternative [Field] definitions until one succeeds.
         *
         * If every attempt fails, the search stops and prints an error log.
         * @param initiate the finder block.
         * @return [Result] the result.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun field(initiate: FieldConditions) = Result().apply {
            remedyPlans.add(FieldFinder(classSet).apply {
                hookerManager = this@FieldFinder.hookerManager
            }.apply(initiate) to this)
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
                            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Field [$it] takes ${ms}ms") }
                        }
                        isFindSuccess = true
                        plan.second.onFindCallback?.invoke(memberInstances.fields())
                        remedyPlansCallback?.invoke()
                        memberInstances.takeIf { it.isNotEmpty() }
                            ?.forEach { debugMsg(msg = "RemedyPlan successed after ${index + 1} attempts of Field [$it]") }
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
            internal var onFindCallback: (MutableList<Field>.() -> Unit)? = null

            /**
             * Runs when a result is found.
             * @param initiate the callback.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun onFind(initiate: MutableList<Field>.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Field] finder result implementation.
     *
     * @param isNoSuch whether no [Field] was found, false by default.
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
         * Gets a [Field] instance handler.
         *
         * - Returns only the first result when multiple [Field] instances match.
         *
         * - This method cannot return an object when [memberInstances] is empty.
         *
         * - For a non-static target, [instance] must be set.
         *
         * - When [remedys] is set, use the [wait] result callback.
         * @param instance the object containing the [Field]. Omit it for a static field. The default is null.
         * @return [Instance]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun get(instance: Any? = null) = Instance(instance, give())

        /**
         * Gets all [Field] instance handlers.
         *
         * - Returns all [Field] instances matching the finder conditions.
         *
         * - This method cannot return objects when [memberInstances] is empty.
         *
         * - For a non-static target, [instance] must be set.
         *
         * - When [remedys] is set, use the [waitAll] result callback.
         * @param instance the object containing the [Field]. Omit it for a static field. The default is null.
         * @return [MutableList]<[Instance]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all(instance: Any? = null) =
            mutableListOf<Instance>().apply { giveAll().takeIf { it.isNotEmpty() }?.forEach { add(Instance(instance, it)) } }

        /**
         * Gets the [Field] itself.
         *
         * - Returns only the first result when multiple [Field] instances match.
         *
         * - Returns null when the finder conditions produce no result.
         * @return [Field] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun give() = giveAll().takeIf { it.isNotEmpty() }?.first()

        /**
         * Gets the [Field] instances themselves.
         *
         * - Returns all [Field] instances matching the finder conditions.
         *
         * - Returns an empty [MutableList] when the finder conditions produce no result.
         * @return [MutableList]<[Field]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun giveAll() = memberInstances.takeIf { it.isNotEmpty() }?.fields() ?: mutableListOf()

        /**
         * Gets a [Field] instance handler.
         *
         * - Returns only the first result when multiple [Field] instances match.
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
         * Gets all [Field] instance handlers.
         *
         * - Returns all [Field] instances matching the finder conditions.
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
         * Creates a [Field] remedy plan.
         *
         * Use this when a field may exist in different forms.
         *
         * [RemedyPlan] can search again without using [onNoSuchField] to catch an exception and perform a second search.
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
         * Listens for a missing [Field].
         *
         * - Returns only the first error, not errors from [RemedyPlan].
         * @param result the error callback.
         * @return [Result] this result for chaining.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun onNoSuchField(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * Ignores exceptions and stops printing error logs.
         *
         * - Automatically ignored when [MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure] is false.
         *
         * - To listen for exception results in this state, implement [onNoSuchField] manually.
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
         * [Field] instance handler.
         *
         * - Use [get] or [all] to obtain [Instance].
         * @param instance the instance of the class containing the current [Field].
         * @param field the current [Field] instance.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inner class Instance internal constructor(private val instance: Any?, private val field: Field?) {

            /**
             * Gets the value of the current [Field].
             *
             * - Call [any] to get a value of an unknown type directly.
             * @return [Any] or null.
             */
            private val self get() = field?.get(instance)

            /**
             * Gets the class operation object for the current [Field] value [self].
             * @param ignored whether to suppress error warnings, false by default.
             * @return [CurrentClass] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun current(ignored: Boolean = false) = self?.current(ignored)

            /**
             * Gets the class operation object for the current [Field] value [self].
             * @param ignored whether to suppress error warnings, false by default.
             * @param initiate the operation block.
             * @return [Any] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            inline fun current(ignored: Boolean = false, initiate: CurrentClass.() -> Unit) = self?.current(ignored, initiate)

            /**
             * Gets the current [Field] value.
             * @return [T] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun <T> cast() = self as? T?

            /**
             * Gets the current [Field] value as [Byte].
             *
             * - Verify the target [Field] type. An error returns null.
             * @return [Byte] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun byte() = cast<Byte?>()

            /**
             * Gets the current [Field] value as [Int].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Int] 0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun int() = cast() ?: 0

            /**
             * Gets the current [Field] value as [Long].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Long] 0L when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun long() = cast() ?: 0L

            /**
             * Gets the current [Field] value as [Short].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Short] 0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun short() = cast<Short?>() ?: 0

            /**
             * Gets the current [Field] value as [Double].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Double] 0.0 when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun double() = cast() ?: 0.0

            /**
             * Gets the current [Field] value as [Float].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Float] 0f when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun float() = cast() ?: 0f

            /**
             * Gets the current [Field] value as [String].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [String] an empty string when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun string() = cast() ?: ""

            /**
             * Gets the current [Field] value as [Char].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Char] a space character when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun char() = cast() ?: ' '

            /**
             * Gets the current [Field] value as [Boolean].
             *
             * - Verify the target [Field] type. An error returns the default value.
             * @return [Boolean] false when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun boolean() = cast() ?: false

            /**
             * Gets the current [Field] value as [Any].
             * @return [Any] or null.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun any() = self

            /**
             * Gets the current [Field] value as an [Array] whose elements are of type [T].
             *
             * - Verify the target [Field] type. An error returns an empty array.
             * @return [Array] an empty array when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            inline fun <reified T> array() = cast() ?: arrayOf<T>()

            /**
             * Gets the current [Field] value as a [List] whose elements are of type [T].
             *
             * - Verify the target [Field] type. An error returns an empty list.
             * @return [List] an empty list when unavailable.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            inline fun <reified T> list() = cast() ?: listOf<T>()

            /**
             * Sets the current [Field] value.
             * @param any the value to set.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun set(any: Any?) = field?.set(instance, any)

            /**
             * Sets the current [Field] value to true.
             *
             * - Ensure that the instance type is [Boolean].
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun setTrue() = set(true)

            /**
             * Sets the current [Field] value to false.
             *
             * - Ensure that the instance type is [Boolean].
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun setFalse() = set(false)

            /** Sets the current [Field] value to null. */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun setNull() = set(null)

            override fun toString() =
                "[${self?.javaClass?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}] value \"$self\""
        }
    }
}