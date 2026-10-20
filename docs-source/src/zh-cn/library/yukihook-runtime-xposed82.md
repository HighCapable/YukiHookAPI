# yukihook-runtime-xposed82

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-runtime-xposed82?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-runtime-xposed82%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

这是 YukiHook 在 [Xposed Framework API](https://api.xposed.info) 上的核心底座模块。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [yukihook-bom](./yukihook-bom.md) 使用 BOM 统一管理版本。

::: warning

Xposed Framework API 没有发布在 Maven Central 中，如需使用，你需要参考 [快速开始 → 配置存储库](../guide/quick-start.md#configure-repositories)。

:::

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
yukihook-runtime-xposed82 = "<version>"

[libraries]
yukihook-runtime-xposed82 = { module = "com.highcapable.yukihookapi:yukihook-runtime-xposed82", version.ref = "yukihook-runtime-xposed82" }
xposed-framework-api = { module = "de.robv.android.xposed:api", version = "82" }
```

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.yukihook.runtime.xposed82)
compileOnly(libs.xposed.framework.api)
```

请将 `<version>` 修改为此文档顶部显示的版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-runtime-xposed82:<version>")
compileOnly("de.robv.android.xposed:api:82")
```

请将 `<version>` 修改为此文档顶部显示的版本。

::: danger

请使用 `compileOnly` 引入 Xposed Framework API，它不能被打包到模块中。

一个构建变体中只能存在一个底座模块，如果你需要同时兼容多个底座，请参考 [多入口变体](./yukihook-core/hook-entry/multi-entry-variants.md)。

:::

## 功能介绍

你可以 [点击这里](kdoc://yukihook-runtime-xposed82) 查看 KDoc。

底座提供了在 Xposed Framework API 上使用 YukiHook 的核心功能支持，请参考 [yukihook-core](./yukihook-core.md) 来使用这些功能。

### 入口注解

这里介绍了入口注解上各种参数的含义，使用方法请参考 [模块入口 → 创建入口类](./yukihook-core/hook-entry.md#创建入口类)。

> 示例如下

```kotlin
@YukiHookXposed82Entry
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        // Your code here.
    }
}
```

| 参数                   | 说明                                                                                                    |
| ---------------------- | ------------------------------------------------------------------------------------------------------- |
| `modulePackageName`    | 模块的包名，留空时将自动读取，详情请参考下方的说明                                                      |
| `entryClassName`       | 自动生成的入口类名，详情请参考 [入口类名](./yukihook-compiler.md#入口类名)                              |
| `generateModuleStatus` | 是否生成模块状态相关代码，默认为 `true`，关闭后会影响 [模块状态](./yukihook-core/module-status.md) 功能 |

编译器会根据这些参数生成 `assets/xposed_init`。

#### 模块包名

在不设置 `modulePackageName` 参数时，编译器将从当前编译中唯一可见的 `BuildConfig.java` 读取 `BuildConfig.APPLICATION_ID` 作为模块包名，无法读取或存在多个时将导致编译失败。

在 Android Gradle Plugin 8+ 中，你需要手动在项目的 `build.gradle.kts` 中启用 `buildConfig`。

> 示例如下

```kotlin
android {
    buildFeatures {
        buildConfig = true
    }
}
```

若你认为有必要手动定义模块包名，那么你可以直接设置 `modulePackageName` 参数，编译时不会检查模块包名是否正确，你会在编译时始终收到一条警告以提醒你确认其是否正确。

::: danger

请勿直接在 `modulePackageName` 中填写 `BuildConfig.APPLICATION_ID`，这会在编译过程中置空，它取决于 Android Gradle Plugin 的行为。

:::

### 模块信息

在 `AndroidManifest.xml` 中定义你的模块信息。

底座已经声明了 `xposedmodule` 与 `xposedminversion` (默认为 `82`)，请按需添加其他元数据。

> 示例如下

```xml
<application
    android:name=".MyApplication"
    android:label="@string/app_name">

    <!-- 设置你的模块描述 -->
    <meta-data
        android:name="xposeddescription"
        android:value="@string/module_description" />

    <!-- 仅限 LSPosed：设置你的模块作用域 -->
    <meta-data
        android:name="xposedscope"
        android:resource="@array/module_scope" />

    <!-- (可选) 覆盖最低 Xposed 版本 -->
    <meta-data
        android:name="xposedminversion"
        android:value="93"
        tools:replace="android:value" />
</application>
```

### 框架事件

YukiHook 保留了调用框架原生 API 的能力，你可以在 `onInit` 中使用 `registerFrameworkEvents` 注册框架事件监听。

> 示例如下

```kotlin
registerFrameworkEvents {
    onInitZygote {
        // `it` 对象即 `StartupParam`
    }
    onHandleLoadPackage {
        // `it` 对象即 `LoadPackageParam`
    }
}
```

框架事件回调与 YukiHook 的 `onHook` 完全独立，你可以继续在 `onHook` 中使用 YukiHook。

::: danger

`registerFrameworkEvents` 只能在 `onInit` 中注册，且只能注册一次，它在每个模块实例中也只能调用一次。

YukiHook 不再支持资源钩子事件，即使框架本身可能支持，所以不再提供 `handleInitPackageResources` 事件。

:::