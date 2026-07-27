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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.classes.rules

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import java.lang.reflect.Method

/**
 * [Method] finder condition implementation.
 * @param rulesData the current finder rule data.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class MethodRules internal constructor(private val rulesData: MethodRulesData) : BaseRules() {

    /**
     * Sets the [Method] name.
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
            rulesData.returnType = value.compat(tag = "Method")
        }

    /**
     * Sets the [Method] modifier conditions.
     *
     * - The conditions are optional.
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /** Configures an empty, parameterless [Method]. */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun emptyParam() {
        rulesData.paramCount = 0
    }

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
     * @param paramType the parameter type array. Entries must be [Class], [String], or [VariousClass].
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(vararg paramType: Any) {
        if (paramType.isEmpty()) error("paramTypes is empty, please use emptyParam() instead")
        rulesData.paramTypes =
            mutableListOf<Class<*>>().apply { paramType.forEach { add(it.compat(tag = "Method") ?: UndefinedType) } }.toTypedArray()
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun param(conditions: ObjectsConditions) {
        rulesData.paramTypesConditions = conditions
    }

    /**
     * Sets the [Method] name conditions.
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun name(conditions: NameConditions) {
        rulesData.nameConditions = conditions
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
     * @param numRange the count range.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(numRange: IntRange) {
        rulesData.paramCountRange = numRange
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun paramCount(conditions: CountConditions) {
        rulesData.paramCountConditions = conditions
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun returnType(conditions: ObjectConditions) {
        rulesData.returnTypeConditions = conditions
    }

    /**
     * Builds the result implementation.
     * @return [MemberRulesResult]
     */
    internal fun build() = MemberRulesResult(rulesData)
}