## ModifierRules [class]

```kotlin
class ModifierRules internal constructor()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 这是一个 `Member` 描述符定义类。

可对 R8 混淆后的 `Member` 进行更加详细的定位。

### asPublic

```kotlin
fun asPublic()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `public`。

### asPrivate

```kotlin
fun asPrivate()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `private`。

### asProtected

```kotlin
fun asProtected()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `protected`。

### asStatic

```kotlin
fun asStatic()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `static`。

对于任意的静态 `Member` 可添加此描述进行确定。

!> 特别注意 Kotlin -> Jvm 后的 `object` 类中的方法并不是静态的。

### asFinal

```kotlin
fun asFinal()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `final`。

!> 特别注意在 Kotlin -> Jvm 后没有 `open` 标识的 `Member` 和没有任何关联的 `Member` 都将为 `final`。

### asSynchronized

```kotlin
fun asSynchronized()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `synchronized`。

### asVolatile

```kotlin
fun asVolatile()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `volatile`。

### asTransient

```kotlin
fun asTransient()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `transient`。

### asNative

```kotlin
fun asNative()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `native`。

对于任意 JNI 对接的 `Member` 可添加此描述进行确定。

### asInterface

```kotlin
fun asInterface()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `interface`。

### asAbstract

```kotlin
fun asAbstract()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `abstract`。

对于任意的抽象 `Member` 可添加此描述进行确定。

### asStrict

```kotlin
fun asStrict()
```

**变更记录**

`v1.0.67` `新增`

**功能描述**

> 添加描述 `Member` 类型包含 `strict`。