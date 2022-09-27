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
fun argument(index: Int): Class<*>
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 获得泛型参数数组下标的 `Class` 实例。