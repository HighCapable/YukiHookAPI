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
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core

import android.os.Bundle
import com.highcapable.kavaref.extension.classOf
import com.highcapable.kavaref.extension.isNotSubclassOf
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.helper.YukiHookHelper
import com.highcapable.yukihookapi.hook.core.api.priority.YukiHookPriority
import com.highcapable.yukihookapi.hook.core.api.result.YukiHookResult
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.HookChain
import com.highcapable.yukihookapi.hook.param.HookInvocation
import com.highcapable.yukihookapi.hook.param.HookParam
import com.highcapable.yukihookapi.runtime.YukiHookBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ChainHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.ClassicHookerBridge
import com.highcapable.yukihookapi.runtime.bridge.hooker.HookerBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.util.concurrent.atomic.AtomicLong

/**
 * Core [Member] hook implementation for [YukiHookAPI].
 *
 * The core API is implemented through [YukiHookHelper].
 */
class YukiHookCreator private constructor() {

    internal companion object {

        /**
         * Creates a chain member hooker.
         * @param members the members to hook.
         * @param priority the hook priority.
         * @param invocation the interceptor invoked for each call.
         * @return [ChainMemberHooker]
         */
        fun createChainHooker(members: Collection<Member>, priority: YukiHookPriority, invocation: HookChain.() -> Any?) =
            ChainMemberHooker(priority, invocation).also { it.members.addAll(members) }

        /**
         * Creates a classic member hooker.
         * @param members the members to hook.
         * @param priority the hook priority.
         * @param isLazyMode whether registration waits until the hook block has been configured.
         * @return [ClassicMemberHooker]
         */
        fun createClassicHooker(members: Collection<Member>, priority: YukiHookPriority, isLazyMode: Boolean) =
            ClassicMemberHooker(priority, if (isLazyMode) HookMode.LAZY else HookMode.IMMEDIATE)
                .also { it.members.addAll(members) }
    }

    /**
     * Base member registration and invocation-listener implementation.
     * @param priority the hook priority.
     */
    abstract class MemberHooker<I : HookInvocation> internal constructor(private val priority: YukiHookPriority) {

        private val registrationLock = Any()
        private val result = Result()

        private var isHooked = false
        private var isRegistering = false
        private var isRemovalPending = false

        private var onConductFailureCallback: ((I, Throwable) -> Unit)? = null
        private var onInvocationFailureCallback: ((Throwable) -> Unit)? = null

        private val results = mutableSetOf<YukiHookResult>()

        /** Members selected for this registration group. */
        internal val members = mutableSetOf<Member>()

        /**
         * Removes every registered hook belonging to this hooker.
         * @param callback callback indicating whether any registrations were removed.
         */
        fun removeSelf(callback: (Boolean) -> Unit = {}) = result.remove(callback)

        /**
         * Registers the configured hooker.
         * @return [Result] for removal and invocation failure listeners.
         */
        internal fun build() = result.apply { hook() }

        /** Registers each member independently so one failure does not interrupt the remaining hooks. */
        internal fun hook() {
            if (!YukiHookBridge.isHookAvailable) return
            val pendingMembers = synchronized(registrationLock) {
                if (isHooked) return

                isHooked = true
                isRegistering = true
                members.toList()
            }

            runCatching {
                pendingMembers.forEach { member ->
                    runCatching {
                        YukiHookHelper.hookMember(member, createHooker(member), priority)?.also(::storeRegistration)
                    }.onFailure { logError(it, member) }
                }
            }.also {
                synchronized(registrationLock) {
                    isRegistering = false
                    isRemovalPending = false
                }
            }.getOrThrow()
        }

        /**
         * Stores a completed registration or removes it when removal was requested while registering.
         * @param registration the completed registration.
         */
        private fun storeRegistration(registration: YukiHookResult) {
            val shouldRemove = synchronized(registrationLock) {
                if (isRemovalPending) true else {
                    results.add(registration)
                    false
                }
            }

            if (shouldRemove) runCatching {
                removeRegistration(registration)
            }.onFailure {
                synchronized(registrationLock) {
                    results.add(registration)
                }
            }.getOrThrow()
        }

        /**
         * Removes one native registration outside [registrationLock].
         * @param registration the registration to remove.
         */
        private fun removeRegistration(registration: YukiHookResult) {
            registration.remove()
            YLog.innerD("Removed hooker for ${registration.member}")
        }

        /**
         * Creates the runtime hooker for one member.
         * @param member the member being registered.
         * @return [HookerBridge]
         */
        internal abstract fun createHooker(member: Member): HookerBridge

