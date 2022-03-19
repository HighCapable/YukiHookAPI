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
@file:Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "OPT_IN_USAGE", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.core.finder

import android.os.SystemClock
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import com.highcapable.yukihookapi.hook.utils.runBlocking
import java.lang.reflect.Field

/**
 * Field 查找类
 *
 * 可通过执行类型查找指定变量
 * @param hookInstance 当前 Hook 实例
 * @param classSet 当前需要查找的 [Class] 实例
 */
class FieldFinder(
    override val hookInstance: YukiHookCreater.MemberHookCreater? = null,
    override val classSet: Class<*>? = null
) : BaseFinder(tag = "Field", hookInstance, classSet) {

    /**
     * [Field] 名称
     *
     * - ❗必须设置
     */
    var name = ""

    /**
     * [Field] 类型
     *
     * - 可不填写类型 - 默认模糊查找并取第一个匹配的 [Field]
     */
    var type: Class<*>? = null

    /**
     * 得到变量处理结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param isBind 是否将结果设置到目标 [YukiHookCreater.MemberHookCreater]
     * @return [Result]
     * @throws IllegalStateException 如果 [name] 没有被设置
     */
    @DoNotUseMethod
    override fun build(isBind: Boolean) = when {
        name.isBlank() -> {
            yLoggerE(msg = "Field name cannot be empty in Class [$classSet] [${hookTag}]")
            Result(isNoSuch = true)
        }
        else -> try {
            runBlocking {
                memberInstance =
                    if (type != null)
                        ReflectionUtils.findFieldIfExists(classSet, type?.name, name)
                    else classSet?.getDeclaredField(name)?.apply { isAccessible = true }
            }.result {
                onHookLogMsg(msg = "Find Field [${memberInstance}] takes ${it}ms [${hookTag}]")
            }
            Result()
        } catch (e: Throwable) {
            Thread {
                SystemClock.sleep(10)
                if (isNotIgnoredNoSuchMemberFailure) yLoggerE(msg = "NoSuchField happend in [$classSet] [${hookTag}]", e = e)
            }.start()
            Result(isNoSuch = true, e)
        }
    }

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Result]
     */
    @DoNotUseMethod
    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    /**
     * [Field] 查找结果实现类
     *
     * @param isNoSuch 是否没有找到变量 - 默认否
     * @param e 错误信息
     */
    inner class Result(private val isNoSuch: Boolean = false, private val e: Throwable? = null) {

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 得到变量实例处理类
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [Instance]
         */
        fun get(instance: Any? = null) = Instance(instance, give()?.get(instance))

        /**
         * 得到变量实例
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [T] or null
         */
        fun <T> of(instance: Any? = null) = get(instance).self as? T?

        /**
         * 得到变量本身
         * @return [Field] or null
         */
        fun give() = memberInstance as? Field?

        /**
         * 监听找不到变量时
         * @param initiate 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoSuchField(initiate: (Throwable) -> Unit): Result {
            if (isNoSuch) initiate(e ?: Throwable())
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
         * @param instance 当前 [Field] 所在类的实例对象
         * @param self 当前 [Field] 自身的实例对象
         */
        inner class Instance(private val instance: Any?, val self: Any?) {

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