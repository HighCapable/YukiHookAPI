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
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.annotationNameLocation
import com.highcapable.yukihookapi.core.lint.detector.extension.argumentSource
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.highcapable.yukihookapi.core.lint.detector.extension.deletionLocation
import com.highcapable.yukihookapi.core.lint.detector.extension.isEmptyArray
import com.highcapable.yukihookapi.core.lint.detector.extension.toUFile
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiEnumConstant
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.io.File

class LibXposedEntryDetector : Detector(), Detector.UastScanner {

    companion object {

        const val ENTRY_ANNOTATION_NAME = "YukiHookLibXposedEntry"
        const val ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.$ENTRY_ANNOTATION_NAME"

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

    class VersionDetector(private val context: JavaContext) {

        data class Entry(
            val annotation: UAnnotation,
            val sourceFile: File,
            val minApiVersion: Int
        )

        fun findLibXposedEntry() = context.project.javaSourceFolders.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
            .filter { context.client.readFile(it).contains(ENTRY_ANNOTATION_NAME) }
            .mapNotNull { sourceFile -> context.toUFile(sourceFile)?.libXposedEntry(sourceFile) }
            .singleOrNull()

        fun buildFix(
            entry: Entry,
            requiredVersion: Int = entry.minApiVersion,
            outside: Boolean = false
        ) = buildFix(entry.annotation, requiredVersion, entry.sourceFile, outside)

        fun buildFix(
            annotation: UAnnotation,
            requiredVersion: Int,
            sourceFile: File = context.file,
            outside: Boolean = false
        ): LintFix? {
            val contents = if (sourceFile == context.file) context.getContents() else context.client.readFile(sourceFile)
            val declared = annotation.findDeclaredAttributeValue(MIN_API_VERSION)?.sourcePsi
            if (declared != null) return buildReplaceFix(
                name = if (outside) "Change $MIN_API_VERSION to $requiredVersion" else "Change to $requiredVersion",
                replacement = requiredVersion.toString(),
                range = Location.create(sourceFile, contents, declared.textRange.startOffset, declared.textRange.endOffset),
                reformat = false
            )

            val source = annotation.sourcePsi ?: return null
            val argument = "$MIN_API_VERSION = $requiredVersion"
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
                else -> return null
            }

            return buildReplaceFix(
                name = "Add $argument${if (outside) " to @$ENTRY_ANNOTATION_NAME" else ""}",
                replacement = insertion,
                range = Location.create(sourceFile, contents, offset, offset)
            )
        }

        private fun UFile.libXposedEntry(sourceFile: File): Entry? {
            var entry: Entry? = null
            accept(object : AbstractUastVisitor() {
                override fun visitAnnotation(node: UAnnotation): Boolean {
                    if (node.qualifiedName != ENTRY_ANNOTATION) return false
                    val minApiVersion = (node.findAttributeValue(MIN_API_VERSION)?.evaluate() as? Int)
                        ?: LOWEST_API_VERSION
                    entry = Entry(node, sourceFile, minApiVersion)
                    return true
                }
            })
            return entry
        }
    }

    override fun getApplicableUastTypes() = listOf(UAnnotation::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitAnnotation(node: UAnnotation) {
            if (node.qualifiedName != ENTRY_ANNOTATION) return

            val versionDetector = VersionDetector(context)

            val declaredMin = node.findDeclaredAttributeValue(MIN_API_VERSION)
            val minVersion = node.findAttributeValue(MIN_API_VERSION)?.evaluate() as? Int
                ?: if (declaredMin == null) LOWEST_API_VERSION else return

            val reload = node.findAttributeValue(HOT_RELOAD) as? UResolvable
            val policy = reload?.resolve() as? PsiEnumConstant
            val usesReload = policy?.containingClass?.qualifiedName == HOT_RELOAD_ENUM && policy.name in setOf("MANUAL", "AUTO")
            val requiredVersion = if (usesReload) RELOAD_API_VERSION else LOWEST_API_VERSION

            if (minVersion < requiredVersion) {
                val location = if (declaredMin != null) context.getLocation(declaredMin) else context.annotationNameLocation(node)
                context.report(
                    issue = ISSUE,
                    scope = node,
                    location = location,
                    message = if (usesReload) "Hot reload requires `$MIN_API_VERSION` to be at least $RELOAD_API_VERSION"
                    else "`$MIN_API_VERSION` must be at least $LOWEST_API_VERSION",
                    quickfixData = versionDetector.buildFix(node, requiredVersion)
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
                        range = context.deletionLocation(source),
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