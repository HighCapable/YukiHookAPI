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
import com.highcapable.yukihookapi_ksp_xposed.bean.GenerateData
import com.highcapable.yukihookapi_ksp_xposed.factory.sources
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.File
import java.util.*
import java.util.regex.Pattern
import javax.xml.parsers.DocumentBuilderFactory

/**
 * 这是 [YukiHookAPI] 的自动生成处理类 - 核心基于 KSP
 *
 * 可以帮你快速生成 Xposed 入口类和包名
 *
 * 你只需要添加 [InjectYukiHookWithXposed] 注解即可完美解决一切问题
 */
@AutoService(SymbolProcessorProvider::class)
class YukiHookXposedProcessor : SymbolProcessorProvider {

    private companion object {

        /** 自动处理程序的 TAG */
        private const val TAG = "YukiHookAPI"

        /** 查找的注解名称 */
        private const val annotationName = "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"

        /** 插入 Xposed 尾部的名称 */
        private const val xposedClassShortName = "_YukiHookXposedInit"
    }

    override fun create(environment: SymbolProcessorEnvironment) = object : SymbolProcessor {

        /**
         * 创建一个环境方法体方便调用
         * @param ignored 是否忽略错误 - 默认否
         * @param env 方法体
         */
        private fun environment(ignored: Boolean = false, env: SymbolProcessorEnvironment.() -> Unit) {
            if (ignored) runCatching { environment.apply(env) }
            else environment.apply(env)
        }

        /**
         * 终止并报错
         * @param msg 错误消息
         */
        private fun SymbolProcessorEnvironment.problem(msg: String) {
            val helpMsg = "Looking for help? Please see the documentation link below\n" +
                    "- English: https://fankes.github.io/YukiHookAPI/en/config/xposed-using\n" +
                    "- Chinese(Simplified): https://fankes.github.io/YukiHookAPI/zh-cn/config/xposed-using"
            logger.error(message = "[$TAG] $msg\n$helpMsg")
            throw RuntimeException("[$TAG] $msg\n$helpMsg")
        }

        /**
         * 创建代码文件 - 类型 kt
         * @param fileName 文件名
         * @param packageName 包名
         * @param content 代码内容
         */
        private fun SymbolProcessorEnvironment.createCodeFile(fileName: String, packageName: String, content: String?) =
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName, fileName
            ).apply { content?.toByteArray()?.let { write(it) }; flush() }.close()

        /**
         * 发出警告
         * @param msg 错误消息
         */
        private fun SymbolProcessorEnvironment.warn(msg: String) = logger.warn(message = "[$TAG] $msg")

        /**
         * 移除字符串中的空格与换行符并将双引号替换为单引号
         * @return [String]
         */
        private fun String.removeSpecialChars() = replace("\\s*|\t|\r|\n".toRegex(), replacement = "").replace(oldValue = "\"", newValue = "'")

        override fun process(resolver: Resolver) = emptyList<KSAnnotated>().let { startProcess(resolver); it }

