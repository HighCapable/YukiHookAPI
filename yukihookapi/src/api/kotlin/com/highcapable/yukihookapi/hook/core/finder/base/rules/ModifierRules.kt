/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
 * @param instance 当前实例对象
 */
class ModifierRules private constructor(private val instance: Any) {

    @PublishedApi
    internal companion object {

        /** 当前实例数组 */
        private val instances = HashMap<Long, ModifierRules>()

        /**
         * 获取模板字符串数组
         * @param value 唯一标识值
         * @return [ArrayList]<[String]>
         */
        internal fun templates(value: Long) = instances[value]?.templates ?: arrayListOf()

        /**
         * 创建实例
         * @param instance 实例对象
         * @param value 唯一标识值 - 默认 0
         * @return [ModifierRules]
         */
        @PublishedApi
        internal fun with(instance: Any, value: Long = 0) = ModifierRules(instance).apply { instances[value] = this }
    }

    /** 当前模板字符串数组 */
    private val templates = ArrayList<String>()

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
    val isPublic get() = Modifier.isPublic(modifiers).also { templates.add("<isPublic> ($it)") }

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
    val isPrivate get() = Modifier.isPrivate(modifiers).also { templates.add("<isPrivate> ($it)") }

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
    val isProtected get() = Modifier.isProtected(modifiers).also { templates.add("<isProtected> ($it)") }

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
    val isStatic get() = Modifier.isStatic(modifiers).also { templates.add("<isStatic> ($it)") }

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
    val isFinal get() = Modifier.isFinal(modifiers).also { templates.add("<isFinal> ($it)") }

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
    val isSynchronized get() = Modifier.isSynchronized(modifiers).also { templates.add("<isSynchronized> ($it)") }

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
    val isVolatile get() = Modifier.isVolatile(modifiers).also { templates.add("<isVolatile> ($it)") }

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
    val isTransient get() = Modifier.isTransient(modifiers).also { templates.add("<isTransient> ($it)") }

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
    val isNative get() = Modifier.isNative(modifiers).also { templates.add("<isNative> ($it)") }

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
    val isInterface get() = Modifier.isInterface(modifiers).also { templates.add("<isInterface> ($it)") }

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
    val isAbstract get() = Modifier.isAbstract(modifiers).also { templates.add("<isAbstract> ($it)") }

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
    val isStrict get() = Modifier.isStrict(modifiers).also { templates.add("<isStrict> ($it)") }

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