# 宿主生命周期

> `YukiHook` 提供了对宿主应用生命周期的扩展能力，可以在宿主启动、运行过程中执行自定义逻辑。

## 注册监听

我们需要监听宿主 `Application` 的启动和生命周期方法，可以使用如下方式实现。

> 示例如下

```kotlin
loadApp("io.github.myapp") {
    // 注册生命周期监听，你可以在这里实现 `Application` 中的生命周期方法监听
    // 你可以传入 `throwToHostOnFailure = false` 来控制异常不抛给宿主，默认为 true
    registerAppLifecycle {
        attachBaseContext { baseContext, hasCalledSuper ->
            // 通过判断 `hasCalledSuper` 来确定是否已执行 `super.attachBaseContext(base)` 方法
            // ...
        }
        onCreate {
            // 通过 `this` 得到当前 `Application` 实例
            // ...
        }
        onTerminate {
            // 通过 `this` 得到当前 `Application` 实例
            // ...
        }
        onLowMemory {
            // 通过 `this` 得到当前 `Application` 实例
            // ...
        }
        onTrimMemory { self, level ->
            // 可在这里判断应用是否已切换到后台
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

`registerAppLifecycle` 只会对当前进程中拥有第一个 `Application` 的宿主生效。

::: tip

`attachBaseContext` 及 `onCreate` 在 [模块热重载](./hot-reload.md) 后不会被重新回调，如果你需要在宿主的 `Application` 可用时执行初始化，请参考 [模块入口 → 处理作用域](./hook-entry.md#处理作用域) 中的 `withApplicationReady`。

:::

::: warning

`attachBaseContext` 不会在系统框架中被回调，由于 `Application` 在 Hook 开始时就已经创建完成，`onCreate` 将会在系统服务启动后被回调。

:::

## 注册系统广播

我们可以在生命周期中使用如下方式注册系统广播。

> 示例如下

```kotlin
registerAppLifecycle {
    // 注册用户解锁时的广播监听
    registerReceiver(Intent.ACTION_USER_PRESENT) { context, intent ->
        // ...
    }
    // 注册多个广播监听，会同时回调多次
    registerReceiver(Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_TIME_TICK) { context, intent ->
        // ...
    }
    // 使用 `IntentFilter` 注册广播监听
    registerReceiver(IntentFilter(Intent.ACTION_SCREEN_OFF)) { context, intent ->
        // ...
    }
}
```

广播默认允许接收来自其他应用的广播，你可以传入 `exported = false` 使其不再接收来自其他应用的广播。

> 示例如下

```kotlin
registerAppLifecycle {
    // 注册不接收来自其他应用的自定义广播
    registerReceiver("io.github.myapp.ACTION_CUSTOM", exported = false) { context, intent ->
        // ...
    }
}
```

::: tip

监听系统广播时请保持 `exported` 的默认值，部分系统广播并不是由系统框架发送的，设置为 `false` 可能会导致无法接收它们。

:::