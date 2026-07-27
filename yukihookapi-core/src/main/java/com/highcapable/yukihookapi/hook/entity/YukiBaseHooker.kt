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
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * Base Hooker implementation for [YukiHookAPI].
 *
 * Extend this class to organize multiple Hooker feature modules in a single Xposed module.
 *
 * See [InjectYukiHookWithXposed] for the entry-point configuration.
 *
 * See [Created by Custom Hooker](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
 */
abstract class YukiBaseHooker : PackageParam() {

    /**
     * Assigns and copies a [PackageParam].
     * @param packageParam the [PackageParam] to use.
     */
    internal fun assignInstance(packageParam: PackageParam) {
        assign(packageParam.wrapper)
        runCatching { onHook() }.onFailure { YLog.innerE("An exception occurred in $this", it) }
    }

    /** Starts this Hooker. */
    abstract fun onHook()
}