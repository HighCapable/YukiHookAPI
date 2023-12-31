/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 * 对接 [XResources] 的中间层实例
 * @param baseInstance 原始实例
 */
class YukiResources private constructor(private val baseInstance: XResources) :
    Resources(
        runCatching { baseInstance.assets }.getOrNull(),
        runCatching { baseInstance.displayMetrics }.getOrNull(),
        runCatching { baseInstance.configuration }.getOrNull()
    ) {

    internal companion object {

        /**
         * 从 [XResources] 创建 [YukiResources] 实例
         * @param baseInstance [XResources] 实例
         * @return [YukiResources]
         */
        internal fun wrapper(baseInstance: XResources) = YukiResources(baseInstance)

        /**
         * 兼容对接替换 Resources
         * @param replacement 替换 Resources
         * @return [Any] 兼容后的替换 Resources
         */
        private fun compat(replacement: Any?) = when (replacement) {
            is YukiResForwarder -> replacement.instance
            is Drawable -> object : XResources.DrawableLoader() {
                override fun newDrawable(res: XResources?, id: Int): Drawable = replacement
            }
            else -> replacement
        }

        /**
         * 在 Zygote 中替换 Resources
         *
         * 对接 [XResources.setSystemWideReplacement]
         * @param resId Resources Id
         * @param replacement 替换 Resources
         * @param callback 是否成功执行回调
         */
        internal fun setSystemWideReplacement(resId: Int, replacement: Any?, callback: () -> Unit = {}) =
            runIfAnyErrors(name = "setSystemWideReplacement") {
                XResources.setSystemWideReplacement(resId, compat(replacement))
                callback()
            }

        /**
         * 在 Zygote 中替换 Resources
         *
         * 对接 [XResources.setSystemWideReplacement]
         * @param packageName 包名
         * @param type Resources 类型
         * @param name Resources 名称
         * @param replacement 替换 Resources
         * @param callback 是否成功执行回调
         */
        internal fun setSystemWideReplacement(packageName: String, type: String, name: String, replacement: Any?, callback: () -> Unit = {}) =
            runIfAnyErrors(name = "setSystemWideReplacement") {
                XResources.setSystemWideReplacement(packageName, type, name, compat(replacement))
                callback()
            }

        /**
         * 在 Zygote 中注入布局 Resources
         *
         * 对接 [XResources.hookSystemWideLayout]
         * @param resId Resources Id
         * @param initiate 注入方法体
         * @param callback 是否成功执行回调
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
         * 在 Zygote 中注入布局 Resources
         *
         * 对接 [XResources.hookSystemWideLayout]
         * @param packageName 包名
         * @param type Resources 类型
         * @param name Resources 名称
         * @param initiate 注入方法体
         * @param callback 是否成功执行回调
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
         * 忽略异常执行
         * @param name 方法名称
         * @param initiate 方法体
         */
        private inline fun runIfAnyErrors(name: String, initiate: () -> Unit) {
            runCatching {
                initiate()
            }.onFailure { YLog.innerE("Failed to execute method \"$name\", maybe your Hook Framework not support Resources Hook", it) }
        }
    }

    /**
     * 执行替换 Resources
     *
     * 对接 [XResources.setReplacement]
     * @param resId Resources Id
     * @param replacement 替换 Resources
     * @param callback 是否成功执行回调
     */
    internal fun setReplacement(resId: Int, replacement: Any?, callback: () -> Unit = {}) =
        runIfAnyErrors(name = "setReplacement") {
            baseInstance.setReplacement(resId, compat(replacement))
            callback()
        }

    /**
     * 执行替换 Resources
     *
     * 对接 [XResources.setReplacement]
     * @param packageName 包名
     * @param type Resources 类型
     * @param name Resources 名称
     * @param replacement 替换 Resources
     * @param callback 是否成功执行回调
     */
    internal fun setReplacement(packageName: String, type: String, name: String, replacement: Any?, callback: () -> Unit = {}) =
        runIfAnyErrors(name = "setReplacement") {
            baseInstance.setReplacement(packageName, type, name, compat(replacement))
            callback()
        }

    /**
     * 执行注入布局 Resources
     *
     * 对接 [XResources.hookLayout]
     * @param resId Resources Id
     * @param initiate 注入方法体
     * @param callback 是否成功执行回调
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
     * 执行注入布局 Resources
     *
     * 对接 [XResources.hookLayout]
     * @param packageName 包名
     * @param type Resources 类型
     * @param name Resources 名称
     * @param initiate 注入方法体
     * @param callback 是否成功执行回调
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
     * 装载 Hook APP 的目标布局 Resources 实现类
     * @param baseParam 对接 [XC_LayoutInflated.LayoutInflatedParam]
     */
    class LayoutInflatedParam(private val baseParam: XC_LayoutInflated.LayoutInflatedParam) {

        /**
         * 获取当前被 Hook 的布局装载目录名称
         *
         * 例如：layout、layout-land、layout-sw600dp
         * @return [String]
         */
        val variantName get() = baseParam.variant ?: ""

        /**
         * 获取当前被 Hook 的布局实例
         * @return [View]
         */
        val currentView get() = baseParam.view ?: error("LayoutInflatedParam View instance got null")

        /**
         * 使用 Identifier 查找 Hook APP 指定 Id 的 [View]
         * @param name Id 的 Identifier 名称
         * @return [T] or null
         */
        inline fun <reified T : View> View.findViewByIdentifier(name: String): T? =
            findViewById(baseParam.res.getIdentifier(name, "id", baseParam.resNames.pkg))

        /**
         * 使用 Identifier 查找 Hook APP 当前装载布局中指定 Id 的 [View]
         * @param name Id 的 Identifier 名称
         * @return [T] or null
         */
        inline fun <reified T : View> findViewByIdentifier(name: String) = currentView.findViewByIdentifier<T>(name)

        override fun toString() = "LayoutInflatedParam by $baseParam"
    }

    override fun toString() = "YukiResources by $baseInstance"
}