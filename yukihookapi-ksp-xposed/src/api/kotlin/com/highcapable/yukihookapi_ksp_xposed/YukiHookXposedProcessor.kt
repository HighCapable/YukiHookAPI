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
 * This file is Created by fankes on 2022/2/5.
 */
@file:Suppress("unused", "KDocUnresolvedReference")

package com.highcapable.yukihookapi_ksp_xposed

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 这是 [YukiHookAPI] 的自动生成处理类 - 核心基于 KSP
 *
 * 可以帮你快速生成 Xposed 入口类和包名
 *
 * 你只需要添加 [InjectYukiHookWithXposed] 注解即可完美解决一切问题
 */
@AutoService(SymbolProcessorProvider::class)
class YukiHookXposedProcessor : SymbolProcessorProvider {

    companion object {

        /** 定义 Jvm 方法名 */
        private const val IS_ACTIVE_METHOD_NAME = "__--"

        /** 定义 Jvm 方法名 */
        private const val GET_XPOSED_VERSION_METHOD_NAME = "--__"

        /** 定义 Jvm 方法名 */
        private const val GET_XPOSED_TAG_METHOD_NAME = "_-_-"
    }

    override fun create(environment: SymbolProcessorEnvironment) = object : SymbolProcessor {

        /** 自动处理程序的 TAG */
        private val TAG = "YukiHookAPI"

        /** 查找的注解名称 */
        private val annotationName = "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"

        /** 插入 Xposed 尾部的名称 */
        private val xposedClassShortName = "_YukiHookXposedInit"

        /**
         * 创建一个环境方法体方便调用
         * @param ignoredError 是否忽略错误 - 默认否
         * @param env 方法体
         */
        private fun environment(ignoredError: Boolean = false, env: SymbolProcessorEnvironment.() -> Unit) {
            if (ignoredError) runCatching { environment.apply(env) }
            else environment.apply(env)
        }

        /**
         * 终止并报错
         * @param msg 错误消息
         */
        private fun SymbolProcessorEnvironment.error(msg: String) {
            val helpMsg = "Looking for help? see https://fankes.github.io/YukiHookAPI/#/config/xposed-using"
            logger.error(message = "[$TAG] $msg\n$helpMsg")
            throw RuntimeException("[$TAG] $msg\n$helpMsg")
        }

        /**
         * 发出警告
         * @param msg 错误消息
         */
        private fun SymbolProcessorEnvironment.warn(msg: String) = logger.warn(message = "[$TAG] $msg")

        override fun process(resolver: Resolver) = emptyList<KSAnnotated>().let {
            injectProcess(resolver)
            it
        }

        /**
         * 开始作业入口
         * @param resolver [Resolver]
         */
        private fun injectProcess(resolver: Resolver) = environment {
            var injectOnce = true
            resolver.getSymbolsWithAnnotation(annotationName).apply {
                /**
                 * 检索需要注入的类
                 * @param sourcePath 指定的 source 路径
                 * @param modulePackageName 模块包名
                 */
                fun fetchKSClassDeclaration(sourcePath: String, modulePackageName: String) {
                    asSequence().filterIsInstance<KSClassDeclaration>().forEach {
                        if (injectOnce)
                            // 允许无序继承其它类或接口
                            if (it.superTypes.any { type -> type.element.toString() == "YukiHookXposedInitProxy" }) {
                                injectAssets(
                                    codePath = (it.location as? FileLocation?)?.filePath ?: "",
                                    sourcePath = sourcePath,
                                    packageName = it.packageName.asString(),
                                    className = it.simpleName.asString(),
                                )
                                injectClass(it.packageName.asString(), it.simpleName.asString(), modulePackageName)
                            } else error(msg = "HookEntryClass \"${it.simpleName.asString()}\" must be implements YukiHookXposedInitProxy")
                        else error(msg = "\"@InjectYukiHookWithXposed\" only can be use in once times")
                        /** 仅处理第一个标记的类 - 再次处理将拦截并报错 */
                        injectOnce = false
                    }
                }
                forEach {
                    it.annotations.forEach { e ->
                        var sourcePath = ""
                        var modulePackageName = ""
                        e.arguments.forEach { pease ->
                            if (pease.name?.asString() == "sourcePath")
                                sourcePath = pease.value.toString()
                            if (pease.name?.asString() == "modulePackageName")
                                modulePackageName = pease.value.toString()
                        }
                        if ((modulePackageName.startsWith(".") ||
                                    modulePackageName.endsWith(".") ||
                                    modulePackageName.contains(".").not() ||
                                    modulePackageName.contains("..")) &&
                            modulePackageName.isNotEmpty()
                        ) error(msg = "Invalid Module Package name \"$modulePackageName\"")
                        else fetchKSClassDeclaration(sourcePath, modulePackageName)
                    }
                }
            }
        }

        /**
         * 自动生成 Xposed assets 入口文件
         * @param codePath 注解类的完整代码文件路径
         * @param sourcePath 指定的 source 路径
         * @param packageName 包名
         * @param className 类名
         */
        private fun injectAssets(codePath: String, sourcePath: String, packageName: String, className: String) =
            environment {
                if (codePath.isBlank()) error(msg = "Project CodePath not available")
                if (sourcePath.isBlank()) error(msg = "Project SourcePath not available")
                /**
                 * Gradle 在这里自动处理了 Windows 和 Unix 下的反斜杠路径问题
                 * 为了防止万一还是做了一下反斜杠处理防止旧版本不支持此用法
                 */
                val separator = when {
                    codePath.contains("\\") -> "\\"
                    codePath.contains("/") -> "/"
                    else -> kotlin.error("Unix File Separator unknown")
                }
                val projectPath = when {
                    codePath.contains("\\") -> sourcePath.replace("/", "\\")
                    codePath.contains("/") -> sourcePath.replace("\\", "/")
                    else -> kotlin.error("Unix File Separator unknown")
                }.let {
                    if (codePath.contains(it))
                        codePath.split(it)[0] + it
                    else error(msg = "Project Source Path \"$it\" not matched")
                }
                File("$projectPath${separator}assets").also { assFile ->
                    if (File("$projectPath${separator}AndroidManifest.xml").exists()) {
                        if (assFile.exists().not() || assFile.isDirectory.not()) {
                            assFile.delete()
                            assFile.mkdirs()
                        }
                        File("${assFile.absolutePath}${separator}xposed_init")
                            .writeText(text = "$packageName.$className$xposedClassShortName")
                        File("${assFile.absolutePath}${separator}yukihookapi_init")
                            .writeText(text = "$packageName.$className")
                    } else error(msg = "Project Source Path \"$sourcePath\" verify failed! Is this an Android Project?")
                }
            }

        /**
         * 注入并生成指定类
         * @param packageName 包名
         * @param className 类名
         * @param modulePackageName 模块包名
         */
        private fun injectClass(packageName: String, className: String, modulePackageName: String) =
            environment(ignoredError = true) {
                if (modulePackageName.isNotBlank()) warn(msg = "You set the customize module package name to \"$modulePackageName\", please check for yourself if it is correct")
                val realPackageName =
                    modulePackageName.ifBlank {
                        if (packageName.contains(".hook.") || packageName.endsWith(".hook"))
                            packageName.split(".hook")[0]
                        else error(msg = "Cannot identify your App's package name, please manually configure the package name")
                    }
                codeGenerator.createNewFile(
                    Dependencies.ALL_FILES,
                    packageName,
                    fileName = "$className$xposedClassShortName"
                ).apply {
                    /** 插入 xposed_init 代码 */
                    write(
                        ("package $packageName\n" +
                                "\n" +
                                "import androidx.annotation.Keep\n" +
                                "import com.highcapable.yukihookapi.annotation.YukiGenerateApi\n" +
                                "import com.highcapable.yukihookapi.hook.log.loggerE\n" +
                                "import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus\n" +
                                "import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookXposedBridge\n" +
                                "import de.robv.android.xposed.*\n" +
                                "import de.robv.android.xposed.callbacks.XC_LoadPackage\n" +
                                "import $packageName.$className\n" +
                                "\n" +
                                "/**\n" +
                                " * XposedInit Inject Class\n" +
                                " *\n" +
                                " * Compiled from YukiHookXposedProcessor\n" +
                                " *\n" +
                                " * HookEntryClass: [$className]\n" +
                                " *\n" +
                                " * Generate Date: ${SimpleDateFormat.getDateTimeInstance().format(Date())}\n" +
                                " *\n" +
                                " * Powered by YukiHookAPI (C) HighCapable 2022\n" +
                                " *\n" +
                                " * Project Address: https://github.com/fankes/YukiHookAPI\n" +
                                " */\n" +
                                "@Keep\n" +
                                "@YukiGenerateApi\n" +
                                "class $className$xposedClassShortName : IXposedHookZygoteInit, IXposedHookLoadPackage {\n" +
                                "\n" +
                                "    private val proxy = HookEntry()\n" +
                                "    private var startupFailed = false\n" +
                                "\n" +
                                "    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {\n" +
                                "        if (startupParam == null) return\n" +
                                "        try {\n" +
                                "            proxy.onStartup(startupParam)\n" +
                                "            if (YukiHookXposedBridge.isXposedCallbackSetUp) {\n" +
                                "                loggerE(\n" +
                                "                    tag = \"YukiHookAPI\",\n" +
                                "                    msg = \"You cannot loading a hooker in \\\"onStartup\\\" method! Aborted\"\n" +
                                "                )\n" +
                                "                startupFailed = true\n" +
                                "            }\n" +
                                "        } catch (e: Throwable) {\n" +
                                "            loggerE(tag = \"YukiHookAPI\", msg = \"YukiHookAPI try to load HookEntryClass failed\", e = e)\n" +
                                "        }\n" +
                                "    }\n" +
                                "\n" +
                                "    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {\n" +
                                "        if (lpparam == null || startupFailed) return\n" +
                                "        try {\n" +
                                "            proxy.apply {\n" +
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
                                "        if (lpparam.packageName == \"$realPackageName\") {\n" +
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
                                "        YukiHookXposedBridge.modulePackageName = \"$realPackageName\"\n" +
                                "        YukiHookXposedBridge.callXposedLoaded(lpparam)\n" +
                                "    }\n" +
                                "}").toByteArray()
                    )
                    flush()
                    close()
                }
            }
    }
}