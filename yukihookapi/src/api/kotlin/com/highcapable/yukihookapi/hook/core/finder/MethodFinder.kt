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
 * This file is Created by fankes on 2022/2/4.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.core.finder

import android.os.SystemClock
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerW
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import com.highcapable.yukihookapi.hook.utils.runBlocking
import java.lang.reflect.Method

/**
 * [Method] 查找类
 *
 * 可通过指定类型查找指定方法
 * @param hookInstance 当前 Hook 实例
 * @param hookClass 当前 Hook 的 Class
 */
class MethodFinder(private val hookInstance: YukiHookCreater.MemberHookCreater, private val hookClass: Class<*>? = null) {

    /** [Method] 参数数组 */
    private var params: Array<out Class<*>>? = null

    /** 是否使用了 [RemedyPlan] */
    private var isUsingRemedyPlan = false

    /**
     * [Method] 名称
     *
     * - ❗必须设置
     */
    var name = ""

    /**
     * [Method] 返回值
     *
     * 可不填写返回值 - 默认模糊查找并取第一个匹配的 [Method]
     */
    var returnType: Class<*>? = null

    /**
     * [Method] 参数
     *
     * - ❗无参 [Method] 不要使用此方法
     *
     * - ❗有参 [Method] 必须使用此方法设定参数
     * @param paramType 参数类型数组
     */
    fun param(vararg paramType: Class<*>) {
        if (paramType.isEmpty()) error("paramType is empty, please delete param() method")
        params = paramType
    }

    /**
     * 得到方法
     * @return [Method]
     * @throws IllegalStateException 如果 [name] 未设置
     * @throws NoSuchMethodError 如果找不到方法
     */
    private val result
        get() = if (params != null)
            ReflectionUtils.findMethodBestMatch(hookClass, returnType, name, *params!!)
        else ReflectionUtils.findMethodNoParam(hookClass, returnType, name)

    /**
     * 得到方法结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @return [Result]
     */
    @DoNotUseMethod
    fun build() = if (name.isBlank()) {
        loggerE(msg = "Method name cannot be empty in Class [$hookClass] [${hookInstance.tag}]")
        Result(isNoSuch = true)
    } else try {
        runBlocking {
            hookInstance.member = result
        }.result {
            hookInstance.onHookLogMsg(msg = "Find Method [${hookInstance.member}] takes ${it}ms [${hookInstance.tag}]")
        }
        Result()
    } catch (e: Throwable) {
        onFailureMsg(throwable = e)
        Result(isNoSuch = true, e)
    }

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Result]
     */
    @DoNotUseMethod
    fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    /**
     * 发生错误时输出日志
     * @param msg 消息日志
     * @param throwable 错误
     * @param isAlwaysPrint 忽略条件每次都打印错误
     */
    private fun onFailureMsg(msg: String = "", throwable: Throwable? = null, isAlwaysPrint: Boolean = false) {
        fun print() = loggerE(msg = "NoSuchMethod happend in [$hookClass] $msg [${hookInstance.tag}]", e = throwable)
        if (isAlwaysPrint) print()
        else Thread {
            SystemClock.sleep(10)
            if (hookInstance.isNotIgnoredHookingFailure && !isUsingRemedyPlan) print()
        }.start()
    }

    /**
     * [Method] 重查找实现类
     *
     * 可累计失败次数直到查找成功
     */
    inner class RemedyPlan {

        /** 失败尝试次数数组 */
        private val remedyPlans = HashSet<Pair<MethodFinder, Result>>()

        /**
         * 创建需要重新查找的 [Method]
         *
         * 你可以添加多个备选方法 - 直到成功为止
         *
         * 若最后依然失败 - 将停止查找并输出错误日志
         * @param initiate 方法体
         * @return [Result] 结果
         */
        fun method(initiate: MethodFinder.() -> Unit) =
            Result().apply { remedyPlans.add(Pair(MethodFinder(hookInstance, hookClass).apply(initiate), this)) }

        /**
         * 开始重查找
         *
         * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
         */
        @DoNotUseMethod
        internal fun build() {
            if (remedyPlans.isNotEmpty()) run {
                var isFindSuccess = false
                var lastError: Throwable? = null
                remedyPlans.forEachIndexed { p, it ->
                    runCatching {
                        runBlocking {
                            hookInstance.member = it.first.result
                        }.result {
                            hookInstance.onHookLogMsg(msg = "Find Method [${hookInstance.member}] takes ${it}ms [${hookInstance.tag}]")
                        }
                        isFindSuccess = true
                        it.second.onFindCallback?.invoke(hookInstance.member as Method)
                        hookInstance.onHookLogMsg(msg = "Method [${hookInstance.member}] trying ${p + 1} times success by RemedyPlan [${hookInstance.tag}]")
                        return@run
                    }.onFailure {
                        lastError = it
                        onFailureMsg(msg = "trying ${p + 1} times by RemedyPlan --> $it", isAlwaysPrint = true)
                    }
                }
                if (!isFindSuccess) {
                    onFailureMsg(
                        msg = "trying ${remedyPlans.size} times and all failure by RemedyPlan",
                        throwable = lastError,
                        isAlwaysPrint = true
                    )
                    remedyPlans.clear()
                }
            } else loggerW(msg = "RemedyPlan is empty,forgot it? [${hookInstance.tag}]")
        }

        /**
         * [RemedyPlan] 结果实现类
         *
         * 可在这里处理是否成功的回调
         */
        inner class Result {

            /** 找到结果时的回调 */
            internal var onFindCallback: (Method.() -> Unit)? = null

            /**
             * 当找到结果时
             * @param initiate 回调
             */
            fun onFind(initiate: Method.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Method] 查找结果实现类
     * @param isNoSuch 是否没有找到方法 - 默认否
     * @param e 错误信息
     */
    inner class Result(private val isNoSuch: Boolean = false, private val e: Throwable? = null) {

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 创建方法重查找功能
         *
         * 当你遇到一种方法可能存在不同形式的存在时
         *
         * 可以使用 [RemedyPlan] 重新查找它 - 而没有必要使用 [onNoSuchMethod] 捕获异常二次查找方法
         *
         * 若第一次查找失败了 - 你还可以在这里继续添加此方法体直到成功为止
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        fun remedys(initiate: RemedyPlan.() -> Unit): Result {
            isUsingRemedyPlan = true
            if (isNoSuch) RemedyPlan().apply(initiate).build()
            return this
        }

        /**
         * 监听找不到方法时
         *
         * 只会返回第一次的错误信息 - 不会返回 [RemedyPlan] 的错误信息
         * @param initiate 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoSuchMethod(initiate: (Throwable) -> Unit): Result {
            if (isNoSuch) initiate(e ?: Throwable())
            return this
        }
    }
}