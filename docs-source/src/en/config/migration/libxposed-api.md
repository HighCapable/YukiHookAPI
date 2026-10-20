# libxposed API

> Here describes how to migrate from the [libxposed API](https://github.com/libxposed/api) to `YukiHook`.

## Migrate the Module Entry

### Create the Entry Class

> The following example

::: code-group

```kotlin [YukiHook]
object HookEntry : YukiHookXposedModule {

    override fun onHook() = encase {
        loadApp("io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [libxposed API]
class HookEntry : XposedModule() {

    override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
        if (param.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

:::

`onHook` is called when hooks start to load, it corresponds to loading callbacks such as
`onPackageReady` and `onSystemServerStarting`, and is called once in every hooked process.

For details, please refer to [Module Entry → Create the Entry Class](../../library/yukihook-core/hook-entry.md#create-the-entry-class).

::: tip

If you need to use native framework entries or events, please refer to
[Native Module Entries](../../library/yukihook-runtime-libxposed.md#native-module-entries) and
[Framework Events](../../library/yukihook-runtime-libxposed.md#framework-events) of the base module.

:::

### Handle Scopes

In the libxposed API, you need to check the package name in `onPackageReady` yourself and
handle the system framework in `onSystemServerStarting`, YukiHook provides the corresponding scope methods.

> The following example

::: code-group

```kotlin [YukiHook]
override fun onHook() = encase {
    // Hook a specific host.
    loadApp("io.github.myapp") {
        // Your code here.
    }
    // Hook multiple hosts.
    loadApp("io.github.myapp", "io.github.anotherapp") {
        // Your code here.
    }
    // Hook the system framework.
    loadSystem {
        // Your code here.
    }
}
```

```kotlin [libxposed API]
override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    // Hook a specific host.
    if (param.packageName == "io.github.myapp") {
        // Your code here.
    }
    // Hook multiple hosts.
    if (param.packageName == "io.github.myapp" || param.packageName == "io.github.anotherapp") {
        // Your code here.
    }
}

// Hook the system framework.
override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
    // Your code here.
}
```

:::

If you need to tell the processes of a host apart, you can use `withProcess`.

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // Hook the main process.
    withProcess(mainProcessName) {
        // Your code here.
    }
    // Hook another process.
    withProcess("$packageName:tool") {
        // Your code here.
    }
}
```

```kotlin [libxposed API]
private var processName = ""

override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
    // The name of the current process.
    processName = param.processName
}

override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    if (param.packageName == "io.github.myapp") {
        // Hook the main process.
        if (processName == param.packageName) {
            // Your code here.
        }
        // Hook another process.
        if (processName == "${param.packageName}:tool") {
            // Your code here.
        }
    }
}
```

:::

If you need to run hooks after the `Application` of the host is created,
the libxposed API requires you to hook `Application.onCreate` manually, while YukiHook provides `withApplicationReady`.

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    withApplicationReady { application ->
        // Your code here.
    }
}
```

```kotlin [libxposed API]
if (param.packageName == "io.github.myapp") {
    hook(Application::class.java.getDeclaredMethod("onCreate")).intercept { chain ->
        val result = chain.proceed()
        val application = chain.thisObject as Application
        // Your code here.
        result
    }
}
```

:::

For details, please refer to [Module Entry → Handle Scopes](../../library/yukihook-core/hook-entry.md#handle-scopes).

### Get Host Information

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // The package name of the current host.
    packageName
    // The process name of the current host.
    processName
    // The main process name of the current host.
    mainProcessName
    // Whether the current host is the first `Application` in the current process.
    isFirstApplication
    // The `ApplicationInfo` of the current host.
    appInfo
    // The `ClassLoader` of the current host.
    hostClassLoader
    // The `Application` of the current host, may be empty on the first load.
    hostApplication
    // The `ApplicationInfo` of the current Xposed module.
    module.appInfo
}
```

```kotlin [libxposed API]
override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    if (param.packageName == "io.github.myapp") {
        // The package name of the current host.
        param.packageName
        // The process name of the current host, it needs to be saved in onModuleLoaded.
        processName
        // The main process name of the current host, you need to decide it yourself, the package name is used here.
        param.packageName
        // Whether the current host is the first `Application` in the current process.
        param.isFirstPackage
        // The `ApplicationInfo` of the current host.
        param.applicationInfo
        // The `ClassLoader` of the current host.
        param.classLoader
        // There is no direct counterpart, please refer to the `Application.onCreate` hook above.
        // The `ApplicationInfo` of the current Xposed module.
        moduleApplicationInfo
    }
}
```

:::

