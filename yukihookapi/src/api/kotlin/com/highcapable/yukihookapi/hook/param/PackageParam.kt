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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.param

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.res.Configuration
import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.bean.HookClass
import com.highcapable.yukihookapi.hook.bean.HookResources
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreater
import com.highcapable.yukihookapi.hook.core.YukiResourcesHookCreater
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.hookClass
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.utils.value
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiResources
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * @param wrapper [PackageParam] 的参数包装类实例 - 默认是空的
 */
open class PackageParam internal constructor(@PublishedApi internal var wrapper: PackageParamWrapper? = null) {

    /**
     * 用于展示的 APP 包名
     * @return [String]
     */
    @PublishedApi
    internal val exhibitName
        get() = wrapper?.exhibitName ?: "unknown"

    /**
     * 获取当前 Hook APP 的 [ClassLoader]
     * @return [ClassLoader]
     * @throws IllegalStateException 如果 [ClassLoader] 是空的
     */
    val appClassLoader
        get() = wrapper?.appClassLoader ?: YukiHookAppHelper.currentApplication()?.classLoader ?: javaClass.classLoader
        ?: error("PackageParam got null ClassLoader")

    /**
     * 获取当前 Hook APP 的 [ApplicationInfo]
     * @return [ApplicationInfo]
     */
    val appInfo get() = wrapper?.appInfo ?: YukiHookAppHelper.currentApplicationInfo() ?: ApplicationInfo()

    /**
     * 获取当前 Hook APP 的进程名称
     *
     * 默认的进程名称是 [packageName]
     * @return [String]
     */
    val processName get() = wrapper?.processName ?: YukiHookAppHelper.currentProcessName() ?: packageName

    /**
     * 获取当前 Hook APP 的包名
     * @return [String]
     */
    val packageName get() = wrapper?.packageName ?: YukiHookAppHelper.currentPackageName() ?: ""

    /**
     * 获取当前 Hook APP 的 [Application] 实例
     *
     * - ❗首次装载可能是空的 - 请延迟一段时间再获取或通过设置 [onAppLifecycle] 监听来完成
     * @return [Application]
     * @throws IllegalStateException 如果 [Application] 是空的
     */
    val appContext get() = YukiHookBridge.hostApplication ?: YukiHookAppHelper.currentApplication() ?: error("PackageParam got null appContext")

    /**
     * 获取当前 Hook APP 的 Resources
     *
     * - ❗你只能在 [HookResources.hook] 方法体内或 [appContext] 装载完毕时进行调用
     * @return [Resources]
     * @throws IllegalStateException 如果当前处于 [loadZygote] 或 [appContext] 尚未加载
     */
    val appResources get() = wrapper?.appResources ?: appContext.resources ?: error("You cannot call to appResources in this time")

    /**
     * 获取当前 Hook APP 是否为第一个 [Application]
     * @return [Boolean]
     */
    val isFirstApplication get() = packageName.trim() == processName.trim()

    /**
     * 获取当前 Hook APP 的主进程名称
     *
     * 其对应的就是 [packageName]
     * @return [String]
     */
    val mainProcessName get() = packageName.trim()

    /**
     * 获取当前 Xposed 模块自身 APK 文件路径
     *
     * - ❗作为 Hook API 装载时无法使用 - 会获取到空字符串
     * @return [String]
     */
    val moduleAppFilePath get() = YukiHookBridge.moduleAppFilePath

    /**
     * 获取当前 Xposed 模块自身 [Resources]
     *
     * - ❗作为 Hook API 或不支持的 Hook Framework 装载时无法使用 - 会抛出异常
     * @return [YukiModuleResources]
     * @throws IllegalStateException 如果当前 Hook Framework 不支持此功能
     */
    val moduleAppResources
        get() = (if (YukiHookAPI.Configs.isEnableModuleAppResourcesCache) YukiHookBridge.moduleAppResources
        else YukiHookBridge.dynamicModuleAppResources) ?: error("Current Hook Framework not support moduleAppResources")

    /**
     * 获得当前使用的存取数据对象缓存实例
     *
     * - ❗作为 Hook API 装载时无法使用 - 会抛出异常
     * @return [YukiHookModulePrefs]
     */
    val prefs get() = YukiHookModulePrefs.instance()