        /**
         * 开始作业入口
         * @param resolver [Resolver]
         */
        private fun startProcess(resolver: Resolver) = environment {
            var isInjectOnce = true
            val data = GenerateData()
            resolver.getSymbolsWithAnnotation(annotationName).apply {
                /**
                 * 检索需要注入的类
                 * @param sourcePath 指定的 source 路径
                 */
                fun fetchKSClassDeclaration(sourcePath: String) {
                    asSequence().filterIsInstance<KSClassDeclaration>().forEach {
                        if (isInjectOnce) when {
                            it.superTypes.any { type -> type.element.toString() == "IYukiHookXposedInit" } -> {
                                val xInitPatchName = data.xInitClassName.ifBlank { "${it.simpleName.asString()}$xposedClassShortName" }
                                if (data.xInitClassName == it.simpleName.asString())
                                    problem(msg = "Duplicate entryClassName \"${data.xInitClassName}\"")
                                data.entryPackageName = it.packageName.asString()
                                data.entryClassName = it.simpleName.asString()
                                data.xInitClassName = xInitPatchName
                                generateAssetsFile(codePath = (it.location as? FileLocation?)?.filePath ?: "", sourcePath = sourcePath, data)
                            }
                            it.superTypes.any { type -> type.element.toString() == "YukiHookXposedInitProxy" } ->
                                problem(msg = "\"YukiHookXposedInitProxy\" was deprecated, please replace to \"IYukiHookXposedInit\"")
                            else -> problem(msg = "HookEntryClass \"${it.simpleName.asString()}\" must be implements \"IYukiHookXposedInit\"")
                        } else problem(msg = "\"@InjectYukiHookWithXposed\" only can be use in once times")
                        /** 仅处理第一个标记的类 - 再次处理将拦截并报错 */
                        isInjectOnce = false
                    }
                }
                forEach {
                    it.annotations.forEach { annotation ->
                        var sourcePath = "" // 项目相对路径
                        annotation.arguments.forEach { args ->
                            if (args.name?.asString() == "sourcePath")
                                sourcePath = args.value.toString().trim()
                            if (args.name?.asString() == "modulePackageName")
                                data.customMPackageName = args.value.toString().trim()
                            if (args.name?.asString() == "entryClassName")
                                data.entryClassName = args.value.toString().trim()
                            if (args.name?.asString() == "isUsingResourcesHook")
                                data.isUsingResourcesHook = args.value as? Boolean ?: true
                        }
                        if ((data.customMPackageName.startsWith(".") ||
                                    data.customMPackageName.endsWith(".") ||
                                    data.customMPackageName.contains(".").not() ||
                                    data.customMPackageName.contains("..")) &&
                            data.customMPackageName.isNotEmpty()
                        ) problem(msg = "Invalid modulePackageName \"${data.customMPackageName}\"")
                        if ((Pattern.compile("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-+=<>]").matcher(data.entryClassName).find() ||
                                    true.let { for (i in 0..9) if (data.entryClassName.startsWith(i.toString())) return@let true; false })
                            && data.entryClassName.isNotEmpty()
                        ) problem(msg = "Invalid entryClassName \"${data.entryClassName}\"")
                        else fetchKSClassDeclaration(sourcePath)
                    }
                }
            }
        }

        /**
         * 自动生成 Xposed assets 入口文件
         * @param codePath 注解类的完整代码文件路径
         * @param sourcePath 指定的 source 路径
         * @param data 生成的模板数据
         */
        private fun generateAssetsFile(codePath: String, sourcePath: String, data: GenerateData) = environment {
            if (codePath.isBlank()) problem(msg = "Project CodePath not available")
            if (sourcePath.isBlank()) problem(msg = "Project SourcePath not available")
            /**
             * Gradle 在这里自动处理了 Windows 和 Unix 下的反斜杠路径问题
             *
             * 为了防止万一还是做了一下反斜杠处理防止旧版本不支持此用法
             */
            val separator = when {
                codePath.contains("\\") -> "\\"
                codePath.contains("/") -> "/"
                else -> error("Unix File Separator unknown")
            }
            var rootPath = ""
            val projectPath = when {
                codePath.contains("\\") -> sourcePath.replace("/", "\\")
                codePath.contains("/") -> sourcePath.replace("\\", "/")
                else -> error("Unknown Unix File Separator")
            }.let {
                if (codePath.contains(it))
                    codePath.split(it)[0].apply { rootPath = this } + it
                else problem(msg = "Project Source Path \"$it\" not matched")
            }
            val gradleFile = File("$rootPath${separator}build.gradle")
            val gradleKtsFile = File("$rootPath${separator}build.gradle.kts")
            val assetsFile = File("$projectPath${separator}assets")
            val manifestFile = File("$projectPath${separator}AndroidManifest.xml")
            if (manifestFile.exists()) {
                if (assetsFile.exists().not() || assetsFile.isDirectory.not()) assetsFile.apply { delete(); mkdirs() }
                data.modulePackageName = parseModulePackageName(manifestFile, gradleFile, gradleKtsFile)
                if (data.modulePackageName.isBlank() && data.customMPackageName.isBlank())
                    problem(msg = "Cannot identify your Module App's package name, tried AndroidManifest.xml, build.gradle and build.gradle.kts")
                File("${assetsFile.absolutePath}${separator}xposed_init")
                    .writeText(text = "${data.entryPackageName}.${data.xInitClassName}")
                File("${assetsFile.absolutePath}${separator}yukihookapi_init")
                    .writeText(text = "${data.entryPackageName}.${data.entryClassName}")
                generateClassFile(data)
            } else problem(msg = "Project Source Path \"$sourcePath\" verify failed! Is this an Android Project?")
        }

