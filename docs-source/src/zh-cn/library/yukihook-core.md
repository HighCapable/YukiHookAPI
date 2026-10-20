# yukihook-core

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-core?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-core%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

这是 YukiHook 的核心依赖，你需要引入此模块才能使用 YukiHook 的基本功能。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [yukihook-bom](./yukihook-bom.md) 使用 BOM 统一管理版本。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
yukihook-core = "<version>"

[libraries]
yukihook-core = { module = "com.highcapable.yukihookapi:yukihook-core", version.ref = "yukihook-core" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.yukihook.core)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-core:<version>")
```

请将 `<version>` 修改为此文档顶部显示的版本。

::: danger

此模块不能单独工作，作为 Xposed 模块使用时，你还需要同时引入 [yukihook-compiler](./yukihook-compiler.md) 以及一个 [底座模块](../guide/quick-start.md#choose-a-base-module)。

:::

## 功能介绍

你可以 [点击这里](kdoc://yukihook-core) 查看 KDoc。

此模块提供了 YukiHook 全部对外公开的 API，这些 API 在底座模块中的用法完全一致。部分 API 仅在特定底座模块或特定 Xposed API 版本中可用，它们会标记 `@RequiresRuntime` 注解。

你可以点击下方的功能前往查看详细的用法。

- [模块入口](./yukihook-core/hook-entry.md)
- [Hook 用法](./yukihook-core/hook-usage.md)
- [调试日志](./yukihook-core/logger.md)
- [模块状态](./yukihook-core/module-status.md)
- [模块偏好设置](./yukihook-core/preferences.md)
- [模块共享文件](./yukihook-core/shared-files.md)
- [模块与宿主通讯桥](./yukihook-core/data-channel.md)
- [宿主生命周期](./yukihook-core/host-lifecycle.md)
- [模块资源注入](./yukihook-core/resource-injection.md)
- [模块热重载](./yukihook-core/hot-reload.md)