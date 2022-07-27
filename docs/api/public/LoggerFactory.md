## LoggerFactory [kt]

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是 `YukiHookAPI` 的日志封装类，可实现同时向 `Logcat` 和 `XposedBridge.log` 打印日志的功能。

### LoggerType [class]

```kotlin
enum class LoggerType
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 需要打印的日志类型。

决定于模块与 (Xposed) 宿主环境使用的打印方式。

#### LOGD [enum]

```kotlin
LOGD
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 仅使用 `android.util.Log`。

#### XPOSEDBRIDGE [enum]

```kotlin
XPOSEDBRIDGE
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 仅使用 `XposedBridge.log`。

!> 只能在 (Xposed) 宿主环境中使用，模块环境将不生效。

#### SCOPE [enum]

```kotlin
SCOPE
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 分区使用。

(Xposed) 宿主环境仅使用 `XPOSEDBRIDGE`。

模块环境仅使用 `LOGD`。

#### BOTH [enum]

```kotlin
BOTH
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 同时使用。

(Xposed) 宿主环境使用 `LOGD` 与 `XPOSEDBRIDGE`。

模块环境仅使用 `LOGD`。

### loggerD [method]

```kotlin
fun loggerD(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `D`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerI [method]

```kotlin
fun loggerI(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `I`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerW [method]

```kotlin
fun loggerW(tag: String, msg: String, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `W`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerE [method]

```kotlin
fun loggerE(tag: String, msg: String, e: Throwable?, type: LoggerType)
```

**变更记录**

`v1.0` `添加`

`v1.0.93` `修改`

新增 `type` 参数

**功能描述**

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `E`，可携带 `e` 异常信息，将打印异常堆栈。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。