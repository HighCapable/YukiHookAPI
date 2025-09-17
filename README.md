# Yuki Hook

A modern Hook API and Xposed module solution.

English | [简体中文](README-zh-CN.md)

## Branch Description

The `2.x` branch is still in development state. All APIs may change before the official version is
released, and no documentation is provided at present. Please bear the risk of direct use at your
own risk.

After the official version is released, the main branch will be switched back to the main branch.
The `1.x` version will be named `1.x` from the `master` branch. It is not recommended to use it only
for regular maintenance.

## Branch Introduction

The `2.x` version is a completely refactored version and does not include `1.x`
Any historical code of the code will function to continue the design scheme and natively
support [libxposed](https://github.com/libxposed).

The `2.x` version will adopt a modular design, and the general content is designed as follows.

Unless there is no mark `*`, all are optional dependencies introduced.

| Name                             | Description                                                                                     |
|----------------------------------|-------------------------------------------------------------------------------------------------|
| yukihook-core                    | Core module, including core Hook API framework                                                  |
| *yukihook-api-rovo89             | Rovo89 Xposed API Support                                                                       |
| *yukihook-api-libxposed          | `libxposed` API Support                                                                         |
| *yukihook-api-helper             | Hook DSL Syntax Sugar and Functional Modules                                                    |
| *yukihook-xposedmodule           | Xposed module supports, including entry points and other configuration functions                |
| *yukihook-xposedmodule-extension | Xposed module extension functions, including resource injection, Activity proxy, etc            |
| *yukihook-gradle-plugin          | Gradle plugin, simplifies Xposed module configuration, and automatically generates entry points |

### Explanation

If you are an Xposed module developer, you need to introduce `yukihook-core`, `yukihook-api-helper`
and
`yukihook-xposedmodule` plus an Xposed API support module;

If you choose to build the Hook API into the app itself, you only need to introduce `yukihook-core`
and
`yukihook-api-helper` plus an Xposed API support module.

### Xposed API Support Module

`yukihook-api-rovo89` and `yukihook-api-libxposed` are native support provided by YukiHook, and you
can also use it yourself or a third-party
The Hook API is connected to `yukihook-core` to implement the corresponding functions.

### Pay Attention

`yukihook-api-rovo89` and `yukihook-api-libxposed` can only select one introduction, otherwise
conflict will occur.

YukiHook `2.x`, like `1.x`, only provides design structure and framework, and does not provide any
core Hook features.

## About Reflection API

YukiHook `2.x` does not come with a reflection API, you can choose the
recommended [KavaRef](https://github.com/HighCapable/KavaRef)
or [DexKit](https://github.com/LuckyPray/DexKit), YukiHook `2.x` will be natively supported.

The currently prepared extension modules are as follows.

| Name                            | Description                                                                  |
|---------------------------------|------------------------------------------------------------------------------|
| *yukihook-api-extension-kavaref | [KavaRef](https://github.com/HighCapable/KavaRef) related extended functions |
| *yukihook-api-extension-dexkit  | [DexKit](https://github.com/LuckyPray/DexKit) related extended functions     |

## Contribution

We welcome developers to participate in the development. If you have good ideas and design ideas,
you can ask this project for PR at any time. We welcome any constructive ideas.