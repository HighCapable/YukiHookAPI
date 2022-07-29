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
 * This file is Created by fankes on 2022/7/28.
 */
package com.highcapable.yukihookapi.hook.xposed.bridge.factory

import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * Hook 回调优先级配置类
 */
internal object YukiHookPriority {

    /** 默认 Hook 回调优先级 */
    internal const val PRIORITY_DEFAULT = 50

    /** 延迟回调 Hook 方法结果 */
    internal const val PRIORITY_LOWEST = -10000

    /** 更快回调 Hook 方法结果 */
    internal const val PRIORITY_HIGHEST = 10000
}

/**
 * 已经 Hook 的方法、构造方法缓存数组
 */
internal object YukiHookedMembers {

    /** 已经 Hook 的 [Member] 数组 */
    internal val hookedMembers = HashSet<YukiMemberHook.Unhook>()

    /** 已经 Hook 的成组 [Method] 数组 */
    internal val hookedQueueMethods = HashMap<String, HashSet<YukiMemberHook.Unhook>>()

    /** 已经 Hook 的成组 [Constructor] 数组 */
    internal val hookedQueueConstructors = HashMap<String, HashSet<YukiMemberHook.Unhook>>()
}

/**
 * Hook 核心功能实现实例
 *
 * 对接 [XposedBridge] 实现 Hook 功能
 */
internal object YukiHookHelper {

    /**
     * 查找 [Class]
     * @param classLoader 当前 [ClassLoader]
     * @param baseClass 当前类
     * @return [Field]
     * @throws IllegalStateException 如果 [ClassLoader] 为空
     */
    internal fun findClass(classLoader: ClassLoader?, baseClass: Class<*>) =
        classLoader?.loadClass(baseClass.name) ?: error("ClassLoader is null")

    /**
     * 查找变量
     * @param baseClass 所在类
     * @param name 变量名称
     * @return [Field]
     * @throws NoSuchFieldError 如果找不到变量
     */
    internal fun findField(baseClass: Class<*>, name: String) = baseClass.getDeclaredField(name).apply { isAccessible = true }

    /**
     * 查找方法
     * @param baseClass 所在类
     * @param name 方法名称
     * @param paramTypes 方法参数
     * @return [Method]
     * @throws NoSuchMethodError 如果找不到方法
     */
    internal fun findMethod(baseClass: Class<*>, name: String, vararg paramTypes: Class<*>) =
        baseClass.getDeclaredMethod(name, *paramTypes).apply { isAccessible = true }

    /**
     * Hook 方法
     *
     * 对接 [XposedBridge.hookMethod]
     * @param hookMethod 需要 Hook 的方法、构造方法
     * @param callback 回调
     * @return [Pair] - ([YukiMemberHook.Unhook],[Boolean] 是否已经 Hook)
     */
    internal fun hookMethod(hookMethod: Member?, callback: YukiHookCallback): Pair<YukiMemberHook.Unhook, Boolean> {
        runCatching {
            YukiHookedMembers.hookedMembers.takeIf { it.isNotEmpty() }?.forEach {
                if (it.member.toString() == hookMethod.toString()) return@runCatching it
            }
        }
        return YukiMemberHook.Unhook.wrapper(XposedBridge.hookMethod(hookMethod, callback.compat())).let {
            YukiHookedMembers.hookedMembers.add(it)
            Pair(it, false)
        }
    }

