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
 * This file is Created by fankes on 2022/2/2.
 */
package com.highcapable.yukihookapi.hook.core

import com.highcapable.yukihookapi.param.HookParam
import com.highcapable.yukihookapi.param.PackageParam
import java.lang.reflect.Constructor
import java.lang.reflect.Method

class YukiHookCreater(private val param: PackageParam) {

    var grabMethod: Method? = null
    var grabConstructor: Constructor<*>? = null

    fun beforeHook(initiate: HookParam.() -> Unit) {

    }

    fun afterHook(initiate: HookParam.() -> Unit) {

    }

    fun replaceHook(initiate: HookParam.() -> Any?) {

    }

    fun replaceTo(any: Any?) {

    }

    fun intercept() {

    }

    fun hook() {

    }
}