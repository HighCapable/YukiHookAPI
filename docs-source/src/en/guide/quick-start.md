# Quick Start

> Integrate `YukiHookAPI` into your project.

## Project Requirements

The project needs to be created using `Android Studio` or `IntelliJ IDEA` and be of type Android project and have integrated Kotlin environment dependencies.

- Android Studio (It is recommended to get the latest version [from here](https://developer.android.com/studio))

- IntelliJ IDEA (It is recommended to get the latest version [from here](https://www.jetbrains.com/idea))

- Kotlin 1.9.0+, Gradle 8+, Java 11, 17+, Android Gradle Plugin 8+

## Automatically Build Project

`YukiHookAPI` provides an automated build tool that can help you quickly build an Android standard project template with Xposed Module dependencies, and use the built template to start the next step directly.

You can [click here](../tools/yukihookapi-projectbuilder) to check it out.

## Manually Configure Project

If you don't want to use automated build tools, you can still manually configure project dependencies as follows.

### Create Project

Use `Android Studio` or `IntelliJ IDEA` to create a new Android project and select Kotlin in the `Language` column to automatically add basic dependencies.

### Integration Dependencies

We recommend using Kotlin DSL as the Gradle build script language and [SweetDependency](https://github.com/HighCapable/SweetDependency) to manage dependencies.

#### SweetDependency (Recommended)

Add the repositories and dependencies in your project's `SweetDependency` configuration file.

> The following example

```yaml
repositories:
  # Must be added when used as an Xposed Module, otherwise optional
  rovo89-xposed-api:
    url: https://api.xposed.info/

plugins:
  # Must be added when used as an Xposed Module, otherwise optional
  com.google.devtools.ksp:
    version: +
  ...

libraries:
  # Must be added when used as an Xposed Module, otherwise optional
  de.robv.android.xposed:
    api:
      version: 82
      repositories:
        rovo89-xposed-api
  com.highcapable.yukihookapi:
    api:
      version: +
    # Must be added when used as an Xposed Module, otherwise optional
    ksp-xposed:
      version-ref: <this>::api
  # YukiHookAPI version 1.3.0 uses KavaRef as core reflection API
  # YukiHookAPI no longer binds its own reflection API, you can start trying to use KavaRef
  com.highcapable.kavaref:
    kavaref-core:
      version: +
    kavaref-extension:
      version: +
  ...
```

After adding it, run Gradle Sync and all dependencies will be autowired.

Next, deploy plugins in your project `build.gradle.kts`.

> The following example

```kotlin
plugins {
    // Must be added when used as an Xposed Module, otherwise optional
    autowire(libs.plugins.com.google.devtools.ksp)
    // ...
}
```

Then, deploy dependencies in your project `build.gradle.kts`.

> The following example

```kotlin
dependencies {
    // Basic dependencies
    implementation(com.highcapable.yukihookapi.api)
    // It is recommended to use KavaRef as the core reflection API
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    // Must be added when used as an Xposed Module, otherwise optional
    compileOnly(de.robv.android.xposed.api)
    // Must be added when used as an Xposed Module, otherwise optional
    ksp(com.highcapable.yukihookapi.ksp.xposed)
}
```

#### Version Catalog

Add repositories in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
repositories {
    google()
    mavenCentral()
    // Must be added when used as an Xposed Module, otherwise optional
    maven { url("https://api.xposed.info/") }
}
```

Add dependency in your project's `gradle/libs.versions.toml`.

> The following example

```toml
[versions]
yukihookapi = "<yuki-version>"
ksp = "<ksp-version>"
kavaref-core = "<kavaref-version>"
kavaref-extension = "<kavaref-version>"

[plugins]
# Must be added when used as an Xposed Module, otherwise optional
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

[libraries]
yukihookapi-api = { module = "com.highcapable.yukihookapi:api", version.ref = "yukihookapi" }
# Must be added when used as an Xposed Module, otherwise optional
yukihookapi-ksp-xposed = { module = "com.highcapable.yukihookapi:ksp-xposed", version.ref = "yukihookapi" }
# YukiHookAPI version 1.3.0 uses KavaRef as core reflection API
# YukiHookAPI no longer binds its own reflection API, you can start trying to use KavaRef
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core", version.ref = "kavaref-core" }
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension", version.ref = "kavaref-extension" }
# Must be added when used as an Xposed Module, otherwise optional
xposed-api = { module = "de.robv.android.xposed:api", version = "82" }
```

Next, deploy plugins in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
plugins {
    // Must be added when used as an Xposed Module, otherwise optional
    alias(libs.plugins.ksp)
}
```

Then, deploy dependencies in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
dependencies {
    // Basic dependency
    implementation(libs.yukihookapi.api)
    // It is recommended to use KavaRef as the core reflection API
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    // Must be added when used as an Xposed Module, otherwise optional
    compileOnly(libs.xposed.api)
    // Must be added when used as an Xposed Module, otherwise optional
    ksp(libs.yukihookapi.ksp.xposed)
}
```

#### Traditional Method

Add repositories in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
repositories {
    google()
    mavenCentral()
    // Must be added when used as an Xposed Module, otherwise optional
    maven { url("https://api.xposed.info/") }
}
```

Add plugins in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
plugins {
    // Must be added when used as an Xposed Module, otherwise optional
    id("com.google.devtools.ksp") version "<ksp-version>"
}
```

Add dependencies in your project `build.gradle.kts`.

> Kotlin DSL

```kotlin
dependencies {
    // Basic dependency
    implementation("com.highcapable.yukihookapi:api:<yuki-version>")
    // It is recommended to use KavaRef as the core reflection API
    implementation("com.highcapable.kavaref:kavaref-core:<kavaref-version>")
    implementation("com.highcapable.kavaref:kavaref-extension:<kavaref-version>")
    // Must be added when used as an Xposed Module, otherwise optional
    compileOnly("de.robv.android.xposed:api:82")
    // Must be added when used as an Xposed Module, otherwise optional
    ksp("com.highcapable.yukihookapi:ksp-xposed:<yuki-version>")
}
```

Please modify **&lt;ksp-version&gt;** to the latest version found [here](https://github.com/google/ksp/releases) **(please note to select your current corresponding Kotlin version)**.

Please change **&lt;yuki-version&gt;** to the latest version [here](../about/changelog).

Please change **&lt;kavaref-version&gt;** to the latest version [here](https://highcapable.github.io/KavaRef/en/about/changelog).

:::danger

The **api** and **ksp-xposed** dependency versions of **YukiHookAPI** must correspond one-to-one, otherwise a version mismatch error will occur.

:::

#### Configure Java Version

Modify the Java version of Kotlin in your project `build.gradle.kts` or `build.gradle` to 17 or above.

> Kotlin DSL

```kt
android {
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}
```

> Groovy DSL

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = '17'
    }
}
```

::: warning

Since API **1.0.80**, the Java version used by Kotlin defaults to 11, and versions 1.8 and below are no longer supported.

Since API **1.2.0**, the Java version used by Kotlin defaults to 17, and versions 11 and below are no longer supported.

:::

### Use as Xposed Module

Add the base code to your `AndroidManifest.xml`.

> The following example

```xml
<!-- Set as Xposed Module -->
<meta-data
    android:name="xposedmodule"
    android:value="true" />

<!-- Set your Xposed Module description -->
<meta-data
    android:name="xposeddescription"
    android:value="Fill in your Xposed Module description" />

<!-- The minimum Xposed version number -->
<!-- If you are using EdXposed/LSPosed, the minimum recommended is 93 -->
<meta-data
    android:name="xposedminversion"
    android:value="93" />

<!-- Optional: Configure support for New XSharedPreferences without adjusting xposedminversion to 93 -->
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

Create a Hook entry class in your project, implements `IYukiHookXposedInit` and add the annotation `@InjectYukiHookWithXposed`.

> The following example

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = YukiHookAPI.encase {
        // Your code here.
    }
}
```

::: tip Suggestion

Please configure **YukiHookAPI** in the **onInit** method and set the **isDebug** mode to the following form.

> The following example

```kotlin
override fun onInit() = configs {
    isDebug = BuildConfig.DEBUG
}
```

You can also extends **Application** of your Module App from **ModuleApplication** to achieve a complete user experience.

For more functions, please refer to [ModuleApplication](../api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication).

:::

Then, you can start writing Hook code.

For configuration details related to use as an Xposed Module, you can [click here](../config/xposed-using) to continue reading.

If you are currently using Hook APIs such as Rovo89 Xposed API, you can refer to [Migrate from Other Hook APIs](../guide/move-to-new-api).

### Use as Hook API

#### Integration

Create your custom `Application`.

::: danger

Regardless of the **Hook Framework** you use, you need to add its docking Xposed dependency support.

If the target **Hook Framework** does not integrate Rovo89 Xposed API, you need to implement and connect **XposedBridge** by yourself.

:::

Add `YukiHookAPI.encase` method to `attachBaseContext`.

> The following example

```kotlin
override fun attachBaseContext(base: Context?) {
    // Load Hook Framework
    //
    // Your code here.
    //
    // Load YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

Then, you can start writing Hook code in much the same way you would use it as an Xposed Module.

For configuration details related to use as a Hook API, you can [click here](../config/api-using) to continue reading.

::: warning

**YukiHookPrefsBridge**, **YukiHookDataChannel** and Resources Hook functionality will not work when using a custom Hook Framework instead of the full Xposed Module.

:::