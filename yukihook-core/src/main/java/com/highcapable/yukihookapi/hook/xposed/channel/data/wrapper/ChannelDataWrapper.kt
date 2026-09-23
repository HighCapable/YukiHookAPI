/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
 * Wraps key-value data sent through the data channel.
 * @param wrapperId the wrapper instance ID.
 * @param isSegmented whether the payload is segmented.
 * @param segmentCount the total number of payload segments.
 * @param segmentIndex the index of the currently received segment.
 * @param instance the original data instance.
 */
internal data class ChannelDataWrapper<T>(
    val wrapperId: String,
    val isSegmented: Boolean,
    val segmentCount: Int,
    val segmentIndex: Int,
    val instance: ChannelData<T>
) : Serializable