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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress(
    "unused", "UNCHECKED_CAST", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION",
    "DeprecatedCallableAddReplaceWith"
)

package com.highcapable.yukihookapi.hook.factory

import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.bean.GenericClass
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.classes.DexClassFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ClassConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ClassLoaderInitializer
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ConstructorConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.FieldConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.MethodConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.type.java.AnyClass
import com.highcapable.yukihookapi.hook.type.java.BooleanClass
import com.highcapable.yukihookapi.hook.type.java.BooleanType
import com.highcapable.yukihookapi.hook.type.java.ByteClass
import com.highcapable.yukihookapi.hook.type.java.ByteType
import com.highcapable.yukihookapi.hook.type.java.CharClass
import com.highcapable.yukihookapi.hook.type.java.CharType
import com.highcapable.yukihookapi.hook.type.java.DoubleClass
import com.highcapable.yukihookapi.hook.type.java.DoubleType
import com.highcapable.yukihookapi.hook.type.java.FloatClass
import com.highcapable.yukihookapi.hook.type.java.FloatType
import com.highcapable.yukihookapi.hook.type.java.IntClass
import com.highcapable.yukihookapi.hook.type.java.IntType
import com.highcapable.yukihookapi.hook.type.java.LongClass
import com.highcapable.yukihookapi.hook.type.java.LongType
import com.highcapable.yukihookapi.hook.type.java.ShortClass
import com.highcapable.yukihookapi.hook.type.java.ShortType
import com.highcapable.yukihookapi.hook.type.java.UnitClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import kotlin.reflect.KProperty

/**
 * Defines the [Member] type in a [Class].
 */
enum class MembersType {
    /** All [Method] and [Constructor] instances. */
    ALL,

    /** All [Method] instances. */
    METHOD,

    /** All [Constructor] instances. */
    CONSTRUCTOR
}

/**
 * Lazy loading [Class] instance.
 * @param instance the current instance.
 * @param initialize whether to initialize the class.
 * @param loader the [ClassLoader] to load the class.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
open class LazyClass<T> internal constructor(
    private val instance: Any,
    private val initialize: Boolean,
    private val loader: ClassLoaderInitializer?,
) {

    /** The current instance. */
    private var baseInstance: Class<T>? = null

    /**
     * Gets a non-null [Class] instance.
     * @return [Class]<[T]>
     */
    internal val nonNull get(): Class<T> {
        if (baseInstance == null) baseInstance = when (instance) {
            is String -> instance.toClass(loader?.invoke(), initialize) as Class<T>
            is VariousClass -> instance.get(loader?.invoke(), initialize) as Class<T>
            else -> error("Unknown lazy class type \"$instance\"")
        }
        return baseInstance ?: error("Exception has been thrown above")
    }

    /**
     * Gets a nullable [Class] instance.
     * @return [Class]<[T]> or null.
     */
    internal val nullable get(): Class<T>? {
        if (baseInstance == null) baseInstance = when (instance) {
            is String -> instance.toClassOrNull(loader?.invoke(), initialize) as? Class<T>?
            is VariousClass -> instance.getOrNull(loader?.invoke(), initialize) as? Class<T>?
            else -> error("Unknown lazy class type \"$instance\"")
        }
        return baseInstance
    }

    /**
     * Creates a non-null [Class] instance.
     * @param instance the current instance.
     * @param initialize whether to initialize the class.
     * @param loader the [ClassLoader] to load the class.
     */
    class NonNull<T> internal constructor(
        instance: Any,
        initialize: Boolean,
        loader: ClassLoaderInitializer?,
    ) : LazyClass<T>(instance, initialize, loader) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = nonNull
    }

    /**
     * Creates a nullable [Class] instance.
     * @param instance the current instance.
     * @param initialize whether to initialize the class.
     * @param loader the [ClassLoader] to load the class.
     */
    class Nullable<T> internal constructor(
        instance: Any,
        initialize: Boolean,
        loader: ClassLoaderInitializer?,
    ) : LazyClass<T>(instance, initialize, loader) {

        operator fun getValue(thisRef: Any?, property: KProperty<*>) = nullable
    }
}

