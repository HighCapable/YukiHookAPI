/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * @param baseParam 对接环境装载类的实现 - 默认是空的
 */
open class PackageParam(private var baseParam: EnvironmentParam? = null) {

    /**
     * 获取当前 APP 的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 是空的
     */
    val appClassLoader
        get() = baseParam?.appClassLoader ?: javaClass.classLoader ?: error("PackageParam got null ClassLoader")

    /**
     * 获取当前 APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = baseParam?.appInfo ?: ApplicationInfo()

    /**
     * 获取当前 APP 的进程名称
     *
     * 默认的进程名称是 [packageName]
     * @return [String]
     */
    val processName get() = baseParam?.processName ?: packageName

    /**
     * 获取当前 APP 的包名
     * @return [String]
     */
    val packageName get() = baseParam?.packageName ?: ""

    /**
     * 获取当前 APP 是否为第一个 Application
     * @return [Boolean]
     */
    val isFirstApplication get() = packageName == processName

    /**
     * 赋值并克隆另一个 [PackageParam]
     * @param another 另一个 [PackageParam]
     * - 此方法为私有功能性 API - 你不应该手动调用此方法
     */
    @DoNotUseMethod
    internal fun baseAssignInstance(another: PackageParam) {
        this.baseParam = another.baseParam
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
     * 装载 Hook 子类
     *
     * 你可以在 Hooker 中继续装载 Hooker
     * @param hooker Hook 子类
     */
    fun loadHooker(hooker: YukiBaseHooker) = hooker.assignInstance(packageParam = this)

    /**
     * 将目标 [Class] 绑定到 [appClassLoader]
     *
     * - 请注意未绑定到 [appClassLoader] 的 [Class] 不能被装载 - 调用 [hook] 方法会自动绑定
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到类会报错
     */
    fun Class<*>.bind(): Class<*> = appClassLoader.loadClass(name)

    /**
     * 通过 [appClassLoader] 查询并装载 [Class]
     * @param name 类名
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到类会报错
     */
    fun findClass(name: String): Class<*> = appClassLoader.loadClass(name)

    /**
     * Hook 方法、构造类
     * @param initiate 方法体
     */
    fun Class<*>.hook(initiate: YukiHookCreater.() -> Unit) =
        YukiHookCreater(packageParam = this@PackageParam, hookClass = bind()).apply(initiate).hook()
}