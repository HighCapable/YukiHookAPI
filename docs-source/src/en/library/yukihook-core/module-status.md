# Module Status

> `YukiHook` wraps the status checks of Xposed modules, you can get the activation status of the module and the hook framework information directly, and manage the module scope.

## Activation Status

In YukiHook, you only need one line of `YukiHook.Status.isModuleActive` to check whether the module itself is activated.

> The following example

```kotlin
if (YukiHook.Status.isModuleActive) {
    // Your code here.
}
```

::: warning

The activation status does not mean that the module has taken effect in a given host, it does not check the module scope or whether the hooks have been registered.

:::

## Hook Framework Information

Besides checking the activation status, you can also get information about the current hook framework through `Executor` in `YukiHook.Status`.

> The following example

```kotlin
// Get the name of the current hook framework.
YukiHook.Status.Executor.name
// Get the API version of the current hook framework.
YukiHook.Status.Executor.apiVersion
// Get the version name of the current hook framework.
YukiHook.Status.Executor.version
// Get the version code of the current hook framework.
YukiHook.Status.Executor.versionCode
```

You can also get the current environment and runtime (base module).

> The following example

```kotlin
// The runtime in use.
YukiHook.Status.runtime
// The compilation timestamp of the current module.
YukiHook.Status.compiledTimestamp
// Whether the current environment is a host environment.
YukiHook.Status.isHostEnvironment
```

::: warning

In the [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) base module, libxposed is designed to no longer hook the module itself but to interact with the hook framework service through AIDL,
so the accuracy of the module status depends on whether the service is connected successfully.

Since the connection to the hook framework service is asynchronous, we recommend that you first refer to [Hook Framework Service Status](#hook-framework-service-status)
below to make sure the service is available in the code that gets the hook framework information above.

:::

## Hook Framework Service Status

You can use `observeFrameworkService` to observe the availability of the hook framework service, the observer callback always runs on the main thread.

> The following example

```kotlin
YukiHook.Status.observeFrameworkService { available ->
    if(!available) return@observeFrameworkService

    val isActive = YukiHook.Status.isModuleActive
    val executorName = YukiHook.Status.Executor.name
    // ...
}
```

The observer is called once immediately on registration, and again when the service connects or disconnects.

You can pass a `Lifecycle` to the function to register a lifecycle observer automatically, or you can call `unregister` on the returned object to cancel the observer manually.

Since the connection to the service is asynchronous, and the hook framework gives no connection result when it is not activated,
a `false` in the first callback only means that the service is not connected yet, it does not mean that the module is not activated,
the observer is called again with `true` when the service connects later.

If you need to make a one-time decision based on it (for example, telling the user that the module is not activated),
you can wait for a while after receiving `false` and use the result that is decided first.

> The following example

```kotlin
val mainHandler = Handler(Looper.getMainLooper())
var isDecided = false
val onUnavailable = Runnable {
    isDecided = true
    // The service is not connected within a while, tell the user that the module is not activated.
}
YukiHook.Status.observeFrameworkService(lifecycle) { available ->
    mainHandler.removeCallbacks(onUnavailable)
    when {
        // The result is decided, only refresh the status.
        isDecided -> refreshStatus()
        available -> {
            isDecided = true
            // The service is connected, you can get the status of the module.
        }
        else -> mainHandler.postDelayed(onUnavailable, 500)
    }
}
```

The waiting time is only an estimate, when the hook framework responds slowly, the module may still be considered not activated first,
so you still need to refresh the status in later callbacks after the result is decided.

::: danger

This feature requires the [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) base module.

:::

## Module Scope

You can use `YukiHookModuleScope` in the module to query and request the scope of the module.

> The following example

```kotlin
val scope = YukiHook.Module.scope
if (scope.isAvailable) {
    // Get the scope of the current module, the system framework is "system".
    val packages = scope.packages
    // Request to add packages to the scope, multiple package names can be requested at once.
    scope.request("com.android.settings") { approved, errorMessage, packages ->
        // The callback runs on the main thread.
    }
    // Remove packages from the scope.
    scope.remove("com.android.settings")
}
```

After changing the scope, you need to restart the affected host processes for it to take effect.

::: danger

This feature requires the [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) base module.

:::