/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
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
 * This file is Created by fankes on 2022/2/2.
 * This file is Modified by fankes on 2022/4/22.
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
 * - ❗请在此类上添加注解 [InjectYukiHookWithXposed] 标记模块 Hook 入口
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
 * 详情请参考 [IYukiHookXposedInit 接口](https://fankes.github.io/YukiHookAPI/#/config/xposed-using?id=iyukihookxposedinit-%e6%8e%a5%e5%8f%a3)
 */
interface IYukiHookXposedInit {

    /**
     * 配置 [YukiHookAPI.Configs] 的初始化方法
     *
     * - ❗在这里只能进行初始化配置 - 不能进行 Hook 操作
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
     * - ❗此接口仅供监听和实现原生 Xposed API 的功能 - 请不要在这里操作 [YukiHookAPI]
     */
    fun onXposedEvent() {}
}