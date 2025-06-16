---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

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