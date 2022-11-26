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
 * This file is Modified by fankes on 2022/9/14.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import java.lang.reflect.Member

/**
 * 这是一个模糊 [Class]、[Member] 名称条件实现类
 *
 * 可对 R8 混淆后的 [Class]、[Member] 进行更加详细的定位
 * @param instance 当前实例对象
 */
class NameRules private constructor(private val instance: String) {

    @PublishedApi
    internal companion object {

        /**
         * 创建实例
         * @param instance 实例对象
         * @return [NameRules]
         */
        @PublishedApi
        internal fun with(instance: String) = NameRules(instance)
    }

    /**
     * 是否为匿名类的主类调用对象名称
     *
     * 它的名称形态通常为：this$[index]
     * @param index 下标 - 默认 0
     * @return [Boolean]
     */
    fun String.isSynthetic(index: Int = 0) = this == "this$$index"

    /**
     * 是否只有符号
     *
     * 筛选仅包含 _、-、?、!、,、.、<、> 等符号以及特殊符号
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlySymbols() = matches("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-_+=<>]+".toRegex())

    /**
     * 是否只有字母
     *
     * 在没有 [isOnlyLowercase] 以及 [isOnlyUppercase] 的条件下筛选仅包含 26 个大小写英文字母
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlyLetters() = matches("[a-zA-Z]+".toRegex())

    /**
     * 是否只有数字
     *
     * 筛选仅包含 0-9 阿拉伯数字
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlyNumbers() = matches("\\d+".toRegex())

    /**
     * 是否只有字母或数字
     *
     * 融合条件 [isOnlyLetters] 和 [isOnlyNumbers]
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlyLettersNumbers() = matches("[a-zA-Z\\d]+".toRegex())

    /**
     * 是否只有小写字母
     *
     * 在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlyLowercase() = matches("[a-z]+".toRegex())

    /**
     * 是否只有大写字母
     *
     * 在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符
     *
     * 你可以使用 [matches] 方法进行更详细的正则匹配
     * @return [Boolean]
     */
    fun String.isOnlyUppercase() = matches("[A-Z]+".toRegex())

    override fun toString() = "NameRules [$instance]"
}