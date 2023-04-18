/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "StaticFieldLeak", "SetWorldReadable", "CommitPrefEdits", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.delegate.XSharedPreferencesDelegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * [YukiHookAPI] 对 [SharedPreferences]、[XSharedPreferences] 的扩展存储桥实现
 *
 * 在不同环境智能选择存取使用的对象
 *
 * - ❗模块与宿主之前共享数据存储为实验性功能 - 仅在 LSPosed 环境测试通过 - EdXposed 理论也可以使用但不再推荐
 *
 * 对于在模块环境中使用 [PreferenceFragmentCompat] - [YukiHookAPI] 提供了 [ModulePreferenceFragment] 来实现同样的功能
 *
 * 详情请参考 [API 文档 - YukiHookPrefsBridge](https://fankes.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge)
 *
 * For English version, see [API Document - YukiHookPrefsBridge](https://fankes.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/prefs/YukiHookPrefsBridge)
 * @param context 上下文实例 - 默认空
 */
class YukiHookPrefsBridge private constructor(private var context: Context? = null) {

    internal companion object {

        /** 当前是否为 (Xposed) 宿主环境 */
        private val isXposedEnvironment = YukiXposedModule.isXposedEnvironment

        /** 当前缓存的 [XSharedPreferencesDelegate] 实例数组 */
        private val xPrefs = HashMap<String, XSharedPreferencesDelegate>()

        /** 当前缓存的 [SharedPreferences] 实例数组 */
        private val sPrefs = HashMap<String, SharedPreferences>()

        /**
         * 创建 [YukiHookPrefsBridge] 对象
         * @param context 实例 - (Xposed) 宿主环境为空
         * @return [YukiHookPrefsBridge]
         */
        internal fun from(context: Context? = null) = YukiHookPrefsBridge(context)

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

    /** 存储名称 */
    private var prefsName = ""

    /**
     * 获取当前存储名称 - 默认包名 + _preferences
     * @return [String]
     */
    private val currentPrefsName
        get() = prefsName.ifBlank {
            if (isUsingNativeStorage) "${context?.packageName ?: "unknown"}_preferences"
            else "${YukiXposedModule.modulePackageName.ifBlank { context?.packageName ?: "unknown" }}_preferences"
        }

    /** 是否使用键值缓存 */
    private var isUsingKeyValueCache = YukiHookAPI.Configs.isEnablePrefsBridgeCache

    /** 是否使用新版存储方式 EdXposed、LSPosed */
    private var isUsingNewXSharedPreferences = false

    /** 是否启用原生存储方式 */
    private var isUsingNativeStorage = false

    /**
     * [XSharedPreferences] 缓存的键值数据
     */
    private object XSharedPreferencesCaches {

        /** 缓存的 [String] 键值数据 */
        var stringData = HashMap<String, String>()

        /** 缓存的 [Set]<[String]> 键值数据 */
        var stringSetData = HashMap<String, Set<String>>()

        /** 缓存的 [Boolean] 键值数据 */
        var booleanData = HashMap<String, Boolean>()

        /** 缓存的 [Int] 键值数据 */
        var intData = HashMap<String, Int>()

        /** 缓存的 [Long] 键值数据 */
        var longData = HashMap<String, Long>()

        /** 缓存的 [Float] 键值数据 */
        var floatData = HashMap<String, Float>()

        /** 清除所有缓存的键值数据 */
        fun clear() {
            stringData.clear()
            stringSetData.clear()
            booleanData.clear()
            intData.clear()
            longData.clear()
            floatData.clear()
        }
    }

    /** 检查 API 装载状态 */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookPrefsBridge not allowed in Custom Hook API")
        if (isXposedEnvironment && YukiXposedModule.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
    }

    /**
     * 获取当前 [XSharedPreferences] 对象
     * @return [XSharedPreferences]
     */
    private val currentXsp
        get() = checkApi().let {
            runCatching {
                (xPrefs[currentPrefsName]?.instance ?: XSharedPreferencesDelegate.from(YukiXposedModule.modulePackageName, currentPrefsName)
                    .also {
                        xPrefs[currentPrefsName] = it
                    }.instance).apply {
                    makeWorldReadable()
                    reload()
                }
            }.onFailure { yLoggerE(msg = it.message ?: "Operating system not supported", e = it) }.getOrNull()
                ?: error("Cannot load the XSharedPreferences, maybe is your Hook Framework not support it")
        }

    /**
     * 获取当前 [SharedPreferences] 对象
     * @return [SharedPreferences]
     */
    private val currentSp
        get() = checkApi().let {
            runCatching {
                @Suppress("DEPRECATION", "WorldReadableFiles")
                sPrefs[context.toString() + currentPrefsName] ?: context?.getSharedPreferences(currentPrefsName, Context.MODE_WORLD_READABLE)
                    ?.also {
                        isUsingNewXSharedPreferences = true
                        sPrefs[context.toString() + currentPrefsName] = it
                    } ?: error("YukiHookPrefsBridge missing Context instance")
            }.getOrElse {
                sPrefs[context.toString() + currentPrefsName] ?: context?.getSharedPreferences(currentPrefsName, Context.MODE_PRIVATE)?.also {
                    isUsingNewXSharedPreferences = false
                    sPrefs[context.toString() + currentPrefsName] = it
                } ?: error("YukiHookPrefsBridge missing Context instance")
            }
        }

    /** 设置全局可读可写 */
    private fun makeWorldReadable() = runCatching {
        if (isUsingNewXSharedPreferences.not()) makeWorldReadable(context, prefsFileName = "${currentPrefsName}.xml")
    }

    /**
     * 获取 [XSharedPreferences] 是否可读
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [isPreferencesAvailable]
     * @return [Boolean]
     */
    @Deprecated(message = "请使用新方式来实现此功能", ReplaceWith("isPreferencesAvailable"))
    val isXSharePrefsReadable get() = isPreferencesAvailable

    /**
     * 获取 [YukiHookPrefsBridge] 是否正处于 EdXposed/LSPosed 的最高权限运行
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [isPreferencesAvailable]
     * @return [Boolean]
     */
    @Deprecated(message = "请使用新方式来实现此功能", ReplaceWith("isPreferencesAvailable"))
    val isRunInNewXShareMode get() = isPreferencesAvailable

    /**
     * 获取当前 [YukiHookPrefsBridge] 的可用状态
     *
     * - 在 (Xposed) 宿主环境中返回 [XSharedPreferences] 可用状态 (可读)
     *
     * - 在模块环境中返回当前是否处于 New XSharedPreferences 模式 (可读可写)
     * @return [Boolean]
     */
    val isPreferencesAvailable
        get() = if (isXposedEnvironment)
            (runCatching { currentXsp.let { it.file.exists() && it.file.canRead() } }.getOrNull() ?: false)
        else runCatching {
            /** 执行一次装载 */
            currentSp.edit()
            isUsingNewXSharedPreferences
        }.getOrNull() ?: false

    /**
     * 自定义 Sp 存储名称
     * @param name 自定义的 Sp 存储名称
     * @return [YukiHookPrefsBridge]
     */
    fun name(name: String): YukiHookPrefsBridge {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnablePrefsBridgeCache
        prefsName = name
        return this
    }

    /**
     * 忽略缓存直接读取键值
     *
     * 无论是否开启 [YukiHookAPI.Configs.isEnableModulePrefsCache]
     *
     * - 仅在 [XSharedPreferences] 下生效
     * @return [YukiHookPrefsBridge]
     */
    fun direct(): YukiHookPrefsBridge {
        isUsingKeyValueCache = false
        return this
    }

    /**
     * 忽略当前环境直接使用 [Context.getSharedPreferences] 存取数据
     * @return [YukiHookPrefsBridge]
     * @throws IllegalStateException 如果 [context] 为空
     */
    fun native(): YukiHookPrefsBridge {
        if (isXposedEnvironment && context == null) context = AppParasitics.currentApplication
            ?: error("The Host App's Context has not yet initialized successfully, the native function cannot be used at this time")
        isUsingNativeStorage = true
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.stringData[key].let {
                    (it ?: currentXsp.getString(key, value) ?: value).let { value ->
                        XSharedPreferencesCaches.stringData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getString(key, value) ?: value }
        else currentSp.getString(key, value) ?: value).let {
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.stringSetData[key].let {
                    (it ?: currentXsp.getStringSet(key, value) ?: value).let { value ->
                        XSharedPreferencesCaches.stringSetData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getStringSet(key, value) ?: value }
        else currentSp.getStringSet(key, value) ?: value).let {
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.booleanData[key].let {
                    it ?: currentXsp.getBoolean(key, value).let { value ->
                        XSharedPreferencesCaches.booleanData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getBoolean(key, value) }
        else currentSp.getBoolean(key, value)).let {
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.intData[key].let {
                    it ?: currentXsp.getInt(key, value).let { value ->
                        XSharedPreferencesCaches.intData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getInt(key, value) }
        else currentSp.getInt(key, value)).let {
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.floatData[key].let {
                    it ?: currentXsp.getFloat(key, value).let { value ->
                        XSharedPreferencesCaches.floatData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getFloat(key, value) }
        else currentSp.getFloat(key, value)).let {
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
        (if (isXposedEnvironment && isUsingNativeStorage.not())
            if (isUsingKeyValueCache)
                XSharedPreferencesCaches.longData[key].let {
                    it ?: currentXsp.getLong(key, value).let { value ->
                        XSharedPreferencesCaches.longData[key] = value
                        value
                    }
                }
            else resetCacheSet { currentXsp.getLong(key, value) }
        else currentSp.getLong(key, value)).let {
            makeWorldReadable()
            it
        }

    /**
     * 智能获取指定类型的键值
     * @param prefs 键值实例
     * @param value 默认值 - 未指定默认为 [prefs] 中的 [PrefsData.value]
     * @return [T] 只能是 [String]、[Set]<[String]>、[Int]、[Float]、[Long]、[Boolean]
     */
    inline fun <reified T> get(prefs: PrefsData<T>, value: T = prefs.value): T = getPrefsData(prefs.key, value) as T

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
     * 判断当前是否包含 [key] 键值的数据
     *
     * - 智能识别对应环境读取键值数据
     * @return [Boolean] 是否包含
     */
    fun contains(key: String) =
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.contains(key)
        else currentSp.contains(key)

    /**
     * 获取全部存储的键值数据
     *
     * - 智能识别对应环境读取键值数据
     *
     * - ❗每次调用都会获取实时的数据 - 不受缓存控制 - 请勿在高并发场景中使用
     * @return [HashMap] 全部类型的键值数组
     */
    fun all() = hashMapOf<String, Any?>().apply {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.all.forEach { (k, v) -> this[k] = v }
        else currentSp.all.forEach { (k, v) -> this[k] = v }
    }

    /**
     * 移除全部包含 [key] 的存储数据
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { remove(key) }"))
    fun remove(key: String) = edit { remove(key) }

    /**
     * 移除 [PrefsData.key] 的存储数据
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param prefs 键值实例
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { remove(prefs) }"))
    inline fun <reified T> remove(prefs: PrefsData<T>) = edit { remove(prefs) }

    /**
     * 移除全部存储数据
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { clear() }"))
    fun clear() = edit { clear() }

    /**
     * 存储 [String] 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putString(key, value) }"))
    fun putString(key: String, value: String) = edit { putString(key, value) }

    /**
     * 存储 [Set]<[String]> 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putStringSet(key, value) }"))
    fun putStringSet(key: String, value: Set<String>) = edit { putStringSet(key, value) }

    /**
     * 存储 [Boolean] 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putBoolean(key, value) }"))
    fun putBoolean(key: String, value: Boolean) = edit { putBoolean(key, value) }

    /**
     * 存储 [Int] 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putInt(key, value) }"))
    fun putInt(key: String, value: Int) = edit { putInt(key, value) }

    /**
     * 存储 [Float] 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putFloat(key, value) }"))
    fun putFloat(key: String, value: Float) = edit { putFloat(key, value) }

    /**
     * 存储 [Long] 键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     * @param key 键值名称
     * @param value 键值数据
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { putLong(key, value) }"))
    fun putLong(key: String, value: Long) = edit { putLong(key, value) }

    /**
     * 智能存储指定类型的键值
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [edit] 方法
     */
    @Deprecated(message = "此方法因为性能问题已被作废，请转移到新用法", ReplaceWith("edit { put(prefs, value) }"))
    inline fun <reified T> put(prefs: PrefsData<T>, value: T) = edit { put(prefs, value) }

    /**
     * 创建新的 [Editor]
     *
     * - 在模块环境中使用
     *
     * - ❗在 (Xposed) 宿主环境下只读 - 无法使用
     * @return [Editor]
     */
    fun edit() = Editor()

    /**
     * 创建新的 [Editor]
     *
     * 自动调用 [Editor.apply] 方法
     *
     * - 在模块环境中使用
     *
     * - ❗在 (Xposed) 宿主环境下只读 - 无法使用
     * @param initiate 方法体
     */
    fun edit(initiate: Editor.() -> Unit = {}) = edit().apply(initiate).apply()

    /**
     * 清除 [XSharedPreferences] 中缓存的键值数据
     *
     * 无论是否开启 [YukiHookAPI.Configs.isEnableModulePrefsCache]
     *
     * 调用此方法将清除当前存储的全部键值缓存
     *
     * 下次将从 [XSharedPreferences] 重新读取
     *
     * - 在 (Xposed) 宿主环境中使用
     */
    fun clearCache() = XSharedPreferencesCaches.clear()

    /**
     * 恢复 [isUsingKeyValueCache] 为默认状态
     * @param result 回调方法体的结果
     * @return [T]
     */
    private inline fun <T> resetCacheSet(result: () -> T): T {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnablePrefsBridgeCache
        return result()
    }

    /**
     * [YukiHookPrefsBridge] 的存储代理类
     *
     * - ❗请使用 [edit] 方法来获取 [Editor]
     *
     * - 在模块环境中使用
     *
     * - ❗在 (Xposed) 宿主环境下只读 - 无法使用
     */
    inner class Editor internal constructor() {

        /** 创建新的存储代理类 */
        private var editor = runCatching { currentSp.edit() }.getOrNull()

        /**
         * 移除全部包含 [key] 的存储数据
         * @param key 键值名称
         * @return [Editor]
         */
        fun remove(key: String) = moduleEnvironment {
            editor?.remove(key)
            makeWorldReadable()
        }

        /**
         * 移除 [PrefsData.key] 的存储数据
         * @param prefs 键值实例
         * @return [Editor]
         */
        inline fun <reified T> remove(prefs: PrefsData<T>) = remove(prefs.key)

        /**
         * 移除全部存储数据
         * @return [Editor]
         */
        fun clear() = moduleEnvironment {
            editor?.clear()
            makeWorldReadable()
        }

        /**
         * 存储 [String] 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putString(key: String, value: String) = moduleEnvironment {
            editor?.putString(key, value)
            makeWorldReadable()
        }

        /**
         * 存储 [Set]<[String]> 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putStringSet(key: String, value: Set<String>) = moduleEnvironment {
            editor?.putStringSet(key, value)
            makeWorldReadable()
        }

        /**
         * 存储 [Boolean] 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putBoolean(key: String, value: Boolean) = moduleEnvironment {
            editor?.putBoolean(key, value)
            makeWorldReadable()
        }

        /**
         * 存储 [Int] 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putInt(key: String, value: Int) = moduleEnvironment {
            editor?.putInt(key, value)
            makeWorldReadable()
        }

        /**
         * 存储 [Float] 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putFloat(key: String, value: Float) = moduleEnvironment {
            editor?.putFloat(key, value)
            makeWorldReadable()
        }

        /**
         * 存储 [Long] 键值
         *
         * - 建议使用 [PrefsData] 创建模板并使用 [put] 存储数据
         * @param key 键值名称
         * @param value 键值数据
         * @return [Editor]
         */
        fun putLong(key: String, value: Long) = moduleEnvironment {
            editor?.putLong(key, value)
            makeWorldReadable()
        }

        /**
         * 智能存储指定类型的键值
         * @param prefs 键值实例
         * @param value 要存储的值 - 只能是 [String]、[Set]<[String]>、[Int]、[Float]、[Long]、[Boolean]
         * @return [Editor]
         */
        inline fun <reified T> put(prefs: PrefsData<T>, value: T) = putPrefsData(prefs.key, value)

        /**
         * 智能存储指定类型的键值
         *
         * 封装方法以调用内联方法
         * @param key 键值
         * @param value 要存储的值 - 只能是 [String]、[Set]<[String]>、[Int]、[Float]、[Long]、[Boolean]
         * @return [Editor]
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
         * 提交更改 (同步)
         * @return [Boolean] 是否成功
         */
        fun commit() = editor?.commit()?.also { makeWorldReadable() } ?: false

        /** 提交更改 (异步) */
        fun apply() = editor?.apply().also { makeWorldReadable() } ?: Unit

        /**
         * 仅在模块环境执行
         *
         * 非模块环境使用会打印警告信息
         * @param callback 在模块环境执行
         * @return [Editor]
         */
        private inline fun moduleEnvironment(callback: () -> Unit): Editor {
            if (isXposedEnvironment.not() || isUsingNativeStorage) callback()
            else yLoggerW(msg = "YukiHookPrefsBridge.Editor not allowed in Xposed Environment")
            return this
        }
    }
}