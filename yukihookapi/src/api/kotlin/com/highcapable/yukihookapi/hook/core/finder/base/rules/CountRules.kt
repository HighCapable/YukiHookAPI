/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is Created by fankes on 2022/9/14.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import java.lang.reflect.Member

/**
 * 这是一个模糊 [Class]、[Member] 数组 (下标) 个数条件实现类
 *
 * 可对 R8 混淆后的 [Class]、[Member] 进行更加详细的定位
 * @param instance 当前实例对象
 */
class CountRules private constructor(private val instance: Int) {

    @PublishedApi
    internal companion object {

        /**
         * 创建实例
         * @param instance 实例对象
         * @return [CountRules]
         */
        @PublishedApi
        internal fun with(instance: Int) = CountRules(instance)
    }

    /**
     * 是否为 0
     * @return [Boolean]
     */
    fun Int.isZero() = this == 0

    /**
     * 大于 [count]
     * @param count 目标对象
     * @return [Boolean]
     */
    fun Int.moreThan(count: Int) = this > count

    /**
     * 小于 [count]
     * @param count 目标对象
     * @return [Boolean]
     */
    fun Int.lessThan(count: Int) = this < count

    /**
     * 在 [countRange] 区间 A ≤ this ≤ B
     * @param countRange 区间
     * @return [Boolean]
     */
    fun Int.inInterval(countRange: IntRange) = this in countRange

    override fun toString() = "CountRules [$instance]"
}