---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# ExecutorType <span class="symbol">- class</span>

```kotlin:no-line-numbers
enum class ExecutorType
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> Hook Framework 类型定义。

定义了目前已知使用频率较高的 Hook Framework。

后期根据 Hook Framework 特征和使用情况将会继续添加新的类型。

无法识别的 Hook Framework 将被定义为 `UNKNOWN`。

## UNKNOWN <span class="symbol">- enum</span>

```kotlin:no-line-numbers
UNKNOWN
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 未知类型。

## XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
XPOSED
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> 原版、第三方 Xposed。

## LSPOSED_LSPATCH <span class="symbol">- enum</span>

```kotlin:no-line-numbers
LSPOSED_LSPATCH
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> LSPosed、LSPatch。

## ED_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
ED_XPOSED
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> EdXposed。

## TAICHI_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
TAICHI_XPOSED
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> TaiChi (太极)。

## BUG_XPOSED <span class="symbol">- enum</span>

```kotlin:no-line-numbers
BUG_XPOSED
```

**变更记录**

`v1.1.9` `新增`

**功能描述**

> BugXposed (应用转生)。