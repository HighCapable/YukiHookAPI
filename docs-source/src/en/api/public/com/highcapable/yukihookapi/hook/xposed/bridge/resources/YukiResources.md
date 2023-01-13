---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiResources <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiResources private constructor(private val baseInstance: XResources) : Resources
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 对接 `XResources` 的中间层实例。

## LayoutInflatedParam <span class="symbol">- class</span>

```kotlin:no-line-numbers
class LayoutInflatedParam(internal val baseParam: XC_LayoutInflated.LayoutInflatedParam)
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 装载 Hook APP 的目标布局 Resources 实现类。

### variantName <span class="symbol">- field</span>

```kotlin:no-line-numbers
val variantName: String
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 获取当前被 Hook 的布局装载目录名称。

例如：`layout`、`layout-land`、`layout-sw600dp`。

### currentView <span class="symbol">- field</span>

```kotlin:no-line-numbers
val currentView: View
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 获取当前被 Hook 的布局实例。

### findViewByIdentifier <span class="symbol">- method</span>

```kotlin:no-line-numbers
inline fun <reified T : View> View.findViewByIdentifier(name: String): T?
```

```kotlin:no-line-numbers
inline fun <reified T : View> findViewByIdentifier(name: String): T?
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 使用 Identifier 查找 Hook APP 指定 Id 的 `View`。

扩展方法可以使用 Identifier 查找 Hook APP 当前装载布局中指定 Id 的 `View`。