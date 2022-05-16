## CurrentClass [class]

```kotlin
class CurrentClass(internal val instance: Class<*>, internal val self: Any)
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 当前实例的类操作对象。

### superClass [method]

```kotlin
fun superClass(): SuperClass
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例。

### field [method]

```kotlin
inline fun field(initiate: FieldFinder.() -> Unit): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的变量。

### method [method]

```kotlin
inline fun method(initiate: MethodFinder.() -> Unit): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的方法。

### SuperClass [class]

```kotlin
inner class SuperClass internal constructor()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 当前类的父类实例的类操作对象。

#### field [method]

```kotlin
inline fun field(initiate: FieldFinder.() -> Unit): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的变量。

#### method [method]

```kotlin
inline fun method(initiate: MethodFinder.() -> Unit): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的方法。