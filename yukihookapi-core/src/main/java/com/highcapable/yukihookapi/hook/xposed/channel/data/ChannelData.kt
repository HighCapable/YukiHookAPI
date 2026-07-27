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
 * This file is created by fankes on 2022/5/16.
 */
package com.highcapable.yukihookapi.hook.xposed.channel.data

import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import java.io.Serializable

/**
 * Defines a typed key-value entry for the data channel.
 *
 * This class provides an extended usage pattern for [YukiHookDataChannel].
 * @param key the channel key.
 * @param value the channel value, which may be null when receiving data.
 */
data class ChannelData<T>(var key: String, var value: T? = null) : Serializable