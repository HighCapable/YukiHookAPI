## ModifierRules *- class*

```kotlin
class ModifierRules internal constructor()
```

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `修改`

新增 `Class` 的描述符判断

**功能描述**

> 这是一个 `Class`、`Member` 描述符定义类。

可对 R8 混淆后的 `Class`、`Member` 进行更加详细的定位。

### ~~asPublic *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asPrivate *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asProtected *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asStatic *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asFinal *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asSynchronized *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asVolatile *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asTransient *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asNative *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asInterface *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asAbstract *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### ~~asStrict *- method*~~ <!-- {docsify-ignore} -->

**变更记录**

`v1.0.67` `新增`

`v1.0.93` `作废`

请将开头的 `as` 修改为 `is`

### isPublic *- method*

```kotlin
fun isPublic()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `public`。

### isPrivate *- method*

```kotlin
fun isPrivate()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `private`。

### isProtected *- method*

```kotlin
fun isProtected()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `protected`。

### isStatic *- method*

```kotlin
fun isStatic()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `static`。

对于任意的静态 `Class`、`Member` 可添加此描述进行确定。

!> 特别注意 Kotlin -> Jvm 后的 `object` 类中的方法并不是静态的。

### isFinal *- method*

```kotlin
fun isFinal()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `final`。

!> 特别注意在 Kotlin -> Jvm 后没有 `open` 标识的 `Class`、`Member` 和没有任何关联的 `Class`、`Member` 都将为 `final`。

### isSynchronized *- method*

```kotlin
fun isSynchronized()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `synchronized`。

### isVolatile *- method*

```kotlin
fun isVolatile()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `volatile`。

### isTransient *- method*

```kotlin
fun isTransient()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `transient`。

### isNative *- method*

```kotlin
fun isNative()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `native`。

对于任意 JNI 对接的 `Class`、`Member` 可添加此描述进行确定。

### isInterface *- method*

```kotlin
fun isInterface()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `interface`。

### isAbstract *- method*

```kotlin
fun isAbstract()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `abstract`。

对于任意的抽象 `Class`、`Member` 可添加此描述进行确定。

### isStrict *- method*

```kotlin
fun isStrict()
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 添加描述 `Class`、`Member` 类型包含 `strict`。