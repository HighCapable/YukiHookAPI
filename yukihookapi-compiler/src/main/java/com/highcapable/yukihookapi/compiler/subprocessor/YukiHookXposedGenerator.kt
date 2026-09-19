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
@file:Suppress("unused")

package com.highcapable.yukihookapi.compiler.subprocessor

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.highcapable.yukihookapi.compiler.DeclaredSymbol
import com.highcapable.yukihookapi.compiler.subprocessor.base.BaseSymbolProcessor
import com.highcapable.yukihookapi.compiler.subprocessor.spec.GenerateSpec
import com.highcapable.yukihookapi.compiler.subprocessor.spec.LibXposedGenerateSpec
import com.highcapable.yukihookapi.compiler.subprocessor.spec.Xposed82GenerateSpec
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LONG
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.writeTo
import java.io.File

class YukiHookXposedGenerator(environment: SymbolProcessorEnvironment) : BaseSymbolProcessor(environment) {

    private companion object {

        const val TAG = YukiHookAPIProperties.PROJECT_NAME
        const val VERSION = YukiHookAPIProperties.PROJECT_YUKIHOOKAPI_BOM_VERSION
        const val XPOSED_CLASS_SHORT_NAME = "_YukiHookXposedInit"

        const val IS_ACTIVE_METHOD_NAME = "__--"
        const val GET_EXECUTOR_API_VERSION_METHOD_NAME = "-__-"
        const val GET_EXECUTOR_NAME_METHOD_NAME = "_-_-"
        const val GET_EXECUTOR_VERSION_METHOD_NAME = "-_-_"
        const val GET_EXECUTOR_VERSION_CODE_METHOD_NAME = "___-"

        val ApiImplClass = ClassName(DeclaredSymbol.API_PACKAGE_NAME, DeclaredSymbol.API_IMPL_CLASS_NAME)
        val ModuleApplicationImplClass = ClassName(DeclaredSymbol.MODULE_APPLICATION_PACKAGE_NAME, DeclaredSymbol.MODULE_APPLICATION_IMPL_CLASS_NAME)
        val LegacyModuleStatusImplClass = ClassName(DeclaredSymbol.MODULE_STATUS_PACKAGE_NAME, DeclaredSymbol.LEGACY_MODULE_STATUS_IMPL_CLASS_NAME)
        val HookEntryClass = ClassName(DeclaredSymbol.HOOK_ENTRY_PACKAGE_NAME, DeclaredSymbol.HOOK_ENTRY_CLASS_NAME)
        val ModuleCallerClass = ClassName(DeclaredSymbol.MODULE_CALLER_PACKAGE_NAME, DeclaredSymbol.MODULE_CALLER_CLASS_NAME)
        val Xposed82EntryClass = ClassName.bestGuess(Xposed82GenerateSpec.ENTRY_CLASS_NAME)
        val LibXposedEntryClass = ClassName.bestGuess(LibXposedGenerateSpec.ENTRY_CLASS_NAME)
        val KeepClass = ClassName(DeclaredSymbol.ANDROIDX_ANNOTATION_PACKAGE_NAME, DeclaredSymbol.KEEP_ANNOTATION_CLASS_NAME)
        val LogClass = ClassName(DeclaredSymbol.ANDROID_UTIL_PACKAGE_NAME, DeclaredSymbol.LOG_CLASS_NAME)
    }

    private var isGenerated = false

