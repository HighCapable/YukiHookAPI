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
package com.highcapable.yukihookapi.hook.core.reflex.tools

import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.store.ReflectsCacheStore
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.factory.YukiHookHelper
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import kotlin.math.abs

/**
 * 这是一个对 [Class]、[Member] 查找的工具实现类
 */
internal object ReflectionTool {

    /** 当前工具类的标签 */
    private const val TAG = "YukiHookAPI#ReflectionTool"

    /**
     * 使用字符串类名查询 [Class] 是否存在
     * @param name [Class] 完整名称
     * @param loader [Class] 所在的 [ClassLoader]
     * @return [Boolean]
     */
    internal fun hasClassByName(name: String, loader: ClassLoader?) = runCatching { findClassByName(name, loader); true }.getOrNull() ?: false

    /**
     * 使用字符串类名获取 [Class]
     * @param name [Class] 完整名称
     * @param loader [Class] 所在的 [ClassLoader]
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
     */
    internal fun findClassByName(name: String, loader: ClassLoader?): Class<*> {
        val hashCode = ("[$name][$loader]").hashCode()
        return ReflectsCacheStore.findClass(hashCode) ?: runCatching {
            when {
                YukiHookBridge.hasXposedBridge -> runCatching { YukiHookHelper.findClass(name, loader) }
                    .getOrNull() ?: (if (loader == null) Class.forName(name) else loader.loadClass(name))
                loader == null -> Class.forName(name)
                else -> loader.loadClass(name)
            }.also { ReflectsCacheStore.putClass(hashCode, it) }
        }.getOrNull() ?: throw NoClassDefFoundError(
            "Can't find this Class --> " +
                    "name:[$name] in [${loader ?: AppParasitics.baseClassLoader}] by $TAG"
        )
    }

