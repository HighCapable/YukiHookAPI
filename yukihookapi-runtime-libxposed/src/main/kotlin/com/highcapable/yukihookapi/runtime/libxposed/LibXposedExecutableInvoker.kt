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
package com.highcapable.yukihookapi.runtime.libxposed

import com.highcapable.yukihookapi.runtime.bridge.ExecutableInvokerBridge
import io.github.libxposed.api.XposedInterface
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Executable invocation backed by the libxposed API.
 */
internal class LibXposedExecutableInvoker(private val api: XposedInterface) : ExecutableInvokerBridge {

    override fun invokeOriginal(member: Member, instance: Any?, args: Array<out Any?>): Any? =
        getInvoker(member).invoke(instance, *args)

    override fun invokeSpecial(member: Member, instance: Any, args: Array<out Any?>): Any? =
        getInvoker(member).invokeSpecial(instance, *args)

    override fun newInstanceOriginal(constructor: Constructor<*>, args: Array<out Any?>): Any =
        getInvoker(constructor).newInstance(*args)

    override fun newInstanceSpecial(constructor: Constructor<*>, subclass: Class<*>, args: Array<out Any?>): Any =
        getInvoker(constructor).newInstanceSpecial(subclass, *args)

    private fun getInvoker(member: Member) = when (member) {
        is Method -> api.getInvoker(member).original()
        is Constructor<*> -> getInvoker(member)
        else -> error("Only methods and constructors have invocation.")
    }

    private fun <T> getInvoker(constructor: Constructor<T>) = api.getInvoker(constructor).original()

    private fun <T : XposedInterface.Invoker<T, *>> XposedInterface.Invoker<T, *>.original() =
        setType(XposedInterface.Invoker.Type.ORIGIN)
}