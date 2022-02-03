/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package com.highcapable.yukihookapi.hook.core.finder

import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.utils.ReflectionUtils
import java.lang.reflect.Method

/**
 * [Method] 查找类
 *
 * 可通过指定类型查找指定方法
 * @param hookClass 当前被 Hook 的 [Class]
 */
class MethodFinder(private val hookClass: Class<*>) {

    /** 方法参数 */
    private var params: Array<out Class<*>>? = null

    /** 方法名 */
    var name = ""

    /** 方法返回值 */
    var returnType: Class<*>? = null

    /**
     * 方法参数
     * @param param 参数数组
     */
    fun param(vararg param: Class<*>) {
        params = param
    }

    /**
     * 得到方法 - 不能在外部调用
     * @return [Method]
     * @throws NoSuchMethodError 如果找不到方法
     */
    @DoNotUseMethod
    fun find(): Method =
        when {
            name.isBlank() -> error("Method name cannot be empty")
            else ->
                if (params != null)
                    ReflectionUtils.findMethodBestMatch(hookClass, returnType, name, *params!!)
                else ReflectionUtils.findMethodNoParam(hookClass, returnType, name)
        }
}