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
 * This file is Created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.hook.entity

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.DoNotUseMethod
import com.highcapable.yukihookapi.hook.proxy.YukiHookXposedInitProxy
import com.highcapable.yukihookapi.param.PackageParam

/**
 * YukiHook 的子类实现
 *
 * 也许你的 Module 中存在多个 Hooker - 继承此类可以方便帮你管理每个 Hooker
 *
 * 你可以继续继承此类进行自定义 Hooker 相关参数
 *
 * 你可以在 [YukiHookXposedInitProxy] 的 [YukiHookXposedInitProxy.onHook] 中实现如下用法
 *
 * 调用 [YukiHookAPI.encase] encase(moduleName = "模块包名", MainHooker(), SecondHooker(), ThirdHooker() ...)
 *
 * 调用 [PackageParam.loadHooker] loadHooker(hooker = CustomHooker())
 *
 * ....
 *
 * 继承类参考示例：
 *
 * ....
 *
 * class CustomHooker : YukiBaseHooker() {
 *
 * ....override fun onHook() {
 *
 * ........// Your code here.
 *
 * ....}
 *
 * }
 *
 * ....
 *
 * 详情请参考 [YukiHookAPI Wiki](https://github.com/fankes/YukiHookAPI/wiki)
 */
abstract class YukiBaseHooker : PackageParam() {

    /**
     * 赋值并克隆一个 [PackageParam]
     * @param packageParam 需要使用的 [PackageParam]
     */
    @DoNotUseMethod
    internal fun assignInstance(packageParam: PackageParam) {
        baseAssignInstance(packageParam)
        onHook()
    }

    /** 子类 Hook 开始 */
    abstract fun onHook()
}