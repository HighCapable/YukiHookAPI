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
package com.highcapable.yukihookapi.runtime.libxposed

import android.content.SharedPreferences
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
 * Owns the asynchronous module-app service connection independently of the host hook API.
 */
internal object LibXposedService {

    private val services = linkedSetOf<XposedService>()

    private val listeners = CopyOnWriteArraySet<FrameworkServiceBridge>()
    private val listenerLock = Any()

    private var initialized = false

    private val service get() = synchronized(services) { services.singleOrNull() }

    val isAvailable get() = runCatching { service?.apiVersion?.let { it >= XposedInterface.API_101 } ?: false }.getOrDefault(false)
    val frameworkName get() = runCatching { service?.frameworkName }.getOrNull() ?: "unknown"
    val apiLevel get() = runCatching { service?.apiVersion }.getOrNull() ?: -1
    val frameworkVersion get() = runCatching { service?.frameworkVersion }.getOrNull() ?: "unknown"
    val frameworkVersionCode get() = runCatching { service?.frameworkVersionCode?.coerceIn(0, Int.MAX_VALUE.toLong())?.toInt() }.getOrNull() ?: -1

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

        return object : FrameworkServiceBridge, DefaultLifecycleObserver {

            @Volatile
            private var isUnregistered = false

            init {
                runCatching {
                    lifecycle?.addObserver(this)

                    initialize()
                    synchronized(listenerLock) {
                        if (!isUnregistered) {
                            listeners.add(this)
                            onServiceChanged(isAvailable)
                        }
                    }
                }.onFailure { unregister() }.getOrThrow()
            }

            override fun onServiceChanged(available: Boolean) {
                if (!isUnregistered) listener.onServiceChanged(available)
            }

            override fun onDestroy(owner: LifecycleOwner) = unregister()

            override fun unregister() {
                isUnregistered = true

                listeners.remove(this)
                lifecycle?.removeObserver(this)
            }
        }
    }

    fun preferencesOrNull(name: String): SharedPreferences? {
        initialize()
        val current = synchronized(services) {
            check(services.size <= 1) {
                "Multiple Xposed frameworks are connected, remote preferences have no unambiguous owner."
            }
            services.singleOrNull()
        } ?: return null

        check(current.frameworkProperties and XposedService.PROP_CAP_REMOTE != 0L) {
            "This framework does not support remote preferences."
        }
        return current.getRemotePreferences(name)
    }

    private fun notifyListeners() = synchronized(listenerLock) {
        val isAvailable = isAvailable
        listeners.forEach { listener ->
            runCatching {
                listener.onServiceChanged(isAvailable)
            }.onFailure {
                Log.e("YukiHookAPI", "Service listener failed", it)
            }
        }
    }
}