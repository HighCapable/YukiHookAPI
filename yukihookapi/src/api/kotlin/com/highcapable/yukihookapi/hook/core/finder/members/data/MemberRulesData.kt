/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/9/4.
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
@PublishedApi
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

    override fun hashCode(other: Any?) = super.hashCode(other) + toString().hashCode()

    override fun toString() = "[$isFindInSuper][$matchIndex][$matchCountRange]"
}