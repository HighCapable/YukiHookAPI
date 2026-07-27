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
 * This file is created by fankes on 2022/9/8.
 */
@file:Suppress("DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.base.data

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.rules.CountRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.NameRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ObjectRules
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Base data model for [Class] and [Member] finder rules.
 * @param modifiers the modifier conditions.
 * @param orderIndex the bytecode or collection order index.
 * @param matchIndex the bytecode or collection match index.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal abstract class BaseRulesData internal constructor(
    var modifiers: ModifierConditions? = null,
    var orderIndex: Pair<Int, Boolean>? = null,
    var matchIndex: Pair<Int, Boolean>? = null
) {

    /** Unique identifier for this rule instance. */
    internal var uniqueValue = 0L

    init {
        uniqueValue = System.currentTimeMillis()
    }

    /**
     * Converts [String] to [NameRules].
     * @return [NameRules]
     */
    internal fun String.cast() = NameRules.with(this)

    /**
     * Converts [Int] to [CountRules].
     * @return [CountRules]
     */
    internal fun Int.cast() = CountRules.with(this)

    /**
     * Converts [Class] to [ModifierRules].
     * @return [ModifierRules]
     */
    internal fun Class<*>.cast() = ModifierRules.with(instance = this, uniqueValue)

    /**
     * Converts [Member] to [ModifierRules].
     * @return [ModifierRules]
     */
    internal fun Member.cast() = ModifierRules.with(instance = this, uniqueValue)

    /**
     * Converts [Field.getType] to [ObjectRules].
     * @return [ObjectRules]
     */
    internal fun Field.type() = ObjectRules.with(type)

    /**
     * Converts [Method.getParameterTypes] to [ObjectRules].
     * @return [ObjectRules]
     */
    internal fun Method.paramTypes() = ObjectRules.with(parameterTypes)

    /**
     * Converts [Method.getReturnType] to [ObjectRules].
     * @return [ObjectRules]
     */
    internal fun Method.returnType() = ObjectRules.with(returnType)

    /**
     * Converts [Constructor.getParameterTypes] to [ObjectRules].
     * @return [ObjectRules]
     */
    internal fun Constructor<*>.paramTypes() = ObjectRules.with(parameterTypes)

    /**
     * Formats parameter types as text.
     * @return [String]
     */
    internal fun Array<out Class<*>>?.typeOfString() =
        StringBuilder("(").also { sb ->
            var isFirst = true
            if (this == null || isEmpty()) return "()"
            forEach {
                if (isFirst) isFirst = false else sb.append(", ")
                sb.append(it.takeIf { it.canonicalName != VagueType.canonicalName }?.canonicalName ?: "*vague*")
            }
            sb.append(")")
        }.toString()

    /**
     * Gets the rule template strings.
     * @return [Array]<[String]>
     */
    internal abstract val templates: Array<String>

    /**
     * Gets the rule object name.
     * @return [String]
     */
    internal abstract val objectName: String

    /**
     * Gets whether any rule parameter has been initialized.
     * @return [Boolean]
     */
    internal open val isInitialize get() = modifiers != null || orderIndex != null || matchIndex != null

    override fun toString() = "[$modifiers][$orderIndex][$matchIndex]"
}