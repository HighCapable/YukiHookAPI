---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# PackageParam <span class="symbol">- class</span>

```kotlin:no-line-numbers
open class PackageParam internal constructor(internal var wrapper: PackageParamWrapper?)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 装载 Hook 的目标 APP 入口对象实现类。

## appClassLoader <span class="symbol">- field</span>

```kotlin:no-line-numbers
val appClassLoader：ClassLoader
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取当前 Hook APP 的 `ClassLoader`。

## appInfo <span class="symbol">- field</span>

```kotlin:no-line-numbers
val appInfo: ApplicationInfo
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取当前 Hook APP 的 `ApplicationInfo`。

## appUserId <span class="symbol">- field</span>

```kotlin:no-line-numbers
val appUserId: Int
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获取当前 Hook APP 的用户 ID。

机主为 `0`，应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同。

## appContext <span class="symbol">- field</span>

```kotlin:no-line-numbers
val appContext: Application?
```

**Change Records**

`v1.0.72` `added`

`v1.1.0` `modified`

加入可空类型 (空安全)

**Function Illustrate**

> 获取当前 Hook APP 的 `Application`。

::: danger

首次装载可能是空的，请延迟一段时间再获取或使用 **onAppLifecycle** 监听来完成。

:::

## appResources <span class="symbol">- field</span>

```kotlin:no-line-numbers
val appResources：Resources?
```

**Change Records**

`v1.0.80` `added`

`v1.1.0` `modified`

加入可空类型 (空安全)

**Function Illustrate**

> 获取当前 Hook APP 的 Resources。

::: danger

你只能在 **HookResources.hook** 方法体内或 **appContext** 装载完毕时进行调用。

:::

## systemContext <span class="symbol">- field</span>

```kotlin:no-line-numbers
val systemContext: Context
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获取当前系统框架的 `Context`。

## processName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val processName: String
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取当前 Hook APP 的进程名称。

## packageName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val packageName: String
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取当前 Hook APP 的包名。

## isFirstApplication <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isFirstApplication: Boolean
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获取当前 Hook APP 是否为第一个 `Application`。

## mainProcessName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val mainProcessName: String
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 获取当前 Hook APP 的主进程名称。

其对应的就是 `packageName`。

## moduleAppFilePath <span class="symbol">- field</span>

```kotlin:no-line-numbers
val moduleAppFilePath: String
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 获取当前 Xposed 模块自身 APK 文件路径。

::: danger

作为 Hook API 装载时无法使用，会获取到空字符串。

:::

## moduleAppResources <span class="symbol">- field</span>

```kotlin:no-line-numbers
val moduleAppResources: YukiModuleResources
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 获取当前 Xposed 模块自身 `Resources`。

::: danger

作为 Hook API 或不支持的 Hook Framework 装载时无法使用，会抛出异常。

:::

## prefs <span class="symbol">- field</span>

```kotlin:no-line-numbers
val prefs: YukiHookModulePrefs
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 获得当前使用的存取数据对象缓存实例。

::: danger

作为 Hook API 装载时无法使用，会抛出异常。

:::

## prefs <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun prefs(name: String): YukiHookModulePrefs
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 获得当前使用的存取数据对象缓存实例。

你可以通过 `name` 来自定义 Sp 存储的名称。

::: danger

作为 Hook API 装载时无法使用，会抛出异常。

:::

## dataChannel <span class="symbol">- field</span>

```kotlin:no-line-numbers
val dataChannel: YukiHookDataChannel.NameSpace
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 获得当前使用的数据通讯桥命名空间对象。

::: danger

作为 Hook API 装载时无法使用，会抛出异常。

:::

## resources <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun resources(): HookResources
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 获得当前 Hook APP 的 `YukiResources` 对象。

请调用 `HookResources.hook` 方法开始 Hook。

## refreshModuleAppResources <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun refreshModuleAppResources()
```

**Change Records**

`v1.0.87` `added`

**Function Illustrate**

> 刷新当前 Xposed 模块自身 `Resources`。

## onAppLifecycle <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun onAppLifecycle(initiate: AppLifecycle.() -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 生命周期装载事件。

::: warning

在 **loadZygote** 中不会被装载，仅会在 **loadSystem**、**loadApp** 中装载。

作为 Hook API 装载时请使用原生的 **Application** 实现生命周期监听。

:::

## loadApp <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun loadApp(name: String, initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun loadApp(name: String, hooker: YukiBaseHooker)
```

**Change Records**

`v1.0` `first`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 装载并 Hook 指定包名的 APP。

`name` 为 APP 的包名，后方的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

装载并 Hook 指定、全部包名的 APP。

若要装载 APP Zygote 事件，请使用 `loadZygote`。

若要 Hook 系统框架，请使用 `loadSystem`。

**Function Example**

你可以使用 `loadApp` 的 `lambda` 方法体形式或直接装载一个 Hooker。

> The following example

```kotlin
// 使用 lambda
loadApp(name = "com.example.test") {
    // Your code here.
}
// 使用 Hooker
loadApp(name = "com.example.test", CustomHooker)
```

若不指定 `name` 参数，则此方法体默认会过滤当前系统中全部可被 Hook 的 APP。

> The following example

```kotlin
// 使用 lambda
loadApp {
    // Your code here.
}
// 使用 Hooker
loadApp(hooker = CustomHooker)
```

## loadZygote <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun loadZygote(initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun loadZygote(hooker: YukiBaseHooker)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 装载 APP Zygote 事件。

方法中的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

## loadSystem <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun loadSystem(initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun loadSystem(hooker: YukiBaseHooker)
```

