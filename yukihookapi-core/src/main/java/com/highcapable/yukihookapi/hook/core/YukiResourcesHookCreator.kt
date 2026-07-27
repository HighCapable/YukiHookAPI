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
 * This file is created by fankes on 2022/5/1.
 */
@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE",
    "DiscouragedApi", "UseCompatLoadingForDrawables", "DEPRECATION"
)

package com.highcapable.yukihookapi.hook.core

import android.content.res.Resources
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.bean.HookResources
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Core [Resources] Hook implementation for [YukiHookAPI].
 *
 * @param packageParam the [PackageParam] used to perform method calls.
 * @param hookResources the [HookResources] instance to hook.
 */
class YukiResourcesHookCreator internal constructor(internal val packageParam: PackageParam, internal val hookResources: HookResources) {

    /** The resources to hook. */
    private var preHookResources = mutableMapOf<String, ResourcesHookCreator>()

    /**
     * Injects a resource to hook.
     * @param tag the optional label used to simplify debugging when an error occurs.
     * @param initiate the injection block.
     * @return [ResourcesHookCreator.Result]
     */
    inline fun injectResource(tag: String = "Default", initiate: ResourcesHookCreator.() -> Unit) =
        ResourcesHookCreator(tag).apply(initiate).apply { preHookResources[toString()] = this }.build()

    /** Hook execution entry point. */
    internal fun hook() {
        if (HookApiCategoryHelper.hasAvailableHookApi.not()) return
        // Filters [HookEntryType.ZYGOTE] and [HookEntryType.RESOURCES].
        if (packageParam.wrapper?.type == HookEntryType.PACKAGE) return
        if (preHookResources.isEmpty()) return YLog.innerW("Hook Resources is empty, hook aborted")
        preHookResources.forEach { (_, r) -> r.hook() }
    }

