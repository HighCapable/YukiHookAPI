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
 * This file is created by fankes on 2026/9/24.
 */
package com.highcapable.yukihookapi.core.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import org.jetbrains.uast.UAnnotation

class LibXposedEntryDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.YukiHookLibXposedEntry"

        private const val MIN_API_VERSION = "minApiVersion"
        private const val TARGET_API_VERSION = "targetApiVersion"
        private const val LOWEST_API_VERSION = 101

        val ISSUE = Issue.create(
            id = "InvalidLibXposedApiVersion",
            briefDescription = "Invalid Xposed API version for module entry",
            explanation = "The minimum Xposed API version must be at least 101, and the target API version " +
                "must not be lower than the minimum version.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                LibXposedEntryDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes() = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitAnnotation(node: UAnnotation) {
            if (node.qualifiedName != ENTRY_ANNOTATION) return

            val minValue = node.findAttributeValue(MIN_API_VERSION) ?: return
            val minVersion = minValue.evaluate() as? Int ?: return
            if (minVersion < LOWEST_API_VERSION) {
                val location = context.getLocation(minValue)
                context.report(
                    issue = ISSUE,
                    scope = node,
                    location = location,
                    message = "`$MIN_API_VERSION` must be at least $LOWEST_API_VERSION",
                    quickfixData = buildReplaceFix(
                        name = "Change to $LOWEST_API_VERSION",
                        replacement = "$LOWEST_API_VERSION",
                        range = location,
                        reformat = false
                    )
                )
            }

            val targetValue = node.findAttributeValue(TARGET_API_VERSION) ?: return
            val targetVersion = targetValue.evaluate() as? Int ?: return
            if (targetVersion >= minVersion) return

            val location = context.getLocation(targetValue)
            context.report(
                issue = ISSUE,
                scope = node,
                location = location,
                message = "`$TARGET_API_VERSION` must be at least `$MIN_API_VERSION`",
                quickfixData = buildReplaceFix(
                    name = "Change to $minVersion",
                    replacement = "$minVersion",
                    range = location,
                    reformat = false
                )
            )
        }
    })
}