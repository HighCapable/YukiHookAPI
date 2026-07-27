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
 * Proxy contract for a module [Activity].
 *
 * An [Activity] implementing this interface can launch in both host and module environments.
 *
 * - In the (Xposed) host environment, call [Context.registerModuleAppActivities] when the host starts.
 *
 * - In the host environment, an [AppCompatActivity] must override [moduleTheme] with an AppCompat theme.
 *
 * The example below forwards the required lifecycle methods to [delegate]. Implement this interface in a custom `BaseActivity`,
 * override the forwarded methods, and extend that `BaseActivity` from module activities.
 *
 * ```kotlin
 * abstract class BaseActivity : AppCompatActivity(), ModuleActivity {
 *
 *     // Sets the AppCompat theme when this is an [AppCompatActivity].
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
     * Provides the module [Activity] proxy implementation.
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
     * Gets a [Delegate] for the current [ModuleActivity].
     * @return [Delegate]
     */
    val delegate get() = Delegate(self = this)

    /**
     * Defines the theme of the proxied [Activity].
     * @return [Int]
     */
    val moduleTheme get() = -1

    /**
     * Defines the class name of the proxied [Activity].
     *
     * When empty, uses the class configured by [Context.registerModuleAppActivities].
     *
     * - The proxied [Activity] class must be declared in the host AndroidManifest.
     * @return [String]
     */
    val proxyClassName get() = ""
}