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
 * This file is created by fankes on 2026/9/15.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.prefs.data

import com.highcapable.yukihookapi.hook.xposed.preference.data.PreferenceData

/** Alias for [PreferenceData] class. */
@Deprecated(
    message = "This name is deprecated, migrate to PreferenceData.",
    replaceWith = ReplaceWith(
        expression = "PreferenceData",
        imports = ["com.highcapable.yukihookapi.hook.xposed.preference.data.PreferenceData"]
    )
)
typealias PrefsData<T> = PreferenceData<T>