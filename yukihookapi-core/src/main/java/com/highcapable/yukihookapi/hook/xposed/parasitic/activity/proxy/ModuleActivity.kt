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
 * This file is created by fankes on 2025/6/18.
 */
@file:Suppress("UNUSED_PARAMETER")

package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.hook.factory.injectModuleAppResources
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity.Delegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.reference.ModuleClassLoader

/**
 * 模块 [Activity] 代理接口
 *
 * 实现了此接口的 [Activity] 可以同时在宿主与模块中启动
 *
 * - 在 (Xposed) 宿主环境需要在宿主启动时调用 [Context.registerModuleAppActivities] 进行注册
 *
 * - 在 (Xposed) 宿主环境需要重写 [moduleTheme] 设置 AppCompat 主题 (如果当前是 [AppCompatActivity]) - 否则会无法启动
 *
 * 请参考下方示例手动调用 [delegate] 对 [Activity] 完成必要方法的注册 - 建议在自己的 `BaseActivity` 中实现此接口并重写相关方法 -
 * 然后继承自此 `BaseActivity` 来实现模块 [Activity] 的代理
 *
 * ```kotlin
 * abstract class BaseActivity : AppCompatActivity(), ModuleActivity {
 *
 *     // 设置 AppCompat 主题 (如果当前是 [AppCompatActivity])
 *     override val moduleTheme get() = R.style.YourAppTheme
 * 
 *     override fun getClassLoader() = delegate.getClassLoader()
 *
 *     override fun onCreate(savedInstanceState: Bundle?) {
 *         delegate.onCreate(savedInstanceState)
 *         super.onCreate(savedInstanceState)
 *     }
 * 
 *     override fun onConfigurationChanged(newConfig: Configuration) {
 *         delegate.onConfigurationChanged(newConfig)
 *         super.onConfigurationChanged(newConfig)
 *     }
 * 
 *     override fun onRestoreInstanceState(savedInstanceState: Bundle) {
 *         delegate.onRestoreInstanceState(savedInstanceState)
 *         super.onRestoreInstanceState(savedInstanceState)
 *     }
 * }
 * ```
 * @see Delegate
 */
interface ModuleActivity {

    /**
     * 模块 [Activity] 代理提供者
     */
    class Delegate internal constructor(private val self: ModuleActivity) {

        private val selfActivity get() = self as? Activity ?: error("ModuleActivity must be implemented an Activity")

        /**
         * @see Activity.getClassLoader
         */
        fun getClassLoader() = ModuleClassLoader.instance()

        /**
         * @see Activity.onCreate
         */
        fun onCreate(savedInstanceState: Bundle?) {
            if (YukiXposedModule.isXposedEnvironment && self.moduleTheme != -1)
                selfActivity.setTheme(self.moduleTheme)
        }

        /**
         * @see Activity.onConfigurationChanged
         */
        fun onConfigurationChanged(newConfig: Configuration) {
            if (YukiXposedModule.isXposedEnvironment) selfActivity.injectModuleAppResources()
        }

        /**
         * @see Activity.onRestoreInstanceState
         */
        fun onRestoreInstanceState(savedInstanceState: Bundle) {
            savedInstanceState.getBundle("android:viewHierarchyState")?.classLoader = selfActivity.classLoader
        }
    }

    /**
     * 获取当前 [ModuleActivity] 的 [Delegate] 实例
     * @return [Delegate]
     */
    val delegate get() = Delegate(self = this)

    /**
     * 设置当前代理的 [Activity] 主题
     * @return [Int]
     */
    val moduleTheme get() = -1

    /**
     * 设置当前代理的 [Activity] 类名
     *
     * 留空则使用 [Context.registerModuleAppActivities] 时设置的类名
     *
     * - 代理的 [Activity] 类名必须存在于宿主的 AndroidMainifest 清单中
     * @return [String]
     */
    val proxyClassName get() = ""
}