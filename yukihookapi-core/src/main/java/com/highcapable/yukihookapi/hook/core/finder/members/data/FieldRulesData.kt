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

import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import java.lang.reflect.Field

/**
 * [Field] 规则查找数据类
 * @param name 名称
 * @param nameConditions 名称规则
 * @param type 类型
 * @param typeConditions 类型条件
 */
internal class FieldRulesData internal constructor(
    var name: String = "",
    var nameConditions: NameConditions? = null,
    var type: Any? = null,
    var typeConditions: ObjectConditions? = null
) : MemberRulesData() {

    override val templates
        get() = arrayOf(
            name.takeIf { it.isNotBlank() }?.let { "name:[$it]" } ?: "",
            nameConditions?.let { "nameConditions:[existed]" } ?: "",
            type?.let { "type:[$it]" } ?: "",
            typeConditions?.let { "typeConditions:[existed]" } ?: "", *super.templates
        )

    override val objectName get() = "Field"

    override val isInitialize
        get() = super.isInitializeOfSuper || name.isNotBlank() || nameConditions != null || type != null || typeConditions != null

    override fun toString() = "[$name][$nameConditions][$type][$typeConditions]" + super.toString()
}