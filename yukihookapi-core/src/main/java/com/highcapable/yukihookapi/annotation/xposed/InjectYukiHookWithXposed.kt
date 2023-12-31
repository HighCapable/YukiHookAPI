/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2022/2/3.
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
 * - 最后这一点很重要：请不要随意修改项目 ../[sourcePath]/assets/xposed_init 中的内容 - 否则可能会导致模块无法装载
 *
 * - 你必须将被注解的类继承于 [IYukiHookXposedInit] 接口实现 [IYukiHookXposedInit.onHook] 方法 - 否则编译会报错
 *
 * - 只能拥有一个 Hook 入口 - 若存在多个注解编译会报错
 *
 * 详情请参考 [InjectYukiHookWithXposed 注解](https://highcapable.github.io/YukiHookAPI/zh-cn/config/xposed-using#injectyukihookwithxposed-%E6%B3%A8%E8%A7%A3)
 *
 * For English version, see [InjectYukiHookWithXposed Annotation](https://highcapable.github.io/YukiHookAPI/en/config/xposed-using#injectyukihookwithxposed-annotation)
 * @param sourcePath 你的项目 source 相对路径 - 默认为 ..src/main..
 * @param modulePackageName 模块包名 - 不填默认自动生成
 * @param entryClassName 定义 [YukiHookAPI] 自动生成 Xposed 模块入口类的名称 - 不填默认使用 "入口类名_YukiHookXposedInit" 进行生成
 * @param isUsingXposedModuleStatus 是否启用 Xposed 模块激活等状态功能 (自动 Hook 模块自身实现状态检测) - 默认是
 * @param isUsingResourcesHook 是否启用 Resources Hook (资源钩子) - 启用后将自动注入 [IXposedHookInitPackageResources] - 默认否
 */
@Target(AnnotationTarget.CLASS)
annotation class InjectYukiHookWithXposed(
    val sourcePath: String = "src/main",
    val modulePackageName: String = "",
    val entryClassName: String = "",
    val isUsingXposedModuleStatus: Boolean = true,
    val isUsingResourcesHook: Boolean = false
)