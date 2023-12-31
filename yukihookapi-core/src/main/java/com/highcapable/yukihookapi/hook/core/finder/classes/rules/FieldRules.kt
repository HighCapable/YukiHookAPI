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
 * This file is created by fankes on 2022/9/12.
 */
package com.highcapable.yukihookapi.hook.core.finder.classes.rules

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import java.lang.reflect.Field

/**
 * [Field] 查找条件实现类
 * @param rulesData 当前查找条件规则数据
 */
class FieldRules internal constructor(private val rulesData: FieldRulesData) : BaseRules() {

    /**
     * 设置 [Field] 名称
     * @return [String]
     */
    var name
        get() = rulesData.name
        set(value) {
            rulesData.name = value
        }

    /**
     * 设置 [Field] 类型
     *
     * - 只能是 [Class]、[String]、[VariousClass]
     *
     * - 可不填写类型
     * @return [Any] or null
     */
    var type
        get() = rulesData.type
        set(value) {
            rulesData.type = value?.compat(tag = "Field")
        }

    /**
     * 设置 [Field] 标识符筛选条件
     *
     * - 可不设置筛选条件
     * @param conditions 条件方法体
     */
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /**
     * 设置 [Field] 名称条件
     * @param conditions 条件方法体
     */
    fun name(conditions: NameConditions) {
        rulesData.nameConditions = conditions
    }

    /**
     * 设置 [Field] 类型条件
     *
     * - 可不填写类型
     *
     * 使用示例如下 ↓
     *
     * ```kotlin
     * type { it == StringClass || it.name == "java.lang.String" }
     * ```
     * @param conditions 条件方法体
     */
    fun type(conditions: ObjectConditions) {
        rulesData.typeConditions = conditions
    }

    /**
     * 返回结果实现类
     * @return [MemberRulesResult]
     */
    internal fun build() = MemberRulesResult(rulesData)
}