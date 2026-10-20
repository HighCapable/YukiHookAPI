# yukihook-runtime-libxposed

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-runtime-libxposed?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-runtime-libxposed%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange?logo=android&style=flat-square)

This is the core base module for YukiHook on the [libxposed API](https://github.com/libxposed).

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you refer to [yukihook-bom](./yukihook-bom.md) first to manage versions with BOM.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
yukihook-runtime-libxposed = "<version>"
libxposed-api = "<libxposed-version>"

[libraries]
yukihook-runtime-libxposed = { module = "com.highcapable.yukihookapi:yukihook-runtime-libxposed", version.ref = "yukihook-runtime-libxposed" }
libxposed-api = { module = "io.github.libxposed:api", version.ref = "libxposed-api" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.yukihook.runtime.libxposed)
compileOnly(libs.libxposed.api)
```

Please change `<version>` to the version displayed at the top of this document, and `<libxposed-version>` to the latest version from [here](https://github.com/libxposed/api).

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-runtime-libxposed:<version>")
compileOnly("io.github.libxposed:api:<libxposed-version>")
```

Please change `<version>` to the version displayed at the top of this document, and `<libxposed-version>` to the latest version from [here](https://github.com/libxposed/api).

::: danger

Please add the libxposed API with `compileOnly`, it must not be packaged into the module.

Only one base module can exist in a build variant. If you need to be compatible with multiple base modules, please refer to [Multi-Entry Variants](./yukihook-core/hook-entry/multi-entry-variants.md).

:::

## Function Introduction

You can view the KDoc [click here](kdoc://yukihook-runtime-libxposed).

The core base module provides essential support for using YukiHook on the libxposed API. Please refer to [yukihook-core](./yukihook-core.md) to use these features.

### Entry Annotation

This section describes the meaning of each parameter of the entry annotation. For usage, please refer to [Module Entry → Create the Entry Class](./yukihook-core/hook-entry.md#create-the-entry-class).

> The following example

```kotlin
@YukiHookLibXposedEntry(
    targetApiVersion = 101,
    scope = ["io.github.myapp", "system"]
)
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        // Your code here.
    }
}
```

| Parameter          | Description                                                                                                         |
| ------------------ | ------------------------------------------------------------------------------------------------------------------- |
| `entryClassName`   | The generated entry class name, see [Entry Class Name](./yukihook-compiler.md#entry-class-name) for details         |
| `minApiVersion`    | The minimum Xposed API version the module supports, `101` by default and cannot be lower than `101`                 |
| `targetApiVersion` | (Required) The target Xposed API version of the module, cannot be lower than `minApiVersion`                        |
| `scope`            | Recommended scope package names, use `system` for the system framework, empty by default                            |
| `staticScope`      | Whether the scope can only be selected from `scope`, `false` by default                                             |
| `hotReload`        | The module hot reload policy, `NONE` by default, see [Module Hot Reload](./yukihook-core/hot-reload.md) for details |
| `javaEntries`      | Additional native libxposed API module entry classes, must extend `XposedModule`                                    |
| `nativeEntries`    | Library file names of native entries, such as `libexample.so`                                                       |

Based on these parameters, the compiler generates `java_init.list`, `module.prop`, `scope.list` and `native_init.list` (only when `nativeEntries` is not empty) in `META-INF/xposed`.

::: warning

In the scope, `system` is a virtual package name defined by the API. `android` is the real package name of the system framework and its process name is `system`, YukiHook only loads it as a system service.

:::

### Module Information

Define your module information in `AndroidManifest.xml`, `label` is the module name and `description` is the module description.

> The following example

```xml
<application
    android:name=".MyApplication"
    android:description="@string/module_description"
    android:label="@string/app_name">

    <!-- ... -->
</application>
```

### API Versions

`minApiVersion` decides the lowest API version the module can run on, and `targetApiVersion` decides the feature behavior YukiHook supports.

Some features currently used by YukiHook require API 102 or above, such as [Module Hot Reload](./yukihook-core/hot-reload.md).

When a feature you use requires a version higher than `minApiVersion`, you need to check `YukiHook.Status.Executor.apiVersion` before using it.

> The following example

```kotlin
if (YukiHook.Status.Executor.apiVersion >= 102) {
    // Your code here.
}
```

### Native Module Entries

If you also need native framework Java entries, you can declare them in `javaEntries`, they are added after the entry generated by YukiHook.

> The following example

```kotlin
@YukiHookLibXposedEntry(
    targetApiVersion = 101,
    javaEntries = [MyXposedEntry::class],
    nativeEntries = ["libexample.so"]
)
object HookEntry : YukiHookXposedModule
```

::: warning

Libraries in `nativeEntries` need to be packaged in the module APK, the hook framework loads them as native entries.

:::

### Framework Events

YukiHook keeps the ability to call the native APIs of the framework, you can register framework event listeners with `registerFrameworkEvents` in `onInit`.

> The following example

```kotlin
registerFrameworkEvents {
    onModuleLoaded { param ->
        // The `this` object is `XposedInterface`.
    }
    onPackageLoaded { param ->
        // Your code here.
    }
    onPackageReady { param ->
        // Your code here.
    }
    onSystemServerStarting { param ->
        // Your code here.
    }
    // The following events are new in API 102.
    onHotReloading { param ->
        // Return whether this hot reload is allowed.
        true
    }
    onHotReloaded { param ->
        // Your code here.
    }
}
```

Framework event callbacks are completely independent of `onHook` of YukiHook, you can keep using YukiHook in `onHook`.

::: danger

`registerFrameworkEvents` can only be registered in `onInit` and only once, it can also only be called once per module instance.

:::