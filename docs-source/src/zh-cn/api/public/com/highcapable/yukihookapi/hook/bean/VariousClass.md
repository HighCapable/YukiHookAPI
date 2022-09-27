---
pageClass: code-page
---

# VariousClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class VariousClass(vararg var name: String)
```

**变更记录**

`v1.0` `添加`

**功能描述**

> 这是一个不确定性 `Class` 类名装载器，通过 `name` 装载 `Class` 名称数组。

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(loader: ClassLoader? = null): Class<*>
```

**变更记录**

`v1.0.70` `新增`

**功能描述**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

## getOrNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getOrNull(loader: ClassLoader? = null): Class<*>?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

匹配不到 `Class` 会返回 `null`，不会抛出异常。