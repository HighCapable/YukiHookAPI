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
 * Hook 回调接口抽象类
 * @param priority Hook 优先级
 */
internal abstract class YukiHookCallback(internal open val priority: YukiHookPriority) {

    /**
     * Hook 结果回调接口
     */
    internal interface Param {

        /**
         * [Member] 实例
         * @return [Member] or null
         */
        val member: Member?

        /**
         * 当前实例对象
         * @return [Any] or null
         */
        val instance: Any?

        /**
         * 方法、构造方法数组
         * @return [Array] or null
         */
        val args: Array<Any?>?

        /**
         * 获取、设置方法返回值 (结果)
         * @return [Any] or null
         */
        var result: Any?

        /**
         * 是否存在设置过的方法调用抛出异常
         * @return [Boolean]
         */
        val hasThrowable: Boolean

        /**
         * 获取、设置方法调用抛出的异常
         * @return [Throwable] or null
         * @throws Throwable
         */
        var throwable: Throwable?
    }
}