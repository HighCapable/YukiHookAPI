---
pageClass: code-page
---

# HookClass <span class="symbol">- class</span>

```kotlin:no-line-numbers
class HookClass internal constructor(internal var instance: Class<*>?, internal var name: String, internal var throwable: Throwable?)
```

**变更记录**

`v1.0` `添加`

`v1.1.0` `修改`

`HookClass` 相关功能不再对外开放

**功能描述**

> 创建一个当前 Hook 的 `Class` 接管类。

`instance` 为实例，`name` 为实例完整包名，`throwable` 为找不到实例的时候抛出的异常。