        /**
         * 自动生成指定类文件
         * @param data 生成的模板数据
         */
        private fun generateClassFile(data: GenerateData) = environment(ignored = true) {
            if (data.customMPackageName.isNotBlank()) warn(
                msg = "You set the customize module package name to \"${data.customMPackageName}\", " +
                        "please check for yourself if it is correct"
            )
            val mdAppInjectPackageName = "com.highcapable.yukihookapi.hook.xposed.application.inject"
            val ykBridgeInjectPackageName = "com.highcapable.yukihookapi.hook.xposed.bridge.inject"
            /** 插入 ModuleApplication_Injector 代码 */
            createCodeFile(
                fileName = "ModuleApplication_Injector",
                packageName = mdAppInjectPackageName,
                content = data.apply { injectPackageName = mdAppInjectPackageName }.sources()["ModuleApplication_Injector"]
            )
            /** 插入 YukiHookBridge_Injector 代码 */
            createCodeFile(
                fileName = "YukiHookBridge_Injector",
                packageName = ykBridgeInjectPackageName,
                content = data.apply { injectPackageName = ykBridgeInjectPackageName }.sources()["YukiHookBridge_Injector"]
            )
            /** 插入 xposed_init 代码 */
            createCodeFile(
                fileName = data.xInitClassName,
                packageName = data.entryPackageName,
                content = data.sources()["xposed_init"]
            )
            /** 插入 xposed_init_Impl 代码 */
            createCodeFile(
                fileName = "${data.entryClassName}_Impl",
                packageName = data.entryPackageName,
                content = data.sources()["xposed_init_Impl"]
            )
        }

        /**
         * 解析模块包名
         * @param manifestFile AndroidManifest.xml 文件
         * @param gradleFile build.gradle 文件
         * @param gradleKtsFile build.gradle.kts 文件
         * @return [String] 模块包名
         */
        private fun parseModulePackageName(manifestFile: File, gradleFile: File, gradleKtsFile: File) = when {
            gradleFile.exists() -> runCatching {
                gradleFile.readText()
                    .removeSpecialChars()
                    .split("namespace'")[1]
                    .split("'")[0]
            }.getOrNull()
            gradleKtsFile.exists() -> runCatching {
                gradleKtsFile.readText()
                    .removeSpecialChars()
                    .replace(oldValue = "varnamespace", newValue = "")
                    .replace(oldValue = "valnamespace", newValue = "")
                    .split("namespace='")[1]
                    .split("'")[0]
            }.getOrNull()
            else -> null
        } ?: runCatching {
            DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(manifestFile).let { document ->
                document.getElementsByTagName("manifest").let { nodeList ->
                    nodeList.item(0).let { node ->
                        if (node.nodeType == Node.ELEMENT_NODE)
                            (node as? Element?)?.getAttribute("package") ?: ""
                        else ""
                    }
                }
            }
        }.getOrNull() ?: ""
    }
}