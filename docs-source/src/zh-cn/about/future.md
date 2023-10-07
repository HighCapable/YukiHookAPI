# 展望未来

> 未来是美好的，也是不确定的，让我们共同期待 `YukiHookAPI` 在未来的发展空间。

## 未解决的问题

> 这里收录了 `YukiHookAPI` 尚未解决的问题。

### YukiHookPrefsBridge

目前仅限完美支持 LSPosed，其它 Xposed 框架需要降级模块 API。

可能完全不支持太极，太极在高版本系统上需要更低的 API 才能适配。

部分 Xposed 模块开发者目前选择 Hook 目标 APP 内置 Sp 存储方案解决模块设置共享问题。

后期 Android 系统的权限将越来越严格，`selinux` 就是目前面临的一个大问题，有待讨论和研究。

::: tip 2023.10.06 更新

LSPosed 现已实验性推出了 [Modern Xposed API](https://github.com/libxposed)，它采用 Service 的方式与模块通信，这将能够解决模块数据存储的问题。

为了保证大部分模块的兼容性，后期 **YukiHookAPI** 计划使用自定义的 ContentProvider 实现模块与宿主的数据互通，敬请期待。 

:::

## 未来的计划

> 这里收录了 `YukiHookAPI` 可能会在后期添加的功能。

### 支持独立使用的 Lite 版本

如果你喜欢 `YukiHookAPI` 的反射 API，但你的项目可能并不需要相关 Hook 功能。

那么这里有一个好消息要告诉你：

`YukiHookAPI` 的核心反射 API 已被解耦合为 [YukiReflection](https://github.com/fankes/YukiReflection) 项目，它现在能在任何 Android 项目中使用。

::: tip 待讨论

目前 API 只支持通过自动处理程序绑定到 **xposed_init**，若您不喜欢自动处理程序，一定要自己实现模块装载入口，未来会按照需求人数推出仅有 API 功能的 Lite 版本，你可向我们提出 **issues**。

:::

API 已经提供了 Xposed 原生 API 监听接口，你可以 [在这里](../config/xposed-using#原生-xposed-api-事件) 找到或查看 Demo 的实现方法。

### 里程碑计划

下方这些计划已在 GitHub 的 `issues` 中发布，你可以查看每个项目的进度。

所有功能预计在 `2.0.0` 版本完成，敬请期待。

- [New Xposed Module Config Plan](https://github.com/fankes/YukiHookAPI/issues/49)
- [New Hook Entry Class](https://github.com/fankes/YukiHookAPI/issues/48)
- [New Hook Code Style](https://github.com/fankes/YukiHookAPI/issues/33)