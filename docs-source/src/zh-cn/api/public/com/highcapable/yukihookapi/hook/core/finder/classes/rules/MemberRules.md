---
pageClass: code-page
---

# MemberRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class MemberRules internal constructor(internal val rulesData: MemberRulesData) : BaseRules
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