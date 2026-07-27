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
    "CommitPrefEdits", "UNCHECKED_CAST", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE"
)

package com.highcapable.yukihookapi.hook.xposed.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceFragmentCompat
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiXposedModule
import com.highcapable.yukihookapi.hook.xposed.bridge.delegate.XSharedPreferencesDelegate
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import com.highcapable.yukihookapi.hook.xposed.prefs.data.PrefsData
import com.highcapable.yukihookapi.hook.xposed.prefs.ui.ModulePreferenceFragment
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * [YukiHookAPI] extended storage bridge implementation for [SharedPreferences] and [XSharedPreferences].
 *
 * Selects the storage object intelligently for different environments.
 *
 * - Shared data storage between the module and host is experimental. It has only been tested under LSPosed. EdXposed should theoretically work but is no longer recommended.
 *
 * For using [PreferenceFragmentCompat] in the module environment, [YukiHookAPI] provides [ModulePreferenceFragment] with the same functionality.
 * @param context the context instance, null by default.
 */
class YukiHookPrefsBridge private constructor(private var context: Context? = null) {

    internal companion object {

        /** Whether the current environment is a (Xposed) host environment. */
        private val isXposedEnvironment = YukiXposedModule.isXposedEnvironment

        /** Currently cached [XSharedPreferencesDelegate] instances. */
        private val xPrefs = mutableMapOf<String, XSharedPreferencesDelegate>()

        /** Currently cached [SharedPreferences] instances. */
        private val sPrefs = mutableMapOf<String, SharedPreferences>()

        /**
         * Creates a [YukiHookPrefsBridge] object.
         * @param context the context instance, null in the (Xposed) host environment.
         * @return [YukiHookPrefsBridge]
         */
        internal fun from(context: Context? = null) = YukiHookPrefsBridge(context)

        /**
         * Makes the preferences file globally readable and writable.
         * @param context the context instance.
         * @param prefsFileName the SharedPreferences file name.
         */
        internal fun makeWorldReadable(context: Context?, prefsFileName: String) {
            runCatching {
                context?.also {
                    File(File(it.applicationInfo.dataDir, "shared_prefs"), prefsFileName).apply {
                        setReadable(true, false)
                        setExecutable(true, false)
                    }
                }
            }
        }
    }

    /** Storage name. */
    private var prefsName = ""

    /** Whether to use the new storage approach for EdXposed and LSPosed. */
    private var isUsingNewXSharedPreferences = false

    /** Whether native storage is enabled. */
    private var isUsingNativeStorage = false

    /**
     * Gets the current storage name, package name plus _preferences by default.
     * @return [String]
     */
    private val currentPrefsName
        get() = prefsName.ifBlank {
            if (isUsingNativeStorage) "${context?.packageName ?: "unknown"}_preferences"
            else "${YukiXposedModule.modulePackageName.ifBlank { context?.packageName ?: "unknown" }}_preferences"
        }

    /** Checks the API loading state. */
    private fun checkApi() {
        if (YukiHookAPI.isLoadedFromBaseContext) error("YukiHookPrefsBridge not allowed in Custom Hook API")
        if (isXposedEnvironment && YukiXposedModule.modulePackageName.isBlank())
            error("Xposed modulePackageName load failed, please reset and rebuild it")
    }

    /**
     * Makes the preferences file globally readable and writable.
     * @param callback the callback block.
     * @return [T]
     */
    private inline fun <T> makeWorldReadable(callback: () -> T): T {
        val result = callback()
        if (isXposedEnvironment.not() && isUsingNewXSharedPreferences.not())
            runCatching { makeWorldReadable(context, prefsFileName = "$currentPrefsName.xml") }
        return result
    }

    /**
     * Gets the current [XSharedPreferences] object.
     * @return [XSharedPreferences]
     */
    private val currentXsp
        get() = checkApi().let {
            runCatching {
                (xPrefs[currentPrefsName]?.instance ?: XSharedPreferencesDelegate.from(YukiXposedModule.modulePackageName, currentPrefsName)
                    .also {
                        xPrefs[currentPrefsName] = it
                    }.instance).apply {
                    makeWorldReadable()
                    reload()
                }
            }.onFailure { YLog.innerE(it.message ?: "Operating system not supported", it) }.getOrNull()
                ?: error("Cannot load the XSharedPreferences, maybe is your Hook Framework not support it")
        }

