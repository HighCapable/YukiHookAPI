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
package com.highcapable.yukihookapi.runtime.bridge

import androidx.annotation.RestrictTo
import java.lang.reflect.Constructor
import java.lang.reflect.Member

/**
 * Executable invocation contract implemented by the selected runtime.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
interface ExecutableInvokerBridge {

    /**
     * Invokes the original method or constructor on an existing receiver.
     * @param member the member to invoke.
     * @param instance the instance to invoke the member on, or null for static members.
     * @param args the arguments to pass to the member.
     * @return [Any] or null.
     */
    fun invokeOriginal(member: Member, instance: Any?, args: Array<out Any?>): Any?

    /**
     * Invokes a non-virtual method or constructor on an existing receiver without applying its hooks.
     * @param member the method or constructor to invoke.
     * @param instance the instance to invoke the member on.
     * @param args the arguments to pass to the member.
     * @return [Any] or null.
     */
    fun invokeSpecial(member: Member, instance: Any, args: Array<out Any?>): Any?

    /**
     * Creates an instance with the original constructor without applying its hooks.
     * @param constructor the constructor to invoke.
     * @param args the arguments to pass to the constructor.
     * @return [Any] the created instance.
     */
    fun newInstanceOriginal(constructor: Constructor<*>, args: Array<out Any?>): Any

    /**
     * Creates a subclass instance initialized by the supplied parent constructor without applying its hooks.
     * @param constructor the parent constructor to invoke.
     * @param subclass the subclass to instantiate.
     * @param args the arguments to pass to the constructor.
     * @return [Any] the created subclass instance.
     */
    fun newInstanceSpecial(constructor: Constructor<*>, subclass: Class<*>, args: Array<out Any?>): Any
}