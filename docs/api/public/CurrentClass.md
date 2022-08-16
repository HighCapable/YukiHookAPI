## CurrentClass *- class*

```kotlin
class CurrentClass internal constructor(internal val classSet: Class<*>, internal val instance: Any)
```

**变更记录**

`v1.0.70` `新增`

`v1.0.93` `修改`

调整了构造方法的参数名称

**功能描述**

> 当前实例的类操作对象。

### name *- field*

```kotlin
val name: String
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获得当前 `classSet` 的 `Class.getName`。

### simpleName *- field*

```kotlin
val simpleName: String
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获得当前 `classSet` 的 `Class.getSimpleName`。

### superClass *- method*

```kotlin
fun superClass(): SuperClass
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例。

### field *- method*

```kotlin
inline fun field(initiate: FieldCondition): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的变量。

### method *- method*

```kotlin
inline fun method(initiate: MethodCondition): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的方法。

### SuperClass *- class*

```kotlin
inner class SuperClass internal constructor()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 当前类的父类实例的类操作对象。

#### name *- field*

```kotlin
val name: String
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获得当前 `classSet` 中父类的 `Class.getName`。

#### simpleName *- field*

```kotlin
val simpleName: String
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 获得当前 `classSet` 中父类的 `Class.getSimpleName`。

#### field *- method*

```kotlin
inline fun field(initiate: FieldCondition): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的变量。

#### method *- method*

```kotlin
inline fun method(initiate: MethodCondition): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的方法。