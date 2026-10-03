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
 * This file is created by fankes on 2026/9/12.
 */
@file:Suppress("unused", "UNCHECKED_CAST")

package com.highcapable.yukihookapi.hook.param

import android.os.Bundle
import com.highcapable.yukihookapi.hook.core.YukiHookCreator
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Member, receiver and callback-scoped data shared by classic callbacks and chain interceptors.
 * @param dataExtraStorage the storage shared by the current callback scope.
 * @param hooker the hooker owning this invocation's registrations.
 * @param invocation the runtime invocation.
 */
abstract class HookInvocation internal constructor(
    dataExtraStorage: Bundle,
    private val hooker: YukiHookCreator.MemberHooker<*>,
    private val invocation: HookerBridge.Invocation
) {

    /**
     * Gets the data storage shared by the current callback scope.
     *
     * Reused across invocations and batch members of this hooker. Classic before, after and
     * replacement callbacks have separate storage, each chain hooker has its own.
     * A hook replacement starts a new callback generation with fresh storage.
     *
     * This is not per-invocation storage or a shared container for different interceptors.
     * Concurrent access to the contents must be synchronized by the caller.
     * @return [Bundle]
     */
    val dataExtra = dataExtraStorage

    /**
     * Gets the currently hooked method or constructor.
     * @see method
     * @see constructor
     * @return [Member]
     */
    val member get() = invocation.member

    /**
     * Gets the currently hooked method.
     * @see member
     * @see constructor
     * @return [Method]
     * @throws IllegalStateException if the member is not a method.
     */
    val method get() = member as? Method ?: error("The current member is not a Method.")

    /**
     * Gets the currently hooked constructor.
     * @see member
     * @see method
     * @return [Constructor]
     * @throws IllegalStateException if the member is not a constructor.
     */
    val constructor get() = member as? Constructor<*> ?: error("The current member is not a Constructor.")

    /**
     * Gets the current receiver instance.
     *
     * Use [instanceOrNull] for static methods.
     * @see instanceOrNull
     * @return [Any]
     * @throws IllegalStateException if the invocation has no receiver.
     */
    val instance get() = instanceOrNull ?: error(
        "The invocation instance is null, check whether the hooked member is static, " +
            "or use instanceOrNull instead."
    )

    /**
     * Gets the receiver instance, or null for a static method.
     * @see instance
     * @return [Any] or null.
     */
    val instanceOrNull get() = invocation.instance

    /**
     * Gets the current receiver instance as [T].
     * @see instanceOrNull
     * @return [T]
     * @throws ClassCastException if the instance cannot be used as [T].
     */
    inline fun <reified T : Any> instance() = instance as T

    /**
     * Gets the current receiver instance as [T].
     * @see instance
     * @return [T] or null when the receiver is absent or has a different type.
     * @throws ClassCastException if the instance cannot be used as [T].
     */
    inline fun <reified T : Any> instanceOrNull() = instanceOrNull as T?

    /**
     * Removes all registrations belonging to this invocation's hooker.
     *
     * For a batch hook this removes the entire registration group, not just the current member.
     * @param callback callback indicating whether any registrations were removed.
     */
    fun removeSelf(callback: (Boolean) -> Unit = {}) = hooker.removeSelf(callback)
}