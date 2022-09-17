# 宿主生命周期扩展
 
> 这是一个自动 Hook 宿主 APP 生命周期的扩展功能。

## 监听生命周期

> 通过自动化 Hook 宿主 APP 的生命周期方法，来实现监听功能。

我们需要监听宿主 `Application` 的启动和生命周期方法，只需要使用以下方式实现。

> 示例如下

```kotlin
loadApp(name = "com.example.demo") {
    // 注册生命周期监听
    onAppLifecycle {
        // 你可以在这里实现 Application 中的生命周期方法监听
        attachBaseContext { baseContext, hasCalledSuper ->
            // 通过判断 hasCalledSuper 来确定是否已执行 super.attachBaseContext(base) 方法
            // ...
        }
        onCreate {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onTerminate {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onLowMemory {
            // 通过 this 得到当前 Application 实例
            // ...
        }
        onTrimMemory { self, level ->
            // 可在这里判断 APP 是否已切换到后台
            if (level == ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN) {
                // ...
            }
            // ...
        }
        onConfigurationChanged { self, config ->
            // ...
        }
    }
}
```

::: tip

更多功能请参考 [AppLifecycle](../public/com/highcapable/yukihookapi/hook/param/PackageParam#applifecycle-class)。

:::

## 注册系统广播

> 通过 `Application.onCreate` 方法注册系统广播，来实现对系统广播的监听。

我们还可以在宿主 `Application` 中注册系统广播。

> 示例如下

```kotlin
loadApp(name = "com.example.demo") {
    // 注册生命周期监听
    onAppLifecycle {
        // 注册用户解锁时的广播监听
        registerReceiver(Intent.ACTION_USER_PRESENT) { context, intent ->
            // ...
        }
        // 注册多个广播监听 - 会同时回调多次
        registerReceiver(Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_TIME_TICK) { context, intent ->
            // ...
        }
    }
}
```

::: tip

更多功能请参考 [AppLifecycle](../public/com/highcapable/yukihookapi/hook/param/PackageParam#applifecycle-class)。

:::