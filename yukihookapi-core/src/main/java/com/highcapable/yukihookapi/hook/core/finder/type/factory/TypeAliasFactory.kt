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
 * This file is created by fankes on 2022/9/14.
 */
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.type.factory

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.base.rules.CountRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.NameRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ObjectRules
import com.highcapable.yukihookapi.hook.core.finder.classes.DexClassFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder

/** Defines an initializer for a [ClassLoader] instance. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ClassLoaderInitializer = () -> ClassLoader?

/** Defines a [DexClassFinder] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ClassConditions = DexClassFinder.() -> Unit

/** Defines a [FieldFinder] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias FieldConditions = FieldFinder.() -> Unit

/** Defines a [MethodFinder] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias MethodConditions = MethodFinder.() -> Unit

/** Defines a [ConstructorFinder] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ConstructorConditions = ConstructorFinder.() -> Unit

/** Defines a [NameRules] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias NameConditions = NameRules.(String) -> Boolean

/** Defines a [CountRules] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias CountConditions = CountRules.(Int) -> Boolean

/** Defines a [ModifierRules] condition block. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ModifierConditions = ModifierRules.() -> Boolean

/** Defines an [ObjectRules] condition for one class. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ObjectConditions = ObjectRules.(Class<*>) -> Boolean

/** Defines an [ObjectRules] condition for multiple classes. */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
internal typealias ObjectsConditions = ObjectRules.(Array<Class<*>>) -> Boolean