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
 * This file is Created by fankes on 2022/4/29.
 */
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.xposed.bridge.dummy

import android.content.res.Resources
import android.content.res.XModuleResources
import android.content.res.XResForwarder

/**
 * 对接 [XModuleResources] 的中间层实例
 * @param baseInstance 原始实例
 */
class YukiModuleResources private constructor(private val baseInstance: XModuleResources) :
    Resources(
        runCatching { baseInstance.assets }.getOrNull(),
        runCatching { baseInstance.displayMetrics }.getOrNull(),
        runCatching { baseInstance.configuration }.getOrNull()
    ) {

    internal companion object {

        /**
         * 对接 [XModuleResources.createInstance] 方法
         *
         * 创建 [YukiModuleResources] 与 [XModuleResources] 实例
         * @param path Xposed 模块 APK 路径
         * @return [YukiModuleResources]
         */
        internal fun createInstance(path: String) = YukiModuleResources(XModuleResources.createInstance(path, null))
    }

    /**
     * 对接 [XModuleResources.fwd] 方法
     *
     * 创建 [YukiResForwarder] 与 [XResForwarder] 实例
     * @param resId Resources Id
     * @return [YukiResForwarder]
     */
    fun fwd(resId: Int) = YukiResForwarder(baseInstance.fwd(resId))

    override fun toString() = "YukiModuleResources by $baseInstance"
}