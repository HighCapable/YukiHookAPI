# Introduction

> `YukiHook` is a modern hook API and Xposed module solution. It does not provide any hook functionality itself and requires the support of related base hook APIs.

## Background

This project was originally created to provide a standard and well-defined hook API to replace `XposedHelpers`, which was defined by Rovo89 on Android and is no longer very modern.
Now, it aims to be a hook API and to bring a unified development experience to Xposed modules, no matter which underlying hook solution you are using.

The project icon is redrawn by [MaiTungTM](https://github.com/Lagrio) and the name is taken from Yuki Kurihara, the heroine of the anime [「ももくり」](https://momokuri-anime.jp),
and it was formerly the Innocent Xposed API used in [this project](https://github.com/fankes/TMore).

**YukiHook** has been renamed from **YukiHookAPI** with a modern rewrite of its underlying implementation, and it will continue to evolve and be maintained as a general hook API and Xposed module solution for Android.

## Why YukiHook?

Previously, when developing an Xposed module, we first had to create an `xposed_init` file under `assets`, then fill in our entry class name manually and use `XposedHelpers` to implement our hook logic.
Since Kotlin became the main language for Android development, this API is no longer very modern to use.

Is there a **useful, lightweight and modern** solution? With this idea in mind, YukiHook was born. Now, we only need to write one set of hook code,
and YukiHook takes care of the entry files and module configuration for different base modules.

With Kotlin's modern DSL syntax and the syntactic sugar provided by YukiHook, your hook logic can be cleaner and clearer.

> The following example

::: code-group

```kotlin [Use on libxposed]
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

```kotlin [Use on Xposed 82]
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

## Usage

YukiHook is currently built entirely with Kotlin DSL syntax, and in the later 2.x versions, we will further support mixed development with Kotlin and Java.

Meanwhile, we are working on a complete refactor for [version 2.x](../about/future.md#_2-x-milestone), and 1.x will then enter the LTS (Long-Term Support) stage.

For Xposed module developers, you can now use it to easily create Xposed modules on the supported [base modules](../guide/quick-start.md#choose-a-base-module).

We also recommend [KavaRef](https://github.com/HighCapable/KavaRef) as a modern reflection API, and YukiHook provides native [extension support](../library/yukihook-extension-kavaref) for it.

If you are using another Xposed module solution or hook API, you can refer to [here](../config/migration) to migrate to YukiHook.

## Skill Requirements

Before you start, you should have the following knowledge to make better use of YukiHook.

- Understand Android development and how the system works at a basic level
- Understand the internal structure of an Android APK and basic decompilation, see [Jadx](https://github.com/skylot/jadx) and [ApkTool](https://github.com/iBotPeaches/Apktool)
- Be proficient with Java reflection, know basic Smali syntax and the DEX file structure, and be able to locate methods with reverse analysis
- Understand how Xposed works
- Know Kotlin and be able to use Kotlin DSL flexibly

## Language Requirement

Please use Kotlin. Parts of the API are also compatible with Java, but basic hook scenarios **may not work at all** in Java.

All the demo code in the documentation is written in Kotlin. If you don't know Kotlin at all, you may not be able to use YukiHook.

## Contribution

The maintenance of this project is inseparable from the support and contributions of all developers. If possible,
feel free to submit a PR to contribute features you think are needed to this project or go to [GitHub Issues](repo://issues) to make suggestions to us.