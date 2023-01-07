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
 * This file is Created by fankes on 2023/1/6.
 */
package com.highcapable.yukihookapi.hook.xposed.channel.data.wrapper

import com.highcapable.yukihookapi.hook.xposed.channel.data.ChannelData
import java.io.Serializable
import java.util.*

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
) : Serializable {

    internal companion object {

        /**
         * 创建新的包装实例 ID
         * @return [String]
         */
        internal fun createWrapperId() = Random().let { random ->
            var randomId = ""
            for (i in 0..5) randomId += ((if (random.nextInt(2) % 2 == 0) 65 else 97) + random.nextInt(26)).toChar()
            "$randomId${System.currentTimeMillis()}"
        }
    }
}