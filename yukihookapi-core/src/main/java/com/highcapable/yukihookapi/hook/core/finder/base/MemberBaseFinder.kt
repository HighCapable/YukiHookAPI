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
 * This file is created by fankes on 2022/2/18.
 */
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.utils.factory.await
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Base implementation for [Member] finders.
 * @param tag the current finder identifier.
 * @param classSet the [Class] instance to search.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
abstract class MemberBaseFinder internal constructor(private val tag: String, internal open val classSet: Class<*>? = null) : BaseFinder() {

    internal companion object {

        /** Message used when [classSet] is null. */
        internal const val CLASSSET_IS_NULL = "classSet is null"
    }

    /** The current [MemberHookerManager]. */
    internal var hookerManager = MemberHookerManager()

    /** Whether the remedy plan is being used. */
    internal var isUsingRemedyPlan = false

    /** Whether error warning suppression is enabled. */
    internal var isIgnoreErrorLogs = false

    /** The currently found [Member] instances. */
    internal var memberInstances = mutableListOf<Member>()

    /**
     * Converts [MutableList]<[Member]> to [MutableList]<[Field]>.
     * @return [MutableList]<[Field]>
     */
    internal fun MutableList<Member>.fields() =
        mutableListOf<Field>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Field?)?.also { f -> it.add(f) } } }

    /**
     * Converts [MutableList]<[Member]> to [MutableList]<[Method]>.
     * @return [MutableList]<[Method]>
     */
    internal fun MutableList<Member>.methods() =
        mutableListOf<Method>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Method?)?.also { m -> it.add(m) } } }

    /**
     * Converts [MutableList]<[Member]> to [MutableList]<[Constructor]>.
     * @return [MutableList]<[Constructor]>
     */
    internal fun MutableList<Member>.constructors() =
        mutableListOf<Constructor<*>>().also { takeIf { e -> e.isNotEmpty() }?.forEach { e -> (e as? Constructor<*>?)?.also { c -> it.add(c) } } }

    /**
     * Converts the target type to a supported compatible type.
     * @return [Class] or null.
     */
    internal fun Any?.compat() = compat(tag, classSet?.classLoader)

    /**
     * Prints debug information during Hook operations when [YukiHookAPI.Configs.isDebug] is enabled
     * and [HookApiCategoryHelper.hasAvailableHookApi] is true.
     * @param msg the message content.
     */
    internal fun debugMsg(msg: String) {
        if (HookApiCategoryHelper.hasAvailableHookApi && hookerManager.instance != null) YLog.innerD(msg)
    }

    /**
     * Prints a log when an error occurs.
     * @param msg the message content.
     * @param e the exception stack trace, null by default.
     * @param e the exception stack traces, empty by default.
     * @param isAlwaysMode whether to print the log every time regardless of conditions.
     */
    internal fun errorMsg(msg: String = "", e: Throwable? = null, es: List<Throwable> = emptyList(), isAlwaysMode: Boolean = false) {
        // Checks whether the message is [CLASSSET_IS_NULL].
        if (e?.message == CLASSSET_IS_NULL) return
        await {
            if (isIgnoreErrorLogs || hookerManager.isNotIgnoredNoSuchMemberFailure.not()) return@await
            if (isAlwaysMode.not() && isUsingRemedyPlan) return@await
            YLog.innerE("NoSuch$tag happend in [$classSet] $msg".trim(), e)
            es.forEachIndexed { index, e -> YLog.innerE("Throwable [${index + 1}]", e) }
        }
    }

    /**
     * Returns the result handler and assigns it to the target [YukiMemberHookCreator.MemberHookCreator].
     *
     * - This operation is performed automatically by the block and should not be called manually.
     * @return [BaseFinder.BaseResult]
     */
    internal abstract fun process(): BaseResult

    /**
     * Returns an exception-only result handler and applies it to the target [YukiMemberHookCreator.MemberHookCreator].
     *
     * - This operation is performed automatically by the block and should not be called manually.
     * @param throwable the exception.
     * @return [BaseFinder.BaseResult]
     */
    internal abstract fun denied(throwable: Throwable?): BaseResult

    /**
     * Current Hooker management implementation.
     */
    internal inner class MemberHookerManager {

        /** The current Hooker. */
        internal var instance: YukiMemberHookCreator.MemberHookCreator? = null

        /** Whether the current [Member] is assigned to the current Hooker. */
        internal var isMemberBinded = false

        /**
         * Gets whether no failure handler is configured for missing methods, constructors, or fields during Hook operations.
         * @return [Boolean] whether no failure handler is configured.
         */
        internal val isNotIgnoredNoSuchMemberFailure get() = instance?.isNotIgnoredNoSuchMemberFailure ?: true

        /**
         * Binds the current [Member] to the current Hooker.
         * @param member the current [Member].
         */
        internal fun bindMember(member: Member?) {
            instance?.members?.clear()
            member?.also { instance?.members?.add(it) }
        }

        /**
         * Binds the [Member] list to the current Hooker.
         * @param members the current [Member] list.
         */
        internal fun bindMembers(members: MutableList<Member>) {
            instance?.members?.clear()
            members.forEach { instance?.members?.add(it) }
        }
    }
}