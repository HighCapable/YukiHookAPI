# Multi-Entry Variants

> Here describes how to use build variants to be compatible with multiple base modules in the same project.

Only one entry annotation and one base module can exist in a build variant, but you can use product flavors of the Android Gradle Plugin to create a build variant for each base module, so that they share the same hook code and are packaged into modules for the corresponding base modules separately.

YukiHook generates the entry and configuration files for each build variant separately, so you do not need any extra configuration for it.

::: warning

The solution on this page packages the same hook code into separate modules for the corresponding base modules, instead of merging them into one module.

:::

## Configure Build Variants

Create a build variant for each base module in the `build.gradle.kts` of your project, and add the corresponding base module and Xposed API to each of them.

> The following example

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

Then deploy the dependencies with the created build variants.

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

> Traditional Method

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

Please change `<version>` to the version displayed at the top of [this document](../../yukihook-core.md), and refer to the documentation of the corresponding base module for `<libxposed-version>`.

The `modern` variant above uses the [yukihook-runtime-libxposed](../../yukihook-runtime-libxposed.md) base module, and the `legacy` variant uses the [yukihook-runtime-xposed82](../../yukihook-runtime-xposed82.md) base module.

::: warning

Please keep the `buildConfig` option enabled, because the [yukihook-runtime-xposed82](../../yukihook-runtime-xposed82.md) base module depends on it, for details please refer to [Module Package Name](../../yukihook-runtime-xposed82.md#module-package-name).

:::

## Directory Structure

The shared hook code is placed in the `main` source set, and the entry class and dedicated configuration of each variant are placed in the source set of the corresponding variant.

> The following example

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

## Create Entry Classes

Create a class implementing the `YukiHookXposedModule` interface in the `main` source set, and write your hook code in it.

> The following example

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

Next, create an entry class in the source set of each variant, add the entry annotation for the corresponding base module, and hand the implementation over to the shared class with Kotlin interface delegation.

> The following example

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

For the parameters of the entry annotations, please refer to the documentation of the corresponding base module.

::: danger

The shared entry class must not have an entry annotation, otherwise each build variant contains two entry annotations, which causes the compilation to fail.

:::

## Module Information

The two base modules read module information in different ways, but they are merged into one `AndroidManifest.xml`, so you can configure them separately.

For details, please refer to [yukihook-runtime-libxposed → Module Information](../../yukihook-runtime-libxposed.md#module-information) and [yukihook-runtime-xposed82 → Module Information](../../yukihook-runtime-xposed82.md#module-information).

::: warning

Please make sure to keep the common `AndroidManifest.xml` configuration in the `main` source set, and put the base module specific configuration in the `AndroidManifest.xml` of the corresponding variant.

:::

## Base Module Specific Features

Some features are only available in specific base modules, such as [Module Shared Files](../shared-files.md) and [Module Scope](../module-status.md#module-scope).

When using these features in the shared code, you need to check the current base module first.

> The following example

```kotlin
if (YukiHook.Status.runtime == YukiHookRuntime.LIBXPOSED) {
    val names = sharedFiles.list()
}
```

::: warning

The features provided by the base modules themselves, such as `registerFrameworkEvents`, are used differently in the two base modules, you can only use them in the source set of the corresponding variant.

:::

## Build and Release

Each build variant generates a separate APK, such as `assembleModernRelease` and `assembleLegacyRelease`, you need to release them separately and tell users in the release notes to choose which APK to install based on the hook framework they use.

::: warning

The packaged APKs have the same package name, so they cannot be installed on the same device at the same time, otherwise they will overwrite each other.

:::