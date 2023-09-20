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
 * This file is created by fankes on 2022/8/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.parasitic.reference

import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics

/**
 * 自动处理 (Xposed) 宿主环境与模块环境的 [ClassLoader]
 */
class ModuleClassLoader private constructor() : ClassLoader(AppParasitics.baseClassLoader) {

    companion object {

        /** 当前 [ModuleClassLoader] 单例 */
        private var instance: ModuleClassLoader? = null

        /** 排除的 Hook APP (宿主) [Class] 类名数组 */
        private val excludeHostClasses = HashSet<String>()

        /** 排除的模块 [Class] 类名数组 */
        private val excludeModuleClasses = HashSet<String>()

        /**
         * 获取 [ModuleClassLoader] 单例
         * @return [ModuleClassLoader]
         */
        internal fun instance() = instance ?: ModuleClassLoader().apply { instance = this }

        /**
         * 添加到 Hook APP (宿主) [Class] 排除列表
         *
         * 排除列表中的 [Class] 将会使用宿主的 [ClassLoader] 进行装载
         *
         * - ❗排除列表仅会在 (Xposed) 宿主环境生效
         * @param name 需要添加的 [Class] 完整类名
         */
        fun excludeHostClasses(vararg name: String) {
            excludeHostClasses.addAll(name.toList())
        }

        /**
         * 添加到模块 [Class] 排除列表
         *
         * 排除列表中的 [Class] 将会使用模块 (当前宿主环境的模块注入进程) 的 [ClassLoader] 进行装载
         *
         * - ❗排除列表仅会在 (Xposed) 宿主环境生效
         * @param name 需要添加的 [Class] 完整类名
         */
        fun excludeModuleClasses(vararg name: String) {
            excludeModuleClasses.addAll(name.toList())
        }

        init {
            excludeHostClasses.add("androidx.lifecycle.ReportFragment")
        }
    }

    /** 默认 [ClassLoader] */
    private val baseLoader get() = AppParasitics.baseClassLoader

    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (YukiXposedModule.isXposedEnvironment.not()) return baseLoader.loadClass(name)
        return AppParasitics.currentApplication?.classLoader?.let { hostLoader ->
            excludeHostClasses.takeIf { it.isNotEmpty() }?.forEach { runCatching { if (name == it) return@let hostLoader.loadClass(name) } }
            excludeModuleClasses.takeIf { it.isNotEmpty() }?.forEach { runCatching { if (name == it) return@let baseLoader.loadClass(name) } }
            runCatching { return@let baseLoader.loadClass(name) }
            runCatching { baseLoader.loadClass(name) }.getOrNull() ?: hostLoader.loadClass(name)
        } ?: super.loadClass(name, resolve)
    }
}