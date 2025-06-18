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
package com.highcapable.yukihookapi.demo_module.ui.base

import android.content.res.Configuration
import android.os.Bundle
import com.highcapable.betterandroid.ui.component.activity.AppViewsActivity
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity

abstract class BaseActivity : AppViewsActivity(), ModuleActivity {

    override val moduleTheme get() = R.style.Theme_Default
    
    override fun getClassLoader() = delegate.getClassLoader()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }
    
    override fun onConfigurationChanged(newConfig: Configuration) {
        delegate.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }
    
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        delegate.onRestoreInstanceState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }
}