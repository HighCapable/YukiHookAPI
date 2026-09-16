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
 * This file is created by fankes on 2025/6/16.
 */
@file:Suppress("unused", "INVISIBLE_MEMBER", "INVISIBLE_REFERENCE", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.core.api.reflect

import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/** Defines an initializer for a [ClassLoader] instance. */
internal typealias ClassLoaderInitializer = () -> ClassLoader?

/**
 * Invokes the method with the given arguments.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@JvmName("invokeOriginalTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeOriginal(vararg args: Any?): T? {
    self.makeAccessible()
    return YukiHookHelper.invokeOriginal(self, instance, args) as? T?
}

/**
 * Invokes the method with the given arguments and ignore any exceptions.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@JvmName("invokeOriginalQuietlyTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
    runCatching { invokeOriginal<T>(*args) }.getOrNull()

/**
 * Invokes the method with the given arguments.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
context(_: PackageParam)
fun MethodResolver<*>.invokeOriginal(vararg args: Any?): Any? {
    self.makeAccessible()
    return YukiHookHelper.invokeOriginal(self, instance, args)
}

/**
 * Invokes the method with the given arguments and ignore any exceptions.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
context(_: PackageParam)
fun MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
    runCatching { invokeOriginal(*args) }.getOrNull()

/**
 * Invokes this method non-virtually on its instance with the given arguments.
 *
 * This action directly invokes the method declared by this resolver without hook or subclass overrides.
 * - An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@JvmName("invokeSpecialTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeSpecial(vararg args: Any?): T? =
    YukiHookHelper.invokeSpecial(self, checkNotNull(instance) { "An instance is required for special invocation." }, args) as? T

/**
 * Invokes this method non-virtually on its instance with the given arguments and ignore any exceptions.
 *
 * This action directly invokes the method declared by this resolver without hook or subclass overrides.
 * - An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@JvmName("invokeSpecialQuietlyTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
    runCatching { invokeSpecial<T>(*args) }.getOrNull()

/**
 * Invokes this method non-virtually on its instance with the given arguments.
 *
 * This action directly invokes the method declared by this resolver without hook or subclass overrides.
 * - An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun MethodResolver<*>.invokeSpecial(vararg args: Any?) =
    YukiHookHelper.invokeSpecial(self, checkNotNull(instance) { "An instance is required for special invocation." }, args)

/**
 * Invokes this method non-virtually on its instance with the given arguments and ignore any exceptions.
 *
 * This action directly invokes the method declared by this resolver without hook or subclass overrides.
 * - An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
    runCatching { invokeSpecial(*args) }.getOrNull()

/**
 * Initialize an existing [instance] non-virtually with this constructor and the given arguments.
 *
 * This action initializes the existing instance and does not create a new one.
 * @receiver [ConstructorResolver]
 * @param instance the instance to initialize.
 * @return [Any] null after the constructor completes.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun ConstructorResolver<*>.initializeSpecial(instance: Any, vararg args: Any?) =
    YukiHookHelper.invokeSpecial(self, instance, args)

/**
 * Initialize an existing [instance] non-virtually with this constructor and the given arguments and ignore any exceptions.
 *
 * This action initializes the existing instance and does not create a new one.
 * @receiver [ConstructorResolver]
 * @param instance the instance to initialize.
 * @return [Any] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun ConstructorResolver<*>.initializeSpecialQuietly(instance: Any, vararg args: Any?) =
    runCatching { initializeSpecial(instance, *args) }.getOrNull()

/**
 * Creates a new instance with this original constructor and the given arguments.
 *
 * This action skips all hooks registered for the constructor.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.create
 * @return [T]
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun <T : Any> ConstructorResolver<T>.createOriginal(vararg args: Any?) =
    YukiHookHelper.newInstanceOriginal(self, args) as T

/**
 * Creates a new instance with this original constructor and the given arguments, and ignore any exceptions.
 *
 * This action skips all hooks registered for the constructor.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.createQuietly
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun <T : Any> ConstructorResolver<T>.createOriginalQuietly(vararg args: Any?) =
    runCatching { createOriginal(*args) }.getOrNull()

/**
 * Creates a [subclass] instance initialized by this parent constructor and the given arguments.
 *
 * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
 * so subclass fields can remain uninitialized.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.create
 * @param subclass the subclass to instantiate.
 * @return [T]
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun <T : Any> ConstructorResolver<*>.createSpecial(subclass: Class<T>, vararg args: Any?) =
    YukiHookHelper.newInstanceSpecial(self, subclass, args) as T

/**
 * Creates a [subclass] instance initialized by this parent constructor and the given arguments, and ignore any exceptions.
 *
 * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
 * so subclass fields can remain uninitialized.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.createQuietly
 * @param subclass the subclass to instantiate.
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(_: PackageParam)
fun <T : Any> ConstructorResolver<*>.createSpecialQuietly(subclass: Class<T>, vararg args: Any?) =
    runCatching { createSpecial(subclass, *args) }.getOrNull()