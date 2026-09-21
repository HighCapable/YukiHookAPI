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
        initClassName = "HookEntry_YukiHookXposedInit"
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