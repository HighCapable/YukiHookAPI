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
 * This file is created by fankes on 2026/9/15.
 */
package com.highcapable.yukihookapi.runtime.bridge

import androidx.annotation.RestrictTo
import java.lang.reflect.Constructor
import java.lang.reflect.Method
import java.lang.reflect.Member as ReflectMember

/**
 * A hook registration point understood by the selected runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
sealed interface HookPoint {

    /**
     * A method or constructor registration point.
     * @param value the method or constructor to hook.
     * @throws IllegalArgumentException when [value] is not a method or constructor.
     */
    data class Member(val value: ReflectMember) : HookPoint {

        init {
            require(value is Method || value is Constructor<*>) {
                "This type $value not support to hook, supported are constructor and method."
            }
        }

        override fun toString() = value.toString()
    }

    /**
     * A class static initializer registration point.
     * @param type the class whose static initializer will be hooked.
     */
    data class Initializer(val type: Class<*>) : HookPoint {
        override fun toString() = "${type.name}.<clinit>"
    }
}