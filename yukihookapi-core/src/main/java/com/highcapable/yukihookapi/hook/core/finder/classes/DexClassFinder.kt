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
 * This file is created by fankes on 2022/9/4.
 */
@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE", "DEPRECATION", "UseKtx",
    "TYPEALIAS_EXPANSION_DEPRECATION", "DeprecatedCallableAddReplaceWith"
)

package com.highcapable.yukihookapi.hook.core.finder.classes

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.core.content.pm.PackageInfoCompat
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.ClassBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.classes.data.ClassRulesData
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.ConstructorRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.FieldRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MemberRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MethodRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.searchClass
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.utils.factory.await
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [Class] finder.
 *
 * Uses [BaseDexClassLoader] to find a specified [Class] or group of [Class] instances by specified conditions.
 *
 * - This feature is still experimental. Performance and stability issues may remain. Report any issues you encounter and help us improve it.
 * @param name the name identifying the current [Class] cache. Caching is disabled when omitted. Enabling caching requires [async].
 * @param async whether asynchronous lookup is enabled.
 * @param loaderSet the current [ClassLoader] instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class DexClassFinder internal constructor(
    internal var name: String,
    internal var async: Boolean,
    override val loaderSet: ClassLoader?
) : ClassBaseFinder(loaderSet) {

    companion object {

        /** Cache storage file name. */
        private const val CACHE_FILE_NAME = "config_yukihook_cache_obfuscate_classes"

        /**
         * Gets the [Context] of the current runtime environment.
         * @return [Context] or null.
         */
        private val currentContext get() = AppParasitics.hostApplication ?: AppParasitics.currentApplication

        /**
         * Gets the current [SharedPreferences] from [Context].
         * @param versionName the version name. The default is null.
         * @param versionCode the version code. The default is null.
         * @return [SharedPreferences]
         */
        private fun Context.currentSp(versionName: String? = null, versionCode: Long? = null) =
            @Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")
            getSharedPreferences(packageManager?.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                ?.let { "${CACHE_FILE_NAME}_${versionName ?: it.versionName}_${versionCode ?: PackageInfoCompat.getLongVersionCode(it)}" }
                ?: "${CACHE_FILE_NAME}_unknown",
                Context.MODE_PRIVATE)

        /**
         * Clears the [Class] cache of the current [DexClassFinder].
         *
         * Applies to every [DexClassFinder] obtained through [ClassLoader.searchClass] or [PackageParam.searchClass].
         * @param context the current [Context]. The default is [currentContext].
         * @param versionName the version name. The default is null.
         * @param versionCode the version code. The default is null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun clearCache(context: Context? = currentContext, versionName: String? = null, versionCode: Long? = null) {
            context?.currentSp(versionName, versionCode)?.edit()?.clear()?.apply()
                ?: YLog.innerW("Cannot clear cache for DexClassFinder because got null context instance")
        }
    }

    override var rulesData = ClassRulesData()

    /**
     * Sets the full name of the [Class].
     *
     * Only matching [Class.getName] values are found.
     *
     * For example, use com.demo.Test for com.demo.Test.
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var fullName
        get() = rulesData.fullName?.name ?: ""
        set(value) {
            rulesData.fullName = rulesData.createNameRulesData(value)
        }

    /**
     * Sets the simple name of the [Class].
     *
     * Only matching [Class.getSimpleName] values are found.
     *
     * For example, use Test for com.demo.Test.
     *
     * An anonymous class such as com.demo.Test$InnerTest has an empty simple name. Use [singleName] in this case.
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var simpleName
        get() = rulesData.simpleName?.name ?: ""
        set(value) {
            rulesData.simpleName = rulesData.createNameRulesData(value)
        }

    /**
     * Sets the standalone name of the [Class].
     *
     * [Class.getSimpleName] is used first. If it is empty, [Class.getName] is used instead.
     *
     * For example, use Test for com.demo.Test.
     *
     * For an anonymous class such as com.demo.Test$InnerTest, use Test$InnerTest.
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    var singleName
        get() = rulesData.singleName?.name ?: ""
        set(value) {
            rulesData.singleName = rulesData.createNameRulesData(value)
        }

    /**
     * Limits lookup of the current [Class] to specified package names.
     *
     * After this is set, lookup only occurs under package paths beginning with the current [name], which can improve lookup speed.
     *
     * For example:
     *
     * com.demo.test
     *
     * com.demo.test.demo
     *
     * - Setting this parameter to specify the lookup scope is recommended, otherwise lookup may be very slow when there are too many [Class] instances.
     * @param name the package names to specify.
     * @return [FromPackageRules] that can use [FromPackageRules.absolute] to require an exact package name match.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun from(vararg name: String) = FromPackageRules(mutableListOf<ClassRulesData.PackageRulesData>().also {
        name.takeIf { e -> e.isNotEmpty() }?.forEach { e -> it.add(rulesData.createPackageRulesData(e)) }
        if (it.isNotEmpty()) rulesData.fromPackages.addAll(it)
    })

    /**
     * Sets the modifier filtering conditions for the [Class].
     *
     * - The filtering conditions are optional.
     * @param conditions the condition body.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /**
     * Sets the full name of the [Class].
     *
     * Only matching [Class.getName] values are found.
     *
     * For example, use com.demo.Test for com.demo.Test.
     * @param value the name.
     * @return [ClassNameRules] that can use [ClassNameRules.optional] to mark the class name as optional.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun fullName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.fullName = it
        ClassNameRules(it)
    }

    /**
     * Sets the simple name of the [Class].
     *
     * Only matching [Class.getSimpleName] values are found.
     *
     * For example, use Test for com.demo.Test.
     *
     * An anonymous class such as com.demo.Test$InnerTest has an empty simple name. Use [singleName] in this case.
     * @param value the name.
     * @return [ClassNameRules] that can use [ClassNameRules.optional] to mark the class name as optional.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun simpleName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.simpleName = it
        ClassNameRules(it)
    }

    /**
     * Sets the standalone name of the [Class].
     *
     * [Class.getSimpleName] is used first. If it is empty, [Class.getName] is used instead.
     *
     * For example, use Test for com.demo.Test.
     *
     * For an anonymous class such as com.demo.Test$InnerTest, use Test$InnerTest.
     * @param value the name.
     * @return [ClassNameRules] that can use [ClassNameRules.optional] to mark the class name as optional.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun singleName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.singleName = it
        ClassNameRules(it)
    }

    /**
     * Sets the full-name condition for the [Class].
     *
     * Only matching [Class.getName] values are found.
     * @param conditions the condition body.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun fullName(conditions: NameConditions) {
        rulesData.fullNameConditions = conditions
    }

    /**
     * Sets the simple-name condition for the [Class].
     *
     * Only matching [Class.getSimpleName] values are found.
     * @param conditions the condition body.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun simpleName(conditions: NameConditions) {
        rulesData.simpleNameConditions = conditions
    }

    /**
     * Sets the standalone-name condition for the [Class].
     *
     * [Class.getSimpleName] is used first. If it is empty, [Class.getName] is used instead.
     * @param conditions the condition body.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun singleName(conditions: NameConditions) {
        rulesData.singleNameConditions = conditions
    }

    /** Sets the parent class inherited by the [Class]. */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun <reified T> extends() {
        rulesData.extendsClass.add(T::class.java.name)
    }

    /**
     * Sets the parent classes inherited by the [Class].
     *
     * Finds all matching parent classes in [name].
     * @param name the full [Class] names.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun extends(vararg name: String) {
        rulesData.extendsClass.addAll(name.toList())
    }

    /** Sets the interface implemented by the [Class]. */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun <reified T> implements() {
        rulesData.implementsClass.add(T::class.java.name)
    }

    /**
     * Sets the interfaces implemented by the [Class].
     *
     * Finds all matching interfaces in [name].
     * @param name the full [Class] names.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun implements(vararg name: String) {
        rulesData.implementsClass.addAll(name.toList())
    }

    /**
     * Marks the [Class] as anonymous.
     *
     * For example, com.demo.Test$1 or com.demo.Test$InnerTest.
     *
     * After marking it, use [enclosing] to further specify the enclosing class of the anonymous class.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun anonymous() {
        rulesData.isAnonymousClass = true
    }

    /**
     * Sets the [Class] to have no inheritance.
     *
     * The [Class] should only inherit from [Any] in this case.
     *
     * - [extends] becomes ineffective after this condition is set.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun noExtends() {
        rulesData.isNoExtendsClass = true
    }

    /**
     * Sets the [Class] to have no interfaces.
     *
     * - [implements] becomes ineffective after this condition is set.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun noImplements() {
        rulesData.isNoImplementsClass = true
    }

    /**
     * Sets the [Class] to have no inheritance or interfaces.
     *
     * The [Class] should only inherit from [Any] in this case.
     *
     * - [extends] and [implements] become ineffective after this condition is set.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun noSuper() {
        noExtends()
        noImplements()
    }

    /** Sets the enclosing class of the anonymous [Class]. */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun <reified T> enclosing() {
        rulesData.enclosingClass.add(T::class.java.name)
    }

    /**
     * Sets the enclosing class of the anonymous [Class].
     *
     * Finds all matching enclosing classes in [name].
     * @param name the full [Class] names.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun enclosing(vararg name: String) {
        rulesData.enclosingClass.addAll(name.toList())
    }

    /**
     * Package name scope filtering condition implementation.
     * @param packages the package name list.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class FromPackageRules internal constructor(private val packages: MutableList<ClassRulesData.PackageRulesData>) {

        /**
         * Enables exact package name matching.
         *
         * For example, given the following package names:
         *
         * com.demo.test.a
         *
         * com.demo.test.a.b
         *
         * com.demo.test.active
         *
         * If the package name condition is "com.demo.test.a", exact matching only matches the first one.
         *
         * Without exact matching, all examples above are matched.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun absolute() = packages.takeIf { it.isNotEmpty() }?.forEach { it.isAbsolute = true }
    }

    /**
     * Class name matching condition implementation.
     * @param name the class name matching instance.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class ClassNameRules internal constructor(private val name: ClassRulesData.NameRulesData) {

        /**
         * Marks the class name as optional.
         *
         * For example, given the following class names:
         *
         * com.demo.Test (fullName) / Test (simpleName)
         *
         * defpackage.a (fullName) / a (simpleName)
         *
         * These two names refer to the same class, but it is obfuscated in some versions and not in others.
         *
         * In this case, set the class name to "com.demo.Test" (fullName) or "Test" (simpleName).
         *
         * This uses the class name and ignores other lookup conditions when the name matches exactly. Otherwise, this condition is ignored and the other lookup conditions continue to be used.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun optional() {
            name.isOptional = true
        }
    }

    /**
     * Sets a [Member] condition that the [Class] must satisfy.
     * @param initiate the condition body.
     * @return [MemberRulesResult]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun member(initiate: MemberRules.() -> Unit = {}) = BaseRules.createMemberRules(this).apply(initiate).build()

    /**
     * Sets a [Field] condition that the [Class] must satisfy.
     * @param initiate the condition body.
     * @return [MemberRulesResult]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun field(initiate: FieldRules.() -> Unit = {}) = BaseRules.createFieldRules(this).apply(initiate).build()

    /**
     * Sets a [Method] condition that the [Class] must satisfy.
     * @param initiate the condition body.
     * @return [MemberRulesResult]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun method(initiate: MethodRules.() -> Unit = {}) = BaseRules.createMethodRules(this).apply(initiate).build()

    /**
     * Sets a [Constructor] condition that the [Class] must satisfy.
     * @param initiate the lookup body.
     * @return [MemberRulesResult]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun constructor(initiate: ConstructorRules.() -> Unit = {}) = BaseRules.createConstructorRules(this).apply(initiate).build()

    /**
     * Gets a [Class] or group of [Class] instances.
     * @return [MutableList]<[Class]>
     * @throws NoClassDefFoundError if no [Class] can be found.
     */
    private val result get() = ReflectionTool.findClasses(loaderSet, rulesData)

    /**
     * Reads [Class] data from the local cache.
     * @return [MutableList]<[Class]>
     */
    private fun readFromCache(): MutableList<Class<*>> =
        if (async && name.isNotBlank()) currentContext?.let {
            mutableListOf<Class<*>>().also { classes ->
                it.currentSp().getStringSet(name, emptySet())?.takeIf { it.isNotEmpty() }
                    ?.forEach { className -> if (className.hasClass(loaderSet)) classes.add(className.toClass(loaderSet)) }
            }
        } ?: let { SystemClock.sleep(1); readFromCache() } else mutableListOf()

    /**
     * Saves the names of the current [Class] list to the local cache.
     * @throws IllegalStateException if the current package name is "android".
     */
    private fun MutableList<Class<*>>.saveToCache() {
        if (name.isNotBlank() && isNotEmpty()) mutableSetOf<String>().also { names ->
            takeIf { it.isNotEmpty() }?.forEach { names.add(it.name) }
            currentContext?.also {
                if (it.packageName == "android") error("Cannot create classes cache for \"android\", please remove \"name\" param")
                it.currentSp().edit().apply { putStringSet(name, names) }.apply()
            }
        }
    }

    /**
     * Sets the instances.
     * @param classes the currently found [Class] list.
     */
    private fun setInstance(classes: MutableList<Class<*>>) {
        classInstances.clear()
        classes.takeIf { it.isNotEmpty() }?.forEach { classInstances.add(it) }
    }

    override fun build() = runCatching {
        if (loaderSet != null) {
            /** Starts the task. */
            fun startProcess() {
                runBlocking {
                    setInstance(readFromCache().takeIf { it.isNotEmpty() } ?: result)
                }.result { ms -> classInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Class [$it] takes ${ms}ms") } }
            }
            Result().also { e ->
                if (async) e.await {
                    runCatching {
                        startProcess()
                        it.waitResultCallback?.invoke(it.get())
                        it.waitAllResultCallback?.invoke(it.all())
                        classInstances.saveToCache()
                    }.onFailure { e ->
                        it.isNotFound = true
                        it.throwable = e
                        it.noClassDefFoundErrorCallback?.invoke()
                        errorMsg(e = e)
                    }
                } else startProcess()
            }
        } else Result(isNotFound = true, Throwable(LOADERSET_IS_NULL)).await { errorMsg() }
    }.getOrElse { e -> Result(isNotFound = true, e).await { errorMsg(e = e) } }

    /**
     * [Class] lookup result implementation.
     * @param isNotFound whether no [Class] was found. The default is false.
     * @param throwable the error information.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class Result internal constructor(
        internal var isNotFound: Boolean = false,
        internal var throwable: Throwable? = null
    ) : BaseResult {

        /** Asynchronous function body result callback. */
        internal var waitResultCallback: ((Class<*>?) -> Unit)? = null

        /** Asynchronous function body list result callback. */
        internal var waitAllResultCallback: ((MutableList<Class<*>>) -> Unit)? = null

        /** Callback body reinvoked for an error result. */
        internal var noClassDefFoundErrorCallback: (() -> Unit)? = null

        /**
         * Creates the result event listener body.
         * @param initiate the function body.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * Gets the [Class] itself.
         *
         * - If there are multiple [Class] results, only the first is returned.
         *
         * - Returns null when no result matches the lookup conditions.
         *
         * - Use [wait] if [async] is enabled.
         * @return [Class] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun get() = all().takeIf { it.isNotEmpty() }?.first()

        /**
         * Gets the [Class] list itself.
         *
         * - Returns all [Class] instances that match the lookup conditions.
         *
         * - Returns an empty [MutableList] when no result matches the lookup conditions.
         *
         * - Use [waitAll] if [async] is enabled.
         * @return [MutableList]<[Class]>
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all() = classInstances

        /**
         * Gets the [Class] list itself by iterating over it.
         *
         * - Invokes the callback for every [Class] instance that matches the lookup conditions.
         *
         * - Does not execute when no result matches the lookup conditions.
         *
         * - Use [waitAll] if [async] is enabled.
         * @param result callback for each result.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun all(result: (Class<*>) -> Unit): Result {
            all().takeIf { it.isNotEmpty() }?.forEach(result)
            return this
        }

        /**
         * Gets the [Class] itself asynchronously.
         *
         * - If there are multiple [Class] results, only the first is passed to the callback.
         *
         * - Passes null to the callback when no result matches the lookup conditions.
         *
         * - This function is only invoked when [async] is enabled. Otherwise, use [get].
         * @param result callback with a [Class] or null.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun wait(result: (Class<*>?) -> Unit): Result {
            waitResultCallback = result
            return this
        }

        /**
         * Gets the [Class] list itself asynchronously.
         *
         * - Passes every [Class] instance that matches the lookup conditions to the callback.
         *
         * - Passes an empty [MutableList] to the callback when no result matches the lookup conditions.
         *
         * - This function is only invoked when [async] is enabled. Otherwise, use [all].
         * @param result callback with a [MutableList] of [Class] instances.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun waitAll(result: (MutableList<Class<*>>) -> Unit): Result {
            waitAllResultCallback = result
            return this
        }

        /**
         * Listens for a missing [Class].
         * @param result the error callback.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun onNoClassDefFoundError(result: (Throwable) -> Unit): Result {
            noClassDefFoundErrorCallback = { if (isNotFound) result(throwable ?: Throwable("Initialization Error")) }
            noClassDefFoundErrorCallback?.invoke()
            return this
        }

        /**
         * Ignores exceptions and stops printing error logs.
         *
         * - To listen for error results in this case, implement [onNoClassDefFoundError] manually.
         * @return [Result] that can continue listening.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun ignored(): Result {
            isIgnoreErrorLogs = true
            return this
        }
    }
}