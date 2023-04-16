---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# ExecutorType <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class ExecutorType
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> Hook Framework 类型定义。

定义了目前已知使用频率较高的 Hook Framework。

后期根据 Hook Framework 特征和使用情况将会继续添加新的类型。

无法识别的 Hook Framework 将被定义为 `UNKNOWN`。

## UNKNOWN <span class="symbol">- enum</span>

```kotlin:no-line-numbers
UNKNOWN
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 未知类型。

## XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
XPOSED
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> 原版、第三方 Xposed。

## LSPOSED_LSPATCH <span class="symbol">- enum</span>

```kotlin:no-line-numbers
LSPOSED_LSPATCH
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> LSPosed、LSPatch。

## ED_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
ED_XPOSED
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> EdXposed。

## TAICHI_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
TAICHI_XPOSED
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> TaiChi (太极)。

## BUG_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
BUG_XPOSED
```

**Change Records**

`v1.1.9` `added`

**Function Illustrate**

> BugXposed (应用转生)。