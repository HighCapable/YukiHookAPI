# yukihook-extension-kavaref

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-extension-kavaref?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-extension-kavaref%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

This is an extended dependency for KavaRef-related features in YukiHook.

## Configure Dependency

You can add this module to your project using the following method.

We recommend that you refer to [yukihook-bom](./yukihook-bom.md) first to manage versions with BOM.

### Version Catalog (Recommended)

Add dependency in your project's `gradle/libs.versions.toml`.

```toml
[versions]
yukihook-extension-kavaref = "<version>"
kavaref-bom = "<kavaref-version>"

[libraries]
yukihook-extension-kavaref = { module = "com.highcapable.yukihookapi:yukihook-extension-kavaref", version.ref = "yukihook-extension-kavaref" }
kavaref-bom = { module = "com.highcapable.kavaref:kavaref-bom", version.ref = "kavaref-bom" }
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core" }
kavaref-android = { module = "com.highcapable.kavaref:kavaref-android" }
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension" }
```

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation(libs.yukihook.extension.kavaref)

implementation(platform(libs.kavaref.bom))
implementation(libs.kavaref.core)
implementation(libs.kavaref.android)
implementation(libs.kavaref.extension)
```

Please change `<version>` to the version displayed at the top of this document,
and change `<kavaref-version>` to the latest version from [here](https://highcapable.github.io/KavaRef/en/about/changelog).

### Traditional Method

Configure dependency in your project's `build.gradle.kts`.

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-extension-kavaref:<version>")

implementation(platform("com.highcapable.kavaref:kavaref-bom:<kavaref-version>"))
implementation("com.highcapable.kavaref:kavaref-core")
implementation("com.highcapable.kavaref:kavaref-android")
implementation("com.highcapable.kavaref:kavaref-extension")
```

Please change `<version>` to the version displayed at the top of this document,
and change `<kavaref-version>` to the latest version from [here](https://highcapable.github.io/KavaRef/en/about/changelog).

::: danger

This module does not add the dependencies of [KavaRef](https://github.com/HighCapable/KavaRef) automatically, please add KavaRef yourself as shown above.

:::

## Function Introduction

You can view the KDoc [click here](kdoc://yukihook-extension-kavaref).

This module provides KavaRef extensions that are used in `PackageParam`.
For detailed usage, please refer to [here](https://highcapable.github.io/KavaRef/en/library/kavaref-core), this will jump to the KavaRef documentation.

::: danger

These extensions can only be used in `PackageParam`. Since there are extensions with the same names in KavaRef,
please make sure to import the corresponding functions under `com.highcapable.yukihookapi.extension.kavaref`.

:::

### Hook Usage

You can use `hook`, `hookAll`, `intercept` and `interceptAll` directly on KavaRef's `MemberResolver`.

> The following example

```kotlin
loadApp("io.github.myapp") {
    "$packageName.MainActivity".toClass()
        .resolve()
        .firstMethod {
            name = "onCreate"
            parameters(Bundle::class)
        }.hook {
            // Your code here.
        }
}
```

They are used in exactly the same way as on `Member`, for details please refer to [Hook Usage](./yukihook-core/hook-usage.md).

You can also use `deoptimize` and `deoptimizeAll`, for details please refer to [Deoptimize](./yukihook-core/hook-usage.md#deoptimize).

::: tip

In [Module Hot Reload → Retain Values](./yukihook-core/hot-reload.md#retain-values),
since a `MemberResolver` object strongly references the `ClassLoader` of the module, you cannot cache it directly.

In this case, you can take out its `self` and use it as below.

> The following example

```kotlin
// A single method.
val foo = remember("myapp-foo-v1") {
    "$packageName.Demo".toClass()
        .resolve()
        .optional()
        .firstMethodOrNull {
            name = "foo"
            emptyParameters()
        }?.self
}
// A group of methods.
val bar = remember("myapp-bar-v1") {
    "$packageName.Demo".toClass()
        .resolve()
        .optional()
        .method {
            name = "bar"
            parameterCount { it in 0..1 }
        }.map { it.self }
}

foo?.hook {
    // Your code here.
}
bar.hookAll {
    // Your code here.
}
```

:::

### Original and Special Calls

You can use KavaRef's `MethodResolver` and `ConstructorResolver` to call original methods and constructors without hooks.

> The following example

```kotlin
loadApp("io.github.myapp") {
    // Suppose this is the instance of the Demo class.
    val instance: Any
    "$packageName.Demo".toClass()
        .resolve()
        .firstMethod {
            name = "foo"
            emptyParameters()
        }.of(instance).invokeOriginal()
}
```

| Function            | Description                                                                                     |
| ------------------- | ----------------------------------------------------------------------------------------------- |
| `invokeOriginal`    | Calls the original method without hooks                                                         |
| *`invokeSpecial`    | Calls the method non-virtually, the instance must be set with `of` first                        |
| `initializeSpecial` | Initializes an existing instance non-virtually with the constructor                             |
| *`createOriginal`   | Creates an instance with the original constructor without hooks                                 |
| *`createSpecial`    | Creates a subclass instance with the parent constructor, the subclass constructor is not called |

All the functions above also provide a version with the `Quietly` suffix, which ignores exceptions during the call and returns `null`.

::: danger

The functions marked with `*` above require the [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) base module.

:::

### Load Classes from the Host

In `PackageParam`, the following extensions with the same names load the `Class` with `hostClassLoader` by default.

- `String.toClass()`
- `String.toClassOrNull()`
- `VariousClass.toClass()`
- `VariousClass.toClassOrNull()`
- `lazyClass(...)`
- `lazyClassOrNull(...)`

> The following example

```kotlin
val DemoClass by lazyClass("io.github.myapp.Demo")

loadApp("io.github.myapp") {
    "$packageName.Demo".toClass()
    VariousClass("$packageName.Demo", "$packageName.NewDemo").toClass()
}
```

### Hidden API Bypass Resolver

YukiHook provides `AndroidHiddenApiBypassResolver`, you can use it where you need to reflect hidden system APIs.

It is replicated from [AndroidHiddenApiBypassResolver](https://highcapable.github.io/KavaRef/en/config/processor-resolvers.html#androidhiddenapibypass) in the KavaRef documentation.

> The following example

```kotlin
val resolver = AndroidHiddenApiBypassResolver()

"android.app.ActivityThread".toClass()
    .resolve()
    // Set a custom Member resolver.
    .processor(resolver)
    .firstMethod {
        name = "currentActivityThread"
        emptyParameters()
    }.invoke()
```