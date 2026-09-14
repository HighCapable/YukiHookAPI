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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.compiler.subprocessor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.highcapable.yukihookapi.compiler.subprocessor.spec.GenerateSpec
import com.highcapable.yukihookapi.compiler.subprocessor.spec.LibXposedGenerateSpec
import com.highcapable.yukihookapi.compiler.subprocessor.spec.Xposed82GenerateSpec
import com.squareup.kotlinpoet.FileSpec
import org.junit.Assert
import org.junit.Test
import java.lang.reflect.Proxy

/**
 * Regressions for generated native-free entry classes and modern version metadata.
 */
class EntryGenerationTest {

    private val generator = YukiHookXposedGenerator(
        SymbolProcessorEnvironment(
            options = emptyMap(),
            kotlinVersion = KotlinVersion.CURRENT,
            codeGenerator = unusedProcessorService<CodeGenerator>(),
            logger = unusedProcessorService<KSPLogger>()
        )
    )

    private fun <T : GenerateSpec> data(value: T) = value.apply {
        entryPackageName = "example.module"
        modulePackageName = "example.module"
        entryClassName = "HookEntry"
        xInitClassName = "HookEntry_YukiHookXposedInit"
        isEntryClassKindOfObject = true
    }

    @Test
    fun modernEntryOnlyReferencesItsRuntimeBase() {
        val source = data(LibXposedGenerateSpec(targetApiVersion = 102)).source("HookEntry_YukiHookXposedInit")
        Assert.assertTrue(source.contains("LibXposedEntry"))
        Assert.assertFalse(source.contains("Xposed82Entry"))
        Assert.assertFalse(source.contains("de.robv"))
        Assert.assertFalse(source.contains("io.github.libxposed.api"))
    }

    @Test
    fun legacyEntryContainsNoResourceHook() {
        val source = data(Xposed82GenerateSpec()).source("HookEntry_YukiHookXposedInit")
        Assert.assertTrue(source.contains("Xposed82Entry"))
        Assert.assertFalse(source.contains("Resources"))
        Assert.assertFalse(source.contains("libxposed"))
    }

    @Test
    fun modernHotReloadIsDisabledAndVersionsAreIndependent() {
        val data = data(LibXposedGenerateSpec(targetApiVersion = 102))
        Assert.assertTrue(data.createModuleProperties().contains("minApiVersion=101"))
        Assert.assertTrue(data.createModuleProperties().contains("targetApiVersion=102"))
        Assert.assertTrue(data.createModuleProperties().contains("autoHotReload=false"))
        data.targetApiVersion = 103
        Assert.assertTrue(data.createModuleProperties().contains("targetApiVersion=103"))
        Assert.assertTrue(data.createModuleProperties().contains("minApiVersion=101"))
    }

    @Test
    fun modernNativeEntryTracksConfiguredLibrariesWithoutChangingOtherMetadata() {
        val data = data(LibXposedGenerateSpec(targetApiVersion = 102, nativeLibraries = listOf("libexample.so", "libsecond.so")))
        val resources = data.createResources()
        Assert.assertEquals("libexample.so\nlibsecond.so", resources["META-INF/xposed/native_init.list"])
        Assert.assertEquals("example.module.HookEntry_YukiHookXposedInit", resources["META-INF/xposed/java_init.list"])
        Assert.assertEquals(4, resources.size)
        data.nativeLibraries = emptyList()
        Assert.assertEquals(resources - "META-INF/xposed/native_init.list", data.createResources())
    }

    @Test
    fun legacyResourcesRemainLimitedToTheJavaEntry() {
        val data = data(Xposed82GenerateSpec())
        Assert.assertEquals(
            mapOf("assets/xposed_init" to "example.module.HookEntry_YukiHookXposedInit"),
            data.createResources()
        )
    }

