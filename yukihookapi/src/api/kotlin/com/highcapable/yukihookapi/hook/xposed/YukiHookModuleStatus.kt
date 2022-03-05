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
 * This file is Created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.hook.xposed

import androidx.annotation.Keep
import com.highcapable.yukihookapi.hook.log.yLoggerI
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus.isActive

/**
 * 这是一个 Xposed 模块 Hook 状态类
 *
 * 我们需要监听自己的模块是否被激活 - 可直接调用这个类的 [isActive] 方法
 *
 * 详情请参考 [判断自身激活状态](https://github.com/fankes/YukiHookAPI/wiki#%E5%88%A4%E6%96%AD%E8%87%AA%E8%BA%AB%E6%BF%80%E6%B4%BB%E7%8A%B6%E6%80%81)
 */
@Keep
object YukiHookModuleStatus {

    /**
     * 此方法经过 Hook 后返回 true 即模块已激活
     * @return [Boolean]
     */
    @Keep
    fun isActive(): Boolean {
        yLoggerI(msg = "This Module is not actived")
        return false
    }
}