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
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.android.tools.lint.detector.api.resolveManifestName
import com.android.utils.XmlUtils
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.analyzeOrNull
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.skipParenthesizedExprDown

class ModuleUsageDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val APPLICATION = "android.app.Application"
        private const val MODULE_APPLICATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.application.ModuleApplication"
        private const val PACKAGE_PARAM_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.param.PackageParam"
        private const val VARIOUS_CLASS = "com.highcapable.kavaref.extension.VariousClass"

        private const val LOADER_PARAMETER = "loader"
        private const val HOST_CLASS_LOADER = "hostClassLoader"

        private val HOST_CLASS_LOADER_GETTERS = setOf("getHostClassLoader", "getAppClassLoader")
        private val VARIOUS_CLASS_LOADING_FUNCTIONS = mapOf("load" to "toClass", "loadOrNull" to "toClassOrNull")

        private val PACKAGE_PARAM_CLASS_ID = ClassId.topLevel(FqName(PACKAGE_PARAM_CLASS))

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

        val VARIOUS_CLASS_MISSING_LOADER_ISSUE = Issue.create(
            id = "VariousClassWithoutHostClassLoader",
            briefDescription = "VariousClass loaded without the host class loader",
            explanation = "In a `PackageParam` scope, `String.toClass` uses `hostClassLoader` by default, but `VariousClass.load` " +
                "and `VariousClass.loadOrNull` are KavaRef functions that use the module class loader when no loader is passed. " +
                "Use `toClass` or `toClassOrNull` to load the class from the host.",
            category = Category.USABILITY,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )

        val VARIOUS_CLASS_HOST_LOADER_ISSUE = Issue.create(
            id = "VariousClassWithHostClassLoader",
            briefDescription = "VariousClass loaded with an explicit host class loader",
            explanation = "In a `PackageParam` scope, `VariousClass.toClass` and `VariousClass.toClassOrNull` use `hostClassLoader` " +
                "by default, passing it to `load` or `loadOrNull` is unnecessary.",
            category = Category.USABILITY,
            priority = 3,
            severity = Severity.INFORMATIONAL,
            implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes() = listOf(UClass::class.java, UCallExpression::class.java)

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

        override fun visitCallExpression(node: UCallExpression) {
            val source = node.sourcePsi as? KtCallExpression ?: return
            val callee = source.calleeExpression ?: return
            val arguments = source.valueArgumentList ?: return
            val replacementName = VARIOUS_CLASS_LOADING_FUNCTIONS[node.methodName] ?: return
            val method = node.resolve() ?: return
            if (method.containingClass?.qualifiedName != VARIOUS_CLASS) return
            if (!source.hasPackageParamReceiver()) return

            val loader = context.evaluator.computeArgumentMapping(node, method)
                .entries.firstOrNull { it.value.name == LOADER_PARAMETER }?.key
            val location = context.getRangeLocation(callee, 0, arguments, 0)

            when {
                loader == null -> context.report(
                    issue = VARIOUS_CLASS_MISSING_LOADER_ISSUE,
                    scope = node,
                    location = location,
                    message = "`${node.methodName}` does not use `$HOST_CLASS_LOADER`, use `$replacementName` instead",
                    quickfixData = replaceFix(source, method, location, replacementName)
                )
                loader.isHostClassLoader() -> context.report(
                    issue = VARIOUS_CLASS_HOST_LOADER_ISSUE,
                    scope = node,
                    location = location,
                    message = "`$replacementName` uses `$HOST_CLASS_LOADER` by default, use it instead",
                    quickfixData = replaceFix(source, method, location, replacementName)
                )
            }
        }

        private fun KtCallExpression.hasPackageParamReceiver() = analyzeOrNull {
            containingKtFile.scopeContext(this@hasPackageParamReceiver).implicitReceivers.any {
                it.type.isSubtypeOf(PACKAGE_PARAM_CLASS_ID)
            }
        } == true

        private fun UExpression.isHostClassLoader(): Boolean {
            val expression = skipParenthesizedExprDown()
            val getter = (expression as? UResolvable)?.resolve() as? PsiMethod ?: return false
            if (getter.name !in HOST_CLASS_LOADER_GETTERS || getter.containingClass?.qualifiedName != PACKAGE_PARAM_CLASS) return false

            // Only the current receiver matches the class loader used by the PackageParam overloads.
            val receiver = (expression as? UQualifiedReferenceExpression)?.receiver?.skipParenthesizedExprDown() ?: return true
            return receiver is UThisExpression && receiver.label == null
        }

        private fun replaceFix(source: KtCallExpression, method: PsiMethod, location: Location, replacementName: String): LintFix? {
            val parameters = method.parameterList.parameters
            // Remaining arguments are named, removing the loader would shift positional ones.
            val remaining = source.valueArguments.mapIndexedNotNull { index, argument ->
                val expression = argument.getArgumentExpression() ?: return null
                val parameter = argument.getArgumentName()?.asName?.asString() ?: parameters.getOrNull(index)?.name ?: return null
                if (argument.isSpread) return null
                "$parameter = ${expression.text}".takeIf { parameter != LOADER_PARAMETER }
            }
            val typeArguments = source.typeArgumentList?.text.orEmpty()

            return buildReplaceFix(
                name = "Replace with '$replacementName'",
                replacement = replacementName + typeArguments + remaining.joinToString(prefix = "(", postfix = ")"),
                range = location,
                reformat = false
            )
        }
    })

    private fun JavaContext.findManifestApplicationName(): String? {
        val manifest = project.mergedManifest?.documentElement ?: return null
        val application = XmlUtils.getFirstSubTagByName(manifest, TAG_APPLICATION) ?: return null

        return resolveManifestName(application, project).takeIf { it.isNotBlank() }
    }
}