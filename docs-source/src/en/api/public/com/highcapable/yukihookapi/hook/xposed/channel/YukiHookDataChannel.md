---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiHookDataChannel <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiHookDataChannel private constructor()
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 实现 Xposed 模块的数据通讯桥。

通过模块与宿主相互注册 `BroadcastReceiver` 来实现数据的交互。

模块需要将 `Application` 继承于 `ModuleApplication` 来实现此功能。

::: danger

模块与宿主需要保持存活状态，否则无法建立通讯。

:::

## NameSpace <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class NameSpace internal constructor(private val context: Context?, private val packageName: String, private val isSecure: Boolean)
```

**Change Records**

`v1.0.88` `added`

`v1.0.90` `modified`

新增 `isSecure` 参数

**Function Illustrate**

> `YukiHookDataChannel` 命名空间。

### with <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun with(initiate: NameSpace.() -> Unit): NameSpace
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 创建一个调用空间。

### allowSendTooLargeData <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun allowSendTooLargeData(): NameSpace
```

**变更记录**

`v1.1.5` `added`

**功能描述**

> 解除发送数据的大小限制并禁止开启分段发送功能。

仅会在每次调用时生效，下一次没有调用此方法则此功能将被自动关闭。

你还需要在整个调用域中声明注解 `CauseProblemsApi` 以消除警告。

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

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 发送键值数据。

### put <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun put(key: String)
```

**Change Records**

`v1.0.88` `added`

**Function Illustrate**

> 仅发送键值监听，使用默认值 `VALUE_WAIT_FOR_LISTENER` 发送键值数据。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun <T> wait(key: String, priority: ChannelPriority?, result: (value: T) -> Unit)
```

```kotlin:no-line-numbers
fun <T> wait(data: ChannelData<T>, priority: ChannelPriority?, result: (value: T) -> Unit)
```

**Change Records**

`v1.0.88` `added`

`v1.0.90` `modified`

移除默认值 `value`

`v1.1.5` `modified`

新增 `priority` 参数

**Function Illustrate**

> 获取键值数据。

### wait <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun wait(key: String, priority: ChannelPriority?, callback: () -> Unit)
```

**Change Records**

`v1.0.88` `added`

`v1.1.5` `modified`

新增 `priority` 参数

**Function Illustrate**

> 仅获取监听结果，不获取键值数据。

::: danger

仅限使用 **VALUE_WAIT_FOR_LISTENER** 发送的监听才能被接收。

:::

### checkingVersionEquals <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun checkingVersionEquals(priority: ChannelPriority?, result: (Boolean) -> Unit)
```

**Change Records**

`v1.0.88` `added`

`v1.1.5` `modified`

新增 `priority` 参数

**Function Illustrate**

> 获取模块与宿主的版本是否匹配。

通过此方法可原生判断 Xposed 模块更新后宿主并未重新装载造成两者不匹配的情况。

### obtainLoggerInMemoryData <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun obtainLoggerInMemoryData(priority: ChannelPriority?, result: (ArrayList<YukiLoggerData>) -> Unit)
```

**Change Records**

`v1.1.4` `added`

`v1.1.5` `modified`

新增 `priority` 参数

**Function Illustrate**

> 获取模块与宿主之间的 `ArrayList<YukiLoggerData>` 数据。

由于模块与宿主处于不同的进程，我们可以使用数据通讯桥访问各自的调试日志数据。

::: danger

模块与宿主必须启用 [YukiHookLogger.Configs.isRecord](../../log/LoggerFactory#isrecord-field) 才能获取到调试日志数据。

由于 Android 限制了数据传输大小的最大值，如果调试日志过多可能会造成 **TransactionTooLargeException** 异常。

:::