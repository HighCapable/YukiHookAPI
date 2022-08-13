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
 * This file is Created by fankes on 2022/4/4.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.core.finder.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.MethodFinder
import com.highcapable.yukihookapi.hook.factory.FieldCondition
import com.highcapable.yukihookapi.hook.factory.MethodCondition
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method

/**
 * 当前实例的类操作对象
 * @param classSet 当前实例的 [Class]
 * @param instance 当前实例本身
 */
class CurrentClass @PublishedApi internal constructor(@PublishedApi internal val classSet: Class<*>, @PublishedApi internal val instance: Any) {

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
     * 调用父类实例
     * @return [SuperClass]
     */
    fun superClass() = SuperClass()

    /**
     * 调用当前实例中的变量
     * @param initiate 查找方法体
     * @return [FieldFinder.Result.Instance]
     */
    inline fun field(initiate: FieldCondition) = classSet.field(initiate).get(instance)

    /**
     * 调用当前实例中的方法
     * @param initiate 查找方法体
     * @return [MethodFinder.Result.Instance]
     */
    inline fun method(initiate: MethodCondition) = classSet.method(initiate).get(instance)

    /**
     * 当前类的父类实例的类操作对象
     *
     * - ❗请使用 [superClass] 方法来获取 [SuperClass]
     */
    inner class SuperClass internal constructor() {

        /**
         * 获得当前 [classSet] 中父类的 [Class.getName]
         * @return [String]
         */
        val name get() = classSet.superclass.name ?: instance.javaClass.superclass.name ?: ""

        /**
         * 获得当前 [classSet] 中父类的 [Class.getSimpleName]
         * @return [String]
         */
        val simpleName get() = classSet.superclass.simpleName ?: instance.javaClass.superclass.simpleName ?: ""

        /**
         * 调用父类实例中的变量
         * @param initiate 查找方法体
         * @return [FieldFinder.Result.Instance]
         */
        inline fun field(initiate: FieldCondition) = classSet.superclass.field(initiate).get(instance)

        /**
         * 调用父类实例中的方法
         * @param initiate 查找方法体
         * @return [MethodFinder.Result.Instance]
         */
        inline fun method(initiate: MethodCondition) = classSet.superclass.method(initiate).get(instance)

        override fun toString() = "CurrentClass super [${classSet.superclass}]"
    }

    override fun toString() = "CurrentClass [$classSet]"
}