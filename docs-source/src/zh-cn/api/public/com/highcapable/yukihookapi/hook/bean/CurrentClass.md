---
pageClass: code-page
---

# CurrentClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class CurrentClass internal constructor(internal val classSet: Class<*>, internal val instance: Any)
```

**变更记录**

`v1.0.70` `新增`

`v1.1.0` `修改`

调整了构造方法的参数名称

**功能描述**

> 当前实例的类操作对象。

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
val name: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得当前 `classSet` 的 `Class.getName`。

## simpleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val simpleName: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得当前 `classSet` 的 `Class.getSimpleName`。

## superClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun superClass(): SuperClass
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例。

## field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldConditions): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的变量。

## method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 调用当前实例中的方法。

## SuperClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class SuperClass internal constructor()
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 当前类的父类实例的类操作对象。

### name <span class="symbol">- field</span>

```kotlin:no-line-numbers
val name: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得当前 `classSet` 中父类的 `Class.getName`。

### simpleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val simpleName: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获得当前 `classSet` 中父类的 `Class.getSimpleName`。

### field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldConditions): FieldFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的变量。

### method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result.Instance
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 调用父类实例中的方法。