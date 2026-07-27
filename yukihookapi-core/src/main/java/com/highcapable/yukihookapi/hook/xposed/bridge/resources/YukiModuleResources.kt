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
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.xposed.bridge.resources

import android.content.res.Resources
import android.content.res.XModuleResources
import android.content.res.XResForwarder

/**
 * Wraps a [XModuleResources] instance.
 * @param baseInstance the original instance.
 */
class YukiModuleResources private constructor(private val baseInstance: XModuleResources) :
    Resources(
        runCatching { baseInstance.assets }.getOrNull(),
        runCatching { baseInstance.displayMetrics }.getOrNull(),
        runCatching { baseInstance.configuration }.getOrNull()
    ) {

        internal companion object {

            /**
             * Delegates to [XModuleResources.createInstance].
             *
             * Creates linked [YukiModuleResources] and [XModuleResources] instances.
             * @param path the Xposed module APK path.
             * @return [YukiModuleResources]
             */
            internal fun wrapper(path: String) = YukiModuleResources(XModuleResources.createInstance(path, null))
        }

        /**
         * Delegates to [XModuleResources.fwd].
         *
         * Creates linked [YukiResForwarder] and [XResForwarder] instances.
         * @param resId resources Id.
         * @return [YukiResForwarder]
         */
        fun fwd(resId: Int) = YukiResForwarder.wrapper(baseInstance.fwd(resId))

        override fun toString() = "YukiModuleResources by $baseInstance"
    }