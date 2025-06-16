---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiResources <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiResources private constructor(private val baseInstance: XResources) : Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XResources` 的中间层实例。

## LayoutInflatedParam <span class="symbol">- class</span>

```kotlin:no-line-numbers
class LayoutInflatedParam(private val baseParam: XC_LayoutInflated.LayoutInflatedParam)
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 装载 Hook APP 的目标布局 Resources 实现类。

### variantName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val variantName: String
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前被 Hook 的布局装载目录名称。

例如：`layout`、`layout-land`、`layout-sw600dp`。

### currentView <span class="symbol">- field</span>

```kotlin:no-line-numbers
val currentView: View
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 获取当前被 Hook 的布局实例。

### findViewByIdentifier <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T : View> View.findViewByIdentifier(name: String): T?
```

```kotlin:no-line-numbers
inline fun <reified T : View> findViewByIdentifier(name: String): T?
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 使用 Identifier 查找 Hook APP 指定 Id 的 `View`。

扩展方法可以使用 Identifier 查找 Hook APP 当前装载布局中指定 Id 的 `View`。