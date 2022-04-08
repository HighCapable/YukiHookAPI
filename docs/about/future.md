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

### 支持资源 Hook 和注入系统框架

目前的 API 仅支持 APP 内的功能 Hook，并不支持 `Resource` 的替换以及 Hook 系统框架。

API 还未实现对 `handleInitPackageResources` 和 `initZygote` 的调用。

在未来会根据使用和需求人数加上这个功能，如有需求你也可以向我们提交 Pull Request 来贡献你的代码。

### 支持更多 Hook Framework

作为 API 来讲，目前仅仅对接 `XposedBridge` 作为兼容层，还是有一定的局限性。

大部分 `inline hook` 没有 `Java` 兼容层，后期可能会考虑 `native hook` 的 `Java` 兼容层适配。