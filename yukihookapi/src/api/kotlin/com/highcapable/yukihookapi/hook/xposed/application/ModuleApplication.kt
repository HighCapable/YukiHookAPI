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
 * This file is Created by fankes on 2022/4/15.
 */
package com.highcapable.yukihookapi.hook.xposed.application

import android.app.Application
import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.application.ModuleApplication.Companion.appContext
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import com.highcapable.yukihookapi.thirdparty.me.weishu.reflection.Reflection

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
 * 详情请参考 [API 文档 - ModuleApplication](https://fankes.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication)
 *
 * For English version, see [API Document - ModuleApplication](https://fankes.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication)
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