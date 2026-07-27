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
 * This file is created by fankes on 2022/9/14.
 */
@file:Suppress("unused", "DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import java.lang.reflect.Member

/**
 * Defines count and index conditions for matching [Class] and [Member] collections.
 *
 * Allows more precise matching of [Class] and [Member] objects obfuscated by R8.
 * @param instance the current count value.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class CountRules private constructor(private val instance: Int) {

    internal companion object {

        /**
         * Creates a [CountRules] instance.
         * @param instance the count value.
         * @return [CountRules]
         */
        internal fun with(instance: Int) = CountRules(instance)
    }

    /**
     * Checks whether the value is zero.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun Int.isZero() = this == 0

    /**
     * Checks whether the value is greater than [count].
     * @param count the target count.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun Int.moreThan(count: Int) = this > count

    /**
     * Checks whether the value is less than [count].
     * @param count the target count.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun Int.lessThan(count: Int) = this < count

    /**
     * Checks whether the value is within [countRange], where A ≤ this ≤ B.
     * @param countRange the accepted range.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun Int.inInterval(countRange: IntRange) = this in countRange

    override fun toString() = "CountRules [$instance]"
}