    @Test
    fun objectAndClassEntriesUseTheSameConstructionInBothBridges() {
        listOf(data(Xposed82GenerateSpec()), data(LibXposedGenerateSpec(targetApiVersion = 102))).forEach { data ->
            listOf(false, true).forEach { singleton ->
                data.isEntryClassKindOfObject = singleton
                val entry = if (singleton) "HookEntry" else "HookEntry()"
                val applicationSource = data.source("ModuleApplication_Impl")
                Assert.assertTrue(data.source(data.xInitClassName).contains("= $entry\n"))
                Assert.assertTrue(applicationSource.contains("YukiXposedModuleCaller.callOnInit($entry)"))
                Assert.assertFalse(applicationSource.contains("YukiXposedModuleCaller.callOnHook("))
                Assert.assertFalse(applicationSource.contains("YukiXposedModuleCaller.callRegisterFrameworkEvents("))
            }
        }
    }

    @Test
    fun generatedSourceSetMatchesRuntimeAndStatusSettings() {
        val commonFiles = listOf(
            "YukiHookAPI_Impl",
            "ModuleApplication_Impl",
            "HookEntry_YukiHookXposedInit"
        )
        listOf(
            data(Xposed82GenerateSpec(isUsingXposedModuleStatus = false)),
            data(Xposed82GenerateSpec(isUsingXposedModuleStatus = true)),
            data(LibXposedGenerateSpec(targetApiVersion = 102))
        ).forEach { data ->
            val sources = data.createSources()
            val hasStatusHooks = data is Xposed82GenerateSpec && data.isUsingXposedModuleStatus
            val expected = if (hasStatusHooks) commonFiles + "YukiHookLegacyModuleStatus_Impl" else commonFiles
            Assert.assertEquals(expected.sorted(), sources.map { it.name }.sorted())
            val statusClass = if (hasStatusHooks)
                "com.highcapable.yukihookapi.hook.xposed.bridge.status.YukiHookLegacyModuleStatus_Impl_example_module"
            else ""
            Assert.assertTrue(data.source("YukiHookAPI_Impl").contains("\"$statusClass\""))
        }
    }

    @Test
    fun legacyStatusJvmNamesAndKeepArePreserved() {
        val source = data(Xposed82GenerateSpec()).source("YukiHookLegacyModuleStatus_Impl")
        Assert.assertTrue(source.contains("@Keep"))
        listOf("__--", "_-_-", "-__-", "-_-_", "___-").forEach { Assert.assertTrue(source.contains("@JvmName(\"$it\")")) }
        Assert.assertTrue(source.contains("return false"))
        Assert.assertTrue(source.contains("return \"unknown\""))
        Assert.assertTrue(source.contains("return -1"))
    }

    @Test
    fun modulePackageOverrideIsOnlyGeneratedForLegacy() {
        listOf(data(Xposed82GenerateSpec()), data(LibXposedGenerateSpec(targetApiVersion = 102))).forEach { data ->
            data.customMPackageName = "example.custom"
            val source = data.source(data.xInitClassName)
            when (data) {
                is Xposed82GenerateSpec -> Assert.assertTrue(source.contains("modulePackageName: String = \"example.custom\""))
                is LibXposedGenerateSpec -> Assert.assertFalse(source.contains("modulePackageName"))
            }
        }
    }

    private fun GenerateSpec.source(name: String) = createSources().single { it.name == name }.toString()

    private fun GenerateSpec.createSources() = generate<List<FileSpec>>("createSources", GenerateSpec::class.java)

    private fun GenerateSpec.createResources() = generate<Map<String, String>>("createResources", GenerateSpec::class.java)

    private fun LibXposedGenerateSpec.createModuleProperties() = generate<String>("createModuleProperties", LibXposedGenerateSpec::class.java)

    // Generation stays private to the processor; inspect its output without exposing production helpers.
    @Suppress("UNCHECKED_CAST")
    private fun <T> GenerateSpec.generate(name: String, parameterType: Class<out GenerateSpec>) =
        YukiHookXposedGenerator::class.java.getDeclaredMethod(name, parameterType)
            .apply { isAccessible = true }.invoke(generator, this) as T

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T> unusedProcessorService() =
        Proxy.newProxyInstance(T::class.java.classLoader, arrayOf(T::class.java)) { _, method, _ ->
            error("Unexpected KSP service call while generating source content: ${method.name}")
        } as T
}