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
 * This file is modified by fankes on 2022/4/22.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.proxy

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.configs
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent

/**
 * [YukiHookAPI] 的 Xposed 装载 API 调用接口
 *
 * - 请在此类上添加注解 [InjectYukiHookWithXposed] 标记模块 Hook 入口
 *
 * [YukiHookAPI] 初始化时将自动调用 [onInit] 方法
 *
 * Hook 开始时将自动调用 [onHook] 方法
 *
 * 请在 [onInit] 中调用 [YukiHookAPI.configs] 或直接调用 [configs]
 *
 * 请在 [onHook] 中调用 [YukiHookAPI.encase] 或直接调用 [encase]
 *
 * 你还可以实现监听原生 Xposed API 功能 - 重写 [onXposedEvent] 方法即可
 *
 * 详情请参考 [IYukiHookXposedInit 接口](https://highcapable.github.io/YukiHookAPI/zh-cn/config/xposed-using#iyukihookxposedinit-%E6%8E%A5%E5%8F%A3)
 *
 * For English version, see [IYukiHookXposedInit Interface](https://highcapable.github.io/YukiHookAPI/en/config/xposed-using#iyukihookxposedinit-interface)
 */
interface IYukiHookXposedInit {

    /**
     * 配置 [YukiHookAPI.Configs] 的初始化方法
     *
     * - 在这里只能进行初始化配置 - 不能进行 Hook 操作
     *
     * 此方法可选 - 你也可以选择不对 [YukiHookAPI.Configs] 进行配置
     */
    fun onInit() {}

    /**
     * 模块装载调用入口方法
     *
     * Xposed API
     *
     * 调用 [YukiHookAPI.encase] 或直接调用 [encase] 开始 Hook
     */
    fun onHook()

    /**
     * 监听 Xposed 原生装载事件
     *
     * 若你的 Hook 事件中存在需要兼容的原生 Xposed 功能 - 可在这里实现
     *
     * 请在这里使用 [YukiXposedEvent] 创建回调事件监听
     *
     * 可监听的事件如下：
     *
     * [YukiXposedEvent.onInitZygote]
     *
     * [YukiXposedEvent.onHandleLoadPackage]
     *
     * [YukiXposedEvent.onHandleInitPackageResources]
     *
     * - 此接口仅供监听和实现原生 Xposed API 的功能 - 请不要在这里操作 [YukiHookAPI]
     */
    fun onXposedEvent() {}
}