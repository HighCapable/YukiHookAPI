---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# GenericClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class GenericClass internal constructor(private val type: ParameterizedType)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 当前 `Class` 的泛型父类操作对象。

## argument <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun argument(index: Int): Class<*>?
```

```kotlin:no-line-numbers
inline fun <reified T> argument(index: Int): Class<T>?
```

**变更记录**

`v1.1.0` `新增`

`v1.1.5` `修改`

新增泛型返回值 `Class<T>` 方法

`v1.2.0` `修改`

方法的返回值可为 `null`

**功能描述**

> 获得泛型参数数组下标的 `Class` 实例。

::: warning

在运行时局部变量的泛型会被擦除，获取不到时将会返回 **null**。

:::