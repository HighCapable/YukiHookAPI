# Xposed Framework API

> 这里介绍了如何从 [Xposed Framework API](https://api.xposed.info/) 迁移至 `YukiHook`。

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

`onHook` 会在开始装载 Hook 时被调用，对应 `handleLoadPackage`，每一个被 Hook 的进程都会调用一次。

详情请参考 [模块入口 → 创建入口类](../../library/yukihook-core/hook-entry.md#创建入口类)。

::: tip

如果你需要使用原生的框架事件，请参考底座的 [框架事件](../../library/yukihook-runtime-xposed82.md#框架事件)。

:::

### 处理作用域

在 Xposed Framework API 中，你需要在 `handleLoadPackage` 中自行判断包名，YukiHook 提供了对应的作用域方法。

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
    // Hook 全部宿主，并排除模块自身
    loadApp(excludeSelf = true) {
        // Your code here.
    }
    // Hook 系统框架
    loadSystem {
        // Your code here.
    }
}
```

```kotlin [Xposed Framework API]
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    // Hook 指定宿主
    if (lpparam.packageName == "io.github.myapp") {
        // Your code here.
    }
    // Hook 多个宿主
    if (lpparam.packageName == "io.github.myapp" || lpparam.packageName == "io.github.anotherapp") {
        // Your code here.
    }
    // Hook 全部宿主，并排除模块自身
    if (lpparam.packageName != "io.github.mymodule") {
        // Your code here.
    }
    // Hook 系统框架，系统框架的包名为 android
    if (lpparam.packageName == "android") {
        // Your code here.
    }
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

```kotlin [Xposed Framework API]
if (lpparam.packageName == "io.github.myapp") {
    // Hook 主进程
    if (lpparam.processName == lpparam.packageName) {
        // Your code here.
    }
    // Hook 其他进程
    if (lpparam.processName == "${lpparam.packageName}:tool") {
        // Your code here.
    }
}
```

:::

如果你需要在宿主的 `Application` 创建完成后再执行 Hook，Xposed Framework API 需要手动 Hook `Application.onCreate`，YukiHook 提供了 `withApplicationReady`。

> 示例如下

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
}
```

```kotlin [Xposed Framework API]
override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
    if (lpparam.packageName == "io.github.myapp") {
        // 得到当前宿主的包名
        lpparam.packageName
        // 得到当前宿主的进程名
        lpparam.processName
        // 得到当前宿主的主进程名，需要自行判断，这里使用包名
        lpparam.packageName
        // 得到当前宿主是否为当前进程中的第一个 `Application`
        lpparam.isFirstApplication
        // 得到当前宿主的 `ApplicationInfo`
        lpparam.appInfo
        // 得到当前宿主的 `ClassLoader`
        lpparam.classLoader
        // 得到当前宿主的 `Application`，初次装载时可能为空
        AndroidAppHelper.currentApplication()
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

    onCreate.hook {
        after {
            // 得到当前 Hook 方法所在的实例
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
    // 使用 Java 反射方案得到 onCreate 方法
    val onCreate = activityClass.getDeclaredMethod(
        "onCreate", Bundle::class.java
    )

    XposedBridge.hookMethod(
        onCreate, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                // 得到当前 Hook 方法所在的实例
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

至此，`onCreate` 方法将被成功 Hook 并在 `io.github.myapp` 中的每个 `Activity` 启动时弹出此对话框。

如果你需要同时 Hook 多个方法或构造方法，可以使用 `hookAll`。

> 示例如下

::: code-group

```kotlin [YukiHook]
// 假设这就是你需要 Hook 的类
val clazz: Class<*>

// Hook 构造方法
clazz.getDeclaredConstructor(String::class.java).hook {
    after {
        // Your code here.
    }
}

// Hook 多个方法
val methods = clazz.declaredMethods.filter { it.name == "foo" }
methods.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [Xposed Framework API]
// 假设这就是你需要 Hook 的类
val clazz: Class<*>

// Hook 构造方法
XposedBridge.hookMethod(
    clazz.getDeclaredConstructor(String::class.java),
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)

// Hook 多个方法
XposedBridge.hookAllMethods(
    clazz, "foo", object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
```

:::

详情请参考 [Hook 用法 → 基本用法](../../library/yukihook-core/hook-usage.md#基本用法)。

### 经典 Hook

YukiHook 的经典 Hook 复刻了在 Xposed Framework API 时代的完整机制，`HookParam` 对应 `XC_MethodHook.MethodHookParam`。

> 示例如下

::: code-group

```kotlin [YukiHook]
hook {
    before {
        // 得到当前 Hook 的实例
        instance
        // 得到当前 Hook 的 Class 实例
        instance.javaClass
        // 得到并 cast 当前 Hook 的实例为指定类型 T
        instance<T>()
        // 静态方法中没有实例，请使用 `instanceOrNull`
        instanceOrNull
        // 得到当前 Hook 的方法或构造方法
        member
        // 得到方法参数数组
        args
        // 得到方法参数的第一位 T
        firstArg().get<T>()
        // 得到方法参数的最后一位 T
        lastArg().get<T>()
        // 得到方法参数的任意下标 T，这里用 2 举例
        arg(2).get<T>()
        // 设置方法参数的任意下标，这里用 2 举例
        arg(2).set(...)
        // 获取当前回调方法体范围内的数据存储实例
        dataExtra
    }
    after {
        // 得到返回值
        result
        // 得到返回值并 cast 为 T
        result<T>()
        // 修改返回值内容
        result = ...
    }
}
```

```kotlin [Xposed Framework API]
object : XC_MethodHook() {
    override fun beforeHookedMethod(param: MethodHookParam) {
        // 得到当前 Hook 的实例
        param.thisObject
        // 得到当前 Hook 的 Class 实例
        param.thisObject.javaClass
        // 得到并 cast 当前 Hook 的实例为指定类型 T
        param.thisObject as T
        // 静态方法中没有实例，param.thisObject 为 null
        param.thisObject as? T
        // 得到当前 Hook 的方法或构造方法
        param.method
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
        // 获取当前回调方法体范围内的数据存储实例
        param.extra
    }

    override fun afterHookedMethod(param: MethodHookParam) {
        // 得到返回值
        param.result
        // 得到返回值并 cast 为 T
        param.result as T
        // 修改返回值内容
        param.result = ...
    }
}
```

:::

`dataExtra` 与 `param.extra` 不同，它在当前 Hook 的全部调用之间共享，而不是每次调用独立存在。

详情请参考 [Hook 用法 → 经典 Hook](../../library/yukihook-core/hook-usage.md#经典-hook)。

### 拦截方法

> 示例如下

::: code-group

```kotlin [YukiHook]
// 有返回值的方法
hook {
    intercept {
        // 在这里实现被替换的逻辑
        // 需要返回方法对应的返回值，无需写 return，只需将返回值放到最后一位
        0
    }
}

// 无返回值的方法 void
hook {
    intercept {
        // 直接在这里实现被替换的逻辑
    }
}

// 替换为你需要的返回值
hook().intercept(...)
// 替换为 Boolean 类型的返回值
hook().intercept(true)
// 拦截方法并返回 null
hook().intercept()
```

```kotlin [Xposed Framework API]
// 有返回值的方法
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // 在这里实现被替换的逻辑
        // ...
        return 0
    }
}

// 无返回值的方法 void
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // 直接在这里实现被替换的逻辑
        return null
    }
}

// 替换为你需要的返回值
XC_MethodReplacement.returnConstant(...)
// 替换为 Boolean 类型的返回值
XC_MethodReplacement.returnConstant(true)
// 拦截方法并返回 null
XC_MethodReplacement.DO_NOTHING
```

:::

详情请参考 [Hook 用法 → 拦截方法](../../library/yukihook-core/hook-usage.md#拦截方法)。

### 调用原始方法

> 示例如下

::: code-group

```kotlin [YukiHook]
hook {
    intercept {
        // 使用原始方法参数调用原始方法，可以省略泛型
        callOriginal<Any?>()
        // 自定义方法参数调用原始方法，可以省略泛型
        invokeOriginal<Any?>(...)
    }
}

// 在 Hook 回调以外调用一个方法的原始实现
method.invokeOriginal(instance, ...)
```

```kotlin [Xposed Framework API]
object : XC_MethodReplacement() {
    override fun replaceHookedMethod(param: MethodHookParam): Any? {
        // 使用原始方法参数调用原始方法
        XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args)
        // 自定义方法参数调用原始方法
        return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, arrayOf(...))
    }
}

// 在 Hook 回调以外调用一个方法的原始实现
XposedBridge.invokeOriginalMethod(method, instance, arrayOf(...))
```

:::

详情请参考 [Hook 用法 → 调用原始方法](../../library/yukihook-core/hook-usage.md#调用原始方法)。

### Hook 优先级

> 示例如下

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

### 解除 Hook

> 示例如下

::: code-group

```kotlin [YukiHook]
// 保存当前实例，在适当的时候调用
val handle = method.hook {
    after {
        // 在回调中移除自身
        removeSelf()
    }
}

handle.remove()
```

```kotlin [Xposed Framework API]
// 保存当前实例，在适当的时候调用
var unhook: XC_MethodHook.Unhook? = null
unhook = XposedBridge.hookMethod(
    method, object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // 在回调中移除自身，需要使用上方保存的实例
            unhook?.unhook()
        }
    }
)

unhook?.unhook()
```

:::

详情请参考 [Hook 用法 → 解除 Hook](../../library/yukihook-core/hook-usage.md#解除-hook)。

### 异常处理

在 Xposed Framework API 中，回调中发生的异常会由框架捕获并输出到日志，在 YukiHook 中你可以监听这些异常。

> 示例如下

::: code-group

```kotlin [YukiHook]
hook {
    after {
        // 向宿主抛出异常
        Throwable("Fatal").throwToHost()
    }
}.apply {
    // 处理 Hook 回调中的异常
    onCallbackFailure { param, throwable -> }
}
```

```kotlin [Xposed Framework API]
object : XC_MethodHook() {
    override fun afterHookedMethod(param: MethodHookParam) {
        // 向宿主抛出异常
        param.throwable = Throwable("Fatal")
    }
}
```

:::

详情请参考 [Hook 用法 → 异常处理](../../library/yukihook-core/hook-usage.md#异常处理)。

## 迁移反射用法

这里介绍了如何将 `XposedHelpers` 中的反射用法迁移至 Java 原生反射与 KavaRef。

::: tip

我们推荐使用 [KavaRef](https://github.com/HighCapable/KavaRef) 作为现代的反射 API，YukiHook 为其提供了原生的 [扩展支持](../../library/yukihook-extension-kavaref.md)。

:::

下方的示例均假设在 `loadApp` 或 `handleLoadPackage` 中使用，其中 `clazz` 为需要反射的 `Class`，`instance` 为它的实例。

### 查找类

`XposedHelpers.findClass` 需要传入 `ClassLoader`，在 `PackageParam` 中，KavaRef 的 `toClass` 默认会使用宿主的 `ClassLoader`。

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找类
val clazz = "$packageName.Demo".toClass()
// 查找类，找不到时返回 null
val clazzOrNull = "$packageName.Demo".toClassOrNull()
// 在多个类名中查找第一个存在的类
val variousClazz = VariousClass("$packageName.Demo", "$packageName.NewDemo").toClass()
```

```kotlin [Java 原生反射]
// 查找类
val clazz = Class.forName("$packageName.Demo", false, hostClassLoader)
// 查找类，找不到时返回 null
val clazzOrNull = runCatching {
    Class.forName("$packageName.Demo", false, hostClassLoader)
}.getOrNull()
```

```kotlin [XposedHelpers]
// 查找类
val clazz = XposedHelpers.findClass("$packageName.Demo", lpparam.classLoader)
// 查找类，找不到时返回 null
val clazzOrNull = XposedHelpers.findClassIfExists("$packageName.Demo", lpparam.classLoader)
```

:::

详情请参考 [yukihook-extension-kavaref → 装载宿主中的类](../../library/yukihook-extension-kavaref.md#装载宿主中的类)。

### 查找并调用方法

`XposedHelpers.callMethod` 会根据传入的参数自动匹配方法，并且会沿 `super` 超类查找，而 Java 原生反射与 KavaRef 都需要明确指定方法的参数类型，`getDeclaredMethod` 与 KavaRef 默认只会查找当前类中声明的方法，需要查找超类时请参考下方示例。

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找方法
val method = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
}
// 查找方法，找不到时返回 null，需要配合 `optional()` 使用
val methodOrNull = clazz.resolve().optional().firstMethodOrNull {
    name = "foo"
    parameters(String::class)
}
// 查找方法，同时查找超类中的方法
val superMethod = clazz.resolve().firstMethod {
    name = "foo"
    parameters(String::class)
    superclass()
}
// 调用实例方法
method.of(instance).invoke("some string")
// 调用静态方法
method.invoke("some string")
// 调用实例方法并将返回值 cast 为 T
method.of(instance).invoke<T>("some string")
```

```kotlin [Java 原生反射]
// 查找方法，只会查找当前类中声明的方法
val method = clazz.getDeclaredMethod("foo", String::class.java)
// 查找方法，找不到时返回 null
val methodOrNull = runCatching {
    clazz.getDeclaredMethod("foo", String::class.java)
}.getOrNull()
// 查找公开的方法，包括超类中公开的方法
val superMethod = clazz.getMethod("foo", String::class.java)
// 调用前需要设置访问权限
method.isAccessible = true
// 调用实例方法
method.invoke(instance, "some string")
// 调用静态方法
method.invoke(null, "some string")
// 调用实例方法并将返回值 cast 为 T
method.invoke(instance, "some string") as T
```

```kotlin [XposedHelpers]
// 查找方法，只会查找当前类中声明的方法
val method = XposedHelpers.findMethodExact(clazz, "foo", String::class.java)
// 查找方法，找不到时返回 null
val methodOrNull = XposedHelpers.findMethodExactIfExists(clazz, "foo", String::class.java)
// 查找方法，同时查找超类中的方法，参数类型允许使用可赋值的类型
val superMethod = XposedHelpers.findMethodBestMatch(clazz, "foo", String::class.java)
// 查找并调用实例方法
XposedHelpers.callMethod(instance, "foo", "some string")
// 查找并调用静态方法
XposedHelpers.callStaticMethod(clazz, "foo", "some string")
// 查找并调用实例方法，同时将返回值 cast 为 T
XposedHelpers.callMethod(instance, "foo", "some string") as T
```

:::

KavaRef 会自动处理成员的访问权限，你不再需要手动设置 `isAccessible`。

### 查找并读写字段

`XposedHelpers.findField` 会同时查找超类中的字段，在 Java 原生反射与 KavaRef 中，你需要手动指定。

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找字段，同时查找超类中的字段
val field = clazz.resolve().firstField {
    name = "bar"
    type = String::class
    superclass()
}
// 查找字段，找不到时返回 null，需要配合 `optional()` 使用
val fieldOrNull = clazz.resolve().optional().firstFieldOrNull {
    name = "bar"
    type = String::class
    superclass()
}
// 读取实例字段
field.of(instance).get<String>()
// 修改实例字段
field.of(instance).set("some string")
// 读取静态字段
field.get<String>()
// 修改静态字段
field.set("some string")
```

```kotlin [Java 原生反射]
// 查找字段，需要自行遍历超类
var field: Field? = null
var current: Class<*>? = clazz
while (field == null && current != null) {
    field = runCatching { current.getDeclaredField("bar") }.getOrNull()
    current = current.superclass
}
// 调用前需要设置访问权限
field?.isAccessible = true
// 读取实例字段
field?.get(instance) as String?
// 修改实例字段
field?.set(instance, "some string")
// 读取静态字段
field?.get(null) as String?
// 修改静态字段
field?.set(null, "some string")
```

```kotlin [XposedHelpers]
// 查找字段，同时查找超类中的字段
val field = XposedHelpers.findField(clazz, "bar")
// 查找字段，找不到时返回 null
val fieldOrNull = XposedHelpers.findFieldIfExists(clazz, "bar")
// 读取实例字段
XposedHelpers.getObjectField(instance, "bar") as String?
// 读取实例字段，基本类型有对应的方法，这里用 Int 举例
XposedHelpers.getIntField(instance, "count")
// 修改实例字段
XposedHelpers.setObjectField(instance, "bar", "some string")
// 修改实例字段，基本类型有对应的方法，这里用 Int 举例
XposedHelpers.setIntField(instance, "count", 1)
// 读取静态字段
XposedHelpers.getStaticObjectField(clazz, "bar") as String?
// 修改静态字段
XposedHelpers.setStaticObjectField(clazz, "bar", "some string")
```

:::

### 创建实例

`XposedHelpers.newInstance` 会根据传入的参数自动匹配构造方法，KavaRef 的 `createInstance` 提供了相同的方式。

> 示例如下

::: code-group

```kotlin [KavaRef]
// 查找构造方法
val constructor = clazz.resolve().firstConstructor {
    parameters(String::class)
}
// 使用构造方法创建实例
constructor.create("some string")
// 根据传入的参数自动匹配公开的构造方法并创建实例
clazz.createInstance("some string")
// 同上，但是允许匹配非公开的构造方法，与 `XposedHelpers.newInstance` 一致
clazz.createInstance("some string", isPublic = false)
```

```kotlin [Java 原生反射]
// 查找构造方法
val constructor = clazz.getDeclaredConstructor(String::class.java)
// 调用前需要设置访问权限
constructor.isAccessible = true
// 使用构造方法创建实例
constructor.newInstance("some string")
```

```kotlin [XposedHelpers]
// 查找构造方法
val constructor = XposedHelpers.findConstructorExact(clazz, String::class.java)
// 查找构造方法，找不到时返回 null
val constructorOrNull = XposedHelpers.findConstructorExactIfExists(clazz, String::class.java)
// 使用构造方法创建实例
constructor.newInstance("some string")
// 根据传入的参数自动匹配构造方法 (包括非公开的构造方法) 并创建实例
XposedHelpers.newInstance(clazz, "some string")
```

:::

### 查找并 Hook

`XposedHelpers.findAndHookMethod` 与 `findAndHookConstructor` 会在查找成员后直接进行 Hook，在 YukiHook 中，你可以直接对查找到的成员使用 `hook`。

> 示例如下

::: code-group

```kotlin [KavaRef]
// Hook 方法
clazz.resolve()
    .firstMethod {
        name = "foo"
        parameters(String::class)
    }.hook {
        after {
            // Your code here.
        }
    }
// Hook 构造方法
clazz.resolve()
    .firstConstructor {
        parameters(String::class)
    }.hook {
        after {
            // Your code here.
        }
    }
// Hook 全部同名方法
clazz.resolve()
    .method { name = "foo" }
    .hookAll {
        after {
            // Your code here.
        }
    }
```

```kotlin [Java 原生反射]
// Hook 方法
clazz.getDeclaredMethod("foo", String::class.java).hook {
    after {
        // Your code here.
    }
}
// Hook 构造方法
clazz.getDeclaredConstructor(String::class.java).hook {
    after {
        // Your code here.
    }
}
// Hook 全部同名方法
clazz.declaredMethods.filter { it.name == "foo" }.hookAll {
    after {
        // Your code here.
    }
}
```

```kotlin [XposedHelpers]
// Hook 方法
XposedHelpers.findAndHookMethod(
    clazz, "foo", String::class.java,
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
// Hook 构造方法
XposedHelpers.findAndHookConstructor(
    clazz, String::class.java,
    object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            // Your code here.
        }
    }
)
// Hook 全部同名方法
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

详情请参考 [yukihook-extension-kavaref → Hook 用法](../../library/yukihook-extension-kavaref.md#hook-用法)。