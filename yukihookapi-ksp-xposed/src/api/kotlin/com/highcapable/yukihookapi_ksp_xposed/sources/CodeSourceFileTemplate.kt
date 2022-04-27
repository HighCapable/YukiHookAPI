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
 * 代码文件注入器
 */
object CodeSourceFileTemplate {

    /** 定义 Jvm 方法名 */
    private const val IS_ACTIVE_METHOD_NAME = "__--"

    /** 定义 Jvm 方法名 */
    private const val GET_XPOSED_VERSION_METHOD_NAME = "--__"

    /** 定义 Jvm 方法名 */
    private const val GET_XPOSED_TAG_METHOD_NAME = "_-_-"

    /**
     * 获得文件注释
     * @param entryClassName 入口类名
     * @param currrentClassTag 当前注入类标签
     * @return [String]
     */
    private fun getCommentContent(entryClassName: String, currrentClassTag: String) =
        ("/**\n" +
                " * $currrentClassTag Inject Class\n" +
                " *\n" +
                " * Compiled from YukiHookXposedProcessor\n" +
                " *\n" +
                " * HookEntryClass: [$entryClassName]\n" +
                " *\n" +
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
     * 获得 xposed_init 注入文件
     * @param packageName 包名
     * @param modulePackageName 模块包名
     * @param entryClassName 入口类名
     * @param xInitClassName xposed_init 入口类名
     * @return [ByteArray]
     */
    fun getXposedInitFileByteArray(packageName: String, modulePackageName: String, entryClassName: String, xInitClassName: String) =
        ("package $packageName\n" +
                "\n" +
                "import androidx.annotation.Keep\n" +
                "import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookXposedBridge\n" +
                "import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus\n" +
                "import com.highcapable.yukihookapi.hook.log.loggerE\n" +
                "import de.robv.android.xposed.IXposedHookLoadPackage\n" +
                "import de.robv.android.xposed.XC_MethodReplacement\n" +
                "import de.robv.android.xposed.XposedHelpers\n" +
                "import de.robv.android.xposed.XposedBridge\n" +
                "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
                "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
                "import $packageName.$entryClassName\n" +
                "\n" +
                getCommentContent(entryClassName, currrentClassTag = "XposedInit") +
                "@Keep\n" +
                "@YukiGenerateApi\n" +
                "class $xInitClassName : IXposedHookLoadPackage {\n" +
                "\n" +
                "    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
                "        if (lpparam == null) return\n" +
                "        try {\n" +
                "            $entryClassName().apply {\n" +
                "                onInit()\n" +
                "                if (YukiHookXposedBridge.isXposedCallbackSetUp) {\n" +
                "                    loggerE(tag = \"YukiHookAPI\", msg = \"You cannot loading a hooker in \\\"onInit\\\" method! Aborted\")\n" +
                "                    return\n" +
                "                }\n" +
                "                onHook()\n" +
                "            }\n" +
                "            YukiHookXposedBridge.callXposedInitialized()\n" +
                "        } catch (e: Throwable) {\n" +
                "            loggerE(tag = \"YukiHookAPI\", msg = \"YukiHookAPI try to load HookEntryClass failed\", e = e)\n" +
                "        }\n" +
                "        if (lpparam.packageName == \"$modulePackageName\") {\n" +
                "            XposedHelpers.findAndHookMethod(\n" +
                "                YukiHookModuleStatus::class.java.name,\n" +
                "                lpparam.classLoader,\n" +
                "                \"$IS_ACTIVE_METHOD_NAME\",\n" +
                "                object : XC_MethodReplacement() {\n" +
                "                    override fun replaceHookedMethod(param: MethodHookParam?) = true\n" +
                "                })\n" +
                "            XposedHelpers.findAndHookMethod(\n" +
                "                YukiHookModuleStatus::class.java.name,\n" +
                "                lpparam.classLoader,\n" +
                "                \"$GET_XPOSED_TAG_METHOD_NAME\",\n" +
                "                object : XC_MethodReplacement() {\n" +
                "                    override fun replaceHookedMethod(param: MethodHookParam?) = try {\n" +
                "                        XposedBridge::class.java.getDeclaredField(\"TAG\").apply { isAccessible = true }.get(null) as String\n" +
                "                    } catch (_: Throwable) {\n" +
                "                        \"invalid\"\n" +
                "                    }\n" +
                "                })\n" +
                "            XposedHelpers.findAndHookMethod(\n" +
                "                YukiHookModuleStatus::class.java.name,\n" +
                "                lpparam.classLoader,\n" +
                "                \"$GET_XPOSED_VERSION_METHOD_NAME\",\n" +
                "                object : XC_MethodReplacement() {\n" +
                "                    override fun replaceHookedMethod(param: MethodHookParam?) = try {\n" +
                "                        XposedBridge.getXposedVersion()\n" +
                "                    } catch (_: Throwable) {\n" +
                "                        -1\n" +
                "                    }\n" +
                "                })\n" +
                "            YukiHookXposedBridge.isModulePackageXposedEnv = true\n" +
                "        }\n" +
                "        YukiHookXposedBridge.modulePackageName = \"$modulePackageName\"\n" +
                "        YukiHookXposedBridge.callXposedLoaded(lpparam)\n" +
                "    }\n" +
                "}").toByteArray()
}