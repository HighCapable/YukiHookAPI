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
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.YukiResourcesHookCreator
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.param.wrapper.PackageParamWrapper
import com.highcapable.yukihookapi.hook.utils.value
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiModuleResources
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiResources
import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import com.highcapable.yukihookapi.hook.xposed.helper.YukiHookAppHelper
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.prefs.YukiHookModulePrefs

/**
 * 装载 Hook 的目标 APP 入口对象实现类
 * @param wrapper [PackageParam] 的参数包装类实例 - 默认是空的
 */
open class PackageParam internal constructor(@PublishedApi internal var wrapper: PackageParamWrapper? = null) {

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
     * 获取当前 Hook APP 的用户 ID
     *
     * 机主为 0 - 应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同
     * @return [Int]
     */
    val appUserId get() = AppParasitics.findUserId(packageName)

    /**
     * 获取当前 Hook APP 的 [Application] 实例
     *
     * - ❗首次装载可能是空的 - 请延迟一段时间再获取或通过设置 [onAppLifecycle] 监听来完成
     * @return [Application] or null
     */
    val appContext get() = AppParasitics.hostApplication ?: YukiHookAppHelper.currentApplication()

    /**
     * 获取当前 Hook APP 的 Resources
     *
     * - ❗你只能在 [HookResources.hook] 方法体内或 [appContext] 装载完毕时进行调用
     * @return [Resources] or null
     */
    val appResources get() = wrapper?.appResources ?: appContext?.resources

    /**
     * 获取当前系统框架的 [Context]
     * @return [Context] ContextImpl 实例对象
     * @throws IllegalStateException 如果获取不到系统框架的 [Context]
     */
    val systemContext get() = AppParasitics.systemContext

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
    val moduleAppFilePath get() = AppParasitics.moduleAppFilePath

