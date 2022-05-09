## YukiHookAPI [object]

```kotlin
object YukiHookAPI
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是 `YukiHookAPI` 的 API 调用总类，Hook 相关功能的开始、Hook 相关功能的配置都在这里。

### API_VERSION_NAME [field]

```kotlin
const val API_VERSION_NAME: String
```

**变更记录**

`v1.0.4` `新增`

**功能描述**

> 获取当前 `YukiHookAPI` 的版本。

### API_VERSION_CODE [field]

```kotlin
const val API_VERSION_CODE: Int
```

**变更记录**

`v1.0.4` `新增`

**功能描述**

> 获取当前 `YukiHookAPI` 的版本号。

### executorName [field]

```kotlin
val executorName: String
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 获取当前 Hook 框架的名称。

无法获取会返回 `unknown`，`XposedBridge` 不存在会返回 `invalid`。

### executorVersion [field]

```kotlin
val executorVersion: Int
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 获取当前 Hook 框架的版本。

无法获取会返回 `-1`。

### Configs [object]

```kotlin
object Configs
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 对 API 相关功能的配置类。

#### debugTag [field]

```kotlin
var debugTag: String
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 模块在调用 `logger` 时打印的日志 `TAG` 名称。

你可以方便地进行自定义，并可以在 `Logcat` 和 `XposedBridge.log` 中找到它们。

#### isDebug [field]

```kotlin
var isDebug: Boolean
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 是否启用 DEBUG 模式。

默认为开启状态，开启后模块将会向 `Logcat` 和 `XposedBridge.log` 打印详细的 Hook 日志，关闭后仅会打印 `E` 级别的日志。

#### isAllowPrintingLogs [field]

```kotlin
var isAllowPrintingLogs: Boolean
```

**变更记录**

`v1.0.4` `新增`

**功能描述**

> 是否启用调试日志的输出功能。

!> 关闭后将会停用 `YukiHookAPI` 对全部日志的输出，但是不影响当你手动调用日志方法输出日志。

#### isEnableModulePrefsCache [field]

```kotlin
var isEnableModulePrefsCache: Boolean
```

**变更记录**

`v1.0.5` `新增`

**功能描述**

> 是否启用 `YukiHookModulePrefs` 的键值缓存功能。

为防止内存复用过高问题，此功能默认启用。

你可以手动在 `YukiHookModulePrefs` 中自由开启和关闭缓存功能以及清除缓存。

#### isEnableModuleAppResourcesCache [field]

```kotlin
var isEnableModuleAppResourcesCache: Boolean
```

**变更记录**

`v1.0.87` `新增`

**功能描述**

> 是否启用当前 Xposed 模块自身 `Resources` 缓存功能。

为防止内存复用过高问题，此功能默认启用。

你可以手动调用 `PackageParam.refreshModuleAppResources` 来刷新缓存。

!> 关闭后每次使用 `PackageParam.moduleAppResources` 都会重新创建，可能会造成运行缓慢。

#### isEnableMemberCache [field]

```kotlin
var isEnableMemberCache: Boolean
```
ø
**变更记录**

`v1.0.68` `新增`

`v1.0.80` `修改`

将方法体进行 inline

**功能描述**

> 是否启用 `Member` 缓存功能。

为防止 `Member` 复用过高造成的系统 GC 问题，此功能默认启用。

启用后会缓存已经找到的 `Class`、`Method`、`Constructor`、`Field`。

缓存的 `Member` 都将处于 `MemberCacheStore` 的全局静态实例中。

推荐使用 `MethodFinder`、`ConstructorFinder`、`FieldFinder` 来获取 `Member`。

除非缓存的 `Member` 发生了混淆的问题，例如使用 R8 混淆后的 APP 的目标 `Member`，否则建议启用。

### configs [method]

```kotlin
inline fun configs(initiate: Configs.() -> Unit)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 对 `Configs` 类实现了一个 `lambda` 方法体。

你可以轻松的调用它进行配置。

**功能示例**

你可以在 `HookEntryClass` 的 `onInit` 方法中调用 `configs` 方法完成对 API 的功能配置，实时生效。

> 示例如下

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() {
        YukiHookAPI.configs {
            debugTag = "YukiHookAPI"
            isDebug = true
            isAllowPrintingLogs = true
            isEnableModulePrefsCache = true
            isEnableModuleAppResourcesCache = true
            isEnableMemberCache = true
        }
    }

    override fun onHook() {
        // Your code here.
    }
}
```

若觉得上面的写法不美观，你还可以写得更加简洁。

> 示例如下

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugTag = "YukiHookAPI"
        isDebug = true
        isAllowPrintingLogs = true
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableMemberCache = true
    }

    override fun onHook() {
        // Your code here.
    }
}
```

你也可以不通过 `configs` 方法，直接进行配置。

> 示例如下

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() {
        YukiHookAPI.Configs.debugTag = "YukiHookAPI"
        YukiHookAPI.Configs.isDebug = true
        YukiHookAPI.Configs.isAllowPrintingLogs = true
        YukiHookAPI.Configs.isEnableModulePrefsCache = true
        YukiHookAPI.Configs.isEnableModuleAppResourcesCache = true
        YukiHookAPI.Configs.isEnableMemberCache = true
    }

    override fun onHook() {
        // Your code here.
    }
}
```

### encase [method]

```kotlin
fun encase(initiate: PackageParam.() -> Unit)
```

```kotlin
fun encase(vararg hooker: YukiBaseHooker)
```

```kotlin
fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit)
```

```kotlin
fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 装载 Hook 入口的核心方法。

**功能示例**

详情请参考

- [通过 Lambda 创建](config/api-example?id=通过-lambda-创建)

- [通过自定义 Hooker 创建](config/api-example?id=通过自定义-hooker-创建)

- [作为 Hook API 使用需要注意的地方](config/api-example?id=作为-hook-api-使用需要注意的地方)