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
 * This file is created by fankes on 2026/9/21.
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
import com.highcapable.yukihookapi.core.lint.detector.extension.toUFile
import com.intellij.psi.PsiMethod
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.visitor.AbstractUastVisitor

class ModuleScopeDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val LIBXPOSED_ENTRY_ANNOTATION_NAME = "YukiHookLibXposedEntry"
        private const val LIBXPOSED_ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.$LIBXPOSED_ENTRY_ANNOTATION_NAME"
        private const val MODULE_SCOPE_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.scope.YukiHookModuleScope"

        private const val LIBXPOSED_ENTRY_STATIC_SCOPE_ATTRIBUTE = "staticScope"

        private val SCOPE_MUTATION_METHODS = setOf("request", "remove")

        val ISSUE = Issue.create(
            id = "ModuleScopeMutationWithStaticScope",
            briefDescription = "Module scope cannot be changed with static scope.",
            explanation = "@YukiHookLibXposedEntry with `staticScope = true` has a fixed scope and cannot request " +
                "or remove packages through YukiHookAPI module scope APIs.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(
                ModuleScopeDetector::class.java,
                Scope.JAVA_FILE_SCOPE
            )
        )
    }

    override fun getApplicableUastTypes(): List<Class<out UElement>> = listOf(
        UCallExpression::class.java,
        UCallableReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        private val hasStaticScope by lazy { context.hasStaticScopeEntry() }

        override fun visitCallExpression(node: UCallExpression) {
            val methodName = node.methodName ?: return
            if (methodName !in SCOPE_MUTATION_METHODS) return
            val method = node.resolve() ?: return
            if (!method.isScopeMutation()) return
            if (!hasStaticScope) return

            report(
                scope = node,
                apiName = methodName,
                location = context.getCallLocation(node, includeReceiver = false, includeArguments = true)
            )
        }

        override fun visitCallableReferenceExpression(node: UCallableReferenceExpression) {
            if (node.callableName !in SCOPE_MUTATION_METHODS) return
            val method = node.resolve() as? PsiMethod ?: return
            if (!method.isScopeMutation()) return
            if (!hasStaticScope) return

            report(node, node.callableName, context.getLocation(node))
        }

        private fun report(scope: UElement, apiName: String, location: Location) = context.report(
            issue = ISSUE,
            scope = scope,
            location = location,
            message = "`$apiName` cannot be used when `${LIBXPOSED_ENTRY_STATIC_SCOPE_ATTRIBUTE}` is true. " +
                "Set `${LIBXPOSED_ENTRY_STATIC_SCOPE_ATTRIBUTE} = false` on `YukiHookLibXposedEntry` or remove this usage."
        )

        private fun PsiMethod.isScopeMutation() = name in SCOPE_MUTATION_METHODS && containingClass?.qualifiedName == MODULE_SCOPE_CLASS
    })

    private fun JavaContext.hasStaticScopeEntry() = project.javaSourceFolders.asSequence()
        .flatMap { it.walkTopDown() }
        .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
        .filter { client.readFile(it).contains(LIBXPOSED_ENTRY_ANNOTATION_NAME) }
        .any { toUFile(it)?.hasStaticScopeEntry() == true }

    private fun UFile.hasStaticScopeEntry(): Boolean {
        var result = false
        accept(object : AbstractUastVisitor() {
            override fun visitAnnotation(node: UAnnotation): Boolean {
                if (node.qualifiedName != LIBXPOSED_ENTRY_ANNOTATION) return false
                if (node.findAttributeValue(LIBXPOSED_ENTRY_STATIC_SCOPE_ATTRIBUTE)?.evaluate() == true) result = true
                return result
            }
        })
        return result
    }
}