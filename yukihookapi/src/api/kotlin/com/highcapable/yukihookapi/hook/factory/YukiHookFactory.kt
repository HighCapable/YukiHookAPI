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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.factory

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.os.Process
import android.view.ContextThemeWrapper
import android.widget.ImageView
import androidx.annotation.StyleRes
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.context.wrapper.ModuleContextThemeWrapper
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs
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
 * @return [YukiHookModulePrefs]
 */
val Context.modulePrefs get() = YukiHookModulePrefs.instance(context = this)

/**
 * 获取模块的存取对象
 * @param name 自定义 Sp 存储名称
 * @return [YukiHookModulePrefs]
 */
fun Context.modulePrefs(name: String) = modulePrefs.name(name)

/**
 * 获取模块的数据通讯桥命名空间对象
 *
 * - ❗只能在模块环境使用此功能 - 其它环境下使用将不起作用
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
 * 注入成功后 - 你就可以直接使用例如 [ImageView.setImageResource] 或 [Resources.getString] 装载当前 Xposed 模块的资源 ID
 *
 * 注入的资源作用域仅限当前 [Context] - 你需要在每个用到宿主 [Context] 的地方重复调用此方法进行注入才能使用
 *
 * 详情请参考 [注入模块资源 (Resources)](https://fankes.github.io/YukiHookAPI/#/guide/special-feature?id=%e6%b3%a8%e5%85%a5%e6%a8%a1%e5%9d%97%e8%b5%84%e6%ba%90-resources)
 *
 * - ❗只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 */
fun Context.injectModuleAppResources() = resources?.injectModuleAppResources()

/**
 * 向 Hook APP (宿主) 指定 [Resources] 直接注入当前 Xposed 模块的资源
 *
 * 注入成功后 - 你就可以直接使用例如 [ImageView.setImageResource] 或 [Resources.getString] 装载当前 Xposed 模块的资源 ID
 *
 * 注入的资源作用域仅限当前 [Resources] - 你需要在每个用到宿主 [Resources] 的地方重复调用此方法进行注入才能使用
 *
 * 详情请参考 [注入模块资源 (Resources)](https://fankes.github.io/YukiHookAPI/#/guide/special-feature?id=%e6%b3%a8%e5%85%a5%e6%a8%a1%e5%9d%97%e8%b5%84%e6%ba%90-resources)
 *
 * - ❗只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 */
fun Resources.injectModuleAppResources() = AppParasitics.injectModuleAppResources(hostResources = this)

/**
 * 向 Hook APP (宿主) 注册当前 Xposed 模块的 [Activity]
 *
 * 注册成功后 - 你就可以直接使用 [Context.startActivity] 来启动未在宿主中注册的 [Activity]
 *
 * 使用此方法会在未注册的 [Activity] 在 Hook APP (宿主) 中启动时自动调用 [injectModuleAppResources] 注入当前 Xposed 模块的资源
 *
 * - 你要将需要在宿主启动的 [Activity] 继承于 [ModuleAppActivity] 或 [ModuleAppCompatActivity]
 *
 * 详情请参考 [注册模块 Activity](https://fankes.github.io/YukiHookAPI/#/guide/special-feature?id=%e6%b3%a8%e5%86%8c%e6%a8%a1%e5%9d%97-activity)
 *
 * - ❗只能在 (Xposed) 宿主环境使用此功能 - 其它环境下使用将不生效且会打印警告信息
 * @param proxy 代理的 [Activity] - 必须存在于宿主的 AndroidMainifest 清单中 - 不填使用默认 [Activity]
 */
fun Context.registerModuleAppActivities(proxy: Any? = null) = AppParasitics.registerModuleAppActivities(context = this, proxy)

/**
 * 生成一个 [ContextThemeWrapper] 代理以应用当前 Xposed 模块的主题资源
 *
 * 在 Hook APP (宿主) 中使用此方法会自动调用 [injectModuleAppResources] 注入当前 Xposed 模块的资源
 *
 * - 如果在 Hook APP (宿主) 中使用此方法发生 [ClassCastException] - 请手动设置新的 [configuration]
 *
 * 详情请参考 [创建 ContextThemeWrapper 代理](https://fankes.github.io/YukiHookAPI/#/guide/special-feature?id=%e5%88%9b%e5%bb%ba-contextthemewrapper-%e4%bb%a3%e7%90%86)
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
        var isModuleActive = false
        runCatching {
            var result: Bundle? = null
            Uri.parse("content://me.weishu.exposed.CP/").also { uri ->
                runCatching {
                    result = contentResolver.call(uri, "active", null, null)
                }.onFailure {
                    // TaiChi is killed, try invoke
                    runCatching {
                        startActivity(Intent("me.weishu.exp.ACTION_ACTIVE").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                    }.onFailure { return false }
                }
                if (result == null)
                    result = contentResolver.call(Uri.parse("content://me.weishu.exposed.CP/"), "active", null, null)
                if (result == null) return false
            }
            isModuleActive = result?.getBoolean("active", false) == true
        }
        return isModuleActive
    }