    /**
     * Gets the current [SharedPreferences] object.
     * @return [SharedPreferences]
     */
    private val currentSp
        get() = checkApi().let {
            runCatching {
                @Suppress("DEPRECATION", "WorldReadableFiles")
                sPrefs[context.toString() + currentPrefsName] ?: context?.getSharedPreferences(currentPrefsName, Context.MODE_WORLD_READABLE)
                    ?.also {
                        isUsingNewXSharedPreferences = true
                        sPrefs[context.toString() + currentPrefsName] = it
                    } ?: error("YukiHookPrefsBridge missing Context instance")
            }.getOrElse {
                sPrefs[context.toString() + currentPrefsName] ?: context?.getSharedPreferences(currentPrefsName, Context.MODE_PRIVATE)?.also {
                    isUsingNewXSharedPreferences = false
                    sPrefs[context.toString() + currentPrefsName] = it
                } ?: error("YukiHookPrefsBridge missing Context instance")
            }
        }

    /**
     * Whether [XSharedPreferences] is readable.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [isPreferencesAvailable].
     * @return [Boolean]
     */
    @Deprecated(message = "Use the new approach to implement this feature", ReplaceWith("isPreferencesAvailable"))
    val isXSharePrefsReadable get() = isPreferencesAvailable

    /**
     * Whether [YukiHookPrefsBridge] is running with the highest EdXposed or LSPosed privileges.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [isPreferencesAvailable].
     * @return [Boolean]
     */
    @Deprecated(message = "Use the new approach to implement this feature", ReplaceWith("isPreferencesAvailable"))
    val isRunInNewXShareMode get() = isPreferencesAvailable

    /**
     * Gets the availability state of the current [YukiHookPrefsBridge].
     *
     * - In the (Xposed) host environment, returns the availability state of [XSharedPreferences] (readable).
     *
     * - In the module environment, returns whether New XSharedPreferences mode is active (readable and writable).
     * @return [Boolean]
     */
    val isPreferencesAvailable
        get() = if (isXposedEnvironment)
            (runCatching { currentXsp.let { it.file.exists() && it.file.canRead() } }.getOrNull() ?: false)
        else runCatching {
            // Performs one load.
            currentSp.edit()
            isUsingNewXSharedPreferences
        }.getOrNull() ?: false

    /**
     * Customizes the SharedPreferences storage name.
     * @param name the custom SharedPreferences storage name.
     * @return [YukiHookPrefsBridge]
     */
    fun name(name: String): YukiHookPrefsBridge {
        prefsName = name
        return this
    }

    /**
     * Reads key-value data directly without using the cache.
     *
     * - This function and feature have been removed. They will be deleted in a future version.
     *
     * - Direct key-value caching has been removed because it can cause out-of-memory (OOM) issues.
     * @return [YukiHookPrefsBridge]
     */
    @Deprecated(message = "This function and feature have been removed. Delete this function", ReplaceWith("this"))
    fun direct() = this

    /**
     * Ignores the current environment and uses [Context.getSharedPreferences] directly to access data.
     * @return [YukiHookPrefsBridge]
     * @throws IllegalStateException if [context] is null.
     */
    fun native(): YukiHookPrefsBridge {
        if (isXposedEnvironment && context == null) context = AppParasitics.currentApplication
            ?: error("The Host App's Context has not yet initialized successfully, the native function cannot be used at this time")
        isUsingNativeStorage = true
        return this
    }

