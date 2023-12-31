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
 * This file is created by fankes on 2022/9/20.
 */
package com.highcapable.yukihookapi.bean

/**
 * 生成的模板数据实例
 * @param entryPackageName 入口类包名
 * @param modulePackageName 模块包名 (命名空间)
 * @param customMPackageName 自定义模块包名
 * @param entryClassName 入口类名
 * @param xInitClassName xposed_init 入口类名
 * @param isEntryClassKindOfObject 入口类种类 (类型) 是否为 object (单例)
 * @param isUsingXposedModuleStatus 是否启用 Xposed 模块状态检测
 * @param isUsingResourcesHook 是否启用 Resources Hook
 */
data class GenerateData(
    var entryPackageName: String = "",
    var modulePackageName: String = "",
    var customMPackageName: String = "",
    var entryClassName: String = "",
    var xInitClassName: String = "",
    var isEntryClassKindOfObject: Boolean = false,
    var isUsingXposedModuleStatus: Boolean = true,
    var isUsingResourcesHook: Boolean = false
)