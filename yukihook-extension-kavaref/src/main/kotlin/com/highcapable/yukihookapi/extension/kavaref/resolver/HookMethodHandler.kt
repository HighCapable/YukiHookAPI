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
 * This file is created by fankes on 2026/10/9.
 */
package com.highcapable.yukihookapi.extension.kavaref.resolver

import com.highcapable.kavaref.resolver.MethodResolver
import com.highcapable.yukihookapi.hook.param.PackageParam
import java.lang.reflect.Method

/**
 * Provides a [MethodResolver.Handler] for `KavaRef` that invokes methods through the hook framework.
 * @param param the current [PackageParam].
 */
internal sealed class HookMethodHandler(protected val param: PackageParam) : MethodResolver.Handler() {

    override fun requireAccessible(member: Method) = Unit

    /**
     * Invokes the original method without hooks.
     * @param param the current [PackageParam].
     */
    class Original(param: PackageParam) : HookMethodHandler(param) {

        override fun invoke(method: Method, instance: Any?, args: Array<out Any?>) = with(param) {
            method.invokeOriginal(instance, *args)
        }
    }

    /**
     * Invokes the method non-virtually on its instance without hooks or subclass overrides.
     * @param param the current [PackageParam].
     */
    class Special(param: PackageParam) : HookMethodHandler(param) {

        override fun invoke(method: Method, instance: Any?, args: Array<out Any?>) = with(param) {
            method.invokeSpecial(checkNotNull(instance) {
                "An instance is required for special invocation."
            }, *args)
        }
    }
}