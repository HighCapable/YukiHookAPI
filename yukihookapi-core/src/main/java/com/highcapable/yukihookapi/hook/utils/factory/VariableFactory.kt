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
 * This file is created by fankes on 2023/9/23.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.utils.factory

/**
 * 对 [T] 返回无返回值的 [Unit]
 * @return [Unit]
 */
internal fun <T> T?.unit() = let {}

/**
 * 获取数组内容依次列出的字符串表示
 * @return [String]
 */
internal inline fun <reified T> Array<out T>.value() = if (isNotEmpty()) {
    var value = ""
    forEach { value += it.toString() + ", " }
    "[${value.trim().let { it.substring(0, it.lastIndex) }}]"
} else "[]"

/**
 * 通过 [conditions] 查找符合条件的最后一个元素的下标
 * @return [Int] 没有找到符合条件的下标将返回 -1
 */
internal inline fun <reified T> Sequence<T>.findLastIndex(conditions: (T) -> Boolean) =
    withIndex().findLast { conditions(it.value) }?.index ?: -1

/**
 * 返回最后一个元素的下标
 * @return [Int] 如果 [Sequence] 为空将返回 -1
 */
internal inline fun <reified T> Sequence<T>.lastIndex() = foldIndexed(-1) { index, _, _ -> index }.takeIf { it >= 0 } ?: -1

/**
 * 满足条件判断方法体 - 对 [kotlin.takeIf] 进行封装
 * @param other 需要满足不为空的对象 - 仅用于判断是否为 null
 * @param predicate 原始方法体
 * @return [T] or null
 */
internal inline fun <T> T.takeIf(other: Any?, predicate: (T) -> Boolean) = if (other != null) takeIf(predicate) else null

/**
 * 满足条件返回值 - 对 [kotlin.let] 进行封装
 * @param other 需要满足不为空的对象 - 仅用于判断是否为 null
 * @param block 原始方法体
 * @return [R] or null
 */
internal inline fun <T, R> T.let(other: Any?, block: (T) -> R) = if (other != null) let(block) else null

/**
 * 条件判断方法体捕获异常返回 true
 * @param block 原始方法体
 * @return [Boolean]
 */
internal inline fun runOrTrue(block: () -> Boolean) = runCatching { block() }.getOrNull() ?: true

/**
 * 条件判断方法体捕获异常返回 false
 * @param block 原始方法体
 * @return [Boolean]
 */
internal inline fun runOrFalse(block: () -> Boolean) = runCatching { block() }.getOrNull() ?: false

/**
 * 创建多项条件判断 - 条件对象 [T]
 * @param initiate 方法体
 * @return [Conditions.Result]
 */
internal inline fun <T> T.conditions(initiate: Conditions<T>.() -> Unit) = Conditions(value = this).apply(initiate).build()

/**
 * 构造条件判断类
 * @param value 当前条件对象
 */
internal class Conditions<T>(internal var value: T) {

    /** 全部判断条件数组 (与) */
    private val andConditions = mutableListOf<Boolean>()

    /** 全部判断条件数组 (或) */
    private val optConditions = mutableListOf<Boolean>()

    /**
     * 添加与 (and) 条件
     * @param value 条件值
     */
    internal fun and(value: Boolean) {
        andConditions.add(value)
    }

    /**
     * 添加或 (or) 条件
     * @param value 条件值
     */
    internal fun opt(value: Boolean) {
        optConditions.add(value)
    }

    /**
     * 结束方法体
     * @return [Result]
     */
    internal fun build() = Result()

    /**
     * 构造条件判断结果类
     */
    inner class Result internal constructor() {

        /**
         * 获取条件判断结果
         * @return [Boolean]
         */
        private val result by lazy {
            optConditions.takeIf { it.isNotEmpty() }?.any { it } == true ||
                andConditions.takeIf { it.isNotEmpty() }?.any { it.not() }?.not() == true
        }

        /**
         * 当条件成立
         * @param callback 回调
         */
        internal inline fun finally(callback: () -> Unit): Result {
            if (result) callback()
            return this
        }

        /**
         * 当条件不成立
         * @param callback 回调
         */
        internal inline fun without(callback: () -> Unit): Result {
            if (result.not()) callback()
            return this
        }
    }
}

/**
 * 获取 [ModifyValue] 对象
 * @return [ModifyValue]
 */
internal fun <T> T.value() = ModifyValue(value = this)

/**
 * 可修改变量实现类
 * @param value 变量自身实例
 */
internal data class ModifyValue<T>(var value: T)

/**
 * 随机种子工具类
 */
internal object RandomSeed {

    /** 随机字母和数字定义 */
    private const val RANDOM_LETTERS_NUMBERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    /**
     * 生成随机字符串
     * @param length 生成长度 - 默认 15
     * @return [String]
     */
    internal fun createString(length: Int = 15) = StringBuilder().apply {
        for (i in 1..length) append(RANDOM_LETTERS_NUMBERS[(0..RANDOM_LETTERS_NUMBERS.lastIndex).random()])
    }.toString()
}