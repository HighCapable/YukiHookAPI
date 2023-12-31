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
 * This file is created by fankes on 2023/1/6.
 */
package com.highcapable.yukihookapi.hook.xposed.channel.data.wrapper

import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import java.io.Serializable

/**
 * 数据通讯桥键值数据包装类
 * @param wrapperId 包装实例 ID
 * @param isSegmentsType 是否为分段数据
 * @param segmentsSize 分段数据总大小 (长度)
 * @param segmentsIndex 分段数据当前接收到的下标
 * @param instance 原始数据实例
 */
internal data class ChannelDataWrapper<T>(
    val wrapperId: String,
    val isSegmentsType: Boolean,
    val segmentsSize: Int,
    val segmentsIndex: Int,
    val instance: ChannelData<T>
) : Serializable