    override fun startProcess(resolver: Resolver) {
        if (isGenerated) return

        if (resolver.getSymbolsWithAnnotation(Xposed82GenerateSpec.LEGACY_ANNOTATION_NAME).any()) problem(
            "`@${Xposed82GenerateSpec.LEGACY_ANNOTATION_NAME.substringAfterLast('.')}` is no longer supported. " +
                "Migrate it to `@${Xposed82GenerateSpec.ANNOTATION_NAME.substringAfterLast('.')}`."
        )

        val legacySymbols = resolver.getSymbolsWithAnnotation(Xposed82GenerateSpec.ANNOTATION_NAME).toList()
        val modernSymbols = resolver.getSymbolsWithAnnotation(LibXposedGenerateSpec.ANNOTATION_NAME).toList()

        if (legacySymbols.isEmpty() && modernSymbols.isEmpty()) return
        if (legacySymbols.size + modernSymbols.size != 1) problem(
            "Declare exactly one hook entry using either " +
                "`@${LibXposedGenerateSpec.ANNOTATION_NAME.substringAfterLast('.')}` or " +
                "`@${Xposed82GenerateSpec.ANNOTATION_NAME.substringAfterLast('.')}`, please remove the others."
        )

        val hasLibXposedRuntime = resolver.getClassDeclarationByName(resolver.getKSNameFromString(LibXposedGenerateSpec.ENTRY_CLASS_NAME)) != null
        val hasXposed82Runtime = resolver.getClassDeclarationByName(resolver.getKSNameFromString(Xposed82GenerateSpec.ENTRY_CLASS_NAME)) != null

        if (hasLibXposedRuntime == hasXposed82Runtime) problem(
            "Select exactly one runtime: `${LibXposedGenerateSpec.RUNTIME_MODULE_NAME}` " +
                "or `${Xposed82GenerateSpec.RUNTIME_MODULE_NAME}`."
        )

        val runtimeType = if (hasLibXposedRuntime) YukiHookRuntime.LIBXPOSED else YukiHookRuntime.XPOSED_82
        val annotationRuntimeType = if (modernSymbols.isNotEmpty()) YukiHookRuntime.LIBXPOSED else YukiHookRuntime.XPOSED_82
        val annotationName = when (annotationRuntimeType) {
            YukiHookRuntime.XPOSED_82 -> Xposed82GenerateSpec.ANNOTATION_NAME
            YukiHookRuntime.LIBXPOSED -> LibXposedGenerateSpec.ANNOTATION_NAME
        }

        if (annotationRuntimeType != runtimeType) {
            val expectedRuntime = when (annotationRuntimeType) {
                YukiHookRuntime.LIBXPOSED -> LibXposedGenerateSpec.RUNTIME_MODULE_NAME
                YukiHookRuntime.XPOSED_82 -> Xposed82GenerateSpec.RUNTIME_MODULE_NAME
            }
            val actualRuntime = when (runtimeType) {
                YukiHookRuntime.LIBXPOSED -> LibXposedGenerateSpec.RUNTIME_MODULE_NAME
                YukiHookRuntime.XPOSED_82 -> Xposed82GenerateSpec.RUNTIME_MODULE_NAME
            }

            problem(
                "`@${annotationName.substringAfterLast('.')}` requires $expectedRuntime, " +
                    "but $actualRuntime is on the compile classpath."
            )
        }

        val entry = (legacySymbols + modernSymbols).single() as? KSClassDeclaration ?: problem(
            "The hook entry annotation must target a class or object implementing `YukiHookXposedModule`."
        )

        val entrySuperTypes = entry.getAllSuperTypes().mapNotNull { it.declaration.qualifiedName?.asString() }.toSet()
        if (DeclaredSymbol.LEGACY_HOOK_ENTRY_CLASS in entrySuperTypes) problem(
            "The hook entry class \"${entry.simpleName.asString()}\" still implements the deprecated " +
                "`IYukiHookXposedInit`. Migrate it to `YukiHookXposedModule`."
        )
        if (DeclaredSymbol.HOOK_ENTRY_CLASS !in entrySuperTypes) problem(
            "The hook entry class \"${entry.simpleName.asString()}\" must implement `YukiHookXposedModule`."
        )

        val annotation = entry.annotations.single {
            it.annotationType.resolve().declaration.qualifiedName?.asString() == annotationName
        }
        val arguments = annotation.arguments.associate { it.name?.asString() to it.value }
        val spec = when (runtimeType) {
            YukiHookRuntime.LIBXPOSED -> LibXposedGenerateSpec(
                minApiVersion = arguments["minApiVersion"] as? Int ?: LibXposedGenerateSpec.RUNTIME_MIN_API_VERSION,
                targetApiVersion = arguments["targetApiVersion"] as? Int ?: LibXposedGenerateSpec.DEFAULT_TARGET_API_VERSION,
                scope = (arguments["scope"] as? List<*>)?.filterIsInstance<String>() ?: emptyList(),
                isStaticScope = arguments["isStaticScope"] as? Boolean ?: false,
                nativeLibraries = (arguments["nativeLibraries"] as? List<*>)?.filterIsInstance<String>() ?: emptyList()
            )
            YukiHookRuntime.XPOSED_82 -> Xposed82GenerateSpec(
                usingXposedModuleStatus = arguments["usingXposedModuleStatus"] as? Boolean ?: true
            )
        }
        val sourcePath = (arguments["sourcePath"] as? String).orEmpty().trim()

        spec.customPackageName = (arguments["modulePackageName"] as? String).orEmpty().trim()
        spec.initClassName = (arguments["entryClassName"] as? String).orEmpty().trim()

        if ((spec.customPackageName.startsWith(".") ||
                spec.customPackageName.endsWith(".") ||
                !spec.customPackageName.contains(".") ||
                spec.customPackageName.contains("..")) &&
            spec.customPackageName.isNotEmpty()
        ) problem("Invalid module package name \"${spec.customPackageName}\".")

        if (("[^\\p{L}\\p{N}_]".toRegex().containsMatchIn(spec.initClassName) ||
                true.let { for (i in 0..9) if (spec.initClassName.startsWith(i.toString())) return@let true; false }) &&
            spec.initClassName.isNotEmpty()
        ) problem("Invalid hook entry class name \"${spec.initClassName}\".")

        if ((entry.primaryConstructor?.parameters?.size ?: 0) > 0)
            problem("The hook entry class \"${entry.simpleName.asString()}\" must not have constructor parameters.")
        if (spec.initClassName == entry.simpleName.asString())
            problem("Duplicate hook entry class name \"${spec.initClassName}\".")

        spec.entryPackageName = entry.packageName.asString()
        spec.entryClassName = entry.simpleName.asString()
        spec.initClassName = spec.initClassName.ifBlank { "${spec.entryClassName}$XPOSED_CLASS_SHORT_NAME" }
        spec.isEntryClassKindOfObject = when (entry.classKind) {
            ClassKind.CLASS -> false
            ClassKind.OBJECT -> true
            else -> problem("Invalid hook entry class \"${spec.entryClassName}\" kind `${entry.classKind}`.")
        }

        generateResources(
            codePath = (entry.location as? FileLocation)?.filePath?.parseFileSeparator() ?: "",
            sourcePath = sourcePath.parseFileSeparator(),
            spec = spec
        )
    }

