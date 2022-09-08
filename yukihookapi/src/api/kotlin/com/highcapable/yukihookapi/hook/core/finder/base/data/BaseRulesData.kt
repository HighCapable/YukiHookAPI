/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
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
 * This file is Created by fankes on 2022/9/8.
 */
package com.highcapable.yukihookapi.hook.core.finder.base.data

import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import java.lang.reflect.Member

/**
 * 这是 [Class] 与 [Member] 规则查询数据基本类实现
 * @param modifiers 描述符
 * @param orderIndex 字节码、数组顺序下标
 * @param matchIndex 字节码、数组筛选下标
 */
@PublishedApi
internal abstract class BaseRulesData internal constructor(
    var modifiers: ModifierRules? = null,
    var orderIndex: Pair<Int, Boolean>? = null,
    var matchIndex: Pair<Int, Boolean>? = null
) {

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

    /**
     * 通过规则数据 [toString] 来得到一个 [Any.hashCode]
     * @param other 额外的数据 - 可选
     * @return [Int]
     */
    internal open fun hashCode(other: Any? = null) = "[$other][$modifiers][$orderIndex][$matchIndex]".hashCode()
}