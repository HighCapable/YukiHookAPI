## NameConditions [class]

```kotlin
class NameConditions internal constructor()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 这是一个模糊 `Member` 名称匹配实现类

可对 R8 混淆后的 `Member` 进行更加详细的定位。

### equalsOf

```kotlin
fun equalsOf(other: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 完全字符匹配。

### startsWith

```kotlin
fun startsWith(prefix: String, startIndex: Int, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 起始字符匹配。

### endsWith

```kotlin
fun endsWith(suffix: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 结束字符匹配。

### contains

```kotlin
fun contains(other: String, isIgnoreCase: Boolean)
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 包含字符匹配。

### matches

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

### thisSynthetic0

```kotlin
fun thisSynthetic0()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为匿名类的主类调用对象。

### onlySymbols

```kotlin
fun onlySymbols()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有符号。

### onlyLetters

```kotlin
fun onlyLetters()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有字母。

### onlyNumbers

```kotlin
fun onlyNumbers()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有数字。

### onlyLettersNumbers

```kotlin
fun onlyLettersNumbers()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有字母或数字。

### onlyLowercase

```kotlin
fun onlyLowercase()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有小写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。

### onlyUppercase

```kotlin
fun onlyUppercase()
```

**变更记录**

`v1.0.88` `新增`

**功能描述**

> 标识为只有大写字母。

在没有其它条件的情况下设置此条件允许判断对象存在字母以外的字符。