    /**
     * Generates the framework entry and metadata resources.
     * @param codePath the full source path of the annotated class.
     * @param sourcePath the configured source path.
     * @param spec the template generation data.
     */
    private fun generateResources(codePath: String, sourcePath: String, spec: GenerateSpec) {
        if (codePath.isBlank()) problem("Project code path not available.")
        if (sourcePath.isBlank()) problem("Project source path not available.")

        // Runtime-specific entries may live outside src/main while sharing its AndroidManifest.
        // Find the module root independently of the annotated entry's source set.
        val projectDir = generateSequence(File(codePath.parseFileSeparator()).parentFile) { it.parentFile }
            .firstOrNull { it.resolve(sourcePath).resolve("AndroidManifest.xml").isFile }
            ?: problem("Project source path \"$sourcePath\" has no AndroidManifest.xml in an enclosing module.")

        val sourceDir = projectDir.resolve(sourcePath)
        val resourcesDir = sourceDir.resolve("resources")

        if (spec is LibXposedGenerateSpec &&
            (spec.minApiVersion < LibXposedGenerateSpec.RUNTIME_MIN_API_VERSION || spec.targetApiVersion < spec.minApiVersion)
        ) problem(
            "libxposed requires minApiVersion >= ${LibXposedGenerateSpec.RUNTIME_MIN_API_VERSION} " +
                "and targetApiVersion >= minApiVersion. API 100 is unsupported."
        )

        spec.modulePackageName = parseModulePackageName(projectDir)
        if (spec.modulePackageName.isBlank() && spec.customPackageName.isBlank()) problem(
            "Cannot identify the module application ID, enable the BuildConfig feature or set `modulePackageName`."
        )
        if (spec is LibXposedGenerateSpec) {
            if (spec.scope.any { it.isBlank() || it.any(Char::isWhitespace) })
                problem("Each modern scope entry must be a nonblank package name or system.")
            if (spec.nativeLibraries.any {
                    it.isBlank() || it.any(Char::isWhitespace) || '/' in it || '\\' in it || !it.endsWith(".so")
                }) problem(
                "Each nativeLibraries entry must be a .so filename without paths or whitespace, for example libexample.so"
            )
        }

        val sourceEntryFiles = when (spec) {
            is Xposed82GenerateSpec -> listOf(sourceDir.resolve(Xposed82GenerateSpec.INIT_FILE_PATH))
            is LibXposedGenerateSpec -> listOf(
                LibXposedGenerateSpec.JAVA_INIT_FILE_PATH,
                LibXposedGenerateSpec.MODULE_PROPERTIES_FILE_PATH,
                LibXposedGenerateSpec.SCOPE_FILE_PATH,
                LibXposedGenerateSpec.NATIVE_INIT_FILE_PATH
            ).map { resourcesDir.resolve(it) }
        }.filter { it.isFile }
        if (sourceEntryFiles.isNotEmpty()) problem(
            "Xposed entry and metadata files must be generated exclusively by YukiHookAPI.\n" +
                "Remove the following files from your source directory manually before building:\n" +
                sourceEntryFiles.joinToString("\n")
        )

        spec.createResources().forEach { (path, content) ->
            codeGenerator.createNewFileByPath(Dependencies.ALL_FILES, path, extensionName = "")
                .bufferedWriter().use { it.write(content) }
        }

        generateCodeFiles(spec)
        isGenerated = true
    }

