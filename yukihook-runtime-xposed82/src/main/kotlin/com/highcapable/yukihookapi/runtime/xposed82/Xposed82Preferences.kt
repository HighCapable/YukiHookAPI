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
import de.robv.android.xposed.XSharedPreferences
import java.io.File

/**
 * New [XSharedPreferences] storage compatible with the original bridge behavior.
 */
internal object Xposed82Preferences {

    private val modulePreferences = mutableMapOf<String, SharedPreferences>()

    @Synchronized
    fun open(context: Context?, packageName: String, name: String): SharedPreferences {
        if (context != null) return modulePreferences[context.toString() + name]
            ?: createModulePreferences(context, name).also { modulePreferences[context.toString() + name] = it }

        check(YukiHookBridge.isAvailable) {
            "XSharedPreferences requires an attached Xposed host or a module context."
        }
        return Host.open(packageName, name)
    }

    private fun createModulePreferences(context: Context, name: String): SharedPreferences {
        @Suppress("DEPRECATION", "WorldReadableFiles")
        val newPreferences = runCatching { context.getSharedPreferences(name, Context.MODE_WORLD_READABLE) }.getOrNull()

        return Module(
            context = context,
            name = name,
            preferences = newPreferences ?: context.getSharedPreferences(name, Context.MODE_PRIVATE),
            isUsingNewXSharedPreferences = newPreferences != null
        )
    }

    /**
     * Isolated because [XSharedPreferences] is not present in an ordinary module process.
     */
    private object Host {

        private val hostPreferences = mutableMapOf<String, XSharedPreferences>()

        @Synchronized
        fun open(packageName: String, name: String): SharedPreferences {
            val prefs = hostPreferences.getOrPut("$packageName/$name") { XSharedPreferences(packageName, name) }

            @Suppress("DEPRECATION")
            prefs.makeWorldReadable()
            prefs.reload()

            return prefs
        }
    }

    /**
     * Applies the original fallback permission behavior around a module's private preferences.
     */
    private class Module(
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

        private inner class Editor(private val editor: SharedPreferences.Editor) : SharedPreferences.Editor {

            override fun putString(key: String, value: String?) = apply { editor.putString(key, value) }
            override fun putStringSet(key: String, values: Set<String>?) = apply { editor.putStringSet(key, values) }
            override fun putInt(key: String, value: Int) = apply { editor.putInt(key, value) }
            override fun putLong(key: String, value: Long) = apply { editor.putLong(key, value) }
            override fun putFloat(key: String, value: Float) = apply { editor.putFloat(key, value) }
            override fun putBoolean(key: String, value: Boolean) = apply { editor.putBoolean(key, value) }

            override fun remove(key: String) = apply { editor.remove(key) }
            override fun clear() = apply { editor.clear() }
            override fun commit() = makeWorldReadable { editor.commit() }
            override fun apply() = makeWorldReadable { editor.apply() }
        }
    }
}