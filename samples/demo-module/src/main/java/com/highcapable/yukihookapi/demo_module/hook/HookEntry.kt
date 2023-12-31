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
 * This file is created by fankes on 2022/2/9.
 */
@file:Suppress("SetTextI18n")

package com.highcapable.yukihookapi.demo_module.hook

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.widget.Button
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.annotation.xposed.InjectYukiHookWithXposed
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.demo_module.hook.factory.compatStyle
import com.highcapable.yukihookapi.demo_module.ui.MainActivity
import com.highcapable.yukihookapi.hook.core.annotation.LegacyResourcesHook
import com.highcapable.yukihookapi.hook.factory.applyModuleTheme
import com.highcapable.yukihookapi.hook.factory.constructor
import com.highcapable.yukihookapi.hook.factory.field
import com.highcapable.yukihookapi.hook.factory.method
import com.highcapable.yukihookapi.hook.factory.registerModuleAppActivities
import com.highcapable.yukihookapi.hook.type.android.ActivityClass
import com.highcapable.yukihookapi.hook.type.android.BundleClass
import com.highcapable.yukihookapi.hook.type.java.StringArrayClass
import com.highcapable.yukihookapi.hook.type.java.StringClass
import com.highcapable.yukihookapi.hook.type.java.UnitType
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit

@InjectYukiHookWithXposed(isUsingResourcesHook = true)
object HookEntry : IYukiHookXposedInit {

    override fun onInit() {
        // Configure YuKiHookAPI
        // Can be shortened to configs {}
        // 配置 YuKiHookAPI
        // 可简写为 configs {}
        YukiHookAPI.configs {
            // Configure YukiHookLogger
            // 配置 YukiHookLogger
            debugLog {
                // TAG for global debugging
                // Filter this TAG in the Logcat console to find detailed logs
                // 全局调试用的 TAG
                // 在 Logcat 控制台过滤此 TAG 可找到详细日志
                tag = "YukiHookAPI-Demo"
                // Whether to enable the output function of the debug log, enabled by default
                // When closed, the API will stop output all logs, it is recommended not to disable this option arbitrarily
                // Although it is incorrect to say that a lot of logs are written to the user's device, but NO LOG NO DEBUGGABLE
                // Whether the log will affect the fluency of the device has always been a false proposition
                // But if not has this option may cause some criticism, it is recommended not to close it
                // 是否启用调试日志的输出功能 - 默认启用
                // 一旦关闭后除手动日志外 API 将停止全部日志的输出 - 建议不要随意关掉这个选项
                // 虽然说对用户的设备写入大量日志是不正确的 - 但是没有日志你将无法调试
                // 关于日志是否会影响设备的流畅度一直是一个伪命题
                // 但是不设置这个选项可能会引起一些非议 - 建议不要关闭就是了
                isEnable = true
                // Whether to enable the logging function of debug log, not enabled by default
                // When enabled, all available logs and exception stacks will be recorded in memory
                // Please note, excessive logging may slow down the Host App or cause frequent GCs
                // After enabling, you can call [YukiHookLogger.saveToFile] to save the log to file in real-time
                // Or use [YukiHookLogger.contents] to get the real-time log file contents
                // 是否启用调试日志的记录功能 - 默认不启用
                // 开启后将会在内存中记录全部可用的日志和异常堆栈
                // 请注意 - 过量的日志可能会导致宿主运行缓慢或造成频繁 GC
                // 开启后你可以调用 [YukiHookLogger.saveToFile] 实时保存日志到文件或使用 [YukiHookLogger.contents] 获取实时日志文件
                isRecord = false
                // Customize the elements displayed by the debug log externally
                // Only valid for logging and [XposedBridge.log]
                // The arrangement of log elements will be displayed in the order you set in [item]
                // You can also leave [item] blank to not show all elements except the log content
                // Available elements are: [TAG], [PRIORITY], [PACKAGE_NAME], [USER_ID]
                // 自定义调试日志对外显示的元素
                // 只对日志记录和 [XposedBridge.log] 生效
                // 日志元素的排列将按照你在 [item] 中设置的顺序进行显示
                // 你还可以留空 [item] 以不显示除日志内容外的全部元素
                // 可用的元素有：[TAG]、[PRIORITY]、[PACKAGE_NAME]、[USER_ID]
                elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
            }
            // Whether to enable debug mode, not enabled by default
            // Note: Please selectively enable this feature, this can cause I/O performance issues caused by continuous output logs
            // Please note, for a release build, be sure to turn off this function to prevent a lot of log stuffing on the user's device
            // To facilitate testing whether the module is activated, the demo enables this function by default
            // 是否启用调试模式 - 默认不启用
            // 注意：请选择性启用此功能 - 这会导致持续输出日志造成的 I/O 性能问题
            // 若作为发布版本请务必关闭此功能防止对用户设备造成大量日志填充
            // 为方便测试模块是否激活 - Demo 默认启用此功能
            isDebug = true
            // Whether to enable the current Xposed Module's own [Resources] cache function
            // Under normal circumstances, the resources of the Module App will not change
            // But in the case of locale changes, screen size changes. Etc. you need to refresh the cache
            // If none of the above requirements, it is recommended to enable it before the Host App restarts
            // You can manually call [PackageParam.refreshModuleAppResources] to refresh the cache
            // 是否启用当前 Xposed 模块自身 [Resources] 缓存功能
            // 一般情况下模块的 Resources 是不会改变的 - 但是在语言区域更改、分辨率更改等情况下 - 就需要刷新缓存
            // 若无上述需求 - 在宿主重新启动之前建议开启
            // 你可以手动调用 [PackageParam.refreshModuleAppResources] 来刷新缓存
            isEnableModuleAppResourcesCache = true
            // Whether to enable Hook [SharedPreferences]
            // Enable will force [SharedPreferences] file permissions to be adjusted to [Context.MODE_WORLD_READABLE] (0664) at Module App startup
            // This is an optional experimental feature, this feature is not enabled by default
            // Only used to fix some systems that may still have file permission errors after enabling New XSharedPreferences
            // If you can use [YukiHookPrefsBridge] normally, it is not recommended to enable this feature
            // 是否启用 Hook [SharedPreferences]
            // 启用后将在模块启动时强制将 [SharedPreferences] 文件权限调整为 [Context.MODE_WORLD_READABLE] (0664)
            // 这是一个可选的实验性功能 - 此功能默认不启用
            // 仅用于修复某些系统可能会出现在启用了 New XSharedPreferences 后依然出现文件权限错误问题 - 若你能正常使用 [YukiHookPrefsBridge] 就不建议启用此功能
            isEnableHookSharedPreferences = false
            // Whether to enable the [YukiHookDataChannel] function of the current Xposed Module interacting with the Host App
            // Please make sure the Xposed Module's [Application] extends [ModuleApplication] to be valid
            // This feature is enabled by default, when disabled it will not load [YukiHookDataChannel] when the feature is initialized
            // When the feature is enabled, it will automatically register the Hook [Application] lifecycle method when the Host App starts
            // 是否启用当前 Xposed 模块与宿主交互的 [YukiHookDataChannel] 功能
            // 请确保 Xposed 模块的 [Application] 继承于 [ModuleApplication] 才能有效
            // 此功能默认启用 - 关闭后将不会在功能初始化的时候装载 [YukiHookDataChannel]
            // 功能启用后 - 将会在宿主启动时自动 Hook [Application] 的生命周期方法进行注册
            isEnableDataChannel = true
        }
    }

