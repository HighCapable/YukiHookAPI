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
@file:Suppress("ClassName", "UNUSED_PARAMETER")

package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl

import android.os.Handler

/**
 * Injection stub for HandlerDelegateImpl.
 */
object HandlerDelegateImpl_Impl {

    /**
     * Gets the [Class] name of the [Handler.Callback] implementation.
     *
     * The return value is generated automatically during each compilation.
     * @return [String]
     */
    val wrapperClassName get(): String = error("Stub!")

    /**
     * Creates an instance from [Handler.Callback].
     *
     * The method body is generated automatically during each compilation.
     * @param baseInstance the nullable [Handler.Callback] instance.
     * @return [Handler.Callback]
     */
    fun createWrapper(baseInstance: Handler.Callback? = null): Handler.Callback = error("Stub!")
}