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

package com.highcapable.yukihookapi.param

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 *
 * 如果你想将 YukiHook 作为 Hook API 使用 - 你可自定义 [customParam]
 *
 * ⚠️ 特别注意如果 [baseParam] 和 [customParam] 都为空将发生问题
 * @param baseParam 对接 Xposed API 的 [XC_LoadPackage.LoadPackageParam] - 默认空
 * @param customParam 自定义装载类 - 默认空
 */
open class PackageParam(
    private var baseParam: XC_LoadPackage.LoadPackageParam? = null,
    private var customParam: CustomParam? = null
) {

    /**
     * 获取当前 APP 的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 是空的
     */
    val appClassLoader
        get() = baseParam?.classLoader ?: customParam?.appClassLoader ?: javaClass.classLoader
        ?: error("PackageParam ClassLoader is null")

    /**
     * 获取当前 APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = baseParam?.appInfo ?: customParam?.appInfo ?: ApplicationInfo()

    /**
     * 获取当前 APP 的进程名称
     *
     * 默认的进程名称是 [packageName] 如果自定义了 [customParam] 将返回包名
     * @return [String]
     */
    val processName get() = baseParam?.processName ?: customParam?.packageName ?: ""

    /**
     * 获取当前 APP 的包名
     * @return [String]
     */
    val packageName get() = baseParam?.packageName ?: customParam?.packageName ?: ""

    /**
     * 获取当前 APP 是否为第一个 Application
     *
     * 若自定义了 [customParam] 将永远返回 true
     * @return [Boolean]
     */
    val isFirstApplication get() = baseParam?.isFirstApplication ?: true

    /**
     * 赋值并克隆另一个 [PackageParam]
     * @param another 另一个 [PackageParam]
     */
    @DoNotUseMethod
    internal fun baseAssignInstance(another: PackageParam) {
        this.baseParam = another.baseParam
        this.customParam = another.customParam
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
     * ⚠️ 请注意未绑定到 [appClassLoader] 的 [Class] 不能被装载 - 调用 [hook] 方法会自动绑定
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