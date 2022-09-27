---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# HookClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class HookClass internal constructor(internal var instance: Class<*>?, internal var name: String, internal var throwable: Throwable?)
```

**Change Records**

`v1.0` `first`

`v1.1.0` `modified`

`HookClass` 相关功能不再对外开放

**Function Illustrate**

> 创建一个当前 Hook 的 `Class` 接管类。

`instance` 为实例，`name` 为实例完整包名，`throwable` 为找不到实例的时候抛出的异常。