/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/4/17.
 */
@file:Suppress("WorldReadableFiles", "DEPRECATION")

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
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs

/**
 * 这是对使用 [YukiHookAPI] Xposed 模块实现中的一个扩展功能
 *
 * 此类接管了 [PreferenceFragmentCompat] 并对其实现了 Sp 存储在 Xposed 模块中的全局可读可写
 *
 * 在你使用 [PreferenceFragmentCompat] 的实例中 - 将继承对象换成此类
 *
 * 然后请将重写方法由 [onCreatePreferences] 替换为 [onCreatePreferencesInModuleApp] 即可
 *
 * 详情请参考 [ModulePreferenceFragment](https://fankes.github.io/YukiHookAPI/#/api/document?id=modulepreferencefragment-class)
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
    private val currentSharedPrefs get() = PreferenceManager.getDefaultSharedPreferences(currentActivity)

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
    private fun makeNewXShareReadableIfPossible() = try {
        currentActivity.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
    } catch (_: Throwable) {
        YukiHookModulePrefs.makeWorldReadable(currentActivity, prefsFileName = "$prefsName.xml")
    }
}