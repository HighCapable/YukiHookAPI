---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiModuleResources <span class="symbol">- class</span>

```kotlin:no-line-numbers
class YukiModuleResources private constructor(private val baseInstance: XModuleResources) : Resources
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XModuleResources` 的中间层实例。

## fwd <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun fwd(resId: Int): YukiResForwarder
```

**变更记录**

`v1.0.80` `新增`

**功能描述**

> 对接 `XModuleResources.fwd` 方法。

创建 `YukiResForwarder` 与 `XResForwarder` 实例。