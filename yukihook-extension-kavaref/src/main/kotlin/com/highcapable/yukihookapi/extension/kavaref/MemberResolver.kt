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
 * This file is created by fankes on 2026/10/13.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.extension.kavaref

import com.highcapable.kavaref.resolver.base.MemberResolver
import com.highcapable.yukihookapi.annotation.internal.RequiresRuntime
import com.highcapable.yukihookapi.hook.core.YukiHookCreator
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.param.HookChain
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.runtime.YukiHookRuntime

/**
 * Creates a direct hook for methods and constructors through [MemberResolver].
 * @receiver the current member to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @return [YukiHookCreator.ClassicMemberHooker]
 */
context(param: PackageParam)
fun MemberResolver<*, *, *>.hook(priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
    with(param) { self.hook(priority) }

/**
 * Creates a direct hook for methods and constructors through [MemberResolver].
 * @receiver the current member to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @param hooker the hook block.
 * @return [YukiHookCreator.MemberHooker.Handle]
 */
context(param: PackageParam)
fun MemberResolver<*, *, *>.hook(
    priority: YukiHookPriority = YukiHookPriority.DEFAULT,
    hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
) = with(param) { self.hook(priority, hooker) }

/**
 * Creates a direct hook for methods and constructors through a [List] of [MemberResolver] instances.
 * @receiver the current members to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @return [YukiHookCreator.ClassicMemberHooker]
 */
context(param: PackageParam)
fun Collection<MemberResolver<*, *, *>>.hookAll(priority: YukiHookPriority = YukiHookPriority.DEFAULT) =
    with(param) { map { it.self }.hookAll(priority) }

/**
 * Creates a direct hook for methods and constructors through a [List] of [MemberResolver] instances.
 * @receiver the current members to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @param hooker the hook block.
 * @return [YukiHookCreator.MemberHooker.Handle]
 */
context(param: PackageParam)
fun Collection<MemberResolver<*, *, *>>.hookAll(
    priority: YukiHookPriority = YukiHookPriority.DEFAULT,
    hooker: YukiHookCreator.ClassicMemberHooker.() -> Unit
) = with(param) { map { it.self }.hookAll(priority, hooker) }

/**
 * Creates a direct chain hook for methods and constructors through [MemberResolver].
 *
 * Intercepting without [HookChain.proceed] skips the original member. The default [invocation] returns null without calling it.
 *
 * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
 *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
 *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
 *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
 * @receiver the current member to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @param invocation the chain hook block.
 * @return [YukiHookCreator.MemberHooker.Handle]
 */
context(param: PackageParam)
fun MemberResolver<*, *, *>.intercept(
    priority: YukiHookPriority = YukiHookPriority.DEFAULT,
    invocation: HookChain.() -> Any? = { null }
) = with(param) { self.intercept(priority, invocation) }

/**
 * Creates a direct chain hook for methods and constructors through a [List] of [MemberResolver] instances.
 *
 * Intercepting without [HookChain.proceed] skips the original member. The default [invocation] returns null without calling it.
 *
 * - Note: [YukiHookRuntime.XPOSED_82] simulates a module-local chain through a low-priority native hook.
 *   [HookChain.proceed] runs this runtime's remaining chain hooks and then the original member.
 *   Classic hooks and other modules are not part of this chain, later native hooks may be skipped.
 *   [priority] orders the local chain and does not preserve framework-wide hook ordering.
 * @receiver the current members to hook.
 * @param priority the hook priority, [YukiHookPriority.DEFAULT] by default.
 * @param invocation the chain hook block.
 * @return [YukiHookCreator.MemberHooker.Handle]
 */
context(param: PackageParam)
fun Collection<MemberResolver<*, *, *>>.interceptAll(
    priority: YukiHookPriority = YukiHookPriority.DEFAULT,
    invocation: HookChain.() -> Any? = { null }
) = with(param) { map { it.self }.interceptAll(priority, invocation) }

/**
 * Deoptimizes this calling method or constructor through [MemberResolver] so its callees are invoked
 * without runtime inlining.
 *
 * Use this on the caller containing an inlined call, not on the hooked callee.
 * @receiver the calling method or constructor to deoptimize.
 * @return [Boolean] whether deoptimization succeeded.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
fun MemberResolver<*, *, *>.deoptimize() = with(param) { self.deoptimize() }

/**
 * Deoptimizes these calling methods and constructors through a [Collection] of [MemberResolver] instances.
 *
 * A false result does not stop the remaining members. Use these on callers containing inlined calls, not on hooked callees.
 * @receiver the calling methods and constructors to deoptimize.
 * @return [Boolean] whether every deoptimization succeeded.
 */
@RequiresRuntime(YukiHookRuntime.LIBXPOSED)
context(param: PackageParam)
fun Collection<MemberResolver<*, *, *>>.deoptimizeAll() = with(param) { map { it.self }.deoptimizeAll() }