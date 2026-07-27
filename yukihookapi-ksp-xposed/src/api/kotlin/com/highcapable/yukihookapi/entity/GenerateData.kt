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
 * This file is created by fankes on 2022/9/20.
 */
package com.highcapable.yukihookapi.entity

/**
 * Represents data used to generate source templates.
 * @param entryPackageName the package name of the entry class.
 * @param modulePackageName the module package name (namespace).
 * @param customMPackageName the custom module package name.
 * @param entryClassName the entry class name.
 * @param xInitClassName the `xposed_init` entry class name.
 * @param isEntryClassKindOfObject whether the entry class is an `object` singleton.
 * @param isUsingXposedModuleStatus whether Xposed module status detection is enabled.
 * @param isUsingResourcesHook whether Resources Hook is enabled.
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