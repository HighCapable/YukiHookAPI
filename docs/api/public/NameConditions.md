## NameConditions *- class*

```kotlin
class NameConditions internal constructor()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 这是一个模糊 `Class`、`Member` 名称匹配实现类。

可对 R8 混淆后的 `Class`、`Member` 进行更加详细的定位。

### equalsOf *- method*

```kotlin
fun equalsOf(other: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 完全字符匹配。

可以重复使用，最终会选择完全匹配的一个。

### startsWith *- method*

```kotlin
fun startsWith(prefix: String, startIndex: Int, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 起始字符匹配。

可以重复使用，最终会选择完全匹配的一个。

### endsWith *- method*

```kotlin
fun endsWith(suffix: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 结束字符匹配。

可以重复使用，最终会选择完全匹配的一个。

### contains *- method*

```kotlin
fun contains(other: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 包含字符匹配。

可以重复使用，最终会选择完全匹配的一个。

### matches *- method*

```kotlin
fun matches(regex: String)
```

```kotlin
fun matches(regex: Regex)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 正则字符匹配。

可以重复使用，最终会选择完全匹配的一个。

### length *- method*

```kotlin
fun length(num: Int)
```

```kotlin
fun length(numRange: IntRange)
```

```kotlin
fun length(conditions: IntConditions)
```

**变更记录**

`v1.0.93` `新增`

**功能描述**

> 字符长度与范围及条件匹配。

不可重复使用，重复使用旧的条件会被当前条件替换。

### thisSynthetic0 *- method*

```kotlin
fun thisSynthetic0()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为匿名类的主类调用对象。

### onlySymbols *- method*

```kotlin
fun onlySymbols()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有符号。

### onlyLetters *- method*

```kotlin
fun onlyLetters()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有字母。

### onlyNumbers *- method*

```kotlin
fun onlyNumbers()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有数字。

### onlyLettersNumbers *- method*

```kotlin
fun onlyLettersNumbers()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有字母或数字。

### onlyLowercase *- method*

```kotlin
fun onlyLowercase()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有小写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。

### onlyUppercase *- method*

```kotlin
fun onlyUppercase()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有大写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。