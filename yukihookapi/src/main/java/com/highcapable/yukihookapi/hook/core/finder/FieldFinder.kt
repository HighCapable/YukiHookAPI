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
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.core.finder

import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import java.lang.reflect.Field

/**
 * Field 查找结果实现类
 *
 * 可在这里处理找到的 [fieldInstance]
 * @param hookClass 当前被 Hook 的 [Class]
 */
class FieldFinder(private val hookClass: Class<*>) {

    /** 当前找到的 [Field] */
    private var fieldInstance: Field? = null

    /** 变量名 */
    var name = ""

    /** 变量类型 */
    var type: Class<*>? = null

    /**
     * 得到变量处理结果
     * @return [Result]
     * @throws NoSuchFieldError 如果找不到变量
     */
    @DoNotUseMethod
    fun find(): Result {
        fieldInstance = when {
            name.isBlank() -> error("Field name cannot be empty")
            else -> ReflectionUtils.findFieldIfExists(hookClass, type?.name, name)
        }
        return Result()
    }

    /**
     * Field 查找结果实现类
     *
     * 可在这里处理找到的 [fieldInstance]
     */
    inner class Result {

        /**
         * 设置变量实例
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @param any 设置的实例内容
         */
        fun set(instance: Any? = null, any: Any?) = fieldInstance?.set(instance, any)

        /**
         * 得到变量实例
         * @param instance 变量所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [Field] or null
         */
        fun <T> get(instance: Any? = null) = fieldInstance?.get(instance) as? T?

        /**
         * 得到变量本身
         * @return [Field] or null
         */
        fun find() = fieldInstance
    }
}