/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 * This file is created by fankes on 2022/3/27.
 */
@file:Suppress("KotlinConstantConditions", "KDocUnresolvedReference")

package com.highcapable.yukihookapi.hook.core.finder.tools

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.core.finder.classes.data.ClassRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.type.defined.VagueType
import com.highcapable.yukihookapi.hook.type.java.DalvikBaseDexClassLoader
import com.highcapable.yukihookapi.hook.type.java.NoClassDefFoundErrorClass
import com.highcapable.yukihookapi.hook.type.java.NoSuchFieldErrorClass
import com.highcapable.yukihookapi.hook.type.java.NoSuchMethodErrorClass
import com.highcapable.yukihookapi.hook.utils.factory.conditions
import com.highcapable.yukihookapi.hook.utils.factory.findLastIndex
import com.highcapable.yukihookapi.hook.utils.factory.lastIndex
import com.highcapable.yukihookapi.hook.utils.factory.let
import com.highcapable.yukihookapi.hook.utils.factory.runOrFalse
import com.highcapable.yukihookapi.hook.utils.factory.takeIf
import com.highcapable.yukihookapi.hook.utils.factory.value
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.AccessibleObject
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.Enumeration
import kotlin.math.abs

/**
 * 这是一个对 [Class]、[Member] 查找的工具实现类
 */
internal object ReflectionTool {

    /** 当前工具类的标签 */
    private const val TAG = "${YukiHookAPI.TAG}#ReflectionTool"

    /**
     * 当前工具类的 [ClassLoader]
     * @return [ClassLoader]
     */
    private val currentClassLoader get() = AppParasitics.baseClassLoader

    /**
     * 内存缓存实例实现
     */
    private object MemoryCache {

        /** 缓存的 [Class] 列表数组 */
        val dexClassListData = mutableMapOf<String, List<String>>()

        /** 缓存的 [Class] 对象数组 */
        val classData = mutableMapOf<String, Class<*>?>()
    }

    /**
     * 写出当前 [ClassLoader] 下所有 [Class] 名称数组
     * @param loader 当前使用的 [ClassLoader]
     * @return [List]<[String]>
     * @throws IllegalStateException 如果 [loader] 不是 [BaseDexClassLoader]
     */
    internal fun findDexClassList(loader: ClassLoader?) = MemoryCache.dexClassListData[loader.toString()]
        ?: DalvikBaseDexClassLoader.field { name = "pathList" }.ignored().get(loader.value().let {
            while (it.value !is BaseDexClassLoader) {
                if (it.value?.parent != null) it.value = it.value?.parent
                else error("ClassLoader [$loader] is not a DexClassLoader")
            }; it.value ?: error("ClassLoader [$loader] load failed")
        }).current(ignored = true)?.field { name = "dexElements" }?.array<Any>()?.flatMap { element ->
            element.current(ignored = true).field { name = "dexFile" }.current(ignored = true)
                ?.method { name = "entries" }?.invoke<Enumeration<String>>()?.toList().orEmpty()
        }.orEmpty().also { if (it.isNotEmpty()) MemoryCache.dexClassListData[loader.toString()] = it }

    /**
     * 使用字符串类名查找 [Class] 是否存在
     * @param name [Class] 完整名称
     * @param loader [Class] 所在的 [ClassLoader]
     * @return [Boolean]
     */
    internal fun hasClassByName(name: String, loader: ClassLoader?) = runCatching { findClassByName(name, loader); true }.getOrNull() ?: false

