# Host Lifecycle

> `YukiHook` provides extensions for the lifecycle of the host app, you can run custom logic while the host starts and runs.

## Register Listeners

To listen to the startup and lifecycle methods of the host `Application`, you can use the following.

> The following example

```kotlin
loadApp("io.github.myapp") {
    // Register the lifecycle listener, you can listen to the lifecycle methods of the `Application` here.
    // You can pass `throwToHostOnFailure = false` to keep
    // exceptions from being thrown to the host, true by default.
    registerAppLifecycle {
        attachBaseContext { baseContext, hasCalledSuper ->
            // Check `hasCalledSuper` to know whether `super.attachBaseContext(base)` has been called.
            // ...
        }
        onCreate {
            // Get the current `Application` instance with `this`.
            // ...
        }
        onTerminate {
            // Get the current `Application` instance with `this`.
            // ...
        }
        onLowMemory {
            // Get the current `Application` instance with `this`.
            // ...
        }
        onTrimMemory { self, level ->
            // You can check here whether the app has moved to the background.
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

`registerAppLifecycle` only takes effect for the host that owns the first `Application` in the current process.

::: tip

`attachBaseContext` and `onCreate` are not called again after [Module Hot Reload](./hot-reload.md).
If you need to initialize when the `Application` of the host is available, please refer to `withApplicationReady` in [Hook Entry → Handle Scopes](./hook-entry.md#handle-scopes).

:::

::: warning

`attachBaseContext` is not called in the system framework. Since the `Application` has already been created when hooking starts, `onCreate` is called after system services start.

:::

## Register System Broadcasts

We can register system broadcasts in the lifecycle as follows.

> The following example

```kotlin
registerAppLifecycle {
    // Listen to the broadcast when the user unlocks the device.
    registerReceiver(Intent.ACTION_USER_PRESENT) { context, intent ->
        // ...
    }
    // Listen to multiple broadcasts, the callback is called for each of them.
    registerReceiver(Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_TIME_TICK) { context, intent ->
        // ...
    }
    // Listen to broadcasts with an `IntentFilter`.
    registerReceiver(IntentFilter(Intent.ACTION_SCREEN_OFF)) { context, intent ->
        // ...
    }
}
```

By default, the receivers can receive broadcasts from other apps.
You can pass `exported = false` so that they no longer receive broadcasts from other apps.

> The following example

```kotlin
registerAppLifecycle {
    // Register a custom broadcast that does not receive broadcasts from other apps.
    registerReceiver("io.github.myapp.ACTION_CUSTOM", exported = false) { context, intent ->
        // ...
    }
}
```

::: tip

Keep the default value of `exported` when listening to system broadcasts. Some system broadcasts are not sent by the system framework, setting it to `false` may cause them not to be received.

:::