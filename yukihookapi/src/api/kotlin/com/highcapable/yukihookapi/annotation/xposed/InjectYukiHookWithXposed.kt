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
@file:Suppress("unused")

package com.highcapable.yukihookapi.annotation.xposed

import androidx.annotation.Keep
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.hook.proxy.YukiHookXposedInitProxy

/**
 * 标识 YukiHook 注入 Xposed 入口的类注释
 *
 * - 你的项目 source 目录必须为 ../src/main/ 后方内容根据你自行喜好定义即可 - 否则编译会报错
 *
 * - 你的 Hook 入口类(HookEntryClass) 需要按照此格式创建 --> 你的模块APP包名/hook/...可允许子包名存在.../你的入口类
 *
 * 例子：com.example.module.hook.MainHook、com.example.module.hook.inject.MainInject、com.example.module.hook.custom.CustomClass
 *
 * 你的 xposed_init 入口将被自动生成为 --> 你的模块APP包名/hook/...可允许子包名存在.../你的入口类_YukiHookXposedInit
 *
 * 例子：com.example.module.hook.MainHook_YukiHookXposedInit
 *
 * - 你的模块包名将被这样识别：|com.example.module|.hook...
 *
 * - 若你不喜欢这样创建类 - 没问题 - 请在 [YukiHookAPI.encase] 中自行指定模块包名即可 - 但不按照规则定义包名你将会收到编译警告
 *
 * 例子：YukiHookAPI.encase(moduleName = "com.example.module", ...)
 *
 * - 最后这一点很重要：请不要随意修改项目 ../src/main/assets/xposed_init 中的内容 - 否则可能会导致模块装载发生错误
 *
 * 你必须将被注释的类继承于 [YukiHookXposedInitProxy] 接口实现 [YukiHookXposedInitProxy.onHook] 方法 - 否则编译会报错
 *
 * 只能拥有一个 Hook 入口 - 若存在多个注释编译会报错
 *
 * 详情请参考 [YukiHookAPI Wiki](https://github.com/fankes/YukiHookAPI/wiki)
 */
@Target(AnnotationTarget.CLASS)
@Keep
annotation class InjectYukiHookWithXposed