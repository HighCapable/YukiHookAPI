# Yuki Hook API

![Eclipse Marketplace](https://img.shields.io/badge/build-passing-brightgreen)
![Eclipse Marketplace](https://img.shields.io/badge/license-MIT-blue)
![Eclipse Marketplace](https://img.shields.io/badge/version-v1.0.2-green)
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

# Function

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

# Support

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

# Changelog

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
  增加直接使用字符串创建 Hook 的方法。
  
# Cooperations

以下是经过合作并稳定使用 `YukiHookAPI` 的项目。

| Repository                                                        | Developer                                |
| ----------------------------------------------------------------- | ---------------------------------------- |
| [TSBattery](https://github.com/fankes/TSBattery)                  | [fankesyooni](https://github.com/fankes) |
| [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon) | [fankesyooni](https://github.com/fankes) |

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