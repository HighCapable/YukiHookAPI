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
 * This file is created by fankes on 2022/9/4.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import java.lang.reflect.Member
import kotlin.math.abs

/**
 * 这是 [Class] 与 [Member] 查找类功能的基本类实现
 */
abstract class BaseFinder {

    /** 当前查找条件规则数据 */
    internal abstract val rulesData: BaseRulesData

    /**
     * 字节码、数组下标筛选数据类型
     */
    internal enum class IndexConfigType { ORDER, MATCH }

    /**
     * 字节码、数组下标筛选实现类
     * @param type 类型
     */
    inner class IndexTypeCondition internal constructor(private val type: IndexConfigType) {

        /**
         * 设置下标
         *
         * 若 index 小于零则为倒序 - 此时可以使用 [IndexTypeConditionSort.reverse] 方法实现
         *
         * 可使用 [IndexTypeConditionSort.first] 和 [IndexTypeConditionSort.last] 设置首位和末位筛选条件
         * @param num 下标
         */
        fun index(num: Int) = when (type) {
            IndexConfigType.ORDER -> rulesData.orderIndex = Pair(num, true)
            IndexConfigType.MATCH -> rulesData.matchIndex = Pair(num, true)
        }

        /**
         * 得到下标
         * @return [IndexTypeConditionSort]
         */
        fun index() = IndexTypeConditionSort()

        /**
         * 字节码、数组下标排序实现类
         *
         * - 请使用 [index] 方法来获取 [IndexTypeConditionSort]
         */
        inner class IndexTypeConditionSort internal constructor() {

            /** 设置满足条件的第一个*/
            fun first() = index(num = 0)

            /** 设置满足条件的最后一个*/
            fun last() = when (type) {
                IndexConfigType.ORDER -> rulesData.orderIndex = Pair(0, false)
                IndexConfigType.MATCH -> rulesData.matchIndex = Pair(0, false)
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
        is String -> runCatching { toClass(loader) }.getOrNull() ?: UndefinedType
        is VariousClass -> runCatching { get(loader) }.getOrNull() ?: UndefinedType
        else -> error("$tag match type \"$javaClass\" not allowed")
    } as Class<*>?

    /**
     * 返回结果实现类
     *
     * - 此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [BaseResult]
     */
    internal abstract fun build(): BaseResult

    /**
     * 返回只有异常的结果实现类
     *
     * - 此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [BaseResult]
     */
    internal abstract fun failure(throwable: Throwable?): BaseResult

    /**
     * 查找结果实现、处理类接口
     *
     * - 此功能交由方法体自动完成 - 你不应该手动继承此接口
     */
    interface BaseResult
}