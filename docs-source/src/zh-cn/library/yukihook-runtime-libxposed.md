# yukihook-runtime-libxposed

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-runtime-libxposed?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-runtime-libxposed%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange?logo=android&style=flat-square)

这是 YukiHook 在 [libxposed API](https://github.com/libxposed) 上的核心底座模块。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [yukihook-bom](./yukihook-bom.md) 使用 BOM 统一管理版本。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
yukihook-runtime-libxposed = "<version>"
libxposed-api = "<libxposed-version>"

[libraries]
yukihook-runtime-libxposed = { module = "com.highcapable.yukihookapi:yukihook-runtime-libxposed", version.ref = "yukihook-runtime-libxposed" }
libxposed-api = { module = "io.github.libxposed:api", version.ref = "libxposed-api" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.yukihook.runtime.libxposed)
compileOnly(libs.libxposed.api)
```

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<libxposed-version>` 修改为 [这里](https://github.com/libxposed/api) 的最新版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-runtime-libxposed:<version>")
compileOnly("io.github.libxposed:api:<libxposed-version>")
```

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<libxposed-version>` 修改为 [这里](https://github.com/libxposed/api) 的最新版本。

::: danger

请使用 `compileOnly` 引入 libxposed API，它不能被打包到模块中。

一个构建变体中只能存在一个底座模块，如果你需要同时兼容多个底座，请参考 [多入口变体](./yukihook-core/hook-entry/multi-entry-variants.md)。

:::

## 功能介绍

你可以 [点击这里](kdoc://yukihook-runtime-libxposed) 查看 KDoc。

底座提供了在 libxposed API 上使用 YukiHook 的核心功能支持，请参考 [yukihook-core](./yukihook-core.md) 来使用这些功能。

### 入口注解

这里介绍了入口注解上各种参数的含义，使用方法请参考 [模块入口 → 创建入口类](./yukihook-core/hook-entry.md#创建入口类)。

> 示例如下

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

| 参数               | 说明                                                                                  |
| ------------------ | ------------------------------------------------------------------------------------- |
| `entryClassName`   | 自动生成的入口类名，详情请参考 [入口类名](./yukihook-compiler.md#入口类名)            |
| `minApiVersion`    | 模块支持的最低 Xposed API 版本，默认为 `101`，不能低于 `101`                          |
| `targetApiVersion` | (必填) 模块的目标 Xposed API 版本，不能低于 `minApiVersion`                           |
| `scope`            | 推荐的作用域包名，系统框架请使用 `system`，默认为空                                   |
| `staticScope`      | 是否限制作用域只能从 `scope` 中选择，默认为 `false`                                   |
| `hotReload`        | 模块热重载策略，默认为 `NONE`，详情请参考 [模块热重载](./yukihook-core/hot-reload.md) |
| `javaEntries`      | 附加的 libxposed API 原生模块入口类，必须继承于 `XposedModule`                        |
| `nativeEntries`    | Native 入口的库文件名，例如 `libexample.so`                                           |

编译器会根据这些参数在 `META-INF/xposed` 中生成 `java_init.list`、`module.prop`、`scope.list` 以及 `native_init.list` (仅在 `nativeEntries` 不为空时生成)。

::: warning

在作用域中，系统框架为 API 定义的虚拟包名 `system`，`android` 是真实的系统框架包名，其进程名为 `system`，YukiHook 仅会将其作为系统服务进行装载。

:::

### 模块信息

在 `AndroidManifest.xml` 中定义你的模块信息，`label` 作为模块名称，`description` 作为模块描述。

> 示例如下

```xml
<application
    android:name=".MyApplication"
    android:description="@string/module_description"
    android:label="@string/app_name">

    <!-- ... -->
</application>
```

### API 版本

`minApiVersion` 决定了模块最低能运行在哪个版本的 API 上，`targetApiVersion` 决定了 YukiHook 支持的功能行为。

YukiHook 目前使用的部分功能需要 API 102 及以上版本，例如 [模块热重载](./yukihook-core/hot-reload.md)。

当你使用的功能版本高于 `minApiVersion` 时，你需要在调用前判断 `YukiHook.Status.Executor.apiVersion`。

> 示例如下

```kotlin
if (YukiHook.Status.Executor.apiVersion >= 102) {
    // Your code here.
}
```

### 原生模块入口

如果你需要同时使用框架原生的 Java 入口，你可以在 `javaEntries` 中声明它们，它们将被添加在 YukiHook 生成的入口之后。

> 示例如下

```kotlin
@YukiHookLibXposedEntry(
    targetApiVersion = 101,
    javaEntries = [MyXposedEntry::class],
    nativeEntries = ["libexample.so"]
)
object HookEntry : YukiHookXposedModule
```

::: warning

`nativeEntries` 中的库文件需要打包在模块 APK 中，Hook 框架会将它们作为 Native 入口进行装载。

:::

### 框架事件

YukiHook 保留了调用框架原生 API 的能力，你可以在 `onInit` 中使用 `registerFrameworkEvents` 注册框架事件监听。

> 示例如下

```kotlin
registerFrameworkEvents {
    onModuleLoaded { param ->
        // `this` 对象即 `XposedInterface`
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
    // 以下为 API 102 新增事件
    onHotReloading { param ->
        // 返回是否允许此次热重载
        true
    }
    onHotReloaded { param ->
        // Your code here.
    }
}
```

框架事件回调与 YukiHook 的 `onHook` 完全独立，你可以继续在 `onHook` 中使用 YukiHook。

::: danger

`registerFrameworkEvents` 只能在 `onInit` 中注册，且只能注册一次，它在每个模块实例中也只能调用一次。

:::