        /**
         * Notifies invocation failure listeners without choosing a recovery policy.
         * @param invocation the failed invocation.
         * @param throwable the invocation failure.
         */
        protected fun notifyConductFailure(invocation: I, throwable: Throwable) {
            onConductFailureCallback?.invoke(invocation, throwable)
            onInvocationFailureCallback?.invoke(throwable)
            if (onConductFailureCallback == null && onInvocationFailureCallback == null) logError(throwable, invocation.member)
        }

        private fun logError(throwable: Throwable, member: Member) = YLog.innerE("Try to hook $member got an exception", throwable)

        override fun toString() = "${javaClass.simpleName}(members=$members, priority=$priority, isHooked=$isHooked)"

        /**
         * Registration group handle and invocation failure listeners.
         *
         * Listeners observe subsequent invocation failures only.
         */
        inner class Result internal constructor() {

            /**
             * Listens for invocation failures.
             *
             * This listener does not change the hooker's exception policy. Chain failures still propagate.
             * @param callback the listener receiving the concrete invocation and its failure.
             * @return [Result]
             */
            fun onConductFailure(callback: (I, Throwable) -> Unit): Result {
                onConductFailureCallback = callback
                return this
            }

            /**
             * Suppresses default invocation failure logging without changing exception propagation.
             * @return [Result]
             */
            fun ignoredConductFailure() = onConductFailure { _, _ -> }

            /**
             * Listens for invocation failures without receiving the invocation context.
             *
             * This listener is independent of [onConductFailure] and does not observe registration failures.
             * It does not change the hooker's exception policy. Chain failures still propagate.
             * @param callback the listener receiving the invocation failure.
             * @return [Result]
             */
            fun onInvocationFailure(callback: (Throwable) -> Unit): Result {
                onInvocationFailureCallback = callback
                return this
            }

            /**
             * Suppresses default invocation failure logging without changing exception propagation.
             *
             * This does not replace the listener configured through [onConductFailure].
             * @return [Result]
             */
            fun ignoredInvocationFailure() = onInvocationFailure {}

            /**
             * Removes every registration belonging to this hooker.
             * @param callback callback indicating whether any registrations were removed.
             */
            fun remove(callback: (Boolean) -> Unit = {}) {
                val registrations = synchronized(registrationLock) {
                    val registrations = results.toList()
                    results.clear()

                    if (isRegistering) isRemovalPending = true
                    registrations
                }

                runCatching {
                    registrations.forEach(::removeRegistration)
                }.onFailure {
                    synchronized(registrationLock) {
                        results.addAll(registrations)
                    }
                }.getOrThrow()

                callback(registrations.isNotEmpty())
            }

            // region Deprecated functions for backward compatibility

            /**
             * Listens for invocation failures through [onInvocationFailure].
             * @param callback the listener receiving the invocation failure.
             * @return [Result]
             */
            @Deprecated(message = "Use onInvocationFailure instead.", replaceWith = ReplaceWith("onInvocationFailure(callback)"))
            fun onAllFailure(callback: (Throwable) -> Unit) = onInvocationFailure(callback)

            /**
             * Suppresses default invocation failure logging through [ignoredInvocationFailure].
             * @return [Result]
             */
            @Deprecated(message = "Use ignoredInvocationFailure instead.", replaceWith = ReplaceWith("ignoredInvocationFailure()"))
            fun ignoredAllFailure() = ignoredInvocationFailure()

            /**
             * Configures this result through [apply].
             * @param block the configuration block.
             * @return [Result]
             */
            @Deprecated(message = "Use apply instead.", replaceWith = ReplaceWith("apply(block)"))
            inline fun result(block: Result.() -> Unit) = apply(block)

            /**
             * This function has no effect and does not evaluate [condition].
             *
             * - Check the condition before registering hooks instead.
             * @param condition the unused condition block.
             * @return [Result]
             */
            @Deprecated(message = "This API has no effect. Check the condition before registering hooks instead.")
            inline fun by(condition: () -> Boolean) = this

            /**
             * This function has no effect and does not invoke [callback].
             *
             * - Registration completes before this result is returned. Remove this call.
             * @param callback the unused registration success listener.
             * @return [Result]
             */
            @Deprecated(message = "This API has no effect. Remove this call.")
            fun onHooked(callback: (Member) -> Unit) = this

            /**
             * This function has no effect and does not invoke [callback].
             *
             * - Registration failures are logged before this result is returned. Remove this call.
             * @param callback the unused registration failure listener.
             * @return [Result]
             */
            @Deprecated(message = "This API has no effect. Remove this call.")
            fun onHookingFailure(callback: (Throwable) -> Unit) = this

            /**
             * This function has no effect and does not suppress registration failure logs.
             *
             * - Remove this call.
             * @return [Result]
             */
            @Deprecated(message = "This API has no effect. Remove this call.")
            fun ignoredHookingFailure() = this

            // endregion
        }
    }

