# 介绍

> `YukiHookAPI` 是一个集成化的 Hook API 框架，本身不提供任何 Hook 功能，需要 Xposed 相关基础 API 的支持。

## 背景

这是一个使用 Kotlin 基于 Xposed API 重新构建的高效 Hook API，同时为 Xposed 模块的开发打造了丰富的功能扩展。

名称取自 [《ももくり》女主 栗原 雪(Yuki)](https://www.bilibili.com/bangumi/play/ss5016)。

前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源。

## 用途

`YukiHookAPI` 完全采用 Kotlin **lambda** 语法构建。

抛弃原始不太友好的 `XposedHelpers`，你可以使用它来轻松创建 Xposed 模块以及轻松实现自定义 Hook API。

## 语言要求

请使用 Kotlin，框架部分代码构成同样兼容 Java 但基础 Hook 场景的实现**可能完全无法使用**。

文档全部的 Demo 示例代码都将使用 Kotlin 进行描述，如果你完全不会使用 Kotlin 那你将有可能无法使用 `YukiHookAPI`。

部分 Java Demo 代码可在 [这里](https://github.com/fankes/YukiHookAPI/tree/master/samples/demo-module/src/main/java/com/highcapable/yukihookapi/demo_module/hook/java) 找到，但不推荐使用。

## 灵感来源

以前，我们在构建 Xposed 模块的时候，首先需要在 `assets` 下创建 `xposed_init` 文件。

然后，将自己的入口类名手动填入文件中，使用 `XposedHelpers` 去实现我们的 Hook 逻辑。

自 Kotlin 作为 Android 主要开发语言以来，这套 API 用起来确实已经不是很优雅了。

有没有什么 **好用、轻量、优雅** 的解决办法呢？

本着这样的想法，`YukiHookAPI` 诞生了。

现在，我们只需要编写少量的代码，一切时间开销和花费交给自动化处理。

借助 Kotlin 优雅的 **lambda** 写法以及 `YukiHookAPI`，可以让你的 Hook 逻辑更加美观清晰。

> 示例如下

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = encase {
        loadZygote {
            ActivityClass.hook {
                injectMember {
                    method {
                        name = "onCreate"
                        param(BundleClass)
                    }
                    beforeHook {
                        // Your code here.
                    }
                    afterHook {
                        // Your code here.
                    }
                }
            }
            resources().hook {
                injectResource {
                    conditions {
                        name = "sym_def_app_icon"
                        mipmap()
                    }
                    replaceToModuleResource(R.mipmap.ic_launcher)
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
                    beforeHook {
                        // Your code here.
                    }
                    afterHook {
                        // Your code here.
                    }
                }
            }
            resources().hook {
                injectResource {
                    conditions {
                        name = "ic_launcher"
                        mipmap()
                    }
                    replaceToModuleResource(R.mipmap.ic_launcher)
                }
            }
        }
    }
}
```

:::
::: code-group-item Rovo89 Xposed API

```kotlin
class HookEntry : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    private lateinit var moduleResources: XModuleResources

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
        moduleResources = XModuleResources.createInstance(sparam.modulePath, null)
        XResources.setSystemWideReplacement(
            "android", "mipmap", "sym_def_app_icon",
            moduleResources.fwd(R.mipmap.ic_launcher)
        )
        XposedHelpers.findAndHookMethod(
                Activity::class.java.name,
                null, "onCreate",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }
                })
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.browser")
            XposedHelpers.findAndHookMethod(
                Activity::class.java.name,
                lpparam.classLoader, "onCreate",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }
                })
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName == "com.android.browser")
            resparam.res.setReplacement(
                "com.android.browser", "mipmap", "ic_launcher",
                moduleResources.fwd(R.mipmap.ic_launcher)
            )
    }
}
```

:::
::::

是的，你没有看错，仅仅就需要这些代码，就能完全取代传统的 Xposed API 实现同样的功能。

现在，借助高效强大的 `YukiHookAPI`，你就可以实现一个非常简单的 Xposed 模块。