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
 * This file is created by fankes on 2026/9/10.
 */
package com.highcapable.yukihookapi.compiler.subprocessor.spec

import com.highcapable.yukihookapi.compiler.DeclaredSymbol

/**
 * Spec used to generate libxposed entry sources and metadata.
 * @param minApiVersion the minimum framework API version.
 * @param targetApiVersion the targeted framework behavior.
 * @param scope the explicitly declared scope.
 * @param staticScope whether the scope is fixed.
 * @param nativeLibraries the native entry library filenames.
 */
data class LibXposedGenerateSpec(
    override var entryPackageName: String = "",
    override var modulePackageName: String = "",
    override var customPackageName: String = "",
    override var entryClassName: String = "",
    override var initClassName: String = "",
    override var isObjectEntry: Boolean = false,
    var minApiVersion: Int = RUNTIME_MIN_API_VERSION,
    var targetApiVersion: Int,
    var scope: List<String> = emptyList(),
    var staticScope: Boolean = false,
    var nativeLibraries: List<String> = emptyList()
) : GenerateSpec {

    companion object {

        const val ANNOTATION_NAME = "${DeclaredSymbol.API_PACKAGE_NAME}.annotation.xposed.YukiHookLibXposedEntry"
        const val ENTRY_CLASS_NAME = "${DeclaredSymbol.API_PACKAGE_NAME}.runtime.libxposed.LibXposedEntry"

        const val RUNTIME_MODULE_NAME = "yukihookapi-runtime-libxposed"

        const val RUNTIME_MIN_API_VERSION = 101
        const val DEFAULT_TARGET_API_VERSION = 102

        const val METADATA_DIRECTORY = "META-INF/xposed"
        const val JAVA_INIT_FILE_PATH = "$METADATA_DIRECTORY/java_init.list"
        const val MODULE_PROPERTIES_FILE_PATH = "$METADATA_DIRECTORY/module.prop"
        const val SCOPE_FILE_PATH = "$METADATA_DIRECTORY/scope.list"
        const val NATIVE_INIT_FILE_PATH = "$METADATA_DIRECTORY/native_init.list"
    }
}