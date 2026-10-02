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
 * This file is created by fankes on 2026/10/3.
 */
@file:Suppress("UnusedExpression")

package com.highcapable.yukihookapi.hook.xposed.bridge.component

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.param.context.PackageContext
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge
import org.junit.Assert
import org.junit.Test
import java.lang.ref.WeakReference
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray
import kotlin.reflect.typeOf

/**
 * Checks retained value scopes, type invalidation, read-only snapshots and handoff state across module generations.
 */
class RetainedValuesStoreTest {

    private val loader = javaClass.classLoader!!
    private val moduleAppInfo = ApplicationInfo()
    private val managedEvents = ModuleEventDispatcher(object : ModuleSessionBridge {
        override fun enter() = true
        override fun leave() = Unit
        override fun freeze() = true
        override fun resume() = Unit
        override fun close() = Unit
    })

    private val host = packageEntry(HookEntryType.PACKAGE, "example.host")
    private val other = packageEntry(HookEntryType.PACKAGE, "example.other")
    private val system = packageEntry(HookEntryType.SYSTEM_SERVER, "android")

    private class ModuleOwned

    private class ModuleList(private val values: List<String>) : AbstractList<String>() {
        override val size get() = values.size
        override fun get(index: Int) = values[index]
    }

    @Test
    fun savedValuesAreReusedWithoutRunningTheBlockAgain() {
        val previous = store()
        val value = previous.retain(host, typeOf<List<String>>(), "names-v1") { listOf("first") }

        val current = store(previous)
        val restored = current.retain<List<String>>(host, typeOf<List<String>>(), "names-v1") {
            error("A saved value with the same type must not be computed again.")
        }
        Assert.assertSame(value, restored)

        val next = store(current)
        Assert.assertSame(value, next.retain(host, typeOf<List<String>>(), "names-v1") { listOf("second") })
    }

    @Test
    fun theSameKeyIsIndependentInEachPackageScope() {
        val previous = store()
        Assert.assertEquals("host", previous.retain(host, typeOf<String>(), "title-v1") { "host" })
        Assert.assertEquals("other", previous.retain(other, typeOf<String>(), "title-v1") { "other" })
        Assert.assertEquals("system", previous.retain(system, typeOf<String>(), "title-v1") { "system" })

        val current = store(previous)
        Assert.assertEquals("other", current.retain(other, typeOf<String>(), "title-v1") { "recomputed" })
        Assert.assertEquals("host", current.retain(host, typeOf<String>(), "title-v1") { "recomputed" })
        Assert.assertEquals("system", current.retain(system, typeOf<String>(), "title-v1") { "recomputed" })
        Assert.assertEquals("new", current.retain(packageEntry(HookEntryType.PACKAGE, "example.new"), typeOf<String>(), "title-v1") { "new" })
    }

    @Test
    fun replayedContextsWithTheSameIdShareTheirScope() {
        val previous = store()
        val value = previous.retain(host, typeOf<List<Int>>(), "list-v1") { listOf(1) }

        val replayed = PackageContext.decode(PackageContext.encode(host.copy(processName = "example.host:remote")))
        Assert.assertSame(value, store(previous).retain(replayed, typeOf<List<Int>>(), "list-v1") { listOf(2) })

        val current = store()
        current.retain(host, typeOf<Int>(), "count-v1") { 1 }
        Assert.assertThrows(IllegalArgumentException::class.java) {
            current.retain(replayed, typeOf<Int>(), "count-v1") { 2 }
        }
    }

    @Test
    fun aKeyCanOnlyBeUsedOnceInTheSameScope() {
        val store = store()
        store.retain(host, typeOf<Int>(), "count-v1") { 1 }

        val failure = Assert.assertThrows(IllegalArgumentException::class.java) {
            store.retain(host, typeOf<Int>(), "count-v1") { 2 }
        }
        Assert.assertTrue(failure.message!!.contains(host.id))
        Assert.assertThrows(IllegalArgumentException::class.java) {
            store.retain(host, typeOf<String>(), " ") { "blank" }
        }
    }

    @Test
    fun aFailedComputationReleasesItsKey() {
        val store = store()
        Assert.assertThrows(UnsupportedOperationException::class.java) {
            store.retain(host, typeOf<Int>(), "count-v1") { throw UnsupportedOperationException() }
        }

        Assert.assertEquals(2, store.retain(host, typeOf<Int>(), "count-v1") { 2 })
        Assert.assertEquals(2, store(store).retain(host, typeOf<Int>(), "count-v1") { 3 })
    }

