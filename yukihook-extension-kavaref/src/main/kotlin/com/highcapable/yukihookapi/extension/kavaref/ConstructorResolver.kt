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

package com.highcapable.yukihookapi.extension.kavaref

import com.highcapable.kavaref.resolver.ConstructorResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.extension.kavaref.resolver.HookConstructorHandler
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Initializes an existing [instance] non-virtually with this constructor and the given arguments.
 *
 * This action initializes the existing instance and does not create a new one.
 * @receiver [ConstructorResolver]
 * @param instance the instance to initialize.
 * @return [Any] null after the constructor completes.
 */
context(param: PackageParam)
fun ConstructorResolver<*>.initializeSpecial(instance: Any, vararg args: Any?) =
    with(param) { self.initializeSpecial(instance, *args) }

/**
 * Initializes an existing [instance] non-virtually with this constructor and the given arguments, ignoring any exceptions.
 *
 * This action initializes the existing instance and does not create a new one.
 * @receiver [ConstructorResolver]
 * @param instance the instance to initialize.
 * @return [Any] or null.
 */
context(param: PackageParam)
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
context(param: PackageParam)
fun <T : Any> ConstructorResolver<T>.createOriginal(vararg args: Any?) =
    withHandler(HookConstructorHandler.Original(param)).create(*args)

/**
 * Creates a new instance with this original constructor and the given arguments, ignoring any exceptions.
 *
 * This action skips all hooks registered for the constructor.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.createQuietly
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
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
context(param: PackageParam)
fun <T : Any> ConstructorResolver<*>.createSpecial(subclass: Class<T>, vararg args: Any?) =
    withHandler(HookConstructorHandler.Special(param, subclass)).create(*args) as T

/**
 * Creates a [subclass] instance initialized by this parent constructor and the given arguments, ignoring any exceptions.
 *
 * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
 * so subclass fields can remain uninitialized.
 * @receiver [ConstructorResolver]
 * @see ConstructorResolver.createQuietly
 * @param subclass the subclass to instantiate.
 * @return [T] or null.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
fun <T : Any> ConstructorResolver<*>.createSpecialQuietly(subclass: Class<T>, vararg args: Any?) =
    runCatching { createSpecial(subclass, *args) }.getOrNull()