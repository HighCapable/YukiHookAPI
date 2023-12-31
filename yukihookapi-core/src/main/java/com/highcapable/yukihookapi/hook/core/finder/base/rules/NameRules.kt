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
 * This file is created by fankes on 2022/5/16.
 * This file is modified by fankes on 2022/9/14.
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

    internal companion object {

        /**
         * 创建实例
         * @param instance 实例对象
         * @return [NameRules]
         */
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