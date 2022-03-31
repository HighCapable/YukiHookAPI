# Yuki Hook API

![Eclipse Marketplace](https://img.shields.io/badge/build-passing-brightgreen)
![Eclipse Marketplace](https://img.shields.io/badge/license-MIT-blue)
![Eclipse Marketplace](https://img.shields.io/badge/version-v1.0.69-green)
[![Telegram](https://img.shields.io/static/v1?label=Telegram&message=交流讨论&color=0088cc)](https://t.me/XiaofangInternet)
<br/><br/>
<img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/icon.png" width = "100" height = "100"/>
<br/>
<br/>
⛱️ An efficient Kotlin version of the Xposed Hook API.
<br/>

# What's this

- 这是一个使用 Kotlin 重新构建的高效 Xposed Hook API
- 名称取自 <a href='https://www.bilibili.com/bangumi/play/ss5016/?from=search&seid=313229405371562533&spm_id_from=333.337.0.0'>
  《ももくり》女主 栗原 雪(Yuki)</a>
- 前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源

# Functions

- <strong>Xposed 模块开发</strong><br/>
  自动构建程序可以帮你快速创建一个 Xposed 模块，完全省去配置入口类和 xposed_init 文件。<br/>
- <strong>轻量优雅</strong><br/>
  拥有一套强大、优雅和人性化的 Kotlin Lambda Hook API，可以帮你快速实现 Method、Constructor、Field 的查找以及 Hook。<br/>
- <strong>高效调试</strong><br/>
  拥有丰富的调试日志功能，细到每个 Hook 方法的名称、所在类以及查找耗时，可进行快速调试和排错。<br/>
- <strong>方便移植</strong><br/>
  原生支持 Xposed API 用法，并原生对接 Xposed API，拥有 Xposed API 的 Hook 框架都能快速对接 Yuki Hook API。<br/>
- <strong>支持混淆</strong><br/>
  使用 Yuki Hook API 构建的 Xposed 模块原生支持 R8 压缩优化混淆，混淆不会破坏 Hook 入口点，R8 下无需任何其它配置。<br/>
- <strong>快速上手</strong><br/>
  简单易用，不需要繁琐的配置，不需要十足的开发经验，搭建环境集成依赖即可立即开始使用。

# Supports

以下是 `YukiHookAPI` 支持的 `Hook Framework` 以及 Xposed 框架。

| Hook Framework                                            | ST | Describe                                                                                 |
| --------------------------------------------------------- | -- | ---------------------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)             | ✅ | 多场景下稳定使用                                                                           |
| [EdXposed](https://github.com/ElderDrivers/EdXposed)      | ☑  | 部分兼容                                                                                  |
| [Pine](https://github.com/canyie/pine)                    | ⭕ | 可以使用                                                                                  |
| [SandHook](https://github.com/asLody/SandHook)            | ⭕ | 可以使用                                                                                  |
| [Whale](https://github.com/asLody/whale)                  | ⭕ | 需要 [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale) |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❗ | 需要自行实现 Xposed API                                                                    |
| [FastHook](https://github.com/turing-technician/FastHook) | ❗ | 需要自行实现 Xposed API                                                                    |
| [Epic](https://github.com/tiann/epic)                     | ❗ | 需要自行对接 [Dexposed](https://github.com/alibaba/dexposed)                               |
| [TaiChi](https://github.com/taichi-framework/TaiChi)      | ⭕ | 可以作为模块使用                                                                           |
| [Xposed](https://github.com/rovo89/Xposed)                | ❎ | 未测试，不再推荐使用                                                                        |

# Advantage

以前，我们在构建 Xposed 模块的时候，首先需要在 `assets` 下创建 `xposed_init` 文件。<br/><br/>
然后，将自己的入口类名手动填入文件中，使用 `XposedHelper` 去实现我们的 Hook 逻辑。

- 示例如下

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

自 `Kotlin` 作为 Android 主要开发语言以来，这套 API 用起来确实已经不是很优雅了。<br/><br/>
有没有什么 <b>好用、轻量、优雅</b> 的解决办法呢？<br/><br/>
本着这样的想法，`YukiHookAPI` 诞生了。<br/><br/>
现在，我们只需要编写少量的代码，一切时间开销和花费交给自动化处理。

- 示例如下

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

是的，你没有看错，仅仅就需要这几行代码，就一切安排妥当。<br/><br/>
代码量少，逻辑清晰，借助高效强大的 `YukiHookAPI`，你就可以实现一个非常简单的 Xposed 模块。

# Get Started

- 你可以点击 [快速开始](https://github.com/fankes/YukiHookAPI/wiki#%E5%BF%AB%E9%80%9F%E5%BC%80%E5%A7%8B)，在 `Gradle` 中集成 `YukiHookAPI` 并开始使用。
- 更多使用教程及 API 文档请 [前往 Wiki 主页](https://github.com/fankes/YukiHookAPI/wiki) 进行查看。

# Changelogs

- 1.0 <br/>
  首个版本提交至 Maven。<br/><br/>
- 1.0.1 <br/>
  `RemedyPlan` 增加 `onFind` 功能；<br/>
  整合并修改了部分反射 API 代码；<br/>
  增加了 `type` 中的 `java` 类型；<br/>
  修复忽略错误在控制台仍然输出的问题。<br/><br/>
- 1.0.2 <br/>
  修复 Windows 下无法找到项目路径的问题；<br/>
  移除部分反射 API，合并至 `BaseFinder` 进行整合；<br/>
  增加直接使用字符串创建 Hook 的方法。<br/><br/>
- 1.0.3 <br/>
  修复一个潜在性的异常未拦截 BUG；<br/>
  增加 `ignoredError` 功能；<br/>
  增加了 `type` 中的 `android` 类型；<br/>
  增加监听 `hook` 后的 `ClassNotFound` 功能。<br/><br/>
- 1.0.4 <br/>
  修复 LSPosed 在最新版本中启用“只有模块classloader可以使用Xposed API”选项后找不到 `XposedBridge` 的问题；<br/>
  添加 `YukiHookAPI` 的常量版本名称和版本号；<br/>
  新增 `hasField` 方法以及 `isAllowPrintingLogs` 配置参数；<br/>
  新增 `isDebug` 开启的情况下 API 将自动打印欢迎信息测试模块是否生效。<br/><br/>
- 1.0.5 <br/>
  修复旧版本 LSPosed 框架情况下欢迎信息多次打印的问题；<br/>
  添加 `onInit` 方法来配置 `YukiHookAPI`；<br/>
  新增 `executorName` 和 `executorVersion` 来获取当前 Hook 框架的名称和版本号；<br/>
  新增 `by` 方法来设置 Hook 的时机和条件；<br/>
  `YukiHookModulePrefs` 新增可控制的键值缓存，可在宿主运行时模块动态更新数据；<br/>
  修复了一些可能存在的 BUG。<br/><br/>
- 1.0.55 <br/>
  修正一处注释错误；<br/>
  临时修复一个 BUG；<br/>
  增加了 `type` 中的大量 `android` 类型以及少量 `java` 类型；<br/>
  修复新版与旧版 Kotlin APIs 的兼容性问题。<br/><br/>
- 1.0.6 <br/>
  修复 `YukiHookModulePrefs` 在使用一次 `direct` 忽略缓存后每次都忽略的 BUG；<br/>
  增加新的 API，作废了 `isActive` 判断模块激活的传统用法；<br/>
  修复非 Xposed 环境使用 API 时打印调试日志的问题；<br/>
  修复查找 `Field` 时的日志输出问题和未拦截的异常问题；<br/>
  解耦合 `ReflectionUtils` 中的 Xposed API；<br/>
  增加 `YukiHookModuleStatus` 方法名称的混淆，以精简模块生成的体积；<br/>
  装载模块自身 Hook 时将不再打印欢迎信息；<br/>
  修复上一个版本仍然存在的某些 BUG。<br/><br/>
- 1.0.65 <br/>
  重新发布版本修复 Maven 仓库因为缓存问题新版本不正确的情况；<br/>
  增加 `MethodFinder` 与 `FieldFinder` 新的返回值调用方法；<br/>
  修复可能存在的问题，并修复太极使用过程中可能存在的问题；<br/>
  修复自动生成 Xposed 入口类可能发生的问题；<br/>
  增加了 `type` 中的 `android` 类型以及 `java` 类型。<br/><br/>
- 1.0.66 <br/>
  修复 `MethodFinder` 中的一个严重问题；<br/>
  增加 `hookParam` 中的 `args` 调用方法；<br/>
  修复其它可能存在的问题以及修复部分类的注释问题。<br/><br/>
- 1.0.67 <br/>
  增加三个 `Finder` 中的 `modifiers` 功能，可筛选 `static`、`native`、`public`、`abstract` 等诸多描述类型；<br/>
  增加方法和构造方法查找时可模糊方法参数类型为指定个数进行查找；<br/>
  增加 `Member` 的 `hasModifiers` 扩展功能；<br/>
  增加 `MethodFinder` 和 `ConstructorFinder` 中的 `give` 方法，可获得原始类型；<br/>
  增加 `YukiHookModulePrefs` 中的 `PrefsData` 模板功能；<br/>
  彻底对方法、构造方法及变量的查找方案进行重构；<br/>
  优化代码注释，修复了可能产生的 BUG。<br/><br/>
- 1.0.68 <br/>
  增加 Demo 中的新用例和 LSPosed 作用域；<br/>
  增加 `Member` 查找缓存和查找缓存配置开关；<br/>
  移除和修改 `MethodFinder`、`FieldFinder` 以及 `HookParam` 相关方法的调用；<br/>
  增加更多 `Finder` 中的 `cast` 类型并支持 `cast` 为数组；<br/>
  整体的性能和稳定性提升；<br/>
  修复上一个版本可能存在的 BUG。<br/><br/>
- 1.0.69 <br/>
  添加并改进一些方法功能的注释；<br/>
  增加 Demo 中的更多示例 Hook 内容；<br/>
  修复在一个 Hook 实例中，`allMethods` 多次使用时只有最后一个生效的问题，感谢 [WankkoRee](https://github.com/WankkoRee) 的反馈。<br/><br/>
- 1.0.7 <br/>
  完善中。

# Features

如果你喜欢 `YukiHookAPI` 项目，欢迎为此项目贡献你的代码，可以是任何改进的建议以及新增的功能。

# Cooperations

以下是经过合作并稳定使用 `YukiHookAPI` 的项目。

| Repository                                                        | Developer                                |
| ----------------------------------------------------------------- | ---------------------------------------- |
| [TSBattery](https://github.com/fankes/TSBattery)                  | [fankesyooni](https://github.com/fankes) |
| [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon) | [fankesyooni](https://github.com/fankes) |
| [ColorOS 通知图标增强](https://github.com/fankes/ColorOSNotifyIcon) | [fankesyooni](https://github.com/fankes) |

# Donate

- 工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。<br/><br/>
  <img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/wechat_code.jpg" width = "200" height = "200"/>

# License

- [MIT](https://choosealicense.com/licenses/mit)

```
MIT License

Copyright (C) 2019-2022 HighCapable

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

版权所有 © 2019-2022 HighCapable