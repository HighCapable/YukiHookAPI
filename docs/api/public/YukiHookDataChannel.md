## YukiHookDataChannel [class]

```kotlin
class YukiHookDataChannel private constructor()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 实现 Xposed 模块的数据通讯桥。

通过模块与宿主相互注册 `BroadcastReceiver` 来实现数据的交互。

模块需要将 `Application` 继承于 `ModuleApplication` 来实现此功能。

!> 模块与宿主需要保持存活状态，否则无法建立通讯。

### NameSpace [class]

```kotlin
inner class NameSpace internal constructor(private val context: Context?, private val packageName: String)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> `YukiHookDataChannel` 命名空间。

#### with [method]

```kotlin
inline fun with(initiate: NameSpace.() -> Unit): NameSpace
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 创建一个调用空间。

#### put [method]

```kotlin
fun <T> put(key: String, value: T)
```

```kotlin
fun <T> put(data: ChannelData<T>, value: T?)
```

```kotlin
fun put(vararg data: ChannelData<*>)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 发送键值数据。

**功能示例**

通过使用 `dataChannel` 来实现模块与宿主之间的通讯桥，原理为发送接收系统无序广播。

> 模块示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").wait<String>(key = "key_from_host") { value ->
    // Your code here.
}
// 发送给指定包名的宿主
dataChannel(packageName = "com.example.demo").put(key = "key_from_module", value = "I am module")
```

> 宿主示例如下

```kotlin
// 从模块获取
dataChannel.wait<String>(key = "key_from_module") { value ->
    // Your code here.
}
// 发送给模块
dataChannel.put(key = "key_from_host", value = "I am host")
```

!> 接收方需要保持存活状态才能收到通讯数据。

#### put [method]

```kotlin
fun put(key: String)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 仅发送键值监听，使用默认值 `VALUE_WAIT_FOR_LISTENER` 发送键值数据。

**功能示例**

你可以不设置 `dataChannel` 的 `value` 来达到仅通知模块或宿主回调 `wait` 方法。

> 模块示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").wait(key = "listener_from_host") {
    // Your code here.
}
// 发送给指定包名的宿主
dataChannel(packageName = "com.example.demo").put(key = "listener_from_module")
```

> 宿主示例如下

```kotlin
// 从模块获取
dataChannel.wait(key = "listener_from_module") {
    // Your code here.
}
// 发送给模块
dataChannel.put(key = "listener_from_host")
```
 
!> 接收方需要保持存活状态才能收到通讯数据。

#### wait [method]

```kotlin
fun <T> wait(key: String, value: T?, result: (value: T) -> Unit)
```

```kotlin
fun <T> wait(data: ChannelData<T>, value: T?, result: (value: T) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 获取键值数据。

**功能示例**

参考第一个 `put` 方法的功能示例。

#### wait [method]

```kotlin
fun wait(key: String, result: () -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 仅获取监听结果，不获取键值数据。

!> 仅限使用 `VALUE_WAIT_FOR_LISTENER` 发送的监听才能被接收。

**功能示例**

参考第二个 `put` 方法的功能示例。

#### checkingVersionEquals [method]

```kotlin
fun checkingVersionEquals(result: (Boolean) -> Unit)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 获取模块与宿主的版本是否匹配。

通过此方法可原生判断 Xposed 模块更新后宿主并未重新装载造成两者不匹配的情况。

**功能示例**

你可以在模块中判断指定包名的宿主是否与当前模块的版本匹配。

> 示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").checkingVersionEquals { isEquals ->
    // Your code here.
}
```

你还可以在宿主中判断是否自身与当前模块的版本匹配。

> 示例如下

```kotlin
// 从模块获取
dataChannel.checkingVersionEquals { isEquals ->
    // Your code here.
}
```

!> 方法回调的条件为宿主、模块保持存活状态，并在激活模块后重启了作用域中的 Hook 目标宿主对象。