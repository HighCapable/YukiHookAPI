/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
 * This file is created by fankes on 2026/10/13.
 */
@file:Suppress("unused", "UNCHECKED_CAST")
@file:JvmName("MethodResolverUtils")

package com.highcapable.yukihookapi.extension.kavaref

import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.extension.kavaref.resolver.HookMethodHandler
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Invokes the method with the given arguments.
 *
 * This action calls the original method without hooks.
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@JvmName("invokeOriginalTyped")
context(param: PackageParam)
fun <T> MethodResolver<*>.invokeOriginal(vararg args: Any?) =
    invokeOriginal(*args) as T?

/**
 * Invokes the method with the given arguments, ignoring any exceptions.
 *
 * This action calls the original method without hooks.
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@JvmName("invokeOriginalQuietlyTyped")
context(param: PackageParam)
fun <T> MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
    runCatching { invokeOriginal<T>(*args) }.getOrNull()

/**
 * Invokes the method with the given arguments.
 *
 * This action calls the original method without hooks.
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
context(param: PackageParam)
fun MethodResolver<*>.invokeOriginal(vararg args: Any?) =
    withHandler(HookMethodHandler.Original(param)).invoke(*args)

/**
 * Invokes the method with the given arguments, ignoring any exceptions.
 *
 * This action calls the original method without hooks.
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
context(param: PackageParam)
fun MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) =
    runCatching { invokeOriginal(*args) }.getOrNull()

/**
 * Invokes this method non-virtually on its instance with the given arguments.
 *
 * This action directly invokes the method declared by this resolver without hooks or subclass overrides.
 * An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@JvmName("invokeSpecialTyped")
context(param: PackageParam)
fun <T> MethodResolver<*>.invokeSpecial(vararg args: Any?) =
    invokeSpecial(*args) as T

/**
 * Invokes this method non-virtually on its instance with the given arguments, ignoring any exceptions.
 *
 * This action directly invokes the method declared by this resolver without hooks or subclass overrides.
 * An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
@JvmName("invokeSpecialQuietlyTyped")
context(param: PackageParam)
fun <T> MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
    runCatching { invokeSpecial<T>(*args) }.getOrNull()

/**
 * Invokes this method non-virtually on its instance with the given arguments.
 *
 * This action directly invokes the method declared by this resolver without hooks or subclass overrides.
 * An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invoke
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
fun MethodResolver<*>.invokeSpecial(vararg args: Any?) =
    withHandler(HookMethodHandler.Special(param)).invoke(*args)

/**
 * Invokes this method non-virtually on its instance with the given arguments, ignoring any exceptions.
 *
 * This action directly invokes the method declared by this resolver without hooks or subclass overrides.
 * An instance must be set using [MethodResolver.of].
 * @see MethodResolver.invokeQuietly
 * @receiver [MethodResolver]
 * @return [Any] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
fun MethodResolver<*>.invokeSpecialQuietly(vararg args: Any?) =
    runCatching { invokeSpecial(*args) }.getOrNull()