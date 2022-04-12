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
 * This file is Created by fankes on 2022/2/9.
 */
package com.highcapable.yukihookapi.demo_module.hook

import android.app.AlertDialog
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

@InjectYukiHookWithXposed
class HookEntry : YukiHookXposedInitProxy {

    override fun onInit() {
        // 配置 YuKiHookAPI
        // 可简写为 configs {}
        YukiHookAPI.configs {
            // 全局调试用的 TAG
            debugTag = "YukiHookAPI"
            // 是否开启调试模式
            isDebug = true
            // 是否启用调试日志的输出功能
            isAllowPrintingLogs = true
            // 是否启用 [YukiHookModulePrefs] 的键值缓存功能
            // 若无和模块频繁交互数据在宿主重新启动之前建议开启
            // 若需要实时交互数据建议关闭或从 [YukiHookModulePrefs] 中进行动态配置
            isEnableModulePrefsCache = true
            // 是否启用 [Member] 缓存功能
            // 为防止 [Member] 复用过高造成的系统 GC 问题 - 此功能默认启用
            // 除非缓存的 [Member] 发生了混淆的问题 - 否则建议启用
            isEnableMemberCache = true
        }
    }

    override fun onHook() {
        // 开始你的 Hook
        // 可简写为 encase {}
        YukiHookAPI.encase {
            // 装载需要 Hook 的 APP
            loadApp(name = "com.highcapable.yukihookapi.demo_app") {
                // 得到需要 Hook 的 Class
                findClass(name = "$packageName.ui.MainActivity").hook {
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getFirstText"
                            returnType = StringType
                        }
                        // 执行替换 Hook
                        replaceTo(any = "Hello YukiHookAPI!")
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "onCreate"
                            param(BundleClass)
                        }
                        // 在方法执行之前拦截
                        beforeHook {
                            field {
                                name = "secondText"
                                type = StringType
                            }.get(instance).set("I am hook result")
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getRegularText"
                            param(StringType)
                            returnType = StringType
                        }
                        // 在方法执行之前拦截
                        beforeHook {
                            // 设置 0 号 param
                            args().first().set("I am hook method param")
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getArray"
                            param(StringArrayClass)
                            returnType = StringArrayClass
                        }
                        // 在方法执行之前拦截
                        beforeHook {
                            // 设置 0 号 param
                            args().first().array<String>()[0] = "peach"
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "toast"
                            returnType = UnitType
                        }
                        // 拦截整个方法
                        replaceUnit {
                            AlertDialog.Builder(instance())
                                .setTitle("Hooked")
                                .setMessage("I am hook your toast showing")
                                .setPositiveButton("OK", null)
                                .show()
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getDataText"
                            returnType = StringType
                        }
                        // 执行替换 Hook
                        replaceTo(prefs.get(DataConst.TEST_KV_DATA))
                    }
                }
                // 得到需要 Hook 的 Class
                findClass(name = "$packageName.utils.Main").hook {
                    // 注入要 Hook 的方法
                    injectMember {
                        constructor { param(StringType) }
                        // 在方法执行之前拦截
                        beforeHook {
                            // 设置 0 号 param
                            args().first().set("I am hook constructor param")
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        allMethods(name = "getTestResultFirst")
                        // 执行替换 Hook
                        replaceTo("I am hook all methods first")
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        allMethods(name = "getTestResultLast")
                        // 执行替换 Hook
                        replaceTo("I am hook all methods last")
                    }
                }
            }
        }
    }
}