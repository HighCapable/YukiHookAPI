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
 * This file is created by fankes on 2022/5/16.
 * This file is modified by fankes on 2022/9/14.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import java.lang.reflect.Member

/**
 * Fuzzy name condition implementation for [Class] and [Member].
 *
 * Provides more precise matching for [Class] and [Member] names obfuscated by R8.
 * @param instance the current instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class NameRules private constructor(private val instance: String) {

    internal companion object {

        /**
         * Creates an instance.
         * @param instance the source instance.
         * @return [NameRules]
         */
        internal fun with(instance: String) = NameRules(instance)
    }

    /**
     * Checks whether this is the enclosing-instance field name of an anonymous class.
     *
     * Its name usually has the form `this$[index]`.
     * @param index the index, 0 by default.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isSynthetic(index: Int = 0) = this == "this$$index"

    /**
     * Checks whether this string contains only symbols.
     *
     * Matches strings containing only symbols such as `_`, `-`, `?`, `!`, `,`, `.`, `<`, and `>`.
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlySymbols() = matches("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-_+=<>]+".toRegex())

    /**
     * Checks whether this string contains only letters.
     *
     * Without [isOnlyLowercase] or [isOnlyUppercase], matches only the 26 uppercase and lowercase English letters.
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlyLetters() = matches("[a-zA-Z]+".toRegex())

    /**
     * Checks whether this string contains only digits.
     *
     * Matches strings containing only the Arabic digits 0 through 9.
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlyNumbers() = matches("\\d+".toRegex())

    /**
     * Checks whether this string contains only letters or digits.
     *
     * Combines the conditions of [isOnlyLetters] and [isOnlyNumbers].
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlyLettersNumbers() = matches("[a-zA-Z\\d]+".toRegex())

    /**
     * Checks whether this string contains only lowercase letters.
     *
     * When used alone, this condition also rejects any non-letter characters.
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlyLowercase() = matches("[a-z]+".toRegex())

    /**
     * Checks whether this string contains only uppercase letters.
     *
     * When used alone, this condition also rejects any non-letter characters.
     *
     * Use [matches] for more specific regular-expression matching.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun String.isOnlyUppercase() = matches("[A-Z]+".toRegex())

    override fun toString() = "NameRules [$instance]"
}