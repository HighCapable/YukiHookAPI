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