    /**
     * Chain interceptor implementation.
     * @param priority the hook priority.
     * @param invocation the interceptor invoked for each call.
     */
    class ChainMemberHooker internal constructor(
        priority: YukiHookPriority,
        private val invocation: HookChain.() -> Any?
    ) : MemberHooker<HookChain>(priority) {

        private val dataExtra = Bundle()

        override fun createHooker(member: Member): ChainHookerBridge {
            require(member is Method || member is Constructor<*>) {
                "This type $member not support to hook, supported are constructor and method."
            }

            val invocationCount = AtomicLong()

            return ChainHookerBridge { chain ->
                val invocation = HookChain.from(dataExtra, this@ChainMemberHooker, chain)

                runCatching {
                    YLog.innerD("Executing hooker [intercept] (${invocationCount.incrementAndGet()}) for $member")
                    invocation(invocation)
                }.getOrElse { throwable ->
                    notifyConductFailure(invocation, throwable)

                    // The remaining chain may already have executed, never retry it as failure recovery.
                    throw throwable
                }
            }
        }
    }

    /**
     * Classic before/after and interception hook implementation.
     * @param priority the hook priority.
     * @param hookMode the registration mode.
     */
    class ClassicMemberHooker internal constructor(
        priority: YukiHookPriority,
        private val hookMode: HookMode
    ) : MemberHooker<HookParam>(priority) {

        private var beforeHookCallback: (HookParam.() -> Unit)? = null
        private var afterHookCallback: (HookParam.() -> Unit)? = null
        private var interceptHookCallback: (HookParam.() -> Any?)? = null

        private var onFailureThrowToHost = false
        private var isInterceptHookMode = false

        private val beforeDataExtra = Bundle()
        private val afterDataExtra = Bundle()
        private val interceptDataExtra = Bundle()

        /**
         * Hooks before [Member] execution completes.
         *
         * - Cannot be used together with [intercept].
         * @param callback the [HookParam] function body.
         * @return [HookCallback]
         */
        fun before(callback: HookParam.() -> Unit): HookCallback {
            isInterceptHookMode = false
            beforeHookCallback = callback
            immediateHook()

            return HookCallback()
        }

        /**
         * Hooks after [Member] execution completes.
         *
         * - Cannot be used together with [intercept].
         * @param callback the [HookParam] function body.
         * @return [HookCallback]
         */
        fun after(callback: HookParam.() -> Unit): HookCallback {
            isInterceptHookMode = false
            afterHookCallback = callback
            immediateHook()

            return HookCallback()
        }

        /**
         * Intercepts this [Member] with the callback return value.
         *
         * - The callback return value is ignored for void methods and constructors.
         * - Cannot be used together with [before] or [after].
         * @param callback the [HookParam] function body.
         */
        fun intercept(callback: HookParam.() -> Any?) {
            isInterceptHookMode = true
            interceptHookCallback = callback
            immediateHook()
        }

        /**
         * Intercepts this [Member] with a constant return value.
         *
         * - The [value] is ignored for void methods and constructors.
         * - Note: Setting a [Member] with a constant return value such as [Int], [Long],
         *   or [Boolean] to null may cause the host to throw an exception.
         * - Cannot be used together with [before] or [after].
         * @param value the replacement constant value, null by default.
         */
        fun intercept(value: Any? = null) = intercept { value }

        // region Deprecated functions for backward compatibility

        /** Intercepts and replaces this [Member] with a return value. */
        @Deprecated(message = "Use intercept instead.", replaceWith = ReplaceWith("intercept(callback)"))
        fun replaceAny(callback: HookParam.() -> Any?) = intercept(callback)

        /** Intercepts and replaces this [Member] without a return value ([Unit]). */
        @Deprecated(message = "Use intercept instead.", replaceWith = ReplaceWith("intercept(callback)"))
        fun replaceUnit(callback: HookParam.() -> Unit) = intercept(callback)

        /** Intercepts and replaces the [Member] return value. */
        @Deprecated(message = "Use intercept instead.", replaceWith = ReplaceWith("intercept(any)"))
        fun replaceTo(any: Any?) = intercept(any)

        /** Intercepts and replaces the [Member] return value with true. */
        @Deprecated(message = "Use intercept instead.", replaceWith = ReplaceWith("intercept(true)"))
        fun replaceToTrue() = intercept(true)

        /** Intercepts and replaces the [Member] return value with false. */
        @Deprecated(message = "Use intercept instead.", replaceWith = ReplaceWith("intercept(false)"))
        fun replaceToFalse() = intercept(false)

        // endregion

        private fun immediateHook() {
            if (hookMode == HookMode.IMMEDIATE) hook()
        }

        override fun createHooker(member: Member): ClassicHookerBridge {
            // The mode is fixed at registration, even if the creator is configured again later.
            val isInterception = isInterceptHookMode

            return object : ClassicHookerBridge {

                private val beforeInvocationCount = AtomicLong()
                private val afterInvocationCount = AtomicLong()

                override fun beforeHookedMember(param: ClassicHookerBridge.Param) {
                    val invocation = HookParam.from(
                        dataExtraStorage = if (isInterception) interceptDataExtra else beforeDataExtra,
                        this@ClassicMemberHooker, param
                    )

                    val result = runCatching {
                        val callback = if (isInterception) interceptHookCallback else beforeHookCallback
                        if (callback != null) YLog.innerD(
                            "Executing hooker [${if (isInterception) "intercept" else "before"}] " +
                                "(${beforeInvocationCount.incrementAndGet()}) for $member"
                        )

                        val value = callback?.invoke(invocation)
                        val returnType = (param.member as? Method?)?.returnType
                        val isReturnIgnored = param.member is Constructor<*> || returnType == Void.TYPE
                        val interceptedValue = if (isInterception && isReturnIgnored) null else value
                        val target = (if (isInterception) interceptedValue else param.result)?.javaClass

                        checkingReturnType(returnType, target)

                        interceptedValue
                    }.getOrElse {
                        notifyConductFailure(invocation, it)

                        // A failed interception invokes the original unhooked `Member` to keep the host running normally.
                        if (isInterception) invocation.callOriginal() else {
                            if (onFailureThrowToHost) param.throwable = it
                            null
                        }
                    }
                    if (isInterception) param.result = result
                }

                override fun afterHookedMember(param: ClassicHookerBridge.Param) {
                    if (isInterception) return

                    HookParam.from(afterDataExtra, this@ClassicMemberHooker, param).also { invocation ->
                        runCatching {
                            val callback = afterHookCallback
                            if (callback != null) {
                                YLog.innerD("Executing hooker [after] (${afterInvocationCount.incrementAndGet()}) for $member")
                                callback(invocation)
                            }
                        }.onFailure {
                            notifyConductFailure(invocation, it)
                            if (onFailureThrowToHost) param.throwable = it
                        }
                    }
                }
            }
        }

        /**
         * Checks the return value of the hooked [Member].
         * @param origin the original return value.
         * @param target the target return value.
         * @throws IllegalStateException if the return value is incorrect.
         */
        private fun checkingReturnType(origin: Class<*>?, target: Class<*>?) {
            if (origin == null || target == null) return
            if (origin == classOf<Any>()) return

            origin.asHookReturnType().also { o ->
                target.asHookReturnType().also { t ->
                    if (o isNotSubclassOf t && t isNotSubclassOf o) error(
                        "Hooked method return type match failed, required $origin but got $target"
                    )
                }
            }
        }

        /** Normalizes boxed primitives and Kotlin [Unit] to their JVM Hook return types. */
        private fun Class<*>.asHookReturnType() = if (this == classOf<Unit>()) Void.TYPE else kotlin.javaPrimitiveType ?: this

        /**
         * Hook callback body implementation.
         */
        inner class HookCallback internal constructor() {

            /** Throws an exception to the current host when one occurs in the callback body. */
            fun onFailureThrowToHost() {
                onFailureThrowToHost = true
            }

            /** Throws an exception to the current host when one occurs in the callback body. */
            @Deprecated(message = "Use onFailureThrowToHost instead.", replaceWith = ReplaceWith("onFailureThrowToHost()"))
            fun onFailureThrowToApp() = onFailureThrowToHost()
        }
    }

    /**
     * Hook mode type definition.
     */
    internal enum class HookMode {
        /** Lazy mode. */
        LAZY,

        /** Immediate mode. */
        IMMEDIATE
    }
}