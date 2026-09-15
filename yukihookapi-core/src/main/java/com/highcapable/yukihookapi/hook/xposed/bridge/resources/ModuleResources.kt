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
 * This file is created by fankes on 2022/4/29.
 */
@file:Suppress("DEPRECATION", "ClassName")

package com.highcapable.yukihookapi.hook.xposed.bridge.resources

import android.content.res.AssetManager
import android.content.res.Resources
import android.content.res.loader.ResourcesLoader
import android.content.res.loader.ResourcesProvider
import android.os.ParcelFileDescriptor
import androidx.annotation.RequiresApi
import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import com.highcapable.kavaref.KavaRef.Companion.resolve
import com.highcapable.yukihookapi.hook.core.api.reflect.AndroidHiddenApiBypassResolver
import com.highcapable.yukihookapi.hook.log.YLog
import java.io.File

/**
 * The module APK resources.
 */
class ModuleResources private constructor(private val baseInstance: Resources) : Resources(
    baseInstance.assets,
    baseInstance.displayMetrics,
    baseInstance.configuration
) {

    internal companion object {

        /**
         * Loads the module APK into a separate [AssetManager] without modifying host resources.
         * @return [ModuleResources] or null when the resources cannot be created.
         */
        fun from(path: String) = runCatching {
            val assets = AssetManager::class.resolve()
                .optional(silent = true)
                .firstConstructorOrNull { emptyParameters() }
                ?.create() ?: error("Unable to create AssetManager.")
            val system = getSystem()
            val resources = Resources(assets, system.displayMetrics, system.configuration)

            addModuleAssetPath(resources, path).getOrThrow()
            ModuleResources(resources)
        }.onFailure {
            YLog.innerE("Failed to create module resources from $path", it)
        }.getOrNull()

        /**
         * Injects the module APK into host [Resources] synchronously.
         *
         * On Android 11 (API 30) or later, call this function on the main thread.
         * Resource loaders participate in system resource rebuilding, older Android versions use asset paths.
         * @param resources the host resources.
         * @param path the module APK path.
         * @return [Result]
         */
        fun injectModuleResources(resources: Resources, path: String) =
            if (AndroidVersion.isAtLeast(AndroidVersion.R)) runCatching {
                require(path.isNotBlank()) { "Module APK path is unavailable." }
                Api30_Impl.inject(resources, path)
            } else addModuleAssetPath(resources, path)

        /**
         * Adds the module APK path to an existing [Resources] instance.
         * @param resources the target resources.
         * @param path the module APK path.
         * @return [Result]
         */
        private fun addModuleAssetPath(resources: Resources, path: String) = runCatching {
            require(path.isNotBlank()) { "Module APK path is unavailable." }

            // `addAssetPath` is hidden on Android. Use the existing hidden-API resolver rather
            // than `XModuleResources`, which is unavailable to modern modules.
            val cookie = resources.assets.asResolver()
                .processor(AndroidHiddenApiBypassResolver.get())
                .optional(silent = true)
                .firstMethodOrNull {
                    name = "addAssetPath"
                    parameters(String::class)
                }?.invoke<Int>(path)
            check(cookie != null && cookie != 0) { "Unable to load module resources from $path" }
        }

        /**
         * Keep API 30 types out of the initialization path used by older Android versions.
         */
        @RequiresApi(AndroidVersion.R)
        private object Api30_Impl {

            private var loader: ResourcesLoader? = null

            fun inject(resources: Resources, path: String) {
                val moduleLoader = loader ?: ParcelFileDescriptor.open(File(path), ParcelFileDescriptor.MODE_READ_ONLY).use { descriptor ->
                    ResourcesLoader().apply {
                        // The provider duplicates the descriptor, so closing it does not unload the APK.
                        addProvider(ResourcesProvider.loadFromApk(descriptor))
                    }.also { loader = it }
                }

                try {
                    // Unlike `addAssetPath`, loaders are recorded in `ResourcesKey` and survive `ResourcesImpl` rebuilding.
                    // Reusing the same loader also lets `Resources.addLoaders` handle duplicate injection.
                    resources.addLoaders(moduleLoader)
                } catch (cause: IllegalArgumentException) {
                    if (cause.message != "Cannot modify resource loaders of ResourcesImpl not registered with ResourcesManager") throw cause

                    // A framework-replaced `ResourcesImpl` may no longer have a `ResourcesManager` record.
                    // Only this registration mismatch falls back to mutating its current `AssetManager`.
                    addModuleAssetPath(resources, path).getOrThrow()
                }
            }
        }
    }

    override fun toString() = "ModuleResources($baseInstance)"
}