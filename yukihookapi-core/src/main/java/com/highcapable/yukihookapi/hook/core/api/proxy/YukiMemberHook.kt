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
 * This file is created by fankes on 2022/4/9.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.proxy

import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import java.lang.reflect.Member

/**
 * Hook 方法回调接口抽象类
 * @param priority Hook 优先级 - 默认 [YukiHookPriority.DEFAULT]
 */
internal abstract class YukiMemberHook(override val priority: YukiHookPriority = YukiHookPriority.DEFAULT) : YukiHookCallback(priority) {

    /**
     * 在方法执行之前注入
     * @param param Hook 结果回调接口
     */
    internal open fun beforeHookedMember(param: Param) {}

    /**
     * 在方法执行之后注入
     * @param param Hook 结果回调接口
     */
    internal open fun afterHookedMember(param: Param) {}

    /**
     * 已经 Hook 且可被解除 Hook 的 [Member] 实现接口抽象类
     */
    internal abstract class HookedMember internal constructor() {

        /**
         * 当前被 Hook 的 [Member]
         * @return [Member] or null
         */
        internal abstract val member: Member?

        /** 解除 Hook */
        internal abstract fun remove()
    }
}