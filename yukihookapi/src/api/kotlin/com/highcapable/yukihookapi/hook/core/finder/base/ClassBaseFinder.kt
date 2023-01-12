/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
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
 * This file is Created by fankes on 2022/9/4.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI

/**
 * 这是 [Class] 查找类功能的基本类实现
 * @param loaderSet 当前使用的 [ClassLoader] 实例
 */
abstract class ClassBaseFinder internal constructor(internal open val loaderSet: ClassLoader? = null) : BaseFinder() {

    internal companion object {

        /** [loaderSet] 为 null 的提示 */
        internal const val LOADERSET_IS_NULL = "loaderSet is null"
    }

    /** 当前找到的 [Class] 数组 */
    internal var classInstances = HashSet<Class<*>>()

    /** 是否开启忽略错误警告功能 */
    internal var isShutErrorPrinting = false

    /**
     * 将目标类型转换为可识别的兼容类型
     * @param any 当前需要转换的实例
     * @param tag 当前查找类的标识
     * @return [Class] or null
     */
    internal fun compatType(any: Any?, tag: String) = any?.compat(tag, loaderSet)

    /**
     * 在开启 [YukiHookAPI.Configs.isDebug] 且在 [HookApiCategoryHelper.hasAvailableHookApi] 情况下输出调试信息
     * @param msg 调试日志内容
     */
    internal fun onDebuggingMsg(msg: String) {
        if (YukiHookAPI.Configs.isDebug && HookApiCategoryHelper.hasAvailableHookApi) yLoggerI(msg = msg)
    }

    /**
     * 发生错误时输出日志
     * @param throwable 错误
     */
    internal fun onFailureMsg(throwable: Throwable? = null) {
        if (isShutErrorPrinting) return
        /** 判断是否为 [LOADERSET_IS_NULL] */
        if (throwable?.message == LOADERSET_IS_NULL) return
        yLoggerE(msg = "NoClassDefFound happend in [$loaderSet]", e = throwable)
    }

    @YukiPrivateApi
    override fun failure(throwable: Throwable?) = error("DexClassFinder does not contain this usage")
}