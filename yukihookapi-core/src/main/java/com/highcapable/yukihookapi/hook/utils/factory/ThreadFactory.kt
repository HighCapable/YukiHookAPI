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