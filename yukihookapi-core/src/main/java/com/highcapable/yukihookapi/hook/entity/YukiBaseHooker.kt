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
package com.highcapable.yukihookapi.hook.entity

import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.log.YLog
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
        runCatching { onHook() }.onFailure { YLog.innerE("An exception occurred in $this", it) }
    }

    /** 子类 Hook 开始 */
    abstract fun onHook()
}