---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# VariousClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class VariousClass(private vararg val name: String)
```

**变更记录**

`v1.0` `添加`

`v1.1.5` `修改`

私有化 `name` 参数并设置为不可修改

**功能描述**

> 这是一个不确定性 `Class` 类名装载器，通过 `name` 装载 `Class` 名称数组。

## get <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun get(loader: ClassLoader? = null, initialize: Boolean): Class<*>
```

**变更记录**

`v1.0.70` `新增`

`v1.1.5` `修改`

新增 `initialize` 参数

**功能描述**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

## getOrNull <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun getOrNull(loader: ClassLoader? = null, initialize: Boolean): Class<*>?
```

**变更记录**

`v1.1.0` `新增`

`v1.1.5` `修改`

新增 `initialize` 参数

**功能描述**

> 获取匹配的实体类。

使用当前 `loader` 装载目标 `Class`。

匹配不到 `Class` 会返回 `null`，不会抛出异常。