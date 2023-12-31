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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI

/**
 * [YukiHookAPI] 的 Xposed 装载 API 调用接口
 *
 * - 此接口已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [IYukiHookXposedInit] 否则此接口的声明将在自动处理程序中被拦截
 */
@Deprecated(message = "此接口的命名和功能已被弃用", ReplaceWith("IYukiHookXposedInit"), level = DeprecationLevel.ERROR)
interface YukiHookXposedInitProxy {

    /**
     * - 此方法已过时
     *
     * - 请将接口迁移到 [IYukiHookXposedInit]
     */
    @Deprecated(message = "请将接口迁移到 IYukiHookXposedInit", level = DeprecationLevel.ERROR)
    fun onInit() = Unit

    /**
     * - 此方法已过时
     *
     * - 请将接口迁移到 [IYukiHookXposedInit]
     */
    @Deprecated(message = "请将接口迁移到 IYukiHookXposedInit", level = DeprecationLevel.ERROR)
    fun onHook() = Unit
}