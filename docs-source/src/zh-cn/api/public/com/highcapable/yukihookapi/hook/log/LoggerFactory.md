---
pageClass: code-page
---

# LoggerFactory <span class="symbol">- kt</span>

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是 `YukiHookAPI` 的日志封装类，可实现同时向 `Logcat` 和 (Xposed) 宿主环境打印日志的功能。

## LoggerType <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class LoggerType
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 需要打印的日志类型。

决定于模块与 (Xposed) 宿主环境使用的打印方式。

### LOGD <span class="symbol">- enum</span>

```kotlin:no-line-numbers
LOGD
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 仅使用 `android.util.Log`。

<h3 class="deprecated">XPOSEDBRIDGE - enum</h3>

**变更记录**

`v1.1.0` `新增`

`v1.1.5` `作废`

请转移到 `XPOSED_ENVIRONMENT`

### XPOSED_ENVIRONMENT <span class="symbol">- enum</span>

```kotlin:no-line-numbers
XPOSED_ENVIRONMENT
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 仅在 (Xposed) 宿主环境使用。

::: danger

只能在 (Xposed) 宿主环境中使用，模块环境将不生效。

:::

### SCOPE <span class="symbol">- enum</span>

```kotlin:no-line-numbers
SCOPE
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 分区使用。

(Xposed) 宿主环境仅使用 `XPOSED_ENVIRONMENT`。

模块环境仅使用 `LOGD`。

### BOTH <span class="symbol">- enum</span>

```kotlin:no-line-numbers
BOTH
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 同时使用。

(Xposed) 宿主环境使用 `LOGD` 与 `XPOSED_ENVIRONMENT`。

模块环境仅使用 `LOGD`。

## YukiLoggerData <span class="symbol">- class</span>

```kotlin:no-line-numbers
data class YukiLoggerData internal constructor(
    var timestamp: Long,
    var time: String,
    var tag: String,
    var priority: String,
    var packageName: String,
    var userId: Int,
    var msg: String,
    var throwable: Throwable?
) : Serializable
```

**变更记录**

`v1.1.2` `新增`

`v1.1.4` `修改`

实现 `Serializable` 接口并标识为 `data class`

**功能描述**

> 调试日志数据实现类。

## YukiHookLogger <span class="symbol">- object</span>

```kotlin:no-line-numbers
object YukiHookLogger
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 调试日志实现类。

### inMemoryData <span class="symbol">- field</span>

```kotlin:no-line-numbers
val inMemoryData: ArrayList<YukiLoggerData>
```

**变更记录**

`v1.1.2` `新增`

`v1.1.4` `修改`

类型由 `HashSet` 修改为 `ArrayList`

**功能描述**

> 当前全部已记录的日志数据。

::: danger

获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的。

:::

### contents <span class="symbol">- field</span>

```kotlin:no-line-numbers
val contents: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获取当前日志文件内容。

如果当前没有已记录的日志会返回空字符串。

::: danger

获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的。

:::

### contents <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun contents(data: ArrayList<YukiLoggerData>): String
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 获取、格式化当前日志文件内容。

如果当前没有已记录的日志 (`data` 为空) 会返回空字符串。

::: danger

获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的。

:::

### clear <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun clear()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 清除全部已记录的日志。

你也可以直接获取 [inMemoryData](#inmemorydata-field) 来清除。

::: danger

获取到的日志数据在 Hook APP (宿主) 及模块进程中是相互隔离的。

:::

### saveToFile <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun saveToFile(fileName: String, data: ArrayList<YukiLoggerData>)
```

**变更记录**

`v1.1.0` `新增`

`v1.1.5` `修改`

新增 `data` 参数

**功能描述**

> 保存当前日志到文件。

若当前未开启 `Configs.isRecord` 或记录为空则不会进行任何操作。

日志文件会追加到 `fileName` 的文件结尾，若文件不存在会自动创建。

::: danger 

文件读写权限取决于当前宿主、模块已获取的权限。

:::

### Configs <span class="symbol">- object</span>

```kotlin:no-line-numbers
object Configs
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 配置 `YukiHookLogger`。

#### TAG <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val TAG: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 标签。

#### PRIORITY <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val PRIORITY: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 优先级。

#### PACKAGE_NAME <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val PACKAGE_NAME: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 当前宿主的包名。

#### USER_ID <span class="symbol">- field</span>

```kotlin:no-line-numbers
const val USER_ID: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 当前宿主的用户 ID (主用户不显示)。

#### isEnable <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isEnable: Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 是否启用调试日志的输出功能。

关闭后将会停用 `YukiHookAPI` 对全部日志的输出。

但是不影响当你手动调用下面这些方法输出日志。

`loggerD`、`loggerI`、`loggerW`、`loggerE`。

当 `isEnable` 关闭后 `YukiHookAPI.Configs.isDebug` 也将同时关闭。

#### isRecord <span class="symbol">- field</span>

```kotlin:no-line-numbers
var isRecord: Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 是否启用调试日志的记录功能。

开启后将会在内存中记录全部可用的日志和异常堆栈。

需要同时启用 [isEnable](#isenable-field) 才能有效。

::: danger

过量的日志可能会导致宿主运行缓慢或造成频繁 GC。

:::

开启后你可以调用 [YukiHookLogger.saveToFile](#savetofile-method) 实时保存日志到文件或使用 [YukiHookLogger.contents](#contents-field) 获取实时日志文件。

#### tag <span class="symbol">- field</span>

```kotlin:no-line-numbers
var tag: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 这是一个调试日志的全局标识。

默认文案为 `YukiHookAPI`。

你可以修改为你自己的文案。

#### elements <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun elements(vararg item: Int)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 自定义调试日志对外显示的元素。

只对日志记录和 (Xposed) 宿主环境的日志生效。

日志元素的排列将按照你在 `item` 中设置的顺序进行显示。

你还可以留空 `item` 以不显示除日志内容外的全部元素。

可用的元素有：`TAG`、`PRIORITY`、`PACKAGE_NAME`、`USER_ID`。

**功能示例**

打印的日志样式将按照你设置的排列顺序和元素内容进行。

> 示例如下

```kotlin
elements(TAG, PRIORITY, PACKAGE_NAME, USER_ID)
```

以上内容定义的日志将显示为如下样式。

> 示例如下

```:no-line-numbers
[YukiHookAPI][D][com.demo.test][999]--> This is a log
```

如果我们调整元素顺序以及减少个数，那么结果又会不一样。

> 示例如下

```kotlin
elements(PACKAGE_NAME, USER_ID, PRIORITY)
```

以上内容定义的日志将显示为如下样式。

> 示例如下

```:no-line-numbers
[com.demo.test][999][D]--> This is a log
```

## loggerD <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun loggerD(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 (Xposed) 宿主环境打印日志，级别 `D`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

## loggerI <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun loggerI(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 (Xposed) 宿主环境打印日志，级别 `I`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

## loggerW <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun loggerW(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 (Xposed) 宿主环境打印日志，级别 `W`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

## loggerE <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun loggerE(tag: String, msg: String, e: Throwable?, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 (Xposed) 宿主环境打印日志，级别 `E`，可携带 `e` 异常信息，将打印异常堆栈。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。