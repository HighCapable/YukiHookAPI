## PackageParam [class]

```kotlin
open class PackageParam(private var wrapper: PackageParamWrapper?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 装载 Hook 的目标 APP 入口对象实现类。

### appClassLoader [field]

```kotlin
val appClassLoader：ClassLoader
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的 `ClassLoader`。

### appInfo [field]

```kotlin
val appInfo: ApplicationInfo
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的 `ApplicationInfo`。

### appContext [field]

```kotlin
val appContext: Application
```

**变更记录**

`v1.0.72` `新增`

**功能描述**

> 获取当前 Hook APP 的 `Application`。

### processName [field]

```kotlin
val processName: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的进程名称。

### packageName [field]

```kotlin
val packageName: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的包名。

### isFirstApplication [field]

```kotlin
val isFirstApplication: Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 是否为第一个 `Application`。

### mainProcessName [field]

```kotlin
val mainProcessName: String
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 获取当前 Hook APP 的主进程名称。

其对应的就是 `packageName`。

### prefs [field]

```kotlin
val prefs: YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获得当前使用的存取数据对象缓存实例。

### prefs [method]

```kotlin
fun prefs(name: String): YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 获得当前使用的存取数据对象缓存实例。

你可以通过 `name` 来自定义 Sp 存储的名称。

### loadApp [method]

```kotlin
inline fun loadApp(name: String, initiate: PackageParam.() -> Unit)
```

```kotlin
fun loadApp(name: String, hooker: YukiBaseHooker)
```

**变更记录**

`v1.0` `添加`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 装载并 Hook 指定包名的 APP。

`name` 为 APP 的包名，后方的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

### withProcess [method]

```kotlin
inline fun withProcess(name: String, initiate: PackageParam.() -> Unit)
```

```kotlin
fun withProcess(name: String, hooker: YukiBaseHooker)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 装载并 Hook APP 的指定进程。

`name` 为 APP 的进程名称，后方的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

### loadHooker [method]

```kotlin
fun loadHooker(hooker: YukiBaseHooker)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 装载 Hook 子类。

你可以填入 `hooker` 在 Hooker 中继续装载 Hooker。

### clazz [field]

```kotlin
val String.clazz: Class<*>
```

```kotlin
val VariousClass.clazz: Class<*>
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 字符串、`VariousClass` 转换为实体类。

**功能示例**

你可以轻松地将 `String` 类型的 `Class` 包名转为 `Class` 实例。

> 示例如下

```kotlin
"com.example.demo.DemoClass".clazz
```

为了美观，你可以把字符串用 `(` `)` 括起来。

> 示例如下

```kotlin
("com.example.demo.DemoClass").clazz
```

你还可以创建一个 `VariousClass`，并转换为实体类。

`VariousClass` 会枚举所有设置的 `Class` 并最终获得完全匹配的那一个。

> 示例如下

```kotlin
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").clazz
```

### hasClass [field]

```kotlin
val String.hasClass: Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 通过字符串使用当前 `appClassLoader` 查找类是否存在。

**功能示例**

你可以轻松的使用此方法判断字符串中的类是否存在。

> 示例如下

```kotlin
if("com.example.demo.DemoClass".hasClass) {
    // Your code here.
}
```

### findClass [method]

```kotlin
fun findClass(name: String): HookClass
```

```kotlin
fun findClass(vararg name: String): VariousClass
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

移除了 ~~`findClass(various: VariousClass)`~~ 方法

**功能描述**

> 通过完整包名+名称查找需要被 Hook 的 `Class`。

**功能示例**

你可以使用三种方式查找你需要 Hook 的目标 `Class`。

你可以直接将被查找的 `Class` 完整包名+名称填入 `name` 中。

> 示例如下

```kotlin
findClass(name = "com.example.demo.DemoClass")
```

若你不确定多个版本的 `Class` 以及不同名称，你可以将多个完整包名+名称填入 `name` 中。

> 示例如下

```kotlin
findClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2", "com.example.demo.DemoClass3")
```

你还可以创建一个 `VariousClass`，将 `Class` 的完整包名+名称填入 `VariousClass` 的 `name` 中并填入 `various` 参数中。

> 示例如下

```kotlin
val variousClass = VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2", "com.example.demo.DemoClass3")
```

### hook [method]

```kotlin
inline fun String.hook(isUseAppClassLoader: Boolean, initiate: YukiHookCreater.() -> Unit): YukiHookCreater.Result
```

```kotlin
inline fun Class<*>.hook(isUseAppClassLoader: Boolean, initiate: YukiHookCreater.() -> Unit): YukiHookCreater.Result
```

```kotlin
inline fun VariousClass.hook(isUseAppClassLoader: Boolean, initiate: YukiHookCreater.() -> Unit): YukiHookCreater.Result
```

```kotlin
inline fun HookClass.hook(isUseAppClassLoader: Boolean, initiate: YukiHookCreater.() -> Unit): YukiHookCreater.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

新增 `VariousClass` 的直接调用 `hook` 方法

`v1.0.2` `修改`

新增 `String` 的直接调用 `hook` 方法

`v1.0.3` `修改`

新增 `YukiHookCreater.Result` 返回值

`v1.0.70` `修改`

新增 `isUseAppClassLoader` 参数

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 这是一切 Hook 的入口创建方法，Hook 方法、构造类。

**功能示例**

如你所见，Hook 方法体的创建可使用 4 种方式。

通过字符串类名得到 `HookClass` 实例进行创建。

> 示例如下

```kotlin
("com.example.demo.DemoClass").hook {
    // Your code here.
}

```
通过 `findClass` 得到 `HookClass` 实例进行创建。

> 示例如下

```kotlin
findClass(name = "com.example.demo.DemoClass").hook {
    // Your code here.
}
```

使用 `stub` 或直接拿到 `Class` 实例进行创建。

> 示例如下

```kotlin
Activity::class.java.hook {
    // Your code here.
}
```

使用 `VariousClass` 实例进行创建。

> 示例如下

```kotlin
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").hook {
    // Your code here.
}
```

或者直接使用可变字符串数组进行创建。

> 示例如下

```kotlin
findClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").hook {
    // Your code here.
}
```

!> 以下是关于 Hook 目标 Class 的一个特殊情况说明。

若你 Hook 的 `Class` 实例的 `ClassLoader` 并不是当前的 `appClassLoader`，那么你需要做一下小的调整。

在 `hook` 方法中加入 `isUseAppClassLoader = false`，这样，你的 `Class` 就不会被重新绑定到 `appClassLoader` 了。

此方案适用于目标 `Class` 无法在当前 `appClassLoader` 中被得到但可以得到 `Class` 实例的情况。

> 示例如下

```kotlin
// 这里的做法标识了 hook 不会再将 YourClass 重新与当前 appClassLoader 绑定
YourClass.hook(isUseAppClassLoader = false) {
    // Your code here.
}
```