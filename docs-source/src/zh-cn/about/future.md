# 展望未来

> 未来是美好的，也是不确定的，让我们共同期待 `YukiHookAPI` 在未来的发展空间。

## 未解决的问题

> 这里收录了 `YukiHookAPI` 尚未解决的问题。

### YukiHookModulePrefs

目前仅限完美支持 LSPosed，其它 Xposed 框架需要降级模块 API。

可能完全不支持太极，太极在高版本系统上需要更低的 API 才能适配。

部分 Xposed 模块开发者目前选择 Hook 目标 APP 内置 Sp 存储方案解决模块设置共享问题。

后期 Android 系统的权限将越来越严格，`selinux` 就是目前面临的一个大问题，有待讨论和研究。

## 未来的计划

> 这里收录了 `YukiHookAPI` 可能会在后期添加的功能。

### 支持独立使用的 Lite 版本

::: tip 待讨论

目前 API 只支持通过自动处理程序绑定到 **xposed_init**，若您不喜欢自动处理程序，一定要自己实现模块装载入口，未来会按照需求人数推出仅有 API 功能的 Lite 版本，你可向我们提出 **issues**。

:::

API 已经提供了 Xposed 原生 API 监听接口，你可以 [在这里](../config/xposed-using#原生-xposed-api-事件) 找到或查看 Demo 的实现方法。

### 支持更多 Hook Framework

作为 API 来讲，目前仅仅对接 `XposedBridge` 作为兼容层，还是有一定的局限性。

大部分 `inline hook` 没有 `Java` 兼容层，后期可能会考虑 `native hook` 的 `Java` 兼容层适配。