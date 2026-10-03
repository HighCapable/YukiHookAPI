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
 * This file is created by fankes on 2022/8/15.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/ui/CommonContextWrapper.java
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.ContextThemeWrapper
import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.hook.factory.injectModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.parasitic.reference.ModuleClassLoader

/**
 * Proxies [ContextThemeWrapper].
 *
 * Allows module theme resources to be used in the host environment.
 * @param baseContext the original [Context].
 * @param theme the theme to apply.
 * @param configuration the [Configuration] to apply.
 */
class ModuleContextThemeWrapper private constructor(baseContext: Context, theme: Int, configuration: Configuration?) : ContextThemeWrapper(
    baseContext, theme
) {

    internal companion object {

        /**
         * Creates a [ModuleContextThemeWrapper] from [Context].
         * @param baseContext the wrapped [Context].
         * @param theme the theme to apply.
         * @param configuration the [Configuration] to apply.
         * @return [ModuleContextThemeWrapper]
         * @throws IllegalStateException if the context is already wrapped.
         */
        fun from(baseContext: Context, theme: Int, configuration: Configuration?) = if (baseContext !is ModuleContextThemeWrapper)
            ModuleContextThemeWrapper(baseContext, theme, configuration)
        else error("ModuleContextThemeWrapper is already loaded.")
    }

    private var baseResources: Resources? = null

    init {
        configuration?.also {
            baseResources = baseContext.createConfigurationContext(it)?.resources
            @Suppress("DEPRECATION")
            baseResources?.updateConfiguration(it, baseContext.resources.displayMetrics)
        }

        if (XposedModuleRuntime.isHostEnvironment) resources?.injectModuleResources()
    }

    /**
     * Applies a [Configuration] to the current [ModuleContextThemeWrapper].
     *
     * Calls [Resources.updateConfiguration] after applying the configuration block.
     * @param block the [Configuration] block.
     * @return [ModuleContextThemeWrapper]
     */
    fun applyConfiguration(block: Configuration.() -> Unit): ModuleContextThemeWrapper {
        resources?.configuration?.apply(block)
        @Suppress("DEPRECATION")
        resources?.updateConfiguration(resources?.configuration, resources?.displayMetrics)

        return this
    }

    @CallSuper
    override fun getClassLoader(): ClassLoader = ModuleClassLoader.get()

    @CallSuper
    override fun getResources(): Resources? = baseResources ?: super.getResources()
}