# Yuki Hook

一个现代的 Hook API 与 Xposed 模块解决方案。

[English](README.md) | 简体中文

## 分支说明

`2.x` 分支目前仍处于开发状态，所有 API 在发布正式版本之前均可能发生变化，且目前不提供任何文档，直接使用的风险请自行承担。

正式版发布后，将切回主分支 `main`，`1.x` 版本将从 `master` 分支命名为 `1.x`，仅做定期维护并不再推荐使用。

## 分支介绍

`2.x` 版本为完全重构版，不包含 `1.x`
的任何历史代码，功能上将延续设计方案，并原生支持 [libxposed](https://github.com/libxposed)。

`2.x` 版本将采用模块化设计，大致内容设计如下。

除非没有标记 `*`，否则均为可选依赖引入。

| 名称                               | 描述                                |
|----------------------------------|-----------------------------------|
| yukihook-core                    | 核心模块，包含核心 Hook API 框架             |
| *yukihook-api-rovo89             | Rovo89 Xposed API 支持              |
| *yukihook-api-libxposed          | `libxposed` API 支持                |
| *yukihook-api-helper             | Hook DSL 语法糖和功能模块                 |
| *yukihook-xposedmodule           | Xposed 模块支持，包含入口点等配置功能            |
| *yukihook-xposedmodule-extension | Xposed 模块扩展功能，包含资源注入、Activity 代理等 |
| *yukihook-gradle-plugin          | Gradle 插件，简化 Xposed 模块配置，自动生成入口点  |

### 说明

如果你是作为 Xposed 模块开发者，你需要引入 `yukihook-core`、`yukihook-api-helper` 和
`yukihook-xposedmodule` 外加一个 Xposed API 支持模块；

如果你是选择将 Hook API 内置进应用自身，你只需要引入 `yukihook-core` 和
`yukihook-api-helper` 外加一个 Xposed API 支持模块。

### Xposed API 支持模块

`yukihook-api-rovo89` 与 `yukihook-api-libxposed` 是 YukiHook 原生提供的支持，你也可以将自己或第三方的
Hook API 对接到 `yukihook-core` 以实现对应的功能。

### 注意

`yukihook-api-rovo89` 与 `yukihook-api-libxposed` 只能选择一个引入，否则会发生冲突。

YukiHook `2.x` 与 `1.x` 一样，只提供设计结构和框架，不提供任何核心 Hook 功能。

## 关于反射 API

YukiHook `2.x` 不会自带反射 API，你可以选用推荐的 [KavaRef](https://github.com/HighCapable/KavaRef)
或是 [DexKit](https://github.com/LuckyPray/DexKit)，YukiHook `2.x` 将原生支持。

目前准备支持的扩展模块如下。

| 名称                              | 描述                                                       |
|---------------------------------|----------------------------------------------------------|
| *yukihook-api-extension-kavaref | [KavaRef](https://github.com/HighCapable/KavaRef) 相关扩展功能 |
| *yukihook-api-extension-dexkit  | [DexKit](https://github.com/LuckyPray/DexKit) 相关扩展功能     |

## 贡献

欢迎广大开发者参与到开发中来，如果有好的想法与设计思路，你可以随时向本项目提 PR，我们欢迎任何有建设性想法的功能。