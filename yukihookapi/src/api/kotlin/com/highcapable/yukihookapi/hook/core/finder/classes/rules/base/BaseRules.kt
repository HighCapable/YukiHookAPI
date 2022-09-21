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
 * This file is Created by fankes on 2022/9/12.
 */
package com.highcapable.yukihookapi.hook.core.finder.classes.rules.base

import com.highcapable.yukihookapi.hook.core.finder.classes.DexClassFinder
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.ConstructorRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.FieldRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MemberRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MethodRules
import com.highcapable.yukihookapi.hook.core.finder.members.data.ConstructorRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MemberRulesData
import com.highcapable.yukihookapi.hook.core.finder.members.data.MethodRulesData
import java.lang.reflect.Member

/**
 * [Member] 查询条件实现父类
 * @param instance 当前查找类实例
 */
open class BaseRules internal constructor(internal var instance: DexClassFinder? = null) {

    @PublishedApi
    internal companion object {

        /**
         * 创建查询条件规则数据
         * @param instance 当前查找类实例
         * @return [MemberRulesData]
         */
        @PublishedApi
        internal fun createMemberRules(instance: DexClassFinder) =
            MemberRules(MemberRulesData().apply { instance.rulesData.memberRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查询条件规则数据
         * @return [FieldRulesData]
         */
        @PublishedApi
        internal fun createFieldRules(instance: DexClassFinder) =
            FieldRules(FieldRulesData().apply { instance.rulesData.fieldRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查询条件规则数据
         * @return [MethodRulesData]
         */
        @PublishedApi
        internal fun createMethodRules(instance: DexClassFinder) =
            MethodRules(MethodRulesData().apply { instance.rulesData.methodRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查询条件规则数据
         * @return [ConstructorRulesData]
         */
        @PublishedApi
        internal fun createConstructorRules(instance: DexClassFinder) =
            ConstructorRules(ConstructorRulesData().apply { instance.rulesData.constroctorRules.add(this) }).apply { this.instance = instance }
    }

    /**
     * 将目标类型转换为可识别的兼容类型
     * @param tag 当前查找类的标识
     * @return [Class] or null
     */
    internal fun Any?.compat(tag: String) = instance?.compatType(any = this, tag)
}