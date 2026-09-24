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
import com.highcapable.yukihookapi.core.lint.detector.extension.toUFile
import com.highcapable.yukihookapi.generated.YukiHookProperties
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiField
import com.intellij.psi.PsiMember
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifierListOwner
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UTypeReferenceExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor

class RuntimeRequirementDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val REQUIRES_RUNTIME_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.internal.RequiresRuntime"
        private const val LIBXPOSED_ENTRY_ANNOTATION_NAME = "YukiHookLibXposedEntry"
        private const val LIBXPOSED_ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.$LIBXPOSED_ENTRY_ANNOTATION_NAME"

        private const val MIN_API_VERSION = "minApiVersion"
        private const val DEFAULT_LIBXPOSED_MIN_API_VERSION = 101

        private const val LIBXPOSED_RUNTIME = "LIBXPOSED"
        private const val XPOSED_82_RUNTIME = "XPOSED_82"

        private val RUNTIME_CLASSES = mapOf(
            LIBXPOSED_RUNTIME to "${DeclaredSymbol.CORE_PACKAGE_NAME}.runtime.libxposed.entry.LibXposedEntry",
            XPOSED_82_RUNTIME to "${DeclaredSymbol.CORE_PACKAGE_NAME}.runtime.xposed82.entry.Xposed82Entry"
        )

        private val RUNTIME_MODULES = mapOf(
            LIBXPOSED_RUNTIME to YukiHookProperties.PROJECT_YUKIHOOK_RUNTIME_LIBXPOSED_MODULE_NAME,
            XPOSED_82_RUNTIME to YukiHookProperties.PROJECT_YUKIHOOK_RUNTIME_XPOSED82_MODULE_NAME
        )

        val ISSUE = Issue.create(
            id = "RequiresYukiHookRuntime",
            briefDescription = "Incompatible YukiHook runtime or Xposed API version.",
            explanation = "YukiHook APIs require a compatible runtime. If an API specifies a minimum Xposed API version, " +
                "the module entry must declare that version or higher.",
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
        UAnnotation::class.java,
        UCallExpression::class.java,
        USimpleNameReferenceExpression::class.java,
        UTypeReferenceExpression::class.java
    )

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        private val libXposedMinApiVersion by lazy { context.findLibXposedMinApiVersion() }

        override fun visitAnnotation(node: UAnnotation) {
            val declaration = node.resolve() ?: return
            reportRequirement(
                scope = node,
                declaration = declaration,
                apiName = declaration.name ?: node.qualifiedName ?: node.asSourceString(),
                location = context.getNameLocation(node)
            )
        }

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
            val psiFacade = JavaPsiFacade.getInstance(source.project)
            if (psiFacade.findClass(runtimeClass, source.resolveScope) == null) {
                val runtimeModule = RUNTIME_MODULES.getValue(runtime)
                context.report(
                    issue = ISSUE,
                    scope = scope,
                    location = location,
                    message = "`$apiName` requires the `$runtimeModule` runtime."
                )
                return
            }

            if (runtime != LIBXPOSED_RUNTIME) return
            val versionValue = annotation.javaPsi?.findAttributeValue(MIN_API_VERSION) ?: return
            val requiredVersion = psiFacade.constantEvaluationHelper.computeConstantExpression(versionValue) as? Int ?: return
            if (requiredVersion <= 0) return
            val declaredVersion = libXposedMinApiVersion ?: return
            if (declaredVersion >= requiredVersion) return

            context.report(
                issue = ISSUE,
                scope = scope,
                location = location,
                message = "`$apiName` requires Xposed API $requiredVersion, but " +
                    "`@$LIBXPOSED_ENTRY_ANNOTATION_NAME` specifies `$MIN_API_VERSION = $declaredVersion`."
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

        private fun JavaContext.findLibXposedMinApiVersion() = project.javaSourceFolders.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
            .filter { client.readFile(it).contains(LIBXPOSED_ENTRY_ANNOTATION_NAME) }
            .mapNotNull { toUFile(it)?.libXposedMinApiVersion() }
            .singleOrNull()

        private fun UFile.libXposedMinApiVersion(): Int? {
            var version: Int? = null
            accept(object : AbstractUastVisitor() {
                override fun visitAnnotation(node: UAnnotation): Boolean {
                    if (node.qualifiedName != LIBXPOSED_ENTRY_ANNOTATION) return false
                    version = (node.findAttributeValue(MIN_API_VERSION)?.evaluate() as? Int)
                        ?: DEFAULT_LIBXPOSED_MIN_API_VERSION
                    return true
                }
            })
            return version
        }
    })
}