# 介绍

> `YukiHook` 是一个现代化的 Hook API 与 Xposed 模块解决方案，本身不提供任何 Hook 功能，需要相关基础 Hook API 的支持。

## 背景

这个项目诞生之初是为了有一套标准、规范的 Hook API 来取代 Android 上由 Rovo89 定义的但是已经不那么现代的 `XposedHelpers`。现在，它的目标是成为一套
Hook API，并为 Xposed 模块带来统一的开发体验，无论你正在使用的是什么底层 Hook 方案。

项目图标由 [MaiTungTM](https://github.com/Lagrio) 重绘，名字取自 [《ももくり》](https://momokuri-anime.jp) 动画中的女主 栗原 雪 (Yuki)，前身为 [这个项目](https://github.com/fankes/TMore) 使用的 Innocent Xposed API。

**YukiHook** 已完成从 **YukiHookAPI** 改名并完成了现代化的底层重写，未来会作为 Android 通用的 Hook API 和 Xposed 模块方案继续演进和维护。

## 为什么是 YukiHook？

以前，我们在开发 Xposed 模块的时候，首先需要在 `assets` 下创建 `xposed_init` 文件，然后将自己的入口类名手动填入文件中，使用 `XposedHelpers` 去实现我们的 Hook
逻辑。自 Kotlin 作为 Android 主要开发语言以来，这套 API 用起来确实已经不是很现代了。

有没有什么 **好用、轻量、现代** 的解决方案呢？本着这样的想法，YukiHook 诞生了。现在，我们只需要实现同一套 Hook 代码，不同底座的入口文件与模块配置交给 YukiHook 来完成。

借助 Kotlin 现代的 DSL 写法以及 YukiHook 自身提供的语法糖，可以让你的 Hook 逻辑更加美观清晰。

> 示例如下

::: code-group

```kotlin [在 libxposed 中使用]
@YukiHookLibXposedEntry(targetApiVersion = 101)
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        loadApp("io.github.myapp") {
            val activityClass = Activity::class.java
            val onCreate = activityClass.getDeclaredMethod(
                "onCreate", Bundle::class.java
            )

            onCreate.hook {
                before {
                    // Your code here.
                }
                after {
                    // Your code here.
                }
            }
        }
    }
}
```

```kotlin [在 Xposed 82 中使用]
@YukiHookXposed82Entry
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        loadApp("io.github.myapp") {
            val activityClass = Activity::class.java
            val onCreate = activityClass.getDeclaredMethod(
                "onCreate", Bundle::class.java
            )

            onCreate.hook {
                before {
                    // Your code here.
                }
                after {
                    // Your code here.
                }
            }
        }
    }
}
```

:::

## 用途

YukiHook 目前完全采用 Kotlin DSL 语法构建，在后期的 2.x 版本中，我们会进一步支持 Kotlin 与 Java 的混合开发。

同时，我们现在正在进行 [2.x 版本](../about/future.md#_2-x-milestone) 的完全重构工作，届时 1.x 将进入 LTS (长期支持) 阶段。

对于 Xposed 模块开发者，你现在可以使用它来轻松创建基于支持 [底座](../guide/quick-start.md#choose-a-base-module) 的 Xposed 模块。

我们同时推荐使用 [KavaRef](https://github.com/HighCapable/KavaRef) 作为现代的反射 API，YukiHook 为其提供了原生的 [扩展支持](../library/yukihook-extension-kavaref)。

如果你正在使用其他 Xposed 模块方案或 Hook API，你可以参考 [这里](../config/migration) 迁移至 YukiHook。

## 技能要求

在开始之前，你需要拥有以下基础才能更好地使用 YukiHook。

- 掌握并了解 Android 开发及简单的系统运行原理
- 掌握并了解 Android APK 内部结构以及简单的反编译知识要领，可参考 [Jadx](https://github.com/skylot/jadx) 与 [ApkTool](https://github.com/iBotPeaches/Apktool)
- 掌握并熟练使用 Java 反射，了解简单的 Smali 语法，了解 DEX 文件结构，会使用逆向分析定位方法位置
- 掌握并了解 Xposed 的运行原理
- 掌握 Kotlin 语言，学会灵活运用 Kotlin DSL

## 语言要求

请使用 Kotlin，框架部分代码构成同样兼容 Java 但基础 Hook 场景的实现**可能完全无法使用**。

文档全部的 Demo 示例代码都将使用 Kotlin 进行描述，如果你完全不会使用 Kotlin 那你将有可能无法使用 YukiHook。

## 功能贡献

本项目的维护离不开各位开发者的支持和贡献，如果可能，欢迎提交 PR 为此项目贡献你认为需要的功能或前往 [GitHub Issues](repo://issues) 向我们提出建议。