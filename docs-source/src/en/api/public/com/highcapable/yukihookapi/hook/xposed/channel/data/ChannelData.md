---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ChannelData <span class="symbol">- class</span>

```kotlin:no-line-numbers
data class ChannelData<T>(var key: String, var value: T?) : Serializable
```

**Change Records**

`v1.0.88` `added`

`v1.1.5` `modified`

实现了 `Serializable` 接口

**Function Illustrate**

> 数据通讯桥键值构造类。

这个类是对 `YukiHookDataChannel` 的一个扩展用法。

**Function Example**

建立一个模板类定义模块与宿主需要发送的键值数据。

> The following example

```kotlin
object DataConst {

    val TEST_KV_DATA_1 = ChannelData("test_data_1", "defalut value")
    val TEST_KV_DATA_2 = ChannelData("test_data_2", 0)
}
```

键值数据定义后，你就可以方便地在模块和宿主中调用所需要发送的数据。

> 模块示例如下

```kotlin
// 从指定包名的宿主获取
dataChannel(packageName = "com.example.demo").wait(DataConst.TEST_KV_DATA_1) { value ->
    // Your code here.
}
// 发送给指定包名的宿主 - 未填写 value 时将使用模板提供的默认值
dataChannel(packageName = "com.example.demo").put(DataConst.TEST_KV_DATA_1, value = "sending value")
```

> 宿主示例如下

```kotlin
// 从模块获取
dataChannel.wait(DataConst.TEST_KV_DATA_1) { value ->
    // Your code here.
}
// 发送给模块 - 未填写 value 时将使用模板提供的默认值
dataChannel.put(DataConst.TEST_KV_DATA_1, value = "sending value")
```

你依然可以不使用模板定义的默认值，随时修改你的默认值。

> The following example

```kotlin
// 获取 - 此时 value 取到的默认值将会是 2 - 并不是模板提供的 0
dataChannel.wait(DataConst.TEST_KV_DATA_2, value = 2) { value ->
    // Your code here.
}
```