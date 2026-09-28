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
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiArrayInitializerMemberValue
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiEnumConstant
import com.intellij.psi.PsiNameValuePair
import com.intellij.psi.PsiWhiteSpace
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.kotlin.psi.KtCollectionLiteralExpression
import org.jetbrains.kotlin.psi.KtUserType
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UNamedExpression
import org.jetbrains.uast.UResolvable

class LibXposedEntryDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.YukiHookLibXposedEntry"
        private const val HOT_RELOAD_ENUM = "$ENTRY_ANNOTATION.HotReload"

        private const val MIN_API_VERSION = "minApiVersion"
        private const val TARGET_API_VERSION = "targetApiVersion"
        private const val HOT_RELOAD = "hotReload"

        private const val JAVA_ENTRIES = "javaEntries"
        private const val LOWEST_API_VERSION = 101
        private const val RELOAD_API_VERSION = 102

        private val IMPLEMENTATION = Implementation(
            LibXposedEntryDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE = Issue.create(
            id = "InvalidLibXposedApiVersion",
            briefDescription = "Invalid Xposed API version for module entry",
            explanation = "The minimum Xposed API version must be at least 101, and the target API version " +
                "must not be lower than the minimum version. Hot reload requires a minimum API version of 102.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )

        val HOT_RELOAD_ISSUE = Issue.create(
            id = "InvalidLibXposedHotReload",
            briefDescription = "Invalid hot reload configuration for module entry",
            explanation = "Hot reload replaces the YukiHook module entry as one generation. Additional libxposed " +
                "Java entries cannot participate in that lifecycle and must not be declared when hot reload is enabled.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes() = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitAnnotation(node: UAnnotation) {
            if (node.qualifiedName != ENTRY_ANNOTATION) return

            val declaredMin = node.findDeclaredAttributeValue(MIN_API_VERSION)
            val minVersion = node.findAttributeValue(MIN_API_VERSION)?.evaluate() as? Int
                ?: if (declaredMin == null) LOWEST_API_VERSION else return
            val reload = node.findAttributeValue(HOT_RELOAD) as? UResolvable
            val policy = reload?.resolve() as? PsiEnumConstant
            val usesReload = policy?.containingClass?.qualifiedName == HOT_RELOAD_ENUM && policy.name in setOf("MANUAL", "AUTO")
            val requiredVersion = if (usesReload) RELOAD_API_VERSION else LOWEST_API_VERSION
            if (minVersion < requiredVersion) {
                val location = if (declaredMin != null) context.getLocation(declaredMin) else annotationNameLocation(context, node)
                context.report(
                    issue = ISSUE,
                    scope = node,
                    location = location,
                    message = if (usesReload) "Hot reload requires `$MIN_API_VERSION` to be at least $RELOAD_API_VERSION"
                    else "`$MIN_API_VERSION` must be at least $LOWEST_API_VERSION",
                    quickfixData = if (declaredMin == null) addMinVersionFix(context, node, requiredVersion) else buildReplaceFix(
                        name = "Change to $requiredVersion",
                        replacement = "$requiredVersion",
                        range = location,
                        reformat = false
                    )
                )
            }

            val javaEntries = node.attributeValues.firstOrNull { it.name == JAVA_ENTRIES }
            if (usesReload && javaEntries != null && !javaEntries.isEmptyArray()) {
                val source = javaEntries.argumentSource() ?: javaEntries.expression.sourcePsi ?: return
                context.report(
                    issue = HOT_RELOAD_ISSUE,
                    scope = javaEntries,
                    location = context.getLocation(source),
                    message = "`$JAVA_ENTRIES` must be empty when hot reload is enabled",
                    quickfixData = buildReplaceFix(
                        name = "Delete Call Expression",
                        replacement = "",
                        range = deletionLocation(context, source),
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

        private fun annotationNameLocation(context: JavaContext, node: UAnnotation): Location {
            // UAST's annotation name location can cover the full Kotlin annotation, including arguments.
            val name = when (val source = node.sourcePsi) {
                is KtAnnotationEntry -> (source.typeReference?.typeElement as? KtUserType)?.referenceExpression
                is PsiAnnotation -> source.nameReferenceElement?.referenceNameElement
                else -> null
            }
            return name?.let(context::getLocation) ?: context.getNameLocation(node)
        }

        private fun UNamedExpression.isEmptyArray() = when (val source = expression.sourcePsi) {
            is KtCollectionLiteralExpression -> source.innerExpressions.isEmpty()
            is PsiArrayInitializerMemberValue -> source.initializers.isEmpty()
            else -> false
        }

        private fun UNamedExpression.argumentSource() = generateSequence(sourcePsi ?: expression.sourcePsi) { it.parent }
            .firstOrNull { it is KtValueArgument || it is PsiNameValuePair }

        private fun deletionLocation(context: JavaContext, source: PsiElement): Location {
            val nextComma = generateSequence(source.nextSibling) { it.nextSibling }
                .firstOrNull { it !is PsiWhiteSpace }
                ?.takeIf { it.text == "," }
            if (nextComma != null)
                return Location.create(context.file, context.getContents(), source.textRange.startOffset, nextComma.textRange.endOffset)

            val previousComma = generateSequence(source.prevSibling) { it.prevSibling }
                .firstOrNull { it !is PsiWhiteSpace }
                ?.takeIf { it.text == "," }
            val start = previousComma?.textRange?.startOffset ?: source.textRange.startOffset
            return Location.create(context.file, context.getContents(), start, source.textRange.endOffset)
        }

        private fun addMinVersionFix(context: JavaContext, node: UAnnotation, version: Int) = node.sourcePsi?.let { source ->
            val argument = "$MIN_API_VERSION = $version"
            val (offset, insertion) = when (source) {
                is KtAnnotationEntry -> {
                    val arguments = source.valueArgumentList
                    val last = arguments?.arguments?.lastOrNull()
                    when {
                        last != null -> last.textRange.endOffset to ", $argument"
                        arguments?.rightParenthesis != null -> arguments.rightParenthesis!!.textRange.startOffset to argument
                        else -> source.textRange.endOffset to "($argument)"
                    }
                }
                is PsiAnnotation -> {
                    val arguments = source.parameterList
                    val last = arguments.attributes.lastOrNull()
                    when {
                        last != null -> last.textRange.endOffset to ", $argument"
                        arguments.text.startsWith("(") -> arguments.textRange.startOffset + 1 to argument
                        else -> source.textRange.endOffset to "($argument)"
                    }
                }
                else -> return@let null
            }

            buildReplaceFix(
                name = "Add $argument",
                replacement = insertion,
                range = Location.create(context.file, context.getContents(), offset, offset)
            )
        }
    })
}