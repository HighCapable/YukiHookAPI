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
 * This file is created by fankes on 2026/9/15.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.prefs.ui

import com.highcapable.yukihookapi.hook.xposed.preference.ui.ModulePreferenceFragment

/** Alias for [ModulePreferenceFragment] class. */
@Deprecated(
    message = "This name is deprecated, migrate to ModulePreferenceFragment.",
    replaceWith = ReplaceWith(
        expression = "ModulePreferenceFragment",
        imports = ["com.highcapable.yukihookapi.hook.xposed.preference.ui.ModulePreferenceFragment"]
    )
)
typealias ModulePreferenceFragment = ModulePreferenceFragment