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

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiHookModuleStatus
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/** 定义 [FieldFinder] 方法体类型 */
internal typealias FieldConditions = FieldFinder.() -> Unit

/** 定义 [MethodFinder] 方法体类型 */
internal typealias MethodConditions = MethodFinder.() -> Unit

/** 定义 [ConstructorFinder] 方法体类型 */
internal typealias ConstructorConditions = ConstructorFinder.() -> Unit

/**
 * 定义一个 [Class] 中的 [Member] 类型
 */
enum class MembersType {
    /** 全部 [Method] 与 [Constructor] */
    ALL,

    /** 全部 [Method] */
    METHOD,

    /** 全部 [Constructor] */
    CONSTRUCTOR
}

/**
 * 监听当前 [ClassLoader] 的 [ClassLoader.loadClass] 方法装载
 *
 * - ❗请注意只有当前 [ClassLoader] 有主动使用 [ClassLoader.loadClass] 事件时才能被捕获
 *
 * - ❗这是一个实验性功能 - 一般情况下不会用到此方法 - 不保证不会发生错误
 *
 * - ❗只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 * @param result 回调 - ([Class] 实例对象)
 */
fun ClassLoader.onLoadClass(result: (Class<*>) -> Unit) = AppParasitics.hookClassLoader(loader = this, result)

/**
 * 当前 [Class] 是否有继承关系 - 父类是 [Any] 将被认为没有继承关系
 * @return [Boolean]
 */
val Class<*>.hasExtends get() = superclass != null && superclass?.name != "java.lang.Object"

/**
 * 通过字符串类名转换为 [loader] 中的实体类
 *
 * - ❗此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - ❗请现在转移到 [toClass]
 * @return [Class]
 * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith(expression = "name.toClass(loader)"))
fun classOf(name: String, loader: ClassLoader? = null) = name.toClass(loader)

/**
 * 通过字符串类名转换为 [loader] 中的实体类
 * @param loader [Class] 所在的 [ClassLoader] - 默认空 - 不填使用默认 [ClassLoader]
 * @return [Class]
 * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
 */
fun String.toClass(loader: ClassLoader? = null) = ReflectionTool.findClassByName(name = this, loader)

/**
 * 通过 [T] 得到其 [Class] 实例并转换为实体类
 * @param loader [Class] 所在的 [ClassLoader] - 默认空 - 可不填
 * @return [Class]
 * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
 */
inline fun <reified T> classOf(loader: ClassLoader? = null) = loader?.let { T::class.java.name.toClass(loader) } ?: T::class.java

/**
 * 通过字符串类名使用指定的 [ClassLoader] 查找是否存在
 * @param loader [Class] 所在的 [ClassLoader] - 不填使用默认 [ClassLoader]
 * @return [Boolean] 是否存在
 */
fun String.hasClass(loader: ClassLoader? = null) = ReflectionTool.hasClassByName(name = this, loader)

/**
 * 查找变量是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasField(initiate: FieldConditions) = field(initiate).ignored().isNoSuch.not()

/**
 * 查找方法是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasMethod(initiate: MethodConditions) = method(initiate).ignored().isNoSuch.not()

/**
 * 查找构造方法是否存在
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasConstructor(initiate: ConstructorConditions = { emptyParam() }) = constructor(initiate).ignored().isNoSuch.not()

/**
 * 查询 [Member] 中匹配的描述符
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Member.hasModifiers(initiate: ModifierRules.() -> Unit) = ModifierRules().apply(initiate).contains(this)

/**
 * 查询 [Class] 中匹配的描述符
 * @param initiate 方法体
 * @return [Boolean] 是否存在
 */
inline fun Class<*>.hasModifiers(initiate: ModifierRules.() -> Unit) = ModifierRules().apply(initiate).contains(this)

/**
 * 查找并得到变量
 * @param initiate 查找方法体
 * @return [FieldFinder.Result]
 */
inline fun Class<*>.field(initiate: FieldConditions) = FieldFinder(classSet = this).apply(initiate).build()

/**
 * 查找并得到方法
 * @param initiate 查找方法体
 * @return [MethodFinder.Result]
 */
inline fun Class<*>.method(initiate: MethodConditions) = MethodFinder(classSet = this).apply(initiate).build()

/**
 * 查找并得到构造方法
 * @param initiate 查找方法体
 * @return [ConstructorFinder.Result]
 */
inline fun Class<*>.constructor(initiate: ConstructorConditions = { emptyParam() }) = ConstructorFinder(classSet = this).apply(initiate).build()

/**
 * 获得当前实例的类操作对象
 * @param ignored 是否开启忽略错误警告功能 - 默认否
 * @return [CurrentClass]
 */
inline fun <reified T : Any> T.current(ignored: Boolean = false): CurrentClass {
    javaClass.checkingInternal()
    return CurrentClass(javaClass, instance = this).apply { isShutErrorPrinting = ignored }
}

/**
 * 获得当前实例的类操作对象
 * @param ignored 是否开启忽略错误警告功能 - 默认否
 * @param initiate 方法体
 * @return [T]
 */
inline fun <reified T : Any> T.current(ignored: Boolean = false, initiate: CurrentClass.() -> Unit): T {
    current(ignored).apply(initiate)
    return this
}

/**
 * 通过构造方法创建新实例 - 任意类型 [Any]
 *
 * - ❗此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - ❗请现在转移到 [buildOf]
 * @return [Any] or null
 */
@Deprecated(message = "请使用新的命名方法", replaceWith = ReplaceWith(expression = "buildOf(*param, initiate)"))
fun Class<*>.buildOfAny(vararg param: Any?, initiate: ConstructorConditions = { emptyParam() }) = buildOf(*param, initiate)

/**
 * 通过构造方法创建新实例 - 任意类型 [Any]
 * @param param 方法参数
 * @param initiate 查找方法体
 * @return [Any] or null
 */
inline fun Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions = { emptyParam() }) =
    constructor(initiate).get().call(*param)

/**
 * 通过构造方法创建新实例 - 指定类型 [T]
 * @param param 方法参数
 * @param initiate 查找方法体
 * @return [T] or null
 */
@JvmName(name = "buildOf_Generics")
inline fun <T> Class<*>.buildOf(vararg param: Any?, initiate: ConstructorConditions = { emptyParam() }) =
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

/**
 * 检查内部类调用
 * @throws RuntimeException 如果遇到非法调用
 */
@PublishedApi
internal fun Class<*>.checkingInternal() {
    if (name == classOf<YukiHookModuleStatus>().name) return
    if (name == classOf<YukiHookAPI>().name || name.startsWith(prefix = "com.highcapable.yukihookapi.hook")) throw RuntimeException(
        "!!!DO NOT ALLOWED!!! You cannot hook or reflection to call the internal class of the YukiHookAPI itself, " +
                "The called class is [$this]"
    )
}