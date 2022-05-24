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

import java.io.UTFDataFormatException
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * 封装对 DEX 格式数据的解析操作
 *
 * 参考了 dongliu 的 [apk-parser](https://github.com/hsiafan/apk-parser) 项目
 *
 * Contributed from [conan](https://github.com/meowool-catnip/conan)
 * @param buffer DEX 字节流
 */
internal class DexParser internal constructor(buffer: ByteBuffer) {

    /**
     * 当前字节流
     * @return [ByteBuffer]
     */
    private val buffer = buffer.duplicate().apply { order(ByteOrder.LITTLE_ENDIAN) }

    /**
     * 读取 Bytes
     * @param size 大小
     * @return [ByteArray]
     */
    private fun ByteBuffer.readBytes(size: Int) = ByteArray(size).also { get(it) }

    /**
     * 获取当前 package 下全部的类名
     * @return [Array]
     */
    internal fun parseClassTypes(): Array<String> {
        // read magic
        val magic = String(buffer.readBytes(8))
        if (magic.startsWith(prefix = "dex\n").not()) return arrayOf()
        val version = magic.substring(4, 7).toInt()
        // now the version is 035
        if (version < 35) {
            // version 009 was used for the M3 releases of the Android platform (November–December 2007),
            // and version 013 was used for the M5 releases of the Android platform (February–March 2008)
            error("Dex file version: $version is not supported")
        }
        // read header
        val header = readDexHeader()
        header.version = version
        // read string offsets
        val stringOffsets = readStringOffsets(header.stringIdsOff, header.stringIdsSize)
        // read type ids
        val typeIds = readTypeIds(header.typeIdsOff, header.typeIdsSize)
        // read class ids
        val classIds = readClassIds(header.classDefsOff, header.classDefsSize)
        // read class types
        return Array(classIds.size) { i ->
            val classId = classIds[i]
            val typeId = typeIds[classId]
            val offset = stringOffsets[typeId]
            readStringAtOffset(offset)
        }
    }

    /**
     * 获取 DEX 文件头
     * @return [DexHeader]
     */
    private fun readDexHeader() = DexHeader().apply {
        checksum = buffer.int.toUInt()
        buffer.get(signature)
        fileSize = buffer.int.toUInt()
        headerSize = buffer.int.toUInt()
        endianTag = buffer.int.toUInt()
        linkSize = buffer.int.toUInt()
        linkOff = buffer.int.toUInt()
        mapOff = buffer.int.toUInt()
        stringIdsSize = buffer.int
        stringIdsOff = buffer.int.toUInt()
        typeIdsSize = buffer.int
        typeIdsOff = buffer.int.toUInt()
        protoIdsSize = buffer.int
        protoIdsOff = buffer.int.toUInt()
        fieldIdsSize = buffer.int
        fieldIdsOff = buffer.int.toUInt()
        methodIdsSize = buffer.int
        methodIdsOff = buffer.int.toUInt()
        classDefsSize = buffer.int
        classDefsOff = buffer.int.toUInt()
        dataSize = buffer.int
        dataOff = buffer.int.toUInt()
    }

    /**
     * 读取字符串偏移量
     * @param stringIdsOff 偏移量
     * @param stringIdsSize 偏移大小
     * @return [IntArray]
     */
    private fun readStringOffsets(stringIdsOff: UInt, stringIdsSize: Int): IntArray {
        (buffer as Buffer).position(stringIdsOff.toInt())
        return IntArray(stringIdsSize) { buffer.int }
    }

    /**
     * 读取 Ids 类型偏移量
     * @param typeIdsOff 偏移量
     * @param typeIdsSize 偏移大小
     * @return [IntArray]
     */
    private fun readTypeIds(typeIdsOff: UInt, typeIdsSize: Int): IntArray {
        (buffer as Buffer).position(typeIdsOff.toInt())
        return IntArray(typeIdsSize) { buffer.int }
    }

    /**
     * 读取 Class Ids 偏移量
     * @param classDefsOff 偏移量
     * @param classDefsSize 偏移大小
     * @return [Array]
     */
    private fun readClassIds(classDefsOff: UInt, classDefsSize: Int): Array<Int> {
        (buffer as Buffer).position(classDefsOff.toInt())
        return Array(classDefsSize) {
            val classIdx = buffer.int
            // access_flags, skip
            buffer.int
            // superclass_idx, skip
            buffer.int
            // interfaces_off, skip
            buffer.int
            // source_file_idx, skip
            buffer.int
            // annotations_off, skip
            buffer.int
            // class_data_off, skip
            buffer.int
            // static_values_off, skip
            buffer.int
            classIdx
        }
    }

    /**
     * 读取偏移量的字符串
     * @param offset 偏移量
     * @return [String]
     */
    private fun readStringAtOffset(offset: Int): String {
        (buffer as Buffer).position(offset)
        return readString(readULEB128Int())
    }

    /**
     * 读取 ULEB128 整型
     * @return [Int]
     */
    private fun readULEB128Int(): Int {
        var ret = 0
        var count = 0
        var byte: Int
        do {
            if (count > 4) error("read varints error.")
            byte = buffer.get().toInt()
            ret = ret or (byte and 0x7f shl count * 7)
            count++
        } while (byte and 0x80 != 0)
        return ret
    }

    /**
     * 读取字符串
     * @param len 长度
     * @return [String]
     */
    private fun readString(len: Int): String {
        val chars = CharArray(len)
        for (i in 0 until len) {
            val byte = buffer.get().toInt()
            when {
                // ascii char
                byte and 0x80 == 0 -> chars[i] = byte.toChar()
                // read one more
                byte and 0xe0 == 0xc0 -> {
                    val b = buffer.get().toInt()
                    chars[i] = (byte and 0x1F shl 6 or (b and 0x3F)).toChar()
                }
                byte and 0xf0 == 0xe0 -> {
                    val b = buffer.get().toInt()
                    val c = buffer.get().toInt()
                    chars[i] = (byte and 0x0F shl 12 or (b and 0x3F shl 6) or (c and 0x3F)).toChar()
                }
                else -> throw UTFDataFormatException()
            }
        }
        return String(chars)
    }
}