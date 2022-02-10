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
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.param

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.hookClass
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * @param wrapper [PackageParam] 的参数包装类实例 - 默认是空的
 */
open class PackageParam(private var wrapper: PackageParamWrapper? = null) {

    /**
     * 获取当前 APP 的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 是空的
     */
    val appClassLoader
        get() = wrapper?.appClassLoader ?: javaClass.classLoader ?: error("PackageParam got null ClassLoader")

    /**
     * 获取当前 APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = wrapper?.appInfo ?: ApplicationInfo()

    /**
     * 获取当前 APP 的进程名称
     *
     * 默认的进程名称是 [packageName]
     * @return [String]
     */
    val processName get() = wrapper?.processName ?: packageName

    /**
     * 获取当前 APP 的包名
     * @return [String]
     */
    val packageName get() = wrapper?.packageName ?: ""

    /**
     * 获取当前 APP 是否为第一个 Application
     * @return [Boolean]
     */
    val isFirstApplication get() = packageName == processName

    /**
     * 获得当前使用的存取数据对象缓存实例
     * @return [YukiHookModulePrefs]
     */
    val prefs by lazy { YukiHookModulePrefs() }

    /**
     * 获得当前使用的存取数据对象缓存实例
     * @param name 自定义 Sp 存储名称
     * @return [YukiHookModulePrefs]
     */
    fun prefs(name: String) = prefs.name(name)

    /**
     * 赋值并克隆另一个 [PackageParam]
     *
     * - ❗此方法为私有功能性 API - 你不应该手动调用此方法
     * @param anotherParam 另一个 [PackageParam]
     */
    @DoNotUseMethod
    internal fun baseAssignInstance(anotherParam: PackageParam) {
        thisParam.wrapper = anotherParam.wrapper
    }

    /**
     * 装载并 Hook 指定包名的 APP
     * @param name 包名
     * @param initiate 方法体
     */
    fun loadApp(name: String, initiate: PackageParam.() -> Unit) {
        if (packageName == name) initiate(this)
    }

    /**
     * 装载并 Hook 指定包名的 APP
     * @param name 包名
     * @param hooker Hook 子类
     */
    fun loadApp(name: String, hooker: YukiBaseHooker) {
        if (packageName == name) loadHooker(hooker)
    }

    /**
     * 装载 Hook 子类
     *
     * 你可以在 Hooker 中继续装载 Hooker
     * @param hooker Hook 子类
     */
    fun loadHooker(hooker: YukiBaseHooker) = hooker.assignInstance(packageParam = this)

    /**
     * 通过 [appClassLoader] 查询并装载 [Class]
     * @param name 类名
     * @return [HookClass]
     */
    fun findClass(name: String) = try {
        appClassLoader.loadClass(name).hookClass
    } catch (e: Throwable) {
        HookClass(name = name, throwable = e)
    }

    /**
     * Hook 方法、构造类
     * @param initiate 方法体
     */
    fun Class<*>.hook(initiate: YukiHookCreater.() -> Unit) =
        YukiHookCreater(packageParam = thisParam, hookClass = hookClass.bind()).apply(initiate).hook()

    /**
     * Hook 方法、构造类
     * @param initiate 方法体
     */
    fun HookClass.hook(initiate: YukiHookCreater.() -> Unit) =
        YukiHookCreater(packageParam = thisParam, hookClass = bind()).apply(initiate).hook()

    /**
     * 将目标 [Class] 绑定到 [appClassLoader]
     *
     * - ❗请注意未绑定到 [appClassLoader] 的 [Class] 不能被装载 - 调用 [hook] 方法会自动绑定
     * @return [HookClass]
     */
    private fun HookClass.bind() = try {
        appClassLoader.loadClass(name).hookClass
    } catch (e: Throwable) {
        HookClass(name = name, throwable = e)
    }

    /**
     * 返回自身实例
     * @return [PackageParam]
     */
    private val thisParam get() = this
}