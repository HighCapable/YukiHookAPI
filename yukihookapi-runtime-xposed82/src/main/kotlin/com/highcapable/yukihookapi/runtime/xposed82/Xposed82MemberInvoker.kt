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
 * This file is created by fankes on 2026/9/16.
 */
@file:Suppress("RedundantNullableReturnType")

package com.highcapable.yukihookapi.runtime.xposed82

import com.highcapable.yukihookapi.runtime.bridge.MemberInvokerBridge
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member

/**
 * Member invocation backed by Xposed API 82.
 */
internal object Xposed82MemberInvoker : MemberInvokerBridge {

    override fun invokeOriginal(member: Member, instance: Any?, args: Array<out Any?>): Any? =
        XposedBridge.invokeOriginalMethod(member, instance, args)

    override fun invokeSpecial(member: Member, instance: Any, args: Array<out Any?>): Any? =
        throw UnsupportedOperationException("Special invocation is unavailable in Xposed API 82.")

    override fun newInstanceOriginal(constructor: Constructor<*>, args: Array<out Any?>): Any =
        throw UnsupportedOperationException("Original construction is unavailable in Xposed API 82.")

    override fun newInstanceSpecial(constructor: Constructor<*>, subclass: Class<*>, args: Array<out Any?>): Any =
        throw UnsupportedOperationException("Special construction is unavailable in Xposed API 82.")
}