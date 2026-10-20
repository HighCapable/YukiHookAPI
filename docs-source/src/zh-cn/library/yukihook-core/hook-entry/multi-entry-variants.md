# 多入口变体

> 这里介绍了如何使用构建变体在同一个项目中同时兼容多个底座。

一个构建变体中只能存在一个入口注解与一个底座模块，但是你可以借助 Android Gradle Plugin 的 Product Flavors 为每个底座分别创建一个构建变体，让它们共享同一套 Hook 代码，并分别打包出对应底座的模块。

YukiHook 会按照每个构建变体分别生成入口与配置文件，你不需要额外对其进行配置。

::: warning

本页面提供的方案是同一套 Hook 代码分别打包出对应底座的模块，而不是合并为一个模块。

:::

## 配置构建变体

在你的项目 `build.gradle.kts` 中为每个底座创建一个构建变体，并分别引入对应的底座模块与 Xposed API。

> 示例如下

```kotlin
android {
    namespace = "io.github.mymodule"

    defaultConfig {
        applicationId = "io.github.mymodule"
    }
    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "runtime"
    productFlavors {
        create("modern") {
            dimension = "runtime"
        }
        create("legacy") {
            dimension = "runtime"
        }
    }
}
```

然后使用创建的构建变体部署依赖。

> Version Catalog

```kotlin
dependencies {
    implementation(libs.yukihook.core)
    ksp(libs.yukihook.compiler)

    "modernImplementation"(libs.yukihook.runtime.libxposed)
    "modernCompileOnly"(libs.libxposed.api)

    "legacyImplementation"(libs.yukihook.runtime.xposed82)
    "legacyCompileOnly"(libs.xposed.framework.api)
}
```

> 传统方案

```kotlin
dependencies {
    implementation("com.highcapable.yukihookapi:yukihook-core:<version>")
    ksp("com.highcapable.yukihookapi:yukihook-compiler:<version>")

    "modernImplementation"("com.highcapable.yukihookapi:yukihook-runtime-libxposed:<version>")
    "modernCompileOnly"("io.github.libxposed:api:<libxposed-version>")

    "legacyImplementation"("com.highcapable.yukihookapi:yukihook-runtime-xposed82:<version>")
    "legacyCompileOnly"("de.robv.android.xposed:api:82")
}
```

请将 `<version>` 修改为 [此文档](../../yukihook-core.md) 顶部显示的版本，`<libxposed-version>` 请参考对应的底座文档。

上方的 `modern` 变体使用 [yukihook-runtime-libxposed](../../yukihook-runtime-libxposed.md) 底座，`legacy` 变体使用 [yukihook-runtime-xposed82](../../yukihook-runtime-xposed82.md) 底座。

::: warning

请保持 `buildConfig` 选项为开启状态，因为 [yukihook-runtime-xposed82](../../yukihook-runtime-xposed82.md) 底座依赖它，详情请参考 [模块包名](../../yukihook-runtime-xposed82.md#模块包名)。

:::

## 目录结构

共享的 Hook 代码放置于 `main` 源集中，每个变体的入口类与专属配置放置于对应变体的源集中。

> 示例如下

```text:no-line-numbers
src
├─ main
│  ├─ AndroidManifest.xml
│  └─ java/io/github/mymodule/hook/HookEntry.kt
├─ modern
│  └─ java/io/github/mymodule/hook/ModernHookEntry.kt
└─ legacy
   ├─ AndroidManifest.xml
   └─ java/io/github/mymodule/hook/LegacyHookEntry.kt
```

## 创建入口类

在 `main` 源集中创建一个实现 `YukiHookXposedModule` 接口的类，并在其中实现你的 Hook 代码。

> 示例如下

```kotlin
object HookEntry : YukiHookXposedModule {

    override fun onInit() = configure {
        // Your code here.
    }

    override fun onHook() = encase {
        loadApp("io.github.myapp") {
            // Your code here.
        }
    }
}
```

接下来，在每个变体的源集中分别创建入口类，加入与底座对应的入口注解，并使用 Kotlin 的接口委托将实现交给共享的类。

> 示例如下

::: code-group

```kotlin [modern]
@YukiHookLibXposedEntry(
    targetApiVersion = 101,
    scope = ["io.github.myapp"]
)
object ModernHookEntry : YukiHookXposedModule by HookEntry
```

```kotlin [legacy]
@YukiHookXposed82Entry
object LegacyHookEntry : YukiHookXposedModule by HookEntry
```

:::

入口注解的参数请参考对应的底座模块文档。

::: danger

共享的入口类不能添加入口注解，否则每个构建变体中都会存在两个入口注解，这会导致编译失败。

:::

## 模块信息

两个底座读取模块信息的方式不同，但是它们会合并为一份 `AndroidManifest.xml`，你可以分别进行配置。

详情请参考 [yukihook-runtime-libxposed → 模块信息](../../yukihook-runtime-libxposed.md#模块信息) 与 [yukihook-runtime-xposed82 → 模块信息](../../yukihook-runtime-xposed82.md#模块信息)。

::: warning

请确保将公共部分的 `AndroidManifest.xml` 配置保留在 `main` 源集中，将底座特殊配置放置在对应变体的 `AndroidManifest.xml` 中。

:::

## 底座专属功能

部分功能仅在特定的底座中可用，例如 [模块共享文件](../shared-files.md) 与 [模块作用域](../module-status.md#模块作用域)。

在共享的代码中使用这些功能时，你需要先判断当前的底座。

> 示例如下

```kotlin
if (YukiHook.Status.runtime == YukiHookRuntime.LIBXPOSED) {
    val names = sharedFiles.list()
}
```

::: warning

底座模块自身提供的功能，例如 `registerFrameworkEvents`，在两个底座中的用法不同，你只能在对应变体的源集中使用它们。

:::

## 构建与发布

每个构建变体都会生成一个独立的 APK，例如 `assembleModernRelease` 与 `assembleLegacyRelease`，你需要将它们分别发布，并在发布说明中告知用户根据所使用的 Hook 框架选择安装对应的 APK。

::: warning

打包后的 APK 包名相同，它们不能同时安装在同一台设备上，否则会互相覆盖。

:::