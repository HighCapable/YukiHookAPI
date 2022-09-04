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

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

/**
 * 创建当前线程池服务
 * @return [ExecutorService]
 */
private val currentThreadPool get() = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())

/**
 * 对 [T] 返回无返回值的 [Unit]
 * @return [Unit]
 */
internal fun <T> T?.unit() = let {}

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
 * 进行一次并行计算的 ForEach 操作
 * @param action 回调内容方法体
 */
internal inline fun <T> Iterable<T>.parallelForEach(crossinline action: (T) -> Unit) {
    currentThreadPool.apply {
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
internal inline fun <reified T> Array<out T>.value() = if (isNotEmpty()) {
    var value = ""
    forEach { value += it.toString() + ", " }
    "[${value.trim().let { it.substring(0, it.lastIndex) }}]"
} else "[]"

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

/**
 * 获取 [ModifyValue] 对象
 * @return [ModifyValue]
 */
internal fun <T> T.value() = ModifyValue(value = this)

/**
 * 可修改变量实现类
 * @param value 变量自身实例
 */
internal data class ModifyValue<T>(var value: T)