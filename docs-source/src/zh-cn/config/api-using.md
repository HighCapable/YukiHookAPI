# 作为 Hook API 使用的相关配置

> 作为 Hook API 通常为做自身 APP 热更新或功能需要以及产品测试的 Hook 操作。

## 依赖配置

你只需要集成 `com.highcapable.yukihookapi:api` 依赖即可。

然后请集成你目标使用的 `Hook Framework` 依赖。

## 入口配置

创建你的自定义 `Application`。

在 `attachBaseContext` 中添加 `YukiHookAPI.encase` 方法。

> 示例如下

```kotlin
class MyApplication : Application() {

    override fun attachBaseContext(base: Context?) {
        // 装载 Hook Framework
        //
        // Your code here.
        //
        // 配置 YukiHookAPI
        YukiHookApi.configs {
            // Your code here.
        }
        // 装载 YukiHookAPI
        YukiHookAPI.encase(base) {
            // Your code here.
        }
        super.attachBaseContext(base)
    }
}
```

这样，你就完成了 API 的相关配置。

你可以 [点击这里](../config/api-example#作为-hook-api-使用需要注意的地方) 查看异同点和注意事项。

::: warning

你不能再使用 **loadApp** 进行包装，可直接开始编写你的 Hook 代码。

:::

## Hook Framework

> 这里给出了一些较高使用率的 `Hook Framework` 如何对接 `YukiHookAPI` 的相关方式。

### [Pine](https://github.com/canyie/pine)

> **所需 Xposed API 依赖** `top.canyie.pine:xposed`

> 示例如下

```kotlin
override fun attachBaseContext(base: Context?) {
    // 装载 Pine
    PineConfig.debug = true
    PineConfig.debuggable = BuildConfig.DEBUG
    // 装载 YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

### [SandHook](https://github.com/asLody/SandHook)

> **所需 Xposed API 依赖** `com.swift.sandhook:xposedcompat` 或 `com.swift.sandhook:xposedcompat_new`

> 示例如下

```kotlin
override fun attachBaseContext(base: Context?) {
    // 装载 SandHook
    SandHookConfig.DEBUG = BuildConfig.DEBUG
    XposedCompat.cacheDir = base?.cacheDir
    XposedCompat.context = base
    XposedCompat.classLoader = javaClass.classLoader
    XposedCompat.isFirstApplication = base?.processName == base?.packageName
    // 装载 YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```

### [Whale](https://github.com/asLody/whale)

> **所需 Xposed API 依赖** `com.wind.xposed:xposed-on-whale`

请参考 [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale)。

> 示例如下

```kotlin
override fun attachBaseContext(base: Context?) {
    // 装载 Whale 不需要任何配置
    // 装载 YukiHookAPI
    YukiHookAPI.encase(base) {
        // Your code here.
    }
    super.attachBaseContext(base)
}
```