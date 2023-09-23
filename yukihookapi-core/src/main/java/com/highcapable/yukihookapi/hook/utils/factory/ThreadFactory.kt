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
 * This file is created by fankes on 2023/9/23.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.utils.factory

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * 创建当前线程池服务
 * @return [ExecutorService]
 */
private val currentThreadPool get() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

/**
 * 启动 [Thread] 延迟等待 [block] 的结果 [T]
 * @param delayMs 延迟毫秒 - 默认 1 ms
 * @param block 方法块
 * @return [T]
 */
internal inline fun <T> T.await(delayMs: Long = 1, crossinline block: (T) -> Unit): T {
    currentThreadPool.apply {
        execute {
            if (delayMs > 0) Thread.sleep(delayMs)
            block(this@await)
            shutdown()
        }
    }
    return this
}

/**
 * 计算方法执行耗时
 * @param block 方法块
 * @return [RunBlockResult]
 */
internal inline fun <R> runBlocking(block: () -> R): RunBlockResult {
    val start = System.currentTimeMillis()
    block()
    return RunBlockResult(afterMs = System.currentTimeMillis() - start)
}

/**
 * 构造耗时计算结果类
 * @param afterMs 耗时
 */
internal class RunBlockResult(internal val afterMs: Long) {

    /**
     * 获取耗时计算结果
     * @param result 回调结果 - ([Long] 耗时)
     */
    internal inline fun result(result: (Long) -> Unit) = result(afterMs)
}