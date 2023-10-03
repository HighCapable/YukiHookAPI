---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# YukiHookPriority <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class YukiHookPriority
```

**Change Records**

`v1.1.5` `added`

`v1.2.0` `modified`

移除 `internal` 对外公开

**Function Illustrate**

> Hook 回调优先级配置类。

## DEFAULT <span class="symbol">- enum</span>

```kotlin:no-line-numbers
DEFAULT
```

**Change Records**

`v1.1.5` `added`

**Function Illustrate**

> 默认 Hook 回调优先级。

## LOWEST <span class="symbol">- enum</span>

```kotlin:no-line-numbers
LOWEST
```

**Change Records**

`v1.1.5` `added`

**Function Illustrate**

> 延迟回调 Hook 方法结果。

## HIGHEST <span class="symbol">- enum</span>

```kotlin:no-line-numbers
HIGHEST
```

**Change Records**

`v1.1.5` `added`

**Function Illustrate**

> 更快回调 Hook 方法结果。