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
package com.highcapable.yukihookapi.hook.xposed.bridge.session

import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.kavaref.extension.classOf
import com.highcapable.yukihookapi.hook.xposed.bridge.event.ModuleEventDispatcher
import com.highcapable.yukihookapi.hook.xposed.bridge.session.state.ModuleHandoffState
import com.highcapable.yukihookapi.hook.xposed.reload.lifecycle.ModuleLifecycle
import com.highcapable.yukihookapi.runtime.bridge.session.ModuleSessionBridge
import java.util.Collections
import java.util.IdentityHashMap
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask

/**
 * Manages module lifecycle callbacks and transports component-owned state between module instances.
 */
internal object ModuleLifecycleManager {

    private var session: ModuleSessionBridge? = null
    private var lifecycle: ModuleLifecycle? = null

    private var restoring = false

    private val components = mutableListOf<Component>()
    private val failures = mutableListOf<Throwable>()

    private class Component(
        val id: String,
        val instance: ModuleReloadable
    )

    /** Entry-supplied dispatch for managed work and caught failures. */
    var events = ModuleEventDispatcher()
        private set

    /**
     * Attaches the runtime's module session without initializing host components.
     * @param session the runtime's module session.
     */
    fun attach(session: ModuleSessionBridge) {
        this.session = session
        events = ModuleEventDispatcher(session, ::recordRestoreFailure)
    }

    /**
     * Adds a component after host identity is established.
     * Events follow registration order.
     * @param id the unique identifier for the component.
     * @param component the component instance to register.
     */
    fun register(id: String, component: ModuleReloadable) {
        check(components.none { it.id == id }) {
            "Reload component '$id' is already registered."
        }

        component.onAttach(events)
        components.add(Component(id, component))
    }

    /**
     * Registers the user's optional lifecycle callbacks.
     * @param lifecycle the user's lifecycle callbacks.
     */
    fun register(lifecycle: ModuleLifecycle) {
        check(this.lifecycle == null) {
            "Module lifecycle callbacks can only be registered once per module instance."
        }

        this.lifecycle = lifecycle
    }

    /**
     * Checks state crossing module classloaders.
     * @param bundle the state to check.
     * @throws IllegalArgumentException if the state contains cycles or unsupported types.
     */
    fun checkBundle(bundle: Bundle?) {
        val visited = Collections.newSetFromMap(IdentityHashMap<Any, Boolean>())

        fun checkValue(value: Any?) {
            if (value == null || value is String || value is Boolean || value is Byte || value is Short ||
                value is Int || value is Long || value is Float || value is Double || value is Char ||
                value is BooleanArray || value is ByteArray || value is ShortArray || value is IntArray ||
                value is LongArray || value is FloatArray || value is DoubleArray || value is CharArray
            ) return

            require(visited.add(value)) {
                "Reload state must not contain cycles."
            }
            when (value) {
                is Bundle -> {
                    // Typed Bundle getters cannot inspect unknown entries without coercion or type warnings.
                    // Resolve the public generic getter only for this classloader-boundary validation.
                    val getter = Bundle::class.resolve().firstMethod {
                        name = "get"
                        parameters(String::class)
                        superclass()
                    }.of(value)
                    value.keySet().forEach { checkValue(getter.invoke<Any>(it)) }
                }
                is Array<*> -> {
                    val component = value.javaClass.componentType
                    require(component == classOf<Any>() || component == classOf<String>() || component == classOf<Bundle>()) {
                        "Reload arrays must contain Object, String or Bundle values."
                    }
                    value.forEach(::checkValue)
                }
                is ArrayList<*> -> {
                    require(value.javaClass == classOf<ArrayList<*>>()) {
                        "Reload lists must not be module-defined subclasses."
                    }

                    value.forEach(::checkValue)
                }
                else -> error("Reload state cannot contain ${value.javaClass.name}. Use primitive values or bundles.")
            }

            visited.remove(value)
        }

        checkValue(bundle)
    }

    /**
     * Checks and saves every component before asking the runtime to accept retirement.
     * @param extras optional extras to pass to the lifecycle callbacks.
     * @param saveState the callback to save the handoff state.
     * @return [Boolean] true if the module can be retired, false if the runtime should continue with the current module instance.
     */
    fun prepare(extras: Bundle?, saveState: (Any) -> Boolean): Boolean {
        val current = session ?: return false
        checkBundle(extras)
        if (lifecycle?.canReloadCallback?.invoke(extras) == false) return false

        components.forEach { it.call { onPrepareReload() } }
        if (!current.freeze()) return false

        var disposing = false
        return runCatching {
            val userState = lifecycle?.saveStateCallback?.invoke()
            checkBundle(userState)
            components.forEach { it.call { onPrepareReload() } }

            val componentStates = components.associate { component ->
                component.id to component.call { onSaveState() }
            }
            val state = ModuleHandoffState(userState, componentStates)
            if (!saveState(ModuleHandoffState.encode(state))) {
                current.resume()
                return false
            }

            disposing = true
            dispose()

            true
        }.onFailure {
            // Once module-owned disposal starts it cannot be rolled back automatically.
            if (!disposing) current.resume()
        }.getOrThrow()
    }