    /**
     * Generates the required class files.
     * @param spec the template generation spec.
     */
    private fun generateCodeFiles(spec: GenerateSpec) {
        if (spec.customPackageName.isNotBlank()) warn(
            "You set the customize module package name to \"${spec.customPackageName}\", " +
                "please check for yourself if it is correct."
        )
        spec.createSources().forEach { it.writeTo(codeGenerator, Dependencies.ALL_FILES) }
    }

    private fun Xposed82GenerateSpec.withModuleSuffix(type: ClassName) =
        ClassName(type.packageName, type.simpleName + "_" + modulePackageName.replace(".", "_"))

    private fun GenerateSpec.createEntryInstance() = CodeBlock.of(
        if (isEntryClassKindOfObject) "%T" else "%T()",
        ClassName(entryPackageName, entryClassName)
    )

    private fun FileSpec.Builder.addCopyrightFileComment() = addFileComment(
        """
          ${YukiHookAPIProperties.PROJECT_NAME} - ${YukiHookAPIProperties.POM_DESCRIPTION}
          Copyright (C) 2019 HighCapable
          ${YukiHookAPIProperties.PROJECT_URL}

          This file is auto generated by ${YukiHookAPIProperties.PROJECT_NAME}.
          **DO NOT EDIT THIS FILE MANUALLY**
        """.trimIndent()
    )

