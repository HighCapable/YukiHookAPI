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
 * This file is created by fankes on 2022/4/3.
 */
package com.highcapable.yukihookapi.hook.type.defined

import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * 未定义类型实例
 *
 * 请使用 [UndefinedType] 来调用它
 */
internal class UndefinedClass private constructor()

/**
 * 模糊类型实例
 *
 * 请使用 [VagueType] 来调用它
 */
class VagueClass private constructor()

/**
 * 得到未定义类型
 * @return [Class]<[UndefinedClass]>
 */
internal val UndefinedType get() = classOf<UndefinedClass>()

/**
 * 得到模糊类型
 * @return [Class]<[VagueClass]>
 */
val VagueType get() = classOf<VagueClass>()