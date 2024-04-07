/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2022/2/5.
 */
@file:Suppress("unused", "KDocUnresolvedReference")

package com.highcapable.yukihookapi

import com.google.auto.service.AutoService
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.highcapable.yukihookapi.bean.GenerateData
import com.highcapable.yukihookapi.factory.ClassName
import com.highcapable.yukihookapi.factory.PackageName
import com.highcapable.yukihookapi.factory.sources
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
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

    private companion object {

        /** 自动处理程序的 TAG */
        private const val TAG = YukiHookAPIProperties.PROJECT_NAME

        /** 查找的注解名称 */
        private const val ANNOTATION_NAME = "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"

        /** 插入 Xposed 尾部的名称 */
        private const val XPOSED_CLASS_SHORT_NAME = "_YukiHookXposedInit"

        /** "kt" 文件扩展名 */
        private const val KOTLIN_FILE_EXT_NAME = "kt"

        /** "java" 文件扩展名 */
        private const val JAVA_FILE_EXT_NAME = "java"
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
         * @return [Nothing]
         */
        private fun SymbolProcessorEnvironment.problem(msg: String): Nothing {
            val helpMsg = "Looking for help? Please see the documentation link below\n" +
                "- English: https://highcapable.github.io/YukiHookAPI/en/config/xposed-using\n" +
                "- Chinese (Simplified): https://highcapable.github.io/YukiHookAPI/zh-cn/config/xposed-using"
            logger.error(message = "[$TAG] $msg\n$helpMsg")
            throw RuntimeException("[$TAG] $msg\n$helpMsg")
        }
        private fun isKotlinKeyword(word: String): Boolean {
            val kotlinHardKeywords = listOf(
                "as", "as?", "break", "class", "continue", "do",
                "else", "false", "for", "fun", "if", "in", "!in", "interface",
                "is", "!is", "null", "object", "package", "return", "super",
                "this", "throw", "true", "try", "typealias", "typeof", "val",
                "var", "when", "while"
            )
            return kotlinHardKeywords.contains(word)
        }

        private fun SymbolProcessorEnvironment.preprocessPackageName(name: String): String {
            val firstPart = name.substringBefore(".")
            val remainingPart = name.substringAfter(".", "") // returns "" if "." not found

            val processedFirstPart = if (isKotlinKeyword(firstPart)) "`$firstPart`" else firstPart
            val r =  if (remainingPart.isNotEmpty()) "$processedFirstPart.$remainingPart" else processedFirstPart

            return r
        }

        /**
         * 创建代码文件
         * @param fileName 文件名
         * @param packageName 包名
         * @param content 代码内容
         * @param extensionName 文件扩展名 - 默认为 [KOTLIN_FILE_EXT_NAME]
         */
        private fun SymbolProcessorEnvironment.createCodeFile(
            fileName: String,
            packageName: String,
            content: String?,
            extensionName: String = KOTLIN_FILE_EXT_NAME
        ) = codeGenerator.createNewFile(
            dependencies = Dependencies.ALL_FILES,
            packageName, fileName, extensionName
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
        private fun String.removeSpecialChars() = replace("\\s*|\t|\r|\n".toRegex(), "").replace("\"", "'")

        override fun process(resolver: Resolver) = emptyList<KSAnnotated>().let { startProcess(resolver); it }

        /**
         * 开始作业入口
         * @param resolver [Resolver]
         */
        private fun startProcess(resolver: Resolver) = environment {
            var isInjectOnce = true
            val data = GenerateData()
            resolver.getSymbolsWithAnnotation(ANNOTATION_NAME).apply {
                /**
                 * 检索需要注入的类
                 * @param sourcePath 指定的 source 路径
                 */
                fun fetchKSClassDeclaration(sourcePath: String) {
                    asSequence().filterIsInstance<KSClassDeclaration>().forEach {
                        if (isInjectOnce) when {
                            it.superTypes.any { type -> type.element.toString() == "IYukiHookXposedInit" } -> {
                                if ((it.primaryConstructor?.parameters?.size ?: 0) > 0)
                                    problem(msg = "The hook entry class \"${it.simpleName.asString()}\" doesn't allowed any constructor parameters")
                                val xInitPatchName = data.xInitClassName.ifBlank { "${it.simpleName.asString()}$XPOSED_CLASS_SHORT_NAME" }
                                if (data.xInitClassName == it.simpleName.asString())
                                    problem(msg = "Duplicate entryClassName \"${data.xInitClassName}\"")
                                data.entryPackageName = preprocessPackageName(it.packageName.asString())
                                data.entryClassName = it.simpleName.asString()
                                data.xInitClassName = xInitPatchName
                                data.isEntryClassKindOfObject = when (it.classKind) {
                                    ClassKind.CLASS -> false
                                    ClassKind.OBJECT -> true
                                    else -> problem(msg = "Invalid hook entry class \"${it.simpleName.asString()}\" kind \"${it.classKind}\"")
                                }; generateAssetsFile(
                                    codePath = (it.location as? FileLocation?)?.filePath?.parseFileSeparator() ?: "",
                                    sourcePath = sourcePath.parseFileSeparator(),
                                    data = data
                                )
                            }
                            it.superTypes.any { type -> type.element.toString() == "YukiHookXposedInitProxy" } ->
                                problem(msg = "\"YukiHookXposedInitProxy\" was deprecated, please replace to \"IYukiHookXposedInit\"")
                            else -> problem(msg = "The hook entry class \"${it.simpleName.asString()}\" must be implements \"IYukiHookXposedInit\"")
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
                                data.xInitClassName = args.value.toString().trim()
                            if (args.name?.asString() == "isUsingXposedModuleStatus")
                                data.isUsingXposedModuleStatus = args.value as? Boolean ?: true
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
                                true.let { for (i in 0..9) if (data.entryClassName.startsWith(i.toString())) return@let true; false }) &&
                            data.entryClassName.isNotEmpty()
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
            if (codePath.isBlank()) problem(msg = "Project code path not available")
            if (sourcePath.isBlank()) problem(msg = "Project source path not available")
            val projectDir = if (codePath.contains(sourcePath))
                codePath.split(sourcePath)[0].toFile()
            else problem(msg = "Project source path \"$sourcePath\" not matched")
            val manifestFile = projectDir.resolve(sourcePath).resolve("AndroidManifest.xml")
            val assetsDir = projectDir.resolve(sourcePath).resolve("assets")
            val metaInfDir = projectDir.resolve(sourcePath).resolve("resources").resolve("META-INF")
            if (manifestFile.exists()) {
                if (assetsDir.exists().not() || assetsDir.isDirectory.not()) assetsDir.apply { delete(); mkdirs() }
                if (metaInfDir.exists().not() || metaInfDir.isDirectory.not()) metaInfDir.apply { delete(); mkdirs() }
                data.modulePackageName = parseModulePackageName(projectDir)
                if (data.modulePackageName.isBlank() && data.customMPackageName.isBlank())
                    problem(msg = "Cannot identify your Module App's package name, please make sure \"BuildConfig.java\" is generated correctly")
                assetsDir.resolve("xposed_init").writeText(text = "${data.entryPackageName}.${data.xInitClassName}")
                metaInfDir.resolve("yukihookapi_init").writeText(text = "${data.entryPackageName}.${data.entryClassName}")
                /** 移除旧版本 API 创建的入口类名称文件 */
                assetsDir.resolve("yukihookapi_init").apply { if (exists()) delete() }
                generateClassFile(data)
            } else problem(msg = "Project source path \"$sourcePath\" verify failed, is this an Android project?")
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
            /** 插入 YukiHookAPI_Impl 代码 */
            createCodeFile(
                fileName = ClassName.YukiHookAPI_Impl,
                packageName = PackageName.YukiHookAPI_Impl,
                content = data.sources()[ClassName.YukiHookAPI_Impl]
            )
            /** 插入 ModuleApplication_Impl 代码 */
            createCodeFile(
                fileName = ClassName.ModuleApplication_Impl,
                packageName = PackageName.ModuleApplication_Impl,
                content = data.sources()[ClassName.ModuleApplication_Impl]
            )
            if (data.isUsingXposedModuleStatus) {
                /** 插入 YukiXposedModuleStatus_Impl 代码 */
                createCodeFile(
                    fileName = ClassName.YukiXposedModuleStatus_Impl,
                    packageName = PackageName.YukiXposedModuleStatus_Impl,
                    content = data.sources()[ClassName.YukiXposedModuleStatus_Impl]
                )
                /** 插入 YukiXposedModuleStatus_Impl_Impl 代码 */
                createCodeFile(
                    fileName = ClassName.YukiXposedModuleStatus_Impl_Impl,
                    packageName = PackageName.YukiXposedModuleStatus_Impl,
                    content = data.sources()[ClassName.YukiXposedModuleStatus_Impl_Impl]
                )
            }
            /** 插入 HandlerDelegateImpl_Impl 代码 */
            createCodeFile(
                fileName = ClassName.HandlerDelegateImpl_Impl,
                packageName = PackageName.HandlerDelegateImpl_Impl,
                content = data.sources()[ClassName.HandlerDelegateImpl_Impl]
            )
            /** 插入 HandlerDelegateClass 代码 */
            createCodeFile(
                fileName = ClassName.HandlerDelegateClass,
                packageName = PackageName.HandlerDelegateClass,
                content = data.sources()[ClassName.HandlerDelegateClass]
            )
            /** 插入 IActivityManagerProxyImpl_Impl 代码 */
            createCodeFile(
                fileName = ClassName.IActivityManagerProxyImpl_Impl,
                packageName = PackageName.IActivityManagerProxyImpl_Impl,
                content = data.sources()[ClassName.IActivityManagerProxyImpl_Impl]
            )
            /** 插入 IActivityManagerProxyClass 代码 */
            createCodeFile(
                fileName = ClassName.IActivityManagerProxyClass,
                packageName = PackageName.IActivityManagerProxyClass,
                content = data.sources()[ClassName.IActivityManagerProxyClass]
            )
            /** 插入 xposed_init 代码 */
            createCodeFile(
                fileName = data.xInitClassName,
                packageName = data.entryPackageName,
                content = data.sources()[ClassName.XposedInit]
            )
            /** 插入 xposed_init_Impl 代码 */
            createCodeFile(
                fileName = "${data.entryClassName}_Impl",
                packageName = data.entryPackageName,
                content = data.sources()[ClassName.XposedInit_Impl]
            )
        }

        /**
         * 解析模块包名
         * @param projectDir 项目目录
         * @return [String] 模块包名
         */
        private fun parseModulePackageName(projectDir: File): String {
            val buildConfigFile = projectDir
                .resolve("build")
                .resolve("generated")
                .resolve("source")
                .resolve("buildConfig")
                .walkTopDown()
                .filter { it.name == "BuildConfig.java" }
                .sortedByDescending { it.lastModified() }
                .firstOrNull() ?: return ""
            val matcher = "APPLICATION_ID\\s*=\\s*\"([^\"]+)\"".toRegex()
            return runCatching { matcher.find(buildConfigFile.readText())?.groups?.get(1)?.value }.getOrNull() ?: ""
        }

        /**
         * 格式化文件分隔符
         * @return [String]
         */
        private fun String.parseFileSeparator() = replace("\\", "/")

        /**
         * 字符串转换为 [File]
         * @return [File]
         */
        private fun String.toFile() = File(parseFileSeparator())
    }
}
