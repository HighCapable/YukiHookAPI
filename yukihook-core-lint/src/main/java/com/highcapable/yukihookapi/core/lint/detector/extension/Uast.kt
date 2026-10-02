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
package com.highcapable.yukihookapi.core.lint.detector.extension

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.JavaContext
import com.intellij.openapi.diagnostic.ControlFlowException
import com.intellij.psi.PsiAnonymousClass
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiIfStatement
import com.intellij.psi.PsiMethod
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtElement
import org.jetbrains.kotlin.psi.KtIfExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtWhenEntry
import org.jetbrains.kotlin.psi.KtWhenExpression
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UastCallKind
import java.io.File
import java.util.concurrent.CancellationException

private const val YUKIHOOK_MODULE_NAME_PREFIX = "yukihook-"

internal fun JavaContext.createExternalOnlyUastHandler(handler: UElementHandler) = handler.takeUnless {
    project.name.split('.', ':').any { it.startsWith(YUKIHOOK_MODULE_NAME_PREFIX) }
}

internal fun USimpleNameReferenceExpression.reportNode(): UElement {
    val parent = uastParent
    val grandparent = parent?.uastParent

    return when {
        parent is UQualifiedReferenceExpression && parent.selector == this -> when (grandparent) {
            is UBinaryExpression -> grandparent
            else -> parent.outermostQualifiedReference()
        }
        parent is UQualifiedReferenceExpression && parent.receiver == this -> parent.outermostQualifiedReference()
        parent is UBinaryExpression -> parent
        grandparent is UBinaryExpression -> grandparent
        else -> this
    }
}

internal fun UElement.outermostQualifiedReference(): UElement {
    var current = this
    while (true) {
        val next = current.uastParent as? UQualifiedReferenceExpression ?: return current
        if (next.receiver != current) return current
        current = next
    }
}

internal fun USimpleNameReferenceExpression.isConstructorReference(): Boolean {
    var parent = uastParent
    while (parent is UQualifiedReferenceExpression) parent = parent.uastParent
    return (parent as? UCallExpression)?.kind == UastCallKind.CONSTRUCTOR_CALL
}

internal fun UCallExpression.enclosingFunction() =
    generateSequence(sourcePsi) { it.parent }.filterIsInstance<KtNamedFunction>().firstOrNull()
        ?: generateSequence(uastParent) { it.uastParent }.filterIsInstance<UMethod>().firstOrNull()
            ?.let { it.sourcePsi ?: it.javaPsi.navigationElement }

internal fun UCallExpression.functionTarget(context: JavaContext): PsiElement? {
    // UAST may resolve a local Kotlin function to a synthetic method instead of its declaration.
    return (sourcePsi as? KtCallExpression)?.calleeExpression?.references
        ?.firstNotNullOfOrNull { it.resolve().localFunction(context) }
        ?: resolve()?.navigationElement.localFunction(context)
}

internal fun UCallableReferenceExpression.functionTarget(context: JavaContext) =
    sourcePsi?.references?.firstNotNullOfOrNull { it.resolve().localFunction(context) }
        ?: resolve()?.navigationElement.localFunction(context)

internal fun PsiElement?.localFunction(context: JavaContext): PsiElement? {
    if (this !is KtNamedFunction && this !is PsiMethod) return null
    return takeIf { it.containingFile?.virtualFile?.path == context.file.path }
}

internal fun JavaContext.toUFile(source: File): UFile? {
    if (source == file) return uastFile
    val sourceContext = JavaContext(driver, project, null, source).also {
        it.uastParser = uastParser
        it.sourceSetType = sourceSetType
    }

    return uastParser.parse(sourceContext)
}

internal fun UCallExpression.branches(): Map<PsiElement, PsiElement> {
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

internal fun PsiClass.displayName() = name ?: (this as? PsiAnonymousClass)?.baseClassType?.className?.let { "anonymous $it" } ?: "anonymous"

internal fun <R> KtElement.analyzeOrNull(block: KaSession.() -> R): R? = try {
    analyze(this, block)
} catch (failure: Throwable) {
    if (failure is ControlFlowException || failure is CancellationException) throw failure
    null
}