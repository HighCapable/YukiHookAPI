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
@file:Suppress("DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.members.data

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import java.lang.reflect.Member

/**
 * Stores rules used to find a [Member].
 * @param isFindInSuper whether to continue searching in superclasses after no match is found.
 * @param matchCount the number of matching bytecode instructions.
 * @param matchCountRange the accepted range of matching bytecode instructions.
 * @param matchCountConditions the matching bytecode-count conditions.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal open class MemberRulesData internal constructor(
    var isFindInSuper: Boolean = false,
    var matchCount: Int = -1,
    var matchCountRange: IntRange = IntRange.EMPTY,
    var matchCountConditions: CountConditions? = null
) : BaseRulesData() {

    override val templates
        get() = arrayOf(
            modifiers?.let { "modifiers:${ModifierRules.templates(uniqueValue)}" } ?: "",
            orderIndex?.let { it.takeIf { it.second }?.let { e -> "orderIndex:[${e.first}]" } ?: "orderIndex:[last]" } ?: "",
            matchIndex?.let { it.takeIf { it.second }?.let { e -> "matchIndex:[${e.first}]" } ?: "matchIndex:[last]" } ?: ""
        )

    override val objectName get() = "Member"

    /**
     * Gets whether any match-count rule has been initialized through [matchCount], [matchCountRange], or a condition.
     * @return [Boolean]
     */
    internal val isInitializeOfMatch get() = matchCount >= 0 || matchCountRange.isEmpty().not() || matchCountConditions != null

    /**
     * Gets whether any [BaseRulesData] rule has been initialized.
     * @return [Boolean]
     */
    internal val isInitializeOfSuper get() = super.isInitialize

    override val isInitialize get() = isInitializeOfSuper || isInitializeOfMatch

    override fun toString() = "[$isFindInSuper][$matchIndex][$matchCountRange][$matchCountConditions]" + super.toString()
}