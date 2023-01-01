---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# VariousClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class VariousClass(private vararg val name: String)
```

**Change Records**

`v1.0` `first`

`v1.1.5` `modified`

私有化 `name` 参数并设置为不可修改

**Function Illustrate**

> 这是一个不确定性 `Class` 类名装载器，通过 `name` 装载 `Class` 名称数组。

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(loader: ClassLoader? = null, initialize: Boolean): Class<*>
```

**Change Records**

`v1.0.70` `added`

`v1.1.5` `modified`

新增 `initialize` 参数

**Function Illustrate**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

## getOrNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getOrNull(loader: ClassLoader? = null, initialize: Boolean): Class<*>?
```

**Change Records**

`v1.1.0` `added`

`v1.1.5` `modified`

新增 `initialize` 参数

**Function Illustrate**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

匹配不到 `Class` 会返回 `null`，不会抛出异常。