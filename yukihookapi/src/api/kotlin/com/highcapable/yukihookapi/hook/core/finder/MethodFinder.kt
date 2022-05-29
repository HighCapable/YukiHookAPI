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
 * This file is Created by fankes on 2022/2/4.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "UNCHECKED_CAST", "KotlinConstantConditions")

package com.highcapable.yukihookapi.hook.core.finder

import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.bean.VariousClass
import com.highcapable.yukihookapi.hook.core.YukiMemberHookCreater
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.type.ModifierRules
import com.highcapable.yukihookapi.hook.core.finder.type.NameConditions
import com.highcapable.yukihookapi.hook.core.reflex.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.factory.hasExtends
import com.highcapable.yukihookapi.hook.log.yLoggerW
import com.highcapable.yukihookapi.hook.type.defined.UndefinedType
import com.highcapable.yukihookapi.hook.utils.runBlocking
import java.lang.reflect.Method

/**
 * [Method] 查找类
 *
 * 可通过指定类型查找指定方法
 * @param hookInstance 当前 Hook 实例 - 填写后将自动设置 [YukiMemberHookCreater.MemberHookCreater.member]
 * @param classSet 当前需要查找的 [Class] 实例
 */
class MethodFinder @PublishedApi internal constructor(
    @property:YukiPrivateApi
    override val hookInstance: YukiMemberHookCreater.MemberHookCreater? = null,
    @property:YukiPrivateApi
    override val classSet: Class<*>? = null
) : BaseFinder(tag = "Method", hookInstance, classSet) {

    /** 当前使用的 [classSet] */
    private var usedClassSet = classSet

    /** 是否在未找到后继续在当前 [classSet] 的父类中查找 */
    private var isFindInSuperClass = false

    /** 当前重查找结果回调 */
    private var remedyPlansCallback: (() -> Unit)? = null

    /** [Method] 参数数组 */
    private var paramTypes: Array<out Class<*>>? = null

    /** [ModifierRules] 实例 */
    @PublishedApi
    internal var modifiers: ModifierRules? = null

    /** [NameConditions] 实例 */
    @PublishedApi
    internal var nameConditions: NameConditions? = null

    /**
     * 设置 [Method] 名称
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Method]
     */
    var name = ""

    /**
     * 设置 [Method] 参数个数
     *
     * 你可以不使用 [param] 指定参数类型而是仅使用此变量指定参数个数
     *
     * 若参数个数小于零则忽略并使用 [param]
     */
    var paramCount = -1

    /**
     * [Method] 返回值
     *
     * - ❗只能是 [Class]、[String]、[VariousClass]
     *
     * - 可不填写返回值 - 默认模糊查找并取第一个匹配的 [Method]
     */
    var returnType: Any? = null

    /**
     * 设置 [Method] 标识符筛选条件
     *
     * 可不设置筛选条件 - 默认模糊查找并取第一个匹配的 [Method]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param initiate 方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    inline fun modifiers(initiate: ModifierRules.() -> Unit): IndexTypeCondition {
        modifiers = ModifierRules().apply(initiate)
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Method] 空参数、无参数
     *
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun emptyParam() = paramCount(num = 0)

    /**
     * 设置 [Method] 参数
     *
     * 如果同时使用了 [paramCount] 则 [paramTypes] 的数量必须与 [paramCount] 完全匹配
     *
     * - ❗无参 [Method] 请使用 [emptyParam] 设置查询条件
     *
     * - ❗有参 [Method] 必须使用此方法设定参数或使用 [paramCount] 指定个数
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param paramType 参数类型数组 - ❗只能是 [Class]、[String]、[VariousClass]
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun param(vararg paramType: Any): IndexTypeCondition {
        if (paramType.isEmpty()) error("paramTypes is empty, please use emptyParam() instead")
        paramTypes = ArrayList<Class<*>>().apply { paramType.forEach { add(it.compat() ?: UndefinedType) } }.toTypedArray()
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 顺序筛选字节码的下标
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun order() = IndexTypeCondition(IndexConfigType.ORDER)

    /**
     * 设置 [Method] 名称
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Method]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 名称
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun name(value: String): IndexTypeCondition {
        name = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Method] 名称条件
     *
     * - ❗若不填写名称则必须存在一个其它条件 - 默认模糊查找并取第一个匹配的 [Method]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param initiate 方法体
     * @return [BaseFinder.IndexTypeCondition]
     */
    inline fun name(initiate: NameConditions.() -> Unit): IndexTypeCondition {
        nameConditions = NameConditions().apply(initiate)
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Method] 参数个数
     *
     * 你可以不使用 [param] 指定参数类型而是仅使用此方法指定参数个数
     *
     * 若参数个数小于零则忽略并使用 [param]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param num 个数
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun paramCount(num: Int): IndexTypeCondition {
        paramCount = num
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置 [Method] 返回值
     *
     * 可不填写返回值 - 默认模糊查找并取第一个匹配的 [Method]
     *
     * - ❗存在多个 [BaseFinder.IndexTypeCondition] 时除了 [order] 只会生效最后一个
     * @param value 个数
     * @return [BaseFinder.IndexTypeCondition]
     */
    fun returnType(value: Any): IndexTypeCondition {
        returnType = value
        return IndexTypeCondition(IndexConfigType.MATCH)
    }

    /**
     * 设置在 [classSet] 的所有父类中查找当前 [Method]
     *
     * - ❗若当前 [classSet] 的父类较多可能会耗时 - API 会自动循环到父类继承是 [Any] 前的最后一个类
     * @param isOnlySuperClass 是否仅在当前 [classSet] 的父类中查找 - 若父类是 [Any] 则不会生效
     */
    fun superClass(isOnlySuperClass: Boolean = false) {
        isFindInSuperClass = true
        if (isOnlySuperClass && classSet?.hasExtends == true) usedClassSet = classSet.superclass
    }

    /**
     * 得到方法
     * @return [Method]
     * @throws NoSuchMethodError 如果找不到方法
     */
    private val result
        get() = ReflectionTool.findMethod(
            usedClassSet, orderIndex, matchIndex, name,
            modifiers, nameConditions, returnType.compat(),
            paramCount, paramTypes, isFindInSuperClass
        )

    /**
     * 设置实例
     * @param isBind 是否将结果设置到目标 [YukiMemberHookCreater.MemberHookCreater]
     * @param method 当前找到的 [Method]
     */
    private fun setInstance(isBind: Boolean, method: Method) {
        memberInstance = method
        if (isBind) hookInstance?.member = method
    }

    /**
     * 得到方法结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param isBind 是否将结果设置到目标 [YukiMemberHookCreater.MemberHookCreater]
     * @return [Result]
     */
    @YukiPrivateApi
    override fun build(isBind: Boolean) = try {
        if (classSet != null) {
            runBlocking {
                isBindToHooker = isBind
                setInstance(isBind, result)
            }.result { onHookLogMsg(msg = "Find Method [${memberInstance}] takes ${it}ms [${hookTag}]") }
            Result()
        } else Result(isNoSuch = true, Throwable("classSet is null"))
    } catch (e: Throwable) {
        onFailureMsg(throwable = e)
        Result(isNoSuch = true, e)
    }

    /**
     * 创建一个异常结果
     *
     * - ❗此功能交由方法体自动完成 - 你不应该手动调用此方法
     * @param throwable 异常
     * @return [Result]
     */
    @YukiPrivateApi
    override fun failure(throwable: Throwable?) = Result(isNoSuch = true, throwable)

    /**
     * [Method] 重查找实现类
     *
     * 可累计失败次数直到查找成功
     */
    inner class RemedyPlan @PublishedApi internal constructor() {

        /** 失败尝试次数数组 */
        @PublishedApi
        internal val remedyPlans = HashSet<Pair<MethodFinder, Result>>()

        /**
         * 创建需要重新查找的 [Method]
         *
         * 你可以添加多个备选方法 - 直到成功为止
         *
         * 若最后依然失败 - 将停止查找并输出错误日志
         * @param initiate 方法体
         * @return [Result] 结果
         */
        inline fun method(initiate: MethodFinder.() -> Unit) =
            Result().apply { remedyPlans.add(Pair(MethodFinder(hookInstance, classSet).apply(initiate), this)) }

        /** 开始重查找 */
        @PublishedApi
        internal fun build() {
            if (classSet == null) return
            if (remedyPlans.isNotEmpty()) run {
                var isFindSuccess = false
                var lastError: Throwable? = null
                remedyPlans.forEachIndexed { p, it ->
                    runCatching {
                        runBlocking {
                            setInstance(isBindToHooker, it.first.result)
                        }.result {
                            onHookLogMsg(msg = "Find Method [${memberInstance}] takes ${it}ms [${hookTag}]")
                        }
                        isFindSuccess = true
                        it.second.onFindCallback?.invoke(memberInstance as Method)
                        remedyPlansCallback?.invoke()
                        onHookLogMsg(msg = "Method [${memberInstance}] trying ${p + 1} times success by RemedyPlan [${hookTag}]")
                        return@run
                    }.onFailure {
                        lastError = it
                        onFailureMsg(msg = "Trying ${p + 1} times by RemedyPlan --> $it", isAlwaysPrint = true)
                    }
                }
                if (isFindSuccess.not()) {
                    onFailureMsg(
                        msg = "Trying ${remedyPlans.size} times and all failure by RemedyPlan",
                        throwable = lastError,
                        isAlwaysPrint = true
                    )
                    remedyPlans.clear()
                }
            } else yLoggerW(msg = "RemedyPlan is empty, forgot it? [${hookTag}]")
        }

        /**
         * [RemedyPlan] 结果实现类
         *
         * 可在这里处理是否成功的回调
         */
        inner class Result @PublishedApi internal constructor() {

            /** 找到结果时的回调 */
            internal var onFindCallback: (Method.() -> Unit)? = null

            /**
             * 当找到结果时
             * @param initiate 回调
             */
            fun onFind(initiate: Method.() -> Unit) {
                onFindCallback = initiate
            }
        }
    }

    /**
     * [Method] 查找结果实现类
     * @param isNoSuch 是否没有找到方法 - 默认否
     * @param e 错误信息
     */
    inner class Result internal constructor(
        @PublishedApi internal val isNoSuch: Boolean = false,
        @PublishedApi internal val e: Throwable? = null
    ) {

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 获得 [Method] 实例处理类
         *
         * - ❗在 [memberInstance] 结果为空时使用此方法将无法获得对象
         *
         * - ❗若你设置了 [remedys] 请使用 [wait] 回调结果方法
         * @param instance 所在实例
         * @return [Instance]
         */
        fun get(instance: Any? = null) = Instance(instance)

        /**
         * 得到方法本身
         * @return [Method] or null
         */
        fun give() = memberInstance as? Method?

        /**
         * 获得 [Method] 实例处理类
         *
         * - ❗若你设置了 [remedys] 必须使用此方法才能获得结果
         *
         * - ❗若你没有设置 [remedys] 此方法将不会被回调
         * @param instance 所在实例
         * @param initiate 回调 [Instance]
         */
        fun wait(instance: Any? = null, initiate: Instance.() -> Unit) {
            if (memberInstance != null) initiate(get(instance))
            else remedyPlansCallback = { initiate(get(instance)) }
        }

        /**
         * 创建方法重查找功能
         *
         * 当你遇到一种方法可能存在不同形式的存在时
         *
         * 可以使用 [RemedyPlan] 重新查找它 - 而没有必要使用 [onNoSuchMethod] 捕获异常二次查找方法
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
         * 监听找不到方法时
         *
         * 只会返回第一次的错误信息 - 不会返回 [RemedyPlan] 的错误信息
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        inline fun onNoSuchMethod(result: (Throwable) -> Unit): Result {
            if (isNoSuch) result(e ?: Throwable("Initialization Error"))
            return this
        }

        /**
         * 忽略任何错误发出的警告
         *
         * - 若 [isNotIgnoredNoSuchMemberFailure] 为 false 则自动忽略
         * @return [Result] 可继续向下监听
         */
        fun ignoredError(): Result {
            isShutErrorPrinting = true
            return this
        }

        /**
         * [Method] 实例处理类
         *
         * - ❗请使用 [get] 或 [wait] 方法来获取 [Instance]
         * @param instance 当前 [Method] 所在类的实例对象
         */
        inner class Instance internal constructor(private val instance: Any?) {

            /**
             * 执行方法
             * @param param 方法参数
             * @return [Any] or null
             */
            private fun baseCall(vararg param: Any?) =
                if (param.isNotEmpty())
                    (memberInstance as? Method?)?.invoke(instance, *param)
                else (memberInstance as? Method?)?.invoke(instance)

            /**
             * 执行方法 - 不指定返回值类型
             * @param param 方法参数
             * @return [Any] or null
             */
            fun call(vararg param: Any?) = baseCall(*param)

            /**
             * 执行方法 - 指定 [T] 返回值类型
             * @param param 方法参数
             * @return [T] or null
             */
            fun <T> invoke(vararg param: Any?) = baseCall(*param) as? T?

            /**
             * 执行方法 - 指定 [Byte] 返回值类型
             *
             * - ❗请确认目标变量的类型 - 发生错误会返回 null
             * @param param 方法参数
             * @return [Byte] or null
             */
            fun byte(vararg param: Any?) = invoke<Byte?>(*param)

            /**
             * 执行方法 - 指定 [Int] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Int] 取不到返回 0
             */
            fun int(vararg param: Any?) = invoke(*param) ?: 0

            /**
             * 执行方法 - 指定 [Long] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Long] 取不到返回 0L
             */
            fun long(vararg param: Any?) = invoke(*param) ?: 0L

            /**
             * 执行方法 - 指定 [Short] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Short] 取不到返回 0
             */
            fun short(vararg param: Any?) = invoke<Short?>(*param) ?: 0

            /**
             * 执行方法 - 指定 [Double] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Double] 取不到返回 0.0
             */
            fun double(vararg param: Any?) = invoke(*param) ?: 0.0

            /**
             * 执行方法 - 指定 [Float] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Float] 取不到返回 0f
             */
            fun float(vararg param: Any?) = invoke(*param) ?: 0f

            /**
             * 执行方法 - 指定 [String] 返回值类型
             * @param param 方法参数
             * @return [String] 取不到返回 ""
             */
            fun string(vararg param: Any?) = invoke(*param) ?: ""

            /**
             * 执行方法 - 指定 [Char] 返回值类型
             * @param param 方法参数
             * @return [Char] 取不到返回 ' '
             */
            fun char(vararg param: Any?) = invoke(*param) ?: ' '

            /**
             * 执行方法 - 指定 [Boolean] 返回值类型
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回默认值
             * @param param 方法参数
             * @return [Boolean] 取不到返回 false
             */
            fun boolean(vararg param: Any?) = invoke(*param) ?: false

            /**
             * 执行方法 - 指定 [Array] 返回值类型 - 每项类型 [T]
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回空数组
             * @return [Array] 取不到返回空数组
             */
            inline fun <reified T> array(vararg param: Any?) = invoke(*param) ?: arrayOf<T>()

            /**
             * 执行方法 - 指定 [List] 返回值类型 - 每项类型 [T]
             *
             * - ❗请确认目标方法的返回值 - 发生错误会返回空数组
             * @return [List] 取不到返回空数组
             */
            inline fun <reified T> list(vararg param: Any?) = invoke(*param) ?: listOf<T>()

            override fun toString() =
                "[${(memberInstance as? Method?)?.name ?: "<empty>"}] in [${instance?.javaClass?.name ?: "<empty>"}]"
        }
    }
}