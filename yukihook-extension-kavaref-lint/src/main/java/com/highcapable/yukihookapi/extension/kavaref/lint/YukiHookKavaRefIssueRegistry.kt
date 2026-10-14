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

package com.highcapable.yukihookapi.extension.kavaref.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.Vendor
import com.android.tools.lint.detector.api.CURRENT_API
import com.highcapable.yukihookapi.extension.kavaref.lint.detector.PackageParamDetector
import com.highcapable.yukihookapi.generated.YukiHookProperties

class YukiHookKavaRefIssueRegistry : IssueRegistry() {

    override val issues get() = listOf(
        PackageParamDetector.CLASS_LOADING_WITHOUT_HOST_CLASS_LOADER_ISSUE,
        PackageParamDetector.CLASS_LOADING_WITH_HOST_CLASS_LOADER_ISSUE
    )

    override val minApi = YukiHookProperties.PROJECT_YUKIHOOK_EXTENSION_KAVAREF_LINT_MIN_API
    override val api = CURRENT_API
    override val vendor = Vendor(
        vendorName = YukiHookProperties.PROJECT_NAME,
        identifier = YukiHookProperties.PROJECT_YUKIHOOK_EXTENSION_KAVAREF_LINT_IDENTIFIER,
        feedbackUrl = "${YukiHookProperties.PROJECT_URL}/issues",
        contact = YukiHookProperties.PROJECT_URL
    )
}