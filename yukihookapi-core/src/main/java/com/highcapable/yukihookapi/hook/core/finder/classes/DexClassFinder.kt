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
 * This file is created by fankes on 2022/9/4.
 */
@file:Suppress("unused", "MemberVisibilityCanBePrivate", "NON_PUBLIC_CALL_FROM_PUBLIC_INLINE")

package com.highcapable.yukihookapi.hook.core.finder.classes

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.core.content.pm.PackageInfoCompat
import com.highcapable.yukihookapi.hook.core.finder.base.ClassBaseFinder
import com.highcapable.yukihookapi.hook.core.finder.classes.data.ClassRulesData
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.ConstructorRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.FieldRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MemberRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.MethodRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.base.BaseRules
import com.highcapable.yukihookapi.hook.core.finder.classes.rules.result.MemberRulesResult
import com.highcapable.yukihookapi.hook.core.finder.tools.ReflectionTool
import com.highcapable.yukihookapi.hook.core.finder.type.factory.ModifierConditions
import com.highcapable.yukihookapi.hook.core.finder.type.factory.NameConditions
import com.highcapable.yukihookapi.hook.factory.hasClass
import com.highcapable.yukihookapi.hook.factory.searchClass
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.log.YLog
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.utils.factory.await
import com.highcapable.yukihookapi.hook.utils.factory.runBlocking
import com.highcapable.yukihookapi.hook.xposed.parasitic.AppParasitics
import dalvik.system.BaseDexClassLoader
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * [Class] 查找类
 *
 * 可使用 [BaseDexClassLoader] 通过指定条件查找指定 [Class] 或一组 [Class]
 *
 * - 此功能尚在实验阶段 - 性能与稳定性可能仍然存在问题 - 使用过程遇到问题请向我们报告并帮助我们改进
 * @param name 标识当前 [Class] 缓存的名称 - 不设置将不启用缓存 - 启用缓存必须启用 [async]
 * @param async 是否启用异步
 * @param loaderSet 当前使用的 [ClassLoader] 实例
 */
