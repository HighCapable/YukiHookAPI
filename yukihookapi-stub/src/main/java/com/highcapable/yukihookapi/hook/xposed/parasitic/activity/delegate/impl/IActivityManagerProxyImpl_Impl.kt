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
 * This file is created by fankes on 2023/4/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
@file:Suppress("unused", "ClassName", "UNUSED_PARAMETER")

package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl

import android.app.ActivityManager

/**
 * Injection stub for IActivityManagerProxyImpl.
 */
object IActivityManagerProxyImpl_Impl {

    /**
     * Creates an [ActivityManager] proxy.
     *
     * The method body is generated automatically during each compilation.
     * @param clazz the target [Class] to proxy.
     * @param instance the target instance to proxy.
     * @return [Any] the proxy-wrapped instance.
     */
    fun createWrapper(clazz: Class<*>?, instance: Any): Any = error("Stub!")
}