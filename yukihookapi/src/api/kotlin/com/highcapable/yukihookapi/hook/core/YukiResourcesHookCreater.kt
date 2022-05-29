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
 * This file is Created by fankes on 2022/5/1.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core

import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.bean.HookResources
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.param.type.HookEntryType
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import com.highcapable.yukihookapi.hook.xposed.bridge.dummy.YukiResources

/**
 * [YukiHookAPI] 的 [Resources] 核心 Hook 实现类
 *
 * @param packageParam 需要传入 [PackageParam] 实现方法调用
 * @param hookResources 要 Hook 的 [HookResources] 实例
 */
class YukiResourcesHookCreater(private val packageParam: PackageParam, @PublishedApi internal val hookResources: HookResources) {

    /** 设置要 Hook 的 Resources */
    @PublishedApi
    internal var preHookResources = HashMap<String, ResourcesHookCreater>()

    /**
     * 注入要 Hook 的 Resources
     * @param tag 可设置标签 - 在发生错误时方便进行调试
     * @param initiate 方法体
     * @return [ResourcesHookCreater.Result]
     */
    inline fun injectResource(tag: String = "Default", initiate: ResourcesHookCreater.() -> Unit) =
        ResourcesHookCreater(tag).apply(initiate).apply { preHookResources[toString()] = this }.build()

    /**
     * Hook 执行入口
     * @throws IllegalStateException 如果必要参数没有被设置
     */
    @PublishedApi
    internal fun hook() {
        if (YukiHookBridge.hasXposedBridge.not()) return
        /** 过滤 [HookEntryType.ZYGOTE] 与 [HookEntryType.RESOURCES] */
        if (packageParam.wrapper?.type == HookEntryType.PACKAGE) return
        if (preHookResources.isEmpty()) error("Hook Resources is empty, hook aborted")
        preHookResources.forEach { (_, r) -> r.hook() }
    }

