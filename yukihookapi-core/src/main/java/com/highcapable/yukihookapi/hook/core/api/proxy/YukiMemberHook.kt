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
 * This file is created by fankes on 2022/4/9.
 * This file is modified by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.proxy

import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import java.lang.reflect.Member

/**
 * Base callback for a hooked member.
 * @param priority the Hook priority, defaults to [YukiHookPriority.DEFAULT].
 */
internal abstract class YukiMemberHook(override val priority: YukiHookPriority = YukiHookPriority.DEFAULT) : YukiHookCallback(priority) {

    /**
     * Invoked before the hooked member executes.
     * @param param the Hook callback parameters.
     */
    internal open fun beforeHookedMember(param: Param) {}

    /**
     * Invoked after the hooked member executes.
     * @param param the Hook callback parameters.
     */
    internal open fun afterHookedMember(param: Param) {}

    /**
     * Represents a hooked [Member] that can be unhooked.
     */
    internal abstract class HookedMember internal constructor() {

        /**
         * Gets the currently hooked [Member].
         * @return [Member] or null.
         */
        internal abstract val member: Member?

        /** Removes the Hook. */
        internal abstract fun remove()
    }
}