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
 * This file is created by fankes on 2022/8/14.
 */
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.config

import android.app.Activity
import android.content.Intent

/**
 * Stores configuration for the current proxied [Activity].
 */
internal object ActivityProxyConfig {

    /**
     * Name of the [Intent] used for proxying.
     */
    internal var proxyIntentName = ""

    /**
     * Class name of the [Activity] to proxy.
     */
    internal var proxyClassName = ""
}