    /**
     * 获取当前 Xposed 模块自身 [Resources]
     *
     * - ❗作为 Hook API 或不支持的 Hook Framework 装载时无法使用 - 会抛出异常
     * @return [YukiModuleResources]
     * @throws IllegalStateException 如果当前 Hook Framework 不支持此功能
     */
    val moduleAppResources
        get() = (if (YukiHookAPI.Configs.isEnableModuleAppResourcesCache) AppParasitics.moduleAppResources
        else AppParasitics.dynamicModuleAppResources) ?: error("Current Hook Framework not support moduleAppResources")

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
    fun refreshModuleAppResources() = AppParasitics.refreshModuleAppResources()

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
     * 通过字符串类名转换为当前 Hook APP 的实体类
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [toClass]
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class]
     */
    @Deprecated(message = "请使用新的命名方法", ReplaceWith(expression = "toClass()"))
    val String.clazz
        get() = toClass()

    /**
     * [VariousClass] 转换为当前 Hook APP 的实体类
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [toClass]
     * @return [Class]
     * @throws IllegalStateException 如果任何 [Class] 都没有匹配到
     */
    @Deprecated(message = "请使用新的命名方法", ReplaceWith(expression = "toClass()"))
    val VariousClass.clazz
        get() = toClass()

    /**
     * 通过字符串类名查找是否存在
     *
     * - ❗此方法已弃用 - 在之后的版本中将直接被删除
     *
     * - ❗请现在转移到 [hasClass]
     * @return [Boolean] 是否存在
     */
    @Deprecated(message = "请使用新的命名方法", ReplaceWith(expression = "hasClass()"))
    val String.hasClass
        get() = hasClass()

    /**
     * 通过字符串类名转换为 [loader] 中的实体类
     * @param loader [Class] 所在的 [ClassLoader] - 不填使用 [appClassLoader]
     * @return [Class]
     * @throws NoClassDefFoundError 如果找不到 [Class]
     */
    fun String.toClass(loader: ClassLoader? = appClassLoader) = ReflectionTool.findClassByName(name = this, loader)

    /**
     * [VariousClass] 转换为 [loader] 中的实体类
     * @param loader [Class] 所在的 [ClassLoader] - 不填使用 [appClassLoader]
     * @return [Class]
     * @throws IllegalStateException 如果任何 [Class] 都没有匹配到
     */
    fun VariousClass.toClass(loader: ClassLoader? = appClassLoader) = get(loader)

    /**
     * 通过字符串类名查找是否存在
     * @param loader [Class] 所在的 [ClassLoader] - 不填使用 [appClassLoader]
     * @return [Boolean] 是否存在
     */
    fun String.hasClass(loader: ClassLoader? = appClassLoader) = ReflectionTool.hasClassByName(name = this, loader)

    /**
     * 查找并装载 [HookClass]
     *
     * - ❗使用此方法会得到一个 [HookClass] 仅用于 Hook - 若想查找 [Class] 请使用 [toClass] 功能
     * @param name 类名
     * @param loader 当前 [ClassLoader] - 默认使用 [appClassLoader] - 设为 null 使用默认 [ClassLoader]
     * @return [HookClass]
     */
    fun findClass(name: String, loader: ClassLoader? = appClassLoader) =
        runCatching { name.toClass(loader).toHookClass() }.getOrElse { HookClass(name = name, throwable = it) }

    /**
     * 查找并装载 [HookClass]
     *
     * 使用此方法查找将会取 [name] 其中命中存在的第一个 [Class] 作为结果
     *
     * - ❗使用此方法会得到一个 [HookClass] 仅用于 Hook - 若想查找 [Class] 请使用 [toClass] 功能
     * @param name 可填入多个类名 - 自动匹配
     * @param loader 当前 [ClassLoader] - 默认使用 [appClassLoader] - 设为 null 使用默认 [ClassLoader]
     * @return [HookClass]
     */
    fun findClass(vararg name: String, loader: ClassLoader? = appClassLoader) = VariousClass(*name).toHookClass(loader)

    /**
     * Hook 方法、构造方法
     *
     * - 使用当前 [appClassLoader] 装载目标 [Class]
     *
     * - ❗为防止任何字符串都被当做 [Class] 进行 Hook - 推荐优先使用 [findClass]
     * @param initiate 方法体
     * @return [YukiMemberHookCreator.Result]
     */
    inline fun String.hook(initiate: YukiMemberHookCreator.() -> Unit) = findClass(name = this).hook(initiate)

    /**
     * Hook 方法、构造方法
     *
     * - 自动选择与当前 [Class] 相匹配的 [ClassLoader] - 优先使用 [appClassLoader]
     *
     * - ❗若当前 [Class] 不在 [appClassLoader] 且自动匹配无法找到该 [Class] - 请启用 [isForceUseAbsolute]
     * @param isForceUseAbsolute 是否强制使用绝对实例对象 - 默认否
     * @param initiate 方法体
     * @return [YukiMemberHookCreator.Result]
     */
    inline fun Class<*>.hook(isForceUseAbsolute: Boolean = false, initiate: YukiMemberHookCreator.() -> Unit) = when {
        isForceUseAbsolute -> toHookClass()
        name.hasClass() -> findClass(name)
        else -> toHookClass()
    }.hook(initiate)

    /**
     * Hook 方法、构造方法
     *
     * - 使用当前 [appClassLoader] 装载目标 [Class]
     * @param initiate 方法体
     * @return [YukiMemberHookCreator.Result]
     */
    inline fun VariousClass.hook(initiate: YukiMemberHookCreator.() -> Unit) = toHookClass(appClassLoader).hook(initiate)

    /**
     * Hook 方法、构造方法
     * @param initiate 方法体
     * @return [YukiMemberHookCreator.Result]
     */
    inline fun HookClass.hook(initiate: YukiMemberHookCreator.() -> Unit) =
        YukiMemberHookCreator(packageParam = this@PackageParam, hookClass = this).apply(initiate).hook()

    /**
     * Hook APP 的 Resources
     *
     * - ❗请注意你需要确保当前 Hook Framework 支持且 [InjectYukiHookWithXposed.isUsingResourcesHook] 已启用
     * @param initiate 方法体
     */
    inline fun HookResources.hook(initiate: YukiResourcesHookCreator.() -> Unit) =
        YukiResourcesHookCreator(packageParam = this@PackageParam, hookResources = this).apply(initiate).hook()

    /**
     * [VariousClass] 转换为 [HookClass]
     * @param loader 当前 [ClassLoader] - 若留空使用默认 [ClassLoader]
     * @return [HookClass]
     */
    @PublishedApi
    internal fun VariousClass.toHookClass(loader: ClassLoader? = null) =
        runCatching { get(loader).toHookClass() }.getOrElse { HookClass(name = "VariousClass", throwable = Throwable(it.message)) }

    /**
     * [Class] 转换为 [HookClass]
     * @return [HookClass]
     */
    @PublishedApi
    internal fun Class<*>.toHookClass() = HookClass(instance = this, name)

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
            AppParasitics.AppLifecycleCallback.attachBaseContextCallback = result
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onCreate]
         * @param initiate 方法体
         */
        fun onCreate(initiate: Application.() -> Unit) {
            AppParasitics.AppLifecycleCallback.onCreateCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onTerminate]
         * @param initiate 方法体
         */
        fun onTerminate(initiate: Application.() -> Unit) {
            AppParasitics.AppLifecycleCallback.onTerminateCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onLowMemory]
         * @param initiate 方法体
         */
        fun onLowMemory(initiate: Application.() -> Unit) {
            AppParasitics.AppLifecycleCallback.onLowMemoryCallback = initiate
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onTrimMemory]
         * @param result 回调 - ([Application] 当前实例,[Int] 类型)
         */
        fun onTrimMemory(result: (self: Application, level: Int) -> Unit) {
            AppParasitics.AppLifecycleCallback.onTrimMemoryCallback = result
        }

        /**
         * 监听当前 Hook APP 装载 [Application.onConfigurationChanged]
         * @param result 回调 - ([Application] 当前实例,[Configuration] 配置实例)
         */
        fun onConfigurationChanged(result: (self: Application, config: Configuration) -> Unit) {
            AppParasitics.AppLifecycleCallback.onConfigurationChangedCallback = result
        }

        /**
         * 注册系统广播监听
         * @param action 系统广播 Action
         * @param result 回调 - ([Context] 当前上下文,[Intent] 当前 Intent)
         */
        fun registerReceiver(vararg action: String, result: (context: Context, intent: Intent) -> Unit) {
            if (action.isNotEmpty()) AppParasitics.AppLifecycleCallback.onReceiversCallback[action.value()] = Pair(action, result)
        }

        /** 设置创建生命周期监听回调 */
        @PublishedApi
        internal fun build() {
            AppParasitics.AppLifecycleCallback.isCallbackSetUp = true
        }
    }

    override fun toString() = "PackageParam by $wrapper"
}