**Change Records**

`v1.0.82` `added`

**Function Illustrate**

> 装载并 Hook 系统框架。

方法中的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

## withProcess <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun withProcess(name: String, initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun withProcess(name: String, hooker: YukiBaseHooker)
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 装载并 Hook APP 的指定进程。

`name` 为 APP 的进程名称，后方的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

## loadHooker <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun loadHooker(hooker: YukiBaseHooker)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 装载 Hook 子类。

你可以填入 `hooker` 在 Hooker 中继续装载 Hooker。

## searchClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun searchClass(name: String, async: Boolean, initiate: ClassConditions): DexClassFinder.Result
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过 `appClassLoader` 按指定条件查找并得到当前 Hook APP **Dex** 中的 `Class`。

::: danger

此方法在 **Class** 数量过多及查找条件复杂时会非常耗时。

建议启用 **async** 或设置 **name** 参数，**name** 参数将在 Hook APP (宿主) 不同版本中自动进行本地缓存以提升效率。

此功能尚在试验阶段，性能与稳定性可能仍然存在问题，使用过程遇到问题请向我们报告并帮助我们改进。

:::

<h2 class="deprecated">String+VariousClass.clazz - i-ext-field</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请转移到 `toClass(...)` 方法

<h2 class="deprecated">String.hasClass - i-ext-field</h2>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请转移到 `hasClass(...)` 方法

## String+VariousClass.toClass <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.toClass(loader: ClassLoader?): Class<*>
```

```kotlin:no-line-numbers
fun VariousClass.toClass(loader: ClassLoader?): Class<*>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过字符串类名、`VariousClass` 转换为 `loader` 中的实体类。

默认使用当前 `appClassLoader` 装载目标 `Class`。

**Function Example**

你可以轻松地将 `String` 类型的 `Class` 包名转为 `Class` 实例。

> The following example

```kotlin
"com.example.demo.DemoClass".toClass()
```

你还可以向 `loader` 参数传入你自定义的 `ClassLoader`。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
"com.example.demo.DemoClass".toClass(customClassLoader)
```

你还可以创建一个 `VariousClass`，并转换为实体类。

`VariousClass` 会枚举所有设置的 `Class` 并最终获得第一个存在的 `Class`。

> The following example

```kotlin
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").toClass()
```

同样地，你还可以向 `loader` 参数传入你自定义的 `ClassLoader`。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").toClass(customClassLoader)
```

## String+VariousClass.toClassOrNull <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.toClassOrNull(loader: ClassLoader?): Class<*>?
```

```kotlin:no-line-numbers
fun VariousClass.toClassOrNull(loader: ClassLoader?): Class<*>?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过字符串类名、`VariousClass` 转换为 `loader` 中的实体类。

默认使用当前 `appClassLoader` 装载目标 `Class`。

找不到 `Class` 会返回 `null`，不会抛出异常。

**Function Example**

