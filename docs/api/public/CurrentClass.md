## CurrentClass [class]

```kotlin
class CurrentClass(private val instance: Class<*>, private val self: Any)
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 当前实例的类操作对象。

### field [method]

```kotlin
fun field(initiate: FieldFinder.() -> Unit): FieldFinder.Result.Instance
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 调用当前实例中的变量。

### method [method]

```kotlin
fun method(initiate: MethodFinder.() -> Unit): MethodFinder.Result.Instance
```

<b>变更记录</b>

`v1.0.70` `新增`

<b>功能描述</b>

> 调用当前实例中的方法。