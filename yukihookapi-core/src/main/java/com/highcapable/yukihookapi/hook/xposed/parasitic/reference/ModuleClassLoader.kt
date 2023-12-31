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
        private val excludeHostClasses = mutableSetOf<String>()

        /** 排除的模块 [Class] 类名数组 */
        private val excludeModuleClasses = mutableSetOf<String>()

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
         * - 排除列表仅会在 (Xposed) 宿主环境生效
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
         * - 排除列表仅会在 (Xposed) 宿主环境生效
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