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
 * This file is created by fankes on 2022/4/4.
 */
@file:Suppress("unused", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE", "DEPRECATION", "TYPEALIAS_EXPANSION_DEPRECATION")

package com.highcapable.yukihookapi.hook.bean

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.core.finder.members.FieldFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.type.factory.FieldConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.MethodConditions
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.generic
import com.highcapable.yukihookapi.hook.factory.method

/**
 * Class operation object for the current instance.
 * @param classSet the [Class] of the current instance.
 * @param instance the current instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class CurrentClass internal constructor(private val classSet: Class<*>, internal val instance: Any) {

    /** Whether error warning suppression is enabled. */
    internal var isIgnoreErrorLogs = false

    /**
     * Gets [Class.getName] for the current [classSet].
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val name get() = classSet.name ?: instance.javaClass.name ?: ""

    /**
     * Gets [Class.getSimpleName] for the current [classSet].
     * @return [String]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    val simpleName get() = classSet.simpleName ?: instance.javaClass.simpleName ?: ""

    /**
     * Gets the generic superclass of the current instance.
     *
     * Returns null when the current instance has no generic superclass.
     * @return [GenericClass] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun generic() = classSet.generic()

    /**
     * Gets the generic superclass of the current instance.
     *
     * Returns null when the current instance has no generic superclass.
     * @param initiate the instance block.
     * @return [GenericClass] or null.
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun generic(initiate: GenericClass.() -> Unit) = classSet.generic(initiate)

    /**
     * Gets the superclass instance.
     * @return [SuperClass]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    fun superClass() = SuperClass(classSet.superclass)

    /**
     * Gets a field from the current instance.
     * @param initiate the finder block.
     * @return [FieldFinder.Result.Instance]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun field(initiate: FieldConditions) = classSet.field(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

    /**
     * Calls a method on the current instance.
     * @param initiate the finder block.
     * @return [MethodFinder.Result.Instance]
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inline fun method(initiate: MethodConditions) = classSet.method(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

    /**
     * Class operation object for the superclass instance of the current class.
     *
     * - Use [superClass] to obtain [SuperClass].
     * @param superClassSet the superclass [Class].
     */
    @Deprecated(ReflectionMigration.KAVAREF_INFO)
    inner class SuperClass internal constructor(private val superClassSet: Class<*>) {

        /**
         * Gets [Class.getName] for the superclass of the current [classSet].
         * @return [String]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        val name get() = superClassSet.name ?: ""

        /**
         * Gets [Class.getSimpleName] for the superclass of the current [classSet].
         * @return [String]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        val simpleName get() = superClassSet.simpleName ?: ""

        /**
         * Gets the generic superclass of the current instance's superclass.
         *
         * Returns null when the current instance has no generic superclass.
         * @return [GenericClass] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        fun generic() = superClassSet.generic()

        /**
         * Gets the generic superclass of the current instance's superclass.
         *
         * Returns null when the current instance has no generic superclass.
         * @param initiate the instance block.
         * @return [GenericClass] or null.
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun generic(initiate: GenericClass.() -> Unit) = superClassSet.generic(initiate)

        /**
         * Gets a field from the superclass instance.
         * @param initiate the finder block.
         * @return [FieldFinder.Result.Instance]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun field(initiate: FieldConditions) = superClassSet.field(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

        /**
         * Calls a method on the superclass instance.
         * @param initiate the finder block.
         * @return [MethodFinder.Result.Instance]
         */
        @Deprecated(ReflectionMigration.KAVAREF_INFO)
        inline fun method(initiate: MethodConditions) =
            superClassSet.method(initiate).result { if (isIgnoreErrorLogs) ignored() }.get(instance)

        override fun toString() = "CurrentClass super [$superClassSet]"
    }

    override fun toString() = "CurrentClass [$classSet]"
}