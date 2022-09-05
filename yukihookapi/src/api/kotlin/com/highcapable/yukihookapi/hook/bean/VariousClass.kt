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
 * This file is Created by fankes on 2022/2/10.
 */
package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.factory.toClass

/**
 * 这是一个不确定性 [Class] 类名装载器
 * @param name 可指定多个类名 - 将会自动匹配存在的第一个类名
 */
class VariousClass(vararg var name: String) {

    /**
     * 获取匹配的实体类
     *
     * - 使用当前 [loader] 装载目标 [Class]
     * @param loader 当前 [ClassLoader] - 若留空使用默认 [ClassLoader]
     * @return [Class]
     * @throws IllegalStateException 如果任何 [Class] 都没有匹配到
     */
    fun get(loader: ClassLoader? = null): Class<*> {
        var finalClass: Class<*>? = null
        if (name.isNotEmpty()) run {
            name.forEach {
                runCatching {
                    finalClass = it.toClass(loader)
                    return@run
                }
            }
        }
        return finalClass ?: error("VariousClass match failed of those $this")
    }

    override fun toString(): String {
        var result = ""
        return if (name.isNotEmpty()) {
            name.forEach { result += "\"$it\"," }
            "[${result.substring(0, result.lastIndex)}]"
        } else "[]"
    }
}