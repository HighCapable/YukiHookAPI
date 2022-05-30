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
 * This file is Created by fankes on 2022/4/27.
 */
package com.highcapable.yukihookapi_ksp_xposed.sources

import java.text.SimpleDateFormat
import java.util.*

/**
 * 代码文件注入模板类
 */
object CodeSourceFileTemplate {

    /**
     * 获得文件注释
     * @param entryClassName 入口类名 - 空则不生成
     * @param currrentClassTag 当前注入类标签
     * @return [String]
     */
    private fun getCommentContent(entryClassName: String = "", currrentClassTag: String) =
        ("/**\n" +
                " * $currrentClassTag Inject Class\n" +
                " *\n" +
                " * Compiled from YukiHookXposedProcessor\n" +
                " *\n" +
                (if (entryClassName.isNotBlank()) " * HookEntryClass: [$entryClassName]\n *\n" else "") +
                " * Generate Date: ${SimpleDateFormat.getDateTimeInstance().format(Date())}\n" +
                " *\n" +
                " * Powered by YukiHookAPI (C) HighCapable 2022\n" +
                " *\n" +
                " * Project Address: [YukiHookAPI](https://github.com/fankes/YukiHookAPI)\n" +
                " */\n")

    /**
     * 获得 ModuleApplication_Injector 注入文件
     * @param packageName 包名
     * @param entryPackageName 入口类包名
     * @param entryClassName 入口类名
     * @return [ByteArray]
     */
    fun getModuleApplicationInjectorFileByteArray(packageName: String, entryPackageName: String, entryClassName: String) =
        ("@file:Suppress(\"ClassName\")\n" +
                "\n" +
                "package $packageName\n" +
                "\n" +
                "import $entryPackageName.$entryClassName\n" +
                "\n" +
                getCommentContent(entryClassName, currrentClassTag = "ModuleApplication") +
                "object ModuleApplication_Injector {\n" +
                "\n" +
                "    @JvmStatic\n" +
                "    fun callApiInit() = try {\n" +
                "        $entryClassName().onInit()\n" +
                "    } catch (_: Throwable) {\n" +
                "    }\n" +
                "}").toByteArray()

    /**
     * 获得 YukiHookBridge_Injector 注入文件
     * @param packageName 包名
     * @return [ByteArray]
     */
    fun getYukiHookBridgeInjectorFileByteArray(packageName: String) =
        ("@file:Suppress(\"ClassName\")\n" +
                "\n" +
                "package $packageName\n" +
                "\n" +
                getCommentContent(currrentClassTag = "YukiHookBridge") +
                "object YukiHookBridge_Injector {\n" +
                "\n" +
                "    @JvmStatic\n" +
                "    fun getModuleGeneratedVersion() = \"${System.currentTimeMillis()}\"\n" +
                "}").toByteArray()

    /**
     * 获得 xposed_init 注入文件
     * @param packageName 包名
     * @param entryClassName 入口类名
     * @param xInitClassName xposed_init 入口类名
     * @param isUsingResourcesHook 是否启用 Resources Hook
     * @return [ByteArray]
     */
    fun getXposedInitFileByteArray(packageName: String, entryClassName: String, xInitClassName: String, isUsingResourcesHook: Boolean) =
        ("@file:Suppress(\"ClassName\")\n" +
                "\n" +
                "package $packageName\n" +
                "\n" +
                "import androidx.annotation.Keep\n" +
                "import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent\n" +
                "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
                (if (isUsingResourcesHook) "import de.robv.android.xposed.IXposedHookInitPackageResources\n" else "") +
                "import de.robv.android.xposed.IXposedHookLoadPackage\n" +
                "import de.robv.android.xposed.IXposedHookZygoteInit\n" +
                (if (isUsingResourcesHook) "import de.robv.android.xposed.callbacks.XC_InitPackageResources\n" else "") +
                "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
                "\n" +
                getCommentContent(entryClassName, currrentClassTag = "Xposed Init") +
                "@Keep\n" +
                "@YukiGenerateApi\n" +
                "class $xInitClassName : IXposedHookZygoteInit, IXposedHookLoadPackage" +
                "${if (isUsingResourcesHook) ", IXposedHookInitPackageResources" else ""} {\n" +
                "\n" +
                "    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {\n" +
                "        ${entryClassName}_Impl.callInitZygote(sparam)\n" +
                "        YukiXposedEvent.EventHandler.callInitZygote(sparam)\n" +
                "    }\n" +
                "\n" +
                "    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
                "        ${entryClassName}_Impl.callHandleLoadPackage(lpparam)\n" +
                "        YukiXposedEvent.EventHandler.callHandleLoadPackage(lpparam)\n" +
                "    }\n" +
                (if (isUsingResourcesHook)
                    ("\n    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {\n" +
                            "        ${entryClassName}_Impl.callHandleInitPackageResources(resparam)\n" +
                            "        YukiXposedEvent.EventHandler.callHandleInitPackageResources(resparam)\n" +
                            "    }\n") else "") +
                "}").toByteArray()

