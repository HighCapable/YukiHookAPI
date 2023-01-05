---
pageClass: code-page
---

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
fun argument(index: Int): Class<*>
```

```kotlin:no-line-numbers
inline fun <reified T> argument(index: Int): Class<T>
```

**变更记录**

`v1.1.0` `新增`

`v1.1.5` `修改`

新增泛型返回值 `Class<T>` 方法

**功能描述**

> 获得泛型参数数组下标的 `Class` 实例。