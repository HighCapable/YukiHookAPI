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
 * This file is created by fankes on 2026/9/16.
 */
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.core.api.reflect

import com.highcapable.kavaref.extension.makeAccessible
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Invokes this method on the given [instance] with the given arguments.
 *
 * This action calls the original method without hook.
 * @receiver [Method]
 * @param instance the instance to invoke the method on, or null for a static method.
 * @return [T] or null.
 */
@JvmName("invokeOriginalTyped")
context(_: PackageParam)
fun <T> Method.invokeOriginal(instance: Any?, vararg args: Any?): T? {
    makeAccessible()
    return YukiHookHelper.invokeOriginal(this, instance, args) as? T
}

/**
 * Invokes this method on the given [instance] with the given arguments.
 *
 * This action calls the original method without hook.
 * @receiver [Method]
 * @param instance the instance to invoke the method on, or null for a static method.
 * @return [Any] or null.
 */
context(_: PackageParam)
fun Method.invokeOriginal(instance: Any?, vararg args: Any?): Any? {
    makeAccessible()
    return YukiHookHelper.invokeOriginal(this, instance, args)
}

/**
 * Invokes this method non-virtually on the given [instance] with the given arguments.
 *
 * This action directly invokes this method without hook or subclass overrides.
 * - Supports for [YukiHookRuntime.LIBXPOSED] only.
 * @receiver [Method]
 * @param instance the instance to invoke the method on.
 * @return [T] or null.
 */
@JvmName("invokeSpecialTyped")
context(_: PackageParam)
fun <T> Method.invokeSpecial(instance: Any, vararg args: Any?) =
    YukiHookHelper.invokeSpecial(this, instance, args) as? T

/**
 * Invokes this method non-virtually on the given [instance] with the given arguments.
 *
 * This action directly invokes this method without hook or subclass overrides.
 * - Supports for [YukiHookRuntime.LIBXPOSED] only.
 * @receiver [Method]
 * @param instance the instance to invoke the method on.
 * @return [Any] or null.
 */
context(_: PackageParam)
fun Method.invokeSpecial(instance: Any, vararg args: Any?) =
    YukiHookHelper.invokeSpecial(this, instance, args)

/**
 * Initialize an existing [instance] non-virtually with this constructor and the given arguments.
 *
 * This action initializes the existing instance and does not create a new one.
 * - Supports for [YukiHookRuntime.LIBXPOSED] only.
 * @receiver [Constructor]
 * @param instance the instance to initialize.
 * @return [Any] null after the constructor completes.
 */
context(_: PackageParam)
fun Constructor<*>.initializeSpecial(instance: Any, vararg args: Any?) =
    YukiHookHelper.invokeSpecial(this, instance, args)

/**
 * Creates a new instance with this original constructor and the given arguments.
 *
 * This action skips all hooks registered for the constructor.
 * - Supports for [YukiHookRuntime.LIBXPOSED] only.
 * @receiver [Constructor]
 * @return [T]
 */
context(_: PackageParam)
fun <T : Any> Constructor<T>.newInstanceOriginal(vararg args: Any?) =
    YukiHookHelper.newInstanceOriginal(this, args) as T

/**
 * Creates a [subclass] instance initialized by this parent constructor and the given arguments.
 *
 * This action skips all hooks registered for the constructor. The subclass constructor is not invoked,
 * so subclass fields can remain uninitialized.
 * - Supports for [YukiHookRuntime.LIBXPOSED] only.
 * @receiver [Constructor]
 * @param subclass the subclass to instantiate.
 * @return [T]
 */
context(_: PackageParam)
fun <T : Any> Constructor<*>.newInstanceSpecial(subclass: Class<T>, vararg args: Any?) =
    YukiHookHelper.newInstanceSpecial(this, subclass, args) as T