# yukihook-bom

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-bom?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-bom%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

This is the BOM dependency for unified version management of YukiHook related modules.

## Configure Dependency

You can add this module to your project using the following method.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

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

Configure dependency in your project's `build.gradle.kts`.

```kotlin
platform(libs.yukihook.bom).apply {
    implementation(this)
    ksp(this)
}

implementation(libs.yukihook.core)
ksp(libs.yukihook.compiler)

// (Optional) If you want to use KavaRef in YukiHook.
implementation(libs.yukihook.extension.kavaref)

// Choose the base module that matches the Xposed API you use (choose one).
implementation(libs.yukihook.runtime.libxposed)
implementation(libs.yukihook.runtime.xposed82)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
platform("com.highcapable.yukihookapi:yukihook-bom:<version>").apply {
    implementation(this)
    ksp(this)
}

implementation("com.highcapable.yukihookapi:yukihook-core")
ksp("com.highcapable.yukihookapi:yukihook-compiler")

// (Optional) If you want to use KavaRef in YukiHook.
implementation("com.highcapable.yukihookapi:yukihook-extension-kavaref")

// Choose the base module that matches the Xposed API you use (choose one).
implementation("com.highcapable.yukihookapi:yukihook-runtime-libxposed")
implementation("com.highcapable.yukihookapi:yukihook-runtime-xposed82")
```

Please change `<version>` to the version displayed at the top of this document.

## Function Introduction

`yukihook-bom` does not contain actual code itself. It only serves as the BOM of YukiHook related modules for unified dependency version management.

It currently manages the versions of the following modules:

- [yukihook-core](./yukihook-core.md)
- [yukihook-compiler](./yukihook-compiler.md)
- [yukihook-extension-kavaref](./yukihook-extension-kavaref.md)
- [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md)
- [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)