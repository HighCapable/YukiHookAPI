---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# CountRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class CountRules private constructor()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 这是一个模糊 `Class`、`Member` 数组 (下标) 个数条件实现类。

可对 R8 混淆后的 `Class`、`Member` 进行更加详细的定位。

## Int.isZero <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun Int.isZero(): Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 是否为 0。

## Int.moreThan <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun Int.moreThan(count: Int): Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 大于 `count`。

## Int.lessThan <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun Int.lessThan(count: Int): Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 小于 `count`。

## Int.inInterval <span class="symbol">- i-ext-method</span>

```kotlin:no-line-numbers
fun Int.inInterval(countRange: IntRange): Boolean
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 在 `countRange` 区间 A ≤ this ≤ B。