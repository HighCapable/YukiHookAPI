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
 * This file is created by fankes on 2025/6/11.
 */
package com.highcapable.yukihookapi.hook.core.finder

/**
 * YukiHookAPI 反射 API 迁移指引类
 */
internal object ReflectionMigration {

    /**
     * [KavaRef](https://github.com/HighCapable/KavaRef) 迁移指引信息
     */
    const val KAVAREF_INFO = "YukiHookAPI 会在 2.0.0 版本完全移除自身反射部分的 API，我们推荐你使用 KavaRef 来实现反射功能以获得更好的性能和可维护性。" +
        "请参阅：https://github.com/HighCapable/KavaRef " +
        "YukiHookAPI will completely remove the API of its own reflection part in version 2.0.0. " +
        "We recommend that you use KavaRef to implement reflection for better performance and maintainability. " +
        "See: https://github.com/HighCapable/KavaRef"
}