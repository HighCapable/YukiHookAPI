# Xposed Framework API

> Here describes how to migrate from the [Xposed Framework API](https://api.xposed.info/) to `YukiHook`.

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

```kotlin [Xposed Framework API]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

:::

`onHook` is called when hooks start to load, it corresponds to `handleLoadPackage` and is called once in every hooked process.

For details, please refer to [Module Entry → Create the Entry Class](../../library/yukihook-core/hook-entry.md#create-the-entry-class).

::: tip

If you need to use native framework events, please refer to the [Framework Events](../../library/yukihook-runtime-xposed82.md#framework-events) of the base module.

:::

### Handle Scopes

In the Xposed Framework API, you need to check the package name in `handleLoadPackage` yourself, YukiHook provides the corresponding scope methods.

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
    // Hook all hosts and exclude the module itself.
    loadApp(excludeSelf = true) {
        // Your code here.
    }
    // Hook the system framework.
    loadSystem {
        // Your code here.
    }
}
```

```kotlin [Xposed Framework API]
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // Hook a specific host.
    if (lpparam.packageName == "io.github.myapp") {
        // Your code here.
    }
    // Hook multiple hosts.
    if (lpparam.packageName == "io.github.myapp" || lpparam.packageName == "io.github.anotherapp") {
        // Your code here.
    }
    // Hook all hosts and exclude the module itself.
    if (lpparam.packageName != "io.github.mymodule") {
        // Your code here.
    }
    // Hook the system framework, the package name of the system framework is android.
    if (lpparam.packageName == "android") {
        // Your code here.
    }
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

```kotlin [Xposed Framework API]
if (lpparam.packageName == "io.github.myapp") {
    // Hook the main process.
    if (lpparam.processName == lpparam.packageName) {
        // Your code here.
    }
    // Hook another process.
    if (lpparam.processName == "${lpparam.packageName}:tool") {
        // Your code here.
    }
}
```

:::

If you need to run hooks after the `Application` of the host is created,
the Xposed Framework API requires you to hook `Application.onCreate` manually, while YukiHook provides `withApplicationReady`.

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    withApplicationReady { application ->
        // Your code here.
    }
}
```

```kotlin [Xposed Framework API]
if (lpparam.packageName == "io.github.myapp") {
    XposedBridge.hookMethod(
        Application::class.java.getDeclaredMethod("onCreate"),
        object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val application = param.thisObject as Application
                // Your code here.
            }
        }
    )
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
}
```

```kotlin [Xposed Framework API]
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    if (lpparam.packageName == "io.github.myapp") {
        // The package name of the current host.
        lpparam.packageName
        // The process name of the current host.
        lpparam.processName
        // The main process name of the current host, you need to decide it yourself,
        // the package name is used here.
        lpparam.packageName
        // Whether the current host is the first `Application` in the current process.
        lpparam.isFirstApplication
        // The `ApplicationInfo` of the current host.
        lpparam.appInfo
        // The `ClassLoader` of the current host.
        lpparam.classLoader
        // The `Application` of the current host, may be empty on the first load.
        AndroidAppHelper.currentApplication()
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

    onCreate.hook {
        after {
            // Get the instance of the method being hooked.
            val context = instance<Activity>()

            AlertDialog.Builder(context)
                .setTitle("Hooked")
                .setMessage("onCreate has been hooked!")
                .setPositiveButton("OK", null)
                .show()
        }
    }
}
```

```kotlin [Xposed Framework API]
if (lpparam.packageName == "io.github.myapp") {
    val activityClass = Activity::class.java
    // Get the onCreate method with Java reflection.
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    XposedBridge.hookMethod(
        onCreate, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                // Get the instance of the method being hooked.
                val context = param.thisObject as Activity

                AlertDialog.Builder(context)
                    .setTitle("Hooked")
                    .setMessage("onCreate has been hooked!")
                    .setPositiveButton("OK", null)
                    .show()
            }
        }
    )
}
```

:::

At this point, the `onCreate` method is hooked successfully, and this dialog is shown whenever an `Activity` in `io.github.myapp` starts.

If you need to hook multiple methods or constructors at the same time, you can use `hookAll`.

> The following example

::: code-group

```kotlin [YukiHook]
// Suppose this is the class you need to hook.
val clazz: Class<*>

// Hook a constructor.
clazz.getDeclaredConstructor(String::class.java).hook {
    after {
        // Your code here.
    }
}

// Hook multiple methods.
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [Xposed Framework API]
// Suppose this is the class you need to hook.
val clazz: Class<*>

// Hook a constructor.
XposedBridge.hookMethod(
    clazz.getDeclaredConstructor(String::class.java),
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)

// Hook multiple methods.
XposedBridge.hookAllMethods(
    clazz, "foo", object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
```

:::

For details, please refer to [Hook Usage → Basic Usage](../../library/yukihook-core/hook-usage.md#basic-usage).

### Classic Hook

The classic hook of YukiHook reproduces the complete mechanism of the Xposed Framework API era, `HookParam` corresponds to `XC_MethodHook.MethodHookParam`.

> The following example

::: code-group

```kotlin [YukiHook]
hook {
    before {
        // The instance of the current hook.
        instance
        // The Class instance of the current hook.
        instance.javaClass
        // The instance of the current hook, cast to the specified type T.
        instance<T>()
        // Static methods have no instance, use `instanceOrNull`.
        instanceOrNull
        // The method or constructor being hooked.
        member
        // The array of method arguments.
        args
        // The first method argument as T.
        firstArg().get<T>()
        // The last method argument as T.
        lastArg().get<T>()
        // The method argument at any index as T, index 2 is used as an example.
        arg(2).get<T>()
        // Set the method argument at any index, index 2 is used as an example.
        arg(2).set(...)
        // The data storage instance within the scope of the current callback body.
        dataExtra
    }
    after {
        // The return value.
        result
        // The return value, cast to T.
        result<T>()
        // Modify the return value.
        result = ...
    }
}
```

```kotlin [Xposed Framework API]
object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        // The instance of the current hook.
        param.thisObject
        // The Class instance of the current hook.
        param.thisObject.javaClass
        // The instance of the current hook, cast to the specified type T.
        param.thisObject as T
        // Static methods have no instance, param.thisObject is null.
        param.thisObject as? T
        // The method or constructor being hooked.
        param.method
        // The array of method arguments.
        param.args
        // The first method argument as T.
        param.args[0] as T
        // The last method argument as T.
        param.args[param.args.lastIndex] as T
        // The method argument at any index as T, index 2 is used as an example.
        param.args[2] as T
        // Set the method argument at any index, index 2 is used as an example.
        param.args[2] = ...
        // The data storage instance within the scope of the current callback body.
        param.extra
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        // The return value.
        param.result
        // The return value, cast to T.
        param.result as T
        // Modify the return value.
        param.result = ...
    }
}
```

:::

`dataExtra` is different from `param.extra`, it is shared among all invocations of the current hook, instead of existing independently in each invocation.

For details, please refer to [Hook Usage → Classic Hook](../../library/yukihook-core/hook-usage.md#classic-hook).

### Intercept the Method

> The following example

::: code-group

```kotlin [YukiHook]
// A method with a return value.
hook {
    intercept {
        // Implement the replaced logic here.
        // The return value of the method needs to be returned,
        // no return is needed, just put it in the last position.
        0
    }
}

// A method without a return value (void).
hook {
    intercept {
        // Implement the replaced logic here directly.
    }
}

// Replace with the return value you need.
hook().intercept(...)
// Replace with a Boolean return value.
hook().intercept(true)
// Intercept the method and return null.
hook().intercept()
```

```kotlin [Xposed Framework API]
// A method with a return value.
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // Implement the replaced logic here.
        // ...
        return 0
    }
}

// A method without a return value (void).
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // Implement the replaced logic here directly.
        return null
    }
}

// Replace with the return value you need.
XC_MethodReplacement.returnConstant(...)
// Replace with a Boolean return value.
XC_MethodReplacement.returnConstant(true)
// Intercept the method and return null.
XC_MethodReplacement.DO_NOTHING
```

:::

For details, please refer to [Hook Usage → Intercept the Method](../../library/yukihook-core/hook-usage.md#intercept-the-method).

### Call the Original Method

> The following example

::: code-group

```kotlin [YukiHook]
hook {
    intercept {
        // Call the original method with the original method arguments, the generic can be omitted.
        callOriginal<Any?>()
        // Call the original method with custom method arguments, the generic can be omitted.
        invokeOriginal<Any?>(...)
    }
}

// Call the original implementation of a method outside the hook callback.
method.invokeOriginal(instance, ...)
```

```kotlin [Xposed Framework API]
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // Call the original method with the original method arguments.
        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args)
        // Call the original method with custom method arguments.
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, arrayOf(...))
    }
}

// Call the original implementation of a method outside the hook callback.
XposedBridge.invokeOriginalMethod(method, instance, arrayOf(...))
```

:::

For details, please refer to [Hook Usage → Call the Original Method](../../library/yukihook-core/hook-usage.md#call-the-original-method).

### Hook Priority

> The following example

::: code-group

```kotlin [YukiHook]
hook(priority = YukiHookPriority.HIGHEST) {
    // Your code here.
}
```

```kotlin [Xposed Framework API]
object : XC_MethodHook(XCallback.PRIORITY_HIGHEST) {
    // Your code here.
}
```

:::

### Remove Hooks

> The following example

::: code-group

```kotlin [YukiHook]
// Save the current instance and call it at the right time.
val handle = method.hook {
    after {
        // Remove itself in the callback.
        removeSelf()
    }
}

handle.remove()
```

```kotlin [Xposed Framework API]
// Save the current instance and call it at the right time.
var unhook: XC_MethodHook.Unhook? = null
unhook = XposedBridge.hookMethod(
    method, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Remove itself in the callback, using the instance saved above.
            unhook?.unhook()
        }
    }
)

unhook?.unhook()
```

:::

For details, please refer to [Hook Usage → Remove Hooks](../../library/yukihook-core/hook-usage.md#remove-hooks).

### Exception Handling

In the Xposed Framework API, exceptions in callbacks are caught by the framework and printed to the logs, while in YukiHook you can listen for these exceptions.

> The following example

::: code-group

```kotlin [YukiHook]
hook {
    after {
        // Throw an exception to the host.
        Throwable("Fatal").throwToHost()
    }
}.apply {
    // Handle exceptions in hook callbacks.
    onCallbackFailure { param, throwable -> }
}
```

```kotlin [Xposed Framework API]
object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        // Throw an exception to the host.
        param.throwable = Throwable("Fatal")
    }
}
```

:::

For details, please refer to [Hook Usage → Exception Handling](../../library/yukihook-core/hook-usage.md#exception-handling).

## Migrate Reflection Usage

This section describes how to migrate the reflection usage in `XposedHelpers` to the Java native reflection API and KavaRef.

::: tip

We recommend [KavaRef](https://github.com/HighCapable/KavaRef) as a modern reflection API,
and YukiHook provides native [extension support](../../library/yukihook-extension-kavaref.md) for it.

:::

The following examples assume they are used in `loadApp` or `handleLoadPackage`, where `clazz` is the `Class` to reflect and `instance` is an instance of it.

### Find Classes

`XposedHelpers.findClass` requires a `ClassLoader`. In `PackageParam`, KavaRef's `toClass` uses the host's `ClassLoader` by default.

> The following example

::: code-group

```kotlin [KavaRef]
// Find a class.
val clazz = "$packageName.Demo".toClass()
// Find a class, returns null if not found.
val clazzOrNull = "$packageName.Demo".toClassOrNull()
// Find the first existing class among multiple class names.
val variousClazz = VariousClass("$packageName.Demo", "$packageName.NewDemo").toClass()
```

```kotlin [Java Native Reflection]
// Find a class.
val clazz = Class.forName("$packageName.Demo", false, hostClassLoader)
// Find a class, returns null if not found.
val clazzOrNull = runCatching {
    Class.forName("$packageName.Demo", false, hostClassLoader)
}.getOrNull()
```

```kotlin [XposedHelpers]
// Find a class.
val clazz = XposedHelpers.findClass("$packageName.Demo", lpparam.classLoader)
// Find a class, returns null if not found.
val clazzOrNull = XposedHelpers.findClassIfExists("$packageName.Demo", lpparam.classLoader)
```

:::

For details, please refer to [yukihook-extension-kavaref → Load Classes from the Host](../../library/yukihook-extension-kavaref.md#load-classes-from-the-host).

### Find and Call Methods

`XposedHelpers.callMethod` matches the method automatically by the given arguments and also searches the `super` classes,
while the Java native reflection API and KavaRef both require the parameter types to be specified explicitly.
`getDeclaredMethod` and KavaRef only find methods declared in the current class by default.
Please refer to the following example if you need to find methods in the superclass.

> The following example

::: code-group

```kotlin [KavaRef]
// Find a method.
val method = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
}
// Find a method, returns null if not found. This needs to be used with `optional()`.
val methodOrNull = clazz.resolve().optional().firstMethodOrNull {
    name = "foo"
    parameters(String::class)
}
// Find a method, also searching the methods in the superclass.
val superMethod = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
    superclass()
}
// Call an instance method.
method.of(instance).invoke("some string")
// Call a static method.
method.invoke("some string")
// Call an instance method and cast the return value to T.
method.of(instance).invoke<T>("some string")
```

```kotlin [Java Native Reflection]
// Find a method, only the methods declared in the current class are searched.
val method = clazz.getDeclaredMethod("foo", String::class.java)
// Find a method, returns null if not found.
val methodOrNull = runCatching {
    clazz.getDeclaredMethod("foo", String::class.java)
}.getOrNull()
// Find a public method, including the public methods in the superclass.
val superMethod = clazz.getMethod("foo", String::class.java)
// The access permission needs to be set before calling.
method.isAccessible = true
// Call an instance method.
method.invoke(instance, "some string")
// Call a static method.
method.invoke(null, "some string")
// Call an instance method and cast the return value to T.
method.invoke(instance, "some string") as T
```

```kotlin [XposedHelpers]
// Find a method, only the methods declared in the current class are searched.
val method = XposedHelpers.findMethodExact(clazz, "foo", String::class.java)
// Find a method, returns null if not found.
val methodOrNull = XposedHelpers.findMethodExactIfExists(clazz, "foo", String::class.java)
// Find a method, also searching the superclass, and assignable parameter types are allowed.
val superMethod = XposedHelpers.findMethodBestMatch(clazz, "foo", String::class.java)
// Find and call an instance method.
XposedHelpers.callMethod(instance, "foo", "some string")
// Find and call a static method.
XposedHelpers.callStaticMethod(clazz, "foo", "some string")
// Find and call an instance method and cast the return value to T.
XposedHelpers.callMethod(instance, "foo", "some string") as T
```

:::

KavaRef handles the access permission of members automatically, so you no longer need to set `isAccessible` manually.

### Find and Access Fields

`XposedHelpers.findField` also searches the fields in the superclass.
In the Java native reflection API and KavaRef, you need to specify this manually.

> The following example

::: code-group

```kotlin [KavaRef]
// Find a field, also searching the fields in the superclass.
val field = clazz.resolve().firstField {
    name = "bar"
    type = String::class
    superclass()
}
// Find a field, returns null if not found. This needs to be used with `optional()`.
val fieldOrNull = clazz.resolve().optional().firstFieldOrNull {
    name = "bar"
    type = String::class
    superclass()
}
// Read an instance field.
field.of(instance).get<String>()
// Modify an instance field.
field.of(instance).set("some string")
// Read a static field.
field.get<String>()
// Modify a static field.
field.set("some string")
```

```kotlin [Java Native Reflection]
// Find a field, you need to traverse the superclasses yourself.
var field: Field? = null
var current: Class<*>? = clazz
while (field == null && current != null) {
    field = runCatching { current.getDeclaredField("bar") }.getOrNull()
    current = current.superclass
}
// The access permission needs to be set before calling.
field?.isAccessible = true
// Read an instance field.
field?.get(instance) as String?
// Modify an instance field.
field?.set(instance, "some string")
// Read a static field.
field?.get(null) as String?
// Modify a static field.
field?.set(null, "some string")
```

```kotlin [XposedHelpers]
// Find a field, also searching the fields in the superclass.
val field = XposedHelpers.findField(clazz, "bar")
// Find a field, returns null if not found.
val fieldOrNull = XposedHelpers.findFieldIfExists(clazz, "bar")
// Read an instance field.
XposedHelpers.getObjectField(instance, "bar") as String?
// Read an instance field, primitive types have their own functions,
// `Int` is used here as an example.
XposedHelpers.getIntField(instance, "count")
// Modify an instance field.
XposedHelpers.setObjectField(instance, "bar", "some string")
// Modify an instance field, primitive types have their own functions,
// `Int` is used here as an example.
XposedHelpers.setIntField(instance, "count", 1)
// Read a static field.
XposedHelpers.getStaticObjectField(clazz, "bar") as String?
// Modify a static field.
XposedHelpers.setStaticObjectField(clazz, "bar", "some string")
```

:::

### Create Instances

`XposedHelpers.newInstance` matches the constructor automatically by the given arguments, and KavaRef's `createInstance` provides the same way.

> The following example

::: code-group

```kotlin [KavaRef]
// Find a constructor.
val constructor = clazz.resolve().firstConstructor {
    parameters(String::class)
}
// Create an instance using the constructor.
constructor.create("some string")
// Match a public constructor automatically by the given arguments and create an instance.
clazz.createInstance("some string")
// Same as above, but non-public constructors are also allowed,
// which is consistent with `XposedHelpers.newInstance`.
clazz.createInstance("some string", isPublic = false)
```

```kotlin [Java Native Reflection]
// Find a constructor.
val constructor = clazz.getDeclaredConstructor(String::class.java)
// The access permission needs to be set before calling.
constructor.isAccessible = true
// Create an instance using the constructor.
constructor.newInstance("some string")
```

```kotlin [XposedHelpers]
// Find a constructor.
val constructor = XposedHelpers.findConstructorExact(clazz, String::class.java)
// Find a constructor, returns null if not found.
val constructorOrNull = XposedHelpers.findConstructorExactIfExists(clazz, String::class.java)
// Create an instance using the constructor.
constructor.newInstance("some string")
// Match a constructor (including non-public ones) automatically by the given arguments and create an instance.
XposedHelpers.newInstance(clazz, "some string")
```

:::

### Find and Hook

`XposedHelpers.findAndHookMethod` and `findAndHookConstructor` hook the member directly after finding it.
In YukiHook, you can use `hook` directly on the found member.

> The following example

::: code-group

```kotlin [KavaRef]
// Hook a method.
clazz.resolve()
    .firstMethod {
        name = "foo"
        parameters(String::class)
    }.hook {
        after {
            // Your code here.
        }
    }
// Hook a constructor.
clazz.resolve()
    .firstConstructor {
        parameters(String::class)
    }.hook {
        after {
            // Your code here.
        }
    }
// Hook all methods with the same name.
clazz.resolve()
    .method { name = "foo" }
    .hookAll {
        after {
            // Your code here.
        }
    }
```

```kotlin [Java Native Reflection]
// Hook a method.
clazz.getDeclaredMethod("foo", String::class.java).hook {
    after {
        // Your code here.
    }
}
// Hook a constructor.
clazz.getDeclaredConstructor(String::class.java).hook {
    after {
        // Your code here.
    }
}
// Hook all methods with the same name.
clazz.declaredMethods.filter { it.name == "foo" }.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [XposedHelpers]
// Hook a method.
XposedHelpers.findAndHookMethod(
    clazz, "foo", String::class.java,
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
// Hook a constructor.
XposedHelpers.findAndHookConstructor(
    clazz, String::class.java,
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
// Hook all methods with the same name.
XposedBridge.hookAllMethods(
    clazz, "foo",
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
```

:::

For details, please refer to [yukihook-extension-kavaref → Hook Usage](../../library/yukihook-extension-kavaref.md#hook-usage).