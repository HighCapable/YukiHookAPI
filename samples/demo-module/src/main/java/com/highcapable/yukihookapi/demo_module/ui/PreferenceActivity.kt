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
 * This file is created by fankes on 2022/4/18.
 */
package com.highcapable.yukihookapi.demo_module.ui

import android.os.Bundle
import androidx.preference.SwitchPreference
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment

class PreferenceActivity : ModuleAppCompatActivity() {

    override val moduleTheme get() = R.style.Theme_Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = "PreferenceFragment"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (savedInstanceState == null)
            supportFragmentManager
                .beginTransaction()
                .replace(android.R.id.content, SettingsFragment())
                .commitAllowingStateLoss()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    class SettingsFragment : ModulePreferenceFragment() {

        override fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.settings_preferences, rootKey)
            findPreference<SwitchPreference>("show_dialog_when_demo_app_opend")?.isEnabled = YukiHookAPI.Status.isXposedEnvironment.not()
        }
    }
}