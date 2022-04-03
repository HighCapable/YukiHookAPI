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
import kotlin.math.abs

/**
 * 这是一个对 [Member] 查找的工具实现类
 */
internal object ReflectionTool {

    /** 当前工具类的标签 */
    private const val TAG = "YukiHookAPI#ReflectionTool"

    /**
     * 查找任意变量
     * @param classSet 变量所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param name 变量名称
     * @param modifiers 变量描述
     * @param type 变量类型
     * @return [Field]
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件
     * @throws NoSuchFieldError 如果找不到变量
     */
    internal fun findField(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        name: String,
        modifiers: ModifierRules?,
        type: Class<*>?
    ): Field {
        if (orderIndex == null && matchIndex == null && name.isBlank() && modifiers == null && type == null)
            error("You must set a condition when finding a Field")
        val hashCode = ("[$orderIndex][$matchIndex][$name][$type][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findField(hashCode) ?: let {
            var field: Field? = null
            classSet?.declaredFields?.apply {
                var typeIndex = -1
                var nameIndex = -1
                var modifyIndex = -1
                val typeLastIndex = if (type != null && matchIndex != null) filter { type == it.type }.lastIndex else -1
                val nameLastIndex = if (name.isNotBlank() && matchIndex != null) filter { name == it.name }.lastIndex else -1
                val modifyLastIndex = if (modifiers != null && matchIndex != null) filter { modifiers.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (type != null)
                        conditions = (type == it.type).let {
                            if (it) typeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == typeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (typeLastIndex - typeIndex) && matchIndex.second) ||
                                    (typeLastIndex == typeIndex && matchIndex.second.not()))
                        }
                    if (name.isNotBlank())
                        conditions = (conditions && name == it.name).let {
                            if (it) nameIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameLastIndex - nameIndex) && matchIndex.second) ||
                                    (nameLastIndex == nameIndex && matchIndex.second.not()))
                        }
                    if (modifiers != null)
                        conditions = (conditions && modifiers.contains(it)).let {
                            if (it) modifyIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == modifyIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (modifyLastIndex - modifyIndex) && matchIndex.second) ||
                                    (modifyLastIndex == modifyIndex && matchIndex.second.not()))
                        }
                    if (orderIndex != null) conditions =
                        (conditions && ((orderIndex.first >= 0 && orderIndex.first == p && orderIndex.second) ||
                                (orderIndex.first < 0 && abs(orderIndex.first) == (lastIndex - p) && orderIndex.second) ||
                                (lastIndex == p && orderIndex.second.not()))).also { isMatched = true }
                    if (conditions && isMatched) {
                        field = it.apply { isAccessible = true }
                        return@apply
                    }
                }
            } ?: error("Can't find this Field [$name] because classSet is null")
            field?.also { MemberCacheStore.putField(hashCode, field) }
                ?: throw NoSuchFieldError(
                    "Can't find this Field --> " +
                            "orderIndex:[${
                                when {
                                    orderIndex == null -> "unspecified"
                                    orderIndex.second.not() -> "last"
                                    else -> orderIndex.first
                                }
                            }] " +
                            "matchIndex:[${
                                when {
                                    matchIndex == null -> "unspecified"
                                    matchIndex.second.not() -> "last"
                                    else -> matchIndex.first
                                }
                            }] " +
                            "name:[${name.takeIf { it.isNotBlank() } ?: "unspecified"}] " +
                            "type:[${type ?: "unspecified"}] " +
                            "modifiers:${modifiers ?: "[]"} " +
                            "in Class [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意方法
     * @param classSet 方法所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param name 方法名称
     * @param modifiers 方法描述
     * @param returnType 方法返回值
     * @param paramCount 方法参数个数
     * @param paramTypes 方法参数类型
     * @return [Method]
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件
     * @throws NoSuchMethodError 如果找不到方法
     */
    internal fun findMethod(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        name: String,
        modifiers: ModifierRules?,
        returnType: Class<*>?,
        paramCount: Int,
        paramTypes: Array<out Class<*>>?
    ): Method {
        if (orderIndex == null && matchIndex == null && name.isBlank() && modifiers == null && paramCount < 0 && paramTypes == null && returnType == null)
            error("You must set a condition when finding a Method")
        val hashCode =
            ("[$orderIndex][$matchIndex][$name][$paramCount][${paramTypes.typeOfString()}][$returnType][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findMethod(hashCode) ?: let {
            var method: Method? = null
            classSet?.declaredMethods?.apply {
                var returnTypeIndex = -1
                var paramTypeIndex = -1
                var paramCountIndex = -1
                var nameIndex = -1
                var modifyIndex = -1
                val returnTypeLastIndex =
                    if (returnType != null && matchIndex != null) filter { returnType == it.returnType }.lastIndex else -1
                val paramCountLastIndex =
                    if (paramCount >= 0 && matchIndex != null) filter { paramCount == it.parameterTypes.size }.lastIndex else -1
                val paramTypeLastIndex =
                    if (paramTypes != null && matchIndex != null) filter { arrayContentsEq(paramTypes, it.parameterTypes) }.lastIndex else -1
                val nameLastIndex = if (name.isNotBlank() && matchIndex != null) filter { name == it.name }.lastIndex else -1
                val modifyLastIndex = if (modifiers != null && matchIndex != null) filter { modifiers.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (name.isNotBlank())
                        conditions = (name == it.name).let {
                            if (it) nameIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameLastIndex - nameIndex) && matchIndex.second) ||
                                    (nameLastIndex == nameIndex && matchIndex.second.not()))
                        }
                    if (returnType != null)
                        conditions = (conditions && returnType == it.returnType).let {
                            if (it) returnTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == returnTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (returnTypeLastIndex - returnTypeIndex) && matchIndex.second) ||
                                    (returnTypeLastIndex == returnTypeIndex && matchIndex.second.not()))
                        }
                    if (paramCount >= 0)
                        conditions = (conditions && it.parameterTypes.size == paramCount).let {
                            if (it) paramCountIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountLastIndex - paramCountIndex) && matchIndex.second) ||
                                    (paramCountLastIndex == paramCountIndex && matchIndex.second.not()))
                        }
                    if (paramTypes != null)
                        conditions = (conditions && arrayContentsEq(paramTypes, it.parameterTypes)).let {
                            if (it) paramTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramTypeLastIndex - paramTypeIndex) && matchIndex.second) ||
                                    (paramTypeLastIndex == paramTypeIndex && matchIndex.second.not()))
                        }
                    if (modifiers != null)
                        conditions = (conditions && modifiers.contains(it)).let {
                            if (it) modifyIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == modifyIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (modifyLastIndex - modifyIndex) && matchIndex.second) ||
                                    (modifyLastIndex == modifyIndex && matchIndex.second.not()))
                        }
                    if (orderIndex != null) conditions =
                        (conditions && ((orderIndex.first >= 0 && orderIndex.first == p && orderIndex.second) ||
                                (orderIndex.first < 0 && abs(orderIndex.first) == (lastIndex - p) && orderIndex.second) ||
                                (lastIndex == p && orderIndex.second.not()))).also { isMatched = true }
                    if (conditions && isMatched) {
                        method = it.apply { isAccessible = true }
                        return@apply
                    }
                }
            } ?: error("Can't find this Method [$name] because classSet is null")
            method?.also { MemberCacheStore.putMethod(hashCode, method) }
                ?: throw NoSuchMethodError(
                    "Can't find this Method --> " +
                            "orderIndex:[${
                                when {
                                    orderIndex == null -> "unspecified"
                                    orderIndex.second.not() -> "last"
                                    else -> orderIndex.first
                                }
                            }] " +
                            "matchIndex:[${
                                when {
                                    matchIndex == null -> "unspecified"
                                    matchIndex.second.not() -> "last"
                                    else -> matchIndex.first
                                }
                            }] " +
                            "name:[${name.takeIf { it.isNotBlank() } ?: "unspecified"}] " +
                            "paramCount:[${paramCount.takeIf { it >= 0 } ?: "unspecified"}] " +
                            "paramTypes:[${paramTypes.typeOfString()}] " +
                            "returnType:[${returnType ?: "unspecified"}] " +
                            "modifiers:${modifiers ?: "[]"} " +
                            "in Class [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意构造方法
     * @param classSet 构造方法所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param modifiers 构造方法描述
     * @param paramCount 构造方法参数个数
     * @param paramTypes 构造方法参数类型
     * @return [Constructor]
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件
     * @throws NoSuchMethodError 如果找不到构造方法
     */
    internal fun findConstructor(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        modifiers: ModifierRules?,
        paramCount: Int,
        paramTypes: Array<out Class<*>>?
    ): Constructor<*> {
        if (orderIndex == null && matchIndex == null && paramCount < 0 && paramTypes == null && modifiers == null)
            error("You must set a condition when finding a Constructor")
        val hashCode = ("[$orderIndex][$matchIndex][$paramCount][${paramTypes.typeOfString()}][$modifiers][$classSet]").hashCode()
        return MemberCacheStore.findConstructor(hashCode) ?: let {
            var constructor: Constructor<*>? = null
            classSet?.declaredConstructors?.apply {
                var paramTypeIndex = -1
                var paramCountIndex = -1
                var modifyIndex = -1
                val paramCountLastIndex =
                    if (paramCount >= 0 && matchIndex != null) filter { paramCount == it.parameterTypes.size }.lastIndex else -1
                val paramTypeLastIndex =
                    if (paramTypes != null && matchIndex != null) filter { arrayContentsEq(paramTypes, it.parameterTypes) }.lastIndex else -1
                val modifyLastIndex = if (modifiers != null && matchIndex != null) filter { modifiers.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (paramCount >= 0)
                        conditions = (it.parameterTypes.size == paramCount).let {
                            if (it) paramCountIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountLastIndex - paramCountIndex) && matchIndex.second) ||
                                    (paramCountLastIndex == paramCountIndex && matchIndex.second.not()))
                        }
                    if (paramTypes != null)
                        conditions = (conditions && arrayContentsEq(paramTypes, it.parameterTypes)).let {
                            if (it) paramTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramTypeLastIndex - paramTypeIndex) && matchIndex.second) ||
                                    (paramTypeLastIndex == paramTypeIndex && matchIndex.second.not()))
                        }
                    if (modifiers != null)
                        conditions = (conditions && modifiers.contains(it)).let {
                            if (it) modifyIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == modifyIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (modifyLastIndex - modifyIndex) && matchIndex.second) ||
                                    (modifyLastIndex == modifyIndex && matchIndex.second.not()))
                        }
                    if (orderIndex != null) conditions =
                        (conditions && ((orderIndex.first >= 0 && orderIndex.first == p && orderIndex.second) ||
                                (orderIndex.first < 0 && abs(orderIndex.first) == (lastIndex - p) && orderIndex.second) ||
                                (lastIndex == p && orderIndex.second.not()))).also { isMatched = true }
                    if (conditions && isMatched) {
                        constructor = it.apply { isAccessible = true }
                        return@apply
                    }
                }
            } ?: error("Can't find this Constructor because classSet is null")
            return constructor?.also { MemberCacheStore.putConstructor(hashCode, constructor) }
                ?: throw NoSuchMethodError(
                    "Can't find this Constructor --> " +
                            "orderIndex:[${
                                when {
                                    orderIndex == null -> "unspecified"
                                    orderIndex.second.not() -> "last"
                                    else -> orderIndex.first
                                }
                            }] " +
                            "matchIndex:[${
                                when {
                                    matchIndex == null -> "unspecified"
                                    matchIndex.second.not() -> "last"
                                    else -> matchIndex.first
                                }
                            }] " +
                            "paramCount:[${
                                paramCount.takeIf { it >= 0 || it == -2 }
                                    ?.toString()?.replace(oldValue = "-2", newValue = "last") ?: "unspecified"
                            }] " +
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