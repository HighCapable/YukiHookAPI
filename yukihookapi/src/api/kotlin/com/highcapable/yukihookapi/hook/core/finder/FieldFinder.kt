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
 * This file is Created by fankes on 2022/2/4.
 */
@file:Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.finder

import android.os.SystemClock
import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreater
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.type.NameConditions
import com.highcapable.yukihookapi.hook.core.reflex.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.utils.runBlocking
import java.lang.reflect.Field

/**
 * Field 查找类
 *
 * 可通过执行类型查找指定变量
 * @param hookInstance 当前 Hook 实例
 * @param classSet 当前需要查找的 [Class] 实例
 */
class FieldFinder @PublishedApi internal constructor(
    @property:YukiPrivateApi
    override val hookInstance: YukiMemberHookCreater.MemberHookCreater? = null,
    @property:YukiPrivateApi
    override val classSet: Class<*>? = null
) : BaseFinder(tag = "Field", hookInstance, classSet) {

    /** 当前使用的 [classSet] */
    private var usedClassSet = classSet

    /** 是否在未找到后继续在当前 [classSet] 的父类中查找 */
    private var isFindInSuperClass = false

    /** [ModifierRules] 实例 */
    @PublishedApi
    internal var modifiers: ModifierRules? = null

    /** [NameConditions] 实例 */
    @PublishedApi
    internal var nameConditions: NameConditions? = null

    /**
     * 设置 [Field] 名称
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Field]
     */
    var name = ""

    /**
     * 设置 [Field] 类型
     *
     * - ❗只能是 [Class]、[String]、[VariousClass]
     *
     * - 可不填写类型 - 默认模糊查找并取第一个匹配的 [Field]
     */
    var type: Any? = null

    /**
     * 设置 [Field] 标识符筛选条件
     *
     * 可不设置筛选条件 - 默认模糊查找并取第一个匹配的 [Field]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param initiate 方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    inline fun modifiers(initiate: ModifierRules.() -> Unit): IndexTypeCondition {
        modifiers = ModifierRules().apply(initiate)
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 顺序筛选字节码的下标
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * 设置 [Field] 名称
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Field]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 名称
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(value: String): IndexTypeCondition {
        name = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Field] 名称条件
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Field]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param initiate 方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    inline fun name(initiate: NameConditions.() -> Unit): IndexTypeCondition {
        nameConditions = NameConditions().apply(initiate)
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Field] 类型
     *
     * - 可不填写类型 - 默认模糊查找并取第一个匹配的 [Field]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 类型 - ❗只能是 [Class]、[String]、[VariousClass]
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun type(value: Any): IndexTypeCondition {
        type = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置在 [classSet] 的所有父类中查找当前 [Field]
     *
     * - ❗若当前 [classSet] 的父类较多可能会耗时 - API 会自动循环到父类继承是 [Any] 前的最后一个类
     * @param isOnlySuperClass 是否仅在当前 [classSet] 的父类中查找 - 若父类是 [Any] 则不会生效
     */
    fun superClass(isOnlySuperClass: Boolean = false) {
        isFindInSuperClass = true
        if (isOnlySuperClass && classSet?.hasExtends == true) usedClassSet = classSet.superclass
    }

    /**
     * 得到变量处理结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param isBind 是否将结果设置到目标 [YukiMemberHookCreater.MemberHookCreater]
     * @return [Result]
     * @throws IllegalStateException 如果 [name] 没有被设置
     */
    @YukiPrivateApi
    override fun build(isBind: Boolean) = try {
        if (classSet != null) {
            runBlocking {
                memberInstance = ReflectionTool.findField(
                    usedClassSet, orderIndex,
                    matchIndex, name,
                    modifiers, nameConditions,
                    type.compat(), isFindInSuperClass
                )
            }.result { onHookLogMsg(msg = "Find Field [${memberInstance}] takes ${it}ms [${hookTag}]") }
            Result()
        } else Result(isNoSuch = true, Throwable("classSet is null"))
    } catch (e: Throwable) {
        Thread {
            /** 延迟使得方法取到返回值 */
            SystemClock.sleep(1)
            onFailureMsg(throwable = e)
        }.start()
        Result(isNoSuch = true, e)
    }

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Result]
     */
    @YukiPrivateApi
    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    /**
     * [Field] 查找结果实现类
     *
     * @param isNoSuch 是否没有找到变量 - 默认否
     * @param e 错误信息
     */
    inner class Result internal constructor(@PublishedApi internal val isNoSuch: Boolean = false, private val e: Throwable? = null) {

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 得到变量实例处理类
         *
         * - ❗如果目标对象不是静态 - 你必须设置 [instance]
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [Instance]
         */
        fun get(instance: Any? = null) = try {
            Instance(instance, give()?.get(instance))
        } catch (e: Throwable) {
            onFailureMsg(msg = "Try to get field instance failed", throwable = e)
            Instance(instance, self = null)
        }

        /**
         * 得到变量本身
         * @return [Field] or null
         */
        fun give() = memberInstance as? Field?

        /**
         * 监听找不到变量时
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoSuchField(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(e ?: Throwable())
            return this
        }

        /**
         * 忽略任何错误发出的警告
         *
         * - 若 [isNotIgnoredNoSuchMemberFailure] 为 false 则自动忽略
         * @return [Result] 可继续向下监听
         */
        fun ignoredError(): Result {
            isShutErrorPrinting = true
            return this
        }

        /**
         * [Field] 实例变量处理类
         *
         * - ❗请使用 [get] 方法来获取 [Instance]
         * @param instance 当前 [Field] 所在类的实例对象
         * @param self 当前 [Field] 自身的实例对象
         */
        inner class Instance internal constructor(private val instance: Any?, val self: Any?) {

            /**
             * 得到变量实例
             * @return [T] or null
             */
            fun <T> cast() = self as? T?

            /**
             * 得到变量的 [Byte] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回 null
             * @return [Byte] or null
             */
            fun byte() = cast<Byte?>()

            /**
             * 得到变量的 [Int] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Int] 取不到返回 0
             */
            fun int() = cast() ?: 0

            /**
             * 得到变量的 [Long] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Long] 取不到返回 0L
             */
            fun long() = cast() ?: 0L

            /**
             * 得到变量的 [Short] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Short] 取不到返回 0
             */
            fun short() = cast<Short?>() ?: 0

            /**
             * 得到变量的 [Double] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Double] 取不到返回 0.0
             */
            fun double() = cast() ?: 0.0

            /**
             * 得到变量的 [Float] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Float] 取不到返回 0f
             */
            fun float() = cast() ?: 0f

            /**
             * 得到变量的 [String] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [String] 取不到返回 ""
             */
            fun string() = cast() ?: ""

            /**
             * 得到变量的 [Char] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Char] 取不到返回 ' '
             */
            fun char() = cast() ?: ' '

            /**
             * 得到变量的 [Boolean] 实例
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回默认值
             * @return [Boolean] 取不到返回 false
             */
            fun boolean() = cast() ?: false

            /**
             * 得到变量的 [Any] 实例
             * @return [Any] or null
             */
            fun any() = cast<Any?>()

            /**
             * 得到变量的 [Array] 实例 - 每项类型 [T]
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回空数组
             * @return [Array] 取不到返回空数组
             */
            inline fun <reified T> array() = cast() ?: arrayOf<T>()

            /**
             * 得到变量的 [List] 实例 - 每项类型 [T]
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回空数组
             * @return [List] 取不到返回空数组
             */
            inline fun <reified T> list() = cast() ?: listOf<T>()

            /**
             * 设置变量实例
             * @param any 设置的实例内容
             */
            fun set(any: Any?) = give()?.set(instance, any)

            /**
             * 设置变量实例为 true
             *
             * - ❗请确保示例对象类型为 [Boolean]
             */
            fun setTrue() = set(true)

            /**
             * 设置变量实例为 true
             *
             * - ❗请确保示例对象类型为 [Boolean]
             */
            fun setFalse() = set(false)

            /** 设置变量实例为 null */
            fun setNull() = set(null)

            override fun toString() =
                "[${self?.javaClass?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}] value \"$self\""
        }
    }
}