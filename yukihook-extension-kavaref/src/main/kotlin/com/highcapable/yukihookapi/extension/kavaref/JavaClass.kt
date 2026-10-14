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
@file:Suppress("unused")
@file:JvmName("JavaClassUtils")

package com.highcapable.yukihookapi.extension.kavaref

import com.highcapable.kavaref.extension.VariousClass
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.kavaref.extension.lazyClass as lazyClassGlobal
import com.highcapable.kavaref.extension.lazyClassOrNull as lazyClassOrNullGlobal
import com.highcapable.kavaref.extension.toClass as toClassGlobal
import com.highcapable.kavaref.extension.toClassOrNull as toClassOrNullGlobal

/** Defines an initializer for a [ClassLoader] instance. */
private typealias ClassLoaderInitializer = () -> ClassLoader?

/**
 * Loads the class with this name, [PackageParam.hostClassLoader] is used by default.
 * @see toClassGlobal
 */
context(param: PackageParam)
fun String.toClass(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) = toClassGlobal(loader, initialize)

/**
 * Loads the class with this name as [T], [PackageParam.hostClassLoader] is used by default.
 * @see toClassGlobal
 */
@JvmName("toClassTyped")
context(param: PackageParam)
fun <T : Any> String.toClass(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    toClassGlobal<T>(loader, initialize)

/**
 * Loads the class with this name or null, [PackageParam.hostClassLoader] is used by default.
 * @see toClassOrNullGlobal
 */
context(param: PackageParam)
fun String.toClassOrNull(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    toClassOrNullGlobal(loader, initialize)

/**
 * Loads the class with this name as [T] or null, [PackageParam.hostClassLoader] is used by default.
 * @see toClassOrNullGlobal
 */
@JvmName("toClassOrNullTyped")
context(param: PackageParam)
fun <T : Any> String.toClassOrNull(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    toClassOrNullGlobal<T>(loader, initialize)

/**
 * Loads the first existing class of this [VariousClass], [PackageParam.hostClassLoader] is used by default.
 * @see VariousClass.load
 */
context(param: PackageParam)
fun VariousClass.toClass(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) = load(loader, initialize)

/**
 * Loads the first existing class of this [VariousClass] as [T], [PackageParam.hostClassLoader] is used by default.
 * @see VariousClass.load
 */
@JvmName("toClassTyped")
context(param: PackageParam)
fun <T : Any> VariousClass.toClass(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    load<T>(loader, initialize)

/**
 * Loads the first existing class of this [VariousClass] or null, [PackageParam.hostClassLoader] is used by default.
 * @see VariousClass.loadOrNull
 */
context(param: PackageParam)
fun VariousClass.toClassOrNull(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    loadOrNull(loader, initialize)

/**
 * Loads the first existing class of this [VariousClass] as [T] or null, [PackageParam.hostClassLoader] is used by default.
 * @see VariousClass.loadOrNull
 */
@JvmName("toClassOrNullTyped")
context(param: PackageParam)
fun <T : Any> VariousClass.toClassOrNull(loader: ClassLoader? = param.hostClassLoader, initialize: Boolean = false) =
    loadOrNull<T>(loader, initialize)

/**
 * Creates a lazy-loaded class, [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassGlobal
 */
context(param: PackageParam)
fun lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassGlobal(name, initialize, loader)

/**
 * Creates a lazy-loaded class as [T], [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassGlobal
 */
@JvmName("lazyClassTyped")
context(param: PackageParam)
fun <T : Any> lazyClass(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassGlobal<T>(name, initialize, loader)

/**
 * Creates a lazy-loaded class from [VariousClass], [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassGlobal
 */
context(param: PackageParam)
fun lazyClass(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassGlobal(variousClass, initialize, loader)

/**
 * Creates a nullable lazy-loaded class, [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassOrNullGlobal
 */
context(param: PackageParam)
fun lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassOrNullGlobal(name, initialize, loader)

/**
 * Creates a nullable lazy-loaded class as [T], [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassOrNullGlobal
 */
@JvmName("lazyClassOrNullTyped")
context(param: PackageParam)
fun <T : Any> lazyClassOrNull(name: String, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassOrNullGlobal<T>(name, initialize, loader)

/**
 * Creates a nullable lazy-loaded class from [VariousClass], [PackageParam.hostClassLoader] is used by default.
 * @see lazyClassOrNullGlobal
 */
context(param: PackageParam)
fun lazyClassOrNull(variousClass: VariousClass, initialize: Boolean = false, loader: ClassLoaderInitializer? = { param.hostClassLoader }) =
    lazyClassOrNullGlobal(variousClass, initialize, loader)