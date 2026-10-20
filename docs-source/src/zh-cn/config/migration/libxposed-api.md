# libxposed API

> 这里介绍了如何从 [libxposed API](https://github.com/libxposed/api) 迁移至 `YukiHook`。

## 迁移模块入口

### 创建入口类

> 示例如下

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

`onHook` 会在开始装载 Hook 时被调用，对应 `onPackageReady` 与 `onSystemServerStarting` 等装载回调，每一个被 Hook 的进程都会调用一次。

详情请参考 [模块入口 → 创建入口类](../../library/yukihook-core/hook-entry.md#创建入口类)。

::: tip

如果你需要使用原生的框架入口或事件，请参考底座的 [原生模块入口](../../library/yukihook-runtime-libxposed.md#原生模块入口) 与 [框架事件](../../library/yukihook-runtime-libxposed.md#框架事件)。

:::

### 处理作用域

在 libxposed API 中，你需要在 `onPackageReady` 中自行判断包名，并在 `onSystemServerStarting` 中处理系统框架，YukiHook 提供了对应的作用域方法。

> 示例如下

::: code-group

```kotlin [YukiHook]
override fun onHook() = encase {
    // Hook 指定宿主
    loadApp("io.github.myapp") {
        // Your code here.
    }
    // Hook 多个宿主
    loadApp("io.github.myapp", "io.github.anotherapp") {
        // Your code here.
    }
    // Hook 系统框架
    loadSystem {
        // Your code here.
    }
}
```

```kotlin [libxposed API]
override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    // Hook 指定宿主
    if (param.packageName == "io.github.myapp") {
        // Your code here.
    }
    // Hook 多个宿主
    if (param.packageName == "io.github.myapp" || param.packageName == "io.github.anotherapp") {
        // Your code here.
    }
}

// Hook 系统框架
override fun onSystemServerStarting(param: XposedModuleInterface.SystemServerStartingParam) {
    // Your code here.
}
```

:::

如果你需要区分宿主的进程，可以使用 `withProcess`。

> 示例如下

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // Hook 主进程
    withProcess(mainProcessName) {
        // Your code here.
    }
    // Hook 其他进程
    withProcess("$packageName:tool") {
        // Your code here.
    }
}
```

```kotlin [libxposed API]
private var processName = ""

override fun onModuleLoaded(param: XposedModuleInterface.ModuleLoadedParam) {
    // 得到当前进程名
    processName = param.processName
}

override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    if (param.packageName == "io.github.myapp") {
        // Hook 主进程
        if (processName == param.packageName) {
            // Your code here.
        }
        // Hook 其他进程
        if (processName == "${param.packageName}:tool") {
            // Your code here.
        }
    }
}
```

:::

如果你需要在宿主的 `Application` 创建完成后再执行 Hook，libxposed API 需要手动 Hook `Application.onCreate`，YukiHook 提供了 `withApplicationReady`。

> 示例如下

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

详情请参考 [模块入口 → 处理作用域](../../library/yukihook-core/hook-entry.md#处理作用域)。

### 获取宿主信息

> 示例如下

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    // 得到当前宿主的包名
    packageName
    // 得到当前宿主的进程名
    processName
    // 得到当前宿主的主进程名
    mainProcessName
    // 得到当前宿主是否为当前进程中的第一个 `Application`
    isFirstApplication
    // 得到当前宿主的 `ApplicationInfo`
    appInfo
    // 得到当前宿主的 `ClassLoader`
    hostClassLoader
    // 得到当前宿主的 `Application`，初次装载时可能为空
    hostApplication
    // 得到当前 Xposed 模块的 `ApplicationInfo`
    module.appInfo
}
```

```kotlin [libxposed API]
override fun onPackageReady(param: XposedModuleInterface.PackageReadyParam) {
    if (param.packageName == "io.github.myapp") {
        // 得到当前宿主的包名
        param.packageName
        // 得到当前宿主的进程名，需要在 onModuleLoaded 中保存
        processName
        // 得到当前宿主的主进程名，需要自行判断，这里使用包名
        param.packageName
        // 得到当前宿主是否为当前进程中的第一个 `Application`
        param.isFirstPackage
        // 得到当前宿主的 `ApplicationInfo`
        param.applicationInfo
        // 得到当前宿主的 `ClassLoader`
        param.classLoader
        // 没有直接对应的 API，请参考上方的 `Application.onCreate` Hook
        // 得到当前 Xposed 模块的 `ApplicationInfo`
        moduleApplicationInfo
    }
}
```

:::

详情请参考 [模块入口 → 宿主信息](../../library/yukihook-core/hook-entry.md#宿主信息)。

## 迁移 Hook 用法

### 基本用法

假设，我们要 Hook `io.github.myapp` 中每个 `Activity` 的 `onCreate` 方法在其执行后弹出一个对话框。

> 示例如下

::: code-group

```kotlin [YukiHook]
loadApp("io.github.myapp") {
    val activityClass = Activity::class.java
    // 使用 Java 反射方案得到 onCreate 方法
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    onCreate.intercept {
        // 先执行原始方法
        val result = proceed()
        // 得到当前 Hook 方法所在的实例
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
    // 使用 Java 反射方案得到 onCreate 方法
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    hook(onCreate).intercept { chain ->
        // 先执行原始方法
        val result = chain.proceed()
        // 得到当前 Hook 方法所在的实例
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

至此，`onCreate` 方法将被成功 Hook 并在 `io.github.myapp` 中的每个 `Activity` 启动时弹出此对话框。

如果你需要同时 Hook 多个方法或构造方法，可以使用 `interceptAll`。

> 示例如下

::: code-group

```kotlin [YukiHook]
// 假设这就是你需要 Hook 的类
val clazz: Class<*>

// Hook 构造方法
clazz.getDeclaredConstructor(String::class.java).intercept {
    proceed()
}

// Hook 多个方法
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.interceptAll {
    proceed()
}
```

```kotlin [libxposed API]
// 假设这就是你需要 Hook 的类
val clazz: Class<*>

// Hook 构造方法
hook(clazz.getDeclaredConstructor(String::class.java)).intercept { chain ->
    chain.proceed()
}

// Hook 多个方法
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.forEach { method ->
    hook(method).intercept { chain ->
        chain.proceed()
    }
}
```

:::

详情请参考 [Hook 用法 → 基本用法](../../library/yukihook-core/hook-usage.md#基本用法)。

### 链式 Hook

YukiHook 的链式 Hook 与 libxposed API 的拦截器基本一致，`HookChain` 对应 `XposedInterface.Chain`。

> 示例如下

::: code-group

```kotlin [YukiHook]
method.intercept {
    // 得到当前 Hook 的实例
    instance
    // 静态方法中没有实例，请使用 `instanceOrNull`
    instanceOrNull
    // 得到当前 Hook 的方法或构造方法
    member
    // 得到只读的方法参数数组
    args
    // 得到方法参数的第一位 T
    firstArg<T>()
    // 得到方法参数的最后一位 T
    lastArg<T>()
    // 得到方法参数的任意下标 T，这里用 2 举例
    arg<T>(2)
    // 使用当前参数继续执行调用链
    proceed()
    // 使用新的参数继续执行调用链
    proceed(arrayOf(...))
    // 使用新的实例继续执行调用链
    proceedWith(instance = ...)
}
```

```kotlin [libxposed API]
hook(method).intercept { chain ->
    // 得到当前 Hook 的实例
    chain.thisObject
    // 静态方法中没有实例，chain.thisObject 为 null
    chain.thisObject as? T
    // 得到当前 Hook 的方法或构造方法
    chain.executable
    // 得到只读的方法参数数组
    chain.args
    // 得到方法参数的第一位 T
    chain.getArg(0) as T
    // 得到方法参数的最后一位 T
    chain.getArg(chain.args.lastIndex) as T
    // 得到方法参数的任意下标 T，这里用 2 举例
    chain.getArg(2) as T
    // 使用当前参数继续执行调用链
    chain.proceed()
    // 使用新的参数继续执行调用链
    chain.proceed(arrayOf(...))
    // 使用新的实例继续执行调用链
    chain.proceedWith(...)
}
```

:::

如果你需要修改参数或返回值，可以将新的参数数组传入 `proceed`，并将需要返回的值放到最后一位。

> 示例如下

::: code-group

```kotlin [YukiHook]
method.intercept {
    // 修改第一个参数后继续执行调用链
    val result = proceed(arrayOf("Hello YukiHook", lastArg()))
    // 修改返回值内容
    "Modified $result"
}
```

```kotlin [libxposed API]
hook(method).intercept { chain ->
    // 修改第一个参数后继续执行调用链
    val result = chain.proceed(arrayOf("Hello YukiHook", chain.getArg(chain.args.lastIndex)))
    // 修改返回值内容
    "Modified $result"
}
```

:::

不调用 `proceed` 时，原始方法将不会被执行，这等同于替换整个方法。

> 示例如下

::: code-group

```kotlin [YukiHook]
// 替换为你需要的返回值
method.intercept { ... }
// 替换为 Boolean 类型的返回值
method.intercept { true }
// 拦截方法并返回 null
method.intercept { null }
```

```kotlin [libxposed API]
// 替换为你需要的返回值
hook(method).intercept { _ -> ... }
// 替换为 Boolean 类型的返回值
hook(method).intercept { _ -> true }
// 拦截方法并返回 null
hook(method).intercept { _ -> null }
```

:::

详情请参考 [Hook 用法 → 链式 Hook](../../library/yukihook-core/hook-usage.md#链式-hook)。

### 经典 Hook

YukiHook 在使用 libxposed API 作为底座时同样支持经典的前后回调式 Hook，详情请参考 [Hook 用法 → 经典 Hook](../../library/yukihook-core/hook-usage.md#经典-hook)。

### 调用原始方法

> 示例如下

::: code-group

```kotlin [YukiHook]
// 调用一个方法的原始实现
method.invokeOriginal(instance, ...)
// 非虚调用一个方法
method.invokeSpecial(instance, ...)
// 调用一个构造方法的原始实现
constructor.newInstanceOriginal(...)
```

```kotlin [libxposed API]
// 调用一个方法的原始实现
getInvoker(method).setType(XposedInterface.Invoker.Type.ORIGIN).invoke(instance, ...)
// 非虚调用一个方法
getInvoker(method).invokeSpecial(instance, ...)
// 调用一个构造方法的原始实现
getInvoker(constructor).setType(XposedInterface.Invoker.Type.ORIGIN).newInstance(...)
```

:::

详情请参考 [Hook 用法 → 调用原始方法](../../library/yukihook-core/hook-usage.md#调用原始方法)。

### Hook 静态初始化

> 示例如下

::: code-group

```kotlin [YukiHook]
// 假设这是你需要 Hook 的类
val clazz: Class<*>
// 对 <clinit> 进行 Hook
clazz.intercept {
    proceed()
}
```

```kotlin [libxposed API]
// 假设这是你需要 Hook 的类
val clazz: Class<*>
// 对 <clinit> 进行 Hook
hookClassInitializer(clazz).intercept { chain ->
    chain.proceed()
}
```

:::

请确保得到这个 `Class` 时没有对其进行初始化，详情请参考 [Hook 用法 → Hook 静态初始化](../../library/yukihook-core/hook-usage.md#hook-静态初始化)。

### Hook 优先级

> 示例如下

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

### 解除 Hook

> 示例如下

::: code-group

```kotlin [YukiHook]
// 保存当前实例，在适当的时候调用
val handle = method.intercept {
    // 在回调中移除自身
    removeSelf()
    proceed()
}

handle.remove()
```

```kotlin [libxposed API]
// 保存当前实例，在适当的时候调用
var handle: XposedInterface.HookHandle? = null
handle = hook(method).intercept { chain ->
    // 在回调中移除自身，需要使用上方保存的实例
    handle?.unhook()
    chain.proceed()
}

handle?.unhook()
```

:::

详情请参考 [Hook 用法 → 解除 Hook](../../library/yukihook-core/hook-usage.md#解除-hook)。

### 替换 Hook 回调

> 示例如下

::: code-group

```kotlin [YukiHook]
val handle = method.intercept {
    proceed()
}
// 替换为新的回调
handle.replace {
    // Your code here.
    proceed()
}
```

```kotlin [libxposed API]
val handle = hook(method).intercept { chain ->
    chain.proceed()
}
// 替换为新的回调
handle.replaceHook { chain ->
    // Your code here.
    chain.proceed()
}
```

:::

详情请参考 [Hook 用法 → 替换 Hook 回调](../../library/yukihook-core/hook-usage.md#替换-hook-回调)。

### 解除内联化

> 示例如下

::: code-group

```kotlin [YukiHook]
// 假设这就是调用的方法
val callingMethod: Method
// 解除内联化
callingMethod.deoptimize()
```

```kotlin [libxposed API]
// 假设这就是调用的方法
val callingMethod: Method
// 解除内联化
deoptimize(callingMethod)
```

:::

详情请参考 [Hook 用法 → 解除内联化](../../library/yukihook-core/hook-usage.md#解除内联化)。

### 异常处理

在 libxposed API 中，你可以通过 `setExceptionMode` 决定 Hook 回调中发生异常时的行为。

YukiHook 不提供此选项，链式 Hook 中的异常默认按照 `ExceptionMode.PROTECTIVE` 的方式处理，设置 `throwToHostOnFailure` 后则对应 `ExceptionMode.PASSTHROUGH`。

> 示例如下

::: code-group

```kotlin [YukiHook]
method.intercept {
    // 向调用方抛出异常
    throw RuntimeException("Exception Test")
}.throwToHostOnFailure()
```

```kotlin [libxposed API]
hook(method)
    .setExceptionMode(XposedInterface.ExceptionMode.PASSTHROUGH)
    .intercept { chain ->
        // 向调用方抛出异常
        throw RuntimeException("Exception Test")
    }
```

:::

详情请参考 [Hook 用法 → 链式 Hook 中的异常](../../library/yukihook-core/hook-usage.md#链式-hook-中的异常)。