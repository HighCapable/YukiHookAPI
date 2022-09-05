## PackageParam *- class*

```kotlin
open class PackageParam internal constructor(internal var wrapper: PackageParamWrapper?)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 装载 Hook 的目标 APP 入口对象实现类。

### appClassLoader *- field*

```kotlin
val appClassLoader：ClassLoader
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的 `ClassLoader`。

### appInfo *- field*

```kotlin
val appInfo: ApplicationInfo
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的 `ApplicationInfo`。

### appUserId *- field*

```kotlin
val appUserId: Int
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获取当前 Hook APP 的用户 ID。

机主为 `0`，应用双开 (分身) 或工作资料因系统环境不同 ID 也各不相同。

### appContext *- field*

```kotlin
val appContext: Application
```

**变更记录**

`v1.0.72` `新增`

**功能描述**

> 获取当前 Hook APP 的 `Application`。

!> 首次装载可能是空的，请延迟一段时间再获取或通过设置 `onAppLifecycle` 监听来完成。

### appResources *- field*

```kotlin
val appResources：Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前 Hook APP 的 Resources。

!> 你只能在 `HookResources.hook` 方法体内或 `appContext` 装载完毕时进行调用。

### systemContext *- field*

```kotlin
val systemContext: Context
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获取当前系统框架的 `Context`。

### processName *- field*

```kotlin
val processName: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的进程名称。

### packageName *- field*

```kotlin
val packageName: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 的包名。

### isFirstApplication *- field*

```kotlin
val isFirstApplication: Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获取当前 Hook APP 是否为第一个 `Application`。

### mainProcessName *- field*

```kotlin
val mainProcessName: String
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 获取当前 Hook APP 的主进程名称。

其对应的就是 `packageName`。

### moduleAppFilePath *- field*

```kotlin
val moduleAppFilePath: String
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前 Xposed 模块自身 APK 文件路径。

!> 作为 Hook API 装载时无法使用，会获取到空字符串。

### moduleAppResources *- field*

```kotlin
val moduleAppResources: YukiModuleResources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前 Xposed 模块自身 `Resources`。

!> 作为 Hook API 或不支持的 Hook Framework 装载时无法使用，会抛出异常。

### prefs *- field*

```kotlin
val prefs: YukiHookModulePrefs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 获得当前使用的存取数据对象缓存实例。

!> 作为 Hook API 装载时无法使用，会抛出异常。

### prefs *- method*

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

!> 作为 Hook API 装载时无法使用，会抛出异常。

### dataChannel *- field*

```kotlin
val dataChannel: YukiHookDataChannel.NameSpace
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 获得当前使用的数据通讯桥命名空间对象。

!> 作为 Hook API 装载时无法使用，会抛出异常。

### resources *- method*

