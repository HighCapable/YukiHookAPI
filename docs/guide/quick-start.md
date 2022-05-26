# 快速开始

> 集成 `YukiHookAPI` 到你的项目中。

## 环境要求

- Windows 7 及以上/macOS 10.14 及以上/Linux 发行版(Arch/Debian)

- Android Studio 4.1 及以上

- IntelliJ IDEA 2021.01 及以上

- Kotlin 1.6.0 及以上

- Android Gradle Plugin 7.0 及以上

- Gradle 7.0 及以上

- Jvm 11 及以上 (Since API `1.0.80`)

## 集成依赖

在你的项目 `build.gradle` 中添加依赖。

> 示例如下

```gradle
repositories {
    google()
    mavenCentral()
    // ❗若你的 Plugin 版本过低，作为 Xposed 模块使用务必添加，其它情况可选
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    maven { url "https://api.xposed.info/" }
    // MavenCentral 有 2 小时缓存，若无法集成最新版本请添加此地址
    maven { url "https://s01.oss.sonatype.org/content/repositories/releases" }
}
```

在你的 app `build.gradle` 中添加 `plugin`。

> 示例如下

```gradle
plugins {
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    id 'com.google.devtools.ksp' version '<version>'
}
```

在你的 app `build.gradle` 中添加依赖。

> 示例如下

```gradle
dependencies {
    // 基础依赖
    implementation 'com.highcapable.yukihookapi:api:<version>'
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    compileOnly 'de.robv.android.xposed:api:82'
    // ❗作为 Xposed 模块使用务必添加，其它情况可选
    ksp 'com.highcapable.yukihookapi:ksp-xposed:<version>'
}
```

请将 **&lt;version&gt;** 修改为 [这里](about/changelog) 的最新版本。 **← 请打开后再次刷新页面确保获取最新数据**

!> `YukiHookAPI` 的 `api` 与 `ksp-xposed` 依赖的版本必须一一对应，否则将会造成版本不匹配错误。

在你的 app `build.gradle` 中修改 `Kotlin` 的 Jvm 版本为 11 及以上。

> 示例如下

```gradle
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_11
        targetCompatibility JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = '11'
    }
}
```

!> 自 API `1.0.80` 版本后 Jvm 版本默认为 11，不再支持 1.8 及以下版本。

## 作为 Xposed 模块使用

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

<!-- 可选：配置支持 New XSharePrefs 可无需调整 xposedminversion 为 93 -->
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

在你的项目中创建一个 Hook 入口类，继承于 `IYukiHookXposedInit` 并加入注解 `@InjectYukiHookWithXposed`。

!> 在默认配置情况下，你的入口类需要建立在你的包名的 hook 子包名下，假设你的包名为 `com.example.demo`，入口类应为 `com.example.demo.hook.你的入口类名称`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onHook() = YukiHookAPI.encase {
        // Your code here.
    }
}
```

你还可以将你的模块 APP 的 `Application` 继承于 `ModuleApplication` 以实现更多功能。

详情请参考 [ModuleApplication](api/document?id=moduleapplication-class)。

然后，你就可以开始编写 Hook 代码了。

有关作为 Xposed 模块使用的相关配置详细内容，你可以 [点击这里](config/xposed-using) 继续阅读。

若你目前正在使用 Xposed API，你可以参考 [从 Xposed API 迁移](guide/move-to-new-api)。

## 作为 Hook API 使用

### 集成方式

创建你的自定义 `Application`。

!> 无论使用任何 `Hook Framework`，你都需要加入其对接的 Xposed 依赖支持。

!> 若目标 `Hook Framework` 没有集成 Xposed API 你需要自行实现并对接 `XposedBridge`。

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

有关作为 Hook API 使用的相关配置详细内容，你可以 [点击这里](config/api-using) 继续阅读。

### 特别说明

!> 由于你使用了自定义的 Hook 框架而并非模块，~~`YukiHookModuleStatus`~~ ~~`YukiHookModulePrefs`~~ ~~`YukiHookDataChannel`~~ 以及 Resources Hook 功能将失效。