# 快速开始

> 集成 `YukiHookAPI` 到你的项目中。

## 项目要求

项目需要使用 `Android Studio` 或 `IntelliJ IDEA` 创建且类型为 Android 项目并已集成 Kotlin 环境依赖。

- Android Studio (建议 [从这里](https://developer.android.com/studio) 获取最新版本)

- IntelliJ IDEA (建议 [从这里](https://www.jetbrains.com/idea) 获取最新版本)

- Kotlin 1.9.0+、Gradle 8+、Java 11、17+、Android Gradle Plugin 8+

## 自动构建项目

`YukiHookAPI` 提供了一个自动化构建工具，它可以帮助你快速构建一个拥有 Xposed 模块依赖的 Android 标准项目模板，使用构建好的模板即可直接开始下一步工作。

你可以 [点击这里](../tools/yukihookapi-projectbuilder) 进行查看。

## 手动配置项目

若你不想使用自动化构建工具，你依然可以按照以下方式手动配置项目依赖。

### 创建项目

使用 `Android Studio` 或 `IntelliJ IDEA` 创建新的 Android 项目，并在 `Language` 一栏选择 Kotlin 以自动添加基础依赖。

### 集成依赖

我们推荐使用 Kotlin DSL 作为 Gradle 构建脚本语言并推荐使用 [SweetDependency](https://github.com/HighCapable/SweetDependency) 来管理依赖。

#### SweetDependency (推荐)

在你的项目 `SweetDependency` 配置文件中添加存储库和依赖。

> 示例如下

```yaml
repositories:
  # 作为 Xposed 模块使用务必添加，其它情况可选
  rovo89-xposed-api:
    url: https://api.xposed.info/

plugins:
  # 作为 Xposed 模块使用务必添加，其它情况可选
  com.google.devtools.ksp:
    version: +
  ...

libraries:
  # 作为 Xposed 模块使用务必添加，其它情况可选
  de.robv.android.xposed:
    api:
      version: 82
      repositories:
        rovo89-xposed-api
  com.highcapable.yukihookapi:
    api:
      version: +
    # 作为 Xposed 模块使用务必添加，其它情况可选
    ksp-xposed:
      version-ref: <this>::api
  # YukiHookAPI 1.3.0 版本后使用 KavaRef 作为核心反射 API
  # YukiHookAPI 不再绑定自身的反射 API，你可以开始尝试使用 KavaRef
  com.highcapable.kavaref:
    kavaref-core:
      version: +
    kavaref-extension:
      version: +
  ...
```

添加完成后运行一次 Gradle Sync，所有依赖版本将自动装配。

接下来，在你的项目 `build.gradle.kts` 中部署插件。

> 示例如下

```kotlin
plugins {
    // 作为 Xposed 模块使用务必添加，其它情况可选
    autowire(libs.plugins.com.google.devtools.ksp)
    // ...
}
```

然后，在你的项目 `build.gradle.kts` 中部署依赖。

> 示例如下

```kotlin
dependencies {
    // 基础依赖
    implementation(com.highcapable.yukihookapi.api)
    // 推荐使用 KavaRef 作为核心反射 API
    implementation(com.highcapable.kavaref.kavaref.core)
    implementation(com.highcapable.kavaref.kavaref.extension)
    // 作为 Xposed 模块使用务必添加，其它情况可选
    compileOnly(de.robv.android.xposed.api)
    // 作为 Xposed 模块使用务必添加，其它情况可选
    ksp(com.highcapable.yukihookapi.ksp.xposed)
}
```

#### Version Catalog

在你的项目 `build.gradle.kts` 中添加存储库。

> Kotlin DSL

```kotlin
repositories {
    google()
    mavenCentral()
    // 作为 Xposed 模块使用务必添加，其它情况可选
    maven("https://api.xposed.info/")
}
```

在你的项目 `gradle/libs.versions.toml` 中添加依赖。

> 示例如下

```toml
[versions]
yukihookapi = "<yuki-version>"
ksp = "<ksp-version>"
kavaref-core = "<kavaref-version>"
kavaref-extension = "<kavaref-version>"

[plugins]
# 作为 Xposed 模块使用务必添加，其它情况可选
ksp = { id = "com.google.devtools.ksp", version.ref = "ksp" }

[libraries]
yukihookapi-api = { module = "com.highcapable.yukihookapi:api", version.ref = "yukihookapi" }
# 作为 Xposed 模块使用务必添加，其它情况可选
yukihookapi-ksp-xposed = { module = "com.highcapable.yukihookapi:ksp-xposed", version.ref = "yukihookapi" }
# YukiHookAPI 1.3.0 版本后使用 KavaRef 作为核心反射 API
# YukiHookAPI 不再绑定自身的反射 API，你可以开始尝试使用 KavaRef
kavaref-core = { module = "com.highcapable.kavaref:kavaref-core", version.ref = "kavaref-core" }
kavaref-extension = { module = "com.highcapable.kavaref:kavaref-extension", version.ref = "kavaref-extension" }
# 作为 Xposed 模块使用务必添加，其它情况可选
xposed-api = { module = "de.robv.android.xposed:api", version = "82" }
```

接下来，在你的项目 `build.gradle.kts` 中部署插件。

> Kotlin DSL

```kotlin
plugins {
    // 作为 Xposed 模块使用务必添加，其它情况可选
    alias(libs.plugins.ksp)
}
```

然后，在你的项目 `build.gradle.kts` 中部署依赖。

> Kotlin DSL

```kotlin
dependencies {
    // 基础依赖
    implementation(libs.yukihookapi.api)
    // 推荐使用 KavaRef 作为核心反射 API
    implementation(libs.kavaref.core)
    implementation(libs.kavaref.extension)
    // 作为 Xposed 模块使用务必添加，其它情况可选
    compileOnly(libs.xposed.api)
    // 作为 Xposed 模块使用务必添加，其它情况可选
    ksp(libs.yukihookapi.ksp.xposed)
}
```

#### 传统方式

在你的项目 `build.gradle.kts` 中添加存储库。

> Kotlin DSL

```kotlin
repositories {
    google()
    mavenCentral()
    // 作为 Xposed 模块使用务必添加，其它情况可选
    maven("https://api.xposed.info/")
}
```

在你的项目 `build.gradle.kts` 中添加插件。

> Kotlin DSL

```kotlin
plugins {
    // 作为 Xposed 模块使用务必添加，其它情况可选
    id("com.google.devtools.ksp") version "<ksp-version>"
}
```

在你的项目 `build.gradle.kts` 中添加依赖。

> Kotlin DSL

```kotlin
dependencies {
    // 基础依赖
    implementation("com.highcapable.yukihookapi:api:<yuki-version>")
    // 推荐使用 KavaRef 作为核心反射 API
    implementation("com.highcapable.kavaref:kavaref-core:<kavaref-version>")
    implementation("com.highcapable.kavaref:kavaref-extension:<kavaref-version>")
    // 作为 Xposed 模块使用务必添加，其它情况可选
    compileOnly("de.robv.android.xposed:api:82")
    // 作为 Xposed 模块使用务必添加，其它情况可选
    ksp("com.highcapable.yukihookapi:ksp-xposed:<yuki-version>")
}
```

请将 **&lt;ksp-version&gt;** 修改为 [这里](https://github.com/google/ksp/releases) 的最新版本 **(请注意选择你当前对应的 Kotlin 版本)**。

请将 **&lt;yuki-version&gt;** 修改为 [这里](../about/changelog) 的最新版本。

请将 **&lt;kavaref-version&gt;** 修改为 [这里](https://highcapable.github.io/KavaRef/zh-cn/about/changelog) 的最新版本。

::: danger

**YukiHookAPI** 的 **api** 与 **ksp-xposed** 依赖的版本必须一一对应，否则将会造成版本不匹配错误。

:::

#### 配置 Java 版本

在你的项目 `build.gradle.kts` 中修改 Kotlin 的 Java 版本为 17 及以上。

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

::: warning

自 API **1.0.80** 版本后 Kotlin 使用的 Java 版本默认为 11，不再支持 1.8 及以下版本。

自 API **1.2.0** 版本后 Kotlin 使用的 Java 版本默认为 17，不再支持 11 及以下版本。

:::

### 作为 Xposed 模块使用

在你的 `AndroidManifest.xml` 中添加基础代码。

> 示例如下

```xml
<!-- 设置为 Xposed 模块 -->
<meta-data
    android:name="xposedmodule"
    android:value="true" />

<!-- 设置你的模块描述 -->
<meta-data
    android:name="xposeddescription"
    android:value="填写你的 Xposed 模块描述" />

<!-- 最低 Xposed 版本号，若你正在使用 EdXposed/LSPosed，建议最低为 93 -->
<meta-data
    android:name="xposedminversion"
    android:value="93" />

<!-- 可选：配置支持 New XSharedPreferences 可无需调整 xposedminversion 为 93 -->
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

在你的项目中创建一个 Hook 入口类，继承于 `IYukiHookXposedInit` 并加入注解 `@InjectYukiHookWithXposed`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = YukiHookAPI.encase {
        // Your code here.
    }
}
```

::: tip 建议

请在 **onInit** 方法中配置 **YukiHookAPI** 并将 **isDebug** 模式设置为如下形式。

> 示例如下

```kotlin
override fun onInit() = configs {
    isDebug = BuildConfig.DEBUG
}
```

你还可以将你的模块 APP 的 **Application** 继承于 **ModuleApplication** 以实现完整使用体验。

更多功能请参考 [ModuleApplication](../api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication)。

:::

然后，你就可以开始编写 Hook 代码了。

有关作为 Xposed 模块使用的相关配置详细内容，你可以 [点击这里](../config/xposed-using) 继续阅读。

若你目前正在使用 Rovo89 Xposed API 等 Hook API，你可以参考 [从其它 Hook API 迁移](../guide/move-to-new-api)。

### 作为 Hook API 使用

#### 集成方式

创建你的自定义 `Application`。

::: danger

无论使用任何 **Hook Framework**，你都需要加入其对接的 Xposed 依赖支持。

若目标 **Hook Framework** 没有集成 Rovo89 Xposed API 你需要自行实现并对接 **XposedBridge**。

:::

在 `attachBaseContext` 中添加 `YukiHookAPI.encase` 方法。

> 示例如下

```kotlin
override fun attachBaseContext(base: Context?) {
    // 装载 Hook Framework
    //
    // Your code here.
    //
    // 装载 YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

然后，你就可以开始编写 Hook 代码了，方式与作为 Xposed 模块使用基本一致。

有关作为 Hook API 使用的相关配置详细内容，你可以 [点击这里](../config/api-using) 继续阅读。

::: warning

使用自定义的 Hook Framework 而并非完整的 Xposed 模块时，**YukiHookPrefsBridge**、**YukiHookDataChannel** 以及 Resources Hook 功能将失效。

:::