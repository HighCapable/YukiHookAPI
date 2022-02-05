/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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

import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import com.highcapable.yukihookapi.hook.utils.runBlocking
import java.lang.reflect.Field

/**
 * Field 查找结果实现类
 *
 * 可在这里处理找到的 [fieldInstance]
 * @param hookInstance 当前 Hook 实例
 * @param hookClass 当前被 Hook 的 [Class]
 */
class FieldFinder(private val hookInstance: YukiHookCreater.MemberHookCreater, private val hookClass: Class<*>) {

    /** 当前找到的 [Field] */
    private var fieldInstance: Field? = null

    /** 变量名 */
    var name = ""

    /** 变量类型 */
    var type: Class<*>? = null

    /**
     * 得到变量处理结果
     * - 此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [Result]
     * @throws IllegalStateException 如果 [name] 没有被设置
     */
    @DoNotUseMethod
    fun build() = if (name.isBlank()) {
        loggerE(msg = "Field name cannot be empty in Class [$hookClass] [${hookInstance.tag}]")
        Result(isNoSuch = true)
    } else try {
        runBlocking {
            fieldInstance = ReflectionUtils.findFieldIfExists(hookClass, type?.name, name)
        }.result {
            hookInstance.onHookLogMsg(msg = "Find Field [${fieldInstance}] takes ${it}ms [${hookInstance.tag}]")
        }
        Result()
    } catch (e: Throwable) {
        loggerE(msg = "NoSuchField happend in [$hookClass] [${hookInstance.tag}]", e = e)
        Result(isNoSuch = true, e)
    }

    /**
     * Field 查找结果实现类
     *
     * 可在这里处理找到的 [fieldInstance]
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
         * 设置变量实例
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @param any 设置的实例内容
         */
        fun set(instance: Any? = null, any: Any?) = give()?.set(instance, any)

        /**
         * 得到变量实例
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [T] or null
         */
        fun <T> get(instance: Any? = null) = give()?.get(instance) as? T?

        /**
         * 得到变量本身
         * @return [Field] or null
         */
        fun give() = fieldInstance

        /**
         * 监听找不到变量时
         *
         * 只会返回第一次和最后一次的错误信息
         * @param initiate 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoSuchField(initiate: (Throwable) -> Unit): Result {
            if (isNoSuch) initiate(e ?: Throwable())
            return this
        }
    }
}