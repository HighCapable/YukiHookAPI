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

import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.core.finder.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import com.highcapable.yukihookapi.hook.store.MemberCacheStore
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
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
 * 当前 [Class] 是否有继承关系 - 父类是 [Any] 将被认为没有继承关系
 * @return [Boolean]
 */
val Class<*>.hasExtends get() = superclass.name != "java.lang.Object"

/**
 * 通过字符串转换为实体类
 * @param name [Class] 的完整包名+名称
 * @param loader [Class] 所在的 [ClassLoader] - 默认空 - 可不填
 * @return [Class]
 * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
 */
fun classOf(name: String, loader: ClassLoader? = null): Class<*> {
    val hashCode = ("[$name][$loader]").hashCode()
    return MemberCacheStore.findClass(hashCode) ?: run {
        when {
            YukiHookBridge.hasXposedBridge ->
                runCatching { XposedHelpers.findClassIfExists(name, loader) }.getOrNull()
                    ?: when (loader) {
                        null -> Class.forName(name)
                        else -> loader.loadClass(name)
                    }
            loader == null -> Class.forName(name)
            else -> loader.loadClass(name)
        }.also { MemberCacheStore.putClass(hashCode, it) }
    }
}

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
 * 查找变量是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasField(initiate: FieldFinder.() -> Unit) = field(initiate).ignoredError().isNoSuch.not()

/**
 * 查找方法是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasMethod(initiate: MethodFinder.() -> Unit) = method(initiate).ignoredError().isNoSuch.not()

/**
 * 查找构造方法是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasConstructor(initiate: ConstructorFinder.() -> Unit) = constructor(initiate).ignoredError().isNoSuch.not()

/**
 * 查询 [Member] 中匹配的描述符
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Member.hasModifiers(initiate: ModifierRules.() -> Unit) = ModifierRules().apply(initiate).contains(this)

/**
 * 查找并得到变量
 * @param initiate 查找方法体
 * @return [FieldFinder.Result]
 */
inline fun Class<*>.field(initiate: FieldFinder.() -> Unit) = FieldFinder(classSet = this).apply(initiate).build()

/**
 * 查找并得到方法
 * @param initiate 查找方法体
 * @return [MethodFinder.Result]
 */
inline fun Class<*>.method(initiate: MethodFinder.() -> Unit) = MethodFinder(classSet = this).apply(initiate).build()

/**
 * 查找并得到构造类
 * @param initiate 查找方法体
 * @return [ConstructorFinder.Result]
 */
inline fun Class<*>.constructor(initiate: ConstructorFinder.() -> Unit) = ConstructorFinder(classSet = this).apply(initiate).build()

/**
 * 获得当前实例的类操作对象
 * @param initiate 方法体
 * @return [T]
 */
inline fun <reified T : Any> T.current(initiate: CurrentClass.() -> Unit): T {
    CurrentClass(javaClass, self = this).apply(initiate)
    return this
}

/**
 * 通过构造方法创建新实例 - 任意类型 [Any]
 * @param param 方法参数
 * @param initiate 查找方法体
 * @return [Any] or null
 */
inline fun Class<*>.buildOfAny(vararg param: Any?, initiate: ConstructorFinder.() -> Unit = {}) = constructor(initiate).get().call(*param)

/**
 * 通过构造方法创建新实例 - 指定类型 [T]
 * @param param 方法参数
 * @param initiate 查找方法体
 * @return [T] or null
 */
inline fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorFinder.() -> Unit = {}) =
    constructor(initiate).get().newInstance<T>(*param)

/**
 * 遍历当前类中的所有方法
 * @param result 回调 - ([Int] 下标,[Method] 实例)
 */
inline fun Class<*>.allMethods(result: (index: Int, method: Method) -> Unit) =
    declaredMethods.forEachIndexed { p, it -> result(p, it.apply { isAccessible = true }) }

/**
 * 遍历当前类中的所有构造方法
 * @param result 回调 - ([Int] 下标,[Constructor] 实例)
 */
inline fun Class<*>.allConstructors(result: (index: Int, constructor: Constructor<*>) -> Unit) =
    declaredConstructors.forEachIndexed { p, it -> result(p, it.apply { isAccessible = true }) }

/**
 * 遍历当前类中的所有变量
 * @param result 回调 - ([Int] 下标,[Field] 实例)
 */
inline fun Class<*>.allFields(result: (index: Int, field: Field) -> Unit) =
    declaredFields.forEachIndexed { p, it -> result(p, it.apply { isAccessible = true }) }