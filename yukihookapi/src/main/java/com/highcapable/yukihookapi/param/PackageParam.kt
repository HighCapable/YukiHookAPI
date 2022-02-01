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
import com.highcapable.yukihookapi.hook.core.YukiHookCreater
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * 如果是侵入式 Hook 自身 APP 可将参数 [instance] 置空获得当前类的 [ClassLoader]
 * @param instance 对接 Xposed API 的 [XC_LoadPackage.LoadPackageParam] - 默认空
 * @param customInstance 自定义装载类 - 默认空
 */
class PackageParam(
    private val instance: XC_LoadPackage.LoadPackageParam? = null,
    private val customInstance: CustomParam? = null
) {

    /**
     * 获取当前 APP 的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 ClassLoader 是空的
     */
    val appClassLoader
        get() = instance?.classLoader ?: customInstance?.appClassLoader ?: javaClass.classLoader
        ?: error("PackageParam ClassLoader is null")

    /**
     * 获取当前 APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = instance?.appInfo ?: customInstance?.appInfo ?: ApplicationInfo()

    /**
     * 获取当前 APP 的进程名称
     * 默认的进程名称是 [packageName]
     * @return [String]
     */
    val processName get() = instance?.processName ?: ""

    /**
     * 获取当前 APP 的包名
     * @return [String]
     */
    val packageName get() = instance?.packageName ?: customInstance?.packageName ?: ""

    /**
     * 获取当前 APP 是否为第一个 Application
     * @return [Boolean]
     */
    val isFirstApplication get() = instance?.isFirstApplication ?: true

    /**
     * 装载并 Hook 指定包名的 APP
     * @param name 包名
     * @param initiate 方法体
     */
    fun loadApp(name: String, initiate: PackageParam.() -> Unit) {
        if (packageName == name) initiate(this)
    }

    /**
     * 通过字符串装载 [Class]
     * @param name 类名
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到类会报错
     */
    fun loadClass(name: String): Class<*> = appClassLoader.loadClass(name)

    /**
     * 查找目标方法
     * @param name 方法名
     * @param params 方法参数 - 没有可空
     * @return [Method]
     * @throws NoSuchMethodError 如果找不到方法会报错
     */
    fun Class<*>.loadMethod(name: String, vararg params: Class<*>): Method =
        getDeclaredMethod(name, *params).apply { isAccessible = true }

    /**
     * 查找目标构造类
     * @param params 方法参数 - 没有可空
     * @return [Constructor]
     * @throws NoSuchMethodError 如果找不到方法会报错
     */
    fun Class<*>.loadConstructor(vararg params: Class<*>): Constructor<*> =
        getDeclaredConstructor(*params).apply { isAccessible = true }

    /**
     * Hook 方法、构造类
     * @param initiate 方法体
     */
    fun Class<*>.hook(initiate: YukiHookCreater.() -> Unit) =
        YukiHookCreater(instance = this@PackageParam, hookClass = this).apply(initiate).hook()
}