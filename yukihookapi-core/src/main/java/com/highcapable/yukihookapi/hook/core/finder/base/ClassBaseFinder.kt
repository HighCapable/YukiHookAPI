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
 * This file is created by fankes on 2022/9/4.
 */
package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.YLog

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
    internal var classInstances = mutableListOf<Class<*>>()

    /** 是否开启忽略错误警告功能 */
    internal var isIgnoreErrorLogs = false

    /**
     * 将目标类型转换为可识别的兼容类型
     * @param any 当前需要转换的实例
     * @param tag 当前查找类的标识
     * @return [Class] or null
     */
    internal fun compatType(any: Any?, tag: String) = any?.compat(tag, loaderSet)

    /**
     * 在开启 [YukiHookAPI.Configs.isDebug] 且在 [HookApiCategoryHelper.hasAvailableHookApi] 情况下输出调试信息
     * @param msg 消息内容
     */
    internal fun debugMsg(msg: String) {
        if (HookApiCategoryHelper.hasAvailableHookApi) YLog.innerD(msg)
    }

    /**
     * 发生错误时输出日志
     * @param e 异常堆栈 - 默认空
     */
    internal fun errorMsg(e: Throwable? = null) {
        if (isIgnoreErrorLogs) return
        /** 判断是否为 [LOADERSET_IS_NULL] */
        if (e?.message == LOADERSET_IS_NULL) return
        YLog.innerE("NoClassDefFound happend in [$loaderSet]", e)
    }

    override fun failure(throwable: Throwable?) = error("DexClassFinder does not contain this usage")
}