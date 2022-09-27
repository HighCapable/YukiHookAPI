---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiHookAPI <span class="symbol">- object</span>

```kotlin:no-line-numbers
object YukiHookAPI
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 这是 `YukiHookAPI` 的 API 调用总类，Hook 相关功能的开始、Hook 相关功能的配置都在这里。

## API_VERSION_NAME <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val API_VERSION_NAME: String
```

**Change Records**

`v1.0.4` `added`

**Function Illustrate**

> 获取当前 `YukiHookAPI` 的版本。

## API_VERSION_CODE <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val API_VERSION_CODE: Int
```

**Change Records**

`v1.0.4` `added`

**Function Illustrate**

> 获取当前 `YukiHookAPI` 的版本号。

<h2 class="deprecated">executorName - field</h2>

**Change Records**

`v1.0.5` `added`

`v1.0.91` `removed`

请转移到 `Status.executorName`

<h2 class="deprecated">executorVersion - field</h2>

**Change Records**

`v1.0.5` `added`

`v1.0.91` `removed`

请转移到 `Status.executorVersion`

## Status <span class="symbol">- object</span>

```kotlin:no-line-numbers
object Status
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 当前 `YukiHookAPI` 的状态。

### compiledTimestamp <span class="symbol">- field</span>

```kotlin:no-line-numbers
val compiledTimestamp: Long
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获取项目编译完成的时间戳 (当前本地时间)。

### isXposedEnvironment <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isXposedEnvironment: Boolean
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获取当前是否为 (Xposed) 宿主环境。

### executorName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val executorName: String
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 获取当前 Hook 框架的名称。

无法获取会返回 `unknown`，`XposedBridge` 不存在会返回 `invalid`。

::: warning

在模块环境中需要启用 **Configs.isEnableHookModuleStatus**。

:::

### executorVersion <span class="symbol">- field</span>

```kotlin:no-line-numbers
val executorVersion: Int
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 获取当前 Hook 框架的版本。

无法获取会返回 `-1`。

::: warning

在模块环境中需要启用 **Configs.isEnableHookModuleStatus**。

:::

### isModuleActive <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isModuleActive: Boolean
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 判断模块是否在 Xposed 或太极、无极中激活。

::: warning

在模块环境中你需要将 **Application** 继承于 **ModuleApplication**。

在模块环境中需要启用 **Configs.isEnableHookModuleStatus**。

在 (Xposed) 宿主环境中仅返回非 **isTaiChiModuleActive** 的激活状态。

:::

### isXposedModuleActive <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isXposedModuleActive: Boolean
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 仅判断模块是否在 Xposed 中激活。

::: warning

在模块环境中需要启用 **Configs.isEnableHookModuleStatus**。

在 (Xposed) 宿主环境中始终返回 true。

:::

### isTaiChiModuleActive <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isTaiChiModuleActive: Boolean
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 仅判断模块是否在太极、无极中激活。

::: warning

在模块环境中你需要将 **Application** 继承于 **ModuleApplication**。

在 (Xposed) 宿主环境中始终返回 false。

:::

### isSupportResourcesHook <span class="symbol">- field</span>

```kotlin:no-line-numbers
val isSupportResourcesHook: Boolean
```

**Change Records**

`v1.0.91` `added`

**Function Illustrate**

> 判断当前 Hook Framework 是否支持资源钩子(Resources Hook)。

::: warning

在模块环境中需要启用 **Configs.isEnableHookModuleStatus**。

在 (Xposed) 宿主环境中可能会延迟等待事件回调后才会返回 true。

请注意你需要确保 **InjectYukiHookWithXposed.isUsingResourcesHook** 已启用，否则始终返回 false。

:::

## Configs <span class="symbol">- object</span>

```kotlin:no-line-numbers
object Configs
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 对 API 相关功能的配置类。

### debugLog <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun debugLog(initiate: YukiHookLogger.Configs.() -> Unit)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 配置 `YukiHookLogger.Configs` 相关参数。

<h3 class="deprecated">debugTag - field</h3>

**Change Records**

`v1.0` `first`

`v1.1.0` `deprecated`

请转移到 `YukiHookLogger.Configs.tag`

### isDebug <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isDebug: Boolean
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 是否启用 DEBUG 模式。

默认为开启状态，开启后模块将会向 `Logcat` 和 `XposedBridge.log` 打印详细的 Hook 日志，关闭后仅会打印 `E` 级别的日志。

<h3 class="deprecated">isAllowPrintingLogs - field</h3>

**Change Records**

`v1.0.4` `added`

`v1.1.0` `deprecated`

请转移到 `YukiHookLogger.Configs.isEnable`

### isEnableModulePrefsCache <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableModulePrefsCache: Boolean
```

**Change Records**

`v1.0.5` `added`

**Function Illustrate**

> 是否启用 `YukiHookModulePrefs` 的键值缓存功能。

为防止内存复用过高问题，此功能默认启用。

你可以手动在 `YukiHookModulePrefs` 中自由开启和关闭缓存功能以及清除缓存。