/**
 * Lists the names of all [Class] instances under the current [ClassLoader].
 *
 * - This function can be very time-consuming when there are too many [Class] instances.
 *
 * - To find a [Class] by specified rules, use [searchClass].
 * @return [List]<[String]>
 * @throws IllegalStateException if the current [ClassLoader] is not a [BaseDexClassLoader].
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun ClassLoader.listOfClasses() = ReflectionTool.findDexClassList(loader = this)

/**
 * Finds [Class] instances in the Dex through the current [ClassLoader] using specified conditions.
 *
 * - This function can be very time-consuming when there are too many [Class] instances or the lookup conditions are complex.
 *
 * - Enabling [async] or setting [name] is recommended. [name] automatically creates a local cache for different versions of the Hook APP (host) to improve efficiency.
 *
 * - This feature is still experimental. Performance and stability issues may remain. Report any issues you encounter and help us improve it.
 * @param name the name identifying the current [Class] cache. Caching is disabled when omitted. Enabling caching automatically enables [async].
 * @param async whether asynchronous lookup is enabled, false by default.
 * @param initiate the finder block.
 * @return [DexClassFinder.Result]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun ClassLoader.searchClass(name: String = "", async: Boolean = false, initiate: ClassConditions) =
    DexClassFinder(name, async = async || name.isNotBlank(), loaderSet = this).apply(initiate).build()

/**
 * Listens for loading through [ClassLoader.loadClass] on the current [ClassLoader].
 *
 * - Events can only be captured when the current [ClassLoader] actively uses [ClassLoader.loadClass].
 *
 * - This is an experimental feature that is generally unnecessary. Errors may occur.
 *
 * - This feature is available only in the (Xposed) host environment. It has no effect in other environments and prints a warning.
 * @param result callback with the [Class] instance.
 */
fun ClassLoader.onLoadClass(result: (Class<*>) -> Unit) = AppParasitics.hookClassLoader(loader = this, result)

/**
 * Whether the current [Class] has an inheritance relationship. A parent class of [Any] is treated as no inheritance.
 * @return [Boolean]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Class<*>.hasExtends get() = superclass != null && superclass != AnyClass

/**
 * Whether the current [Class] inherits from [other].
 *
 * Returns true when the current [Class] is [other] itself.
 *
 * Returns false when the current [Class] or [other] is null.
 * @param other the [Class] to check.
 * @return [Boolean]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
infix fun Class<*>?.extends(other: Class<*>?): Boolean {
    if (this == null || other == null) return false
    var isMatched = false

    /**
     * Finds whether a parent class exists.
     * @param current the current [Class].
     */
    fun findSuperClass(current: Class<*>) {
        if (current == other)
            isMatched = true
        else if (current != AnyClass && current.superclass != null) findSuperClass(current.superclass)
    }
    findSuperClass(current = this)
    return isMatched
}

/**
 * Whether the current [Class] does not inherit from [other].
 *
 * This function is the inverse of [extends].
 * @param other the [Class] to check.
 * @return [Boolean]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
infix fun Class<*>?.notExtends(other: Class<*>?) = extends(other).not()

/**
 * Whether the current [Class] implements the [other] interface.
 *
 * Returns false when the current [Class] or [other] is null.
 * @param other the [Class] to check.
 * @return [Boolean]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
infix fun Class<*>?.implements(other: Class<*>?): Boolean {
    if (this == null || other == null) return false
    /**
     * Gets all interfaces implemented by the current [Class].
     * @return [Set]<[Class]>
     */
    fun Class<*>.findAllInterfaces(): Set<Class<*>> = mutableSetOf(*interfaces).apply { superclass?.also { addAll(it.findAllInterfaces()) } }
    return findAllInterfaces().takeIf { it.isNotEmpty() }?.any { it.name == other.name } ?: false
}

