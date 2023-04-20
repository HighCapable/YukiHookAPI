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
package com.highcapable.yukihookapi.hook.utils.memory.factory

import android.util.LruCache
import com.highcapable.yukihookapi.hook.utils.memory.LruCacheMemory

/**
 * 创建 [LruCache]<[K], [V]>
 * @param sizeOf 回调自定义缓存大小计算数值 - 默认不设置
 * @return [LruCache]<[K], [V]>
 */
internal fun <K, V> createLruCache(sizeOf: (() -> Int)? = null) = object : LruCache<K, V>(LruCacheMemory.maxCacheSize) {
    override fun sizeOf(key: K?, value: V?) = when {
        sizeOf != null -> sizeOf()
        value is Array<*> -> {
            var allLengths = 0
            if (value.isNotEmpty()) value.forEach { allLengths += it.toString().length }
            allLengths / 1024
        }
        value is Map<*, *> -> {
            var allLengths = 0
            if (value.isNotEmpty()) value.forEach { allLengths += it.toString().length }
            allLengths / 1024
        }
        value is List<*> -> {
            var allLengths = 0
            if (value.isNotEmpty()) value.forEach { allLengths += it.toString().length }
            allLengths / 1024
        }
        value is Set<*> -> {
            var allLengths = 0
            if (value.isNotEmpty()) value.forEach { allLengths += it.toString().length }
            allLengths / 1024
        }
        value is String -> value.length / 1024
        else -> value.toString().length / 1024
    }
}