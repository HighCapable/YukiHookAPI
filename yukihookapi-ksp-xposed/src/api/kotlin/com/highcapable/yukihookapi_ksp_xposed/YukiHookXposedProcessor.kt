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
import com.highcapable.yukihookapi_ksp_xposed.sources.CodeSourceFileTemplate
import java.io.File
import java.util.*
import java.util.regex.Pattern

/**
 * 这是 [YukiHookAPI] 的自动生成处理类 - 核心基于 KSP
 *
 * 可以帮你快速生成 Xposed 入口类和包名
 *
 * 你只需要添加 [InjectYukiHookWithXposed] 注解即可完美解决一切问题
 */
@AutoService(SymbolProcessorProvider::class)
class YukiHookXposedProcessor : SymbolProcessorProvider {

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
        private fun SymbolProcessorEnvironment.problem(msg: String) {
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
            var isInjectOnce = true
            resolver.getSymbolsWithAnnotation(annotationName).apply {
                /**
                 * 检索需要注入的类
                 * @param sourcePath 指定的 source 路径
                 * @param modulePackageName 模块包名
                 * @param xInitClassName xposed_init 入口类名
                 * @param isUsingResourcesHook 是否启用 Resources Hook
                 */
                fun fetchKSClassDeclaration(
                    sourcePath: String,
                    modulePackageName: String,
                    xInitClassName: String,
                    isUsingResourcesHook: Boolean
                ) {
                    asSequence().filterIsInstance<KSClassDeclaration>().forEach {
                        if (isInjectOnce) when {
                            it.superTypes.any { type -> type.element.toString() == "IYukiHookXposedInit" } -> {
                                val xcName = xInitClassName.ifBlank { "${it.simpleName.asString()}$xposedClassShortName" }
                                if (xInitClassName == it.simpleName.asString()) problem(msg = "Duplicate entryClassName \"$xInitClassName\"")
                                injectAssets(
                                    codePath = (it.location as? FileLocation?)?.filePath ?: "",
                                    sourcePath = sourcePath,
                                    packageName = it.packageName.asString(),
                                    entryClassName = it.simpleName.asString(),
                                    xInitClassName = xcName
                                )
                                injectClass(it.packageName.asString(), modulePackageName, it.simpleName.asString(), xcName, isUsingResourcesHook)
                            }
                            it.superTypes.any { type -> type.element.toString() == "YukiHookXposedInitProxy" } ->
                                problem(msg = "\"YukiHookXposedInitProxy\" was deprecated, please replace to \"IYukiHookXposedInit\"")
                            else -> problem(msg = "HookEntryClass \"${it.simpleName.asString()}\" must be implements \"IYukiHookXposedInit\"")
                        }
                        else problem(msg = "\"@InjectYukiHookWithXposed\" only can be use in once times")
                        /** 仅处理第一个标记的类 - 再次处理将拦截并报错 */
                        isInjectOnce = false
                    }
                }
                forEach {
                    it.annotations.forEach { e ->
                        var sourcePath = "" // 项目相对路径
                        var modulePackageName = "" // 模块包名
                        var entryClassName = "" // xposed_init 入口类名
                        var isUsingResourcesHook = false // 是否启用 Resources Hook
                        e.arguments.forEach { pease ->
                            if (pease.name?.asString() == "sourcePath")
                                sourcePath = pease.value.toString().trim()
                            if (pease.name?.asString() == "modulePackageName")
                                modulePackageName = pease.value.toString().trim()
                            if (pease.name?.asString() == "entryClassName")
                                entryClassName = pease.value.toString().trim()
                            if (pease.name?.asString() == "isUsingResourcesHook")
                                isUsingResourcesHook = pease.value as Boolean
                        }
                        if ((modulePackageName.startsWith(".") ||
                                    modulePackageName.endsWith(".") ||
                                    modulePackageName.contains(".").not() ||
                                    modulePackageName.contains("..")) &&
                            modulePackageName.isNotEmpty()
                        ) problem(msg = "Invalid modulePackageName \"$modulePackageName\"")
                        if ((Pattern.compile("[*,.:~`'\"|/\\\\?!^()\\[\\]{}%@#$&\\-+=<>]").matcher(entryClassName).find() ||
                                    true.let { for (i in 0..9) if (entryClassName.startsWith(i.toString())) return@let true;false })
                            && entryClassName.isNotEmpty()
                        ) problem(msg = "Invalid entryClassName \"$entryClassName\"")
                        else fetchKSClassDeclaration(sourcePath, modulePackageName, entryClassName, isUsingResourcesHook)
                    }
                }
            }
        }

