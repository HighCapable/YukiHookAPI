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
 * This file is created by fankes on 2026/9/16.
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
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.highcapable.yukihookapi.core.lint.detector.extension.isConstructorReference
import com.highcapable.yukihookapi.core.lint.detector.extension.reportNode
import com.highcapable.yukihookapi.generated.YukiHookAPIProperties
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UTypeReferenceExpression

class RuntimeRequirementDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val REQUIRES_RUNTIME_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.internal.RequiresRuntime"

        private const val LIBXPOSED_RUNTIME = "LIBXPOSED"
        private const val XPOSED_82_RUNTIME = "XPOSED_82"

        private val RUNTIME_CLASSES = mapOf(
            LIBXPOSED_RUNTIME to "${DeclaredSymbol.CORE_PACKAGE_NAME}.runtime.libxposed.LibXposedEntry",
            XPOSED_82_RUNTIME to "${DeclaredSymbol.CORE_PACKAGE_NAME}.runtime.xposed82.Xposed82Entry"
        )
        private val RUNTIME_MODULES = mapOf(
            LIBXPOSED_RUNTIME to YukiHookAPIProperties.PROJECT_YUKIHOOKAPI_RUNTIME_LIBXPOSED_MODULE_NAME,
            XPOSED_82_RUNTIME to YukiHookAPIProperties.PROJECT_YUKIHOOKAPI_RUNTIME_XPOSED82_MODULE_NAME
        )

        val ISSUE = Issue.create(
            id = "RequiresYukiHookRuntime",
            briefDescription = "Requires the matching YukiHookAPI runtime.",
            explanation = "Runtime-specific YukiHookAPI declarations must only be used from modules using their required runtime.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                RuntimeRequirementDetector::class.java,
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
            reportRequirement(
                scope = node,
                declaration = method,
                apiName = if (method.isConstructor) method.containingClass?.name ?: method.name
                else node.methodName ?: method.name,
                location = context.getCallLocation(node, includeReceiver = false, includeArguments = true)
            )
        }

        override fun visitSimpleNameReferenceExpression(node: USimpleNameReferenceExpression) {
            val declaration = node.resolve() as? PsiModifierListOwner ?: return
            if (declaration is PsiClass && node.isConstructorReference()) return
            reportRequirement(
                scope = node,
                declaration = declaration,
                apiName = node.identifier,
                location = context.getLocation(node.reportNode())
            )
        }

        override fun visitTypeReferenceExpression(node: UTypeReferenceExpression) {
            val declaration = context.evaluator.getTypeClass(node.type) ?: return
            reportRequirement(
                scope = node,
                declaration = declaration,
                apiName = declaration.name ?: node.asSourceString(),
                location = context.getLocation(node)
            )
        }

        private fun reportRequirement(scope: UElement, declaration: PsiModifierListOwner, apiName: String, location: Location) {
            val annotation = declaration.findRuntimeAnnotation()
                ?: (declaration as? PsiMember)?.containingClass?.let {
                    context.evaluator.getAnnotation(it, REQUIRES_RUNTIME_ANNOTATION)
                } ?: return
            val runtime = annotation.findAttributeValue("value")?.asSourceString()?.substringAfterLast('.') ?: return
            val runtimeClass = RUNTIME_CLASSES[runtime] ?: return
            val source = scope.sourcePsi ?: return
            if (JavaPsiFacade.getInstance(source.project).findClass(runtimeClass, source.resolveScope) != null) return

            val runtimeModule = RUNTIME_MODULES.getValue(runtime)
            context.report(
                issue = ISSUE,
                scope = scope,
                location = location,
                message = "`$apiName` requires the `$runtimeModule` runtime."
            )
        }

        private fun PsiModifierListOwner.findRuntimeAnnotation() =
            context.evaluator.getAnnotation(this, REQUIRES_RUNTIME_ANNOTATION)
                ?: (this as? PsiMethod)?.findPropertyField()?.let {
                    context.evaluator.getAnnotation(it, REQUIRES_RUNTIME_ANNOTATION)
                }

        private fun PsiMethod.findPropertyField(): PsiField? {
            val fields = containingClass?.fields ?: return null
            fields.firstOrNull { it.navigationElement == navigationElement }?.let { return it }

            val nameStem = when (parameterList.parametersCount) {
                0 if name.startsWith("get") -> name.removePrefix("get")
                0 if name.startsWith("is") -> return fields.firstOrNull { it.name == name }
                1 if name.startsWith("set") -> name.removePrefix("set")
                else -> return null
            }
            val propertyName = nameStem.replaceFirstChar { it.lowercase() }

            return fields.firstOrNull { it.name == propertyName || it.name == "is$nameStem" }
        }
    })
}