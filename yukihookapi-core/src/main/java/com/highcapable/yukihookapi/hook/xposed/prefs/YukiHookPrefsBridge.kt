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
 * This file is created by fankes on 2022/2/8.
 */
@file:Suppress(
    "unused", "MemberVisibilityCanBePrivate", "StaticFieldLeak", "SetWorldReadable",
    "CommitPrefEdits", "UNCHECKED_CAST"
)

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.bridge.XposedModuleRuntime
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitism
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * [YukiHookAPI] storage bridge implementation for [SharedPreferences] with additional store proposal.
 *
 * Uses the storage selected by the current [YukiHookRuntime].
 *
 * For using [PreferenceFragmentCompat] in the module environment, [YukiHookAPI] provides [ModulePreferenceFragment] with the same functionality.
 * @param context the context instance, null by default.
 */
class YukiHookPrefsBridge private constructor(private var context: Context? = null) {

    internal companion object {

        /**
         * Creates a bridge for the module or the current host.
         * @param context the context instance, null by default.
         * @return [YukiHookPrefsBridge]
         */
        fun from(context: Context? = null) = YukiHookPrefsBridge(context)
    }

    private val isHostEnvironment get() = XposedModuleRuntime.isHostEnvironment

    /** Storage name. */
    private var prefsName = ""

    /** Whether native storage is enabled. */
    private var isUsingNativeStorage = false

    /**
     * Gets the current storage name, package name + "_preferences" by default.
     * @return [String]
     */
    private val currentPrefsName get() = prefsName.ifBlank {
        if (isUsingNativeStorage) "${context?.packageName ?: "unknown"}_preferences"
        else "${XposedModuleRuntime.modulePackageName.ifBlank { context?.packageName ?: "unknown" }}_preferences"
    }

    /** Checks the API loading state. */
    private fun checkApi() {
        require(!YukiHookAPI.isLoadedFromBaseContext) {
            "YukiHookPrefsBridge not allowed in custom hook API."
        }
        if (isHostEnvironment) require(XposedModuleRuntime.modulePackageName.isNotBlank()) {
            "Xposed modulePackageName load failed, please reset and rebuild it."
        }
    }

    /**
     * Gets the preferences selected by the current runtime.
     * @return [SharedPreferences]
     */
    internal val current: SharedPreferences get() {
        checkApi()
        if (isUsingNativeStorage) return checkNotNull(context) {
            "Native preferences require a Context"
        }.getSharedPreferences(currentPrefsName, Context.MODE_PRIVATE)

        return YukiHookBridge.preferences(
            context = context,
            packageName = XposedModuleRuntime.modulePackageName.ifBlank { context?.packageName ?: "" },
            name = currentPrefsName,
            hostEnvironment = isHostEnvironment
        )
    }

    // region Deprecated properties for backward compatibility

    /**
     * Gets the availability state of the current [YukiHookPrefsBridge].
     *
     * - Migrate to [isAvailable] or [isWritable].
     * @return [Boolean]
     */
    @Deprecated(message = "Use isAvailable or isWritable instead.")
    val isPreferencesAvailable get() = false

    // endregion

    /**
     * Gets whether the current preferences are available.
     * @return [Boolean]
     */
    val isAvailable get() = runCatching { current.contains(""); true }.getOrDefault(false)

    /**
     * Gets whether the current preferences are writable.
     * @return [Boolean]
     */
    val isWritable get() = !isHostEnvironment || isUsingNativeStorage

    /**
     * Customizes the shared preferences storage name.
     * @param name the custom storage name.
     * @return [YukiHookPrefsBridge]
     */
    fun name(name: String): YukiHookPrefsBridge {
        prefsName = name
        return this
    }

    /**
     * Ignores the current environment and uses [Context.getSharedPreferences] directly to access data.
     * @return [YukiHookPrefsBridge]
     * @throws IllegalStateException if [context] is null.
     */
    fun native(): YukiHookPrefsBridge {
        if (isHostEnvironment && context == null) context = AppParasitism.currentApplication
            ?: error("The host's context has not yet initialized successfully, the native function cannot be used at this time.")
        isUsingNativeStorage = true

        return this
    }

    /**
     * Gets a [String] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, "" by default.
     * @return [String]
     */
    fun getString(key: String, value: String = "") = current.getString(key, value) ?: value

    /**
     * Gets a [Set] of [String] values.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, an empty [MutableSet] of [String] values by default.
     * @return [Set]<[String]>
     */
    fun getStringSet(key: String, value: Set<String> = mutableSetOf()) = current.getStringSet(key, value) ?: value

    /**
     * Gets a [Boolean] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, false by default.
     * @return [Boolean]
     */
    fun getBoolean(key: String, value: Boolean = false) = current.getBoolean(key, value)

