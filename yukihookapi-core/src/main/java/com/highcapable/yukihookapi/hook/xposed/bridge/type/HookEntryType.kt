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
 * This file is created by fankes on 2022/4/26.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.type

/**
 * 当前正在进行的 Hook 类型
 */
internal enum class HookEntryType {

    /** 装载 Zygote */
    ZYGOTE,

    /** 装载 APP */
    PACKAGE,

    /** 装载 Resources Hook */
    RESOURCES;

    companion object
}