/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by fankes on 2022/4/29.
 */
@file:Suppress("DEPRECATION", "unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.dummy

import android.content.res.Resources
import android.content.res.XResources
import android.graphics.drawable.Drawable
import android.view.View
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiResources.LayoutInflatedParam
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
        internal fun createFromXResources(baseInstance: XResources) = YukiResources(baseInstance)

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
            }.onFailure { yLoggerE(msg = "Failed to execute method \"$name\", maybe your Hook Framework not support Resources Hook", it) }
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
    class LayoutInflatedParam(@PublishedApi internal val baseParam: XC_LayoutInflated.LayoutInflatedParam) {

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