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
 * This file is Created by fankes on 2022/9/12.
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
class FieldRules internal constructor(@PublishedApi internal val rulesData: FieldRulesData) : BaseRules() {

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
     * - ❗只能是 [Class]、[String]、[VariousClass]
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
    @PublishedApi
    internal fun build() = MemberRulesResult(rulesData)
}