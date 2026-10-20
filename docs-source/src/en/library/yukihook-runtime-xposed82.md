# yukihook-runtime-xposed82

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-runtime-xposed82?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-runtime-xposed82%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

This is the core base module for YukiHook on the [Xposed Framework API](https://api.xposed.info).

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you refer to [yukihook-bom](./yukihook-bom.md) first to manage versions with BOM.

::: warning

The Xposed Framework API is not published on Maven Central.
To use it, please refer to [Quick Start → Configure Repositories](../guide/quick-start.md#configure-repositories).

:::

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
yukihook-runtime-xposed82 = "<version>"

[libraries]
yukihook-runtime-xposed82 = { module = "com.highcapable.yukihookapi:yukihook-runtime-xposed82", version.ref = "yukihook-runtime-xposed82" }
xposed-framework-api = { module = "de.robv.android.xposed:api", version = "82" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.yukihook.runtime.xposed82)
compileOnly(libs.xposed.framework.api)
```

Please change `<version>` to the version displayed at the top of this document.

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-runtime-xposed82:<version>")
compileOnly("de.robv.android.xposed:api:82")
```

Please change `<version>` to the version displayed at the top of this document.

::: danger

Please add the Xposed Framework API with `compileOnly`, it must not be packaged into the module.

Only one base module can exist in a build variant. If you need to be compatible with multiple base modules, please refer to [Multi-Entry Variants](./yukihook-core/hook-entry/multi-entry-variants.md).

:::

## Function Introduction

You can view the KDoc [click here](kdoc://yukihook-runtime-xposed82).

The core base module provides essential support for using YukiHook on the Xposed Framework API. Please refer to [yukihook-core](./yukihook-core.md) to use these features.

### Entry Annotation

This section describes the meaning of each parameter of the entry annotation. For usage, please refer to [Module Entry → Create the Entry Class](./yukihook-core/hook-entry.md#create-the-entry-class).

> The following example

```kotlin
@YukiHookXposed82Entry
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        // Your code here.
    }
}
```

| Parameter              | Description                                                                                                                           |
| ---------------------- | ------------------------------------------------------------------------------------------------------------------------------------- |
| `modulePackageName`    | The package name of the module, read automatically when empty, see the notes below for details                                        |
| `entryClassName`       | The generated entry class name, see [Entry Class Name](./yukihook-compiler.md#entry-class-name) for details                           |
| `generateModuleStatus` | Whether to generate the module status code, `true` by default, disabling it affects [Module Status](./yukihook-core/module-status.md) |

Based on these parameters, the compiler generates `assets/xposed_init`.

#### Module Package Name

When the `modulePackageName` parameter is not set, the compiler reads `BuildConfig.APPLICATION_ID` from the only `BuildConfig.java` visible in the current compilation as the module package name. The build fails when it cannot be read or multiple ones exist.

In Android Gradle Plugin 8+, you need to enable `buildConfig` manually in your project's `build.gradle.kts`.

> The following example

```kotlin
android {
    buildFeatures {
        buildConfig = true
    }
}
```

If you think it is necessary to define the module package name manually, you can set the `modulePackageName` parameter directly.
The module package name is not checked at compile time, and you always get a warning at compile time to remind you to check whether it is correct.

::: danger

Do not fill `BuildConfig.APPLICATION_ID` directly into `modulePackageName`, it becomes empty during compilation, this depends on the behavior of the Android Gradle Plugin.

:::

### Module Information

Define your module information in `AndroidManifest.xml`.

The base module already declares `xposedmodule` and `xposedminversion` (`82` by default), please add other meta-data as needed.

> The following example

```xml
<application
    android:name=".MyApplication"
    android:label="@string/app_name">

    <!-- Set your module description. -->
    <meta-data
        android:name="xposeddescription"
        android:value="@string/module_description" />

    <!-- LSPosed only: Set your module scope. -->
    <meta-data
        android:name="xposedscope"
        android:resource="@array/module_scope" />

    <!-- (Optional) Override the minimum Xposed version. -->
    <meta-data
        android:name="xposedminversion"
        android:value="93"
        tools:replace="android:value" />
</application>
```

### Framework Events

YukiHook keeps the ability to call the native APIs of the framework, you can register framework event listeners with `registerFrameworkEvents` in `onInit`.

> The following example

```kotlin
registerFrameworkEvents {
    onInitZygote {
        // The `it` object is `StartupParam`.
    }
    onHandleLoadPackage {
        // The `it` object is `LoadPackageParam`.
    }
}
```

Framework event callbacks are completely independent of `onHook` of YukiHook, you can keep using YukiHook in `onHook`.

::: danger

`registerFrameworkEvents` can only be registered in `onInit` and only once, it can also only be called once per module instance.

YukiHook no longer supports resources hook events, even if the framework itself may support them, so the `handleInitPackageResources` event is not provided.

:::