```kotlin
fun resources(): HookResources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获得当前 Hook APP 的 `YukiResources` 对象。

请调用 `HookResources.hook` 方法开始 Hook。

### refreshModuleAppResources *- method*

```kotlin
fun refreshModuleAppResources()
```

**变更记录**

`v1.0.87` `新增`

**功能描述**

> 刷新当前 Xposed 模块自身 `Resources`。

### onAppLifecycle *- method*

```kotlin
inline fun onAppLifecycle(initiate: AppLifecycle.() -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 生命周期装载事件。

!> 在 `loadZygote` 中不会被装载，仅会在 `loadSystem`、`loadApp` 中装载。

!> 作为 Hook API 装载时请使用原生的 `Application` 实现生命周期监听。

### loadApp *- method*

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

装载并 Hook 指定、全部包名的 APP。

若要装载 APP Zygote 事件，请使用 `loadZygote`。

若要 Hook 系统框架，请使用 `loadSystem`。

**功能示例**

你可以使用 `loadApp` 的 `lambda` 方法体形式或直接装载一个 Hooker。

> 示例如下

```kotlin
// 使用 lambda
loadApp(name = "com.example.test") {
    // Your code here.
}
// 使用 Hooker
loadApp(name = "com.example.test", CustomHooker)
```

若不指定 `name` 参数，则此方法体默认会过滤当前系统中全部可被 Hook 的 APP。

> 示例如下

```kotlin
// 使用 lambda
loadApp {
    // Your code here.
}
// 使用 Hooker
loadApp(hooker = CustomHooker)
```

### loadZygote *- method*

```kotlin
inline fun loadZygote(initiate: PackageParam.() -> Unit)
```

```kotlin
fun loadZygote(hooker: YukiBaseHooker)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 装载 APP Zygote 事件。

方法中的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

### loadSystem *- method*

```kotlin
inline fun loadSystem(initiate: PackageParam.() -> Unit)
```

```kotlin
fun loadSystem(hooker: YukiBaseHooker)
```

**变更记录**

`v1.0.82` `新增`

**功能描述**

> 装载并 Hook 系统框架。

方法中的两个参数一个可作为 `lambda` 方法体使用，一个可以直接装载子 Hooker。

### withProcess *- method*

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

### loadHooker *- method*

```kotlin
fun loadHooker(hooker: YukiBaseHooker)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 装载 Hook 子类。

你可以填入 `hooker` 在 Hooker 中继续装载 Hooker。

### ~~String+VariousClass.clazz *- i-ext-field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `作废`

请转移到 `toAppClass()` 方法

### ~~String.hasClass *- i-ext-field*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0` `添加`

`v1.0.93` `作废`

请转移到 `hasClass(...)` 方法

### String+VariousClass.toAppClass *- i-ext-method*

```kotlin
fun String.toAppClass(): Class<*>
```

```kotlin
fun VariousClass.toAppClass(): Class<*>
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 通过字符串类名、`VariousClass` 转换为当前 Hook APP 的实体类。

使用当前 `appClassLoader` 装载目标 `Class`。

**功能示例**

你可以轻松地将 `String` 类型的 `Class` 包名转为 `Class` 实例。

> 示例如下

```kotlin
"com.example.demo.DemoClass".toAppClass()
```

你还可以创建一个 `VariousClass`，并转换为实体类。

`VariousClass` 会枚举所有设置的 `Class` 并最终获得第一个存在的 `Class`。

> 示例如下

```kotlin
VariousClass("com.example.demo.DemoClass1", "com.example.demo.DemoClass2").toAppClass()
```

### String.hasClass *- i-ext-method*

```kotlin
fun String.hasClass(loader: ClassLoader?): Boolean
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 通过字符串类名查找是否存在。

**功能示例**

你可以轻松的使用此方法判断字符串中的类是否存在。

> 示例如下

```kotlin
if("com.example.demo.DemoClass".hasClass()) {
    // Your code here.
}
```

你还可以自定义其中的 `loader` 参数，默认为 `appClassLoader`。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
if("com.example.demo.DemoClass".hasClass(customClassLoader)) {
    // Your code here.
}
```

### findClass *- method*

```kotlin
fun findClass(name: String, loader: ClassLoader?): HookClass
```

```kotlin
fun findClass(vararg name: String, loader: ClassLoader?): VariousClass
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

移除了 ~~`findClass(various: VariousClass)`~~ 方法

`v1.0.93` `修改`

新增 `loader` 参数

**功能描述**

> 通过完整包名+名称查找需要被 Hook 的 `Class`。

!> 使用此方法会得到一个 `HookClass` 仅用于 Hook，若想查找 `Class` 请使用 `classOf`、`toAppClass` 功能。

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

若你当前需要查找的 `Class` 不属于 `appClassLoader`，你可以使用 `loader` 参数指定你要装载的 `ClassLoader`。

> 示例如下

```kotlin
val outsideLoader: ClassLoader? = ... // 假设这就是你的 ClassLoader
findClass(name = "com.example.demo.OutsideClass", loader = outsideLoader)
```

同样地，在不确定多个版本的 `Class` 以及不同名称时，也可以使用 `loader` 参数指定你要装载的 `ClassLoader`。

> 示例如下

```kotlin
val outsideLoader: ClassLoader? = ... // 假设这就是你的 ClassLoader
findClass("com.example.demo.OutsideClass1", "com.example.demo.OutsideClass2", "com.example.demo.OutsideClass3", loader = outsideLoader)
```

### ClassLoader.fetching *- i-ext-method*

```kotlin
fun ClassLoader.fetching(result: (clazz: Class<*>, resolve: Boolean) -> Unit)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 监听并 Hook 当前 `ClassLoader` 的 `ClassLoader.loadClass` 方法。

!> 请注意只有当前 `ClassLoader` 有主动使用 `ClassLoader.loadClass` 事件时才能被捕获。

!> 这是一个实验性功能，一般情况下不会用到此方法，不保证不会发生错误。

**功能示例**

针对一些使用特定 `ClassLoader` 装载 `Class` 的宿主应用，你可以使用此方法来监听 `Class` 加载情况。

!> 为了防止发生问题，你需要<u>**得到一个存在的 `ClassLoader` 实例**</u>来使用此功能。

比如我们使用 `appClassLoader`。

> 示例如下

```kotlin
appClassLoader.fetching { clazz, resolve ->
    // 得到 clazz 即加载对象
    clazz... // 这里进行你需要的操作
    // resolve 为 loadClass 的第二位参数，可参考官方文档的说明，一般情况下用不到
    resolve // 类型为 Boolean
}
```

或使用你得到的存在的 `ClassLoader` 实例，可以通过 Hook 获取。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
customClassLoader?.fetching { clazz, resolve ->
    // ...
}
```

在判断到这个 `Class` 被装载成功时，开始执行你的 Hook 功能。

> 示例如下

```kotlin
val customClassLoader: ClassLoader? = ... // 假设这个就是你的 ClassLoader
customClassLoader?.fetching { clazz, resolve ->
    if(clazz.name == /** 你需要的 Class 名称 */) {
        clazz.hook {
            // ...
        }
    }
}
```

### String+Class+VariousClass+HookClass.hook *- i-ext-method*

```kotlin
inline fun String.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin
inline fun Class<*>.hook(isForceUseAbsolute: Boolean, initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin
inline fun VariousClass.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

```kotlin
inline fun HookClass.hook(initiate: YukiMemberHookCreator.() -> Unit): YukiMemberHookCreator.Result
```

**变更记录**

`v1.0` `添加`

`v1.0.1` `修改`

新增 `VariousClass` 的直接调用 `hook` 方法

`v1.0.2` `修改`

新增 `String` 的直接调用 `hook` 方法

`v1.0.3` `修改`

新增 `YukiMemberHookCreator.Result` 返回值

`v1.0.70` `修改`

新增 `isUseAppClassLoader` 参数

`v1.0.80` `修改`

将方法体进行 inline

`v1.0.93` `修改`

移除了 ~~`isUseAppClassLoader`~~ 参数

添加了 `isForceUseAbsolute` 参数到 `Class.hook` 方法

**功能描述**

> 这是一切 Hook 的入口创建方法，Hook 方法、构造方法。

**功能示例**

如你所见，Hook 方法体的创建可使用 4 种方式。

通过字符串类名得到 `HookClass` 实例进行创建。

> 示例如下

```kotlin
"com.example.demo.DemoClass".hook {
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

默认情况下 API 会将 `Class` 实例转换为类名并绑定到 `appClassLoader`，若失败，则会使用原始 `Class` 实例直接进行 Hook。

> 示例如下

```kotlin
Stub::class.java.hook {
    // Your code here.
}
```

若当前 `Class` 不在 `appClassLoader` 且自动匹配无法找到该 `Class`，请启用 `isForceUseAbsolute`。

> 示例如下

```kotlin
YourClass::class.java.hook(isForceUseAbsolute = true) {
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

### HookResources.hook *- i-ext-method*

```kotlin
inline fun HookResources.hook(initiate: YukiResourcesHookCreator.() -> Unit)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> Hook APP 的 Resources。

!> 请注意你需要确保当前 Hook Framework 支持且 `InjectYukiHookWithXposed.isUsingResourcesHook` 已启用。

**功能示例**

Resources Hook 为固定用法，获取 `resources` 对象，然后调用 `hook` 方法开始 Hook。

> 示例如下

```kotlin
resources().hook {
    // Your code here.
}
```

!> 这是固定用法，为了防止发生问题，你不可手动实现任何 `HookResources` 实例执行 `hook` 调用。

将 Resources 的 Hook 设置为这样是为了与 `findClass(...).hook` 做到统一，使得调用起来逻辑不会混乱。

### AppLifecycle *- class*

```kotlin
inner class AppLifecycle internal constructor()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 当前 Hook APP 的生命周期实例处理类。

#### attachBaseContext *- method*

```kotlin
fun attachBaseContext(result: (baseContext: Context, hasCalledSuper: Boolean) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.attachBaseContext`。

#### onCreate *- method*

```kotlin
fun onCreate(initiate: Application.() -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.onCreate`。

#### onTerminate *- method*

```kotlin
fun onTerminate(initiate: Application.() -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.onTerminate`。

#### onLowMemory *- method*

```kotlin
fun onLowMemory(initiate: Application.() -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.onLowMemory`。

#### onTrimMemory *- method*

```kotlin
fun onTrimMemory(result: (self: Application, level: Int) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.onTrimMemory`。

#### onConfigurationChanged *- method*

```kotlin
fun onConfigurationChanged(result: (self: Application, config: Configuration) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 监听当前 Hook APP 装载 `Application.onConfigurationChanged`。

#### registerReceiver *- method*

```kotlin
fun registerReceiver(vararg action: String, result: (context: Context, intent: Intent) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 注册系统广播监听。