    /**
     * Gets an [Int] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0 by default.
     * @return [Int]
     */
    fun getInt(key: String, value: Int = 0) = current.getInt(key, value)

    /**
     * Gets a [Float] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0f by default.
     * @return [Float]
     */
    fun getFloat(key: String, value: Float = 0f) = current.getFloat(key, value)

    /**
     * Gets a [Long] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0L by default.
     * @return [Long]
     */
    fun getLong(key: String, value: Long = 0L) = current.getLong(key, value)

    /**
     * Gets a value of the specified type intelligently.
     * @param prefs the key-value instance.
     * @param value the default value. The default is [PrefsData.value] in [prefs].
     * @return [T] which can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
     */
    fun <T> get(prefs: PrefsData<T>, value: T = prefs.value): T = getPrefsData(prefs.key, value) as T

    /**
     * Gets a value of the specified type intelligently.
     *
     * @param key the key.
     * @param value the default value.
     * @return [Any]
     */
    private fun getPrefsData(key: String, value: Any?): Any = when (value) {
        is String -> getString(key, value)
        is Set<*> -> getStringSet(key, value as? Set<String> ?: error("Key-value type ${value.javaClass.name} is not allowed."))
        is Int -> getInt(key, value)
        is Float -> getFloat(key, value)
        is Long -> getLong(key, value)
        is Boolean -> getBoolean(key, value)
        else -> error("Key-value type ${value?.javaClass?.name} is not allowed.")
    }

    /**
     * Whether data for [key] exists.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * @return [Boolean] whether the key exists.
     */
    fun contains(key: String) = current.contains(key)

    /**
     * Gets all stored key-value data.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * - Each call retrieves real-time data without cache control. Do not use this in highly concurrent scenarios.
     * @return [MutableMap] containing key-value data of all types.
     */
    fun all() = current.all.toMutableMap()

    /**
     * Creates a new [Editor].
     *
     * Module preferences are read-only in a host process, so changes are ignored there.
     * Call [native] first only when writing the host's private storage is intended.
     * @return [Editor]
     */
    fun edit() = Editor()

    /**
     * Creates a new [Editor].
     *
     * Calls [Editor.apply] automatically.
     *
     * Module preferences are read-only in a host process, so changes are ignored there.
     * Call [native] first only when writing the host's private storage is intended.
     * @param block the editing block.
     */
    inline fun edit(block: Editor.() -> Unit) = edit().apply(block).apply()

    /**
     * Storage proxy for [YukiHookPrefsBridge].
     *
     * - Use [edit] to obtain [Editor].
     * - Module preferences are read-only in a host process, so changes are ignored there.
     */
    inner class Editor internal constructor() {

        private val editor = if (isWritable) current.edit() else null

        /**
         * Removes all stored data containing [key].
         * @param key the key name.
         * @return [Editor]
         */
        fun remove(key: String) = apply { editor?.remove(key) }

        /**
         * Removes the stored data for [PrefsData.key].
         * @param prefs the key-value instance.
         * @return [Editor]
         */
        fun remove(prefs: PrefsData<*>) = remove(prefs.key)

        /**
         * Removes all stored data.
         * @return [Editor]
         */
        fun clear() = apply { editor?.clear() }

        /**
         * Stores a [String] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putString(key: String, value: String) = apply { editor?.putString(key, value) }

        /**
         * Stores a [Set] of [String] values.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putStringSet(key: String, value: Set<String>) = apply { editor?.putStringSet(key, value) }

        /**
         * Stores a [Boolean] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putBoolean(key: String, value: Boolean) = apply { editor?.putBoolean(key, value) }

        /**
         * Stores an [Int] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putInt(key: String, value: Int) = apply { editor?.putInt(key, value) }

        /**
         * Stores a [Float] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putFloat(key: String, value: Float) = apply { editor?.putFloat(key, value) }

        /**
         * Stores a [Long] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putLong(key: String, value: Long) = apply { editor?.putLong(key, value) }

        /**
         * Stores a value of the specified type intelligently.
         * @param prefs the key-value instance.
         * @param value the value to store. It can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
         * @return [Editor]
         */
        fun <T> put(prefs: PrefsData<T>, value: T) = putPrefsData(prefs.key, value)

        /**
         * Stores a value of the specified type intelligently.
         *
         * @param key the key.
         * @param value the value to store. It can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
         * @return [Editor]
         */
        private fun putPrefsData(key: String, value: Any?) = when (value) {
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, value as? Set<String> ?: error("Key-value type ${value.javaClass.name} is not allowed."))
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            else -> error("Key-value type ${value?.javaClass?.name} is not allowed.")
        }

        /**
         * Commits changes synchronously.
         * @return [Boolean] whether the operation succeeded.
         */
        fun commit() = editor?.commit() ?: false

        /** Applies changes asynchronously. */
        fun apply() = editor?.apply() ?: Unit
    }
}