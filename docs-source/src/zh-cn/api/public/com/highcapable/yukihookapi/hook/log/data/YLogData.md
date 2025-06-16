---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YLogData <span class="symbol">- class</span>

```kotlin:no-line-numbers
data class YLogData internal constructor(
    var timestamp: Long,
    var time: String,
    var tag: String,
    var priority: String,
    var packageName: String,
    var userId: Int,
    var msg: String,
    var throwable: Throwable?
) : Serializable
```

**变更记录**

`v1.2.0` `新增`

**功能描述**

> 调试日志数据实现类。