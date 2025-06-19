# 从其它 Hook API 迁移

此文档可以帮助你快速从你熟悉的 Hook API 迁移至 `YukiHookAPI` 来熟悉对 `YukiHookAPI` 的相关写法。

## Rovo89 Xposed API

> 若你熟悉 [Rovo89 Xposed API](https://api.xposed.info/)，你可以参考下方的相同点将自己的 API 快速迁移至 `YukiHookAPI`。

### 迁移 Hook 入口点

> 从 `XC_LoadPackage.LoadPackageParam` 迁移至 `PackageParam`。

`YukiHookAPI` 对 `PackageParam` 实现了 **lambda** 方法体 `this` 用法，在 `encase` 方法体内即可全局得到 `PackageParam` 对象。

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
        // Member Hook
        "com.demo.test.TestClass".toClass()
            .resolve()
            .firstMethod {
                name = "test"
                parameters(Boolean::class)
            }.hook {
                after {
                    // ...
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
::: code-group-item Rovo89 Xposed API

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
    // 在 Rovo89 Xposed API 中没有现成的调用方法，你需要自行反射 ActivityThread 来实现
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

### 迁移 Hook 方法体

> 从 `XC_MethodHook.MethodHookParam` 迁移至 `HookParam`。

#### Before/After Hook

`YukiHookAPI` 同样对 `HookParam` 实现了 **lambda** 方法体 `this` 用法，在 `before`、`after` 等方法体内即可全局得到 `HookParam` 对象。

> API 功能差异对比如下

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
after {
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
    // 获取当前回调方法体范围内的数据存储实例
    dataExtra
    // 向 Hook APP 抛出异常
    Throwable("Fatal").throwToApp()
    // 执行未经 Hook 的原始方法并使用原始方法参数调用，泛型可略
    callOriginal<Any?>()
    // 执行未经 Hook 的原始方法并自定义方法参数调用，泛型可略
    invokeOriginal<Any?>(...)
}
```

:::
::: code-group-item Rovo89 Xposed API

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
    // 获取当前回调方法体范围内的数据存储实例
    param.extra
    // 向 Hook APP 抛出异常
    param.throwable = Throwable("Fatal")
    // 执行未经 Hook 的原始方法
    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, ...)
}
```

:::
::::

#### Replace Hook

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
::: code-group-item Rovo89 Xposed API

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

### 迁移 XposedHelpers 注意事项

`YukiHookAPI` 中提供的反射功能与 `XposedHelpers` 的反射功能有所不同，这里提供一个误区指引。

`XposedHelpers.callMethod`、`XposedHelpers.callStaticMethod` 等方法自动查找的方法会自动调用所有公开的方法 (包括 `super` 超类)，这是 Java 原生反射的特性，
而 `YukiHookAPI` 提供的反射方案为先反射查找再调用，而查找过程默认不会自动查找 `super` 超类的方法。

::: warning

`YukiHookAPI` 自身的反射 API 在 `1.3.0` 版本已被弃用，以下内容仅作为 `1.3.0` 版本之前的迁移指引，我们会对其进行保留但不会再进行内容的更新。

你可以迁移至 [KavaRef](https://github.com/HighCapable/KavaRef)，`KavaRef` 同样适用此特性。

:::

例如，类 `A` 继承于 `B`， `B` 中存在公开的方法 `test`，而 `A` 中并不存在。

```java
public class B {
    public void test(String a) {
        // ...
    }
}

public class A extends B {
    // ...
}
```

此时 `XposedHelpers` 的用法。

```kotlin
val instance: A = ...
XposedHelpers.callMethod(instance, "test", "some string")
```

`YukiHookAPI` 的用法。

```kotlin
val instance: A = ...
instance.current().method {
    name = "test"
    // 请注意，这里需要添加此查找条件以确保其会查找超类的方法
    superClass()
}.call("some string")
// 或者直接调用 superClass() 方法
instance.current().superClass()?.method {
    name = "test"
}?.call("some string")
```

## 迁移更多有关 Hook API 的功能

`YukiHookAPI` 是一套全新的 Hook API，与其它 Hook API 存在着本质区别，你可以参考 [API 文档](../api/home) 以及 [特色功能](../api/special-features/reflection) 来决定一些功能性的迁移和使用。