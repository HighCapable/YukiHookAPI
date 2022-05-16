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

import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreater
import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Hook 方法、构造类的目标对象实现类
 * @param createrInstance [YukiMemberHookCreater] 的实例对象
 * @param wrapper [HookParam] 的参数包装类实例
 */
class HookParam internal constructor(private val createrInstance: YukiMemberHookCreater, private var wrapper: HookParamWrapper? = null) {

    internal companion object {

        /** [HookParam] 是否已经执行首次回调事件 */
        internal var isCallbackCalled = false

        /** 设置 [HookParam] 执行首次回调事件 */
        internal fun invoke() {
            isCallbackCalled = true
        }
    }

    /**
     * 在回调中设置 [HookParam] 使用的 [HookParamWrapper]
     * @param wrapper [HookParamWrapper] 实例
     * @return [HookParam]
     */
    internal fun assign(wrapper: HookParamWrapper): HookParam {
        this.wrapper = wrapper
        return this
    }

    /**
     * 获取当前 Hook 对象 [method] or [constructor] 的参数对象数组
     * @return [Array]
     */
    val args get() = wrapper?.args ?: arrayOf(0)

    /**
     * 获取当前 Hook 实例的对象
     *
     * - ❗如果你当前 Hook 的对象是一个静态 - 那么它将不存在实例的对象
     * @return [Any]
     * @throws IllegalStateException 如果对象为空
     */
    val instance get() = wrapper?.instance ?: error("HookParam instance got null! Is this a static member?")

    /**
     * 获取当前 Hook 实例的类对象
     * @return [Class]
     */
    val instanceClass get() = wrapper?.instance?.javaClass ?: createrInstance.instanceClass

    /**
     * 获取当前 Hook 对象的方法
     * @return [Method]
     * @throws IllegalStateException 如果 [Method] 为空或方法类型不是 [Method]
     */
    val method get() = wrapper?.member as? Method? ?: error("Current hook Method type is wrong or null")

    /**
     * 获取当前 Hook 对象的构造方法
     * @return [Constructor]
     * @throws IllegalStateException 如果 [Constructor] 为空或方法类型不是 [Constructor]
     */
    val constructor get() = wrapper?.member as? Constructor<*>? ?: error("Current hook Constructor type is wrong or null")

    /**
     * 获取、设置当前 Hook 对象的 [method] or [constructor] 的返回值
     * @return [Any] or null
     */
    var result: Any?
        get() = wrapper?.result
        set(value) {
            wrapper?.result = value
        }

    /**
     * 获取当前 Hook 对象的 [method] or [constructor] 的返回值 [T]
     * @return [T] or null
     */
    inline fun <reified T> result() = result as? T?

    /**
     * 获取当前 Hook 实例的对象 [T]
     * @return [T]
     * @throws IllegalStateException 如果对象为空或对象类型不是 [T]
     */
    inline fun <reified T> instance() = instance as? T? ?: error("HookParam instance cannot cast to ${T::class.java.name}")

    /**
     * 获取当前 Hook 对象的 [method] or [constructor] 的参数数组下标实例化类
     * @return [ArgsIndexCondition]
     */
    fun args() = ArgsIndexCondition()