    private fun ClassName.sourceFileBuilder(vararg suppressions: String) = FileSpec.builder(packageName, simpleName).apply {
        addCopyrightFileComment()
        indent("    ")
        addAnnotation(AnnotationSpec.builder(Suppress::class).apply {
            addMember("%S", "ClassName")
            suppressions.forEach { addMember("%S", it) }
        }.build())
    }

    /**
     * Creates the Kotlin source files required by the selected runtime.
     * @return [List]<[FileSpec]>
     */
    private fun GenerateSpec.createSources() = buildList {
        add(createApiSource())
        add(createApplicationSource())
        if (this@createSources is Xposed82GenerateSpec && usingXposedModuleStatus) add(createLegacyModuleStatusSource())
        add(createXposedEntrySource())
    }

    /**
     * Creates the entry registration and framework metadata resources for the selected runtime.
     * @return [Map]<[String], [String]>
     */
    private fun GenerateSpec.createResources(): Map<String, String> {
        val entryName = "$entryPackageName.$initClassName"

        return when (this) {
            is LibXposedGenerateSpec -> buildMap {
                put(LibXposedGenerateSpec.JAVA_INIT_FILE_PATH, entryName)
                put(LibXposedGenerateSpec.MODULE_PROPERTIES_FILE_PATH, createModuleProperties())
                put(LibXposedGenerateSpec.SCOPE_FILE_PATH, scope.joinToString("\n"))

                if (nativeLibraries.isNotEmpty()) put(LibXposedGenerateSpec.NATIVE_INIT_FILE_PATH, nativeLibraries.joinToString("\n"))
            }
            is Xposed82GenerateSpec -> mapOf(Xposed82GenerateSpec.INIT_FILE_PATH to entryName)
        }
    }

    /**
     * Returns modern framework metadata.
     * @return [String]
     */
    private fun LibXposedGenerateSpec.createModuleProperties() =
        """
          # Generated by YukiHookAPI $VERSION
          minApiVersion=$minApiVersion
          targetApiVersion=$targetApiVersion
          staticScope=$isStaticScope
          autoHotReload=false
        """.trimIndent()

    private fun GenerateSpec.createApiSource() = ApiImplClass.sourceFileBuilder().apply {
        addType(TypeSpec.objectBuilder(ApiImplClass).apply {
            addKdoc("%L Class\n", ApiImplClass.simpleName)
            addProperty(PropertySpec.builder("legacyModuleStatusClassName", STRING).apply {
                addKdoc("Name of the generated legacy module-status target, or empty when unavailable.\n")
                getter(FunSpec.getterBuilder().apply {
                    addStatement(
                        "return %S",
                        if (this@createApiSource is Xposed82GenerateSpec && usingXposedModuleStatus)
                            withModuleSuffix(LegacyModuleStatusImplClass).canonicalName else ""
                    )
                }.build())
            }.build())
            addProperty(PropertySpec.builder("compiledTimestamp", LONG).apply {
                addKdoc("Timestamp of this generated module build.\n")
                getter(FunSpec.getterBuilder().apply {
                    addStatement("return %L", System.currentTimeMillis())
                }.build())
            }.build())
        }.build())
    }.build()

    private fun GenerateSpec.createApplicationSource() = ModuleApplicationImplClass.sourceFileBuilder().apply {
        addType(TypeSpec.objectBuilder(ModuleApplicationImplClass).apply {
            addKdoc("%L Class\n", ModuleApplicationImplClass.simpleName)
            addFunction(FunSpec.builder("callHookEntryInit").apply {
                addKdoc("Initializes the hook entry in the module application.\n")
                beginControlFlow("try")
                addStatement("%T.callOnInit(%L)", ModuleCallerClass, createEntryInstance())
                nextControlFlow("catch (_: %T)", Throwable::class)
                endControlFlow()
            }.build())
        }.build())
    }.build()

