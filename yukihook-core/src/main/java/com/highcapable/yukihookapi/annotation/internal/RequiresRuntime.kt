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
 * This file is created by fankes on 2026/9/16.
 */
package com.highcapable.yukihookapi.annotation.internal

import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Marks an API that requires a specific [YukiHookRuntime].
 * @param value the required runtime.
 * @param apiVersion the Xposed API version required by the API, or 0 to check only the runtime.
 */
@Retention(AnnotationRetention.BINARY)
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FIELD,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.FUNCTION
)
internal annotation class RequiresRuntime(
    val value: YukiHookRuntime,
    val apiVersion: Int = 0
)