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
@file:Suppress("unused", "DEPRECATION", "OVERRIDE_DEPRECATION")

package com.highcapable.yukihookapi.runtime

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.events
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.bridge.event.registerFrameworkEvents
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import org.junit.Assert
import org.junit.Test

/**
 * Verifies the legacy module application's native-API isolation and explicit storage failure.
 */
class RuntimeIsolationTest {

    @Test
    fun frameworkEventsRegisterOnceOnlyDuringOnInit() {
        val registered = YukiHookModuleCaller.asResolver().firstField { name = "isFrameworkEventsRegistered" }
        val previous = checkNotNull(registered.get<Boolean>())
        registered.set(false)
        try {
            var configurations = 0
            val entry = object : YukiHookXposedModule {
                override fun onInit() = registerFrameworkEvents {
                    configurations++
                    Assert.assertThrows(IllegalStateException::class.java) {
                        registerFrameworkEvents { configurations++ }
                    }
                }

                override fun onHook() = registerFrameworkEvents { configurations++ }

                override fun onXposedEvent() = events {
                    configurations++
                    onInitZygote { configurations++ }
                    onHandleLoadPackage { configurations++ }
                    onHandleInitPackageResources { configurations++ }
                }
            }
            val anotherEntry = object : YukiHookXposedModule {
                override fun onInit() = registerFrameworkEvents { configurations++ }
                override fun onHook() = Unit
            }
            YukiXposedEvent.events { configurations++ }
            entry.onXposedEvent()
            Assert.assertEquals(0, configurations)
            Assert.assertThrows(IllegalStateException::class.java) { entry.onHook() }
            YukiHookModuleCaller.callOnInit(entry)
            Assert.assertThrows(IllegalStateException::class.java) { YukiHookModuleCaller.checkFrameworkEventsRegistration() }
            Assert.assertThrows(IllegalStateException::class.java) { entry.onHook() }
            Assert.assertThrows(IllegalStateException::class.java) { YukiHookModuleCaller.callOnInit(anotherEntry) }
            Assert.assertThrows(IllegalStateException::class.java) { entry.registerFrameworkEvents { configurations++ } }
            entry.onXposedEvent()
            Assert.assertEquals(1, configurations)
        } finally {
            registered.set(previous)
        }
    }

    private class HookContext : ContextWrapper(null) {
        override fun getPackageName() = "test.host"
        override fun getClassLoader() = checkNotNull(javaClass.classLoader)
        override fun getApplicationInfo() = ApplicationInfo()
    }

    private class StorageRequested : RuntimeException()

    /** Generated status target used by reflection tests. */
    object GeneratedModuleStatus {

        /** Whether the generated methods simulate installed self-hooks. */
        var isHooked = false

        /** Returns the current simulated activation state. */
        @JvmStatic
        @JvmName("__--")
        fun isActive() = isHooked

        /** Returns the framework name after self-hook installation. */
        @JvmStatic
        @JvmName("_-_-")
        fun frameworkName() = if (isHooked) "LSPosed" else "unknown"

        /** Returns the framework API level after self-hook installation. */
        @JvmStatic
        @JvmName("-__-")
        fun apiLevel() = if (isHooked) 82 else -1

        /** Returns the framework version after self-hook installation. */
        @JvmStatic
        @JvmName("-_-_")
        fun frameworkVersion() = if (isHooked) "1.9.2" else "unknown"

        /** Returns the framework version code after self-hook installation. */
        @JvmStatic
        @JvmName("___-")
        fun frameworkVersionCode() = if (isHooked) 7190L else -1L
    }

    @Test
    fun moduleStatusLoadsWithoutInjectedXposedBridge() {
        Assert.assertNull("de.robv.android.xposed.XposedBridge".toClassOrNull())
        YukiHookBridge.initializeModuleServices()
        Assert.assertEquals(YukiHookRuntime.XPOSED_82, YukiHookAPI.Status.runtime)
        Assert.assertFalse(YukiHookAPI.Status.isHostEnvironment)
        Assert.assertFalse(YukiHookAPI.Status.isXposedModuleActive)
        val exception = Assert.assertThrows(UnsupportedOperationException::class.java) {
            YukiHookAPI.Status.observeFrameworkService { Assert.fail("Xposed API 82 does not provide framework services.") }
        }
        Assert.assertEquals("Framework service unavailable in Xposed API 82.", exception.message)
    }