### isEnableModuleAppResourcesCache <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableModuleAppResourcesCache: Boolean
```

**Change Records**

`v1.0.87` `added`

**Function Illustrate**

> 是否启用当前 Xposed 模块自身 `Resources` 缓存功能。

为防止内存复用过高问题，此功能默认启用。

你可以手动调用 `PackageParam.refreshModuleAppResources` 来刷新缓存。

::: warning

关闭后每次使用 **PackageParam.moduleAppResources** 都会重新创建，可能会造成运行缓慢。

:::

### isEnableHookModuleStatus <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableHookModuleStatus: Boolean
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 是否启用 Hook Xposed 模块激活等状态功能.

为原生支持 Xposed 模块激活状态检测，此功能默认启用。

::: warning

关闭后你将不能再在模块环境中使用 **YukiHookAPI.Status** 中的激活状态判断功能。

:::

### isEnableHookSharedPreferences <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableHookSharedPreferences: Boolean
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 是否启用 Hook `SharedPreferences`。

启用后将在模块启动时强制将 `SharedPreferences` 文件权限调整为 `Context.MODE_WORLD_READABLE` (0664)。

::: warning

这是一个可选的实验性功能，此功能默认不启用。

仅用于修复某些系统可能会出现在启用了 **New XSharedPreferences** 后依然出现文件权限错误问题，若你能正常使用 **YukiHookModulePrefs** 就不建议启用此功能。

:::

### isEnableDataChannel <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableDataChannel: Boolean
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 是否启用当前 Xposed 模块与宿主交互的 `YukiHookDataChannel` 功能。

请确保 Xposed 模块的 `Application` 继承于 `ModuleApplication` 才能有效。

此功能默认启用，关闭后将不会在功能初始化的时候装载 `YukiHookDataChannel`。

### isEnableMemberCache <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnableMemberCache: Boolean
```

**Change Records**

`v1.0.68` `added`

`v1.0.80` `modified`

将方法体进行 inline

**Function Illustrate**

> 是否启用 `Member` 缓存功能。

为防止 `Member` 复用过高造成的系统 GC 问题，此功能默认启用。

启用后会缓存已经找到的 `Method`、`Constructor`、`Field`。

缓存的 `Member` 都将处于 `MemberCacheStore` 的全局静态实例中。

推荐使用 `MethodFinder`、`ConstructorFinder`、`FieldFinder` 来获取 `Member`。

除非缓存的 `Member` 发生了混淆的问题，例如使用 R8 混淆后的 APP 的目标 `Member`，否则建议启用。

## configs <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun configs(initiate: Configs.() -> Unit)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 对 `Configs` 类实现了一个 `lambda` 方法体。

你可以轻松的调用它进行配置。

**Function Example**

你可以在 `HookEntryClass` 的 `onInit` 方法中调用 `configs` 方法和 `debugLog` 方法完成对 API 的功能配置，实时生效。

> The following example

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() {
        YukiHookAPI.configs {
            debugLog {
                tag = "YukiHookAPI"
                isEnable = true
                isRecord = false
                elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
            }
            isDebug = BuildConfig.DEBUG
            isEnableModulePrefsCache = true
            isEnableModuleAppResourcesCache = true
            isEnableHookModuleStatus = true
            isEnableHookSharedPreferences = false
            isEnableDataChannel = true
            isEnableMemberCache = true
        }
    }

    override fun onHook() {
        // Your code here.
    }
}
```

若觉得上面的写法不美观，你还可以写得更加简洁。

> The following example

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() = configs {
        debugLog {
            tag = "YukiHookAPI"
            isEnable = true
            isRecord = false
            elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
        }
        isDebug = BuildConfig.DEBUG
        isEnableModulePrefsCache = true
        isEnableModuleAppResourcesCache = true
        isEnableHookModuleStatus = true
        isEnableHookSharedPreferences = false
        isEnableDataChannel = true
        isEnableMemberCache = true
    }

    override fun onHook() {
        // Your code here.
    }
}
```

你也可以不通过 `configs` 和 `debugLog` 方法，直接进行配置。

> The following example

```kotlin
class HookEntryClass : IYukiHookXposedInit {

    override fun onInit() {
        YukiHookLogger.Configs.tag = "YukiHookAPI"
        YukiHookLogger.Configs.isEnable = true
        YukiHookLogger.Configs.isRecord = false
        YukiHookLogger.Configs.elements(
            YukiHookLogger.Configs.TAG,
            YukiHookLogger.Configs.PRIORITY,
            YukiHookLogger.Configs.PACKAGE_NAME,
            YukiHookLogger.Configs.USER_ID
        )
        YukiHookAPI.Configs.isDebug = BuildConfig.DEBUG
        YukiHookAPI.Configs.isEnableModulePrefsCache = true
        YukiHookAPI.Configs.isEnableModuleAppResourcesCache = true
        YukiHookAPI.Configs.isEnableHookModuleStatus = true
        YukiHookAPI.Configs.isEnableHookSharedPreferences = false
        YukiHookAPI.Configs.isEnableDataChannel = true
        YukiHookAPI.Configs.isEnableMemberCache = true
    }

    override fun onHook() {
        // Your code here.
    }
}
```

## encase <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun encase(initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun encase(vararg hooker: YukiBaseHooker)
```

```kotlin:no-line-numbers
fun encase(baseContext: Context?, initiate: PackageParam.() -> Unit)
```

```kotlin:no-line-numbers
fun encase(baseContext: Context?, vararg hooker: YukiBaseHooker)
```

**Change Records**

`v1.0` `first`

**Function Illustrate**

> 装载 Hook 入口的核心方法。

**Function Example**

详情请参考

- [Created by lambda](../../../../../config/api-example#created-by-lambda)

- [Created by Custom Hooker](../../../../../config/api-example#created-by-custom-hooker)

- [Precautions when using as Hook API](../../../../../config/api-example#precautions-when-using-as-hook-api)