    /**
     * 使用字符串类名获取 [Class]
     * @param name [Class] 完整名称
     * @param loader [Class] 所在的 [ClassLoader]
     * @param initialize 是否初始化 [Class] 的静态方法块 - 默认否
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class] 或设置了错误的 [ClassLoader]
     */
    internal fun findClassByName(name: String, loader: ClassLoader?, initialize: Boolean = false): Class<*> {
        val uniqueCode = "[$name][$loader]"

        /**
         * 获取 [Class.forName] 的 [Class] 对象
         * @param name [Class] 完整名称
         * @param initialize 是否初始化 [Class] 的静态方法块
         * @param loader [Class] 所在的 [ClassLoader] - 默认为 [currentClassLoader]
         * @return [Class]
         */
        fun classForName(name: String, initialize: Boolean, loader: ClassLoader? = currentClassLoader) =
            Class.forName(name, initialize, loader)

        /**
         * 使用默认方式和 [ClassLoader] 装载 [Class]
         * @return [Class] or null
         */
        fun loadWithDefaultClassLoader() = if (initialize.not()) loader?.loadClass(name) else classForName(name, initialize, loader)
        return MemoryCache.classData[uniqueCode] ?: runCatching {
            (loadWithDefaultClassLoader() ?: classForName(name, initialize)).also { MemoryCache.classData[uniqueCode] = it }
        }.getOrNull() ?: throw createException(loader ?: currentClassLoader, name = "Class", "name:[$name]")
    }

