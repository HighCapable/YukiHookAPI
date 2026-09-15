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
 * This file is created by fankes on 2026/9/11.
 */
package com.highcapable.yukihookapi.compiler

import com.highcapable.yukihookapi.generated.YukiHookAPIProperties

object DeclaredSymbol {

    const val API_PACKAGE_NAME = YukiHookAPIProperties.PROJECT_GROUP_NAME
    const val API_IMPL_CLASS_NAME = "YukiHookAPI_Impl"

    const val MODULE_APPLICATION_PACKAGE_NAME = "$API_PACKAGE_NAME.hook.xposed.application"
    const val MODULE_APPLICATION_IMPL_CLASS_NAME = "ModuleApplication_Impl"

    const val MODULE_STATUS_PACKAGE_NAME = "$API_PACKAGE_NAME.hook.xposed.bridge.status"
    const val LEGACY_MODULE_STATUS_IMPL_CLASS_NAME = "YukiHookLegacyModuleStatus_Impl"

    const val HOOK_ENTRY_PACKAGE_NAME = "$API_PACKAGE_NAME.hook.xposed"
    const val HOOK_ENTRY_CLASS_NAME = "YukiHookXposedModule"
    const val HOOK_ENTRY_CLASS = "$HOOK_ENTRY_PACKAGE_NAME.$HOOK_ENTRY_CLASS_NAME"
    const val LEGACY_HOOK_ENTRY_CLASS = "$API_PACKAGE_NAME.hook.xposed.proxy.IYukiHookXposedInit"

    const val MODULE_CALLER_PACKAGE_NAME = "$API_PACKAGE_NAME.hook.xposed.bridge.caller"
    const val MODULE_CALLER_CLASS_NAME = "YukiHookModuleCaller"

    const val ANDROIDX_ANNOTATION_PACKAGE_NAME = "androidx.annotation"
    const val KEEP_ANNOTATION_CLASS_NAME = "Keep"

    const val ANDROID_UTIL_PACKAGE_NAME = "android.util"
    const val LOG_CLASS_NAME = "Log"
}