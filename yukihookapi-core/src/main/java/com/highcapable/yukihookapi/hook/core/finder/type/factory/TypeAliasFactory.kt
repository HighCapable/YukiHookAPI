/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
package com.highcapable.yukihookapi.hook.core.finder.type.factory

import com.highcapable.yukihookapi.hook.core.finder.base.rules.CountRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.NameRules
import com.highcapable.yukihookapi.hook.core.finder.base.rules.ObjectRules
import com.highcapable.yukihookapi.hook.core.finder.classes.DexClassFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder

/** 定义 [ClassLoader] 装载实例方法体类型 */
internal typealias ClassLoaderInitializer = () -> ClassLoader?

/** 定义 [DexClassFinder] 方法体类型 */
internal typealias ClassConditions = DexClassFinder.() -> Unit

/** 定义 [FieldFinder] 方法体类型 */
internal typealias FieldConditions = FieldFinder.() -> Unit

/** 定义 [MethodFinder] 方法体类型 */
internal typealias MethodConditions = MethodFinder.() -> Unit

/** 定义 [ConstructorFinder] 方法体类型 */
internal typealias ConstructorConditions = ConstructorFinder.() -> Unit

/** 定义 [NameRules] 方法体类型 */
internal typealias NameConditions = NameRules.(String) -> Boolean

/** 定义 [CountRules] 方法体类型 */
internal typealias CountConditions = CountRules.(Int) -> Boolean

/** 定义 [ModifierRules] 方法体类型 */
internal typealias ModifierConditions = ModifierRules.() -> Boolean

/** 定义 [ObjectRules] 方法体类型 */
internal typealias ObjectConditions = ObjectRules.(Class<*>) -> Boolean

/** 定义 [ObjectRules] 方法体类型 */
internal typealias ObjectsConditions = ObjectRules.(Array<Class<*>>) -> Boolean