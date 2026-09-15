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
 * This file is created by fankes on 2022/2/2.
 * This file is modified by fankes on 2022/4/22.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule

/**
 * Xposed module entry point for [YukiHookAPI].
 *
 * - This entry was deprecated, migrate to [YukiHookXposedModule].
 */
@Deprecated(
    message = "This entry was deprecated, migrate to YukiHookXposedModule.",
    replaceWith = ReplaceWith(
        expression = "YukiHookXposedModule",
        imports = ["com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule"]
    )
)
interface IYukiHookXposedInit {

    @Deprecated(message = "Migrate to YukiHookXposedModule.onInit()")
    fun onInit() = Unit

    @Deprecated(message = "Migrate to YukiHookXposedModule.onHook()")
    fun onHook()

    @Deprecated(message = "Migrate to YukiHookXposedModule.onXposedEvent()")
    fun onXposedEvent() = Unit
}