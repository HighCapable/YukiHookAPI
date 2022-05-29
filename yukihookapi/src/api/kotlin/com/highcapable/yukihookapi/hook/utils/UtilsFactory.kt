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
 * This file is Created by fankes on 2022/2/5.
 */
package com.highcapable.yukihookapi.hook.utils

import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 进行一次并行计算的 ForEach 操作
 * @param action 回调内容方法体
 */
@YukiPrivateApi
inline fun <T> Iterable<T>.parallelForEach(crossinline action: (T) -> Unit) {
    Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()).apply {
        val iterator = iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            execute { runCatching { action(item) } }
        }
        shutdown()
        awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)
    }
}

/**
 * 获取数组内容依次列出的字符串表示
 * @return [String]
 */
@YukiPrivateApi
inline fun <reified T> Array<out T>.value() = if (isNotEmpty()) {
    var value = ""
    forEach { value += it.toString() + ", " }
    "[${value.trim().let { it.substring(0, it.lastIndex) }}]"
} else "[]"

/**
 * 计算方法执行耗时
 * @param block 方法块
 * @return [RunBlockResult]
 */
@YukiPrivateApi
inline fun <R> runBlocking(block: () -> R): RunBlockResult {
    val start = System.currentTimeMillis()
    block()
    return RunBlockResult(afterMs = System.currentTimeMillis() - start)
}

/**
 * 构造耗时计算结果类
 * @param afterMs 耗时
 */
@YukiPrivateApi
class RunBlockResult(@PublishedApi internal val afterMs: Long) {

    /**
     * 获取耗时计算结果
     * @param result 回调结果 - ([Long] 耗时)
     */
    @YukiPrivateApi
    inline fun result(result: (Long) -> Unit) = result(afterMs)
}