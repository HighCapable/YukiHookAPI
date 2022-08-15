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
@file:Suppress("NewApi")

package com.highcapable.yukihookapi.hook.xposed.bridge.factory

import com.highcapable.yukihookapi.hook.core.finder.ConstructorFinder
import com.highcapable.yukihookapi.hook.core.finder.MethodFinder
import com.highcapable.yukihookapi.hook.core.finder.base.BaseFinder
import com.highcapable.yukihookapi.hook.log.yLoggerE
import com.highcapable.yukihookapi.hook.param.wrapper.HookParamWrapper
import com.highcapable.yukihookapi.hook.xposed.bridge.YukiHookBridge
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
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
     * Hook [BaseFinder.BaseResult]
     * @param traction 直接调用 [BaseFinder.BaseResult]
     * @param callback 回调
     * @return [Pair] - ([YukiMemberHook.Unhook] or null,[Boolean] 是否已经 Hook)
     */
    internal fun hook(traction: BaseFinder.BaseResult, callback: YukiHookCallback) = runCatching {
        hookMember(
            when (traction) {
                is MethodFinder.Result -> traction.ignored().give()
                is ConstructorFinder.Result -> traction.ignored().give()
                else -> error("Unexpected BaseFinder result interface type")
            }, callback
        )
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
                YukiMemberHook.Unhook.wrapper(XposedBridge.hookMethod(member, callback.compat())).let {
                    YukiHookedMembers.hookedMembers.add(it)
                    Pair(it, false)
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
     */
    internal class Unhook private constructor(private val instance: XC_MethodHook.Unhook) {

        internal companion object {

            /**
             * 从 [XC_MethodHook.Unhook] 创建 [Unhook] 实例
             * @param instance [XC_MethodHook.Unhook] 实例
             * @return [Unhook]
             */
            internal fun wrapper(instance: XC_MethodHook.Unhook) = Unhook(instance)
        }

        /**
         * 当前被 Hook 的 [Member]
         * @return [Member] or null
         */
        internal val member: Member? get() = instance.hookedMethod

        /** 解除 [instance] 的 Hook 并从 [YukiHookedMembers.hookedMembers] 缓存数组中移除 */
        internal fun remove() {
            if (YukiHookBridge.hasXposedBridge.not()) return
            instance.unhook()
            runCatching { YukiHookedMembers.hookedMembers.remove(this) }
        }
    }
}

/**
 * Hook 回调接口父类
 * @param priority Hook 优先级
 */
internal abstract class YukiHookCallback(open val priority: Int)