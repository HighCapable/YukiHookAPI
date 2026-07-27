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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import android.os.Bundle
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator.MemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.proxy.YukiHookCallback
import com.highcapable.yukihookapi.hook.log.YLog
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Target object implementation for hooked methods and constructors.
 * @param creatorInstance the [YukiMemberHookCreator] instance.
 * @param paramId the current callback block ID.
 * @param param the Hook result callback interface.
 */
class HookParam private constructor(
    private val creatorInstance: YukiMemberHookCreator,
    private var paramId: String = "",
    private var param: YukiHookCallback.Param? = null
) {

    internal companion object {

        /** Stored data for each callback block. */
        private val dataExtras = mutableMapOf<String, Bundle>()

        /** Whether [HookParam] has executed its first callback event. */
        internal var isCallbackCalled = false

        /**
         * Creates a new [HookParam].
         * @param creatorInstance the [YukiMemberHookCreator] instance.
         * @param paramId the current callback block ID.
         * @param param the Hook result callback interface.
         * @return [HookParam]
         */
        internal fun create(creatorInstance: YukiMemberHookCreator, paramId: String, param: YukiHookCallback.Param) =
            HookParam(creatorInstance, paramId, param)

        /** Marks [HookParam] as having executed its first callback event. */
        internal fun invoke() {
            isCallbackCalled = true
        }
    }

    /**
     * Gets the argument array of the currently hooked [method] or [constructor].
     *
     * Each array element has the default type [Any]. Use [args] to access [ArgsModifyer.cast].
     * @return [Array]
     * @throws IllegalStateException if the object is null.
     */
    val args get() = param?.args ?: error("Current hooked Member args is null")

    /**
     * Gets the current hooked receiver instance.
     *
     * - A static hooked member has no receiver instance.
     *
     * - Use [instanceOrNull] when the current receiver may be null.
     * @return [Any]
     * @throws IllegalStateException if the object is null.
     */
    val instance get() = param?.instance ?: error("HookParam instance got null! Is this a static member?")

    /**
     * Gets the current hooked receiver instance.
     *
     * - A static hooked member has no receiver instance.
     * @return [Any] or null.
     */
    val instanceOrNull get() = param?.instance

    /**
     * Gets the class of the current hooked receiver instance.
     *
     * - A static hooked member has no receiver instance.
     * @return [Class] or null.
     */
    val instanceClass get() = param?.instance?.javaClass

    /**
     * Gets the currently hooked [Member].
     *
     * Use this property when the [Member] type may be either [Method] or [Constructor].
     * @return [Member]
     * @throws IllegalStateException if [member] is null.
     */
    val member get() = param?.member ?: error("Current hooked Member is null")

    /**
     * Gets the currently hooked method.
     * @return [Method]
     * @throws IllegalStateException if [member] is not a [Method].
     */
    val method get() = member as? Method? ?: error("Current hooked Member is not a Method")

    /**
     * Gets the currently hooked constructor.
     * @return [Constructor]
     * @throws IllegalStateException if [member] is not a [Constructor].
     */
    val constructor get() = member as? Constructor<*>? ?: error("Current hooked Member is not a Constructor")

    /**
     * Gets or sets the result of the currently hooked [method] or [constructor].
     * @return [Any] or null.
     */
    var result: Any?
        get() = param?.result
        set(value) {
            param?.result = value
        }

    /**
     * Gets the data storage instance scoped to the current callback block.
     * @return [Bundle]
     */
    val dataExtra get() = dataExtras[paramId] ?: Bundle().apply { dataExtras[paramId] = this }

    /**
     * Gets whether an exception has been set for the invocation.
     * @return [Boolean]
     */
    val hasThrowable get() = param?.hasThrowable

    /**
     * Gets the exception set for the invocation.
     * @return [Throwable] or null.
     */
    val throwable get() = param?.throwable

    /**
     * Throws an exception to the host app.
     *
     * Use [hasThrowable] to check whether an exception is currently set.
     *
     * Use [throwable] to get the exception set for the invocation.
     *
     * - This API is effective only in [MemberHookCreator.before] or [MemberHookCreator.after] callbacks.
     *
     * - Setting the exception also calls [resultNull] and throws it to the current host app.
     * @return [Throwable] or null.
     * @throws Throwable
     */
    fun Throwable.throwToApp() {
        param?.throwable = this
        YLog.innerE(message ?: "", this)
    }

    /**
     * Gets the result [T] of the currently hooked [method] or [constructor].
     * @return [T] or null.
     */
    inline fun <reified T : Any> result() = result as? T?

    /**
     * Gets the current hooked receiver instance as [T].
     * @return [T]
     * @throws IllegalStateException if the object is null or is not of type [T].
     */
    inline fun <reified T : Any> instance() = instance as? T? ?: error("HookParam instance cannot cast to ${classOf<T>().name}")

    /**
     * Gets the current hooked receiver instance as [T].
     * @return [T] or null.
     */
    inline fun <reified T : Any> instanceOrNull() = instanceOrNull as? T?

    /**
     * Gets the argument-index helper for the currently hooked [method] or [constructor].
     * @return [ArgsIndexCondition]
     */
    fun args() = ArgsIndexCondition()

    /**
     * Gets an argument helper for the currently hooked [method] or [constructor].
     * @param index the argument array index.
     * @return [ArgsModifyer]
     */
    fun args(index: Int) = ArgsModifyer(index)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with its original arguments.
     * @return [Any] or null.
     */
    fun callOriginal() = callOriginal<Any>()

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with its original arguments.
     * @return [T] or null.
     */
    @JvmName(name = "callOriginal_Generics")
    fun <T> callOriginal() = invokeOriginal<T>(*args)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with custom [args].
     * @param args the argument instances.
     * @return [Any] or null.
     */
    fun invokeOriginal(vararg args: Any?) = invokeOriginal<Any>(*args)

    /**
     * Invokes the original [Member].
     *
     * Invokes the unhooked original [Member] with custom [args].
     * @param args the argument instances.
     * @return [T] or null.
     */
    @JvmName(name = "invokeOriginal_Generics")
    fun <T> invokeOriginal(vararg args: Any?) = YukiHookHelper.invokeOriginalMember(member, param?.instance, args) as T?

    /**
     * Sets the [result] of the currently hooked method to true.
     *
     * - Ensure that [result] is of type [Boolean].
     */
    fun resultTrue() {
        result = true
    }

    /**
     * Sets the [result] of the currently hooked method to false.
     *
     * - Ensure that [result] is of type [Boolean].
     */
    fun resultFalse() {
        result = false
    }

    /**
     * Sets the [result] of the currently hooked method to null.
     *
     * - This method forces the callback [result] to null.
     */
    fun resultNull() {
        result = null
    }

    /**
     * Argument-array index helper.
     *
     * - Use the first [args] overload to obtain [ArgsIndexCondition].
     */
    inner class ArgsIndexCondition internal constructor() {

        /**
         * Gets the first argument of the currently hooked [method] or [constructor].
         * @return [ArgsModifyer]
         */
        fun first() = args(index = 0)

        /**
         * Gets the last argument of the currently hooked [method] or [constructor].
         * @return [ArgsModifyer]
         */
        fun last() = args(index = args.lastIndex)
    }

    /**
     * Argument modification helper.
     *
     * - Use the second [args] overload to obtain [ArgsModifyer].
     * @param index the argument array index.
     */
    inner class ArgsModifyer internal constructor(private val index: Int) {

        /**
         * Gets the argument instance as [T].
         * @return [T] or null.
         */
        fun <T> cast() = runCatching { args[index] as? T? }.getOrNull()

        /**
         * Gets the argument instance as [Byte].
         *
         * - Verify the target argument type. An error returns null.
         * @return [Byte] or null.
         */
        fun byte() = cast<Byte?>()

        /**
         * Gets the argument instance as [Int].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Int] 0 when unavailable.
         */
        fun int() = cast() ?: 0

        /**
         * Gets the argument instance as [Long].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Long] 0L when unavailable.
         */
        fun long() = cast() ?: 0L

        /**
         * Gets the argument instance as [Short].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Short] 0 when unavailable.
         */
        fun short() = cast<Short?>() ?: 0

        /**
         * Gets the argument instance as [Double].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Double] 0.0 when unavailable.
         */
        fun double() = cast() ?: 0.0

        /**
         * Gets the argument instance as [Float].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Float] 0f when unavailable.
         */
        fun float() = cast() ?: 0f

        /**
         * Gets the argument instance as [String].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [String] an empty string when unavailable.
         */
        fun string() = cast() ?: ""

        /**
         * Gets the argument instance as [Char].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Char] a space character when unavailable.
         */
        fun char() = cast() ?: ' '

        /**
         * Gets the argument instance as [Boolean].
         *
         * - Verify the target argument type. An error returns the default value.
         * @return [Boolean] false when unavailable.
         */
        fun boolean() = cast() ?: false

        /**
         * Gets the argument instance as [Any].
         * @return [Any] or null.
         */
        fun any() = cast<Any?>()

        /**
         * Gets the argument instance as an [Array] whose elements are of type [T].
         *
         * - Verify the target argument type. An error returns an empty array.
         * @return [Array] an empty array when unavailable.
         */
        inline fun <reified T> array() = cast() ?: arrayOf<T>()

        /**
         * Gets the argument instance as a [List] whose elements are of type [T].
         *
         * - Verify the target argument type. An error returns an empty list.
         * @return [List] an empty list when unavailable.
         */
        inline fun <reified T> list() = cast() ?: listOf<T>()

        /**
         * Sets the argument instance.
         * @param any the source instance.
         * @throws IllegalStateException if the target argument array is empty or [index] does not exist.
         */
        fun <T> set(any: T?) {
            if (index < 0) error("HookParam Method args index must be >= 0")
            if (args.isEmpty()) error("HookParam Method args is empty, mabe not has args")
            if (index > args.lastIndex) error("HookParam Method args index out of bounds, max is ${args.lastIndex}")
            param?.args?.set(index, any)
        }

        /**
         * Sets the argument instance to null.
         *
         * This method can set any hooked target argument to null.
         */
        fun setNull() = set(null)

        /**
         * Sets the argument instance to true.
         *
         * - Ensure that the target object is of type [Boolean] to avoid unexpected behavior.
         */
        fun setTrue() = set(true)

        /**
         * Sets the argument instance to false.
         *
         * - Ensure that the target object is of type [Boolean] to avoid unexpected behavior.
         */
        fun setFalse() = set(false)

        override fun toString() = "Args of index $index"
    }

    override fun toString() = "HookParam(${super.toString()}) by $param"
}