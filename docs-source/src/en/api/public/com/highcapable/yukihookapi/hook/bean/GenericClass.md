---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# GenericClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class GenericClass internal constructor(private val type: ParameterizedType)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 当前 `Class` 的泛型父类操作对象。

## argument <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun argument(index: Int): Class<*>?
```

```kotlin:no-line-numbers
inline fun <reified T> argument(index: Int): Class<T>?
```

**Change Records**

`v1.1.0` `added`

`v1.1.5` `modified`

新增泛型返回值 `Class<T>` 方法

`v1.2.0` `modified`

方法的返回值可为 `null`

**Function Illustrate**

> 获得泛型参数数组下标的 `Class` 实例。

::: warning

在运行时局部变量的泛型会被擦除，获取不到时将会返回 **null**。

:::