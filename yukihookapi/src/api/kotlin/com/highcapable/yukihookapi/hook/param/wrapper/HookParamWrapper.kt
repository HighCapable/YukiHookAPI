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
 * This file is Created by fankes on 2022/2/9.
 */
package com.highcapable.yukihookapi.hook.param.wrapper

import com.highcapable.yukihookapi.annotation.YukiPrivateApi
import com.highcapable.yukihookapi.hook.param.HookParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import java.lang.reflect.Member

/**
 * 用于包装 [HookParam]
 *
 * - ❗这是一个私有 API - 请不要在外部使用
 * @param baseParam 对接 [XC_MethodHook.MethodHookParam]
 */
@YukiPrivateApi
class HookParamWrapper internal constructor(private var baseParam: XC_MethodHook.MethodHookParam? = null) {

    /**
     * 在回调中设置 [HookParamWrapper] 使用的 [XC_MethodHook.MethodHookParam]
     * @param baseParam 对接 [XC_MethodHook.MethodHookParam]
     * @return [HookParamWrapper]
     */
    internal fun assign(baseParam: XC_MethodHook.MethodHookParam): HookParamWrapper {
        this.baseParam = baseParam
        return this
    }

    /**
     * [Member] 实例
     * @return [Member] or null
     */
    val member: Member? get() = baseParam?.method

    /**
     * 当前实例对象
     * @return [Any] or null
     */
    val instance: Any? get() = baseParam?.thisObject

    /**
     * 方法、构造方法数组
     * @return [Array] or null
     */
    val args: Array<Any?>? get() = baseParam?.args

    /**
     * 方法、设置方法结果
     * @return [Any] or null
     */
    var result: Any?
        get() = baseParam?.result
        set(value) {
            baseParam?.result = value
        }

    /**
     * 设置方法参数
     * @param index 数组下标
     * @param any 参数对象实例
     */
    fun setArgs(index: Int, any: Any?) = baseParam?.args?.set(index, any)

    /**
     * 执行原始 [Member]
     *
     * 未进行 Hook 的 [Member]
     * @param member 实例
     * @param args 参数实例
     * @return [Any] or null
     */
    fun invokeOriginalMember(member: Member, vararg args: Any?): Any? = XposedBridge.invokeOriginalMethod(member, instance, args)

    override fun toString() = "HookParamWrapper[$baseParam]"
}