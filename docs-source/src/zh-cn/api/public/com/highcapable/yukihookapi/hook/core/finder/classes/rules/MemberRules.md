---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# MemberRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class MemberRules internal constructor(private val rulesData: MemberRulesData) : BaseRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Member` 查找条件实现类。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Member` 标识符筛选条件。

可不设置筛选条件。