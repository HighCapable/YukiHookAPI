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
 * This file is Created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.status

import androidx.annotation.Keep
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.yLoggerD
import de.robv.android.xposed.XposedBridge

/**
 * 这是一个 Xposed 模块 Hook 状态类
 *
 * 我们需要监听自己的模块是否被激活 - 可使用以下方法调用
 *
 * 调用 [YukiHookAPI.Status.isModuleActive] 或 [YukiHookAPI.Status.isTaiChiModuleActive]
 *
 * 调用 [YukiHookAPI.Status.isXposedModuleActive]
 *
 * 你还可以使用以下方法获取当前 Hook 框架的详细信息
 *
 * 调用 [YukiHookAPI.Status.executorName] 来获取当前 Hook 框架的名称
 *
 * 调用 [YukiHookAPI.Status.executorVersion] 来获取当前 Hook 框架的版本
 *
 * 详情请参考 [Xposed 模块判断自身激活状态](https://fankes.github.io/YukiHookAPI/#/guide/example?id=xposed-%e6%a8%a1%e5%9d%97%e5%88%a4%e6%96%ad%e8%87%aa%e8%ba%ab%e6%bf%80%e6%b4%bb%e7%8a%b6%e6%80%81)
 */
internal object YukiHookModuleStatus {

    /** 定义 Jvm 方法名 */
    internal const val IS_ACTIVE_METHOD_NAME = "__--"

    /** 定义 Jvm 方法名 */
    internal const val HAS_RESOURCES_HOOK_METHOD_NAME = "_--_"

    /** 定义 Jvm 方法名 */
    internal const val GET_XPOSED_VERSION_METHOD_NAME = "--__"

    /** 定义 Jvm 方法名 */
    internal const val GET_XPOSED_TAG_METHOD_NAME = "_-_-"

    /**
     * 获取当前 Hook 框架的名称
     *
     * 从 [XposedBridge] 获取 TAG
     *
     * 请使用 [YukiHookAPI.Status.executorName] 获取
     * @return [String] 模块未激活会返回 unknown
     */
    internal val executorName
        get() = getXposedBridgeTag().replace(oldValue = "Bridge", newValue = "").replace(oldValue = "-", newValue = "").trim()

    /**
     * 获取当前 Hook 框架的版本
     *
     * 获取 [XposedBridge.getXposedVersion]
     *
     * 请使用 [YukiHookAPI.Status.executorVersion] 获取
     * @return [Int] 模块未激活会返回 -1
     */
    internal val executorVersion get() = getXposedVersion()

    /**
     * 此方法经过 Hook 后返回 true 即模块已激活
     *
     * 请使用 [YukiHookAPI.Status.isModuleActive]、[YukiHookAPI.Status.isXposedModuleActive]、[YukiHookAPI.Status.isTaiChiModuleActive] 判断模块激活状态
     * @return [Boolean]
     */
    @Keep
    @JvmName(IS_ACTIVE_METHOD_NAME)
    internal fun isActive(): Boolean {
        yLoggerD(msg = IS_ACTIVE_METHOD_NAME, isDisableLog = true)
        return false
    }

    /**
     * 此方法经过 Hook 后返回 true 即当前 Hook Framework 支持资源钩子(Resources Hook)
     *
     * 请使用 [YukiHookAPI.Status.isSupportResourcesHook] 判断支持状态
     * @return [Boolean]
     */
    @Keep
    @JvmName(HAS_RESOURCES_HOOK_METHOD_NAME)
    internal fun hasResourcesHook(): Boolean {
        yLoggerD(msg = HAS_RESOURCES_HOOK_METHOD_NAME, isDisableLog = true)
        return false
    }

    /**
     * 此方法经过 Hook 后返回 [XposedBridge.getXposedVersion]
     * @return [Int]
     */
    @Keep
    @JvmName(GET_XPOSED_VERSION_METHOD_NAME)
    private fun getXposedVersion(): Int {
        yLoggerD(msg = GET_XPOSED_VERSION_METHOD_NAME, isDisableLog = true)
        return -1
    }

    /**
     * 此方法经过 Hook 后返回 [XposedBridge] 的 TAG
     * @return [String]
     */
    @Keep
    @JvmName(GET_XPOSED_TAG_METHOD_NAME)
    private fun getXposedBridgeTag(): String {
        yLoggerD(msg = GET_XPOSED_TAG_METHOD_NAME, isDisableLog = true)
        return "unknown"
    }
}