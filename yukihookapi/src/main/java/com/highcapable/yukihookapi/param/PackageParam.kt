@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.param

import android.content.pm.ApplicationInfo
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * 如果是侵入式 Hook 自身 APP 可将参数 [instance] 置空获得当前类的 [ClassLoader]
 * @param instance 对接 Xposed API 的 [XC_LoadPackage.LoadPackageParam]
 */
class PackageParam(private val instance: XC_LoadPackage.LoadPackageParam? = null) {

    /** 当前 classLoader */
    private var privateClassLoader = instance?.classLoader ?: javaClass.classLoader

    /**
     * 获取、设置当前 APP 的 [ClassLoader]
     * @return [ClassLoader]
     */
    var appClassLoader: ClassLoader
        get() = privateClassLoader
        set(value) {
            privateClassLoader = value
        }

    /**
     * 获取当前 APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = instance?.appInfo ?: ApplicationInfo()

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
    val packageName get() = instance?.packageName ?: ""

    /**
     * 获取当前 APP 是否为第一个 Application
     * @return [Boolean]
     */
    val isFirstApplication get() = instance?.isFirstApplication ?: true
}