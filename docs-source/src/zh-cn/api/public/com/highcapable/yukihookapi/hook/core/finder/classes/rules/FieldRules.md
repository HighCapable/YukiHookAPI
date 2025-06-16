---
pageClass: code-page
---

::: warning

由于维护成本，`YukiHookAPI` 从 `1.3.0` 版本开始将不再会对此文档进行更新且在 `2.0.0` 版本切换为 Dokka 插件自动生成的 API 文档。

:::

# FieldRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class FieldRules internal constructor(private val rulesData: FieldRulesData) : BaseRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Field` 查找条件实现类。

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
var name: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Field` 名称。

## type <span class="symbol">- field</span>

```kotlin:no-line-numbers
var type: Any?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Field` 类型。

可不填写类型。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Field` 标识符筛选条件。

可不设置筛选条件。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(conditions: NameConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Field` 名称条件。

## type <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun type(conditions: ObjectConditions)
```

**变更记录**

`v1.1.5` `新增`

**功能描述**

> 设置 `Field` 类型条件。

可不填写类型。