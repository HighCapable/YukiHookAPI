---
pageClass: code-page
---

# MethodRules <span class="symbol">- class</span>

```kotlin:no-line-numbers
class MethodRules internal constructor(internal val rulesData: MethodRulesData) : BaseRules
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> `Method` 查询条件实现类。

## name <span class="symbol">- field</span>

```kotlin:no-line-numbers
var name: String
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 名称。

## paramCount <span class="symbol">- field</span>

```kotlin:no-line-numbers
var paramCount: Int
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 参数个数。

你可以不使用 `param` 指定参数类型而是仅使用此变量指定参数个数。

若参数个数小于零则忽略并使用 `param`。

## returnType <span class="symbol">- field</span>

```kotlin:no-line-numbers
var returnType: Any?
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 返回值。

可不填写返回值。

## modifiers <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun modifiers(conditions: ModifierConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 标识符筛选条件。

可不设置筛选条件。

## emptyParam <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun emptyParam()
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 空参数、无参数。

## param <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun param(vararg paramType: Any)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 参数。

如果同时使用了 `paramCount` 则 `paramType` 的数量必须与 `paramCount` 完全匹配。

如果 `Method` 中存在一些无意义又很长的类型，你可以使用 `VagueType` 来替代它。

::: danger

无参 **Method** 请使用 **emptyParam** 设置查询条件。

有参 **Method** 必须使用此方法设定参数或使用 **paramCount** 指定个数。

:::

## name <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun name(conditions: NameConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 名称条件。

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(numRange: IntRange)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 参数个数范围。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数范围。

## paramCount <span class="symbol">- method</span>

```kotlin:no-line-numbers
fun paramCount(conditions: CountConditions)
```

**变更记录**

`v1.1.0` `新增`

**功能描述**

> 设置 `Method` 参数个数条件。

你可以不使用 `param` 指定参数类型而是仅使用此方法指定参数个数条件。