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
@file:Suppress("MemberVisibilityCanBePrivate", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.classes.rules

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import java.lang.reflect.Constructor

/**
 * [Constructor] finder condition implementation.
 * @param rulesData the current finder rule data.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class ConstructorRules internal constructor(private val rulesData: ConstructorRulesData) : BaseRules() {

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
     * - The conditions are optional.
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /** Configures an empty, parameterless [Constructor]. */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun emptyParam() {
        rulesData.paramCount = 0
    }

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
     * @param paramType the parameter type array. Entries must be [Class], [String], or [VariousClass].
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(vararg paramType: Any) {
        if (paramType.isEmpty()) error("paramTypes is empty, please use emptyParam() instead")
        rulesData.paramTypes =
            mutableListOf<Class<*>>().apply { paramType.forEach { add(it.compat(tag = "Constructor") ?: UndefinedType) } }.toTypedArray()
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(conditions: ObjectsConditions) {
        rulesData.paramTypesConditions = conditions
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
     * @param numRange the count range.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(numRange: IntRange) {
        rulesData.paramCountRange = numRange
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(conditions: CountConditions) {
        rulesData.paramCountConditions = conditions
    }

    /**
     * Builds the result implementation.
     * @return [MemberRulesResult]
     */
    internal fun build() = MemberRulesResult(rulesData)
}