For details, please refer to [Module Entry → Host Information](../../library/yukihook-core/hook-entry.md#host-information).

## Migrate Hook Usage

### Basic Usage

Suppose we want to hook the `onCreate` method of every `Activity` in `io.github.myapp` and show a dialog after it runs.

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    val activityClass = Activity::class.java
    // Get the onCreate method with Java reflection.
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    onCreate.intercept {
        // Run the original method first.
        val result = proceed()
        // Get the instance of the method being hooked.
        val context = instance<Activity>()

        AlertDialog.Builder(context)
            .setTitle("Hooked")
            .setMessage("onCreate has been hooked!")
            .setPositiveButton("OK", null)
            .show()

        result
    }
}
```

```kotlin [libxposed API]
if (param.packageName == "io.github.myapp") {
    val activityClass = Activity::class.java
    // Get the onCreate method with Java reflection.
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    hook(onCreate).intercept { chain ->
        // Run the original method first.
        val result = chain.proceed()
        // Get the instance of the method being hooked.
        val context = chain.thisObject as Activity

        AlertDialog.Builder(context)
            .setTitle("Hooked")
            .setMessage("onCreate has been hooked!")
            .setPositiveButton("OK", null)
            .show()

        result
    }
}
```

:::

At this point, the `onCreate` method is hooked successfully, and this dialog is shown whenever an `Activity` in `io.github.myapp` starts.

If you need to hook multiple methods or constructors at the same time, you can use `interceptAll`.

> The following example

::: code-group

```kotlin [YukiHook]
// Suppose this is the class you need to hook.
val clazz: Class<*>

// Hook a constructor.
clazz.getDeclaredConstructor(String::class.java).intercept {
    proceed()
}

// Hook multiple methods.
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.interceptAll {
    proceed()
}
```

```kotlin [libxposed API]
// Suppose this is the class you need to hook.
val clazz: Class<*>

// Hook a constructor.
hook(clazz.getDeclaredConstructor(String::class.java)).intercept { chain ->
    chain.proceed()
}

// Hook multiple methods.
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.forEach { method ->
    hook(method).intercept { chain ->
        chain.proceed()
    }
}
```

:::

For details, please refer to [Hook Usage → Basic Usage](../../library/yukihook-core/hook-usage.md#basic-usage).

### Chain Hook

The chain hook of YukiHook is basically the same as the interceptor of libxposed API, `HookChain` corresponds to `XposedInterface.Chain`.

> The following example

::: code-group

```kotlin [YukiHook]
method.intercept {
    // The instance of the current hook.
    instance
    // Static methods have no instance, use `instanceOrNull`.
    instanceOrNull
    // The method or constructor being hooked.
    member
    // The read-only array of method arguments.
    args
    // The first method argument as T.
    firstArg<T>()
    // The last method argument as T.
    lastArg<T>()
    // The method argument at any index as T, index 2 is used as an example.
    arg<T>(2)
    // Continue the chain with the current arguments.
    proceed()
    // Continue the chain with new arguments.
    proceed(arrayOf(...))
    // Continue the chain with a new instance.
    proceedWith(instance = ...)
}
```

```kotlin [libxposed API]
hook(method).intercept { chain ->
    // The instance of the current hook.
    chain.thisObject
    // Static methods have no instance, chain.thisObject is null.
    chain.thisObject as? T
    // The method or constructor being hooked.
    chain.executable
    // The read-only array of method arguments.
    chain.args
    // The first method argument as T.
    chain.getArg(0) as T
    // The last method argument as T.
    chain.getArg(chain.args.lastIndex) as T
    // The method argument at any index as T, index 2 is used as an example.
    chain.getArg(2) as T
    // Continue the chain with the current arguments.
    chain.proceed()
    // Continue the chain with new arguments.
    chain.proceed(arrayOf(...))
    // Continue the chain with a new instance.
    chain.proceedWith(...)
}
```

:::

If you need to modify the arguments or the return value, you can pass a new array of arguments to `proceed` and put the value to return in the last position.

> The following example

::: code-group

```kotlin [YukiHook]
method.intercept {
    // Continue the chain after modifying the first argument.
    val result = proceed(arrayOf("Hello YukiHook", lastArg()))
    // Modify the return value.
    "Modified $result"
}
```

```kotlin [libxposed API]
hook(method).intercept { chain ->
    // Continue the chain after modifying the first argument.
    val result = chain.proceed(arrayOf("Hello YukiHook", chain.getArg(chain.args.lastIndex)))
    // Modify the return value.
    "Modified $result"
}
```

:::

When `proceed` is not called, the original method is not run, which is the same as replacing the whole method.

> The following example

::: code-group

```kotlin [YukiHook]
// Replace with the return value you need.
method.intercept { ... }
// Replace with a Boolean return value.
method.intercept { true }
// Intercept the method and return null.
method.intercept { null }
```

```kotlin [libxposed API]
// Replace with the return value you need.
hook(method).intercept { _ -> ... }
// Replace with a Boolean return value.
hook(method).intercept { _ -> true }
// Intercept the method and return null.
hook(method).intercept { _ -> null }
```

:::

For details, please refer to [Hook Usage → Chain Hook](../../library/yukihook-core/hook-usage.md#chain-hook).

### Classic Hook

YukiHook also supports the classic before/after callback hook when using the libxposed API as the base module,
for details please refer to [Hook Usage → Classic Hook](../../library/yukihook-core/hook-usage.md#classic-hook).

### Call the Original Method

> The following example

::: code-group

```kotlin [YukiHook]
// Call the original implementation of a method.
method.invokeOriginal(instance, ...)
// Call a method non-virtually.
method.invokeSpecial(instance, ...)
// Call the original implementation of a constructor.
constructor.newInstanceOriginal(...)
```

```kotlin [libxposed API]
// Call the original implementation of a method.
getInvoker(method).setType(XposedInterface.Invoker.Type.ORIGIN).invoke(instance, ...)
// Call a method non-virtually.
getInvoker(method).invokeSpecial(instance, ...)
// Call the original implementation of a constructor.
getInvoker(constructor).setType(XposedInterface.Invoker.Type.ORIGIN).newInstance(...)
```

:::

For details, please refer to [Hook Usage → Call the Original Method](../../library/yukihook-core/hook-usage.md#call-the-original-method).

### Hook Static Initializers

> The following example

::: code-group

```kotlin [YukiHook]
// Suppose this is the class you need to hook.
val clazz: Class<*>
// Hook <clinit>.
clazz.intercept {
    proceed()
}
```

```kotlin [libxposed API]
// Suppose this is the class you need to hook.
val clazz: Class<*>
// Hook <clinit>.
hookClassInitializer(clazz).intercept { chain ->
    chain.proceed()
}
```

:::

Please make sure the `Class` is not initialized when you get it,
for details please refer to [Hook Usage → Hook Static Initializers](../../library/yukihook-core/hook-usage.md#hook-static-initializers).

### Hook Priority

> The following example

::: code-group

```kotlin [YukiHook]
method.intercept(priority = YukiHookPriority.HIGHEST) {
    // Your code here.
}
```

```kotlin [libxposed API]
hook(method)
    .setPriority(XposedInterface.PRIORITY_HIGHEST)
    .intercept { chain ->
        // Your code here.
    }
