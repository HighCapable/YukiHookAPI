---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# CurrentClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class CurrentClass internal constructor(private val classSet: Class<*>, internal val instance: Any)
```

**Change Records**

`v1.0.70` `added`

`v1.1.0` `modified`

调整了构造方法的参数名称

**Function Illustrate**

> 当前实例的类操作对象。

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
val name: String
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `classSet` 的 `Class.getName`。

## simpleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val simpleName: String
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `classSet` 的 `Class.getSimpleName`。

## generic <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun generic(): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前实例中的泛型父类。

如果当前实例不存在泛型将返回 `null`。

## generic <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun generic(initiate: GenericClass.() -> Unit): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前实例中的泛型父类。

如果当前实例不存在泛型将返回 `null`。

## superClass <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun superClass(): SuperClass
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 调用父类实例。

## field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldConditions): FieldFinder.Result.Instance
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 调用当前实例中的变量。

## method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result.Instance
```

**Change Records**

`v1.0.70` `added`

**Function Illustrate**

> 调用当前实例中的方法。

## SuperClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
inner class SuperClass internal constructor(private val superClassSet: Class<*>)
```

**Change Records**

`v1.0.80` `added`

`v1.1.0` `modified`

新增 `superClassSet` 参数

**Function Illustrate**

> 当前类的父类实例的类操作对象。

### name <span class="symbol">- field</span>

```kotlin:no-line-numbers
val name: String
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `classSet` 中父类的 `Class.getName`。

### simpleName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val simpleName: String
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前 `classSet` 中父类的 `Class.getSimpleName`。

### generic <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun generic(): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前实例父类中的泛型父类。

如果当前实例不存在泛型将返回 `null`。

### generic <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun generic(initiate: GenericClass.() -> Unit): GenericClass?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得当前实例父类中的泛型父类。

如果当前实例不存在泛型将返回 `null`。

### field <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun field(initiate: FieldConditions): FieldFinder.Result.Instance
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 调用父类实例中的变量。

### method <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun method(initiate: MethodConditions): MethodFinder.Result.Instance
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 调用父类实例中的方法。