class DexClassFinder internal constructor(
    internal var name: String,
    internal var async: Boolean,
    override val loaderSet: ClassLoader?
) : ClassBaseFinder(loaderSet) {

    companion object {

        /** 缓存的存储文件名 */
        private const val CACHE_FILE_NAME = "config_yukihook_cache_obfuscate_classes"

        /**
         * 获取当前运行环境的 [Context]
         * @return [Context] or null
         */
        private val currentContext get() = AppParasitics.hostApplication ?: AppParasitics.currentApplication

        /**
         * 通过 [Context] 获取当前 [SharedPreferences]
         * @param versionName 版本名称 - 默认空
         * @param versionCode 版本号 - 默认空
         * @return [SharedPreferences]
         */
        private fun Context.currentSp(versionName: String? = null, versionCode: Long? = null) =
            @Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")
            getSharedPreferences(packageManager?.getPackageInfo(packageName, PackageManager.GET_META_DATA)
                ?.let { "${CACHE_FILE_NAME}_${versionName ?: it.versionName}_${versionCode ?: PackageInfoCompat.getLongVersionCode(it)}" }
                ?: "${CACHE_FILE_NAME}_unknown",
                Context.MODE_PRIVATE)

        /**
         * 清除当前 [DexClassFinder] 的 [Class] 缓存
         *
         * 适用于全部通过 [ClassLoader.searchClass] or [PackageParam.searchClass] 获取的 [DexClassFinder]
         * @param context 当前 [Context] - 不填默认获取 [currentContext]
         * @param versionName 版本名称 - 默认空
         * @param versionCode 版本号 - 默认空
         */
        fun clearCache(context: Context? = currentContext, versionName: String? = null, versionCode: Long? = null) {
            context?.currentSp(versionName, versionCode)?.edit()?.clear()?.apply()
                ?: YLog.innerW("Cannot clear cache for DexClassFinder because got null context instance")
        }
    }

    override var rulesData = ClassRulesData()

    /**
     * 设置 [Class] 完整名称
     *
     * 只会查找匹配到的 [Class.getName]
     *
     * 例如 com.demo.Test 需要填写 com.demo.Test
     * @return [String]
     */
    var fullName
        get() = rulesData.fullName?.name ?: ""
        set(value) {
            rulesData.fullName = rulesData.createNameRulesData(value)
        }

    /**
     * 设置 [Class] 简单名称
     *
     * 只会查找匹配到的 [Class.getSimpleName]
     *
     * 例如 com.demo.Test 只需要填写 Test
     *
     * 对于匿名类例如 com.demo.Test$InnerTest 会为空 - 此时你可以使用 [singleName]
     * @return [String]
     */
    var simpleName
        get() = rulesData.simpleName?.name ?: ""
        set(value) {
            rulesData.simpleName = rulesData.createNameRulesData(value)
        }

    /**
     * 设置 [Class] 独立名称
     *
     * 设置后将首先使用 [Class.getSimpleName] - 若为空则会使用 [Class.getName] 进行处理
     *
     * 例如 com.demo.Test 只需要填写 Test
     *
     * 对于匿名类例如 com.demo.Test$InnerTest 只需要填写 Test$InnerTest
     * @return [String]
     */
    var singleName
        get() = rulesData.singleName?.name ?: ""
        set(value) {
            rulesData.singleName = rulesData.createNameRulesData(value)
        }

    /**
     * 设置在指定包名范围查找当前 [Class]
     *
     * 设置后仅会在当前 [name] 开头匹配的包名路径下进行查找 - 可提升查找速度
     *
     * 例如 ↓
     *
     * com.demo.test
     *
     * com.demo.test.demo
     *
     * - 建议设置此参数指定查找范围 - 否则 [Class] 过多时将会非常慢
     * @param name 指定包名
     * @return [FromPackageRules] 可设置 [FromPackageRules.absolute] 标识包名绝对匹配
     */
    fun from(vararg name: String) = FromPackageRules(mutableListOf<ClassRulesData.PackageRulesData>().also {
        name.takeIf { e -> e.isNotEmpty() }?.forEach { e -> it.add(rulesData.createPackageRulesData(e)) }
        if (it.isNotEmpty()) rulesData.fromPackages.addAll(it)
    })

    /**
     * 设置 [Class] 标识符筛选条件
     *
     * - 可不设置筛选条件
     * @param conditions 条件方法体
     */
    fun modifiers(conditions: ModifierConditions) {
        rulesData.modifiers = conditions
    }

    /**
     * 设置 [Class] 完整名称
     *
     * 只会查找匹配到的 [Class.getName]
     *
     * 例如 com.demo.Test 需要填写 com.demo.Test
     * @param value 名称
     * @return [ClassNameRules] 可设置 [ClassNameRules.optional] 标识类名可选
     */
    fun fullName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.fullName = it
        ClassNameRules(it)
    }

    /**
     * 设置 [Class] 简单名称
     *
     * 只会查找匹配到的 [Class.getSimpleName]
     *
     * 例如 com.demo.Test 只需要填写 Test
     *
     * 对于匿名类例如 com.demo.Test$InnerTest 会为空 - 此时你可以使用 [singleName]
     * @param value 名称
     * @return [ClassNameRules] 可设置 [ClassNameRules.optional] 标识类名可选
     */
    fun simpleName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.simpleName = it
        ClassNameRules(it)
    }

    /**
     * 设置 [Class] 独立名称
     *
     * 设置后将首先使用 [Class.getSimpleName] - 若为空则会使用 [Class.getName] 进行处理
     *
     * 例如 com.demo.Test 只需要填写 Test
     *
     * 对于匿名类例如 com.demo.Test$InnerTest 只需要填写 Test$InnerTest
     * @param value 名称
     * @return [ClassNameRules] 可设置 [ClassNameRules.optional] 标识类名可选
     */
    fun singleName(value: String) = rulesData.createNameRulesData(value).let {
        rulesData.singleName = it
        ClassNameRules(it)
    }

    /**
     * 设置 [Class] 完整名称条件
     *
     * 只会查找匹配到的 [Class.getName]
     * @param conditions 条件方法体
     */
    fun fullName(conditions: NameConditions) {
        rulesData.fullNameConditions = conditions
    }

    /**
     * 设置 [Class] 简单名称条件
     *
     * 只会查找匹配到的 [Class.getSimpleName]
     * @param conditions 条件方法体
     */
    fun simpleName(conditions: NameConditions) {
        rulesData.simpleNameConditions = conditions
    }

    /**
     * 设置 [Class] 独立名称条件
     *
     * 设置后将首先使用 [Class.getSimpleName] - 若为空则会使用 [Class.getName] 进行处理
     * @param conditions 条件方法体
     */
    fun singleName(conditions: NameConditions) {
        rulesData.singleNameConditions = conditions
    }

    /** 设置 [Class] 继承的父类 */
    inline fun <reified T> extends() {
        rulesData.extendsClass.add(T::class.java.name)
    }

    /**
     * 设置 [Class] 继承的父类
     *
     * 会同时查找 [name] 中所有匹配的父类
     * @param name [Class] 完整名称
     */
    fun extends(vararg name: String) {
        rulesData.extendsClass.addAll(name.toList())
    }

    /** 设置 [Class] 实现的接口类 */
    inline fun <reified T> implements() {
        rulesData.implementsClass.add(T::class.java.name)
    }

    /**
     * 设置 [Class] 实现的接口类
     *
     * 会同时查找 [name] 中所有匹配的接口类
     * @param name [Class] 完整名称
     */
    fun implements(vararg name: String) {
        rulesData.implementsClass.addAll(name.toList())
    }

    /**
     * 标识 [Class] 为匿名类
     *
     * 例如 com.demo.Test$1 或 com.demo.Test$InnerTest
     *
     * 标识后你可以使用 [enclosing] 来进一步指定匿名类的 (封闭类) 主类
     */
    fun anonymous() {
        rulesData.isAnonymousClass = true
    }

    /**
     * 设置 [Class] 没有任何继承
     *
     * 此时 [Class] 只应该继承于 [Any]
     *
     * - 设置此条件后 [extends] 将失效
     */
    fun noExtends() {
        rulesData.isNoExtendsClass = true
    }

    /**
     * 设置 [Class] 没有任何接口
     *
     * - 设置此条件后 [implements] 将失效
     */
    fun noImplements() {
        rulesData.isNoImplementsClass = true
    }

    /**
     * 设置 [Class] 没有任何继承与接口
     *
     * 此时 [Class] 只应该继承于 [Any]
     *
     * - 设置此条件后 [extends] 与 [implements] 将失效
     */
    fun noSuper() {
        noExtends()
        noImplements()
    }

    /** 设置 [Class] 匿名类的 (封闭类) 主类 */
    inline fun <reified T> enclosing() {
        rulesData.enclosingClass.add(T::class.java.name)
    }

    /**
     * 设置 [Class] 匿名类的 (封闭类) 主类
     *
     * 会同时查找 [name] 中所有匹配的 (封闭类) 主类
     * @param name [Class] 完整名称
     */
    fun enclosing(vararg name: String) {
        rulesData.enclosingClass.addAll(name.toList())
    }

    /**
     * 包名范围名称过滤匹配条件实现类
     * @param packages 包名数组
     */
    inner class FromPackageRules internal constructor(private val packages: MutableList<ClassRulesData.PackageRulesData>) {

        /**
         * 设置包名绝对匹配
         *
         * 例如有如下包名 ↓
         *
         * com.demo.test.a
         *
         * com.demo.test.a.b
         *
         * com.demo.test.active
         *
         * 若包名条件为 "com.demo.test.a" 则绝对匹配仅能匹配到第一个
         *
         * 相反地 - 不设置以上示例会全部匹配
         */
        fun absolute() = packages.takeIf { it.isNotEmpty() }?.forEach { it.isAbsolute = true }
    }

    /**
     * 类名匹配条件实现类
     * @param name 类名匹配实例
     */
    inner class ClassNameRules internal constructor(private val name: ClassRulesData.NameRulesData) {

        /**
         * 设置类名可选
         *
         * 例如有如下类名 ↓
         *
         * com.demo.Test (fullName) / Test (simpleName)
         *
         * defpackage.a (fullName) / a (simpleName)
         *
         * 这两个类名都是同一个类 - 但是在有些版本中被混淆有些版本没有
         *
         * 此时可设置类名为 "com.demo.Test" (fullName) / "Test" (simpleName)
         *
         * 这样就可在完全匹配类名情况下使用类名而忽略其它查找条件 - 否则忽略此条件继续使用其它查找条件
         */
        fun optional() {
            name.isOptional = true
        }
    }

    /**
     * 设置 [Class] 满足的 [Member] 条件
     * @param initiate 条件方法体
     * @return [MemberRulesResult]
     */
    inline fun member(initiate: MemberRules.() -> Unit = {}) = BaseRules.createMemberRules(this).apply(initiate).build()

    /**
     * 设置 [Class] 满足的 [Field] 条件
     * @param initiate 条件方法体
     * @return [MemberRulesResult]
     */
    inline fun field(initiate: FieldRules.() -> Unit = {}) = BaseRules.createFieldRules(this).apply(initiate).build()

    /**
     * 设置 [Class] 满足的 [Method] 条件
     * @param initiate 条件方法体
     * @return [MemberRulesResult]
     */
    inline fun method(initiate: MethodRules.() -> Unit = {}) = BaseRules.createMethodRules(this).apply(initiate).build()

    /**
     * 设置 [Class] 满足的 [Constructor] 条件
     * @param initiate 查找方法体
     * @return [MemberRulesResult]
     */
    inline fun constructor(initiate: ConstructorRules.() -> Unit = {}) = BaseRules.createConstructorRules(this).apply(initiate).build()

    /**
     * 得到 [Class] 或一组 [Class]
     * @return [MutableList]<[Class]>
     * @throws NoClassDefFoundError 如果找不到 [Class]
     */
    private val result get() = ReflectionTool.findClasses(loaderSet, rulesData)

    /**
     * 从本地缓存读取 [Class] 数据
     * @return [MutableList]<[Class]>
     */
    private fun readFromCache(): MutableList<Class<*>> =
        if (async && name.isNotBlank()) currentContext?.let {
            mutableListOf<Class<*>>().also { classes ->
                it.currentSp().getStringSet(name, emptySet())?.takeIf { it.isNotEmpty() }
                    ?.forEach { className -> if (className.hasClass(loaderSet)) classes.add(className.toClass(loaderSet)) }
            }
        } ?: let { SystemClock.sleep(1); readFromCache() } else mutableListOf()

    /**
     * 将当前 [Class] 数组名称保存到本地缓存
     * @throws IllegalStateException 如果当前包名为 "android"
     */
    private fun MutableList<Class<*>>.saveToCache() {
        if (name.isNotBlank() && isNotEmpty()) mutableSetOf<String>().also { names ->
            takeIf { it.isNotEmpty() }?.forEach { names.add(it.name) }
            currentContext?.also {
                if (it.packageName == "android") error("Cannot create classes cache for \"android\", please remove \"name\" param")
                it.currentSp().edit().apply { putStringSet(name, names) }.apply()
            }
        }
    }

    /**
     * 设置实例
     * @param classes 当前找到的 [Class] 数组
     */
    private fun setInstance(classes: MutableList<Class<*>>) {
        classInstances.clear()
        classes.takeIf { it.isNotEmpty() }?.forEach { classInstances.add(it) }
    }

    override fun build() = runCatching {
        if (loaderSet != null) {
            /** 开始任务 */
            fun startProcess() {
                runBlocking {
                    setInstance(readFromCache().takeIf { it.isNotEmpty() } ?: result)
                }.result { ms -> classInstances.takeIf { it.isNotEmpty() }?.forEach { debugMsg(msg = "Find Class [$it] takes ${ms}ms") } }
            }
            Result().also { e ->
                if (async) e.await {
                    runCatching {
                        startProcess()
                        it.waitResultCallback?.invoke(it.get())
                        it.waitAllResultCallback?.invoke(it.all())
                        classInstances.saveToCache()
                    }.onFailure { e ->
                        it.isNotFound = true
                        it.throwable = e
                        it.noClassDefFoundErrorCallback?.invoke()
                        errorMsg(e = e)
                    }
                } else startProcess()
            }
        } else Result(isNotFound = true, Throwable(LOADERSET_IS_NULL)).await { errorMsg() }
    }.getOrElse { e -> Result(isNotFound = true, e).await { errorMsg(e = e) } }

    /**
     * [Class] 查找结果实现类
     * @param isNotFound 是否没有找到 [Class] - 默认否
     * @param throwable 错误信息
     */
    inner class Result internal constructor(
        internal var isNotFound: Boolean = false,
        internal var throwable: Throwable? = null
    ) : BaseResult {

        /** 异步方法体回调结果 */
        internal var waitResultCallback: ((Class<*>?) -> Unit)? = null

        /** 异步方法体回调数组结果 */
        internal var waitAllResultCallback: ((MutableList<Class<*>>) -> Unit)? = null

        /** 异常结果重新回调方法体 */
        internal var noClassDefFoundErrorCallback: (() -> Unit)? = null

        /**
         * 创建监听结果事件方法体
         * @param initiate 方法体
         * @return [Result] 可继续向下监听
         */
        inline fun result(initiate: Result.() -> Unit) = apply(initiate)

        /**
         * 得到 [Class] 本身
         *
         * - 若有多个 [Class] 结果只会返回第一个
         *
         * - 在查找条件找不到任何结果的时候将返回 null
         *
         * - 若你设置了 [async] 请使用 [wait] 方法
         * @return [Class] or null
         */
        fun get() = all().takeIf { it.isNotEmpty() }?.first()

        /**
         * 得到 [Class] 本身数组
         *
         * - 返回全部查找条件匹配的多个 [Class] 实例
         *
         * - 在查找条件找不到任何结果的时候将返回空的 [MutableList]
         *
         * - 若你设置了 [async] 请使用 [waitAll] 方法
         * @return [MutableList]<[Class]>
         */
        fun all() = classInstances

        /**
         * 得到 [Class] 本身数组 (依次遍历)
         *
         * - 回调全部查找条件匹配的多个 [Class] 实例
         *
         * - 在查找条件找不到任何结果的时候将不会执行
         *
         * - 若你设置了 [async] 请使用 [waitAll] 方法
         * @param result 回调每个结果
         * @return [Result] 可继续向下监听
         */
        fun all(result: (Class<*>) -> Unit): Result {
            all().takeIf { it.isNotEmpty() }?.forEach(result)
            return this
        }

        /**
         * 得到 [Class] 本身 (异步)
         *
         * - 若有多个 [Class] 结果只会回调第一个
         *
         * - 在查找条件找不到任何结果的时候将回调 null
         *
         * - 你需要设置 [async] 后此方法才会被回调 - 否则请使用 [get] 方法
         * @param result 回调 - ([Class] or null)
         * @return [Result] 可继续向下监听
         */
        fun wait(result: (Class<*>?) -> Unit): Result {
            waitResultCallback = result
            return this
        }

        /**
         * 得到 [Class] 本身数组 (异步)
         *
         * - 回调全部查找条件匹配的多个 [Class] 实例
         *
         * - 在查找条件找不到任何结果的时候将回调空的 [MutableList]
         *
         * - 你需要设置 [async] 后此方法才会被回调 - 否则请使用 [all] 方法
         * @param result 回调 - ([MutableList]<[Class]>)
         * @return [Result] 可继续向下监听
         */
        fun waitAll(result: (MutableList<Class<*>>) -> Unit): Result {
            waitAllResultCallback = result
            return this
        }

        /**
         * 监听找不到 [Class] 时
         * @param result 回调错误
         * @return [Result] 可继续向下监听
         */
        fun onNoClassDefFoundError(result: (Throwable) -> Unit): Result {
            noClassDefFoundErrorCallback = { if (isNotFound) result(throwable ?: Throwable("Initialization Error")) }
            noClassDefFoundErrorCallback?.invoke()
            return this
        }

        /**
         * 忽略异常并停止打印任何错误日志
         *
         * - 此时若要监听异常结果 - 你需要手动实现 [onNoClassDefFoundError] 方法
         * @return [Result] 可继续向下监听
         */
        fun ignored(): Result {
            isIgnoreErrorLogs = true
            return this
        }
    }
}