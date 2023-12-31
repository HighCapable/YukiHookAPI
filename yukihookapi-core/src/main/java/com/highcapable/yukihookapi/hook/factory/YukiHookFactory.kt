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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "UnusedReceiverParameter", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.factory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Process
import android.view.ContextThemeWrapper
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.annotation.StyleRes
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper.ModuleContextThemeWrapper
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookPrefsBridge
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * 在 [IYukiHookXposedInit] 中调用 [YukiHookAPI.configs]
 * @param initiate 配置方法体
 */
inline fun IYukiHookXposedInit.configs(initiate: YukiHookAPI.Configs.() -> Unit) = YukiHookAPI.configs(initiate)

/**
 * 在 [IYukiHookXposedInit] 中调用 [YukiHookAPI.encase]
 * @param initiate Hook 方法体
 */
fun IYukiHookXposedInit.encase(initiate: PackageParam.() -> Unit) = YukiHookAPI.encase(initiate)

/**
 * 在 [IYukiHookXposedInit] 中装载 [YukiHookAPI]
 * @param hooker Hook 子类数组 - 必填不能为空
 * @throws IllegalStateException 如果 [hooker] 是空的
 */
fun IYukiHookXposedInit.encase(vararg hooker: YukiBaseHooker) = YukiHookAPI.encase(hooker = hooker)

/**
 * 获取模块的存取对象
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [Context.prefs] 方法
 * @return [YukiHookPrefsBridge]
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith("prefs()"))
val Context.modulePrefs get() = prefs()

/**
 * 获取模块的存取对象
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [Context.prefs] 方法
 * @return [YukiHookPrefsBridge]
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith("prefs(name)"))
fun Context.modulePrefs(name: String) = prefs(name)

/**
 * 创建 [YukiHookPrefsBridge] 对象
 *
 * 可以同时在模块与 (Xposed) 宿主环境中使用
 *
 * 如果你想在 (Xposed) 宿主环境将数据存入当前宿主的私有空间 - 请使用 [YukiHookPrefsBridge.native] 方法
 *
 * 在未声明任何条件的情况下 (Xposed) 宿主环境默认读取模块中的数据
 * @param name 自定义 Sp 存储名称 - 默认空
 * @return [YukiHookPrefsBridge]
 */
fun Context.prefs(name: String = "") = YukiHookPrefsBridge.from(context = this).let { if (name.isNotBlank()) it.name(name) else it }

/**
 * 获取 [YukiHookDataChannel] 对象
 *
 * - 只能在模块环境使用此功能 - 其它环境下使用将不起作用
 * @param packageName 目标 Hook APP (宿主) 包名
 * @return [YukiHookDataChannel.NameSpace]
 */
fun Context.dataChannel(packageName: String) = YukiHookDataChannel.instance().nameSpace(context = this, packageName)

/**
 * 获取当前进程名称
 * @return [String]
 */
val Context.processName
    get() = runCatching {
        BufferedReader(FileReader(File("/proc/${Process.myPid()}/cmdline"))).let { buff ->
            buff.readLine().trim { it <= ' ' }.let {
                buff.close()
                it
            }
        }
    }.getOrNull() ?: packageName ?: ""

/**
 * 向 Hook APP (宿主) [Context] 注入当前 Xposed 模块的资源
 *
 * 注入成功后 - 你就可以直接使用例如 [ImageView.setImageResource] or [Resources.getString] 装载当前 Xposed 模块的资源 ID
 *
 * 注入的资源作用域仅限当前 [Context] - 你需要在每个用到宿主 [Context] 的地方重复调用此方法进行注入才能使用
 *
 * 详情请参考 [注入模块资源 (Resources)](https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject#%E6%B3%A8%E5%85%A5%E6%A8%A1%E5%9D%97%E8%B5%84%E6%BA%90-resources)
 *
 * For English version, see [Inject Module App's Resources](https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject#inject-module-app-s-resources)
 *
 * - 只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 */
fun Context.injectModuleAppResources() = resources?.injectModuleAppResources()

