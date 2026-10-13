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
 * This file is created by fankes on 2026/10/13.
 */
package com.highcapable.yukihookapi.extension.kavaref.lint.detector.extension

import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.JavaContext
import com.intellij.openapi.diagnostic.ControlFlowException
import org.jetbrains.kotlin.analysis.api.KaSession
import org.jetbrains.kotlin.analysis.api.analyze
import org.jetbrains.kotlin.psi.KtElement
import java.util.concurrent.CancellationException

private const val YUKIHOOK_MODULE_NAME_PREFIX = "yukihook-"

internal fun JavaContext.createExternalOnlyUastHandler(handler: UElementHandler) = handler.takeUnless {
    project.name.split('.', ':').any { it.startsWith(YUKIHOOK_MODULE_NAME_PREFIX) }
}

internal fun <R> KtElement.analyzeOrNull(block: KaSession.() -> R): R? = try {
    analyze(this, block)
} catch (failure: Throwable) {
    if (failure is ControlFlowException || failure is CancellationException) throw failure
    null
}