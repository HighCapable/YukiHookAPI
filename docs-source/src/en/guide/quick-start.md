# Quick Start

> Integrate `YukiHook` into your project.

## Project Requirements

The project needs to be created using `Android Studio` or `IntelliJ IDEA` and must be an Android
project with integrated Kotlin environment dependencies.

- Android Studio (It is recommended to get the latest version from [here](https://developer.android.com/studio))

- IntelliJ IDEA (It is recommended to get the latest version from [here](https://www.jetbrains.com/idea))

- Kotlin 1.9.0+, Gradle 8+, Java 17+, Android Gradle Plugin 8+

### Configure Repositories

The dependencies of YukiHook are published in **Maven Central** and our public repository,
you can use the following method to configure repositories.

We recommend using Kotlin DSL as the Gradle build script language.

Configure repositories in your project's `build.gradle.kts`.

```kotlin
repositories {
    google()
    mavenCentral()
    // (Optional) You can add this URL to use our public repository
    // When Sonatype-OSS fails and cannot publish dependencies, this repository is added as a backup
    // For details, please visit: https://github.com/HighCapable/maven-repository
    maven("https://raw.githubusercontent.com/HighCapable/maven-repository/main/repository/releases")
    // (Optional) If you use yukihook-runtime-xposed82,
    // you need to add this URL to get the Xposed Framework API
    maven("https://api.xposed.info/") {
        content {
            includeGroup("de.robv.android.xposed")
        }
    }
}
```

### Configure Java Version

Modify the Java version of Kotlin in your project's `build.gradle.kts` to 17 or above.

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

## Features Overview

The project is divided into multiple modules. You can choose the module you wish to include as a dependency in your project, but be sure to include **yukihook-core**, **yukihook-compiler** and a base module.

Click the corresponding module below to view detailed feature descriptions.

::: tip Version Notes

Starting from `1.5.0`, YukiHook started to be split into multiple modules and released with unified versioning.
In most cases, you only need to pay attention to the same major version.
You can also refer to the [yukihook-bom](../library/yukihook-bom.md) below to use BOM for unified dependency version management.

For details, please see the [changelog](../about/changelog.md).

:::

- [yukihook-bom](../library/yukihook-bom.md)
- [yukihook-core](../library/yukihook-core.md)
- [yukihook-compiler](../library/yukihook-compiler.md)
- [yukihook-extension-kavaref](../library/yukihook-extension-kavaref.md)
- [yukihook-runtime-libxposed](../library/yukihook-runtime-libxposed.md)
- [yukihook-runtime-xposed82](../library/yukihook-runtime-xposed82.md)

### Choose a Base Module

The base module decides which Xposed API your Xposed module uses. **Only one** base module can exist in a build variant.

<div class="base-modules-table">

| Base Module                                                            | Xposed API                                      | Version | Min SDK | Use Case                                                   |
| ---------------------------------------------------------------------- | ----------------------------------------------- | :-----: | :-----: | ---------------------------------------------------------- |
| [yukihook-runtime-libxposed](../library/yukihook-runtime-libxposed.md) | [libxposed API](https://github.com/libxposed)   |  101+   |   26    | For newer Xposed frameworks that support the libxposed API |
| [yukihook-runtime-xposed82](../library/yukihook-runtime-xposed82.md)   | [Xposed Framework API](https://api.xposed.info) |   82    |   21    | For frameworks that only support the Xposed Framework API  |

</div>

## Demo

You can find some examples [here](repo://tree/1.x/samples), view the corresponding demo projects to better understand how these features work and quickly pick the features you need.

- `demo-app`: the host demo
- `demo-module-modern`: the module demo using `yukihook-runtime-libxposed`
- `demo-module-legacy`: the module demo using `yukihook-runtime-xposed82`

Install both the host and the module demo, and activate the module to test the hooked features in the host.