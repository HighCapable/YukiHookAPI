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