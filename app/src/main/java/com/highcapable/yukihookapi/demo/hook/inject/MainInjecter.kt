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
 * This file is Created by fankes on 2022/2/4.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.demo.hook.inject

import android.app.AlertDialog
import android.widget.Toast
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.demo.*
import com.highcapable.yukihookapi.hook.factory.encase
import com.highcapable.yukihookapi.hook.factory.findMethod
import com.highcapable.yukihookapi.hook.type.ActivityClass
import com.highcapable.yukihookapi.hook.type.BundleClass
import com.highcapable.yukihookapi.hook.type.StringType
import com.highcapable.yukihookapi.hook.type.UnitType
import com.highcapable.yukihookapi.hook.xposed.proxy.YukiHookXposedInitProxy

// for test
@InjectYukiHookWithXposed
class MainInjecter : YukiHookXposedInitProxy {

    override fun onHook() {
        // 设置模式
        YukiHookAPI.configs {
            debugTag = "YukiSuki"
            isDebug = true
        }
        // 方案 1
        // encase(MainHooker(), SecondHooker())
        // 方案 2
        encase {
            loadApp(name = BuildConfig.APPLICATION_ID) {
                MainActivity::class.java.hook {
                    injectMember {
                        method {
                            name = "onCreate"
                            param(BundleClass)
                        }
                        beforeHook {
                            field {
                                name = "a"
                                type = StringType
                            }.set(instance, "这段文字被修改成功了")
                        }
                    }
                    injectMember {
                        method {
                            name = "xptest"
                            returnType = StringType
                        }
                        replaceTo(prefs.getString(key = "data", default = "获取 Hook：没数据"))
                    }
                    injectMember {
                        method {
                            name = "toast"
                            returnType = UnitType
                        }
                        intercept()
                    }
                    injectMember {
                        method {
                            name = "a"
                            param(StringType, StringType)
                            returnType = StringType
                        }
                        beforeHook {
                            args(index = 0).set("✌️改了前面的")
                            args(index = 1).set("✌️改了后面的")
                        }
                    }
                    injectMember {
                        method {
                            name = "test"
                            returnType = StringType
                        }
                        replaceTo("这段文字已被 Hook 成功")
                    }
                    injectMember {
                        method {
                            name = "test"
                            param(StringType)
                            returnType = StringType
                        }
                        beforeHook { args().set("方法参数已被 Hook 成功") }
                    }
                }
                InjectLucky::class.java.hook {
                    injectMember {
                        allConstructors()
                        afterHook {
                            field {
                                name = "string"
                                type = StringType
                            }.set(instance, "内容被改掉了")
                        }
                    }
                }
                InjectTest::class.java.hook {
                    injectMember {
                        constructor { param(StringType) }
                        beforeHook { args().set("构造方法已被 Hook 成功") }
                    }
                }
                InjectNoParamTest::class.java.hook {
                    injectMember {
                        constructor()
                        afterHook {
                            field {
                                name = "test"
                                type = StringType
                            }.set(instance, "内容被改掉了")
                        }
                    }
                }
                findClass(name = "$packageName.InjectTestName").hook {
                    injectMember {
                        constructor { param(StringType) }
                        beforeHook { args().set("构造方法已被 Hook 成功 [2]") }
                    }
                }
            }
            loadApp(name = "com.android.browser") {
                ActivityClass.hook {
                    injectMember {
                        method {
                            name = "onCreate"
                            param(BundleClass)
                        }
                        afterHook {
                            AlertDialog.Builder(instance())
                                .setCancelable(false)
                                .setTitle("测试 Hook")
                                .setMessage(
                                    "Hook 已成功\n" +
                                            "test_key:${prefs.getBoolean("test_key")}\n" +
                                            "test_key_name:${prefs.getString("test_key_name", "默认值")}"
                                )
                                .setPositiveButton("OK") { _, _ ->
                                    Toast.makeText(instance(), "Hook Success", Toast.LENGTH_SHORT).show()
                                }.show()
                        }
                    }
                    injectMember {
                        member = hookClass.findMethod(name = "onStart")
                        afterHook {
                            Toast.makeText(instance(), "手动 Hook", Toast.LENGTH_SHORT).show()
                        }
                    }.failures {
                        onConductFailure { _, _ -> }
                        onHookingFailure {}
                        onAllFailure {}
                        ignoredConductFailure()
                        ignoredHookingFailure()
                        ignoredAllFailure()
                    }
                }
            }
        }
    }
}