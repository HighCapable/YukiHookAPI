---
pageClass: code-page
---

::: warning

The English translation of this page has not been completed, you are welcome to contribute translations to us.

You can use the **Chrome Translation Plugin** to translate entire pages for reference.

:::

# FieldRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class FieldRules internal constructor(internal val rulesData: FieldRulesData) : BaseRules
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> `Field` 查找条件实现类。

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
var name: String
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Field` 名称。

## type <span class="symbol">- field</span>

```kotlin:no-line-numbers
var type: Any?
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Field` 类型。

可不填写类型。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Field` 标识符筛选条件。

可不设置筛选条件。

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(conditions: NameConditions)
```

**Change Records**

`v1.1.0` `added`

**Function Illustrate**

> 设置 `Field` 名称条件。

## type <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun type(conditions: ObjectConditions)
```

**Change Records**

`v1.1.5` `added`

**Function Illustrate**

> 设置 `Field` 类型条件。

可不填写类型。