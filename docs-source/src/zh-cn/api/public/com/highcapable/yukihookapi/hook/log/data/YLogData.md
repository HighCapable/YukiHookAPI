---
pageClass: code-page
---

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