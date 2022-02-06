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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.factory

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

/**
 * 在 [YukiHookXposedInitProxy] 中装载 [YukiHookAPI]
 * @param initiate Hook 方法体
 */
fun YukiHookXposedInitProxy.encase(initiate: PackageParam.() -> Unit) = YukiHookAPI.encase(initiate)

/**
 * 在 [YukiHookXposedInitProxy] 中装载 [YukiHookAPI]
 * @param hooker Hook 子类数组 - 必填不能为空
 * @throws IllegalStateException 如果 [hooker] 是空的
 */
fun YukiHookXposedInitProxy.encase(vararg hooker: YukiBaseHooker) = YukiHookAPI.encase(hooker = hooker)

/**
 * 判断模块是否在太极、无极中激活
 * @return [Boolean] 是否激活
 */
val Context.isTaichiModuleActive: Boolean
    get() {
        var isModuleActive = false
        runCatching {
            var result: Bundle? = null
            Uri.parse("content://me.weishu.exposed.CP/").also { uri ->
                runCatching {
                    result = contentResolver.call(uri, "active", null, null)
                }.onFailure {
                    // TaiChi is killed, try invoke
                    runCatching {
                        startActivity(Intent("me.weishu.exp.ACTION_ACTIVE").apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) })
                    }.onFailure { return false }
                }
                if (result == null)
                    result = contentResolver.call(Uri.parse("content://me.weishu.exposed.CP/"), "active", null, null)
                if (result == null) return false
            }
            isModuleActive = result?.getBoolean("active", false) == true
        }
        return isModuleActive
    }