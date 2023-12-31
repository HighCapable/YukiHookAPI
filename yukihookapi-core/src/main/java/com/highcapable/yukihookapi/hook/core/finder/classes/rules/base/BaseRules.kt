/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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
 * This file is created by fankes on 2022/9/12.
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
 * [Member] 查找条件实现父类
 * @param instance 当前查找类实例
 */
open class BaseRules internal constructor(internal var instance: DexClassFinder? = null) {

    internal companion object {

        /**
         * 创建查找条件规则数据
         * @param instance 当前查找类实例
         * @return [MemberRulesData]
         */
        internal fun createMemberRules(instance: DexClassFinder) =
            MemberRules(MemberRulesData().apply { instance.rulesData.memberRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查找条件规则数据
         * @return [FieldRulesData]
         */
        internal fun createFieldRules(instance: DexClassFinder) =
            FieldRules(FieldRulesData().apply { instance.rulesData.fieldRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查找条件规则数据
         * @return [MethodRulesData]
         */
        internal fun createMethodRules(instance: DexClassFinder) =
            MethodRules(MethodRulesData().apply { instance.rulesData.methodRules.add(this) }).apply { this.instance = instance }

        /**
         * 创建查找条件规则数据
         * @return [ConstructorRulesData]
         */
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