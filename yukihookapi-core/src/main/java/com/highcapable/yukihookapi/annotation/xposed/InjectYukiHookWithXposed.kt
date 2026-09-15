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
 * This file is created by fankes on 2022/2/3.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.annotation.xposed

import com.highcapable.yukihookapi.YukiHookAPI

/**
 * Marks the [YukiHookAPI] legacy Xposed entry class for code generation.
 *
 * - This annotation was deprecated, migrate to [YukiHookXposed82Entry].
 */
@Deprecated(message = "Migrate to YukiHookXposed82Entry.", replaceWith = ReplaceWith("YukiHookXposed82Entry"))
@Target(AnnotationTarget.CLASS)
annotation class InjectYukiHookWithXposed(
    val sourcePath: String = "src/main",
    val modulePackageName: String = "",
    val entryClassName: String = "",
    val isUsingXposedModuleStatus: Boolean = true
)