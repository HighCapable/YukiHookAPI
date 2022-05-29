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
    "SetWorldReadable", "CommitPrefEdits", "DEPRECATION", "WorldReadableFiles",
    "unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "StaticFieldLeak"
)

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * 实现 Xposed 模块的数据存取
 *
 * 对接 [SharedPreferences] 和 [XSharedPreferences]
 *
 * 在不同环境智能选择存取使用的对象
 *
 * - ❗请注意此功能为实验性功能 - 仅在 LSPosed 环境测试通过 - EdXposed 理论也可以使用但不再推荐
 *
 * - 使用 LSPosed 环境请在 AndroidManifests.xml 中将 "xposedminversion" 最低设置为 93
 *
 * - 未使用 LSPosed 环境请将你的模块 API 降至 26 以下 - [YukiHookAPI] 将会尝试使用 [makeWorldReadable] 但仍有可能不成功
 *
 * - ❗当你在模块中存取数据的时候 [context] 必须不能是空的
 *
 * - 若你正在使用 [PreferenceFragmentCompat] - 请迁移到 [ModulePreferenceFragment] 以适配上述功能特性
 *
 * - 详情请参考 [API 文档 - YukiHookModulePrefs](https://fankes.github.io/YukiHookAPI/#/api/document?id=yukihookmoduleprefs-class)
 * @param context 上下文实例 - 默认空
 */
class YukiHookModulePrefs private constructor(private var context: Context? = null) {

    internal companion object {

        /** 是否为 Xposed 环境 */
        private val isXposedEnvironment = YukiHookBridge.hasXposedBridge

        /** 当前 [YukiHookModulePrefs] 单例 */
        private var instance: YukiHookModulePrefs? = null

        /**
         * 获取 [YukiHookModulePrefs] 单例
         * @param context 实例 - Xposed 环境为空
         * @return [YukiHookModulePrefs]
         */
        internal fun instance(context: Context? = null) =
            instance?.apply { if (context != null) this.context = context } ?: YukiHookModulePrefs(context).apply { instance = this }

        /**
         * 设置全局可读可写
         * @param context 实例
         * @param prefsFileName Sp 文件名
         */
        internal fun makeWorldReadable(context: Context?, prefsFileName: String) {
            runCatching {
                context?.also {
                    File(File(it.applicationInfo.dataDir, "shared_prefs"), prefsFileName).apply {
                        setReadable(true, false)
                        setExecutable(true, false)
                    }
                }
            }
        }
    }

    /** 存储名称 - 默认包名 + _preferences */
    private var prefsName = "${YukiHookBridge.modulePackageName.ifBlank { context?.packageName ?: "" }}_preferences"

    /** [XSharedPreferences] 缓存的 [String] 键值数据 */
    private var xPrefCacheKeyValueStrings = HashMap<String, String>()

    /** [XSharedPreferences] 缓存的 [Set]<[String]> 键值数据 */
    private var xPrefCacheKeyValueStringSets = HashMap<String, Set<String>>()

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

    /** 是否使用新版存储方式 EdXposed/LSPosed */
    private var isUsingNewXSharePrefs = false

    /** 检查 API 装载状态 */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookModulePrefs not allowed in Custom Hook API")
        if (YukiHookBridge.hasXposedBridge && YukiHookBridge.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
    }

    /**
     * 获得 [XSharedPreferences] 对象
     * @return [XSharedPreferences]
     */
    private val xPref
        get() = XSharedPreferences(YukiHookBridge.modulePackageName, prefsName).apply {
            checkApi()
            makeWorldReadable()
            reload()
        }

    /**
     * 获得 [SharedPreferences] 对象
     * @return [SharedPreferences]
     */
    private val sPref
        get() = try {
            checkApi()
            context?.getSharedPreferences(prefsName, Context.MODE_WORLD_READABLE).also { isUsingNewXSharePrefs = true }
                ?: error("If you want to use module prefs, you must set the context instance first")
        } catch (_: Throwable) {
            checkApi()
            context?.getSharedPreferences(prefsName, Context.MODE_PRIVATE).also { isUsingNewXSharePrefs = false }
                ?: error("If you want to use module prefs, you must set the context instance first")
        }

    /** 设置全局可读可写 */
    private fun makeWorldReadable() = runCatching {
        if (isUsingNewXSharePrefs.not()) makeWorldReadable(context, prefsFileName = "${prefsName}.xml")
    }

    /**
     * 获取 [XSharedPreferences] 是否可读
     *
     * - ❗只能在 [isXposedEnvironment] 中使用 - 模块环境中始终返回 false
     * @return [Boolean] 是否可读
     */
    val isXSharePrefsReadable get() = runCatching { xPref.let { it.file.exists() && it.file.canRead() } }.getOrNull() ?: false

    /**
     * 获取 [YukiHookModulePrefs] 是否正处于 EdXposed/LSPosed 的最高权限运行
     *
     * - 前提条件为当前 Xposed 模块已被激活
     *
     * - ❗只能在模块环境中使用 - [isXposedEnvironment] 环境中始终返回 false
     * @return [Boolean] 仅限在模块中判断 - 在宿主 [XSharedPreferences] 环境中始终返回 false
     */
    val isRunInNewXShareMode
        get() = runCatching {
            /** 执行一次装载 */
            sPref.edit()
            isUsingNewXSharePrefs
        }.getOrNull() ?: false

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
     * 获取 [Set]<[String]> 键值
     *
     * - 智能识别对应环境读取键值数据
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [get] 获取数据
     * @param key 键值名称
     * @param value 默认数据
     * @return [Set]<[String]>
     */
    fun getStringSet(key: String, value: Set<String>) =
        (if (isXposedEnvironment)
            if (isUsingKeyValueCache)
                xPrefCacheKeyValueStrings[key].let {
                    (it ?: xPref.getStringSet(key, value) ?: value).let { value ->
                        xPrefCacheKeyValueStringSets[key] = value as Set<String>
                        value
                    }
                }
            else resetCacheSet { xPref.getStringSet(key, value) ?: value }
        else sPref.getStringSet(key, value) ?: value).let {
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
     *  获取全部存储的键值数据
     *
     * - 智能识别对应环境读取键值数据
     *
     * - ❗每次调用都会获取实时的数据 - 不受缓存控制 - 请勿在高并发场景中使用
     * @return [HashMap] 全部类型的键值数组
     */
    fun all() = HashMap<String, Any?>().apply {
        if (isXposedEnvironment)
            xPref.all.forEach { (k, v) -> this[k] = v }
        else sPref.all.forEach { (k, v) -> this[k] = v }
    }

    /**
     * 移除全部包含 [key] 的存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     */
    fun remove(key: String) = moduleEnvironment {
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
     * 移除全部存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     */
    fun clear() = moduleEnvironment {
        sPref.edit().clear().apply()
        makeWorldReadable()
    }

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
    fun putString(key: String, value: String) = moduleEnvironment {
        sPref.edit().putString(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 存储 [Set]<[String]> 键值
     *
     * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值名称
     * @param value 键值数据
     */
    fun putStringSet(key: String, value: Set<String>) = moduleEnvironment {
        sPref.edit().putStringSet(key, value).apply()
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
    fun putBoolean(key: String, value: Boolean) = moduleEnvironment {
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
    fun putInt(key: String, value: Int) = moduleEnvironment {
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
    fun putFloat(key: String, value: Float) = moduleEnvironment {
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
    fun putLong(key: String, value: Long) = moduleEnvironment {
        sPref.edit().putLong(key, value).apply()
        makeWorldReadable()
    }

    /**
     * 智能获取指定类型的键值
     * @param prefs 键值实例
     * @param value 默认值 - 未指定默认为 [prefs] 中的 [PrefsData.value]
     * @return [T] 只能是 [String]、[Int]、[Float]、[Long]、[Boolean]
     */
    inline fun <reified T> get(prefs: PrefsData<T>, value: T = prefs.value): T = getPrefsData(prefs.key, value) as T

    /**
     * 智能存储指定类型的键值
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param prefs 键值实例
     * @param value 要存储的值 - 只能是 [String]、[Int]、[Float]、[Long]、[Boolean]
     */
    inline fun <reified T> put(prefs: PrefsData<T>, value: T) = putPrefsData(prefs.key, value)

    /**
     * 智能获取指定类型的键值
     *
     * 封装方法以调用内联方法
     * @param key 键值
     * @param value 默认值
     * @return [Any]
     */
    @PublishedApi
    internal fun getPrefsData(key: String, value: Any?): Any = when (value) {
        is String -> getString(key, value)
        is Set<*> -> getStringSet(key, value as? Set<String> ?: error("Key-Value type ${value.javaClass.name} is not allowed"))
        is Int -> getInt(key, value)
        is Float -> getFloat(key, value)
        is Long -> getLong(key, value)
        is Boolean -> getBoolean(key, value)
        else -> error("Key-Value type ${value?.javaClass?.name} is not allowed")
    }

    /**
     * 智能存储指定类型的键值
     *
     * 封装方法以调用内联方法
     *
     * - 在模块 [Context] 环境中使用
     *
     * - ❗在 [XSharedPreferences] 环境下只读 - 无法使用
     * @param key 键值
     * @param value 要存储的值 - 只能是 [String]、[Int]、[Float]、[Long]、[Boolean]
     */
    @PublishedApi
    internal fun putPrefsData(key: String, value: Any?) = when (value) {
        is String -> putString(key, value)
        is Set<*> -> putStringSet(key, value as? Set<String> ?: error("Key-Value type ${value.javaClass.name} is not allowed"))
        is Int -> putInt(key, value)
        is Float -> putFloat(key, value)
        is Long -> putLong(key, value)
        is Boolean -> putBoolean(key, value)
        else -> error("Key-Value type ${value?.javaClass?.name} is not allowed")
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
        xPrefCacheKeyValueStringSets.clear()
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
    private inline fun <T> resetCacheSet(result: () -> T): T {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnableModulePrefsCache
        return result()
    }

    /**
     * 仅在模块环境执行
     *
     * 非模块环境使用会打印警告信息
     * @param callback 在模块环境执行
     */
    private inline fun moduleEnvironment(callback: () -> Unit) {
        if (isXposedEnvironment.not()) callback()
        else yLoggerW(msg = "You cannot use write prefs function in Xposed Environment")
    }
}