    /**
     * Decodes the handoff and checks that every registered component has saved state.
     * @param state the handoff state to decode.
     * @return [ModuleHandoffState] the decoded handoff state.
     */
    fun beginRestore(state: Any?): ModuleHandoffState {
        val restored = ModuleHandoffState.decode(state)
        components.forEach {
            check(it.id in restored.components) {
                "Missing reload component '${it.id}'."
            }
        }
        synchronized(failures) { failures.clear() }
        restoring = true

        return restored
    }

    /**
     * Restores user state after onInit and before `onHook`.
     * @param state the state to restore.
     */
    fun restoreState(state: ModuleHandoffState) {
        checkBundle(state.userState)
        lifecycle?.restoreStateCallback?.invoke(state.userState)
    }

    /**
     * Lets components prepare their own saved prerequisites before declarations are replayed.
     * @param appInfo the new Xposed module application info.
     * @param state the state to restore.
     */
    fun prepareRestore(appInfo: ApplicationInfo, state: ModuleHandoffState) {
        components.forEach { component ->
            runCatching {
                component.call { onPrepareRestore(appInfo, state.components.getValue(component.id)) }
            }.onFailure(::recordRestoreFailure)
        }
    }

    /**
     * Dispatches restoration at an entry lifecycle boundary using each component's registered thread.
     * @param appInfo the new Xposed module application info.
     * @param state the state to restore.
     * @param phase the restore phase.
     */
    fun restoreComponents(appInfo: ApplicationInfo, state: ModuleHandoffState, phase: ModuleReloadable.RestorePhase) {
        components.filter { it.instance.restorePhase == phase }.forEach { component ->
            val result = runCatching { component.call { onRestoreState(appInfo, state.components.getValue(component.id)) } }

            // Prerequisites must succeed before module code runs.
            // Rebuild failures are collected so other components can continue.
            if (phase == ModuleReloadable.RestorePhase.PREREQUISITE)
                result.getOrThrow()
            else result.onFailure(::recordRestoreFailure)
        }
    }

    /**
     * Runs the completion callback and reports all failures collected during restoration.
     * @param extras optional extras to pass to the lifecycle callbacks.
     */
    fun finishRestore(extras: Bundle?) {
        runCatching { lifecycle?.reloadedCallback?.invoke(extras) }.onFailure(::recordRestoreFailure)
        restoring = false

        val errors = synchronized(failures) { failures.toList().also { failures.clear() } }
        if (errors.isNotEmpty()) throw IllegalStateException(
            "Module reload completed with ${errors.size} failure(s).",
            errors.first()
        ).apply { errors.drop(1).forEach(::addSuppressed) }
    }

    /** Ends restoration after an unrecoverable initialization failure. */
    fun abortRestore() {
        restoring = false
        synchronized(failures) { failures.clear() }
    }

    /**
     * Records a failure without interrupting the remaining registrations.
     * @param failure the failure to record.
     */
    fun recordRestoreFailure(failure: Throwable) {
        if (restoring) synchronized(failures) { failures.add(failure) }
    }

    private fun dispose() {
        var failure: Throwable? = null

        fun release(block: () -> Unit) = runCatching(block).onFailure {
            if (failure == null) failure = it else if (failure !== it) failure.addSuppressed(it)
        }
        release { lifecycle?.disposeCallback?.invoke() }

        components.forEach { component -> release { component.call { onDispose() } } }
        failure?.let { throw it }
    }

    private fun <T> Component.call(block: ModuleReloadable.() -> T): T {
        val value = instance
        return if (value.requiresMainThread) mainThread { value.block() } else value.block()
    }

    private fun <T> mainThread(block: () -> T): T {
        if (Looper.myLooper() == Looper.getMainLooper()) return block()

        val task = FutureTask(block)
        check(Handler(Looper.getMainLooper()).post(task)) {
            "The host main thread is unavailable."
        }

        return try {
            task.get()
        } catch (failure: ExecutionException) {
            throw failure.cause ?: failure
        }
    }
}