/**
 * Whether the current [Class] does not implement the [other] interface.
 *
 * This function is the inverse of [implements].
 * @param other the [Class] to check.
 * @return [Boolean]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
infix fun Class<*>?.notImplements(other: Class<*>?) = implements(other).not()

/**
 * Converts the current [Class] to a Java primitive type automatically.
 *
 * Type conversion is performed automatically when the current [Class] is a Java or Kotlin primitive type.
 *
 * The following primitive types can currently be converted automatically:
 *
 * - [kotlin.Unit]
 * - [java.lang.Void]
 * - [java.lang.Boolean]
 * - [java.lang.Integer]
 * - [java.lang.Float]
 * - [java.lang.Double]
 * - [java.lang.Long]
 * - [java.lang.Short]
 * - [java.lang.Character]
 * - [java.lang.Byte]
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun Class<*>.toJavaPrimitiveType() = when (this) {
    classOf<Unit>(), UnitClass, UnitType -> UnitType
    BooleanClass, BooleanType -> BooleanType
    IntClass, IntType -> IntType
    FloatClass, FloatType -> FloatType
    DoubleClass, DoubleType -> DoubleType
    LongClass, LongType -> LongType
    ShortClass, ShortType -> ShortType
    CharClass, CharType -> CharType
    ByteClass, ByteType -> ByteType
    else -> this
}

/**
 * Converts a string class name to a concrete class in [loader].
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [toClass].
 * @return [Class]
 * @throws NoClassDefFoundError if the [Class] cannot be found or an incorrect [ClassLoader] is set.
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("name.toClass(loader)"))
fun classOf(name: String, loader: ClassLoader? = null) = name.toClass(loader)

/**
 * Converts a string class name to a concrete class in [loader].
 * @param loader the [ClassLoader] containing the [Class]. The default [ClassLoader] is used when omitted.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @return [Class]
 * @throws NoClassDefFoundError if the [Class] cannot be found or an incorrect [ClassLoader] is set.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun String.toClass(loader: ClassLoader? = null, initialize: Boolean = false) = ReflectionTool.findClassByName(name = this, loader, initialize)

/**
 * Converts a string class name to a concrete class in [loader].
 * @param loader the [ClassLoader] containing the [Class]. The default [ClassLoader] is used when omitted.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @return [Class]<[T]>
 * @throws NoClassDefFoundError if the [Class] cannot be found or an incorrect [ClassLoader] is set.
 * @throws IllegalStateException if the [Class] type is not [T].
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
@JvmName("toClass_Generics")
inline fun <reified T> String.toClass(loader: ClassLoader? = null, initialize: Boolean = false) =
    ReflectionTool.findClassByName(name = this, loader, initialize) as? Class<T>? ?: error("Target Class type cannot cast to ${T::class.java}")

/**
 * Converts a string class name to a concrete class in [loader].
 *
 * Returns null without throwing an exception when the [Class] cannot be found.
 * @param loader the [ClassLoader] containing the [Class]. The default [ClassLoader] is used when omitted.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @return [Class] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun String.toClassOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = runCatching { toClass(loader, initialize) }.getOrNull()

/**
 * Converts a string class name to a concrete class in [loader].
 *
 * Returns null without throwing an exception when the [Class] cannot be found.
 * @param loader the [ClassLoader] containing the [Class]. The default [ClassLoader] is used when omitted.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @return [Class]<[T]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
@JvmName("toClassOrNull_Generics")
inline fun <reified T> String.toClassOrNull(loader: ClassLoader? = null, initialize: Boolean = false) =
    runCatching { toClass<T>(loader, initialize) }.getOrNull()

/**
 * Gets the [Class] instance of [T] and converts it to a concrete class.
 * @param loader the [ClassLoader] containing the [Class], optional.
 * @param initialize whether to initialize the static block of the [Class]. This has no effect when [loader] is null. The default is false.
 * @return [Class]<[T]>
 * @throws NoClassDefFoundError if the [Class] cannot be found or an incorrect [ClassLoader] is set.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun <reified T> classOf(loader: ClassLoader? = null, initialize: Boolean = false) =
    loader?.let { T::class.java.name.toClass(loader, initialize) as Class<T> } ?: T::class.java

/**
 * Creates a lazily loaded non-null [Class] instance.
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.NonNull]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    lazyClass<Any>(name, initialize, loader)

/**
 * Creates a lazily loaded non-null [Class] instance of type [T].
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.NonNull]<[T]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
@JvmName("lazyClass_Generics")
inline fun <reified T> lazyClass(name: String, initialize: Boolean = false, noinline loader: ClassLoaderInitializer? = null) =
    LazyClass.NonNull<T>(name, initialize, loader)

/**
 * Creates a lazily loaded non-null [Class] instance.
 * @param variousClass [VariousClass].
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.NonNull]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.NonNull<Any>(variousClass, initialize, loader)

/**
 * Creates a lazily loaded nullable [Class] instance.
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.Nullable]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    lazyClassOrNull<Any>(name, initialize, loader)

/**
 * Creates a lazily loaded nullable [Class] instance of type [T].
 * @param name the fully qualified class name.
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.Nullable]<[T]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
@JvmName("lazyClassOrNull_Generics")
inline fun <reified T> lazyClassOrNull(name: String, initialize: Boolean = false, noinline loader: ClassLoaderInitializer? = null) =
    LazyClass.Nullable<T>(name, initialize, loader)

/**
 * Creates a lazily loaded nullable [Class] instance.
 * @param variousClass [VariousClass].
 * @param initialize whether to initialize the static block of the [Class], false by default.
 * @param loader the [ClassLoader] to load the class. The default [ClassLoader] is used when omitted.
 * @return [LazyClass.Nullable]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = null) =
    LazyClass.Nullable<Any>(variousClass, initialize, loader)

/**
 * Checks whether a string class name exists using the specified [ClassLoader].
 * @param loader the [ClassLoader] containing the [Class]. The default [ClassLoader] is used when omitted.
 * @return [Boolean] whether the class exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun String.hasClass(loader: ClassLoader? = null) = ReflectionTool.hasClassByName(name = this, loader)

/**
 * Checks whether a field exists.
 * @param initiate the finder block.
 * @return [Boolean] whether the field exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.hasField(initiate: FieldConditions) = field(initiate).ignored().isNoSuch.not()

/**
 * Checks whether a method exists.
 * @param initiate the finder block.
 * @return [Boolean] whether the method exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.hasMethod(initiate: MethodConditions) = method(initiate).ignored().isNoSuch.not()

/**
 * Checks whether a constructor exists.
 * @param initiate the finder block.
 * @return [Boolean] whether the constructor exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.hasConstructor(initiate: ConstructorConditions = { emptyParam() }) = constructor(initiate).ignored().isNoSuch.not()

/**
 * Checks whether a matching modifier exists in the [Member].
 * @param conditions the condition block.
 * @return [Boolean] whether a matching modifier exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Member.hasModifiers(conditions: ModifierConditions) = conditions(ModifierRules.with(instance = this))

/**
 * Checks whether a matching modifier exists in the [Class].
 * @param conditions the condition block.
 * @return [Boolean] whether a matching modifier exists.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.hasModifiers(conditions: ModifierConditions) = conditions(ModifierRules.with(instance = this))

/**
 * Finds and gets a field.
 * @param initiate the finder block.
 * @return [FieldFinder.Result]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.field(initiate: FieldConditions = {}) = FieldFinder(classSet = this).apply(initiate).build()

/**
 * Finds and gets a method.
 * @param initiate the finder block.
 * @return [MethodFinder.Result]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.method(initiate: MethodConditions = {}) = MethodFinder(classSet = this).apply(initiate).build()

/**
 * Finds and gets a constructor.
 * @param initiate the finder block.
 * @return [ConstructorFinder.Result]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.constructor(initiate: ConstructorConditions = {}) = ConstructorFinder(classSet = this).apply(initiate).build()

/**
 * Gets the generic superclass of the current [Class].
 *
 * Returns null when the current instance has no generic type.
 * @return [GenericClass] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun Class<*>.generic() = genericSuperclass?.let { (it as? ParameterizedType?)?.let { e -> GenericClass(e) } }

/**
 * Gets the generic superclass of the current [Class].
 *
 * Returns null when the current instance has no generic type.
 * @param initiate the instance block.
 * @return [GenericClass] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.generic(initiate: GenericClass.() -> Unit) = generic()?.apply(initiate)

/**
 * Gets the class operation object for the current instance.
 * @param ignored whether to suppress error warnings, false by default.
 * @return [CurrentClass]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun <reified T : Any> T.current(ignored: Boolean = false) =
    CurrentClass(javaClass, instance = this).apply { isIgnoreErrorLogs = ignored }

/**
 * Gets the class operation object for the current instance.
 * @param ignored whether to suppress error warnings, false by default.
 * @param initiate the operation block.
 * @return [T]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun <reified T : Any> T.current(ignored: Boolean = false, initiate: CurrentClass.() -> Unit): T {
    current(ignored).apply(initiate)
    return this
}

/**
 * Creates a new instance of any type [Any] through a constructor.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [buildOf].
 * @return [Any] or null.
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("buildOf(*param, initiate)"))
fun Class<*>.buildOfAny(vararg args: Any?, initiate: ConstructorConditions = { emptyParam() }) = buildOf(*args, initiate)

/**
 * Creates a new instance of any type [Any] through a constructor.
 * @param args the constructor arguments.
 * @param initiate the finder block.
 * @return [Any] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.buildOf(vararg args: Any?, initiate: ConstructorConditions = { emptyParam() }) =
    constructor(initiate).get().call(*args)

/**
 * Creates a new instance of type [T] through a constructor.
 * @param args the constructor arguments.
 * @param initiate the finder block.
 * @return [T] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
@JvmName(name = "buildOf_Generics")
inline fun <T> Class<*>.buildOf(vararg args: Any?, initiate: ConstructorConditions = { emptyParam() }) =
    constructor(initiate).get().newInstance<T>(*args)

/**
 * Iterates over all methods in the current class.
 * @param isAccessible whether to force members to be accessible, true by default.
 * @param result callback with the [Int] index and [Method] instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.allMethods(isAccessible: Boolean = true, result: (index: Int, method: Method) -> Unit) =
    declaredMethods.forEachIndexed { p, it -> result(p, it.also { e -> e.isAccessible = isAccessible }) }

/**
 * Iterates over all constructors in the current class.
 * @param isAccessible whether to force members to be accessible, true by default.
 * @param result callback with the [Int] index and [Constructor] instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.allConstructors(isAccessible: Boolean = true, result: (index: Int, constructor: Constructor<*>) -> Unit) =
    declaredConstructors.forEachIndexed { p, it -> result(p, it.also { e -> e.isAccessible = isAccessible }) }

/**
 * Iterates over all fields in the current class.
 * @param isAccessible whether to force members to be accessible, true by default.
 * @param result callback with the [Int] index and [Field] instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
inline fun Class<*>.allFields(isAccessible: Boolean = true, result: (index: Int, field: Field) -> Unit) =
    declaredFields.forEachIndexed { p, it -> result(p, it.also { e -> e.isAccessible = isAccessible }) }