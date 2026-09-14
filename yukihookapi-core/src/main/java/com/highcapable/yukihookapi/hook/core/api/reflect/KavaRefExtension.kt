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
import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.param.PackageParam

/** Defines an initializer for a [ClassLoader] instance. */
internal typealias ClassLoaderInitializer = () -> ClassLoader?

/**
 * Invoke the method with the given arguments.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invoke
 * @return [T] or null.
 */
@JvmName("invokeOriginalTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeOriginal(vararg args: Any?): T? {
    self.makeAccessible()
    return YukiHookHelper.invokeOriginal(self, instance, args) as? T?
}

/**
 * Invoke the method with the given arguments and ignore any exceptions.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invokeQuietly
 * @return [T] or null.
 */
@JvmName("invokeOriginalQuietlyTyped")
context(_: PackageParam)
fun <T> MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) = runCatching { invokeOriginal<T>(*args) }.getOrNull()

/**
 * Invoke the method with the given arguments.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invoke
 * @return [Any] or null.
 */
context(_: PackageParam)
fun MethodResolver<*>.invokeOriginal(vararg args: Any?): Any? {
    self.makeAccessible()
    return YukiHookHelper.invokeOriginal(self, instance, args)
}

/**
 * Invoke the method with the given arguments and ignore any exceptions.
 *
 * This action calls the original method without hook.
 * @see MethodResolver.invokeQuietly
 * @return [Any] or null.
 */
context(_: PackageParam)
fun MethodResolver<*>.invokeOriginalQuietly(vararg args: Any?) = runCatching { invokeOriginal(*args) }.getOrNull()