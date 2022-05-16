/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
 * https://github.com/fankes/YukiHookAPI
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
 * This file is Created by fankes on 2022/3/27.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core.finder.type

import java.lang.reflect.Member
import java.lang.reflect.Modifier

/**
 * 这是一个 [Member] 描述符定义类
 *
 * 可对 R8 混淆后的 [Member] 进行更加详细的定位
 */
class ModifierRules @PublishedApi internal constructor() {

    /** 描述声明使用 */
    private var isPublic = false

    /** 描述声明使用 */
    private var isPrivate = false

    /** 描述声明使用 */
    private var isProtected = false

    /** 描述声明使用 */
    private var isStatic = false

    /** 描述声明使用 */
    private var isFinal = false

    /** 描述声明使用 */
    private var isSynchronized = false

    /** 描述声明使用 */
    private var isVolatile = false

    /** 描述声明使用 */
    private var isTransient = false

    /** 描述声明使用 */
    private var isNative = false

    /** 描述声明使用 */
    private var isInterface = false

    /** 描述声明使用 */
    private var isAbstract = false

    /** 描述声明使用 */
    private var isStrict = false

    /** 添加描述 [Member] 类型包含 public */
    fun asPublic() {
        isPublic = true
    }

    /** 添加描述 [Member] 类型包含 private */
    fun asPrivate() {
        isPrivate = true
    }

    /** 添加描述 [Member] 类型包含 protected */
    fun asProtected() {
        isProtected = true
    }

    /**
     * 添加描述 [Member] 类型包含 static
     *
     * 对于任意的静态 [Member] 可添加此描述进行确定
     *
     * - ❗特别注意 Kotlin -> Jvm 后的 object 类中的方法并不是静态的
     */
    fun asStatic() {
        isStatic = true
    }

    /**
     * 添加描述 [Member] 类型包含 final
     *
     * - ❗特别注意在 Kotlin -> Jvm 后没有 open 标识的 [Member] 和没有任何关联的 [Member] 都将为 final
     */
    fun asFinal() {
        isFinal = true
    }

    /** 添加描述 [Member] 类型包含 synchronized */
    fun asSynchronized() {
        isSynchronized = true
    }

    /** 添加描述 [Member] 类型包含 volatile */
    fun asVolatile() {
        isVolatile = true
    }

    /** 添加描述 [Member] 类型包含 transient */
    fun asTransient() {
        isTransient = true
    }

    /**
     * 添加描述 [Member] 类型包含 native
     *
     * 对于任意 JNI 对接的 [Member] 可添加此描述进行确定
     */
    fun asNative() {
        isNative = true
    }

    /** 添加描述 [Member] 类型包含 interface */
    fun asInterface() {
        isInterface = true
    }

    /**
     * 添加描述 [Member] 类型包含 abstract
     *
     * 对于任意的抽象 [Member] 可添加此描述进行确定
     */
    fun asAbstract() {
        isAbstract = true
    }

    /** 添加描述 [Member] 类型包含 strict */
    fun asStrict() {
        isStrict = true
    }

    /**
     * 对比 [Member] 类型是否符合条件
     * @param member 实例
     * @return [Boolean] 是否符合条件
     */
    @PublishedApi
    internal fun contains(member: Member): Boolean {
        var conditions = true
        if (isPublic) conditions = Modifier.isPublic(member.modifiers)
        if (isPrivate) conditions = conditions && Modifier.isPrivate(member.modifiers)
        if (isProtected) conditions = conditions && Modifier.isProtected(member.modifiers)
        if (isStatic) conditions = conditions && Modifier.isStatic(member.modifiers)
        if (isFinal) conditions = conditions && Modifier.isFinal(member.modifiers)
        if (isSynchronized) conditions = conditions && Modifier.isSynchronized(member.modifiers)
        if (isVolatile) conditions = conditions && Modifier.isVolatile(member.modifiers)
        if (isTransient) conditions = conditions && Modifier.isTransient(member.modifiers)
        if (isNative) conditions = conditions && Modifier.isNative(member.modifiers)
        if (isInterface) conditions = conditions && Modifier.isInterface(member.modifiers)
        if (isAbstract) conditions = conditions && Modifier.isAbstract(member.modifiers)
        if (isStrict) conditions = conditions && Modifier.isStrict(member.modifiers)
        return conditions
    }

    override fun toString(): String {
        var conditions = ""
        if (isPublic) conditions += "<public> "
        if (isPrivate) conditions += "<private> "
        if (isProtected) conditions += "<protected> "
        if (isStatic) conditions += "<static> "
        if (isFinal) conditions += "<final> "
        if (isSynchronized) conditions += "<synchronized> "
        if (isVolatile) conditions += "<volatile> "
        if (isTransient) conditions += "<transient> "
        if (isNative) conditions += "<native> "
        if (isInterface) conditions += "<interface> "
        if (isAbstract) conditions += "<abstract> "
        if (isStrict) conditions += "<strict> "
        return "[${conditions.trim()}]"
    }
}