    private fun Xposed82GenerateSpec.createLegacyModuleStatusSource() = LegacyModuleStatusImplClass.sourceFileBuilder().apply {
        fun funSpec(jvmName: String, resultType: TypeName, defaultValue: CodeBlock) =
            FunSpec.builder("function" + (1000..99999).random()).apply {
                addKdoc("Returns the fallback value until the legacy framework hooks this function.\n")
                addAnnotation(JvmStatic::class)
                addAnnotation(AnnotationSpec.builder(JvmName::class).apply {
                    addMember("%S", jvmName)
                }.build())
                returns(resultType)
                addStatement("phe()")
                addStatement("return %L", defaultValue)
            }.build()

        addType(TypeSpec.objectBuilder(withModuleSuffix(LegacyModuleStatusImplClass)).apply {
            addKdoc("%L Class\n", LegacyModuleStatusImplClass.simpleName)
            addAnnotation(KeepClass)
            addFunction(funSpec(IS_ACTIVE_METHOD_NAME, BOOLEAN, CodeBlock.of("false")))
            addFunction(funSpec(GET_EXECUTOR_API_VERSION_METHOD_NAME, INT, CodeBlock.of("-1")))
            addFunction(funSpec(GET_EXECUTOR_NAME_METHOD_NAME, STRING, CodeBlock.of("%S", "unknown")))
            addFunction(funSpec(GET_EXECUTOR_VERSION_METHOD_NAME, STRING, CodeBlock.of("%S", "unknown")))
            addFunction(funSpec(GET_EXECUTOR_VERSION_CODE_METHOD_NAME, LONG, CodeBlock.of("-1L")))
            addFunction(FunSpec.builder("phe").apply {
                addModifiers(KModifier.PRIVATE)
                addAnnotation(JvmStatic::class)
                addAnnotation(AnnotationSpec.builder(JvmName::class).apply {
                    addMember("%S", "_" + (1000..99999).random())
                }.build())
                addComment("Consume a long method body.")
                addStatement(
                    "if (%T.currentTimeMillis() == 0L) %T.d(%S, %S)",
                    System::class,
                    LogClass,
                    (1000..9999).random().toString(),
                    (100000..999999).random().toString()
                )
            }.build())
        }.build())
    }.build()

    private fun GenerateSpec.createXposedEntrySource() = FileSpec.builder(entryPackageName, initClassName).apply {
        addCopyrightFileComment()
        indent("    ")
        addType(TypeSpec.classBuilder(initClassName).apply {
            addKdoc("Framework entry generated by `YukiHookAPI`.\n")
            addAnnotation(KeepClass)

            superclass(when (this@createXposedEntrySource) {
                is Xposed82GenerateSpec -> Xposed82EntryClass
                is LibXposedGenerateSpec -> LibXposedEntryClass
            })

            if (this@createXposedEntrySource is Xposed82GenerateSpec)
                addProperty(PropertySpec.builder("modulePackageName", STRING).apply {
                    addModifiers(KModifier.OVERRIDE)
                    initializer("%S", customPackageName.ifBlank { modulePackageName })
                }.build())

            addFunction(FunSpec.builder("createHookEntry").apply {
                addModifiers(KModifier.OVERRIDE)
                returns(HookEntryClass)
                addStatement("return %L", createEntryInstance())
            }.build())
        }.build())
    }.build()

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
     * Reports a fatal error and aborts processing.
     * @param msg the error message.
     * @return [Nothing]
     */
    private fun problem(msg: String): Nothing {
        val helpMsg = """
          Looking for help? Please see the documentation link below.
          English: https://highcapable.github.io/YukiHookAPI/en/config/xposed-using
          简体中文: https://highcapable.github.io/YukiHookAPI/zh-cn/config/xposed-using
        """.trimIndent()

        logger.error(message = "[$TAG] $msg\n\n$helpMsg")
        throw RuntimeException("[$TAG] $msg\n\n$helpMsg")
    }

    /**
     * Reports a warning.
     * @param msg the warning message.
     */
    private fun warn(msg: String) = logger.warn(message = "[$TAG] $msg")

    private fun String.parseFileSeparator() = replace("\\", "/")
}