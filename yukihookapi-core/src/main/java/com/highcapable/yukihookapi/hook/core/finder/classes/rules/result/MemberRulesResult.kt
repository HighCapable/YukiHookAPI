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
 * This file is created by fankes on 2022/9/12.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.core.finder.classes.rules.result

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import java.lang.reflect.Member

/**
 * Configures match-count constraints for the current [Member] finder rules.
 * @param rulesData the current finder rule data.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class MemberRulesResult internal constructor(private val rulesData: MemberRulesData) {

    /**
     * Requires zero matching [Member] instances.
     * @return [MemberRulesResult] this result for chaining.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun none() = count(num = 0)

    /**
     * Requires the specified number of matching [Member] instances.
     * @param num the required count.
     * @return [MemberRulesResult] this result for chaining.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun count(num: Int): MemberRulesResult {
        rulesData.matchCount = num
        return this
    }

    /**
     * Requires a range of matching [Member] instances.
     *
     * Example:
     *
     * ```kotlin
     * count(1..5)
     * ```
     * @param numRange the accepted count range.
     * @return [MemberRulesResult] this result for chaining.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun count(numRange: IntRange): MemberRulesResult {
        rulesData.matchCountRange = numRange
        return this
    }

    /**
     * Requires the matching [Member] count to satisfy a condition.
     *
     * Example:
     *
     * ```kotlin
     * count { it >= 5 || it.isZero() }
     * ```
     * @param conditions the count condition.
     * @return [MemberRulesResult] this result for chaining.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun count(conditions: CountConditions): MemberRulesResult {
        rulesData.matchCountConditions = conditions
        return this
    }
}