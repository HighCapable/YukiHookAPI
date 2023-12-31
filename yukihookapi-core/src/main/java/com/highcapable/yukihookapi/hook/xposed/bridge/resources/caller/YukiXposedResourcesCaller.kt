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
 * This file is created by fankes on 2023/1/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.resources.caller

import android.content.res.XResources
import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources

/**
 * Xposed 模块资源钩子 (Resources Hook) 调用类
 */
internal object YukiXposedResourcesCaller {

    /**
     * 从 [XResources] 创建 [YukiResources]
     * @param xResources [XResources] 实例
     * @return [YukiResources] or null
     */
    internal fun createYukiResourcesFromXResources(xResources: XResources?) = xResources?.let { YukiResources.wrapper(it) }
}