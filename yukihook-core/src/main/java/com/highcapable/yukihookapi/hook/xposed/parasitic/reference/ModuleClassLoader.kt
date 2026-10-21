/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.parasitic.reference

import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism

/**
 * Resolves classes across the host and module [ClassLoader] environments.
 *
 * The initial partial solution was provided by [Parasitics](https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java)
 */
class ModuleClassLoader private constructor() : ClassLoader(AppParasitism.baseClassLoader) {

    companion object {

        private val instance by lazy { ModuleClassLoader() }

        /** Host [Class] names that must bypass the module-first lookup. */
        @Volatile
        private var excludeHostClasses = setOf("androidx.lifecycle.ReportFragment")

        /** Module [Class] names that must bypass the host lookup. */
        @Volatile
        private var excludeModuleClasses = emptySet<String>()

        /** Coordinates publication of class-exclusion snapshots. */
        private val exclusionLock = Any()

        /**
         * Gets the [ModuleClassLoader] singleton.
         * @return [ModuleClassLoader]
         */
        internal fun get() = instance

        /**
         * Adds classes to the host exclusion list.
         *
         * Excluded [Class] objects are loaded with the host [ClassLoader].
         *
         * - This list is effective only in the host environment.
         * @param name the fully qualified [Class] names to add.
         */
        fun excludeHostClasses(vararg name: String) {
            synchronized(exclusionLock) {
                excludeHostClasses = excludeHostClasses + name
            }
        }

        /**
         * Adds classes to the module exclusion list.
         *
         * Excluded [Class] objects are loaded with the module [ClassLoader] injected into the current host process.
         *
         * - This list is effective only in the host environment.
         * @param name the fully qualified [Class] names to add.
         */
        fun excludeModuleClasses(vararg name: String) {
            synchronized(exclusionLock) {
                excludeModuleClasses = excludeModuleClasses + name
            }
        }
    }

    private val baseLoader get() = AppParasitism.baseClassLoader

    /** Only a missing class is a miss, a class that exists but fails to load keeps its own error. */
    private fun ClassLoader.loadClassOrNull(name: String) = try {
        loadClass(name)
    } catch (_: ClassNotFoundException) {
        null
    }

    @CallSuper
    override fun loadClass(name: String, resolve: Boolean): Class<*> {
        if (!XposedModuleRuntime.isHostEnvironment) return baseLoader.loadClass(name)

        val hostLoader = AppParasitism.currentApplication?.classLoader ?: return super.loadClass(name, resolve)
        if (name in excludeHostClasses) hostLoader.loadClassOrNull(name)?.let { return it }
        if (name in excludeModuleClasses) baseLoader.loadClassOrNull(name)?.let { return it }

        return baseLoader.loadClassOrNull(name) ?: hostLoader.loadClass(name)
    }
}