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
 * This file is created by fankes on 2022/2/3.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.status

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import com.highcapable.yukihookapi.hook.log.YLog

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
 * 详情请参考 [Xposed 模块判断自身激活状态](https://highcapable.github.io/YukiHookAPI/zh-cn/guide/example#xposed-%E6%A8%A1%E5%9D%97%E5%88%A4%E6%96%AD%E8%87%AA%E8%BA%AB%E6%BF%80%E6%B4%BB%E7%8A%B6%E6%80%81)
 *
 * For English version, see [Xposed Module own Active State](https://highcapable.github.io/YukiHookAPI/en/guide/example#xposed-module-own-active-state)
 */
internal object YukiXposedModuleStatus {

    internal const val IS_ACTIVE_METHOD_NAME = "__--"
    internal const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"
    internal const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    internal const val GET_EXECUTOR_API_LEVEL_METHOD_NAME = "-__-"
    internal const val GET_EXECUTOR_VERSION_NAME_METHOD_NAME = "-_-_"
    internal const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

    /**
     * 获取 YukiXposedModuleStatus 完整类名
     * @return [String]
     */
    internal val className get() = runCatching { YukiXposedModuleStatus_Impl.className }.getOrNull() ?: ""

    /**
     * 获取当前模块的激活状态
     *
     * 请使用 [YukiHookAPI.Status.isModuleActive]、[YukiHookAPI.Status.isXposedModuleActive]、[YukiHookAPI.Status.isTaiChiModuleActive] 判断模块激活状态
     * @return [Boolean]
     */
    internal val isActive get() = classMethod(IS_ACTIVE_METHOD_NAME)?.boolean() ?: false

    /**
     * 获取当前 Hook Framework 是否支持资源钩子 (Resources Hook)
     *
     * 请使用 [YukiHookAPI.Status.isSupportResourcesHook] 判断支持状态
     * @return [Boolean]
     */
    internal val isSupportResourcesHook get() = classMethod(IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME)?.boolean() ?: false

    /**
     * 获取当前 Hook Framework 名称
     *
     * 请使用 [YukiHookAPI.Status.Executor.name] 获取
     * @return [String] 模块未激活会返回 unknown
     */
    internal val executorName get() = classMethod(GET_EXECUTOR_NAME_METHOD_NAME)?.string()?.ifBlank { "unknown" } ?: "unknown"

    /**
     * 获取当前 Hook Framework 的 API 版本
     *
     * 请使用 [YukiHookAPI.Status.Executor.apiLevel] 获取
     * @return [Int] 模块未激活会返回 -1
     */
    internal val executorApiLevel get() = classMethod(GET_EXECUTOR_API_LEVEL_METHOD_NAME)?.int()?.takeIf { it > 0 } ?: -1

    /**
     * 获取当前 Hook Framework 版本名称
     *
     * 请使用 [YukiHookAPI.Status.Executor.versionName] 获取
     * @return [Int] 模块未激活会返回 unknown
     */
    internal val executorVersionName get() = classMethod(GET_EXECUTOR_VERSION_NAME_METHOD_NAME)?.string()?.ifBlank { "unknown" } ?: "unknown"

    /**
     * 获取当前 Hook Framework 版本号
     *
     * 请使用 [YukiHookAPI.Status.Executor.versionCode] 获取
     * @return [Int] 模块未激活会返回 -1
     */
    internal val executorVersionCode get() = classMethod(GET_EXECUTOR_VERSION_CODE_METHOD_NAME)?.int()?.takeIf { it > 0 } ?: -1

    /**
     * 通过 [className] 获取方法实例
     * @param name 方法名称
     * @return [MethodFinder.Result.Instance] or null
     */
    private fun classMethod(name: String) = className.toClassOrNull()?.method { this.name = name }?.ignored()?.onNoSuchMethod {
        YLog.innerW("Failed to initialize YukiXposedModuleStatus", it)
    }?.get()
}