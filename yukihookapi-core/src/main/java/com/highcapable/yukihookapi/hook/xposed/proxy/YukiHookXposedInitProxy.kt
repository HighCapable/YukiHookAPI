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
 */
@file:Suppress("unused", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI

/**
 * Legacy Xposed loading API for [YukiHookAPI].
 *
 * - This interface is deprecated and will be removed in a future release.
 *
 * - Migrate to [IYukiHookXposedInit]. The processor rejects declarations that still use this interface.
 */
@Deprecated(message = "This interface name and behavior are deprecated", ReplaceWith("IYukiHookXposedInit"), level = DeprecationLevel.ERROR)
interface YukiHookXposedInitProxy {

    /**
     * - This method is obsolete.
     *
     * - Migrate the interface to [IYukiHookXposedInit].
     */
    @Deprecated(message = "Migrate the interface to IYukiHookXposedInit", level = DeprecationLevel.ERROR)
    fun onInit() = Unit

    /**
     * - This method is obsolete.
     *
     * - Migrate the interface to [IYukiHookXposedInit].
     */
    @Deprecated(message = "Migrate the interface to IYukiHookXposedInit", level = DeprecationLevel.ERROR)
    fun onHook() = Unit
}