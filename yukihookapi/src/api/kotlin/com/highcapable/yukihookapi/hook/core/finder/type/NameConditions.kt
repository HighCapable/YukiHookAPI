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

import com.highcapable.yukihookapi.hook.utils.IntConditions
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是一个模糊 [Class]、[Member] 名称匹配实现类
 *
 * 可对 R8 混淆后的 [Class]、[Member] 进行更加详细的定位
 */
class NameConditions @PublishedApi internal constructor() {

    /** 完全字符匹配条件数组 */
    private var cdsEqualsOfs = ArrayList<Pair<String, Boolean>>()

    /** 起始字符匹配条件数组 */
    private var cdsStartsWiths = ArrayList<Triple<String, Int, Boolean>>()

    /** 结束字符匹配条件数组 */
    private var cdsEndsWiths = ArrayList<Pair<String, Boolean>>()

    /** 包含字符匹配条件数组 */
    private var cdsContains = ArrayList<Pair<String, Boolean>>()

    /** 正则字符匹配条件数组 */
    private var cdsMatches = ArrayList<Regex>()

    /** 字符长度匹配条件 */
    private var cdsLength = -1

    /** 字符长度范围匹配条件 */
    private var cdsLengthRange = IntRange.EMPTY

    /** 字符长度条件匹配条件 */
    private var cdsLengthConditions: IntConditions? = null

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
     * 例如匹配 "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * equalsOf(other = "catMonitor")
     * ```
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     *
     * 例如匹配 "cargoSale" or "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * name {
     *     equalsOf(other = "cargoSale")
     *     equalsOf(other = "catMonitor")
     * }
     * ```
     * @param other 字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun equalsOf(other: String, isIgnoreCase: Boolean = false) {
        cdsEqualsOfs.add(Pair(other, isIgnoreCase))
    }

    /**
     * 起始字符匹配
     *
     * 例如匹配 "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * startsWith(prefix = "cat")
     * ```
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     *
     * 例如匹配 "cargoSale" or "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * name {
     *     startsWith(prefix = "cargo")
     *     startsWith(prefix = "cat")
     * }
     * ```
     * @param prefix 起始字符匹配
     * @param startIndex 起始字符下标 - 默认从 0 开始
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun startsWith(prefix: String, startIndex: Int = 0, isIgnoreCase: Boolean = false) {
        cdsStartsWiths.add(Triple(prefix, startIndex, isIgnoreCase))
    }

    /**
     * 结束字符匹配
     *
     * 例如匹配 "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * endsWith(suffix = "Monitor")
     * ```
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     *
     * 例如匹配 "cargoSale" or "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * name {
     *     endsWith(suffix = "Sale")
     *     endsWith(suffix = "Monitor")
     * }
     * ```
     * @param suffix 结束字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun endsWith(suffix: String, isIgnoreCase: Boolean = false) {
        cdsEndsWiths.add(Pair(suffix, isIgnoreCase))
    }

    /**
     * 包含字符匹配
     *
     * 例如匹配 catMonitor 可设置为 ↓
     *
     * ```kotlin
     * contains(other = "atMoni")
     * ```
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     *
     * 例如匹配 "cargoSale" or "catMonitor" 可设置为 ↓
     *
     * ```kotlin
     * name {
     *     contains(other = "goSal")
     *     contains(other = "atMoni")
     * }
     * ```
     * @param other 包含字符匹配
     * @param isIgnoreCase 是否忽略字符中的大小写 - 默认否
     */
    fun contains(other: String, isIgnoreCase: Boolean = false) {
        cdsContains.add(Pair(other, isIgnoreCase))
    }

    /**
     * 正则字符匹配
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     * @param regex 正则字符
     */
    fun matches(regex: String) {
        cdsMatches.add(regex.toRegex())
    }

    /**
     * 正则字符匹配
     *
     * - 可以重复使用 - 最终会选择完全匹配的一个
     * @param regex 正则字符
     */
    fun matches(regex: Regex) {
        cdsMatches.add(regex)
    }

