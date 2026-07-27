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
 * This file is created by fankes on 2022/12/30.
 */
@file:Suppress("unused", "DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import java.lang.reflect.Member

/**
 * Defines matching conditions for an arbitrary object.
 *
 * Allows more precise matching of [Class] and [Member] objects obfuscated by R8.
 * @param instance the current object instance.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
class ObjectRules private constructor(private val instance: Any) {

    internal companion object {

        /**
         * Creates an [ObjectRules] instance.
         * @param instance the object instance.
         * @return [ObjectRules]
         */
        internal fun with(instance: Any) = ObjectRules(instance)
    }

    override fun toString() = "ObjectRules [$instance]"
}