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
 * This file is Created by fankes on 2022/8/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate

import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics

/**
 * 自动处理 (Xposed) 宿主环境与模块环境的 [ClassLoader]
 */
internal class ModuleClassLoader private constructor() : ClassLoader(AppParasitics.baseClassLoader) {

    internal companion object {

        /** 当前 [ModuleClassLoader] 单例 */
        private var instance: ModuleClassLoader? = null

        /**
         * 获取 [ModuleClassLoader] 单例
         * @return [ModuleClassLoader]
         */
        internal fun instance() = instance ?: ModuleClassLoader().apply { instance = this }
    }

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (YukiHookBridge.hasXposedBridge.not()) return AppParasitics.baseClassLoader.loadClass(name)
        return YukiHookAppHelper.currentApplication()?.classLoader?.let { loader ->
            runCatching { return@let AppParasitics.baseClassLoader.loadClass(name) }
            runCatching { if (name == "androidx.lifecycle.ReportFragment") return@let loader.loadClass(name) }
            runCatching { AppParasitics.baseClassLoader.loadClass(name) }.getOrNull() ?: loader.loadClass(name)
        } ?: super.loadClass(name, resolve)
    }
}