    /**
     * 查找任意 [Field] 或一组 [Field]
     * @param classSet [Field] 所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Field]>
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件或 [FieldRulesData.type] 目标类不存在
     * @throws NoSuchFieldError 如果找不到 [Field]
     */
    internal fun findFields(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        rulesData: FieldRulesData
    ): HashSet<Field> {
        if (rulesData.type == UndefinedType) error("Field match type class is not found")
        if (orderIndex == null && matchIndex == null && rulesData.name.isBlank() && rulesData.modifiers == null && rulesData.type == null)
            error("You must set a condition when finding a Field")
        val hashCode = ("[$orderIndex][$matchIndex][${rulesData.name}][${rulesData.type}][${rulesData.modifiers}][$classSet]").hashCode()
        return ReflectsCacheStore.findFields(hashCode) ?: let {
            val fields = HashSet<Field>()
            classSet?.declaredFields?.apply {
                var typeIndex = -1
                var nameIndex = -1
                var modifyIndex = -1
                var nameCdsIndex = -1
                val typeLastIndex =
                    if (rulesData.type != null && matchIndex != null) filter { rulesData.type == it.type }.lastIndex else -1
                val nameLastIndex =
                    if (rulesData.name.isNotBlank() && matchIndex != null) filter { rulesData.name == it.name }.lastIndex else -1
                val modifyLastIndex =
                    if (rulesData.modifiers != null && matchIndex != null) filter { rulesData.modifiers!!.contains(it) }.lastIndex else -1
                val nameCdsLastIndex =
                    if (rulesData.nameConditions != null && matchIndex != null) filter { rulesData.nameConditions!!.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (rulesData.type != null)
                        conditions = (rulesData.type == it.type).let {
                            if (it) typeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == typeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (typeLastIndex - typeIndex) && matchIndex.second) ||
                                    (typeLastIndex == typeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.name.isNotBlank())
                        conditions = (conditions && rulesData.name == it.name).let {
                            if (it) nameIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameLastIndex - nameIndex) && matchIndex.second) ||
                                    (nameLastIndex == nameIndex && matchIndex.second.not()))
                        }
                    if (rulesData.modifiers != null)
                        conditions = (conditions && rulesData.modifiers!!.contains(it)).let {
                            if (it) modifyIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == modifyIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (modifyLastIndex - modifyIndex) && matchIndex.second) ||
                                    (modifyLastIndex == modifyIndex && matchIndex.second.not()))
                        }
                    if (rulesData.nameConditions != null)
                        conditions = (conditions && rulesData.nameConditions!!.contains(it)).let {
                            if (it) nameCdsIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameCdsIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameCdsLastIndex - nameCdsIndex) && matchIndex.second) ||
                                    (nameCdsLastIndex == nameCdsIndex && matchIndex.second.not()))
                        }
                    if (orderIndex != null) conditions =
                        (conditions && ((orderIndex.first >= 0 && orderIndex.first == p && orderIndex.second) ||
                                (orderIndex.first < 0 && abs(orderIndex.first) == (lastIndex - p) && orderIndex.second) ||
                                (lastIndex == p && orderIndex.second.not()))).also { isMatched = true }
                    if (conditions && isMatched) fields.add(it.apply { isAccessible = true })
                }
            } ?: error("Can't find this Field [${rulesData.name}] because classSet is null")
            fields.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putFields(hashCode, fields) }
                ?: if (rulesData.isFindInSuper && classSet.hasExtends)
                    findFields(classSet.superclass, orderIndex, matchIndex, rulesData)
                else throw NoSuchFieldError(
                    "Can't find this Field --> " +
                            when {
                                orderIndex == null -> ""
                                orderIndex.second.not() -> "orderIndex:[last] "
                                else -> "orderIndex:[${orderIndex.first}] "
                            } +
                            when {
                                matchIndex == null -> ""
                                matchIndex.second.not() -> "matchIndex:[last] "
                                else -> "matchIndex:[${matchIndex.first}] "
                            } +
                            when (rulesData.nameConditions) {
                                null -> ""
                                else -> "nameConditions:${rulesData.nameConditions} "
                            } +
                            "name:[${rulesData.name.takeIf { it.isNotBlank() } ?: "unspecified"}] " +
                            "type:[${rulesData.type ?: "unspecified"}] " +
                            "modifiers:${rulesData.modifiers ?: "[]"} " +
                            "in [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意 [Method] 或一组 [Method]
     * @param classSet [Method] 所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Method]>
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件或 [MethodRulesData.paramTypes] 以及 [MethodRulesData.returnType] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Method]
     */
    internal fun findMethods(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        rulesData: MethodRulesData
    ): HashSet<Method> {
        if (rulesData.returnType == UndefinedType) error("Method match returnType class is not found")
        rulesData.paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Method match paramType[$p] class is not found") }
        if (orderIndex == null && matchIndex == null &&
            rulesData.name.isBlank() && rulesData.modifiers == null &&
            rulesData.paramCount < 0 && rulesData.paramCountRange.isEmpty() &&
            rulesData.paramTypes == null && rulesData.returnType == null
        ) error("You must set a condition when finding a Method")
        val hashCode =
            ("[$orderIndex][$matchIndex][${rulesData.name}][${rulesData.paramCount}][${rulesData.paramTypes.typeOfString()}]" +
                    "[${rulesData.returnType}][${rulesData.modifiers}][$classSet]").hashCode()
        return ReflectsCacheStore.findMethods(hashCode) ?: let {
            val methods = HashSet<Method>()
            classSet?.declaredMethods?.apply {
                var returnTypeIndex = -1
                var paramTypeIndex = -1
                var paramCountIndex = -1
                var paramCountRangeIndex = -1
                var nameIndex = -1
                var modifyIndex = -1
                var nameCdsIndex = -1
                val returnTypeLastIndex =
                    if (rulesData.returnType != null && matchIndex != null) filter { rulesData.returnType == it.returnType }.lastIndex else -1
                val paramCountLastIndex =
                    if (rulesData.paramCount >= 0 && matchIndex != null) filter { rulesData.paramCount == it.parameterTypes.size }.lastIndex else -1
                val paramCountRangeLastIndex = if (rulesData.paramCountRange.isEmpty().not() && matchIndex != null)
                    filter { it.parameterTypes.size in rulesData.paramCountRange }.lastIndex else -1
                val paramTypeLastIndex =
                    if (rulesData.paramTypes != null && matchIndex != null)
                        filter { arrayContentsEq(rulesData.paramTypes, it.parameterTypes) }.lastIndex else -1
                val nameLastIndex =
                    if (rulesData.name.isNotBlank() && matchIndex != null) filter { rulesData.name == it.name }.lastIndex else -1
                val modifyLastIndex =
                    if (rulesData.modifiers != null && matchIndex != null) filter { rulesData.modifiers!!.contains(it) }.lastIndex else -1
                val nameCdsLastIndex =
                    if (rulesData.nameConditions != null && matchIndex != null) filter { rulesData.nameConditions!!.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (rulesData.name.isNotBlank())
                        conditions = (rulesData.name == it.name).let {
                            if (it) nameIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameLastIndex - nameIndex) && matchIndex.second) ||
                                    (nameLastIndex == nameIndex && matchIndex.second.not()))
                        }
                    if (rulesData.returnType != null)
                        conditions = (conditions && rulesData.returnType == it.returnType).let {
                            if (it) returnTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == returnTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (returnTypeLastIndex - returnTypeIndex) && matchIndex.second) ||
                                    (returnTypeLastIndex == returnTypeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.paramCount >= 0)
                        conditions = (conditions && it.parameterTypes.size == rulesData.paramCount).let {
                            if (it) paramCountIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountLastIndex - paramCountIndex) && matchIndex.second) ||
                                    (paramCountLastIndex == paramCountIndex && matchIndex.second.not()))
                        }
                    if (rulesData.paramCountRange.isEmpty().not())
                        conditions = (conditions && it.parameterTypes.size in rulesData.paramCountRange).let {
                            if (it) paramCountRangeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountRangeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountRangeLastIndex - paramCountRangeIndex) && matchIndex.second) ||
                                    (paramCountRangeLastIndex == paramCountRangeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.paramTypes != null)
                        conditions = (conditions && arrayContentsEq(rulesData.paramTypes, it.parameterTypes)).let {
                            if (it) paramTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramTypeLastIndex - paramTypeIndex) && matchIndex.second) ||
                                    (paramTypeLastIndex == paramTypeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.modifiers != null)
                        conditions = (conditions && rulesData.modifiers!!.contains(it)).let {
                            if (it) modifyIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == modifyIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (modifyLastIndex - modifyIndex) && matchIndex.second) ||
                                    (modifyLastIndex == modifyIndex && matchIndex.second.not()))
                        }
                    if (rulesData.nameConditions != null)
                        conditions = (conditions && rulesData.nameConditions!!.contains(it)).let {
                            if (it) nameCdsIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == nameCdsIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (nameCdsLastIndex - nameCdsIndex) && matchIndex.second) ||
                                    (nameCdsLastIndex == nameCdsIndex && matchIndex.second.not()))
                        }
                    if (orderIndex != null) conditions =
                        (conditions && ((orderIndex.first >= 0 && orderIndex.first == p && orderIndex.second) ||
                                (orderIndex.first < 0 && abs(orderIndex.first) == (lastIndex - p) && orderIndex.second) ||
                                (lastIndex == p && orderIndex.second.not()))).also { isMatched = true }
                    if (conditions && isMatched) methods.add(it.apply { isAccessible = true })
                }
            } ?: error("Can't find this Method [${rulesData.name}] because classSet is null")
            methods.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putMethods(hashCode, methods) }
                ?: if (rulesData.isFindInSuper && classSet.hasExtends)
                    findMethods(classSet.superclass, orderIndex, matchIndex, rulesData)
                else throw NoSuchMethodError(
                    "Can't find this Method --> " +
                            when {
                                orderIndex == null -> ""
                                orderIndex.second.not() -> "orderIndex:[last] "
                                else -> "orderIndex:[${orderIndex.first}] "
                            } +
                            when {
                                matchIndex == null -> ""
                                matchIndex.second.not() -> "matchIndex:[last] "
                                else -> "matchIndex:[${matchIndex.first}] "
                            } +
                            when (rulesData.nameConditions) {
                                null -> ""
                                else -> "nameConditions:${rulesData.nameConditions} "
                            } +
                            "name:[${rulesData.name.takeIf { it.isNotBlank() } ?: "unspecified"}] " +
                            "paramCount:[${rulesData.paramCount.takeIf { it >= 0 } ?: "unspecified"}] " +
                            "paramCountRange:[${rulesData.paramCountRange.takeIf { it.isEmpty().not() } ?: "unspecified"}] " +
                            "paramTypes:[${rulesData.paramTypes.typeOfString()}] " +
                            "returnType:[${rulesData.returnType ?: "unspecified"}] " +
                            "modifiers:${rulesData.modifiers ?: "[]"} " +
                            "in [$classSet] " +
                            "by $TAG"
                )
        }
    }

    /**
     * 查找任意 [Constructor] 或一组 [Constructor]
     * @param classSet [Constructor] 所在类
     * @param orderIndex 字节码顺序下标
     * @param matchIndex 字节码筛选下标
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Constructor]>
     * @throws IllegalStateException 如果 [classSet] 为 null 或未设置任何条件或 [ConstructorRulesData.paramTypes] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Constructor]
     */
    internal fun findConstructors(
        classSet: Class<*>?,
        orderIndex: Pair<Int, Boolean>?,
        matchIndex: Pair<Int, Boolean>?,
        rulesData: ConstructorRulesData
    ): HashSet<Constructor<*>> {
        rulesData.paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Constructor match paramType[$p] class is not found") }
        if (orderIndex == null && matchIndex == null &&
            rulesData.modifiers == null && rulesData.paramCount < 0 &&
            rulesData.paramCountRange.isEmpty() && rulesData.paramTypes == null
        ) error("You must set a condition when finding a Constructor")
        val hashCode = ("[$orderIndex][$matchIndex][${rulesData.paramCount}][${rulesData.paramTypes.typeOfString()}]" +
                "[${rulesData.modifiers}][$classSet]").hashCode()
        return ReflectsCacheStore.findConstructors(hashCode) ?: let {
            val constructors = HashSet<Constructor<*>>()
            classSet?.declaredConstructors?.apply {
                var paramTypeIndex = -1
                var paramCountIndex = -1
                var paramCountRangeIndex = -1
                var modifyIndex = -1
                val paramCountLastIndex =
                    if (rulesData.paramCount >= 0 && matchIndex != null) filter { rulesData.paramCount == it.parameterTypes.size }.lastIndex else -1
                val paramCountRangeLastIndex = if (rulesData.paramCountRange.isEmpty().not() && matchIndex != null)
                    filter { it.parameterTypes.size in rulesData.paramCountRange }.lastIndex else -1
                val paramTypeLastIndex =
                    if (rulesData.paramTypes != null && matchIndex != null)
                        filter { arrayContentsEq(rulesData.paramTypes, it.parameterTypes) }.lastIndex else -1
                val modifyLastIndex =
                    if (rulesData.modifiers != null && matchIndex != null) filter { rulesData.modifiers!!.contains(it) }.lastIndex else -1
                forEachIndexed { p, it ->
                    var isMatched = false
                    var conditions = true
                    if (rulesData.paramCount >= 0)
                        conditions = (it.parameterTypes.size == rulesData.paramCount).let {
                            if (it) paramCountIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountLastIndex - paramCountIndex) && matchIndex.second) ||
                                    (paramCountLastIndex == paramCountIndex && matchIndex.second.not()))
                        }
                    if (rulesData.paramCountRange.isEmpty().not())
                        conditions = (conditions && it.parameterTypes.size in rulesData.paramCountRange).let {
                            if (it) paramCountRangeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramCountRangeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramCountRangeLastIndex - paramCountRangeIndex) && matchIndex.second) ||
                                    (paramCountRangeLastIndex == paramCountRangeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.paramTypes != null)
                        conditions = (conditions && arrayContentsEq(rulesData.paramTypes, it.parameterTypes)).let {
                            if (it) paramTypeIndex++
                            isMatched = true
                            it && (matchIndex == null ||
                                    (matchIndex.first >= 0 && matchIndex.first == paramTypeIndex && matchIndex.second) ||
                                    (matchIndex.first < 0 &&
                                            abs(matchIndex.first) == (paramTypeLastIndex - paramTypeIndex) && matchIndex.second) ||
                                    (paramTypeLastIndex == paramTypeIndex && matchIndex.second.not()))
                        }
                    if (rulesData.modifiers != null)
                        conditions = (conditions && rulesData.modifiers!!.contains(it)).let {
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
                    if (conditions && isMatched) constructors.add(it.apply { isAccessible = true })
                }
            } ?: error("Can't find this Constructor because classSet is null")
            return constructors.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putConstructors(hashCode, constructors) }
                ?: if (rulesData.isFindInSuper && classSet.hasExtends)
                    findConstructors(classSet.superclass, orderIndex, matchIndex, rulesData)
                else throw NoSuchMethodError(
                    "Can't find this Constructor --> " +
                            when {
                                orderIndex == null -> ""
                                orderIndex.second.not() -> "orderIndex:[last] "
                                else -> "orderIndex:[${orderIndex.first}] "
                            } +
                            when {
                                matchIndex == null -> ""
                                matchIndex.second.not() -> "matchIndex:[last] "
                                else -> "matchIndex:[${matchIndex.first}] "
                            } +
                            "paramCount:[${rulesData.paramCount.takeIf { it >= 0 } ?: "unspecified"}] " +
                            "paramCountRange:[${rulesData.paramCountRange.takeIf { it.isEmpty().not() } ?: "unspecified"}] " +
                            "paramTypes:[${rulesData.paramTypes.typeOfString()}] " +
                            "modifiers:${rulesData.modifiers ?: "[]"} " +
                            "in [$classSet] " +
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