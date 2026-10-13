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
 * This file is created by fankes on 2026/9/17.
 */
package com.highcapable.yukihookapi.core.lint.detector

import com.android.SdkConstants.TAG_APPLICATION
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.resolveManifestName
import com.android.utils.XmlUtils
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement

class ModuleUsageDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val APPLICATION = "android.app.Application"
        private const val MODULE_APPLICATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.application.ModuleApplication"

        private val IMPLEMENTATION = Implementation(
            ModuleUsageDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val MODULE_APPLICATION_ISSUE = Issue.create(
            id = "UseModuleApplication",
            briefDescription = "Inherit ModuleApplication to enable full functionality",
            explanation = "YukiHook module applications should inherit `ModuleApplication` to " +
                "initialize module runtime services and enable full functionality.",
            category = Category.USABILITY,
            priority = 5,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes() = listOf(UClass::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        private val applicationName = context.findManifestApplicationName()

        override fun visitClass(node: UClass) {
            if (node.qualifiedName != applicationName) return
            if (!context.evaluator.extendsClass(node.javaPsi, APPLICATION, false)) return
            if (context.evaluator.extendsClass(node.javaPsi, MODULE_APPLICATION, false)) return

            context.report(
                issue = MODULE_APPLICATION_ISSUE,
                scope = node as UElement,
                location = context.getNameLocation(node),
                message = "`${node.name}` should inherit `ModuleApplication` to enable full YukiHook functionality"
            )
        }
    })

    private fun JavaContext.findManifestApplicationName(): String? {
        val manifest = project.mergedManifest?.documentElement ?: return null
        val application = XmlUtils.getFirstSubTagByName(manifest, TAG_APPLICATION) ?: return null

        return resolveManifestName(application, project).takeIf { it.isNotBlank() }
    }
}