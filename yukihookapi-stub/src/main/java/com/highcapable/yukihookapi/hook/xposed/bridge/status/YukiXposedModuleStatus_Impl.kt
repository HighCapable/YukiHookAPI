/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is Modified by fankes on 2023/1/9.
 */
@file:Suppress("ClassName")

package com.highcapable.yukihookapi.hook.xposed.bridge.status

/**
 * YukiXposedModuleStatus 注入 Stub
 */
object YukiXposedModuleStatus_Impl {

    private const val IS_ACTIVE_METHOD_NAME = "__--"
    private const val IS_SUPPORT_RESOURCES_HOOK_METHOD_NAME = "_--_"
    private const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
    private const val GET_EXECUTOR_API_LEVEL_METHOD_NAME = "-__-"
    private const val GET_EXECUTOR_VERSION_NAME_METHOD_NAME = "-_-_"
    private const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

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
     * 此方法经过 Hook 后返回当前 Hook Framework 名称
     *
     * 返回值将在每次编译时自动生成
     * @return [String]
     */
    @JvmName(GET_EXECUTOR_NAME_METHOD_NAME)
    fun getExecutorName(): String = error("Stub!")

    /**
     * 此方法经过 Hook 后返回当前 Hook Framework 的 API 版本
     *
     * 返回值将在每次编译时自动生成
     * @return [Int]
     */
    @JvmName(GET_EXECUTOR_API_LEVEL_METHOD_NAME)
    fun getExecutorApiLevel(): Int = error("Stub!")

    /**
     * 此方法经过 Hook 后返回当前 Hook Framework 版本名称
     *
     * 返回值将在每次编译时自动生成
     * @return [String]
     */
    @JvmName(GET_EXECUTOR_VERSION_NAME_METHOD_NAME)
    fun getExecutorVersionName(): String = error("Stub!")

    /**
     * 此方法经过 Hook 后返回当前 Hook Framework 版本号
     *
     * 返回值将在每次编译时自动生成
     * @return [Int]
     */
    @JvmName(GET_EXECUTOR_VERSION_CODE_METHOD_NAME)
    fun getExecutorVersionCode(): Int = error("Stub!")
}