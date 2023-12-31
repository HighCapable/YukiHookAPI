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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import android.os.Bundle
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator.MemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.log.YLog
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Hook 方法、构造方法的目标对象实现类
 * @param creatorInstance [YukiMemberHookCreator] 的实例对象
 * @param paramId 当前回调方法体 ID
 * @param param Hook 结果回调接口
 */
class HookParam private constructor(
    private val creatorInstance: YukiMemberHookCreator,
    private var paramId: String = "",
    private var param: YukiHookCallback.Param? = null
) {

    internal companion object {

        /** 每个回调方法体的数据存储实例数据 */
        private val dataExtras = mutableMapOf<String, Bundle>()

        /** [HookParam] 是否已经执行首次回调事件 */
        internal var isCallbackCalled = false

        /**
         * 创建新的 [HookParam]
         * @param creatorInstance [YukiMemberHookCreator] 的实例对象
         * @param paramId 当前回调方法体 ID
         * @param param Hook 结果回调接口
         * @return [HookParam]
         */
        internal fun create(creatorInstance: YukiMemberHookCreator, paramId: String, param: YukiHookCallback.Param) =
            HookParam(creatorInstance, paramId, param)

        /** 设置 [HookParam] 执行首次回调事件 */
        internal fun invoke() {
            isCallbackCalled = true
        }
    }

    /**
     * 获取当前 Hook 对象 [method] or [constructor] 的参数对象数组
     *
     * 这里的数组每项类型默认为 [Any] - 你可以使用 [args] 方法来实现 [ArgsModifyer.cast] 功能
     * @return [Array]
     * @throws IllegalStateException 如果对象为空
     */
    val args get() = param?.args ?: error("Current hooked Member args is null")

    /**
     * 获取当前 Hook 实例的对象
     *
     * - 如果你当前 Hook 的对象是一个静态 - 那么它将不存在实例的对象
     *
     * - 如果你不确定当前实例的对象是否为 null - 你可以使用 [instanceOrNull]
     * @return [Any]
     * @throws IllegalStateException 如果对象为空
     */
    val instance get() = param?.instance ?: error("HookParam instance got null! Is this a static member?")

    /**
     * 获取当前 Hook 实例的对象
     *
     * - 如果你当前 Hook 的对象是一个静态 - 那么它将不存在实例的对象
     * @return [Any] or null
     */
    val instanceOrNull get() = param?.instance

    /**
     * 获取当前 Hook 实例的类对象
     *
     * - 如果你当前 Hook 的对象是一个静态 - 那么它将不存在实例的对象
     * @return [Class] or null
     */
    val instanceClass get() = param?.instance?.javaClass

    /**
     * 获取当前 Hook 对象的 [Member]
     *
     * 在不确定 [Member] 类型为 [Method] or [Constructor] 时可以使用此方法
     * @return [Member]
     * @throws IllegalStateException 如果 [member] 为空
     */
    val member get() = param?.member ?: error("Current hooked Member is null")

    /**
     * 获取当前 Hook 对象的方法
     * @return [Method]
     * @throws IllegalStateException 如果 [member] 类型不是 [Method]
     */
    val method get() = member as? Method? ?: error("Current hooked Member is not a Method")

    /**
     * 获取当前 Hook 对象的构造方法
     * @return [Constructor]
     * @throws IllegalStateException 如果 [member] 类型不是 [Constructor]
     */
    val constructor get() = member as? Constructor<*>? ?: error("Current hooked Member is not a Constructor")

    /**
     * 获取、设置当前 Hook 对象的 [method] or [constructor] 的返回值
     * @return [Any] or null
     */
    var result: Any?
        get() = param?.result
        set(value) {
            param?.result = value
        }

    /**
     * 获取当前回调方法体范围内的数据存储实例
     * @return [Bundle]
     */
    val dataExtra get() = dataExtras[paramId] ?: Bundle().apply { dataExtras[paramId] = this }

    /**
     * 判断是否存在设置过的方法调用抛出异常
     * @return [Boolean]
     */
    val hasThrowable get() = param?.hasThrowable

    /**
     * 获取设置的方法调用抛出异常
     * @return [Throwable] or null
     */
    val throwable get() = param?.throwable

    /**
     * 向 Hook APP 抛出异常
     *
     * 使用 [hasThrowable] 判断当前是否存在被抛出的异常
     *
     * 使用 [throwable] 获取当前设置的方法调用抛出异常
     *
     * - 仅会在回调方法的 [MemberHookCreator.before] or [MemberHookCreator.after] 中生效
     *
     * - 设置后会同时执行 [resultNull] 方法并将异常抛出给当前 Hook APP
     * @return [Throwable] or null
     * @throws Throwable
     */
    fun Throwable.throwToApp() {
        param?.throwable = this
        YLog.innerE(message ?: "", this)
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
    inline fun <reified T> instance() = instance as? T? ?: error("HookParam instance cannot cast to ${classOf<T>().name}")

    /**
     * 获取当前 Hook 实例的对象 [T]
     * @return [T] or null
     */
    inline fun <reified T> instanceOrNull() = instanceOrNull as? T?

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
     * 调用自身未进行 Hook 的原始 [Member] 并调用原始参数执行
     * @return [Any] or null
     */
    fun callOriginal() = callOriginal<Any>()

    /**
     * 执行原始 [Member]
     *
     * 调用自身未进行 Hook 的原始 [Member] 并调用原始参数执行
     * @return [T] or null
     */
    @JvmName(name = "callOriginal_Generics")
    fun <T> callOriginal() = invokeOriginal<T>(*args)

    /**
     * 执行原始 [Member]
     *
     * 调用自身未进行 Hook 的原始 [Member] 并自定义 [args] 执行
     * @param args 参数实例
     * @return [Any] or null
     */
    fun invokeOriginal(vararg args: Any?) = invokeOriginal<Any>(*args)

    /**
     * 执行原始 [Member]
     *
     * 调用自身未进行 Hook 的原始 [Member] 并自定义 [args] 执行
     * @param args 参数实例
     * @return [T] or null
     */
    @JvmName(name = "invokeOriginal_Generics")
    fun <T> invokeOriginal(vararg args: Any?) = YukiHookHelper.invokeOriginalMember(member, param?.instance, args) as T?

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 true
     *
     * - 请确保 [result] 类型为 [Boolean]
     */
    fun resultTrue() {
        result = true
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 返回值为 false
     *
     * - 请确保 [result] 类型为 [Boolean]
     */
    fun resultFalse() {
        result = false
    }

    /**
     * 设置当前 Hook 对象方法的 [result] 为 null
     *
     * - 此方法将强制设置方法体的 [result] 为 null
     */
    fun resultNull() {
        result = null
    }

    /**
     * 对方法参数的数组下标进行实例化类
     *
     * - 请使用第一个 [args] 方法来获取 [ArgsIndexCondition]
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
     * - 请使用第二个 [args] 方法来获取 [ArgsModifyer]
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
         * - 请确认目标参数的类型 - 发生错误会返回 null
         * @return [Byte] or null
         */
        fun byte() = cast<Byte?>()

        /**
         * 得到方法参数的实例对象 [Int]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Int] 取不到返回 0
         */
        fun int() = cast() ?: 0

        /**
         * 得到方法参数的实例对象 [Long]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Long] 取不到返回 0L
         */
        fun long() = cast() ?: 0L

        /**
         * 得到方法参数的实例对象 [Short]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Short] 取不到返回 0
         */
        fun short() = cast<Short?>() ?: 0

        /**
         * 得到方法参数的实例对象 [Double]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Double] 取不到返回 0.0
         */
        fun double() = cast() ?: 0.0

        /**
         * 得到方法参数的实例对象 [Float]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Float] 取不到返回 0f
         */
        fun float() = cast() ?: 0f

        /**
         * 得到方法参数的实例对象 [String]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [String] 取不到返回 ""
         */
        fun string() = cast() ?: ""

        /**
         * 得到方法参数的实例对象 [Char]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
         * @return [Char] 取不到返回 ' '
         */
        fun char() = cast() ?: ' '

        /**
         * 得到方法参数的实例对象 [Boolean]
         *
         * - 请确认目标参数的类型 - 发生错误会返回默认值
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
         * - 请确认目标参数的类型 - 发生错误会返回空数组
         * @return [Array] 取不到返回空数组
         */
        inline fun <reified T> array() = cast() ?: arrayOf<T>()

        /**
         * 得到方法参数的实例对象 [List] - 每项类型 [T]
         *
         * - 请确认目标参数的类型 - 发生错误会返回空数组
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
            param?.args?.set(index, any)
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
         * - 请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setTrue() = set(true)

        /**
         * 设置方法参数的实例对象为 false
         *
         * - 请确保目标对象的类型是 [Boolean] 不然会发生意想不到的问题
         */
        fun setFalse() = set(false)

        override fun toString() = "Args of index $index"
    }

    override fun toString() = "HookParam(${super.toString()}) by $param"
}