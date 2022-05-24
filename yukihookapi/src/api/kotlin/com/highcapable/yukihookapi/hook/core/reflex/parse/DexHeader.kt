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
 * This file is Created by fankes on 2022/5/24.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core.reflex.parse

/**
 * DEX 格式的文件头
 *
 * 参考来源 [Dex Format](https://source.android.com/devices/tech/dalvik/dex-format)
 *
 * Contributed from [conan](https://github.com/meowool-catnip/conan)
 */
internal class DexHeader internal constructor() {

    internal var version = 0
    internal var checksum = 0u
    internal var signature = ByteArray(kSHA1DigestLen)
    internal var fileSize = 0u
    internal var headerSize = 0u
    internal var endianTag = 0u
    internal var linkSize = 0u
    internal var linkOff = 0u
    internal var mapOff = 0u
    internal var stringIdsSize = 0
    internal var stringIdsOff = 0u
    internal var typeIdsSize = 0
    internal var typeIdsOff = 0u
    internal var protoIdsSize = 0
    internal var protoIdsOff = 0u
    internal var fieldIdsSize = 0
    internal var fieldIdsOff = 0u
    internal var methodIdsSize = 0
    internal var methodIdsOff = 0u
    internal var classDefsSize = 0
    internal var classDefsOff = 0u
    internal var dataSize = 0
    internal var dataOff = 0u

    internal companion object {

        internal const val kSHA1DigestLen = 20
        internal const val kSHA1DigestOutputLen = kSHA1DigestLen * 2 + 1
    }
}