/**
 * 向 Hook APP (宿主) 指定 [Resources] 直接注入当前 Xposed 模块的资源
 *
 * 注入成功后 - 你就可以直接使用例如 [ImageView.setImageResource] or [Resources.getString] 装载当前 Xposed 模块的资源 ID
 *
 * 注入的资源作用域仅限当前 [Resources] - 你需要在每个用到宿主 [Resources] 的地方重复调用此方法进行注入才能使用
 *
 * 详情请参考 [注入模块资源 (Resources)](https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject#%E6%B3%A8%E5%85%A5%E6%A8%A1%E5%9D%97%E8%B5%84%E6%BA%90-resources)
 *
 * For English version, see [Inject Module App's Resources](https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject#inject-module-app-s-resources)
 *
 * - 只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 */
fun Resources.injectModuleAppResources() = AppParasitics.injectModuleAppResources(hostResources = this)

/**
 * 向 Hook APP (宿主) 注册当前 Xposed 模块的 [Activity]
 *
 * 注册成功后 - 你就可以直接使用 [Context.startActivity] 来启动未在宿主中注册的 [Activity]
 *
 * 使用此方法会在未注册的 [Activity] 在 Hook APP (宿主) 中启动时自动调用 [injectModuleAppResources] 注入当前 Xposed 模块的资源
 *
 * - 你要将需要在宿主启动的 [Activity] 继承于 [ModuleAppActivity] or [ModuleAppCompatActivity]
 *
 * 详情请参考 [注册模块 Activity](https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject#%E6%B3%A8%E5%86%8C%E6%A8%A1%E5%9D%97-activity)
 *
 * For English version, see [Register Module App's Activity](https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject#register-module-app-s-activity)
 *
 * - 只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 *
 * - 最低支持 Android 7.0 (API 24)
 * @param proxy 代理的 [Activity] - 必须存在于宿主的 AndroidMainifest 清单中 - 不填使用默认 [Activity]
 */
@RequiresApi(Build.VERSION_CODES.N)
fun Context.registerModuleAppActivities(proxy: Any? = null) = AppParasitics.registerModuleAppActivities(context = this, proxy)

/**
 * 生成一个 [ContextThemeWrapper] 代理以应用当前 Xposed 模块的主题资源
 *
 * 在 Hook APP (宿主) 中使用此方法会自动调用 [injectModuleAppResources] 注入当前 Xposed 模块的资源
 *
 * - 如果在 Hook APP (宿主) 中使用此方法发生 [ClassCastException] - 请手动设置新的 [configuration]
 *
 * 详情请参考 [创建 ContextThemeWrapper 代理](https://highcapable.github.io/YukiHookAPI/zh-cn/api/special-features/host-inject#%E5%88%9B%E5%BB%BA-contextthemewrapper-%E4%BB%A3%E7%90%86)
 *
 * For English version, see [Create ContextThemeWrapper Proxy](https://highcapable.github.io/YukiHookAPI/en/api/special-features/host-inject#create-contextthemewrapper-proxy)
 * @param theme 主题资源 ID
 * @param configuration 使用的 [Configuration] - 默认空
 * @return [ModuleContextThemeWrapper]
 */
fun Context.applyModuleTheme(@StyleRes theme: Int, configuration: Configuration? = null) =
    ModuleContextThemeWrapper.wrapper(baseContext = this, theme, configuration)

/**
 * 仅判断模块是否在太极、无极中激活
 *
 * 此处的实现代码来自太极官方文档中示例代码的封装与改进
 *
 * 详情请参考太极开发指南中的 [如何判断模块是否激活了？](https://taichi.cool/zh/doc/for-xposed-dev.html#%E5%A6%82%E4%BD%95%E5%88%A4%E6%96%AD%E6%A8%A1%E5%9D%97%E6%98%AF%E5%90%A6%E6%BF%80%E6%B4%BB%E4%BA%86%EF%BC%9F)
 * @return [Boolean] 是否激活
 */
internal val Context.isTaiChiModuleActive: Boolean
    get() {
        /**
         * 获取模块是否激活
         * @return [Boolean] or null
         */
        fun isModuleActive() =
            contentResolver?.call(Uri.parse("content://me.weishu.exposed.CP/"), "active", null, null)?.getBoolean("active", false)
        return runCatching { isModuleActive() }.getOrNull() ?: runCatching {
            startActivity(Intent("me.weishu.exp.ACTION_ACTIVE").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
            isModuleActive()
        }.getOrNull() ?: false
    }