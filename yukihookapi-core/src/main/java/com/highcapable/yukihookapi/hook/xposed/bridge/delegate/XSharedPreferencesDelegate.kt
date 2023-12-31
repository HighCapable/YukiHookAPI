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
 * This file is created by fankes on 2023/4/16.
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