# 从 Xposed API 迁移

> 若你熟悉 Xposed API，你可以参考下方的相同点将自己的 API 快速迁移到 `YukiHookAPI`。

## 迁移 Hook 入口点

> 从 `XC_LoadPackage.LoadPackageParam` 迁移至 `PackageParam`。

`YukiHookAPI` 对 `PackageParam` 实现了 `lambda` 方法体 `this` 用法，在 `encase` 方法体内即可全局得到 `PackageParam` 对象。

> API 功能差异对比如下

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
override fun onHook() = encase {
    // 得到当前 Hook 的包名
    packageName
    // 得到当前 Hook 的 ApplicationInfo
    appInfo
    // 得到系统上下文对象
    systemContext
    // 得到宿主 Application 生命周期
    appContext
    // Hook 指定的 APP
    loadApp(name = "com.demo.test") {
        // Class Hook
        findClass("com.demo.test.TestClass").hook {
            injectMember {
                method {
                    name = "test"
                    param(BooleanType)
                }
                afterHook {
                    // ...
                }
            }
        }
        // Resources Hook (固定用法)
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
::: code-group-item Xposed API

```kotlin
private lateinit var moduleResources: XModuleResources

override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
    moduleResources = XModuleResources.createInstance(sparam.modulePath, null)
}

override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // 得到当前 Hook 的包名
    lpparam.packageName
    // 得到当前 Hook 的 ApplicationInfo
    lpparam.applicationInfo
    // 得到系统上下文对象
    // 在原生 Xposed API 中没有现成的调用方法，你需要自行反射 ActivityThread 来实现
    // 得到宿主 Application 生命周期
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
    // 得到当前 Hook 的包名
    resparam.packageName
    // Resources Hook
    resparam.res.setReplacement(
        "com.demo.test", "mipmap","ic_launcher",
        moduleResources.fwd(R.mipmap.ic_launcher)
    )
}
```

:::
::::

## 迁移 Hook 方法体

> 从 `XC_MethodHook.MethodHookParam` 迁移至 `HookParam`。

### Before/After Hook

`YukiHookAPI` 同样对 `HookParam` 实现了 `lambda` 方法体 `this` 用法，在 `beforeHook`、`afterHook` 等方法体内即可全局得到 `HookParam` 对象。

> API 功能差异对比如下

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
afterHook {
    // 得到当前 Hook 的实例
    instance
    // 得到当前 Hook 的 Class 实例
    instanceClass
    // 得到并 cast 当前 Hook 的实例为指定类型 T
    instance<T>()
    // 得到方法参数数组
    args
    // 得到方法参数的第一位 T
    args().first().cast<T>()
    // 得到方法参数的最后一位 T
    args().last().cast<T>()
    // 得到方法参数的任意下标 T，这里用 2 举例
    args(index = 2).cast<T>()
    // 设置方法参数的任意下标，这里用 2 举例
    args(index = 2).set(...)
    // 得到返回值
    result
    // 得到返回值并 cast 为 T
    result<T>()
    // 修改返回值内容
    result = ...
    // 删除返回值内容
    resultNull()
    // 向 Hook APP 抛出异常
    Throwable("Fatal").throwToApp()
    // 执行未经 Hook 的原始方法并使用原始方法参数调用，泛型可略
    callOriginal<Any?>()
    // 执行未经 Hook 的原始方法并自定义方法参数调用，泛型可略
    invokeOriginal<Any?>(...)
}
```

:::
::: code-group-item Xposed API

```kotlin
override fun afterHookedMethod(param: MethodHookParam) {
    // 得到当前 Hook 的实例
    param.thisObject
    // 得到当前 Hook 的 Class 实例
    param.thisObject.javaClass
    // 得到并 cast 当前 Hook 的实例为指定类型 T
    param.thisObject as T
    // 得到方法参数数组
    param.args
    // 得到方法参数的第一位 T
    param.args[0] as T
    // 得到方法参数的最后一位 T
    param.args[param.args.lastIndex] as T
    // 得到方法参数的任意下标 T，这里用 2 举例
    param.args[2] as T
    // 设置方法参数的任意下标，这里用 2 举例
    param.args[2] = ...
    // 得到返回值
    param.result
    // 得到返回值并 cast 为 T
    param.result as T
    // 修改返回值内容
    param.result = ...
    // 删除返回值内容
    param.result = null
    // 向 Hook APP 抛出异常
    param.throwable = Throwable("Fatal")
    // 执行未经 Hook 的原始方法
    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, ...)
}
```

:::
::::

### Replace Hook

`replaceHook` 方法比较特殊，`YukiHookAPI` 为它做出了多种形式以供选择。

> API 功能差异对比如下

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
/// 无返回值的方法 void

replaceUnit {
    // 直接在这里实现被替换的逻辑
}

/// 有返回值的方法

replaceAny {
    // 在这里实现被替换的逻辑
    // ...
    // 需要返回方法对应的返回值，无需写 return，只需将参数放到最后一位
    // 假设这个方法的返回值是 Int，我们只需要保证最后一位是我们需要的返回值即可
    0
}

/// 有些方法我们只需替换其返回值，则有如下实现
/// 需要注意的是：直接替换返回值的方法传入的参数是固定不变的，若想实现动态替换返回值请使用上面的 replaceAny 方法体

// 替换为你需要的返回值
replaceTo(...)
// 替换为 Boolean 类型的返回值
replaceToTrue()
// 拦截返回值
intercept()
```

:::
::: code-group-item Xposed API

```kotlin
/// 无返回值的方法 void

override fun replaceHookedMethod(param: MethodHookParam): Any? {
    // 直接在这里实现被替换的逻辑
    return null
}

/// 有返回值的方法

override fun replaceHookedMethod(param: MethodHookParam): Int {
    // 在这里实现被替换的逻辑
    // ...
    // 假设这个方法的返回值是 Int
    return 0
}

/// 有些方法我们只需替换其返回值，则有如下实现

// 替换为你需要的返回值
override fun replaceHookedMethod(param: MethodHookParam) = ...
// 替换为 Boolean 类型的返回值
override fun replaceHookedMethod(param: MethodHookParam) = true
// 拦截返回值
override fun replaceHookedMethod(param: MethodHookParam) = null
```

:::
::::

## 迁移其它功能

`YukiHookAPI` 对 Xposed API 进行了完全重写，你可以参考 [API 文档](../api/home) 以及 [特色功能](../api/special-features/reflection) 来决定一些功能性的迁移和使用。