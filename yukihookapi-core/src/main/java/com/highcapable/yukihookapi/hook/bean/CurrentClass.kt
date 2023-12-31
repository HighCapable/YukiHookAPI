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
 * This file is created by fankes on 2022/4/4.
 */
@file:Suppress("unused", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.type.factory.FieldConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.MethodConditions
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.generic
import com.highcapable.yukihookapi.hook.factory.method

/**
 * 当前实例的类操作对象
 * @param classSet 当前实例的 [Class]
 * @param instance 当前实例本身
 */
class CurrentClass internal constructor(private val classSet: Class<*>, internal val instance: Any) {

    /** 是否开启忽略错误警告功能 */
    internal var isIgnoreErrorLogs = false

    /**
     * 获得当前 [classSet] 的 [Class.getName]
     * @return [String]
     */
    val name get() = classSet.name ?: instance.javaClass.name ?: ""

    /**
     * 获得当前 [classSet] 的 [Class.getSimpleName]
     * @return [String]
     */
    val simpleName get() = classSet.simpleName ?: instance.javaClass.simpleName ?: ""

    /**
     * 获得当前实例中的泛型父类
     *
     * 如果当前实例不存在泛型将返回 null
     * @return [GenericClass] or null
     */
    fun generic() = classSet.generic()

    /**
     * 获得当前实例中的泛型父类
     *
     * 如果当前实例不存在泛型将返回 null
     * @param initiate 实例方法体
     * @return [GenericClass] or null
     */
    inline fun generic(initiate: GenericClass.() -> Unit) = classSet.generic(initiate)

    /**
     * 调用父类实例
     * @return [SuperClass]
     */
    fun superClass() = SuperClass(classSet.superclass)

    /**
     * 调用当前实例中的变量
     * @param initiate 查找方法体
     * @return [FieldFinder.Result.Instance]
     */
    inline fun field(initiate: FieldConditions) = classSet.field(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

    /**
     * 调用当前实例中的方法
     * @param initiate 查找方法体
     * @return [MethodFinder.Result.Instance]
     */
    inline fun method(initiate: MethodConditions) = classSet.method(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

    /**
     * 当前类的父类实例的类操作对象
     *
     * - 请使用 [superClass] 方法来获取 [SuperClass]
     * @param superClassSet 父类 [Class] 对象
     */
    inner class SuperClass internal constructor(private val superClassSet: Class<*>) {

        /**
         * 获得当前 [classSet] 中父类的 [Class.getName]
         * @return [String]
         */
        val name get() = superClassSet.name ?: ""

        /**
         * 获得当前 [classSet] 中父类的 [Class.getSimpleName]
         * @return [String]
         */
        val simpleName get() = superClassSet.simpleName ?: ""

        /**
         * 获得当前实例父类中的泛型父类
         *
         * 如果当前实例不存在泛型将返回 null
         * @return [GenericClass] or null
         */
        fun generic() = superClassSet.generic()

        /**
         * 获得当前实例父类中的泛型父类
         *
         * 如果当前实例不存在泛型将返回 null
         * @param initiate 实例方法体
         * @return [GenericClass] or null
         */
        inline fun generic(initiate: GenericClass.() -> Unit) = superClassSet.generic(initiate)

        /**
         * 调用父类实例中的变量
         * @param initiate 查找方法体
         * @return [FieldFinder.Result.Instance]
         */
        inline fun field(initiate: FieldConditions) = superClassSet.field(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

        /**
         * 调用父类实例中的方法
         * @param initiate 查找方法体
         * @return [MethodFinder.Result.Instance]
         */
        inline fun method(initiate: MethodConditions) =
            superClassSet.method(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

        override fun toString() = "CurrentClass super [$superClassSet]"
    }

    override fun toString() = "CurrentClass [$classSet]"
}