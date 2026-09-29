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
 * This file is created by fankes on 2026/9/29.
 */
package com.highcapable.yukihookapi.core.lint.detector

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.Category
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.highcapable.yukihookapi.core.lint.detector.extension.enclosingFunction
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiLocalVariable
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiType
import com.intellij.psi.PsiWildcardType
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UThisExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor

class ClassicHookerDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val CLASSIC_HOOKER = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.core.YukiHookCreator.ClassicMemberHooker"

        val ISSUE = Issue.create(
            id = "ConflictingClassicHookCallbacks",
            briefDescription = "Conflicting classic hook callbacks",
            explanation = "A classic hooker may use before and after together, but intercept replaces their callbacks. " +
                "Do not configure them with intercept on the same hooker.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = Implementation(ClassicHookerDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }

    private enum class Callback { BEFORE, AFTER, INTERCEPT }

    private data class HookerGroup(val owner: PsiElement?, val declaration: PsiElement)

    private data class CallbackCall(
        val node: UCallExpression,
        val callback: Callback,
        val branches: Map<PsiElement, PsiElement>
    ) {

        fun conflictsWith(other: CallbackCall) = (callback == Callback.INTERCEPT) != (other.callback == Callback.INTERCEPT) &&
            branches.none { (condition, branch) -> other.branches[condition]?.let { it != branch } == true }
    }

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitFile(node: UFile) {
            val callbacks = mutableMapOf<HookerGroup, MutableList<CallbackCall>>()

            node.accept(object : AbstractUastVisitor() {

                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val method = node.resolve() ?: return false
                    if (method.containingClass?.qualifiedName != CLASSIC_HOOKER) return false

                    val callback = when (method.name) {
                        "before" -> Callback.BEFORE
                        "after" -> Callback.AFTER
                        "intercept", "replaceAny", "replaceUnit",
                        "replaceTo", "replaceToTrue", "replaceToFalse" -> Callback.INTERCEPT
                        else -> return false
                    }
                    val group = node.hookerGroup() ?: return false
                    callbacks.getOrPut(group) { mutableListOf() }.add(CallbackCall(node, callback, node.branches()))

                    return false
                }
            })

            callbacks.values.forEach { group ->
                val previous = mutableListOf<CallbackCall>()

                group.sortedBy { it.node.sourcePsi?.textRange?.startOffset ?: Int.MAX_VALUE }.forEach { call ->
                    if (previous.any(call::conflictsWith)) {
                        val interceptions = if (call.callback == Callback.INTERCEPT) listOf(call)
                        else group.filter(call::conflictsWith)
                        val callbacks = listOf(Callback.BEFORE, Callback.AFTER).filter { callback ->
                            group.any { it.callback == callback && interceptions.any(it::conflictsWith) }
                        }.joinToString(" and ") { "`${it.name.lowercase()}`" }
                        val expression = generateSequence(call.node as UElement) { it.uastParent as? UQualifiedReferenceExpression }.last()
                        val location = context.getLocation(expression)
                        val parent = expression.uastParent
                        val isStandalone = parent is UBlockExpression || (parent is UReturnExpression && 
                            parent.sourcePsi == null && parent.uastParent is UBlockExpression)

                        context.report(
                            issue = ISSUE,
                            scope = call.node,
                            location = location,
                            message = "`intercept` cannot be used with $callbacks on the same classic hooker",
                            quickfixData = if (isStandalone)
                                buildReplaceFix("Delete Call Expression", "", range = location, reformat = false)
                            else null
                        )
                    }

                    previous.add(call)
                }
            }
        }

        private fun UCallExpression.hookerGroup(): HookerGroup? {
            val receiver = receiver
            if (receiver != null && receiver !is UThisExpression) {
                val property = receiver.sourcePsi?.references?.firstNotNullOfOrNull { it.resolve() as? KtProperty }
                if (property != null) return HookerGroup(enclosingFunction(), property).takeIf { property.isLocal && !property.isVar }

                val target = (receiver as? USimpleNameReferenceExpression)?.resolve() ?: return null
                if (target !is PsiLocalVariable && target !is PsiParameter) return null
                if (target is PsiLocalVariable && !target.hasModifierProperty(PsiModifier.FINAL)) return null
                return HookerGroup(enclosingFunction(), target.navigationElement)
            }

            val lambdas = generateSequence(uastParent) { it.uastParent }.filterIsInstance<ULambdaExpression>().toList()
            val scope = lambdas.firstOrNull { it.isClassicScope() }
                ?: lambdas.firstOrNull { it.enclosingCall()?.resolve()?.containingClass?.qualifiedName != CLASSIC_HOOKER }
            return scope?.sourcePsi?.let { HookerGroup(null, it) }
        }

        private fun ULambdaExpression.enclosingCall() = generateSequence(uastParent) { it.uastParent }
            .filterIsInstance<UCallExpression>().firstOrNull()

        private fun ULambdaExpression.isClassicScope(): Boolean {
            val call = enclosingCall() ?: return false
            if (call.resolve()?.containingClass?.qualifiedName == CLASSIC_HOOKER) return false
            return call.resolve()?.parameterList?.parameters?.any { it.type.containsClassicHooker() } == true ||
                call.receiver?.getExpressionType().containsClassicHooker()
        }

        private fun PsiType?.containsClassicHooker(): Boolean = when (this) {
            is PsiArrayType -> componentType.containsClassicHooker()
            is PsiClassType -> resolve()?.qualifiedName == CLASSIC_HOOKER || parameters.any { it.containsClassicHooker() }
            is PsiWildcardType -> bound.containsClassicHooker()
            else -> false
        }

        private fun UCallExpression.branches(): Map<PsiElement, PsiElement> {
            val source = sourcePsi ?: return emptyMap()
            val branches = mutableMapOf<PsiElement, PsiElement>()

            generateSequence(source.parent) { it.parent }.forEach { parent ->
                when (parent) {
                    is KtIfExpression -> listOfNotNull(parent.then, parent.`else`)
                        .firstOrNull { it.textRange.contains(source.textRange) }
                        ?.let { branches[parent] = it }
                    is PsiIfStatement -> listOfNotNull(parent.thenBranch, parent.elseBranch)
                        .firstOrNull { it.textRange.contains(source.textRange) }
                        ?.let { branches[parent] = it }
                    is KtWhenEntry -> (parent.parent as? KtWhenExpression)?.let { branches[it] = parent }
                }
            }

            return branches
        }
    })
}