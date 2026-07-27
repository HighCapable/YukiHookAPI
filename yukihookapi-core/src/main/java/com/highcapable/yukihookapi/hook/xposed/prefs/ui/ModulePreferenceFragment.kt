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
 * This file is created by fankes on 2022/4/17.
 */
package com.highcapable.yukihookapi.hook.xposed.prefs.ui

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import androidx.preference.PreferenceScreen
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.utils.factory.unit
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge

/**
 * Extends preference support for Xposed modules built with [YukiHookAPI].
 *
 * Extends [PreferenceFragmentCompat] and makes the module's SharedPreferences globally readable and writable.
 *
 * Extend this class instead of [PreferenceFragmentCompat].
 *
 * Override [onCreatePreferencesInModuleApp] instead of [onCreatePreferences].
 */
abstract class ModulePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * Gets the SharedPreferences name.
     * @return [String]
     */
    private val prefsName get() = "${activity?.packageName}_preferences"

    /**
     * Gets the [Activity] attached to the current [Fragment].
     * @return [Activity]
     * @throws IllegalStateException if the [Fragment] is destroyed or not attached correctly.
     */
    private val currentActivity get() = requireActivity()

    /**
     * Gets the app's default [SharedPreferences].
     * @return [SharedPreferences]
     */
    private val currentSharedPrefs get() = runCatching {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        currentActivity.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    }.getOrNull() ?: PreferenceManager.getDefaultSharedPreferences(currentActivity)

    @CallSuper
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        currentSharedPrefs.registerOnSharedPreferenceChangeListener(this)
        makeNewXShareReadableIfPossible()
        onCreatePreferencesInModuleApp(savedInstanceState, rootKey)
    }

    @CallSuper
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        makeNewXShareReadableIfPossible()
    }

    @CallSuper
    override fun onDestroy() {
        currentSharedPrefs.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    /**
     * Replaces the original [onCreatePreferences] entry point.
     *
     * Override this method to configure preferences while automatically enabling global module access.
     * @param savedInstanceState if the fragment is being re-created from a previous saved state, this is the state.
     * @param rootKey if non-null, this preference fragment should be rooted at the [PreferenceScreen] with this key.
     */
    abstract fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?)

    /** Makes the module preferences globally readable and writable when possible. */
    private fun makeNewXShareReadableIfPossible() = runCatching {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        currentActivity.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    }.onFailure { YukiHookPrefsBridge.makeWorldReadable(currentActivity, prefsFileName = "$prefsName.xml") }.unit()
}