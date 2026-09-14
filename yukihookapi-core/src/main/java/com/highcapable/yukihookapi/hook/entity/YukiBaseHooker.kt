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
 * This file is created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.hook.entity

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithLibXposed
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.bridge.type.HookEntryType

/**
 * Base hooker implementation for [YukiHookAPI].
 * Inherit this class to organize multiple hooker feature modules in a single Xposed module.
 *
 * See [InjectYukiHookWithLibXposed] or [InjectYukiHookWithXposed] for the entry-point configuration.
 * @see InjectYukiHookWithLibXposed
 * @see InjectYukiHookWithXposed
 * @see PackageParam
 */
abstract class YukiBaseHooker : PackageParam() {

    private val contextLock = Any()

    /**
     * Binds a [PackageParam] context and loads this hooker.
     * @param param the [PackageParam] to use.
     */
    internal fun bindTo(param: PackageParam) {
        synchronized(contextLock) {
            val targetWrapper = param.wrapper ?: return

            val boundWrapper = wrapper
            if (boundWrapper?.id == targetWrapper.id) return
            if (boundWrapper != null &&
                (boundWrapper.type != HookEntryType.ZYGOTE || targetWrapper.type == HookEntryType.ZYGOTE)
            ) {
                YLog.innerW(
                    msg = "This hooker \"${this::class.java.name}\" is singleton or reused, " +
                        "but it is already bound to hook entry \"${boundWrapper.id}\" while the current entry is " +
                        "\"${targetWrapper.id}\". Make sure your hooker supports multiple instances for this situation, " +
                        "the current hook entry will be ignored."
                )
                return
            }

            bind(targetWrapper)
            runCatching { onHook() }.onFailure { YLog.innerE("An exception occurred in $this", it) }
        }
    }

    /** Starts this hooker. */
    abstract fun onHook()
}