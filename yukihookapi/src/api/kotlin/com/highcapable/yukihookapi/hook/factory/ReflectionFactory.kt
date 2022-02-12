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

package com.highcapable.yukihookapi.hook.factory

import com.highcapable.yukihookapi.hook.bean.HookClass
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Method

/**
 * [Class] 转换为 [HookClass]
 * @return [HookClass]
 */
val Class<*>.hookClass get() = HookClass(instance = this, name)

/**
 * [HookClass] 转换为 [Class]
 * @return [Class] or null
 */
val HookClass.normalClass get() = instance

/**
 * 通过字符串查找类是否存在
 *
 * - ❗仅限使用当前的 [ClassLoader]
 * @return [Boolean] 是否存在
 */
val String.hasClass get() = hasClass(loader = null)

/**
 * 通过字符串转换为实体类
 * @param name [Class] 的完整包名+名称
 * @param loader [Class] 所在的 [ClassLoader] - 默认空 - 可不填
 * @return [Class]
 * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
 */
fun classOf(name: String, loader: ClassLoader? = null): Class<*> =
    if (loader == null) Class.forName(name)
    else loader.loadClass(name)

/**
 * 通过字符串查找类是否存在
 * @param loader [Class] 所在的 [ClassLoader]
 * @return [Boolean] 是否存在
 */
fun String.hasClass(loader: ClassLoader?) = try {
    classOf(name = this, loader)
    true
} catch (_: Throwable) {
    false
}

/**
 * 查找方法是否存在
 * @param name 名称
 * @param paramType params
 * @return [Boolean] 是否存在
 */
fun Class<*>.hasMethod(name: String, vararg paramType: Class<*>): Boolean =
    try {
        getDeclaredMethod(name, *paramType)
        true
    } catch (_: Throwable) {
        false
    }

/**
 * 查找并得到静态 [Field] 的实例
 * @param name 名称
 * @return [T] 实例对象 or null
 * @throws NoSuchFieldError
 */
inline fun <reified T> Class<*>.obtainStaticFieldAny(name: String): T? =
    getDeclaredField(name).apply { isAccessible = true }[null] as? T?

/**
 * 查找并得到 [Field] 的实例
 * @param any 对象
 * @param name 名称
 * @return [T] 实例对象 or null
 * @throws NoSuchFieldError
 */
inline fun <reified T> Class<*>.obtainFieldAny(any: Any?, name: String): T? =
    getDeclaredField(name).apply { isAccessible = true }[any] as? T?

/**
 * 修改静态 [Field] 的实例内容
 * @param name 名称
 * @param value 值
 * @throws NoSuchFieldError
 */
fun Class<*>.modifyStaticField(name: String, value: Any?) {
    getDeclaredField(name).apply {
        isAccessible = true
        set(null, value)
    }
}

/**
 * 修改 [Field] 的实例内容
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
 * 查找并得到方法
 * @param name 方法名称
 * @param paramType params
 * @return [Method] or null
 * @throws NoSuchMethodError
 */
fun Class<*>.obtainMethod(name: String, vararg paramType: Class<*>): Method? =
    getDeclaredMethod(name, *paramType).apply { isAccessible = true }

/**
 * 查找并得到构造类
 * @param paramType params
 * @return [Constructor] or null
 * @throws NoSuchMethodError
 */
fun Class<*>.obtainConstructor(vararg paramType: Class<*>): Constructor<out Any>? =
    getDeclaredConstructor(*paramType).apply { isAccessible = true }

/**
 * 执行静态方法
 * @param param 方法参数
 * @return [T]
 * @throws IllegalStateException 如果 [T] 类型错误
 */
inline fun <reified T> Method.invokeStatic(vararg param: Any?) =
    if (param.isNotEmpty())
        invoke(null, param) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")
    else invoke(null) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")

/**
 * 执行方法
 * @param any 目标对象
 * @param param 方法参数
 * @return [T]
 * @throws IllegalStateException 如果 [T] 类型错误
 */
inline fun <reified T> Method.invokeAny(any: Any?, vararg param: Any?) =
    if (param.isNotEmpty())
        invoke(any, param) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")
    else invoke(any) as? T? ?: error("Method ReturnType cannot cast to ${T::class.java}")