    /**
     * Core Hook implementation.
     *
     * Finds and processes the resources to hook.
     * @param tag the current label.
     */
    inner class ResourcesHookCreator internal constructor(private val tag: String) {

        /** Whether the Hook has already been executed. */
        private var isHooked = false

        /**
         * Module app resource replacement.
         * @param resId resources Id.
         */
        private inner class ModuleResFwd(var resId: Int)

        /** Whether Hook operations are disabled for the current [ResourcesHookCreator]. */
        private var isDisableCreatorRunHook = false

        /** The current finder conditions. */
        private var conditions: ConditionFinder? = null

        /** Callback for Hook failures. */
        private var onHookFailureCallback: ((Throwable) -> Unit)? = null

        /** The current replacement value. */
        private var replaceInstance: Any? = null

        /** The current replacement-value callback. */
        private var replaceCallback: ((Any) -> Any?)? = null

        /** The current forwarded replacement-value callback. */
        private var replaceFwdCallback: ((Any) -> Int)? = null

        /** The current layout injection block. */
        private var layoutInstance: (YukiResources.LayoutInflatedParam.() -> Unit)? = null

        /** Directly sets the resource ID to replace. */
        var resourceId = -1

        /**
         * Sets the resource finder conditions.
         *
         * This method is not used when [resourceId] is set.
         * @param initiate the condition block.
         */
        inline fun conditions(initiate: ConditionFinder.() -> Unit) {
            conditions = ConditionFinder().apply(initiate).build()
        }

        /**
         * Replaces the specified resource with the given value.
         * @param any any replacement type. An unsupported type may cause an error.
         */
        fun replaceTo(any: Any) {
            replaceInstance = any
        }

        /**
         * Replaces the specified resource with true.
         *
         * - Ensure that the target resource type is [Boolean].
         */
        fun replaceToTrue() = replaceTo(any = true)

        /**
         * Replaces the specified resource with false.
         *
         * - Ensure that the target resource type is [Boolean].
         */
        fun replaceToFalse() = replaceTo(any = false)

        /**
         * Replaces the target with a resource from the current Xposed module.
         *
         * Module resources such as `R.string.xxx`, `R.mipmap.xxx`, and `R.drawable.xxx` can directly replace host app resources.
         * @param resId the current Xposed module's resource ID.
         */
        fun replaceToModuleResource(resId: Int) {
            replaceInstance = ModuleResFwd(resId)
        }

        /**
         * Replaces the specified resource with the value returned by a callback.
         *
         * - This method supports only certain types, such as [String] and [Boolean].
         *
         * - This method is not supported for [HookEntryType.ZYGOTE].
         * @param result the callback receiving the original value and returning the replacement.
         */
        fun replaceTo(result: (original: Any) -> Any?) {
            replaceCallback = result
        }

        /**
         * Replaces the target with a resource from the current Xposed module.
         *
         * Module resources such as `R.string.xxx`, `R.mipmap.xxx`, and `R.drawable.xxx` can directly replace host app resources.
         *
         * - This method supports only certain types, such as [String] and [Boolean].
         *
         * - This method is not supported for [HookEntryType.ZYGOTE].
         * @param result the callback receiving the original value and returning the current Xposed module's resource ID.
         */
        fun replaceToModuleResource(result: (original: Any) -> Int) {
            replaceFwdCallback = result
        }

        /**
         * Hooks the loaded layout.
         * @param initiate the [YukiResources.LayoutInflatedParam] block.
         */
        fun injectAsLayout(initiate: YukiResources.LayoutInflatedParam.() -> Unit) {
            layoutInstance = initiate
        }

        /**
         * Adapts the current replacement resource type for compatibility.
         * @param any the replacement value of any type.
         * @return [Any]
         */
        private fun compat(any: Any?) = if (any is ModuleResFwd) packageParam.moduleAppResources.fwd(any.resId) else any

        /**
         * Hook creation entry point.
         * @return [Result]
         */
        internal fun build() = Result()

        /** Hook execution entry point. */
        internal fun hook() {
            if (isHooked) return
            isHooked = true
            if (isDisableCreatorRunHook.not()) runCatching {
                when {
                    conditions == null -> YLog.innerE("You must set the conditions before hook a Resources [$tag]")
                    replaceInstance == null && replaceCallback == null && replaceFwdCallback == null && layoutInstance == null ->
                        YLog.innerE("Resources Hook got null replaceInstance [$tag]")
                    packageParam.wrapper?.type == HookEntryType.RESOURCES && hookResources.instance != null ->
                        if (resourceId == -1) when {
                            layoutInstance != null ->
                                hookResources.instance?.hookLayout(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, layoutInstance!!
                                ) { YLog.innerD("Hook Resources Layout $conditions done [$tag]") }
                            replaceCallback != null -> conditionsResValue?.let {
                                hookResources.instance?.setReplacement(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, compat(replaceCallback!!(it))
                                ) { YLog.innerD("Hook Resources Value $conditions done [$tag]") }
                            } ?: YLog.innerW("Resources type \"${conditions!!.type}\" not support replaceTo { ... } function")
                            replaceFwdCallback != null -> conditionsResValue?.let {
                                hookResources.instance?.setReplacement(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, compat(ModuleResFwd(replaceFwdCallback!!(it)))
                                ) { YLog.innerD("Hook Resources Value $conditions done [$tag]") }
                            } ?: YLog.innerW("Resources type \"${conditions!!.type}\" not support replaceToModuleResource { ... } function")
                            else -> hookResources.instance?.setReplacement(
                                packageParam.packageName, conditions!!.type,
                                conditions!!.name, compat(replaceInstance)
                            ) { YLog.innerD("Hook Resources Value $conditions done [$tag]") }
                        } else when {
                            layoutInstance != null -> hookResources.instance?.hookLayout(resourceId, layoutInstance!!) {
                                YLog.innerD("Hook Resources Layout Id $resourceId done [$tag]")
                            }
                            else -> hookResources.instance?.setReplacement(resourceId, compat(replaceInstance)) {
                                YLog.innerD("Hook Resources Value Id $resourceId done [$tag]")
                            }
                        }
                    packageParam.wrapper?.type == HookEntryType.ZYGOTE ->
                        if (resourceId == -1) when {
                            layoutInstance != null ->
                                YukiResources.hookSystemWideLayout(
                                    packageParam.packageName, conditions!!.type,
                                    conditions!!.name, layoutInstance!!
                                ) { YLog.innerD("Hook Wide Resources Layout $conditions done [$tag]") }
                            replaceCallback != null -> YLog.innerW("Zygote not support replaceTo { ... } function")
                            replaceFwdCallback != null -> YLog.innerW("Zygote not support replaceToModuleResource { ... } function")
                            else -> YukiResources.setSystemWideReplacement(
                                packageParam.packageName, conditions!!.type,
                                conditions!!.name, compat(replaceInstance)
                            ) { YLog.innerD("Hook Wide Resources Value $conditions done [$tag]") }
                        } else when {
                            layoutInstance != null -> YukiResources.hookSystemWideLayout(resourceId, layoutInstance!!) {
                                YLog.innerD("Hook Wide Resources Layout Id $resourceId done [$tag]")
                            }
                            else -> YukiResources.setSystemWideReplacement(resourceId, compat(replaceInstance)) {
                                YLog.innerD("Hook Wide Resources Value Id $resourceId done [$tag]")
                            }
                        }
                    else -> YLog.innerE("Resources Hook type is invalid [$tag]")
                }
            }.onFailure {
                if (onHookFailureCallback == null)
                    YLog.innerE("Resources Hook got an exception [$tag]", it)
                else onHookFailureCallback?.invoke(it)
            }
        }

        /**
         * Gets the original host [Resources] value matching the finder conditions.
         * @return [Any] or null.
         */
        private val conditionsResValue get(): Any? {
            val appResources = packageParam.appResources ?: error("Failed to got Host Resources")
            val original = runCatching {
                appResources.getIdentifier(conditions!!.name, conditions!!.type, packageParam.packageName)
            }.getOrNull() ?: -1
            return when (conditions!!.type) {
                "anim" -> appResources.getAnimation(original)
                "bool" -> appResources.getBoolean(original)
                "color" -> appResources.getColor(original)
                "dimen" -> appResources.getDimension(original)
                "drawable", "mipmap" -> appResources.getDrawable(original)
                "integer" -> appResources.getInteger(original)
                "layout" -> appResources.getLayout(original)
                "string" -> appResources.getString(original)
                "xml" -> appResources.getXml(original)
                else -> null
            }
        }

        /**
         * Resource finder condition implementation.
         */
        inner class ConditionFinder internal constructor() {

            /** The resource type. */
            internal var type = ""

            /** Sets the resource name. */
            var name = ""

            /** Sets the resource type to animation. */
            fun anim() {
                type = "anim"
            }

            /** Sets the resource type to property animation. */
            fun animator() {
                type = "animator"
            }

            /** Sets the resource type to Boolean. */
            fun bool() {
                type = "bool"
            }

            /** Sets the resource type to color. */
            fun color() {
                type = "color"
            }

            /** Sets the resource type to dimension. */
            fun dimen() {
                type = "dimen"
            }

            /** Sets the resource type to Drawable. */
            fun drawable() {
                type = "drawable"
            }

            /** Sets the resource type to integer. */
            fun integer() {
                type = "integer"
            }

            /** Sets the resource type to layout. */
            fun layout() {
                type = "layout"
            }

            /** Sets the resource type to plurals. */
            fun plurals() {
                type = "plurals"
            }

            /** Sets the resource type to string. */
            fun string() {
                type = "string"
            }

            /** Sets the resource type to XML. */
            fun xml() {
                type = "xml"
            }

            /** Sets the resource type to mipmap. */
            fun mipmap() {
                type = "mipmap"
            }

            /** Sets the resource type to array. */
            fun array() {
                type = "array"
            }

            /**
             * Creates the finder instance.
             * @return [ConditionFinder]
             * @throws IllegalStateException if [name] or [type] is not set.
             */
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
         * Listener implementation for all Hook results.
         *
         * Failure events can be handled here.
         */
        inner class Result internal constructor() {

            /**
             * Creates the listener event block.
             * @param initiate the listener block.
             * @return [Result] this result for chaining.
             */
            inline fun result(initiate: Result.() -> Unit) = apply(initiate)

            /**
             * Adds a condition required to execute the Hook.
             *
             * Hook execution stops immediately when the condition is not satisfied.
             * @param condition the condition block.
             * @return [Result] this result for chaining.
             */
            inline fun by(condition: () -> Boolean): Result {
                isDisableCreatorRunHook = (runCatching { condition() }.getOrNull() ?: false).not()
                return this
            }

            /**
             * Listens for errors during the Hook process.
             * @param result the error callback.
             * @return [Result] this result for chaining.
             */
            fun onHookingFailure(result: (Throwable) -> Unit): Result {
                onHookFailureCallback = result
                return this
            }

            /**
             * Ignores errors during the Hook process.
             * @return [Result] this result for chaining.
             */
            fun ignoredHookingFailure(): Result {
                onHookingFailure {}
                return this
            }
        }

        override fun toString() = "[tag] $tag [conditions] $conditions [replaceInstance] $replaceInstance [layoutInstance] $layoutInstance"
    }
}