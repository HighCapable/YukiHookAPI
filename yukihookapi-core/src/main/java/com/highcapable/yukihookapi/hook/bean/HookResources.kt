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
 * This file is created by fankes on 2022/5/1.
 */
package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.xposed.bridge.resources.YukiResources

/**
 * 创建一个当前 Hook 的 [YukiResources] 接管类
 * @param instance 实例
 */
class HookResources internal constructor(var instance: YukiResources? = null) {

    override fun toString() = "[instance] $instance"
}