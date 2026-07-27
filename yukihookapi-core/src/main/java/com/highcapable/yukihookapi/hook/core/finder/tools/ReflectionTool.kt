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
 * This file is created by fankes on 2022/3/27.
 */
@file:Suppress("KotlinConstantConditions", "KDocUnresolvedReference", "DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.tools

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
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
 * Utility implementation for finding [Class] and [Member] instances.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal object ReflectionTool {

    /** The tag for this utility. */
    private const val TAG = "${YukiHookAPI.TAG}#ReflectionTool"

    /**
     * The [ClassLoader] used by this utility.
     * @return [ClassLoader]
     */
    private val currentClassLoader get() = AppParasitics.baseClassLoader

    /**
     * In-memory cache implementation.
     */
    private object MemoryCache {

        /** Cached [Class] name lists. */
        val dexClassListData = mutableMapOf<String, List<String>>()

        /** Cached [Class] instances. */
        val classData = mutableMapOf<String, Class<*>?>()
    }

    /**
     * Lists all [Class] names under the current [ClassLoader].
     * @param loader the current [ClassLoader].
     * @return [List]<[String]>
     * @throws IllegalStateException if [loader] is not a [BaseDexClassLoader].
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
     * Checks whether a [Class] exists by its string name.
     * @param name the fully qualified [Class] name.
     * @param loader the [ClassLoader] containing the [Class].
     * @return [Boolean]
     */
    internal fun hasClassByName(name: String, loader: ClassLoader?) = runCatching { findClassByName(name, loader); true }.getOrNull() ?: false

    /**
     * Gets a [Class] by its string name.
     * @param name the fully qualified [Class] name.
     * @param loader the [ClassLoader] containing the [Class].
     * @param initialize whether to initialize the [Class] static block, false by default.
     * @return [Class]
     * @throws NoClassDefFoundError if the [Class] cannot be found or the wrong [ClassLoader] is supplied.
     */
    internal fun findClassByName(name: String, loader: ClassLoader?, initialize: Boolean = false): Class<*> {
        val uniqueCode = "[$name][$loader]"

        /**
         * Gets a [Class] through [Class.forName].
         * @param name the fully qualified [Class] name.
         * @param initialize whether to initialize the [Class] static block.
         * @param loader the [ClassLoader] containing the [Class], [currentClassLoader] by default.
         * @return [Class]
         */
        fun classForName(name: String, initialize: Boolean, loader: ClassLoader? = currentClassLoader) =
            Class.forName(name, initialize, loader)

        /**
         * Loads a [Class] through the default mechanism and [ClassLoader].
         * @return [Class] or null.
         */
        fun loadWithDefaultClassLoader() = if (initialize.not()) loader?.loadClass(name) else classForName(name, initialize, loader)
        return MemoryCache.classData[uniqueCode] ?: runCatching {
            (loadWithDefaultClassLoader() ?: classForName(name, initialize)).also { MemoryCache.classData[uniqueCode] = it }
        }.getOrNull() ?: throw createException(loader ?: currentClassLoader, name = "Class", "name:[$name]")
    }

    /**
     * Finds any [Class] or group of [Class] instances.
     * @param loaderSet the [ClassLoader] containing the classes.
     * @param rulesData the finder rule data.
     * @return [MutableList]<[Class]>
     * @throws IllegalStateException if [loaderSet] is null or no conditions are set.
     * @throws NoClassDefFoundError if no [Class] can be found.
     */
    internal fun findClasses(loaderSet: ClassLoader?, rulesData: ClassRulesData) = rulesData.createResult {
        mutableListOf<Class<*>>().also { classes ->
            /**
             * Starts the finder operation.
             * @param instance the current [Class] instance.
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
                     * Matches [MemberRulesData].
                     * @param size the [Member] count.
                     * @param result the callback receiving whether the rule matches.
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
                     * Checks whether each [Class] in the types exists, meaning no [UndefinedType] is present.
                     * @param type the types to check.
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
                // Separates the package name: `com.demo.Test` to `com.demo` by removing the final dot and simple class name.
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
     * Finds any [Field] or group of [Field] instances.
     * @param classSet the class containing the [Field].
     * @param rulesData the finder rule data.
     * @return [MutableList]<[Field]>
     * @throws IllegalStateException if no conditions are set or the target class in [FieldRulesData.type] does not exist.
     * @throws NoSuchFieldError if no [Field] can be found.
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
     * Finds any [Method] or group of [Method] instances.
     * @param classSet the class containing the [Method].
     * @param rulesData the finder rule data.
     * @return [MutableList]<[Method]>
     * @throws IllegalStateException if no conditions are set or a target class in [MethodRulesData.paramTypes].
     * or [MethodRulesData.returnType] does not exist.
     * @throws NoSuchMethodError if no [Method] can be found.
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
     * Finds any [Constructor] or group of [Constructor] instances.
     * @param classSet the class containing the [Constructor].
     * @param rulesData the finder rule data.
     * @return [MutableList]<[Constructor]>
     * @throws IllegalStateException if no conditions are set or a target class in [ConstructorRulesData.paramTypes] does not exist.
     * @throws NoSuchMethodError if no [Constructor] can be found.
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
     * Compares the relative order of position indices.
     * @param need the current position.
     * @param last the last position.
     * @return [Boolean] whether the comparison succeeds.
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int) = this == null || ((first >= 0 && first == need && second) ||
        (first < 0 && abs(first) == (last - need) && second) || (last == need && second.not()))

    /**
     * Compares the relative order of position indices.
     * @param need the current position.
     * @param last the last position.
     * @param result the callback receiving whether the comparison succeeds.
     */
    private fun Pair<Int, Boolean>?.compare(need: Int, last: Int, result: (Boolean) -> Unit) {
        if (this == null) return
        ((first >= 0 && first == need && second) ||
            (first < 0 && abs(first) == (last - need) && second) ||
            (last == need && second.not())).also(result)
    }

    /**
     * Creates the finder result block.
     * @param result the callback block.
     * @return [T]
     * @throws IllegalStateException if [BaseRulesData.isInitialize] is false.
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
     * Finds a result through [Class.getSuperclass] or throws an exception.
     * @param classSet the containing class.
     * @return [T]
     * @throws NoSuchFieldError propagated from [throwNotFoundError].
     * @throws NoSuchMethodError propagated from [throwNotFoundError].
     * @throws IllegalStateException if the [R] type is invalid.
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
     * Throws an exception when a [Class] or [Member] cannot be found.
     * @param instanceSet the containing [ClassLoader] or [Class].
     * @throws NoClassDefFoundError if no [Class] can be found.
     * @throws NoSuchFieldError if no [Field] can be found.
     * @throws NoSuchMethodError if no [Method] or [Constructor] can be found.
     * @throws IllegalStateException if the [BaseRulesData] type is invalid.
     */
    private fun BaseRulesData.throwNotFoundError(instanceSet: Any?): Nothing = when (this) {
        is FieldRulesData -> throw createException(instanceSet, objectName, *templates)
        is MethodRulesData -> throw createException(instanceSet, objectName, *templates)
        is ConstructorRulesData -> throw createException(instanceSet, objectName, *templates)
        is ClassRulesData -> throw createException(instanceSet ?: currentClassLoader, objectName, *templates)
        else -> error("Type [$this] not allowed")
    }

    /**
     * Creates an exception.
     * @param instanceSet the containing [ClassLoader] or [Class].
     * @param name the instance name.
     * @param content the exception content.
     * @return [Throwable]
     */
    private fun createException(instanceSet: Any?, name: String, vararg content: String): Throwable {
        /**
         * Creates padding based on the length of [Class.getName] and appends `->`.
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
     * Gets the [Member] instances declared in the current [Class].
     * @return [Sequence]<[Member]> or null.
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
     * Gets the [Field] instances declared in the current [Class].
     * @return [Sequence]<[Field]> or null.
     */
    private val Class<*>.existFields
        get() = runCatching { declaredFields.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Fields in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * Gets the [Method] instances declared in the current [Class].
     * @return [Sequence]<[Method]> or null.
     */
    private val Class<*>.existMethods
        get() = runCatching { declaredMethods.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Methods in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * Gets the [Constructor] instances declared in the current [Class].
     * @return [Sequence]<[Constructor]> or null.
     */
    private val Class<*>.existConstructors
        get() = runCatching { declaredConstructors.asSequence() }.onFailure {
            YLog.innerW("Failed to get the declared Constructors in [$this] because got an exception", it)
        }.getOrNull()

    /**
     * Makes all members accessible.
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
     * Checks whether two method or constructor type arrays are equal.
     *
     * Adapted from [Class.arrayContentsEq] in [Class].
     * @param compare the array to compare.
     * @param original the original method or constructor array.
     * @return [Boolean] whether the arrays are equal.
     * @throws IllegalStateException if [VagueType] is configured incorrectly.
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