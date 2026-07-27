/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
 * Converts [T] to a valueless [Unit].
 * @return [Unit]
 */
internal fun <T> T?.unit() = let {}

/**
 * Gets a string representation that lists the array contents in order.
 * @return [String]
 */
internal inline fun <reified T> Array<out T>.value() = if (isNotEmpty()) {
    var value = ""
    forEach { value += "$it, " }
    "[${value.trim().let { it.substring(0, it.lastIndex) }}]"
} else "[]"

/**
 * Finds the index of the last element matching [conditions].
 * @return [Int] -1 when no matching index is found.
 */
internal inline fun <reified T> Sequence<T>.findLastIndex(conditions: (T) -> Boolean) =
    withIndex().findLast { conditions(it.value) }?.index ?: -1

/**
 * Returns the index of the last element.
 * @return [Int] -1 when the [Sequence] is empty.
 */
internal inline fun <reified T> Sequence<T>.lastIndex() = foldIndexed(-1) { index, _, _ -> index }.takeIf { it >= 0 } ?: -1

/**
 * Conditional wrapper around [kotlin.takeIf].
 * @param other the object that must be non-null. Used only for the null check.
 * @param predicate the original predicate.
 * @return [T] or null.
 */
internal inline fun <T> T.takeIf(other: Any?, predicate: (T) -> Boolean) = if (other != null) takeIf(predicate) else null

/**
 * Conditional return-value wrapper around [kotlin.let].
 * @param other the object that must be non-null. Used only for the null check.
 * @param block the original block.
 * @return [R] or null.
 */
internal inline fun <T, R> T.let(other: Any?, block: (T) -> R) = if (other != null) let(block) else null

/**
 * Runs a condition block and returns true when it throws an exception.
 * @param block the original block.
 * @return [Boolean]
 */
internal inline fun runOrTrue(block: () -> Boolean) = runCatching { block() }.getOrNull() ?: true

/**
 * Runs a condition block and returns false when it throws an exception.
 * @param block the original block.
 * @return [Boolean]
 */
internal inline fun runOrFalse(block: () -> Boolean) = runCatching { block() }.getOrNull() ?: false

/**
 * Creates a compound condition for [T].
 * @param initiate the condition block.
 * @return [Conditions.Result]
 */
internal inline fun <T> T.conditions(initiate: Conditions<T>.() -> Unit) = Conditions(value = this).apply(initiate).build()

/**
 * Compound condition implementation.
 * @param value the current condition object.
 */
internal class Conditions<T>(internal var value: T) {

    /** All AND conditions. */
    private val andConditions = mutableListOf<Boolean>()

    /** All OR conditions. */
    private val optConditions = mutableListOf<Boolean>()

    /**
     * Adds an AND condition.
     * @param value the condition value.
     */
    internal fun and(value: Boolean) {
        andConditions.add(value)
    }

    /**
     * Adds an OR condition.
     * @param value the condition value.
     */
    internal fun opt(value: Boolean) {
        optConditions.add(value)
    }

    /**
     * Completes the condition block.
     * @return [Result]
     */
    internal fun build() = Result()

    /**
     * Compound condition result implementation.
     */
    inner class Result internal constructor() {

        /**
         * Gets the condition result.
         * @return [Boolean]
         */
        private val result by lazy {
            optConditions.takeIf { it.isNotEmpty() }?.any { it } == true ||
                andConditions.takeIf { it.isNotEmpty() }?.any { it.not() }?.not() == true
        }

        /**
         * Runs when the condition is satisfied.
         * @param callback the callback.
         */
        internal inline fun finally(callback: () -> Unit): Result {
            if (result) callback()
            return this
        }

        /**
         * Runs when the condition is not satisfied.
         * @param callback the callback.
         */
        internal inline fun without(callback: () -> Unit): Result {
            if (result.not()) callback()
            return this
        }
    }
}

/**
 * Gets a [ModifyValue] object.
 * @return [ModifyValue]
 */
internal fun <T> T.value() = ModifyValue(value = this)

/**
 * Mutable value implementation.
 * @param value the value instance.
 */
internal data class ModifyValue<T>(var value: T)

/**
 * Random-seed utility.
 */
internal object RandomSeed {

    /** Available random letters and digits. */
    private const val RANDOM_LETTERS_NUMBERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

    /**
     * Generates a random string.
     * @param length the generated length, 15 by default.
     * @return [String]
     */
    internal fun createString(length: Int = 15): String = buildString {
        repeat(length) { append(RANDOM_LETTERS_NUMBERS.random()) }
    }
}