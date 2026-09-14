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
 * This file is created by fankes on 2022/4/30.
 * This file is modified by fankes on 2022/1/10.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.xposed.bridge.event

import com.highcapable.yukihookapi.hook.xposed.bridge.caller.YukiXposedModuleCaller
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Registers framework event listeners from [IYukiHookXposedInit.onInit].
 *
 * The configuration block runs immediately, event callbacks run when the framework dispatches them.
 * Use native Xposed APIs in these callbacks and keep `YukiHookAPI` hook loading in [IYukiHookXposedInit.onHook].
 *
 * Only one registration block is allowed per module process, even across different entry instances.
 * @receiver [IYukiHookXposedInit]
 * @param block the event configuration block.
 * @throws IllegalStateException if called outside [IYukiHookXposedInit.onInit] or more than once.
 */
fun IYukiHookXposedInit.registerFrameworkEvents(block: YukiXposed82Event.() -> Unit) =
    YukiXposedModuleCaller.callRegisterFrameworkEvents(this) { YukiXposed82Event.block() }

/**
 * Registers listeners for framework lifecycle events exposed by Xposed API 82.
 * Configure listeners only inside [registerFrameworkEvents]. Each lifecycle listener can be set once.
 */
object YukiXposed82Event {

    internal var initZygoteCallback: ((IXposedHookZygoteInit.StartupParam) -> Unit)? = null
        private set
    internal var handleLoadPackageCallback: ((XC_LoadPackage.LoadPackageParam) -> Unit)? = null
        private set

    /**
     * Sets the `initZygote` event listener.
     * @param callback the event callback.
     */
    fun onInitZygote(callback: (IXposedHookZygoteInit.StartupParam) -> Unit) {
        YukiXposedModuleCaller.checkFrameworkEventsRegistration()
        check(initZygoteCallback == null) {
            "`onInitZygote` is already registered."
        }

        initZygoteCallback = callback
    }

    /**
     * Sets the `handleLoadPackage` event listener.
     * @param callback the event callback.
     */
    fun onHandleLoadPackage(callback: (XC_LoadPackage.LoadPackageParam) -> Unit) {
        YukiXposedModuleCaller.checkFrameworkEventsRegistration()
        check(handleLoadPackageCallback == null) {
            "`onHandleLoadPackage` is already registered."
        }

        handleLoadPackageCallback = callback
    }
}