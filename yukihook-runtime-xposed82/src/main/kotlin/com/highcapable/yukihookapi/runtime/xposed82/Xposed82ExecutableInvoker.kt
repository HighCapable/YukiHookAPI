/*
 * YukiHook - A modern hook API and Xposed module solution.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHook
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
 * This file is created by fankes on 2026/9/16.
 */
@file:Suppress("RedundantNullableReturnType")

package com.highcapable.yukihookapi.runtime.xposed82

import com.highcapable.yukihookapi.runtime.bridge.ExecutableInvokerBridge
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Executable invocation backed by Xposed Framework API.
 */
internal object Xposed82ExecutableInvoker : ExecutableInvokerBridge {

    override fun invokeOriginal(member: Member, instance: Any?, args: Array<out Any?>): Any? =
        XposedBridge.invokeOriginalMethod(member, instance, args)

    override fun invokeSpecial(member: Member, instance: Any, args: Array<out Any?>): Any? = when (member) {
        is Constructor<*> -> XposedBridge.invokeOriginalMethod(member, instance, args)
        // Xposed Framework API has no non-virtual method invocation support.
        is Method -> throw UnsupportedOperationException("Special method invocation is unavailable in Xposed Framework API.")
        else -> throw IllegalArgumentException("Unsupported member type: ${member.javaClass.name}")
    }

    override fun newInstanceOriginal(constructor: Constructor<*>, args: Array<out Any?>): Any =
        throw UnsupportedOperationException("Original construction is unavailable in Xposed Framework API.")

    override fun newInstanceSpecial(constructor: Constructor<*>, subclass: Class<*>, args: Array<out Any?>): Any =
        throw UnsupportedOperationException("Special construction is unavailable in Xposed Framework API.")
}