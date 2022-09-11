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

import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.store.ReflectsCacheStore
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.java.NoClassDefFoundErrorClass
import com.highcapable.yukihookapi.hook.type.java.NoSuchFieldErrorClass
import com.highcapable.yukihookapi.hook.type.java.NoSuchMethodErrorClass
import com.highcapable.yukihookapi.hook.utils.conditions
import com.highcapable.yukihookapi.hook.utils.let
import com.highcapable.yukihookapi.hook.utils.takeIf
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
        }.getOrNull() ?: throw createException(loader ?: AppParasitics.baseClassLoader, name = "Class", "name:[$name]")
    }

    /**
     * 查找任意 [Field] 或一组 [Field]
     * @param classSet [Field] 所在类
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Field]>
     * @throws IllegalStateException 如果未设置任何条件或 [FieldRulesData.type] 目标类不存在
     * @throws NoSuchFieldError 如果找不到 [Field]
     */
    internal fun findFields(classSet: Class<*>?, rulesData: FieldRulesData) = rulesData.createResult {
        if (type == UndefinedType) error("Field match type class is not found")
        if (classSet == null) return@createResult hashSetOf()
        ReflectsCacheStore.findFields(hashCode(classSet)) ?: hashSetOf<Field>().also { fields ->
            classSet.existFields?.also { declares ->
                var iType = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                val iLType = type?.let(matchIndex) { e -> declares.filter { e == it.type }.lastIndex } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.filter { e == it.name }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { e.contains(it) }.lastIndex } ?: -1
                val iLNameCds = nameConditions?.let(matchIndex) { e -> declares.filter { e.contains(it) }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        type?.also {
                            and((it == instance.type).let { hold ->
                                if (hold) iType++
                                hold && matchIndex.compare(iType, iLType)
                            })
                        }
                        name.takeIf { it.isNotBlank() }?.also {
                            and((it == instance.name).let { hold ->
                                if (hold) iName++
                                hold && matchIndex.compare(iName, iLName)
                            })
                        }
                        modifiers?.also {
                            and(it.contains(instance).let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(it.contains(instance).let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { fields.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putFields(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * 查找任意 [Method] 或一组 [Method]
     * @param classSet [Method] 所在类
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Method]>
     * @throws IllegalStateException 如果未设置任何条件或 [MethodRulesData.paramTypes] 以及 [MethodRulesData.returnType] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Method]
     */
    internal fun findMethods(classSet: Class<*>?, rulesData: MethodRulesData) = rulesData.createResult {
        if (returnType == UndefinedType) error("Method match returnType class is not found")
        if (classSet == null) return@createResult hashSetOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Method match paramType[$p] class is not found") }
        ReflectsCacheStore.findMethods(hashCode(classSet)) ?: hashSetOf<Method>().also { methods ->
            classSet.existMethods?.also { declares ->
                var iReturnType = -1
                var iParamTypes = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                val iLReturnType = returnType?.let(matchIndex) { e -> declares.filter { e == it.returnType }.lastIndex } ?: -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.filter { e == it.parameterTypes.size }.lastIndex } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.filter { it.parameterTypes.size in e }.lastIndex } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.filter { arrayContentsEq(e, it.parameterTypes) }.lastIndex } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.filter { e == it.name }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { e.contains(it) }.lastIndex } ?: -1
                val iLNameCds = nameConditions?.let(matchIndex) { e -> declares.filter { e.contains(it) }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        name.takeIf { it.isNotBlank() }?.also {
                            and((it == instance.name).let { hold ->
                                if (hold) iName++
                                hold && matchIndex.compare(iName, iLName)
                            })
                        }
                        returnType?.also {
                            and((it == instance.returnType).let { hold ->
                                if (hold) iReturnType++
                                hold && matchIndex.compare(iReturnType, iLReturnType)
                            })
                        }
                        paramCount.takeIf { it >= 0 }?.also {
                            and((instance.parameterTypes.size == it).let { hold ->
                                if (hold) iParamCount++
                                hold && matchIndex.compare(iParamCount, iLParamCount)
                            })
                        }
                        paramCountRange.takeIf { it.isEmpty().not() }?.also {
                            and((instance.parameterTypes.size in it).let { hold ->
                                if (hold) iParamCountRange++
                                hold && matchIndex.compare(iParamCountRange, iLParamCountRange)
                            })
                        }
                        paramTypes?.also {
                            and(arrayContentsEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        modifiers?.also {
                            and(it.contains(instance).let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(it.contains(instance).let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { methods.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putMethods(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * 查找任意 [Constructor] 或一组 [Constructor]
     * @param classSet [Constructor] 所在类
     * @param rulesData 规则查询数据
     * @return [HashSet]<[Constructor]>
     * @throws IllegalStateException 如果未设置任何条件或 [ConstructorRulesData.paramTypes] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Constructor]
     */
    internal fun findConstructors(classSet: Class<*>?, rulesData: ConstructorRulesData) = rulesData.createResult {
        if (classSet == null) return@createResult hashSetOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Constructor match paramType[$p] class is not found") }
        ReflectsCacheStore.findConstructors(hashCode(classSet)) ?: hashSetOf<Constructor<*>>().also { constructors ->
            classSet.existConstructors?.also { declares ->
                var iParamTypes = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iModify = -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.filter { e == it.parameterTypes.size }.lastIndex } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.filter { it.parameterTypes.size in e }.lastIndex } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.filter { arrayContentsEq(e, it.parameterTypes) }.lastIndex } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.filter { e.contains(it) }.lastIndex } ?: -1
                declares.forEachIndexed { index, instance ->
                    conditions {
                        paramCount.takeIf { it >= 0 }?.also {
                            and((instance.parameterTypes.size == it).let { hold ->
                                if (hold) iParamCount++
                                hold && matchIndex.compare(iParamCount, iLParamCount)
                            })
                        }
                        paramCountRange.takeIf { it.isEmpty().not() }?.also {
                            and((instance.parameterTypes.size in it).let { hold ->
                                if (hold) iParamCountRange++
                                hold && matchIndex.compare(iParamCountRange, iLParamCountRange)
                            })
                        }
                        paramTypes?.also {
                            and(arrayContentsEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        modifiers?.also {
                            and(it.contains(instance).let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex) { and(it) }
                    }.finally { constructors.add(instance.apply { isAccessible = true }) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.also { ReflectsCacheStore.putConstructors(hashCode(classSet), it) } ?: findSuperOrThrow(classSet)
    }

    /**
     * 比较位置下标的前后顺序
     * @param need 当前位置
     * @param last 最后位置
     * @return [Boolean] 返回是否成立
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int) = this == null || ((first >= 0 && first == need && second) ||
            (first < 0 && abs(first) == (last - need) && second) || (last == need && second.not()))

    /**
     * 比较位置下标的前后顺序
     * @param need 当前位置
     * @param last 最后位置
     * @param result 回调是否成立
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int, result: (Boolean) -> Unit) {
        if (this == null) return
        ((first >= 0 && first == need && second) ||
                (first < 0 && abs(first) == (last - need) && second) ||
                (last == need && second.not())).also(result)
    }

    /**
     * 创建查找结果方法体
     * @param result 回调方法体
     * @return [T]
     * @throws IllegalStateException 如果没有 [BaseRulesData.isInitialize]
     */
    private inline fun <reified T, R : BaseRulesData> R.createResult(result: R.() -> T): T {
        when (this) {
            is FieldRulesData -> isInitialize.not()
            is MethodRulesData -> isInitialize.not()
            is ConstructorRulesData -> isInitialize.not()
            is MemberRulesData -> isInitialize.not()
            else -> true
        }.takeIf { it }?.also { error("You must set a condition when finding a $objectName") }
        return result(this)
    }

    /**
     * 在 [Class.getSuperclass] 中查找或抛出异常
     * @param classSet 所在类
     * @return [T]
     * @throws NoSuchFieldError 继承于方法 [throwNotFoundError] 的异常
     * @throws NoSuchMethodError 继承于方法 [throwNotFoundError] 的异常
     * @throws IllegalStateException 如果 [R] 的类型错误
     */
    private inline fun <reified T, R : MemberRulesData> R.findSuperOrThrow(classSet: Class<*>): T = when (this) {
        is FieldRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findFields(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        is MethodRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findMethods(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        is ConstructorRulesData ->
            if (isFindInSuper && classSet.hasExtends)
                findConstructors(classSet.superclass, rulesData = this) as T
            else throwNotFoundError(classSet)
        else -> error("Type [$this] not allowed")
    }

    /**
     * 抛出找不到 [Class]、[Member] 的异常
     * @param instanceSet 所在 [ClassLoader] or [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class]
     * @throws NoSuchFieldError 如果找不到 [Field]
     * @throws NoSuchMethodError 如果找不到 [Method] or [Constructor]
     * @throws IllegalStateException 如果 [BaseRulesData] 的类型错误
     */
    private fun BaseRulesData.throwNotFoundError(instanceSet: Any?): Nothing = when (this) {
        is FieldRulesData -> throw createException(
            instanceSet, name = "Field",
            "name:[${name.takeIf { it.isNotBlank() } ?: "unspecified"}]",
            nameConditions?.let { "nameConditions:$it" } ?: "",
            "type:[${type ?: "unspecified"}] ",
            "modifiers:${modifiers ?: "[]"} ",
            "name:[${name.takeIf { it.isNotBlank() } ?: "unspecified"}]",
            orderIndex?.let { it.takeIf { it.second }?.let { e -> "orderIndex:[${e.first}]" } ?: "orderIndex:[last]" } ?: "",
            matchIndex?.let { it.takeIf { it.second }?.let { e -> "matchIndex:[${e.first}]" } ?: "matchIndex:[last]" } ?: ""
        )
        is MethodRulesData -> throw createException(
            instanceSet, name = "Method",
            "name:[${name.takeIf { it.isNotBlank() } ?: "unspecified"}]",
            nameConditions?.let { "nameConditions:$it" } ?: "",
            "paramCount:[${paramCount.takeIf { it >= 0 } ?: "unspecified"}]",
            "paramCountRange:[${paramCountRange.takeIf { it.isEmpty().not() } ?: "unspecified"}]",
            "paramTypes:[${paramTypes.typeOfString()}]",
            "returnType:[${returnType ?: "unspecified"}]",
            "modifiers:${modifiers ?: "[]"}",
            orderIndex?.let { it.takeIf { it.second }?.let { e -> "orderIndex:[${e.first}]" } ?: "orderIndex:[last]" } ?: "",
            matchIndex?.let { it.takeIf { it.second }?.let { e -> "matchIndex:[${e.first}]" } ?: "matchIndex:[last]" } ?: ""
        )
        is ConstructorRulesData -> throw createException(
            instanceSet, name = "Constructor",
            "paramCount:[${paramCount.takeIf { it >= 0 } ?: "unspecified"}]",
            "paramCountRange:[${paramCountRange.takeIf { it.isEmpty().not() } ?: "unspecified"}]",
            "paramTypes:[${paramTypes.typeOfString()}]",
            "modifiers:${modifiers ?: "[]"}",
            orderIndex?.let { it.takeIf { it.second }?.let { e -> "orderIndex:[${e.first}]" } ?: "orderIndex:[last]" } ?: "",
            matchIndex?.let { it.takeIf { it.second }?.let { e -> "matchIndex:[${e.first}]" } ?: "matchIndex:[last]" } ?: ""
        )
        else -> error("Type [$this] not allowed")
    }

    /**
     * 创建一个异常
     * @param instanceSet 所在 [ClassLoader] or [Class]
     * @param name 实例名称
     * @param content 异常内容
     * @return [Throwable]
     */
    private fun createException(instanceSet: Any?, name: String, vararg content: String): Throwable {
        /**
         * 获取 [Class.getName] 长度的空格数量并使用 "->" 拼接
         * @return [String]
         */
        fun Class<*>.space(): String {
            var space = ""
            for (i in 0..this.name.length) space += " "
            return "$space -> "
        }
        if (content.isEmpty()) return IllegalStateException("Exception content is null")
        val space = when (name) {
            "Class" -> NoClassDefFoundErrorClass.space()
            "Field" -> NoSuchFieldErrorClass.space()
            "Method", "Constructor" -> NoSuchMethodErrorClass.space()
            else -> error("Invalid Exception type")
        }
        var splicing = ""
        content.forEach { if (it.isNotBlank()) splicing += "$space$it\n" }
        val template = "Can't find this $name in [$instanceSet]:\n${splicing}Generated by $TAG"
        return when (name) {
            "Class" -> NoClassDefFoundError(template)
            "Field" -> NoSuchFieldError(template)
            "Method", "Constructor" -> NoSuchMethodError(template)
            else -> error("Invalid Exception type")
        }
    }

    /**
     * 获取当前 [Class] 中存在的 [Field] 数组
     * @return [Array]<[Field]>
     */
    private val Class<*>.existFields
        get() = runCatching { declaredFields }.onFailure {
            yLoggerW(msg = "Failed to get the declared Fields in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * 获取当前 [Class] 中存在的 [Method] 数组
     * @return [Array]<[Method]>
     */
    private val Class<*>.existMethods
        get() = runCatching { declaredMethods }.onFailure {
            yLoggerW(msg = "Failed to get the declared Methods in [$this] because got an exception\n$it")
        }.getOrNull()

    /**
     * 获取当前 [Class] 中存在的 [Constructor] 数组
     * @return [Array]<[Constructor]>
     */
    private val Class<*>.existConstructors
        get() = runCatching { declaredConstructors }.onFailure {
            yLoggerW(msg = "Failed to get the declared Constructors in [$this] because got an exception\n$it")
        }.getOrNull()

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