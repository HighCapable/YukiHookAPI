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

import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import java.lang.reflect.Member

/**
 * [Member] 规则查找数据类
 * @param isFindInSuper 是否在未找到后继续在父类中查找
 * @param matchCount 匹配的字节码个数
 * @param matchCountRange 匹配的字节码个数范围
 * @param matchCountConditions 匹配的字节码个数条件
 */
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
     * 判断 [matchCount]、[matchCountRange] 规则是否已经初始化 (设置了任意一个参数)
     * @return [Boolean]
     */
    internal val isInitializeOfMatch get() = matchCount >= 0 || matchCountRange.isEmpty().not() || matchCountConditions != null

    /**
     * 判断 [BaseRulesData] 规则是否已经初始化 (设置了任意一个参数)
     * @return [Boolean]
     */
    internal val isInitializeOfSuper get() = super.isInitialize

    override val isInitialize get() = isInitializeOfSuper || isInitializeOfMatch

    override fun toString() = "[$isFindInSuper][$matchIndex][$matchCountRange][$matchCountConditions]" + super.toString()
}