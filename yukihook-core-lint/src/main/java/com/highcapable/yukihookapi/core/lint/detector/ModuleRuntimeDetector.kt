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
import com.android.tools.lint.detector.api.ConstantEvaluator
import com.android.tools.lint.detector.api.Detector
import com.android.tools.lint.detector.api.Implementation
import com.android.tools.lint.detector.api.Issue
import com.android.tools.lint.detector.api.JavaContext
import com.android.tools.lint.detector.api.LintFix
import com.android.tools.lint.detector.api.Location
import com.android.tools.lint.detector.api.Scope
import com.android.tools.lint.detector.api.Severity
import com.highcapable.yukihookapi.core.lint.DeclaredSymbol
import com.highcapable.yukihookapi.core.lint.detector.extension.allClasses
import com.highcapable.yukihookapi.core.lint.detector.extension.analyzeOrNull
import com.highcapable.yukihookapi.core.lint.detector.extension.branches
import com.highcapable.yukihookapi.core.lint.detector.extension.buildReplaceFix
import com.highcapable.yukihookapi.core.lint.detector.extension.createExternalOnlyUastHandler
import com.highcapable.yukihookapi.core.lint.detector.extension.displayName
import com.highcapable.yukihookapi.core.lint.detector.extension.enclosingCall
import com.highcapable.yukihookapi.core.lint.detector.extension.enclosingFunction
import com.highcapable.yukihookapi.core.lint.detector.extension.functionTarget
import com.highcapable.yukihookapi.core.lint.detector.extension.hasImplicitReceiver
import com.highcapable.yukihookapi.core.lint.detector.extension.isLocal
import com.highcapable.yukihookapi.core.lint.detector.extension.returnedExpressions
import com.highcapable.yukihookapi.core.lint.detector.extension.toUFile
import com.intellij.psi.PsiArrayType
import com.intellij.psi.PsiCapturedWildcardType
import com.intellij.psi.PsiClass
import com.intellij.psi.PsiClassType
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiMethod
import com.intellij.psi.PsiModifier
import com.intellij.psi.PsiParameter
import com.intellij.psi.PsiType
import com.intellij.psi.PsiTypeParameter
import com.intellij.psi.PsiVariable
import com.intellij.psi.PsiWildcardType
import com.intellij.psi.util.InheritanceUtil
import com.intellij.psi.util.PsiUtil
import org.jetbrains.kotlin.analysis.api.resolution.singleFunctionCallOrNull
import org.jetbrains.kotlin.analysis.api.resolution.symbol
import org.jetbrains.kotlin.analysis.api.symbols.KaNamedFunctionSymbol
import org.jetbrains.kotlin.analysis.api.types.KaClassType
import org.jetbrains.kotlin.analysis.api.types.KaFlexibleType
import org.jetbrains.kotlin.analysis.api.types.KaFunctionType
import org.jetbrains.kotlin.analysis.api.types.KaType
import org.jetbrains.kotlin.name.ClassId
import org.jetbrains.kotlin.name.FqName
import org.jetbrains.kotlin.psi.KtCallElement
import org.jetbrains.kotlin.psi.KtCallExpression
import org.jetbrains.kotlin.psi.KtExpression
import org.jetbrains.kotlin.psi.KtLabeledExpression
import org.jetbrains.kotlin.psi.KtLambdaExpression
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtParameter
import org.jetbrains.kotlin.psi.KtParenthesizedExpression
import org.jetbrains.kotlin.psi.KtProperty
import org.jetbrains.kotlin.psi.KtValueArgument
import org.jetbrains.kotlin.psi.psiUtil.getStrictParentOfType
import org.jetbrains.uast.UBlockExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UCallableReferenceExpression
import org.jetbrains.uast.UClass
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UExpression
import org.jetbrains.uast.UFile
import org.jetbrains.uast.ULambdaExpression
import org.jetbrains.uast.UMethod
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.UReturnExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.skipParenthesizedExprDown
import org.jetbrains.uast.toUElement
import org.jetbrains.uast.visitor.AbstractUastVisitor
import java.io.File

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

        private const val RETAINED_VALUE_METHOD_NAME = "remember"
        private const val HOOKER_ENTRY_METHOD_NAME = "onHook"
        private const val LOAD_APP_METHOD_NAME = "loadApp"
        private const val LOAD_SYSTEM_METHOD_NAME = "loadSystem"
        private const val LOAD_APP_NAME_PARAMETER = "name"
        private const val DATA_CHANNEL_METHOD_NAME = "dataChannel"
        private const val SYSTEM_DATA_CHANNEL_METHOD_NAME = "systemDataChannel"

        private val SYSTEM_FRAMEWORK_NAMES = setOf("android", "system")

        private const val STRING_CLASS = "java.lang.String"
        private const val CLASS_CLASS = "java.lang.Class"
        private const val ITERABLE_CLASS = "java.lang.Iterable"
        private const val COLLECTION_CLASS = "java.util.Collection"
        private const val MAP_CLASS = "java.util.Map"
        private const val BUNDLE_CLASS = "android.os.Bundle"

        private val IMPLICIT_RECEIVER = Any()

        private const val MODULE_VALUE_ADVICE = "return a classloader-neutral value"
        private const val SNAPSHOT_TYPE_ADVICE = "declare it as read-only `List`, `Set`, `Map`, `Collection` or `Iterable`"

        private val SNAPSHOT_CLASSES = setOf(ITERABLE_CLASS, COLLECTION_CLASS, "java.util.List", "java.util.Set", MAP_CLASS)

        private val REFERENCE_HOLDER_CLASSES = setOf(
            "java.lang.ref.Reference", "java.util.concurrent.atomic.AtomicReference",
            "java.util.concurrent.atomic.AtomicReferenceArray", "java.lang.ThreadLocal"
        )

        private val PACKAGE_PARAM_CLASS_ID = ClassId.topLevel(FqName(PACKAGE_PARAM_CLASS))

        private val READ_ONLY_SNAPSHOT_TYPES = setOf(
            "kotlin.collections.Iterable", "kotlin.collections.Collection", "kotlin.collections.List",
            "kotlin.collections.Set", "kotlin.collections.Map"
        )

        private val MUTABLE_SNAPSHOT_TYPES = mapOf(
            "kotlin.collections.MutableIterable" to "Iterable", "kotlin.collections.MutableCollection" to "Collection",
            "kotlin.collections.MutableList" to "List", "kotlin.collections.MutableSet" to "Set", "kotlin.collections.MutableMap" to "Map"
        )

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

        val RETAINED_VALUE_ISSUE = Issue.create(
            id = "InvalidModuleRetainedValue",
            briefDescription = "Invalid retained module value",
            explanation = "Retained values must not refer to classes compiled into the module, including its packaged dependencies " +
                "such as the Kotlin standard library, either directly or inside iterables, maps and class values. " +
                "Iterable and map results are frozen into read-only snapshots, declare them as read-only List, Set, Map, Collection or Iterable. " +
                "Arrays and Bundle cannot be frozen and are rejected. " +
                "Keys must be non-blank and unique in the current package scope, the same key can be used in different package scopes. " +
                "Runtime validation covers values that lint cannot determine.",
            category = Category.CORRECTNESS,
            priority = 10,
            severity = Severity.ERROR,
            implementation = IMPLEMENTATION
        )

        val SYSTEM_FRAMEWORK_ISSUE = Issue.create(
            id = "LoadAppForSystemFramework",
            briefDescription = "System framework loaded through loadApp",
            explanation = "The system server is a dedicated hook entry on every runtime. Passing \"android\" or \"system\" " +
                "to loadApp is redirected to loadSystem at runtime, use loadSystem directly instead.",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )

        val SYSTEM_DATA_CHANNEL_ISSUE = Issue.create(
            id = "DataChannelForSystemFramework",
            briefDescription = "System framework connected through dataChannel",
            explanation = "Passing \"android\" or \"system\" to dataChannel is redirected to systemDataChannel at runtime, " +
                "use systemDataChannel directly instead.",
            category = Category.CORRECTNESS,
            priority = 6,
            severity = Severity.WARNING,
            implementation = IMPLEMENTATION
        )
    }

    private enum class Phase { INIT, HOOK, PACKAGE, OUTSIDE, UNKNOWN }
    private enum class Execution { IMMEDIATE, PACKAGE, UNKNOWN }

    private enum class LoadKind { ALWAYS, PACKAGE, ANY_PACKAGE, SYSTEM, ZYGOTE }
    private enum class LambdaExecution { DECLARATION, INLINE, DEFERRED }

    private data class RetainedProblem(val subject: String, val advice: String, val isIndirect: Boolean = false) {

        fun indirect() = copy(isIndirect = true)

        override fun toString() = "${if (isIndirect) "refers to" else "is"} $subject, $advice"
    }

    private data class LoadGuard(val kind: LoadKind, val target: Any? = Unit) {

        fun canRunWith(other: LoadGuard) = kind == LoadKind.ALWAYS || other.kind == LoadKind.ALWAYS || (this == other && target != null)
    }

    private data class RetainedKey(val key: String, val element: UElement, val owner: String?)

    private class KeyContribution(
        val anchor: UElement,
        val guard: LoadGuard,
        val site: RetentionSite,
        val hooker: PsiClass?,
        val keys: List<RetainedKey>
    )

    private data class KeyConflict(val retained: RetainedKey, val previous: RetainedKey) {

        val loadSites = mutableListOf<UElement>()
    }

    private data class RetentionScope(val block: UBlockExpression, val receiver: Any)

    private data class RetentionSite(val branches: Map<PsiElement, PsiElement>) {

        fun canRunWith(other: RetentionSite) = branches.none { (condition, branch) ->
            val otherBranch = other.branches[condition]
            otherBranch != null && otherBranch != branch
        }
    }

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

    private var projectSources: List<File>? = null
    private val sourceTexts = mutableMapOf<File, String>()
    private val parsedSources = mutableMapOf<File, UFile?>()
    private val sourceHookerLoads = mutableMapOf<File, List<Pair<String?, Set<String>>>>()

    override fun getApplicableUastTypes() = listOf(UFile::class.java)

    override fun createUastHandler(context: JavaContext) = context.createExternalOnlyUastHandler(object : UElementHandler() {

        private val packagedLibraries by lazy {
            context.evaluator.dependencies?.packageDependencies?.getAllLibraries().orEmpty().mapTo(mutableSetOf()) { it.identifier }
        }

        private val hookerKeys = mutableMapOf<PsiClass, List<RetainedKey>>()

        override fun visitFile(node: UFile) {
            val calls = mutableListOf<CallSite>()
            val roots = mutableMapOf<PsiElement, Phase>()
            val functions = mutableSetOf<PsiElement>()
            val references = mutableSetOf<PsiElement>()
            val retainedKeys = mutableMapOf<RetentionScope, MutableMap<String, MutableList<RetentionSite>>>()
            val contributions = mutableMapOf<UBlockExpression, MutableList<KeyContribution>>()

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
                    val method = node.resolve()
                    val caller = node.enclosingFunction()
                    val target = node.functionTarget(context)
                    if (caller != null) functions.add(caller)
                    if (target != null) functions.add(target)

                    calls.add(CallSite(
                        node = node,
                        method = method,
                        caller = caller,
                        target = target,
                        execution = node.execution(caller),
                        entryOwned = node.isEntryOwned(),
                        insideEncase = node.insideEncase()
                    ))

                    if (method.isRetainedValue())
                        reportRetainedValue(node, retainedKeys)
                    if (method != null) {
                        reportSystemFrameworkLoading(node, method)
                        reportSystemDataChannel(node, method)
                    }
                    collectKeyContributions(node, method, contributions)

                    return false
                }

                override fun visitCallableReferenceExpression(node: UCallableReferenceExpression): Boolean {
                    val target = node.functionTarget(context) ?: return false
                    functions.add(target)
                    references.add(target)

                    return false
                }
            })

            reportHookerKeyConflicts(node, contributions)

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

        private fun reportRetainedValue(
            node: UCallExpression,
            keys: MutableMap<RetentionScope, MutableMap<String, MutableList<RetentionSite>>>
        ) {
            node.deferredRetention()?.let { owner ->
                context.report(
                    issue = RETAINED_VALUE_ISSUE,
                    scope = node,
                    location = context.getNameLocation(node),
                    message = "`$RETAINED_VALUE_METHOD_NAME` must be called while declaring hooks, not in $owner"
                )
            }

            val key = node.retainedKeyArgument()
            val resolvedKey = key?.let { ConstantEvaluator.evaluate(context, it) as? String }
            if (resolvedKey != null) {
                val message = when {
                    resolvedKey.isBlank() -> "`$RETAINED_VALUE_METHOD_NAME` key must not be blank"
                    else -> {
                        val block = generateSequence(node.uastParent) { it.uastParent }
                            .filterIsInstance<UBlockExpression>()
                            .firstOrNull()
                        val receiver = node.retentionReceiver()
                        val site = RetentionSite(node.branches())
                        val previous = if (block != null && receiver != null)
                            keys.getOrPut(RetentionScope(block, receiver)) { mutableMapOf() }.getOrPut(resolvedKey) { mutableListOf() }
                        else null
                        val isDuplicate = previous?.any(site::canRunWith) == true
                        previous?.add(site)

                        if (isDuplicate)
                            "`$RETAINED_VALUE_METHOD_NAME` key '$resolvedKey' is already used in this block"
                        else null
                    }
                }

                if (message != null) context.report(
                    issue = RETAINED_VALUE_ISSUE,
                    scope = node,
                    location = context.getLocation(key),
                    message = message
                )
            }

            // Mark the returned values, or the block itself when it is not a lambda with results, the call is not the cause.
            // A problem in the declared type already covers every result, check the narrower result types only when it has none.
            val results = node.retainedResults()
            (node.getExpressionType()?.retainedProblem(declared = true) ?: node.mutableSnapshotProblem())?.let { problem ->
                if (results.isEmpty()) reportRetainedProblem(node.valueArguments.lastOrNull() ?: node, problem)
                else results.forEach { reportRetainedProblem(it, problem) }
                return
            }
            results.forEach { result ->
                result.getExpressionType()?.retainedProblem(declared = false)?.let { reportRetainedProblem(result, it) }
            }
        }

        private fun reportRetainedProblem(node: UElement, problem: RetainedProblem) = context.report(
            issue = RETAINED_VALUE_ISSUE,
            scope = node,
            location = context.getLocation(node),
            message = "`$RETAINED_VALUE_METHOD_NAME` result $problem"
        )

        private fun UCallExpression.retainedKeyArgument(): UExpression? {
            val index = resolve()?.parameterList?.parameters?.indexOfFirst { it.type.canonicalText == STRING_CLASS } ?: -1
            return getArgumentForParameter(index.coerceAtLeast(0))
        }

        private fun UCallExpression.retainedKey() = retainedKeyArgument()?.let { argument ->
            (ConstantEvaluator.evaluate(context, argument) as? String)?.takeIf { it.isNotBlank() }?.let { it to argument }
        }

        private fun PsiMethod?.isRetainedValue() =
            this?.name == RETAINED_VALUE_METHOD_NAME && containingClass?.qualifiedName == PACKAGE_PARAM_CLASS

        private fun reportSystemFrameworkLoading(node: UCallExpression, method: PsiMethod) {
            if (method.name != LOAD_APP_METHOD_NAME || method.containingClass?.qualifiedName != PACKAGE_PARAM_CLASS) return
            val names = context.evaluator.computeArgumentMapping(node, method)
                .filterValues { it.name == LOAD_APP_NAME_PARAMETER }.keys
            val framework = names.firstNotNullOfOrNull { name ->
                (ConstantEvaluator.evaluate(context, name) as? String)?.takeIf { it in SYSTEM_FRAMEWORK_NAMES }
            } ?: return

            context.report(
                issue = SYSTEM_FRAMEWORK_ISSUE,
                scope = node,
                location = node.callWithoutLambdaLocation(),
                message = "\"$framework\" refers to the system framework, use `$LOAD_SYSTEM_METHOD_NAME` instead",
                quickfixData = node.loadSystemFix(names.singleOrNull())
            )
        }

        private fun reportSystemDataChannel(node: UCallExpression, method: PsiMethod) {
            if (method.name != DATA_CHANNEL_METHOD_NAME || method.containingClass?.qualifiedName != YUKIHOOK_FACTORY_CLASS) return
            val packageName = node.valueArguments.singleOrNull() ?: return
            val framework = (ConstantEvaluator.evaluate(context, packageName) as? String)?.takeIf { it in SYSTEM_FRAMEWORK_NAMES } ?: return
            val callee = (node.sourcePsi as? KtCallExpression)?.calleeExpression
            val arguments = (node.sourcePsi as? KtCallExpression)?.valueArgumentList

            context.report(
                issue = SYSTEM_DATA_CHANNEL_ISSUE,
                scope = node,
                location = context.getCallLocation(node, includeReceiver = false, includeArguments = true),
                message = "\"$framework\" refers to the system framework, use `$SYSTEM_DATA_CHANNEL_METHOD_NAME` instead",
                quickfixData = if (callee != null && arguments != null) buildReplaceFix(
                    name = "Replace with '$SYSTEM_DATA_CHANNEL_METHOD_NAME'",
                    replacement = "$SYSTEM_DATA_CHANNEL_METHOD_NAME()",
                    imports = arrayOf("${YUKIHOOK_FACTORY_CLASS.substringBeforeLast('.')}.$SYSTEM_DATA_CHANNEL_METHOD_NAME"),
                    range = context.getRangeLocation(callee, 0, arguments, 0),
                    reformat = false
                ) else null
            )
        }

        private fun UCallExpression.callWithoutLambdaLocation(): Location {
            // Trailing lambdas carry the hook body, keep them out of the call location.
            val source = sourcePsi as? KtCallExpression
                ?: return context.getCallLocation(this, includeReceiver = false, includeArguments = true)
            val callee = source.calleeExpression ?: return context.getNameLocation(this)

            return context.getRangeLocation(callee, 0, source.valueArgumentList ?: callee, 0)
        }

        private fun UCallExpression.loadSystemFix(name: UExpression?): LintFix? {
            // Only a call loading the system framework alone keeps its behavior after migrating.
            val source = sourcePsi as? KtCallExpression ?: return null
            val callee = source.calleeExpression ?: return null
            val arguments = source.valueArgumentList ?: return null
            val nameArgument = name?.sourcePsi?.getStrictParentOfType<KtValueArgument>() ?: return null
            if (nameArgument.isSpread) return null

            val remaining = arguments.arguments.filter { it != nameArgument }.map { argument ->
                (if (argument.isSpread) "*" else "") + (argument.getArgumentExpression()?.text ?: return null)
            }
            val replacement = LOAD_SYSTEM_METHOD_NAME +
                if (remaining.isEmpty() && source.lambdaArguments.isNotEmpty()) "" else remaining.joinToString(prefix = "(", postfix = ")")

            return buildReplaceFix(
                name = "Replace with '$LOAD_SYSTEM_METHOD_NAME'",
                replacement = replacement,
                range = context.getRangeLocation(callee, 0, arguments, 0),
                reformat = false
            )
        }

        private fun PsiMethod.loadGuard(node: UCallExpression): LoadGuard? {
            if (containingClass?.qualifiedName != PACKAGE_PARAM_CLASS) return null
            val argument = node.getArgumentForParameter(0)

            return when (name) {
                "loadHooker" -> LoadGuard(LoadKind.ALWAYS)
                "loadSystem" -> LoadGuard(LoadKind.SYSTEM)
                "loadZygote" -> LoadGuard(LoadKind.ZYGOTE)
                "loadApp" -> if (parameterList.parameters.firstOrNull()?.type?.canonicalText == STRING_CLASS) {
                    val name = argument?.let { ConstantEvaluator.evaluate(context, it) as? String }
                    // The system framework names are redirected to loadSystem at runtime.
                    if (name in SYSTEM_FRAMEWORK_NAMES) LoadGuard(LoadKind.SYSTEM) else LoadGuard(LoadKind.PACKAGE, name)
                } else LoadGuard(LoadKind.ANY_PACKAGE, argument?.let { ConstantEvaluator.evaluate(context, it) as? Boolean } ?: false.takeIf { argument == null })
                else -> null
            }
        }

        private fun UCallExpression.loadedHookers() = valueArguments.mapNotNull { argument ->
            val type = (argument.getExpressionType() as? PsiClassType)?.resolve() ?: return@mapNotNull null
            type.takeIf { context.evaluator.extendsClass(it, BASE_HOOKER_CLASS, false) }?.let { argument to it }
        }

        private fun collectKeyContributions(
            node: UCallExpression,
            method: PsiMethod?,
            contributions: MutableMap<UBlockExpression, MutableList<KeyContribution>>
        ) {
            if (method == null || !node.hasImplicitReceiver()) return
            val block = generateSequence(node.uastParent) { it.uastParent }.filterIsInstance<UBlockExpression>().firstOrNull() ?: return
            val site = RetentionSite(node.branches())

            if (method.isRetainedValue()) node.retainedKey()?.let { (key, argument) ->
                contributions.getOrPut(block) { mutableListOf() } +=
                    KeyContribution(argument, LoadGuard(LoadKind.ALWAYS), site, hooker = null, listOf(RetainedKey(key, argument, owner = null)))
            }

            val guard = method.loadGuard(node) ?: return
            node.loadedHookers().forEach { (argument, hooker) ->
                contributions.getOrPut(block) { mutableListOf() } += KeyContribution(argument, guard, site, hooker, hooker.hookerKeys())
            }
        }

        private fun PsiClass.hookerKeys(visiting: MutableSet<PsiClass> = mutableSetOf()): List<RetainedKey> {
            hookerKeys[this]?.let { return it }
            if (!visiting.add(this)) return emptyList()

            val owner = displayName()
            val keys = mutableListOf<RetainedKey>()
            val onHook = (toUElement() as? UClass)?.methods?.firstOrNull { it.name == HOOKER_ENTRY_METHOD_NAME && it.uastParameters.isEmpty() }
            onHook?.uastBody?.accept(object : AbstractUastVisitor() {

                override fun visitClass(node: UClass) = true

                override fun visitLambdaExpression(node: ULambdaExpression) = true

                override fun visitCallExpression(node: UCallExpression): Boolean {
                    val method = node.resolve()
                    if (method != null && node.hasImplicitReceiver()) when {
                        method.isRetainedValue() -> node.retainedKey()?.let { (key, argument) -> keys += RetainedKey(key, argument, owner) }
                        method.loadGuard(node)?.kind == LoadKind.ALWAYS -> node.loadedHookers().forEach { (_, hooker) ->
                            keys += hooker.hookerKeys(visiting)
                        }
                    }

                    return super.visitCallExpression(node)
                }
            })

            visiting.remove(this)
            return keys.also { hookerKeys[this] = it }
        }

        private fun reportHookerKeyConflicts(file: UFile, contributions: Map<UBlockExpression, List<KeyContribution>>) {
            val conflicts = linkedMapOf<Pair<PsiElement?, PsiElement?>, KeyConflict>()
            contributions.values.forEach { collectHookerKeyConflicts(it, conflicts) }
            // Key contributions depend on the hookers visited from this file, so they are not cached.
            file.hookerLoaderFiles().forEach { loader -> loader.keyContributions().values.forEach { collectHookerKeyConflicts(it, conflicts) } }

            conflicts.values
                .filter { it.retained.element.sourcePsi?.containingFile?.virtualFile?.path == context.file.path }
                .forEach(::reportHookerKeyConflict)
        }

        private fun UFile.keyContributions(): Map<UBlockExpression, List<KeyContribution>> {
            val contributions = mutableMapOf<UBlockExpression, MutableList<KeyContribution>>()
            accept(object : AbstractUastVisitor() {

                override fun visitCallExpression(node: UCallExpression): Boolean {
                    collectKeyContributions(node, node.resolve(), contributions)
                    return false
                }
            })

            return contributions
        }

        private fun UFile.hookerLoaderFiles(): List<UFile> {
            val names = allClasses().filter { it.isHooker() }.mapNotNullTo(mutableSetOf()) { it.name }
            if (names.isEmpty()) return emptyList()

            val sources = (projectSources ?: context.project.javaSourceFolders.asSequence()
                .flatMap { it.walkTopDown() }
                .filter { it.isFile && (it.extension == "java" || it.extension == "kt") }
                .toList()
                .also { projectSources = it }).filter { it.path != context.file.path }
            val loaders = linkedMapOf<File, UFile>()
            var pending = names.toSet()
            while (pending.isNotEmpty()) {
                val found = mutableSetOf<String>()
                sources.filter { it !in loaders }
                    .filter { source ->
                        sourceTexts.getOrPut(source) {
                            context.client.readFile(source).toString() }.let { text ->
                            pending.any { it in text }
                        }
                    }
                    .forEach { source ->
                        val loader = parsedSources.getOrPut(source) { context.toUFile(source) } ?: return@forEach
                        loaders[source] = loader
                        val hookerLoads = sourceHookerLoads.getOrPut(source) {
                            loader.allClasses().filter { it.isHooker() }.map { it.name to it.loadedHookerNames() }
                        }
                        hookerLoads.filter { (_, loaded) ->
                            loaded.any(names::contains)
                        }.mapNotNullTo(found) { it.first }
                    }
                pending = found - names
                names += found
            }

            return loaders.values.toList()
        }

        private fun UClass.isHooker() = context.evaluator.extendsClass(javaPsi, BASE_HOOKER_CLASS, false)

        private fun UClass.loadedHookerNames(): Set<String> {
            val names = mutableSetOf<String>()
            methods.firstOrNull { it.name == HOOKER_ENTRY_METHOD_NAME && it.uastParameters.isEmpty() }
                ?.uastBody
                ?.accept(object : AbstractUastVisitor() {

                    override fun visitClass(node: UClass) = true
                    override fun visitLambdaExpression(node: ULambdaExpression) = true

                    override fun visitCallExpression(node: UCallExpression): Boolean {
                        val method = node.resolve()
                        if (method != null && node.hasImplicitReceiver() && method.loadGuard(node)?.kind == LoadKind.ALWAYS)
                            node.loadedHookers().mapNotNullTo(names) { it.second.name }
                        return super.visitCallExpression(node)
                    }
                })

            return names
        }

        private fun collectHookerKeyConflicts(contributions: List<KeyContribution>, conflicts: MutableMap<Pair<PsiElement?, PsiElement?>, KeyConflict>) {
            val ordered = contributions.sortedBy { it.anchor.sourcePsi?.textRange?.startOffset ?: 0 }
            ordered.forEachIndexed { index, current ->
                current.keys.forEach { retained ->
                    val previous = ordered.subList(0, index).asSequence()
                        .filter { it.hooker != null || current.hooker != null }
                        .filter { it.hooker == null || current.hooker == null || it.hooker != current.hooker }
                        .filter { it.guard.canRunWith(current.guard) && it.site.canRunWith(current.site) }
                        .firstNotNullOfOrNull { other -> other.keys.firstOrNull { it.key == retained.key } }
                        ?: return@forEach

                    val conflict = conflicts.getOrPut(retained.element.sourcePsi to previous.element.sourcePsi) { KeyConflict(retained, previous) }
                    if (current.hooker != null) conflict.loadSites += current.anchor
                }
            }
        }

        private fun reportHookerKeyConflict(conflict: KeyConflict) {
            val (retained, previous) = conflict
            val usage = when {
                previous.owner != null -> "by `${previous.owner}` in this package scope"
                retained.owner != null -> "where this hooker is loaded"
                else -> "in this block"
            }
            val location = context.getLocation(retained.element)
            val secondaries = listOf(context.getLocation(previous.element).apply { message = "Previous usage" }) +
                conflict.loadSites.map { site -> context.getLocation(site).apply { message = "Loaded here" } }
            secondaries.reduceRight { secondary, next -> secondary.apply { this.secondary = next } }
            location.secondary = secondaries.first()

            context.report(
                issue = RETAINED_VALUE_ISSUE,
                scope = retained.element,
                location = location,
                message = "`$RETAINED_VALUE_METHOD_NAME` key '${retained.key}' is already used $usage"
            )
        }

        private fun UCallExpression.retentionReceiver(): Any? {
            // UAST may supply a synthetic receiver without source for implicit receivers.
            val receiver = receiver?.skipParenthesizedExprDown()?.takeIf { it.sourcePsi != null } ?: return IMPLICIT_RECEIVER
            val declaration = (receiver as? USimpleNameReferenceExpression)?.resolve() ?: return null
            return when (val source = declaration.navigationElement) {
                is KtParameter -> source.takeUnless { it.hasValOrVar() && it.isMutable }
                is KtProperty -> source.takeUnless { it.isVar }
                is PsiParameter -> source
                is PsiVariable -> source.takeIf { it.hasModifierProperty(PsiModifier.FINAL) }
                else -> null
            }
        }

        private fun UCallExpression.retainedResults() =
            (valueArguments.lastOrNull()?.skipParenthesizedExprDown() as? ULambdaExpression)?.returnedExpressions().orEmpty()

        private fun PsiType.retainedProblem(declared: Boolean): RetainedProblem? = when (this) {
            is PsiArrayType -> RetainedProblem("an array", "return a `List` instead")
            is PsiCapturedWildcardType -> wildcard.retainedProblem(declared)
            is PsiWildcardType -> if (isExtends) extendsBound.retainedProblem(declared) else null
            is PsiClassType -> resolve()?.takeUnless { it is PsiTypeParameter }?.let { type ->
                when {
                    type.qualifiedName == CLASS_CLASS -> parameters.firstOrNull()?.classTarget()?.let {
                        RetainedProblem("module-defined `${it.displayName()}`", MODULE_VALUE_ADVICE, isIndirect = true)
                    }
                    InheritanceUtil.isInheritor(type, BUNDLE_CLASS) ->
                        RetainedProblem("a `Bundle`", "retain its values instead")
                    REFERENCE_HOLDER_CLASSES.any { InheritanceUtil.isInheritor(type, it) } ->
                        RetainedProblem("`${type.displayName()}`", "retain the referenced value instead")
                    // Other iterable types, such as paths, are values unless they are declared as an Iterable.
                    InheritanceUtil.isInheritor(type, COLLECTION_CLASS) ||
                        InheritanceUtil.isInheritor(type, MAP_CLASS) ||
                        (declared && type.qualifiedName == ITERABLE_CLASS) ->
                        if (declared && type.qualifiedName !in SNAPSHOT_CLASSES)
                            RetainedProblem("`${type.displayName()}`", SNAPSHOT_TYPE_ADVICE)
                        else snapshotElementTypes(type).firstNotNullOfOrNull { it.retainedProblem(declared) }?.indirect()
                    type.isModuleDefined() -> RetainedProblem("module-defined `${type.displayName()}`", MODULE_VALUE_ADVICE)
                    else -> null
                }
            }
            else -> null
        }

        private fun PsiType.classTarget(): PsiClass? = when (this) {
            is PsiArrayType -> componentType.classTarget()
            is PsiCapturedWildcardType -> wildcard.classTarget()
            is PsiWildcardType -> if (isExtends) extendsBound.classTarget() else null
            is PsiClassType -> resolve()?.takeUnless { it is PsiTypeParameter || !it.isModuleDefined() }
            else -> null
        }

        private fun PsiClassType.snapshotElementTypes(type: PsiClass) = when {
            InheritanceUtil.isInheritor(type, MAP_CLASS) -> listOfNotNull(
                PsiUtil.substituteTypeParameter(this, MAP_CLASS, 0, true),
                PsiUtil.substituteTypeParameter(this, MAP_CLASS, 1, true)
            )
            else -> listOfNotNull(PsiUtil.substituteTypeParameter(this, ITERABLE_CLASS, 0, true))
        }

        private fun UCallExpression.deferredRetention(): String? {
            for (parent in generateSequence(uastParent) { it.uastParent }) when (parent) {
                is ULambdaExpression -> return when (parent.lambdaExecution()) {
                    LambdaExecution.DECLARATION -> null
                    LambdaExecution.INLINE -> continue
                    LambdaExecution.DEFERRED -> (parent.enclosingCall()?.methodName?.let { "`$it`" } ?: "a lambda") + " that may run later"
                    null -> null
                }
                is UMethod -> return "a local class that may run later".takeIf { parent.isLocal() }
                is UClass -> return "a property initializer that runs before the hooker is loaded".takeIf {
                    hasImplicitReceiver() && context.evaluator.extendsClass(parent.javaPsi, PACKAGE_PARAM_CLASS, false)
                }
            }

            return null
        }

        private fun ULambdaExpression.lambdaExecution(): LambdaExecution? {
            val lambda = sourcePsi as? KtLambdaExpression ?: return null
            return lambda.analyzeOrNull {
                val type = lambda.expressionType as? KaFunctionType
                if ((type?.receiverType as? KaClassType)?.classId == PACKAGE_PARAM_CLASS_ID) return@analyzeOrNull LambdaExecution.DECLARATION

                // Labels and parentheses wrap the lambda inside its argument.
                val argument = generateSequence(lambda.parent) { it.parent }
                    .firstOrNull { it !is KtLabeledExpression && it !is KtParenthesizedExpression } as? KtValueArgument
                    ?: return@analyzeOrNull LambdaExecution.DEFERRED
                val call = argument.getStrictParentOfType<KtCallElement>()?.resolveToCall()?.singleFunctionCallOrNull()
                    ?: return@analyzeOrNull null
                val parameter = (call.valueArgumentMapping[lambda]
                    ?: argument.getArgumentExpression()?.let { call.valueArgumentMapping[it] })?.symbol
                val function = call.symbol as? KaNamedFunctionSymbol
                if (function?.isInline == true && parameter != null && !parameter.isNoinline && !parameter.isCrossinline)
                    LambdaExecution.INLINE
                else LambdaExecution.DEFERRED
            }
        }

        private fun UCallExpression.mutableSnapshotProblem(): RetainedProblem? {
            val expression = sourcePsi as? KtExpression ?: return null

            // Java types cannot tell mutable Kotlin containers from read-only ones, platform types use their read-only bound.
            return expression.analyzeOrNull {
                fun KaType.problem(): RetainedProblem? {
                    val type = (if (this is KaFlexibleType) upperBound else this) as? KaClassType ?: return null
                    val name = type.classId.asSingleFqName().asString()
                    MUTABLE_SNAPSHOT_TYPES[name]?.let {
                        return RetainedProblem("`${type.classId.shortClassName.asString()}`", "declare it as read-only `$it`")
                    }
                    if (name !in READ_ONLY_SNAPSHOT_TYPES) return null

                    return type.typeArguments.firstNotNullOfOrNull { it.type?.problem() }?.indirect()
                }

                expression.expressionType?.problem()
            }
        }

        private fun PsiClass.isModuleDefined(): Boolean {
            // Platform classes and dependencies that are not packaged, such as compileOnly, are supplied by other class loaders.
            val evaluator = context.evaluator
            if (evaluator.getProject(this) != null) return true

            val library = evaluator.findJarPath(this)?.let(evaluator::findOwnerLibrary) ?: return false
            return library.identifier in packagedLibraries
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