# Use as Hook API Configs

> As a Hook API, it is usually used for Hook operations for hot updates or functional needs of its own app and product testing.

## Dependency Configs

You just need to integrate the `com.highcapable.yukihookapi:api` dependency.

Then please integrate the `Hook Framework` dependencies used by your target.

## Entry Configs

Create your custom `Application`.

Add `YukiHookAPI.encase` method to `attachBaseContext`.

> The following example

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        // Load Hook Framework
        //
        // Your code here.
        //
        // Configure YukiHookAPI
        YukiHookApi.configs {
            // Your code here.
        }
        // Load YukiHookAPI
        YukiHookAPI.encase(base) {
            // Your code here.
        }
        super.attachBaseContext(base)
    }
}
```

In this way, you have completed the relevant configuration of the API.

You can [click here](../config/api-example#precautions-when-using-as-hook-api) to see the similarities, differences and caveats.

::: warning

You can no longer wrap with **loadApp** and start writing your Hook code directly.

:::

## Hook Framework

> Here are some related ways of how to connect the `Hook Framework` with the `YukiHookAPI`, which is widely used.

### [Pine](https://github.com/canyie/pine)

> **Required Xposed API dependencies** `top.canyie.pine:xposed`

> The following example

```kotlin
override fun attachBaseContext(base: Context?) {
    // Load Pine
    PineConfig.debug = true
    PineConfig.debuggable = BuildConfig.DEBUG
    // Load YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

### [SandHook](https://github.com/asLody/SandHook)

> **Required Xposed API dependencies** `com.swift.sandhook:xposedcompat` or `com.swift.sandhook:xposedcompat_new`

> The following example

```kotlin
override fun attachBaseContext(base: Context?) {
    // Load SandHook
    SandHookConfig.DEBUG = BuildConfig.DEBUG
    XposedCompat.cacheDir = base?.cacheDir
    XposedCompat.context = base
    XposedCompat.classLoader = javaClass.classLoader
    XposedCompat.isFirstApplication = base?.processName == base?.packageName
    // Load YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

### [Whale](https://github.com/asLody/whale)

> **Required Xposed API dependencies** `com.wind.xposed:xposed-on-whale`

Please refer to [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale).

> The following example

```kotlin
override fun attachBaseContext(base: Context?) {
    // Loading Whale does not require any configuration
    // Load YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```