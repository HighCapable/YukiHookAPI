# 快速开始

> 集成 `YukiHook` 到你的项目中。

## 项目要求

项目需要使用 `Android Studio` 或 `IntelliJ IDEA` 创建且类型为 Android 项目并已集成 Kotlin 环境依赖。

- Android Studio (建议从 [这里](https://developer.android.com/studio) 获取最新版本)

- IntelliJ IDEA (建议从 [这里](https://www.jetbrains.com/idea) 获取最新版本)

- Kotlin 1.9.0+、Gradle 8+、Java 17+、Android Gradle Plugin 8+

### 配置存储库

YukiHook 的依赖发布在 **Maven Central** 和我们的公共存储库中，你可以使用如下方式配置存储库。

我们推荐使用 Kotlin DSL 作为 Gradle 构建脚本语言。

在你的项目 `build.gradle.kts` 中配置存储库。

```kotlin
repositories {
    google()
    mavenCentral()
    // (可选) 你可以添加此 URL 以使用我们的公共存储库
    // 当 Sonatype-OSS 发生故障无法发布依赖时，此存储库作为备选进行添加
    // 详情请前往：https://github.com/HighCapable/maven-repository
    // 中国大陆用户请将下方的 "raw.githubusercontent.com" 修改为 "raw.gitmirror.com"
    maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
    // (可选) 如果你使用 yukihook-runtime-xposed82，你需要添加此 URL 以获取 Xposed Framework API
    maven("https://api.xposed.info/") {
        content {
            includeGroup("de.robv.android.xposed")
        }
    }
}
```

### 配置 Java 版本

在你的项目 `build.gradle.kts` 中修改 Kotlin 的 Java 版本为 17 及以上。

```kotlin
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinJvmCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}
```

## 功能一览

整个项目分为多个模块，你可以选择你希望引入的模块作为依赖应用到你的项目中，但一定要包含 **yukihook-core**、**yukihook-compiler** 和一个底座模块。

你可以点击下方对应的模块前往查看详细的功能介绍。

::: tip 版本说明

从 `1.5.0` 起，YukiHook 开始拆分为多个模块并采用统一版本进行发布，通常情况下你只需要关注同一个主版本即可，你也可以直接参考下方的 [yukihook-bom](../library/yukihook-bom.md) 使用 BOM 统一管理依赖版本。

详情请见 [更新日志](../about/changelog.md)。

:::

- [yukihook-bom](../library/yukihook-bom.md)
- [yukihook-core](../library/yukihook-core.md)
- [yukihook-compiler](../library/yukihook-compiler.md)
- [yukihook-extension-kavaref](../library/yukihook-extension-kavaref.md)
- [yukihook-runtime-libxposed](../library/yukihook-runtime-libxposed.md)
- [yukihook-runtime-xposed82](../library/yukihook-runtime-xposed82.md)

### 选择底座模块

底座模块决定了你的 Xposed 模块使用的 Xposed API，一个构建变体中**只能存在一个**底座模块。

<div class="base-modules-table">

| 底座模块                                                               | Xposed API                                      | 版本  | Min SDK | 适用场景                                   |
| ---------------------------------------------------------------------- | ----------------------------------------------- | :---: | :-----: | ------------------------------------------ |
| [yukihook-runtime-libxposed](../library/yukihook-runtime-libxposed.md) | [libxposed API](https://github.com/libxposed)   | 101+  |   26    | 支持 libxposed API 的新版 Xposed 框架      |
| [yukihook-runtime-xposed82](../library/yukihook-runtime-xposed82.md)   | [Xposed Framework API](https://api.xposed.info) |  82   |   21    | 需要兼容仅支持 Xposed Framework API 的框架 |

</div>

## Demo

你可以在 [这里](repo://tree/1.x/samples) 找到一些示例，查看对应的演示项目来更好地了解这些功能的运作方式，快速地挑选出你需要的功能。

- `demo-app`：宿主 Demo
- `demo-module-modern`：使用 `yukihook-runtime-libxposed` 的模块 Demo
- `demo-module-legacy`：使用 `yukihook-runtime-xposed82` 的模块 Demo

同时安装宿主与模块 Demo，通过激活模块来测试宿主中被 Hook 的功能。