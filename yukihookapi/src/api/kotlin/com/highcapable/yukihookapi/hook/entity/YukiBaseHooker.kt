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
package com.highcapable.yukihookapi.hook.entity

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.param.PackageParam
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

/**
 * [YukiHookAPI] 的子类 Hooker 实现
 *
 * 也许你的 Module 中存在多个 Hooker - 继承此类可以方便帮你管理每个 Hooker
 *
 * 你可以继续继承此类进行自定义 Hooker 相关参数
 *
 * 你可以在 [YukiHookXposedInitProxy] 的 [YukiHookXposedInitProxy.onHook] 中实现如下用法：
 *
 * 1.调用 [YukiHookAPI.encase] encase(MainHooker(), SecondHooker(), ThirdHooker() ...)
 *
 * 2.调用 [PackageParam.loadHooker] loadHooker(hooker = CustomHooker())
 *
 * 更多请参考 [InjectYukiHookWithXposed] 中的注释内容
 *
 * 详情请参考 [通过自定义 Hooker 创建](https://github.com/fankes/YukiHookAPI/wiki/API-%E5%9F%BA%E6%9C%AC%E9%85%8D%E7%BD%AE#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
 */
abstract class YukiBaseHooker : PackageParam() {

    /**
     * 赋值并克隆一个 [PackageParam]
     * @param packageParam 需要使用的 [PackageParam]
     */
    internal fun assignInstance(packageParam: PackageParam) {
        baseAssignInstance(packageParam)
        onHook()
    }

    /** 子类 Hook 开始 */
    abstract fun onHook()
}