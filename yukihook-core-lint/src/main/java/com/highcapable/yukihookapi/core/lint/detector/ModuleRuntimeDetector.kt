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
import com.highcapable.yukihookapi.core.lint.detector.extension.functionTarget
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiType
import com.intellij.psi.PsiWildcardType
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.visitor.AbstractUastVisitor

class ModuleRuntimeDetector : Detector(), Detector.UastScanner {

    companion object {

        private const val CONTEXT_CLASS = "android.content.Context"
        private const val MODULE_ENTRY_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.YukiHookXposedModule"
        private const val PACKAGE_PARAM_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.param.PackageParam"
        private const val BASE_HOOKER_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.entity.YukiBaseHooker"
        private const val YUKIHOOK_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.YukiHook"
        private const val YUKIHOOKAPI_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.YukiHookAPI"
        private const val YUKIHOOK_FACTORY_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.factory.YukiHookFactoryKt"
        private const val MODULE_LIFECYCLE_CLASS = "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.reload.lifecycle.ModuleLifecycleKt"

        private val EVENT_REGISTRATIONS = setOf(
            "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.bridge.event.YukiHookLibXposedEventKt",
            "${DeclaredSymbol.CORE_PACKAGE_NAME}.hook.xposed.bridge.event.YukiHookXposed82EventKt"
        )

        private val IMPLEMENTATION = Implementation(
            ModuleRuntimeDetector::class.java,
            Scope.JAVA_FILE_SCOPE
        )

        val REGISTRATION_ISSUE = Issue.create(
            id = "ModuleRegistrationOutsideOnInit",
            briefDescription = "Module registration must run during onInit",
            explanation = "Framework events and module lifecycle callbacks must be registered while the current module entry runs onInit.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )

        val HOOK_LOADING_ISSUE = Issue.create(
            id = "InvalidModuleHookLoading",
            briefDescription = "Invalid module hook loading",
            explanation = "Register package hook callbacks from the module's onHook. " +
                "Do not call encase from a PackageParam context or inside another encase block.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )
    }

    private enum class Phase { INIT, HOOK, PACKAGE, OUTSIDE, UNKNOWN }
    private enum class Execution { IMMEDIATE, PACKAGE, UNKNOWN }

    private data class CallSite(
        val node: UCallExpression,
        val method: PsiMethod?,
        val caller: PsiElement?,
        val target: PsiElement?,
        val execution: Execution,
        val entryOwned: Boolean,
        val insideEncase: Boolean
    ) {

        fun phases(functionPhases: Map<PsiElement, Set<Phase>>): Set<Phase> {
            val callers = caller?.let { functionPhases[it].orEmpty() } ?: setOf(Phase.OUTSIDE)
            return callers.mapTo(mutableSetOf()) { phase ->
                when (execution) {
                    Execution.IMMEDIATE -> phase
                    Execution.PACKAGE -> Phase.PACKAGE
                    Execution.UNKNOWN -> if (phase == Phase.PACKAGE || phase == Phase.OUTSIDE) phase else Phase.UNKNOWN
                }
            }
        }
    }

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        override fun visitFile(node: UFile) {
            val calls = mutableListOf<CallSite>()
            val roots = mutableMapOf<PsiElement, Phase>()
            val functions = mutableSetOf<PsiElement>()
            val references = mutableSetOf<PsiElement>()

            node.accept(object : AbstractUastVisitor() {

                override fun visitMethod(node: UMethod): Boolean {
                    val owner = node.javaPsi.containingClass ?: return false
                    val phase = when {
                        context.evaluator.extendsClass(owner, PACKAGE_PARAM_CLASS, false) -> Phase.PACKAGE
                        (node.sourcePsi as? KtNamedFunction)?.receiverTypeReference != null &&
                            node.javaPsi.parameterList.parameters.firstOrNull()?.type.isPackageParam() -> Phase.PACKAGE
                        node.javaPsi.parameterList.parametersCount == 0 &&
                            context.evaluator.extendsClass(owner, MODULE_ENTRY_CLASS, false) -> when (node.name) {
                            "onInit" -> Phase.INIT
                            "onHook" -> Phase.HOOK
                            else -> return false
                        }
                        else -> return false
                    }

                    val function = node.sourcePsi ?: node.javaPsi.navigationElement
                    roots[function] = phase
                    functions.add(function)

                    return false
                }

                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val caller = node.enclosingFunction()
                    val target = node.functionTarget(context)
                    if (caller != null) functions.add(caller)
                    if (target != null) functions.add(target)

                    calls.add(CallSite(
                        node = node,
                        method = node.resolve(),
                        caller = caller,
                        target = target,
                        execution = node.execution(caller),
                        entryOwned = node.isEntryOwned(),
                        insideEncase = node.insideEncase()
                    ))

                    return false
                }

                override fun visitCallableReferenceExpression(node: UCallableReferenceExpression): Boolean {
                    val target = node.functionTarget(context) ?: return false
                    functions.add(target)
                    references.add(target)

                    return false
                }
            })

            val phases = roots.mapValuesTo(mutableMapOf()) { mutableSetOf(it.value) }
            references.forEach { phases.getOrPut(it) { mutableSetOf() }.add(Phase.UNKNOWN) }

            // Resolve known entry paths before marking unreferenced functions as outside the lifecycle.
            fun propagate() {
                var changed = true
                while (changed) {
                    changed = false
                    calls.forEach { call ->
                        val target = call.target ?: return@forEach
                        val targetPhases = phases.getOrPut(target) { mutableSetOf() }
                        if (targetPhases.addAll(call.phases(phases))) changed = true
                    }
                }
            }

            propagate()
            functions.filter { phases[it].isNullOrEmpty() }.forEach {
                phases.getOrPut(it) { mutableSetOf() }.add(Phase.OUTSIDE)
            }
            propagate()

            val reachable = roots.keys.toMutableSet()

            var changed = true
            while (changed) {
                changed = false
                calls.forEach { call ->
                    val target = call.target ?: return@forEach
                    if (call.caller in reachable && reachable.add(target)) changed = true
                }
            }

            calls.forEach { call ->
                val method = call.method ?: return@forEach
                val callPhases = call.phases(phases)
                if (!method.isHookLoading() && !call.entryOwned && call.caller !in reachable &&
                    Phase.PACKAGE !in callPhases && !call.insideEncase) return@forEach

                val message = when {
                    method.isEncase() && call.insideEncase -> "Do not nest `encase` inside another `encase`"
                    method.isEncase() && Phase.PACKAGE in callPhases -> "Do not call `encase` from a `PackageParam` context"
                    method.isHookLoading() && Phase.INIT in callPhases -> "`encase` must be called from `onHook`"
                    method.isHookLoading() && Phase.OUTSIDE in callPhases -> "`encase` must be called from `onHook` of the current module entry"
                    method.isModuleRegistration() && callPhases.any { it == Phase.HOOK || it == Phase.PACKAGE || it == Phase.OUTSIDE } ->
                        "`${call.node.methodName}` must be called from `onInit` of the current module entry"
                    else -> return@forEach
                }

                val expression = generateSequence(call.node as UElement) { it.uastParent as? UQualifiedReferenceExpression }.last()
                val location = context.getLocation(expression)
                val parent = expression.uastParent

                // UAST can wrap the last statement in a Kotlin lambda in an implicit return.
                val isStandalone = parent is UBlockExpression || (parent is UReturnExpression &&
                    parent.sourcePsi == null && parent.uastParent is UBlockExpression)

                context.report(
                    issue = if (method.isModuleRegistration()) REGISTRATION_ISSUE else HOOK_LOADING_ISSUE,
                    scope = call.node,
                    location = location,
                    message = message,
                    quickfixData = if (isStandalone)
                        buildReplaceFix("Delete Call Expression", "", range = location, reformat = false)
                    else null
                )
            }
        }

        private fun UCallExpression.isEntryOwned() = generateSequence(uastParent) { it.uastParent }
            .filterIsInstance<UClass>().firstOrNull()?.javaPsi?.let {
                context.evaluator.extendsClass(it, MODULE_ENTRY_CLASS, false)
            } == true

        private fun UCallExpression.insideEncase() = generateSequence(uastParent) { it.uastParent }
            .filterIsInstance<UCallExpression>().any { it.resolve()?.isEncase() == true }

        private fun UCallExpression.execution(caller: PsiElement?): Execution {
            var unknown = false
            var packageContext = false
            for (parent in generateSequence(uastParent) { it.uastParent }) {
                if (parent.sourcePsi == caller) break
                if (parent !is ULambdaExpression) continue

                val enclosingNode = generateSequence(parent.uastParent) { it.uastParent }
                    .filterIsInstance<UCallExpression>().firstOrNull()
                val enclosingCall = enclosingNode?.resolve()
                when {
                    enclosingNode?.hasPackageParamBlock(parent) == true -> packageContext = true
                    enclosingCall?.isModuleRegistration() == true -> Unit
                    else -> unknown = true
                }
            }

            return when {
                packageContext -> Execution.PACKAGE
                unknown -> Execution.UNKNOWN
                else -> Execution.IMMEDIATE
            }
        }

        private fun UCallExpression.hasPackageParamBlock(lambda: ULambdaExpression): Boolean {
            val parameters = resolve()?.parameterList?.parameters ?: return false
            return parameters.indices.any { index ->
                parameters[index].type.containsPackageParam() &&
                    getArgumentForParameter(index)?.sourcePsi == lambda.sourcePsi
            }
        }

        private fun PsiType.containsPackageParam(): Boolean = when (this) {
            is PsiArrayType -> componentType.containsPackageParam()
            is PsiClassType -> isPackageParam() || parameters.any { it.containsPackageParam() }
            is PsiWildcardType -> bound?.containsPackageParam() == true
            else -> false
        }

        private fun PsiType?.isPackageParam() = (this as? PsiClassType)?.resolve()?.let {
            context.evaluator.extendsClass(it, PACKAGE_PARAM_CLASS, false)
        } == true

        private fun PsiType.isHookLoadingArgument() = containsPackageParam() ||
            (this is PsiArrayType && componentType.canonicalText == BASE_HOOKER_CLASS)

        private fun PsiMethod.isModuleRegistration() =
            parameterList.parametersCount == 2 && parameterList.parameters[0].type.canonicalText == MODULE_ENTRY_CLASS &&
                (containingClass?.qualifiedName in EVENT_REGISTRATIONS || containingClass?.qualifiedName == MODULE_LIFECYCLE_CLASS)

        private fun PsiMethod.isHookLoading() = when (containingClass?.qualifiedName) {
            YUKIHOOK_CLASS, YUKIHOOKAPI_CLASS -> parameterList.parametersCount == 1 &&
                parameterList.parameters[0].type.isHookLoadingArgument()
            YUKIHOOK_FACTORY_CLASS -> parameterList.parametersCount == 2 &&
                parameterList.parameters[0].type.canonicalText == MODULE_ENTRY_CLASS &&
                parameterList.parameters[1].type.isHookLoadingArgument()
            else -> false
        }

        private fun PsiMethod.isEncase() = isHookLoading() ||
            (parameterList.parametersCount == 2 &&
                (containingClass?.qualifiedName == YUKIHOOK_CLASS || containingClass?.qualifiedName == YUKIHOOKAPI_CLASS) &&
                parameterList.parameters[0].type.canonicalText == CONTEXT_CLASS &&
                parameterList.parameters[1].type.containsPackageParam())
    })
}