    /**
     * Hook 核心功能实现类
     *
     * 查找和处理需要 Hook 的 Resources
     * @param tag 当前设置的标签
     */
    inner class ResourcesHookCreater @PublishedApi internal constructor(private val tag: String) {

        /** 是否已经执行 Hook */
        private var isHooked = false

        /**
         * 模块 APP Resources 替换实例
         * @param resId Resources Id
         */
        private inner class ModuleResFwd(var resId: Int)

        /** 是否对当前 [ResourcesHookCreater] 禁止执行 Hook 操作 */
        @PublishedApi
        internal var isDisableCreaterRunHook = false

        /** 当前的查找条件 */
        @PublishedApi
        internal var conditions: ConditionFinder? = null

        /** Hook 出现错误回调 */
        private var onHookFailureCallback: ((Throwable) -> Unit)? = null

        /** 当前的替换值实例 */
        private var replaceInstance: Any? = null

        /** 当前的布局注入实例 */
        private var layoutInstance: (YukiResources.LayoutInflatedParam.() -> Unit)? = null

        /** 直接设置需要替换的 Resources Id */
        var resourceId = -1

        /**
         * 设置 Resources 查找条件
         *
         * 若你设置了 [resourceId] 则此方法将不会被使用
         * @param initiate 条件方法体
         */
        inline fun conditions(initiate: ConditionFinder.() -> Unit) {
            conditions = ConditionFinder().apply(initiate).build()
        }

        /**
         * 替换指定 Resources 为指定的值
         * @param any 可以是任何你想替换的类型 - 但要注意若当前类型不支持可能会报错
         */
        fun replaceTo(any: Any) {
            replaceInstance = any
        }

        /**
         * 替换指定 Resources 为 true
         *
         * - ❗确保目标替换 Resources 的类型为 [Boolean]
         */
        fun replaceToTrue() = replaceTo(any = true)

        /**
         * 替换指定 Resources 为 false
         *
         * - ❗确保目标替换 Resources 的类型为 [Boolean]
         */
        fun replaceToFalse() = replaceTo(any = false)

        /**
         * 替换为当前 Xposed 模块的 Resources
         *
         * 你可以直接使用模块的 R.string.xxx、R.mipmap.xxx、R.drawable.xxx 替换 Hook APP 的 Resources
         * @param resId 当前 Xposed 模块的 Resources Id
         */
        fun replaceToModuleResource(resId: Int) {
            replaceInstance = ModuleResFwd(resId)
        }

        /**
         * 作为装载的布局注入
         * @param initiate [YukiResources.LayoutInflatedParam] 方法体
         */
        fun injectAsLayout(initiate: YukiResources.LayoutInflatedParam.() -> Unit) {
            layoutInstance = initiate
        }

        /**
         * 自动兼容当前替换的 Resources 类型
         * @param any 替换的任意类型
         * @return [Any]
         */
        private fun compat(any: Any?) = if (any is ModuleResFwd) packageParam.moduleAppResources.fwd(any.resId) else any

        /**
         * Hook 创建入口
         * @return [Result]
         */
        @PublishedApi
        internal fun build() = Result()

        /** Hook 执行入口 */
        @PublishedApi
        internal fun hook() {
            if (isHooked) return
            isHooked = true
            if (isDisableCreaterRunHook.not()) runCatching {
                when {
                    conditions == null -> yLoggerE(msg = "You must set the conditions before hook a Resources [$tag]")
                    replaceInstance == null && layoutInstance == null -> yLoggerE(msg = "Resources Hook got null replaceInstance [$tag]")
                    packageParam.wrapper?.type == HookEntryType.RESOURCES && hookResources.instance != null ->
                        if (resourceId == -1) when {
                            layoutInstance != null ->
                                hookResources.instance?.hookLayout(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, layoutInstance!!
                                ) { onHookLogMsg(msg = "Hook Resources Layout $conditions done [$tag]") }
                            else -> hookResources.instance?.setReplacement(
                                packageParam.packageName, conditions!!.type,
                                conditions!!.name, compat(replaceInstance)
                            ) { onHookLogMsg(msg = "Hook Resources Value $conditions done [$tag]") }
                        } else when {
                            layoutInstance != null -> hookResources.instance?.hookLayout(resourceId, layoutInstance!!)
                            { onHookLogMsg(msg = "Hook Resources Layout Id $resourceId done [$tag]") }
                            else -> hookResources.instance?.setReplacement(resourceId, compat(replaceInstance))
                            { onHookLogMsg(msg = "Hook Resources Value Id $resourceId done [$tag]") }
                        }
                    packageParam.wrapper?.type == HookEntryType.ZYGOTE ->
                        if (resourceId == -1) when {
                            layoutInstance != null ->
                                YukiResources.hookSystemWideLayout(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, layoutInstance!!
                                ) { onHookLogMsg(msg = "Hook Wide Resources Layout $conditions done [$tag]") }
                            else -> YukiResources.setSystemWideReplacement(
                                packageParam.packageName, conditions!!.type,
                                conditions!!.name, compat(replaceInstance)
                            ) { onHookLogMsg(msg = "Hook Wide Resources Value $conditions done [$tag]") }
                        } else when {
                            layoutInstance != null -> YukiResources.hookSystemWideLayout(resourceId, layoutInstance!!)
                            { onHookLogMsg(msg = "Hook Wide Resources Layout Id $resourceId done [$tag]") }
                            else -> YukiResources.setSystemWideReplacement(resourceId, compat(replaceInstance))
                            { onHookLogMsg(msg = "Hook Wide Resources Value Id $resourceId done [$tag]") }
                        }
                    else -> yLoggerE(msg = "Resources Hook type is invalid [$tag]")
                }
            }.onFailure {
                if (onHookFailureCallback == null)
                    yLoggerE(msg = "Resources Hook got an Exception [$tag]", e = it)
                else onHookFailureCallback?.invoke(it)
            }
        }

        /**
         * Hook 过程中开启了 [YukiHookAPI.Configs.isDebug] 输出调试信息
         * @param msg 调试日志内容
         */
        private fun onHookLogMsg(msg: String) {
            if (YukiHookAPI.Configs.isDebug) yLoggerI(msg = "[${packageParam.exhibitName}] $msg")
        }

        /**
         * Resources 查找条件实现类
         */
        inner class ConditionFinder @PublishedApi internal constructor() {

            /** Resources 类型 */
            internal var type = ""

            /** 设置 Resources 名称 */
            var name = ""

            /** 设置 Resources 类型为动画 */
            fun anim() {
                type = "anim"
            }

            /** 设置 Resources 类型为属性动画 */
            fun animator() {
                type = "animator"
            }

            /** 设置 Resources 类型为布朗(Boolean) */
            fun bool() {
                type = "bool"
            }

            /** 设置 Resources 类型为颜色(Color) */
            fun color() {
                type = "color"
            }

            /** 设置 Resources 类型为尺寸(Dimention) */
            fun dimen() {
                type = "dimen"
            }

            /** 设置 Resources 类型为 Drawable */
            fun drawable() {
                type = "drawable"
            }

            /** 设置 Resources 类型为整型(Integer) */
            fun integer() {
                type = "integer"
            }

            /** 设置 Resources 类型为布局(Layout) */
            fun layout() {
                type = "layout"
            }

            /** 设置 Resources 类型为 Plurals */
            fun plurals() {
                type = "plurals"
            }

            /** 设置 Resources 类型为字符串(String) */
            fun string() {
                type = "string"
            }

            /** 设置 Resources 类型为 Xml */
            fun xml() {
                type = "xml"
            }

            /** 设置 Resources 类型为位图(Mipmap) */
            fun mipmap() {
                type = "mipmap"
            }

            /**
             * 创建查找对象实例
             * @return [ConditionFinder]
             * @throws IllegalStateException 如果没有设置 [name] 或 [type]
             */
            @PublishedApi
            internal fun build(): ConditionFinder {
                when {
                    name.isBlank() -> error("Resources Hook condition name cannot be empty [$tag]")
                    type.isBlank() -> error("Resources Hook condition type cannot be empty [$tag]")
                }
                return this
            }

            override fun toString() = "[${if (packageParam.wrapper?.type == HookEntryType.ZYGOTE) "android." else ""}R.$type.$name]"
        }

        /**
         * 监听全部 Hook 结果实现类
         *
         * 可在这里处理失败事件监听
         */
        inner class Result internal constructor() {

            /**
             * 创建监听事件方法体
             * @param initiate 方法体
             * @return [Result] 可继续向下监听
             */
            inline fun result(initiate: Result.() -> Unit) = apply(initiate)

            /**
             * 添加执行 Hook 需要满足的条件
             *
             * 不满足条件将直接停止 Hook
             * @param condition 条件方法体
             * @return [Result] 可继续向下监听
             */
            inline fun by(condition: () -> Boolean): Result {
                isDisableCreaterRunHook = (runCatching { condition() }.getOrNull() ?: false).not()
                return this
            }

            /**
             * 监听 Hook 过程发生错误的回调方法
             * @param result 回调错误
             * @return [Result] 可继续向下监听
             */
            fun onHookingFailure(result: (Throwable) -> Unit): Result {
                onHookFailureCallback = result
                return this
            }

            /**
             * 忽略 Hook 过程出现的错误
             * @return [Result] 可继续向下监听
             */
            fun ignoredHookingFailure(): Result {
                onHookingFailure {}
                return this
            }
        }

        override fun toString() = "[tag] $tag [conditions] $conditions [replaceInstance] $replaceInstance [layoutInstance] $layoutInstance"
    }
}