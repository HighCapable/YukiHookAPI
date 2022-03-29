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
 * This file is Created by fankes on 2022/3/27.
 */
package com.highcapable.yukihookapi.hook.utils

import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import com.highcapable.yukihookapi.hook.store.MemberCacheStore
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是一个对 [Member] 查找的工具实现类
 */
internal object ReflectionTool {

    /** 当前工具类的标签 */
    private const val TAG = "YukiHookAPI#ReflectionTool"

    /**
     * 查找任意变量
     * @param classSet 变量所在类
     * @param name 变量名称
     * @param modifiers 变量描述
     * @param type 变量类型
     * @return [Field]
     * @throws IllegalStateException 如果 [classSet] 为 null
     * @throws NoSuchFieldError 如果找不到变量
     */
    internal fun findField(classSet: Class<*>?, name: String, modifiers: ModifierRules?, type: Class<*>?): Field {
        val hashCode = ("[$name][$type][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findField(hashCode) ?: let {
            var field: Field? = null
            run {
                classSet?.declaredFields?.forEach {
                    var conditions = name == it.name
                    if (type != null) conditions = conditions && it.type == type
                    if (modifiers != null) conditions = conditions && modifiers.contains(it)
                    if (conditions) {
                        field = it.apply { isAccessible = true }
                        return@run
                    }
                } ?: error("Can't find this Field [$name] because classSet is null")
            }
            field?.also { MemberCacheStore.putField(hashCode, field) }
                ?: throw NoSuchFieldError(
                    "Can't find this Field --> " +
                            "name:[$name] " +
                            "type:[$type] " +
                            "modifiers:${modifiers ?: "[]"} " +
                            "in Class [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意方法
     * @param classSet 方法所在类
     * @param name 方法名称
     * @param modifiers 方法描述
     * @param returnType 方法返回值
     * @param paramCount 方法参数个数
     * @param paramTypes 方法参数类型
     * @return [Method]
     * @throws IllegalStateException 如果 [classSet] 为 null
     * @throws NoSuchMethodError 如果找不到方法
     */
    internal fun findMethod(
        classSet: Class<*>?,
        name: String,
        modifiers: ModifierRules?,
        returnType: Class<*>?,
        paramCount: Int,
        paramTypes: Array<out Class<*>>?
    ): Method {
        val hashCode = ("[$name][$paramCount][${paramTypes.typeOfString()}][$returnType][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findMethod(hashCode) ?: let {
            var method: Method? = null
            run {
                classSet?.declaredMethods?.forEach {
                    var conditions = name == it.name
                    if (returnType != null) conditions = conditions && it.returnType == returnType
                    if (paramCount >= 0) conditions = conditions && it.parameterTypes.size == paramCount
                    if (paramTypes != null) conditions = conditions && arrayContentsEq(paramTypes, it.parameterTypes)
                    if (modifiers != null) conditions = conditions && modifiers.contains(it)
                    if (conditions) {
                        method = it.apply { isAccessible = true }
                        return@run
                    }
                } ?: error("Can't find this Method [$name] because classSet is null")
            }
            method?.also { MemberCacheStore.putMethod(hashCode, method) }
                ?: throw NoSuchMethodError(
                    "Can't find this Method --> " +
                            "name:[$name] " +
                            "paramCount:[${paramCount.takeIf { it >= 0 } ?: "unspecified"}] " +
                            "paramTypes:[${paramTypes.typeOfString()}] " +
                            "returnType:[$returnType] " +
                            "modifiers:${modifiers ?: "[]"} " +
                            "in Class [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意构造方法
     * @param classSet 构造方法所在类
     * @param modifiers 构造方法描述
     * @param paramCount 构造方法参数个数
     * @param paramTypes 构造方法参数类型
     * @return [Constructor]
     * @throws IllegalStateException 如果 [classSet] 为 null
     * @throws NoSuchMethodError 如果找不到构造方法
     */
    internal fun findConstructor(
        classSet: Class<*>?,
        modifiers: ModifierRules?,
        paramCount: Int,
        paramTypes: Array<out Class<*>>?
    ): Constructor<*> {
        val hashCode = ("[$paramCount][${paramTypes.typeOfString()}][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findConstructor(hashCode) ?: let {
            var constructor: Constructor<*>? = null
            run {
                classSet?.declaredConstructors?.forEach {
                    var conditions = false
                    if (paramCount >= 0) conditions = it.parameterTypes.size == paramCount
                    if (paramTypes != null) conditions = arrayContentsEq(paramTypes, it.parameterTypes)
                    if (modifiers != null) conditions = conditions && modifiers.contains(it)
                    if (conditions) {
                        constructor = it.apply { isAccessible = true }
                        return@run
                    }
                } ?: error("Can't find this Constructor because classSet is null")
            }
            return constructor?.also { MemberCacheStore.putConstructor(hashCode, constructor) }
                ?: throw NoSuchMethodError(
                    "Can't find this Constructor --> " +
                            "paramCount:[${paramCount.takeIf { it >= 0 } ?: "unspecified"}] " +
                            "paramTypes:[${paramTypes.typeOfString()}] " +
                            "modifiers:${modifiers ?: "[]"} " +
                            "in Class [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 获取参数数组文本化内容
     * @return [String]
     */
    private fun Array<out Class<*>>?.typeOfString() =
        StringBuilder("(").also { sb ->
            var isFirst = true
            if (this == null || isEmpty()) return "()"
            forEach {
                if (isFirst) isFirst = false else sb.append(",")
                sb.append(it.canonicalName)
            }
            sb.append(")")
        }.toString()

    /**
     * 判断两个数组是否相等
     *
     * 复制自 [Class] 中的 [Class.arrayContentsEq]
     * @param fArray 第一个数组
     * @param lArray 第二个数组
     * @return [Boolean] 是否相等
     */
    private fun arrayContentsEq(fArray: Array<out Any>?, lArray: Array<out Any>?) = run {
        if (fArray != null) when {
            lArray == null -> fArray.isEmpty()
            fArray.size != lArray.size -> false
            else -> {
                for (i in fArray.indices) if (fArray[i] !== lArray[i]) return@run false
                true
            }
        } else lArray == null || lArray.isEmpty()
    }
}