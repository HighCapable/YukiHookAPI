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
 * This file is created by fankes on 2026/9/17.
 */
package com.highcapable.yukihookapi.core.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.highcapable.yukihookapi.core.lint.detector.extension.isConstructorReference
import com.highcapable.yukihookapi.core.lint.detector.extension.reportNode
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiMember
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UTypeReferenceExpression

class InternalApiDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val RUNTIME_PACKAGE_NAME = "${DeclaredSymbol.CORE_PACKAGE_NAME}.runtime"
        private const val RUNTIME_BRIDGE_PACKAGE_NAME = "$RUNTIME_PACKAGE_NAME.bridge"
        private const val HOOKER_BRIDGE_PACKAGE_NAME = "$RUNTIME_BRIDGE_PACKAGE_NAME.hooker"

        private const val GENERATED_MODULE_STATUS_PACKAGE_NAME = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.bridge.status"
        private const val GENERATED_MODULE_STATUS_CLASS_PREFIX = "YukiHookLegacyModuleStatus_Impl_"

        private val INTERNAL_API_CLASSES = setOf(
            "$RUNTIME_PACKAGE_NAME.YukiHookBridge",
            "$HOOKER_BRIDGE_PACKAGE_NAME.ChainHookerBridge",
            "$HOOKER_BRIDGE_PACKAGE_NAME.ClassicHookerBridge",
            "$HOOKER_BRIDGE_PACKAGE_NAME.HookerBridge",
            "$HOOKER_BRIDGE_PACKAGE_NAME.HookPoint",
            "$RUNTIME_BRIDGE_PACKAGE_NAME.ExecutableInvokerBridge",
            "$RUNTIME_BRIDGE_PACKAGE_NAME.FrameworkHookBridge",
            "$RUNTIME_BRIDGE_PACKAGE_NAME.SharedFilesBridge",
            "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.bridge.caller.YukiHookModuleCaller",
            "${DeclaredSymbol.CORE_PACKAGE_NAME}.YukiHookAPI_Impl",
            "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.application.ModuleApplication_Impl"
        )
        private val GENERATED_ENTRY_SUPER_CLASSES = setOf(
            "$RUNTIME_PACKAGE_NAME.libxposed.LibXposedEntry",
            "$RUNTIME_PACKAGE_NAME.xposed82.Xposed82Entry"
        )

        val ISSUE = Issue.create(
            id = "YukiHookInternalApi",
            briefDescription = "YukiHookAPI internal API must not be used directly.",
            explanation = "YukiHookAPI internal and generated declarations are public for runtime linkage only and must not be used directly.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                InternalApiDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes() = listOf(
        UCallExpression::class.java,
        USimpleNameReferenceExpression::class.java,
        UTypeReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitCallExpression(node: UCallExpression) {
            val method = node.resolve() ?: return
            if (!method.isInternalApi()) return

            report(
                scope = node,
                apiName = if (method.isConstructor) method.containingClass?.name ?: method.name
                else node.methodName ?: method.name,
                location = context.getCallLocation(node, includeReceiver = true, includeArguments = true)
            )
        }

        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            val declaration = node.resolve() as? PsiMember ?: return
            if (!declaration.isInternalApi()) return
            if (declaration is PsiClass && (node.isConstructorReference() || node.hasInternalApiSelector())) return

            val reportNode = node.reportNode()
            report(
                scope = reportNode,
                apiName = node.identifier,
                location = context.getLocation(reportNode)
            )
        }

        override fun visitTypeReferenceExpression(node: UTypeReferenceExpression) {
            val declaration = context.evaluator.getTypeClass(node.type) ?: return
            if (!declaration.isInternalApi()) return

            report(
                scope = node,
                apiName = declaration.name ?: node.asSourceString(),
                location = context.getLocation(node)
            )
        }

        private fun report(scope: UElement, apiName: String, location: Location) {
            val lintFix = buildReplaceFix("Delete Call Expression", "", range = location)

            context.report(
                issue = ISSUE,
                scope = scope,
                location = location,
                message = "`$apiName` is an internal YukiHookAPI's API and must not be used directly.",
                quickfixData = lintFix
            )
        }

        private fun PsiMember.isInternalApi() = when (this) {
            is PsiClass -> isInternalApiClass()
            else -> containingClass?.isInternalApiClass() == true
        }

        private fun PsiClass.isInternalApiClass(): Boolean {
            var type: PsiClass? = this
            while (type != null) {
                val qualifiedName = type.qualifiedName
                if (qualifiedName in INTERNAL_API_CLASSES || qualifiedName.isGeneratedModuleStatusClass()) return true
                type = type.containingClass
            }
            return superClass?.qualifiedName in GENERATED_ENTRY_SUPER_CLASSES
        }

        private fun String?.isGeneratedModuleStatusClass() =
            this?.substringBeforeLast('.', missingDelimiterValue = "") == GENERATED_MODULE_STATUS_PACKAGE_NAME &&
                substringAfterLast('.').startsWith(GENERATED_MODULE_STATUS_CLASS_PREFIX)

        private fun USimpleNameReferenceExpression.hasInternalApiSelector(): Boolean {
            val parent = uastParent as? UQualifiedReferenceExpression ?: return false
            if (parent.receiver != this) return false

            val declaration = when (val selector = parent.selector) {
                is UCallExpression -> selector.resolve()
                is USimpleNameReferenceExpression -> selector.resolve()
                else -> null
            } as? PsiMember ?: return false
            return declaration.isInternalApi()
        }
    })
}