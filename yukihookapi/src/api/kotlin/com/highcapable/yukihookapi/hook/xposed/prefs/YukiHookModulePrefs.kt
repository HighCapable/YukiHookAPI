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
 * This file is Created by fankes on 2022/2/8.
 */
@file:Suppress("SetWorldReadable", "CommitPrefEdits", "DEPRECATION", "WorldReadableFiles", "unused")

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * 实现 Xposed 模块的数据存取
 *
 * 对接 [SharedPreferences] 和 [XSharedPreferences]
 *
 * 在不同环境智能选择存取使用的对象
 *
 * - ❗请注意此功能为实验性功能 - 仅在 LSPosed 环境测试通过
 *
 * - 使用 LSPosed 环境请在 AndroidManifests.xml 中将 "xposedminversion" 最低设置为 93
 *
 * - 未使用 LSPosed 环境请将你的模块 API 降至 26 以下 - [YukiHookAPI] 将会尝试使用 [makeWorldReadable] 但仍有可能不成功
 *
 * - ❗当你在模块中存取数据的时候 [context] 必须不能是空的
 *
 * - 详情请参考 [API 文档 - YukiHookModulePrefs](https://github.com/fankes/YukiHookAPI/wiki/API-%E6%96%87%E6%A1%A3#yukihookmoduleprefs-class)
 * @param context 上下文实例 - 默认空
 */
class YukiHookModulePrefs(private val context: Context? = null) {

    /** 存储名称 - 默认包名 + _preferences */
    private var prefsName = "${YukiHookAPI.modulePackageName.ifBlank { context?.packageName ?: "" }}_preferences"

    /** 是否为 Xposed 环境 */
    private val isXposedEnvironment = YukiHookAPI.hasXposedBridge

    /** [XSharedPreferences] 缓存的 [String] 键值数据 */
    private var xPrefCacheKeyValueStrings = HashMap<String, String>()

    /** [XSharedPreferences] 缓存的 [Boolean] 键值数据 */
    private var xPrefCacheKeyValueBooleans = HashMap<String, Boolean>()

    /** [XSharedPreferences] 缓存的 [Int] 键值数据 */
    private var xPrefCacheKeyValueInts = HashMap<String, Int>()

    /** [XSharedPreferences] 缓存的 [Long] 键值数据 */
    private var xPrefCacheKeyValueLongs = HashMap<String, Long>()

    /** [XSharedPreferences] 缓存的 [Float] 键值数据 */
    private var xPrefCacheKeyValueFloats = HashMap<String, Float>()

    /** 是否使用键值缓存 */
    private var isUsingKeyValueCache = YukiHookAPI.Configs.isEnableModulePrefsCache

    /** 检查是否处于自定义 Hook API 状态 */
    private fun checkApiInBaseContext() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookModulePrefs not allowed in Custom Hook API")
    }

    /**
     * 获得 [XSharedPreferences] 对象
     * @return [XSharedPreferences]
     */
    private val xPref
        get() = XSharedPreferences(YukiHookAPI.modulePackageName, prefsName).apply {
            checkApiInBaseContext()
            makeWorldReadable()
            reload()
        }

    /**
     * 获得 [SharedPreferences] 对象
     * @return [SharedPreferences]
     */
    private val sPref
        get() = try {
            checkApiInBaseContext()
            context?.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
                ?: error("If you want to use module prefs,you must set the context instance first")
        } catch (_: Throwable) {
            checkApiInBaseContext()
            context?.getSharedPreferences(prefsName, Context.MODE_PRIVATE)
                ?: error("If you want to use module prefs,you must set the context instance first")
        }

    /** 设置全局可读可写 */
    private fun makeWorldReadable() = runCatching {
        File(File(context!!.applicationInfo.dataDir, "shared_prefs"), "$prefsName.xml").apply {
            setReadable(true, false)
            setExecutable(true, false)
        }
    }

    /**
     * 自定义 Sp 存储名称
     * @param name 自定义的 Sp 存储名称
     * @return [YukiHookModulePrefs]
     */
    fun name(name: String): YukiHookModulePrefs {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnableModulePrefsCache
        prefsName = name
        return this
    }

    /**
     * 忽略缓存直接读取键值
     *
     * 无论是否开启 [YukiHookAPI.Configs.isEnableModulePrefsCache]
     *
     * - 仅在 [XSharedPreferences] 下生效
     * @return [YukiHookModulePrefs]
     */
    fun direct(): YukiHookModulePrefs {
        isUsingKeyValueCache = false
        return this
    }

    /**
     * 获取 [String] 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据 - ""
     * @return [String]
     */
    fun getString(key: String, value: String = "") =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueStrings[key].let {
                    (it ?: xPref.getString(key, value) ?: value).let { value ->
                        xPrefCacheKeyValueStrings[key] = value
                        value
                    }
                }
            else resetCacheSet { xPref.getString(key, value) ?: value }
        else sPref.getString(key, value) ?: value).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Boolean] 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据 - false
     * @return [Boolean]
     */
    fun getBoolean(key: String, value: Boolean = false) =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueBooleans[key].let {
                    it ?: xPref.getBoolean(key, value).let { value ->
                        xPrefCacheKeyValueBooleans[key] = value
                        value
                    }
                }
            else resetCacheSet { xPref.getBoolean(key, value) }
        else sPref.getBoolean(key, value)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Int] 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据 - 0
     * @return [Int]
     */
    fun getInt(key: String, value: Int = 0) =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueInts[key].let {
                    it ?: xPref.getInt(key, value).let { value ->
                        xPrefCacheKeyValueInts[key] = value
                        value
                    }
                }
            else resetCacheSet { xPref.getInt(key, value) }
        else sPref.getInt(key, value)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Float] 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据 - 0f
     * @return [Float]
     */
    fun getFloat(key: String, value: Float = 0f) =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueFloats[key].let {
                    it ?: xPref.getFloat(key, value).let { value ->
                        xPrefCacheKeyValueFloats[key] = value
                        value
                    }
                }
            else resetCacheSet { xPref.getFloat(key, value) }
        else sPref.getFloat(key, value)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Long] 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据 - 0L
     * @return [Long]
     */
    fun getLong(key: String, value: Long = 0L) =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueLongs[key].let {
                    it ?: xPref.getLong(key, value).let { value ->
                        xPrefCacheKeyValueLongs[key] = value
                        value
                    }
                }
            else resetCacheSet { xPref.getLong(key, value) }
        else sPref.getLong(key, value)).let {
            makeWorldReadable()
            it
        }

    /**
     * 移除全部包含 [key] 的存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     */
    fun remove(key: String) {
        if (isXposedEnvironment) return
        sPref.edit().remove(key).apply()
        makeWorldReadable()
    }

    /**
     * 移除 [PrefsData.key] 的存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param prefs 键值实例
     */
    inline fun <reified T> remove(prefs: PrefsData<T>) = remove(prefs.key)

    /**
     * 存储 [String] 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putString(key: String, value: String) {
        if (isXposedEnvironment) return
        sPref.edit().putString(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 存储 [Boolean] 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putBoolean(key: String, value: Boolean) {
        if (isXposedEnvironment) return
        sPref.edit().putBoolean(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 存储 [Int] 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putInt(key: String, value: Int) {
        if (isXposedEnvironment) return
        sPref.edit().putInt(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 存储 [Float] 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putFloat(key: String, value: Float) {
        if (isXposedEnvironment) return
        sPref.edit().putFloat(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 存储 [Long] 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putLong(key: String, value: Long) {
        if (isXposedEnvironment) return
        sPref.edit().putLong(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 智能获取指定类型的键值
     * @param prefs 键值实例
     * @param value 默认值 - 未指定默认为 [prefs] 中的 [PrefsData.value]
     * @return [T] 只能是 [String]、[Int]、[Float]、[Long]、[Boolean]
     */
    inline fun <reified T> get(prefs: PrefsData<T>, value: T = prefs.value): T = when (prefs.value) {
        is String -> getString(prefs.key, value as String) as T
        is Int -> getInt(prefs.key, value as Int) as T
        is Float -> getFloat(prefs.key, value as Float) as T
        is Long -> getLong(prefs.key, value as Long) as T
        is Boolean -> getBoolean(prefs.key, value as Boolean) as T
        else -> error("Key-Value type ${T::class.java.name} is not allowed")
    }

    /**
     * 智能存储指定类型的键值
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param prefs 键值实例
     * @param value 要存储的值 - 只能是 [String]、[Int]、[Float]、[Long]、[Boolean]
     */
    inline fun <reified T> put(prefs: PrefsData<T>, value: T) = when (prefs.value) {
        is String -> putString(prefs.key, value as String)
        is Int -> putInt(prefs.key, value as Int)
        is Float -> putFloat(prefs.key, value as Float)
        is Long -> putLong(prefs.key, value as Long)
        is Boolean -> putBoolean(prefs.key, value as Boolean)
        else -> error("Key-Value type ${T::class.java.name} is not allowed")
    }

    /**
     * 清除 [XSharedPreferences] 中缓存的键值数据
     *
     * 无论是否开启 [YukiHookAPI.Configs.isEnableModulePrefsCache]
     *
     * 调用此方法将清除当前存储的全部键值缓存
     *
     * 下次将从 [XSharedPreferences] 重新读取
     */
    fun clearCache() {
        xPrefCacheKeyValueStrings.clear()
        xPrefCacheKeyValueBooleans.clear()
        xPrefCacheKeyValueInts.clear()
        xPrefCacheKeyValueLongs.clear()
        xPrefCacheKeyValueFloats.clear()
    }

    /**
     * 恢复 [isUsingKeyValueCache] 为默认状态
     * @param result 回调方法体的结果
     * @return [T]
     */
    private fun <T> resetCacheSet(result: () -> T): T {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnableModulePrefsCache
        return result()
    }
}