        /**
         * 自动生成 Xposed assets 入口文件
         * @param codePath 注解类的完整代码文件路径
         * @param sourcePath 指定的 source 路径
         * @param packageName 包名
         * @param entryClassName 入口类名
         * @param xInitClassName xposed_init 入口类名
         */
        private fun injectAssets(codePath: String, sourcePath: String, packageName: String, entryClassName: String, xInitClassName: String) =
            environment {
                if (codePath.isBlank()) problem(msg = "Project CodePath not available")
                if (sourcePath.isBlank()) problem(msg = "Project SourcePath not available")
                /**
                 * Gradle 在这里自动处理了 Windows 和 Unix 下的反斜杠路径问题
                 * 为了防止万一还是做了一下反斜杠处理防止旧版本不支持此用法
                 */
                val separator = when {
                    codePath.contains("\\") -> "\\"
                    codePath.contains("/") -> "/"
                    else -> error("Unix File Separator unknown")
                }
                val projectPath = when {
                    codePath.contains("\\") -> sourcePath.replace("/", "\\")
                    codePath.contains("/") -> sourcePath.replace("\\", "/")
                    else -> error("Unix File Separator unknown")
                }.let {
                    if (codePath.contains(it))
                        codePath.split(it)[0] + it
                    else problem(msg = "Project Source Path \"$it\" not matched")
                }
                File("$projectPath${separator}assets").also { assFile ->
                    if (File("$projectPath${separator}AndroidManifest.xml").exists()) {
                        if (assFile.exists().not() || assFile.isDirectory.not()) {
                            assFile.delete()
                            assFile.mkdirs()
                        }
                        File("${assFile.absolutePath}${separator}xposed_init")
                            .writeText(text = "$packageName.$xInitClassName")
                        File("${assFile.absolutePath}${separator}yukihookapi_init")
                            .writeText(text = "$packageName.$entryClassName")
                    } else problem(msg = "Project Source Path \"$sourcePath\" verify failed! Is this an Android Project?")
                }
            }

        /**
         * 注入并生成指定类
         * @param packageName 包名
         * @param modulePackageName 模块包名
         * @param entryClassName 入口类名
         * @param xInitClassName xposed_init 入口类名
         * @param isUsingResourcesHook 是否启用 Resources Hook
         */
        private fun injectClass(
            packageName: String,
            modulePackageName: String,
            entryClassName: String,
            xInitClassName: String,
            isUsingResourcesHook: Boolean
        ) = environment(ignoredError = true) {
            if (modulePackageName.isNotBlank())
                warn(msg = "You set the customize module package name to \"$modulePackageName\", please check for yourself if it is correct")
            val fModulePackageName = modulePackageName.ifBlank {
                if (packageName.contains(other = ".hook.") || packageName.endsWith(suffix = ".hook"))
                    packageName.split(".hook")[0]
                else error("Cannot identify your Module App's package name, please manually configure the package name")
            }
            val mdAppInjectPackageName = "com.highcapable.yukihookapi.hook.xposed.application.inject"
            val ykBridgeInjectPackageName = "com.highcapable.yukihookapi.hook.xposed.bridge.inject"
            /** 插入 ModuleApplication_Injector 代码 */
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = mdAppInjectPackageName,
                fileName = "ModuleApplication_Injector"
            ).apply {
                write(CodeSourceFileTemplate.getModuleApplicationInjectorFileByteArray(mdAppInjectPackageName, packageName, entryClassName))
                flush()
                close()
            }
            /** 插入 YukiHookBridge_Injector 代码 */
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = ykBridgeInjectPackageName,
                fileName = "YukiHookBridge_Injector"
            ).apply {
                write(CodeSourceFileTemplate.getYukiHookBridgeInjectorFileByteArray(ykBridgeInjectPackageName))
                flush()
                close()
            }
            /** 插入 xposed_init 代码 */
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = packageName,
                fileName = xInitClassName
            ).apply {
                write(CodeSourceFileTemplate.getXposedInitFileByteArray(packageName, entryClassName, xInitClassName, isUsingResourcesHook))
                flush()
                close()
            }
            /** 插入 xposed_init_Impl 代码 */
            codeGenerator.createNewFile(
                dependencies = Dependencies.ALL_FILES,
                packageName = packageName,
                fileName = "${entryClassName}_Impl"
            ).apply {
                write(CodeSourceFileTemplate.getXposedInitImplFileByteArray(packageName, fModulePackageName, entryClassName))
                flush()
                close()
            }
        }
    }
}