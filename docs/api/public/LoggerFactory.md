## LoggerFactory [kt]

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 这是 `YukiHookAPI` 的日志封装类，可实现同时向 `Logcat` 和 `XposedBridge.log` 打印日志的功能。

### loggerD [method]

```kotlin
fun loggerD(tag: String, msg: String)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `D`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerI [method]

```kotlin
fun loggerI(tag: String, msg: String)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `I`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerW [method]

```kotlin
fun loggerW(tag: String, msg: String)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `W`。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。

### loggerE [method]

```kotlin
fun loggerE(tag: String, msg: String, e: Throwable?)
```

<b>变更记录</b>

`v1.0` `添加`

<b>功能描述</b>

> 向 `Logcat` 和 `XposedBridge` 打印日志，级别 `E`，可携带 `e` 异常信息，将打印异常堆栈。

`tag` 的默认参数为 `YukiHookAPI.Configs.debugTag`，你可以进行自定义。