    /**
     * 查找任意 [Class] 或一组 [Class]
     * @param loaderSet 类所在 [ClassLoader]
     * @param rulesData 规则查找数据
     * @return [MutableList]<[Class]>
     * @throws IllegalStateException 如果 [loaderSet] 为 null 或未设置任何条件
     * @throws NoClassDefFoundError 如果找不到 [Class]
     */
    internal fun findClasses(loaderSet: ClassLoader?, rulesData: ClassRulesData) = rulesData.createResult {
        mutableListOf<Class<*>>().also { classes ->
            /**
             * 开始查找作业
             * @param instance 当前 [Class] 实例
             */
            fun startProcess(instance: Class<*>) {
                conditions {
                    fromPackages.takeIf { it.isNotEmpty() }?.also { and(true) }
                    fullName?.also { it.equals(instance, it.TYPE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    simpleName?.also { it.equals(instance, it.TYPE_SIMPLE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    singleName?.also { it.equals(instance, it.TYPE_SINGLE_NAME).also { e -> if (it.isOptional) opt(e) else and(e) } }
                    fullNameConditions?.also { instance.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                    simpleNameConditions?.also { instance.simpleName.also { n -> runCatching { and(it(n.cast(), n)) } } }
                    singleNameConditions?.also { classSingleName(instance).also { n -> runCatching { and(it(n.cast(), n)) } } }
                    modifiers?.also { runCatching { and(it(instance.cast())) } }
                    extendsClass.takeIf { it.isNotEmpty() }?.also { and(instance.hasExtends && it.contains(instance.superclass.name)) }
                    implementsClass.takeIf { it.isNotEmpty() }
                        ?.also { and(instance.interfaces.isNotEmpty() && instance.interfaces.any { e -> it.contains(e.name) }) }
                    enclosingClass.takeIf { it.isNotEmpty() }
                        ?.also { and(instance.enclosingClass != null && it.contains(instance.enclosingClass.name)) }
                    isAnonymousClass?.also { and(instance.isAnonymousClass && it) }
                    isNoExtendsClass?.also { and(instance.hasExtends.not() && it) }
                    isNoImplementsClass?.also { and(instance.interfaces.isEmpty() && it) }
                    /**
                     * 匹配 [MemberRulesData]
                     * @param size [Member] 个数
                     * @param result 回调是否匹配
                     */
                    fun MemberRulesData.matchCount(size: Int, result: (Boolean) -> Unit) {
                        takeIf { it.isInitializeOfMatch }?.also { rule ->
                            rule.conditions {
                                value.matchCount.takeIf { it >= 0 }?.also { and(it == size) }
                                value.matchCountRange.takeIf { it.isEmpty().not() }?.also { and(size in it) }
                                value.matchCountConditions?.also { runCatching { and(it(size.cast(), size)) } }
                            }.finally { result(true) }.without { result(false) }
                        } ?: result(true)
                    }

                    /**
                     * 检查类型中的 [Class] 是否存在 - 即不存在 [UndefinedType]
                     * @param type 类型
                     * @return [Boolean]
                     */
                    fun MemberRulesData.exists(vararg type: Any?): Boolean {
                        if (type.isEmpty()) return true
                        for (i in type.indices) if (type[i] == UndefinedType) {
                            YLog.innerW("$objectName type[$i] mistake, it will be ignored in current conditions")
                            return false
                        }
                        return true
                    }
                    memberRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existMembers?.apply {
                            var numberOfFound = 0
                            if (rule.isInitializeOfSuper) forEach { member ->
                                rule.conditions {
                                    value.modifiers?.also { runCatching { and(it(member.cast())) } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(count()) { and(it) }
                        }
                    }
                    fieldRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existFields?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { field ->
                                rule.conditions {
                                    value.type?.takeIf { value.exists(it) }?.also { and(it == field.type) }
                                    value.name.takeIf { it.isNotBlank() }?.also { and(it == field.name) }
                                    value.modifiers?.also { runCatching { and(it(field.cast())) } }
                                    value.nameConditions?.also { field.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                                    value.typeConditions?.also { field.also { t -> runCatching { and(it(t.type(), t.type)) } } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(count()) { and(it) }
                        }
                    }
                    methodRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existMethods?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { method ->
                                rule.conditions {
                                    value.name.takeIf { it.isNotBlank() }?.also { and(it == method.name) }
                                    value.returnType?.takeIf { value.exists(it) }?.also { and(it == method.returnType) }
                                    value.returnTypeConditions
                                        ?.also { method.also { r -> runCatching { and(it(r.returnType(), r.returnType)) } } }
                                    value.paramCount.takeIf { it >= 0 }?.also { and(method.parameterTypes.size == it) }
                                    value.paramCountRange.takeIf { it.isEmpty().not() }?.also { and(method.parameterTypes.size in it) }
                                    value.paramCountConditions
                                        ?.also { method.parameterTypes.size.also { s -> runCatching { and(it(s.cast(), s)) } } }
                                    value.paramTypes?.takeIf { value.exists(*it) }?.also { and(paramTypesEq(it, method.parameterTypes)) }
                                    value.paramTypesConditions
                                        ?.also { method.also { t -> runCatching { and(it(t.paramTypes(), t.parameterTypes)) } } }
                                    value.modifiers?.also { runCatching { and(it(method.cast())) } }
                                    value.nameConditions?.also { method.name.also { n -> runCatching { and(it(n.cast(), n)) } } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(count()) { and(it) }
                        }
                    }
                    constroctorRules.takeIf { it.isNotEmpty() }?.forEach { rule ->
                        instance.existConstructors?.apply {
                            var numberOfFound = 0
                            if (rule.isInitialize) forEach { constructor ->
                                rule.conditions {
                                    value.paramCount.takeIf { it >= 0 }?.also { and(constructor.parameterTypes.size == it) }
                                    value.paramCountRange.takeIf { it.isEmpty().not() }?.also { and(constructor.parameterTypes.size in it) }
                                    value.paramCountConditions
                                        ?.also { constructor.parameterTypes.size.also { s -> runCatching { and(it(s.cast(), s)) } } }
                                    value.paramTypes?.takeIf { value.exists(*it) }?.also { and(paramTypesEq(it, constructor.parameterTypes)) }
                                    value.paramTypesConditions
                                        ?.also { constructor.also { t -> runCatching { and(it(t.paramTypes(), t.parameterTypes)) } } }
                                    value.modifiers?.also { runCatching { and(it(constructor.cast())) } }
                                }.finally { numberOfFound++ }
                            }.run { rule.matchCount(numberOfFound) { and(it && numberOfFound > 0) } }
                            else rule.matchCount(count()) { and(it) }
                        }
                    }
                }.finally { classes.add(instance) }
            }
            findDexClassList(loaderSet).takeIf { it.isNotEmpty() }?.forEach { className ->
                /** 分离包名 → com.demo.Test → com.demo (获取最后一个 "." + 简单类名的长度) → 由于末位存在 "." 最后要去掉 1 个长度 */
                (if (className.contains("."))
                    className.substring(0, className.length - className.split(".").let { it[it.lastIndex] }.length - 1)
                else className).also { packageName ->
                    if ((fromPackages.isEmpty() || fromPackages.any {
                            if (it.isAbsolute) packageName == it.name else packageName.startsWith(it.name)
                        }) && className.hasClass(loaderSet)
                    ) startProcess(className.toClass(loaderSet))
                }
            }
        }.takeIf { it.isNotEmpty() } ?: throwNotFoundError(loaderSet)
    }

    /**
     * 查找任意 [Field] 或一组 [Field]
     * @param classSet [Field] 所在类
     * @param rulesData 规则查找数据
     * @return [MutableList]<[Field]>
     * @throws IllegalStateException 如果未设置任何条件或 [FieldRulesData.type] 目标类不存在
     * @throws NoSuchFieldError 如果找不到 [Field]
     */
    internal fun findFields(classSet: Class<*>?, rulesData: FieldRulesData) = rulesData.createResult { hasCondition ->
        if (type == UndefinedType) error("Field match type class is not found")
        if (classSet == null) return@createResult mutableListOf()
        if (hasCondition.not()) return@createResult classSet.existFields?.toList()?.toAccessibleMembers() ?: mutableListOf()
        mutableListOf<Field>().also { fields ->
            classSet.existFields?.also { declares ->
                var iType = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                var iTypeCds = -1
                val iLType = type?.let(matchIndex) { e -> declares.findLastIndex { e == it.type } } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.findLastIndex { e == it.name } } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.cast()) } } } ?: -1
                val iLNameCds = nameConditions
                    ?.let(matchIndex) { e -> declares.findLastIndex { it.name.let { n -> runOrFalse { e(n.cast(), n) } } } } ?: -1
                val iLTypeCds = typeConditions?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.type(), it.type) } } } ?: -1
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
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(instance.name.let { n -> runOrFalse { it(n.cast(), n) } }.let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        typeConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.type(), t.type) } }.let { hold ->
                                if (hold) iTypeCds++
                                hold && matchIndex.compare(iTypeCds, iLTypeCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex()) { and(it) }
                    }.finally { fields.add(instance) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.toAccessibleMembers() ?: findSuperOrThrow(classSet)
    }

    /**
     * 查找任意 [Method] 或一组 [Method]
     * @param classSet [Method] 所在类
     * @param rulesData 规则查找数据
     * @return [MutableList]<[Method]>
     * @throws IllegalStateException 如果未设置任何条件或 [MethodRulesData.paramTypes] 以及 [MethodRulesData.returnType] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Method]
     */
    internal fun findMethods(classSet: Class<*>?, rulesData: MethodRulesData) = rulesData.createResult { hasCondition ->
        if (returnType == UndefinedType) error("Method match returnType class is not found")
        if (classSet == null) return@createResult mutableListOf()
        if (hasCondition.not()) return@createResult classSet.existMethods?.toList()?.toAccessibleMembers() ?: mutableListOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Method match paramType[$p] class is not found") }
        mutableListOf<Method>().also { methods ->
            classSet.existMethods?.also { declares ->
                var iReturnType = -1
                var iReturnTypeCds = -1
                var iParamTypes = -1
                var iParamTypesCds = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iParamCountCds = -1
                var iName = -1
                var iModify = -1
                var iNameCds = -1
                val iLReturnType = returnType?.let(matchIndex) { e -> declares.findLastIndex { e == it.returnType } } ?: -1
                val iLReturnTypeCds = returnTypeConditions
                    ?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.returnType(), it.returnType) } } } ?: -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.findLastIndex { e == it.parameterTypes.size } } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.findLastIndex { it.parameterTypes.size in e } } ?: -1
                val iLParamCountCds = paramCountConditions?.let(matchIndex) { e ->
                    declares.findLastIndex { it.parameterTypes.size.let { s -> runOrFalse { e(s.cast(), s) } } }
                } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.findLastIndex { paramTypesEq(e, it.parameterTypes) } } ?: -1
                val iLParamTypesCds = paramTypesConditions
                    ?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.paramTypes(), it.parameterTypes) } } } ?: -1
                val iLName = name.takeIf(matchIndex) { it.isNotBlank() }?.let { e -> declares.findLastIndex { e == it.name } } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.cast()) } } } ?: -1
                val iLNameCds = nameConditions
                    ?.let(matchIndex) { e -> declares.findLastIndex { it.name.let { n -> runOrFalse { e(n.cast(), n) } } } } ?: -1
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
                        returnTypeConditions?.also {
                            and(instance.let { r -> runOrFalse { it(r.returnType(), r.returnType) } }.let { hold ->
                                if (hold) iReturnTypeCds++
                                hold && matchIndex.compare(iReturnTypeCds, iLReturnTypeCds)
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
                        paramCountConditions?.also {
                            and(instance.parameterTypes.size.let { s -> runOrFalse { it(s.cast(), s) } }.let { hold ->
                                if (hold) iParamCountCds++
                                hold && matchIndex.compare(iParamCountCds, iLParamCountCds)
                            })
                        }
                        paramTypes?.also {
                            and(paramTypesEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        paramTypesConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.paramTypes(), t.parameterTypes) } }.let { hold ->
                                if (hold) iParamTypesCds++
                                hold && matchIndex.compare(iParamTypesCds, iLParamTypesCds)
                            })
                        }
                        modifiers?.also {
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        nameConditions?.also {
                            and(instance.name.let { n -> runOrFalse { it(n.cast(), n) } }.let { hold ->
                                if (hold) iNameCds++
                                hold && matchIndex.compare(iNameCds, iLNameCds)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex()) { and(it) }
                    }.finally { methods.add(instance) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.toAccessibleMembers() ?: findSuperOrThrow(classSet)
    }

    /**
     * 查找任意 [Constructor] 或一组 [Constructor]
     * @param classSet [Constructor] 所在类
     * @param rulesData 规则查找数据
     * @return [MutableList]<[Constructor]>
     * @throws IllegalStateException 如果未设置任何条件或 [ConstructorRulesData.paramTypes] 目标类不存在
     * @throws NoSuchMethodError 如果找不到 [Constructor]
     */
    internal fun findConstructors(classSet: Class<*>?, rulesData: ConstructorRulesData) = rulesData.createResult { hasCondition ->
        if (classSet == null) return@createResult mutableListOf()
        if (hasCondition.not()) return@createResult classSet.existConstructors?.toList()?.toAccessibleMembers() ?: mutableListOf()
        paramTypes?.takeIf { it.isNotEmpty() }
            ?.forEachIndexed { p, it -> if (it == UndefinedType) error("Constructor match paramType[$p] class is not found") }
        mutableListOf<Constructor<*>>().also { constructors ->
            classSet.existConstructors?.also { declares ->
                var iParamTypes = -1
                var iParamTypesCds = -1
                var iParamCount = -1
                var iParamCountRange = -1
                var iParamCountCds = -1
                var iModify = -1
                val iLParamCount = paramCount.takeIf(matchIndex) { it >= 0 }
                    ?.let { e -> declares.findLastIndex { e == it.parameterTypes.size } } ?: -1
                val iLParamCountRange = paramCountRange.takeIf(matchIndex) { it.isEmpty().not() }
                    ?.let { e -> declares.findLastIndex { it.parameterTypes.size in e } } ?: -1
                val iLParamCountCds = paramCountConditions?.let(matchIndex) { e ->
                    declares.findLastIndex { it.parameterTypes.size.let { s -> runOrFalse { e(s.cast(), s) } } }
                } ?: -1
                val iLParamTypes = paramTypes?.let(matchIndex) { e -> declares.findLastIndex { paramTypesEq(e, it.parameterTypes) } } ?: -1
                val iLParamTypesCds = paramTypesConditions
                    ?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.paramTypes(), it.parameterTypes) } } } ?: -1
                val iLModify = modifiers?.let(matchIndex) { e -> declares.findLastIndex { runOrFalse { e(it.cast()) } } } ?: -1
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
                        paramCountConditions?.also {
                            and(instance.parameterTypes.size.let { s -> runOrFalse { it(s.cast(), s) } }.let { hold ->
                                if (hold) iParamCountCds++
                                hold && matchIndex.compare(iParamCountCds, iLParamCountCds)
                            })
                        }
                        paramTypes?.also {
                            and(paramTypesEq(it, instance.parameterTypes).let { hold ->
                                if (hold) iParamTypes++
                                hold && matchIndex.compare(iParamTypes, iLParamTypes)
                            })
                        }
                        paramTypesConditions?.also {
                            and(instance.let { t -> runOrFalse { it(t.paramTypes(), t.parameterTypes) } }.let { hold ->
                                if (hold) iParamTypesCds++
                                hold && matchIndex.compare(iParamTypesCds, iLParamTypesCds)
                            })
                        }
                        modifiers?.also {
                            and(runOrFalse { it(instance.cast()) }.let { hold ->
                                if (hold) iModify++
                                hold && matchIndex.compare(iModify, iLModify)
                            })
                        }
                        orderIndex.compare(index, declares.lastIndex()) { and(it) }
                    }.finally { constructors.add(instance) }
                }
            }
        }.takeIf { it.isNotEmpty() }?.toAccessibleMembers() ?: findSuperOrThrow(classSet)
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
    private inline fun <reified T, R : BaseRulesData> R.createResult(result: R.(hasCondition: Boolean) -> T) =
        result(when (this) {
            is FieldRulesData -> isInitialize
            is MethodRulesData -> isInitialize
            is ConstructorRulesData -> isInitialize
            is ClassRulesData -> isInitialize
            else -> false
        })

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
        is FieldRulesData -> throw createException(instanceSet, objectName, *templates)
        is MethodRulesData -> throw createException(instanceSet, objectName, *templates)
        is ConstructorRulesData -> throw createException(instanceSet, objectName, *templates)
        is ClassRulesData -> throw createException(instanceSet ?: currentClassLoader, objectName, *templates)
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
     * 获取当前 [Class] 中存在的 [Member] 数组
     * @return [Sequence]<[Member]> or null
     */
    private val Class<*>.existMembers
        get() = runCatching {
            mutableListOf<Member>().apply {
                addAll(declaredFields.toList())
                addAll(declaredMethods.toList())
                addAll(declaredConstructors.toList())
            }.asSequence()
        }.onFailure {
            YLog.innerW("Failed to get the declared Members in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * 获取当前 [Class] 中存在的 [Field] 数组
     * @return [Sequence]<[Field]> or null
     */
    private val Class<*>.existFields
        get() = runCatching { declaredFields.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Fields in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * 获取当前 [Class] 中存在的 [Method] 数组
     * @return [Sequence]<[Method]> or null
     */
    private val Class<*>.existMethods
        get() = runCatching { declaredMethods.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Methods in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * 获取当前 [Class] 中存在的 [Constructor] 数组
     * @return [Sequence]<[Constructor]> or null
     */
    private val Class<*>.existConstructors
        get() = runCatching { declaredConstructors.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Constructors in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * 批量允许访问内部方法
     * @return [MutableList]<[T]>
     */
    private inline fun <reified T : AccessibleObject> List<T>.toAccessibleMembers() =
        mutableListOf<T>().also { list ->
            forEach { member ->
                runCatching {
                    member.isAccessible = true
                    list.add(member)
                }.onFailure { YLog.innerW("Failed to access [$member] because got an exception", it) }
            }
        }

    /**
     * 判断两个方法、构造方法类型数组是否相等
     *
     * 复制自 [Class] 中的 [Class.arrayContentsEq]
     * @param compare 用于比较的数组
     * @param original 方法、构造方法原始数组
     * @return [Boolean] 是否相等
     * @throws IllegalStateException 如果 [VagueType] 配置不正确
     */
    private fun paramTypesEq(compare: Array<out Any>?, original: Array<out Any>?): Boolean {
        return when {
            (compare == null && original == null) || (compare?.isEmpty() == true && original?.isEmpty() == true) -> true
            (compare == null && original != null) || (compare != null && original == null) || (compare?.size != original?.size) -> false
            else -> {
                if (compare == null || original == null) return false
                if (compare.all { it == VagueType }) error("The number of VagueType must be at least less than the count of paramTypes")
                for (i in compare.indices) if ((compare[i] !== VagueType) && (compare[i] !== original[i])) return false
                true
            }
        }
    }
}