---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiResForwarder <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiResForwarder private constructor(private val baseInstance: XResForwarder)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XResForwarder` 的中间层实例。

<h2 class="deprecated">instance - field</h2>

**变更记录**

`v1.0.80` `新增`

`v1.1.0` `作废`

不再对外公开 `instance` 参数

## id <span class="symbol">- field</span>

```kotlin:no-line-numbers
val id: Int
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获得当前 APP 的 Resources Id。

## resources <span class="symbol">- field</span>

```kotlin:no-line-numbers
val resources: Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获得当前 APP 的 Resources。