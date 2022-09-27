---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiModuleResources <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiModuleResources private constructor(private val baseInstance: XModuleResources) : Resources
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 对接 `XModuleResources` 的中间层实例。

## fwd <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun fwd(resId: Int): YukiResForwarder
```

**Change Records**

`v1.0.80` `added`

**Function Illustrate**

> 对接 `XModuleResources.fwd` 方法。

创建 `YukiResForwarder` 与 `XResForwarder` 实例。