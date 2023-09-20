# Yuki Hook API

[![GitHub license](https://img.shields.io/github/license/fankes/YukiHookAPI?color=blue)](https://github.com/fankes/YukiHookAPI/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/fankes/YukiHookAPI?display_name=release&logo=github&color=green)](https://github.com/fankes/YukiHookAPI/releases)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/YukiHookAPI)
[![Telegram](https://img.shields.io/badge/discussion%20dev-Telegram-blue.svg?logo=telegram)](https://t.me/HighCapable_Dev)

<img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/icon.png?raw=true" width = "100" height = "100" alt="LOGO"/>

⛱️ 一个使用 Kotlin 构建的高效 Hook API 与 Xposed 模块解决方案。

[English](https://github.com/fankes/YukiHookAPI/blob/master/README.md) | 简体中文

## 这是什么

这是一个使用 Kotlin 基于 Xposed API 重新构建的高效 Hook API，同时为 Xposed 模块的开发打造了丰富的功能扩展。

名称取自 [《ももくり》女主 栗原 雪(Yuki)](https://www.bilibili.com/bangumi/play/ss5016)。

前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源。

## 支持的功能

> 基本功能

- [x] Hook Framework 支持的基本 ART 动态方法 Hook 功能
- [x] Xposed 资源钩子 (Resources Hook) **(计划 2.x.x 版本作废)**

> Hook Frameworks API

- [x] [Rovo89 Xposed API](https://api.xposed.info)
- [ ] [Modern Xposed API](https://github.com/libxposed) **(计划 2.x.x 版本支持)**

## 扩展功能

- [x] 自动化 Xposed 模块构建 (完全无需自行创建 `assets/xposed_init`)
- [x] [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module)
- [x] DataChannel (宿主 ←→ 模块)
- [x] 混淆的字节码查找 & 反射 (由 [YukiReflection](https://github.com/fankes/YukiReflection) 提供支持)

## 开始使用

- [点击这里](https://fankes.github.io/YukiHookAPI/zh-cn/) 前往文档页面查看更多详细教程和内容。

## 合作项目

以下是经过合作并稳定使用 `YukiHookAPI` 的项目。

| Repository                                                                      | Developer                                       |
|---------------------------------------------------------------------------------|-------------------------------------------------|
| [TSBattery](https://github.com/fankes/TSBattery)                                | [fankesyooni](https://github.com/fankes)        |
| [MIUI 原生通知图标](https://github.com/fankes/MIUINativeNotifyIcon)                   | [fankesyooni](https://github.com/fankes)        |
| [ColorOS 通知图标增强](https://github.com/fankes/ColorOSNotifyIcon)                   | [fankesyooni](https://github.com/fankes)        |
| [自由屏幕旋转](https://github.com/Xposed-Modules-Repo/com.fankes.forcerotate)         | [fankesyooni](https://github.com/fankes)        |
| [拒绝强制亮度](https://github.com/Xposed-Modules-Repo/com.fankes.refusebrightness)    | [fankesyooni](https://github.com/fankes)        |
| [AppErrorsTracking](https://github.com/KitsunePie/AppErrorsTracking)            | [fankesyooni](https://github.com/fankes)        |
| [Enable WebView Debugging](https://github.com/WankkoRee/EnableWebViewDebugging) | [WankkoRee](https://github.com/WankkoRee)       |
| [Fuck MIUI Gesture](https://github.com/HCGStudio/FuckMIUIGesture)               | [mahoshojoHCG](https://github.com/mahoshojoHCG) |
| [MIUI遮罩进化](https://github.com/GSWXXN/RestoreSplashScreen)                       | [GSWXXN](https://github.com/GSWXXN)             |
| [Color OS Installer Plus](https://github.com/NextAlone/ColorOSInstallerPlus)    | [NextAlone](https://github.com/NextAlone)       |
| [Auto NFC](https://github.com/GSWXXN/AutoNFC)                                   | [GSWXXN](https://github.com/GSWXXN)             |
| [不要竖屏](https://github.com/WankkoRee/Portrait2Landscape)                         | [WankkoRee](https://github.com/WankkoRee)       |
| [QDReadHook](https://github.com/xihan123/QDReadHook)                            | [xihan123](https://github.com/xihan123)         |
| [HXReadHook](https://github.com/xihan123/HXReadHook)                            | [xihan123](https://github.com/xihan123)         |
| [WxRecordRead](https://github.com/pwh-pwh/wxrecordread)                         | [Coderpwh](https://github.com/pwh-pwh)          |
| [MIUI更新进化](https://miup.utssg.xyz)                                              | [ZQDesigned](https://github.com/ZQDesigned)     |
| [MIUI录屏进化](https://www.coolapk.com/apk/UTSSG.ZQDesigned.miuirecordercracker)    | [ZQDesigned](https://github.com/ZQDesigned)     |
| [Fuck AD](https://github.com/hujiayucc/Fuck-AD)                                 | [hujiayucc](https://github.com/hujiayucc)       |
| [最右强力去广告](https://github.com/kazutoiris/zuiyou-adfree)                          | [kazutoiris](https://github.com/kazutoiris)     |

你也在使用 `YukiHookAPI` 吗？快来 **PR** 将你的存储仓库添加到上方的列表 (私有仓库可以不需要注明网页链接)。

## 项目推广

如果你正在寻找一个可以自动管理 Gradle 项目依赖的 Gradle 插件，你可以了解一下 [SweetDependency](https://github.com/HighCapable/SweetDependency) 项目。

如果你正在寻找一个可以自动生成属性键值的 Gradle 插件，你可以了解一下 [SweetProperty](https://github.com/HighCapable/SweetProperty) 项目。

本项目同样使用了 **SweetDependency** 和 **SweetProperty**。

## 捐赠支持

工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。

<img src="https://github.com/fankes/fankes/blob/main/img-src/payment_code.jpg?raw=true" width = "500" alt="Payment Code"/>

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=fankes/YukiHookAPI&type=Date)

## 第三方开源使用声明

- [Kotlin Symbol Processing API](https://github.com/google/ksp)
- [FreeReflection](https://github.com/tiann/FreeReflection)

## 许可证

- [MIT](https://choosealicense.com/licenses/mit)

```
MIT License

Copyright (C) 2019-2023 HighCapable

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

版权所有 © 2019-2023 HighCapable