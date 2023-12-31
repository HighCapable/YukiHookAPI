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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.resources

import android.content.res.Resources
import android.content.res.XResForwarder

/**
 * 对接 [XResForwarder] 的中间层实例
 * @param baseInstance 原始实例
 */
class YukiResForwarder private constructor(private val baseInstance: XResForwarder) {

    internal companion object {

        /**
         * 从 [XResForwarder] 创建 [YukiResForwarder] 实例
         * @param baseInstance [XResForwarder] 实例
         * @return [YukiResForwarder]
         */
        internal fun wrapper(baseInstance: XResForwarder) = YukiResForwarder(baseInstance)
    }

    /**
     * 获得 [XResForwarder] 实例
     * @return [XResForwarder]
     */
    internal val instance get() = baseInstance

    /**
     * 获得当前 Resources Id
     * @return [Int]
     */
    val id get() = baseInstance.id

    /**
     * 获得当前 Resources
     * @return [Resources]
     * @throws IllegalStateException 如果 [XResForwarder] 出现问题
     */
    val resources get() = baseInstance.resources ?: error("XResForwarder is invalid")

    override fun toString() = "YukiResForwarder by $baseInstance"
}