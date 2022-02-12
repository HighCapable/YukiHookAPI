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
@file:Suppress("unused")

package com.highcapable.yukihookapi.demo_module.hook

import android.app.AlertDialog
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

@InjectYukiHookWithXposed
class MainHook : YukiHookXposedInitProxy {

    override fun onHook() {
        // 配置 YuKiHookAPI
        YukiHookAPI.configs {
            // 全局调试用的 TAG
            debugTag = "YukiHookAPI"
            // 是否开启全局调试日志输出功能
            isDebug = true
        }
        // 开始你的 Hook
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
                            args().set("I am hook method param")
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
                        replaceTo(prefs.getString(key = "test_data", default = "Test data is nothing"))
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
                            args().set("I am hook constructor param")
                        }
                    }
                }
            }
        }
    }
}