    @Test
    fun aChangedDeclaredTypeComputesANewValue() {
        val previous = store()
        previous.retain(host, typeOf<Int>(), "value-v1") { 1 }
        previous.retain(host, typeOf<List<Int>>(), "list-v1") { listOf(1) }
        previous.retain(host, typeOf<String>(), "nullable-v1") { "value" }

        val current = store(previous)
        Assert.assertEquals("1", current.retain(host, typeOf<String>(), "value-v1") { "1" })
        Assert.assertEquals(listOf("1"), current.retain(host, typeOf<List<String>>(), "list-v1") { listOf("1") })
        Assert.assertNull(current.retain(host, typeOf<String?>(), "nullable-v1") { null })

        val next = store(current)
        Assert.assertEquals("1", next.retain(host, typeOf<String>(), "value-v1") { "2" })
        Assert.assertEquals(listOf("1"), next.retain(host, typeOf<List<String>>(), "list-v1") { listOf("2") })
        Assert.assertNull(next.retain(host, typeOf<String?>(), "nullable-v1") { "2" })
    }

    @Test
    fun moduleDefinedValuesAreRejectedAndReleaseTheirKey() {
        val store = store()
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<Any>(), "owned-v1") { ModuleOwned() }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<List<Any>>(), "owned-list-v1") { listOf<Any>(ModuleOwned()) }
        }

        Assert.assertEquals("neutral", store.retain(host, typeOf<Any>(), "owned-v1") { "neutral" })
        Assert.assertEquals(1, (store.onSaveState(recovery = false) as Array<*>).size)
    }

    @Test
    fun containerResultsAreFrozenIntoReadOnlySnapshots() {
        val store = store()
        val source = arrayListOf("first")
        val list = store.retain<List<String>>(host, typeOf<List<String>>(), "list-v1") { source }
        source.add("second")
        Assert.assertEquals(listOf("first"), list)
        Assert.assertThrows(UnsupportedOperationException::class.java) { (list as MutableList<String>).add("third") }

        val map = store.retain<Map<String, List<Int>>>(host, typeOf<Map<String, List<Int>>>(), "map-v1") {
            linkedMapOf("b" to arrayListOf(2), "a" to arrayListOf(1))
        }
        Assert.assertEquals(listOf("b", "a"), map.keys.toList())
        Assert.assertThrows(UnsupportedOperationException::class.java) { (map as MutableMap<String, List<Int>>).clear() }
        Assert.assertThrows(UnsupportedOperationException::class.java) { (map.getValue("a") as MutableList<Int>).add(3) }

        val collection = store.retain<Collection<String>>(host, typeOf<Collection<String>>(), "collection-v1") { linkedSetOf("b", "a") }
        Assert.assertTrue(collection is Set<*>)
        Assert.assertEquals(listOf("b", "a"), collection.toList())
        val iterable = store.retain(host, typeOf<Iterable<String>>(), "iterable-v1") { Iterable { listOf("a").iterator() } }
        Assert.assertEquals(listOf("a"), iterable)
        Assert.assertThrows(UnsupportedOperationException::class.java) { (iterable as MutableList<String>).add("b") }

        val restored = store(store).retain(host, typeOf<List<String>>(), "list-v1") { listOf("recomputed") }
        Assert.assertSame(list, restored)
    }

    @Test
    fun anyContainerImplementationIsAcceptedAsASnapshot() {
        val store = store()
        Assert.assertEquals(emptyList<String>(), store.retain(host, typeOf<List<String>>(), "empty-v1") { emptyList<String>() })
        Assert.assertEquals(listOf("a"), store.retain(host, typeOf<List<String>>(), "builder-v1") { buildList { add("a") } })
        Assert.assertEquals(listOf("a"), store.retain(host, typeOf<List<String>>(), "module-list-v1") { ModuleList(listOf("a")) })
        Assert.assertEquals(emptyMap<String, Int>(), store.retain(host, typeOf<Map<String, Int>>(), "empty-map-v1") { emptyMap<String, Int>() })
        Assert.assertEquals(listOf("a"), store.retain(host, typeOf<Any>(), "any-v1") { ModuleList(listOf("a")) })
    }

    @Test
    fun arraysAreRejectedAndReleaseTheirKey() {
        val store = store()
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<Array<String>>(), "array-v1") { arrayOf("a") }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<IntArray>(), "int-array-v1") { intArrayOf(1) }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<List<Any>>(), "nested-array-v1") { listOf<Any>(arrayOf("a")) }
        }

        Assert.assertEquals(listOf("a"), store.retain(host, typeOf<List<String>>(), "array-v1") { listOf("a") })
    }

    @Test
    fun nonCollectionIterablesAreValuesUnlessDeclaredAsIterable() {
        val store = store()
        val path = Paths.get("first", "second")
        Assert.assertSame(path, store.retain(host, typeOf<Any>(), "any-path-v1") { path })
        Assert.assertSame(path, store.retain(host, typeOf<Path>(), "path-v1") { path })

        val iterable = store.retain<Iterable<Path>>(host, typeOf<Iterable<Path>>(), "iterable-path-v1") { path }
        Assert.assertEquals(listOf(Paths.get("first"), Paths.get("second")), iterable)
        Assert.assertThrows(UnsupportedOperationException::class.java) { (iterable as MutableList<Path>).clear() }
    }

    @Test
    fun referenceHoldersAreRejectedAndReleaseTheirKey() {
        val store = store()
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<WeakReference<String>>(), "reference-v1") { WeakReference("value") }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<AtomicReference<String>>(), "atomic-v1") { AtomicReference("value") }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<AtomicReferenceArray<String>>(), "atomic-array-v1") { AtomicReferenceArray<String>(1) }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<ThreadLocal<String>>(), "thread-local-v1") { ThreadLocal<String>() }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<List<Any>>(), "nested-reference-v1") { listOf<Any>(WeakReference("value")) }
        }

        Assert.assertEquals("value", store.retain(host, typeOf<String>(), "reference-v1") { "value" })
    }

    @Test
    fun declaredContainerTypesMustHoldReadOnlySnapshots() {
        val store = store()
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<ArrayList<String>>(), "array-list-v1") { arrayListOf("a") }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<HashMap<String, Int>>(), "hash-map-v1") { hashMapOf("a" to 1) }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<List<ArrayList<String>>>(), "nested-list-v1") { listOf(arrayListOf("a")) }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<Map<String, HashSet<String>>>(), "nested-map-v1") { mapOf("a" to hashSetOf("b")) }
        }
    }

    @Test
    fun unmanagedInstancesComputeAndValidateValuesWithoutSavingThem() {
        class LocalType
        val store = RetainedValuesStore()
        Assert.assertEquals(1, store.retain(host, typeOf<Int>(), "count-v1") { 1 })
        Assert.assertEquals(2, store.retain(host, typeOf<LocalType>(), "local-v1") { 2 })
        Assert.assertThrows(IllegalArgumentException::class.java) {
            store.retain(host, typeOf<Int>(), "count-v1") { 3 }
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            store.retain(host, typeOf<Any>(), "owned-v1") { ModuleOwned() }
        }
        Assert.assertEquals(0, (store.onSaveState(recovery = false) as Array<*>).size)
    }

    @Test
    fun recoveryStateCarriesTheSameValues() {
        val failed = store()
        val value = failed.retain(host, typeOf<List<Int>>(), "list-v1") { listOf(1) }

        val current = RetainedValuesStore().apply {
            onAttach(managedEvents)
            onPrepareRestore(moduleAppInfo, failed.onSaveState(recovery = true))
        }
        Assert.assertSame(value, current.retain(host, typeOf<List<Int>>(), "list-v1") { listOf(2) })
    }

    @Test
    fun invalidRestoredStateIsRejected() {
        val duplicate = arrayOf(host.id, "key", "type", null)
        Assert.assertThrows(IllegalStateException::class.java) {
            RetainedValuesStore().onPrepareRestore(moduleAppInfo, arrayOf(duplicate, duplicate.copyOf()))
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            RetainedValuesStore().onPrepareRestore(moduleAppInfo, arrayOf(arrayOf("key", "type", null)))
        }
        Assert.assertThrows(IllegalStateException::class.java) {
            RetainedValuesStore().onPrepareRestore(moduleAppInfo, null)
        }
    }

    private fun store(previous: RetainedValuesStore? = null) = RetainedValuesStore().apply {
        onAttach(managedEvents)
        if (previous != null) onPrepareRestore(moduleAppInfo, previous.onSaveState(recovery = false))
    }

    private fun packageEntry(type: HookEntryType, packageName: String) = PackageContext(
        type = type,
        packageName = packageName,
        processName = packageName,
        isFirstApplication = type == HookEntryType.PACKAGE,
        classLoader = loader
    )
}