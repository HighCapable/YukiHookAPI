/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
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
 * This file is created by fankes on 2022/2/3.
 */
package com.highcapable.yukihookapi.hook.entity

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.param.PackageParam

/**
 * [YukiHookAPI] 的子类 Hooker 实现
 *
 * 也许你的模块中存在多个功能模块 (Hooker) - 继承并使用此类可以方便帮你管理每个功能模块 (Hooker)
 *
 * 更多请参考 [InjectYukiHookWithXposed] 中的注解内容
 *
 * 详情请参考 [通过自定义 Hooker 创建](https://highcapable.github.io/YukiHookAPI/zh-cn/config/api-example#%E9%80%9A%E8%BF%87%E8%87%AA%E5%AE%9A%E4%B9%89-hooker-%E5%88%9B%E5%BB%BA)
 *
 * For English version, see [Created by Custom Hooker](https://highcapable.github.io/YukiHookAPI/en/config/api-example#created-by-custom-hooker)
 */
abstract class YukiBaseHooker : PackageParam() {

    /**
     * 赋值并克隆一个 [PackageParam]
     * @param packageParam 需要使用的 [PackageParam]
     */
    internal fun assignInstance(packageParam: PackageParam) {
        assign(packageParam.wrapper)
        onHook()
    }

    /** 子类 Hook 开始 */
    abstract fun onHook()
}