    /**
     * 获得当前使用的存取数据对象缓存实例
     *
     * - ❗作为 Hook API 装载时无法使用 - 会抛出异常
     * @param name 自定义 Sp 存储名称
     * @return [YukiHookModulePrefs]
     */
    fun prefs(name: String) = prefs.name(name)

    /**
     * 获得当前使用的数据通讯桥命名空间对象
     *
     * - ❗作为 Hook API 装载时无法使用 - 会抛出异常
     * @return [YukiHookDataChannel.NameSpace]
     * @throws IllegalStateException 如果在 [HookEntryType.ZYGOTE] 装载
     */
    val dataChannel
        get() = if (wrapper?.type != HookEntryType.ZYGOTE)
            YukiHookDataChannel.instance().nameSpace(packageName = packageName)
        else error("YukiHookDataChannel cannot used in zygote")

    /**
     * 赋值并克隆另一个 [PackageParam]
     * @param anotherParam 另一个 [PackageParam]
     */
    internal fun baseAssignInstance(anotherParam: PackageParam) {
        this.wrapper = anotherParam.wrapper
    }

    /**
     * 获得当前 Hook APP 的 [YukiResources] 对象
     *
     * 请调用 [HookResources.hook] 方法开始 Hook
     * @return [HookResources]
     */
    fun resources() = HookResources(wrapper?.appResources)

    /** 刷新当前 Xposed 模块自身 [Resources] */
    fun refreshModuleAppResources() = YukiHookBridge.refreshModuleAppResources()

    /**
     * 监听当前 Hook APP 生命周期装载事件
     *
     * - ❗在 [loadZygote] 中不会被装载 - 仅会在 [loadSystem]、[loadApp] 中装载
     *
     * - ❗作为 Hook API 装载时请使用原生的 [Application] 实现生命周期监听
     * @param initiate 方法体
     */
    inline fun onAppLifecycle(initiate: AppLifecycle.() -> Unit) = AppLifecycle().apply(initiate).build()

