/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
 * Resolves classes across the (Xposed) host and module [ClassLoader] environments.
 */
class ModuleClassLoader private constructor() : ClassLoader(AppParasitics.baseClassLoader) {

    companion object {

        /** Current [ModuleClassLoader] singleton. */
        private var instance: ModuleClassLoader? = null

        /** Host [Class] names that must bypass the module-first lookup. */
        private val excludeHostClasses = mutableSetOf<String>()

        /** Module [Class] names that must bypass the host lookup. */
        private val excludeModuleClasses = mutableSetOf<String>()

        /**
         * Gets the [ModuleClassLoader] singleton.
         * @return [ModuleClassLoader]
         */
        internal fun instance() = instance ?: ModuleClassLoader().apply { instance = this }

        /**
         * Adds classes to the host exclusion list.
         *
         * Excluded [Class] objects are loaded with the host [ClassLoader].
         *
         * - This list is effective only in the (Xposed) host environment.
         * @param name the fully qualified [Class] names to add.
         */
        fun excludeHostClasses(vararg name: String) {
            excludeHostClasses.addAll(name.toList())
        }

        /**
         * Adds classes to the module exclusion list.
         *
         * Excluded [Class] objects are loaded with the module [ClassLoader] injected into the current host process.
         *
         * - This list is effective only in the (Xposed) host environment.
         * @param name the fully qualified [Class] names to add.
         */
        fun excludeModuleClasses(vararg name: String) {
            excludeModuleClasses.addAll(name.toList())
        }

        init {
            excludeHostClasses.add("androidx.lifecycle.ReportFragment")
        }
    }

    /** Default module [ClassLoader]. */
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