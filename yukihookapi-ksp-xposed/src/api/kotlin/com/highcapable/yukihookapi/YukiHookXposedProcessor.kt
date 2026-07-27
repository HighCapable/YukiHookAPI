/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
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
import com.highcapable.yukihookapi.YukiHookXposedProcessor.Companion.KOTLIN_FILE_EXT_NAME
import com.highcapable.yukihookapi.entity.GenerateData
import com.highcapable.yukihookapi.factory.ClassName
import com.highcapable.yukihookapi.factory.PackageName
import com.highcapable.yukihookapi.factory.sources
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import java.io.File
import java.util.regex.Pattern

/**
 * KSP-based code generation processor for [YukiHookAPI].
 *
 * Generates the Xposed entry class and package metadata.
 *
 * Add the [InjectYukiHookWithXposed] annotation to enable generation.
 */
@AutoService(SymbolProcessorProvider::class)
class YukiHookXposedProcessor : SymbolProcessorProvider {

    private companion object {

        /** Tag used by the processor. */
        private const val TAG = YukiHookAPIProperties.PROJECT_NAME

        /** Fully qualified name of the annotation to process. */
        private const val ANNOTATION_NAME = "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"

        /** Suffix appended to generated Xposed entry classes. */
        private const val XPOSED_CLASS_SHORT_NAME = "_YukiHookXposedInit"

        /** Kotlin source file extension. */
        private const val KOTLIN_FILE_EXT_NAME = "kt"

        /** Java source file extension. */
        private const val JAVA_FILE_EXT_NAME = "java"
    }

