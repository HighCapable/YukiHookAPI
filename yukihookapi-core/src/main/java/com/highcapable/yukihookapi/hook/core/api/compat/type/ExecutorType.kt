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
 * This file is created by fankes on 2023/4/16.
 */
package com.highcapable.yukihookapi.hook.core.api.compat.type

/**
 * Hook Framework 类型定义
 *
 * 定义了目前已知使用频率较高的 Hook Framework
 *
 * 后期根据 Hook Framework 特征和使用情况将会继续添加新的类型
 *
 * 无法识别的 Hook Framework 将被定义为 [UNKNOWN]
 */
enum class ExecutorType {
    /** 未知类型 */
    UNKNOWN,

    /** 原版、第三方 Xposed */
    XPOSED,

    /** LSPosed、LSPatch */
    LSPOSED_LSPATCH,

    /** EdXposed */
    ED_XPOSED,

    /** TaiChi (太极) */
    TAICHI_XPOSED,

    /** BugXposed (应用转生) */
    BUG_XPOSED
}