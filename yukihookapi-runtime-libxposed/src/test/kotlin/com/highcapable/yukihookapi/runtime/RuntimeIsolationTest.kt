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
@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.highcapable.yukihookapi.runtime

import android.content.Context
import android.content.ContextWrapper
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.extension.toClassOrNull
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.factory.events
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.YukiHookXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiHookModuleCaller
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.bridge.event.registerFrameworkEvents
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import com.highcapable.yukihookapi.runtime.libxposed.LibXposedService
import org.junit.Assert
import org.junit.Test

/**
 * Checks that the module application's code path does not need the injected native hook API.
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

    private class ModuleContext : ContextWrapper(null) {

        var requestedMode = -1

        override fun getPackageName() = "test.module"

        override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
            requestedMode = mode
            throw StorageRequested()
        }
    }

    private class StorageRequested : RuntimeException()

    @Test
    fun moduleStatusLoadsWithoutNativeHookApi() {
        Assert.assertNull("io.github.libxposed.api.XposedModule".toClassOrNull())
        YukiHookBridge.initializeModuleServices()
        Assert.assertEquals(YukiHookRuntime.LIBXPOSED, YukiHookAPI.Status.runtime)
        Assert.assertFalse(YukiHookAPI.Status.isHostEnvironment)
        Assert.assertFalse(YukiHookAPI.Status.isXposedModuleActive)
    }

    @Test
    fun deprecatedCustomHookEntriesDoNothingInModernRuntime() {
        val loadedFromBaseContext = YukiHookAPI.asResolver().firstField { name = "isLoadedFromBaseContext" }
        val previous = checkNotNull(loadedFromBaseContext.get<Boolean>())
        loadedFromBaseContext.set(false)
        try {
            var lambdaCalled = false
            var hookerCalled = false
            val hooker = object : YukiBaseHooker() {
                override fun onHook() { hookerCalled = true }
            }
            YukiHookAPI.encase(ModuleContext()) { lambdaCalled = true }
            YukiHookAPI.encase(ModuleContext(), hooker)
            Assert.assertFalse(YukiHookBridge.isCustomHookAvailable)
            Assert.assertFalse(lambdaCalled)
            Assert.assertFalse(hookerCalled)
            Assert.assertFalse(loadedFromBaseContext.get<Boolean>() ?: true)
        } finally {
            loadedFromBaseContext.set(previous)
        }
    }

    @Test
    fun missingServiceFallsBackToPrivateStorage() {
        val context = ModuleContext()
        val preferences = context.prefs()
        Assert.assertThrows(StorageRequested::class.java) { preferences.getString("missing", "default") }
        Assert.assertEquals(Context.MODE_PRIVATE, context.requestedMode)
        Assert.assertThrows(StorageRequested::class.java) { preferences.edit() }
        Assert.assertEquals(Context.MODE_PRIVATE, context.requestedMode)
    }

    @Test
    fun hookedHostModulePreferenceWritesAreIgnored() {
        val moduleInstance = checkNotNull(
            checkNotNull("com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime".toClassOrNull())
                .asResolver().firstField { name = "INSTANCE" }.get<Any>()
        )
        val isModuleLoaded = moduleInstance.asResolver().firstField { name = "isModuleLoaded" }
        val previousModuleLoaded = checkNotNull(isModuleLoaded.get<Boolean>())
        val previousHook = YukiHookBridge.hookCallback
        isModuleLoaded.set(true)
        YukiHookBridge.hookCallback = { _, _, callback -> callback }
        try {
            val preferences = ModuleContext().prefs()
            Assert.assertFalse(preferences.edit().putBoolean("key", true).commit())
            preferences.edit { putBoolean("key", true) }
        } finally {
            YukiHookBridge.hookCallback = previousHook
            isModuleLoaded.set(previousModuleLoaded)
        }
    }

    @Test
    fun serviceObserverReportsInitialUnavailableState() {
        val states = mutableListOf<Boolean>()
        val subscription = checkNotNull(YukiHookAPI.Status.observeFrameworkService { states += it })
        AutoCloseable { subscription.unregister() }.use {
            Assert.assertEquals(listOf(false), states)
        }
        LibXposedService.asResolver().firstMethod {
            name = "notifyListeners"
            emptyParameters()
        }.invoke()
        Assert.assertEquals(listOf(false), states)
    }

    @Test
    fun serviceSubscriptionsCancelIndependently() {
        val states = mutableListOf<Boolean>()
        val listener = FrameworkServiceBridge { states += it }
        val first = checkNotNull(YukiHookAPI.Status.observeFrameworkService(listener = listener))
        AutoCloseable { first.unregister() }.use {
            val second = checkNotNull(YukiHookAPI.Status.observeFrameworkService(listener = listener))
            AutoCloseable { second.unregister() }.use {
                val notifyListeners = LibXposedService.asResolver().firstMethod {
                    name = "notifyListeners"
                    emptyParameters()
                }
                Assert.assertEquals(listOf(false, false), states)
                first.unregister()
                first.unregister()
                notifyListeners.invoke()
                Assert.assertEquals(listOf(false, false, false), states)
                second.unregister()
                notifyListeners.invoke()
                Assert.assertEquals(listOf(false, false, false), states)
            }
        }
    }

    @Test
    fun lifecycleServiceSubscriptionsCancelIndependentlyAndOnDestroy() {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this)
        }
        owner.lifecycle.currentState = Lifecycle.State.STARTED
        val states = mutableListOf<Boolean>()
        val listener = FrameworkServiceBridge { states += it }
        val first = checkNotNull(YukiHookAPI.Status.observeFrameworkService(owner.lifecycle, listener))
        AutoCloseable { first.unregister() }.use {
            val second = checkNotNull(YukiHookAPI.Status.observeFrameworkService(owner.lifecycle, listener))
            AutoCloseable { second.unregister() }.use {
                val notifyListeners = LibXposedService.asResolver().firstMethod {
                    name = "notifyListeners"
                    emptyParameters()
                }
                Assert.assertEquals(listOf(false, false), states)
                Assert.assertEquals(2, owner.lifecycle.observerCount)
                first.unregister()
                first.unregister()
                Assert.assertEquals(1, owner.lifecycle.observerCount)
                owner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
                notifyListeners.invoke()
                Assert.assertEquals(listOf(false, false, false), states)
                owner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
                Assert.assertEquals(0, owner.lifecycle.observerCount)
                notifyListeners.invoke()
                Assert.assertEquals(listOf(false, false, false), states)
            }
        }
    }

    @Test
    fun serviceSubscriptionsDoNotRemainRegisteredForDestroyedLifecycles() {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this)
        }
        owner.lifecycle.currentState = Lifecycle.State.CREATED
        var callbacks = 0
        val subscription = checkNotNull(YukiHookAPI.Status.observeFrameworkService(owner.lifecycle) {
            callbacks++
            Assert.assertEquals(1, owner.lifecycle.observerCount)
            owner.lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        })
        AutoCloseable { subscription.unregister() }.use {
            Assert.assertNull(YukiHookAPI.Status.observeFrameworkService(owner.lifecycle) { callbacks++ })
            Assert.assertEquals(1, callbacks)
            Assert.assertEquals(0, owner.lifecycle.observerCount)
            LibXposedService.asResolver().firstMethod {
                name = "notifyListeners"
                emptyParameters()
            }.invoke()
            Assert.assertEquals(1, callbacks)
        }
    }

    @Test
    fun failedServiceRegistrationReleasesBothObservers() {
        val owner = object : LifecycleOwner {
            override val lifecycle = LifecycleRegistry.createUnsafe(this)
        }
        owner.lifecycle.currentState = Lifecycle.State.CREATED
        val listeners = checkNotNull(LibXposedService.asResolver().firstField { name = "listeners" }.get<Set<FrameworkServiceBridge>>())
        val previousCount = listeners.size
        val failure = IllegalStateException("Listener failed")
        val thrown = Assert.assertThrows(IllegalStateException::class.java) {
            YukiHookAPI.Status.observeFrameworkService(owner.lifecycle) { throw failure }?.unregister()
        }
        Assert.assertSame(failure, thrown)
        Assert.assertEquals(0, owner.lifecycle.observerCount)
        Assert.assertEquals(previousCount, listeners.size)
    }

    @Test
    fun explicitNativeStorageDoesUseTheLocalContext() {
        var requestedMode = -1
        val context = object : ContextWrapper(null) {
            override fun getPackageName() = "test.module"
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                requestedMode = mode
                error("Local storage requested")
            }
        }
        Assert.assertThrows(IllegalStateException::class.java) { context.prefs().native().getString("test") }
        Assert.assertEquals(Context.MODE_PRIVATE, requestedMode)
    }
}