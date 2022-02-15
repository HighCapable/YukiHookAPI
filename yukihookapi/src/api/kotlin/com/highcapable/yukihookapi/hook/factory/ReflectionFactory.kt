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
@file:Suppress("unused", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.factory

import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import java.lang.reflect.Constructor
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
 * @param returnType 返回类型 - 不填默认模糊
 * @return [Boolean] 是否存在
 */
fun Class<*>.hasMethod(name: String, vararg paramType: Class<*>, returnType: Class<*>? = null): Boolean =
    try {
        method(name, *paramType, returnType = returnType)
        true
    } catch (_: Throwable) {
        false
    }

/**
 * 查找并得到方法
 * @param name 方法名称
 * @param paramType params
 * @param returnType 返回类型 - 不填默认模糊
 * @return [Method] or null
 * @throws NoSuchMethodError
 */
fun Class<*>.method(name: String, vararg paramType: Class<*>, returnType: Class<*>? = null): Method? =
    if (paramType.isNotEmpty())
        ReflectionUtils.findMethodBestMatch(this, returnType, name, *paramType)
    else ReflectionUtils.findMethodNoParam(this, returnType, name)

/**
 * 查找并得到构造类
 * @param paramType params
 * @return [Constructor] or null
 * @throws NoSuchMethodError
 */
fun Class<*>.constructor(vararg paramType: Class<*>): Constructor<out Any>? =
    if (paramType.isNotEmpty())
        ReflectionUtils.findConstructorExact(this, *paramType)
    else ReflectionUtils.findConstructorExact(this)

/**
 * 执行静态方法
 * @param param 方法参数
 * @return [T] or null
 */
inline fun <reified T> Method.callStatic(vararg param: Any?) =
    if (param.isNotEmpty())
        invoke(null, *param) as? T?
    else invoke(null) as? T?

/**
 * 执行方法
 * @param instance 目标对象
 * @param param 方法参数
 * @return [T] or null
 */
inline fun <reified T> Method.call(instance: Any?, vararg param: Any?) =
    if (param.isNotEmpty())
        invoke(instance, *param) as? T?
    else invoke(instance) as? T?
