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

import android.content.pm.ApplicationInfo
import com.highcapable.yukihookapi.hook.utils.parallelForEach
import java.io.Closeable
import java.io.File
import java.nio.ByteBuffer
import java.util.zip.ZipEntry
import java.util.zip.ZipFile

/**
 * 封装对 APK 文件的解析操作
 *
 * 参考了 dongliu 的 [apk-parser](https://github.com/hsiafan/apk-parser) 项目
 *
 * Contributed from [conan](https://github.com/meowool-catnip/conan)
 * @param apkFile APK 文件
 */
internal class ApkFile private constructor(apkFile: File) : Closeable {

    internal companion object {

        private const val DEX_FILE = "classes.dex"
        private const val DEX_ADDITIONAL = "classes%d.dex"

        /**
         * 获取 [ApkFile] 实例
         * @param appInfo 当前 APP 的 [ApplicationInfo]
         * @return [ApkFile] or null
         */
        internal fun from(appInfo: ApplicationInfo?) = runCatching { ApkFile(File(appInfo!!.sourceDir)) }.getOrNull()
    }

    override fun close() = zipFile.close()

    /**
     * 当前 APK 文件
     * @return [ZipFile]
     */
    private val zipFile = ZipFile(apkFile)

    /**
     * 读取 Entry
     * @param entry 压缩文件 Entry
     * @return [ByteArray]
     */
    private fun readEntry(entry: ZipEntry) = zipFile.getInputStream(entry).use { it.readBytes() }

    /**
     * 读取 DEX 文件路径
     * @param idx 字节序号
     * @return [String]
     */
    private fun readDexFilePath(idx: Int) = if (idx == 1) DEX_FILE else String.format(DEX_ADDITIONAL, idx)

    /**
     * DEX 文件是否存在
     * @param idx 字节序号
     * @return [Boolean]
     */
    private fun isDexFileExist(idx: Int) = zipFile.getEntry(readDexFilePath(idx)) != null

    /**
     * 读取 DEX 文件字节流
     * @param idx 字节序号
     * @return [ByteArray]
     */
    private fun readDexFile(idx: Int) = readEntry(zipFile.getEntry(readDexFilePath(idx)))

    /**
     * 获取当前 DEX 的 package 结构实例
     * @return [ClassTrie]
     */
    internal val classTypes by lazy {
        var end = 2
        while (isDexFileExist(end)) end++
        val ret = ClassTrie()
        (1 until end).parallelForEach { idx ->
            val data = readDexFile(idx)
            val buffer = ByteBuffer.wrap(data)
            val parser = DexParser(buffer)
            ret += parser.parseClassTypes()
        }
        return@lazy ret.apply { mutable = false }
    }
}