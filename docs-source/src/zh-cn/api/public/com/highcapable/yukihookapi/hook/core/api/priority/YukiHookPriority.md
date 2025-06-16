---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# YukiHookPriority <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class YukiHookPriority
```

**变更记录**

`v1.1.5` `新增`

`v1.2.0` `修改`

移除 `internal` 对外公开

**功能描述**

> Hook 回调优先级配置类。

## DEFAULT <span class="symbol">- enum</span>

```kotlin:no-line-numbers
DEFAULT
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 默认 Hook 回调优先级。

## LOWEST <span class="symbol">- enum</span>

```kotlin:no-line-numbers
LOWEST
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 延迟回调 Hook 方法结果。

## HIGHEST <span class="symbol">- enum</span>

```kotlin:no-line-numbers
HIGHEST
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 更快回调 Hook 方法结果。