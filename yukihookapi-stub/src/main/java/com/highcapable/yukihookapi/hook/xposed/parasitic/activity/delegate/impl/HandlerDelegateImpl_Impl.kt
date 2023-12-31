/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 *  HandlerDelegateImpl 注入 Stub
 */
object HandlerDelegateImpl_Impl {

    /**
     * 获取 [Handler.Callback] 实例 [Class] 名称
     *
     * 返回值将在每次编译时自动生成
     * @return [String]
     */
    val wrapperClassName get(): String = error("Stub!")

    /**
     * 从 [Handler.Callback] 创建实例
     *
     * 方法内容将在每次编译时自动生成
     * @param baseInstance [Handler.Callback] 实例 - 可空
     * @return [Handler.Callback]
     */
    fun createWrapper(baseInstance: Handler.Callback? = null): Handler.Callback = error("Stub!")
}