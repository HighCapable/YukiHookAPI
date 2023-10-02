# Migrate from Other Hook APIs

This document can help you quickly migrate from the Hook APIs you are familiar with to `YukiHookAPI` to become familiar with the related writing methods of `YukiHookAPI`.

## Rovo89 Xposed API

> If you are familiar with [Rovo89 Xposed API](https://api.xposed.info/), you can refer to the same point below to quickly migrate your API to `YukiHookAPI`.

### Migrate Hook Entry Point

> Migrated from `XC_LoadPackage.LoadPackageParam` to `PackageParam`.

`YukiHookAPI` implements the **lambda** method body `this` usage for `PackageParam`, and the `PackageParam` object can be obtained globally in the `encase` method body.

> The API function differences are compared as follows

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
override fun onHook() = encase {
    // Get the package name of the current Hook
    packageName
    // Get the ApplicationInfo of the current Hook
    appInfo
    // Get the system context object
    systemContext
    // Get the host Application lifecycle
    appContext
    // Hook specified app
    loadApp(name = "com.demo.test") {
        // Member Hook
        "com.demo.test.TestClass".toClass()
            .method {
                name = "test"
                param(BooleanType)
            }.hook {
                after {
                    // ...
                }
            }
        // Resources Hook (fixed usage)
        resources().hook {
            injectResource {
                conditions {
                    name = "ic_launcher"
                    mipmap()
                }
                replaceToModuleResource(R.mipmap.ic_launcher)
            }
        }
    }
}
```

:::
::: code-group-item Rovo89 Xposed API

```kotlin
private lateinit var moduleResources: XModuleResources

override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
    moduleResources = XModuleResources.createInstance(sparam.modulePath, null)
}

override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // Get the package name of the current Hook
    lpparam.packageName
    // Get the ApplicationInfo of the current Hook
    lpparam.applicationInfo
    // Get the system context object
    // There is no ready-made calling method in the Rovo89 Xposed API, you need to reflect ActivityThread to achieve it
    // Get the host Application lifecycle
    AndroidAppHelper.currentApplication()
    // Class Hook
    if(lpparam.packageName == "com.demo.test")
        XposedHelpers.findAndHookMethod(
            "com.demo.test.TestClass", lpparam.classLoader,
            "test", Boolean::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    // ...
                }
            }
        )
}

override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
    // Get the package name of the current Hook
    resparam.packageName
    // Resources Hook
    resparam.res.setReplacement(
        "com.demo.test", "mipmap", "ic_launcher",
        moduleResources.fwd(R.mipmap.ic_launcher)
    )
}
```

:::
::::

### Migrate Hook Method Body

> Migrated from `XC_MethodHook.MethodHookParam` to `HookParam`.

#### Before/After Hook

`YukiHookAPI` also implements the **lambda** method body `this` usage for `HookParam`, and the `HookParam` object can be obtained globally in the method bodies such as `before` and `after`.

> The API function differences are compared as follows

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
after {
    // Get the current Hook instance
    instance
    // Get the Class instance of the current Hook
    instanceClass
    // Get and cast the current Hook instance to the specified type T
    instance<T>()
    // Get the method parameter array
    args
    // Get the first T of the method parameter
    args().first().cast<T>()
    // Get the last bit of the method parameter T
    args().last().cast<T>()
    // Get any subscript T of the method parameter, here is an example of 2
    args(index = 2).cast<T>()
    // Set any subscript of the method parameter, here is an example of 2
    args(index = 2).set(...)
    // Get the return value
    result
    // Get the return value and cast to T
    result<T>()
    // Modify the content of the return value
    result = ...
    // Remove the content of the return value
    resultNull()
    // Get the data storage instance within the scope of the current callback method body
    dataExtra
    // Throw an exception to the Hook app
    Throwable("Fatal").throwToApp()
    // Execute the original method without hook and call with the original method parameters, generics can be omitted
    callOriginal<Any?>()
    // Execute the original method without Hook and customize the method parameter call, the generic type can be omitted
    invokeOriginal<Any?>(...)
}
```

:::
::: code-group-item Rovo89 Xposed API

```kotlin
override fun afterHookedMethod(param: MethodHookParam) {
    // Get the current Hook instance
    param.thisObject
    // Get the Class instance of the current Hook
    param.thisObject.javaClass
    // Get and cast the current Hook instance to the specified type T
    param.thisObject as T
    // Get the method parameter array
    param.args
    // Get the first T of the method parameter
    param.args[0] as T
    // Get the last bit of the method parameter T
    param.args[param.args.lastIndex] as T
    // Get any subscript T of the method parameter, here is an example of 2
    param.args[2] as T
    // Set any subscript of the method parameter, here is an example of 2
    param.args[2] = ...
    // Get the return value
    param.result
    // Get the return value and cast to T
    param.result as T
    // Modify the content of the return value
    param.result = ...
    // Remove the content of the return value
    param.result = null
    // Get the data storage instance within the scope of the current callback method body
    param.extra
    // Throw an exception to the Hook app
    param.throwable = Throwable("Fatal")
    // Execute the original method without hooking
    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, ...)
}
```

:::
::::

#### Replace Hook

The `replaceHook` method is special, and the `YukiHookAPI` makes a variety of forms for it to choose from.

> The API function differences are compared as follows

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
/// A method with no return value void

replaceUnit {
    // Implement the replaced logic directly here
}

/// A method with a return value

replaceAny {
    // Implement the replaced logic here
    // ...
    // Need to return the return value corresponding to the method, no need to write return, just put the parameter in the last digit
    // Assuming the return value of this method is an Int, we just need to ensure that the last bit is the return value we need
    0
}

/// For some methods, we just need to replace their return value, then there are the following implementations
/// It should be noted that the parameters passed in by the method of directly replacing the return value are fixed. If you want to dynamically replace the return value, please use the above replaceAny method body

// Replace with the return value you need
replaceTo(...)
// Replace with return value of type Boolean
replaceToTrue()
// Intercept return value
intercept()
```

:::
::: code-group-item Rovo89 Xposed API

```kotlin
/// A method with no return value void

override fun replaceHookedMethod(param: MethodHookParam): Any? {
    // Implement the replaced logic directly here
    return null
}

/// A method with a return value

override fun replaceHookedMethod(param: MethodHookParam): Int {
    // Implement the replaced logic here
    // ...
    // Assume the return value of this method is an Int
    return 0
}

/// For some methods, we just need to replace their return value, then there are the following implementations

// Replace with the return value you need
override fun replaceHookedMethod(param: MethodHookParam) = ...
// Replace with return value of type Boolean
override fun replaceHookedMethod(param: MethodHookParam) = true
// Intercept return value
override fun replaceHookedMethod(param: MethodHookParam) = null
```

:::
::::

## Migrate More Functions Related to Hook API

`YukiHookAPI` is a brand new Hook API, which is fundamentally different from other Hook APIs, you can refer to [API Document](../api/home) and [Special Features](../api/special-features/reflection) to determine some functional Migration and use.