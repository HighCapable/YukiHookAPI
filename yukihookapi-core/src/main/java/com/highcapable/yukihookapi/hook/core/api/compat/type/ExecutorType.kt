/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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