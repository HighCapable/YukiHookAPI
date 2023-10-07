/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is created by fankes on 2022/9/4.
 */
package com.highcapable.yukihookapi.hook.core.finder.members.data

import com.highcapable.yukihookapi.hook.core.finder.type.factory.CountConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectsConditions
import java.lang.reflect.Method

/**
 * [Method] 规则查找数据类
 * @param name 名称
 * @param nameConditions 名称规则
 * @param paramTypes 参数类型数组
 * @param paramTypesConditions 参数类型条件
 * @param paramCount 参数个数
 * @param paramCountRange 参数个数范围
 * @param paramCountConditions 参数个数条件
 * @param returnType 返回值类型
 * @param returnTypeConditions 返回值类型条件
 */
internal class MethodRulesData internal constructor(
    var name: String = "",
    var nameConditions: NameConditions? = null,
    var paramTypes: Array<out Class<*>>? = null,
    var paramTypesConditions: ObjectsConditions? = null,
    var paramCount: Int = -1,
    var paramCountRange: IntRange = IntRange.EMPTY,
    var paramCountConditions: CountConditions? = null,
    var returnType: Any? = null,
    var returnTypeConditions: ObjectConditions? = null
) : MemberRulesData() {

    override val templates
        get() = arrayOf(
            name.takeIf { it.isNotBlank() }?.let { "name:[$it]" } ?: "",
            nameConditions?.let { "nameConditions:[existed]" } ?: "",
            paramCount.takeIf { it >= 0 }?.let { "paramCount:[$it]" } ?: "",
            paramCountRange.takeIf { it.isEmpty().not() }?.let { "paramCountRange:[$it]" } ?: "",
            paramCountConditions?.let { "paramCountConditions:[existed]" } ?: "",
            paramTypes?.typeOfString()?.let { "paramTypes:[$it]" } ?: "",
            paramTypesConditions?.let { "paramTypesConditions:[existed]" } ?: "",
            returnType?.let { "returnType:[$it]" } ?: "",
            returnTypeConditions?.let { "returnTypeConditions:[existed]" } ?: "", *super.templates
        )

    override val objectName get() = "Method"

    override val isInitialize
        get() = super.isInitializeOfSuper || name.isNotBlank() || nameConditions != null || paramTypes != null || paramTypesConditions != null ||
            paramCount >= 0 || paramCountRange.isEmpty().not() || paramCountConditions != null ||
            returnType != null || returnTypeConditions != null

    override fun toString() = "[$name][$nameConditions][$paramTypes][$paramTypesConditions][$paramCount]" +
        "[$paramCountRange][$returnType][$returnTypeConditions]" + super.toString()
}