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
@file:Suppress("MemberVisibilityCanBePrivate", "unused", "EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi

import android.app.Application
import android.content.Context
import com.highcapable.yukihookapi.YukiHookAPI.configs
import com.highcapable.yukihookapi.YukiHookAPI.encase
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.processName
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * YukiHook 的装载 API 调用类
 *
 * 可以实现作为模块装载和自定义 Hook 装载两种方式
 *
 * 模块装载方式已经自动对接 Xposed API - 可直接调用 [encase] 完成操作
 *
 * 你可以调用 [configs] 对 YukiHook 进行配置
 */
object YukiHookAPI {

    /** Xposed Hook API 方法体回调 */
    private var packageParamCallback: (PackageParam.() -> Unit)? = null

    /**
     * 配置 YukiHookAPI
     */
    object Configs {

        /**
         * 这是一个调试日志的全局标识
         *
         * 默认文案为 YukiHookAPI
         *
         * 你可以修改为你自己的文案
         */
        var debugTag = "YukiHookAPI"

        /**
         * 是否开启调试模式 - 默认启用
         *
         * 启用后将交由日志输出管理器打印详细 Hook 日志到控制台
         *
         * 关闭后将只输出 Error 级别的日志
         *
         * 请过滤 [debugTag] 即可找到每条日志
         */
        var isDebug = true

        /**
         * Xposed Hook API 绑定的模块包名
         *
         * - 用于 [YukiHookModuleStatus] 判断模块激活状态
         *
         * 未写将自动生成
         */
        var modulePackageName = ""
    }

    /**
     * 配置 YukiHook 相关参数
     * @param initiate 方法体
     * @return [Configs]
     */
    fun configs(initiate: Configs.() -> Unit) = Configs.apply(initiate)

    /**
     * 装载 Xposed API 回调
     *
     * - ⚡装载代码将自动生成 - 你不应该手动使用此方法装载 Xposed 模块事件
     * @param lpparam Xposed [XC_LoadPackage.LoadPackageParam]
     */
    @DoNotUseMethod
    fun onXposedLoaded(lpparam: XC_LoadPackage.LoadPackageParam) =
        packageParamCallback?.invoke(
            PackageParam(
                PackageParamWrapper(
                    packageName = lpparam.packageName,
                    processName = lpparam.processName,
                    appClassLoader = lpparam.classLoader,
                    appInfo = lpparam.appInfo
                )
            )
        )

    /**
     * 作为模块装载调用入口方法 - Xposed API
     * @param initiate Hook 方法体
     */
    fun encase(initiate: PackageParam.() -> Unit) {
        packageParamCallback = initiate
    }

    /**
     * 作为模块装载调用入口方法 - Xposed API
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(vararg hooker: YukiBaseHooker) {
        packageParamCallback = {
            if (hooker.isNotEmpty())
                hooker.forEach { it.assignInstance(packageParam = this) }
            else error("Hooker is empty")
        }
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中写入如下代码：
     *
     * ....
     *
     *  override fun attachBaseContext(base: Context?) {
     *
     *  ....////
     *
     *  ....// 装载你使用的 Hook 框架的代码
     *
     *  ....// 你的 Hook 框架需要支持 Xposed API
     *
     *  ....////
     *
     *  ....// 装载 YukiHookAPI
     *
     *  ....YukiHookAPI.encase(base) {
     *
     *  ........// Your code here.
     *
     *  ....}
     *
     *  ....super.attachBaseContext(base)
     *
     *  }
     *
     *  ....
     *
     * 详情请参考 [YukiHookAPI Wiki](https://github.com/fankes/YukiHookAPI/wiki)
     * @param baseContext attachBaseContext
     * @param initiate Hook 方法体
     */
    fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit) {
        if (baseContext != null) initiate.invoke(baseContext.packagePararm)
    }

    /**
     * 作为 [Application] 装载调用入口方法
     *
     * 请在 [Application.attachBaseContext] 中写入如下代码：
     *
     * ....
     *
     *  override fun attachBaseContext(base: Context?) {
     *
     *  ....////
     *
     *  ....// 装载你使用的 Hook 框架的代码
     *
     *  ....// 你的 Hook 框架需要支持 Xposed API
     *
     *  ....////
     *
     *  ....// 装载 YukiHookAPI
     *
     *  ....YukiHookAPI.encase(base, MainHooker(), SecondHooker() ...)
     *
     *  ....super.attachBaseContext(base)
     *
     *  }
     *
     *  ....
     *
     * 详情请参考 [YukiHookAPI Wiki](https://github.com/fankes/YukiHookAPI/wiki)
     * @param baseContext attachBaseContext
     * @param hooker Hook 子类数组 - 必填不能为空
     * @throws IllegalStateException 如果 [hooker] 是空的
     */
    fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker) {
        if (baseContext != null)
            if (hooker.isNotEmpty())
                hooker.forEach { it.assignInstance(packageParam = baseContext.packagePararm) }
            else error("Hooker is empty")
    }

    /**
     * 通过 baseContext 创建 Hook 入口类
     * @return [PackageParam]
     */
    private val Context.packagePararm
        get() = PackageParam(PackageParamWrapper(packageName, processName, classLoader, applicationInfo))
}