    /**
     * 获取当前 Hook 对象的 [method] or [constructor] 的参数实例化对象类
     * @param index 参数对象数组下标
     * @return [ArgsModifyer]
     */
    fun args(index: Int) = ArgsModifyer(index)

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param args 参数实例
     * @return [T]
     */
    fun <T> Member.invokeOriginal(vararg args: Any?) = wrapper?.invokeOriginalMember(member = this, *args) as? T?

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 true
     *
     * - ❗请确保 [result] 类型为 [Boolean]
     */
    fun resultTrue() {
        result = true
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 false
     *
     * - ❗请确保 [result] 类型为 [Boolean]
     */
    fun resultFalse() {
        result = false
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 为 null
     *
     * - ❗此方法将强制设置方法体的 [result] 为 null
     */
    fun resultNull() {
        result = null
    }

    /**
     * 对方法参数的数组下标进行实例化类
     *
     * - ❗请使用第一个 [args] 方法来获取 [ArgsIndexCondition]
     */
    inner class ArgsIndexCondition internal constructor() {

        /**
         * 获取当前 Hook 对象的 [method] or [constructor] 的参数数组第一位
         * @return [ArgsModifyer]
         */
        fun first() = args(index = 0)

        /**
         * 获取当前 Hook 对象的 [method] or [constructor] 的参数数组最后一位
         * @return [ArgsModifyer]
         */
        fun last() = args(index = args.lastIndex)
    }

    /**
     * 对方法参数的修改进行实例化类
     *
     * - ❗请使用第二个 [args] 方法来获取 [ArgsModifyer]
     * @param index 参数对象数组下标
     */
    inner class ArgsModifyer internal constructor(private val index: Int) {

        /**
         * 得到方法参数的实例对象 [T]
         * @return [T] or null
         */
        fun <T> cast() = runCatching { args[index] as? T? }.getOrNull()

        /**
         * 得到方法参数的实例对象 [Byte]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回 null
         * @return [Byte] or null
         */
        fun byte() = cast<Byte?>()

        /**
         * 得到方法参数的实例对象 [Int]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Int] 取不到返回 0
         */
        fun int() = cast() ?: 0

        /**
         * 得到方法参数的实例对象 [Long]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Long] 取不到返回 0L
         */
        fun long() = cast() ?: 0L

        /**
         * 得到方法参数的实例对象 [Short]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Short] 取不到返回 0
         */
        fun short() = cast<Short?>() ?: 0

        /**
         * 得到方法参数的实例对象 [Double]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Double] 取不到返回 0.0
         */
        fun double() = cast() ?: 0.0

        /**
         * 得到方法参数的实例对象 [Float]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Float] 取不到返回 0f
         */
        fun float() = cast() ?: 0f

        /**
         * 得到方法参数的实例对象 [String]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [String] 取不到返回 ""
         */
        fun string() = cast() ?: ""

        /**
         * 得到方法参数的实例对象 [Char]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Char] 取不到返回 ' '
         */
        fun char() = cast() ?: ' '

        /**
         * 得到方法参数的实例对象 [Boolean]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Boolean] 取不到返回 false
         */
        fun boolean() = cast() ?: false

        /**
         * 得到方法参数的实例对象 [Any]
         * @return [Any] or null
         */
        fun any() = cast<Any?>()

        /**
         * 得到方法参数的实例对象 [Array] - 每项类型 [T]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回空数组
         * @return [Array] 取不到返回空数组
         */
        inline fun <reified T> array() = cast() ?: arrayOf<T>()

        /**
         * 得到方法参数的实例对象 [List] - 每项类型 [T]
         *
         * - ❗请确认目标参数的类型 - 发生错误会返回空数组
         * @return [List] 取不到返回空数组
         */
        inline fun <reified T> list() = cast() ?: listOf<T>()

        /**
         * 设置方法参数的实例对象
         * @param any 实例对象
         * @throws IllegalStateException 如果目标方法参数对象数组为空或 [index] 下标不存在
         */
        fun <T> set(any: T?) {
            if (index < 0) error("HookParam Method args index must be >= 0")
            if (args.isEmpty()) error("HookParam Method args is empty, mabe not has args")
            if (index > args.lastIndex) error("HookParam Method args index out of bounds, max is ${args.lastIndex}")
            wrapper?.setArgs(index, any)
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
         * - ❗请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setTrue() = set(true)

        /**
         * 设置方法参数的实例对象为 false
         *
         * - ❗请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setFalse() = set(false)

        override fun toString() = "Args of index $index"
    }

    override fun toString() = "HookParam by $wrapper"
}