```

:::

### Remove Hooks

> The following example

::: code-group

```kotlin [YukiHook]
// Save the current instance and call it at the right time.
val handle = method.intercept {
    // Remove itself in the callback.
    removeSelf()
    proceed()
}

handle.remove()
```

```kotlin [libxposed API]
// Save the current instance and call it at the right time.
var handle: XposedInterface.HookHandle? = null
handle = hook(method).intercept { chain ->
    // Remove itself in the callback, using the instance saved above.
    handle?.unhook()
    chain.proceed()
}

handle?.unhook()
```

:::

For details, please refer to [Hook Usage → Remove Hooks](../../library/yukihook-core/hook-usage.md#remove-hooks).

### Replace Hook Callbacks

This feature requires libxposed API 102 or above.

> The following example

::: code-group

```kotlin [YukiHook]
val handle = method.intercept {
    proceed()
}
// Replace with a new callback.
handle.replace {
    // Your code here.
    proceed()
}
```

```kotlin [libxposed API]
val handle = hook(method).intercept { chain ->
    chain.proceed()
}
// Replace with a new callback.
handle.replaceHook { chain ->
    // Your code here.
    chain.proceed()
}
```

:::

For details, please refer to [Hook Usage → Replace Hook Callbacks](../../library/yukihook-core/hook-usage.md#replace-hook-callbacks).

### Deoptimize

> The following example

::: code-group

```kotlin [YukiHook]
// Suppose this is the calling method.
val callingMethod: Method
// Deoptimize it.
callingMethod.deoptimize()
```

```kotlin [libxposed API]
// Suppose this is the calling method.
val callingMethod: Method
// Deoptimize it.
deoptimize(callingMethod)
```

:::

For details, please refer to [Hook Usage → Deoptimize](../../library/yukihook-core/hook-usage.md#deoptimize).

### Exception Handling

In the libxposed API, you can decide the behavior when an exception occurs in a hook callback with `setExceptionMode`.

YukiHook does not provide this option, exceptions in a chain hook are handled in the way of `ExceptionMode.PROTECTIVE` by default,
and setting `throwToHostOnFailure` corresponds to `ExceptionMode.PASSTHROUGH`.

> The following example

::: code-group

```kotlin [YukiHook]
method.intercept {
    // Throw an exception to the caller.
    throw RuntimeException("Exception Test")
}.throwToHostOnFailure()
```

```kotlin [libxposed API]
hook(method)
    .setExceptionMode(XposedInterface.ExceptionMode.PASSTHROUGH)
    .intercept { chain ->
        // Throw an exception to the caller.
        throw RuntimeException("Exception Test")
    }
```

:::

For details, please refer to [Hook Usage → Exceptions in Chain Hooks](../../library/yukihook-core/hook-usage.md#exceptions-in-chain-hooks).