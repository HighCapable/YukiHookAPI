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
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.delegate.impl

import android.os.Handler

/**
 * 代理当前 [Handler.Callback] 调用接口实现
 */
internal object HandlerDelegateImpl {

    /**
     * 获取 [Handler.Callback] 实例 [Class] 名称
     * @return [String]
     */
    internal val wrapperClassName get() = HandlerDelegateImpl_Impl.wrapperClassName

    /**
     * 从 [Handler.Callback] 创建实例
     * @param baseInstance [Handler.Callback] 实例 - 可空
     * @return [Handler.Callback]
     */
    internal fun createWrapper(baseInstance: Handler.Callback? = null) = HandlerDelegateImpl_Impl.createWrapper(baseInstance)
}