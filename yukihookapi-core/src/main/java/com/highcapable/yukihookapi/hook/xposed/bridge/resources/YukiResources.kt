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
@file:Suppress("unused", "DEPRECATION", "DiscouragedApi", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi.hook.xposed.bridge.resources

import android.content.res.Resources
import android.content.res.XResources
import android.graphics.drawable.Drawable
import android.view.View
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources.LayoutInflatedParam
import de.robv.android.xposed.callbacks.XC_LayoutInflated

/**
 * Adapter layer for [XResources].
 * @param baseInstance the original instance.
 */
class YukiResources private constructor(private val baseInstance: XResources) :
    Resources(
        runCatching { baseInstance.assets }.getOrNull(),
        runCatching { baseInstance.displayMetrics }.getOrNull(),
        runCatching { baseInstance.configuration }.getOrNull()
    ) {

        internal companion object {

            /**
             * Creates a [YukiResources] instance from [XResources].
             * @param baseInstance the [XResources] instance.
             * @return [YukiResources]
             */
            internal fun wrapper(baseInstance: XResources) = YukiResources(baseInstance)

            /**
             * Adapts a resource replacement for compatibility.
             * @param replacement the resource replacement.
             * @return [Any] the adapted resource replacement.
             */
            private fun compat(replacement: Any?) = when (replacement) {
                is YukiResForwarder -> replacement.instance
                is Drawable -> object : XResources.DrawableLoader() {
                    override fun newDrawable(res: XResources?, id: Int): Drawable = replacement
                }
                else -> replacement
            }

            /**
             * Replaces resources in Zygote.
             *
             * Adapts [XResources.setSystemWideReplacement].
             * @param resId resources Id.
             * @param replacement the resource replacement.
             * @param callback the callback invoked after successful execution.
             */
            internal fun setSystemWideReplacement(resId: Int, replacement: Any?, callback: () -> Unit = {}) =
                runIfAnyErrors(name = "setSystemWideReplacement") {
                    XResources.setSystemWideReplacement(resId, compat(replacement))
                    callback()
                }

            /**
             * Replaces resources in Zygote.
             *
             * Adapts [XResources.setSystemWideReplacement].
             * @param packageName the package name.
             * @param type the resource type.
             * @param name the resource name.
             * @param replacement the resource replacement.
             * @param callback the callback invoked after successful execution.
             */
            internal fun setSystemWideReplacement(packageName: String, type: String, name: String, replacement: Any?, callback: () -> Unit = {}) =
                runIfAnyErrors(name = "setSystemWideReplacement") {
                    XResources.setSystemWideReplacement(packageName, type, name, compat(replacement))
                    callback()
                }

            /**
             * Hooks a layout resource in Zygote.
             *
             * Adapts [XResources.hookSystemWideLayout].
             * @param resId resources Id.
             * @param initiate the injection block.
             * @param callback the callback invoked after successful execution.
             */
            internal fun hookSystemWideLayout(resId: Int, initiate: LayoutInflatedParam.() -> Unit, callback: () -> Unit = {}) =
                runIfAnyErrors(name = "hookSystemWideLayout") {
                    XResources.hookSystemWideLayout(resId, object : XC_LayoutInflated() {
                        override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                            if (liparam == null) return
                            initiate(LayoutInflatedParam(liparam))
                        }
                    })
                    callback()
                }

            /**
             * Hooks a layout resource in Zygote.
             *
             * Adapts [XResources.hookSystemWideLayout].
             * @param packageName the package name.
             * @param type the resource type.
             * @param name the resource name.
             * @param initiate the injection block.
             * @param callback the callback invoked after successful execution.
             */
            internal fun hookSystemWideLayout(
                packageName: String,
                type: String,
                name: String,
                initiate: LayoutInflatedParam.() -> Unit,
                callback: () -> Unit = {}
            ) = runIfAnyErrors(name = "hookSystemWideLayout") {
                XResources.hookSystemWideLayout(packageName, type, name, object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                        if (liparam == null) return
                        initiate(LayoutInflatedParam(liparam))
                    }
                })
                callback()
            }

            /**
             * Executes while handling any exception.
             * @param name the method name.
             * @param initiate the block to execute.
             */
            private inline fun runIfAnyErrors(name: String, initiate: () -> Unit) {
                runCatching {
                    initiate()
                }.onFailure { YLog.innerE("Failed to execute method \"$name\", maybe your Hook Framework not support Resources Hook", it) }
            }
        }

        /**
         * Replaces a resource.
         *
         * Adapts [XResources.setReplacement].
         * @param resId resources Id.
         * @param replacement the resource replacement.
         * @param callback the callback invoked after successful execution.
         */
        internal fun setReplacement(resId: Int, replacement: Any?, callback: () -> Unit = {}) =
            runIfAnyErrors(name = "setReplacement") {
                baseInstance.setReplacement(resId, compat(replacement))
                callback()
            }

        /**
         * Replaces a resource.
         *
         * Adapts [XResources.setReplacement].
         * @param packageName the package name.
         * @param type the resource type.
         * @param name the resource name.
         * @param replacement the resource replacement.
         * @param callback the callback invoked after successful execution.
         */
        internal fun setReplacement(packageName: String, type: String, name: String, replacement: Any?, callback: () -> Unit = {}) =
            runIfAnyErrors(name = "setReplacement") {
                baseInstance.setReplacement(packageName, type, name, compat(replacement))
                callback()
            }

        /**
         * Hooks a layout resource.
         *
         * Adapts [XResources.hookLayout].
         * @param resId resources Id.
         * @param initiate the injection block.
         * @param callback the callback invoked after successful execution.
         */
        internal fun hookLayout(resId: Int, initiate: LayoutInflatedParam.() -> Unit, callback: () -> Unit = {}) =
            runIfAnyErrors(name = "hookLayout") {
                baseInstance.hookLayout(resId, object : XC_LayoutInflated() {
                    override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                        if (liparam == null) return
                        initiate(LayoutInflatedParam(liparam))
                    }
                })
                callback()
            }

        /**
         * Hooks a layout resource.
         *
         * Adapts [XResources.hookLayout].
         * @param packageName the package name.
         * @param type the resource type.
         * @param name the resource name.
         * @param initiate the injection block.
         * @param callback the callback invoked after successful execution.
         */
        internal fun hookLayout(
            packageName: String,
            type: String,
            name: String,
            initiate: LayoutInflatedParam.() -> Unit,
            callback: () -> Unit = {}
        ) = runIfAnyErrors(name = "hookLayout") {
            baseInstance.hookLayout(packageName, type, name, object : XC_LayoutInflated() {
                override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                    if (liparam == null) return
                    initiate(LayoutInflatedParam(liparam))
                }
            })
            callback()
        }

        /**
         * Target layout resource implementation for the host app.
         * @param baseParam the adapted [XC_LayoutInflated.LayoutInflatedParam].
         */
        class LayoutInflatedParam(private val baseParam: XC_LayoutInflated.LayoutInflatedParam) {

            /**
             * Gets the resource directory qualifier of the currently hooked layout.
             *
             * For example: `layout`, `layout-land`, or `layout-sw600dp`.
             * @return [String]
             */
            val variantName get() = baseParam.variant ?: ""

            /**
             * Gets the currently hooked layout instance.
             * @return [View]
             */
            val currentView get() = baseParam.view ?: error("LayoutInflatedParam View instance got null")

            /**
             * Finds a [View] with the specified ID in the host app by identifier.
             * @param name the ID identifier name.
             * @return [T] or null.
             */
            inline fun <reified T : View> View.findViewByIdentifier(name: String): T? =
                findViewById(baseParam.res.getIdentifier(name, "id", baseParam.resNames.pkg))

            /**
             * Finds a [View] with the specified ID in the host app's currently loaded layout by identifier.
             * @param name the ID identifier name.
             * @return [T] or null.
             */
            inline fun <reified T : View> findViewByIdentifier(name: String) = currentView.findViewByIdentifier<T>(name)

            override fun toString() = "LayoutInflatedParam by $baseParam"
        }

        override fun toString() = "YukiResources by $baseInstance"
    }