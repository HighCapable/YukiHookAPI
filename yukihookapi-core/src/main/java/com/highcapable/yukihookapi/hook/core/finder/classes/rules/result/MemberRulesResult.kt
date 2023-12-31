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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.finder.classes.rules.result

import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import java.lang.reflect.Member

/**
 * 当前 [Member] 查找条件结果实现类
 * @param rulesData 当前查找条件规则数据
 */
class MemberRulesResult internal constructor(private val rulesData: MemberRulesData) {

    /**
     * 设置当前 [Member] 在查找条件中个数为 0
     * @return [MemberRulesResult] 可继续向下监听
     */
    fun none() = count(num = 0)

    /**
     * 设置当前 [Member] 在查找条件中需要全部匹配的个数
     * @param num 个数
     * @return [MemberRulesResult] 可继续向下监听
     */
    fun count(num: Int): MemberRulesResult {
        rulesData.matchCount = num
        return this
    }

    /**
     * 设置当前 [Member] 在查找条件中需要全部匹配的个数范围
     *
     * 使用示例如下 ↓
     *
     * ```kotlin
     * count(1..5)
     * ```
     * @param numRange 个数范围
     * @return [MemberRulesResult] 可继续向下监听
     */
    fun count(numRange: IntRange): MemberRulesResult {
        rulesData.matchCountRange = numRange
        return this
    }

    /**
     * 设置当前 [Member] 在查找条件中需要全部匹配的个数条件
     *
     * 使用示例如下 ↓
     *
     * ```kotlin
     * count { it >= 5 || it.isZero() }
     * ```
     * @param conditions 条件方法体
     * @return [MemberRulesResult] 可继续向下监听
     */
    fun count(conditions: CountConditions): MemberRulesResult {
        rulesData.matchCountConditions = conditions
        return this
    }
}