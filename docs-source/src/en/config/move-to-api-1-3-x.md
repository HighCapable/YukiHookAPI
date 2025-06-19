# Migrate to YukiHookAPI 1.3.x

`YukiHookAPI` has deprecated its own reflection API since `1.3.0`, you can read on to see what are the notes and new features.

::: warning

If you are using `1.2.x` and previous versions of `YukiHookAPI`, it is recommended to read [Migrate to YukiHookAPI 1.2.x](move-to-api-1-2-x) instead of this document.

:::

## Self-reflection API Deprecated

`YukiHookAPI` has deprecated its own reflection API since the `1.3.0` version. Now we recommend that all developers move to a brand new development.
[KavaRef](https://github.com/HighCapable/KavaRef), we no longer recommend the reflection API of `YukiHookAPI` itself, which have been marked as deprecated.

Please refer to the migration document [here](https://highcapable.github.io/KavaRef/en/config/migration) which will jump to the `KavaRef` document.

`YukiHookAPI` has now implemented complete decoupling of the reflection API.
The reflection API used by its internal API has also been migrated to `KavaRef` and has been tested stably.

In later versions of `2.0.0`, the self-reflection API will be completely removed,
during which time you will have enough time to learn and migrate to this brand new set of reflection APIs.

## FreeReflection Deprecated

`YukiHookAPI` has deprecated [FreeReflection](https://github.com/tiann/FreeReflection) since the `1.3.0` version and migrated to a maintained by the LSPosed team
[AndroidHiddenApiBypass](https://github.com/LSPosed/AndroidHiddenApiBypass).

When the reflection system hides the API, you cannot reflect directly like before, but need to do some operations.

YukiHookAPI has built-in `AndroidHiddenApiBypassResolver` in `KavaRef`'s third-party Member parser,
and now you can use it like this where you need the reflection system to hide the API.

> The following example

```kotlin
"android.app.ActivityThread".toClass()
    .resolve()
    // Add a custom Member parser
    .processor(AndroidHiddenApiBypassResolver.get())
    .firstMethod {
        name = "currentActivityThread"
        emptyParameters()
    }.invoke()
```

::: warning

`AndroidHiddenApiBypassResolver` is a tentative feature and may be migrated to a separate module in the `2.0.0` version,
you can also refer to [Third-party Member Resolvers](https://highcapable.github.io/KavaRef/en/config/processor-resolvers) implement one by yourself,
which will jump to the `KavaRef` document.

:::

## Original Method Call

`Xposed` provides the `XposedBridge.invokeOriginalMethod` function, which can call original methods without a Hook.

Due to deprecation of the self-reflection API, the method `method { ... }.get().original().call(...)` will no longer be available.

So, YukiHookAPI has added an extension to `KavaRef`, and you can still implement this feature now.

`YukiHookAPI` provides the following methods to connect to the original method calls of `KavaRef`.

- `invokeOriginal(...)` → `invoke(...)`
- `invokeOriginalQuietly(...)` → `invokeQuietly(...)`

> The following example

```kotlin
// Suppose this is an instance of the Test class
val instance: Any
// Original call to the method using KavaRef
"com.example.Test".toClass()
    .resolve()
    .firstMethod {
        name = "test"
        emptyParameters()
    }.of(instance).invokeOriginal()
```

## Repeat Hook Restricted Deprecated

`YukiHookAPI` has deprecated the restriction of duplicate Hook since the `1.3.0` version.
Now, `YukiHookAPI` no longer limits duplicate Hooks to the same method, you can hook multiple times on the same method.

`YukiHookAPI` also deprecated the `onAlreadyHooked` method of `hook { ... }`.
Now this method will be useless and will not be called back. If necessary, please manually handle the relevant logic of duplicate Hooks.

## Register Module App's Activity Behavior Change

`YukiHookAPI` Starting with `1.3.0`, the way in which the module `Activity` behavior has changed.

Please read [Register Module App's Activity](../api/special-features/host-inject#register-module-app-s-activity) for more information.

## YLog Behavior Change

`YukiHookAPI` allows the `msg` parameter of `YLog` to be passed into any object starting from `1.3.0`, and they will be automatically converted using the `toString()` method.