# yukihook-bom

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-bom?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-bom%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

这是针对 YukiHook 相关模块统一版本管理的 BOM 依赖。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
yukihook-bom = "<version>"

[libraries]
yukihook-bom = { module = "com.highcapable.yukihookapi:yukihook-bom", version.ref = "yukihook-bom" }
yukihook-core = { module = "com.highcapable.yukihookapi:yukihook-core" }
yukihook-compiler = { module = "com.highcapable.yukihookapi:yukihook-compiler" }
yukihook-extension-kavaref = { module = "com.highcapable.yukihookapi:yukihook-extension-kavaref" }
yukihook-runtime-libxposed = { module = "com.highcapable.yukihookapi:yukihook-runtime-libxposed" }
yukihook-runtime-xposed82 = { module = "com.highcapable.yukihookapi:yukihook-runtime-xposed82" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
platform(libs.yukihook.bom).apply {
    implementation(this)
    ksp(this)
}

implementation(libs.yukihook.core)
ksp(libs.yukihook.compiler)

// (可选) 如果你需要在 YukiHook 中使用 KavaRef
implementation(libs.yukihook.extension.kavaref)

// 根据你使用的 Xposed API 选择合适的底座模块 (二选一)
implementation(libs.yukihook.runtime.libxposed)
implementation(libs.yukihook.runtime.xposed82)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
platform("com.highcapable.yukihookapi:yukihook-bom:<version>").apply {
    implementation(this)
    ksp(this)
}

implementation("com.highcapable.yukihookapi:yukihook-core")
ksp("com.highcapable.yukihookapi:yukihook-compiler")

// (可选) 如果你需要在 YukiHook 中使用 KavaRef
implementation("com.highcapable.yukihookapi:yukihook-extension-kavaref")

// 根据你使用的 Xposed API 选择合适的底座模块 (二选一)
implementation("com.highcapable.yukihookapi:yukihook-runtime-libxposed")
implementation("com.highcapable.yukihookapi:yukihook-runtime-xposed82")
```

请将 `<version>` 修改为此文档顶部显示的版本。

## 功能介绍

`yukihook-bom` 本身不包含实际代码，它仅作为 YukiHook 相关模块的 BOM 用于统一管理依赖版本。

目前它会管理以下模块的版本：

- [yukihook-core](./yukihook-core.md)
- [yukihook-compiler](./yukihook-compiler.md)
- [yukihook-extension-kavaref](./yukihook-extension-kavaref.md)
- [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md)
- [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)