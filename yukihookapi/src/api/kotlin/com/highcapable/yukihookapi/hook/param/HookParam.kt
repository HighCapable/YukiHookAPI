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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Hook 方法、构造类的目标对象实现类
 * @param wrapper [HookParam] 的参数包装类实例
 */
class HookParam(private val wrapper: HookParamWrapper) {

    /**
     * 获取当前 Hook 对象 [method] or [constructor] 的参数对象数组
     * @return [Array]
     */
    val args get() = wrapper.args ?: arrayOf(0)

    /**
     * 获取当前 Hook 对象 [method] or [constructor] 的参数对象数组第一位
     * @return [Array]
     * @throws IllegalStateException 如果数组为空
     */
    val firstArgs get() = if (args.isNotEmpty()) args[0] else error("HookParam args is empty")

    /**
     * 获取当前 Hook 对象 [method] or [constructor] 的参数对象数组最后一位
     * @return [Array]
     * @throws IllegalStateException 如果数组为空
     */
    val lastArgs get() = if (args.isNotEmpty()) args[args.lastIndex] else error("HookParam args is empty")

    /**
     * 获取当前 Hook 实例的对象
     * @return [Any]
     * @throws IllegalStateException 如果对象为空
     */
    val instance get() = wrapper.instance ?: error("HookParam must with a non-null instance")

    /**
     * 获取当前 Hook 实例的类对象
     * @return [Class]
     */
    val instanceClass get() = instance.javaClass

    /**
     * 获取当前 Hook 对象的方法
     * @return [Method]
     * @throws IllegalStateException 如果 [Method] 为空或方法类型不是 [Method]
     */
    val method get() = wrapper.member as? Method? ?: error("Current hook method type is wrong or null")

    /**
     * 获取当前 Hook 对象的构造方法
     * @return [Constructor]
     * @throws IllegalStateException 如果 [Constructor] 为空或方法类型不是 [Constructor]
     */
    val constructor get() = wrapper.member as? Constructor<*>? ?: error("Current hook constructor type is wrong or null")

    /**
     * 获取、设置当前 Hook 对象的 [method] or [constructor] 的返回值
     * @return [Any] or null
     */
    var result: Any?
        get() = wrapper.result
        set(value) {
            wrapper.result = value
        }

    /**
     * 获取当前 Hook 实例的对象 [T]
     * @return [T]
     * @throws IllegalStateException 如果对象为空或对象类型不是 [T]
     */
    inline fun <reified T> instance() = instance as? T? ?: error("HookParam instance cannot cast to ${T::class.java.name}")

    /**
     * 获取当前 Hook 对象的 [method] or [constructor] 的参数实例化对象类
     * @param index 参数对象数组下标 - 默认是 0
     * @return [ArgsModifyer]
     */
    fun args(index: Int = 0) = ArgsModifyer(index)

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param args 参数实例
     * @return [T]
     */
    fun <T> Member.invokeOriginal(vararg args: Any?) = wrapper.invokeOriginalMember(member = this, *args) as? T?

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 true
     *
     * ❗请确保 [result] 类型为 [Boolean]
     */
    fun resultTrue() {
        result = true
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 false
     *
     * ❗请确保 [result] 类型为 [Boolean]
     */
    fun resultFalse() {
        result = false
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 为 null
     *
     * ❗此方法将强制设置方法体的 [result] 为 null
     */
    fun resultNull() {
        result = null
    }

    /**
     * 对方法参数的修改进行实例化类
     * @param index 参数对象数组下标
     */
    inner class ArgsModifyer(private val index: Int) {

        /**
         * 设置方法参数的实例对象
         * @param any 实例对象
         * @throws IllegalStateException 如果目标方法参数对象数组为空或 [index] 下标不存在
         */
        fun <T> set(any: T?) {
            if (args.isEmpty()) error("HookParam method args is empty,mabe not has args")
            if (index > args.lastIndex) error("HookParam method args index out of bounds,max is ${args.lastIndex}")
            wrapper.setArgs(index, any)
        }

        /**
         * 设置方法参数的实例对象为 null
         *
         * 此方法可以将任何被 Hook 的目标对象设置为空
         */
        fun setNull() = set(null)

        /**
         * 设置方法参数的实例对象为 true
         *
         * ❗请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setTrue() = set(true)

        /**
         * 设置方法参数的实例对象为 false
         *
         * ❗请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setFalse() = set(false)
    }
}