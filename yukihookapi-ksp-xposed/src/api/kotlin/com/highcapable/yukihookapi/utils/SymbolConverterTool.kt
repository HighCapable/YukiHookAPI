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
 * This file is created by fankes on 2024/6/20.
 */
package com.highcapable.yukihookapi.utils

/**
 * Converts symbols into valid Kotlin identifiers.
 */
object SymbolConverterTool {

    /** Kotlin hard keywords. */
    private val kotlinHardKeywords = listOf(
        "as", "as?", "break", "class", "continue", "do",
        "else", "false", "for", "fun", "if", "in", "!in", "interface",
        "is", "!is", "null", "object", "package", "return", "super",
        "this", "throw", "true", "try", "typealias", "typeof", "val",
        "var", "when", "while"
    )

    /**
     * Converts the given content when necessary.
     * @param content the content to convert.
     * @return [String]
     */
    fun process(content: String) = when {
        content.contains(".") && !content.startsWith(".") && !content.endsWith(".") ->
            content.split(".").joinToString(".") { if (isKotlinKeyword(it)) "`$it`" else it }
        isKotlinKeyword(content) -> "`$content`"
        else -> content
    }

    /**
     * Checks whether the given word is a Kotlin hard keyword.
     * @param word the word to check.
     * @return [Boolean]
     */
    private fun isKotlinKeyword(word: String) = kotlinHardKeywords.contains(word)
}