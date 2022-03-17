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
package com.highcapable.yukihookapi.annotation.xposed

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

/**
 * 标识 [YukiHookAPI] 注入 Xposed 入口的类注释
 *
 * - 你的项目 source 目录默认为 "src/main/" 可在 [sourcePath] 中进行自定义 - 自动处理程序将只检查 ..app/[sourcePath]/java.. 中间部分
 *
 * - 自动处理程序将自动在 ../[sourcePath]/assets/ 下建立 xposed_init 文件
 *
 * - 你的 Hook 入口类(HookEntryClass) 需要按照此格式创建 --> 你的模块 APP 包名/hook/...可允许子包名存在.../你的入口类
 *
 * 例子：com.example.module.hook.MainHook、com.example.module.hook.inject.MainInject、com.example.module.hook.custom.CustomClass
 *
 * 你的 xposed_init 入口将被自动生成为 --> 你的模块APP包名/hook/...可允许子包名存在.../你的入口类_YukiHookXposedInit
 *
 * 例子：com.example.module.hook.MainHook_YukiHookXposedInit
 *
 * - 你的模块包名将被这样识别：|com.example.module|.hook...
 *
 * - 若你不喜欢这样创建类 - 没问题 - 请在 [modulePackageName] 填写你的模块包名即可 - 默认会按照标准识别 - 失败编译会报错
 *
 * - 为了防止模块包名无法正常被识别 - 自定义 [modulePackageName] 会在编译时产生警告
 *
 * - ❗最后这一点很重要：请不要随意修改项目 ../[sourcePath]/assets/xposed_init 中的内容 - 否则可能会导致模块无法装载
 *
 * - ❗你必须将被注释的类继承于 [YukiHookXposedInitProxy] 接口实现 [YukiHookXposedInitProxy.onHook] 方法 - 否则编译会报错
 *
 * - ❗只能拥有一个 Hook 入口 - 若存在多个注释编译会报错
 *
 * 详情请参考 [InjectYukiHookWithXposed 注释](https://github.com/fankes/YukiHookAPI/wiki/%E4%BD%9C%E4%B8%BA-Xposed-%E6%A8%A1%E5%9D%97%E4%BD%BF%E7%94%A8%E7%9A%84%E7%9B%B8%E5%85%B3%E9%85%8D%E7%BD%AE#injectyukihookwithxposed-%E6%B3%A8%E9%87%8A)
 * @param sourcePath 你的项目 source 相对路径 - 默认为 ..src/main..
 * @param modulePackageName 模块包名 - 使用标准路径可不填会自动生成
 */
@Target(AnnotationTarget.CLASS)
annotation class InjectYukiHookWithXposed(val sourcePath: String = "src/main", val modulePackageName: String = "")