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
 * This file is created by fankes on 2022/9/20.
 */
@file:Suppress("unused", "UNCHECKED_CAST", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi.hook.bean

import java.lang.reflect.ParameterizedType

/**
 * 当前 [Class] 的泛型父类操作对象
 * @param type 类型声明实例
 */
class GenericClass internal constructor(private val type: ParameterizedType) {

    /**
     * 获得泛型参数数组下标的 [Class] 实例
     *
     * - 在运行时局部变量的泛型会被擦除 - 获取不到时将会返回 null
     * @param index 数组下标 - 默认 0
     * @return [Class] or null
     */
    fun argument(index: Int = 0) = type.actualTypeArguments[index] as? Class<*>?

    /**
     * 获得泛型参数数组下标的 [Class] 实例
     *
     * - 在运行时局部变量的泛型会被擦除 - 获取不到时将会返回 null
     * @param index 数组下标 - 默认 0
     * @return [Class]<[T]> or null
     * @throws IllegalStateException 如果 [Class] 的类型不为 [T]
     */
    @JvmName("argument_Generics")
    inline fun <reified T> argument(index: Int = 0) =
        type.actualTypeArguments[index].let { args ->
            if (args is Class<*>) args as? Class<T>? ?: error("Target Class type cannot cast to ${T::class.java}")
            else null
        }
}