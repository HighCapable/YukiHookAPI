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
@file:Suppress("SetTextI18n")

package com.highcapable.yukihookapi.demo_module.hook

import android.app.Activity
import android.app.AlertDialog
import android.widget.Button
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringType
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onInit() {
        // 配置 YuKiHookAPI
        // 可简写为 configs {}
        YukiHookAPI.configs {
            // 全局调试用的 TAG
            // 在 Logcat 控制台过滤此 TAG 可找到详细日志
            debugTag = "YukiHookAPI-Demo"
            // 是否开启调试模式
            // 请注意 - 若作为发布版本请务必关闭调试功能防止对用户设备造成大量日志填充
            isDebug = true
            // 是否启用调试日志的输出功能
            // 一旦关闭后除手动日志外 API 将停止全部日志的输出 - 建议不要随意关掉这个选项
            // 虽然说对用户的设备写入大量日志是不正确的 - 但是没有日志你将无法调试
            // 关于日志是否会影响设备的流畅度一直是一个伪命题
            // 但是不设置这个选项可能会引起一些非议 - 建议不要关闭就是了
            isAllowPrintingLogs = true
            // 是否启用 [YukiHookModulePrefs] 的键值缓存功能
            // 若无和模块频繁交互数据在宿主重新启动之前建议开启
            // 若需要实时交互数据建议关闭或从 [YukiHookModulePrefs] 中进行动态配置
            isEnableModulePrefsCache = true
            // 是否启用当前 Xposed 模块自身 [Resources] 缓存功能
            // 一般情况下模块的 Resources 是不会改变的 - 但是在语言区域更改、分辨率更改等情况下 - 就需要刷新缓存
            // 若无上述需求 - 在宿主重新启动之前建议开启
            // 你可以手动调用 [PackageParam.refreshModuleAppResources] 来刷新缓存
            isEnableModuleAppResourcesCache = true
            // 是否启用 Hook Xposed 模块激活等状态功能
            // 为原生支持 Xposed 模块激活状态检测 - 此功能默认启用
            // 关闭后你将不能再在模块环境中使用 [YukiHookAPI.Status] 中的功能
            // 功能启用后 - 将会在宿主启动时自动 Hook [YukiHookModuleStatus]
            isEnableHookModuleStatus = true
            // 是否启用当前 Xposed 模块与宿主交互的 [YukiHookDataChannel] 功能
            // 请确保 Xposed 模块的 [Application] 继承于 [ModuleApplication] 才能有效
            // 此功能默认启用 - 关闭后将不会在功能初始化的时候装载 [YukiHookDataChannel]
            // 功能启用后 - 将会在宿主启动时自动 Hook [Application] 的生命周期方法进行注册
            isEnableDataChannel = true
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
            // 装载 APP Zygote 事件
            loadZygote {
                // 得到需要 Hook 的 Class
                ActivityClass.hook {
                    injectMember {
                        method {
                            name = "onCreate"
                            param(BundleClass)
                        }
                        afterHook {
                            // 在 [Activity] 标题后方加入文字
                            instance<Activity>().apply { title = "$title [Active]" }
                        }
                    }
                }
                // 得到需要 Hook 的 Resources
                // 需要 Hook Framework 支持 Resources Hook(资源钩子) 才能成功
                // 在 Zygote 中的 Resources Hook 只需要 Hook Framework 支持 - 无需启用此功能
                resources().hook {
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // 设置条件
                        conditions {
                            name = "sym_def_app_icon"
                            mipmap()
                        }
                        // 替换为当前模块的 Resources
                        // 模块的 Resources 可以使用 R8 混淆 - 结果不受影响
                        replaceToModuleResource(R.mipmap.ic_icon)
                    }
                }
            }
            // 装载需要 Hook 的 APP
            loadApp(name = "com.highcapable.yukihookapi.demo_app") {
                // 得到需要 Hook 的 Class
                findClass(name = "$packageName.ui.MainActivity").hook {
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getFirstText"
                            emptyParam()
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
                        // 在执行方法之后拦截
                        afterHook {
                            if (prefs.getBoolean("show_dialog_when_demo_app_opend"))
                                AlertDialog.Builder(instance())
                                    .setTitle("Hooked")
                                    .setMessage(
                                        "This App has been hooked!\n\n" +
                                                "Hook Framework: ${YukiHookAPI.Status.executorName}\n\n" +
                                                "Xposed API Version: ${YukiHookAPI.Status.executorVersion}\n\n" +
                                                "Support Resources Hook: ${YukiHookAPI.Status.isSupportResourcesHook}"
                                    )
                                    .setPositiveButton("OK", null)
                                    .show()
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
                            emptyParam()
                            returnType = UnitType
                        }
                        // 拦截整个方法
                        replaceUnit {
                            AlertDialog.Builder(instance())
                                .setTitle("Hooked")
                                .setMessage("I am hook your toast showing!")
                                .setPositiveButton("OK", null)
                                .setNegativeButton("SEND MSG TO MODULE") { _, _ ->
                                    dataChannel.put(DataConst.TEST_CN_DATA, value = "I am host, can you hear me?")
                                }.show()
                        }
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        method {
                            name = "getDataText"
                            emptyParam()
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
                        method {
                            name = "getSuperString"
                            emptyParam()
                            // 这个方法不在当前的 Class
                            // 只需要设置此查找条件即可自动前往当前 Class 的父类查找
                            // 由于演示的方法只会在父类存在 - 所以可以设置仅查找父类 isOnlySuperClass = true 节省时间
                            // 如果想继续尝试查找当前 Class - 请删除 isOnlySuperClass = true
                            superClass(isOnlySuperClass = true)
                        }
                        // 执行替换 Hook
                        replaceTo(any = "I am hook super class method")
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        allMethods(name = "getTestResultFirst")
                        // 执行替换 Hook
                        replaceTo(any = "I am hook all methods first")
                    }
                    // 注入要 Hook 的方法
                    injectMember {
                        allMethods(name = "getTestResultLast")
                        // 执行替换 Hook
                        replaceTo(any = "I am hook all methods last")
                    }
                }
                // 得到需要 Hook 的 Resources
                // 需要 Hook Framework 支持 Resources Hook(资源钩子) 且启用此功能才能成功
                resources().hook {
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // 设置条件
                        conditions {
                            name = "activity_main"
                            layout()
                        }
                        // Hook 布局装载器
                        injectAsLayout {
                            // 替换布局中指定 Id 的按钮文本
                            findViewByIdentifier<Button>(name = "app_demo_button")?.text = "Touch Me!"
                        }
                    }
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // 设置条件
                        conditions {
                            name = "test_string"
                            string()
                        }
                        // 替换为指定的 Resources
                        replaceTo(any = "I am hook to make your Happy")
                    }
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // 设置条件
                        conditions {
                            name = "ic_face_unhappy"
                            mipmap()
                        }
                        // 替换为当前模块的 Resources
                        // 模块的 Resources 可以使用 R8 混淆 - 结果不受影响
                        replaceToModuleResource(R.mipmap.ic_face_happy)
                    }
                }
            }
        }
    }

    // 可选的监听功能 - 如不需要你可以不重写这个方法
    // Demo 中实现这个方法仅为了介绍它的功能
    override fun onXposedEvent() {
        // (可选) 监听原生 Xposed API 的装载事件
        // 若你的 Hook 事件中存在需要兼容的原生 Xposed 功能 - 可在这里实现
        // 不要在这里处理任何 YukiHookAPI 的事件 - 请在 onHook 中完成
        YukiXposedEvent.events {
            onInitZygote {
                // 实现监听 initZygote 事件
            }
            onHandleLoadPackage {
                // 实现监听 handleLoadPackage 事件
                // 可调用原生 Xposed API 方法
                // XposedHelpers.findAndHookMethod("className", it.classLoader, "methodName", object : XC_MethodHook())
            }
            onHandleInitPackageResources {
                // 实现监听 handleInitPackageResources 事件
                // 可调用原生 Xposed API 方法
                // it.res.setReplacement(0x7f060001, "replaceMent")
            }
        }
    }
}