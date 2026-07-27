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
 * This file is created by fankes on 2022/9/4.
 */
@file:Suppress("DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import java.lang.reflect.Member
import kotlin.math.abs

/**
 * Base implementation for [Class] and [Member] finders.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
abstract class BaseFinder {

    /** The current finder rule data. */
    internal abstract val rulesData: BaseRulesData

    /**
     * Bytecode and array index filter type.
     */
    internal enum class IndexConfigType { ORDER, MATCH }

    /**
     * Bytecode and array index filter implementation.
     * @param type the filter type.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class IndexTypeCondition internal constructor(private val type: IndexConfigType) {

        /**
         * Sets the index.
         *
         * A negative index uses reverse order. This can be configured through [IndexTypeConditionSort.reverse].
         *
         * Use [IndexTypeConditionSort.first] and [IndexTypeConditionSort.last] to select the first or last match.
         * @param num the index.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun index(num: Int) = when (type) {
            IndexConfigType.ORDER -> rulesData.orderIndex = Pair(num, true)
            IndexConfigType.MATCH -> rulesData.matchIndex = Pair(num, true)
        }

        /**
         * Gets the index configuration.
         * @return [IndexTypeConditionSort]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun index() = IndexTypeConditionSort()

        /**
         * Bytecode and array index ordering implementation.
         *
         * - Use [index] to obtain [IndexTypeConditionSort].
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inner class IndexTypeConditionSort internal constructor() {

            /** Selects the first matching item. */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun first() = index(num = 0)

            /** Selects the last matching item. */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun last() = when (type) {
                IndexConfigType.ORDER -> rulesData.orderIndex = Pair(0, false)
                IndexConfigType.MATCH -> rulesData.matchIndex = Pair(0, false)
            }

            /**
             * Sets an index in reverse order.
             * @param num the index.
             */
            @Deprecated(ReflectionMigration.KAVAREF_INFO)
            fun reverse(num: Int) = when {
                num < 0 -> index(abs(num))
                num == 0 -> index().last()
                else -> index(-num)
            }
        }
    }

    /**
     * Converts the target type to a supported compatible type.
     * @param tag the current finder identifier.
     * @param loader the [ClassLoader] to use.
     * @return [Class] or null.
     */
    internal fun Any?.compat(tag: String, loader: ClassLoader?) = when (this) {
        null -> null
        is Class<*> -> this
        is String -> runCatching { toClass(loader) }.getOrNull() ?: UndefinedType
        is VariousClass -> runCatching { get(loader) }.getOrNull() ?: UndefinedType
        else -> error("$tag match type \"$javaClass\" not allowed")
    }

    /**
     * Builds the result implementation.
     *
     * - This operation is performed automatically by the block and should not be called manually.
     * @return [BaseResult]
     */
    internal abstract fun build(): BaseResult

    /**
     * Builds a result implementation that contains only an exception.
     *
     * - This operation is performed automatically by the block and should not be called manually.
     * @param throwable the exception.
     * @return [BaseResult]
     */
    internal abstract fun failure(throwable: Throwable?): BaseResult

    /**
     * Finder result implementation and processing interface.
     *
     * - This interface is implemented automatically by the block and should not be implemented manually.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    interface BaseResult
}