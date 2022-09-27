# Quick Start

> Integrate `YukiHookAPI` into your project.

## Environment Requirements

- Windows 7 and above / macOS 10.14 and above / Linux distributions (Arch/Debian)

- Android Studio 2021.1 and above

- IntelliJ IDEA 2021.1 and above

- Kotlin 1.7.0 and above

- Android Gradle Plugin 7.0 and above

- Gradle 7.0 and above

- JVM 11 and above (Since API `1.0.80`)

## Automatically Build Project

`YukiHookAPI` provides an automated build tool that can help you quickly build an Android standard project template with Xposed Module dependencies, and use the built template to start the next step directly.

You can [click here](../tools/yukihookapi-projectbuilder) to check it out.

## Manually Configure Project

If you don't want to use automated build tools, you can still manually configure project dependencies as follows.

### Create Project

Use `Android Studio` or `IntelliJ IDEA` to create a new Android project and select `Kotlin` in the `Language` column to automatically add basic dependencies.

### Integration Dependencies

Add dependencies to your project `build.gradle`.

> The following example

```groovy
repositories {
    google()
    mavenCentral()
    // ❗If your Plugin version is too low, be sure to add it as an Xposed Module, other cases are optional
    maven { url "https://dl.bintray.com/kotlin/kotlin-eap" }
    // ❗Be sure to add it as an Xposed Module, optional in other cases
    maven { url "https://api.xposed.info/" }
    // MavenCentral has a 2-hour cache, if you cannot integrate the latest version, please add this address
    maven { url "https://s01.oss.sonatype.org/content/repositories/releases" }
}
```

Add `plugin` to your app `build.gradle`.

> The following example

```groovy
plugins {
    // ❗Be sure to add it as an Xposed Module, optional in other cases
    id 'com.google.devtools.ksp' version '<ksp-version>'
}
```

Add dependencies to your app `build.gradle`.

> The following example

```groovy
dependencies {
    // base dependencies
    implementation 'com.highcapable.yukihookapi:api:<yuki-version>'
    // ❗Be sure to add it as an Xposed Module, optional in other cases
    compileOnly 'de.robv.android.xposed:api:82'
    // ❗Be sure to add it as an Xposed Module, optional in other cases
    ksp 'com.highcapable.yukihookapi:ksp-xposed:<yuki-version>'
}
```

Please modify **&lt;ksp-version&gt;** to the latest version from [here](https://github.com/google/ksp/releases) **(Please choose your current corresponding Kotlin version)**.

Please modify **&lt;yuki-version&gt;** to the latest version [here](../about/changelog).

::: danger

The **api** of **YukiHookAPI** and the versions that **ksp-xposed** depend on must correspond one by one, otherwise a version mismatch error will occur.

:::

Modify the JVM version of `Kotlin` to 11 and above in your app `build.gradle`.

> The following example

```groovy
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

::: warning

Since API **1.0.80** version, the default JVM version is 11, and 1.8 and below are no longer supported.

:::

### Use as Xposed Module

Add the base code to your `AndroidManifest.xml`.

> The following example

```xml
<!-- Set as Xposed Module -->
<meta-data
    android:name="xposedmodule"
    android:value="true" />

<!-- Set your module description -->
<meta-data
    android:name="xposeddescription"
    android:value="Fill in your Xposed Module description" />

<!-- The minimum Xposed version number -->
<!-- If you are using EdXposed/LSPosed, the minimum recommended is 93 -->
<meta-data
    android:name="xposedminversion"
    android:value="93" />

<!-- Optional: Configure support for New XSharePrefs without adjusting xposedminversion to 93 -->
<meta-data
    android:name="xposedsharedprefs"
    android:value="true"/>
```

Create a Hook entry class in your project, implements `IYukiHookXposedInit` and add the annotation `@InjectYukiHookWithXposed`.

> The following example

```kotlin
@InjectYukiHookWithXposed
class HookEntry : IYukiHookXposedInit {

    override fun onHook() = YukiHookAPI.encase {
        // Your code here.
    }
}
```

::: tip Suggestion

You can extends **Application** of your Module App from **ModuleApplication** to achieve a complete user experience.

For more functions, please refer to [ModuleApplication](../api/public/com/highcapable/yukihookapi/hook/xposed/application/ModuleApplication).

:::

Then, you can start writing Hook code.

For configuration details related to use as an Xposed Module, you can [click here](../config/xposed-using) to continue reading.

If you are currently using Xposed API, you can refer to [Migrate from Xposed API](../guide/move-to-new-api).

### Use as Hook API

#### Integration

Create your custom `Application`.

::: danger

Regardless of the **Hook Framework** you use, you need to add its docking Xposed dependency support.

If the target **Hook Framework** does not integrate Xposed API, you need to implement and connect **XposedBridge** by yourself.

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

**YukiHookModuleStatus**, **YukiHookModulePrefs**, **YukiHookDataChannel** and Resources Hook functionality will not work when using a custom Hook Framework instead of the full Xposed Module.

:::