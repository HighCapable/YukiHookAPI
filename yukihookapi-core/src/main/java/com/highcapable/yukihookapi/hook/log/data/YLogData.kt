/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
 * This file is created by fankes on 2023/9/27.
 */
package com.highcapable.yukihookapi.hook.log.data

import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 调试日志数据实现类
 * @param timestamp 当前时间戳
 * @param time 当前 UTC 时间
 * @param tag 当前标签
 * @param priority 当前优先级 - D、I、W、E
 * @param packageName 当前包名
 * @param userId 当前用户 ID
 * @param msg 当前日志内容
 * @param throwable 当前异常堆栈
 */
data class YLogData internal constructor(
    var timestamp: Long = 0L,
    var time: String = "",
    var tag: String = YLog.Configs.tag,
    var priority: String = "",
    var packageName: String = "",
    var userId: Int = 0,
    var msg: String = "",
    var throwable: Throwable? = null
) : Serializable {

    /** 是否隐式打印 */
    internal var isImplicit = false

    init {
        timestamp = System.currentTimeMillis()
        time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT).format(Date(timestamp))
        packageName = if (YukiXposedModule.isXposedEnvironment) YukiXposedModule.hostProcessName else AppParasitics.currentPackageName
        userId = AppParasitics.findUserId(AppParasitics.currentPackageName)
    }

    /**
     * 获取头部时间字符串
     * @return [String]
     */
    internal val head get() = "$time ------ "

    override fun toString(): String {
        var content = ""
        YLog.Configs.elements.takeIf { it.isNotEmpty() }?.forEach {
            if (it == YLog.Configs.TAG) content += "[$tag]"
            if (it == YLog.Configs.PRIORITY) content += "[$priority]"
            if (it == YLog.Configs.PACKAGE_NAME && isImplicit.not() && packageName.isNotBlank()) content += "[$packageName]"
            if (it == YLog.Configs.USER_ID && isImplicit.not() && userId != 0) content += "[$userId]"
        }; return content.takeIf { it.isNotBlank() }?.let { "$content $msg" } ?: msg
    }
}