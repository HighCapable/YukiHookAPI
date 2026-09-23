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
 * This file is created by fankes on 2022/4/17.
 */
package com.highcapable.yukihookapi.hook.xposed.preference.ui

import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.preference.PreferenceDataStore
import androidx.preference.PreferenceFragmentCompat
import com.highcapable.yukihookapi.YukiHook
import com.highcapable.yukihookapi.hook.xposed.preference.YukiHookPreferences

/**
 * Extends preference support for Xposed modules built with [YukiHook].
 *
 * Inherits [PreferenceFragmentCompat] to provides a [PreferenceDataStore] that
 * uses [YukiHookPreferences] to store preferences in the module's app.
 *
 * Inherits this class instead of [PreferenceFragmentCompat].
 */
abstract class ModulePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    private var current: SharedPreferences? = null

    private val preferences by lazy {
        YukiHookPreferences.from(requireContext())
            .name(preferenceManager.sharedPreferencesName ?: "${requireContext().packageName}_preferences")
    }

    @CallSuper
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.preferenceDataStore = createDataStore()

        current = preferences.current
        current?.registerOnSharedPreferenceChangeListener(this)

        @Suppress("DEPRECATION")
        onCreatePreferencesInModuleApp(savedInstanceState, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) = Unit

    @CallSuper
    override fun onDestroy() {
        current?.unregisterOnSharedPreferenceChangeListener(this)
        current = null

        super.onDestroy()
    }

    // region Deprecated functions for backward compatibility

    /** This function was deprecated, please use [onCreatePreferences] directly. */
    @Deprecated(message = "Use onCreatePreferences directly.")
    open fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?) = Unit

    // endregion

    private fun createDataStore() = object : PreferenceDataStore() {
        override fun getString(key: String, defValue: String?) = preferences.current.getString(key, defValue)
        override fun getStringSet(key: String, defValues: Set<String>?) = preferences.current.getStringSet(key, defValues)
        override fun getInt(key: String, defValue: Int) = preferences.getInt(key, defValue)
        override fun getLong(key: String, defValue: Long) = preferences.getLong(key, defValue)
        override fun getFloat(key: String, defValue: Float) = preferences.getFloat(key, defValue)
        override fun getBoolean(key: String, defValue: Boolean) = preferences.getBoolean(key, defValue)
        override fun putString(key: String, value: String?) = preferences.edit {
            if (value == null) remove(key) else putString(key, value)
        }
        override fun putStringSet(key: String, values: Set<String>?) = preferences.edit {
            if (values == null) remove(key) else putStringSet(key, values)
        }
        override fun putInt(key: String, value: Int) = preferences.edit { putInt(key, value) }
        override fun putLong(key: String, value: Long) = preferences.edit { putLong(key, value) }
        override fun putFloat(key: String, value: Float) = preferences.edit { putFloat(key, value) }
        override fun putBoolean(key: String, value: Boolean) = preferences.edit { putBoolean(key, value) }
    }
}