    /**
     * Hook 当前 [hookClass] 所有 [methodName] 的方法
     *
     * 对接 [XposedBridge.hookAllMethods]
     * @param hookClass 当前 Hook 的 [Class]
     * @param methodName 方法名
     * @param callback 回调
     * @return [Pair] - ([HashSet] 成功 Hook 的 [YukiMemberHook.Unhook] 数组,[Boolean] 是否已经 Hook)
     */
    internal fun hookAllMethods(
        hookClass: Class<*>?, methodName: String, callback: YukiHookCallback
    ): Pair<HashSet<YukiMemberHook.Unhook>, Boolean> {
        var isAlreadyHook = false
        val hookedMembers = HashSet<YukiMemberHook.Unhook>().also {
            val allMethodsName = "$hookClass$methodName"
            if (YukiHookedMembers.hookedQueueMethods.contains(allMethodsName)) {
                isAlreadyHook = true
                YukiHookedMembers.hookedQueueMethods[allMethodsName]?.forEach { e -> it.add(e) }
                return@also
            }
            XposedBridge.hookAllMethods(hookClass, methodName, callback.compat()).takeIf { e -> e.isNotEmpty() }
                ?.forEach { e -> it.add(YukiMemberHook.Unhook.wrapper(e, allMethodsName)) }
            YukiHookedMembers.hookedQueueMethods[allMethodsName] = it
        }
        return Pair(hookedMembers, isAlreadyHook)
    }

    /**
     * Hook 当前 [hookClass] 所有构造方法
     *
     * 对接 [XposedBridge.hookAllConstructors]
     * @param hookClass 当前 Hook 的 [Class]
     * @param callback 回调
     * @return [Pair] - ([HashSet] 成功 Hook 的 [YukiMemberHook.Unhook] 数组,[Boolean] 是否已经 Hook)
     */
    internal fun hookAllConstructors(hookClass: Class<*>?, callback: YukiHookCallback): Pair<HashSet<YukiMemberHook.Unhook>, Boolean> {
        var isAlreadyHook = false
        val hookedMembers = HashSet<YukiMemberHook.Unhook>().also {
            val allConstructorsName = "$hookClass<init>"
            if (YukiHookedMembers.hookedQueueConstructors.contains(allConstructorsName)) {
                isAlreadyHook = true
                YukiHookedMembers.hookedQueueConstructors[allConstructorsName]?.forEach { e -> it.add(e) }
                return@also
            }
            XposedBridge.hookAllConstructors(hookClass, callback.compat()).takeIf { e -> e.isNotEmpty() }
                ?.forEach { e -> it.add(YukiMemberHook.Unhook.wrapper(e, allConstructorsName)) }
            YukiHookedMembers.hookedQueueConstructors[allConstructorsName] = it
        }
        return Pair(hookedMembers, isAlreadyHook)
    }

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     */
    internal fun invokeOriginalMember(member: Member, instance: Any?, vararg args: Any?): Any? {
        val isHookedMember = YukiHookedMembers.hookedMembers.any { it.member.toString() == member.toString() }
        val isQueueMethod = YukiHookedMembers.hookedQueueMethods.any { it.value.any { e -> e.member.toString() == member.toString() } }
        val isQueueConstructor = YukiHookedMembers.hookedQueueConstructors.any { it.value.any { e -> e.member.toString() == member.toString() } }
        return if (isHookedMember || isQueueMethod || isQueueConstructor)
            XposedBridge.invokeOriginalMethod(member, instance, args)
        else null
    }

    /**
     * 兼容对接 Hook 回调接口
     * @return [XC_MethodHook] 原始接口
     */
    private fun YukiHookCallback.compat() = object : XC_MethodHook(priority) {

        /** 创建 Hook 前 [HookParamWrapper] */
        private val beforeHookWrapper = HookParamWrapper()

        /** 创建 Hook 后 [HookParamWrapper] */
        private val afterHookWrapper = HookParamWrapper()

        override fun beforeHookedMethod(param: MethodHookParam?) {
            if (param == null) return
            if (this@compat !is YukiMemberHook) error("Invalid YukiHookCallback type")
            if (this@compat is YukiMemberReplacement)
                param.result = replaceHookedMember(beforeHookWrapper.assign(param))
            else beforeHookedMember(beforeHookWrapper.assign(param))
        }

        override fun afterHookedMethod(param: MethodHookParam?) {
            if (param == null) return
            if (this@compat !is YukiMemberHook) error("Invalid YukiHookCallback type")
            afterHookedMember(afterHookWrapper.assign(param))
        }
    }
}

