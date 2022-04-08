## ModifierRules [class]

```kotlin
class ModifierRules
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 这是一个 `Member` 描述符定义类。

可对 R8 混淆后的 `Member` 进行更加详细的定位。

### asPublic

```kotlin
fun asPublic()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `public`。

### asPrivate

```kotlin
fun asPrivate()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `private`。

### asProtected

```kotlin
fun asProtected()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `protected`。

### asStatic

```kotlin
fun asStatic()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `static`。

对于任意的静态 `Member` 可添加此描述进行确定。

!> 特别注意 Kotlin -> Jvm 后的 `object` 类中的方法并不是静态的。

### asFinal

```kotlin
fun asFinal()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `final`。

!> 特别注意在 Kotlin -> Jvm 后没有 `open` 标识的 `Member` 和没有任何关联的 `Member` 都将为 `final`。

### asSynchronized

```kotlin
fun asSynchronized()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `synchronized`。

### asVolatile

```kotlin
fun asVolatile()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `volatile`。

### asTransient

```kotlin
fun asTransient()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `transient`。

### asNative

```kotlin
fun asNative()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `native`。

对于任意 JNI 对接的 `Member` 可添加此描述进行确定。

### asInterface

```kotlin
fun asInterface()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `interface`。

### asAbstract

```kotlin
fun asAbstract()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `abstract`。

对于任意的抽象 `Member` 可添加此描述进行确定。

### asStrict

```kotlin
fun asStrict()
```

<b>变更记录</b>

`v1.0.67` `新增`

<b>功能描述</b>

> 添加描述 `Member` 类型包含 `strict`。