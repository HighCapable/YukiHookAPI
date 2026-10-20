# yukihook-compiler

![Maven Central](https://img.shields.io/maven-central/v/com.highcapable.yukihookapi/yukihook-compiler?logo=apachemaven&logoColor=orange&style=flat-square)
<span style="margin-left: 5px"/>
![Maven metadata URL](https://img.shields.io/maven-metadata/v?metadataUrl=https%3A%2F%2Fraw.githubusercontent.com%2FHighCapable%2Fmaven-repository%2Frefs%2Fheads%2Fmain%2Frepository%2Freleases%2Fcom%2Fhighcapable%2Fyukihookapi%2Fyukihook-compiler%2Fmaven-metadata.xml&logo=apachemaven&logoColor=orange&label=highcapable-maven-releases&style=flat-square)

这是 YukiHook 的 Xposed 模块自动化编译模块。

## 配置依赖

你可以使用如下方式将此模块添加到你的项目中。

我们推荐你优先参考 [yukihook-bom](./yukihook-bom.md) 使用 BOM 统一管理版本。

::: warning

你需要在你的项目中集成适合于你项目当前 Kotlin 版本的 [Google KSP](https://github.com/google/ksp/releases) 插件。

:::

### Version Catalog (推荐)

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

```toml
[versions]
ksp = "<ksp-version>"
yukihook-compiler = "<version>"

[plugins]
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

[libraries]
yukihook-compiler = { module = "com.highcapable.yukihookapi:yukihook-compiler", version.ref = "yukihook-compiler" }
```

在你的根项目 `build.gradle.kts` 中配置依赖。

```kotlin
plugins {
    // ...
    alias(libs.plugins.ksp) apply false
}
```

在你的项目 `build.gradle.kts` 中配置依赖。

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

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<ksp-version>` 修改为你项目当前使用的 Kotlin 版本对应的 KSP 版本。

### 传统方式

在你的根项目 `build.gradle.kts` 中配置依赖。

```kotlin
plugins {
    // ...
    id("com.google.devtools.ksp") version "<ksp-version>" apply false
}
```

在你的项目 `build.gradle.kts` 中配置依赖。

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

请将 `<version>` 修改为此文档顶部显示的版本，并将 `<ksp-version>` 修改为你项目当前使用的 Kotlin 版本对应的 KSP 版本。

::: danger

此模块与 [yukihook-core](./yukihook-core.md) 的版本必须保持一致，我们推荐你使用 BOM 统一管理版本。

:::

## 功能介绍

此模块会查找项目中标记了入口注解的类，并根据当前使用的底座模块生成对应 Hook 框架所需的原生入口类与配置文件。

### 入口注解

一个构建变体中**只能存在一个**入口注解，入口注解需要与你引入的底座模块相对应，如果你需要同时兼容多个底座，请参考 [多入口变体](./yukihook-core/hook-entry/multi-entry-variants.md)。

| 入口注解                  | 底座模块                                                      |
| ------------------------- | ------------------------------------------------------------- |
| `@YukiHookLibXposedEntry` | [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) |
| `@YukiHookXposed82Entry`  | [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)   |

每个入口注解的参数说明请参考对应的底座模块文档。

::: danger

以下情况将导致编译失败。

- 一个构建变体中存在多个入口注解
- 一个构建变体中没有底座模块或同时存在多个底座模块
- 入口注解与引入的底座模块不匹配
- 入口注解的类没有实现 `YukiHookXposedModule` 接口，或其构造方法存在参数
- 在源码目录中手动声明了 Hook 框架所需的配置文件 (例如 `resources/META-INF/xposed`、`assets/xposed_init`)

:::

### 入口类名

Hook 框架入口类名默认会使用你的入口类完整类名插入 `_YukiHookXposedInit` 后缀进行生成。

假设这是你的入口类。

> 示例如下

```kotlin
@YukiHookLibXposedEntry(targetApiVersion = 101)
object HookEntry : YukiHookXposedModule
```

编译后的类名结构如下。

> 示例如下

```text:no-line-numbers
HookEntry ← 你的入口类
HookEntry_YukiHookXposedInit ← 自动生成的框架入口类
```

你可以使用入口注解的 `entryClassName` 参数自定义生成的入口类名。

> 示例如下

```kotlin
@YukiHookLibXposedEntry(
    entryClassName = "MyHookEntry",
    targetApiVersion = 102
)
object HookEntry : YukiHookXposedModule
```

编译后的类名结构如下。

> 示例如下

```text:no-line-numbers
HookEntry ← 你的入口类
MyHookEntry ← 自动生成的框架入口类
```

::: tip

入口类可以使用 `class` 或 `object` 定义，但是建议使用 `object` 定义来确保每一个注入的进程都是单例运行。

:::

::: danger

自定义的入口类名只能包含字母、数字与下划线，不能以数字开头，也不能与你的入口类同名。

:::

### 生成的内容

入口文件与配置文件会按照每个构建变体分别生成在构建目录中，你不需要也不应该在源码目录中手动创建它们。

| 底座模块                                                      | 生成的文件                                                                        |
| ------------------------------------------------------------- | --------------------------------------------------------------------------------- |
| [yukihook-runtime-libxposed](./yukihook-runtime-libxposed.md) | `META-INF/xposed/java_init.list`、`module.prop`、`scope.list`、`native_init.list` |
| [yukihook-runtime-xposed82](./yukihook-runtime-xposed82.md)   | `assets/xposed_init`                                                              |

::: warning

编译器不会生成或修改你的 `AndroidManifest.xml`，模块的名称、描述等信息需要按照对应底座模块的具体要求进行配置。

:::