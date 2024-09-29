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
 * 这是对使用 [YukiHookAPI] Xposed 模块实现中的一个扩展功能
 *
 * 此类接管了 [PreferenceFragmentCompat] 并对其实现了 Sp 存储在 Xposed 模块中的全局可读可写
 *
 * 在你使用 [PreferenceFragmentCompat] 的实例中 - 将继承对象换成此类
 *
 * 然后请将重写方法由 [onCreatePreferences] 替换为 [onCreatePreferencesInModuleApp] 即可
 *
 * 详情请参考 [API 文档 - ModulePreferenceFragment](https://highcapable.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/prefs/ModulePreferenceFragment)
 *
 * For English version, see [API Document - ModulePreferenceFragment](https://highcapable.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/prefs/ModulePreferenceFragment)
 */
abstract class ModulePreferenceFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

    /**
     * 获得 Sp 存储名称
     * @return [String]
     */
    private val prefsName get() = "${activity?.packageName}_preferences"

    /**
     * 获取当前 [Fragment] 绑定的 [Activity]
     * @return [Activity]
     * @throws IllegalStateException 如果 [Fragment] 已被销毁或未正确装载
     */
    private val currentActivity get() = requireActivity()

    /**
     * 获取应用默认的 [SharedPreferences]
     * @return [SharedPreferences]
     */
    @Suppress("DEPRECATION", "WorldReadableFiles")
    private val currentSharedPrefs get() = currentActivity.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)

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
     * 对接原始方法 [onCreatePreferences]
     *
     * 请重写此方法以实现模块 Sp 存储的自动化设置全局可读可写数据操作
     * @param savedInstanceState If the fragment is being re-created from a previous saved state, this is the state.
     * @param rootKey If non-null, this preference fragment should be rooted at the [PreferenceScreen] with this key.
     */
    abstract fun onCreatePreferencesInModuleApp(savedInstanceState: Bundle?, rootKey: String?)

    /** 设置自动适配模块 Sp 存储全局可读可写 */
    private fun makeNewXShareReadableIfPossible() = runCatching {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        currentActivity.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    }.onFailure { YukiHookPrefsBridge.makeWorldReadable(currentActivity, prefsFileName = "$prefsName.xml") }.unit()
}