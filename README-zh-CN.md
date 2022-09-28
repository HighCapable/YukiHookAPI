# Yuki Hook API

![Blank](https://img.shields.io/badge/license-MIT-blue)
![Blank](https://img.shields.io/badge/version-v1.1.1-green)
[![Telegram](https://img.shields.io/badge/Follow-Telegram-blue.svg?logo=telegram)](https://t.me/YukiHookAPI)
<br/><br/>
<img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/icon.png" width = "100" height = "100"/>
<br/>
<br/>
⛱️ 一个使用 Kotlin 重新构建的高效 Xposed Hook API。
<br/>

[English](https://github.com/fankes/YukiHookAPI/blob/master/README.md) | 简体中文

## 这是什么

- 这是一个使用 Kotlin 重新构建的高效 Xposed Hook API
- 名称取自 [《ももくり》女主 栗原 雪(Yuki)](https://www.bilibili.com/bangumi/play/ss5016)
- 前身为 [开发学习项目](https://github.com/fankes/TMore) 中使用的 Innocent Xposed API，现在重新命名并开源

## 支持的功能

- 标准 Hook
- Zygote Hook
- 资源钩子(Resources Hook)

## 扩展功能

- 自动化 Xposed 模块构建 (完全无需自行创建 `assets/xposed_init`)
- [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module) 支持
- DataChannel (宿主 ←→ 模块) 无序广播通讯通道功能
- 简单快捷地实现混淆的字节码反射、查找功能

## 开始使用

- [点击这里](https://fankes.github.io/YukiHookAPI/zh-cn/) 前往文档页面查看更多详细教程和内容。

## 联系我们

- [点击加入 Telegram 群组](https://t.me/YukiHookAPI)

## 展望未来

如果你喜欢 `YukiHookAPI` 项目，欢迎为此项目贡献你的代码 **PR**，可以是任何改进的建议以及新增的功能。

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
| [WxRecordRead](https://github.com/pwh-pwh/wxrecordread)                         | [Coderpwh](https://github.com/pwh-pwh)          |

你也在使用 `YukiHookAPI` 吗？快来 **PR** 将你的存储仓库添加到上方的列表(私有仓库可以不需要注明网页链接)。

## 捐赠支持

- 工作不易，无意外情况此项目将继续维护下去，提供更多可能，欢迎打赏。<br/><br/>
  <img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/wechat_code.jpg" width = "200" height = "200"/>

## 第三方开源使用声明

- [Kotlin Symbol Processing API](https://github.com/google/ksp)
- [FreeReflection](https://github.com/tiann/FreeReflection)

## 许可证

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