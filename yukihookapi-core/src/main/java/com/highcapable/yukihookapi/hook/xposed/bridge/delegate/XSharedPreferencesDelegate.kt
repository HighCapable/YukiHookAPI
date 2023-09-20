/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * This file is Created by fankes on 2023/4/16.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.delegate

import de.robv.android.xposed.XSharedPreferences

/**
 * [XSharedPreferences] 代理类
 * @param packageName APP 包名
 * @param prefFileName 存储文件名
 */
internal class XSharedPreferencesDelegate private constructor(private val packageName: String, private val prefFileName: String) {

    internal companion object {

        /**
         * 创建代理类
         * @param packageName APP 包名
         * @param prefFileName 存储文件名
         * @return [XSharedPreferencesDelegate]
         */
        fun from(packageName: String, prefFileName: String) = XSharedPreferencesDelegate(packageName, prefFileName)
    }

    /**
     * 获取实例
     * @return [XSharedPreferences]
     */
    internal val instance by lazy { XSharedPreferences(packageName, prefFileName) }
}