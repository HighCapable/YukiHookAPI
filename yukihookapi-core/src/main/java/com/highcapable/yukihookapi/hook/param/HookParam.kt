/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import android.os.Bundle
import com.highcapable.yukihookapi.hook.core.YukiHookCreator
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import java.lang.reflect.Member

/**
 * Target object implementation for hooked methods and constructors.
 * @param dataExtraStorage the storage shared by the current callback scope.
 * @param hooker the hooker owning this invocation's registrations.
 * @param param the hook result callback interface.
 */
class HookParam private constructor(
    dataExtraStorage: Bundle,
    hooker: YukiHookCreator.MemberHooker<HookParam>,
    private val param: ClassicHookerBridge.Param
) : HookInvocation(dataExtraStorage, hooker, param) {

    internal companion object {

        /**
         * Creates a new [HookParam] from the given member hooker.
         * @param dataExtraStorage the storage shared by the current callback scope.
         * @param hooker the hooker owning this invocation's registrations.
         * @param param the hook result callback interface.
         * @return [HookParam]
         */
        fun from(
            dataExtraStorage: Bundle,
            hooker: YukiHookCreator.MemberHooker<HookParam>,
            param: ClassicHookerBridge.Param
        ) = HookParam(dataExtraStorage, hooker, param)
    }

    /**
     * Gets the argument array of the currently hooked method or constructor.
     *
     * Each array element has the default type [Any]. Use [args] to access [ArgsEditor.cast].
     * @see arg
     * @see firstArg
     * @see lastArg
     * @return [Array]
     */
    val args get() = param.args

    /**
     * Gets or sets the result of the currently hooked method or constructor.
     * @return [Any] or null.
     */
    var result: Any?
        get() = param.result
        set(value) {
            param.result = value
        }

    /**
     * Gets whether an exception has been set for the invocation.
     * @return [Boolean]
     */
    val hasThrowable: Boolean get() = param.throwable != null

    /**
     * Gets the exception set for the invocation.
     * @return [Throwable] or null.
     */
    val throwable get() = param.throwable

    /**
     * Throws an exception to the current host.
     *
     * - Use [hasThrowable] to check whether an exception is currently set.
     * - Use [throwable] to get the exception set for the invocation.
     *
     * - This API is effective only in [YukiHookCreator.ClassicMemberHooker.before] or [YukiHookCreator.ClassicMemberHooker.after] callbacks.
     * - Setting the exception also calls [result] to null and throws it to the current host.
     * @receiver the exception to throw to the current host.
     * @return [Throwable] or null.
     * @throws Throwable
     */
    fun Throwable.throwToHost() {
        param.throwable = this
        YLog.innerE(message ?: "", this)
    }

    /**
     * Gets the result [T] of the currently hooked method or constructor.
     * @return [T] or null.
     * @throws ClassCastException if the result cannot be used as [T].
     */
    inline fun <reified T : Any> result() = result as T?

    /**
     * Gets an argument editor for the currently hooked method or constructor.
     * @see firstArg
     * @see lastArg
     * @param index the argument array index.
     * @return [ArgsEditor]
     */
    fun arg(index: Int) = ArgsEditor(index)

    /**
     * Gets the first index argument editor for the currently hooked method or constructor.
     * @see arg
     * @return [ArgsEditor]
     */
    fun firstArg() = arg(0)

    /**
     * Gets the last index argument editor for the currently hooked method or constructor.
     * @see arg
     * @return [ArgsEditor]
     */
    fun lastArg() = arg(args.lastIndex)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with its original arguments.
     * @see invokeOriginal
     * @return [Any] or null.
     */
    fun callOriginal() = callOriginal<Any>()

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with its original arguments.
     * @see invokeOriginal
     * @return [T] or null.
     * @throws ClassCastException if the result cannot be used as [T].
     */
    @JvmName(name = "callOriginalTyped")
    fun <T> callOriginal() = invokeOriginal<T>(*args)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with custom [args].
     * @see callOriginal
     * @param args the argument instances.
     * @return [Any] or null.
     */
    fun invokeOriginal(vararg args: Any?) = invokeOriginal<Any>(*args)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with custom [args].
     * @see callOriginal
     * @param args the argument instances.
     * @return [T] or null.
     * @throws ClassCastException if the result cannot be used as [T].
     */
    @JvmName(name = "invokeOriginalTyped")
    fun <T> invokeOriginal(vararg args: Any?) = YukiHookHelper.invokeOriginal(member, param.instance, args) as T?

    /**
     * Argument editor for the currently hooked method or constructor.
     *
     * - Use the second [args] overload to obtain [ArgsEditor].
     * @param index the argument array index.
     */
    inner class ArgsEditor internal constructor(private val index: Int) {

        /**
         * Gets the argument instance.
         * @return [Any] or null.
         */
        fun get() = args.getOrNull(index)

        /**
         * Gets the argument instance as [T].
         * @return [T] or null.
         * @throws ClassCastException if the argument cannot be used as [T].
         */
        @JvmName("getTyped")
        fun <T> get() = get() as T?

        /**
         * Sets the argument instance.
         * @param any the source instance.
         * @throws IllegalStateException if the target argument array is empty or [index] does not exist.
         */
        fun <T> set(any: T?) {
            require(args.isNotEmpty()) { "Method args is empty, maybe not have args." }
            require(index >= 0) { "Method args index must be >= 0." }
            require(index <= args.lastIndex) { "Method args index out of bounds, max is ${args.lastIndex}." }

            param.args[index] = any
        }

        /**
         * Gets the argument instance as [T].
         * @return [T] or null.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<T>()"))
        fun <T> cast() = get<T>()

        /**
         * Gets the argument instance as [Byte].
         * @return [Byte] or null.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Byte>()"))
        fun byte() = get<Byte>()

        /**
         * Gets the argument instance as [Int].
         * @return [Int] 0 when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Int>() ?: 0"))
        fun int() = get<Int>() ?: 0

        /**
         * Gets the argument instance as [Long].
         * @return [Long] 0L when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Long>() ?: 0L"))
        fun long() = get<Long>() ?: 0L

        /**
         * Gets the argument instance as [Short].
         * @return [Short] 0 when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Short>() ?: 0"))
        fun short() = get<Short>() ?: 0

        /**
         * Gets the argument instance as [Double].
         * @return [Double] 0.0 when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Double>() ?: 0.0"))
        fun double() = get<Double>() ?: 0.0

        /**
         * Gets the argument instance as [Float].
         * @return [Float] 0f when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Float>() ?: 0f"))
        fun float() = get<Float>() ?: 0f

        /**
         * Gets the argument instance as [String].
         * @return [String] an empty string when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<String>() ?: \"\""))
        fun string() = get<String>() ?: ""

        /**
         * Gets the argument instance as [Char].
         * @return [Char] a space character when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Char>() ?: ' '"))
        fun char() = get<Char>() ?: ' '

        /**
         * Gets the argument instance as [Boolean].
         * @return [Boolean] false when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Boolean>() ?: false"))
        fun boolean() = get<Boolean>() ?: false

        /**
         * Gets the argument instance as [Any].
         * @return [Any] or null.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get()"))
        fun any() = get()

        /**
         * Gets the argument instance as an [Array] whose elements are of type [T].
         * @return [Array] an empty array when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<Array<T>>() ?: emptyArray()"))
        inline fun <reified T> array() = get<Array<T>>() ?: emptyArray()

        /**
         * Gets the argument instance as a [List] whose elements are of type [T].
         * @return [List] an empty list when unavailable.
         */
        @Deprecated(message = "Use get instead.", replaceWith = ReplaceWith("get<List<T>>() ?: emptyList()"))
        inline fun <reified T> list() = get<List<T>>() ?: emptyList()

        /** Sets the argument instance to null. */
        @Deprecated(message = "Use set instead.", replaceWith = ReplaceWith("set(null)"))
        fun setNull() = set(null)

        /** Sets the argument instance to true. */
        @Deprecated(message = "Use set instead.", replaceWith = ReplaceWith("set(true)"))
        fun setTrue() = set(true)

        /** Sets the argument instance to false. */
        @Deprecated(message = "Use set instead.", replaceWith = ReplaceWith("set(false)"))
        fun setFalse() = set(false)

        override fun toString() = "ArgsEditor(index=$index, value=${args.getOrNull(index)}) by ${this@HookParam}"
    }

    /**
     * Gets the class of the current hooked receiver instance.
     *
     * - Use [instanceOrNull] with [javaClass] directly.
     * @return [Class] or null.
     */
    @Deprecated(message = "Use instanceOrNull?.javaClass directly.", replaceWith = ReplaceWith("instanceOrNull?.javaClass"))
    val instanceClass get() = instanceOrNull?.javaClass

    /**
     * Gets the argument-index helper for the currently hooked method or constructor.
     * @return [ArgsIndexCondition]
     */
    @Suppress("DEPRECATION")
    @Deprecated(message = "Use firstArg or lastArg instead.")
    fun args() = ArgsIndexCondition()

    /**
     * Gets an argument editor for the currently hooked method or constructor.
     * @return [ArgsEditor]
     */
    @Deprecated(message = "Use arg instead.", replaceWith = ReplaceWith("arg(index)"))
    fun args(index: Int) = ArgsEditor(index)

    /** Sets the [result] of the currently hooked method to true. */
    @Deprecated(message = "Use result instead.", replaceWith = ReplaceWith("result = true"))
    fun resultTrue() {
        result = true
    }

    /** Sets the [result] of the currently hooked method to false. */
    @Deprecated(message = "Use result instead.", replaceWith = ReplaceWith("result = false"))
    fun resultFalse() {
        result = false
    }

    /** Sets the [result] of the currently hooked method to null. */
    @Deprecated(message = "Use result instead.", replaceWith = ReplaceWith("result = null"))
    fun resultNull() {
        result = null
    }

    /** Throws an exception to the current host. */
    @Deprecated(message = "Use throwToHost instead.", replaceWith = ReplaceWith("throwToHost()"))
    fun Throwable.throwToApp() = throwToHost()

    /**
     * Argument-array index helper.
     */
    @Deprecated(message = "Use firstArg or lastArg instead.")
    inner class ArgsIndexCondition internal constructor() {

        /**
         * Gets the first argument of the currently hooked method or constructor.
         * @return [ArgsEditor]
         */
        @Deprecated(message = "Use firstArg instead.")
        fun first() = firstArg()

        /**
         * Gets the last argument of the currently hooked method or constructor.
         * @return [ArgsEditor]
         */
        @Deprecated(message = "Use lastArg instead.")
        fun last() = lastArg()
    }

    override fun toString() = "HookParam(${super.toString()}) by $param"
}