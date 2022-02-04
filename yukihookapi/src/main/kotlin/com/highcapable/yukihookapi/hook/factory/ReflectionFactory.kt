/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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

package com.highcapable.yukihookapi.hook.factory

import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * 字符串转换为实体类
 * @return [Class]
 * @throws NoClassDefFoundError
 */
val String.clazz: Class<*> get() = Class.forName(this)

/**
 * 查找方法是否存在
 * @param name 名称
 * @param clazz params
 * @return [Boolean] 是否存在
 */
fun Class<*>.hasMethod(name: String, vararg clazz: Class<*>): Boolean =
    try {
        getDeclaredMethod(name, *clazz)
        true
    } catch (_: Throwable) {
        false
    }

/**
 * 查找静态 [Field] 的实例
 * @param name 名称
 * @return [Any] 实例对象
 * @throws NoSuchFieldError
 */
fun Class<*>.findStaticField(name: String): Any? = getDeclaredField(name).apply { isAccessible = true }[null]

/**
 * 查找 [Field] 的实例 - 不能是静态
 * @param any 对象
 * @param name 名称
 * @return [Any] 实例对象
 * @throws NoSuchFieldError
 */
fun Class<*>.findField(any: Any?, name: String): Any? = getDeclaredField(name).apply { isAccessible = true }[any]

/**
 * 设置 [Field] - 不能是静态
 * @param any 对象
 * @param name 名称
 * @param value 值
 * @throws NoSuchFieldError
 */
fun Class<*>.modifyField(any: Any?, name: String, value: Any?) {
    getDeclaredField(name).apply {
        isAccessible = true
        set(any, value)
    }
}

/**
 * 查找目标变量
 * @param name 方法名
 * @return [Field]
 * @throws NoSuchFieldError 如果找不到变量会报错
 */
fun Class<*>.findField(name: String): Field =
    getDeclaredField(name).apply { isAccessible = true }

/**
 * 得到方法
 * @param name 方法名称
 * @param clazz params
 * @return [Method]
 * @throws NoSuchMethodError
 */
fun Class<*>.findMethod(name: String, vararg clazz: Class<*>): Method? =
    getDeclaredMethod(name, *clazz).apply { isAccessible = true }

/**
 * 得到构造类
 * @param parameterTypes params
 * @return [Constructor]
 * @throws NoSuchMethodError
 */
fun Class<*>.findConstructor(vararg parameterTypes: Class<*>?): Constructor<out Any>? =
    getDeclaredConstructor(*parameterTypes).apply { isAccessible = true }

/**
 * 执行方法 - 静态
 * @param anys 方法参数
 * @return [T]
 * @throws IllegalStateException 如果 [T] 类型错误
 */
inline fun <reified T> Method.invokeStatic(vararg anys: Any) =
    invoke(null, anys) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")

/**
 * 执行方法 - 非静态
 * @param any 目标对象
 * @param anys 方法参数
 * @return [T]
 * @throws IllegalStateException 如果 [T] 类型错误
 */
inline fun <reified T> Method.invokeAny(any: Any?, vararg anys: Any) =
    invoke(any, anys) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")
