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
 * This file is created by fankes on 2023/1/9.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.caller

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Xposed 模块核心功能调用类
 */
internal object YukiXposedModuleCaller {

    /**
     * 模块是否装载了 Xposed 回调方法
     * @return [Boolean]
     */
    internal val isXposedCallbackSetUp get() = YukiXposedModule.isXposedCallbackSetUp

    /**
     * 标识 Xposed 模块开始装载
     * @param packageName 当前 Xposed 模块包名
     * @param appFilePath 当前 Xposed 模块自身 APK 路径
     */
    internal fun callOnStartLoadModule(packageName: String, appFilePath: String) = YukiXposedModule.onStartLoadModule(packageName, appFilePath)

    /**
     * 标识 Xposed 模块装载完成
     */
    internal fun callOnFinishLoadModule() = YukiXposedModule.onFinishLoadModule()

    /**
     * 标识可用的 Hook APP (宿主) 开始装载
     * @param type 当前正在进行的 Hook 类型
     * @param packageName 宿主包名
     * @param processName 宿主进程名
     * @param appClassLoader 宿主 [ClassLoader]
     * @param appInfo 宿主 [ApplicationInfo]
     * @param appResources 宿主 [YukiResources]
     */
    internal fun callOnPackageLoaded(
        type: HookEntryType,
        packageName: String?,
        processName: String? = "",
        appClassLoader: ClassLoader? = null,
        appInfo: ApplicationInfo? = null,
        appResources: YukiResources? = null
    ) = YukiXposedModule.onPackageLoaded(type, packageName, processName, appClassLoader, appInfo, appResources)

    /**
     * 打印 Error 级别 Log
     * @param msg 日志打印的内容
     * @param e 异常堆栈信息 - 默认空
     */
    internal fun callLogError(msg: String, e: Throwable? = null) = YLog.innerE(msg, e)
}