    @Test
    fun hookRegistrationFailsBeforeLoadingNativeApiOutsideHost() {
        val member = String::class.resolve().firstMethod {
            name = "substring"
            parameters(Int::class)
        }.self
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            YukiHookBridge.hook(HookPoint.Executable(member), 50, object : HookerBridge {})
        }
        val hookers = listOf(object : ClassicHookerBridge {}, ChainHookerBridge { error("No framework is attached.") })
        hookers.forEach { hooker ->
            Assert.assertThrows(IllegalStateException::class.java) {
                YukiHookBridge.hook(HookPoint.Executable(member), 50, hooker)
            }
        }
    }

    @Test
    fun generatedModuleStatusIsResolvedThroughTheRuntimeBridge() {
        Assert.assertFalse(YukiHookBridge.isModuleActive)
        Assert.assertEquals("unknown", YukiHookBridge.frameworkName)
        Assert.assertEquals(-1, YukiHookBridge.apiVersion)
        Assert.assertEquals("unknown", YukiHookBridge.frameworkVersion)
        Assert.assertEquals(-1L, YukiHookBridge.frameworkVersionCode)
        GeneratedModuleStatus.isHooked = true
        try {
            Assert.assertTrue(YukiHookBridge.isModuleActive)
            Assert.assertEquals("LSPosed", YukiHookBridge.frameworkName)
            Assert.assertEquals(82, YukiHookBridge.apiVersion)
            Assert.assertEquals("1.9.2", YukiHookBridge.frameworkVersion)
            Assert.assertEquals(7190L, YukiHookBridge.frameworkVersionCode)
        } finally {
            GeneratedModuleStatus.isHooked = false
        }
    }

    @Test
    fun standaloneInitializationFailsCleanlyWithoutInjectedXposedBridge() {
        val attached = YukiHookBridge.asResolver().firstField { name = "attached" }
        val previous = checkNotNull(attached.get<Boolean>())
        attached.set(false)
        try {
            Assert.assertFalse(YukiHookBridge.isStandaloneAvailable)
        } finally {
            attached.set(previous)
        }
    }

    @Test
    fun deprecatedStandaloneEntriesRunAfterLegacyAttachment() {
        val attached = YukiHookBridge.asResolver().firstField { name = "attached" }
        val loadedFromBaseContext = YukiHookAPI.asResolver().firstField { name = "isLoadedFromBaseContext" }
        val previousAttached = checkNotNull(attached.get<Boolean>())
        val previousLoadedFromBaseContext = checkNotNull(loadedFromBaseContext.get<Boolean>())
        attached.set(false)
        loadedFromBaseContext.set(false)
        try {
            var lambdaCalled = false
            var hookerCalled = false
            val hooker = object : YukiBaseHooker() {
                override fun onHook() { hookerCalled = true }
            }
            YukiHookBridge.attach()
            YukiHookAPI.encase(HookContext()) { lambdaCalled = true }
            YukiHookAPI.encase(HookContext(), hooker)
            Assert.assertTrue(lambdaCalled)
            Assert.assertTrue(hookerCalled)
        } finally {
            attached.set(previousAttached)
            loadedFromBaseContext.set(previousLoadedFromBaseContext)
        }
    }

    @Test
    fun modulePreferencesFallBackToPrivateStorageWithoutNewXshare() {
        val modes = mutableListOf<Int>()
        val context = object : ContextWrapper(null) {
            override fun getPackageName() = "test.module"
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                modes += mode
                throw StorageRequested()
            }
        }
        val prefs = context.prefs()
        Assert.assertThrows(StorageRequested::class.java) { prefs.getBoolean("key", true) }
        Assert.assertThrows(StorageRequested::class.java) { prefs.edit() }
        Assert.assertEquals(
            listOf(Context.MODE_WORLD_READABLE, Context.MODE_PRIVATE, Context.MODE_WORLD_READABLE, Context.MODE_PRIVATE),
            modes
        )
    }

    @Test
    fun unavailableHostXshareStillFailsExplicitly() {
        val attached = YukiHookBridge.asResolver().firstField { name = "attached" }
        val previous = checkNotNull(attached.get<Boolean>())
        attached.set(false)
        try {
            val error = Assert.assertThrows(IllegalStateException::class.java) {
                YukiHookBridge.preferences(
                    context = HookContext(),
                    packageName = "test.module",
                    name = "test_preferences",
                    hostEnvironment = true
                )
            }
            Assert.assertEquals("XSharedPreferences requires an attached Xposed host or a module context.", error.message)
        } finally {
            attached.set(previous)
        }
    }
}