    /**
     * Gets a [String] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, "" by default.
     * @return [String]
     */
    fun getString(key: String, value: String = "") = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getString(key, value) ?: value
        else currentSp.getString(key, value) ?: value
    }

    /**
     * Gets a [Set] of [String] values.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, an empty [MutableSet] of [String] values by default.
     * @return [Set]<[String]>
     */
    fun getStringSet(key: String, value: Set<String> = mutableSetOf()) = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getStringSet(key, value) ?: value
        else currentSp.getStringSet(key, value) ?: value
    }

    /**
     * Gets a [Boolean] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, false by default.
     * @return [Boolean]
     */
    fun getBoolean(key: String, value: Boolean = false) = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getBoolean(key, value)
        else currentSp.getBoolean(key, value)
    }

    /**
     * Gets an [Int] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0 by default.
     * @return [Int]
     */
    fun getInt(key: String, value: Int = 0) = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getInt(key, value)
        else currentSp.getInt(key, value)
    }

    /**
     * Gets a [Float] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0f by default.
     * @return [Float]
     */
    fun getFloat(key: String, value: Float = 0f) = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getFloat(key, value)
        else currentSp.getFloat(key, value)
    }

    /**
     * Gets a [Long] value.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Using [PrefsData] to create a template and [get] to retrieve data is recommended.
     * @param key the key name.
     * @param value the default value, 0L by default.
     * @return [Long]
     */
    fun getLong(key: String, value: Long = 0L) = makeWorldReadable {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.getLong(key, value)
        else currentSp.getLong(key, value)
    }

    /**
     * Gets a value of the specified type intelligently.
     * @param prefs the key-value instance.
     * @param value the default value. The default is [PrefsData.value] in [prefs].
     * @return [T] which can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
     */
    inline fun <reified T> get(prefs: PrefsData<T>, value: T = prefs.value): T = getPrefsData(prefs.key, value) as T

    /**
     * Gets a value of the specified type intelligently.
     *
     * Wrapper function for calling the inline function.
     * @param key the key.
     * @param value the default value.
     * @return [Any]
     */
    private fun getPrefsData(key: String, value: Any?): Any = when (value) {
        is String -> getString(key, value)
        is Set<*> -> getStringSet(key, value as? Set<String> ?: error("Key-Value type ${value.javaClass.name} is not allowed"))
        is Int -> getInt(key, value)
        is Float -> getFloat(key, value)
        is Long -> getLong(key, value)
        is Boolean -> getBoolean(key, value)
        else -> error("Key-Value type ${value?.javaClass?.name} is not allowed")
    }

    /**
     * Whether data for [key] exists.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     * @return [Boolean] whether the key exists.
     */
    fun contains(key: String) =
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.contains(key)
        else currentSp.contains(key)

    /**
     * Gets all stored key-value data.
     *
     * - Detects the corresponding environment intelligently when reading key-value data.
     *
     * - Each call retrieves real-time data without cache control. Do not use this in highly concurrent scenarios.
     * @return [MutableMap] containing key-value data of all types.
     */
    fun all() = mutableMapOf<String, Any?>().apply {
        if (isXposedEnvironment && isUsingNativeStorage.not())
            currentXsp.all.forEach { (k, v) -> this[k] = v }
        else currentSp.all.forEach { (k, v) -> this[k] = v }
    }

    /**
     * Removes all stored data containing [key].
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { remove(key) }"))
    fun remove(key: String) = edit { remove(key) }

    /**
     * Removes the stored data for [PrefsData.key].
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param prefs the key-value instance.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { remove(prefs) }"))
    inline fun <reified T> remove(prefs: PrefsData<T>) = edit { remove(prefs) }

    /**
     * Removes all stored data.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { clear() }"))
    fun clear() = edit { clear() }

    /**
     * Stores a [String] value.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putString(key, value) }"))
    fun putString(key: String, value: String) = edit { putString(key, value) }

    /**
     * Stores a [Set] of [String] values.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putStringSet(key, value) }"))
    fun putStringSet(key: String, value: Set<String>) = edit { putStringSet(key, value) }

    /**
     * Stores a [Boolean] value.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putBoolean(key, value) }"))
    fun putBoolean(key: String, value: Boolean) = edit { putBoolean(key, value) }

    /**
     * Stores an [Int] value.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putInt(key, value) }"))
    fun putInt(key: String, value: Int) = edit { putInt(key, value) }

    /**
     * Stores a [Float] value.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putFloat(key, value) }"))
    fun putFloat(key: String, value: Float) = edit { putFloat(key, value) }

    /**
     * Stores a [Long] value.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     * @param key the key name.
     * @param value the value data.
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { putLong(key, value) }"))
    fun putLong(key: String, value: Long) = edit { putLong(key, value) }

    /**
     * Stores a value of the specified type intelligently.
     *
     * - This API is deprecated and will be removed in a future version.
     *
     * - Migrate to [edit].
     */
    @Deprecated(message = "This function is deprecated due to performance issues. Migrate to the new usage", ReplaceWith("edit { put(prefs, value) }"))
    inline fun <reified T> put(prefs: PrefsData<T>, value: T) = edit { put(prefs, value) }

    /**
     * Creates a new [Editor].
     *
     * - Use this in the module environment or after [isUsingNativeStorage] is enabled.
     *
     * - The (Xposed) host environment is read-only, so this is unavailable there.
     * @return [Editor]
     */
    fun edit() = Editor()

    /**
     * Creates a new [Editor].
     *
     * Calls [Editor.apply] automatically.
     *
     * - Use this in the module environment or after [isUsingNativeStorage] is enabled.
     *
     * - The (Xposed) host environment is read-only, so this is unavailable there.
     * @param initiate the editing block.
     */
    fun edit(initiate: Editor.() -> Unit) = edit().apply(initiate).apply()

    /**
     * Clears key-value data cached in [YukiHookPrefsBridge].
     *
     * - This function and feature have been removed. They will be deleted in a future version.
     *
     * - Direct key-value caching has been removed because it can cause out-of-memory (OOM) issues.
     * @return [YukiHookPrefsBridge]
     */
    @Deprecated(message = "This function and feature have been removed. Delete this function")
    fun clearCache() {
    }

    /**
     * Storage proxy for [YukiHookPrefsBridge].
     *
     * - Use [edit] to obtain [Editor].
     *
     * - Use this in the module environment or after [isUsingNativeStorage] is enabled.
     *
     * - The (Xposed) host environment is read-only, so this is unavailable there.
     */
    inner class Editor internal constructor() {

        /** Creates a new storage proxy. */
        private var editor = runCatching { currentSp.edit() }.getOrNull()

        /**
         * Removes all stored data containing [key].
         * @param key the key name.
         * @return [Editor]
         */
        fun remove(key: String) = specifiedScope { editor?.remove(key) }

        /**
         * Removes the stored data for [PrefsData.key].
         * @param prefs the key-value instance.
         * @return [Editor]
         */
        inline fun <reified T> remove(prefs: PrefsData<T>) = remove(prefs.key)

        /**
         * Removes all stored data.
         * @return [Editor]
         */
        fun clear() = specifiedScope { editor?.clear() }

        /**
         * Stores a [String] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putString(key: String, value: String) = specifiedScope { editor?.putString(key, value) }

        /**
         * Stores a [Set] of [String] values.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putStringSet(key: String, value: Set<String>) = specifiedScope { editor?.putStringSet(key, value) }

        /**
         * Stores a [Boolean] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putBoolean(key: String, value: Boolean) = specifiedScope { editor?.putBoolean(key, value) }

        /**
         * Stores an [Int] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putInt(key: String, value: Int) = specifiedScope { editor?.putInt(key, value) }

        /**
         * Stores a [Float] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putFloat(key: String, value: Float) = specifiedScope { editor?.putFloat(key, value) }

        /**
         * Stores a [Long] value.
         *
         * - Using [PrefsData] to create a template and [put] to store data is recommended.
         * @param key the key name.
         * @param value the value data.
         * @return [Editor]
         */
        fun putLong(key: String, value: Long) = specifiedScope { editor?.putLong(key, value) }

        /**
         * Stores a value of the specified type intelligently.
         * @param prefs the key-value instance.
         * @param value the value to store. It can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
         * @return [Editor]
         */
        inline fun <reified T> put(prefs: PrefsData<T>, value: T) = putPrefsData(prefs.key, value)

        /**
         * Stores a value of the specified type intelligently.
         *
         * Wrapper function for calling the inline function.
         * @param key the key.
         * @param value the value to store. It can only be [String], [Set] of [String], [Int], [Float], [Long], or [Boolean].
         * @return [Editor]
         */
        private fun putPrefsData(key: String, value: Any?) = when (value) {
            is String -> putString(key, value)
            is Set<*> -> putStringSet(key, value as? Set<String> ?: error("Key-Value type ${value.javaClass.name} is not allowed"))
            is Int -> putInt(key, value)
            is Float -> putFloat(key, value)
            is Long -> putLong(key, value)
            is Boolean -> putBoolean(key, value)
            else -> error("Key-Value type ${value?.javaClass?.name} is not allowed")
        }

        /**
         * Commits changes synchronously.
         * @return [Boolean] whether the operation succeeded.
         */
        fun commit() = makeWorldReadable { editor?.commit() ?: false }

        /** Applies changes asynchronously. */
        fun apply() = makeWorldReadable { editor?.apply() ?: Unit }

        /**
         * Executes only in the module environment or when [isUsingNativeStorage] is enabled.
         *
         * Using this outside the module environment prints a warning.
         * @param callback the callback to execute in the module environment.
         * @return [Editor]
         */
        private inline fun specifiedScope(callback: () -> Unit): Editor {
            if (isXposedEnvironment.not() || isUsingNativeStorage) callback()
            else YLog.innerW("YukiHookPrefsBridge.Editor not allowed in Xposed Environment")
            return this
        }
    }
}