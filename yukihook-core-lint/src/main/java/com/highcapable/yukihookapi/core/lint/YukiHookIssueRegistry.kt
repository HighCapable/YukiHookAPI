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
 * This file is created by fankes on 2026/9/16.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.core.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.highcapable.yukihookapi.core.lint.detector.ClassicHookerDetector
import com.highcapable.yukihookapi.core.lint.detector.InternalApiDetector
import com.highcapable.yukihookapi.core.lint.detector.LibXposedEntryDetector
import com.highcapable.yukihookapi.core.lint.detector.ModuleRuntimeDetector
import com.highcapable.yukihookapi.core.lint.detector.ModuleUsageDetector
import com.highcapable.yukihookapi.core.lint.detector.RuntimeRequirementDetector
import com.highcapable.yukihookapi.generated.YukiHookProperties

class YukiHookIssueRegistry : IssueRegistry() {

    override val issues get() = listOf(
        InternalApiDetector.ISSUE,
        RuntimeRequirementDetector.ISSUE,
        RuntimeRequirementDetector.NEW_XPOSED_API_ISSUE,
        LibXposedEntryDetector.ISSUE,
        LibXposedEntryDetector.HOT_RELOAD_ISSUE,
        LibXposedEntryDetector.SYSTEM_FRAMEWORK_IN_SCOPE_ISSUE,
        ModuleRuntimeDetector.REGISTRATION_ISSUE,
        ModuleRuntimeDetector.HOOK_LOADING_ISSUE,
        ModuleRuntimeDetector.RETAINED_VALUE_ISSUE,
        ModuleRuntimeDetector.SYSTEM_FRAMEWORK_ISSUE,
        ModuleRuntimeDetector.SYSTEM_FRAMEWORK_IN_PACKAGES_ISSUE,
        ModuleRuntimeDetector.SYSTEM_PROCESS_NAME_ISSUE,
        ModuleRuntimeDetector.SYSTEM_DATA_CHANNEL_ISSUE,
        ClassicHookerDetector.ISSUE,
        ModuleUsageDetector.MODULE_APPLICATION_ISSUE,
        ModuleUsageDetector.VARIOUS_CLASS_MISSING_LOADER_ISSUE,
        ModuleUsageDetector.VARIOUS_CLASS_HOST_LOADER_ISSUE
    )

    override val minApi = YukiHookProperties.PROJECT_YUKIHOOK_CORE_LINT_MIN_API
    override val api = CURRENT_API
    override val vendor = Vendor(
        vendorName = YukiHookProperties.PROJECT_NAME,
        identifier = YukiHookProperties.PROJECT_YUKIHOOK_CORE_LINT_IDENTIFIER,
        feedbackUrl = "${YukiHookProperties.PROJECT_URL}/issues",
        contact = YukiHookProperties.PROJECT_URL
    )
}