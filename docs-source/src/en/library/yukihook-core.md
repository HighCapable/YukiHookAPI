# yukihook-core

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-core?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-core%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

This is the core dependency of YukiHook, you need to add this module to use the basic features of YukiHook.

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you refer to [yukihook-bom](./yukihook-bom.md) first to manage versions with BOM.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
yukihook-core = "<version>"

[libraries]
yukihook-core = { module = "com.highcapable.yukihookapi:yukihook-core", version.ref = "yukihook-core" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.yukihook.core)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-core:<version>")
```

Please change `<version>` to the version displayed at the top of this document.

::: danger

This module does not work alone. To use it as an Xposed module, you also need to add [yukihook-compiler](./yukihook-compiler.md) and one [base module](../guide/quick-start.md#choose-a-base-module).

:::

## Function Introduction

You can view the KDoc [click here](kdoc://yukihook-core).

This module provides all public APIs of YukiHook, and these APIs are used in exactly the same way on the base modules. Some APIs are only available in a specific base module or Xposed API version, they are marked with the `@RequiresRuntime` annotation.

Click a feature below to view its detailed usage.

- [Module Entry](./yukihook-core/hook-entry.md)
- [Hook Usage](./yukihook-core/hook-usage.md)
- [Debug Logs](./yukihook-core/logger.md)
- [Module Status](./yukihook-core/module-status.md)
- [Module Preferences](./yukihook-core/preferences.md)
- [Module Shared Files](./yukihook-core/shared-files.md)
- [Module and Host Channel](./yukihook-core/data-channel.md)
- [Host Lifecycle](./yukihook-core/host-lifecycle.md)
- [Module Resource Injection](./yukihook-core/resource-injection.md)
- [Module Hot Reload](./yukihook-core/hot-reload.md)