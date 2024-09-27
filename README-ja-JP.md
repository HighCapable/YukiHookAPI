# Yuki Hook API

[![GitHub license](https://img.shields.io/github/license/HighCapable/YukiHookAPI?color=blue)](https://github.com/HighCapable/YukiHookAPI/blob/master/LICENSE)
[![GitHub release](https://img.shields.io/github/v/release/HighCapable/YukiHookAPI?display_name=release&logo=github&color=green)](https://github.com/HighCapable/YukiHookAPI/releases)
[![Telegram](https://img.shields.io/badge/discussion-Telegram-blue.svg?logo=telegram)](https://t.me/YukiHookAPI)
[![Telegram](https://img.shields.io/badge/discussion%20dev-Telegram-blue.svg?logo=telegram)](https://t.me/HighCapable_Dev)
[![QQ](https://img.shields.io/badge/discussion%20dev-QQ-blue.svg?logo=tencent-qq&logoColor=red)](https://qm.qq.com/cgi-bin/qm/qr?k=Pnsc5RY6N2mBKFjOLPiYldbAbprAU3V7&jump_from=webapi&authKey=X5EsOVzLXt1dRunge8ryTxDRrh9/IiW1Pua75eDLh9RE3KXE+bwXIYF5cWri/9lf)

<img src="img-src/icon.png" width = "100" height = "100" alt="LOGO"/>

⛱️ Kotlin でビルドされた効率的なフック API と Xposed モジュールソリューションです。

[English](README.md) | [简体中文](README-zh-CN.md)

| <img src="https://github.com/HighCapable/.github/blob/main/img-src/logo.jpg?raw=true" width = "30" height = "30" alt="LOGO"/> | [HighCapable](https://github.com/HighCapable) |
|-------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------|

このプロジェクトは上記の組織に属しています。**この組織をフォローするには上記のリンクをクリック**して、その他の優れたプロジェクトをご確認ください。

## これは何でしょうか?

これは、Kotlin を使用して Xposed API に基づきビルドされた効率的なフック API です。Xposed モジュールの開発のための豊富な機能拡張が作成できます。

この API の名前は[ももくり](https://www.comico.jp/comic/29)のヒロイン、[栗原雪](https://momokuri-anime.jp/character.html)の名前から由来しています。

以前は[開発の学習でのプロジェクト](https://github.com/fankes/TMore)で使用されていた Innocent Xposed API でしたが、名前が変更され現在はオープンソース化されています。

## 始め方

より詳細なチュートリアルとコンテンツに関しては、[こちら](https://highcapable.github.io/YukiHookAPI/en/)をクリックしてドキュメントのページをご確認ください。

サポートに関する情報については、[こちら](https://highcapable.github.io/YukiHookAPI/en/guide/supportive)をクリックで確認できます。

## 協力

以下は `YukiHookAPI` の使用と連携をしているプロジェクトです。

| リポジトリ                                                                      | 開発者                                       |
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
| [FuckShake](https://github.com/pwh-pwh/fuck_shake)                              | [Coderpwh](https://github.com/pwh-pwh)          |
| [MIUI更新进化](https://miup.utssg.xyz)                                              | [ZQDesigned](https://github.com/ZQDesigned)     |
| [MIUI录屏进化](https://www.coolapk.com/apk/UTSSG.ZQDesigned.miuirecordercracker)    | [ZQDesigned](https://github.com/ZQDesigned)     |
| [Fuck AD](https://github.com/hujiayucc/Fuck-AD)                                 | [hujiayucc](https://github.com/hujiayucc)       |
| [Zuiyou ADFree](https://github.com/kazutoiris/zuiyou-adfree)                    | [kazutoiris](https://github.com/kazutoiris)     |
| [Dingda ADFree](https://github.com/kazutoiris/dingda-adfree)                    | [kazutoiris](https://github.com/kazutoiris)     |

そこのあなたも `YukiHookAPI` を使って開発をしていますか? 是非、**PR** をしてあなたのリポジトリを上記のリストに追加しませんか? (プライベートリポジトリは Web リンクを出す必要はありません)

## プロモーション

<!--suppress HtmlDeprecatedAttribute -->
<div align="center">
     <h2>ねぇ、ちょっと訊いて! 👋</h2>
     <h3>ここではAndroid の開発ツール、UI デザイン、Gradle プラグイン、実用的なソフトウェアなどの関連したプロジェクトを紹介しています。</h3>
     <h3>プロジェクトがあなたの役に立てたのであれば、Star を付けてください!</h3>
     <h3>すべてのプロジェクトは無料でオープンソースであり、対応するオープンソースライセンスに従っています。</h3>
     <h1><a href="https://github.com/fankes/fankes/blob/main/project-promote/README.md">→ 私のプロジェクトの詳細はここをクリックしてください ←</a></h1>
</div>

## Star の推移

![Star History Chart](https://api.star-history.com/svg?repos=HighCapable/YukiHookAPI&type=Date)

## サードパーティーのソースについて

- [Kotlin Symbol Processing API](https://github.com/google/ksp)
- [FreeReflection](https://github.com/tiann/FreeReflection)

## ライセンス

- [Apache-2.0](https://www.apache.org/licenses/LICENSE-2.0)

```
Apache License Version 2.0

Copyright (C) 2019-2024 HighCapable

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

Copyright © 2019-2024 HighCapable