    /**
     * 获得 xposed_init_Impl 注入文件
     * @param packageName 包名
     * @param modulePackageName 模块包名
     * @param entryClassName 入口类名
     * @return [ByteArray]
     */
    fun getXposedInitImplFileByteArray(packageName: String, modulePackageName: String, entryClassName: String) =
        ("@file:Suppress(\"ClassName\")\n" +
                "\n" +
                "package $packageName\n" +
                "\n" +
                "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
                "import com.highcapable.yukihookapi.hook.log.loggerE\n" +
                "import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge\n" +
                "import de.robv.android.xposed.IXposedHookZygoteInit\n" +
                "import de.robv.android.xposed.callbacks.XC_InitPackageResources\n" +
                "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
                "\n" +
                getCommentContent(entryClassName, currrentClassTag = "Xposed Init Impl") +
                "@YukiGenerateApi\n" +
                "object ${entryClassName}_Impl {\n" +
                "\n" +
                "    private const val modulePackageName = \"$modulePackageName\"\n" +
                "\n" +
                "    private val hookEntry = $entryClassName()\n" +
                "\n" +
                "    private var moduleClassLoader: ClassLoader? = null\n" +
                "    private var isZygoteBinded = false\n" +
                "\n" +
                "    private fun callXposedLoaded(\n" +
                "        isZygoteLoaded: Boolean = false,\n" +
                "        lpparam: XC_LoadPackage.LoadPackageParam? = null,\n" +
                "        resparam: XC_InitPackageResources.InitPackageResourcesParam? = null\n" +
                "    ) {\n" +
                "        if (isZygoteBinded.not()) runCatching {\n" +
                "            hookEntry.onXposedEvent()\n" +
                "            hookEntry.onInit()\n" +
                "            if (YukiHookBridge.isXposedCallbackSetUp) {\n" +
                "                loggerE(msg = \"You cannot load a hooker in \\\"onInit\\\" or \\\"onXposedEvent\\\" method! Aborted\")\n" +
                "                return\n" +
                "            }\n" +
                "            hookEntry.onHook()\n" +
                "            YukiHookBridge.callXposedInitialized()\n" +
                "            YukiHookBridge.modulePackageName = modulePackageName\n" +
                "        }.onFailure { loggerE(msg = \"YukiHookAPI try to load HookEntryClass failed\", e = it) }\n" +
                "        YukiHookBridge.callXposedLoaded(isZygoteLoaded, lpparam, resparam)\n" +
                "    }\n" +
                "\n" +
                "    private fun hookModuleAppStatus(classLoader: ClassLoader? = null, isHookResourcesStatus: Boolean = false) {\n" +
                "        classLoader?.let { moduleClassLoader = it }\n" +
                "        runCatching { YukiHookBridge.hookModuleAppStatus(moduleClassLoader, isHookResourcesStatus) }\n" +
                "    }\n" +
                "\n" +
                "    @YukiGenerateApi\n" +
                "    fun callInitZygote(sparam: IXposedHookZygoteInit.StartupParam?) {\n" +
                "        if (sparam == null) return\n" +
                "        runCatching {\n" +
                "            YukiHookBridge.callXposedZygoteLoaded(sparam)\n" +
                "        }.onFailure { loggerE(msg = \"YukiHookAPI bind initZygote failed\", e = it) }\n" +
                "        callXposedLoaded(isZygoteLoaded = true)\n" +
                "        isZygoteBinded = true\n" +
                "    }\n" +
                "\n" +
                "    @YukiGenerateApi\n" +
                "    fun callHandleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
                "        if (lpparam == null) return\n" +
                "        if (lpparam.packageName == modulePackageName) hookModuleAppStatus(lpparam.classLoader)\n" +
                "        callXposedLoaded(lpparam = lpparam)\n" +
                "    }\n" +
                "\n" +
                "    @YukiGenerateApi\n" +
                "    fun callHandleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {\n" +
                "        if (resparam == null) return\n" +
                "        if (resparam.packageName == modulePackageName) hookModuleAppStatus(isHookResourcesStatus = true)\n" +
                "        callXposedLoaded(resparam = resparam)\n" +
                "    }\n" +
                "}").toByteArray()
}