用法请参考 [String+VariousClass.toClass](#string-variousclass-toclass-i-ext-method) 方法。

## String.hasClass <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun String.hasClass(loader: ClassLoader?): Boolean
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 通过字符串类名查找是否存在。

默认使用当前 `appClassLoader` 装载目标 `Class`。

**Function Example**

你可以轻松的使用此方法判断字符串中的类是否存在。

> The following example

```kotlin
if("com.example.demo.DemoClass".hasClass()) {
    // Your code here.
}
```

你还可以自定义其中的 `loader` 参数，默认为 `appClassLoader`。

> The following example

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
if("com.example.demo.DemoClass".hasClass(customClassLoader)) {
    // Your code here.
}
```

## findClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun findClass(name: String, loader: ClassLoader?): HookClass
```

```kotlin:no-line-numbers
fun findClass(vararg name: String, loader: ClassLoader?): VariousClass
```

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

移除了 ~~`findClass(various: VariousClass)`~~ 方法

`v1.1.0` `modified`

新增 `loader` 参数

**Function Illustrate**

> 通过完整包名+名称查找需要被 Hook 的 `Class`。

::: warning

使用此方法会得到一个 **HookClass** 仅用于 Hook，若想查找 **Class** 请使用 [toClass](#string-variousclass-toclass-i-ext-method) 功能。

:::

**Function Example**

你可以使用三种方式查找你需要 Hook 的目标 `Class`。

你可以直接将被查找的 `Class` 完整包名+名称填入 `name` 中。

> The following example

```kotlin
findClass(name = "com.example.demo.DemoClass")
```

若你不确定多个版本的 `Class` 以及不同名称，你可以将多个完整包名+名称填入 `name` 中。

> The following example

```kotlin
findClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2", "com.example.demo.DemoClass3")
```

你还可以创建一个 `VariousClass`，将 `Class` 的完整包名+名称填入 `VariousClass` 的 `name` 中并填入 `various` 参数中。

> The following example

```kotlin
val variousClass = VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2", "com.example.demo.DemoClass3")
```

若你当前需要查找的 `Class` 不属于 `appClassLoader`，你可以使用 `loader` 参数指定你要装载的 `ClassLoader`。

> The following example

```kotlin
val outsideLoader: ClassLoader? = ... // 假设这就是你的 ClassLoader
findClass(name = "com.example.demo.OutsideClass", loader = outsideLoader)
```

同样地，在不确定多个版本的 `Class` 以及不同名称时，也可以使用 `loader` 参数指定你要装载的 `ClassLoader`。

> The following example

```kotlin
val outsideLoader: ClassLoader? = ... // 假设这就是你的 ClassLoader
findClass("com.example.demo.OutsideClass1", "com.example.demo.OutsideClass2", "com.example.demo.OutsideClass3", loader = outsideLoader)
```

## String+Class+VariousClass+HookClass.hook <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun String.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin:no-line-numbers
inline fun Class<*>.hook(isForceUseAbsolute: Boolean, initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin:no-line-numbers
inline fun VariousClass.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin:no-line-numbers
inline fun HookClass.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

**Change Records**

`v1.0` `first`

`v1.0.1` `modified`

新增 `VariousClass` 的直接调用 `hook` 方法

`v1.0.2` `modified`

新增 `String` 的直接调用 `hook` 方法

`v1.0.3` `modified`

新增 `YukiMemberHookCreator.Result` 返回值

`v1.0.70` `modified`

新增 `isUseAppClassLoader` 参数

`v1.0.80` `modified`

将方法体进行 inline

`v1.1.0` `modified`

移除了 ~~`isUseAppClassLoader`~~ 参数

添加了 `isForceUseAbsolute` 参数到 `Class.hook` 方法

**Function Illustrate**

> 这是一切 Hook 的入口创建方法，Hook 方法、构造方法。

**Function Example**

如你所见，Hook 方法体的创建可使用 4 种方式。

通过字符串类名得到 `HookClass` 实例进行创建。

> The following example

```kotlin
"com.example.demo.DemoClass".hook {
    // Your code here.
}

```
通过 `findClass` 得到 `HookClass` 实例进行创建。

> The following example

```kotlin
findClass(name = "com.example.demo.DemoClass").hook {
    // Your code here.
}
```

使用 `stub` 或直接拿到 `Class` 实例进行创建。

默认情况下 API 会将 `Class` 实例转换为类名并绑定到 `appClassLoader`，若失败，则会使用原始 `Class` 实例直接进行 Hook。

> The following example

```kotlin
Stub::class.java.hook {
    // Your code here.
}
```

若当前 `Class` 不在 `appClassLoader` 且自动匹配无法找到该 `Class`，请启用 `isForceUseAbsolute`。

> The following example

```kotlin
YourClass::class.java.hook(isForceUseAbsolute = true) {
    // Your code here.
}
```

使用 `VariousClass` 实例进行创建。

> The following example

```kotlin
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").hook {
    // Your code here.
}
```

或者直接使用可变字符串数组进行创建。

> The following example

```kotlin
findClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").hook {
    // Your code here.
}
```

## HookResources.hook <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
inline fun HookResources.hook(initiate: YukiResourcesHookCreator.() -> Unit)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> Hook APP 的 Resources。

::: danger

请注意你需要确保当前 Hook Framework 支持且 **InjectYukiHookWithXposed.isUsingResourcesHook** 已启用。

:::

**Function Example**

Resources Hook 为固定用法，获取 `resources` 对象，然后调用 `hook` 方法开始 Hook。

> The following example

```kotlin
resources().hook {
    // Your code here.
}
```

::: danger

这是固定用法，为了防止发生问题，你不可手动实现任何 **HookResources** 实例执行 **hook** 调用。

:::

将 Resources 的 Hook 设置为这样是为了与 `findClass(...).hook` 做到统一，使得调用起来逻辑不会混乱。

## AppLifecycle <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class AppLifecycle internal constructor()
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 当前 Hook APP 的生命周期实例处理类。

### attachBaseContext <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun attachBaseContext(result: (baseContext: Context, hasCalledSuper: Boolean) -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.attachBaseContext`。

### onCreate <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onCreate(initiate: Application.() -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.onCreate`。

### onTerminate <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onTerminate(initiate: Application.() -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.onTerminate`。

### onLowMemory <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onLowMemory(initiate: Application.() -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.onLowMemory`。

### onTrimMemory <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onTrimMemory(result: (self: Application, level: Int) -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.onTrimMemory`。

### onConfigurationChanged <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun onConfigurationChanged(result: (self: Application, config: Configuration) -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 监听当前 Hook APP 装载 `Application.onConfigurationChanged`。

### registerReceiver <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun registerReceiver(vararg action: String, result: (context: Context, intent: Intent) -> Unit)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 注册系统广播监听。