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
 * This file is created by fankes on 2022/2/4.
 */
@file:Suppress("unused", "UNCHECKED_CAST", "MemberVisibilityCanBePrivate", "KotlinConstantConditions", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi.hook.core.finder.members

import com.highcapable.yukihookapi.hook.bean.CurrentClass
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreator
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.base.MemberBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.data.FieldRulesData
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.FieldConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ObjectConditions
import com.highcapable.yukihookapi.hook.factory.current
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import java.lang.reflect.Field

/**
 * [Field] 查找类
 *
 * 可通过指定类型查找指定 [Field] 或一组 [Field]
 * @param classSet 当前需要查找的 [Class] 实例
 */
class FieldFinder internal constructor(override val classSet: Class<*>? = null) : MemberBaseFinder(tag = "Field", classSet) {

    internal companion object {

        /**
         * 通过 [YukiMemberHookCreator.MemberHookCreator] 创建 [Field] 查找类
         * @param hookInstance 当前 Hooker
         * @param classSet 当前需要查找的 [Class] 实例
         * @return [FieldFinder]
         */
        internal fun fromHooker(hookInstance: YukiMemberHookCreator.MemberHookCreator, classSet: Class<*>? = null) =
            FieldFinder(classSet).apply { hookerManager.instance = hookInstance }
    }

    override var rulesData = FieldRulesData()

    /** 当前使用的 [classSet] */
    private var usedClassSet = classSet

    /** 当前重查找结果回调 */
    private var remedyPlansCallback: (() -> Unit)? = null

    /**
     * 设置 [Field] 名称
     *
     * - 若不填写名称则必须存在一个其它条件
     * @return [String]
     */
    var name
        get() = rulesData.name
        set(value) {
            rulesData.name = value
        }

    /**
     * 设置 [Field] 类型
     *
     * - 只能是 [Class]、[String]、[VariousClass]
     *
     * - 可不填写类型
     * @return [Any] or null
     */
    var type
        get() = rulesData.type
        set(value) {
            rulesData.type = value.compat()
        }

    /**
     * 设置 [Field] 标识符筛选条件
     *
     * - 可不设置筛选条件
     *
     * - 存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param conditions 条件方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun modifiers(conditions: ModifierConditions): IndexTypeCondition {
        rulesData.modifiers = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 顺序筛选字节码的下标
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * 设置 [Field] 名称
     *
     * - 若不填写名称则必须存在一个其它条件
     *
     * - 存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 名称
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(value: String): IndexTypeCondition {
        rulesData.name = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Field] 名称条件
     *
     * - 若不填写名称则必须存在一个其它条件
     *
     * - 存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param conditions 条件方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(conditions: NameConditions): IndexTypeCondition {
        rulesData.nameConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Field] 类型
     *
     * - 可不填写类型
     *
     * - 存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 类型 - 只能是 [Class]、[String]、[VariousClass]
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun type(value: Any): IndexTypeCondition {
        rulesData.type = value.compat()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Field] 类型条件
     *
     * - 可不填写类型
     *
     * 使用示例如下 ↓
     *
     * ```kotlin
     * type { it == StringClass || it.name == "java.lang.String" }
     * ```
     *
     * - 存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param conditions 条件方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun type(conditions: ObjectConditions): IndexTypeCondition {
        rulesData.typeConditions = conditions
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置在 [classSet] 的所有父类中查找当前 [Field]
     *
     * - 若当前 [classSet] 的父类较多可能会耗时 - API 会自动循环到父类继承是 [Any] 前的最后一个类
     * @param isOnlySuperClass 是否仅在当前 [classSet] 的父类中查找 - 若父类是 [Any] 则不会生效
     */
    fun superClass(isOnlySuperClass: Boolean = false) {
        rulesData.isFindInSuper = true
        if (isOnlySuperClass && classSet?.hasExtends == true) usedClassSet = classSet.superclass
    }

    /**
     * 得到 [Field] 或一组 [Field]
     * @return [MutableList]<[Field]>
     * @throws NoSuchFieldError 如果找不到 [Field]
     */
    private val result get() = ReflectionTool.findFields(usedClassSet, rulesData)

    /**
     * 设置实例
     * @param fields 当前找到的 [Field] 数组
     */
    private fun setInstance(fields: MutableList<Field>) {
        memberInstances.clear()
        fields.takeIf { it.isNotEmpty() }?.forEach { memberInstances.add(it) }
    }

    /** 得到 [Field] 结果 */
    private fun internalBuild() {
        if (classSet == null) error(CLASSSET_IS_NULL)
        runBlocking {
            setInstance(result)
        }.result { ms ->
            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Field [$it] takes ${ms}ms") }
        }
    }

    override fun build() = runCatching {
        internalBuild()
        Result()
    }.getOrElse {
        errorMsg(e = it)
        Result(isNoSuch = true, it)
    }

    override fun process() = error("FieldFinder does not contain this usage")

    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    override fun denied(throwable: Throwable?) = error("FieldFinder does not contain this usage")

    /**
     * [Field] 重查找实现类
     *
     * 可累计失败次数直到查找成功
     */
    inner class RemedyPlan internal constructor() {

        /** 失败尝试次数数组 */
        private val remedyPlans = mutableSetOf<Pair<FieldFinder, Result>>()

        /**
         * 创建需要重新查找的 [Field]
         *
         * 你可以添加多个备选 [Field] - 直到成功为止
         *
         * 若最后依然失败 - 将停止查找并输出错误日志
         * @param initiate 方法体
         * @return [Result] 结果
         */
        inline fun field(initiate: FieldConditions) = Result().apply {
            remedyPlans.add(FieldFinder(classSet).apply {
                hookerManager = this@FieldFinder.hookerManager
            }.apply(initiate) to this)
        }

        /** 开始重查找 */
        internal fun build() {
            if (classSet == null) return
            if (remedyPlans.isNotEmpty()) {
                val errors = mutableListOf<Throwable>()
                var isFindSuccess = false
                remedyPlans.forEachIndexed { index, plan ->
                    runCatching {
                        runBlocking {
                            setInstance(plan.first.result)
                        }.result { ms ->
                            memberInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Field [$it] takes ${ms}ms") }
                        }
                        isFindSuccess = true
                        plan.second.onFindCallback?.invoke(memberInstances.fields())
                        remedyPlansCallback?.invoke()
                        memberInstances.takeIf { it.isNotEmpty() }
                            ?.forEach { debugMsg(msg = "RemedyPlan successed after ${index + 1} attempts of Field [$it]") }
                        return
                    }.onFailure { errors.add(it) }
                }
                if (isFindSuccess) return
                errorMsg(msg = "RemedyPlan failed after ${remedyPlans.size} attempts", es = errors, isAlwaysMode = true)
                remedyPlans.clear()
            } else YLog.innerW("RemedyPlan is empty, forgot it?")
        }

        /**
         * [RemedyPlan] 结果实现类
         *
         * 可在这里处理是否成功的回调
         */
        inner class Result internal constructor() {

            /** 找到结果时的回调 */
            internal var onFindCallback: (MutableList<Field>.() -> Unit)? = null

            /**
             * 当找到结果时
             * @param initiate 回调
             */
            fun onFind(initiate: MutableList<Field>.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Field] 查找结果实现类
     *
     * @param isNoSuch 是否没有找到 [Field] - 默认否
     * @param throwable 错误信息
     */
    inner class Result internal constructor(
        internal val isNoSuch: Boolean = false,
        internal val throwable: Throwable? = null
    ) : BaseResult {

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 获得 [Field] 实例处理类
         *
         * - 若有多个 [Field] 结果只会返回第一个
         *
         * - 在 [memberInstances] 结果为空时使用此方法将无法获得对象
         *
         * - 如果目标对象不是静态 - 你必须设置 [instance]
         *
         * - 若你设置了 [remedys] 请使用 [wait] 回调结果方法
         * @param instance [Field] 所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [Instance]
         */
        fun get(instance: Any? = null) = Instance(instance, give())

        /**
         * 获得 [Field] 实例处理类数组
         *
         * - 返回全部查找条件匹配的多个 [Field] 实例结果
         *
         * - 在 [memberInstances] 结果为空时使用此方法将无法获得对象
         *
         * - 如果目标对象不是静态 - 你必须设置 [instance]
         *
         * - 若你设置了 [remedys] 请使用 [waitAll] 回调结果方法
         * @param instance [Field] 所在的实例对象 - 如果是静态可不填 - 默认 null
         * @return [MutableList]<[Instance]>
         */
        fun all(instance: Any? = null) =
            mutableListOf<Instance>().apply { giveAll().takeIf { it.isNotEmpty() }?.forEach { add(Instance(instance, it)) } }

        /**
         * 得到 [Field] 本身
         *
         * - 若有多个 [Field] 结果只会返回第一个
         *
         * - 在查找条件找不到任何结果的时候将返回 null
         * @return [Field] or null
         */
        fun give() = giveAll().takeIf { it.isNotEmpty() }?.first()

        /**
         * 得到 [Field] 本身数组
         *
         * - 返回全部查找条件匹配的多个 [Field] 实例
         *
         * - 在查找条件找不到任何结果的时候将返回空的 [MutableList]
         * @return [MutableList]<[Field]>
         */
        fun giveAll() = memberInstances.takeIf { it.isNotEmpty() }?.fields() ?: mutableListOf()

        /**
         * 获得 [Field] 实例处理类
         *
         * - 若有多个 [Field] 结果只会返回第一个
         *
         * - 若你设置了 [remedys] 必须使用此方法才能获得结果
         *
         * - 若你没有设置 [remedys] 此方法将不会被回调
         * @param instance 所在实例
         * @param initiate 回调 [Instance]
         */
        fun wait(instance: Any? = null, initiate: Instance.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(get(instance))
            else remedyPlansCallback = { initiate(get(instance)) }
        }

        /**
         * 获得 [Field] 实例处理类数组
         *
         * - 返回全部查找条件匹配的多个 [Field] 实例结果
         *
         * - 若你设置了 [remedys] 必须使用此方法才能获得结果
         *
         * - 若你没有设置 [remedys] 此方法将不会被回调
         * @param instance 所在实例
         * @param initiate 回调 [MutableList]<[Instance]>
         */
        fun waitAll(instance: Any? = null, initiate: MutableList<Instance>.() -> Unit) {
            if (memberInstances.isNotEmpty()) initiate(all(instance))
            else remedyPlansCallback = { initiate(all(instance)) }
        }

        /**
         * 创建 [Field] 重查找功能
         *
         * 当你遇到一种方法可能存在不同形式的存在时
         *
         * 可以使用 [RemedyPlan] 重新查找它 - 而没有必要使用 [onNoSuchField] 捕获异常二次查找 [Field]
         *
         * 若第一次查找失败了 - 你还可以在这里继续添加此方法体直到成功为止
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun remedys(initiate: RemedyPlan.() -> Unit): Result {
            isUsingRemedyPlan = true
            if (isNoSuch) RemedyPlan().apply(initiate).build()
            return this
        }

        /**
         * 监听找不到 [Field] 时
         *
         * - 只会返回第一次的错误信息 - 不会返回 [RemedyPlan] 的错误信息
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoSuchField(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(throwable ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * 忽略异常并停止打印任何错误日志
         *
         * - 若 [MemberBaseFinder.MemberHookerManager.isNotIgnoredNoSuchMemberFailure] 为 false 则自动忽略
         *
         * - 此时若要监听异常结果 - 你需要手动实现 [onNoSuchField] 方法
         * @return [Result] 可继续向下监听
         */
        fun ignored(): Result {
            isIgnoreErrorLogs = true
            return this
        }

        /**
         * 忽略异常并停止打印任何错误日志
         *
         * - 此方法已弃用 - 在之后的版本中将直接被删除
         *
         * - 请现在迁移到 [ignored]
         * @return [Result] 可继续向下监听
         */
        @Deprecated(message = "请使用新的命名方法", ReplaceWith("ignored()"))
        fun ignoredError() = ignored()

        /**
         * [Field] 实例处理类
         *
         * - 请使用 [get]、[all] 方法来获取 [Instance]
         * @param instance 当前 [Field] 所在类的实例对象
         * @param field 当前 [Field] 实例对象
         */
        inner class Instance internal constructor(private val instance: Any?, private val field: Field?) {

            /**
             * 获取当前 [Field] 自身的实例化对象
             *
             * - 若要直接获取不确定的实例对象 - 请调用 [any] 方法
             * @return [Any] or null
             */
            private val self get() = field?.get(instance)

            /**
             * 获得当前 [Field] 自身 [self] 实例的类操作对象
             * @param ignored 是否开启忽略错误警告功能 - 默认否
             * @return [CurrentClass] or null
             */
            fun current(ignored: Boolean = false) = self?.current(ignored)

            /**
             * 获得当前 [Field] 自身 [self] 实例的类操作对象
             * @param ignored 是否开启忽略错误警告功能 - 默认否
             * @param initiate 方法体
             * @return [Any] or null
             */
            inline fun current(ignored: Boolean = false, initiate: CurrentClass.() -> Unit) = self?.current(ignored, initiate)

            /**
             * 得到当前 [Field] 实例
             * @return [T] or null
             */
            fun <T> cast() = self as? T?

            /**
             * 得到当前 [Field] 的 [Byte] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回 null
             * @return [Byte] or null
             */
            fun byte() = cast<Byte?>()

            /**
             * 得到当前 [Field] 的 [Int] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Int] 取不到返回 0
             */
            fun int() = cast() ?: 0

            /**
             * 得到当前 [Field] 的 [Long] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Long] 取不到返回 0L
             */
            fun long() = cast() ?: 0L

            /**
             * 得到当前 [Field] 的 [Short] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Short] 取不到返回 0
             */
            fun short() = cast<Short?>() ?: 0

            /**
             * 得到当前 [Field] 的 [Double] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Double] 取不到返回 0.0
             */
            fun double() = cast() ?: 0.0

            /**
             * 得到当前 [Field] 的 [Float] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Float] 取不到返回 0f
             */
            fun float() = cast() ?: 0f

            /**
             * 得到当前 [Field] 的 [String] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [String] 取不到返回 ""
             */
            fun string() = cast() ?: ""

            /**
             * 得到当前 [Field] 的 [Char] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Char] 取不到返回 ' '
             */
            fun char() = cast() ?: ' '

            /**
             * 得到当前 [Field] 的 [Boolean] 实例
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回默认值
             * @return [Boolean] 取不到返回 false
             */
            fun boolean() = cast() ?: false

            /**
             * 得到当前 [Field] 的 [Any] 实例
             * @return [Any] or null
             */
            fun any() = self

            /**
             * 得到当前 [Field] 的 [Array] 实例 - 每项类型 [T]
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回空数组
             * @return [Array] 取不到返回空数组
             */
            inline fun <reified T> array() = cast() ?: arrayOf<T>()

            /**
             * 得到当前 [Field] 的 [List] 实例 - 每项类型 [T]
             *
             * - 请确认目标 [Field] 的类型 - 发生错误会返回空数组
             * @return [List] 取不到返回空数组
             */
            inline fun <reified T> list() = cast() ?: listOf<T>()

            /**
             * 设置当前 [Field] 实例
             * @param any 设置的实例内容
             */
            fun set(any: Any?) = field?.set(instance, any)

            /**
             * 设置当前 [Field] 实例为 true
             *
             * - 请确保示例对象类型为 [Boolean]
             */
            fun setTrue() = set(true)

            /**
             * 设置当前 [Field] 实例为 true
             *
             * - 请确保示例对象类型为 [Boolean]
             */
            fun setFalse() = set(false)

            /** 设置当前 [Field] 实例为 null */
            fun setNull() = set(null)

            override fun toString() =
                "[${self?.javaClass?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}] value \"$self\""
        }
    }
}