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
 * This file is created by fankes on 2022/8/15.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/ui/CommonContextWrapper.java
 */
@file:Suppress("unused", "DEPRECATION")

package com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.ContextThemeWrapper
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.reference.ModuleClassLoader

/**
 * 代理 [ContextThemeWrapper]
 *
 * 通过包装 - 你可以轻松在 (Xposed) 宿主环境使用来自模块的主题资源
 * @param baseContext 原始 [Context]
 * @param theme 使用的主题
 * @param configuration 使用的 [Configuration]
 */
class ModuleContextThemeWrapper private constructor(baseContext: Context, theme: Int, configuration: Configuration?) :
    ContextThemeWrapper(baseContext, theme) {

    internal companion object {

        /**
         * 从 [Context] 创建 [ModuleContextThemeWrapper]
         * @param baseContext 对接的 [Context]
         * @param theme 需要使用的主题
         * @param configuration 使用的 [Configuration]
         * @return [ModuleContextThemeWrapper]
         * @throws IllegalStateException 如果重复装载
         */
        internal fun wrapper(baseContext: Context, theme: Int, configuration: Configuration?) =
            if (baseContext !is ModuleContextThemeWrapper)
                ModuleContextThemeWrapper(baseContext, theme, configuration)
            else error("ModuleContextThemeWrapper already loaded")
    }

    /** 创建用于替换的 [Resources] */
    private var baseResources: Resources? = null

    init {
        configuration?.also {
            baseResources = baseContext.createConfigurationContext(it)?.resources
            baseResources?.updateConfiguration(it, baseContext.resources.displayMetrics)
        }
        if (YukiXposedModule.isXposedEnvironment) resources?.injectModuleAppResources()
    }

    /**
     * 设置当前 [ModuleContextThemeWrapper] 的 [Configuration]
     *
     * 设置后会自动调用 [Resources.updateConfiguration]
     * @param initiate [Configuration] 方法体
     * @return [ModuleContextThemeWrapper]
     */
    fun applyConfiguration(initiate: Configuration.() -> Unit): ModuleContextThemeWrapper {
        resources?.configuration?.apply(initiate)
        resources?.updateConfiguration(resources?.configuration, resources?.displayMetrics)
        return this
    }

    override fun getClassLoader(): ClassLoader = ModuleClassLoader.instance()

    override fun getResources(): Resources? = baseResources ?: super.getResources()
}