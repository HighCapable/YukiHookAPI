/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
 * This file is created by fankes on 2022/9/8.
 */
package com.highcapable.yukihookapi.hook.core.finder.base.data

import com.highcapable.yukihookapi.hook.core.finder.base.rules.CountRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.NameRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ObjectRules
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是 [Class] 与 [Member] 规则查找数据基本类实现
 * @param modifiers 描述符条件
 * @param orderIndex 字节码、数组顺序下标
 * @param matchIndex 字节码、数组筛选下标
 */
internal abstract class BaseRulesData internal constructor(
    var modifiers: ModifierConditions? = null,
    var orderIndex: Pair<Int, Boolean>? = null,
    var matchIndex: Pair<Int, Boolean>? = null
) {

    /** 当前类唯一标识值 */
    internal var uniqueValue = 0L

    init {
        uniqueValue = System.currentTimeMillis()
    }

    /**
     * [String] 转换为 [NameRules]
     * @return [NameRules]
     */
    internal fun String.cast() = NameRules.with(this)

    /**
     * [Int] 转换为 [CountRules]
     * @return [CountRules]
     */
    internal fun Int.cast() = CountRules.with(this)

    /**
     * [Class] 转换为 [ModifierRules]
     * @return [ModifierRules]
     */
    internal fun Class<*>.cast() = ModifierRules.with(instance = this, uniqueValue)

    /**
     * [Member] 转换为 [ModifierRules]
     * @return [ModifierRules]
     */
    internal fun Member.cast() = ModifierRules.with(instance = this, uniqueValue)

    /**
     * [Field.getType] 转换为 [ObjectRules]
     * @return [ObjectRules]
     */
    internal fun Field.type() = ObjectRules.with(type)

    /**
     * [Method.getParameterTypes] 转换为 [ObjectRules]
     * @return [ObjectRules]
     */
    internal fun Method.paramTypes() = ObjectRules.with(parameterTypes)

    /**
     * [Method.getReturnType] 转换为 [ObjectRules]
     * @return [ObjectRules]
     */
    internal fun Method.returnType() = ObjectRules.with(returnType)

    /**
     * [Constructor.getParameterTypes] 转换为 [ObjectRules]
     * @return [ObjectRules]
     */
    internal fun Constructor<*>.paramTypes() = ObjectRules.with(parameterTypes)

    /**
     * 获取参数数组文本化内容
     * @return [String]
     */
    internal fun Array<out Class<*>>?.typeOfString() =
        StringBuilder("(").also { sb ->
            var isFirst = true
            if (this == null || isEmpty()) return "()"
            forEach {
                if (isFirst) isFirst = false else sb.append(", ")
                sb.append(it.takeIf { it.canonicalName != VagueType.canonicalName }?.canonicalName ?: "*vague*")
            }
            sb.append(")")
        }.toString()

    /**
     * 获取规则对象模板字符串数组
     * @return [Array]<[String]>
     */
    internal abstract val templates: Array<String>

    /**
     * 获取规则对象名称
     * @return [String]
     */
    internal abstract val objectName: String

    /**
     * 判断规则是否已经初始化 (设置了任意一个参数)
     * @return [Boolean]
     */
    internal open val isInitialize get() = modifiers != null || orderIndex != null || matchIndex != null

    override fun toString() = "[$modifiers][$orderIndex][$matchIndex]"
}