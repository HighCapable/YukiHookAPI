/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
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
 * This file is Created by fankes on 2022/2/3.
 * This file is Modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.status

import com.highcapable.yukihookapi.YukiHookAPI

/**
 * 这是一个 Xposed 模块 Hook 状态类
 *
 * 我们需要监听自己的模块是否被激活 - 可使用以下方法调用
 *
 * 调用 [YukiHookAPI.Status.isModuleActive] or [YukiHookAPI.Status.isTaiChiModuleActive]
 *
 * 调用 [YukiHookAPI.Status.isXposedModuleActive]
 *
 * 你还可以通过调用 [YukiHookAPI.Status.Executor] 获取当前 Hook Framework 的详细信息
 *
 * 详情请参考 [Xposed 模块判断自身激活状态](https://fankes.github.io/YukiHookAPI/zh-cn/guide/example#xposed-%E6%A8%A1%E5%9D%97%E5%88%A4%E6%96%AD%E8%87%AA%E8%BA%AB%E6%BF%80%E6%B4%BB%E7%8A%B6%E6%80%81)
 *
 * For English version, see [Xposed Module own Active State](https://fankes.github.io/YukiHookAPI/en/guide/example#xposed-module-own-active-state)
 */
internal object YukiXposedModuleStatus {

    internal const val IS_ACTIVE_METHOD_NAME = "__--"
    internal const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"
    internal const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    internal const val GET_EXECUTOR_API_LEVEL_METHOD_NAME = "-__-"
    internal const val GET_EXECUTOR_VERSION_NAME_METHOD_NAME = "-_-_"
    internal const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

    /** [YukiXposedModuleStatus_Impl] 完整类名 */
    internal const val IMPL_CLASS_NAME = "com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiXposedModuleStatus_Impl"

    /**
     * 获取当前模块的激活状态
     *
     * 请使用 [YukiHookAPI.Status.isModuleActive]、[YukiHookAPI.Status.isXposedModuleActive]、[YukiHookAPI.Status.isTaiChiModuleActive] 判断模块激活状态
     * @return [Boolean]
     */
    internal val isActive get() = runCatching { YukiXposedModuleStatus_Impl.isActive() }.getOrNull() ?: false

    /**
     * 获取当前 Hook Framework 是否支持资源钩子 (Resources Hook)
     *
     * 请使用 [YukiHookAPI.Status.isSupportResourcesHook] 判断支持状态
     * @return [Boolean]
     */
    internal val isSupportResourcesHook get() = runCatching { YukiXposedModuleStatus_Impl.isSupportResourcesHook() }.getOrNull() ?: false

    /**
     * 获取当前 Hook Framework 名称
     *
     * 请使用 [YukiHookAPI.Status.Executor.name] 获取
     * @return [String] 模块未激活会返回 unknown
     */
    internal val executorName get() = runCatching { YukiXposedModuleStatus_Impl.getExecutorName() }.getOrNull() ?: "unknown"

    /**
     * 获取当前 Hook Framework 的 API 版本
     *
     * 请使用 [YukiHookAPI.Status.Executor.apiLevel] 获取
     * @return [Int] 模块未激活会返回 -1
     */
    internal val executorApiLevel get() = runCatching { YukiXposedModuleStatus_Impl.getExecutorApiLevel() }.getOrNull() ?: -1

    /**
     * 获取当前 Hook Framework 版本名称
     *
     * 请使用 [YukiHookAPI.Status.Executor.versionName] 获取
     * @return [Int] 模块未激活会返回 unknown
     */
    internal val executorVersionName get() = runCatching { YukiXposedModuleStatus_Impl.getExecutorVersionName() }.getOrNull() ?: "unknown"

    /**
     * 获取当前 Hook Framework 版本号
     *
     * 请使用 [YukiHookAPI.Status.Executor.versionCode] 获取
     * @return [Int] 模块未激活会返回 -1
     */
    internal val executorVersionCode get() = runCatching { YukiXposedModuleStatus_Impl.getExecutorVersionCode() }.getOrNull() ?: -1
}