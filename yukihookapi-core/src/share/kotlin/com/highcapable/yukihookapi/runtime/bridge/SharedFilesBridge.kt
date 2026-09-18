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
package com.highcapable.yukihookapi.runtime.bridge

import android.os.ParcelFileDescriptor
import androidx.annotation.RestrictTo

/**
 * Shared-file storage contract implemented by the selected runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface SharedFilesBridge {

    /** Whether shared-file storage is currently available. */
    val isAvailable: Boolean

    /** Whether shared-file storage is writable in the current environment. */
    val isWritable: Boolean

    /**
     * Lists all shared-file names.
     * @return [List]<[String]>
     */
    fun list(): List<String>

    /**
     * Opens the shared file named [name].
     * @param name the name of the shared file to open.
     * @return [ParcelFileDescriptor]
     */
    fun open(name: String): ParcelFileDescriptor

    /**
     * Deletes the shared file named [name].
     * @param name the name of the shared file to delete.
     * @return [Boolean] if the file was successfully deleted.
     */
    fun delete(name: String): Boolean
}