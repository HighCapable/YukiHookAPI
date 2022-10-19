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

import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.core.finder.members.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.members.MethodFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Member

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
}

/**
 * Hook 核心功能实现实例
 *
 * 对接 [XposedBridge] 实现 Hook 功能
 */
internal object YukiHookHelper {

    /**
     * 使用 [XposedHelpers.findClass] 来查找 [Class]
     * @param name [Class] 的完整包名+名称
     * @param loader [Class] 所在的 [ClassLoader] - 默认空 - 可不填
     * @return [Class] or null
     * @throws ClassNotFoundException 如果找不到 [Class]
     */
    internal fun findClass(name: String, loader: ClassLoader? = null) =
        if (YukiHookBridge.hasXposedBridge) XposedHelpers.findClass(name, loader) else null

    /**
     * Hook [BaseFinder.BaseResult]
     * @param traction 直接调用 [BaseFinder.BaseResult]
     * @param callback 回调
     * @return [Pair] - ([YukiMemberHook.Unhook] or null,[Boolean] 是否已经 Hook)
     */
    internal fun hook(traction: BaseFinder.BaseResult, callback: YukiHookCallback) = runCatching {
        val member: Member? = when (traction) {
            is MethodFinder.Result -> traction.ignored().give()
            is ConstructorFinder.Result -> traction.ignored().give()
            else -> error("Unexpected BaseFinder result interface type")
        }
        hookMember(member, callback)
    }.onFailure { yLoggerE(msg = "Hooking Process exception occurred", e = it) }.getOrNull() ?: Pair(null, false)

    /**
     * Hook [Member]
     *
     * 对接 [XposedBridge.hookMethod]
     * @param member 需要 Hook 的方法、构造方法
     * @param callback 回调
     * @return [Pair] - ([YukiMemberHook.Unhook] or null,[Boolean] 是否已经 Hook)
     */
    internal fun hookMember(member: Member?, callback: YukiHookCallback): Pair<YukiMemberHook.Unhook?, Boolean> {
        runCatching {
            YukiHookedMembers.hookedMembers.takeIf { it.isNotEmpty() }?.forEach {
                if (it.member.toString() == member?.toString()) return@runCatching it
            }
        }
        return when {
            member == null -> Pair(null, false)
            YukiHookBridge.hasXposedBridge ->
                XposedBridge.hookMethod(member, callback.compat()).compat().let { memberUnhook ->
                    YukiHookedMembers.hookedMembers.add(memberUnhook)
                    Pair(memberUnhook, false)
                }
            else -> Pair(null, false)
        }
    }

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     */
    internal fun invokeOriginalMember(member: Member?, instance: Any?, vararg args: Any?) =
        if (YukiHookBridge.hasXposedBridge && YukiHookedMembers.hookedMembers.any { it.member.toString() == member.toString() })
            member?.let { XposedBridge.invokeOriginalMethod(it, instance, args) }
        else null

    /**
     * 兼容对接已 Hook 的 [Member] 接口
     * @return [YukiMemberHook.Unhook]
     */
    private fun XC_MethodHook.Unhook.compat() = object : YukiMemberHook.Unhook() {
        override val member get() = hookedMethod
        override fun unhook() = this@compat.unhook()
    }

    /**
     * 兼容对接 Hook 回调接口
     * @return [XC_MethodHook] 原始接口
     */
    private fun YukiHookCallback.compat() = object : XC_MethodHook(priority) {

        override fun beforeHookedMethod(param: MethodHookParam?) {
            if (param == null) return
            if (this@compat !is YukiMemberHook) error("Invalid YukiHookCallback type")
            if (this@compat is YukiMemberReplacement)
                param.result = replaceHookedMember(param.compat())
            else beforeHookedMember(param.compat())
        }

        override fun afterHookedMethod(param: MethodHookParam?) {
            if (param == null) return
            if (this@compat !is YukiMemberHook) error("Invalid YukiHookCallback type")
            afterHookedMember(param.compat())
        }
    }

    /**
     * 兼容对接 Hook 结果回调接口
     * @return [YukiHookCallback.Param]
     */
    private fun XC_MethodHook.MethodHookParam.compat() = object : YukiHookCallback.Param {
        override val member get() = this@compat.method
        override val instance get() = this@compat.thisObject
        override val args get() = this@compat.args
        override val hasThrowable get() = this@compat.hasThrowable()
        override var result
            get() = this@compat.result
            set(value) {
                this@compat.result = value
            }
        override var throwable
            get() = this@compat.throwable
            set(value) {
                this@compat.throwable = value
            }
    }
}

/**
 * Hook 替换方法回调接口抽象类
 * @param priority Hook 优先级- 默认 [YukiHookPriority.PRIORITY_DEFAULT]
 */
internal abstract class YukiMemberReplacement(override val priority: Int = YukiHookPriority.PRIORITY_DEFAULT) : YukiMemberHook(priority) {

    override fun beforeHookedMember(param: Param) {
        param.result = replaceHookedMember(param)
    }

    override fun afterHookedMember(param: Param) {}

    /**
     * 拦截替换为指定结果
     * @param param Hook 结果回调接口
     * @return [Any] or null
     */
    abstract fun replaceHookedMember(param: Param): Any?
}

/**
 * Hook 方法回调接口抽象类
 * @param priority Hook 优先级 - 默认 [YukiHookPriority.PRIORITY_DEFAULT]
 */
internal abstract class YukiMemberHook(override val priority: Int = YukiHookPriority.PRIORITY_DEFAULT) : YukiHookCallback(priority) {

    /**
     * 在方法执行之前注入
     * @param param Hook 结果回调接口
     */
    open fun beforeHookedMember(param: Param) {}

    /**
     * 在方法执行之后注入
     * @param param Hook 结果回调接口
     */
    open fun afterHookedMember(param: Param) {}

    /**
     * 已经 Hook 且可被解除 Hook 的 [Member] 实现接口抽象类
     */
    internal abstract class Unhook internal constructor() {

        /**
         * 当前被 Hook 的 [Member]
         * @return [Member] or null
         */
        internal abstract val member: Member?

        /** 解除 Hook */
        internal abstract fun unhook()

        /** 解除 Hook 并从 [YukiHookedMembers.hookedMembers] 缓存数组中移除 */
        internal fun remove() {
            if (YukiHookBridge.hasXposedBridge.not()) return
            unhook()
            runCatching { YukiHookedMembers.hookedMembers.remove(this) }
        }
    }
}

/**
 * Hook 回调接口抽象类
 * @param priority Hook 优先级
 */
internal abstract class YukiHookCallback(open val priority: Int) {

    /**
     * Hook 结果回调接口
     */
    internal interface Param {

        /**
         * [Member] 实例
         * @return [Member] or null
         */
        val member: Member?

        /**
         * 当前实例对象
         * @return [Any] or null
         */
        val instance: Any?

        /**
         * 方法、构造方法数组
         * @return [Array] or null
         */
        val args: Array<Any?>?

        /**
         * 获取、设置方法结果
         * @return [Any] or null
         */
        var result: Any?

        /**
         * 判断是否存在设置过的方法调用抛出异常
         * @return [Boolean]
         */
        val hasThrowable: Boolean

        /**
         * 获取、设置方法调用抛出的异常
         * @return [Throwable] or null
         * @throws Throwable
         */
        var throwable: Throwable?
    }
}