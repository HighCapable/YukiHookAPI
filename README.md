# Yuki Hook API

![Blank](https://img.shields.io/badge/license-MIT-blue)
![Blank](https://img.shields.io/badge/version-v1.1.11-green)
[![Telegram](https://img.shields.io/badge/Follow-Telegram-blue.svg?logo=telegram)](https://t.me/YukiHookAPI)
<br/><br/>
<img src="https://github.com/fankes/YuKiHookAPI/blob/master/img-src/icon.png?raw=true" width = "100" height = "100"/>
<br/>
<br/>
⛱️ An efficient Hook API and Xposed Module solution built in Kotlin.
<br/>

English | [简体中文](https://github.com/fankes/YukiHookAPI/blob/master/README-zh-CN.md)

## What's this

- This is an efficient Hook API rebuilt based on the Xposed API using Kotlin, and creates rich function extensions for the development of
  Xposed Modules
- The name is taken from ["ももくり" heroine Yuki Kurihara](https://www.bilibili.com/bangumi/play/ss5016)
- Formerly the Innocent Xposed API used in [Development Learning Project](https://github.com/fankes/TMore), now renamed and open sourced

## Supports

- Basic ART dynamic method hook functions supported by Hook Framework
- Xposed Resources Hook

## Extensions

- Automatic Xposed Module Build (No need to create `assets/xposed_init` by yourself)
- [New XSharedPreferences](https://github.com/LSPosed/LSPosed/wiki/New-XSharedPreferences#for-the-module) Supports
- DataChannel (Host ←→ Module)
- Simple and quick implementation of obfuscated bytecode reflection and finding functions

## Get Started

- [Click here](https://fankes.github.io/YukiHookAPI/en/) go to the documentation page for more detailed tutorials and content.

## Contacts

- [Follow us on Telegram](https://t.me/YukiHookAPI)

## Features

If you like the `YukiHookAPI` project, we welcome you to make a **PR** in this project, any suggestions for improvement and new features.

## Cooperations

The following are projects that have collaborated and are using `YukiHookAPI`.

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
| [Zuiyou ADFree](https://github.com/kazutoiris/zuiyou-adfree)                    | [kazutoiris](https://github.com/kazutoiris)     |

Are you also using `YukiHookAPI`? Come and **PR** to add your repository to the list above (private repositories do not need to indicate web
links).

## Star History

![Star History Chart](https://api.star-history.com/svg?repos=fankes/YukiHookAPI&type=Date)

## Third-Party Open Source Usage Statement

- [Kotlin Symbol Processing API](https://github.com/google/ksp)
- [FreeReflection](https://github.com/tiann/FreeReflection)

## License

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

Copyright © 2019-2023 HighCapable