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
 * This file is created by fankes on 2026/9/18.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.file

import android.os.ParcelFileDescriptor
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

/**
 * [YukiHookAPI] shared-file storage backed by the module and host environments.
 *
 * Files are writable from the module environment and read-only from a host environment.
 * This storage is independent of the module application's private file directory.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
class YukiHookSharedFiles private constructor() {

    internal companion object {

        /** Whether the current environment is a host environment. */
        private val isHostEnvironment = XposedModuleRuntime.isHostEnvironment

        /** The current [YukiHookSharedFiles] singleton. */
        private val instance = YukiHookSharedFiles()

        /**
         * Gets the [YukiHookSharedFiles] singleton.
         * @return [YukiHookSharedFiles]
         */
        fun get() = instance
    }

    /** The storage supplied by the selected runtime. */
    private val current get() = YukiHookBridge.sharedFiles(isHostEnvironment)

    /** Checks the API loading state and access mode. */
    private fun checkApi(requireWritable: Boolean = false) {
        require(!YukiHookAPI.isLoadedFromBaseContext) {
            "YukiHookSharedFiles not allowed in standalone hook API."
        }
        if (requireWritable) check(!isHostEnvironment) {
            "Shared files are read-only in a host process."
        }
    }

    /**
     * Gets whether shared-file storage is currently available.
     * @return [Boolean]
     */
    val isAvailable get() = runCatching {
        checkApi()
        current.isAvailable
    }.getOrDefault(false)

    /**
     * Gets whether shared files can be modified in the current environment.
     * @return [Boolean]
     */
    val isWritable get() = runCatching {
        checkApi()
        current.isWritable
    }.getOrDefault(false)

    /**
     * Lists all shared-file names.
     * @return [List]<[String]> a snapshot of the current file names.
     */
    fun list(): List<String> {
        checkApi()
        return current.list()
    }

    /**
     * Opens a shared file.
     *
     * - In the module environment, a missing file is created and the descriptor is writable.
     * - In a host environment, the descriptor is read-only and the file must already exist.
     *
     * The caller must close the returned descriptor.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @return [ParcelFileDescriptor]
     */
    fun open(name: String): ParcelFileDescriptor {
        checkApi()
        return current.open(name)
    }

    /**
     * Opens a shared file for reading and closes its descriptor when the stream is closed.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @return [FileInputStream]
     */
    fun inputStream(name: String): FileInputStream {
        checkApi()
        return ParcelFileDescriptor.AutoCloseInputStream(current.open(name))
    }

    /**
     * Opens a shared file for writing and closes its descriptor when the stream is closed.
     *
     * Existing content is truncated when the stream is opened.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @return [FileOutputStream]
     * @throws IllegalStateException in a host environment.
     */
    fun outputStream(name: String): FileOutputStream {
        checkApi(requireWritable = true)
        return ParcelFileDescriptor.AutoCloseOutputStream(current.open(name)).apply { channel.truncate(0) }
    }

    /**
     * Reads the entire content of a shared file.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @return [ByteArray] containing the file content.
     */
    fun readBytes(name: String) = inputStream(name).use { it.readBytes() }

    /**
     * Writes [content] to a shared file, replacing its existing content.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @param content the content to write.
     * @throws IllegalStateException in a host environment.
     */
    fun writeBytes(name: String, content: ByteArray) = outputStream(name).use { it.write(content) }

    /**
     * Reads the entire content of a shared file as text.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @param charset the character set to use, UTF-8 by default.
     * @return [String] containing the file content.
     */
    fun readText(name: String, charset: Charset = Charsets.UTF_8) = readBytes(name).toString(charset)

    /**
     * Writes [content] to a shared file, replacing its existing content.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @param content the text to write.
     * @param charset the character set to use, UTF-8 by default.
     * @throws IllegalStateException in a host environment.
     */
    fun writeText(name: String, content: String, charset: Charset = Charsets.UTF_8) =
        outputStream(name).writer(charset).use { it.write(content) }

    /**
     * Deletes a shared file from the module environment.
     * @param name the file name without path separators and not equal to `.` or `..`.
     * @return [Boolean] whether the file was deleted.
     * @throws IllegalStateException in a host environment.
     */
    fun delete(name: String): Boolean {
        checkApi(requireWritable = true)
        return current.delete(name)
    }
}