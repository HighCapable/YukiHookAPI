/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
package com.highcapable.yukihookapi.hook.core.finder.members.data

import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import java.lang.reflect.Constructor

/**
 * [Constructor] 规则查找数据类
 * @param paramTypes 参数类型数组
 * @param paramTypesConditions 参数类型条件
 * @param paramCount 参数个数
 * @param paramCountRange 参数个数范围
 * @param paramCountConditions 参数个数条件
 */
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