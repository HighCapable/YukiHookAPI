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
 * This file is Created by fankes on 2022/9/4.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import java.lang.reflect.Member
import kotlin.math.abs

/**
 * 这是 [Class] 与 [Member] 查找类功能的基本类实现
 */
abstract class BaseFinder {

    /**
     * 字节码、数组下标筛选数据类型
     */
    @PublishedApi
    internal enum class IndexConfigType { ORDER, MATCH }

    /** 字节码、数组顺序下标 */
    internal var orderIndex: Pair<Int, Boolean>? = null

    /** 字节码、数组筛选下标 */
    internal var matchIndex: Pair<Int, Boolean>? = null

    /**
     * 字节码、数组下标筛选实现类
     * @param type 类型
     */
    inner class IndexTypeCondition @PublishedApi internal constructor(private val type: IndexConfigType) {

        /**
         * 设置下标
         *
         * 若 index 小于零则为倒序 - 此时可以使用 [IndexTypeConditionSort.reverse] 方法实现
         *
         * 可使用 [IndexTypeConditionSort.first] 和 [IndexTypeConditionSort.last] 设置首位和末位筛选条件
         * @param num 下标
         */
        fun index(num: Int) = when (type) {
            IndexConfigType.ORDER -> orderIndex = Pair(num, true)
            IndexConfigType.MATCH -> matchIndex = Pair(num, true)
        }

        /**
         * 得到下标
         * @return [IndexTypeConditionSort]
         */
        fun index() = IndexTypeConditionSort()

        /**
         * 字节码、数组下标排序实现类
         *
         * - ❗请使用 [index] 方法来获取 [IndexTypeConditionSort]
         */
        inner class IndexTypeConditionSort internal constructor() {

            /** 设置满足条件的第一个*/
            fun first() = index(num = 0)

            /** 设置满足条件的最后一个*/
            fun last() = when (type) {
                IndexConfigType.ORDER -> orderIndex = Pair(0, false)
                IndexConfigType.MATCH -> matchIndex = Pair(0, false)
            }

            /**
             * 设置倒序下标
             * @param num 下标
             */
            fun reverse(num: Int) = when {
                num < 0 -> index(abs(num))
                num == 0 -> index().last()
                else -> index(-num)
            }
        }
    }

    /**
     * 将目标类型转换为可识别的兼容类型
     * @param tag 当前查找类的标识
     * @param loader 使用的 [ClassLoader]
     * @return [Class] or null
     */
    internal fun Any?.compat(tag: String, loader: ClassLoader?) = when (this) {
        null -> null
        is Class<*> -> this
        is String -> runCatching { classOf(name = this, loader) }.getOrNull() ?: UndefinedType
        is VariousClass -> runCatching { get(loader) }.getOrNull() ?: UndefinedType
        else -> error("$tag match type \"$javaClass\" not allowed")
    } as Class<*>?

    /**
     * 返回结果实现类
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [BaseResult]
     */
    @YukiPrivateApi
    abstract fun build(): BaseResult

    /**
     * 返回只有异常的结果实现类
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [BaseResult]
     */
    @YukiPrivateApi
    abstract fun failure(throwable: Throwable?): BaseResult

    /**
     * 查找结果实现、处理类接口
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动继承此接口
     */
    @YukiPrivateApi
    interface BaseResult
}