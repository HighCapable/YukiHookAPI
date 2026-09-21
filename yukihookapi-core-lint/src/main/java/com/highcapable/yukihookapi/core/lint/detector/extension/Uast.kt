/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
import org.jetbrains.uast.UBinaryExpression
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement
import org.jetbrains.uast.UFile
import org.jetbrains.uast.UQualifiedReferenceExpression
import org.jetbrains.uast.USimpleNameReferenceExpression
import org.jetbrains.uast.UastCallKind
import java.io.File

private const val YUKIHOOKAPI_MODULE_NAME_PREFIX = "yukihookapi-"

internal fun JavaContext.createExternalOnlyUastHandler(handler: UElementHandler) = handler.takeUnless {
    project.name.split('.', ':').any { it.startsWith(YUKIHOOKAPI_MODULE_NAME_PREFIX) }
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

internal fun JavaContext.toUFile(source: File): UFile? {
    if (source == file) return uastFile
    val sourceContext = JavaContext(driver, project, null, source).also {
        it.uastParser = uastParser
        it.sourceSetType = sourceSetType
    }

    return uastParser.parse(sourceContext)
}