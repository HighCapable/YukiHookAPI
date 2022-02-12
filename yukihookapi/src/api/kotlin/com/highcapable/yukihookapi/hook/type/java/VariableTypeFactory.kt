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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.type.java

import java.io.Serializable

/**
 * 获得 [Any] 类型
 * @return [Class]
 */
val AnyType get() = Any::class.java

/**
 * 获得 [Unit] 类型
 * @return [Class]
 */
val UnitType get() = Void.TYPE ?: Unit::class.java

/**
 * 获得 [Boolean] 类型
 * @return [Class]
 */
val BooleanType get() = Boolean::class.java

/**
 * 获得 [Int] 类型
 * @return [Class]
 */
val IntType get() = Int::class.javaPrimitiveType

/**
 * 获得 [Long] 类型
 * @return [Class]
 */
val LongType get() = Long::class.javaPrimitiveType

/**
 * 获得 [Short] 类型
 * @return [Class]
 */
val ShortType get() = Short::class.javaPrimitiveType

/**
 * 获得 [Float] 类型
 * @return [Class]
 */
val FloatType get() = Float::class.javaPrimitiveType

/**
 * 获得 [Double] 类型
 * @return [Class]
 */
val DoubleType get() = Double::class.javaPrimitiveType

/**
 * 获得 [String] 类型
 * @return [Class]
 */
val StringType get() = String::class.java

/**
 * 获得 [Char] 类型
 * @return [Class]
 */
val CharType get() = Char::class.java

/**
 * 获得 [CharSequence] 类型
 * @return [Class]
 */
val CharSequenceType get() = CharSequence::class.java

/**
 * 获得 [Serializable] 类型
 * @return [Class]
 */
val SerializableClass get() = Serializable::class.java