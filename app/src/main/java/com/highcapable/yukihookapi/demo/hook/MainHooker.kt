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
@file:Suppress("unused")

package com.highcapable.yukihookapi.demo.hook

import com.highcapable.yukihookapi.demo.BuildConfig
import com.highcapable.yukihookapi.demo.InjectTest
import com.highcapable.yukihookapi.demo.MainActivity
import com.highcapable.yukihookapi.hook.entity.YukiBaseHooker
import com.highcapable.yukihookapi.hook.type.BundleClass
import com.highcapable.yukihookapi.hook.type.StringType
import com.highcapable.yukihookapi.hook.type.UnitType

// for test
class MainHooker : YukiBaseHooker() {

    override fun onHook() =
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
                        args(index = 0).set("改了前面的")
                        args(index = 1).set("改了后面的")
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
            InjectTest::class.java.hook {
                injectMember {
                    constructor { param(StringType) }
                    beforeHook { args().set("构造方法已被 Hook 成功") }
                }
            }
            loadHooker(hooker = TestChildHooker())
        }
}