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
 * This file is Created by fankes on 2023/1/1.
 */
@file:Suppress("ClassName")

package com.highcapable.yukihookapi.hook.xposed.bridge.status

import de.robv.android.xposed.XposedBridge

/**
 * YukiHookModuleStatus 注入 Stub
 */
object YukiHookModuleStatus_Impl {

    /** 定义 Jvm 方法名 */
    private const val IS_ACTIVE_METHOD_NAME = "__--"

    /** 定义 Jvm 方法名 */
    private const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"

    /** 定义 Jvm 方法名 */
    private const val GET_XPOSED_VERSION_METHOD_NAME = "--__"

    /** 定义 Jvm 方法名 */
    private const val GET_XPOSED_TAG_METHOD_NAME = "_-_-"

    /**
     * 此方法经过 Hook 后返回 true 即模块已激活
     *
     * 返回值将在每次编译时自动生成
     * @return [Boolean]
     */
    @JvmName(IS_ACTIVE_METHOD_NAME)
    fun isActive(): Boolean = error("Stub!")

    /**
     * 此方法经过 Hook 后返回 true 即当前 Hook Framework 支持资源钩子 (Resources Hook)
     *
     * 返回值将在每次编译时自动生成
     * @return [Boolean]
     */
    @JvmName(IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME)
    fun isSupportResourcesHook(): Boolean = error("Stub!")

    /**
     * 此方法经过 Hook 后返回 [XposedBridge.getXposedVersion]
     *
     * 返回值将在每次编译时自动生成
     * @return [Int]
     */
    @JvmName(GET_XPOSED_VERSION_METHOD_NAME)
    fun getXposedVersion(): Int = error("Stub!")

    /**
     * 此方法经过 Hook 后返回 [XposedBridge] 的 TAG
     *
     * 返回值将在每次编译时自动生成
     * @return [String]
     */
    @JvmName(GET_XPOSED_TAG_METHOD_NAME)
    fun getXposedBridgeTag(): String = error("Stub!")
}