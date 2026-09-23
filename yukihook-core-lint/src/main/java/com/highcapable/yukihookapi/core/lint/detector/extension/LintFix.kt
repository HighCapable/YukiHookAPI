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
 * This file is created by fankes on 2026/9/17.
 */
package com.highcapable.yukihookapi.core.lint.detector.extension

import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location

internal fun buildReplaceFix(name: String, replacement: String, vararg imports: String, range: Location? = null, reformat: Boolean = true) =
    LintFix.create()
        .name(name)
        .replace()
        .run { if (range != null) range(range) else all() }
        .with(replacement)
        .reformat(reformat)
        .shortenNames()
        .imports(*imports)
        .build()