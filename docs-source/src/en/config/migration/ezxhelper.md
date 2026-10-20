# EzXHelper

> Here describes how to migrate from [EzXHelper](https://github.com/KyuubiRan/EzXHelper) to `YukiHook`.

EzXHelper is not a complete hook framework. It is a helper library based on the Xposed API, and the module entry is still provided by the Xposed API.

This page compares the common usages by the major versions of EzXHelper.
The usage of `2.x` and `3.x` is basically the same, only the package names and the initialization classes are different,
so they are shown together below. For more usages, please refer to the detailed documentation of the corresponding chapters.

::: tip

The `xposed-api-101` of EzXHelper `3.x` corresponds to the libxposed API, which is also supported by YukiHook. Please refer to [libxposed API](./libxposed-api.md).

:::

## Migrate the Module Entry

### Initialization

EzXHelper requires manual initialization in the entry, while YukiHook does not. You can use everything directly once you are in the scope.

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

```kotlin [EzXHelper 3.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXposed.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [EzXHelper 2.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelper.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

```kotlin [EzXHelper 1.x]
class HookEntry : IXposedHookLoadPackage {

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        EzXHelperInit.initHandleLoadPackage(lpparam)

        if (lpparam.packageName == "io.github.myapp") {
            // Your code here.
        }
    }
}
```

:::

For details, please refer to [Module Entry → Create the Entry Class](../../library/yukihook-core/hook-entry.md#create-the-entry-class).

### Get Host Information

In EzXHelper, information such as the `ClassLoader` and the `Context` is provided by global properties after initialization,
while YukiHook provides it directly in the scope.

> The following example

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // Get the package name of the current host.
    packageName
    // Get the `ClassLoader` of the current host.
    hostClassLoader
    // Get the `Application` of the current host, which may be null during the first load.
    hostApplication
}
```

```kotlin [EzXHelper 3.x]
// Get the package name of the current host.
EzXposed.hookedPackageName
// Get the `ClassLoader` of the current host.
ClassLoaderProvider.classLoader
// Get the `Context` of the current host.
EzXposed.appContext
```

```kotlin [EzXHelper 2.x]
// Get the package name of the current host.
EzXHelper.hostPackageName
// Get the `ClassLoader` of the current host.
EzXHelper.classLoader
// Get the `Context` of the current host.
EzXHelper.appContext
```

```kotlin [EzXHelper 1.x]
// Get the package name of the current host.
hostPackageName
// Get the `ClassLoader` of the current host.
ezXClassLoader
// Get the `Context` of the current host.
appContext
```

:::

