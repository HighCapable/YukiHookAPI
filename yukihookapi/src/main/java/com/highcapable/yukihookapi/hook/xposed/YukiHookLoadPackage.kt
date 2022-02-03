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
@file:Suppress("EXPERIMENTAL_API_USAGE")

package com.highcapable.yukihookapi.hook.xposed

import androidx.annotation.Keep
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.factory.clazz
import com.highcapable.yukihookapi.hook.factory.findConstructor
import com.highcapable.yukihookapi.hook.factory.findMethod
import com.highcapable.yukihookapi.hook.log.loggerE
import com.highcapable.yukihookapi.hook.proxy.YukiHookXposedInitProxy
import com.highcapable.yukihookapi.hook.type.BooleanType
import com.highcapable.yukihookapi.param.PackageParam
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * 接管 Xposed 的 [IXposedHookLoadPackage] 入口
 *
 * 你可以使用 [YukiHookAPI.encase] 或在 [YukiHookXposedInitProxy] 中监听模块开始装载
 *
 * 需要标识 Hook 入口的类 - 请声明注释 [InjectYukiHookWithXposed]
 */
@Keep
class YukiHookLoadPackage : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) return
        runCatching {
            /** 执行入口方法 */
            hookEntryClassName().clazz.apply {
                findMethod(name = "onHook")?.invoke(findConstructor()?.newInstance())
            }
        }.onFailure {
            loggerE(msg = "Try to load ${hookEntryClassName()} Failed", e = it)
        }
        /** 装载 APP Hook 实体类 */
        PackageParam(lpparam).apply {
            /** Hook 模块激活状态 */
            loadApp(name = YukiHookAPI.modulePackageName) {
                YukiHookModuleStatus::class.java.hook {
                    injectMember {
                        method {
                            name = "isActive"
                            returnType = BooleanType
                        }
                        replaceToTrue()
                    }.onAllFailure { loggerE(msg = "Try to Hook ModuleStatus Failed", e = it) }
                }
            }
            /** 设置装载回调 */
            YukiHookAPI.packageParamCallback?.invoke(this)
        }
    }

    /**
     * 获得目标装载类名 - 通过 APT 自动设置 TODO 待实现
     * @return [String] 目标装载类名
     */
    @Keep
    private fun hookEntryClassName() = "com.highcapable.yukihookapi.demo.hook.inject.MainInjecter"
}