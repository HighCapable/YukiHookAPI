# Host Lifecycle Extension

> This is an extension of the lifecycle of an automatic hooking Host App.

## Listener Lifecycle

> Implement the listening function by automating the lifecycle method of the Host App.

We need to listen to the startup and lifecycle methods of the Host App's `Application`, just use the following methods.

> The following example

```kotlin
loadApp(name = "com.example.demo") {
    // Register lifecycle listeners
    // Optional parameter:
    // You can set isOnFailureThrowToApp = false
    // So that the exception will not be thrown to the Host App to prevent the Host App from crashing
    // The default is true
    onAppLifecycle(isOnFailureThrowToApp = true) {
        // You can implement lifecycle method listeners in Application here
        attachBaseContext { baseContext, hasCalledSuper ->
            // Determine whether
            // The super.attachBaseContext(base) method has been executed by judging hasCalledSuper
            // ...
        }
        onCreate {
            // Get the current Application instance through this
            // ...
        }
        onTerminate {
            // Get the current Application instance through this
            // ...
        }
        onLowMemory {
            // Get the current Application instance through this
            // ...
        }
        onTrimMemory { self, level ->
            // Here you can judge whether the app has switched to the background
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

For more functions, please refer to [AppLifecycle](../public/com/highcapable/yukihookapi/hook/param/PackageParam#applifecycle-class).

:::

## Register System Broadcast

> Register system broadcast through the `Application.onCreate` method to listening system broadcast.

We can also register system broadcast in the Host App's `Application`.

> The following example

```kotlin
loadApp(name = "com.example.demo") {
    // Register lifecycle listeners
    onAppLifecycle {
        // Broadcast listening when the registered user is unlocked
        registerReceiver(Intent.ACTION_USER_PRESENT) { context, intent ->
            // ...
        }
        // Register multiple broadcast listeners, will call back multiple times at the same time
        registerReceiver(Intent.ACTION_PACKAGE_CHANGED, Intent.ACTION_TIME_TICK) { context, intent ->
            // ...
        }
    }
}
```

::: tip

For more functions, please refer to [AppLifecycle](../public/com/highcapable/yukihookapi/hook/param/PackageParam#applifecycle-class).

:::