/**
 * Hook 替换方法回调接口
 * @param priority Hook 优先级- 默认 [YukiHookPriority.PRIORITY_DEFAULT]
 */
internal abstract class YukiMemberReplacement(override val priority: Int = YukiHookPriority.PRIORITY_DEFAULT) : YukiMemberHook(priority) {

    override fun beforeHookedMember(wrapper: HookParamWrapper) {
        wrapper.result = replaceHookedMember(wrapper)
    }

    override fun afterHookedMember(wrapper: HookParamWrapper) {}

    /**
     * 拦截替换为指定结果
     * @param wrapper 包装实例
     * @return [Any] or null
     */
    abstract fun replaceHookedMember(wrapper: HookParamWrapper): Any?
}

/**
 * Hook 方法回调接口
 * @param priority Hook 优先级 - 默认 [YukiHookPriority.PRIORITY_DEFAULT]
 */
internal abstract class YukiMemberHook(override val priority: Int = YukiHookPriority.PRIORITY_DEFAULT) : YukiHookCallback(priority) {

    /**
     * 在方法执行之前注入
     * @param wrapper 包装实例
     */
    open fun beforeHookedMember(wrapper: HookParamWrapper) {}

    /**
     * 在方法执行之后注入
     * @param wrapper 包装实例
     */
    open fun afterHookedMember(wrapper: HookParamWrapper) {}

    /**
     * 已经 Hook 且可被解除 Hook 的 [Member] 实现类
     * @param instance 对接 [XC_MethodHook.Unhook]
     * @param tag 标识多个 [Member] Hook 的标签 - 例如 [YukiHookHelper.hookAllMethods]、[YukiHookHelper.hookAllConstructors]
     */
    internal class Unhook private constructor(private val instance: XC_MethodHook.Unhook, private val tag: String) {

        companion object {

            /**
             * 从 [XC_MethodHook.Unhook] 创建 [Unhook] 实例
             * @param instance [XC_MethodHook.Unhook] 实例
             * @param tag 标识多个 [Member] Hook 的标签 - 默认空
             * @return [Unhook]
             */
            internal fun wrapper(instance: XC_MethodHook.Unhook, tag: String = "") = Unhook(instance, tag)
        }

        /**
         * 当前被 Hook 的 [Member]
         * @return [Member] or null
         */
        internal val member: Member? get() = instance.hookedMethod

        /**
         * 解除 Hook 并从
         * [YukiHookedMembers.hookedMembers]、
         * [YukiHookedMembers.hookedQueueMethods]、
         * [YukiHookedMembers.hookedQueueConstructors]
         * 缓存数组中移除
         */
        internal fun remove() {
            if (tag.isNotBlank()) runCatching {
                when {
                    YukiHookedMembers.hookedQueueMethods.contains(tag) -> {
                        YukiHookedMembers.hookedQueueMethods[tag]?.takeIf { it.isNotEmpty() }?.forEach { it.unhook(isNeedRemove = false) }
                        YukiHookedMembers.hookedQueueMethods.remove(tag)
                    }
                    YukiHookedMembers.hookedQueueConstructors.contains(tag) -> {
                        YukiHookedMembers.hookedQueueConstructors[tag]?.takeIf { it.isNotEmpty() }?.forEach { it.unhook(isNeedRemove = false) }
                        YukiHookedMembers.hookedQueueConstructors.remove(tag)
                    }
                    else -> unhook()
                }
            } else unhook()
        }

        /**
         * 解除 [instance] 的 Hook
         * @param isNeedRemove 是否需要从 [YukiHookedMembers.hookedMembers] 中移除
         */
        private fun unhook(isNeedRemove: Boolean = true) {
            instance.unhook()
            if (isNeedRemove) runCatching { YukiHookedMembers.hookedMembers.remove(this) }
        }
    }
}

/**
 * Hook 回调接口父类
 * @param priority Hook 优先级
 */
internal abstract class YukiHookCallback(open val priority: Int)