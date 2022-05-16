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
 * This file is Created by fankes on 2022/5/16.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.finder.type

import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是一个模糊 [Member] 名称匹配实现类
 *
 * 可对 R8 混淆后的 [Member] 进行更加详细的定位
 */
class NameConditions @PublishedApi internal constructor() {

    /** 完全字符匹配条件 */
    private var cdsEqualsOf: Pair<String, Boolean>? = null

    /** 起始字符匹配条件 */
    private var cdsStartsWith: Triple<String, Int, Boolean>? = null

    /** 结束字符匹配条件 */
    private var cdsEndsWith: Pair<String, Boolean>? = null

    /** 包含字符匹配条件 */
    private var cdsContains: Pair<String, Boolean>? = null

    /** 正则字符匹配条件 */
    private var cdsMatches: Regex? = null

    /** 标识为匿名类的主类调用对象条件 */
    private var isThisSynthetic0 = false

    /** 标识为只有符号条件 */
    private var isOnlySymbols = false

    /** 标识为只有字母条件 */
    private var isOnlyLetters = false

    /** 标识为只有数字条件 */
    private var isOnlyNumbers = false

    /** 标识为只有字母或数字条件 */
    private var isOnlyLettersNumbers = false

    /** 标识为只有小写字母条件 */
    private var isOnlyLowercase = false

    /** 标识为只有大写字母条件 */
    private var isOnlyUppercase = false

    /**
     * 完全字符匹配
     *
     * 例如匹配 catMonitor 可设置为 equalsOf(other = "catMonitor")
     *
     * @param other 字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun equalsOf(other: String, isIgnoreCase: Boolean = false) {
        cdsEqualsOf = Pair(other, isIgnoreCase)
    }

    /**
     * 起始字符匹配
     *
     * 例如匹配 catMonitor 可设置为 startsWith(prefix = "cat")
     *
     * @param prefix 起始字符匹配
     * @param startIndex 起始字符下标 - 默认从 0 开始
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun startsWith(prefix: String, startIndex: Int = 0, isIgnoreCase: Boolean = false) {
        cdsStartsWith = Triple(prefix, startIndex, isIgnoreCase)
    }

    /**
     * 结束字符匹配
     *
     * 例如匹配 catMonitor 可设置为 endsWith(suffix = "Monitor")
     *
     * @param suffix 结束字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun endsWith(suffix: String, isIgnoreCase: Boolean = false) {
        cdsEndsWith = Pair(suffix, isIgnoreCase)
    }

    /**
     * 包含字符匹配
     *
     * 例如匹配 catMonitor 可设置为 contains(other = "atMoni")
     *
     * @param other 包含字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun contains(other: String, isIgnoreCase: Boolean = false) {
        cdsContains = Pair(other, isIgnoreCase)
    }

    /**
     * 正则字符匹配
     *
     * @param regex 正则字符
     */
    fun matches(regex: String) {
        cdsMatches = regex.toRegex()
    }

    /**
     * 正则字符匹配
     *
     * @param regex 正则字符
     */
    fun matches(regex: Regex) {
        cdsMatches = regex
    }

    /**
     * 标识为匿名类的主类调用对象
     *
     * 它的名称形态通常为：this$0
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun thisSynthetic0() {
        isThisSynthetic0 = true
    }

    /**
     * 标识为只有符号
     *
     * 筛选仅包含 _、-、?、!、,、.、<、> 等符号以及特殊符号
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlySymbols() {
        isOnlySymbols = true
    }

    /**
     * 标识为只有字母
     *
     * 在没有 [onlyLowercase] 以及 [onlyUppercase] 的条件下筛选仅包含 26 个大小写英文字母
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlyLetters() {
        isOnlyLetters = true
    }

    /**
     * 标识为只有数字
     *
     * 筛选仅包含 0-9 阿拉伯数字
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlyNumbers() {
        isOnlyNumbers = true
    }

    /**
     * 标识为只有字母或数字
     *
     * 融合条件 [onlyLetters] 和 [onlyNumbers]
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlyLettersNumbers() {
        isOnlyLettersNumbers = true
    }

    /**
     * 标识为只有小写字母
     *
     * 在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlyLowercase() {
        isOnlyLowercase = true
    }

    /**
     * 标识为只有大写字母
     *
     * 在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     */
    fun onlyUppercase() {
        isOnlyUppercase = true
    }

    /**
     * 对比 [Member] 类型是否符合条件
     * @param member 实例 - 只支持 [Method]、[Field]
     * @return [Boolean] 是否符合条件
     */
    @PublishedApi
    internal fun contains(member: Member): Boolean {
        var conditions = true
        when (member) {
            is Method -> member.name
            is Field -> member.name
            else -> ""
        }.also {
            if (isThisSynthetic0) conditions = conditions && it == "this$0"
            if (isOnlySymbols) conditions = conditions && it.matches("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-_+=<>]+".toRegex())
            if (isOnlyLetters) conditions = conditions && it.matches("[a-zA-Z]+".toRegex())
            if (isOnlyNumbers) conditions = conditions && it.matches("[0-9]+".toRegex())
            if (isOnlyLettersNumbers) conditions = conditions && it.matches("[a-zA-Z0-9]+".toRegex())
            if (isOnlyLowercase) conditions = conditions && it.matches("[a-z]+".toRegex())
            if (isOnlyUppercase) conditions = conditions && it.matches("[A-Z]+".toRegex())
            if (cdsEqualsOf != null) cdsEqualsOf?.apply { conditions = conditions && it.equals(first, second) }
            if (cdsStartsWith != null) cdsStartsWith?.apply { conditions = conditions && it.startsWith(first, second, third) }
            if (cdsEndsWith != null) cdsEndsWith?.apply { conditions = conditions && it.endsWith(first, second) }
            if (cdsContains != null) cdsContains?.apply { conditions = conditions && it.contains(first, second) }
            if (cdsMatches != null) cdsMatches?.apply { conditions = conditions && it.matches(regex = this) }
        }
        return conditions
    }

    override fun toString(): String {
        var conditions = ""
        if (isThisSynthetic0) conditions += "<ThisSynthetic0> "
        if (isOnlySymbols) conditions += "<OnlySymbols> "
        if (isOnlyLetters) conditions += "<OnlyLetters> "
        if (isOnlyNumbers) conditions += "<OnlyNumbers> "
        if (isOnlyLettersNumbers) conditions += "<OnlyLettersNumbers> "
        if (isOnlyLowercase) conditions += "<OnlyLowercase> "
        if (isOnlyUppercase) conditions += "<OnlyUppercase> "
        if (cdsEqualsOf != null) cdsEqualsOf?.apply { conditions += "<EqualsOf:[other: $first, isIgnoreCase: $second]> " }
        if (cdsStartsWith != null) cdsStartsWith?.apply { conditions += "<StartsWith:[prefix: $first, startIndex: $second, isIgnoreCase: $third]> " }
        if (cdsEndsWith != null) cdsEndsWith?.apply { conditions += "<EndsWith:[suffix: $first, isIgnoreCase: $second]> " }
        if (cdsContains != null) cdsContains?.apply { conditions += "<Contains:[other: $first, isIgnoreCase: $second]> " }
        if (cdsMatches != null) cdsMatches?.apply { conditions += "<Matches:[regex: $this]> " }
        return "[${conditions.trim()}]"
    }
}