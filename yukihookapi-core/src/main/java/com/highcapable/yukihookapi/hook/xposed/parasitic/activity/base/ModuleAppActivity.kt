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
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.reference.ModuleClassLoader

/**
 * 代理 [Activity]
 *
 * 继承于此类的 [Activity] 可以同时在宿主与模块中启动
 *
 * - 在 (Xposed) 宿主环境需要在宿主启动时调用 [Context.registerModuleAppActivities] 进行注册
 */
open class ModuleAppActivity : Activity() {

    /**
     * 设置当前代理的 [Activity] 类名
     *
     * 留空则使用 [Context.registerModuleAppActivities] 时设置的类名
     *
     * - 代理的 [Activity] 类名必须存在于宿主的 AndroidMainifest 清单中
     * @return [String]
     */
    open val proxyClassName get() = ""

    override fun getClassLoader(): ClassLoader? = ModuleClassLoader.instance()

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        if (YukiXposedModule.isXposedEnvironment) injectModuleAppResources()
        super.onConfigurationChanged(newConfig)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.getBundle("android:viewHierarchyState")?.classLoader = classLoader
        super.onRestoreInstanceState(savedInstanceState)
    }
}