    override fun create(environment: SymbolProcessorEnvironment) = object : SymbolProcessor {

        /**
         * Runs an action with the current processor environment.
         * @param ignored whether errors should be ignored, defaults to false.
         * @param env the action to run.
         */
        private fun environment(ignored: Boolean = false, env: SymbolProcessorEnvironment.() -> Unit) {
            if (ignored) runCatching { environment.apply(env) }
            else environment.apply(env)
        }

        /**
         * Reports a fatal error and aborts processing.
         * @param msg the error message.
         * @return [Nothing]
         */
        private fun SymbolProcessorEnvironment.problem(msg: String): Nothing {
            val helpMsg = "Looking for help? Please see the documentation link below\n" +
                "https://highcapable.github.io/YukiHookAPI/en/config/xposed-using"
            logger.error(message = "[$TAG] $msg\n$helpMsg")
            throw RuntimeException("[$TAG] $msg\n$helpMsg")
        }

        /**
         * Creates a source file.
         * @param fileName the file name.
         * @param packageName the package name.
         * @param content the source content.
         * @param extensionName the file extension, defaults to [KOTLIN_FILE_EXT_NAME].
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
         * Reports a warning.
         * @param msg the warning message.
         */
        private fun SymbolProcessorEnvironment.warn(msg: String) = logger.warn(message = "[$TAG] $msg")

        /**
         * Removes whitespace and line breaks, then replaces double quotes with single quotes.
         * @return [String]
         */
        private fun String.removeSpecialChars() = replace("\\s*|\t|\r|\n".toRegex(), "").replace("\"", "'")

        override fun process(resolver: Resolver) = emptyList<KSAnnotated>().let { startProcess(resolver); it }

        /**
         * Starts symbol processing.
         * @param resolver [Resolver]
         */
        private fun startProcess(resolver: Resolver) = environment {
            var isInjectOnce = true
            val data = GenerateData()
            resolver.getSymbolsWithAnnotation(ANNOTATION_NAME).apply {
                /**
                 * Finds the class to inject.
                 * @param sourcePath the configured source path.
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
                                data.entryPackageName = it.packageName.asString()
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
                        // Only the first annotated class is processed. Additional classes produce an error.
                        isInjectOnce = false
                    }
                }
                forEach {
                    it.annotations.forEach { annotation ->
                        var sourcePath = "" // Path relative to the project root.
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
         * Generates the Xposed assets entry file.
         * @param codePath the full source path of the annotated class.
         * @param sourcePath the configured source path.
         * @param data the template generation data.
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
                // Removes the entry-class name file created by older API versions.
                assetsDir.resolve("yukihookapi_init").apply { if (exists()) delete() }
                generateClassFile(data)
            } else problem(msg = "Project source path \"$sourcePath\" verify failed, is this an Android project?")
        }

        /**
         * Generates the required class files.
         * @param data the template generation data.
         */
        private fun generateClassFile(data: GenerateData) = environment(ignored = true) {
            if (data.customMPackageName.isNotBlank()) warn(
                msg = "You set the customize module package name to \"${data.customMPackageName}\", " +
                    "please check for yourself if it is correct"
            )
            // Generates YukiHookAPI_Impl.
            createCodeFile(
                fileName = ClassName.YukiHookAPI_Impl,
                packageName = PackageName.YukiHookAPI_Impl,
                content = data.sources()[ClassName.YukiHookAPI_Impl]
            )
            // Generates ModuleApplication_Impl.
            createCodeFile(
                fileName = ClassName.ModuleApplication_Impl,
                packageName = PackageName.ModuleApplication_Impl,
                content = data.sources()[ClassName.ModuleApplication_Impl]
            )
            if (data.isUsingXposedModuleStatus) {
                // Generates YukiXposedModuleStatus_Impl.
                createCodeFile(
                    fileName = ClassName.YukiXposedModuleStatus_Impl,
                    packageName = PackageName.YukiXposedModuleStatus_Impl,
                    content = data.sources()[ClassName.YukiXposedModuleStatus_Impl]
                )
                // Generates YukiXposedModuleStatus_Impl_Impl.
                createCodeFile(
                    fileName = ClassName.YukiXposedModuleStatus_Impl_Impl,
                    packageName = PackageName.YukiXposedModuleStatus_Impl,
                    content = data.sources()[ClassName.YukiXposedModuleStatus_Impl_Impl]
                )
            }
            // Generates HandlerDelegateImpl_Impl.
            createCodeFile(
                fileName = ClassName.HandlerDelegateImpl_Impl,
                packageName = PackageName.HandlerDelegateImpl_Impl,
                content = data.sources()[ClassName.HandlerDelegateImpl_Impl]
            )
            // Generates HandlerDelegateClass.
            createCodeFile(
                fileName = ClassName.HandlerDelegateClass,
                packageName = PackageName.HandlerDelegateClass,
                content = data.sources()[ClassName.HandlerDelegateClass]
            )
            // Generates IActivityManagerProxyImpl_Impl.
            createCodeFile(
                fileName = ClassName.IActivityManagerProxyImpl_Impl,
                packageName = PackageName.IActivityManagerProxyImpl_Impl,
                content = data.sources()[ClassName.IActivityManagerProxyImpl_Impl]
            )
            // Generates IActivityManagerProxyClass.
            createCodeFile(
                fileName = ClassName.IActivityManagerProxyClass,
                packageName = PackageName.IActivityManagerProxyClass,
                content = data.sources()[ClassName.IActivityManagerProxyClass]
            )
            // Generates xposed_init.
            createCodeFile(
                fileName = data.xInitClassName,
                packageName = data.entryPackageName,
                content = data.sources()[ClassName.XposedInit]
            )
            // Generates xposed_init_Impl.
            createCodeFile(
                fileName = "${data.entryClassName}_Impl",
                packageName = data.entryPackageName,
                content = data.sources()[ClassName.XposedInit_Impl]
            )
        }

        /**
         * Resolves the module package name.
         * @param projectDir the project directory.
         * @return [String] the module package name.
         */
        private fun parseModulePackageName(projectDir: File): String {
            val buildConfigFile = projectDir
                .resolve("build")
                .resolve("generated")
                .resolve("source")
                .resolve("buildConfig")
                .walkTopDown()
                .filter { it.name == "BuildConfig.java" }
                .maxByOrNull { it.lastModified() } ?: return ""
            val matcher = "APPLICATION_ID\\s*=\\s*\"([^\"]+)\"".toRegex()
            return runCatching { matcher.find(buildConfigFile.readText())?.groups?.get(1)?.value }.getOrNull() ?: ""
        }

        /**
         * Normalizes file separators.
         * @return [String]
         */
        private fun String.parseFileSeparator() = replace("\\", "/")

        /**
         * Converts this path string to a [File].
         * @return [File]
         */
        private fun String.toFile() = File(parseFileSeparator())
    }
}