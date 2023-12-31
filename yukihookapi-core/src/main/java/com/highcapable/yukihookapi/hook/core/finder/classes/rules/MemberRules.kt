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

import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import java.lang.reflect.Member

/**
 * [Member] 查找条件实现类
 * @param rulesData 当前查找条件规则数据
 */
class MemberRules internal constructor(private val rulesData: MemberRulesData) : BaseRules() {

    /**
     * 设置 [Member] 标识符筛选条件
     *
     * - 可不设置筛选条件
     * @param conditions 条件方法体
     */
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /**
     * 返回结果实现类
     * @return [MemberRulesResult]
     */
    internal fun build() = MemberRulesResult(rulesData)
}