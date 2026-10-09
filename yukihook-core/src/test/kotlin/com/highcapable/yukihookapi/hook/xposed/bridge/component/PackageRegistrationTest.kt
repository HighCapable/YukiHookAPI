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
 * This file is created by fankes on 2026/9/27.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.component

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.param.context.PackageContext
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge
import org.junit.Assert
import org.junit.Test

/**
 * Checks loaded package replay across module generations.
 */
class PackageRegistrationTest {

    private val loader = javaClass.classLoader!!
    private val moduleAppInfo = ApplicationInfo()
    private val managedEvents = ModuleEventDispatcher(object : ModuleSessionBridge {
        override fun enter() = true
        override fun leave() = Unit
        override fun freeze() = true
        override fun resume() = Unit
        override fun close() = Unit
    })

    @Test
    fun loadedPackagesReplayOnceAndCanBeSavedAgain() {
        val previous = PackageRegistration { error("The previous generation must not replay packages.") }
        previous.onAttach(managedEvents)
        val host = packageEntry(HookEntryType.PACKAGE, "example.host")
        val system = packageEntry(HookEntryType.SYSTEM_SERVER, "android")

        Assert.assertTrue(previous.record(host))
        Assert.assertFalse(previous.record(host.copy(processName = "example.host:other")))
        Assert.assertTrue(previous.record(system))

        val replayed = mutableListOf<PackageContext>()
        lateinit var current: PackageRegistration
        current = PackageRegistration { context ->
            replayed += context
            current.record(context)
        }
        current.onAttach(managedEvents)
        current.onRestoreState(moduleAppInfo, previous.onSaveState(recovery = false))

        Assert.assertEquals(
            listOf(HookEntryType.PACKAGE to "example.host", HookEntryType.SYSTEM_SERVER to "android"),
            replayed.map { it.type to it.packageName }
        )
        Assert.assertNotSame(host, replayed.first())
        Assert.assertSame(loader, replayed.first().classLoader)
        Assert.assertFalse(current.record(replayed.first()))

        val nextReplay = mutableListOf<PackageContext>()
        PackageRegistration { nextReplay += it }.apply {
            onAttach(managedEvents)
            onRestoreState(moduleAppInfo, current.onSaveState(recovery = false))
        }
        Assert.assertEquals(listOf("example.host", "android"), nextReplay.map { it.packageName })
    }

    @Test
    fun zygoteEventsRemainRepeatableAndAreNotReplayed() {
        val previous = PackageRegistration { error("The previous generation must not replay packages.") }
        previous.onAttach(managedEvents)
        val zygote = packageEntry(HookEntryType.ZYGOTE, "android")

        Assert.assertTrue(previous.record(zygote))
        Assert.assertTrue(previous.record(zygote))

        val replayed = mutableListOf<PackageContext>()
        PackageRegistration { replayed += it }.onRestoreState(moduleAppInfo, previous.onSaveState(recovery = false))
        Assert.assertTrue(replayed.isEmpty())
    }

    @Test
    fun contextsObservedBeforeRuntimeBindingReplayInTheNextInstance() {
        val failed = PackageRegistration { error("A failed entry must only record host contexts.") }
        val host = packageEntry(HookEntryType.PACKAGE, "example.host")
        val secondary = packageEntry(HookEntryType.PACKAGE, "example.secondary")
        val system = packageEntry(HookEntryType.SYSTEM_SERVER, "android")
        failed.record(host, retain = true)
        failed.record(secondary, retain = true)
        failed.record(system, retain = true)
        failed.record(host.copy(processName = "ignored.duplicate"), retain = true)

        val replayed = mutableListOf<PackageContext>()
        PackageRegistration { replayed += it }.apply {
            onAttach(managedEvents)
            onRestoreState(moduleAppInfo, failed.onSaveState(recovery = true))
        }

        Assert.assertEquals(listOf("example.host", "example.secondary", "android"), replayed.map { it.packageName })
        Assert.assertEquals("example.host", replayed.first().processName)
        Assert.assertSame(loader, replayed.last().classLoader)
    }

    @Test
    fun aPreviouslyObservedContextCanBeRetainedWithoutDispatchingItAgain() {
        val previous = PackageRegistration { error("Recording must not execute declarations.") }
        val host = packageEntry(HookEntryType.PACKAGE, "example.host")
        previous.record(host)

        Assert.assertFalse(previous.record(host, retain = true))
        val replayed = mutableListOf<PackageContext>()
        PackageRegistration { replayed += it }.onRestoreState(moduleAppInfo, previous.onSaveState(recovery = false))

        Assert.assertEquals(listOf("example.host"), replayed.map { it.packageName })
    }

    private fun packageEntry(type: HookEntryType, packageName: String) = PackageContext(
        type = type,
        packageName = packageName,
        processName = packageName,
        isFirstApplication = type == HookEntryType.PACKAGE,
        classLoader = loader
    )
}