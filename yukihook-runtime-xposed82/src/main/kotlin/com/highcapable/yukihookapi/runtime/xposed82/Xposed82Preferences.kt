/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
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
 * This file is created by fankes on 2026/9/6.
 */
package com.highcapable.yukihookapi.runtime.xposed82

import android.content.Context
import android.content.SharedPreferences
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.PreferencesBridge
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * New [XSharedPreferences] storage compatible with the original bridge behavior.
 */
internal object Xposed82Preferences {

    fun open(hostEnvironment: Boolean) = if (hostEnvironment) Host else Module

    private object Module : PreferencesBridge {

        private val modulePreferences = mutableMapOf<String, SharedPreferences>()

        @Synchronized
        override fun open(context: Context?, packageName: String, name: String): SharedPreferences {
            if (context == null) return Host.open(context, packageName, name)
            return modulePreferences[context.toString() + name]
                ?: createModulePreferences(context, name).also { modulePreferences[context.toString() + name] = it }
        }
    }

    private fun createModulePreferences(context: Context, name: String): SharedPreferences {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        val newPreferences = runCatching { context.getSharedPreferences(name, Context.MODE_WORLD_READABLE) }.getOrNull()

        return ModulePreferences(
            context = context,
            name = name,
            preferences = newPreferences ?: context.getSharedPreferences(name, Context.MODE_PRIVATE),
            isUsingNewXSharedPreferences = newPreferences != null
        )
    }

    /**
     * Isolated because [XSharedPreferences] is not present in an ordinary module process.
     */
    private object Host : PreferencesBridge {

        private val hostPreferences = mutableMapOf<String, XSharedPreferences>()

        @Synchronized
        override fun open(context: Context?, packageName: String, name: String): SharedPreferences {
            check(YukiHookBridge.isAvailable) {
                "XSharedPreferences requires an attached Xposed host or a module context."
            }
            val prefs = hostPreferences.getOrPut("$packageName/$name") { XSharedPreferences(packageName, name) }

            @Suppress("DEPRECATION")
            prefs.makeWorldReadable()
            prefs.reload()

            return prefs
        }
    }

    /**
     * Attempts the original permission fallback for private module preferences.
     *
     * An asynchronous [SharedPreferences.Editor.apply] may write the file after the permission attempt
     * and replace the file's permissions.
     */
    private class ModulePreferences(
        context: Context, name: String,
        private val preferences: SharedPreferences,
        private val isUsingNewXSharedPreferences: Boolean
    ) : SharedPreferences by preferences {

        private val preferencesFile = File(context.applicationInfo.dataDir)
            .resolve("shared_prefs")
            .resolve("$name.xml")

        private inline fun <T> makeWorldReadable(callback: () -> T): T {
            val result = callback()
            if (!isUsingNewXSharedPreferences) runCatching {
                preferencesFile.apply {
                    @Suppress("SetWorldReadable")
                    setReadable(true, false)
                    setExecutable(true, false)
                }
            }
            return result
        }

        override fun getString(key: String, defValue: String?) = makeWorldReadable { preferences.getString(key, defValue) }
        override fun getStringSet(key: String, defValues: Set<String>?) = makeWorldReadable { preferences.getStringSet(key, defValues) }
        override fun getInt(key: String, defValue: Int) = makeWorldReadable { preferences.getInt(key, defValue) }
        override fun getLong(key: String, defValue: Long) = makeWorldReadable { preferences.getLong(key, defValue) }
        override fun getFloat(key: String, defValue: Float) = makeWorldReadable { preferences.getFloat(key, defValue) }
        override fun getBoolean(key: String, defValue: Boolean) = makeWorldReadable { preferences.getBoolean(key, defValue) }

        override fun edit(): SharedPreferences.Editor = Editor(preferences.edit())

        private inner class Editor(private val base: SharedPreferences.Editor) : SharedPreferences.Editor {

            override fun putString(key: String, value: String?) = apply { base.putString(key, value) }
            override fun putStringSet(key: String, values: Set<String>?) = apply { base.putStringSet(key, values) }
            override fun putInt(key: String, value: Int) = apply { base.putInt(key, value) }
            override fun putLong(key: String, value: Long) = apply { base.putLong(key, value) }
            override fun putFloat(key: String, value: Float) = apply { base.putFloat(key, value) }
            override fun putBoolean(key: String, value: Boolean) = apply { base.putBoolean(key, value) }

            override fun remove(key: String) = apply { base.remove(key) }
            override fun clear() = apply { base.clear() }
            override fun commit() = makeWorldReadable { base.commit() }
            override fun apply() = makeWorldReadable { base.apply() }
        }
    }
}