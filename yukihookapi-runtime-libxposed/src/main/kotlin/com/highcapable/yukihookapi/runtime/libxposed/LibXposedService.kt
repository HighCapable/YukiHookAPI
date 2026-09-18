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
@file:Suppress("SameParameterValue")

package com.highcapable.yukihookapi.runtime.libxposed

import android.util.Log
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.highcapable.yukihookapi.runtime.bridge.FrameworkServiceBridge
import io.github.libxposed.api.XposedInterface
import io.github.libxposed.service.XposedService
import io.github.libxposed.service.XposedServiceHelper
import java.util.concurrent.CopyOnWriteArraySet

/**
 * Owns the asynchronous module service connection independently of the host hook API.
 */
internal object LibXposedService {

    private val services = linkedSetOf<XposedService>()
    private val listeners = CopyOnWriteArraySet<ServiceSubscription>()

    private val service get() = synchronized(services) { services.singleOrNull() }

    private var initialized = false

    val isAvailable get() = runCatching { service?.apiVersion?.let { it >= XposedInterface.API_101 } ?: false }.getOrDefault(false)
    val frameworkName get() = runCatching { service?.frameworkName }.getOrNull() ?: "unknown"
    val apiLevel get() = runCatching { service?.apiVersion }.getOrNull() ?: -1
    val frameworkVersion get() = runCatching { service?.frameworkVersion }.getOrNull() ?: "unknown"
    val frameworkVersionCode get() = runCatching { service?.frameworkVersionCode?.coerceIn(0, Int.MAX_VALUE.toLong())?.toInt() }.getOrNull() ?: -1
    val isRemoteFilesAvailable get() = useRemoteFiles(false) { true }

    @Synchronized
    fun initialize() {
        if (initialized) return
        initialized = true

        XposedServiceHelper.registerListener(object : XposedServiceHelper.OnServiceListener {
            override fun onServiceBind(service: XposedService) {
                synchronized(services) { services.add(service) }
                notifyListeners()
            }

            override fun onServiceDied(service: XposedService) {
                synchronized(services) { services.remove(service) }
                notifyListeners()
            }
        })
    }

    fun observe(lifecycle: Lifecycle?, listener: FrameworkServiceBridge): FrameworkServiceBridge? {
        if (lifecycle?.currentState == Lifecycle.State.DESTROYED) return null

        return ServiceSubscription(lifecycle, listener).also { subscription ->
            runCatching {
                lifecycle?.addObserver(subscription)

                initialize()
                subscription.register()
            }.onFailure {
                subscription.unregister()
            }.getOrThrow()
        }
    }

    fun preferencesOrNull(name: String) = useService(XposedService.PROP_CAP_REMOTE, "remote preferences") {
        it.getRemotePreferences(name)
    }

    fun listRemoteFiles() = useRemoteFiles { it.listRemoteFiles().toList() }
    fun openRemoteFile(name: String) = useRemoteFiles { it.openRemoteFile(name) }
    fun deleteRemoteFile(name: String) = useRemoteFiles { it.deleteRemoteFile(name) }

    private inline fun <T> useRemoteFiles(defaultValue: T? = null, block: (XposedService) -> T) = checkNotNull(
        useService(XposedService.PROP_CAP_REMOTE, "remote files", block) ?: defaultValue
    ) { "libxposed services are unavailable." }

    private inline fun <T> useService(capability: Long, capabilityName: String, block: (XposedService) -> T): T? {
        initialize()
        val current = synchronized(services) {
            check(services.size <= 1) {
                "Multiple Xposed frameworks are connected, $capabilityName have no unambiguous owner."
            }
            services.singleOrNull()
        } ?: return null

        check(current.frameworkProperties and capability != 0L) {
            "This framework does not support $capabilityName."
        }
        return block(current)
    }

    private fun notifyListeners() {
        val available = isAvailable

        listeners.forEach { subscription ->
            runCatching {
                subscription.dispatch(available)
            }.onFailure {
                Log.e("YukiHookAPI", "Service listener failed", it)
            }
        }
    }

    /**
     * Independent lifecycle-aware subscription with ordered callback delivery.
     */
    private class ServiceSubscription(
        private val lifecycle: Lifecycle?,
        private val listener: FrameworkServiceBridge
    ) : FrameworkServiceBridge, DefaultLifecycleObserver {

        private val callbackLock = Any()
        private val pendingEvents = ArrayDeque<Boolean>()

        private var isInitialized = false
        private var isUnregistered = false

        override fun onServiceChanged(available: Boolean) = synchronized(callbackLock) {
            notifyListener(available)
        }

        override fun onDestroy(owner: LifecycleOwner) = unregister()

        override fun unregister() {
            synchronized(callbackLock) {
                if (isUnregistered) return
                isUnregistered = true
                pendingEvents.clear()
            }

            listeners.remove(this)
            lifecycle?.removeObserver(this)
        }

        fun register() = synchronized(callbackLock) {
            if (isUnregistered) return@synchronized

            listeners.add(this)
            notifyListener(isAvailable)
            if (isUnregistered) {
                pendingEvents.clear()
                return@synchronized
            }

            isInitialized = true
            while (pendingEvents.isNotEmpty() && !isUnregistered) runCatching {
                notifyListener(pendingEvents.removeFirst())
            }.onFailure {
                Log.e("YukiHookAPI", "Service listener failed", it)
            }
        }

        fun dispatch(available: Boolean) = synchronized(callbackLock) {
            if (isUnregistered) return@synchronized

            if (!isInitialized)
                pendingEvents.addLast(available)
            else notifyListener(available)
        }

        private fun notifyListener(available: Boolean) {
            if (!isUnregistered) listener.onServiceChanged(available)
        }
    }
}