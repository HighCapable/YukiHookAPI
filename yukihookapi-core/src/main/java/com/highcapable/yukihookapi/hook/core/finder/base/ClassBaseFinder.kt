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
 * This file is created by fankes on 2022/9/4.
 */
@file:Suppress("DEPRECATION")

package com.highcapable.yukihookapi.hook.core.finder.base

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.core.api.compat.HookApiCategoryHelper
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.log.YLog

/**
 * Base implementation for class finders.
 * @param loaderSet the current [ClassLoader].
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
abstract class ClassBaseFinder internal constructor(internal open val loaderSet: ClassLoader? = null) : BaseFinder() {

    internal companion object {

        /** Message used when [loaderSet] is null. */
        internal const val LOADERSET_IS_NULL = "loaderSet is null"
    }

    /** Classes found by the current lookup. */
    internal var classInstances = mutableListOf<Class<*>>()

    /** Whether finder error logs are suppressed. */
    internal var isIgnoreErrorLogs = false

    /**
     * Converts the target type to a supported compatible type.
     * @param any the instance to convert.
     * @param tag the identifier of the class being searched.
     * @return [Class] or null.
     */
    internal fun compatType(any: Any?, tag: String) = any?.compat(tag, loaderSet)

    /**
     * Prints debug information when [YukiHookAPI.Configs.isDebug] is enabled and a Hook API is available.
     * @param msg the message content.
     */
    internal fun debugMsg(msg: String) {
        if (HookApiCategoryHelper.hasAvailableHookApi) YLog.innerD(msg)
    }

    /**
     * Prints an error log when lookup fails.
     * @param e the exception stack trace, defaults to null.
     */
    internal fun errorMsg(e: Throwable? = null) {
        if (isIgnoreErrorLogs) return
        // Ignores the expected [LOADERSET_IS_NULL] state.
        if (e?.message == LOADERSET_IS_NULL) return
        YLog.innerE("NoClassDefFound happend in [$loaderSet]", e)
    }

    override fun failure(throwable: Throwable?) = error("DexClassFinder does not contain this usage")
}