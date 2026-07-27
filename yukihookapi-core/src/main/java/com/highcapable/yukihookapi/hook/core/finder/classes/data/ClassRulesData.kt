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
 * This file is created by fankes on 2022/9/5.
 */
@file:Suppress("PropertyName", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.classes.data

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.data.BaseRulesData
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [Class] finder rule data.
 * @param fromPackages the package-name scope entries.
 * @param fullName the fully qualified name.
 * @param simpleName the simple name.
 * @param singleName the standalone name.
 * @param fullNameConditions the fully qualified name conditions.
 * @param simpleNameConditions the simple name conditions.
 * @param singleNameConditions the standalone name conditions.
 * @param isAnonymousClass whether the class is anonymous.
 * @param isNoExtendsClass whether the class has no superclass.
 * @param isNoImplementsClass whether the class implements no interfaces.
 * @param extendsClass the superclass names.
 * @param implementsClass the implemented interface names.
 * @param enclosingClass the enclosing class names.
 * @param memberRules the [Member] finder rule data.
 * @param fieldRules the [Field] finder rule data.
 * @param methodRules the [Method] finder rule data.
 * @param constroctorRules the [Constructor] finder rule data.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal class ClassRulesData internal constructor(
    var fromPackages: MutableList<PackageRulesData> = mutableListOf(),
    var fullName: NameRulesData? = null,
    var simpleName: NameRulesData? = null,
    var singleName: NameRulesData? = null,
    var fullNameConditions: NameConditions? = null,
    var simpleNameConditions: NameConditions? = null,
    var singleNameConditions: NameConditions? = null,
    var isAnonymousClass: Boolean? = null,
    var isNoExtendsClass: Boolean? = null,
    var isNoImplementsClass: Boolean? = null,
    var extendsClass: MutableList<String> = mutableListOf(),
    var implementsClass: MutableList<String> = mutableListOf(),
    var enclosingClass: MutableList<String> = mutableListOf(),
    var memberRules: MutableList<MemberRulesData> = mutableListOf(),
    var fieldRules: MutableList<FieldRulesData> = mutableListOf(),
    var methodRules: MutableList<MethodRulesData> = mutableListOf(),
    var constroctorRules: MutableList<ConstructorRulesData> = mutableListOf()
) : BaseRulesData() {

    /**
     * Creates class-name matching rule data.
     * @param name the package name.
     * @return [NameRulesData]
     */
    internal fun createNameRulesData(name: String) = NameRulesData(name)

    /**
     * Creates package-scope name-filtering rule data.
     * @param name the package name.
     * @return [PackageRulesData]
     */
    internal fun createPackageRulesData(name: String) = PackageRulesData(name)

    /**
     * Gets the standalone name derived from [Class.getSimpleName] and [Class.getName].
     * @param instance the current [Class] instance.
     * @return [String]
     */
    internal fun classSingleName(instance: Class<*>) = instance.simpleName.takeIf { it.isNotBlank() }
        ?: instance.enclosingClass?.let { it.simpleName + instance.name.replace(it.name, newValue = "") } ?: ""

    /**
     * Class-name matching rule data.
     * @param name the package name.
     * @param isOptional whether the rule is optional, false by default.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class NameRulesData internal constructor(var name: String, var isOptional: Boolean = false) {

        /** [Class.getName] */
        internal val TYPE_NAME = 0

        /** [Class.getSimpleName] */
        internal val TYPE_SIMPLE_NAME = 1

        /** [Class.getSimpleName] or [Class.getName] */
        internal val TYPE_SINGLE_NAME = 2

        /**
         * Matches the current [Class] instance.
         * @param instance the current [Class] instance.
         * @param type the comparison type.
         * @return [Boolean]
         */
        internal fun equals(instance: Class<*>, type: Int) = when (type) {
            TYPE_NAME -> instance.name == name
            TYPE_SIMPLE_NAME -> instance.simpleName == name
            TYPE_SINGLE_NAME -> classSingleName(instance) == name
            else -> false
        }

        override fun toString() = "$name optional($isOptional)"
    }

    /**
     * Package-scope name-filtering rule data.
     * @param name the package name.
     * @param isAbsolute whether to require an exact match, false by default.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class PackageRulesData internal constructor(var name: String, var isAbsolute: Boolean = false) {
        override fun toString() = "$name absolute($isAbsolute)"
    }

    override val templates
        get() = arrayOf(
            fromPackages.takeIf { it.isNotEmpty() }?.let { "from:$it" } ?: "",
            fullName?.let { "fullName:[$it]" } ?: "",
            simpleName?.let { "simpleName:[$it]" } ?: "",
            singleName?.let { "singleName:[$it]" } ?: "",
            fullNameConditions?.let { "fullNameConditions:[existed]" } ?: "",
            simpleNameConditions?.let { "simpleNameConditions:[existed]" } ?: "",
            singleNameConditions?.let { "singleNameConditions:[existed]" } ?: "",
            modifiers?.let { "modifiers:${ModifierRules.templates(uniqueValue)}" } ?: "",
            isAnonymousClass?.let { "isAnonymousClass:[$it]" } ?: "",
            isNoExtendsClass?.let { "isNoExtendsClass:[$it]" } ?: "",
            isNoImplementsClass?.let { "isNoImplementsClass:[$it]" } ?: "",
            extendsClass.takeIf { it.isNotEmpty() }?.let { "extendsClass:$it" } ?: "",
            implementsClass.takeIf { it.isNotEmpty() }?.let { "implementsClass:$it" } ?: "",
            enclosingClass.takeIf { it.isNotEmpty() }?.let { "enclosingClass:$it" } ?: "",
            memberRules.takeIf { it.isNotEmpty() }?.let { "memberRules:[${it.size} existed]" } ?: "",
            fieldRules.takeIf { it.isNotEmpty() }?.let { "fieldRules:[${it.size} existed]" } ?: "",
            methodRules.takeIf { it.isNotEmpty() }?.let { "methodRules:[${it.size} existed]" } ?: "",
            constroctorRules.takeIf { it.isNotEmpty() }?.let { "constroctorRules:[${it.size} existed]" } ?: ""
        )

    override val objectName get() = "Class"

    override val isInitialize
        get() = super.isInitialize || fromPackages.isNotEmpty() || fullName != null || simpleName != null || singleName != null ||
            fullNameConditions != null || simpleNameConditions != null || singleNameConditions != null || isAnonymousClass != null ||
            isNoExtendsClass != null || isNoImplementsClass != null || extendsClass.isNotEmpty() || enclosingClass.isNotEmpty() ||
            memberRules.isNotEmpty() || fieldRules.isNotEmpty() || methodRules.isNotEmpty() || constroctorRules.isNotEmpty()

    override fun toString() = "[$fromPackages][$fullName][$simpleName][$singleName][$fullNameConditions][$simpleNameConditions]" +
        "[$singleNameConditions][$modifiers][$isAnonymousClass][$isNoExtendsClass][$isNoImplementsClass][$extendsClass][$implementsClass]" +
        "[$enclosingClass][$memberRules][$fieldRules][$methodRules][$constroctorRules]" + super.toString()
}