    /**
     * 装载并 Hook 指定、全部包名的 APP
     *
     * 若要装载 APP Zygote 事件 - 请使用 [loadZygote]
     *
     * 若要 Hook 系统框架 - 请使用 [loadSystem]
     * @param name 包名 - 不填将过滤除了 [loadZygote] 事件外的全部 APP
     * @param initiate 方法体
     */
    inline fun loadApp(name: String = "", initiate: PackageParam.() -> Unit) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (packageName == name || name.isBlank())) initiate(this)
    }

    /**
     * 装载并 Hook 指定、全部包名的 APP
     *
     * 若要装载 APP Zygote 事件 - 请使用 [loadZygote]
     *
     * 若要 Hook 系统框架 - 请使用 [loadSystem]
     * @param name 包名 - 不填将过滤除了 [loadZygote] 事件外的全部 APP
     * @param hooker Hook 子类
     */
    fun loadApp(name: String = "", hooker: YukiBaseHooker) {
        if (wrapper?.type != HookEntryType.ZYGOTE && (packageName == name || name.isBlank())) loadHooker(hooker)
    }

    /**
     * 装载并 Hook 系统框架
     * @param initiate 方法体
     */
    inline fun loadSystem(initiate: PackageParam.() -> Unit) = loadApp(YukiHookBridge.SYSTEM_FRAMEWORK_NAME, initiate)

    /**
     * 装载并 Hook 系统框架
     * @param hooker Hook 子类
     */
    fun loadSystem(hooker: YukiBaseHooker) = loadApp(YukiHookBridge.SYSTEM_FRAMEWORK_NAME, hooker)

    /**
     * 装载 APP Zygote 事件
     * @param initiate 方法体
     */
    inline fun loadZygote(initiate: PackageParam.() -> Unit) {
        if (wrapper?.type == HookEntryType.ZYGOTE) initiate(this)
    }

    /**
     * 装载 APP Zygote 事件
     * @param hooker Hook 子类
     */
    fun loadZygote(hooker: YukiBaseHooker) {
        if (wrapper?.type == HookEntryType.ZYGOTE) loadHooker(hooker)
    }

    /**
     * 装载并 Hook APP 的指定进程
     * @param name 进程名 - 若要指定主进程可填写 [mainProcessName] - 效果与 [isFirstApplication] 一致
     * @param initiate 方法体
     */
    inline fun withProcess(name: String, initiate: PackageParam.() -> Unit) {
        if (processName == name) initiate(this)
    }

    /**
     * 装载并 Hook APP 的指定进程
     * @param name 进程名 - 若要指定主进程可填写 [mainProcessName] - 效果与 [isFirstApplication] 一致
     * @param hooker Hook 子类
     */
    fun withProcess(name: String, hooker: YukiBaseHooker) {
        if (processName == name) loadHooker(hooker)
    }

    /**
     * 装载 Hook 子类
     *
     * 你可以在 Hooker 中继续装载 Hooker
     * @param hooker Hook 子类
     */
    fun loadHooker(hooker: YukiBaseHooker) = hooker.assignInstance(packageParam = this)

    /**
     * 通过字符串转换为实体类
     *
     * - 使用当前 [appClassLoader] 装载目标 [Class]
     *
     * - 若要使用指定的 [ClassLoader] 装载 - 请手动调用 [classOf] 方法
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class]
     */
    val String.clazz get() = classOf(name = this, appClassLoader)

    /**
     * [VariousClass] 转换为实体类
     *
     * - 使用当前 [appClassLoader] 装载目标 [Class]
     * @return [Class]
     * @throws IllegalStateException 如果任何 [Class] 都没有匹配到
     */
    val VariousClass.clazz get() = get(appClassLoader)

    /**
     * 通过字符串查找类是否存在
     *
     * - 默认使用当前 [appClassLoader] 装载目标 [Class]
     * @return [Boolean] 是否存在
     */
    val String.hasClass get() = hasClass(appClassLoader)

    /**
     * 默认使用当前 [appClassLoader] 查询并装载 [Class]
     * @param name 类名
     * @return [HookClass]
     */
    fun findClass(name: String) = try {
        name.clazz.hookClass
    } catch (e: Throwable) {
        HookClass(name = name, throwable = e)
    }

    /**
     * 默认使用当前 [appClassLoader] 查询并装载 [Class]
     *
     * 使用此方法查询将会取 [name] 其中命中存在的第一个 [Class] 作为结果
     * @param name 可填入多个类名 - 自动匹配
     * @return [VariousClass]
     */
    fun findClass(vararg name: String) = VariousClass(*name)

    /**
     * Hook 方法、构造类
     *
     * - ❗为防止任何字符串都被当做 [Class] 进行 Hook - 推荐优先使用 [findClass]
     * @param isUseAppClassLoader 是否使用 [appClassLoader] 重新绑定当前 [Class] - 默认启用
     * @param initiate 方法体
     * @return [YukiMemberHookCreater.Result]
     */
    inline fun String.hook(isUseAppClassLoader: Boolean = true, initiate: YukiMemberHookCreater.() -> Unit) =
        findClass(name = this).hook(isUseAppClassLoader, initiate)

    /**
     * Hook 方法、构造类
     * @param isUseAppClassLoader 是否使用 [appClassLoader] 重新绑定当前 [Class] - 默认启用
     * @param initiate 方法体
     * @return [YukiMemberHookCreater.Result]
     */
    inline fun Class<*>.hook(isUseAppClassLoader: Boolean = true, initiate: YukiMemberHookCreater.() -> Unit) =
        hookClass.hook(isUseAppClassLoader, initiate)

    /**
     * Hook 方法、构造类
     * @param isUseAppClassLoader 是否使用 [appClassLoader] 重新绑定当前 [Class] - 默认启用
     * @param initiate 方法体
     * @return [YukiMemberHookCreater.Result]
     */
    inline fun VariousClass.hook(isUseAppClassLoader: Boolean = true, initiate: YukiMemberHookCreater.() -> Unit) =
        hookClass(if (isUseAppClassLoader) appClassLoader else null).hook(isUseAppClassLoader, initiate)

    /**
     * Hook 方法、构造类
     * @param isUseAppClassLoader 是否使用 [appClassLoader] 重新绑定当前 [Class] - 默认启用
     * @param initiate 方法体
     * @return [YukiMemberHookCreater.Result]
     */
    inline fun HookClass.hook(isUseAppClassLoader: Boolean = true, initiate: YukiMemberHookCreater.() -> Unit) =
        YukiMemberHookCreater(packageParam = this@PackageParam, hookClass = if (isUseAppClassLoader) bind() else this).apply(initiate).hook()

    /**
     * Hook APP 的 Resources
     *
     * - ❗请注意你需要确保当前 Hook Framework 支持且 [InjectYukiHookWithXposed.isUsingResourcesHook] 已启用
     * @param initiate 方法体
     */
    inline fun HookResources.hook(initiate: YukiResourcesHookCreater.() -> Unit) =
        YukiResourcesHookCreater(packageParam = this@PackageParam, hookResources = this).apply(initiate).hook()

    /**
     * [VariousClass] 转换为 [HookClass] 并绑定到 [appClassLoader]
     * @param loader 当前 [ClassLoader] - 若留空使用默认 [ClassLoader]
     * @return [HookClass]
     */
    @PublishedApi
    internal fun VariousClass.hookClass(loader: ClassLoader? = null) = try {
        get(loader).hookClass
    } catch (e: Throwable) {
        HookClass(name = "VariousClass", throwable = Throwable(e.message))
    }

    /**
     * 将目标 [Class] 绑定到 [appClassLoader]
     *
     * - ❗请注意未绑定到 [appClassLoader] 的 [Class] 是不安全的 - 调用 [hook] 方法会根据设定自动绑定
     * @return [HookClass]
     */
    @PublishedApi
    internal fun HookClass.bind() = try {
        name.clazz.hookClass
    } catch (e: Throwable) {
        HookClass(name = name, throwable = throwable ?: e)
    }

    /**
     * 当前 Hook APP 的生命周期实例处理类
     *
     * - ❗请使用 [onAppLifecycle] 方法来获取 [AppLifecycle]
     */
    inner class AppLifecycle @PublishedApi internal constructor() {

        /**
         * 监听当前 Hook APP 装载 [Application.attachBaseContext]
         * @param result 回调 - ([Context] baseContext,[Boolean] 是否已执行 super)
         */
        fun attachBaseContext(result: (baseContext: Context, hasCalledSuper: Boolean) -> Unit) {
            YukiHookBridge.AppLifecycleCallback.attachBaseContextCallback = result
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onCreate]
         * @param initiate 方法体
         */
        fun onCreate(initiate: Application.() -> Unit) {
            YukiHookBridge.AppLifecycleCallback.onCreateCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onTerminate]
         * @param initiate 方法体
         */
        fun onTerminate(initiate: Application.() -> Unit) {
            YukiHookBridge.AppLifecycleCallback.onTerminateCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onLowMemory]
         * @param initiate 方法体
         */
        fun onLowMemory(initiate: Application.() -> Unit) {
            YukiHookBridge.AppLifecycleCallback.onLowMemoryCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onTrimMemory]
         * @param result 回调 - ([Application] 当前实例,[Int] 类型)
         */
        fun onTrimMemory(result: (self: Application, level: Int) -> Unit) {
            YukiHookBridge.AppLifecycleCallback.onTrimMemoryCallback = result
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onConfigurationChanged]
         * @param result 回调 - ([Application] 当前实例,[Configuration] 配置实例)
         */
        fun onConfigurationChanged(result: (self: Application, config: Configuration) -> Unit) {
            YukiHookBridge.AppLifecycleCallback.onConfigurationChangedCallback = result
        }

        /**
         * 注册系统广播监听
         * @param action 系统广播 Action
         * @param result 回调 - ([Context] 当前上下文,[Intent] 当前 Intent)
         */
        fun registerReceiver(vararg action: String, result: (context: Context, intent: Intent) -> Unit) {
            if (action.isNotEmpty()) YukiHookBridge.AppLifecycleCallback.onReceiversCallback[action.value()] = Pair(action, result)
        }

        /** 设置创建生命周期监听回调 */
        @PublishedApi
        internal fun build() {
            YukiHookBridge.AppLifecycleCallback.isCallbackSetUp = true
        }
    }

    override fun toString() = "PackageParam by $wrapper"
}