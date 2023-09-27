---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

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

**Change Records**

`v1.2.0` `added`

**Function Illustrate**

> 调试日志数据实现类。