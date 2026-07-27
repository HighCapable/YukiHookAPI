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
@file:Suppress("TYPEALIAS_EXPANSION_DEPRECATION", "DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.members.data

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import java.lang.reflect.Constructor

/**
 * Stores rules used to find a [Constructor].
 * @param paramTypes the parameter types.
 * @param paramTypesConditions the parameter-type conditions.
 * @param paramCount the parameter count.
 * @param paramCountRange the accepted parameter-count range.
 * @param paramCountConditions the parameter-count conditions.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal class ConstructorRulesData internal constructor(
    var paramTypes: Array<out Class<*>>? = null,
    var paramTypesConditions: ObjectsConditions? = null,
    var paramCount: Int = -1,
    var paramCountRange: IntRange = IntRange.EMPTY,
    var paramCountConditions: CountConditions? = null
) : MemberRulesData() {

    override val templates
        get() = arrayOf(
            paramCount.takeIf { it >= 0 }?.let { "paramCount:[$it]" } ?: "",
            paramCountRange.takeIf { it.isEmpty().not() }?.let { "paramCountRange:[$it]" } ?: "",
            paramCountConditions?.let { "paramCountConditions:[existed]" } ?: "",
            paramTypes?.typeOfString()?.let { "paramTypes:[$it]" } ?: "",
            paramTypesConditions?.let { "paramTypesConditions:[existed]" } ?: "", *super.templates
        )

    override val objectName get() = "Constructor"

    override val isInitialize
        get() = super.isInitializeOfSuper || paramTypes != null || paramTypesConditions != null || paramCount >= 0 ||
            paramCountRange.isEmpty().not() || paramCountConditions != null

    override fun toString() = "[$paramTypes][$paramTypesConditions][$paramCount][$paramCountRange]" + super.toString()
}