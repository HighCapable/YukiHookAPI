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
 * This file is Created by fankes on 2022/2/18.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import android.os.SystemClock
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.log.loggerI
import java.lang.reflect.Member

/**
 * 这是查找类功能的基本类实现
 * @param tag 当前查找类的标识
 * @param hookInstance 当前 Hook 实例
 * @param classSet 当前需要查找的 [Class] 实例
 */
abstract class BaseFinder(
    private val tag: String,
    open val hookInstance: YukiHookCreater.MemberHookCreater? = null,
    open val classSet: Class<*>? = null
) {

    /** 是否开启忽略错误警告功能 */
    internal var isShutErrorPrinting = false

    /** 是否使用了重查找功能 */
    internal var isUsingRemedyPlan = false

    /** 当前找到的 [Member] */
    internal var memberInstance: Member? = null

    /**
     * 获取当前使用的 TAG
     * @return [String] 使用的 TAG
     */
    internal val hookTag get() = hookInstance?.tag ?: "FinderMode"

    /**
     * 判断是否没有设置 Hook 过程中的任何异常拦截
     * @return [Boolean] 没有设置任何异常拦截
     */
    internal val isNotIgnoredHookingFailure get() = hookInstance?.isNotIgnoredHookingFailure ?: true

    /**
     * 发生错误时输出日志
     * @param msg 消息日志
     * @param throwable 错误
     * @param isAlwaysPrint 忽略条件每次都打印错误
     */
    internal fun onFailureMsg(msg: String = "", throwable: Throwable? = null, isAlwaysPrint: Boolean = false) {
        fun print() = loggerE(msg = "NoSuch$tag happend in [$classSet] $msg [${hookTag}]", e = throwable)
        if (isAlwaysPrint) print()
        else Thread {
            SystemClock.sleep(10)
            if (isNotIgnoredHookingFailure && !isUsingRemedyPlan && !isShutErrorPrinting) print()
        }.start()
    }

    /**
     * Hook 过程中开启了 [YukiHookAPI.Configs.isDebug] 输出调试信息
     * @param msg 调试日志内容
     */
    internal fun onHookLogMsg(msg: String) {
        if (YukiHookAPI.Configs.isDebug) loggerI(msg = msg)
    }

    /**
     * 得到结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param isBind 是否将结果设置到目标 [YukiHookCreater.MemberHookCreater]
     * @return [Any]
     */
    @DoNotUseMethod
    abstract fun build(isBind: Boolean = false): Any

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Any]
     */
    @DoNotUseMethod
    abstract fun failure(throwable: Throwable?): Any
}