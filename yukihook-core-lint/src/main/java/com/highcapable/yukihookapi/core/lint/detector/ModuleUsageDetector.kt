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
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.intellij.psi.CommonClassNames
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.lexer.KtTokens
import org.jetbrains.kotlin.psi.KtBinaryExpressionWithTypeRHS
import org.jetbrains.kotlin.psi.KtBlockExpression
import org.jetbrains.kotlin.psi.KtCatchClause
import org.jetbrains.kotlin.psi.KtDestructuringDeclaration
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtForExpression
import org.jetbrains.kotlin.psi.KtFunction
import org.jetbrains.kotlin.psi.KtNullableType
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtPsiUtil
import org.jetbrains.kotlin.psi.KtQualifiedExpression
import org.jetbrains.uast.UBinaryExpressionWithType
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UResolvable
import org.jetbrains.uast.skipParenthesizedExprDown

class ModuleUsageDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val APPLICATION = "android.app.Application"
        private const val MODULE_APPLICATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.application.ModuleApplication"
        private const val HOOK_INVOCATION = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.param.HookInvocation"

        private const val INSTANCE_GETTER = "getInstance"
        private const val INSTANCE_OR_NULL_GETTER = "getInstanceOrNull"
        private const val MEMBER_GETTER = "getMember"

        private const val METHOD_CLASS = "java.lang.reflect.Method"
        private const val STAR_CONSTRUCTOR_TYPE = "java.lang.reflect.Constructor<?>"

        private val IMPLEMENTATION = Implementation(
            ModuleUsageDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val MODULE_APPLICATION_ISSUE = Issue.create(
            id = "UseModuleApplication",
            briefDescription = "Inherit ModuleApplication to enable full functionality",
            explanation = "YukiHook module applications should inherit `ModuleApplication` to " +
                "initialize module runtime services and enable full functionality.",
            category = Category.CORRECTNESS,
            priority = 5,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )

        val TYPED_INSTANCE_ISSUE = Issue.create(
            id = "UseTypedInstance",
            briefDescription = "Casting instance instead of using instance()",
            explanation = "`HookInvocation` provides `instance<T>()` to get the receiver instance as `T`, " +
                "casting `instance` or `instanceOrNull` to a non-null type with `as` is not needed.",
            category = Category.PRODUCTIVITY,
            priority = 3,
            severity = Severity.INFORMATIONAL,
            implementation = IMPLEMENTATION
        )

        val TYPED_INSTANCE_OR_NULL_ISSUE = Issue.create(
            id = "UseTypedInstanceOrNull",
            briefDescription = "Casting the instance instead of using instanceOrNull()",
            explanation = "`HookInvocation` provides `instanceOrNull<T>()` to get the receiver instance as `T`, " +
                "or null when the receiver is absent or has a different type. " +
                "`instance as? T` still throws for a static method, and casting `instanceOrNull` to a nullable type is not needed.",
            category = Category.PRODUCTIVITY,
            priority = 3,
            severity = Severity.INFORMATIONAL,
            implementation = IMPLEMENTATION
        )

        val METHOD_PROPERTY_ISSUE = Issue.create(
            id = "UseMethodProperty",
            briefDescription = "Casting member to Method instead of using method",
            explanation = "`HookInvocation` provides `method` to get the currently hooked method, " +
                "casting `member` to `Method` is not needed.",
            category = Category.PRODUCTIVITY,
            priority = 3,
            severity = Severity.INFORMATIONAL,
            implementation = IMPLEMENTATION
        )

        val CONSTRUCTOR_PROPERTY_ISSUE = Issue.create(
            id = "UseConstructorProperty",
            briefDescription = "Casting member to Constructor instead of using constructor",
            explanation = "`HookInvocation` provides `constructor` to get the currently hooked constructor, " +
                "casting `member` to `Constructor<*>` is not needed.",
            category = Category.PRODUCTIVITY,
            priority = 3,
            severity = Severity.INFORMATIONAL,
            implementation = IMPLEMENTATION
        )
    }

    override fun getApplicableUastTypes() = listOf(UClass::class.java, UBinaryExpressionWithType::class.java)

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

        override fun visitBinaryExpressionWithType(node: UBinaryExpressionWithType) {
            val source = node.sourcePsi as? KtBinaryExpressionWithTypeRHS ?: return
            val typeReference = source.right ?: return
            val getter = (node.operand.skipParenthesizedExprDown() as? UResolvable)?.resolve() as? PsiMethod ?: return
            if (getter.containingClass?.qualifiedName != HOOK_INVOCATION) return

            val isSafeCast = source.operationReference.getReferencedNameElementType() == KtTokens.AS_SAFE
            val castType = node.type.canonicalText
            val propertyName = getter.name.removePrefix("get").replaceFirstChar { it.lowercase() }

            // Casting the instance to Any is already redundant.
            if (getter.name != MEMBER_GETTER && castType == CommonClassNames.JAVA_LANG_OBJECT) return
            val (issue, replacementName) = when (getter.name) {
                INSTANCE_GETTER if !isSafeCast -> TYPED_INSTANCE_ISSUE to "instance"
                // A non-null cast already throws when the instance is absent, the same as instance<T>().
                INSTANCE_OR_NULL_GETTER if !isSafeCast && typeReference.typeElement !is KtNullableType -> TYPED_INSTANCE_ISSUE to "instance"
                INSTANCE_GETTER, INSTANCE_OR_NULL_GETTER -> TYPED_INSTANCE_OR_NULL_ISSUE to "instanceOrNull"
                MEMBER_GETTER if isSafeCast -> return
                MEMBER_GETTER if castType == METHOD_CLASS -> METHOD_PROPERTY_ISSUE to "method"
                MEMBER_GETTER if castType == STAR_CONSTRUCTOR_TYPE -> CONSTRUCTOR_PROPERTY_ISSUE to "constructor"
                else -> return
            }
            val replacement = if (getter.name == MEMBER_GETTER) replacementName else "$replacementName<${typeReference.text}>()"

            // Keep the explicit receiver, e.g. "param.instance as T" becomes "param.instance<T>()".
            val operand = KtPsiUtil.safeDeparenthesize(source.left)
            val receiverPrefix = (operand as? KtQualifiedExpression)?.selectorExpression?.let {
                operand.text.substring(0, it.textRange.startOffset - operand.textRange.startOffset)
            } ?: ""
            val fixedText = receiverPrefix + replacement
            // Without an explicit receiver, a local declaration with the new name would shadow the HookInvocation property.
            val isShadowed = receiverPrefix.isEmpty() && replacementName != propertyName && source.hasLocalDeclaration(replacementName)
            // The replacement is a postfix expression, so parentheses that only wrap the cast are no longer needed.
            val replaceTarget = generateSequence(source.parent) { it.parent }
                .takeWhile { it is KtParenthesizedExpression }
                .lastOrNull() ?: source

            context.report(
                issue = issue,
                scope = node as UElement,
                location = context.getLocation(source),
                message = "Can be replaced with `$fixedText`",
                quickfixData = if (isShadowed) null else buildReplaceFix(
                    name = "Replace with '$replacementName'",
                    replacement = fixedText,
                    range = context.getLocation(replaceTarget),
                    reformat = false
                )
            )
        }
    })

    private fun KtElement.hasLocalDeclaration(name: String) =
        generateSequence<PsiElement>(this) { it.parent }.zipWithNext().any { (child, parent) ->
            when (parent) {
                // A local declaration is only visible after itself, not in its own initializer.
                is KtBlockExpression -> parent.statements.takeWhile { it != child }.any { statement ->
                    (statement is KtProperty && statement.name == name) ||
                        (statement is KtDestructuringDeclaration && statement.entries.any { it.name == name })
                }
                is KtFunction -> parent.valueParameters.any { it.name == name }
                is KtForExpression -> parent.loopParameter?.name == name ||
                    parent.destructuringDeclaration?.entries?.any { it.name == name } == true
                is KtCatchClause -> parent.catchParameter?.name == name
                else -> false
            }
        }

    private fun JavaContext.findManifestApplicationName(): String? {
        val manifest = project.mergedManifest?.documentElement ?: return null
        val application = XmlUtils.getFirstSubTagByName(manifest, TAG_APPLICATION) ?: return null

        return resolveManifestName(application, project).takeIf { it.isNotBlank() }
    }
}