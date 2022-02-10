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
@file:Suppress(
    "EXPERIMENTAL_API_USAGE", "SetWorldReadable", "CommitPrefEdits",
    "DEPRECATION", "WorldReadableFiles", "unused"
)

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import com.highcapable.yukihookapi.YukiHookAPI
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
 * - 详见 [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)
 *
 * - 未使用 LSPosed 环境请将你的模块 API 降至 26 以下 - YukiHookAPI 将会尝试使用 [makeWorldReadable] 但仍有可能不成功
 *
 * - ❗当你在模块中存取数据的时候 [context] 必须不能是空的
 * @param context 上下文实例 - 默认空
 */
class YukiHookModulePrefs(private val context: Context? = null) {

    /** 存储名称 - 默认包名 + _preferences */
    private var prefsName = "${YukiHookAPI.modulePackageName.ifBlank { context?.packageName ?: "" }}_preferences"

    /** 是否为 Xposed 环境 */
    private val isXposedEnvironment = YukiHookAPI.hasXposedBridge

    /** 缓存数据 */
    private var xPrefCacheKeyValueStrings = HashMap<String, String>()

    /** 缓存数据 */
    private var xPrefCacheKeyValueBooleans = HashMap<String, Boolean>()

    /** 缓存数据 */
    private var xPrefCacheKeyValueInts = HashMap<String, Int>()

    /** 缓存数据 */
    private var xPrefCacheKeyValueLongs = HashMap<String, Long>()

    /** 缓存数据 */
    private var xPrefCacheKeyValueFloats = HashMap<String, Float>()

    /**
     * 获得 [XSharedPreferences] 对象
     * @return [XSharedPreferences]
     */
    private val xPref
        get() = XSharedPreferences(YukiHookAPI.modulePackageName, prefsName).apply {
            makeWorldReadable()
            reload()
        }

    /**
     * 获得 [SharedPreferences] 对象
     * @return [SharedPreferences]
     */
    private val sPref
        get() = try {
            context?.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE)
                ?: error("If you want to use module prefs,you must set the context instance first")
        } catch (_: Throwable) {
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
        prefsName = name
        return this
    }

    /**
     * 获取 [String] 键值
     *
     * - 智能识别对应环境读取键值数据
     * @param key 键值名称
     * @param default 默认数据 - ""
     * @return [String]
     */
    fun getString(key: String, default: String = "") =
        (if (isXposedEnvironment)
            xPrefCacheKeyValueStrings[key].let {
                (it ?: xPref.getString(key, default) ?: default).let { value ->
                    xPrefCacheKeyValueStrings[key] = value
                    value
                }
            }
        else sPref.getString(key, default) ?: default).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Boolean] 键值
     *
     * - 智能识别对应环境读取键值数据
     * @param key 键值名称
     * @param default 默认数据 - false
     * @return [Boolean]
     */
    fun getBoolean(key: String, default: Boolean = false) =
        (if (isXposedEnvironment)
            xPrefCacheKeyValueBooleans[key].let {
                it ?: xPref.getBoolean(key, default).let { value ->
                    xPrefCacheKeyValueBooleans[key] = value
                    value
                }
            }
        else sPref.getBoolean(key, default)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Int] 键值
     *
     * - 智能识别对应环境读取键值数据
     * @param key 键值名称
     * @param default 默认数据 - 0
     * @return [Int]
     */
    fun getInt(key: String, default: Int = 0) =
        (if (isXposedEnvironment)
            xPrefCacheKeyValueInts[key].let {
                it ?: xPref.getInt(key, default).let { value ->
                    xPrefCacheKeyValueInts[key] = value
                    value
                }
            }
        else sPref.getInt(key, default)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Float] 键值
     *
     * - 智能识别对应环境读取键值数据
     * @param key 键值名称
     * @param default 默认数据 - 0f
     * @return [Float]
     */
    fun getFloat(key: String, default: Float = 0f) =
        (if (isXposedEnvironment)
            xPrefCacheKeyValueFloats[key].let {
                it ?: xPref.getFloat(key, default).let { value ->
                    xPrefCacheKeyValueFloats[key] = value
                    value
                }
            }
        else sPref.getFloat(key, default)).let {
            makeWorldReadable()
            it
        }

    /**
     * 获取 [Long] 键值
     *
     * - 智能识别对应环境读取键值数据
     * @param key 键值名称
     * @param default 默认数据 - 0L
     * @return [Long]
     */
    fun getLong(key: String, default: Long = 0L) =
        (if (isXposedEnvironment)
            xPrefCacheKeyValueLongs[key].let {
                it ?: xPref.getLong(key, default).let { value ->
                    xPrefCacheKeyValueLongs[key] = value
                    value
                }
            }
        else sPref.getLong(key, default)).let {
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
     * 存储 [String] 键值
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
}