For details, please refer to [Module Entry → Host Information](../../library/yukihook-core/hook-entry.md#host-information).

## Migrate Hook Usage

### Basic Usage

EzXHelper hooks through the extension functions of `Method` and `Constructor`, and YukiHook also uses `hook`.
The `param` in the callbacks corresponds to `HookParam`.

> The following example

::: code-group

```kotlin [YukiHook]
method.hook {
    before {
        // Get the first argument as T.
        firstArg().get<T>()
    }
    after {
        // Get the instance of the current hook.
        instance
        // Get the return value.
        result
    }
}
```

```kotlin [EzXHelper 2.x / 3.x]
method.createHook {
    before { param ->
        // Get the first argument as T.
        param.args[0] as T
    }
    after { param ->
        // Get the instance of the current hook.
        param.thisObject
        // Get the return value.
        param.result
    }
}
```

```kotlin [EzXHelper 1.x]
method.hookBefore { param ->
    // Get the first argument as T.
    param.args[0] as T
}
method.hookAfter { param ->
    // Get the instance of the current hook.
    param.thisObject
    // Get the return value.
    param.result
}
```

:::

For details, please refer to [Hook Usage → Basic Usage](../../library/yukihook-core/hook-usage.md#basic-usage).

### Intercept the Method

`replace`, `returnConstant` and `interrupt` in EzXHelper are all replaced by `intercept` in YukiHook.

> The following example

::: code-group

```kotlin [YukiHook]
// Replace the logic of the whole method.
method.hook {
    intercept {
        // The return value of the method needs to be returned.
        0
    }
}
// Replace with a fixed return value.
method.hook {
    intercept(true)
}
// Intercept the method and return null.
method.hook {
    intercept()
}
```

```kotlin [EzXHelper 2.x / 3.x]
// Replace the logic of the whole method.
method.createHook {
    replace { param ->
        // The return value of the method needs to be returned.
        0
    }
}
// Replace with a fixed return value.
method.createHook {
    returnConstant(true)
}
// Intercept the method and return null.
method.createHook {
    interrupt()
}
```

```kotlin [EzXHelper 1.x]
// Replace the logic of the whole method.
method.hookReplace { param ->
    // The return value of the method needs to be returned.
    0
}
// Replace with a fixed return value.
method.hookReturnConstant(true)
```

:::

For details, please refer to [Hook Usage → Intercept the Method](../../library/yukihook-core/hook-usage.md#intercept-the-method).

### Hook Multiple Members

EzXHelper can hook a collection of `Method` or `Constructor` directly, and YukiHook uses `hookAll`.

> The following example

::: code-group

```kotlin [YukiHook]
methods.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [EzXHelper 2.x / 3.x]
methods.createHooks {
    after { param ->
        // Your code here.
    }
}
```

```kotlin [EzXHelper 1.x]
methods.hookAfter { param ->
    // Your code here.
}
```

:::

For details, please refer to [Hook Usage → Basic Usage](../../library/yukihook-core/hook-usage.md#basic-usage).

### Remove Hooks

EzXHelper returns `XC_MethodHook.Unhook`, while YukiHook returns `Handle`.

> The following example

::: code-group

```kotlin [YukiHook]
val handle = method.hook {
    after {
        // Your code here.
    }
}

handle.remove()
```

```kotlin [EzXHelper 2.x / 3.x]
val unhook = method.createHook {
    after { param ->
        // Your code here.
    }
}

unhook.unhook()
```

```kotlin [EzXHelper 1.x]
val unhook = method.hookAfter { param ->
    // Your code here.
}

unhook.unhook()
```

:::

For details, please refer to [Hook Usage → Remove Hooks](../../library/yukihook-core/hook-usage.md#remove-hooks).

## Migrate Reflection Usage

This section describes how to migrate the reflection usage in EzXHelper to the Java native reflection API and KavaRef.

::: tip

We recommend [KavaRef](https://github.com/HighCapable/KavaRef) as a modern reflection API,
and YukiHook provides native [extension support](../../library/yukihook-extension-kavaref.md) for it.

:::

The following examples assume that `clazz` is the `Class` to reflect and `instance` is an instance of it.

### Find Classes

> The following example

::: code-group

```kotlin [KavaRef]
// Find a class.
val clazz = "io.github.myapp.Demo".toClass()
// Find a class, returns null if not found.
val clazzOrNull = "io.github.myapp.Demo".toClassOrNull()
// Find the first existing class among multiple class names.
val variousClazz = VariousClass("io.github.myapp.Demo", "io.github.myapp.NewDemo").toClass()
```

```kotlin [Java Native Reflection]
// Find a class.
val clazz = Class.forName("io.github.myapp.Demo", false, hostClassLoader)
// Find a class, returns null if not found.
val clazzOrNull = runCatching {
    Class.forName("io.github.myapp.Demo", false, hostClassLoader)
}.getOrNull()
```

```kotlin [EzXHelper 3.x]
// Find a class.
val clazz = ClassUtil.loadClass("io.github.myapp.Demo")
// Find a class, returns null if not found.
val clazzOrNull = ClassUtil.loadClassOrNull("io.github.myapp.Demo")
// Find the first existing class among multiple class names.
val variousClazz = ClassUtil.loadFirstClass("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

```kotlin [EzXHelper 2.x]
// Find a class.
val clazz = ClassUtils.loadClass("io.github.myapp.Demo")
// Find a class, returns null if not found.
val clazzOrNull = ClassUtils.loadClassOrNull("io.github.myapp.Demo")
// Find the first existing class among multiple class names.
val variousClazz = ClassUtils.loadFirstClass("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

```kotlin [EzXHelper 1.x]
// Find a class.
val clazz = loadClass("io.github.myapp.Demo")
// Find a class, returns null if not found.
val clazzOrNull = loadClassOrNull("io.github.myapp.Demo")
// Find the first existing class among multiple class names.
val variousClazz = loadClassAny("io.github.myapp.Demo", "io.github.myapp.NewDemo")
```

:::

For details, please refer to [yukihook-extension-kavaref → Load Classes from the Host](../../library/yukihook-extension-kavaref.md#load-classes-from-the-host).

### Find Methods and Constructors

> The following example

::: code-group

```kotlin [KavaRef]
// Find a method.
val method = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
}
// Find a constructor.
val constructor = clazz.resolve().firstConstructor {
    parameters(String::class)
}
```

```kotlin [Java Native Reflection]
// Find a method.
val method = clazz.getDeclaredMethod("foo", String::class.java)
// Find a constructor.
val constructor = clazz.getDeclaredConstructor(String::class.java)
```

```kotlin [EzXHelper 2.x / 3.x]
// Find a method.
val method = MethodFinder.fromClass(clazz)
    .filterByName("foo")
    .filterByParamTypes(String::class.java)
    .first()
// Find a constructor.
val constructor = ConstructorFinder.fromClass(clazz)
    .filterByParamTypes(String::class.java)
    .first()
```

```kotlin [EzXHelper 1.x]
// Find a method.
val method = clazz.findMethod {
    name == "foo" && parameterTypes.contentEquals(arrayOf(String::class.java))
}
// Find a constructor.
val constructor = clazz.findConstructor {
    parameterTypes.contentEquals(arrayOf(String::class.java))
}
```

:::

### Access Fields and Call Methods

> The following example

::: code-group

```kotlin [KavaRef]
// Find a field.
val field = clazz.resolve().firstField {
    name = "bar"
    type = String::class
}
// Read an instance field.
field.of(instance).get<String>()
// Modify an instance field.
field.of(instance).set("some string")
// Call an instance method.
method.of(instance).invoke("some string")
// Call a static method.
method.invoke("some string")
```

```kotlin [Java Native Reflection]
// Find a field.
val field = clazz.getDeclaredField("bar")
// The access permission needs to be set before calling.
field.isAccessible = true
method.isAccessible = true
// Read an instance field.
field.get(instance) as String?
// Modify an instance field.
field.set(instance, "some string")
// Call an instance method.
method.invoke(instance, "some string")
// Call a static method.
method.invoke(null, "some string")
```

```kotlin [EzXHelper 2.x / 3.x]
// Find a field.
val field = FieldFinder.fromClass(clazz)
    .filterByName("bar")
    .first()
// Read an instance field.
instance.objectHelper().getObjectOrNull("bar")
// Modify an instance field.
instance.objectHelper().setObject("bar", "some string")
// Call an instance method.
instance.objectHelper().invokeMethodBestMatch("foo", null, "some string")
// Call a static method.
clazz.classHelper().invokeStaticMethodBestMatch("foo", null, "some string")
```

```kotlin [EzXHelper 1.x]
// Find a field.
val field = clazz.findField {
    name == "bar"
}
// Read an instance field.
instance.getObjectOrNull("bar")
// Modify an instance field.
instance.putObject("bar", "some string")
// Call an instance method.
instance.invokeMethod("foo", args("some string"), argTypes(String::class.java))
// Call a static method.
clazz.invokeStaticMethod("foo", args("some string"), argTypes(String::class.java))
```

:::

### Create Instances

> The following example

::: code-group

```kotlin [KavaRef]
// Create an instance using the constructor.
constructor.create("some string")
// Match a public constructor automatically by the given arguments and create an instance.
clazz.createInstance("some string")
```

```kotlin [Java Native Reflection]
// The access permission needs to be set before calling.
constructor.isAccessible = true
// Create an instance using the constructor.
constructor.newInstance("some string")
```

```kotlin [EzXHelper 2.x / 3.x]
// Create an instance using the constructor.
constructor.newInstance("some string")
// Match a constructor automatically by the given arguments and create an instance.
clazz.classHelper().newInstanceBestMatch("some string")
```

```kotlin [EzXHelper 1.x]
// Create an instance using the constructor.
constructor.newInstance("some string")
// Create an instance by the given arguments.
clazz.newInstance(args("some string"), argTypes(String::class.java))
```

:::