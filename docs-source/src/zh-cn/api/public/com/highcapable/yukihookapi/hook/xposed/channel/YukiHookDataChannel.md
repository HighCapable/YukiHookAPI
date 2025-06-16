---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiHookDataChannel <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiHookDataChannel private constructor()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 实现 Xposed 模块的数据通讯桥。

通过模块与宿主相互注册 `BroadcastReceiver` 来实现数据的交互。

模块需要将 `Application` 继承于 `ModuleApplication` 来实现此功能。

::: danger

模块与宿主需要保持存活状态，否则无法建立通讯。

:::

## NameSpace <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class NameSpace internal constructor(private val context: Context?, private val packageName: String)
```

**变更记录**

`v1.0.88` `新增`

`v1.0.90` `修改`

新增 `isSecure` 参数

`v1.1.9` `修改`

移除 `isSecure` 参数

**功能描述**

> `YukiHookDataChannel` 命名空间。

### with <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun with(initiate: NameSpace.() -> Unit): NameSpace
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 创建一个调用空间。

### dataMaxByteSize <span class="symbol">- field</span>

```kotlin:no-line-numbers
var dataMaxByteSize: Int
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> `YukiHookDataChannel` 允许发送的最大数据字节大小。

默认为 `500 KB (500 * 1024)`，详情请参考 `receiverDataMaxByteSize` 的注释。

最小不能低于 `100 KB (100 * 1024)`，否则会被重新设置为 `100 KB (100 * 1024)`。

设置后将在全局生效，直到当前进程结束。

超出最大数据字节大小后的数据将被自动分段发送。

::: danger

请谨慎调整此参数，如果超出了系统能够允许的大小会引发 **TransactionTooLargeException** 异常。

:::

### dataMaxByteCompressionFactor <span class="symbol">- field</span>

```kotlin:no-line-numbers
var dataMaxByteCompressionFactor: Int
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> `YukiHookDataChannel` 允许发送的最大数据字节大小倍数 (分段数据)。

默认为 `3`，详情请参考 `receiverDataMaxByteCompressionFactor` 的注释。

最小不能低于 `2`，否则会被重新设置为 `2`。

设置后将在全局生效，直到当前进程结束。

超出最大数据字节大小后的数据将按照此倍数自动划分 `receiverDataMaxByteSize` 的大小。

::: danger

请谨慎调整此参数，如果超出了系统能够允许的大小会引发 **TransactionTooLargeException** 异常。

:::

### allowSendTooLargeData <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun allowSendTooLargeData(): NameSpace
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 解除发送数据的大小限制并禁止开启分段发送功能。

仅会在每次调用时生效，下一次没有调用此方法则此功能将被自动关闭。

你还需要在整个调用域中声明注解 `SendTooLargeChannelData` 以消除警告。

::: danger

若你不知道允许此功能会带来何种后果，请勿使用。

:::

### put <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> put(key: String, value: T)
```

```kotlin:no-line-numbers
fun <T> put(data: ChannelData<T>, value: T?)
```

```kotlin:no-line-numbers
fun put(vararg data: ChannelData<*>)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 发送键值数据。

### put <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun put(key: String)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 仅发送键值监听，使用默认值 `VALUE_WAIT_FOR_LISTENER` 发送键值数据。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> wait(key: String, priority: ChannelPriority?, result: (value: T) -> Unit)
```

```kotlin:no-line-numbers
fun <T> wait(data: ChannelData<T>, priority: ChannelPriority?, result: (value: T) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

`v1.0.90` `修改`

移除默认值 `value`

`v1.1.5` `修改`

新增 `priority` 参数

**功能描述**

> 获取键值数据。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(key: String, priority: ChannelPriority?, callback: () -> Unit)
```

**变更记录**

`v1.0.88` `新增`

`v1.1.5` `修改`

新增 `priority` 参数

**功能描述**

> 仅获取监听结果，不获取键值数据。

::: danger

仅限使用 **VALUE_WAIT_FOR_LISTENER** 发送的监听才能被接收。

:::

### checkingVersionEquals <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun checkingVersionEquals(priority: ChannelPriority?, result: (Boolean) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

`v1.1.5` `修改`

新增 `priority` 参数

**功能描述**

> 获取模块与宿主的版本是否匹配。

通过此方法可原生判断 Xposed 模块更新后宿主并未重新装载造成两者不匹配的情况。

### obtainLoggerInMemoryData <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun obtainLoggerInMemoryData(priority: ChannelPriority?, result: (List<YLogData>) -> Unit)
```

**变更记录**

`v1.1.4` `新增`

`v1.1.5` `修改`

新增 `priority` 参数

**功能描述**

> 获取模块与宿主之间的 `List<YLogData>` 数据。

由于模块与宿主处于不同的进程，我们可以使用数据通讯桥访问各自的调试日志数据。

::: danger

模块与宿主必须启用 [YLog.Configs.isRecord](../../log/YLog#isrecord-field) 才能获取到调试日志数据。

由于 Android 限制了数据传输大小的最大值，如果调试日志过多将会自动进行分段发送，数据越大速度越慢。

:::