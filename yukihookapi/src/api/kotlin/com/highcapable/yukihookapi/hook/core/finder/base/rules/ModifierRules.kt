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
 * This file is Modified by fankes on 2022/9/14.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core.finder.base.rules

import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.lang.reflect.Modifier

/**
 * 这是一个 [Class]、[Member] 描述符条件实现类
 *
 * 可对 R8 混淆后的 [Class]、[Member] 进行更加详细的定位
 */
class ModifierRules private constructor(private val instance: Any) {

    @PublishedApi
    internal companion object {

        /**
         * 创建实例
         * @param instance 实例对象
         * @return [ModifierRules]
         */
        @PublishedApi
        internal fun with(instance: Any) = ModifierRules(instance)
    }

    /**
     * [Class]、[Member] 类型是否包含 public
     *
     * 如下所示 ↓
     *
     * public class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isPublic get() = Modifier.isPublic(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 private
     *
     * 如下所示 ↓
     *
     * private class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isPrivate get() = Modifier.isPrivate(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 protected
     *
     * 如下所示 ↓
     *
     * protected class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isProtected get() = Modifier.isProtected(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 static
     *
     * 对于任意的静态 [Class]、[Member] 可添加此描述进行确定
     *
     * 如下所示 ↓
     *
     * static class/void/int/String...
     *
     * ^^^
     *
     * - ❗注意 Kotlin → Jvm 后的 object 类中的方法并不是静态的
     * @return [Boolean]
     */
    val isStatic get() = Modifier.isStatic(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 final
     *
     * 如下所示 ↓
     *
     * final class/void/int/String...
     *
     * ^^^
     *
     * - ❗注意 Kotlin → Jvm 后没有 open 标识的 [Class]、[Member] 和没有任何关联的 [Class]、[Member] 都将为 final
     * @return [Boolean]
     */
    val isFinal get() = Modifier.isFinal(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 synchronized
     *
     * 如下所示 ↓
     *
     * synchronized class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isSynchronized get() = Modifier.isSynchronized(modifiers)

    /**
     * [Field] 类型是否包含 volatile
     *
     * 如下所示 ↓
     *
     * volatile int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isVolatile get() = Modifier.isVolatile(modifiers)

    /**
     * [Field] 类型是否包含 transient
     *
     * 如下所示 ↓
     *
     * transient int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isTransient get() = Modifier.isTransient(modifiers)

    /**
     * [Method] 类型是否包含 native
     *
     * 对于任意 JNI 对接的 [Method] 可添加此描述进行确定
     *
     * 如下所示 ↓
     *
     * native void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isNative get() = Modifier.isNative(modifiers)

    /**
     * [Class] 类型是否包含 interface
     *
     * 如下所示 ↓
     *
     * interface ...
     *
     * ^^^
     * @return [Boolean]
     */
    val isInterface get() = Modifier.isInterface(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 abstract
     *
     * 对于任意的抽象 [Class]、[Member] 可添加此描述进行确定
     *
     * 如下所示 ↓
     *
     * abstract class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isAbstract get() = Modifier.isAbstract(modifiers)

    /**
     * [Class]、[Member] 类型是否包含 strictfp
     *
     * 如下所示 ↓
     *
     * strictfp class/void/int/String...
     *
     * ^^^
     * @return [Boolean]
     */
    val isStrict get() = Modifier.isStrict(modifiers)

    /**
     * 获取当前对象的类型描述符
     * @return [Int]
     */
    private val modifiers
        get() = when (instance) {
            is Member -> instance.modifiers
            is Class<*> -> instance.modifiers
            else -> 0
        }

    override fun toString() = "ModifierRules [$instance]"
}