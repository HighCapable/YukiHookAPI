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
@file:Suppress("DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.classes.rules

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import java.lang.reflect.Field

/**
 * Defines conditions used to find a [Field].
 * @param rulesData the current finder rule data.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class FieldRules internal constructor(private val rulesData: FieldRulesData) : BaseRules() {

    /**
     * Sets the [Field] name.
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
     * - Accepts [Class], [String], or [VariousClass].
     *
     * - The type is optional.
     * @return [Any] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var type
        get() = rulesData.type
        set(value) {
            rulesData.type = value?.compat(tag = "Field")
        }

    /**
     * Sets the [Field] modifier conditions.
     *
     * - This condition is optional.
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /**
     * Sets the [Field] name condition.
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun name(conditions: NameConditions) {
        rulesData.nameConditions = conditions
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
     * @param conditions the condition block.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun type(conditions: ObjectConditions) {
        rulesData.typeConditions = conditions
    }

    /**
     * Builds the rule result.
     * @return [MemberRulesResult]
     */
    internal fun build() = MemberRulesResult(rulesData)
}