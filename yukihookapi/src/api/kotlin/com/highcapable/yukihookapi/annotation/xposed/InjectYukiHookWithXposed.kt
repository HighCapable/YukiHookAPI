/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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
package com.highcapable.yukihookapi.annotation.xposed

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit
import de.robv.android.xposed.IXposedHookInitPackageResources

/**
 * 标识 [YukiHookAPI] 注入 Xposed 入口的类注解
 *
 * - 你的项目 source 目录默认为 "src/main/" 可在 [sourcePath] 中进行自定义 - 自动处理程序将只检查 ../[sourcePath]/java.. 中间部分
 *
 * - 自动处理程序将自动在 ../[sourcePath]/assets/ 下建立 xposed_init 文件
 *
 * 你的 xposed_init 入口将被自动生成为 --> 你的入口类完整包名/你的入口类名_YukiHookXposedInit 或自定义 [entryClassName]
 *
 * - 你可以在 [modulePackageName] 自定义你的模块包名 - 未定义的情况下会使用 AndroidManifest.xml 与 build.gradle/kts 进行分析 - 失败编译会报错
 *
 * - 为了防止模块包名无法正常被识别 - 自定义 [modulePackageName] 会在编译时产生警告
 *
 * - ❗最后这一点很重要：请不要随意修改项目 ../[sourcePath]/assets/xposed_init 中的内容 - 否则可能会导致模块无法装载
 *
 * - ❗你必须将被注解的类继承于 [IYukiHookXposedInit] 接口实现 [IYukiHookXposedInit.onHook] 方法 - 否则编译会报错
 *
 * - ❗只能拥有一个 Hook 入口 - 若存在多个注解编译会报错
 *
 * 详情请参考 [InjectYukiHookWithXposed 注解](https://fankes.github.io/YukiHookAPI/zh-cn/config/xposed-using#injectyukihookwithxposed-%E6%B3%A8%E8%A7%A3)
 *
 * For English version, see [InjectYukiHookWithXposed Annotation](https://fankes.github.io/YukiHookAPI/en/config/xposed-using#injectyukihookwithxposed-annotation)
 * @param sourcePath 你的项目 source 相对路径 - 默认为 ..src/main..
 * @param modulePackageName 模块包名 - 不填默认自动生成
 * @param entryClassName 定义 [YukiHookAPI] 自动生成 Xposed 模块入口类的名称 - 不填默认使用 HookEntryClass_YukiHookXposedInit 进行生成
 * @param isUsingResourcesHook 是否启用 Resources Hook (资源钩子) - 启用后将自动注入 [IXposedHookInitPackageResources] - 默认是
 */
@Target(AnnotationTarget.CLASS)
annotation class InjectYukiHookWithXposed(
    val sourcePath: String = "src/main",
    val modulePackageName: String = "",
    val entryClassName: String = "",
    val isUsingResourcesHook: Boolean = true
)