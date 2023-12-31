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
 * This file is created by fankes on 2022/4/15.
 */
package com.highcapable.yukihookapi.hook.xposed.application

import android.app.Application
import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication.Companion.appContext
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import me.weishu.reflection.Reflection

/**
 * 这是对使用 [YukiHookAPI] Xposed 模块实现中的一个扩展功能
 *
 * 在你的 Xposed 模块的 [Application] 中继承此类
 *
 * 或在 AndroidManifest.xml 的 application 标签中指定此类
 *
 * 目前可实现功能如下
 *
 * - 全局共享模块中静态的 [appContext]
 *
 * - 在模块与宿主中装载 [YukiHookAPI.Configs] 以确保 [YukiHookAPI.Configs.debugTag] 不需要重复定义
 *
 * - 在模块与宿主中使用 [YukiHookDataChannel] 进行通讯
 *
 * - 在模块中使用系统隐藏 API - 核心技术引用了开源项目 [FreeReflection](https://github.com/tiann/FreeReflection)
 *
 * - 在模块中使用 [YukiHookAPI.Status.isTaiChiModuleActive] 判断太极、无极激活状态
 *
 * 详情请参考 [API 文档 - ModuleApplication](https://highcapable.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication)
 *
 * For English version, see [API Document - ModuleApplication](https://highcapable.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication)
 */
open class ModuleApplication : Application() {

    companion object {

        /** 全局静态 [Application] 实例 */
        internal var currentContext: ModuleApplication? = null

        /**
         * 获取全局静态 [Application] 实例
         * @return [ModuleApplication]
         * @throws IllegalStateException 如果 [Application] 没有正确装载完成
         */
        val appContext get() = currentContext ?: error("App is dead, You cannot call to appContext")
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        runCatching { Reflection.unseal(base) }
    }

    override fun onCreate() {
        super.onCreate()
        currentContext = this
        /** 调用 Hook 入口类的 [IYukiHookXposedInit.onInit] 方法 */
        runCatching { ModuleApplication_Impl.callHookEntryInit() }
        YukiHookDataChannel.instance().register(context = this)
    }
}