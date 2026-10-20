# Module Hot Reload

> This is a feature that reloads module code without restarting hosts.

YukiHook provides complete module lifecycle support for base modules that support hot reload. In YukiHook,
you basically do not need to handle the handover between the old and new generations yourself, and your hook code can continue running seamlessly in the new generation.

::: danger

This feature requires the target hook framework to support Xposed API version `102` or above, and requires the [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) base module.

:::

## Schema Version

To protect the integrity of data structures after hot reload, YukiHook defines its own schema version (`schemaVersion`), which identifies the internal semantic version of the API.

You can get the current schema version with `YukiHook.SCHEMA_VERSION`. The table below records the version each schema version corresponds to,
if there is no additional note, the schema version did not change after the corresponding version update.

| Version | Corresponding Version | Release Notes   |
| :-----: | :-------------------: | --------------- |
|    1    |         1.5.0         | Initial release |

## Enable Hot Reload

You need to mark the module entry with the `YukiHookLibXposedEntry` annotation, the hot reload feature is enabled or not based on the policy of the
[Entry Annotation](../yukihook-runtime-libxposed.md#entry-annotation). Then add the `hotReload` parameter to the annotation, and set `targetApiVersion` to `102` or above.

> The following example

```kotlin
@YukiHookLibXposedEntry(
    targetApiVersion = 102,
    // Enable the hot reload policy, see the policy description below.
    hotReload = YukiHookLibXposedEntry.HotReload.MANUAL
)
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        // Your code here.
    }
}
```

| Policy   | Description                                                                                              |
| -------- | -------------------------------------------------------------------------------------------------------- |
| `NONE`   | The default policy, hot reload requests are not accepted                                                 |
| `MANUAL` | Only accepts hot reload requests made manually                                                           |
| `AUTO`   | Accepts hot reload requests made manually, and hot reloads automatically after the module APK is updated |

Hot reload removes the old hooks and events, then replays the `onInit` and `onHook` events with the new module instance,
and the [Host Lifecycle](./host-lifecycle.md) events that have already completed are not replayed.

If you need to initialize when the `Application` of the host is available, please refer to `withApplicationReady` in [Module Entry → Handle Scopes](./hook-entry.md#handle-scopes).

::: danger

Due to the limitation of the base module, additional `javaEntries` cannot be declared in the entry annotation when hot reload is enabled, you can refer to
[Framework Events](../yukihook-runtime-libxposed.md#framework-events) to handle the additional entry logic of the framework native module.

Threads, native hooks and external callbacks registered in the host that the module creates need to be released manually, you can refer to the `onDispose` callback in [Module Lifecycle](#module-lifecycle) below.

The module refuses hot reload requests while an `Activity` started through [Module Resource Injection](./resource-injection.md) is running in the host,
the injected resources fail to be released, or a callback managed by YukiHook is running.

If the process does not respond on the main thread for more than 15 seconds during hot reload, the hot reload request will be automatically rejected.

After enabling the hot reload feature for the first time, the host must be restarted once after installing the module APK for the next hot reload request to take effect.

After the YukiHook dependency version is updated, the module can still hot reload normally. If this update also updates the [Schema Version](#schema-version),
the host must be restarted for it to take effect.

:::

## Request Hot Reload

In the module, you can use `YukiHook.Module.reload` to query running hosts and request hot reload.

> The following example

```kotlin
val reload = YukiHook.Module.reload
if (reload.isAvailable) {
    // Query host processes that can be reloaded.
    reload.targets.forEach { target ->
        // Process name, UID, PID and the current state.
        val processName = target.processName
        val state = target.state
        // Request a hot reload.
        reload.request(target) { result ->
            // The callback runs on the main thread.
            when (result.status) {
                ModuleReloadStatus.SUCCEEDED -> {
                    // Hot reload succeeded.
                }
                ModuleReloadStatus.SKIPPED -> {
                    // The host is already running the latest code.
                }
                else -> YLog.warn(result.message)
            }
        }
    }
    // You can also request it with the process name directly.
    // Usually, the main process name of the host is its package name.
    reload.request("io.github.myapp") { result ->
        // result is null when no unique process matches.
    }
}
```

By default, the request is skipped and returns `SKIPPED` when the host is already running the latest code, you can set `skippedIfUpToDate = false` to force the request again.

::: tip

You can also pass `extras` with the request, but it can only contain primitive values, strings, arrays and nested `Bundle` objects,
not objects defined in the module (classes loaded by a specific `ClassLoader`).

:::

## Module Lifecycle

You can register module lifecycle callbacks with `registerModuleLifecycle` in `onInit` to handle the resources held by the module that
need to be released or handed over during hot reload.

> The following example

```kotlin
registerModuleLifecycle {
    // Check whether the old generation can retire, returning false refuses this request.
    canReload { extras ->
        true
    }
    // Save state before reloading, the new generation receives it before `onHook`.
    onSaveState {
        bundleOf("count" to count)
    }
    // Stop and release the resources held by the old generation.
    onDispose {
        // Suppose you have an object that manages native hooks.
        nativeHookManager.release()
    }
    // Restore state after `onInit` and before `onHook` of the new generation.
    onRestoreState { state ->
        count = state?.getInt("count") ?: 0
    }
    // Called after the new generation is fully loaded.
    onReloaded { extras ->
        // Your code here.
    }
}
```

::: danger

`registerModuleLifecycle` can only be registered in `onInit`, and only once.

The saved and restored state must not contain objects defined in the module (classes loaded by a specific `ClassLoader`).

The callbacks run on the thread chosen by the hook framework, switch to the main thread for UI work.

:::

## Retain Values

Hot reload replays `onHook` completely, you can use `remember` to keep expensive computed results between the old and new generations of the module.

Currently the most expensive ones are members found by reflection, it is recommended to cache them first.

> The following example

```kotlin
loadApp("io.github.myapp") {
    val method = remember("myapp-obfuscated-a-v1") {
        val obfuscated = hostClassLoader.loadClass("io.github.myapp.Obfuscated")
        obfuscated.declaredMethods.singleOrNull {
            it.name == "a"
        }
    }

    method?.hook {
        // Your code here.
    }
}
```

The same `key` and declared result type reuse the saved result directly without running the block again. When hot reload is not enabled, the block runs on every call.

When the calculation changes but the result type stays the same, please change the `key` so that the block runs again in the new generation to cache the new result.

::: danger

`remember` can only be called while declaring hooks, such as in `onHook` or `loadApp`, it cannot be called in hook callbacks, lifecycle callbacks or other threads.

The `key` must be unique and non-blank in the scope of the current package name.

Retained values must not contain objects defined in the module (classes loaded by a specific `ClassLoader`), including the dependencies packaged with the module,
such as `Pair`, `Unit` and function types from the Kotlin standard library. Classes loaded by the system `ClassLoader` such as the `android.*` packages,
and `Class` objects from the host and `compileOnly` dependencies can be retained.

Collections and `Map` objects are frozen into read-only snapshots, please declare them as read-only `List`, `Set`, `Map`, `Collection` or `Iterable` types.
Arrays, `Bundle` and reference holders such as `WeakReference` are rejected.

:::