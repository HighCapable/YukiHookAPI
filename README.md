# Yuki Hook API

![Blank](https://img.shields.io/badge/build-passing-brightgreen)
![Blank](https://img.shields.io/badge/license-MIT-blue)
![Blank](https://img.shields.io/badge/version-v1.0.92-green)
[![Telegram](https://img.shields.io/badge/Follow-Telegram-blue.svg?logo=telegram)](https://t.me/YukiHookAPI)
<br/><br/>
<img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/icon.png" width = "100" height = "100"/>
<br/>
<br/>
⛱️ An efficient Kotlin version of the Xposed Hook API.
<br/>

## What's this

- This is an efficient Xposed Hook API rebuilt in Kotlin
- The name is taken from ["ももくり" heroine Yuki Kurihara](https://www.bilibili.com/bangumi/play/ss5016)
- Formerly the Innocent Xposed API used in [Development Learning Project](https://github.com/fankes/TMore), now renamed and open sourced

**这是什么**

- 这是一个使用 Kotlin 重新构建的高效 Xposed Hook API
- 名称取自 [《ももくり》女主 栗原 雪(Yuki)](https://www.bilibili.com/bangumi/play/ss5016)
- 前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源

## Supports

- Standard Hook
- Zygote Hook
- Resources Hook

**支持的功能**

- 标准 Hook
- Zygote Hook
- 资源钩子(Resources Hook)

## Extensions

- Automatic Xposed Module Build (No need to create `assets/xposed_init` by yourself)
- [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module) Supports
- DataChannel (Host ←→ Module)
- Simple and quick Obfuscated Member Reflection

**扩展功能**

- 自动化 Xposed 模块构建 (完全无需自行创建 `assets/xposed_init`)
- [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module) 支持
- DataChannel (宿主 ←→ 模块) 无序广播通讯通道功能
- 简单快捷地实现混淆的字节码反射、查找功能

## Get Started

- [Click here](https://fankes.github.io/YukiHookAPI) go to the documentation page for more detailed tutorials and content.
- Only **Simplified Chinese** translations in this time, English ver is in future.

**开始使用**

- [点击这里](https://fankes.github.io/YukiHookAPI) 前往文档页面查看更多详细教程和内容。
- 目前只有 **简体中文** 的翻译文档，English 版本将在日后根据需求更新。

> You may encounter the problem that the document is not the latest version due to the browser cache.
> If you have viewed the document once, please manually refresh it once on each page to synchronize the latest version, or clear the browser cache.

> 你大概率会遇到浏览器缓存造成文档不是最新版本的问题，若已经查看过一次文档，请手动在每个页面上刷新一次以同步最新版本，或清除浏览器缓存。

> 面向中国大陆开发者的提示：若打开页面发生 404 问题，可能是由于你的 DNS 受到了污染，请科学上网后清除浏览器缓存再试一次。

The latest version update time/最新版本更新时间：2022-05-31 01:35

## Contacts

- [Follow us on Telegram](https://t.me/YukiHookAPI)

**联系我们**

- [点击加入 Telegram 群组](https://t.me/YukiHookAPI)

## Features

If you like the `YukiHookAPI` project, we welcome you to make a **PR** in this project, any suggestions for improvement and new features.

**展望未来**

如果你喜欢 `YukiHookAPI` 项目，欢迎为此项目贡献你的代码 **PR**，可以是任何改进的建议以及新增的功能。

## Cooperation's

The following are projects that have collaborated and are using `YukiHookAPI`.

**合作项目**

以下是经过合作并稳定使用 `YukiHookAPI` 的项目。

| Repository                                                                         | Developer                                       |
|------------------------------------------------------------------------------------|-------------------------------------------------|
| [TSBattery](https://github.com/fankes/TSBattery)                                   | [fankesyooni](https://github.com/fankes)        |
| [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon)                      | [fankesyooni](https://github.com/fankes)        |
| [ColorOS 通知图标增强](https://github.com/fankes/ColorOSNotifyIcon)                      | [fankesyooni](https://github.com/fankes)        |
| [自由屏幕旋转](https://github.com/Xposed-Modules-Repo/com.fankes.forcerotate)            | [fankesyooni](https://github.com/fankes)        |
| [拒绝强制亮度](https://github.com/Xposed-Modules-Repo/com.fankes.refusebrightness)       | [fankesyooni](https://github.com/fankes)        |
| [AppErrorsTracking](https://github.com/KitsunePie/AppErrorsTracking)               | [fankesyooni](https://github.com/fankes)        |
| [Enable WebView Debugging](https://github.com/WankkoRee/EnableWebViewDebugging)    | [WankkoRee](https://github.com/WankkoRee)       |
| [Fuck MIUI Gesture](https://github.com/HCGStudio/FuckMIUIGesture)                  | [mahoshojoHCG](https://github.com/mahoshojoHCG) |
| [MIUI遮罩进化](https://github.com/GSWXXN/RestoreSplashScreen)                          | [GSWXXN](https://github.com/GSWXXN)             |
| [Color OS Installer Plus](https://github.com/NextAlone/ColorOSInstallerPlus)       | [NextAlone](https://github.com/NextAlone)       |
| [Auto NFC](https://github.com/GSWXXN/AutoNFC)                                    | [GSWXXN](https://github.com/GSWXXN)             |

## Donate

- Like this project? Please use WeChat Pay for donate in China.

- 工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。<br/><br/>
  <img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/wechat_code.jpg" width = "200" height = "200"/>

## Third-Party Open Source Usage Statement

- [Kotlin Symbol Processing API](https://github.com/google/ksp)
- [FreeReflection](https://github.com/tiann/FreeReflection)

## License

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