    @OptIn(LegacyResourcesHook::class)
    override fun onHook() {
        // Start your hook
        // Can be shortened to encase {}
        // 开始你的 Hook
        // 可简写为 encase {}
        YukiHookAPI.encase {
            // Load App Zygote event
            // 装载 APP Zygote 事件
            loadZygote {
                // Find Class to hook
                // 得到需要 Hook 的 Class
                ActivityClass.method {
                    name = "onCreate"
                    param(BundleClass)
                }.hook {
                    after {
                        // Add text after the [Activity] title
                        // 在 [Activity] 标题后方加入文字
                        instance<Activity>().apply { title = "$title [Active]" }
                    }
                }
                // Find Resources to hook
                // Requires Hook Framework to support Resources Hook to succeed
                // Resources Hook in Zygote only needs Hook Framework support, no need to enable this feature
                // 得到需要 Hook 的 Resources
                // 需要 Hook Framework 支持 Resources Hook(资源钩子) 才能成功
                // 在 Zygote 中的 Resources Hook 只需要 Hook Framework 支持 - 无需启用此功能
                resources().hook {
                    // Inject the Resources to be hooked
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // Set the conditions
                        // 设置条件
                        conditions {
                            name = "sym_def_app_icon"
                            mipmap()
                        }
                        // Replace with the Resources of the current Module App
                        // Module App's Resources can be obfuscated with R8, results are not affected
                        // 替换为当前模块的 Resources
                        // 模块的 Resources 可以使用 R8 混淆 - 结果不受影响
                        replaceToModuleResource(R.mipmap.ic_icon)
                    }
                }
            }
            // Load the app to be hooked
            // 装载需要 Hook 的 APP
            loadApp(name = "com.highcapable.yukihookapi.demo_app") {
                // Register Activity Proxy
                // 注册模块 Activity 代理
                onAppLifecycle {
                    onCreate { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) registerModuleAppActivities() }
                }
                // Find Class to hook
                // 得到需要 Hook 的 Class
                "$packageName.ui.MainActivity".toClass().apply {
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getFirstText"
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        // Replaced hook
                        // 执行替换 Hook
                        replaceTo(any = "Hello YukiHookAPI!")
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "onCreate"
                        param(BundleClass)
                    }.hook {
                        // Before hook the method
                        // 在方法执行之前拦截
                        before {
                            // The instance is "$packageName.ui.MainActivity"
                            // We used "apply" function on the top
                            // 当前实例为 "$packageName.ui.MainActivity"
                            // 我们在顶部使用了 "apply" 方法
                            field {
                                name = "secondText"
                                type = StringClass
                            }.get(instance).set("I am hook result")
                        }
                        // After hook the method
                        // 在执行方法之后拦截
                        after {
                            if (prefs.getBoolean("show_dialog_when_demo_app_opend"))
                                MaterialAlertDialogBuilder(instance<Activity>().applyModuleTheme(R.style.Theme_Default))
                                    .setTitle("Hooked")
                                    .setMessage(
                                        "This App has been hooked!\n\n" +
                                            "Hook Framework: ${YukiHookAPI.Status.Executor.name}\n\n" +
                                            "Xposed API Version: ${YukiHookAPI.Status.Executor.apiLevel}\n\n" +
                                            "Support Resources Hook: ${YukiHookAPI.Status.isSupportResourcesHook}"
                                    )
                                    .setPositiveButton("OK", null)
                                    .show().compatStyle()
                        }
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getRegularText"
                        param(StringClass)
                        returnType = StringClass
                    }.hook {
                        // Before hook the method
                        // 在方法执行之前拦截
                        before {
                            // Set the 0th param (recomment)
                            // 设置 0 号 param (推荐)
                            args().first().set("I am hook method param")
                            // The following method is also ok
                            // 下面这种方式也是可以的
                            // args[0] = "I am hook method param"
                        }
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getArray"
                        param(StringArrayClass)
                        returnType = StringArrayClass
                    }.hook {
                        // Before hook the method
                        // 在方法执行之前拦截
                        before {
                            // Set the 0th param
                            // 设置 0 号 param
                            args().first().array<String>()[0] = "peach"
                        }
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "toast"
                        emptyParam()
                        returnType = UnitType
                    }.hook {
                        // Intercept the entire method
                        // 拦截整个方法
                        replaceUnit {
                            instance<Activity>().applyModuleTheme(R.style.Theme_Default).also { context ->
                                MaterialAlertDialogBuilder(context)
                                    .setTitle("Hooked")
                                    .setMessage("I am hook your toast showing!")
                                    .apply {
                                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                                            setPositiveButton("START PARASITIC") { _, _ ->
                                                MaterialAlertDialogBuilder(context)
                                                    .setTitle("Start Parasitic")
                                                    .setMessage("This function will start MainActivity that exists in the module app.")
                                                    .setPositiveButton("YES") { _, _ ->
                                                        context.startActivity(Intent(context, MainActivity::class.java))
                                                    }.setNegativeButton("NO", null).show().compatStyle()
                                            }
                                    }.setNegativeButton("SEND MSG TO MODULE") { _, _ ->
                                        dataChannel.put(DataConst.TEST_CN_DATA, value = "I am host, can you hear me?")
                                    }.setNeutralButton("REMOVE HOOK") { _, _ ->
                                        removeSelf()
                                    }.show().compatStyle()
                            }
                        }
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getDataText"
                        emptyParam()
                        returnType = StringClass
                    }.hook {
                        // Replaced hook
                        // 执行替换 Hook
                        replaceTo(prefs.get(DataConst.TEST_KV_DATA))
                    }
                }
                // Find Class to hook
                // 得到需要 Hook 的 Class
                "$packageName.test.Main".toClass().apply {
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    constructor { param(StringClass) }.hook {
                        // Before hook the method
                        // 在方法执行之前拦截
                        before {
                            // Set the 0th param
                            // 设置 0 号 param
                            args().first().set("I am hook constructor param")
                        }
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getSuperString"
                        emptyParam()
                        // This method is not in the current Class
                        // Just set this find condition to automatically go to the super class of the current Class to find
                        // Since the method shown will only exist in the super class
                        // So you can set only the super class to find isOnlySuperClass = true to save time
                        // If you want to keep trying to find the current Class, please remove isOnlySuperClass = true
                        // 这个方法不在当前的 Class
                        // 只需要设置此查找条件即可自动前往当前 Class 的父类查找
                        // 由于演示的方法只会在父类存在 - 所以可以设置仅查找父类 isOnlySuperClass = true 节省时间
                        // 如果想继续尝试查找当前 Class - 请删除 isOnlySuperClass = true
                        superClass(isOnlySuperClass = true)
                    }.hook {
                        // Replaced hook
                        // 执行替换 Hook
                        replaceTo(any = "I am hook super class method")
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getTestResultFirst"
                    }.hookAll {
                        // Replaced hook
                        // 执行替换 Hook
                        replaceTo(any = "I am hook all methods first")
                    }
                    // Inject the method to be hooked
                    // 注入要 Hook 的方法
                    method {
                        name = "getTestResultLast"
                    }.hookAll {
                        // Replaced hook
                        // 执行替换 Hook
                        replaceTo(any = "I am hook all methods last")
                    }
                }
                // Find Resources to hook
                // Requires Hook Framework to support Resources Hook to succeed
                // Resources Hook in Zygote only needs Hook Framework support, no need to enable this feature
                // 得到需要 Hook 的 Resources
                // 需要 Hook Framework 支持 Resources Hook(资源钩子) 且启用此功能才能成功
                resources().hook {
                    // Inject the Resources to be hooked
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // Set the conditions
                        // 设置条件
                        conditions {
                            name = "activity_main"
                            layout()
                        }
                        // Hook layout inflater
                        // Hook 布局装载器
                        injectAsLayout {
                            // Replace the button text with the specified Id in this layout
                            // 替换布局中指定 Id 的按钮文本
                            findViewByIdentifier<Button>(name = "app_demo_button")?.text = "Touch Me!"
                        }
                    }
                    // Inject the Resources to be hooked
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // Set the conditions
                        // 设置条件
                        conditions {
                            name = "test_string"
                            string()
                        }
                        // Replace with the specified Resources
                        // 替换为指定的 Resources
                        replaceTo(any = "I am hook to make you happy")
                    }
                    // Inject the Resources to be hooked
                    // 注入要 Hook 的 Resources
                    injectResource {
                        // Set the conditions
                        // 设置条件
                        conditions {
                            name = "ic_face_unhappy"
                            mipmap()
                        }
                        // Replace with the Resources of the current Module App
                        // Module App's Resources can be obfuscated with R8, results are not affected
                        // 替换为当前模块的 Resources
                        // 模块的 Resources 可以使用 R8 混淆 - 结果不受影响
                        replaceToModuleResource(R.mipmap.ic_face_happy)
                    }
                }
            }
        }
    }

    // Optional listener function, you can not override this method if you don't need it
    // This method is implemented in Demo only to introduce its function
    // 可选的监听功能 - 如不需要你可以不重写这个方法
    // Demo 中实现这个方法仅为了介绍它的功能
    override fun onXposedEvent() {
        // (Optional) Listen to the loading event of the native Xposed API
        // If your Hook event has native Xposed functionality that needs to be compatible, it can be implemented here
        // Don't handle any YukiHookAPI events here, do it in onHook
        // (可选) 监听原生 Xposed API 的装载事件
        // 若你的 Hook 事件中存在需要兼容的原生 Xposed 功能 - 可在这里实现
        // 不要在这里处理任何 YukiHookAPI 的事件 - 请在 onHook 中完成
        YukiXposedEvent.events {
            onInitZygote {
                // Implement listening for the initZygote event
                // 实现监听 initZygote 事件
            }
            onHandleLoadPackage {
                // Implement listener handleLoadPackage event
                // Call native Xposed API methods
                // 实现监听 handleLoadPackage 事件
                // 可调用原生 Xposed API 方法
                // XposedHelpers.findAndHookMethod("className", it.classLoader, "methodName", object : XC_MethodHook())
            }
            onHandleInitPackageResources {
                // Implement the listener handleInitPackageResources event
                // Call native Xposed API methods
                // 实现监听 handleInitPackageResources 事件
                // 可调用原生 Xposed API 方法
                // it.res.setReplacement(0x7f060001, "replaceMent")
            }
        }
    }
}