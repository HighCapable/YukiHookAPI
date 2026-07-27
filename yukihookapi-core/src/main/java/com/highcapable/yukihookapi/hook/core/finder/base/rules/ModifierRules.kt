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
 * This file is created by fankes on 2022/3/27.
 * This file is modified by fankes on 2022/9/14.
 */
@file:Suppress("unused", "DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * Modifier condition implementation for [Class] and [Member].
 *
 * Provides more precise matching for [Class] and [Member] instances obfuscated by R8.
 * @param instance the current instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class ModifierRules private constructor(private val instance: Any) {

    internal companion object {

        /** The current instances. */
        private val instances = mutableMapOf<Long, ModifierRules>()

        /**
         * Gets the template strings.
         * @param value the unique identifier.
         * @return [MutableList]<[String]>
         */
        internal fun templates(value: Long) = instances[value]?.templates ?: mutableListOf()

        /**
         * Creates an instance.
         * @param instance the source instance.
         * @param value the unique identifier, 0 by default.
         * @return [ModifierRules]
         */
        internal fun with(instance: Any, value: Long = 0) = ModifierRules(instance).apply { instances[value] = this }
    }

    /** The current template strings. */
    private val templates = mutableListOf<String>()

    /**
     * Checks whether the [Class] or [Member] modifiers include `public`.
     *
     * Example:
     *
     * public class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isPublic get() = Modifier.isPublic(modifiers).also { templates.add("<isPublic> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `private`.
     *
     * Example:
     *
     * private class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isPrivate get() = Modifier.isPrivate(modifiers).also { templates.add("<isPrivate> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `protected`.
     *
     * Example:
     *
     * protected class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isProtected get() = Modifier.isProtected(modifiers).also { templates.add("<isProtected> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `static`.
     *
     * Use this condition to identify any static [Class] or [Member].
     *
     * Example:
     *
     * static class/void/int/String...
     *
     * ^^^
     *
     * - Note that methods in a Kotlin `object` are not static on the JVM.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isStatic get() = Modifier.isStatic(modifiers).also { templates.add("<isStatic> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `final`.
     *
     * Example:
     *
     * final class/void/int/String...
     *
     * ^^^
     *
     * - Note that on the JVM, Kotlin [Class] and [Member] declarations without `open`, as well as unrelated declarations, are `final`.
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isFinal get() = Modifier.isFinal(modifiers).also { templates.add("<isFinal> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `synchronized`.
     *
     * Example:
     *
     * synchronized class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isSynchronized get() = Modifier.isSynchronized(modifiers).also { templates.add("<isSynchronized> ($it)") }

    /**
     * Checks whether the [Field] modifiers include `volatile`.
     *
     * Example:
     *
     * volatile int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isVolatile get() = Modifier.isVolatile(modifiers).also { templates.add("<isVolatile> ($it)") }

    /**
     * Checks whether the [Field] modifiers include `transient`.
     *
     * Example:
     *
     * transient int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isTransient get() = Modifier.isTransient(modifiers).also { templates.add("<isTransient> ($it)") }

    /**
     * Checks whether the [Method] modifiers include `native`.
     *
     * Use this condition to identify any JNI-backed [Method].
     *
     * Example:
     *
     * native void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isNative get() = Modifier.isNative(modifiers).also { templates.add("<isNative> ($it)") }

    /**
     * Checks whether the [Class] modifiers include `interface`.
     *
     * Example:
     *
     * interface ...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isInterface get() = Modifier.isInterface(modifiers).also { templates.add("<isInterface> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `abstract`.
     *
     * Use this condition to identify any abstract [Class] or [Member].
     *
     * Example:
     *
     * abstract class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isAbstract get() = Modifier.isAbstract(modifiers).also { templates.add("<isAbstract> ($it)") }

    /**
     * Checks whether the [Class] or [Member] modifiers include `strictfp`.
     *
     * Example:
     *
     * strictfp class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val isStrict get() = Modifier.isStrict(modifiers).also { templates.add("<isStrict> ($it)") }

    /**
     * Gets the modifiers of the current object.
     * @return [Int]
     */
    private val modifiers
        get() = when (instance) {
            is Member -> instance.modifiers
            is Class<*> -> instance.modifiers
            else -> 0
        }

    override fun toString() = "ModifierRules [$instance]"
}