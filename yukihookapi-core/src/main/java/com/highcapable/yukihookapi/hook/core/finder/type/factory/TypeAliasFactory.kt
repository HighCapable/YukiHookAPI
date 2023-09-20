/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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