    /**
     * 字符长度匹配
     *
     * - 不可重复使用 - 重复使用旧的条件会被当前条件替换
     * @param num 预期的长度
     */
    fun length(num: Int) {
        cdsLength = num
    }

    /**
     * 字符长度范围匹配
     *
     * - 不可重复使用 - 重复使用旧的条件会被当前条件替换
     * @param numRange 预期的长度范围
     */
    fun length(numRange: IntRange) {
        cdsLengthRange = numRange
    }

    /**
     * 字符长度条件匹配
     *
     * - 不可重复使用 - 重复使用旧的条件会被当前条件替换
     * @param conditions 条件方法体
     */
    fun length(conditions: IntConditions) {
        cdsLengthConditions = conditions
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
     * 对比 [Class]、[Member] 名称是否符合条件
     * @param symbolName 符号名称 - 可以使用 [Class.getName]、[Class.getSimpleName]、[Field.getName]、[Method.getName] 获取
     * @return [Boolean] 是否符合条件
     */
    @PublishedApi
    internal fun contains(symbolName: String): Boolean {
        var conditions = true
        if (isThisSynthetic0) conditions = conditions && symbolName == "this$0"
        if (isOnlySymbols) conditions = conditions && symbolName.matches("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-_+=<>]+".toRegex())
        if (isOnlyLetters) conditions = conditions && symbolName.matches("[a-zA-Z]+".toRegex())
        if (isOnlyNumbers) conditions = conditions && symbolName.matches("[0-9]+".toRegex())
        if (isOnlyLettersNumbers) conditions = conditions && symbolName.matches("[a-zA-Z0-9]+".toRegex())
        if (isOnlyLowercase) conditions = conditions && symbolName.matches("[a-z]+".toRegex())
        if (isOnlyUppercase) conditions = conditions && symbolName.matches("[A-Z]+".toRegex())
        cdsEqualsOfs.takeIf { it.isNotEmpty() }?.also { conditions = conditions && it.any { e -> symbolName.equals(e.first, e.second) } }
        cdsStartsWiths.takeIf { it.isNotEmpty() }
            ?.also { conditions = conditions && it.any { e -> symbolName.startsWith(e.first, e.second, e.third) } }
        cdsEndsWiths.takeIf { it.isNotEmpty() }?.also { conditions = conditions && it.any { e -> symbolName.endsWith(e.first, e.second) } }
        cdsContains.takeIf { it.isNotEmpty() }?.also { conditions = conditions && it.any { e -> symbolName.contains(e.first, e.second) } }
        cdsMatches.takeIf { it.isNotEmpty() }?.also { conditions = conditions && it.any { e -> symbolName.matches(e) } }
        cdsLength.takeIf { it >= 0 }?.also { conditions = conditions && symbolName.length == it }
        cdsLengthRange.takeIf { it.isEmpty().not() }?.also { conditions = conditions && symbolName.length in it }
        cdsLengthConditions?.also { conditions = conditions && it(symbolName.length) }
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
        if (cdsEqualsOfs.isNotEmpty()) conditions += "<EqualsOf:$cdsEqualsOfs> "
        if (cdsStartsWiths.isNotEmpty()) conditions += "<StartsWith:$cdsStartsWiths> "
        if (cdsEndsWiths.isNotEmpty()) conditions += "<EndsWith:$cdsEndsWiths> "
        if (cdsContains.isNotEmpty()) conditions += "<Contains:$cdsContains> "
        if (cdsMatches.isNotEmpty()) conditions += "<Matches:$cdsMatches> "
        if (cdsLength >= 0) conditions += "<Length:[$cdsLength]> "
        if (cdsLengthRange.isEmpty().not()) conditions += "<LengthRange:[$cdsLengthRange]> "
        if (cdsLengthConditions != null) conditions += "<LengthConditions:[existed]> "
        return "[${conditions.trim()}]"
    }
}