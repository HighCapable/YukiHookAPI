# yukihook-extension-kavaref

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-extension-kavaref?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-extension-kavaref%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)
<span style="margin-left: 5px"/>
![Android Min SDK](https://img.shields.io/badge/Min%20SDK-21-orange?logo=android&style=flat-square)

这是 YukiHook 对 KavaRef 相关功能的扩展依赖。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [yukihook-bom](./yukihook-bom.md) 使用 BOM 统一管理版本。

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

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

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation(libs.yukihook.extension.kavaref)

implementation(platform(libs.kavaref.bom))
implementation(libs.kavaref.core)
implementation(libs.kavaref.android)
implementation(libs.kavaref.extension)
```

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<kavaref-version>` 修改为 [这里](https://highcapable.github.io/KavaRef/zh-cn/about/changelog) 的最新版本。

### 传统方式

在你的项目 `build.gradle.kts` 中配置依赖。

```kotlin
implementation("com.highcapable.yukihookapi:yukihook-extension-kavaref:<version>")

implementation(platform("com.highcapable.kavaref:kavaref-bom:<kavaref-version>"))
implementation("com.highcapable.kavaref:kavaref-core")
implementation("com.highcapable.kavaref:kavaref-android")
implementation("com.highcapable.kavaref:kavaref-extension")
```

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<kavaref-version>` 修改为 [这里](https://highcapable.github.io/KavaRef/zh-cn/about/changelog) 的最新版本。

::: danger

此模块不会自动装配 [KavaRef](https://github.com/HighCapable/KavaRef) 的依赖，请按照上方的方式自行引入 KavaRef。

:::

## 功能介绍

你可以 [点击这里](kdoc://yukihook-extension-kavaref) 查看 KDoc。

此模块为 KavaRef 提供了在 `PackageParam` 中使用的扩展方法，详细的用法请参考 [这里](https://highcapable.github.io/KavaRef/zh-cn/library/kavaref-core)，这将跳转到 KavaRef 的文档。

::: danger

这些扩展方法只能在 `PackageParam` 中使用，由于存在与 KavaRef 同名的扩展，请一定确保导入的是 `com.highcapable.yukihookapi.extension.kavaref` 下对应的方法。

:::

### Hook 用法

你可以直接对 KavaRef 的 `MemberResolver` 使用 `hook`、`hookAll`、`intercept` 以及 `interceptAll`。

> 示例如下

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

它们的用法与对 `Member` 使用时完全一致，详情请参考 [Hook 用法](./yukihook-core/hook-usage.md)。

你还可以使用 `deoptimize` 与 `deoptimizeAll`，详情请参考 [解除内联化](./yukihook-core/hook-usage.md#解除内联化)。

::: tip

在 [模块热重载 → 保留数据](./yukihook-core/hot-reload.md#保留数据) 中，由于 `MemberResolver` 对象强引用模块的 `ClassLoader`，你不能直接对它进行缓存。

此时你可以取出其中的 `self`，像下方这样使用。

> 示例如下

```kotlin
// 单个方法
val foo = remember("myapp-foo-v1") {
    "$packageName.Demo".toClass()
        .resolve()
        .optional()
        .firstMethodOrNull {
            name = "foo"
            emptyParameters()
        }?.self
}
// 一组方法
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

### 原始与特殊调用

你可以使用 KavaRef 的 `MethodResolver` 与 `ConstructorResolver` 调用未经 Hook 的原始方法与构造方法。

> 示例如下

```kotlin
loadApp("io.github.myapp") {
    // 假设这就是 Demo 类的实例
    val instance: Any
    "$packageName.Demo".toClass()
        .resolve()
        .firstMethod {
            name = "foo"
            emptyParameters()
        }.of(instance).invokeOriginal()
}
```

| 方法                | 说明                                                   |
| ------------------- | ------------------------------------------------------ |
| `invokeOriginal`    | 调用未经 Hook 的原始方法                               |
| *`invokeSpecial`    | 非虚调用方法，需要先使用 `of` 设置实例                 |
| `initializeSpecial` | 使用构造方法非虚初始化一个已经存在的实例               |
| *`createOriginal`   | 使用未经 Hook 的原始构造方法创建实例                   |
| *`createSpecial`    | 使用父类构造方法创建子类实例，子类的构造方法不会被调用 |

以上方法均提供了带有 `Quietly` 后缀的版本，它们将忽略调用过程中的异常并返回 `null`。

::: danger

上述标有 `*` 的方法需要 [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) 底座支持。

:::

### 装载宿主中的类

在 `PackageParam` 中，下方的同名扩展会默认使用 `hostClassLoader` 装载 `Class`。

- `String.toClass()`
- `String.toClassOrNull()`
- `VariousClass.toClass()`
- `VariousClass.toClassOrNull()`
- `lazyClass(...)`
- `lazyClassOrNull(...)`

> 示例如下

```kotlin
val DemoClass by lazyClass("io.github.myapp.Demo")

loadApp("io.github.myapp") {
    "$packageName.Demo".toClass()
    VariousClass("$packageName.Demo", "$packageName.NewDemo").toClass()
}
```

### 隐藏 API 绕过解析器

YukiHook 提供了 `AndroidHiddenApiBypassResolver`，你可以在需要反射系统隐藏 API 的地方使用它。

它复刻自 KavaRef 文档的 [AndroidHiddenApiBypassResolver](https://highcapable.github.io/KavaRef/zh-cn/config/processor-resolvers.html#androidhiddenapibypass)。

> 示例如下

```kotlin
val resolver = AndroidHiddenApiBypassResolver()

"android.app.ActivityThread".toClass()
    .resolve()
    // 设置自定义 Member 解析器
    .processor(resolver)
    .firstMethod {
        name = "currentActivityThread"
        emptyParameters()
    }.invoke()
```