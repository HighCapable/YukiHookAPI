---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# ChannelPriority <span class="symbol">- class</span>

```kotlin:no-line-numbers
class ChannelPriority(private val conditions: () -> Boolean)
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 数据通讯桥响应优先级构造类。

这个类是对 `YukiHookDataChannel` 的一个扩展用法。