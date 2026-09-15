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
 * This file is created by fankes on 2023/1/9.
 */
package com.highcapable.yukihookapi.hook.core.api.helper

import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.HookPoint
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Member

/**
 * Implements core hook operations.
 */
internal object YukiHookHelper {

    private const val DEFAULT_PRIORITY = 50
    private const val LOWEST_PRIORITY = -10000
    private const val HIGHEST_PRIORITY = 10000

    /**
     * Hooks a [HookPoint].
     * @param target the point to hook.
     * @param hooker the hooker to register.
     * @param priority the hook priority, defaults to [YukiHookPriority.DEFAULT].
     * @return [YukiHookResult] or null when [target] is null.
     */
    fun hook(target: HookPoint?, hooker: HookerBridge, priority: YukiHookPriority = YukiHookPriority.DEFAULT) = target?.let {
        val hook = YukiHookBridge.hook(
            target = it,
            priority = when (priority) {
                YukiHookPriority.DEFAULT -> DEFAULT_PRIORITY
                YukiHookPriority.LOWEST -> LOWEST_PRIORITY
                YukiHookPriority.HIGHEST -> HIGHEST_PRIORITY
            },
            hooker = hooker
        )

        YukiHookResult(it, hook)
    }

    /**
     * Hooks a [MemberResolver].
     * @param resolver the [MemberResolver] to hook.
     * @param hooker the hooker to register.
     * @param priority the hook priority, defaults to [YukiHookPriority.DEFAULT].
     * @return [YukiHookResult] or null when [resolver] is null.
     */
    fun hook(resolver: MemberResolver<*, *>?, hooker: HookerBridge, priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
        hook(resolver?.self?.let { HookPoint.Member(it) }, hooker, priority)

    /**
     * Invokes the original [Member].
     *
     * The [Member] is invoked without applying its hook.
     * @param member the member instance.
     * @param args the argument array.
     * @return [Any] or null.
     * @throws IllegalStateException if the [Member] argument count is invalid.
     */
    fun invokeOriginal(member: Member?, instance: Any?, args: Array<out Any?>?) = member?.let {
        runCatching { YukiHookBridge.invokeOriginal(member, instance, args ?: emptyArray()) }.onFailure {
            if (it.message?.lowercase()?.contains("wrong number of arguments") == true) error(it.message ?: it.toString())
            YLog.innerE("Invoke original $member failed.", it)
        }.getOrNull()
    }

    /**
     * Logs a message with the given [priority], [tag], [msg] and optional [e].
     * @param priority the log priority.
     * @param tag the log tag.
     * @param msg the log message.
     * @param e the optional throwable to log.
     */
    fun log(priority: Int, tag: String, msg: String, e: Throwable? = null) {
        if (YukiHookBridge.isHookAvailable) YukiHookBridge.log(priority, tag, msg, e)
    }
}