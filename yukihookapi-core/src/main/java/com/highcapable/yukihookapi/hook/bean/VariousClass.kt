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
 * This file is created by fankes on 2022/2/10.
 */
package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.factory.toClassOrNull

/**
 * 这是一个不确定性 [Class] 类名装载器
 * @param name 可指定多个类名 - 将会自动匹配存在的第一个类名
 */
class VariousClass(private vararg val name: String) {

    /**
     * 获取匹配的实体类
     *
     * - 使用当前 [loader] 装载目标 [Class]
     * @param loader 当前 [ClassLoader] - 若留空使用默认 [ClassLoader]
     * @param initialize 是否初始化 [Class] 的静态方法块 - 默认否
     * @return [Class]
     * @throws IllegalStateException 如果任何 [Class] 都没有匹配到
     */
    fun get(loader: ClassLoader? = null, initialize: Boolean = false): Class<*> {
        var finalClass: Class<*>? = null
        if (name.isNotEmpty()) run {
            name.forEach {
                finalClass = it.toClassOrNull(loader, initialize)
                if (finalClass != null) return@run
            }
        }
        return finalClass ?: error("VariousClass match failed of those $this")
    }

    /**
     * 获取匹配的实体类
     *
     * - 使用当前 [loader] 装载目标 [Class]
     *
     * 匹配不到 [Class] 会返回 null - 不会抛出异常
     * @param loader 当前 [ClassLoader] - 若留空使用默认 [ClassLoader]
     * @param initialize 是否初始化 [Class] 的静态方法块 - 默认否
     * @return [Class] or null
     */
    fun getOrNull(loader: ClassLoader? = null, initialize: Boolean = false) = runCatching { get(loader, initialize) }.getOrNull()

    override fun toString(): String {
        var result = ""
        return if (name.isNotEmpty()) {
            name.forEach { result += "\"$it\"," }
            "[${result.substring(0, result.lastIndex)}]"
        } else "[]"
    }
}