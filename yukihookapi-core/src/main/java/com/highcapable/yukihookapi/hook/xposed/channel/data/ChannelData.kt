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
 * This file is created by fankes on 2022/5/16.
 */
package com.highcapable.yukihookapi.hook.xposed.channel.data

import com.highcapable.yukihookapi.hook.xposed.channel.YukiHookDataChannel
import java.io.Serializable

/**
 * 数据通讯桥键值构造类
 *
 * 这个类是对 [YukiHookDataChannel] 的一个扩展用法
 *
 * 详情请参考 [API 文档 - ChannelData](https://highcapable.github.io/YukiHookAPI/zh-cn/api/public/com/highcapable/yukihookapi/hook/xposed/channel/data/ChannelData)
 *
 * For English version, see [API Document - ChannelData](https://highcapable.github.io/YukiHookAPI/en/api/public/com/highcapable/yukihookapi/hook/xposed/channel/data/ChannelData)
 * @param key 键值
 * @param value 键值数据 - 作为接收数据时可空
 */
data class ChannelData<T>(var key: String, var value: T? = null) : Serializable