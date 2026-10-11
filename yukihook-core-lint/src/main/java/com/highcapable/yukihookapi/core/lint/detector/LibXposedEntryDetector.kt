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
import com.highcapable.yukihookapi.core.lint.detector.extension.isStringLiteral
import com.highcapable.yukihookapi.core.lint.detector.extension.toUFile
import com.intellij.psi.PsiAnnotation
import com.intellij.psi.PsiEnumConstant
import org.jetbrains.kotlin.psi.KtAnnotationEntry
import org.jetbrains.uast.UAnnotation
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UNamedExpression
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.io.File

class LibXposedEntryDetector : Detector(), Detector.UastScanner {

    companion object {

        const val ENTRY_ANNOTATION_NAME = "YukiHookLibXposedEntry"
        const val ENTRY_ANNOTATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.annotation.xposed.$ENTRY_ANNOTATION_NAME"

        const val MIN_API_VERSION = "minApiVersion"
        const val TARGET_API_VERSION = "targetApiVersion"

        private const val HOT_RELOAD_ENUM = "$ENTRY_ANNOTATION.HotReload"
        private const val HOT_RELOAD_NONE = "NONE"

        private const val HOT_RELOAD = "hotReload"
        private const val JAVA_ENTRIES = "javaEntries"
        private const val SCOPE = "scope"

        private const val FRAMEWORK_PACKAGE_NAME = "android"
        private const val FRAMEWORK_SCOPE_NAME = "system"

        private const val LOWEST_API_VERSION = 101
        private const val RELOAD_API_VERSION = 102

        private val IMPLEMENTATION = Implementation(
            LibXposedEntryDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val ISSUE = Issue.create(
            id = "InvalidLibXposedApiVersion",
            briefDescription = "Invalid Xposed API version for module entry",
            explanation = "The minimum Xposed API version must be at least $LOWEST_API_VERSION, and the target API version " +
                "must not be lower than the minimum version. Hot reload requires a target API version of $RELOAD_API_VERSION.",
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

        val SYSTEM_FRAMEWORK_IN_SCOPE_ISSUE = Issue.create(
            id = "SystemFrameworkInScope",
            briefDescription = "Framework package used as the system framework scope",
            explanation = "In the module scope, the system server is the virtual package \"$FRAMEWORK_SCOPE_NAME\". " +
                "\"$FRAMEWORK_PACKAGE_NAME\" only selects the framework package outside the system server, " +
                "which YukiHook does not load as an app. Use \"$FRAMEWORK_SCOPE_NAME\" for the system framework.",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )
    }

    class VersionDetector(private val context: JavaContext) {

        data class Entry(
            val annotation: UAnnotation,
            val sourceFile: File,
            val minApiVersion: Int,
            val targetApiVersion: Int?
        )

        fun findLibXposedEntry() = context.project.javaSourceFolders.asSequence()
            .flatMap { it.walkTopDown() }
            .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
            .filter { context.client.readFile(it).contains(ENTRY_ANNOTATION_NAME) }
            .mapNotNull { sourceFile -> context.toUFile(sourceFile)?.libXposedEntry(sourceFile) }
            .singleOrNull()

        fun buildFix(
            entry: Entry,
            requiredVersion: Int,
            attribute: String = MIN_API_VERSION,
            outside: Boolean = false
        ) = buildFix(entry.annotation, requiredVersion, attribute, entry.sourceFile, outside)

        fun buildFix(
            annotation: UAnnotation,
            requiredVersion: Int,
            attribute: String = MIN_API_VERSION,
            sourceFile: File = context.file,
            outside: Boolean = false
        ): LintFix? {
            val contents = if (sourceFile == context.file) context.getContents() else context.client.readFile(sourceFile)
            val declared = annotation.findDeclaredAttributeValue(attribute)?.sourcePsi
            if (declared != null) return buildReplaceFix(
                name = if (outside) "Change $attribute to $requiredVersion" else "Change to $requiredVersion",
                replacement = requiredVersion.toString(),
                range = Location.create(sourceFile, contents, declared.textRange.startOffset, declared.textRange.endOffset),
                reformat = false
            )

            val source = annotation.sourcePsi ?: return null
            val argument = "$attribute = $requiredVersion"
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
                    val targetApiVersion = node.findAttributeValue(TARGET_API_VERSION)?.evaluate() as? Int
                    entry = Entry(node, sourceFile, minApiVersion, targetApiVersion)
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

            node.attributeValues.firstOrNull { it.name == SCOPE }?.let(::reportFrameworkScope)

            val versionDetector = VersionDetector(context)

            val declaredMin = node.findDeclaredAttributeValue(MIN_API_VERSION)
            val minVersion = node.findAttributeValue(MIN_API_VERSION)?.evaluate() as? Int
                ?: if (declaredMin == null) LOWEST_API_VERSION else return

            val reload = node.findAttributeValue(HOT_RELOAD) as? UResolvable
            val policy = reload?.resolve() as? PsiEnumConstant
            val usesReload = policy?.containingClass?.qualifiedName == HOT_RELOAD_ENUM && policy.name != HOT_RELOAD_NONE

            if (minVersion < LOWEST_API_VERSION) {
                val location = if (declaredMin != null) context.getLocation(declaredMin) else context.annotationNameLocation(node)
                context.report(
                    issue = ISSUE,
                    scope = node,
                    location = location,
                    message = "`$MIN_API_VERSION` must be at least $LOWEST_API_VERSION",
                    quickfixData = versionDetector.buildFix(node, LOWEST_API_VERSION)
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

            // Hot reload degrades to disabled on older frameworks, so only the target version has to support it.
            if (usesReload && targetVersion < RELOAD_API_VERSION) context.report(
                issue = ISSUE,
                scope = node,
                location = context.getLocation(targetValue),
                message = "Hot reload requires `$TARGET_API_VERSION` to be at least $RELOAD_API_VERSION",
                quickfixData = versionDetector.buildFix(node, RELOAD_API_VERSION, TARGET_API_VERSION)
            )
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

        private fun reportFrameworkScope(scope: UNamedExpression) {
            val expression = scope.expression
            // A Java annotation can declare a single array element without braces.
            val entries = (expression as? UCallExpression)?.valueArguments ?: listOf(expression)
            // Replacing every framework package would duplicate "system", only the first one is replaced if it is missing.
            var hasSystem = entries.any { it.evaluate() == FRAMEWORK_SCOPE_NAME }
            entries.filter { it.evaluate() == FRAMEWORK_PACKAGE_NAME }.forEach { entry ->
                val location = context.getLocation(entry)
                val isDuplicate = hasSystem
                if (entry.isStringLiteral()) hasSystem = true
                context.report(
                    issue = SYSTEM_FRAMEWORK_IN_SCOPE_ISSUE,
                    scope = entry,
                    location = location,
                    message = "\"$FRAMEWORK_PACKAGE_NAME\" is not the system framework in `$SCOPE`, use \"$FRAMEWORK_SCOPE_NAME\" instead",
                    // A referenced constant may be shared with other code, only a literal is changed.
                    quickfixData = when {
                        !entry.isStringLiteral() -> null
                        isDuplicate -> entry.sourcePsi?.let {
                            buildReplaceFix("Delete Call Expression", "", range = context.deletionLocation(it), reformat = false)
                        }
                        else -> buildReplaceFix(
                            name = "Replace with \"$FRAMEWORK_SCOPE_NAME\"",
                            replacement = "\"$FRAMEWORK_SCOPE_NAME\"",
                            range = location,
                            reformat = false
                        )
                    }
                )
            }
        }
    })
}