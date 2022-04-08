# 介绍

> 这是一个 Hook API 框架，本身不提供任何 Hook 功能，需要 Xposed 基础 API 的支持。

## 背景

这是一个使用 `Kotlin` 重新构建的高效 Xposed Hook API。

名称取自 [《ももくり》女主 栗原 雪(Yuki)](https://www.bilibili.com/bangumi/play/ss5016)。

前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源。

## 用途

`YukiHookAPI` 完全采用 `Kotlin` `lambda` 语法构建。

抛弃原始不太友好的 `XposedHelper`，你可以使用它来轻松创建 Xposed 模块以及轻松实现自定义 Hook API。

## 语言要求

请使用 `Kotlin`，框架部分代码构成同样兼容 `Java` 但基础 Hook 场景的实现<b>可能完全无法使用</b>。

文档全部的 Demo 示例代码都将使用 `Kotlin` 进行描述，如果你完全不会使用 `Kotlin` 那你将有可能无法使用 `YukiHookAPI`。

## 功能特性

- <b>Xposed 模块开发</b>

  自动构建程序可以帮你快速创建一个 Xposed 模块，完全省去配置入口类和 `xposed_init` 文件。

- <b>轻量优雅</b>

  拥有一套强大、优雅和人性化的 `Kotlin Lambda Hook API`，可以帮你快速实现 `Method`、`Constructor`、`Field` 的查找以及 Hook。

- <b>高效调试</b>

  拥有丰富的调试日志功能，细到每个 Hook 方法的名称、所在类以及查找耗时，可进行快速调试和排错。

- <b>方便移植</b>

  原生支持 Xposed API 用法，并原生对接 Xposed API，拥有 Xposed API 的 Hook 框架都能快速对接 Yuki Hook API。

- <b>支持混淆</b>

  使用 `YukiHookAPI` 构建的 Xposed 模块原生支持 R8 压缩优化混淆，混淆不会破坏 Hook 入口点，R8 下无需任何其它配置。

- <b>快速上手</b>

  简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用。

## 灵感来源

以前，我们在构建 Xposed 模块的时候，首先需要在 `assets` 下创建 `xposed_init` 文件。

然后，将自己的入口类名手动填入文件中，使用 `XposedHelper` 去实现我们的 Hook 逻辑。

自 `Kotlin` 作为 Android 主要开发语言以来，这套 API 用起来确实已经不是很优雅了。

有没有什么 <b>好用、轻量、优雅</b> 的解决办法呢？

本着这样的想法，`YukiHookAPI` 诞生了。

现在，我们只需要编写少量的代码，一切时间开销和花费交给自动化处理。

> 示例如下

<!-- tabs:start -->

#### **Yuki Hook API**

```kotlin
@InjectYukiHookWithXposed
class MainHook : YukiHookXposedInitProxy {

    override fun onHook() = encase {
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
        }
    }
}
```

#### **Xposed API**

```kotlin
class MainHook : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.browser")
            XposedHelpers.findAndHookMethod(
                Activity::class.java.name,
                lpparam.classLoader,
                "onCreate",
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
}
```

<!-- tabs:end -->

是的，你没有看错，仅仅就需要这几行代码，就一切安排妥当。

代码量少，逻辑清晰，借助高效强大的 `YukiHookAPI`，你就可以实现一个非常简单的 Xposed 模块。

## 支持的 Hook 框架

以下是 `YukiHookAPI` 支持的 `Hook Framework` 以及 Xposed 框架。

| Hook Framework                                            | ST  | Describe                                                                                  |
| --------------------------------------------------------- | --- | ----------------------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)             | ✅   | 多场景下稳定使用                                                                          |
| [EdXposed](https://github.com/ElderDrivers/EdXposed)      | ✅   | 部分兼容                                                                                  |
| [Pine](https://github.com/canyie/pine)                    | ⭕   | 可以使用                                                                                  |
| [SandHook](https://github.com/asLody/SandHook)            | ⭕   | 可以使用                                                                                  |
| [Whale](https://github.com/asLody/whale)                  | ⭕   | 需要 [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale) |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❗   | 需要自行实现 Xposed API                                                                   |
| [FastHook](https://github.com/turing-technician/FastHook) | ❗   | 需要自行实现 Xposed API                                                                   |
| [Epic](https://github.com/tiann/epic)                     | ❗   | 需要自行对接 [Dexposed](https://github.com/alibaba/dexposed)                              |
| [TaiChi](https://github.com/taichi-framework/TaiChi)      | ⭕   | 可以作为模块使用                                                                          |
| [Xposed](https://github.com/rovo89/Xposed)                | ❎   | 未测试，不再推荐使用                                                                      |