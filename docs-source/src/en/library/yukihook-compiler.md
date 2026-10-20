# yukihook-compiler

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-compiler?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-compiler%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

This is the YukiHook Xposed module automatic compilation module.

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you first refer to [yukihook-bom](./yukihook-bom.md) to use BOM for unified version management.

::: warning

You need to integrate the [Google KSP](https://github.com/google/ksp/releases) plugin in your project that is suitable for the current Kotlin version of your project.

:::

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
ksp = "<ksp-version>"
yukihook-compiler = "<version>"

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

[libraries]
yukihook-compiler = { module = "com.highcapable.yukihookapi:yukihook-compiler", version.ref = "yukihook-compiler" }
```

Configure dependency in your root project's `build.gradle.kts`.

```kotlin
plugins {
    // ...
    alias(libs.plugins.ksp) apply false
}
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
plugins {
    // ...
    alias(libs.plugins.ksp)
}

dependencies {
    // ...
    ksp(libs.yukihook.compiler)
}
```

Please change `<version>` to the version displayed at the top of this document,
and change `<ksp-version>` to the KSP version corresponding to the Kotlin version currently used by your project.

### Traditional Method

Configure dependency in your root project's `build.gradle.kts`.

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "<ksp-version>" apply false
}
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp")
}

dependencies {
    // ...
    ksp("com.highcapable.yukihookapi:yukihook-compiler:<version>")
}
```

Please change `<version>` to the version displayed at the top of this document,
and change `<ksp-version>` to the KSP version corresponding to the Kotlin version currently used by your project.

::: danger

The versions of this module and [yukihook-core](./yukihook-core.md) must be the same. We recommend managing versions with the BOM.

:::

## Function Introduction

This module finds the class with the entry annotation in your project, and generates the native entry class and configuration files required by the corresponding hook framework based on the base module in use.

### Entry Annotations

**Only one** entry annotation can exist in a build variant, and it must match the base module you added. If you need to support multiple base modules at the same time, please refer to [Multi-Entry Variants](./yukihook-core/hook-entry/multi-entry-variants.md).

| Entry Annotation          | Base Module                                                   |
| ------------------------- | ------------------------------------------------------------- |
| `@YukiHookLibXposedEntry` | [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) |
| `@YukiHookXposed82Entry`  | [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)   |

For the parameters of each entry annotation, please refer to the corresponding base module documentation.

::: danger

The following cases cause the compilation to fail.

- A build variant contains multiple entry annotations
- A build variant has no base module or multiple base modules
- The entry annotation does not match the base module you added
- The class with the entry annotation does not implement the `YukiHookXposedModule` interface, or its constructor has parameters
- Configuration files required by the hook framework are declared manually in the source directory (such as `resources/META-INF/xposed` and `assets/xposed_init`)

:::

### Entry Class Name

The hook framework entry class name is generated from the full class name of your entry class with the `_YukiHookXposedInit` suffix by default.

Suppose this is your entry class.

> The following example

```kotlin
@YukiHookLibXposedEntry(targetApiVersion = 101)
object HookEntry : YukiHookXposedModule
```

The class names after compilation are as follows.

> The following example

```text:no-line-numbers
HookEntry ← Your entry class
HookEntry_YukiHookXposedInit ← The generated framework entry class
```

You can use the `entryClassName` parameter of the entry annotation to customize the generated entry class name.

> The following example

```kotlin
@YukiHookLibXposedEntry(
    entryClassName = "MyHookEntry",
    targetApiVersion = 102
)
object HookEntry : YukiHookXposedModule
```

The class names after compilation are as follows.

> The following example

```text:no-line-numbers
HookEntry ← Your entry class
MyHookEntry ← The generated framework entry class
```

::: tip

The entry class can be defined with `class` or `object`, but it is recommended to use `object` to make sure each injected process runs it as a singleton.

:::

::: danger

A custom entry class name can only contain letters, digits and underscores, cannot start with a digit, and cannot be the same as your entry class name.

:::

### Generated Content

The entry and configuration files are generated in the build directory for each build variant, you don't need to and should not create them manually in the source directory.

| Base Module                                                   | Generated Files                                                                   |
| ------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) | `META-INF/xposed/java_init.list`, `module.prop`, `scope.list`, `native_init.list` |
| [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)   | `assets/xposed_init`                                                              |

::: warning

The compiler does not generate or modify your `AndroidManifest.xml`, information such as the module name and description needs to be configured according to the specific requirements of the corresponding base module.

:::