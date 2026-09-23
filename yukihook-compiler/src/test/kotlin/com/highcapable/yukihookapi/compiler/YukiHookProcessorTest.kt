/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.compiler

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSName
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Ensures annotation/runtime mismatches fail before generating any files.
 */
class YukiHookProcessorTest {

    private companion object {
        const val DEPRECATED_LEGACY_ANNOTATION = "com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed"
        const val LEGACY_ANNOTATION = "com.highcapable.yukihookapi.annotation.xposed.YukiHookXposed82Entry"
        const val MODERN_ANNOTATION = "com.highcapable.yukihookapi.annotation.xposed.YukiHookLibXposedEntry"
        const val LEGACY_RUNTIME = "com.highcapable.yukihookapi.runtime.xposed82.entry.Xposed82Entry"
        const val MODERN_RUNTIME = "com.highcapable.yukihookapi.runtime.libxposed.entry.LibXposedEntry"
    }

    private val generatedFiles = mutableListOf<String>()
    private val errors = mutableListOf<String>()
    private val entry = proxy<KSAnnotated> { name, _ -> error("Entry must not be inspected before validation: $name") }
    private val processor = YukiHookProcessor().create(
        SymbolProcessorEnvironment(
            options = emptyMap(),
            kotlinVersion = KotlinVersion.CURRENT,
            codeGenerator = proxy<CodeGenerator> { name, _ ->
                generatedFiles += name
                error("No file generation expected: $name")
            },
            logger = proxy<KSPLogger> { name, args ->
                if (name == "error") errors += args[0] as String
                Unit
            }
        )
    )

    @Test
    fun legacyAnnotationRejectsModernRuntime() = assertRejected(
        resolver(legacyEntries = 1, runtimes = setOf(MODERN_RUNTIME)),
        "@YukiHookXposed82Entry", "requires yukihook-runtime-xposed82", "but yukihook-runtime-libxposed"
    )

    @Test
    fun modernAnnotationRejectsLegacyRuntime() = assertRejected(
        resolver(modernEntries = 1, runtimes = setOf(LEGACY_RUNTIME)),
        "@YukiHookLibXposedEntry", "requires yukihook-runtime-libxposed", "but yukihook-runtime-xposed82"
    )

    @Test
    fun legacyAnnotationRequiresRuntime() = assertRejected(
        resolver(legacyEntries = 1),
        "Select exactly one runtime"
    )

    @Test
    fun modernAnnotationRequiresRuntime() = assertRejected(
        resolver(modernEntries = 1),
        "Select exactly one runtime"
    )

    @Test
    fun legacyAnnotationRejectsBothRuntimes() = assertRejected(
        resolver(legacyEntries = 1, runtimes = setOf(LEGACY_RUNTIME, MODERN_RUNTIME)),
        "Select exactly one runtime"
    )

    @Test
    fun modernAnnotationRejectsBothRuntimes() = assertRejected(
        resolver(modernEntries = 1, runtimes = setOf(LEGACY_RUNTIME, MODERN_RUNTIME)),
        "Select exactly one runtime"
    )

    @Test
    fun duplicateLegacyEntriesFailBeforeGeneration() = assertRejected(
        resolver(legacyEntries = 2, runtimes = setOf(LEGACY_RUNTIME)),
        "Declare exactly one hook entry"
    )

    @Test
    fun duplicateModernEntriesFailBeforeGeneration() = assertRejected(
        resolver(modernEntries = 2, runtimes = setOf(MODERN_RUNTIME)),
        "Declare exactly one hook entry"
    )

    @Test
    fun bothAnnotationsOnSameEntryFailBeforeGeneration() = assertRejected(
        resolver(legacyEntries = 1, modernEntries = 1, runtimes = setOf(LEGACY_RUNTIME)),
        "Declare exactly one hook entry"
    )

    @Test
    fun noAnnotationDoesNotGenerateAnEntry() {
        Assert.assertTrue(processor.process(resolver()).isEmpty())
        Assert.assertTrue(errors.isEmpty())
        Assert.assertTrue(generatedFiles.isEmpty())
    }

    private fun assertRejected(resolver: Resolver, vararg details: String) {
        val failure = Assert.assertThrows(RuntimeException::class.java) { processor.process(resolver) }
        details.forEach { Assert.assertTrue("Missing '$it' in: ${failure.message}", failure.message.orEmpty().contains(it)) }
        Assert.assertEquals(listOf(failure.message), errors)
        Assert.assertTrue(generatedFiles.isEmpty())
    }

    private fun resolver(legacyEntries: Int = 0, modernEntries: Int = 0, runtimes: Set<String> = emptySet()) =
        proxy<Resolver> { name, args ->
            when (name) {
                "getSymbolsWithAnnotation" -> List(
                    when (args[0]) {
                        DEPRECATED_LEGACY_ANNOTATION -> 0
                        LEGACY_ANNOTATION -> legacyEntries
                        MODERN_ANNOTATION -> modernEntries
                        else -> error("Unexpected annotation: ${args[0]}")
                    }
                ) { entry }.asSequence()
                "getKSNameFromString" -> proxy<KSName> { method, _ ->
                    check(method == "asString")
                    args[0]
                }
                "getClassDeclarationByName" -> if ((args[0] as KSName).asString() in runtimes)
                    proxy<KSClassDeclaration> { method, _ -> error("Runtime declaration must not be inspected: $method") }
                else null
                else -> error("Unexpected resolver call: $name")
            }
        }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> proxy(crossinline invoke: (String, Array<out Any?>) -> Any?) =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, args ->
            invoke(method.name, args.orEmpty())
        } as T
}