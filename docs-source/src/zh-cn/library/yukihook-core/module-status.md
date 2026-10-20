# 模块状态

> `YukiHook` 封装了 Xposed 模块的状态判断，你可以直接获取模块的激活状态、Hook 框架信息并管理模块作用域。

## 激活状态

在 YukiHook 中，你只需要一行 `YukiHook.Status.isModuleActive` 即可在模块中判断自身是否被激活。

> 示例如下

```kotlin
if (YukiHook.Status.isModuleActive) {
    // Your code here.
}
```

::: warning

激活状态并不代表模块已经在某个指定的宿主中生效，它不会检查模块的作用域以及 Hook 是否已经注册。

:::

## Hook 框架信息

除了判断自身激活状态之外，你还可以通过 `YukiHook.Status` 中的 `Executor` 来获取当前 Hook 框架的相关信息。

> 示例如下

```kotlin
// 获取当前 Hook 框架的名称
YukiHook.Status.Executor.name
// 获取当前 Hook 框架的 API 版本
YukiHook.Status.Executor.apiVersion
// 获取当前 Hook 框架的版本名称
YukiHook.Status.Executor.version
// 获取当前 Hook 框架的版本号
YukiHook.Status.Executor.versionCode
```

你还可以得到当前的运行环境与运行时 (底座)。

> 示例如下

```kotlin
// 当前使用的运行时
YukiHook.Status.runtime
// 当前模块的编译时间戳
YukiHook.Status.compiledTimestamp
// 当前是否处于宿主环境
YukiHook.Status.isHostEnvironment
```

::: warning

在 [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) 底座中，由于 libxposed 被设计为不再 Hook 模块自身而是与 Hook 框架服务通过 AIDL 交互，因此模块的状态准确性取决于服务连接是否成功。

由于 Hook 框架服务的连接是异步的，我们建议你在上述获取 Hook 框架信息的代码中首先参考下方的 [Hook 框架服务状态](#hook-框架服务状态) 以确保服务可用。

:::

## Hook 框架服务状态

你可以使用 `observeFrameworkService` 监听 Hook 框架服务的可用状态，监听的回调保持运行在主线程。

> 示例如下

```kotlin
YukiHook.Status.observeFrameworkService { available ->
    if(!available) return@observeFrameworkService

    val isActive = YukiHook.Status.isModuleActive
    val executorName = YukiHook.Status.Executor.name
    // ...
}
```

监听器会在注册时立即被回调一次，并在服务连接或断开时再次回调。

你可以对方法传入 `Lifecycle` 以自动注册生命周期监听，或者你也可以对返回的对象调用 `unregister` 手动取消监听。

::: danger

此功能需要 [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) 底座支持。

:::

## 模块作用域

你可以在模块中使用 `YukiHookModuleScope` 查询与请求模块的作用域。

> 示例如下

```kotlin
val scope = YukiHook.Module.scope
if (scope.isAvailable) {
    // 获取当前模块的作用域，系统框架为 "system"
    val packages = scope.packages
    // 请求添加作用域，可同时请求多个包名
    scope.request("com.android.settings") { approved, errorMessage, packages ->
        // 回调运行在主线程
    }
    // 移除作用域
    scope.remove("com.android.settings")
}
```

修改作用域后，你需要重启受影响的宿主进程才能使其生效。

::: danger

此功能需要 [yukihook-runtime-libxposed](../yukihook-runtime-libxposed.md) 底座支持。

:::