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
 * This file is Created by fankes on 2023/4/20.
 */
package com.highcapable.yukihookapi.hook.xposed.prefs.cache

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.utils.memory.factory.createLruCache
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge

/**
 * [YukiHookPrefsBridge] 缓存管理类
 */
internal class PreferencesCacheManager private constructor() {

    internal companion object {

        /** 当前 [PreferencesCacheManager] 单例 */
        private var instance: PreferencesCacheManager? = null

        /**
         * 获取 [PreferencesCacheManager] 单例
         * @return [PreferencesCacheManager]
         */
        internal fun instance() = instance ?: PreferencesCacheManager().apply { instance = this }
    }

    /** 是否使用键值缓存功能 */
    private var isUsingKeyValueCache = YukiHookAPI.Configs.isEnablePrefsBridgeCache

    private val stringData = createLruCache<String, String>()
    private val stringSetData = createLruCache<String, Set<String>>()
    private val booleanData = createLruCache<String, Boolean>()
    private val intData = createLruCache<String, Int>()
    private val longData = createLruCache<String, Long>()
    private val floatData = createLruCache<String, Float>()

    /**
     * 获取缓存的 [String] 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getString(key: String, value: () -> String) =
        if (isUsingKeyValueCache) stringData.get(key) ?: resetCacheSet { value().also { stringData.put(key, it) } } else value()

    /**
     * 获取缓存的 [Set]<[String]> 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getStringSet(key: String, value: () -> Set<String>) =
        if (isUsingKeyValueCache) stringSetData.get(key) ?: resetCacheSet { value().also { stringSetData.put(key, it) } } else value()

    /**
     * 获取缓存的 [Boolean] 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getBoolean(key: String, value: () -> Boolean) =
        if (isUsingKeyValueCache) booleanData.get(key) ?: resetCacheSet { value().also { booleanData.put(key, it) } } else value()

    /**
     * 获取缓存的 [Int] 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getInt(key: String, value: () -> Int) =
        if (isUsingKeyValueCache) intData.get(key) ?: resetCacheSet { value().also { intData.put(key, it) } } else value()

    /**
     * 获取缓存的 [Long] 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getLong(key: String, value: () -> Long) =
        if (isUsingKeyValueCache) longData.get(key) ?: resetCacheSet { value().also { longData.put(key, it) } } else value()

    /**
     * 获取缓存的 [Float] 键值
     * @param key 键值名称
     * @param value 无缓存获取回调
     */
    internal fun getFloat(key: String, value: () -> Float) =
        if (isUsingKeyValueCache) floatData.get(key) ?: resetCacheSet { value().also { floatData.put(key, it) } } else value()

    /**
     * 创建新的 [Editor]
     * @return [Editor]
     */
    internal fun edit() = Editor()

    /** 启用缓存功能 - 跟随 [YukiHookAPI.Configs.isEnablePrefsBridgeCache] 控制 */
    internal fun enableByConfig() {
        isUsingKeyValueCache = YukiHookAPI.Configs.isEnablePrefsBridgeCache
    }

    /** 禁用缓存功能 */
    internal fun disable() {
        isUsingKeyValueCache = false
    }

    /**
     * [PreferencesCacheManager] 的存储代理类
     *
     * - ❗请使用 [edit] 方法来获取 [Editor]
     */
    internal inner class Editor internal constructor() {

        /** 预提交任务数组 */
        private val preSubmitTasks = HashSet<() -> Unit>()

        /**
         * 更新缓存的 [String] 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateString(key: String, value: String): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { stringData.put(key, value) }
            return this
        }

        /**
         * 更新缓存的 [Set]<[String]> 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateStringSet(key: String, value: Set<String>): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { stringSetData.put(key, value) }
            return this
        }

        /**
         * 更新缓存的 [Boolean] 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateBoolean(key: String, value: Boolean): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { booleanData.put(key, value) }
            return this
        }

        /**
         * 更新缓存的 [Int] 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateInt(key: String, value: Int): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { intData.put(key, value) }
            return this
        }

        /**
         * 更新缓存的 [Long] 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateLong(key: String, value: Long): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { longData.put(key, value) }
            return this
        }

        /**
         * 更新缓存的 [Float] 键值
         * @param key 键值名称
         * @param value 键值内容
         * @return [Editor]
         */
        internal fun updateFloat(key: String, value: Float): Editor {
            if (isUsingKeyValueCache) preSubmitTasks.add { floatData.put(key, value) }
            return this
        }

        /**
         * 移除缓存的键值
         * @param key 键值名称
         * @return [Editor]
         */
        internal fun remove(key: String): Editor {
            if (isUsingKeyValueCache) runCatching {
                if (stringSetData.get(key) != null) preSubmitTasks.add { stringData.remove(key) }
                if (stringSetData.get(key) != null) preSubmitTasks.add { stringSetData.remove(key) }
                if (booleanData.get(key) != null) preSubmitTasks.add { booleanData.remove(key) }
                if (intData.get(key) != null) preSubmitTasks.add { intData.remove(key) }
                if (longData.get(key) != null) preSubmitTasks.add { longData.remove(key) }
                if (floatData.get(key) != null) preSubmitTasks.add { floatData.remove(key) }
            }; return this
        }

        /**
         * 清除所有缓存的键值数据
         * @return [Editor]
         */
        internal fun clear(): Editor {
            preSubmitTasks.add {
                stringData.evictAll()
                stringSetData.evictAll()
                booleanData.evictAll()
                intData.evictAll()
                longData.evictAll()
                floatData.evictAll()
            }; return this
        }

        /** 提交更改 */
        internal fun apply() {
            preSubmitTasks.takeIf { it.isNotEmpty() }?.onEach { it() }?.clear()
        }
    }

    /**
     * 恢复 [isUsingKeyValueCache] 为默认状态
     * @param result 回调方法体的结果
     * @return [T]
     */
    private inline fun <T> resetCacheSet(result: () -> T): T {
        enableByConfig()
        return result()
    }
}