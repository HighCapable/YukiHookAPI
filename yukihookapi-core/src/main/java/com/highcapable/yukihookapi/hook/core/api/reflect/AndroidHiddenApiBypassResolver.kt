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
 * This file is created by fankes on 2025/6/15.
 */
package com.highcapable.yukihookapi.hook.core.api.reflect

import com.highcapable.betterandroid.system.extension.utils.AndroidVersion
import com.highcapable.kavaref.resolver.processor.MemberProcessor
import org.lsposed.hiddenapibypass.HiddenApiBypass
import java.lang.reflect.Constructor
import java.lang.reflect.Method

/**
 * Provides [HiddenApiBypass] integration for KavaRef.
 *
 * Implemented with [AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass).
 *
 * Call [AndroidHiddenApiBypassResolver.get] wherever access to Android hidden APIs is required.
 *
 * - This feature is experimental in 1.x.x and may move to a new module in 2.0.0.
 */
class AndroidHiddenApiBypassResolver private constructor() : MemberProcessor.Resolver() {

    companion object {

        private val self by lazy { AndroidHiddenApiBypassResolver() }

        /**
         * Gets the [AndroidHiddenApiBypassResolver] instance.
         * @return [AndroidHiddenApiBypassResolver]
         */
        fun get() = self
    }

    override fun <T : Any> getDeclaredConstructors(declaringClass: Class<T>): List<Constructor<T>> =
        AndroidVersion.require(AndroidVersion.P, super.getDeclaredConstructors(declaringClass)) {
            HiddenApiBypass.getDeclaredMethods(declaringClass).filterIsInstance<Constructor<T>>().toList()
        }

    override fun <T : Any> getDeclaredMethods(declaringClass: Class<T>): List<Method> =
        AndroidVersion.require(AndroidVersion.P, super.getDeclaredMethods(declaringClass)) {
            HiddenApiBypass.getDeclaredMethods(declaringClass).filterIsInstance<Method>().toList()
        }
}